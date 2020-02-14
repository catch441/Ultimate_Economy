package com.ue.townsystem.town.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.townsystem.town.impl.TownImpl;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.townsystem.townworld.api.TownworldController;

public class TownController {

    private static List<String> townNameList = new ArrayList<>();

    /**
     * Creates a new town if player has enough money. Player money decreases if
     * player has enough money.
     * 
     * @param townworld
     * @param townName
     * @param location
     * @param player
     *            the player who wants to be the mayor of the town
     * @throws TownSystemException
     * @throws PlayerException
     * @throws GeneralEconomyException
     */
    public static void createTown(Townworld townworld, String townName, Location location, EconomyPlayer player)
	    throws TownSystemException, PlayerException, GeneralEconomyException {
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
	player.addJoinedTown(townName);
	player.decreasePlayerAmount(townworld.getFoundationPrice(), true);
	for (Player p : Bukkit.getOnlinePlayers()) {
	    TownworldController.handleTownWorldLocationCheck(p.getWorld().getName(), p.getLocation().getChunk(),
		    p.getName());
	}
    }

    private static void checkForMaxJoinedTownsNotReached(EconomyPlayer player) throws PlayerException {
	if (player.reachedMaxJoinedTowns()) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
	}
    }

    private static void checkForPlayerHasEnoughMoney(Townworld townworld, EconomyPlayer player) throws PlayerException {
	if (!player.hasEnoughtMoney(townworld.getFoundationPrice())) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
	}
    }

    private static void checkForChunkIsFree(Townworld townworld, Location location)
	    throws TownSystemException {
	if (!townworld.isChunkFree(location.getChunk())) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED);
	}
    }

    private static void checkForTownDoesNotExist(String townName) throws TownSystemException {
	if (townNameList.contains(townName)) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.TOWN_ALREADY_EXIST);
	}
    }

    /**
     * Dissolves a entire town. The Chunks are not resettet.
     * 
     * @param town
     * @param player
     * @throws TownSystemException
     * @throws PlayerException
     */
    public static void dissolveTown(Town town, EconomyPlayer player) throws TownSystemException, PlayerException {
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
	    throw PlayerException.getException(PlayerExceptionMessageEnum.TOWN_NOT_TOWN_OWNER);
	}
    }

    /**
     * Static method for loading a existing town by name. EconomyPlayers have to be
     * loaded first.
     * 
     * @param townworld
     * @param townName
     * @return Town
     * @throws TownSystemException
     * @throws PlayerException
     */
    public static Town loadTown(Townworld townworld, String townName) throws TownSystemException, PlayerException {
	TownImpl townImpl = new TownImpl(townworld, townName);
	townNameList.add(townName);
	return townImpl;
    }

    /**
     * This method returns the townNameList.
     * 
     * @return List of Strings
     */
    public static List<String> getTownNameList() {
	return townNameList;
    }

    /**
     * Sets and saves the townName list. Do not use this if you create and load
     * towns woth the TownController.
     * 
     * @param townworld
     * @param townNames
     */
    public static void setAndSaveTownNameList(Townworld townworld, List<String> townNames) {
	FileConfiguration config = YamlConfiguration.loadConfiguration(townworld.getSaveFile());
	townNameList = townNames;
	config.set("TownNames", townNameList);
	save(townworld.getSaveFile(), config);
    }

    /**
     * Saves a config in a file.
     * 
     * @param config
     */
    private static void save(File file, FileConfiguration config) {
	try {
	    config.save(file);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
