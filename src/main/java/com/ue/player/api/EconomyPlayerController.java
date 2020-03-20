package com.ue.player.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.player.impl.EconomyPlayerImpl;

public class EconomyPlayerController {

    private static List<EconomyPlayer> economyPlayers = new ArrayList<>();
    private static File playerFile;

    /**
     * This method returns a list of all player names.
     * 
     * @return list of player names
     */
    public static List<String> getEconomyPlayerNameList() {
	List<String> list = new ArrayList<>();
	for (EconomyPlayer economyPlayer : economyPlayers) {
	    list.add(economyPlayer.getName());
	}
	return list;
    }

    /**
     * This method returns the player save file.
     * 
     * @return File
     */
    public static File getPlayerFile() {
	return playerFile;
    }

    /**
     * This method sets the player save file.
     * 
     * @param file
     */
    public static void setPlayerFile(File file) {
	playerFile = file;
    }

    /**
     * This method returns a economyplayer by it's name.
     * 
     * @param name
     * @return EconomyPlayer
     * @throws PlayerException
     */
    public static EconomyPlayer getEconomyPlayerByName(String name) throws PlayerException {
	for (EconomyPlayer economyPlayer : economyPlayers) {
	    if (economyPlayer.getName().equals(name)) {
		return economyPlayer;
	    }
	}
	throw PlayerException.getException(PlayerExceptionMessageEnum.PLAYER_DOES_NOT_EXIST);
    }

    /**
     * This method returns all economyPlayers.
     * 
     * @return List of EcnomyPlayers
     */
    public static List<EconomyPlayer> getAllEconomyPlayers() {
	return economyPlayers;
    }

    /**
     * This method should me used to create a new EconomyPlayer.
     * 
     * @param playerName
     * @throws PlayerException
     */
    public static void createEconomyPlayer(String playerName) throws PlayerException {
	if (getEconomyPlayerNameList().contains(playerName)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.PLAYER_ALREADY_EXIST);
	} else { 
	    economyPlayers.add(new EconomyPlayerImpl(playerName, true));
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
	    config.set("Player", getEconomyPlayerNameList());
	    try {
		config.save(playerFile);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * This method loads all economyPlayers. !!! 
     * The jobs have to be loaded first.
     * The banc accounts have to be loaded first.i
     * @param dataFolder
     */
    public static void loadAllEconomyPlayers(File dataFolder) {
	playerFile = new File(dataFolder, "PlayerFile.yml");
	if (!playerFile.exists()) {
	    try {
		playerFile.createNewFile();
	    } catch (IOException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the playerfile");
	    }
	} else {
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
	    List<String> playerList = config.getStringList("Player");
	    for (String player : playerList) {
		economyPlayers.add(new EconomyPlayerImpl(player, false));
	    }
	}
    }
}
