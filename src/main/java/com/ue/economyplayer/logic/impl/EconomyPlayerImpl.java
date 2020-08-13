package com.ue.economyplayer.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.BukkitService;
import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

public class EconomyPlayerImpl implements EconomyPlayer {

	private final BukkitService bukkitService;
	private final ConfigManager configManager;
	private final BankManager bankManager;
	private final JobManager jobManager;
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerDao ecoPlayerDao;
	private final EconomyPlayerValidationHandler validationHandler;
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
	 * @param bukkitService
	 * @param validationHandler
	 * @param ecoPlayerDao
	 * @param messageWrapper
	 * @param configManager
	 * @param bankManager
	 * @param jobManager
	 * @param player
	 * @param name
	 * @param isNew
	 */
	public EconomyPlayerImpl(BukkitService bukkitService, EconomyPlayerValidationHandler validationHandler,
			EconomyPlayerDao ecoPlayerDao, MessageWrapper messageWrapper, ConfigManager configManager,
			BankManager bankManager, JobManager jobManager, Player player, String name, boolean isNew) {
		this.configManager = configManager;
		this.bankManager = bankManager;
		this.jobManager = jobManager;
		this.messageWrapper = messageWrapper;
		this.ecoPlayerDao = ecoPlayerDao;
		this.validationHandler = validationHandler;
		this.bukkitService = bukkitService;
		this.player = player;
		if (isNew) {
			setupNewPlayer(name);
		} else {
			loadExistingPlayer(name);
		}
		bossBar = bukkitService.createBossBar();
		getBossBar().setVisible(false);
	}

	@Override
	public boolean isOnline() {
		return !(getPlayer() == null);
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
	public void joinJob(Job job, boolean sendMessage) throws EconomyPlayerException {
		validationHandler.checkForNotReachedMaxJoinedJobs(reachedMaxJoinedJobs());
		validationHandler.checkForJobNotJoined(getJobList(), job);
		getJobList().add(job);
		ecoPlayerDao.saveJoinedJobsList(getName(), getJobList());
		if (sendMessage && isOnline()) {
			getPlayer().sendMessage(messageWrapper.getString("job_join", job.getName()));
		}
	}

	@Override
	public void leaveJob(Job job, boolean sendMessage) throws EconomyPlayerException {
		validationHandler.checkForJobJoined(getJobList(), job);
		getJobList().remove(job);
		ecoPlayerDao.saveJoinedJobsList(getName(), getJobList());
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(messageWrapper.getString("job_left", job.getName()));
		}
	}

	@Override
	public boolean hasJob(Job job) {
		return getJobList().contains(job);
	}

	@Override
	public List<Job> getJobList() {
		return jobs;
	}

	@Override
	public Location getHome(String homeName) throws EconomyPlayerException {
		validationHandler.checkForExistingHome(getHomeList(), homeName);
		return getHomeList().get(homeName);
	}

	@Override
	public Map<String, Location> getHomeList() {
		return homes;
	}

	@Override
	public void addJoinedTown(String townName) throws EconomyPlayerException {
		validationHandler.checkForTownNotJoined(getJoinedTownList(), townName);
		validationHandler.checkForNotReachedMaxJoinedTowns(reachedMaxJoinedTowns());
		getJoinedTownList().add(townName);
		ecoPlayerDao.saveJoinedTowns(getName(), getJoinedTownList());
	}

	@Override
	public void removeJoinedTown(String townName) throws EconomyPlayerException {
		validationHandler.checkForJoinedTown(getJoinedTownList(), townName);
		getJoinedTownList().remove(townName);
		ecoPlayerDao.saveJoinedTowns(getName(), getJoinedTownList());
	}

	@Override
	public List<String> getJoinedTownList() {
		return joinedTowns;
	}

	@Override
	public boolean reachedMaxJoinedTowns() {
		return getJoinedTownList().size() >= configManager.getMaxJoinedTowns();
	}

	@Override
	public boolean reachedMaxHomes() {
		return getHomeList().size() >= configManager.getMaxHomes();
	}

	@Override
	public boolean reachedMaxJoinedJobs() {
		return getJobList().size() >= configManager.getMaxJobs();
	}

