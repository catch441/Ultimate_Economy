package com.ue.config.api;

import org.bukkit.configuration.file.FileConfiguration;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

public class ConfigController {

    private static int maxHomes;
    private static int maxJobs;
    private static int maxJoinedTowns;
    private static int maxPlayershops;
    private static boolean homesSystem;
    private static String currencyPl;
    private static String currencySg;
    private static int maxRentedDays;
    private static boolean extendedInteraction;
    private static boolean wildernessInteraction;

    /**
     * Setup all configuration parameters of UltimateEconomy.
     */
    public static void setupConfig() {
	FileConfiguration fileConfig = UltimateEconomy.getInstance.getConfig();
	try {
	    setupMaxHomes(fileConfig);
	    setupMaxJobs(fileConfig);
	    setupMaxJoinedTowns(fileConfig);
	    setupMaxPlayershops(fileConfig);
	    setupHomesFeature(fileConfig);
	    setupMaxRentedDays(fileConfig);
	    setupExtendedInteraction(fileConfig);
	    setupWildernessInteraction(fileConfig);
	    setupCurrencyPl(fileConfig);
	    setupCurrencySg(fileConfig);
	} catch (GeneralEconomyException e) {
	}
    }

    private static void setupMaxRentedDays(FileConfiguration fileConfig) throws GeneralEconomyException {
	if (!fileConfig.isSet("MaxRentedDays")) {
	setMaxRentedDays(14);
	} else {
	maxRentedDays = fileConfig.getInt("MaxRentedDays");
	}
    }

    private static void setupExtendedInteraction(FileConfiguration fileConfig) {
	if (!fileConfig.isSet("ExtendedInteraction")) {
	setExtendedInteraction(false);
	} else {
	extendedInteraction = fileConfig.getBoolean("ExtendedInteraction");
	}
    }

    private static void setupWildernessInteraction(FileConfiguration fileConfig) {
	if (!fileConfig.isSet("WildernessInteraction")) {
	setWildernessInteraction(false);
	} else {
	wildernessInteraction = fileConfig.getBoolean("WildernessInteraction");
	}
    }

    private static void setupCurrencyPl(FileConfiguration fileConfig) {
	if (!fileConfig.isSet("currencyPl")) {
	setCurrencyPl("$");
	} else {
	setCurrencyPl(fileConfig.getString("currencyPl"));
	}
    }

    private static void setupCurrencySg(FileConfiguration fileConfig) {
	if (!fileConfig.isSet("currencySg")) {
	setCurrencySg("$");
	} else {
	setCurrencySg(fileConfig.getString("currencySg"));
	}
    }

    private static void setupHomesFeature(FileConfiguration fileConfig) {
	if (!fileConfig.isSet("homes")) {
	setHomeSystem(true);
	} else {
	homesSystem = fileConfig.getBoolean("homes");
	}
    }

    private static void setupMaxPlayershops(FileConfiguration fileConfig) throws GeneralEconomyException {
	if (!fileConfig.isSet("MaxPlayershops")) {
	setMaxPlayershops(3);
	} else {
	maxPlayershops = fileConfig.getInt("MaxPlayershops");
	}
    }

    private static void setupMaxJoinedTowns(FileConfiguration fileConfig) throws GeneralEconomyException {
	if (!fileConfig.isSet("MaxJoinedTowns")) {
	setMaxJoinedTowns(1);
	} else {
	maxJoinedTowns = fileConfig.getInt("MaxJoinedTowns");
	}
    }

    private static void setupMaxJobs(FileConfiguration fileConfig) throws GeneralEconomyException {
	if (!fileConfig.isSet("MaxJobs")) {
	setMaxJobs(2);
	} else {
	maxJobs = fileConfig.getInt("MaxJobs");
	}
    }

    private static void setupMaxHomes(FileConfiguration fileConfig) throws GeneralEconomyException {
	if (!fileConfig.isSet("MaxHomes")) {
	setMaxHomes(3);
	} else {
	maxHomes = fileConfig.getInt("MaxHomes");
	}
    }

