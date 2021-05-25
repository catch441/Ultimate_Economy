package org.ue.economyplayer.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankException;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerValidator;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.JobsystemException;

public class EconomyPlayerImpl implements EconomyPlayer {

	private static final Logger log = LoggerFactory.getLogger(EconomyPlayerImpl.class);
	private final ServerProvider serverProvider;
	private final ConfigManager configManager;
	private final BankManager bankManager;
	private final JobManager jobManager;
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerDao ecoPlayerDao;
	private final EconomyPlayerValidator validationHandler;
	private Map<String, Location> homes = new HashMap<>();
	private BankAccount bankAccount;
	private Player player;
	private String name;
	private List<Job> jobs = new ArrayList<>();
	private List<String> joinedTowns = new ArrayList<>();
	private boolean scoreboardObjectiveVisible;
	private BossBar bossBar;

	/**
	 * Constructor for creating a new economyPlayer/loading an existing player.
	 * 
	 * @param serverProvider
	 * @param validationHandler
	 * @param ecoPlayerDao
	 * @param messageWrapper
	 * @param configManager
	 * @param bankManager
	 * @param jobManager
	 */
	public EconomyPlayerImpl(ServerProvider serverProvider, EconomyPlayerValidator validationHandler,
			EconomyPlayerDao ecoPlayerDao, MessageWrapper messageWrapper, ConfigManager configManager,
			BankManager bankManager, JobManager jobManager) {
		this.configManager = configManager;
		this.bankManager = bankManager;
		this.jobManager = jobManager;
		this.messageWrapper = messageWrapper;
		this.ecoPlayerDao = ecoPlayerDao;
		this.validationHandler = validationHandler;
		this.serverProvider = serverProvider;
	}

	@Override
	public void setupNew(Player player, String name) {
		this.player = player;
		setName(name);
		bankAccount = bankManager.createBankAccount(0.0);
		ecoPlayerDao.saveBankIban(getName(), getBankAccount().getIban());
		setScoreBoardObjectiveVisible(false);
		bossBar = serverProvider.createBossBar();
		getBossBar().setVisible(false);
	}

	@Override
	public void setupExisting(Player player, String name) {
		this.player = player;
		setName(name);
		scoreboardObjectiveVisible = ecoPlayerDao.loadScoreboardObjectiveVisible(getName());
		loadJoinedJobs();
		joinedTowns = ecoPlayerDao.loadJoinedTowns(getName());
		homes = ecoPlayerDao.loadHomeList(getName());
		loadBankAccount();
		setupScoreboard();
		updateScoreBoardObjective();
		bossBar = serverProvider.createBossBar();
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
		validationHandler.checkForNotReachedMax(reachedMaxJoinedJobs());
		validationHandler.checkForJobNotJoined(getJobList(), job);
		getJobList().add(job);
		ecoPlayerDao.saveJoinedJobsList(getName(), getJobList());
		if (sendMessage && isOnline()) {
			getPlayer().sendMessage(messageWrapper.getString(MessageEnum.JOB_JOIN, job.getName()));
		}
	}

	@Override
	public void leaveJob(Job job, boolean sendMessage) throws EconomyPlayerException {
		validationHandler.checkForJobJoined(getJobList(), job);
		getJobList().remove(job);
		ecoPlayerDao.saveJoinedJobsList(getName(), getJobList());
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(messageWrapper.getString(MessageEnum.JOB_LEFT, job.getName()));
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
		validationHandler.checkForValueInList(new ArrayList<>(getHomeList().keySet()), homeName);
		return getHomeList().get(homeName);
	}

	@Override
	public Map<String, Location> getHomeList() {
		return homes;
	}

