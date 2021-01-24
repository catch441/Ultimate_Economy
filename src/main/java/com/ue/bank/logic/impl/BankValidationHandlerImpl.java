package com.ue.bank.logic.impl;

import javax.inject.Inject;

import com.ue.bank.logic.api.BankValidationHandler;
import com.ue.common.utils.MessageWrapper;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BankValidationHandlerImpl implements BankValidationHandler {

	private final MessageWrapper messageWrapper;

	@Override
	public void checkForHasEnoughMoney(double bankAmount, double redAmount) throws GeneralEconomyException {
		if (bankAmount < redAmount) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.NOT_ENOUGH_MONEY);
		}
	}
}
