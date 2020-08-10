package com.ue.economyplayer.dataaccess.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.BukkitService;
import com.ue.common.utils.SaveFileUtils;
import com.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import com.ue.jobsystem.logic.api.Job;

public class EconomyPlayerDaoImpl extends SaveFileUtils implements EconomyPlayerDao {

	@Inject
	BankManager bankManager;
	@Inject
	BukkitService bukkitService;
	private File file;
	private YamlConfiguration config;

	@Override
	public void setupSavefile(String datafolder) {
		file = new File(datafolder, "PlayerFile.yml");
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
	public void deleteEconomyPlayer(String playerName) {
		getConfig().set(playerName, null);
		save(getConfig(), getSavefile());
	}

	private File getSavefile() {
		return file;
	}

	private YamlConfiguration getConfig() {
		return config;
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
	public void saveBankIban(String playerName, String iban) {
		getConfig().set(playerName + ".Iban", iban);
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

	private Location loadHome(String playerName, String homeName) {
		return new Location(
				bukkitService.getWorld(getConfig().getString(playerName + ".Home." + homeName + ".World")),
				getConfig().getDouble(playerName + ".Home." + homeName + ".X"),
				getConfig().getDouble(playerName + ".Home." + homeName + ".Y"),
				getConfig().getDouble(playerName + ".Home." + homeName + ".Z"));
	}

	@Override
	public Map<String, Location> loadHomeList(String playerName) {
		removeDeprecatedHomelist(playerName);
		Map<String, Location> homes = new HashMap<>();
		for(String home: getConfig().getConfigurationSection(playerName + ".Home").getKeys(false)) {
			homes.put(home, loadHome(playerName, home));
		}
		return homes;
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
	private void removeDeprecatedHomelist(String playerName) {
		if (getConfig().contains(playerName + ".Home.Homelist")) {
			getConfig().set(playerName + ".Home.Homelist", null);
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
