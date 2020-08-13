package com.ue.config.dataaccess.impl;

import java.io.File;

import javax.inject.Inject;

import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.common.utils.BukkitService;
import com.ue.common.utils.SaveFileUtils;
import com.ue.config.dataaccess.api.ConfigDao;

public class ConfigDaoImpl extends SaveFileUtils implements ConfigDao {

	private final YamlConfiguration fileConfig;
	private final File file;
	
	/**
	 * Config Data Access constructor.
	 */
	@Inject
	public ConfigDaoImpl(BukkitService bukkitService) {
		file = new File(bukkitService.getDataFolderPath(), "config.yml");
		if (!file.exists()) {
			createFile(file);
		}
		fileConfig = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public int loadMaxRentedDays() {
		return fileConfig.getInt("MaxRentedDays");
	}

	@Override
	public boolean hasMaxRentedDays() {
		return fileConfig.isSet("MaxRentedDays");
	}

	@Override
	public void saveMaxRentedDays(Integer maxRentedDays) {
		fileConfig.set("MaxRentedDays", maxRentedDays);
		save(fileConfig, file);
	}

	@Override
	public boolean loadExtendedInteraction() {
		return fileConfig.getBoolean("ExtendedInteraction");
	}

	@Override
	public boolean hasExtendedInteraction() {
		return fileConfig.isSet("ExtendedInteraction");
	}

	@Override
	public void saveExtendedInteraction(Boolean extendedInteraction) {
		fileConfig.set("ExtendedInteraction", extendedInteraction);
		save(fileConfig, file);
	}

	@Override
	public boolean loadWildernessInteraction() {
		return fileConfig.getBoolean("WildernessInteraction");
	}

	@Override
	public boolean hasWildernessInteraction() {
		return fileConfig.isSet("WildernessInteraction");
	}

	@Override
	public void saveWildernessInteraction(Boolean wildernessInteraction) {
		fileConfig.set("WildernessInteraction", wildernessInteraction);
		save(fileConfig, file);
	}

	@Override
	public String loadCurrencyPl() {
		return fileConfig.getString("currencyPl");
	}

	@Override
	public boolean hasCurrencyPl() {
		return fileConfig.isSet("currencyPl");
	}
	
	@Override
	public void saveCurrencyPl(String currencyPl) {
		fileConfig.set("currencyPl", currencyPl);
		save(fileConfig, file);
	}

	@Override
	public String loadCurrencySg() {
		return fileConfig.getString("currencySg");
	}

	@Override
	public boolean hasCurrencySg() {
		return fileConfig.isSet("currencySg");
	}
	
	@Override
	public void saveCurrencySg(String currencySg) {
		fileConfig.set("currencySg", currencySg);
		save(fileConfig, file);
	}

	@Override
	public boolean loadHomesFeature() {
		return fileConfig.getBoolean("homes");
	}

	@Override
	public boolean hasHomesFeature() {
		return fileConfig.isSet("homes");
	}
	
	@Override
	public void saveHomesFeature(Boolean homesFeature) {
		fileConfig.set("homes", homesFeature);
		save(fileConfig, file);
	}

	@Override
	public int loadMaxPlayershops() {
		return fileConfig.getInt("MaxPlayershops");
	}

	@Override
	public boolean hasMaxPlayershops() {
		return fileConfig.isSet("MaxPlayershops");
	}
	
	@Override
	public void saveMaxPlayershops(Integer maxPlayershops) {
		fileConfig.set("MaxPlayershops", maxPlayershops);
		save(fileConfig, file);
	}

	@Override
	public int loadMaxJoinedTowns() {
		return fileConfig.getInt("MaxJoinedTowns");
	}

	@Override
	public boolean hasMaxJoinedTowns() {
		return fileConfig.isSet("MaxJoinedTowns");
	}
	
	@Override
	public void saveMaxJoinedTowns(Integer maxJoinedTowns) {
		fileConfig.set("MaxJoinedTowns", maxJoinedTowns);
		save(fileConfig, file);
	}

	@Override
	public int loadMaxJobs() {
		return fileConfig.getInt("MaxJobs");
	}

	@Override
	public boolean hasMaxJobs() {
		return fileConfig.isSet("MaxJobs");
	}
	
	@Override
	public void saveMaxJobs(Integer maxJobs) {
		fileConfig.set("MaxJobs", maxJobs);
		save(fileConfig, file);
	}

	@Override
	public int loadMaxHomes() {
		return fileConfig.getInt("MaxHomes");
	}

	@Override
	public boolean hasMaxHomes() {
		return fileConfig.isSet("MaxHomes");
	}
	
	@Override
	public void saveMaxHomes(Integer maxHomes) {
		fileConfig.set("MaxHomes", maxHomes);
		save(fileConfig, file);
	}
	
	@Override
	public String loadCountry() {
		return fileConfig.getString("localeCountry");
	}

	@Override
	public boolean hasCountry() {
		return fileConfig.isSet("localeCountry");
	}
	
	@Override
	public void saveCountry(String country) {
		fileConfig.set("localeCountry", country);
		save(fileConfig, file);
	}
	
	@Override
	public String loadLanguage() {
		return fileConfig.getString("localeLanguage");
	}

	@Override
	public boolean hasLanguage() {
		return fileConfig.isSet("localeLanguage");
	}
	
	@Override
	public void saveLanguage(String language) {
		fileConfig.set("localeLanguage", language);
		save(fileConfig, file);
	}
	
	@Deprecated
	@Override
	public void removeDeprecated() {
		fileConfig.set("TownNames", null);
		save(fileConfig, file);
	}
}
