package com.ue.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.Job;

public class EconomyPlayer {
	
	private static List<EconomyPlayer> economyPlayers = new ArrayList<>();
	private static File playerFile;

	private Map<String,Location> homes;
	private double account;
	private String name;
	private List<Job> jobs;
	private boolean scoreBoardDisabled;
	
	/**
	 * Constructor for creating a new economyPlayer.
	 * 
	 * @param name
	 */
	private EconomyPlayer(String name) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
		jobs = new ArrayList<>();
		homes = new HashMap<>();
		this.name = name;
		scoreBoardDisabled = true;
		account = 0.0;	
		config.set(name + ".bank", scoreBoardDisabled);
		config.set(name + ".account amount", account);
		save(config);
	}
	
	/**
	 * Constructor for loading an existing economyPlayer.
	 * 
	 * @param name
	 * @param jobList
	 */
	private EconomyPlayer(String name,List<Job> jobList) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
		homes = new HashMap<>();
		this.name = name;
		scoreBoardDisabled = config.getBoolean(name + ".bank");
		account = config.getDouble(name + ".account amount");
		jobs = jobList;
		List<String> homeNameList = config.getStringList(name + ".Home.Homelist");
		for(String homeName:homeNameList) {
			Location homeLocation = new Location(
					Bukkit.getWorld(config.getString(name + ".Home." + homeName + ".World")), 
					config.getDouble(name + ".Home." + homeName + ".X"), 
					config.getDouble(name + ".Home." + homeName + ".Y"), 
					config.getDouble(name + ".Home." + homeName + ".Z"));
			homes.put(homeName, homeLocation);
		}
	}
	
	/**
	 * This method returns the name of this player.
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * This method adds a job to this player.
	 * 
	 * @param playerFile
	 * @param job
	 * @throws PlayerException
	 */
	public void addJob(File playerFile,Job job) throws PlayerException {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
		List<String> joblist = config.getStringList(name + ".Jobs");
		if(joblist.contains(job.getName())) {
			throw new PlayerException(PlayerException.JOB_ALREADY_JOINED);
		}
		else {
			joblist.add(job.getName());
			config.set(name + ".Jobs", joblist);
			jobs.add(job);
			save(config);
		}
	}
	
	/**
	 * This method removes a job from this player.
	 * 
	 * @param playerFile
	 * @param job
	 * @throws PlayerException
	 */
	public void removeJob(File playerFile,Job job) throws PlayerException {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
		List<String> joblist = config.getStringList(name + ".Jobs");
		if(!joblist.contains(job.getName())) {
			throw new PlayerException(PlayerException.JOB_NOT_JOINED);
		}
		else {
			joblist.remove(job.getName());
			config.set(name + ".Jobs", joblist);
			jobs.remove(job);
			save(config);
		}
	}
	
	/**
	 * This method returns true if the player has this job.
	 * 
	 * @param job
	 */
	public boolean hasJob(Job job) {
		boolean has = false;
		for(Job j:jobs) {
			if(job.equals(j)) {
				has = true;
			}
		}
		return has;
	}
	
	public void updateAccount(double amount) {
		account = amount;
	}
	
	private void save(YamlConfiguration config) {
		try {
			config.save(playerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static List<String> getEconomyPlayerNameList() {
		List<String> list = new ArrayList<>();
		for(EconomyPlayer economyPlayer:economyPlayers) {
			list.add(economyPlayer.getName());
		}
		return list;
	}
	
	/**
	 * This method returns the player save file.
	 * @return
	 */
	public static File getPlayerFile() {
		return playerFile;
	}
	
	/**
	 * This method returns a economyplayer by it's name.
	 * 
	 * @param name
	 * @return EconomyPlayer
	 * @throws PlayerException
	 */
	public static EconomyPlayer getEconomyPlayerByName(String name) throws PlayerException {
		for(EconomyPlayer economyPlayer:economyPlayers) {
			if(economyPlayer.getName().equals(name)) {
				return economyPlayer;
			}
		}
		throw new PlayerException(PlayerException.PLAYER_DOES_NOT_EXIST);
	}
	
	/**
	 * This method should me used to create a new EconomyPlayer.
	 * 
	 * @param playerFile
	 * @param name
	 * @throws PlayerException
	 */
	public static void createEconomyPlayer(String name) throws PlayerException {
		if(getEconomyPlayerNameList().contains(name)) {
			throw new PlayerException(PlayerException.PLAYER_ALREADY_EXIST);
		}
		else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			economyPlayers.add(new EconomyPlayer(name));
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
	 * @param dataFolder
	 */
	public static void loadAllEconomyPlayers(File dataFolder) {
		playerFile = new File(dataFolder , "PlayerFile.yml");
		if(!playerFile.exists()) {
			try {
				playerFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			List<String> playerList = config.getStringList("Player");
			for(String player:playerList) {
				economyPlayers.add(new EconomyPlayer(player, jobList));
			}
		}
	}
}
