package com.ue.economyplayer.logic.api;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.ue.bank.logic.api.BankAccount;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.jobsystem.logic.api.Job;

public interface EconomyPlayerValidationHandler {

	/**
	 * Check for has enough money.
	 * 
	 * @param account
	 * @param amount
	 * @param personal
	 * @throws EconomyPlayerException
	 * @throws GeneralEconomyException
	 */
	public void checkForEnoughMoney(BankAccount account, double amount, boolean personal)
			throws EconomyPlayerException, GeneralEconomyException;
	
	/**
	 * Check for joined town.
	 * 
	 * @param joinedTowns
	 * @param townName
	 * @throws EconomyPlayerException
	 */
	public void checkForJoinedTown(List<String> joinedTowns, String townName) throws EconomyPlayerException;
	
	/**
	 * Check for homes does not exist.
	 * 
	 * @param homeList
	 * @param homeName
	 * @throws GeneralEconomyException
	 */
	public void checkForNotExistingHome(Map<String, Location> homeList, String homeName) throws GeneralEconomyException;
	
	/**
	 * Check for town not joined.
	 * 
	 * @param joinedTowns
	 * @param townName
	 * @throws EconomyPlayerException
	 */
	public void checkForTownNotJoined(List<String> joinedTowns, String townName) throws EconomyPlayerException;
	
	/**
	 * Check for job joined.
	 * 
	 * @param joinedJobs
	 * @param job
	 * @throws EconomyPlayerException
	 */
	public void checkForJobJoined(List<Job> joinedJobs, Job job) throws EconomyPlayerException;
	
	/**
	 * Checks for job not joined.
	 * 
	 * @param joinedJobs
	 * @param job
	 * @throws EconomyPlayerException
	 */
	public void checkForJobNotJoined(List<Job> joinedJobs, Job job) throws EconomyPlayerException;
	
	/**
	 * Check for not reached max homes.
	 * 
	 * @param reachedMaxHomes
	 * @throws EconomyPlayerException
	 */
	public void checkForNotReachedMaxHomes(boolean reachedMaxHomes) throws EconomyPlayerException;
	
	/**
	 * Check for home exists.
	 * 
	 * @param homeList
	 * @param homeName
	 * @throws GeneralEconomyException
	 */
	public void checkForExistingHome(Map<String, Location> homeList, String homeName) throws GeneralEconomyException;
	
	/**
	 * Check for not reached may joined towns.
	 * 
	 * @param reachedMaxJoinedTowns
	 * @throws EconomyPlayerException
	 */
	public void checkForNotReachedMaxJoinedTowns(boolean reachedMaxJoinedTowns) throws EconomyPlayerException;
	
	/**
	 * Check for not reached max joined jobs.
	 * 
	 * @param reachedMaxJoinedJobs
	 * @throws EconomyPlayerException
	 */
	public void checkForNotReachedMaxJoinedJobs(boolean reachedMaxJoinedJobs) throws EconomyPlayerException;
	
	/**
	 * Check for player does not exist.
	 * @param playerNameList
	 * @param playerName
	 * @throws GeneralEconomyException
	 */
	public void checkForPlayerDoesNotExist(List<String> playerNameList, String playerName) throws GeneralEconomyException;
}
