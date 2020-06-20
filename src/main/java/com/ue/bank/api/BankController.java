package com.ue.bank.api;

import java.util.ArrayList;
import java.util.List;
import com.ue.bank.impl.BankAccountImpl;
import com.ue.bank.impl.BankSavefileHandler;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;

public class BankController {

	private static List<BankAccount> accounts = new ArrayList<>();

	/**
	 * Creats a new bank account with a given start amount.
	 * 
	 * @param startAmount
	 * @return bank account
	 */
	public static BankAccount createBankAccount(double startAmount) {
		BankAccount account = new BankAccountImpl(startAmount);
		getBankAccounts().add(account);
		BankSavefileHandler.saveIbanList(getIbanList());
		return account;
	}

	/**
	 * Creats a new bank account with a given start amount and a external iban.
	 * 
	 * @param startAmount
	 * @param externalIban
	 * @throws GeneralEconomyException
	 */
	public static void createExternalBankAccount(double startAmount, String externalIban)
			throws GeneralEconomyException {
		if (getIbanList().contains(externalIban)) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, externalIban);
		}
		getBankAccounts().add(new BankAccountImpl(startAmount, externalIban));
		BankSavefileHandler.saveIbanList(getIbanList());
	}

	/**
	 * Deletes a bank account.
	 * 
	 * @param account
	 */
	public static void deleteBankAccount(BankAccount account) {
		getBankAccounts().remove(account);
		BankSavefileHandler.saveIbanList(getIbanList());
		BankSavefileHandler.deleteAccount(account.getIban());
	}

	/**
	 * Loads all bank accounts.
	 */
	public static void loadBankAccounts() {
		BankSavefileHandler.setupSavefile();
		for (String iban : BankSavefileHandler.loadIbanList()) {
			getBankAccounts().add(new BankAccountImpl(iban));
		}
	}

	/**
	 * Returns a account by a given iban.
	 * 
	 * @param iban
	 * @return the bank account
	 * @throws GeneralEconomyException
	 */
	public static BankAccount getBankAccountByIban(String iban) throws GeneralEconomyException {
		for (BankAccount account : getBankAccounts()) {
			if (account.getIban().equals(iban)) {
				return account;
			}
		}
		throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, iban);
	}

	/**
	 * Returns a list of bank accounts.
	 * 
	 * @return list of bank accounts
	 */
	public static List<BankAccount> getBankAccounts() {
		return accounts;
	}

	/**
	 * Returns a list of all ibans.
	 * 
	 * @return list of strings
	 */
	public static List<String> getIbanList() {
		List<String> ibans = new ArrayList<>();
		for (BankAccount account : getBankAccounts()) {
			ibans.add(account.getIban());
		}
		return ibans;
	}
}
