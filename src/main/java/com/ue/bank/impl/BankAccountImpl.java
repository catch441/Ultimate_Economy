package com.ue.bank.impl;

import java.util.UUID;

import com.ue.bank.api.BankAccount;

public class BankAccountImpl implements BankAccount {

    private double amount;
    private AbstractBankEntity owner;
    private final String iban;

    /**
     * Constructor for creating a new bank account.
     * 
     * @param owner
     * @param startAmount
     */
    public BankAccountImpl(AbstractBankEntity owner, double startAmount) {
	this.owner = owner;
	amount = startAmount;
	iban = UUID.randomUUID().toString();
    }

    @Override
    public void decreaseAmount(double amount) {
	// TODO decreaseAmount

    }

    @Override
    public void increaseAmount(double amount) {
	// TODO increaseAmount

    }

    @Override
    public double getAmount() {
	return amount;
    }

    @Override
    public AbstractBankEntity getOwner() {
	return owner;
    }

    @Override
    public String getIban() {
	return iban;
    }

}
