package com.ue.bank.dataaccess.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.bank.dataaccess.api.BankDao;
import com.ue.common.utils.SaveFileUtils;
import com.ue.ultimate_economy.UltimateEconomy;

public class BankDaoImpl extends SaveFileUtils implements BankDao {

	private File file;
	private YamlConfiguration config;
	
	@Override
	public void setupSavefile() {
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
	
	@Override
	public void saveIbanList(List<String> ibans) {
		getConfig().set("Ibans", ibans);
		save(getConfig(), file);
	}
	
	@Override
	public List<String> loadIbanList() {
		return getConfig().getStringList("Ibans");
	}
	
	@Override
	public void deleteAccount(String iban) {
		getConfig().set(iban, null);
		save(getConfig(), file);
	}
	
	private File getSavefile() {
		return file;
	}
	
	private YamlConfiguration getConfig() {
		return config;
	}
	
	@Override
	public double loadAmount(String iban) {
		return getConfig().getDouble(iban + ".amount");
	}

	@Override
	public void saveAmount(String iban, double amount) {
		getConfig().set(iban + ".amount", amount);
		save(getConfig(), file);
	}
}
