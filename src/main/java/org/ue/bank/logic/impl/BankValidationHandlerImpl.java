package org.ue.bank.logic.impl;

import org.ue.bank.logic.api.BankException;
import org.ue.bank.logic.api.BankValidator;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.impl.GeneralValidatorImpl;
import org.ue.common.utils.api.MessageWrapper;

public class BankValidationHandlerImpl extends GeneralValidatorImpl<BankException> implements BankValidator {

	public BankValidationHandlerImpl(MessageWrapper messageWrapper) {
		super(messageWrapper);
	}

	@Override
	public void checkForHasEnoughMoney(double bankAmount, double redAmount) throws BankException {
		if (bankAmount < redAmount) {
			throw new BankException(messageWrapper, ExceptionMessageEnum.NOT_ENOUGH_MONEY);
		}
	}

	@Override
	protected BankException createNew(MessageWrapper messageWrapper, ExceptionMessageEnum key, Object... params) {
		return new BankException(messageWrapper, key, params);
	}
}
