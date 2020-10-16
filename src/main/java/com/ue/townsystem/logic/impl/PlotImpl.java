package com.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.EconomyVillager;
import com.ue.townsystem.dataaccess.api.TownworldDao;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;

public class PlotImpl implements Plot {

	private final TownsystemValidationHandler validationHandler;
	private final TownworldDao townworldDao;
	private final ServerProvider serverProvider;
	private final String chunkCoords;
	private EconomyPlayer owner;
	private List<EconomyPlayer> residents;
	private boolean isForSale;
	private double salePrice;
	private Villager villager;
	private Inventory salesVillagerInv;
	private Town town;

	/**
	 * Creating a new plot constructor.
	 * 
	 * @param chunkCoords       (format "X/Z")
	 * @param validationHandler
	 * @param townworldDao
	 * @param town
	 * @param owner
	 * @param serverProvider
	 */
	public PlotImpl(String chunkCoords, TownsystemValidationHandler validationHandler,
			TownworldDao townworldDao, Town town, EconomyPlayer owner, ServerProvider serverProvider) {
		this.chunkCoords = chunkCoords;
		this.town = town;
		this.townworldDao = townworldDao;
		this.validationHandler = validationHandler;
		this.serverProvider = serverProvider;
		setupNewPlot(owner);
	}

	/**
	 * Loading an existing plot constructor.
	 * 
	 * @param chunkCoords       (format "X/Z")
	 * @param validationHandler
	 * @param townworldDao
	 * @param town
	 * @param serverProvider
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 */
	public PlotImpl(String chunkCoords, TownsystemValidationHandler validationHandler,
			TownworldDao townworldDao, Town town, ServerProvider serverProvider)
			throws EconomyPlayerException, TownSystemException {
		this.chunkCoords = chunkCoords;
		this.town = town;
		this.townworldDao = townworldDao;
		this.validationHandler = validationHandler;
		this.serverProvider = serverProvider;
		loadExistingPlot();
	}

	private void loadExistingPlot() throws EconomyPlayerException, TownSystemException {
		owner = townworldDao.loadPlotOwner(town.getTownName(), chunkCoords);
		residents = townworldDao.loadResidents(town.getTownName(), chunkCoords);
		salePrice = townworldDao.loadPlotSalePrice(town.getTownName(), chunkCoords);
		isForSale = townworldDao.loadPlotIsForSale(town.getTownName(), chunkCoords);
		if (isForSale) {
			spawnSaleVillager(townworldDao.loadPlotVillagerLocation(town.getTownName(), chunkCoords));
		}
	}

	private void setupNewPlot(EconomyPlayer owner) {
		setOwner(owner);
		isForSale = false;
		salePrice = 0;
		residents = new ArrayList<>();
		townworldDao.savePlotResidents(town.getTownName(), chunkCoords, residents);
		townworldDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townworldDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
	}

	private void spawnSaleVillager(Location location) {
		location.getChunk().load();
		removeDuplicatedVillagers(location);
		setupSaleVillager(location);
		setupSaleVillagerInventory(location);
	}

	private void setupSaleVillagerInventory(Location location) {
		salesVillagerInv = serverProvider.createInventory(villager, 9,
				"Plot " + location.getChunk().getX() + "/" + location.getChunk().getZ());
		ItemStack buyStack = serverProvider.createItemStack(Material.GREEN_WOOL, 1);
		ItemMeta buyItemMeta = buyStack.getItemMeta();
		buyItemMeta.setDisplayName("Buy");
		List<String> listBuy = new ArrayList<String>();
		listBuy.add(ChatColor.GOLD + "Price: " + ChatColor.GREEN + salePrice);
		listBuy.add(ChatColor.GOLD + "Is sold by " + ChatColor.GREEN + owner.getName());
		buyItemMeta.setLore(listBuy);
		buyStack.setItemMeta(buyItemMeta);
		salesVillagerInv.setItem(0, buyStack);
		ItemStack cancelStack = serverProvider.createItemStack(Material.RED_WOOL, 1);
		ItemMeta cancelItemMeta = cancelStack.getItemMeta();
		List<String> listCancel = new ArrayList<String>();
		listCancel.add(ChatColor.RED + "Only for plot owner!");
		cancelItemMeta.setDisplayName("Cancel Sale");
		cancelItemMeta.setLore(listCancel);
		cancelStack.setItemMeta(cancelItemMeta);
		salesVillagerInv.setItem(8, cancelStack);
	}

