package com.ue.townsystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

import com.ue.exceptions.TownSystemException;

import ultimate_economy.UEVillagerType;
import ultimate_economy.Ultimate_Economy;

public class Plot {

	private String owner;
	private List<String> coOwners;
	private final String chunkCoords;
	private boolean isForSale;
	private double salePrice;
	private final String townName;
	private File file;
	private Villager villager;
	private Inventory inventory;

	/**
	 * <p>
	 * Represents a plot in a town.
	 * <p>
	 * 
	 * @param file
	 * @param owner
	 * @param chunkCoords
	 *            (format "X/Z")
	 * @param townName
	 */
	public Plot(File file, String owner, String chunkCoords, String townName) {
		this.chunkCoords = chunkCoords;
		this.townName = townName;
		this.file = setOwner(file, owner);
		isForSale = false;
		salePrice = 0;
		coOwners = new ArrayList<>();
	}

	private Plot(String chunkCoords, String townName) {
		this.chunkCoords = chunkCoords;
		this.townName = townName;
		coOwners = new ArrayList<>();
	}

	/**
	 * <p>
	 * Spawns a sale villager with saving.
	 * <p>
	 * 
	 * @param file
	 * @param location
	 * @return file
	 */
	private File spawnSaleVillager(File file, Location location) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.x", location.getX());
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.y", location.getY());
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.z", location.getZ());
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.world",
				location.getWorld().getName());
		spawnSaleVillager(location);
		return save(file, config);
	}

	/**
	 * <p>
	 * Spawns a sale villager without saving.
	 * <p>
	 * 
	 * @param file
	 * @param location
	 */
	private void spawnSaleVillager(Location location) {
		location.getChunk().load();
		Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 10, 10, 10);
		for (Entity entity : entitys) {
			if (entity.getName()
					.equals("Plot " + location.getChunk().getX() + "/" + location.getChunk().getZ() + " For Sale!")) {
				entity.remove();
			}
		}
		villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		villager.setCustomName("Plot " + location.getChunk().getX() + "/" + location.getChunk().getZ() + " For Sale!");
		villager.setCustomNameVisible(true);
		// set the tye of the villager to meta
		villager.setMetadata("ue-type", new FixedMetadataValue(Ultimate_Economy.getInstance, UEVillagerType.PLOTSALE));
		villager.setProfession(Villager.Profession.NITWIT);
		villager.setSilent(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
		villager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000, 30000000));
		inventory = Bukkit.createInventory(villager, 9,
				"Plot " + location.getChunk().getX() + "/" + location.getChunk().getZ());
		ItemStack itemStack = new ItemStack(Material.GREEN_WOOL, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName("Buy");
		List<String> list = new ArrayList<String>();
		list.add(ChatColor.GOLD + "Price: " + ChatColor.GREEN + salePrice);
		list.add(ChatColor.GOLD + "Is sold by " + ChatColor.GREEN + owner);
		meta.setLore(list);
		itemStack.setItemMeta(meta);
		inventory.setItem(0, itemStack);
		itemStack = new ItemStack(Material.RED_WOOL, 1);
		meta = itemStack.getItemMeta();
		list.clear();
		list.add(ChatColor.RED + "Only for plot owner!");
		meta.setDisplayName("Cancel Sale");
		meta.setLore(list);
		itemStack.setItemMeta(meta);
		inventory.setItem(8, itemStack);
	}

	/**
	 * <p>
	 * Despawns the sale villager.
	 * <p>
	 */
	public void despawnSaleVillager() {
		if (villager != null) {
			villager.remove();
		}
	}

	/**
	 * <p>
	 * Moves a sale villager to a new location.
	 * <p>
	 * 
	 * @param file
	 * @param newLocation
	 * @return
	 * @throws TownSystemException
	 */
	public File moveSaleVillager(File file, Location newLocation) throws TownSystemException {
		if (chunkCoords.equals(newLocation.getChunk().getX() + "/" + newLocation.getChunk().getZ())) {
			villager.teleport(newLocation);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.x", newLocation.getX());
			config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.y", newLocation.getY());
			config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.z", newLocation.getZ());
			config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager.world",
					newLocation.getWorld().getName());
			return save(file, config);
		} else {
			throw new TownSystemException(TownSystemException.OUTSIDE_OF_THE_PLOT);
		}
	}

	/**
	 * <p>
	 * Opens the inventory of the saleManager.
	 * <p>
	 */
	public void openSaleVillagerInv(Player player) {
		player.openInventory(inventory);
	}

	/**
	 * @return file
	 */
	public File getSaveFile() {
		return file;
	}

	/**
	 * <p>
	 * Get the owner of this plot.
	 * <p>
	 * 
	 * @return
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * <p>
	 * Set the owner of this plot.
	 * <p>
	 * 
	 * @param file
	 * @param owner
	 * @return file
	 */
	public File setOwner(File file, String owner) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".owner", owner);
		this.owner = owner;
		return save(file, config);
	}

	/**
	 * <p>
	 * Set owner without saving.
	 * <p>
	 * 
	 * @param owner
	 */
	private void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * <p>
	 * Get a list of all coOwners of this plot.
	 * <p>
	 * 
	 * @return List
	 */
	public List<String> getCoOwners() {
		return coOwners;
	}

	/**
	 * <p>
	 * Set the list of coOwners of this plot.
	 * <p>
	 * 
	 * @param file
	 * @param coOwners
	 */
	private void setCoOwners(List<String> coOwners) {
		this.coOwners = coOwners;
	}

	/**
	 * <p>
	 * Add a coOwner to this plot.
	 * <p>
	 * 
	 * @param file
	 * @param citizen
	 * @return file
	 * @throws TownSystemException
	 */
	public File addCoOwner(File file, String citizen) throws TownSystemException {
		if (!coOwners.contains(citizen)) {
			coOwners.add(citizen);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Towns." + townName + ".Plots." + chunkCoords + ".coOwners", coOwners);
			return save(file, config);
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_ALREADY_COOWNERN);
		}
	}

	/**
	 * <p>
	 * Removes a coOwner from this plot.
	 * <p>
	 * 
	 * @param file
	 * @param citizen
	 * @return File
	 * @throws TownSystemException
	 */
	public File removeCoOwner(File file, String citizen) throws TownSystemException {
		if (coOwners.contains(citizen)) {
			coOwners.remove(citizen);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Towns." + townName + ".Plots." + chunkCoords + ".coOwners", coOwners);
			return save(file, config);
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NO_COOWNER);
		}
	}

	/**
	 * <p>
	 * Set 'isForSale' without saving it in the file.
	 * <p>
	 * 
	 * @param isForSale
	 */
	private void setIsForSale(boolean isForSale) {
		this.isForSale = isForSale;
	}

	/**
	 * <p>
	 * Set 'isForSale' with saving it in the file.
	 * <p>
	 * 
	 * @param isForSale
	 * @return File
	 */
	private File setIsForSale(File file, boolean isForSale) {
		this.isForSale = isForSale;
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".isForSale", isForSale);
		return save(file, config);
	}

	/**
	 * <p>
	 * Get the chunk coords of this plot.
	 * <p>
	 * 
	 * @return String
	 */
	public String getChunkCoords() {
		return chunkCoords;
	}

	/**
	 * <p>
	 * Returns true if the player is the owner of this plot.
	 * <p>
	 * 
	 * @param owner
	 * @return booelan
	 */
	public boolean isOwner(String owner) {
		if (this.owner.equals(owner)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * Returns true if the player is a coOwner of this plot.
	 * <p>
	 * 
	 * @param coOwner
	 * @return boolean
	 */
	public boolean isCoOwner(String coOwner) {
		boolean is = false;
		for (String string : coOwners) {
			if (string.equals(coOwner)) {
				is = true;
				break;
			}
		}
		return is;
	}

	/**
	 * <p>
	 * Returns true if this plot is for sale.
	 * <p>
	 * 
	 * @return boolean
	 */
	public boolean isForSale() {
		return isForSale;
	}

	/**
	 * <p>
	 * Removes a plot from sale. Removes also the saleVillager.
	 * <p>
	 * 
	 * @param file
	 * @param owner
	 * @return
	 * @throws TownSystemException
	 */
	public File removeFromSale(File file, String owner) throws TownSystemException {
		if (isOwner(owner)) {
			file = setIsForSale(file, false);
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Towns." + townName + ".Plots." + chunkCoords + ".SaleVillager", null);
			World world = villager.getLocation().getWorld();
			villager.remove();
			world.save();
			return save(file, config);
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_OWNER);
		}
	}

	/**
	 * <p>
	 * Set this plot for sale with saving it in the file. Spawns a SellVillager at
	 * playerposition.
	 * <p>
	 * 
	 * @param file
	 * @param isForSale
	 * @param salePrice
	 *            Ignored if isForSale is 'false'
	 * @return file
	 * @throws TownSystemException
	 */
	public File setForSale(File file, double salePrice, Location location) throws TownSystemException {
		if (this.isForSale) {
			throw new TownSystemException(TownSystemException.PLOT_IS_ALREADY_FOR_SALE);
		} else {
			file = setSalePrice(file, salePrice);
			file = spawnSaleVillager(file, location);
			file = setIsForSale(file, true);
			return file;
		}
	}

	/**
	 * <p>
	 * Returns the salePrice for this slot.
	 * <p>
	 * 
	 * @return double
	 */
	public double getSalePrice() {
		return salePrice;
	}

	/**
	 * <p>
	 * Set the salePrice for this plot.
	 * <p>
	 * 
	 * @param file
	 * @param salePrice
	 * @return file
	 */
	public File setSalePrice(File file, double salePrice) {
		setSalePrice(salePrice);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Towns." + townName + ".Plots." + chunkCoords + ".salePrice", salePrice);
		return save(file, config);
	}

	/**
	 * <p>
	 * Set 'salePrice' without saving.
	 * <p>
	 * 
	 * @param salePrice
	 */
	private void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	/**
	 * <p>
	 * Saves a config in a file.
	 * <p>
	 * 
	 * @param file
	 * @param config
	 * @return file
	 */
	private File save(File file, FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * <p>
	 * Returns a Plot loaded by the parameters.
	 * <p>
	 * 
	 * @param file
	 * @param townName
	 * @param coords
	 * @return Plot
	 * @throws TownSystemException
	 */
	public static Plot loadPlot(File file, String townName, String coords) throws TownSystemException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (config.getStringList("Towns." + townName + ".chunks").contains(coords)) {
			Plot plot = new Plot(coords, townName);
			plot.setIsForSale(config.getBoolean("Towns." + townName + ".Plots." + coords + ".isForSale"));
			plot.setCoOwners(config.getStringList("Towns." + townName + ".Plots." + coords + ".coOwners"));
			plot.setOwner(config.getString("Towns." + townName + ".Plots." + coords + ".owner"));
			plot.setSalePrice(config.getDouble("Towns." + townName + ".Plots." + coords + ".salePrice"));
			if (plot.isForSale()) {
				Location location = new Location(
						Bukkit.getWorld(
								config.getString("Towns." + townName + ".Plots." + coords + ".SaleVillager.world")),
						config.getDouble("Towns." + townName + ".Plots." + coords + ".SaleVillager.x"),
						config.getDouble("Towns." + townName + ".Plots." + coords + ".SaleVillager.y"),
						config.getDouble("Towns." + townName + ".Plots." + coords + ".SaleVillager.z"));
				plot.spawnSaleVillager(location);
			}
			return plot;
		} else {
			throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED_BY_TOWN);
		}
	}
}
