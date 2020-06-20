package com.ue.bank.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.ultimate_economy.UltimateEconomy;

public class BankSavefileHandler {

	private static File file;
	private static YamlConfiguration config;
	private String iban;
	
	public BankSavefileHandler(String iban) {
		this.iban = iban;
	}
	
	public static void setupSavefile() {
		file = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		if(!file.exists()) {
			try {
				getSavefile().createNewFile();
			} catch (IOException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create savefile");
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
			config = YamlConfiguration.loadConfiguration(getSavefile());
			saveIbanList(new ArrayList<>());
		} else {
			config = YamlConfiguration.loadConfiguration(getSavefile());
		}
	}
	
	/**
	 * Saves the list of ibans.
	 * @param ibans
	 */
	public static void saveIbanList(List<String> ibans) {
		config.set("Ibans", ibans);
		save();
	}
	
	/**
	 * Loads the iban list.
	 * @return list of ibans
	 */
	public static List<String> loadIbanList() {
		return config.getStringList("Ibans");
	}
	
	/**
	 * Deletes a account from the savefile.
	 * @param iban
	 */
	public static void deleteAccount(String iban) {
		config.set(iban, null);
	}
	
	private static File getSavefile() {
		return file;
	}
	
	private static YamlConfiguration getConfig() {
		return config;
	}

	private static void save() {
		try {
			getConfig().save(getSavefile());
		} catch (IOException e) {
			Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
			Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}
	
	/**
	 * Loads the bank amount.
	 * @return amount as double
	 */
	public double loadAmount() {
		return config.getDouble(iban + ".amount");
	}

	public void saveAmount(double amount) {
		config.set(iban + ".amount", amount);
		save();
	}
}
