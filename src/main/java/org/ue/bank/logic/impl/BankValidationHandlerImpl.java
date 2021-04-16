package org.ue.bank.logic.impl;

import javax.inject.Inject;

import org.ue.bank.logic.api.BankValidationHandler;
import org.ue.common.logic.impl.GeneralValidationHandlerImpl;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;

public class BankValidationHandlerImpl extends GeneralValidationHandlerImpl implements BankValidationHandler {

	@Inject
	public BankValidationHandlerImpl(MessageWrapper messageWrapper) {
		super(messageWrapper);
	}

	@Override
	public void checkForHasEnoughMoney(double bankAmount, double redAmount) throws GeneralEconomyException {
		if (bankAmount < redAmount) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.NOT_ENOUGH_MONEY);
		}
	}
}
