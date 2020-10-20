package com.ue.config.dataaccess.impl;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.SaveFileUtils;
import com.ue.config.dataaccess.api.ConfigDao;

public class ConfigDaoImpl extends SaveFileUtils implements ConfigDao {

	/**
	 * Config Data Access constructor.
	 * 
	 * @param serverProvider
	 * @param logger
	 */
	@Inject
	public ConfigDaoImpl(ServerProvider serverProvider, Logger logger) {
		super(logger);
		file = new File(serverProvider.getDataFolderPath(), "config.yml");
		if (!file.exists()) {
			createFile(file);
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public int loadMaxRentedDays() {
		return config.getInt("MaxRentedDays");
	}

	@Override
	public boolean hasMaxRentedDays() {
		return config.isSet("MaxRentedDays");
	}

	@Override
	public void saveMaxRentedDays(Integer maxRentedDays) {
		config.set("MaxRentedDays", maxRentedDays);
		save(config, file);
	}

	@Override
	public boolean loadExtendedInteraction() {
		return config.getBoolean("ExtendedInteraction");
	}

	@Override
	public boolean hasExtendedInteraction() {
		return config.isSet("ExtendedInteraction");
	}

	@Override
	public void saveExtendedInteraction(Boolean extendedInteraction) {
		config.set("ExtendedInteraction", extendedInteraction);
		save(config, file);
	}

	@Override
	public boolean loadWildernessInteraction() {
		return config.getBoolean("WildernessInteraction");
	}

	@Override
	public boolean hasWildernessInteraction() {
		return config.isSet("WildernessInteraction");
	}

	@Override
	public void saveWildernessInteraction(Boolean wildernessInteraction) {
		config.set("WildernessInteraction", wildernessInteraction);
		save(config, file);
	}

	@Override
	public String loadCurrencyPl() {
		return config.getString("currencyPl");
	}

	@Override
	public boolean hasCurrencyPl() {
		return config.isSet("currencyPl");
	}

	@Override
	public void saveCurrencyPl(String currencyPl) {
		config.set("currencyPl", currencyPl);
		save(config, file);
	}

	@Override
	public String loadCurrencySg() {
		return config.getString("currencySg");
	}

	@Override
	public boolean hasCurrencySg() {
		return config.isSet("currencySg");
	}

	@Override
	public void saveCurrencySg(String currencySg) {
		config.set("currencySg", currencySg);
		save(config, file);
	}

	@Override
	public boolean loadHomesFeature() {
		return config.getBoolean("homes");
	}

	@Override
	public boolean hasHomesFeature() {
		return config.isSet("homes");
	}

	@Override
	public void saveHomesFeature(Boolean homesFeature) {
		config.set("homes", homesFeature);
		save(config, file);
	}

	@Override
	public int loadMaxPlayershops() {
		return config.getInt("MaxPlayershops");
	}

	@Override
	public boolean hasMaxPlayershops() {
		return config.isSet("MaxPlayershops");
	}

	@Override
	public void saveMaxPlayershops(Integer maxPlayershops) {
		config.set("MaxPlayershops", maxPlayershops);
		save(config, file);
	}

	@Override
	public int loadMaxJoinedTowns() {
		return config.getInt("MaxJoinedTowns");
	}

	@Override
	public boolean hasMaxJoinedTowns() {
		return config.isSet("MaxJoinedTowns");
	}

	@Override
	public void saveMaxJoinedTowns(Integer maxJoinedTowns) {
		config.set("MaxJoinedTowns", maxJoinedTowns);
		save(config, file);
	}

	@Override
	public int loadMaxJobs() {
		return config.getInt("MaxJobs");
	}

	@Override
	public boolean hasMaxJobs() {
		return config.isSet("MaxJobs");
	}

	@Override
	public void saveMaxJobs(Integer maxJobs) {
		config.set("MaxJobs", maxJobs);
		save(config, file);
	}

	@Override
	public int loadMaxHomes() {
		return config.getInt("MaxHomes");
	}

	@Override
	public boolean hasMaxHomes() {
		return config.isSet("MaxHomes");
	}

	@Override
	public void saveMaxHomes(Integer maxHomes) {
		config.set("MaxHomes", maxHomes);
		save(config, file);
	}

	@Override
	public String loadCountry() {
		return config.getString("localeCountry");
	}

	@Override
	public boolean hasCountry() {
		return config.isSet("localeCountry");
	}

	@Override
	public void saveCountry(String country) {
		config.set("localeCountry", country);
		save(config, file);
	}

	@Override
	public String loadLanguage() {
		return config.getString("localeLanguage");
	}

	@Override
	public boolean hasLanguage() {
		return config.isSet("localeLanguage");
	}

	@Override
	public void saveLanguage(String language) {
		config.set("localeLanguage", language);
		save(config, file);
	}

	@Override
	public void saveJobcenterList(List<String> jobcenters) {
		config.set("JobCenterNames", jobcenters);
		save(config, file);
	}

	@Override
	public List<String> loadJobcenterList() {
		return config.getStringList("JobCenterNames");
	}

	@Override
	public void saveJobList(List<String> jobs) {
		config.set("JobList", jobs);
		save(config, file);
	}
	
	@Override
	public List<String> loadRentshopIds() {
		return config.getStringList("RentShopIds");
	}

	@Override
	public void saveRentshopIds(List<String> ids) {
		config.set("RentShopIds", ids);
		save(config, file);
	}

	@Override
	public List<String> loadPlayershopIds() {
		return config.getStringList("PlayerShopIds");
	}

	@Override
	public void savePlayershopIds(List<String> ids) {
		config.set("PlayerShopIds", ids);
		save(config, file);
	}
	
	@Override
	public void saveTownworldNamesList(List<String> townworlds) {
		config.set("TownWorlds", townworlds);
		save(config, file);
	}

	@Override
	public List<String> loadTownworldNames() {
		return config.getStringList("TownWorlds");
	}

	@Override
	public List<String> loadAdminshopIds() {
		changeAdminshopIdSavename();
		return config.getStringList("AdminShopIds");
	}

	@Deprecated
	private void changeAdminshopIdSavename() {
		if (config.contains("AdminshopIds")) {
			config.set("AdminShopIds", config.get("AdminshopIds"));
			config.set("AdminshopIds", null);
			save(config, file);
		}
	}

	@Override
	public void saveAdminshopIds(List<String> ids) {
		config.set("AdminShopIds", ids);
		save(config, file);
	}

	@Override
	public List<String> loadJobList() {
		return config.getStringList("JobList");
	}
	
	@Override
	public void saveStartAmount(double amount) {
		config.set("StartAmount", amount);
		save(config, file);
	}
	
	@Override
	public boolean hasStartAmount() {
		return config.isSet("StartAmount");
	}

	@Override
	public double loadStartAmount() {
		return config.getDouble("StartAmount");
	}

	@Deprecated
	@Override
	public List<String> loadPlayerShopNames() {
		return config.getStringList("PlayerShopNames");
	}

	@Deprecated
	@Override
	public boolean hasPlayerShopNames() {
		return config.isSet("PlayerShopNames");
	}

	@Deprecated
	@Override
	public void removeDeprecatedTownNames() {
		config.set("TownNames", null);
		save(config, file);
	}

	@Deprecated
	@Override
	public void removeDeprecatedPlayerShopNames() {
		config.set("PlayerShopNames", null);
		save(config, file);
	}
	
	
	@Deprecated
	@Override
	public List<String> loadAdminShopNames() {
		return config.getStringList("ShopNames");
	}

	@Deprecated
	@Override
	public boolean hasAdminShopNames() {
		return config.isSet("ShopNames");
	}

	@Deprecated
	@Override
	public void removeDeprecatedAdminshopNames() {
		config.set("ShopNames", null);
		save(config, file);
	}
}