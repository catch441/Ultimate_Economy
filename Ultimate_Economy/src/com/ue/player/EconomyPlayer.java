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
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

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
	private List<String> jobs;
	private List<String> joinedTowns;
	private List<String> playerShops;
	private boolean scoreBoardDisabled;
	
	/**
	 * Constructor for creating a new economyPlayer/loading an existing player.
	 * 
	 * @param name
	 * @param isNew
	 */
	private EconomyPlayer(String name,boolean isNew) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
		jobs = new ArrayList<>();
		homes = new HashMap<>();
		joinedTowns = new ArrayList<>();
		this.name = name;
		if(isNew) {
			scoreBoardDisabled = true;
			account = 0.0;	
			config.set(name + ".bank", scoreBoardDisabled);
			config.set(name + ".account amount", account);
			save(config);
		}
		else {
			scoreBoardDisabled = config.getBoolean(name + ".bank");
			account = config.getDouble(name + ".account amount");
			jobs = config.getStringList(name + ".Jobs");
			joinedTowns = config.getStringList(name + "joinedTowns");
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
	 * @param jobName
	 * @throws PlayerException
	 * @throws JobSystemException 
	 */
	public void addJob(String jobName) throws PlayerException, JobSystemException {
		if(!Job.getJobNameList().contains(jobName)) {
			throw new JobSystemException(JobSystemException.JOB_DOES_NOT_EXIST);
		}
		else if(jobs.size() == maxJobs) {
			throw new PlayerException(PlayerException.MAX_JOINED_JOBS);
		}
		else if(jobs.contains(jobName)) {
			throw new PlayerException(PlayerException.JOB_ALREADY_JOINED);
		}
		else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			jobs.add(jobName);
			config.set(name + ".Jobs", jobName);
			save(config);
		}
	}
	
	/**
	 * This method removes a job from this player.
	 * 
	 * @param jobName
	 * @throws PlayerException
	 * @throws JobSystemException 
	 */
	public void removeJob(String jobName) throws PlayerException, JobSystemException {
		if(!Job.getJobNameList().contains(jobName)) {
			throw new JobSystemException(JobSystemException.JOB_DOES_NOT_EXIST);
		}
		else if(!jobs.contains(jobName)) {
			throw new PlayerException(PlayerException.JOB_NOT_JOINED);
		}
		else {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			jobs.remove(jobName);
			config.set(name + ".Jobs", jobs);
			save(config);
		}
	}
	
	/**
	 * This method returns true if the player has this job.
	 * 
	 * @param jobName
	 * @throws JobSystemException 
	 */
	public boolean hasJob(String jobName) throws JobSystemException {
		if(!Job.getJobNameList().contains(jobName)) {
			throw new JobSystemException(JobSystemException.JOB_DOES_NOT_EXIST);
		}
		else {
			if(jobs.contains(jobName)) {
				return true;
			}
			else {
			 return false;
			}
		}
	}
	
	/**
	 * This method returns the list of joined jobs as string list.
	 * 
	 * @return List of Strings
	 */
	public List<String> getJobList() {
		return jobs;
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
	 * This method returns the list of homes as string list.
	 * 
	 * @return List of Strings
	 */
	public Map<String, Location> getHomeList() {
		return homes;
	}
	
	/**
	 * This method adds a town to the joined town list of this player.
	 * 
	 * @param townName
	 * @throws PlayerException
	 */
	public void addJoinedTown(String townName) throws PlayerException {
		if(joinedTowns.contains(townName)) {
			throw new PlayerException(PlayerException.TOWN_ALREADY_JOINED);
		}
		else if(!(joinedTowns.size() < maxJoinedTowns)) {
			throw new PlayerException(PlayerException.MAX_JOINED_TOWNS);
		}
		else {
			FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			joinedTowns.add(townName);
			config.set(name + ".joinedTowns", joinedTowns);
			save(config);
			
		}
	}
	
	/**
	 * This method removes a town from the joined town list.
	 * 
	 * @param townName
	 * @throws PlayerException
	 */
	public void removeJoinedTown(String townName) throws PlayerException {
		if(joinedTowns.contains(townName)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			List<String> list = config.getStringList(name + ".joinedTowns");
			list.remove(townName);
			config.set(name + ".joinedTowns", list);
			save(config);
		}
		else {
			throw new PlayerException(PlayerException.TOWN_NOT_JOINED);
		}
	}
	
	/**
	 * This method returns the list of joined towns.
	 * 
	 * @return List of Strings
	 */
	public List<String> getJoinedTownList() {
		return joinedTowns;
	}
	
	/**
	 * This method return true if this player reached the max number of joined towns.
	 * 
	 * @return boolean
	 */
	public boolean reachedMaxJoinedTowns() {
		if(maxJoinedTowns <= joinedTowns.size()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * This method return true if this player reached the max number of homes.
	 * 
	 * @return boolean
	 */
	public boolean reachedMaxHomes() {
		if(maxHomes <= homes.size()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * This method return true if this player reached the max number of joined jobs.
	 * 
	 * @return boolean
	 */
	public boolean reachedMaxJoinedJobs() {
		if(maxJobs <= jobs.size()) {
			return true;
		}
		else {
			return false;
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
	
	public void setScoreBoardDisabled(boolean scoreBoardDisabled) {
		this.scoreBoardDisabled = scoreBoardDisabled;
		YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
		config.set(name + ".bank", scoreBoardDisabled);
	}
		
	/**
	 * 
	 * Returns true if the player has at minimum 'amount' on his bank account.
	 * 
	 * @param amount
	 * @return boolean
	 * @throws PlayerDoesNotExistException
	 */
	public boolean hasEnoughtMoney(double amount) {
		if(account >= amount) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * 
	 * Transfers a money amount from this player to another player.
	 * 
	 * @param reciever
	 * @param amount
	 * @throws PlayerException 
	 */
	public void payToOtherPlayer(EconomyPlayer reciever,double amount) throws PlayerException {
		if(amount < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		}
		else if(hasEnoughtMoney(amount)) {
			reciever.increasePlayerAmount(amount);
			decreasePlayerAmount(amount,true);
		}
	}
	 /**
	  * 
	  * Increase the bank amount of a player.
	  * 
	  * @param amount
	  * @throws PlayerException 
	  */
	public void increasePlayerAmount(double amount) throws PlayerException {
		if(amount < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		}
		else {
			FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			account += amount;
			config.set(name + ".account amount", account);
			save(config);
			if(Bukkit.getPlayer(name).isOnline()) {
				updateScoreBoard(Bukkit.getPlayer(name));
			}
		}
	}
	
	/**
	 * 
	 * Decrease the bank amount of this player.
	 * 
	 * @param amount
	 * @param personal
	 * @throws PlayerException 
	 */
	public void decreasePlayerAmount(double amount,boolean personal) throws PlayerException {
		if(amount < 0) {
			throw new PlayerException(PlayerException.INVALID_NUMBER);
		}
		else if(hasEnoughtMoney(amount)) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
			account -= amount;
			config.set(name + ".account amount", account);
			save(config);
			if(Bukkit.getPlayer(name).isOnline()) {
				updateScoreBoard(Bukkit.getPlayer(name));
			}
		}
		else {
			if(personal) {
				throw new PlayerException(PlayerException.NOT_ENOUGH_MONEY_PERSONAL);
			}
			else {
				throw new PlayerException(PlayerException.NOT_ENOUGH_MONEY_NON_PERSONAL);
			}
		}
	}
	
	/**
	 * 
	 * Get the bank amount of this player.
	 * 
	 * @return double
	 */
	public double getBankAmount() {
		return account;
	}
	
	/**
	 * <p>
	 * Set the bank scoreboard of this player.
	 * <p>
	 * @param file
	 * @param p
	 * @param score
	 */
	private void setScoreboard(Player p,int score) {
		if(!scoreBoardDisabled) {
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective o = board.registerNewObjective("test", "dummy","§6§lBank");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			o.getScore("§6Money:").setScore(score);
			p.setScoreboard(board);
		}
		else {
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			p.setScoreboard(board);
		}
	}
	
	/**
	 * <p>
	 * Update a bank scoreboard of a player.
	 * <p>
	 * @param file
	 * @param p
	 */
	public void updateScoreBoard(Player p) {
		int score = (int) account;
		setScoreboard(p,score);
	}
	
	private void save(FileConfiguration config) {
		try {
			config.save(playerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns a list of all player names.
	 * 
	 * @return
	 */
	public static List<String> getEconomyPlayerNameList() {
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
			economyPlayers.add(new EconomyPlayer(name,true));
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
			for(String player:playerList) {
				economyPlayers.add(new EconomyPlayer(player,false));
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
