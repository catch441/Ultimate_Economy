package com.ue.townsystem.town.impl;

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
import com.ue.townsystem.town.api.Plot;
import com.ue.ultimate_economy.UEVillagerType;
import com.ue.ultimate_economy.Ultimate_Economy;

public class PlotImpl implements Plot {

	private String owner;
	private List<String> coOwners;
	private final String chunkCoords;
	private boolean isForSale;
	private double salePrice;
	private Villager villager;
	private Inventory inventory;
	private TownImpl townImpl;

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
	public PlotImpl(TownImpl townImpl, String owner, String chunkCoords) {
		this.chunkCoords = chunkCoords;
		this.townImpl = townImpl;
		setOwner(owner);
		isForSale = false;
		salePrice = 0;
		coOwners = new ArrayList<>();
	}

	/**
	 * Spawns a sale villager with saving.
	 * 
	 * @param location
	 */
	private void spawnSaleVillagerWithSaving(Location location) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
		config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.x", location.getX());
		config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.y", location.getY());
		config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.z", location.getZ());
		config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.world",
				location.getWorld().getName());
		spawnSaleVillager(location);
		save(townImpl.getTownworld().getSaveFile(), config);
	}

	/**
	 * <p>
	 * Spawns a sale villager without saving.
	 * <p>
	 * 
	 * @param file
	 * @param location
	 */
	public void spawnSaleVillager(Location location) {
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

	public void despawnSaleVillager() {
		if (villager != null) {
			villager.remove();
		}
	}

	/**
	 * Moves a sale villager to a new location.
	 * 
	 * @param newLocation
	 * @throws TownSystemException
	 */
	public void moveSaleVillager(Location newLocation) throws TownSystemException {
		if (chunkCoords.equals(newLocation.getChunk().getX() + "/" + newLocation.getChunk().getZ())) {
			villager.teleport(newLocation);
			FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.x", newLocation.getX());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.y", newLocation.getY());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.z", newLocation.getZ());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.world",
					newLocation.getWorld().getName());
			save(townImpl.getTownworld().getSaveFile(), config);
		} else {
			throw new TownSystemException(TownSystemException.OUTSIDE_OF_THE_PLOT);
		}
	}

	/**
	 * Opens the inventory of the saleManager.
	 * 
	 */
	public void openSaleVillagerInv(Player player) {
		player.openInventory(inventory);
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
		config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".owner", owner);
		this.owner = owner;
		save(townImpl.getTownworld().getSaveFile(), config);
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
	public void setCoOwners(List<String> coOwners) {
		this.coOwners = coOwners;
	}

	/**
	 * Add a coOwner to this plot.
	 * 
	 * @param citizen
	 * @throws TownSystemException
	 */
	public void addCoOwner(String citizen) throws TownSystemException {
		if (!coOwners.contains(citizen)) {
			coOwners.add(citizen);
			FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".coOwners", coOwners);
			save(townImpl.getTownworld().getSaveFile(), config);
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_ALREADY_COOWNERN);
		}
	}

	public void removeCoOwner(String citizen) throws TownSystemException {
		if (coOwners.contains(citizen)) {
			coOwners.remove(citizen);
			FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".coOwners", coOwners);
			save(townImpl.getTownworld().getSaveFile(), config);
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NO_COOWNER);
		}
	}

	/**
	 * Set 'isForSale' with saving it into the file.
	 * 
	 * @param isForSale
	 */
	public void setIsForSale(boolean isForSale) {
		this.isForSale = isForSale;
		FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
		config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".isForSale", isForSale);
		save(townImpl.getTownworld().getSaveFile(), config);
	}

	public String getChunkCoords() {
		return chunkCoords;
	}

	public boolean isOwner(String owner) {
		if (this.owner.equals(owner)) {
			return true;
		} else {
			return false;
		}
	}

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

	public boolean isForSale() {
		return isForSale;
	}

	public void removeFromSale(String owner) throws TownSystemException {
		if (isOwner(owner)) {
			setIsForSale(false);
			FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager", null);
			World world = villager.getLocation().getWorld();
			villager.remove();
			world.save();
			save(townImpl.getTownworld().getSaveFile(), config);
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_IS_NOT_OWNER);
		}
	}

	public void setForSale(double salePrice, Location playerLocation,String player) throws TownSystemException {
		if (isOwner(player)) {
			if (this.isForSale) {
				throw new TownSystemException(TownSystemException.PLOT_IS_ALREADY_FOR_SALE);
			} else {
				setSalePrice(salePrice);
				spawnSaleVillagerWithSaving(playerLocation);
				setIsForSale(true);
			}
		} else {
			throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
		}
	}

	public double getSalePrice() {
		return salePrice;
	}

	/**
	 * Set the salePrice for this plot with saving.
	 * 
	 * @param salePrice
	 */
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
		FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
		config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".salePrice", salePrice);
		save(townImpl.getTownworld().getSaveFile(), config);
	}

	/**
	 * Saves a config in a file.
	 * 
	 * @param config
	 */
	private void save(File file, FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