    /**
     * This method sets the extended town interaction configuration.
     * 
     * @param value
     */
    public static void setExtendedInteraction(boolean value) {
	UltimateEconomy.getInstance.getConfig().set("ExtendedInteraction", value);
	extendedInteraction = value;
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * Returns the extended town interaction configuration.
     * 
     * @return boolean
     */
    public static boolean isExtendedInteraction() {
	return extendedInteraction;
    }

    /**
     * This method enables/disables the wilderness interaction.
     * 
     * @param value
     */
    public static void setWildernessInteraction(boolean value) {
	UltimateEconomy.getInstance.getConfig().set("WildernessInteraction", value);
	wildernessInteraction = value;
	if (value) {
	    for (EconomyPlayer player : EconomyPlayerController.getAllEconomyPlayers()) {
		player.addWildernessPermission();
	    }
	} else {
	    for (EconomyPlayer player : EconomyPlayerController.getAllEconomyPlayers()) {
		player.denyWildernessPermission();
	    }
	}
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * Returns true, if the wilderness is enabled.
     * 
     * @return boolean
     */
    public static boolean isWildernessInteraction() {
	return wildernessInteraction;
    }

    /**
     * This method sets the maxRentedDays value.
     * 
     * @param days
     * @throws GeneralEconomyException
     */
    public static void setMaxRentedDays(int days) throws GeneralEconomyException {
	if (days < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, days);
	} else {
	    UltimateEconomy.getInstance.getConfig().set("MaxRentedDays", days);
	    maxRentedDays = days;
	}
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * Returns the max rented days.
     * 
     * @return int
     */
    public static int getMaxRentedDays() {
	return maxRentedDays;
    }

    /**
     * This method sets the maxPlayershops per player value.
     * 
     * @param value
     * @throws GeneralEconomyException
     */
    public static void setMaxPlayershops(int value) throws GeneralEconomyException {
	if (value < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
	} else {
	    UltimateEconomy.getInstance.getConfig().set("MaxPlayershops", value);
	    maxPlayershops = value;
	}
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * Returns the maxPlayershops per player value.
     * 
     * @return int
     */
    public static int getMaxPlayershops() {
	return maxPlayershops;
    }

    /**
     * This method sets the maxHomes value.
     * 
     * @param value
     * @throws GeneralEconomyException
     */
    public static void setMaxHomes(int value) throws GeneralEconomyException {
	if (value < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
	} else {
	    UltimateEconomy.getInstance.getConfig().set("MaxHomes", value);
	    maxHomes = value;
	}
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * Returns the max homes configuration.
     * 
     * @return int
     */
    public static int getMaxHomes() {
	return maxHomes;
    }

    /**
     * This method sets the maxJobs value.
     * 
     * @param value
     * @throws GeneralEconomyException
     */
    public static void setMaxJobs(int value) throws GeneralEconomyException {
	if (value < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
	} else {
	    UltimateEconomy.getInstance.getConfig().set("MaxJobs", value);
	    maxJobs = value;
	}
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * Returns the max jobs configuration.
     * 
     * @return int
     */
    public static int getMaxJobs() {
	return maxJobs;
    }

    /**
     * This method sets the maxJoinedTowns value.
     * 
     * @param value
     * @throws GeneralEconomyException
     */
    public static void setMaxJoinedTowns(int value) throws GeneralEconomyException {
	if (value < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
	} else {
	    UltimateEconomy.getInstance.getConfig().set("MaxJoinedTowns", value);
	    maxJoinedTowns = value;
	}
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * Returns the max joined towns configuration.
     * 
     * @return int
     */
    public static int getMaxJoinedTowns() {
	return maxJoinedTowns;
    }

    /**
     * Enables/disables the home system.
     * 
     * @param value
     */
    public static void setHomeSystem(boolean value) {
	UltimateEconomy.getInstance.getConfig().set("homes", Boolean.valueOf(value));
	homesSystem = value;
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * Returns true, if the home system is enabled.
     * 
     * @return boolean
     */
    public static boolean isHomeSystem() {
	return homesSystem;
    }

    /**
     * Returns the currency text plural.
     * 
     * @return String
     */
    public static String getCurrencyPl() {
	return currencyPl;
    }

    /**
     * Set the curreny text plural.
     * 
     * @param value
     */
    public static void setCurrencyPl(String value) {
	UltimateEconomy.getInstance.getConfig().set("currencyPl", value);
	currencyPl = value;
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * Returns the currency text singular.
     * 
     * @return String
     */
    public static String getCurrencySg() {
	return currencySg;
    }

    /**
     * Set the currency text singular.
     * 
     * @param value
     */
    public static void setCurrencySg(String value) {
	UltimateEconomy.getInstance.getConfig().set("currencySg", value);
	currencySg = value;
	UltimateEconomy.getInstance.saveConfig();
    }

    /**
     * Returns the singular for amount = 1 and plural for != 1.
     * 
     * @param value
     *            money amount
     * @return currency text
     */
    public static String getCurrencyText(double value) {
	if (value == 1) {
	    return ConfigController.getCurrencySg();
	} else {
	    return ConfigController.getCurrencyPl();
	}
    }
}
