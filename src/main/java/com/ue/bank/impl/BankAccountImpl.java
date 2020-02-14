package com.ue.bank.impl;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.bank.api.BankAccount;
import com.ue.bank.api.BankController;

public class BankAccountImpl implements BankAccount {

    private double amount;
    private final String iban;
    
    /**
     * Constructor for creating a new bank account.
     * 
     * @param startAmount
     */
    public BankAccountImpl(double startAmount) {
	setAmount(startAmount);
	saveAmount(startAmount);
	iban = UUID.randomUUID().toString();
    }
    
    /**
     * Constructor for creating a account with a external iban.
     * @param startAmount
     * @param externalIban
     */
    public BankAccountImpl(double startAmount, String externalIban) {
	setAmount(startAmount);
	saveAmount(startAmount);
	iban = externalIban;
    }
    
    /**
     * Constructor for loading a existing bank account.
     * @param iban
     */
    public BankAccountImpl(String iban) {
	this.iban = iban;
	loadAmount();
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
    
    private void setAmount(double amount) {
	this.amount = amount;
    }
    
    private void loadAmount() {
	FileConfiguration config = YamlConfiguration.loadConfiguration(BankController.getSavefile());
	setAmount(config.getDouble(iban + ".amount"));
    }
    
    private void saveAmount(double amount) {
	FileConfiguration config = YamlConfiguration.loadConfiguration(BankController.getSavefile());
	config.set(iban + ".amount", amount);
	saveConfig(config);
    }

    @Override
    public String getIban() {
	return iban;
    }

    private static void saveConfig(FileConfiguration config) {
	try {
	    config.save(BankController.getSavefile());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
