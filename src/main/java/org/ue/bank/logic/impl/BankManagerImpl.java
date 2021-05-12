package org.ue.bank.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.ue.bank.dataaccess.api.BankDao;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankException;
import org.ue.bank.logic.api.BankManager;
import org.ue.bank.logic.api.BankValidator;

public class BankManagerImpl implements BankManager {

	private final BankDao bankDao;
	private final BankValidator validationHandler;
	private Map<String, BankAccount> accounts = new HashMap<>();

	@Inject
	public BankManagerImpl(BankDao bankDao, BankValidator validationHandler) {
		this.bankDao = bankDao;
		this.validationHandler = validationHandler;
	}

	@Override
	public BankAccount createBankAccount(double startAmount) {
		BankAccount account = new BankAccountImpl(bankDao, validationHandler, startAmount);
		accounts.put(account.getIban(), account);
		return account;
	}

	@Override
	public BankAccount createExternalBankAccount(double startAmount, String externalIban) throws BankException {
		validationHandler.checkForValueNotInList(getIbanList(), externalIban);
		BankAccount account = new BankAccountImpl(bankDao, validationHandler, startAmount,
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
			accounts.put(iban, new BankAccountImpl(bankDao, validationHandler, iban));
		}
	}

	@Override
	public BankAccount getBankAccountByIban(String iban) throws BankException {
		BankAccount account = accounts.get(iban);
		validationHandler.checkForValueExists(account, iban);
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
