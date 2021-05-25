package org.ue.economyplayer.dataaccess.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.utils.SaveFileUtils;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import org.ue.jobsystem.logic.api.Job;

public class EconomyPlayerDaoImpl extends SaveFileUtils implements EconomyPlayerDao {

	private final BankManager bankManager;
	private final ServerProvider serverProvider;

	public EconomyPlayerDaoImpl(BankManager bankManager, ServerProvider serverProvider) {
		this.bankManager = bankManager;
		this.serverProvider = serverProvider;
	}

	@Override
	public void setupSavefile() {
		file = new File(serverProvider.getDataFolderPath(), "PlayerFile.yml");
		if (!file.exists()) {
			createFile(file);
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public List<String> loadPlayerList() {
		removeDeprecatedPlayerList();
		return new ArrayList<>(config.getConfigurationSection("").getKeys(false));
	}

	@Override
	public void deleteEconomyPlayer(String playerName) {
		config.set(playerName, null);
		save();
	}

	@Override
	public void saveHome(String playerName, String homeName, Location location) {
		if (location == null) {
			config.set(playerName + ".Home." + homeName, null);
		} else {
			config.set(playerName + ".Home." + homeName + ".Name", homeName);
			config.set(playerName + ".Home." + homeName + ".World", location.getWorld().getName());
			config.set(playerName + ".Home." + homeName + ".X", location.getX());
			config.set(playerName + ".Home." + homeName + ".Y", location.getY());
			config.set(playerName + ".Home." + homeName + ".Z", location.getZ());
		}
		save();
	}

	@Override
	public void saveBankIban(String playerName, String iban) {
		config.set(playerName + ".Iban", iban);
		save();
	}

	@Override
	public void saveJoinedJobsList(String playerName, List<Job> jobList) {
		List<String> jobs = new ArrayList<>();
		for (Job job : jobList) {
			jobs.add(job.getName());
		}
		config.set(playerName + ".Jobs", jobs);
		save();
	}

	@Override
	public void saveJoinedTowns(String playerName, List<String> joinedTowns) {
		config.set(playerName + ".joinedTowns", joinedTowns);
		save();
	}

	@Override
	public void saveScoreboardObjectiveVisible(String playerName, Boolean visible) {
		config.set(playerName + ".scoreboardDisabled", !visible);
		save();
	}

	@Override
	public List<String> loadJoinedTowns(String playerName) {
		return config.getStringList(playerName + ".joinedTowns");
	}

	@Override
	public List<String> loadJobsList(String playerName) {
		return config.getStringList(playerName + ".Jobs");
	}

	@Override
	public boolean loadScoreboardObjectiveVisible(String playerName) {
		convertBankToScoreboardBool(playerName);
		return !config.getBoolean(playerName + ".scoreboardDisabled");
	}

	private Location loadHome(String playerName, String homeName) {
		return new Location(serverProvider.getWorld(config.getString(playerName + ".Home." + homeName + ".World")),
				config.getDouble(playerName + ".Home." + homeName + ".X"),
				config.getDouble(playerName + ".Home." + homeName + ".Y"),
				config.getDouble(playerName + ".Home." + homeName + ".Z"));
	}

	@Override
	public Map<String, Location> loadHomeList(String playerName) {
		removeDeprecatedHomelist(playerName);
		Map<String, Location> homes = new HashMap<>();
		if (config.getConfigurationSection(playerName + ".Home") != null) {
			for (String home : config.getConfigurationSection(playerName + ".Home").getKeys(false)) {
				homes.put(home, loadHome(playerName, home));
			}
		}
		return homes;
	}

	@Override
	public String loadBankIban(String playerName) {
		convertToIban(playerName);
		return config.getString(playerName + ".Iban");
	}

	/**
	 * @since 1.2.5
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void convertToIban(String playerName) {
		if (!config.isSet(playerName + ".Iban")) {
			// old loading, convert to new
			double amount = config.getDouble(playerName + ".account amount");
			BankAccount bankAccount = bankManager.createBankAccount(amount);
			config.set(playerName + ".account amount", null);
			config.set(playerName + ".Iban", bankAccount.getIban());
			save();
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void removeDeprecatedPlayerList() {
		if (config.contains("Player")) {
			config.set("Player", null);
			save();
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void removeDeprecatedHomelist(String playerName) {
		if (config.contains(playerName + ".Home.Homelist")) {
			config.set(playerName + ".Home.Homelist", null);
			save();
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void convertBankToScoreboardBool(String playerName) {
		if (config.contains(playerName + ".bank")) {
			boolean isDisabled = config.getBoolean(playerName + ".bank");
			config.set(playerName + ".bank", null);
			config.set(playerName + ".scoreboardDisabled", isDisabled);
			save();
		}
	}
}
