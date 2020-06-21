package com.ue.townsystem.townworld.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.town.api.Plot;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.town.api.TownController;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.ultimate_economy.UltimateEconomy;

public class TownworldImpl implements Townworld {

	private double foundationPrice, expandPrice;
	private final String worldName;
	private File file;
	private List<String> townNames;
	private List<Town> towns;

	/**
	 * Represents a townworld.
	 * 
	 * @param world
	 */
	public TownworldImpl(String world) {
		worldName = world;
		towns = new ArrayList<>();
		file = new File(UltimateEconomy.getInstance.getDataFolder(), world + "_TownWorld" + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
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

	@Override
	public void delete() throws TownSystemException, PlayerException, GeneralEconomyException {
		file.delete();
		despawnAllTownVillagers();
		List<Town> listCopy = new ArrayList<>(getTownList());
		Iterator<Town> iter = listCopy.iterator();
		while(iter.hasNext()) {
			Town town = iter.next();
			TownController.dissolveTown(town, town.getMayor());
		}
	}

	@Override
	public void addTown(Town town) throws GeneralEconomyException {
		if (townNames.contains(town.getTownName())) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS,
					town.getTownName());
		} else {
			towns.add(town);
			townNames.add(town.getTownName());
		}
	}

	@Override
	public void removeTown(Town town) throws GeneralEconomyException {
		if (!townNames.contains(town.getTownName())) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST,
					town.getTownName());
		} else {
			towns.remove(town);
			townNames.remove(town.getTownName());
		}
	}

	@Override
	public void despawnAllTownVillagers() {
		for (Town town : towns) {
			town.despawnAllVillagers();
		}
	}

	@Override
	public List<String> getTownNameList() {
		return townNames;
	}

	public void setTownNameList(List<String> names) {
		townNames = names;
	}

	@Override
	public double getFoundationPrice() {
		return foundationPrice;
	}

	/**
	 * Set the town list. Not necessary if you load the townworld with the
	 * TownworldController.
	 * 
	 * @param towns
	 */
	public void setTownList(List<Town> towns) {
		this.towns = towns;
	}

	@Override
	public void setFoundationPrice(double foundationPrice, boolean saving) {
		this.foundationPrice = foundationPrice;
		if (saving) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Config.foundationPrice", foundationPrice);
			save(config);
		}
	}

	@Override
	public double getExpandPrice() {
		return expandPrice;
	}

	@Override
	public void setExpandPrice(double expandPrice, boolean saving) {
		this.expandPrice = expandPrice;
		if (saving) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Config.expandPrice", expandPrice);
			save(config);
		}
	}

	@Override
	public String getWorldName() {
		return worldName;
	}

	@Override
	public Town getTownByName(String townName) throws GeneralEconomyException {
		for (Town town : towns) {
			if (town.getTownName().equals(townName)) {
				return town;
			}
		}
		throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, townName);
	}

	@Override
	public boolean isChunkFree(Chunk chunk) {
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

	@Override
	public List<Town> getTownList() {
		return towns;
	}

	@Override
	public Town getTownByChunk(Chunk chunk) throws TownSystemException {
		for (Town town : towns) {
			if (town.isClaimedByTown(chunk)) {
				return town;
			}
		}
		throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_NOT_CLAIMED);
	}

	@Override
	public File getSaveFile() {
		return file;
	}

	private void save(FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleTownVillagerInvClick(InventoryClickEvent event)
			throws TownSystemException, PlayerException, GeneralEconomyException {
		Chunk chunk = ((Villager) event.getClickedInventory().getHolder()).getLocation().getChunk();
		EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName(event.getWhoClicked().getName());
		Town town = getTownByChunk(chunk);
		Plot plot = town.getPlotByChunk(chunk.getX() + "/" + chunk.getZ());
		switch (event.getCurrentItem().getItemMeta().getDisplayName()) {
		case "Buy":
			if (!ecoPlayer.hasEnoughtMoney(plot.getSalePrice())) {
				throw PlayerException.getException(PlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
			} else {
				EconomyPlayer receiver = plot.getOwner();
				ecoPlayer.payToOtherPlayer(receiver, plot.getSalePrice(), false);
				town.buyPlot(ecoPlayer, chunk.getX(), chunk.getZ());
				event.getWhoClicked().sendMessage(ChatColor.GOLD + "Congratulation! You bought this plot!");
			}
			break;
		case "Cancel Sale":
			if (plot.isOwner(ecoPlayer)) {
				plot.removeFromSale(ecoPlayer);
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
		default:
			break;
		}
		event.getWhoClicked().closeInventory();
	}
}
