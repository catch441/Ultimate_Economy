package com.ue.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.Job;

public class EconomyPlayer {
	
	private static List<EconomyPlayer> economyPlayers = new ArrayList<>();
	private static File playerFile;
	private static int maxHomes;
	private static int maxJobs;
	private static int maxJoinedTowns;

	private Map<String,Location> homes;
	private double account;
	private String name;
	private List<Job> jobs;
	private List<String> joinedTowns;
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
		if(jobs.size() == maxJobs) {
			throw new PlayerException(PlayerException.MAX_JOINED_JOBS);
		}
		else if(jobs.contains(job)) {
			throw new PlayerException(PlayerException.JOB_ALREADY_JOINED);
		}
		else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			List<String> joblist = config.getStringList(name + ".Jobs");
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
		if(!jobs.contains(job)) {
			throw new PlayerException(PlayerException.JOB_NOT_JOINED);
		}
		else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			List<String> joblist = config.getStringList(name + ".Jobs");
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
	
	/**
	 * This method returns a home location by it's name.
	 * 
	 * @param homeName
	 * @return Location
	 * @throws PlayerException
	 */
	public Location getHome(String homeName) throws PlayerException {
		if(homes.containsKey(homeName)) {
			return homes.get(homeName);
		}
		else {
			throw new PlayerException(PlayerException.HOME_DOES_NOT_EXIST);
		}
	}
	
	/**
	 * This method adds a home location to this player.
	 * 
	 * @param homeName
	 * @param location
	 * @throws PlayerException
	 */
	public void addHome(String homeName,Location location) throws PlayerException {
		if(homes.size() == maxHomes) {
			throw new PlayerException(PlayerException.MAX_HOMES);
		}
		else if(homes.containsKey(homeName)) {
			throw new PlayerException(PlayerException.HOME_ALREAD_EXIST);
		}
		else {
			homes.put(homeName, location);
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			List<String> homeNameList = config.getStringList(name + ".Home.Homelist");
			homeNameList.add(homeName);
			config.set(name + ".Home.Homelist",homeNameList);
			config.set(name + ".Home." + homeName + ".Name", homeName);
			config.set(name + ".Home." + homeName + ".World", location.getWorld().getName());
			config.set(name + ".Home." + homeName + ".X", location.getX());
			config.set(name + ".Home." + homeName + ".Y", location.getY());
			config.set(name + ".Home." + homeName + ".Z", location.getZ());
			save(config);
		}
	}
	
	/**
	 * This method removes a home location from this player.
	 * 
	 * @param homeName
	 * @throws PlayerException
	 */
	public void removeHome(String homeName) throws PlayerException {
		if(homes.containsKey(homeName)) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			List<String> homeNameList = config.getStringList(name + ".Home.Homelist");
			homes.remove(homeName);
			homeNameList.remove(homeName);
			config.set(name + ".Home." + homeName, null);
			config.set(name + ".Home.Homelist",homeNameList);
			save(config);
		}
		else {
			throw new PlayerException(PlayerException.HOME_DOES_NOT_EXIST);
		}
	}
	
	/**
	 * This method returns true if the bank scoreboard is disabled.
	 * 
	 * @return
	 */
	public boolean isScoreBoardDisabled() {
		return scoreBoardDisabled;
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
	 * Jobs.loadAllJobs() have to be executed before this method.
	 * 
	 * @param dataFolder
	 * @throws JobSystemException 
	 */
	public static void loadAllEconomyPlayers(File dataFolder) throws JobSystemException {
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
			List<String> jobNames = new ArrayList<>();
			List<Job> jobList = new ArrayList<>();
			for(String jobName:jobNames) {
				jobList.add(Job.getJobByName(jobName));
			}
			for(String player:playerList) {
				economyPlayers.add(new EconomyPlayer(player, jobList));
			}
		}
	}
	
	public static void setupConfig(FileConfiguration fileConfig) {
		if(fileConfig.isSet("MaxHomes")) {
			fileConfig.set("MaxHomes", 3);
			maxHomes = 3;
		}
		if(fileConfig.isSet("MaxJobs")) {
			fileConfig.set("MaxJobs", 2);
			maxJobs = 2;
		}
		if(fileConfig.isSet("MaxJoinedTowns")) {
			fileConfig.set("MaxJoinedTowns", 1);
			maxJoinedTowns = 1;
		}
	}
	
	/**
	 * This method sets the maxHomes value. 
	 * 
	 * @param value
	 * @throws PlayerException 
	 */
	public static void setMaxHomes(int value) throws PlayerException {
		if(value < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		}
		else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			config.set("MaxHomes", value);
			maxHomes = value;
		}
	}
	
	/**
	 * This method sets the maxJobs value. 
	 * 
	 * @param value
	 * @throws PlayerException 
	 */
	public static void setMaxJobs(int value) throws PlayerException {
		if(value < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		}
		else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			config.set("MaxJobs", value);
			maxJobs = value;
		}
	}
	
	/**
	 * This method sets the maxJoinedTowns value. 
	 * 
	 * @param value
	 * @throws PlayerException 
	 */
	public static void setMaxJoinedTowns(int value) throws PlayerException {
		if(value < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		}
		else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			config.set("MaxJoinedTowns", value);
			maxJoinedTowns = value;
		}
	}
}
