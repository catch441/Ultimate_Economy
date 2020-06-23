package com.ue.economyplayer.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.bank.api.BankAccount;
import com.ue.bank.api.BankController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.jobsystem.api.Job;
import com.ue.ultimate_economy.UltimateEconomy;

public class EconomyPlayerSavefileHandler {

	private static File file;
	private static YamlConfiguration config;
	private EconomyPlayer ecoPlayer;

	public EconomyPlayerSavefileHandler(EconomyPlayer ecoPlayer) {
		this.ecoPlayer = ecoPlayer;
	}

	/**
	 * Creates a new savefile if no one exists and loads an existing file if the
	 * savefile already exists.
	 */
	public static void setupSavefile() {
		file = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
		if (!file.exists()) {
			try {
				getSavefile().createNewFile();
			} catch (IOException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create savefile");
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		config = YamlConfiguration.loadConfiguration(getSavefile());
	}

	/**
	 * Saves the player list.
	 * 
	 * @param playerList
	 */
	public static void savePlayerList(List<String> playerList) {
		getConfig().set("Player", playerList);
		save();
	}

	/**
	 * Loads the player list.
	 * 
	 * @return player names as String List
	 */
	public static List<String> loadPlayerList() {
		return getConfig().getStringList("Player");
	}

	/**
	 * Deletes a economy player from the savefile.
	 * 
	 * @param ecoPlayer
	 */
	public static void deleteEconomyPlayer(EconomyPlayer ecoPlayer) {
		getConfig().set(ecoPlayer.getName(), null);
		save();
	}

	private static File getSavefile() {
		return file;
	}

	private static YamlConfiguration getConfig() {
		return config;
	}

	private static void save() {
		try {
			getConfig().save(getSavefile());
		} catch (IOException e) {
			Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
			Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}
	
	private EconomyPlayer getEconomyPlayer() {
		return ecoPlayer;
	}
	
	/**
	 * Saves the home list.
	 */
	public void saveHomeList() {
		getConfig().set(getEconomyPlayer().getName() + ".Home.Homelist", new ArrayList<String>(getEconomyPlayer().getHomeList().keySet()));
		save();
	}
	
	/**
	 * Saves a home location. If location is null, then the location gets deleted.
	 * @param homeName
	 * @param location
	 */
	public void saveHome(String homeName, Location location) {
		if (location == null) {
			getConfig().set(getEconomyPlayer().getName() + ".Home." + homeName, null);
		} else {
			getConfig().set(getEconomyPlayer().getName() + ".Home." + homeName + ".Name", homeName);
			getConfig().set(getEconomyPlayer().getName() + ".Home." + homeName + ".World", location.getWorld().getName());
			getConfig().set(getEconomyPlayer().getName() + ".Home." + homeName + ".X", location.getX());
			getConfig().set(getEconomyPlayer().getName() + ".Home." + homeName + ".Y", location.getY());
			getConfig().set(getEconomyPlayer().getName() + ".Home." + homeName + ".Z", location.getZ());
		}
		save();
	}
	
	/**
	 * Saves the bank imab
	 */
	public void saveBankIban() {
		getConfig().set(getEconomyPlayer().getName() + ".Iban", getEconomyPlayer().getBankAccount().getIban());
		save();
	}
	
	/**
	 * Save the joined job list.
	 */
	public void saveJoinedJobsList() {
		List<String> jobList = new ArrayList<>();
		for (Job job : getEconomyPlayer().getJobList()) {
			jobList.add(job.getName());
		}
		getConfig().set(getEconomyPlayer().getName() + ".Jobs", jobList);
		save();
	}
	
	/**
	 * Saves the joined town list.
	 */
	public void saveJoinedTowns() {
		getConfig().set(getEconomyPlayer().getName() + ".joinedTowns", getEconomyPlayer().getJoinedTownList());
		save();
	}
	
	/**
	 * Saves the scoreboard disabled value.
	 */
	public void saveScoreboardDisabled() {
		getConfig().set(getEconomyPlayer().getName() + ".scoreboardDisabled", getEconomyPlayer().isScoreBoardDisabled());
		save();
	}
	
	/**
	 * Loads the joined towns name list.
	 * @return list of joined towns
	 */
	public List<String> loadJoinedTowns() {
		return getConfig().getStringList(getEconomyPlayer().getName() + ".joinedTowns");
	}
	
	/**
	 * Loads the name list of all joined jobs.
	 * @return list of joined jobs
	 */
	public List<String> loadJobsList() {
		return getConfig().getStringList(getEconomyPlayer().getName() + ".Jobs");
	}
	
	/**
	 * Load the scoreboard disabled value.
	 * @return boolean
	 */
	public boolean loadScoreboardDisabled() {
		convertBankToScoreboardBool();
		return getConfig().getBoolean(getEconomyPlayer().getName() + ".scoreboardDisabled");
	}
	
	/**
	 * Load a home location.
	 * @param homeName
	 * @return location
	 */
	public Location loadHome(String homeName) {
		return new Location(
				Bukkit.getWorld(getConfig().getString(getEconomyPlayer().getName() + ".Home." + homeName + ".World")),
				getConfig().getDouble(getEconomyPlayer().getName() + ".Home." + homeName + ".X"),
				getConfig().getDouble(getEconomyPlayer().getName() + ".Home." + homeName + ".Y"),
				getConfig().getDouble(getEconomyPlayer().getName() + ".Home." + homeName + ".Z"));
	}
	
	/**
	 * Load all home names.
	 * @return home name list
	 */
	public List<String> loadHomeList() {
		return getConfig().getStringList(getEconomyPlayer().getName() + ".Home.Homelist");
	}
	
	/**
	 * Loads the bank iban.
	 * @return iban
	 */
	public String loadBankIban() {
		convertToIban();
		return getConfig().getString(getEconomyPlayer().getName() + ".Iban");
	}
	
	/**
	 * @since 1.2.5
	 * @deprecated can be removed later
	 */
	private void convertToIban() {
		if (getConfig().isSet(getEconomyPlayer().getName() + ".account amount")) {
			// old loading, convert to new
			double amount = getConfig().getDouble(getEconomyPlayer().getName() + ".account amount");
			BankAccount bankAccount = BankController.createBankAccount(amount);
			getConfig().set(getEconomyPlayer().getName() + ".account amount", null);
			getConfig().set(getEconomyPlayer().getName() + ".Iban", bankAccount.getIban());
			save();
		}
	}
	
	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	private void convertBankToScoreboardBool() {
		if(getConfig().contains(getEconomyPlayer().getName() + ".bank")) {
			boolean isDisabled = getConfig().getBoolean(getEconomyPlayer().getName() + ".bank");
			getConfig().set(getEconomyPlayer().getName() + ".bank", null);
			getConfig().set(getEconomyPlayer().getName() + ".scoreboardDisabled", isDisabled);
			save();
		}
	}
}
