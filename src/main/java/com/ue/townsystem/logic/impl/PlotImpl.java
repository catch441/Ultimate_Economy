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
import com.ue.townsystem.dataaccess.api.TownsystemDao;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.ultimate_economy.EconomyVillager;

public class PlotImpl implements Plot {

	private final TownsystemValidationHandler validationHandler;
	private final TownsystemDao townsystemDao;
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
	 * @param townsystemDao
	 * @param town
	 * @param owner
	 * @param serverProvider
	 */
	public PlotImpl(String chunkCoords, TownsystemValidationHandler validationHandler,
			TownsystemDao townsystemDao, Town town, EconomyPlayer owner, ServerProvider serverProvider) {
		this.chunkCoords = chunkCoords;
		this.town = town;
		this.townsystemDao = townsystemDao;
		this.validationHandler = validationHandler;
		this.serverProvider = serverProvider;
		setupNewPlot(owner);
	}

	/**
	 * Loading an existing plot constructor.
	 * 
	 * @param chunkCoords       (format "X/Z")
	 * @param validationHandler
	 * @param townsystemDao
	 * @param town
	 * @param serverProvider
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 */
	public PlotImpl(String chunkCoords, TownsystemValidationHandler validationHandler,
			TownsystemDao townsystemDao, Town town, ServerProvider serverProvider)
			throws EconomyPlayerException, TownSystemException {
		this.chunkCoords = chunkCoords;
		this.town = town;
		this.townsystemDao = townsystemDao;
		this.validationHandler = validationHandler;
		this.serverProvider = serverProvider;
		loadExistingPlot();
	}

	private void loadExistingPlot() throws EconomyPlayerException, TownSystemException {
		owner = townsystemDao.loadPlotOwner(town.getTownName(), chunkCoords);
		residents = townsystemDao.loadResidents(town.getTownName(), chunkCoords);
		salePrice = townsystemDao.loadPlotSalePrice(town.getTownName(), chunkCoords);
		isForSale = townsystemDao.loadPlotIsForSale(town.getTownName(), chunkCoords);
		if (isForSale) {
			spawnSaleVillager(townsystemDao.loadPlotVillagerLocation(town.getTownName(), chunkCoords));
		}
	}

	private void setupNewPlot(EconomyPlayer owner) {
		setOwner(owner);
		isForSale = false;
		salePrice = 0;
		residents = new ArrayList<>();
		townsystemDao.savePlotResidents(town.getTownName(), chunkCoords, residents);
		townsystemDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townsystemDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
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
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
		villager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000, 30000000));
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
		townsystemDao.savePlotVillagerLocation(town.getTownName(), chunkCoords, newLocation);
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
		townsystemDao.savePlotOwner(town.getTownName(), chunkCoords, player);
	}

	@Override
	public List<EconomyPlayer> getResidents() {
		return new ArrayList<>(residents);
	}

	@Override
	public void addResident(EconomyPlayer player) throws TownSystemException {
		validationHandler.checkForPlayerIsNotResidentOfPlot(getResidents(), player);
		residents.add(player);
		townsystemDao.savePlotResidents(town.getTownName(), chunkCoords, residents);
	}

	@Override
	public void removeResident(EconomyPlayer player) throws TownSystemException {
		validationHandler.checkForPlayerIsResidentOfPlot(getResidents(), player);
		residents.remove(player);
		townsystemDao.savePlotResidents(town.getTownName(), chunkCoords, residents);
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
		townsystemDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townsystemDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
	}

	@Override
	public void setForSale(double salePrice, Location playerLocation, EconomyPlayer player)
			throws TownSystemException, EconomyPlayerException {
		validationHandler.checkForIsPlotOwner(getOwner(), player);
		validationHandler.checkForPlotIsNotForSale(isForSale());
		isForSale = true;
		this.salePrice = salePrice;
		spawnSaleVillager(playerLocation);
		townsystemDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townsystemDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
		townsystemDao.savePlotVillagerLocation(town.getTownName(), chunkCoords, villager.getLocation());
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
