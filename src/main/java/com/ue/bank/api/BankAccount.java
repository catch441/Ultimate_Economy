package com.ue.bank.api;

import com.ue.bank.impl.AbstractBankEntity;

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
	 * Returns the owner of this bank account. It can be a town or a economy player.
	 * 
	 * @return AbstractBankEntity implemented by town and economy player
	 */
	public AbstractBankEntity getOwner();
	
	/**
	 * Returns the unique iban of this bank account.
	 * @return iban
	 */
	public String getIban();
}
