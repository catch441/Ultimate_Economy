package com.ue.player.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.ue.bank.api.BankAccount;
import com.ue.bank.api.BankController;
import com.ue.config.api.ConfigController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.language.MessageWrapper;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

public class EconomyPlayerImpl implements EconomyPlayer {

	private Map<String, Location> homes = new HashMap<>();
	private BankAccount bankAccount;
	private Player player;
	private String name;
	private List<Job> jobs = new ArrayList<>();
	private List<String> joinedTowns = new ArrayList<>();
	private boolean scoreBoardDisabled;
	private BossBar bossBar;

	/**
	 * Constructor for creating a new economyPlayer/loading an existing player.
	 * 
	 * @param name
	 * @param isNew
	 */
	public EconomyPlayerImpl(String name, boolean isNew) {
		this.player = Bukkit.getPlayer(name);
		if (isNew) {
			setupNewPlayer(name);
		} else {
			loadExistingPlayer(name);
		}
		bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
		getBossBar().setVisible(false);
	}

	@Override
	public boolean isOnline() {
		if (getPlayer() == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public BossBar getBossBar() {
		return bossBar;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void joinJob(Job job, boolean sendMessage) throws PlayerException, JobSystemException {
		checkForNotReachedMaxJoinedJobs();
		checkForJobNotJoined(job);
		getJobList().add(job);
		saveJoinedJobsList();
		if (sendMessage && isOnline()) {
			getPlayer().sendMessage(MessageWrapper.getString("job_join", job.getName()));
		}
	}

	@Override
	public void leaveJob(Job job, boolean sendMessage) throws PlayerException {
		checkForJobJoined(job);
		getJobList().remove(job);
		saveJoinedJobsList();
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(MessageWrapper.getString("job_left", job.getName()));
		}
	}

	@Override
	public boolean hasJob(Job job) {
		if (getJobList().contains(job)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Job> getJobList() {
		return jobs;
	}

	@Override
	public Location getHome(String homeName) throws PlayerException {
		checkForExistingHome(homeName);
		return getHomeList().get(homeName);
	}

	@Override
	public Map<String, Location> getHomeList() {
		return homes;
	}

	@Override
	public void addJoinedTown(String townName) throws PlayerException {
		checkForTownNotJoined(townName);
		checkForNotReachedMaxJoinedTowns();
		getJoinedTownList().add(townName);
		saveJoinedTowns();
	}

	@Override
	public void removeJoinedTown(String townName) throws PlayerException {
		checkForJoinedTown(townName);
		getJoinedTownList().remove(townName);
		saveJoinedTowns();
	}

	@Override
	public List<String> getJoinedTownList() {
		return joinedTowns;
	}

	@Override
	public boolean reachedMaxJoinedTowns() {
		if (ConfigController.getMaxJoinedTowns() <= getJoinedTownList().size()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean reachedMaxHomes() {
		if (ConfigController.getMaxHomes() <= getHomeList().size()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean reachedMaxJoinedJobs() {
		if (ConfigController.getMaxJobs() <= getJobList().size()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void addHome(String homeName, Location location, boolean sendMessage) throws PlayerException {
		checkForNotReachedMaxHomes();
		checkForNotExistingHome(homeName);
		getHomeList().put(homeName, location);
		saveHomeList();
		saveHome(homeName, location);
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(MessageWrapper.getString("sethome", homeName));
		}
	}

	@Override
	public void removeHome(String homeName, boolean sendMessage) throws PlayerException {
		checkForExistingHome(homeName);
		getHomeList().remove(homeName);
		saveHomeList();
		saveHome(homeName, null);
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(MessageWrapper.getString("delhome", homeName));
		}
	}

	@Override
	public boolean isScoreBoardDisabled() {
		return scoreBoardDisabled;
	}

	@Override
	public void setScoreBoardDisabled(boolean scoreBoardDisabled) {
		this.scoreBoardDisabled = scoreBoardDisabled;
		saveScoreboardDisabled();
		if (isScoreBoardDisabled()) {
			if (isOnline()) {
				getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
		} else {
			new UpdateScoreboardRunnable().runTask(UltimateEconomy.getInstance);
		}
	}

	@Override
	public void payToOtherPlayer(EconomyPlayer reciever, double amount, boolean sendMessage)
			throws GeneralEconomyException, PlayerException {
		reciever.increasePlayerAmount(amount, false);
		decreasePlayerAmount(amount, true);
		if (reciever.isOnline() && sendMessage) {
			reciever.getPlayer().sendMessage(MessageWrapper.getString("got_money_with_sender", amount,
					ConfigController.getCurrencyText(amount), player.getName()));
		}
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(MessageWrapper.getString("gave_money", reciever.getName(), amount,
					ConfigController.getCurrencyText(amount)));
		}
	}

	@Override
	public void increasePlayerAmount(double amount, boolean sendMessage) throws GeneralEconomyException {
		getBankAccount().increaseAmount(amount);
		if (isOnline()) {
			new UpdateScoreboardRunnable().runTask(UltimateEconomy.getInstance);
			if (sendMessage) {
				getPlayer().sendMessage(
						MessageWrapper.getString("got_money", amount, ConfigController.getCurrencyText(amount)));
			}
		}
	}

	@Override
	public void decreasePlayerAmount(double amount, boolean personal) throws GeneralEconomyException, PlayerException {
		checkForEnoughMoney(amount, personal);
		getBankAccount().decreaseAmount(amount);
		if (isOnline()) {
			new UpdateScoreboardRunnable().runTask(UltimateEconomy.getInstance);
		}
	}

	@Override
	public boolean hasEnoughtMoney(double amount) throws GeneralEconomyException {
		return getBankAccount().hasAmount(amount);
	}

	@Override
	public BankAccount getBankAccount() {
		return bankAccount;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(Player player) {
		getBossBar().removeAll();
		this.player = player;
		getBossBar().addPlayer(player);
	}

	@Override
	public void addWildernessPermission() {
		if (isOnline()) {
			getPlayer().addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.wilderness", true);
		}
	}

	@Override
	public void denyWildernessPermission() {
		if (isOnline()) {
			getPlayer().addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.wilderness", false);
		}
	}

	/*
	 * Utility methods/classes
	 * 
	 */

	private void setScoreboard(int score) {
		if (!isScoreBoardDisabled()) {
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective o = board.registerNewObjective("bank", "dummy", MessageWrapper.getString("bank"));
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			o.getScore(ChatColor.GOLD + ConfigController.getCurrencyText(score)).setScore(score);
			getPlayer().setScoreboard(board);
		}
	}

	private void updateScoreBoard() {
		int score = (int) getBankAccount().getAmount();
		if (isOnline()) {
			setScoreboard(score);
		}
	}

	private void setName(String name) {
		this.name = name;
	}

	private void save(FileConfiguration config) {
		try {
			config.save(EconomyPlayerController.getPlayerFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class UpdateScoreboardRunnable extends BukkitRunnable {

		@Override
		public void run() {
			updateScoreBoard();
		}
	}

	/*
	 * Save methods
	 * 
	 */

	private void saveScoreboardDisabled() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		// TODO refactor name
		config.set(getName() + ".bank", isScoreBoardDisabled());
		save(config);
	}

	private void saveHome(String homeName, Location location) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		if (location == null) {
			config.set(getName() + ".Home." + homeName, null);
		} else {
			config.set(getName() + ".Home." + homeName + ".Name", homeName);
			config.set(getName() + ".Home." + homeName + ".World", location.getWorld().getName());
			config.set(getName() + ".Home." + homeName + ".X", location.getX());
			config.set(getName() + ".Home." + homeName + ".Y", location.getY());
			config.set(getName() + ".Home." + homeName + ".Z", location.getZ());
		}
		save(config);
	}

	private void saveHomeList() {
		List<String> homeNameList = new ArrayList<>(getHomeList().keySet());
		YamlConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		config.set(getName() + ".Home.Homelist", homeNameList);
		save(config);
	}

	private void saveJoinedTowns() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		config.set(getName() + ".joinedTowns", getJoinedTownList());
		save(config);
	}

	private void saveJoinedJobsList() {
		List<String> jobList = new ArrayList<>();
		for (Job job : getJobList()) {
			jobList.add(job.getName());
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		config.set(getName() + ".Jobs", jobList);
		save(config);
	}

	private void saveBankIban() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		config.set(getName() + ".Iban", getBankAccount().getIban());
		save(config);
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupNewPlayer(String name) {
		setName(name);
		setupScoreboardDisabled();
		setupBankAccount();
		updateScoreBoard();
	}

	private void setupBankAccount() {
		bankAccount = BankController.createBankAccount(0.0);
		saveBankIban();
	}

	private void setupScoreboardDisabled() {
		scoreBoardDisabled = true;
		saveScoreboardDisabled();
	}

	/*
	 * Loading methods
	 * 
	 */

	private void loadExistingPlayer(String name) {
		setName(name);
		loadScoreboardDisabled();
		loadBankAccount();
		loadJoinedJobs();
		loadJoinedTowns();
		loadHomes();
		updateScoreBoard();
	}

	private void loadScoreboardDisabled() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		scoreBoardDisabled = config.getBoolean(getName() + ".bank");
		save(config);
	}

	private void loadJoinedTowns() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		joinedTowns = config.getStringList(getName() + ".joinedTowns");
		save(config);
	}

	private void loadBankAccount() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		if (config.isSet(getName() + ".account amount")) {
			// old loading, convert to new
			double amount = config.getDouble(getName() + ".account amount");
			bankAccount = BankController.createBankAccount(amount);
			config.set(getName() + ".account amount", null);
			config.set(getName() + ".Iban", getBankAccount().getIban());
			save(config);
		} else {
			// new loading
			String iban = config.getString(getName() + ".Iban");
			try {
				bankAccount = BankController.getBankAccountByIban(iban);
			} catch (GeneralEconomyException e) {
				Bukkit.getLogger().warning(
						"[Ultimate_Economy] Failed to load the bank account " + iban + " for the player " + getName());
			}
		}
		save(config);
	}

	private void loadHomes() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		for (String homeName : config.getStringList(getName() + ".Home.Homelist")) {
			Location homeLocation = new Location(
					Bukkit.getWorld(config.getString(getName() + ".Home." + homeName + ".World")),
					config.getDouble(getName() + ".Home." + homeName + ".X"),
					config.getDouble(getName() + ".Home." + homeName + ".Y"),
					config.getDouble(getName() + ".Home." + homeName + ".Z"));
			getHomeList().put(homeName, homeLocation);
		}
		save(config);
	}

	private void loadJoinedJobs() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(EconomyPlayerController.getPlayerFile());
		for (String jobName : config.getStringList(getName() + ".Jobs")) {
			try {
				getJobList().add(JobController.getJobByName(jobName));
			} catch (GeneralEconomyException e) {
				Bukkit.getLogger().warning(
						"[Ultimate_Economy] " + MessageWrapper.getErrorString("job_does_not_exist") + ":" + jobName);
			}
		}
		save(config);
	}

	/*
	 * Validation check methods
	 * 
	 */

	private void checkForEnoughMoney(double amount, boolean personal) throws PlayerException, GeneralEconomyException {
		if (!getBankAccount().hasAmount(amount)) {
			if (personal) {
				throw PlayerException.getException(PlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
			} else {
				throw PlayerException.getException(PlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_NON_PERSONAL);
			}
		}
	}

	private void checkForExistingHome(String homeName) throws PlayerException {
		if (!getHomeList().containsKey(homeName)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.HOME_DOES_NOT_EXIST);
		}
	}

	private void checkForNotReachedMaxHomes() throws PlayerException {
		if (reachedMaxHomes()) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	private void checkForNotExistingHome(String homeName) throws PlayerException {
		if (getHomeList().containsKey(homeName)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.HOME_ALREADY_EXIST);
		}
	}

	private void checkForJoinedTown(String townName) throws PlayerException {
		if (!getJoinedTownList().contains(townName)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.TOWN_NOT_JOINED);
		}
	}

	private void checkForNotReachedMaxJoinedTowns() throws PlayerException {
		if (reachedMaxJoinedTowns()) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	private void checkForTownNotJoined(String townName) throws PlayerException {
		if (getJoinedTownList().contains(townName)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.TOWN_ALREADY_JOINED);
		}
	}

	private void checkForJobJoined(Job job) throws PlayerException {
		if (!getJobList().contains(job)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.JOB_NOT_JOINED);
		}
	}

	private void checkForNotReachedMaxJoinedJobs() throws PlayerException {
		if (reachedMaxJoinedJobs()) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	private void checkForJobNotJoined(Job job) throws PlayerException {
		if (getJobList().contains(job)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.JOB_ALREADY_JOINED);
		}
	}

}
