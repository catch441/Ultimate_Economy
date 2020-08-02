package com.ue.bank.logic.api;

import com.ue.ultimate_economy.GeneralEconomyException;

public interface BankValidationHandler {

	/**
	 * Checks for a positive amount.
	 * @param amount
	 * @throws GeneralEconomyException
	 */
	public void checkForPositiveAmount(double amount) throws GeneralEconomyException;

	/**
	 * Checks for bank account has enough money to reduce.
	 * @param bankAmount
	 * @param redAmount
	 * @throws GeneralEconomyException
	 */
	public void checkForHasEnoughMoney(double bankAmount, double redAmount) throws GeneralEconomyException;
}
