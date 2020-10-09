package com.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.townsystem.dataaccess.api.TownsystemDao;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.ultimate_economy.EconomyVillager;
import com.ue.ultimate_economy.UltimateEconomy;

public class PlotImpl implements Plot {

	private final MessageWrapper messageWrapper;
	private final TownsystemValidationHandler validationHandler;
	private final TownsystemDao townsystemDao;
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
	 * @param messageWrapper
	 * @param townsystemDao
	 * @param town
	 * @param owner
	 */
	public PlotImpl(String chunkCoords, TownsystemValidationHandler validationHandler, MessageWrapper messageWrapper,
			TownsystemDao townsystemDao, Town town, EconomyPlayer owner) {
		this.chunkCoords = chunkCoords;
		this.town = town;
		this.townsystemDao = townsystemDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		setupNewPlot(owner);
	}

	/**
	 * Loading an existing plot constructor.
	 * 
	 * @param chunkCoords       (format "X/Z")
	 * @param validationHandler
	 * @param messageWrapper
	 * @param townsystemDao
	 * @param town
	 */
	public PlotImpl(String chunkCoords, TownsystemValidationHandler validationHandler, MessageWrapper messageWrapper,
			TownsystemDao townsystemDao, Town town) {
		this.chunkCoords = chunkCoords;
		this.town = town;
		this.townsystemDao = townsystemDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		loadExistingPlot();
	}

	private void loadExistingPlot() {
		residents = townsystemDao.loadResidents(town.getTownName(), chunkCoords);
		salePrice = townsystemDao.loadPlotSalePrice(town.getTownName(), chunkCoords);
		isForSale = townsystemDao.loadPlotIsForSale(town.getTownName(), chunkCoords);
		if (isForSale) {
			try {
				spawnSaleVillager(townsystemDao.loadPlotVillagerLocation(town.getTownName(), chunkCoords));
			} catch (TownSystemException e) {
				Bukkit.getLogger().warning(e.getMessage());
			}
		}
	}

	private void setupNewPlot(EconomyPlayer owner) {
		setOwner(owner);
		isForSale = false;
		salePrice = 0;
		residents = new ArrayList<>();
	}

	private void spawnSaleVillager(Location location) {
		location.getChunk().load();
		removeDuplicatedVillagers(location);
		setupSaleVillager(location);
		setupSaleVillagerInventory(location);
	}

	private void setupSaleVillagerInventory(Location location) {
		salesVillagerInv = Bukkit.createInventory(villager, 9,
				"Plot " + location.getChunk().getX() + "/" + location.getChunk().getZ());
		ItemStack itemStack = new ItemStack(Material.GREEN_WOOL, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName("Buy");
		List<String> list = new ArrayList<String>();
		list.add(ChatColor.GOLD + "Price: " + ChatColor.GREEN + salePrice);
		list.add(ChatColor.GOLD + "Is sold by " + ChatColor.GREEN + owner.getName());
		meta.setLore(list);
		itemStack.setItemMeta(meta);
		salesVillagerInv.setItem(0, itemStack);
		itemStack = new ItemStack(Material.RED_WOOL, 1);
		meta = itemStack.getItemMeta();
		list.clear();
		list.add(ChatColor.RED + "Only for plot owner!");
		meta.setDisplayName("Cancel Sale");
		meta.setLore(list);
		itemStack.setItemMeta(meta);
		salesVillagerInv.setItem(8, itemStack);
	}

	private void setupSaleVillager(Location location) {
		villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		villager.setCustomName("Plot " + location.getChunk().getX() + "/" + location.getChunk().getZ() + " For Sale!");
		villager.setCustomNameVisible(true);
		// set the tye of the villager to meta
		villager.setMetadata("ue-type", new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.PLOTSALE));
		villager.setProfession(Villager.Profession.NITWIT);
		villager.setSilent(true);
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
	public void moveSaleVillager(Location newLocation) throws EconomyPlayerException {
		validationHandler.checkForLocationInsidePlot(chunkCoords, newLocation);
		villager.teleport(newLocation);
		townsystemDao.savePlotVillagerLocation(town.getTownName(), chunkCoords, newLocation);
	}

	@Override
	public void openSaleVillagerInv(Player player) {
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
		return residents;
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
		validationHandler.checkForIsPlotOwner(this.owner, owner);
		isForSale = false;
		World world = villager.getLocation().getWorld();
		villager.remove();
		world.save();
		salePrice = 0;
		townsystemDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townsystemDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
	}

	@Override
	public void setForSale(double salePrice, Location playerLocation, EconomyPlayer player, boolean sendMessage)
			throws TownSystemException, EconomyPlayerException {
		validationHandler.checkForIsPlotOwner(this.owner, owner);
		validationHandler.checkForPlotIsNotForSale(isForSale());
		spawnSaleVillager(playerLocation);
		isForSale = true;
		this.salePrice = salePrice;
		townsystemDao.savePlotSalePrice(town.getTownName(), chunkCoords, salePrice);
		townsystemDao.savePlotIsForSale(town.getTownName(), chunkCoords, isForSale);
		townsystemDao.savePlotVillagerLocation(town.getTownName(), chunkCoords, villager.getLocation());
		if (player.isOnline() && sendMessage) {
			player.getPlayer().sendMessage(messageWrapper.getString("town_plot_setForSale"));
		}
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