	@Override
	public void addJoinedTown(String townName) throws EconomyPlayerException {
		validationHandler.checkForTownNotJoined(getJoinedTownList(), townName);
		validationHandler.checkForNotReachedMax(reachedMaxJoinedTowns());
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
		validationHandler.checkForValueNotInList(new ArrayList<>(getHomeList().keySet()), homeName);
		validationHandler.checkForNotReachedMax(reachedMaxHomes());
		homes.put(homeName, location);
		ecoPlayerDao.saveHome(getName(), homeName, location);
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(messageWrapper.getString(MessageEnum.CREATED, homeName));
		}

	}

	@Override
	public void removeHome(String homeName, boolean sendMessage) throws EconomyPlayerException {
		validationHandler.checkForValueInList(new ArrayList<>(getHomeList().keySet()), homeName);
		getHomeList().remove(homeName);
		ecoPlayerDao.saveHome(getName(), homeName, null);
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(messageWrapper.getString(MessageEnum.DELETED, homeName));
		}
	}

	@Override
	public boolean isScoreBoardObjectiveVisible() {
		return scoreboardObjectiveVisible;
	}

	@Override
	public void setScoreBoardObjectiveVisible(boolean visible) {
		boolean old = scoreboardObjectiveVisible;
		scoreboardObjectiveVisible = visible;
		ecoPlayerDao.saveScoreboardObjectiveVisible(getName(), isScoreBoardObjectiveVisible());
		if (visible) {
			setupScoreboard();
			updateScoreBoardObjective();
		} else {
			if (isOnline() && old) {
				getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
			}
		}

	}

	@Override
	public void payToOtherPlayer(EconomyPlayer reciever, double amount, boolean sendMessage)
			throws BankException, EconomyPlayerException {
		decreasePlayerAmount(amount, true);
		reciever.increasePlayerAmount(amount, false);
		if (reciever.isOnline() && sendMessage) {
			reciever.getPlayer().sendMessage(messageWrapper.getString(MessageEnum.GOT_MONEY_WITH_SENDER, amount,
					configManager.getCurrencyText(amount), getName()));
		}
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(messageWrapper.getString(MessageEnum.GAVE_MONEY, reciever.getName(), amount,
					configManager.getCurrencyText(amount)));
		}
	}

	@Override
	public void increasePlayerAmount(double amount, boolean sendMessage) throws BankException {
		getBankAccount().increaseAmount(amount);
		if (isOnline()) {
			updateScoreBoardObjective();
			if (sendMessage) {
				getPlayer().sendMessage(
						messageWrapper.getString(MessageEnum.GOT_MONEY, amount, configManager.getCurrencyText(amount)));
			}
		}
	}

	@Override
	public void decreasePlayerAmount(double amount, boolean personal) throws BankException, EconomyPlayerException {
		validationHandler.checkForEnoughMoney(getBankAccount(), amount, personal);
		getBankAccount().decreaseAmount(amount);
		if (isOnline()) {
			updateScoreBoardObjective();
		}
	}

	@Override
	public boolean hasEnoughtMoney(double amount) throws EconomyPlayerException {
		validationHandler.checkForPositiveValue(amount);
		return getBankAccount().getAmount() >= amount;
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
			setupScoreboard();
			updateScoreBoardObjective();
		}
	}

	@Override
	public void addWildernessPermission() {
		if (isOnline()) {
			getPlayer().addAttachment(serverProvider.getJavaPluginInstance())
					.setPermission("ultimate_economy.wilderness", true);
		}
	}

	@Override
	public void denyWildernessPermission() {
		if (isOnline()) {
			getPlayer().addAttachment(serverProvider.getJavaPluginInstance())
					.setPermission("ultimate_economy.wilderness", false);
		}
	}

	private void setupScoreboard() {
		if (isOnline() && isScoreBoardObjectiveVisible()) {
			Scoreboard board = serverProvider.createScoreboard();
			Objective o = board.registerNewObjective("bank", "dummy", messageWrapper.getString(MessageEnum.BANK));
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			getPlayer().setScoreboard(board);
		}
	}

	private void updateScoreBoardObjective() {
		int score = 0;
		if (getBankAccount() != null) {
			score = (int) getBankAccount().getAmount();
		}
		if (isOnline()) {
			if (isScoreBoardObjectiveVisible()) {
				Scoreboard board = getPlayer().getScoreboard();
				Objective o = board.getObjective(DisplaySlot.SIDEBAR);
				o.getScore(ChatColor.GOLD + configManager.getCurrencyText(score)).setScore(score);
			}
		}
	}

	private void setName(String name) {
		this.name = name;
	}

	private void loadBankAccount() {
		String iban = ecoPlayerDao.loadBankIban(getName());
		try {
			bankAccount = bankManager.getBankAccountByIban(iban);
		} catch (BankException e) {
			log.warn("[Ultimate_Economy] Failed to load the bank account " + iban + " for the player " + getName());
		}
	}

	private void loadJoinedJobs() {
		for (String jobName : ecoPlayerDao.loadJobsList(getName())) {
			try {
				getJobList().add(jobManager.getJobByName(jobName));
			} catch (JobsystemException e) {
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}
}
