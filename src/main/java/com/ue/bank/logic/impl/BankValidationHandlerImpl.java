package com.ue.bank.logic.impl;

import java.util.List;

import javax.inject.Inject;

import com.ue.bank.logic.api.BankValidationHandler;
import com.ue.common.utils.MessageWrapper;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

public class BankValidationHandlerImpl implements BankValidationHandler {

	@Inject
	MessageWrapper messageWrapper;

	@Override
	public void checkForPositiveAmount(double amount) throws GeneralEconomyException {
		if (amount < 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					amount);
		}
	}

	@Override
	public void checkForHasEnoughMoney(double bankAmount, double redAmount) throws GeneralEconomyException {
		if (bankAmount < redAmount) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.NOT_ENOUGH_MONEY);
		}
	}

	@Override
	public void checkForIbanIsFree(List<String> ibans, String iban) throws GeneralEconomyException {
		if (ibans.contains(iban)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, iban);
		}
	}
}
