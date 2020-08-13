package com.ue.bank.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.ue.bank.dataaccess.api.BankDao;
import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.bank.logic.api.BankValidationHandler;
import com.ue.common.utils.MessageWrapper;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

public class BankManagerImpl implements BankManager {

	private final MessageWrapper messageWrapper;
	private final BankDao bankDao;
	private final BankValidationHandler validationHandler;
	private List<BankAccount> accounts = new ArrayList<>();

	/**
	 * Inject constructor.
	 * 
	 * @param messageWrapper
	 * @param bankDao
	 * @param validationHandler
	 */
	@Inject
	public BankManagerImpl(MessageWrapper messageWrapper, BankDao bankDao, BankValidationHandler validationHandler) {
		this.messageWrapper = messageWrapper;
		this.bankDao = bankDao;
		this.validationHandler = validationHandler;
	}

	@Override
	public BankAccount createBankAccount(double startAmount) {
		BankAccount account = new BankAccountImpl(bankDao, validationHandler, startAmount);
		getBankAccounts().add(account);
		bankDao.saveIbanList(getIbanList());
		return account;
	}

	@Override
	public BankAccount createExternalBankAccount(double startAmount, String externalIban)
			throws GeneralEconomyException {
		validationHandler.checkForIbanIsFree(getIbanList(), externalIban);
		BankAccount account = new BankAccountImpl(bankDao, validationHandler, startAmount, externalIban);
		getBankAccounts().add(account);
		bankDao.saveIbanList(getIbanList());
		return account;
	}

	@Override
	public void deleteBankAccount(BankAccount account) {
		getBankAccounts().remove(account);
		bankDao.saveIbanList(getIbanList());
		bankDao.deleteAccount(account.getIban());
	}

	@Override
	public void loadBankAccounts() {
		bankDao.setupSavefile();
		for (String iban : bankDao.loadIbanList()) {
			getBankAccounts().add(new BankAccountImpl(bankDao, validationHandler, iban));
		}
	}

	@Override
	public BankAccount getBankAccountByIban(String iban) throws GeneralEconomyException {
		for (BankAccount account : getBankAccounts()) {
			if (account.getIban().equals(iban)) {
				return account;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, iban);
	}

	@Override
	public List<BankAccount> getBankAccounts() {
		return accounts;
	}

	@Override
	public List<String> getIbanList() {
		List<String> ibans = new ArrayList<>();
		for (BankAccount account : getBankAccounts()) {
			ibans.add(account.getIban());
		}
		return ibans;
	}
}
