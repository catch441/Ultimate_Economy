package com.ue.config.logic.impl;

import java.util.Locale;

import javax.inject.Inject;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

public class ConfigManagerImpl implements ConfigManager {

	private final ConfigDao configDao;
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerManager ecoPlayerManager;

	private int maxHomes;
	private int maxJobs;
	private int maxJoinedTowns;
	private int maxPlayershops;
	private boolean homesSystem;
	private String currencyPl;
	private String currencySg;
	private int maxRentedDays;
	private boolean extendedInteraction;
	private boolean wildernessInteraction;
	private Locale locale;

	/**
	 * Config manager constructor.
	 * 
	 * @param ecoPlayerManager
	 * @param configDao
	 * @param messageWrapper
	 */
	@Inject
	public ConfigManagerImpl(EconomyPlayerManager ecoPlayerManager, ConfigDao configDao,
			MessageWrapper messageWrapper) {
		this.configDao = configDao;
		this.messageWrapper = messageWrapper;
		this.ecoPlayerManager = ecoPlayerManager;
	}

	@Override
	public void setupConfig() {
		try {
			setupMaxHomes();
			setupMaxJobs();
			setupMaxJoinedTowns();
			setupMaxPlayershops();
			setupHomesFeature();
			setupMaxRentedDays();
			setupExtendedInteraction();
			setupWildernessInteraction();
			setupCurrencyPl();
			setupCurrencySg();
			setupLocale();
		} catch (GeneralEconomyException e) {
		}
	}

	private void setupLocale() throws GeneralEconomyException {
		if (!configDao.hasCountry()) {
			setLocale("en", "US");
		} else {
			locale = new Locale(configDao.loadLanguage(), configDao.loadCountry());
		}
	}

	private void setupMaxRentedDays() throws GeneralEconomyException {
		if (!configDao.hasMaxRentedDays()) {
			setMaxRentedDays(14);
		} else {
			maxRentedDays = configDao.loadMaxRentedDays();
		}
	}

	private void setupExtendedInteraction() {
		if (!configDao.hasExtendedInteraction()) {
			setExtendedInteraction(false);
		} else {
			extendedInteraction = configDao.loadExtendedInteraction();
		}
	}

	private void setupWildernessInteraction() {
		if (!configDao.hasWildernessInteraction()) {
			setWildernessInteraction(false);
		} else {
			wildernessInteraction = configDao.loadWildernessInteraction();
		}
	}

	private void setupCurrencyPl() {
		if (!configDao.hasCurrencyPl()) {
			setCurrencyPl("$");
		} else {
			currencyPl = configDao.loadCurrencyPl();
		}
	}

	private void setupCurrencySg() {
		if (!configDao.hasCurrencySg()) {
			setCurrencySg("$");
		} else {
			currencySg = configDao.loadCurrencySg();
		}
	}

	private void setupHomesFeature() {
		if (!configDao.hasHomesFeature()) {
			setHomeSystem(true);
		} else {
			homesSystem = configDao.loadHomesFeature();
		}
	}

	private void setupMaxPlayershops() throws GeneralEconomyException {
		if (!configDao.hasMaxPlayershops()) {
			setMaxPlayershops(3);
		} else {
			maxPlayershops = configDao.loadMaxPlayershops();
		}
	}

	private void setupMaxJoinedTowns() throws GeneralEconomyException {
		if (!configDao.hasMaxJoinedTowns()) {
			setMaxJoinedTowns(1);
		} else {
			maxJoinedTowns = configDao.loadMaxJoinedTowns();
		}
	}

	private void setupMaxJobs() throws GeneralEconomyException {
		if (!configDao.hasMaxJobs()) {
			setMaxJobs(2);
		} else {
			maxJobs = configDao.loadMaxJobs();
		}
	}

	private void setupMaxHomes() throws GeneralEconomyException {
		if (!configDao.hasMaxHomes()) {
			setMaxHomes(3);
		} else {
			maxHomes = configDao.loadMaxHomes();
		}
	}

	@Override
	public void setExtendedInteraction(boolean value) {
		extendedInteraction = value;
		configDao.saveExtendedInteraction(extendedInteraction);
	}

