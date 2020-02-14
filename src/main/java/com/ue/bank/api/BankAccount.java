package com.ue.bank.api;

public interface BankAccount {

	/**
	 * Decrease the bank amount.
	 * 
	 * @param amount has to be positive
	 */
	public void decreaseAmount(double amount);
	
	/**
	 * Increase the bank amount.
	 * 
	 * @param amount has to be positive
	 */
	public void increaseAmount(double amount);
	
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
}
