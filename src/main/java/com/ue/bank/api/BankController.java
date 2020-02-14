package com.ue.bank.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.ue.bank.impl.BankAccountImpl;
import com.ue.ultimate_economy.UltimateEconomy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BankController {

    private static List<BankAccount> accounts = new ArrayList<>();
    private static File bankFile;

    /**
     * Creats a new bank account with a given start
     * amount.
     * 
     * @param startAmount
     */
    public static void createBankAccount(double startAmount) {
	FileConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
	accounts.add(new BankAccountImpl(startAmount));
	config.set("IbanList", getIbanList());
	saveConfig(config);
    }

    /**
     * Delets a bank account by a iban.
     * 
     * @param iban
     */
    public static void deleteBankAccount(String iban) {
	if (!getIbanList().contains(iban)) {
	    // TODO throw error -> account does not exist
	}
	BankAccount account = getBankAccountByIban(iban);
	accounts.remove(account);
	FileConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
	config.set("IbanList", getIbanList());
	config.set(iban, null);
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
	    for(String iban:config.getStringList("Ibans")) {
		accounts.add(new BankAccountImpl(iban));
	    }
	}
    }

    /**
     * Returns a account by a given iban.
     * 
     * @param iban
     * @return the bank account
     */
    public static BankAccount getBankAccountByIban(String iban) {
	for (BankAccount account : getBankAccounts()) {
	    if (account.getIban().equals(iban)) {
		return account;
	    }
	}
	// TODO throw error -> account does not exist
	return null;
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
     * @return bankfile
     */
    public static File getSavefile() {
	return bankFile;
    }

    private static List<String> getIbanList() {
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