	@Override
	public boolean isExtendedInteraction() {
		return extendedInteraction;
	}

	@Override
	public void setWildernessInteraction(boolean value) {
		wildernessInteraction = value;
		configDao.saveWildernessInteraction(wildernessInteraction);
		if (value) {
			for (EconomyPlayer player : ecoPlayerManager.getAllEconomyPlayers()) {
				player.addWildernessPermission();
			}
		} else {
			for (EconomyPlayer player : ecoPlayerManager.getAllEconomyPlayers()) {
				player.denyWildernessPermission();
			}
		}
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public boolean isWildernessInteraction() {
		return wildernessInteraction;
	}

	@Override
	public void setMaxRentedDays(int days) throws GeneralEconomyException {
		if (days < 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					days);
		} else {
			maxRentedDays = days;
			configDao.saveMaxRentedDays(maxRentedDays);
		}
	}

	@Override
	public int getMaxRentedDays() {
		return maxRentedDays;
	}

	@Override
	public void setLocale(String language, String country) throws GeneralEconomyException {
		if (!isLanguageSupported(language)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					language);
		} else if (!isCountryMatching(language, country)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					country);
		} else {
			locale = new Locale(language, country);
			configDao.saveLanguage(language);
			configDao.saveCountry(country);
		}
	}

	@Override
	public void setMaxPlayershops(int value) throws GeneralEconomyException {
		if (value < 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					value);
		} else {
			maxPlayershops = value;
			configDao.saveMaxPlayershops(maxPlayershops);
		}
	}

	@Override
	public int getMaxPlayershops() {
		return maxPlayershops;
	}

	@Override
	public void setMaxHomes(int value) throws GeneralEconomyException {
		if (value < 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					value);
		} else {
			maxHomes = value;
			configDao.saveMaxHomes(maxHomes);
		}
	}

	@Override
	public int getMaxHomes() {
		return maxHomes;
	}

	@Override
	public void setMaxJobs(int value) throws GeneralEconomyException {
		if (value < 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					value);
		} else {
			maxJobs = value;
			configDao.saveMaxJobs(maxJobs);
		}
	}

	@Override
	public int getMaxJobs() {
		return maxJobs;
	}

	@Override
	public void setMaxJoinedTowns(int value) throws GeneralEconomyException {
		if (value < 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					value);
		} else {
			maxJoinedTowns = value;
			configDao.saveMaxJoinedTowns(maxJoinedTowns);
		}
	}

	@Override
	public int getMaxJoinedTowns() {
		return maxJoinedTowns;
	}

	@Override
	public void setHomeSystem(boolean value) {
		homesSystem = value;
		configDao.saveHomesFeature(homesSystem);
	}

	@Override
	public boolean isHomeSystem() {
		return homesSystem;
	}

	@Override
	public String getCurrencyPl() {
		return currencyPl;
	}

	@Override
	public void setCurrencyPl(String value) {
		currencyPl = value;
		configDao.saveCurrencyPl(currencyPl);
	}

	@Override
	public String getCurrencySg() {
		return currencySg;
	}

	@Override
	public void setCurrencySg(String value) {
		currencySg = value;
		configDao.saveCurrencySg(currencySg);
	}

	@Override
	public String getCurrencyText(double value) {
		if (value == 1) {
			return getCurrencySg();
		} else {
			return getCurrencyPl();
		}
	}

	private boolean isLanguageSupported(String lang) {
		switch (lang) {
		case "cs":
		case "de":
		case "en":
		case "fr":
		case "zh":
		case "ru":
		case "es":
		case "lt":
		case "it":
		case "pl":
			return true;
		default:
			return false;
		}
	}

	private boolean isCountryMatching(String lang, String country) {
		switch (lang) {
		case "cs":
			if ("CZ".equals(country)) {
				return true;
			}
		case "en":
			if ("US".equals(country)) {
				return true;
			}
		case "zh":
			if ("CN".equals(country)) {
				return true;
			}
		default:
			if (lang.toUpperCase().equals(country)) {
				return true;
			}
		}
		return false;
	}
}
