package com.ue.townsystem.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.townsystem.dataaccess.api.TownsystemDao;
import com.ue.townsystem.impl.TownImpl;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

public class TownManagerImpl {

	@Inject 
	MessageWrapper messageWrapper;
	private List<String> townNameList = new ArrayList<>();
	private final TownsystemValidationHandler townsystemValidationHandler;
	private final EconomyPlayerValidationHandler ecoPlayerValidationHandler;

	/**
	 * Inject constructor.
	 * 
	 * @param townsystemValidationHandler
	 * @param ecoPlayerValidationHandler
	 */
	@Inject
	public TownManagerImpl(TownsystemValidationHandler townsystemValidationHandler,
			EconomyPlayerValidationHandler ecoPlayerValidationHandler) {
		this.townsystemValidationHandler = townsystemValidationHandler;
		this.ecoPlayerValidationHandler = ecoPlayerValidationHandler;
	}

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
		townsystemValidationHandler.checkForTownDoesNotExist(getTownNameList(), townName);
		checkForChunkIsFree(townworld, location);
		ecoPlayerValidationHandler.checkForNotReachedMaxJoinedTowns(player.reachedMaxJoinedTowns());
		ecoPlayerValidationHandler.checkForEnoughMoney(player.getBankAccount(), townworld.getFoundationPrice(), true);
		TownImpl townImpl = new TownImpl(townworld, player, townName, location);
		townworld.addTown(townImpl);
		FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
		townNameList.add(townName);
		config.set("TownNames", townNameList);
		save(townworld.getSaveFile(), config);
		player.decreasePlayerAmount(townworld.getFoundationPrice(), true);
		for (Player p : Bukkit.getOnlinePlayers()) {
			TownworldManagerImpl.performTownWorldLocationCheck(p.getWorld().getName(), p.getLocation().getChunk(),
					p.getName());
		}
	}

	private void checkForChunkIsFree(Townworld townworld, Location location) throws TownSystemException {
		if (!townworld.isChunkFree(location.getChunk())) {
			throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
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
				TownworldManagerImpl.performTownWorldLocationCheck(p.getWorld().getName(), p.getLocation().getChunk(),
						p.getName());
			}
		} else {
			throw EconomyPlayerException.getException(EconomyPlayerExceptionMessageEnum.TOWN_NOT_TOWN_OWNER);
		}
	}

	/**
	 * Method for loading a existing town by name. EconomyPlayers and bank accounts
	 * have to be loaded first.
	 * 
	 * @param townsystemDao
	 * @param townworld
	 * @param townName
	 * @return Town
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 * @throws GeneralEconomyException 
	 */
	public Town loadTown(TownsystemDao townsystemDao, Townworld townworld, String townName)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		TownImpl townImpl = new TownImpl(townsystemDao, townworld, townName);
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
	 * towns with the TownController.
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
		for (Townworld world : TownworldManagerImpl.getTownWorldList()) {
			for (Town town : world.getTownList()) {
				if (town.getTownName().equals(name)) {
					return town;
				}
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}
}
