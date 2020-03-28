package com.ue.player.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.bank.api.BankController;
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
	checkForPlayerDoesNotExist(playerName);
	economyPlayers.add(new EconomyPlayerImpl(playerName, true));
	YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
	config.set("Player", getEconomyPlayerNameList());
	save(config);
    }

    /**
     * Deletes a economy player.
     * 
     * @param player
     */
    public static void deleteEconomyPlayer(EconomyPlayer player) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
	economyPlayers.remove(player);
	config.set("Player", getEconomyPlayerNameList());
	config.set(player.getName(), null);
	save(config);
	BankController.deleteBankAccount(player.getBankAccount());
	// to remove all references
	player = null;
    }

    /**
     * This method loads all economyPlayers. !!! The jobs have to be loaded first.
     * The banc accounts have to be loaded first.i
     * 
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

    private static void save(YamlConfiguration config) {
	try {
	    config.save(playerFile);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    /*
     * Validation methods
     * 
     */
    
    private static void checkForPlayerDoesNotExist(String playerName) throws PlayerException {
	if (getEconomyPlayerNameList().contains(playerName)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.PLAYER_ALREADY_EXIST);
	}
    }
}
