package com.ue.bank.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.ue.bank.impl.AbstractBankEntity;
import com.ue.bank.impl.BankAccountImpl;
import org.bukkit.configuration.file.YamlConfiguration;

public class BankController {

    private static List<BankAccount> accounts;
    private static File bankFile;

    /**
     * Creats a new bank account for a abstract bank entity with a given start
     * amount.
     * 
     * @param owner
     * @param startAmount
     */
    public static void createBankAccount(AbstractBankEntity owner, double startAmount) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
	accounts.add(new BankAccountImpl(owner, startAmount));
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
	YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
	config.set("IbanList", getIbanList());
	config.set(iban, null);
	saveConfig(config);
    }

    /**
     * 
     */
    public static void loadBankAccounts() {
	// TODO loadBankAccounts
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

    private static List<String> getIbanList() {
	List<String> ibans = new ArrayList<>();
	for (BankAccount account : getBankAccounts()) {
	    ibans.add(account.getIban());
	}
	return ibans;
    }

    private static void saveConfig(YamlConfiguration config) {
	try {
	    config.save(bankFile);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
