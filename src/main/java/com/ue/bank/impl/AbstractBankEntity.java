package com.ue.bank.impl;

import com.ue.bank.api.BankAccount;

public abstract class AbstractBankEntity {

	// represents a object that can have a bank account
	
	private BankAccount bankAccount = new BankAccountImpl(this,0);
	
	/**
	 * Returns the bank account.
	 * 
	 * @return bankAccount
	 */
	public BankAccount getBankAccount() {
		return bankAccount;
	}
}
