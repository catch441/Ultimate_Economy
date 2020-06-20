package com.ue.bank.impl;

import java.util.UUID;

import com.ue.bank.api.BankAccount;
import com.ue.exceptions.GeneralEconomyException;

public class BankAccountImpl implements BankAccount {

	private double amount;
	private final String iban;
	private BankSavefileHandler savefileHandler;
	private BankValidationHandler validationHandler;

	/**
	 * Constructor for creating a new bank account.
	 * 
	 * @param startAmount
	 */
	public BankAccountImpl(double startAmount) {
		iban = UUID.randomUUID().toString();
		savefileHandler = new BankSavefileHandler(getIban());
		validationHandler = new BankValidationHandler();
		setAmount(startAmount);
		getSavefileHandler().saveAmount(getAmount());
	}

	/**
	 * Constructor for creating a account with a external iban.
	 * 
	 * @param startAmount
	 * @param externalIban
	 */
	public BankAccountImpl(double startAmount, String externalIban) {
		iban = externalIban;
		savefileHandler = new BankSavefileHandler(getIban());
		validationHandler = new BankValidationHandler();
		setAmount(startAmount);
		getSavefileHandler().saveAmount(getAmount());
	}

	/**
	 * Constructor for loading an existing bank account.
	 * 
	 * @param iban
	 */
	public BankAccountImpl(String iban) {
		this.iban = iban;
		savefileHandler = new BankSavefileHandler(getIban());
		validationHandler = new BankValidationHandler();
		setAmount(getSavefileHandler().loadAmount());
	}

	@Override
	public void decreaseAmount(double amount) throws GeneralEconomyException {
		getValidationHandler().checkForPositiveAmount(amount);
		getValidationHandler().checkForHasEnoughMoney(getAmount(), amount);
		setAmount(getAmount() - amount);
		getSavefileHandler().saveAmount(getAmount());
	}

	@Override
	public void increaseAmount(double amount) throws GeneralEconomyException {
		getValidationHandler().checkForPositiveAmount(amount);
		setAmount(getAmount() + amount);
		getSavefileHandler().saveAmount(getAmount());
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

	private BankSavefileHandler getSavefileHandler() {
		return savefileHandler;
	}

	private BankValidationHandler getValidationHandler() {
		return validationHandler;
	}
}
