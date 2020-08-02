package com.ue.bank.logic.api;

import com.ue.ultimate_economy.GeneralEconomyException;

public interface BankAccount {

	/**
	 * Decrease the bank amount.
	 * 
	 * @param amount has to be positive
	 * @throws GeneralEconomyException 
	 */
	public void decreaseAmount(double amount) throws GeneralEconomyException;
	
	/**
	 * Increase the bank amount.
	 * 
	 * @param amount has to be positive
	 * @throws GeneralEconomyException 
	 */
	public void increaseAmount(double amount) throws GeneralEconomyException;
	
	/**
	 * Returns the bank amount.
	 * 
	 * @return bank amount
	 */
	public double getAmount();
	
	/**
	 * Returns the unique iban of this bank account.
	 * @return iban
	 */
	public String getIban();

	/**
	 * Returns true if the account has enough money.
	 * @param amount
	 * @return boolean
	 * @throws GeneralEconomyException
	 */
	boolean hasAmount(double amount) throws GeneralEconomyException;
}
