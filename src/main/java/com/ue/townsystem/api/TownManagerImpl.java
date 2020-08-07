package com.ue.townsystem.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.impl.TownImpl;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

public class TownController {

	private List<String> townNameList = new ArrayList<>();

	/**
	 * Creates a new town if player has enough money. Player money decreases if
	 * player has enough money.
	 * 
	 * @param townworld
	 * @param townName
	 * @param location
	 * @param player    the player who wants to be the mayor of the town
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 * @throws GeneralEconomyException
	 */
	public void createTown(Townworld townworld, String townName, Location location, EconomyPlayer player)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		checkForTownDoesNotExist(townName);
		checkForChunkIsFree(townworld, location);
		checkForPlayerHasEnoughMoney(townworld, player);
		checkForMaxJoinedTownsNotReached(player);
		TownImpl townImpl = new TownImpl(townworld, player, townName, location);
		townworld.addTown(townImpl);
		FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
		townNameList.add(townName);
		config.set("TownNames", townNameList);
		save(townworld.getSaveFile(), config);
		player.decreasePlayerAmount(townworld.getFoundationPrice(), true);
		for (Player p : Bukkit.getOnlinePlayers()) {
			TownworldController.handleTownWorldLocationCheck(p.getWorld().getName(), p.getLocation().getChunk(),
					p.getName());
		}
	}

	private void checkForMaxJoinedTownsNotReached(EconomyPlayer player) throws EconomyPlayerException {
		if (player.reachedMaxJoinedTowns()) {
			throw EconomyPlayerException.getException(EconomyPlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	private void checkForPlayerHasEnoughMoney(Townworld townworld, EconomyPlayer player)
			throws EconomyPlayerException, GeneralEconomyException {
		if (!player.hasEnoughtMoney(townworld.getFoundationPrice())) {
			throw EconomyPlayerException.getException(EconomyPlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
		}
	}

	private void checkForChunkIsFree(Townworld townworld, Location location) throws TownSystemException {
		if (!townworld.isChunkFree(location.getChunk())) {
			throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
		}
	}

	private void checkForTownDoesNotExist(String townName) throws GeneralEconomyException {
		if (townNameList.contains(townName)) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, townName);
		}
	}

	/**
	 * Dissolves a entire town. The Chunks are not resettet.
	 * 
	 * @param town
	 * @param player
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 * @throws GeneralEconomyException
	 */
	public void dissolveTown(Town town, EconomyPlayer player)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		if (town.isMayor(player)) {
			List<EconomyPlayer> tList = new ArrayList<>();
			tList.addAll(town.getCitizens());
			for (EconomyPlayer citizen : tList) {
				citizen.removeJoinedTown(town.getTownName());
			}
			town.despawnAllVillagers();
			town.getTownworld().removeTown(town);
			townNameList.remove(town.getTownName());
			FileConfiguration config = YamlConfiguration.loadConfiguration(town.getTownworld().getSaveFile());
			config.set("Towns." + town.getTownName(), null);
			config.set("TownNames", townNameList);
			save(town.getTownworld().getSaveFile(), config);
			for (Player p : Bukkit.getOnlinePlayers()) {
				TownworldController.handleTownWorldLocationCheck(p.getWorld().getName(), p.getLocation().getChunk(),
						p.getName());
			}
		} else {
			throw EconomyPlayerException.getException(EconomyPlayerExceptionMessageEnum.TOWN_NOT_TOWN_OWNER);
		}
	}

	/**
	 * Static method for loading a existing town by name. EconomyPlayers and bank accounts have to be
	 * loaded first.
	 * 
	 * @param townworld
	 * @param townName
	 * @return Town
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 */
	public Town loadTown(Townworld townworld, String townName) throws TownSystemException, EconomyPlayerException {
		TownImpl townImpl = new TownImpl(townworld, townName);
		townNameList.add(townName);
		return townImpl;
	}

	/**
	 * This method returns the townNameList.
	 * 
	 * @return List of Strings
	 */
	public List<String> getTownNameList() {
		return townNameList;
	}

	/**
	 * Sets and saves the townName list. Do not use this if you create and load
	 * towns woth the TownController.
	 * 
	 * @param townworld
	 * @param townNames
	 */
	public void setAndSaveTownNameList(Townworld townworld, List<String> townNames) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
		townNameList = townNames;
		config.set("TownNames", townNameList);
		save(townworld.getSaveFile(), config);
	}

	/**
	 * Returns a town by it's name.
	 * 
	 * @param name
	 * @return town
	 * @throws GeneralEconomyException
	 */
	public Town getTown(String name) throws GeneralEconomyException {
		for (Townworld world : TownworldController.getTownWorldList()) {
			for (Town town : world.getTownList()) {
				if (town.getTownName().equals(name)) {
					return town;
				}
			}
		}
		throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}
}
