package com.ue.bank.logic.impl;

import java.util.UUID;

import com.ue.bank.dataaccess.api.BankDao;
import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankValidationHandler;
import com.ue.general.impl.GeneralEconomyException;

public class BankAccountImpl implements BankAccount {

	private final BankDao bankDao;
	private final BankValidationHandler validationHandler;
	private double amount;
	private final String iban;

	/**
	 * Constructor for creating a new bank account.
	 * 
	 * @param bankDao
	 * @param validationHandler
	 * @param startAmount
	 */
	public BankAccountImpl(BankDao bankDao, BankValidationHandler validationHandler, double startAmount) {
		this.bankDao = bankDao;
		this.validationHandler = validationHandler;
		iban = UUID.randomUUID().toString();
		setAmount(startAmount);
		bankDao.saveAmount(iban, getAmount());
	}

	/**
	 * Constructor for creating a account with a external iban.
	 * 
	 * @param bankDao
	 * @param validationHandler
	 * @param startAmount
	 * @param externalIban
	 */
	public BankAccountImpl(BankDao bankDao, BankValidationHandler validationHandler, double startAmount,
			String externalIban) {
		this.bankDao = bankDao;
		this.validationHandler = validationHandler;
		iban = externalIban;
		setAmount(startAmount);
		bankDao.saveAmount(iban, getAmount());
	}

	/**
	 * Constructor for loading an existing bank account.
	 * 
	 * @param bankDao
	 * @param validationHandler
	 * @param iban
	 */
	public BankAccountImpl(BankDao bankDao, BankValidationHandler validationHandler, String iban) {
		this.bankDao = bankDao;
		this.validationHandler = validationHandler;
		this.iban = iban;
		setAmount(bankDao.loadAmount(iban));
	}

	@Override
	public void decreaseAmount(double amount) throws GeneralEconomyException {
		getValidationHandler().checkForPositiveAmount(amount);
		getValidationHandler().checkForHasEnoughMoney(getAmount(), amount);
		setAmount(getAmount() - amount);
		bankDao.saveAmount(iban, getAmount());
	}

	@Override
	public void increaseAmount(double amount) throws GeneralEconomyException {
		getValidationHandler().checkForPositiveAmount(amount);
		setAmount(getAmount() + amount);
		bankDao.saveAmount(iban, getAmount());
	}

	@Override
	public boolean hasAmount(double amount) throws GeneralEconomyException {
		getValidationHandler().checkForPositiveAmount(amount);
		if (getAmount() < amount) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public double getAmount() {
		return amount;
	}

	private void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String getIban() {
		return iban;
	}

	private BankValidationHandler getValidationHandler() {
		return validationHandler;
	}
}
