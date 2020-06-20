package com.ue.bank.impl;

import java.util.UUID;

import com.ue.bank.api.BankAccount;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;

public class BankAccountImpl implements BankAccount {

	private double amount;
	private final String iban;
	private BankAccountSavefileHandler savefileHandler;

	/**
	 * Constructor for creating a new bank account.
	 * 
	 * @param startAmount
	 */
	public BankAccountImpl(double startAmount) {
		iban = UUID.randomUUID().toString();
		savefileHandler = new BankAccountSavefileHandler(getIban());
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
		savefileHandler = new BankAccountSavefileHandler(getIban());
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
		savefileHandler = new BankAccountSavefileHandler(getIban());
		setAmount(getSavefileHandler().loadAmount());
	}

	@Override
	public void decreaseAmount(double amount) throws GeneralEconomyException {
		checkForPositiveAmount(amount);
		if (getAmount() < amount) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.NOT_ENOUGH_MONEY);
		} else {
			setAmount(getAmount() - amount);
			getSavefileHandler().saveAmount(getAmount());
		}
	}

	@Override
	public void increaseAmount(double amount) throws GeneralEconomyException {
		checkForPositiveAmount(amount);
		setAmount(getAmount() + amount);
		getSavefileHandler().saveAmount(getAmount());
	}

	private void checkForPositiveAmount(double amount) throws GeneralEconomyException {
		if (amount < 0) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, amount);
		}
	}

	@Override
	public boolean hasAmount(double amount) throws GeneralEconomyException {
		checkForPositiveAmount(amount);
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

	private BankAccountSavefileHandler getSavefileHandler() {
		return savefileHandler;
	}
}
