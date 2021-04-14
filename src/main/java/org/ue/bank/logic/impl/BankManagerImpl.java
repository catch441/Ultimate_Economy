package org.ue.bank.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.ue.bank.dataaccess.api.BankDao;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankManager;
import org.ue.bank.logic.api.BankValidationHandler;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;

public class BankManagerImpl implements BankManager {

	private final BankDao bankDao;
	private final BankValidationHandler validationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private Map<String, BankAccount> accounts = new HashMap<>();

	@Inject
	public BankManagerImpl(BankDao bankDao, BankValidationHandler validationHandler,
			GeneralEconomyValidationHandler generalValidator) {
		this.bankDao = bankDao;
		this.validationHandler = validationHandler;
		this.generalValidator = generalValidator;
	}

	@Override
	public BankAccount createBankAccount(double startAmount) {
		BankAccount account = new BankAccountImpl(generalValidator, bankDao, validationHandler, startAmount);
		accounts.put(account.getIban(), account);
		return account;
	}

	@Override
	public BankAccount createExternalBankAccount(double startAmount, String externalIban)
			throws GeneralEconomyException {
		generalValidator.checkForValueNotInList(getIbanList(), externalIban);
		BankAccount account = new BankAccountImpl(generalValidator, bankDao, validationHandler, startAmount,
				externalIban);
		accounts.put(externalIban, account);
		return account;
	}

	@Override
	public void deleteBankAccount(BankAccount account) {
		accounts.remove(account.getIban());
		bankDao.deleteAccount(account.getIban());
	}

	@Override
	public void loadBankAccounts() {
		bankDao.setupSavefile();
		for (String iban : bankDao.loadIbanList()) {
			accounts.put(iban, new BankAccountImpl(generalValidator, bankDao, validationHandler, iban));
		}
	}

	@Override
	public BankAccount getBankAccountByIban(String iban) throws GeneralEconomyException {
		BankAccount account = accounts.get(iban);
		generalValidator.checkForValueExists(account, iban);
		return account;
	}

	@Override
	public List<BankAccount> getBankAccounts() {
		return new ArrayList<>(accounts.values());
	}

	@Override
	public List<String> getIbanList() {
		return new ArrayList<>(accounts.keySet());
	}
}
