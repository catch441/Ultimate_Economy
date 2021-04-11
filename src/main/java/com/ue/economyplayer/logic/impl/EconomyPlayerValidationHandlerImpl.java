package com.ue.economyplayer.logic.impl;

import java.util.List;

import javax.inject.Inject;

import com.ue.bank.logic.api.BankAccount;
import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.jobsystem.logic.api.Job;

public class EconomyPlayerValidationHandlerImpl implements EconomyPlayerValidationHandler {

	private final MessageWrapper messageWrapper;

	@Inject
	public EconomyPlayerValidationHandlerImpl(MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
	}

	@Override
	public void checkForEnoughMoney(BankAccount account, double amount, boolean personal)
			throws EconomyPlayerException, GeneralEconomyException {
		if (!account.hasAmount(amount)) {
			if (personal) {
				throw new EconomyPlayerException(messageWrapper,
						EconomyPlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
			} else {
				throw new EconomyPlayerException(messageWrapper,
						EconomyPlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_NON_PERSONAL);
			}
		}
	}

	@Override
	public void checkForNotReachedMaxHomes(boolean reachedMaxHomes) throws EconomyPlayerException {
		if (reachedMaxHomes) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	@Override
	public void checkForJoinedTown(List<String> joinedTowns, String townName) throws EconomyPlayerException {
		if (!joinedTowns.contains(townName)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.TOWN_NOT_JOINED);
		}
	}

	@Override
	public void checkForNotReachedMaxJoinedTowns(boolean reachedMaxJoinedTowns) throws EconomyPlayerException {
		if (reachedMaxJoinedTowns) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	@Override
	public void checkForTownNotJoined(List<String> joinedTowns, String townName) throws EconomyPlayerException {
		if (joinedTowns.contains(townName)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.TOWN_ALREADY_JOINED);
		}
	}

	@Override
	public void checkForJobJoined(List<Job> joinedJobs, Job job) throws EconomyPlayerException {
		if (!joinedJobs.contains(job)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.JOB_NOT_JOINED);
		}
	}

	@Override
	public void checkForNotReachedMaxJoinedJobs(boolean reachedMaxJoinedJobs) throws EconomyPlayerException {
		if (reachedMaxJoinedJobs) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.MAX_REACHED);
		}
	}

	@Override
	public void checkForJobNotJoined(List<Job> joinedJobs, Job job) throws EconomyPlayerException {
		if (joinedJobs.contains(job)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.JOB_ALREADY_JOINED);
		}
	}
}
