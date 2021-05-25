package org.ue.economyplayer.logic.impl;

import java.util.List;

import org.ue.bank.logic.api.BankAccount;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.impl.GeneralValidatorImpl;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerValidator;
import org.ue.jobsystem.logic.api.Job;

public class EconomyPlayerValidatorImpl extends GeneralValidatorImpl<EconomyPlayerException>
		implements EconomyPlayerValidator {

	public EconomyPlayerValidatorImpl(MessageWrapper messageWrapper) {
		super(messageWrapper);
	}
	
	@Override
	protected EconomyPlayerException createNew(MessageWrapper messageWrapper, ExceptionMessageEnum key,
			Object... params) {
		return new EconomyPlayerException(messageWrapper, key, params);
	}

	@Override
	public void checkForEnoughMoney(BankAccount account, double amount, boolean personal)
			throws EconomyPlayerException {
		if (account.getAmount() < amount) {
			if (personal) {
				throw createNew(messageWrapper,
						ExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL);
			} else {
				throw createNew(messageWrapper,
						ExceptionMessageEnum.NOT_ENOUGH_MONEY_NON_PERSONAL);
			}
		}
	}

	@Override
	public void checkForJoinedTown(List<String> joinedTowns, String townName) throws EconomyPlayerException {
		if (!joinedTowns.contains(townName)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.TOWN_NOT_JOINED);
		}
	}

	@Override
	public void checkForTownNotJoined(List<String> joinedTowns, String townName) throws EconomyPlayerException {
		if (joinedTowns.contains(townName)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.TOWN_ALREADY_JOINED);
		}
	}

	@Override
	public void checkForJobJoined(List<Job> joinedJobs, Job job) throws EconomyPlayerException {
		if (!joinedJobs.contains(job)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.JOB_NOT_JOINED);
		}
	}

	@Override
	public void checkForJobNotJoined(List<Job> joinedJobs, Job job) throws EconomyPlayerException {
		if (joinedJobs.contains(job)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.JOB_ALREADY_JOINED);
		}
	}
}
