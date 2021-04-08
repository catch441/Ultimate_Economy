package com.ue.economyplayer.logic.api;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.ue.bank.logic.api.BankAccount;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.jobsystem.logic.api.Job;

public interface EconomyPlayer {

	/**
	 * Returns the player. Returns null, if the player is not online.
	 * 
	 * @return Player
	 */
	public Player getPlayer();

	/**
	 * Adds a bukkit player to the economy player. And adds this player to the
	 * bossbar of the economy player.
	 * 
	 * @param player
	 */
	public void setPlayer(Player player);

	/**
	 * Returns true, if the player is online.
	 * 
	 * @return boolean
	 */
	public boolean isOnline();

	/**
	 * This method returns true if the bank scoreboard is disabled.
	 * 
	 * @return boolean
	 */
	public boolean isScoreBoardObjectiveVisible();

	/**
	 * This method return true if this player reached the max number of homes.
	 * 
	 * @return boolean
	 */
	public boolean reachedMaxHomes();

	/**
	 * This method return true if this player reached the max number of joined jobs.
	 * 
	 * @return boolean
	 */
	public boolean reachedMaxJoinedJobs();

	/**
	 * Adds the wilderness permission to the player. Only if player is online.
	 */
	public void addWildernessPermission();

	/**
	 * Denys the wilderness permission for the player. Only if player is online.
	 */
	public void denyWildernessPermission();

	/**
	 * Transfers a money amount from this player to another player.
	 * 
	 * @param reciever
	 * @param amount
	 * @param sendMessage when true a message is send to the receiver and this
	 *                    player
	 * @throws GeneralEconomyException
	 * @throws EconomyPlayerException
	 */
	public void payToOtherPlayer(EconomyPlayer reciever, double amount, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException;

	/**
	 * Increase the bank amount of a player.
	 * 
	 * @param amount
	 * @param sendMessage when true then a message is send to the player
	 * @throws GeneralEconomyException
	 */
	public void increasePlayerAmount(double amount, boolean sendMessage) throws GeneralEconomyException;

	/**
	 * Decrease the bank amount of this player.
	 * 
	 * @param amount
	 * @param personal only for player exception, if player has not enough money
	 * @throws GeneralEconomyException
	 * @throws EconomyPlayerException
	 */
	public void decreasePlayerAmount(double amount, boolean personal)
			throws GeneralEconomyException, EconomyPlayerException;

	/**
	 * Returns true if the player has at minimum 'amount' on his bank account.
	 * 
	 * @param amount
	 * @return boolean
	 * @throws GeneralEconomyException
	 */
	public boolean hasEnoughtMoney(double amount) throws GeneralEconomyException;

	/**
	 * Get the bank account of this player.
	 * 
	 * @return bank account
	 */
	public BankAccount getBankAccount();

	/**
	 * This method returns a home location by it's name.
	 * 
	 * @param homeName
	 * @return Location
	 * @throws GeneralEconomyException
	 */
	public Location getHome(String homeName) throws GeneralEconomyException;

	/**
	 * This method adds a home location to this player.
	 * 
	 * @param homeName
	 * @param location
	 * @param sendMessage when true then a message is send to the player
	 * @throws GeneralEconomyException
	 * @throws EconomyPlayerException
	 */
	public void addHome(String homeName, Location location, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException;

	/**
	 * This method removes a home location from this player.
	 * 
	 * @param homeName
	 * @param sendMessage when true then a message is send to the player
	 * @throws GeneralEconomyException
	 */
	public void removeHome(String homeName, boolean sendMessage) throws GeneralEconomyException;

	/**
	 * This method returns the list of homes as string list.
	 * 
	 * @return List of Strings
	 */
	public Map<String, Location> getHomeList();

	/**
	 * This method returns the name of this player.
	 * 
	 * @return String
	 */
	public String getName();

	/**
	 * Returns the bossbar of this player.
	 * 
	 * @return bossbar
	 */
	public BossBar getBossBar();

	/**
	 * This method return true if this player reached the max number of joined
	 * towns.
	 * 
	 * @return boolean
	 */
	public boolean reachedMaxJoinedTowns();

	/**
	 * This method adds a town to the joined town list of this player. Not necessary
	 * if the join method from the Town is used. This only adds the town name. The
	 * town join method should be prefered.
	 * 
	 * @param townName
	 * @throws EconomyPlayerException
	 */
	public void addJoinedTown(String townName) throws EconomyPlayerException;

	/**
	 * This method returns true if the player has this job.
	 * 
	 * @param job
	 * @return boolean
	 */
	public boolean hasJob(Job job);

	/**
	 * This method adds a job to this player.
	 * 
	 * @param job
	 * @param sendMessage when true then a message is send to the player
	 * @throws EconomyPlayerException
	 */
	public void joinJob(Job job, boolean sendMessage) throws EconomyPlayerException;

	/**
	 * This method removes a job from this player.
	 * 
	 * @param job
	 * @param sendMessage when true then a message is send to the player
	 * @throws EconomyPlayerException
	 */
	public void leaveJob(Job job, boolean sendMessage) throws EconomyPlayerException;

	/**
	 * This method removes a town from the joined town list. Not necessary if the
	 * leaveTown method from Town is used.
	 * 
	 * @param townName
	 * @throws EconomyPlayerException
	 */
	public void removeJoinedTown(String townName) throws EconomyPlayerException;

	/**
	 * This method returns the list of joined jobs as string list.
	 * 
	 * @return List of Strings
	 */
	public List<Job> getJobList();

	/**
	 * This method returns the list of joined towns.
	 * 
	 * @return joined towns list
	 */
	public List<String> getJoinedTownList();

	/**
	 * Set true, if the scoreboard objective with money should be visible.
	 * 
	 * @param visible
	 */
	public void setScoreBoardObjectiveVisible(boolean visible);
}
