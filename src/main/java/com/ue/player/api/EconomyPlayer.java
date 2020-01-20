package com.ue.player.api;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;

public interface EconomyPlayer {
	
	/**
	 * Returns the player. Returns null, if the player is not online.
	 * 
	 * @return Player
	 */
	public Player getPlayer();
	
	/**
	 * Adds a bukkit player to the economy player.
	 * And adds this player to the bossbar of the economy player.
	 * 
	 * @param player
	 */
	public void setPlayer(Player player);
	
	/**
	 * Returns true, if the player is online;
	 * 
	 * @return boolean
	 */
	public boolean isOnline();
	
	/**
	 * Transfers a money amount from this player to another player.
	 * 
	 * @param reciever
	 * @param amount
	 * @param sendMessage when true a message is send to the receiver and this player
	 * @throws PlayerException
	 */
	public void payToOtherPlayer(EconomyPlayer reciever, double amount,boolean sendMessage) throws PlayerException;
	
	/**
	 * Increase the bank amount of a player.
	 * 
	 * @param amount
	 * @param sendMessage when true then a message is send to the player
	 * @throws PlayerException
	 */
	public void increasePlayerAmount(double amount,boolean sendMessage) throws PlayerException;
	
	/**
	 * Decrease the bank amount of this player.
	 * 
	 * @param amount
	 * @param personal only for player exception, if player has not enough money
	 * @throws PlayerException
	 */
	public void decreasePlayerAmount(double amount, boolean personal) throws PlayerException;
	
	/**
	 * Returns true if the player has at minimum 'amount' on his bank account.
	 * 
	 * @param amount
	 * @return boolean
	 * @throws PlayerDoesNotExistException
	 */
	public boolean hasEnoughtMoney(double amount);
	
	/**
	 * Get the bank amount of this player.
	 * 
	 * @return double
	 */
	public double getBankAmount();
	
	/**
	 * This method returns a home location by it's name.
	 * 
	 * @param homeName
	 * @return Location
	 * @throws PlayerException
	 */
	public Location getHome(String homeName) throws PlayerException;
	
	/**
	 * This method adds a home location to this player.
	 * 
	 * @param homeName
	 * @param location
	 * @param sendMessage when true then a message is send to the player
	 * @throws PlayerException
	 */
	public void addHome(String homeName, Location location, boolean sendMessage) throws PlayerException;
	
	/**
	 * This method removes a home location from this player.
	 * 
	 * @param homeName
	 * @param sendMessage when true then a message is send to the player
	 * @throws PlayerException
	 */
	public void removeHome(String homeName, boolean sendMessage) throws PlayerException;
	
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
	 * @return
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
	 * This method adds a town to the joined town list of this player.
	 * Not necessary if the join method from the Town is used.
	 * 
	 * @param townName
	 * @throws PlayerException
	 */
	public void addJoinedTown(String townName) throws PlayerException;
	
	/**
	 * This method returns true if the player has this job.
	 * 
	 * @param job
	 * @throws JobSystemException
	 */
	public boolean hasJob(Job job) throws JobSystemException;
	
	/**
	 * This method adds a job to this player.
	 * 
	 * @param job
	 * @param sendMessage when true then a message is send to the player
	 * @throws PlayerException
	 * @throws JobSystemException
	 */
	public void joinJob(Job job,boolean sendMessage) throws PlayerException, JobSystemException;
	
	/**
	 * This method removes a job from this player.
	 * 
	 * @param job
	 * @param sendMessage when true then a message is send to the player
	 * @throws PlayerException
	 * @throws JobSystemException
	 */
	public void leaveJob(Job job,boolean sendMessage) throws PlayerException, JobSystemException;
	
	/**
	 * This method removes a town from the joined town list.
	 * Not necessary if the leaveTown method from Town is used.
	 * 
	 * @param townName
	 * @throws PlayerException
	 */
	public void removeJoinedTown(String townName) throws PlayerException;
	
	/**
	 * This method returns the list of joined jobs as string list.
	 * 
	 * @return List of Strings
	 */
	public List<Job> getJobList();
	
	/**
	 * This method returns the list of joined towns.
	 * 
	 * @return List of Strings
	 */
	public List<String> getJoinedTownList();
	
	/**
	 * Set true, if the scoreboard should be disabled.
	 * 
	 * @param scoreBoardDisabled
	 */
	public void setScoreBoardDisabled(boolean scoreBoardDisabled);
	
	/**
	 * Update the bank scoreboard of a player.
	 * 
	 */
	public void updateScoreBoard();
}
