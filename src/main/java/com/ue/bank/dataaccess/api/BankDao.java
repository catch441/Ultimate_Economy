package com.ue.bank.dataaccess.api;

import java.util.List;

public interface BankDao {

	/**
	 * Creates a new savefile if no one exists and loads an existing file if the
	 * savefile already exists.
	 * 
	 */
	public void setupSavefile();

	/**
	 * Saves the list of ibans.
	 * 
	 * @param ibans
	 */
	public void saveIbanList(List<String> ibans);

	/**
	 * Loads the iban list.
	 * 
	 * @return list of ibans
	 */
	public List<String> loadIbanList();

	/**
	 * Deletes a account from the savefile.
	 * 
	 * @param iban
	 */
	public void deleteAccount(String iban);

	/**
	 * Loads the bank amount.
	 * 
	 * @param iban
	 * @return amount as double
	 */
	public double loadAmount(String iban);

	/**
	 * Saves the money amount.
	 * 
	 * @param iban
	 * @param amount
	 */
	public void saveAmount(String iban, double amount);
}
