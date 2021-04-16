package org.ue.config.logic.impl;

import java.util.Locale;

import javax.inject.Inject;

import org.ue.common.logic.api.GeneralValidationHandler;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.config.logic.api.ConfigManager;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;

public class ConfigManagerImpl implements ConfigManager {

	private final ConfigDao configDao;
	private final MessageWrapper messageWrapper;
	private final GeneralValidationHandler generalValidator;

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
	private boolean allowQuickShop;
	private Locale locale;
	private double startAmount;

	@Inject
	public ConfigManagerImpl(ConfigDao configDao, MessageWrapper messageWrapper,
			GeneralValidationHandler generalValidator) {
		this.configDao = configDao;
		this.messageWrapper = messageWrapper;
		this.generalValidator = generalValidator;
	}

	@SuppressWarnings("deprecation")
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
			setupStartAmount();
			setupAllowQuickshop();
			configDao.removeDeprecatedTownNames();
		} catch (GeneralEconomyException e) {
		}
	}

	private void setupAllowQuickshop() {
		if (!configDao.hasAllowQuickshop()) {
			setAllowQuickshop(false);
		} else {
			allowQuickShop = configDao.loadAllowQuickshop();
		}
	}

	private void setupStartAmount() throws GeneralEconomyException {
		if (!configDao.hasStartAmount()) {
			setStartAmount(0.0);
		} else {
			startAmount = configDao.loadStartAmount();
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
	public void setAllowQuickshop(boolean value) {
		allowQuickShop = value;
		configDao.saveAllowQuickshop(allowQuickShop);
	}

	@Override
	public boolean isAllowQuickshop() {
		return allowQuickShop;
	}

	@Override
	public void setStartAmount(double amount) throws GeneralEconomyException {
		generalValidator.checkForPositiveValue(amount);
		startAmount = amount;
		configDao.saveStartAmount(amount);
	}

	@Override
	public double getStartAmount() {
		return startAmount;
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
		generalValidator.checkForValueGreaterZero(days);
		maxRentedDays = days;
		configDao.saveMaxRentedDays(maxRentedDays);
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
		generalValidator.checkForPositiveValue(value);
		maxPlayershops = value;
		configDao.saveMaxPlayershops(maxPlayershops);
	}

	@Override
	public int getMaxPlayershops() {
		return maxPlayershops;
	}

	@Override
	public void setMaxHomes(int value) throws GeneralEconomyException {
		generalValidator.checkForPositiveValue(value);
		maxHomes = value;
		configDao.saveMaxHomes(maxHomes);
	}

	@Override
	public int getMaxHomes() {
		return maxHomes;
	}

	@Override
	public void setMaxJobs(int value) throws GeneralEconomyException {
		generalValidator.checkForPositiveValue(value);
		maxJobs = value;
		configDao.saveMaxJobs(maxJobs);
	}

	@Override
	public int getMaxJobs() {
		return maxJobs;
	}

	@Override
	public void setMaxJoinedTowns(int value) throws GeneralEconomyException {
		generalValidator.checkForPositiveValue(value);
		maxJoinedTowns = value;
		configDao.saveMaxJoinedTowns(maxJoinedTowns);
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
