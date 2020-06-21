package com.ue.economyplayer.impl;

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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.ue.bank.api.BankAccount;
import com.ue.bank.api.BankController;
import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.language.MessageWrapper;
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
	private EconomyPlayerValidationHandler validationHandler;
	private EconomyPlayerSavefileHandler savefileHandler;

	/**
	 * Constructor for creating a new economyPlayer/loading an existing player.
	 * 
	 * @param name
	 * @param isNew
	 */
	public EconomyPlayerImpl(String name, boolean isNew) {
		this.player = Bukkit.getPlayer(name);
		validationHandler = new EconomyPlayerValidationHandler(this);
		savefileHandler = new EconomyPlayerSavefileHandler(this);
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
		getValidationHandler().checkForNotReachedMaxJoinedJobs();
		getValidationHandler().checkForJobNotJoined(job);
		getJobList().add(job);
		getSavefileHandler().saveJoinedJobsList();
		if (sendMessage && isOnline()) {
			getPlayer().sendMessage(MessageWrapper.getString("job_join", job.getName()));
		}
	}

	@Override
	public void leaveJob(Job job, boolean sendMessage) throws PlayerException {
		getValidationHandler().checkForJobJoined(job);
		getJobList().remove(job);
		getSavefileHandler().saveJoinedJobsList();
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
		getValidationHandler().checkForExistingHome(homeName);
		return getHomeList().get(homeName);
	}

	@Override
	public Map<String, Location> getHomeList() {
		return homes;
	}

	@Override
	public void addJoinedTown(String townName) throws PlayerException {
		getValidationHandler().checkForTownNotJoined(townName);
		getValidationHandler().checkForNotReachedMaxJoinedTowns();
		getJoinedTownList().add(townName);
		getSavefileHandler().saveJoinedTowns();
	}

	@Override
	public void removeJoinedTown(String townName) throws PlayerException {
		getValidationHandler().checkForJoinedTown(townName);
		getJoinedTownList().remove(townName);
		getSavefileHandler().saveJoinedTowns();
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
		getValidationHandler().checkForNotReachedMaxHomes();
		getValidationHandler().checkForNotExistingHome(homeName);
		getHomeList().put(homeName, location);
		getSavefileHandler().saveHomeList();
		getSavefileHandler().saveHome(homeName, location);
		if (isOnline() && sendMessage) {
			getPlayer().sendMessage(MessageWrapper.getString("sethome", homeName));
		}
		
	}

	@Override
	public void removeHome(String homeName, boolean sendMessage) throws PlayerException {
		getValidationHandler().checkForExistingHome(homeName);
		getHomeList().remove(homeName);
		getSavefileHandler().saveHomeList();
		getSavefileHandler().saveHome(homeName, null);
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
		getSavefileHandler().saveScoreboardDisabled();
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
		getValidationHandler().checkForEnoughMoney(amount, personal);
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

	private EconomyPlayerValidationHandler getValidationHandler() {
		return validationHandler;
	}

	private EconomyPlayerSavefileHandler getSavefileHandler() {
		return savefileHandler;
	}

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

	private class UpdateScoreboardRunnable extends BukkitRunnable {

		@Override
		public void run() {
			updateScoreBoard();
		}
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupNewPlayer(String name) {
		setName(name);
		setScoreBoardDisabled(true);
		bankAccount = BankController.createBankAccount(0.0);
		getSavefileHandler().saveBankIban();
	}

	/*
	 * Loading methods
	 * 
	 */

	private void loadExistingPlayer(String name) {
		setName(name);
		scoreBoardDisabled = getSavefileHandler().loadScoreboardDisabled();
		loadJoinedJobs();
		joinedTowns = getSavefileHandler().loadJoinedTowns();
		loadHomes();
		loadBankAccount();
		updateScoreBoard();
	}

	private void loadBankAccount() {
		String iban = getSavefileHandler().loadBankIban();
		try {
			bankAccount = BankController.getBankAccountByIban(iban);
		} catch (GeneralEconomyException e) {
			Bukkit.getLogger().warning(
					"[Ultimate_Economy] Failed to load the bank account " + iban + " for the player " + getName());
		}
	}

	private void loadHomes() {
		for (String homeName : getSavefileHandler().loadHomeList()) {
			Location homeLocation = getSavefileHandler().loadHome(homeName);
			getHomeList().put(homeName, homeLocation);
		}
	}

	private void loadJoinedJobs() {
		for (String jobName : getSavefileHandler().loadJobsList()) {
			try {
				getJobList().add(JobController.getJobByName(jobName));
			} catch (GeneralEconomyException e) {
				Bukkit.getLogger().warning(
						"[Ultimate_Economy] " + MessageWrapper.getErrorString("job_does_not_exist") + ":" + jobName);
			}
		}
	}
}
