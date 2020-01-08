package com.ue.player.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.player.impl.EconomyPlayerImpl;

public class EconomyPlayerController {
	
	private static List<EconomyPlayer> economyPlayers = new ArrayList<>();
	private static File playerFile;
	private static int maxHomes;
	private static int maxJobs;
	private static int maxJoinedTowns;

	/**
	 * This method returns a list of all player names.
	 * 
	 * @return
	 */
	public static List<String> getEconomyPlayerNameList() {
		List<String> list = new ArrayList<>();
		for (EconomyPlayer economyPlayer: economyPlayers) {
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
		throw new PlayerException(PlayerException.PLAYER_DOES_NOT_EXIST);
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
			throw new PlayerException(PlayerException.PLAYER_ALREADY_EXIST);
		} else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			economyPlayers.add(new EconomyPlayerImpl(playerName, true));
			config.set("Player", getEconomyPlayerNameList());
			try {
				config.save(playerFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method loads all economyPlayers. 
	 * 
	 * !!! {@link JobController#loadAllJobs()} have to be executed before this method. !!!
	 * 
	 * @param dataFolder
	 * @throws JobSystemException
	 */
	public static void loadAllEconomyPlayers(File dataFolder) throws JobSystemException {
		playerFile = new File(dataFolder, "PlayerFile.yml");
		if (!playerFile.exists()) {
			try {
				playerFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			List<String> playerList = config.getStringList("Player");
			for (String player : playerList) {
				economyPlayers.add(new EconomyPlayerImpl(player, false));
			}
		}
	}

	public static void setupConfig(FileConfiguration fileConfig) {
		if (!fileConfig.isSet("MaxHomes")) {
			fileConfig.set("MaxHomes", 3);
			maxHomes = 3;
		} else {
			maxHomes = fileConfig.getInt("MaxHomes");
		}
		if (!fileConfig.isSet("MaxJobs")) {
			fileConfig.set("MaxJobs", 2);
			maxJobs = 2;
		} else {
			maxJobs = fileConfig.getInt("MaxJobs");
		}
		if (!fileConfig.isSet("MaxJoinedTowns")) {
			fileConfig.set("MaxJoinedTowns", 1);
			maxJoinedTowns = 1;
		} else {
			maxJoinedTowns = fileConfig.getInt("MaxJoinedTowns");
		}
	}

	/**
	 * This method sets the maxHomes value.
	 * 
	 * @param config
	 * @param value
	 * @throws PlayerException
	 */
	public static void setMaxHomes(FileConfiguration config, int value) throws PlayerException {
		if (value < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		} else {
			config.set("MaxHomes", value);
			maxHomes = value;
		}
	}
	
	/**
	 * Returns the max homes configuration.
	 * 
	 * @return int
	 */
	public static int getMaxHomes() {
		return maxHomes;
	}

	/**
	 * This method sets the maxJobs value.
	 * 
	 * @param config
	 * @param value
	 * @throws PlayerException
	 */
	public static void setMaxJobs(FileConfiguration config, int value) throws PlayerException {
		if (value < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		} else {
			config.set("MaxJobs", value);
			maxJobs = value;
		}
	}
	
	/**
	 * Returns the max jobs configuration.
	 * 
	 * @return int
	 */
	public static int getMaxJobs() {
		return maxJobs;
	}

	/**
	 * This method sets the maxJoinedTowns value.
	 * 
	 * @param config
	 * @param value
	 * @throws PlayerException
	 */
	public static void setMaxJoinedTowns(FileConfiguration config, int value) throws PlayerException {
		if (value < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		} else {
			config.set("MaxJoinedTowns", value);
			maxJoinedTowns = value;
		}
	}
	
	/**
	 * Returns the max joined towns configuration.
	 * 
	 * @return int
	 */
	public static int getMaxJoinedTowns() {
		return maxJoinedTowns;
	}
}
