package com.ue.bank.logic.api;

import com.ue.general.impl.GeneralEconomyException;

public interface BankValidationHandler {

	/**
	 * Checks for bank account has enough money to reduce.
	 * 
	 * @param bankAmount
	 * @param redAmount
	 * @throws GeneralEconomyException
	 */
	public void checkForHasEnoughMoney(double bankAmount, double redAmount) throws GeneralEconomyException;
}
