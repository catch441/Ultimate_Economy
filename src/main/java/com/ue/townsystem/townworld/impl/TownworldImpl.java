package com.ue.townsystem.townworld.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.EconomyPlayer;
import com.ue.townsystem.town.api.Plot;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.town.api.TownController;
import com.ue.townsystem.townworld.api.Townworld;

public class TownworldImpl implements Townworld {

	private double foundationPrice, expandPrice;
	private final String worldName;
	private File file;
	private List<String> townNames;
	private List<Town> towns;

	/**
	 * Represents a townworld.
	 * 
	 * @param main
	 * @param world
	 */
	public TownworldImpl(File mainDataFolder, String world) {
		worldName = world;
		towns = new ArrayList<>();
		file = new File(mainDataFolder, world + "_TownWorld" + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			foundationPrice = 0;
			expandPrice = 0;
			config.set("World", world);
			config.set("Config.foundationPrice", 0);
			config.set("Config.expandPrice", 0);
			townNames = new ArrayList<>();
			save(config);
		} else {
			townNames = config.getStringList("TownNames");
			foundationPrice = config.getDouble("Config.foundationPrice");
			expandPrice = config.getDouble("Config.expandPrice");
		}
	}

	public void delete() {
		file.delete();
		despawnAllTownVillagers();
		for (EconomyPlayer ecoPlayer : EconomyPlayer.getAllEconomyPlayers()) {
			if (!ecoPlayer.getJoinedTownList().isEmpty()) {
				for (String townName : townNames) {
					if (ecoPlayer.getJoinedTownList().contains(townName)) {
						try {
							ecoPlayer.removeJoinedTown(townName);
						} catch (PlayerException e) {
						}
					}
				}
			}
		}
		for (String townName : townNames) {
			TownController.getTownNameList().remove(townName);
		}
	}
	
	public void addTown(Town town) throws TownSystemException {
		if(townNames.contains(town.getTownName())) {
			throw new TownSystemException(TownSystemException.TOWN_ALREADY_EXIST);
		} else {
			towns.add(town);
			townNames.add(town.getTownName());
		}
	}
	
	public void removeTown(Town town) throws TownSystemException {
		if(!townNames.contains(town.getTownName())) {
			throw new TownSystemException(TownSystemException.TOWN_DOES_NOT_EXIST);
		} else {
			towns.remove(town);
			townNames.remove(town.getTownName());
		}
	}

	public void despawnAllTownVillagers() {
		for (Town town : towns) {
			town.despawnAllVillagers();
		}
	}

	public List<String> getTownNameList() {
		return townNames;
	}
	
	public void setTownNameList(List<String> names) {
		townNames = names;
	}

	public double getFoundationPrice() {
		return foundationPrice;
	}

	/**
	 * Set the town list. Not necessary if you load the townworld with the TownworldController.
	 * 
	 * @param townImpls
	 */
	public void setTownList(List<Town> towns) {
		this.towns = towns;
	}

	public void setFoundationPrice(double foundationPrice, boolean saving) {
		this.foundationPrice = foundationPrice;
		if (saving) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Config.foundationPrice", foundationPrice);
			save(config);
		}
	}

	public double getExpandPrice() {
		return expandPrice;
	}

	public void setExpandPrice(double expandPrice, boolean saving) {
		this.expandPrice = expandPrice;
		if (saving) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Config.expandPrice", expandPrice);
			save(config);
		}
	}

	public String getWorldName() {
		return worldName;
	}

	public Town getTownByName(String townName) throws TownSystemException {
		for (Town town : towns) {
			if (town.getTownName().equals(townName)) {
				return town;
			}
		}
		throw new TownSystemException(TownSystemException.TOWN_DOES_NOT_EXIST);
	}

	public boolean chunkIsFree(Chunk chunk) {
		boolean isFree = true;
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String chunkCoords = chunk.getX() + "/" + chunk.getZ();
		for (String name : townNames) {
			if (config.getStringList("Towns." + name + ".chunks").contains(chunkCoords)) {
				isFree = false;
				break;
			}
		}
		return isFree;
	}

	/**
	 * Returns the townlist.
	 * 
	 * @return List
	 */
	public List<Town> getTownList() {
		return towns;
	}

	public Town getTownByChunk(Chunk chunk) throws TownSystemException {
		for (Town town : towns) {
			if (town.isClaimedByTown(chunk)) {
				return town;
			}
		}
		throw new TownSystemException(TownSystemException.CHUNK_NOT_CLAIMED);
	}

	public File getSaveFile() {
		return file;
	}

	public void setSaveFile(File file) {
		this.file = file;
	}

	private void save(FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleTownVillagerInvClick(InventoryClickEvent event) throws TownSystemException, PlayerException {
		Chunk chunk = event.getWhoClicked().getLocation().getChunk();
		String playerName = event.getWhoClicked().getName();
		EconomyPlayer ecoPlayer = EconomyPlayer.getEconomyPlayerByName(playerName);
		Town town = getTownByChunk(chunk);
		Plot plot = town.getPlotByChunk(chunk.getX() + "/" + chunk.getZ());
		switch (event.getCurrentItem().getItemMeta().getDisplayName()) {
			case "Buy":
				if (!ecoPlayer.hasEnoughtMoney(plot.getSalePrice())) {
					throw new PlayerException(PlayerException.NOT_ENOUGH_MONEY_PERSONAL);
				} else {
					String receiverName = plot.getOwner();
					EconomyPlayer receiver = EconomyPlayer.getEconomyPlayerByName(receiverName);
					ecoPlayer.payToOtherPlayer(receiver, plot.getSalePrice());
					town.buyPlot(playerName, chunk.getX(), chunk.getZ());
					event.getWhoClicked().sendMessage(ChatColor.GOLD + "Congratulation! You bought this plot!");
				}
				break;
			case "Cancel Sale":
				if (plot.isOwner(playerName)) {
					plot.removeFromSale(playerName);
					event.getWhoClicked().sendMessage(ChatColor.GOLD + "You removed this plot from sale!");
				}
				break;
			case "Join":
				town.joinTown(ecoPlayer);
				event.getWhoClicked().sendMessage(ChatColor.GOLD + "You joined the town " + town.getTownName() + ".");
				break;
			case "Leave":
				town.leaveTown(ecoPlayer);
				event.getWhoClicked().sendMessage(ChatColor.GOLD + "You left the town " + town.getTownName() + ".");
				break;
		}
	}
}
