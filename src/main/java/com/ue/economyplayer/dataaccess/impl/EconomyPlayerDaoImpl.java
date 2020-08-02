package com.ue.economyplayer.dataaccess.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.SaveFileUtils;
import com.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.jobsystem.api.Job;
import com.ue.ultimate_economy.UltimateEconomy;

public class EconomyPlayerDaoImpl extends SaveFileUtils implements EconomyPlayerDao {

	private File file;
	private YamlConfiguration config;
	private final BankManager bankManager;
	
	/**
	 * Inject constructor.
	 * @param bankManager
	 */
	@Inject
	public EconomyPlayerDaoImpl(BankManager bankManager) {
		this.bankManager = bankManager;
	}

	@Override
	public void setupSavefile() {
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

	@Override
	public void savePlayerList(List<String> playerList) {
		getConfig().set("Player", playerList);
		save(getConfig(), getSavefile());
	}

	@Override
	public List<String> loadPlayerList() {
		return getConfig().getStringList("Player");
	}

	@Override
	public void deleteEconomyPlayer(EconomyPlayer ecoPlayer) {
		getConfig().set(ecoPlayer.getName(), null);
		save(getConfig(), getSavefile());
	}

	private File getSavefile() {
		return file;
	}

	private YamlConfiguration getConfig() {
		return config;
	}

	@Override
	public void saveHomeList(String playerName, Map<String, Location> homeList) {
		getConfig().set(playerName + ".Home.Homelist", new ArrayList<String>(homeList.keySet()));
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveHome(String playerName, String homeName, Location location) {
		if (location == null) {
			getConfig().set(playerName + ".Home." + homeName, null);
		} else {
			getConfig().set(playerName + ".Home." + homeName + ".Name", homeName);
			getConfig().set(playerName + ".Home." + homeName + ".World", location.getWorld().getName());
			getConfig().set(playerName + ".Home." + homeName + ".X", location.getX());
			getConfig().set(playerName + ".Home." + homeName + ".Y", location.getY());
			getConfig().set(playerName + ".Home." + homeName + ".Z", location.getZ());
		}
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveBankIban(String playerName, BankAccount account) {
		getConfig().set(playerName + ".Iban", account.getIban());
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveJoinedJobsList(String playerName, List<Job> jobList) {
		List<String> jobs = new ArrayList<>();
		for (Job job : jobList) {
			jobs.add(job.getName());
		}
		getConfig().set(playerName + ".Jobs", jobs);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveJoinedTowns(String playerName, List<String> joinedTowns) {
		getConfig().set(playerName + ".joinedTowns", joinedTowns);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveScoreboardDisabled(String playerName, Boolean scoreboardDisabled) {
		getConfig().set(playerName + ".scoreboardDisabled", scoreboardDisabled);
		save(getConfig(), getSavefile());
	}

	@Override
	public List<String> loadJoinedTowns(String playerName) {
		return getConfig().getStringList(playerName + ".joinedTowns");
	}

	@Override
	public List<String> loadJobsList(String playerName) {
		return getConfig().getStringList(playerName + ".Jobs");
	}

	@Override
	public boolean loadScoreboardDisabled(String playerName) {
		convertBankToScoreboardBool(playerName);
		return getConfig().getBoolean(playerName + ".scoreboardDisabled");
	}

	@Override
	public Location loadHome(String playerName, String homeName) {
		return new Location(
				Bukkit.getWorld(getConfig().getString(playerName + ".Home." + homeName + ".World")),
				getConfig().getDouble(playerName + ".Home." + homeName + ".X"),
				getConfig().getDouble(playerName + ".Home." + homeName + ".Y"),
				getConfig().getDouble(playerName + ".Home." + homeName + ".Z"));
	}

	@Override
	public List<String> loadHomeList(String playerName) {
		return getConfig().getStringList(playerName + ".Home.Homelist");
	}

	@Override
	public String loadBankIban(String playerName) {
		convertToIban(playerName);
		return getConfig().getString(playerName + ".Iban");
	}

	/**
	 * @since 1.2.5
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void convertToIban(String playerName) {
		if (getConfig().isSet(playerName + ".account amount")) {
			// old loading, convert to new
			double amount = getConfig().getDouble(playerName + ".account amount");
			BankAccount bankAccount = bankManager.createBankAccount(amount);
			getConfig().set(playerName + ".account amount", null);
			getConfig().set(playerName + ".Iban", bankAccount.getIban());
			save(getConfig(), getSavefile());
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void convertBankToScoreboardBool(String playerName) {
		if (getConfig().contains(playerName + ".bank")) {
			boolean isDisabled = getConfig().getBoolean(playerName + ".bank");
			getConfig().set(playerName + ".bank", null);
			getConfig().set(playerName + ".scoreboardDisabled", isDisabled);
			save(getConfig(), getSavefile());
		}
	}
}