	@Override
	public void addHome(String homeName, Location location, boolean sendMessage) throws EconomyPlayerException {
		validationHandler.checkForNotExistingHome(getHomeList(), homeName);
		validationHandler.checkForNotReachedMaxHomes(reachedMaxHomes());
		homes.put(homeName, location);
		ecoPlayerDao.saveHome(getName(), homeName, location);
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(messageWrapper.getString("sethome", homeName));
		}

	}

	@Override
	public void removeHome(String homeName, boolean sendMessage) throws EconomyPlayerException {
		validationHandler.checkForExistingHome(getHomeList(), homeName);
		getHomeList().remove(homeName);
		ecoPlayerDao.saveHome(getName(), homeName, null);
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(messageWrapper.getString("delhome", homeName));
		}
	}

	@Override
	public boolean isScoreBoardDisabled() {
		return scoreBoardDisabled;
	}

	@Override
	public void setScoreBoardDisabled(boolean scoreBoardDisabled) {
		this.scoreBoardDisabled = scoreBoardDisabled;
		ecoPlayerDao.saveScoreboardDisabled(getName(), isScoreBoardDisabled());
		if (isScoreBoardDisabled()) {
			if (isOnline()) {
				getPlayer().setScoreboard(bukkitService.createScoreBoard());
			}
		} else {
			updateScoreBoard();
		}
	}

	@Override
	public void payToOtherPlayer(EconomyPlayer reciever, double amount, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException {
		decreasePlayerAmount(amount, true);
		reciever.increasePlayerAmount(amount, false);
		if (reciever.isOnline() && sendMessage) {
			reciever.getPlayer().sendMessage(messageWrapper.getString("got_money_with_sender", amount,
					configManager.getCurrencyText(amount), getName()));
		}
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(messageWrapper.getString("gave_money", reciever.getName(), amount,
					configManager.getCurrencyText(amount)));
		}
	}

	@Override
	public void increasePlayerAmount(double amount, boolean sendMessage) throws GeneralEconomyException {
		getBankAccount().increaseAmount(amount);
		if (isOnline()) {
			updateScoreBoard();
			if (sendMessage) {
				getPlayer().sendMessage(
						messageWrapper.getString("got_money", amount, configManager.getCurrencyText(amount)));
			}
		}
	}

	@Override
	public void decreasePlayerAmount(double amount, boolean personal)
			throws GeneralEconomyException, EconomyPlayerException {
		validationHandler.checkForEnoughMoney(getBankAccount(), amount, personal);
		getBankAccount().decreaseAmount(amount);
		if (isOnline()) {
			updateScoreBoard();
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
		if (isOnline()) {
			getBossBar().addPlayer(player);
			updateScoreBoard();
		}
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

	private void setScoreboard(int score) {
		if (!isScoreBoardDisabled()) {
			Scoreboard board = bukkitService.createScoreBoard();
			Objective o = board.registerNewObjective("bank", "dummy", messageWrapper.getString("bank"));
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			o.getScore(ChatColor.GOLD + configManager.getCurrencyText(score)).setScore(score);
			getPlayer().setScoreboard(board);
		}
	}

	private void updateScoreBoard() {
		int score = 0;
		if (getBankAccount() != null) {
			score = (int) getBankAccount().getAmount();
		}
		if (isOnline()) {
			setScoreboard(score);
		}
	}

	private void setName(String name) {
		this.name = name;
	}

	private void setupNewPlayer(String name) {
		setName(name);
		setScoreBoardDisabled(true);
		bankAccount = bankManager.createBankAccount(0.0);
		ecoPlayerDao.saveBankIban(getName(), getBankAccount().getIban());
	}

	private void loadExistingPlayer(String name) {
		setName(name);
		scoreBoardDisabled = ecoPlayerDao.loadScoreboardDisabled(getName());
		loadJoinedJobs();
		joinedTowns = ecoPlayerDao.loadJoinedTowns(getName());
		homes = ecoPlayerDao.loadHomeList(getName());
		loadBankAccount();
		updateScoreBoard();
	}

	private void loadBankAccount() {
		String iban = ecoPlayerDao.loadBankIban(getName());
		try {
			bankAccount = bankManager.getBankAccountByIban(iban);
		} catch (GeneralEconomyException e) {
			Bukkit.getLogger().warning(
					"[Ultimate_Economy] Failed to load the bank account " + iban + " for the player " + getName());
		}
	}

	private void loadJoinedJobs() {
		for (String jobName : ecoPlayerDao.loadJobsList(getName())) {
			try {
				getJobList().add(jobManager.getJobByName(jobName));
			} catch (GeneralEconomyException e) {
				Bukkit.getLogger().warning(
						"[Ultimate_Economy] " + messageWrapper.getErrorString("job_does_not_exist") + ":" + jobName);
			}
		}
	}
}
