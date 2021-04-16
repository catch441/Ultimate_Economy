package org.ue.bank.logic.impl;

import java.util.UUID;

import org.ue.bank.dataaccess.api.BankDao;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankValidationHandler;
import org.ue.common.logic.api.GeneralValidationHandler;
import org.ue.general.GeneralEconomyException;

public class BankAccountImpl implements BankAccount {

	private final BankDao bankDao;
	private final BankValidationHandler validationHandler;
	private final GeneralValidationHandler generalValidator;
	private double amount;
	private final String iban;

	/**
	 * Constructor for creating a new bank account.
	 * 
	 * @param generalValidator
	 * @param bankDao
	 * @param validationHandler
	 * @param startAmount
	 */
	public BankAccountImpl(GeneralValidationHandler generalValidator, BankDao bankDao,
			BankValidationHandler validationHandler, double startAmount) {
		this.bankDao = bankDao;
		this.validationHandler = validationHandler;
		this.generalValidator = generalValidator;
		iban = UUID.randomUUID().toString();
		setAmount(startAmount);
		bankDao.saveAmount(iban, getAmount());
	}

	/**
	 * Constructor for creating a account with a external iban.
	 * 
	 * @param generalValidator
	 * @param bankDao
	 * @param validationHandler
	 * @param startAmount
	 * @param externalIban
	 */
	public BankAccountImpl(GeneralValidationHandler generalValidator, BankDao bankDao,
			BankValidationHandler validationHandler, double startAmount, String externalIban) {
		this.bankDao = bankDao;
		this.validationHandler = validationHandler;
		this.generalValidator = generalValidator;
		iban = externalIban;
		setAmount(startAmount);
		bankDao.saveAmount(iban, getAmount());
	}

	/**
	 * Constructor for loading an existing bank account.
	 * 
	 * @param generalValidator
	 * @param bankDao
	 * @param validationHandler
	 * @param iban
	 */
	public BankAccountImpl(GeneralValidationHandler generalValidator, BankDao bankDao,
			BankValidationHandler validationHandler, String iban) {
		this.bankDao = bankDao;
		this.validationHandler = validationHandler;
		this.iban = iban;
		this.generalValidator = generalValidator;
		setAmount(bankDao.loadAmount(iban));
	}

	@Override
	public void decreaseAmount(double amount) throws GeneralEconomyException {
		generalValidator.checkForPositiveValue(amount);
		validationHandler.checkForHasEnoughMoney(getAmount(), amount);
		setAmount(getAmount() - amount);
		bankDao.saveAmount(iban, getAmount());
	}

	@Override
	public void increaseAmount(double amount) throws GeneralEconomyException {
		generalValidator.checkForPositiveValue(amount);
		setAmount(getAmount() + amount);
		bankDao.saveAmount(iban, getAmount());
	}

	@Override
	public boolean hasAmount(double amount) throws GeneralEconomyException {
		generalValidator.checkForPositiveValue(amount);
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
}
