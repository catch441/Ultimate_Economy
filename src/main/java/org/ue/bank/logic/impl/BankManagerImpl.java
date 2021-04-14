package org.ue.bank.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.ue.bank.dataaccess.api.BankDao;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankManager;
import org.ue.bank.logic.api.BankValidationHandler;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;

public class BankManagerImpl implements BankManager {

	private final MessageWrapper messageWrapper;
	private final BankDao bankDao;
	private final BankValidationHandler validationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private List<BankAccount> accounts = new ArrayList<>();

	@Inject
	public BankManagerImpl(MessageWrapper messageWrapper, BankDao bankDao, BankValidationHandler validationHandler,
			GeneralEconomyValidationHandler generalValidator) {
		this.messageWrapper = messageWrapper;
		this.bankDao = bankDao;
		this.validationHandler = validationHandler;
		this.generalValidator = generalValidator;
	}

	@Override
	public BankAccount createBankAccount(double startAmount) {
		BankAccount account = new BankAccountImpl(generalValidator, bankDao, validationHandler, startAmount);
		getBankAccounts().add(account);
		bankDao.saveIbanList(getIbanList());
		return account;
	}

	@Override
	public BankAccount createExternalBankAccount(double startAmount, String externalIban)
			throws GeneralEconomyException {
		generalValidator.checkForValueNotInList(getIbanList(), externalIban);
		BankAccount account = new BankAccountImpl(generalValidator, bankDao, validationHandler, startAmount,
				externalIban);
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
			getBankAccounts().add(new BankAccountImpl(generalValidator, bankDao, validationHandler, iban));
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
