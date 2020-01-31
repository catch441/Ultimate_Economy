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

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.townsystem.town.api.Plot;
import com.ue.ultimate_economy.UEVillagerType;
import com.ue.ultimate_economy.Ultimate_Economy;

public class PlotImpl implements Plot {

	private EconomyPlayer owner;
	private List<EconomyPlayer> residents;
	private final String chunkCoords;
	private boolean isForSale;
	private double salePrice;
	private Villager villager;
	private Inventory inventory;
	private TownImpl townImpl;

	/**
	 * Represents a plot in a town.
	 * 
	 * @param townImpl
	 * @param owner
	 * @param chunkCoords
	 *            (format "X/Z")
	 */
	public PlotImpl(TownImpl townImpl, EconomyPlayer owner, String chunkCoords) {
		this.chunkCoords = chunkCoords;
		this.townImpl = townImpl;
		setOwner(owner);
		isForSale = false;
		salePrice = 0;
		residents = new ArrayList<>();
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
	 * Spawns a sale villager without saving.
	 * 
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
		list.add(ChatColor.GOLD + "Is sold by " + ChatColor.GREEN + owner.getName());
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
	 * @throws PlayerException
	 */
	public void moveSaleVillager(Location newLocation) throws PlayerException {
		if (chunkCoords.equals(newLocation.getChunk().getX() + "/" + newLocation.getChunk().getZ())) {
			villager.teleport(newLocation);
			FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.x",
					newLocation.getX());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.y",
					newLocation.getY());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.z",
					newLocation.getZ());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager.world",
					newLocation.getWorld().getName());
			save(townImpl.getTownworld().getSaveFile(), config);
		} else {
			throw PlayerException.getException(PlayerExceptionMessageEnum.OUTSIDE_OF_THE_PLOT);
		}
	}

	/**
	 * Opens the inventory of the saleManager.
	 */
	public void openSaleVillagerInv(Player player) {
		player.openInventory(inventory);
	}

	public EconomyPlayer getOwner() {
		return owner;
	}

	public void setOwner(EconomyPlayer player) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
		config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".owner", player.getName());
		this.owner = player;
		save(townImpl.getTownworld().getSaveFile(), config);
	}

	/**
	 * Get a list of all residents of this plot.
	 * 
	 * @return List
	 */
	public List<EconomyPlayer> getResidents() {
		return residents;
	}

	/**
	 * Set the list of residents of this plot.
	 * 
	 * @param residents
	 */
	public void setResidents(List<EconomyPlayer> residents) {
		this.residents = residents;
	}

	/**
	 * Add a residents to this plot.
	 * 
	 * @param player
	 * @throws TownSystemException
	 */
	public void addResident(EconomyPlayer player) throws TownSystemException {
		if (!residents.contains(player)) {
			residents.add(player);
			FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
			List<String> list = config
					.getStringList("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".coOwners");
			list.add(player.getName());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".coOwners", list);
			save(townImpl.getTownworld().getSaveFile(), config);
		} else {
			throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_ALREADY_RESIDENT);
		}
	}

	public void removeResident(EconomyPlayer player) throws TownSystemException {
		if (residents.contains(player)) {
			residents.remove(player);
			FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
			List<String> list = config
					.getStringList("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".coOwners");
			list.remove(player.getName());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".coOwners", list);
			save(townImpl.getTownworld().getSaveFile(), config);
		} else {
			throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_NO_RESIDENT);
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

	public boolean isOwner(EconomyPlayer owner) {
		if (this.owner.equals(owner)) {
			return true;
		} else {
			return false;
		}
	}

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

	public boolean isForSale() {
		return isForSale;
	}

	public void removeFromSale(EconomyPlayer owner) throws TownSystemException {
		if (isOwner(owner)) {
			setIsForSale(false);
			FileConfiguration config = YamlConfiguration.loadConfiguration(townImpl.getTownworld().getSaveFile());
			config.set("Towns." + townImpl.getTownName() + ".Plots." + chunkCoords + ".SaleVillager", null);
			World world = villager.getLocation().getWorld();
			villager.remove();
			world.save();
			save(townImpl.getTownworld().getSaveFile(), config);
		} else {
			throw TownSystemException.getException(TownExceptionMessageEnum.PLAYER_IS_NOT_OWNER);
		}
	}

	public void setForSale(double salePrice, Location playerLocation, EconomyPlayer player,boolean sendMessage) throws TownSystemException, PlayerException {
		if (isOwner(player)) {
			if (this.isForSale) {
				throw TownSystemException.getException(TownExceptionMessageEnum.PLOT_IS_ALREADY_FOR_SALE);
			} else {
				setSalePrice(salePrice);
				spawnSaleVillagerWithSaving(playerLocation);
				setIsForSale(true);
				if(player.isOnline() && sendMessage) {
					player.getPlayer().sendMessage(MessageWrapper.getString("town_plot_setForSale"));
				}
			}
		} else {
			throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
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
