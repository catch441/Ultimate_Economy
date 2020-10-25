package com.ue.bank.logic.api;

import java.util.List;

import com.ue.general.impl.GeneralEconomyException;

public interface BankManager {

	/**
	 * Creats a new bank account with a given start amount.
	 * 
	 * @param startAmount
	 * @return bank account
	 */
	public BankAccount createBankAccount(double startAmount);

	/**
	 * Creats a new bank account with a given start amount and a external iban.
	 * 
	 * @param startAmount
	 * @param externalIban
	 * @return bank account
	 * @throws GeneralEconomyException
	 */
	public BankAccount createExternalBankAccount(double startAmount, String externalIban)
			throws GeneralEconomyException;

	/**
	 * Deletes a bank account.
	 * 
	 * @param account
	 */
	public void deleteBankAccount(BankAccount account);

	/**
	 * Loads all bank accounts.
	 * 
	 */
	public void loadBankAccounts();

	/**
	 * Returns a account by a given iban.
	 * 
	 * @param iban
	 * @return the bank account
	 * @throws GeneralEconomyException
	 */
	public BankAccount getBankAccountByIban(String iban) throws GeneralEconomyException;

	/**
	 * Returns a list of bank accounts.
	 * 
	 * @return list of bank accounts
	 */
	public List<BankAccount> getBankAccounts();

	/**
	 * Returns a list of all ibans.
	 * 
	 * @return list of strings
	 */
	public List<String> getIbanList();
}
