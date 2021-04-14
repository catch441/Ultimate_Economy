package org.ue.bank.dataaccess.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.bukkit.configuration.file.YamlConfiguration;
import org.ue.bank.dataaccess.api.BankDao;
import org.ue.common.utils.SaveFileUtils;
import org.ue.common.utils.ServerProvider;

public class BankDaoImpl extends SaveFileUtils implements BankDao {

	private final ServerProvider serverProvider;
	
	@Inject
	public BankDaoImpl(ServerProvider serverProvider) {
		this.serverProvider = serverProvider;
	}
	
	@Override
	public void setupSavefile() {
		file = new File(serverProvider.getDataFolderPath(), "BankAccounts.yml");
		if(!file.exists()) {
			createFile(file);
			config = YamlConfiguration.loadConfiguration(file);
		} else {
			config = YamlConfiguration.loadConfiguration(file);
		}
	}
	
	@Override
	public List<String> loadIbanList() {
		removeIbanList();
		Set<String> keySet = config.getKeys(false);
		return new ArrayList<String>(keySet);
	}
	
	@Override
	public void deleteAccount(String iban) {
		config.set(iban, null);
		save();
	}
	
	@Override
	public double loadAmount(String iban) {
		return config.getDouble(iban + ".amount");
	}

	@Override
	public void saveAmount(String iban, double amount) {
		config.set(iban + ".amount", amount);
		save();
	}
	
	/**
	 * @since 1.2.7
	 */
	@Deprecated
	private void removeIbanList() {
		if (config.getConfigurationSection("Ibans") != null) {
			config.set("Ibans", null);
			save();
		}
	}
}
