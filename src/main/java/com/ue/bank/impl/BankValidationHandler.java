package com.ue.bank.impl;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;

public class BankValidationHandler {

	/**
	 * Checks for a positive amount.
	 * @param amount
	 * @throws GeneralEconomyException
	 */
	public void checkForPositiveAmount(double amount) throws GeneralEconomyException {
		if (amount < 0) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, amount);
		}
	}
	
	/**
	 * Checks for bank account has enough money to reduce.
	 * @param bankAmount
	 * @param redAmount
	 * @throws GeneralEconomyException
	 */
	public void checkForHasEnoughMoney(double bankAmount, double redAmount) throws GeneralEconomyException {
		if (bankAmount < redAmount) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.NOT_ENOUGH_MONEY);
		}
	}
}
