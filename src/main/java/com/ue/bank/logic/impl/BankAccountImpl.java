package com.ue.bank.logic.impl;

import java.util.UUID;

import com.ue.bank.dataaccess.api.BankDao;
import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankValidationHandler;
import com.ue.ultimate_economy.GeneralEconomyException;

public class BankAccountImpl implements BankAccount {

	private double amount;
	private final String iban;
	private final BankDao bankDao;
	private final BankValidationHandler validationHandler;

	/**
	 * Constructor for creating a new bank account.
	 * 
	 * @param validationHandler
	 * @param bankDao
	 * @param startAmount
	 */
	public BankAccountImpl(BankValidationHandler validationHandler, BankDao bankDao, double startAmount) {
		this.bankDao = bankDao;
		iban = UUID.randomUUID().toString();
		this.validationHandler = validationHandler;
		setAmount(startAmount);
		bankDao.saveAmount(iban, getAmount());
	}

	/**
	 * Constructor for creating a account with a external iban.
	 * 
	 * @param validationHandler
	 * @param bankDao
	 * @param startAmount
	 * @param externalIban
	 */
	public BankAccountImpl(BankValidationHandler validationHandler, BankDao bankDao, double startAmount,
			String externalIban) {
		this.bankDao = bankDao;
		iban = externalIban;
		this.validationHandler = validationHandler;
		setAmount(startAmount);
		bankDao.saveAmount(iban, getAmount());
	}

	/**
	 * Constructor for loading an existing bank account.
	 * 
	 * @param validationHandler
	 * @param bankDao
	 * @param iban
	 */
	public BankAccountImpl(BankValidationHandler validationHandler, BankDao bankDao, String iban) {
		this.bankDao = bankDao;
		this.iban = iban;
		this.validationHandler = validationHandler;
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
