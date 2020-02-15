package com.ue.townsystem.townworld.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayerController;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.town.api.TownController;
import com.ue.townsystem.townworld.impl.TownworldImpl;
import com.ue.ultimate_economy.UltimateEconomy;

public class TownworldController {

    private static List<Townworld> townWorldList = new ArrayList<>();

    /**
     * This method returns a townworld by it's name.
     * 
     * @param name
     * @return Townworld
     * @throws TownSystemException
     */
    public static Townworld getTownWorldByName(String name) throws TownSystemException {
	for (Townworld townworld : townWorldList) {
	    if (townworld.getWorldName().equals(name)) {
		return townworld;
	    }
	}
	throw TownSystemException.getException(TownExceptionMessageEnum.TOWNWORLD_DOES_NOT_EXIST);
    }

    /**
     * This method returns a list of all townworlds.
     * 
     * @return List of TownWorlds
     */
    public static List<Townworld> getTownWorldList() {
	return townWorldList;
    }

    /**
     * This method despawns all town villager in this townworld.
     */
    public static void despawnAllVillagers() {
	for (Townworld townworld : townWorldList) {
	    townworld.despawnAllTownVillagers();
	}
    }

    /**
     * Returns a list of all townworld names.
     * 
     * @return list of strings
     */
    public static List<String> getTownWorldNameList() {
	List<String> nameList = new ArrayList<>();
	for (Townworld townworld : townWorldList) {
	    nameList.add(townworld.getWorldName());
	}
	return nameList;
    }

    /**
     * This method returns true, if the world is an townworld.
     * 
     * @param worldName
     * @return boolean
     */
    public static boolean isTownWorld(String worldName) {
	if (getTownWorldNameList().contains(worldName)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Handles the townworld location check.
     * 
     * @param worldname
     * @param chunk
     * @param playername
     */
    public static void handleTownWorldLocationCheck(String worldname, Chunk chunk, String playername) {
	try {
	    BossBar bossbar = EconomyPlayerController.getEconomyPlayerByName(playername).getBossBar();
	    try {
		Townworld townworld = getTownWorldByName(worldname);
		try {
		    Town town = townworld.getTownByChunk(chunk);
		    bossbar.setTitle(town.getTownName());
		    bossbar.setColor(BarColor.RED);
		    bossbar.setVisible(true);
		} catch (TownSystemException e) {
		    // if chunk is in the wilderness
		    bossbar.setTitle("Wilderness");
		    bossbar.setColor(BarColor.GREEN);
		    bossbar.setVisible(true);
		}
	    } catch (TownSystemException e) {
		// disable bossbar in other worlds
		bossbar.setVisible(false);
	    }
	} catch (PlayerException e) {
	    // should never happen
	}
    }

    /**
     * This method should be used to create/enble a new townworld.
     * 
     * @param mainDataFolder
     * @param world
     * @throws TownSystemException
     */
    public static void createTownWorld(File mainDataFolder, String world) throws TownSystemException {
	if (Bukkit.getWorld(world) == null) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST, world);
	} else if (isTownWorld(world)) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.TOWNWORLD_ALREADY_EXIST);
	} else {
	    townWorldList.add(new TownworldImpl(mainDataFolder, world));
	    UltimateEconomy.getInstance.getConfig().set("TownWorlds", TownworldController.getTownWorldNameList());
	    UltimateEconomy.getInstance.saveConfig();
	}
    }

    /**
     * This method should be used to delete/disable a townworld.
     * 
     * @param world
     * @throws TownSystemException
     * @throws PlayerException
     * @throws GeneralEconomyException
     */
    public static void deleteTownWorld(String world)
	    throws TownSystemException, PlayerException, GeneralEconomyException {
	if (Bukkit.getWorld(world) == null) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST, world);
	} else {
	    Townworld townworld = getTownWorldByName(world);
	    for (Town town : townworld.getTownList()) {
		TownController.dissolveTown(town, town.getMayor());
	    }
	    townWorldList.remove(townworld);
	    townworld.delete();
	    UltimateEconomy.getInstance.getConfig().set("TownWorlds", TownworldController.getTownWorldNameList());
	    UltimateEconomy.getInstance.saveConfig();
	}
    }

    /**
     * This method loads all townworlds from the save file. Loads all towns and
     * plots in the townworld as well. EconomyPlayers have to be loaded first.
     * 
     * @param mainDataFolder
     * @param fileConfig
     */
    public static void loadAllTownWorlds(File mainDataFolder, FileConfiguration fileConfig) {
	for (String townWorldName : fileConfig.getStringList("TownWorlds")) {
	    try {
		TownworldImpl townworldImpl = new TownworldImpl(mainDataFolder, townWorldName);
		List<Town> towns = new ArrayList<>();
		for (String townName : townworldImpl.getTownNameList()) {
		    towns.add(TownController.loadTown(townworldImpl, townName));
		}
		townworldImpl.setTownList(towns);
		townWorldList.add(townworldImpl);
	    } catch (TownSystemException | PlayerException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
		e.printStackTrace();
	    }
	}
    }
}
