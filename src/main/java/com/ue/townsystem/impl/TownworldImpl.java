package com.ue.townsystem.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.api.TownController;
import com.ue.townsystem.dataaccess.api.TownsystemDao;
import com.ue.townsystem.dataaccess.impl.TownsystemDaoImpl;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;
import com.ue.ultimate_economy.UltimateEconomy;

public class TownworldImpl implements Townworld {

	private double foundationPrice, expandPrice;
	private final String worldName;
	private Map<String, Town> towns = new HashMap<>();;
	private final TownsystemDao townsystemDao;

	/**
	 * Represents a townworld.
	 * 
	 * @param world
	 */
	public TownworldImpl(String world) {
		townsystemDao = new TownsystemDaoImpl(world);
		
		worldName = world;
		if (!file.exists()) {

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
	public void delete() throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
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
			towns.put(town.getTownName(), town);
		}
	}

	@Override
	public void removeTown(Town town) throws GeneralEconomyException {
		if (!townNames.contains(town.getTownName())) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST,
					town.getTownName());
		} else {
			towns.remove(town.getTownName());
		}
	}

	@Override
	public void despawnAllTownVillagers() {
		for (Entry<String, Town> town : towns.entrySet()) {
			town.getValue().despawnAllVillagers();
		}
	}

	@Override
	public List<String> getTownNameList() {
		return new ArrayList<>(towns.keySet());
	}

	@Override
	public double getFoundationPrice() {
		return foundationPrice;
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
}
