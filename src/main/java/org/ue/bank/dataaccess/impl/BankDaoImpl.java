package org.ue.bank.dataaccess.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
