package com.ue.bank.logic.api;

import java.util.List;

import com.ue.general.impl.GeneralEconomyException;

public interface BankValidationHandler {

	/**
	 * Checks for a positive amount.
	 * 
	 * @param amount
	 * @throws GeneralEconomyException
	 */
	public void checkForPositiveAmount(double amount) throws GeneralEconomyException;

	/**
	 * Checks for bank account has enough money to reduce.
	 * 
	 * @param bankAmount
	 * @param redAmount
	 * @throws GeneralEconomyException
	 */
	public void checkForHasEnoughMoney(double bankAmount, double redAmount) throws GeneralEconomyException;

	/**
	 * Checks for iban is free.
	 * 
	 * @param ibans
	 * @param iban
	 * @throws GeneralEconomyException
	 */
	public void checkForIbanIsFree(List<String> ibans, String iban) throws GeneralEconomyException;
}
