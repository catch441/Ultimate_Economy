package com.ue.bank.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.ue.bank.impl.BankAccountImpl;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.ultimate_economy.UltimateEconomy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BankController {

    private static List<BankAccount> accounts = new ArrayList<>();
    private static File bankFile;

    /**
     * Creats a new bank account with a given start amount.
     * 
     * @param startAmount
     * @return bank account
     */
    public static BankAccount createBankAccount(double startAmount) {
	BankAccount account = new BankAccountImpl(startAmount);
	accounts.add(account);
	FileConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
	config.set("Ibans", getIbanList());
	saveConfig(config);
	return account;
    }

    /**
     * Creats a new bank account with a given start amount and a external iban.
     * 
     * @param startAmount
     * @param externalIban
     * @throws GeneralEconomyException
     */
    public static void createExternalBankAccount(double startAmount, String externalIban)
	    throws GeneralEconomyException {
	if (getIbanList().contains(externalIban)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, externalIban);
	}	
	accounts.add(new BankAccountImpl(startAmount, externalIban));
	FileConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
	config.set("Ibans", getIbanList());
	saveConfig(config);
    }

    /**
     * Deletes a bank account.
     * 
     * @param account
     */
    public static void deleteBankAccount(BankAccount account) {
	accounts.remove(account);
	FileConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
	config.set("Ibans", getIbanList());
	config.set(account.getIban(), null);
	saveConfig(config);
    }

    /**
     * Loads all bank accounts.
     */
    public static void loadBankAccounts() {
	bankFile = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
	FileConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
	if (!bankFile.exists()) {
	    try {
		bankFile.createNewFile();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    config.set("Ibans", new ArrayList<>());
	    saveConfig(config);
	} else {
	    for (String iban : config.getStringList("Ibans")) {
		accounts.add(new BankAccountImpl(iban));
	    }
	}
    }

    /**
     * Returns a account by a given iban.
     * 
     * @param iban
     * @return the bank account
     * @throws GeneralEconomyException
     */
    public static BankAccount getBankAccountByIban(String iban) throws GeneralEconomyException {
	for (BankAccount account : getBankAccounts()) {
	    if (account.getIban().equals(iban)) {
		return account;
	    }
	}
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, iban);
    }

    /**
     * Returns a list of bank accounts.
     * 
     * @return list of bank accounts
     */
    public static List<BankAccount> getBankAccounts() {
	return accounts;
    }

    /**
     * Returns the savefile of the bank accounts.
     * 
     * @return bankfile
     */
    public static File getSavefile() {
	return bankFile;
    }

    /**
     * Returns a list of all ibans.
     * 
     * @return list of strings
     */
    public static List<String> getIbanList() {
	List<String> ibans = new ArrayList<>();
	for (BankAccount account : getBankAccounts()) {
	    ibans.add(account.getIban());
	}
	return ibans;
    }

    private static void saveConfig(FileConfiguration config) {
	try {
	    config.save(bankFile);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
