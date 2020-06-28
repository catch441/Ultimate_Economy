package com.ue.economyplayer.impl;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.jobsystem.api.Job;

public class EconomyPlayerValidationHandler {
	
	private final EconomyPlayer ecoPlayer;
	
	/**
	 * Constructor for a new economy player validation handler.
	 * @param ecoPlayer
	 */
	public EconomyPlayerValidationHandler(EconomyPlayer ecoPlayer) {
		this.ecoPlayer = ecoPlayer;
	}

	/**
	 * Check for has enough money.
	 * @param amount
	 * @param personal
	 * @throws PlayerException
	 * @throws GeneralEconomyException
	 */
	public void checkForEnoughMoney(double amount, boolean personal) throws PlayerException, GeneralEconomyException {
		if (!getEconomyPlayer().getBankAccount().hasAmount(amount)) {
			if (personal) {
				throw PlayerException.getException(PlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
			} else {
				throw PlayerException.getException(PlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_NON_PERSONAL);
			}
		}
	}

	/**
	 * Check for home exists.
	 * @param homeName
	 * @throws PlayerException
	 */
	public void checkForExistingHome(String homeName) throws PlayerException {
		if (!getEconomyPlayer().getHomeList().containsKey(homeName)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.HOME_DOES_NOT_EXIST);
		}
	}

	/**
	 * Check for not reached max homes.
	 * @throws PlayerException
	 */
	public void checkForNotReachedMaxHomes() throws PlayerException {
		if (getEconomyPlayer().reachedMaxHomes()) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	/**
	 * Check for homes does not exist.
	 * @param homeName
	 * @throws PlayerException
	 */
	public void checkForNotExistingHome(String homeName) throws PlayerException {
		if (getEconomyPlayer().getHomeList().containsKey(homeName)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.HOME_ALREADY_EXIST);
		}
	}

	/**
	 * Check for joined town.
	 * @param townName
	 * @throws PlayerException
	 */
	public void checkForJoinedTown(String townName) throws PlayerException {
		if (!getEconomyPlayer().getJoinedTownList().contains(townName)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.TOWN_NOT_JOINED);
		}
	}

	/**
	 * Check for not reached may joined towns.
	 * @throws PlayerException
	 */
	public void checkForNotReachedMaxJoinedTowns() throws PlayerException {
		if (getEconomyPlayer().reachedMaxJoinedTowns()) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	/**
	 * Check for town not joined.
	 * @param townName
	 * @throws PlayerException
	 */
	public void checkForTownNotJoined(String townName) throws PlayerException {
		if (getEconomyPlayer().getJoinedTownList().contains(townName)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.TOWN_ALREADY_JOINED);
		}
	}

	/**
	 * Check for job joined.
	 * @param job
	 * @throws PlayerException
	 */
	public void checkForJobJoined(Job job) throws PlayerException {
		if (!getEconomyPlayer().getJobList().contains(job)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.JOB_NOT_JOINED);
		}
	}

	/**
	 * Check for not reached max joined jobs.
	 * @throws PlayerException
	 */
	public void checkForNotReachedMaxJoinedJobs() throws PlayerException {
		if (getEconomyPlayer().reachedMaxJoinedJobs()) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	/**
	 * Checks for job not joined.
	 * @param job
	 * @throws PlayerException
	 */
	public void checkForJobNotJoined(Job job) throws PlayerException {
		if (getEconomyPlayer().getJobList().contains(job)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.JOB_ALREADY_JOINED);
		}
	}
	
	private EconomyPlayer getEconomyPlayer() {
		return ecoPlayer;
	}
}