	private void setupSaleVillager(Location location) {
		villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		villager.setCustomName("Plot " + location.getChunk().getX() + "/" + location.getChunk().getZ() + " For Sale!");
		villager.setCustomNameVisible(true);
		// set the tye of the villager to meta
		villager.setMetadata("ue-type",
				new FixedMetadataValue(serverProvider.getPluginInstance(), EconomyVillager.PLOTSALE));
		villager.setProfession(Profession.NITWIT);
		villager.setSilent(true);
		villager.setInvulnerable(true);
		villager.setCollidable(false);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
	}

	private void removeDuplicatedVillagers(Location location) {
		Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 10, 10, 10);
		for (Entity entity : entitys) {
			if (entity.getName()
					.equals("Plot " + location.getChunk().getX() + "/" + location.getChunk().getZ() + " For Sale!")) {
				entity.remove();
			}
		}
	}

	@Override
	public void despawnSaleVillager() {
		if (villager != null) {
			villager.remove();
		}
	}

	@Override
	public void moveSaleVillager(Location newLocation) throws EconomyPlayerException, TownSystemException {
		validationHandler.checkForLocationInsidePlot(chunkCoords, newLocation);
		validationHandler.checkForPlotIsForSale(isForSale());
		villager.teleport(newLocation);
		townworldDao.savePlotVillagerLocation(town.getTownName(), chunkCoords, newLocation);
	}

	@Override
	public void openSaleVillagerInv(Player player) throws TownSystemException {
		validationHandler.checkForPlotIsForSale(isForSale());
		player.openInventory(salesVillagerInv);
	}

	@Override
	public EconomyPlayer getOwner() {
		return owner;
	}

	@Override
	public void setOwner(EconomyPlayer player) {
		this.owner = player;
		townworldDao.savePlotOwner(town.getTownName(), chunkCoords, player);
	}

	@Override
	public List<EconomyPlayer> getResidents() {
		return new ArrayList<>(residents);
	}

	@Override
	public void addResident(EconomyPlayer player) throws TownSystemException {
		validationHandler.checkForPlayerIsNotResidentOfPlot(getResidents(), player);
		residents.add(player);
		townworldDao.savePlotResidents(town.getTownName(), chunkCoords, residents);
	}

	@Override
	public void removeResident(EconomyPlayer player) throws TownSystemException {
		validationHandler.checkForPlayerIsResidentOfPlot(getResidents(), player);
		residents.remove(player);
		townworldDao.savePlotResidents(town.getTownName(), chunkCoords, residents);
	}

	@Override
	public String getChunkCoords() {
		return chunkCoords;
	}

	@Override
	public boolean isOwner(EconomyPlayer owner) {
		if (this.owner.equals(owner)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isResident(EconomyPlayer player) {
		boolean is = false;
		for (EconomyPlayer resident : residents) {
			if (resident.equals(player)) {
				is = true;
				break;
			}
		}
		return is;
	}

	@Override
	public boolean isForSale() {
		return isForSale;
	}

	@Override
	public void removeFromSale(EconomyPlayer owner) throws EconomyPlayerException {
		validationHandler.checkForIsPlotOwner(getOwner(), owner);
		isForSale = false;
		World world = villager.getLocation().getWorld();
		villager.remove();
		world.save();
		salePrice = 0;
		townworldDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townworldDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
	}

	@Override
	public void setForSale(double salePrice, Location playerLocation, EconomyPlayer player)
			throws TownSystemException, EconomyPlayerException {
		validationHandler.checkForIsPlotOwner(getOwner(), player);
		validationHandler.checkForPlotIsNotForSale(isForSale());
		isForSale = true;
		this.salePrice = salePrice;
		spawnSaleVillager(playerLocation);
		townworldDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townworldDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
		townworldDao.savePlotVillagerLocation(town.getTownName(), chunkCoords, villager.getLocation());
	}

	@Override
	public double getSalePrice() {
		return salePrice;
	}

	@Override
	public Town getTown() {
		return town;
	}
}
