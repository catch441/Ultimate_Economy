package com.ue.config.api;

import org.bukkit.configuration.file.FileConfiguration;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.Ultimate_Economy;

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
	
	public static void setupConfig() {
		FileConfiguration fileConfig = Ultimate_Economy.getInstance.getConfig();
		try {
			if (!fileConfig.isSet("MaxHomes")) {
				setMaxHomes(3);
			} else {
				maxHomes = fileConfig.getInt("MaxHomes");
			}
			if (!fileConfig.isSet("MaxJobs")) {
				setMaxJobs(2);
			} else {
				maxJobs = fileConfig.getInt("MaxJobs");
			}
			if (!fileConfig.isSet("MaxJoinedTowns")) {
				setMaxJoinedTowns(1);
			} else {
				maxJoinedTowns = fileConfig.getInt("MaxJoinedTowns");
			}
			if (!fileConfig.isSet("MaxPlayershops")) {
				setMaxPlayershops(3);
			} else {
				maxPlayershops = fileConfig.getInt("MaxPlayershops");
			}
			if (!fileConfig.isSet("homes")) {
				setHomeSystem(true);
			} else {
				homesSystem = fileConfig.getBoolean("homes");
			}
			if (!fileConfig.isSet("MaxRentedDays")) {
				fileConfig.set("MaxRentedDays", 14);
				maxRentedDays = 14;
			} else {
				maxRentedDays = fileConfig.getInt("MaxRentedDays");
			}
			if (!fileConfig.isSet("ExtendedInteraction")) {
				setExtendedInteraction(false);
			} else {
				extendedInteraction = fileConfig.getBoolean("ExtendedInteraction");
			}
			if (!fileConfig.isSet("WildernessInteraction")) {
				setWildernessInteraction(false);
			} else {
				wildernessInteraction = fileConfig.getBoolean("WildernessInteraction");
			}
			if(!fileConfig.isSet("currencyPl")) {
				setCurrencyPl("$");
			} else {
				setCurrencyPl(fileConfig.getString("currencyPl"));
			}
			if(!fileConfig.isSet("currencySg")) {
				setCurrencySg("$");
			} else {
				setCurrencySg(fileConfig.getString("currencySg"));
			}
		} catch (PlayerException e) {
		}
	}
	
	/**
	 * This method sets the extended town interaction configuration.
	 * 
	 * @param value
	 */
	public static void setExtendedInteraction(boolean value) {
		Ultimate_Economy.getInstance.getConfig().set("ExtendedInteraction", value);
		extendedInteraction = value;
		Ultimate_Economy.getInstance.saveConfig();
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
		Ultimate_Economy.getInstance.getConfig().set("WildernessInteraction", value);
		wildernessInteraction = value;
		if(value) {
			for(EconomyPlayer player:EconomyPlayerController.getAllEconomyPlayers()) {
				player.addWildernessPermission();
			}
		} else {
			for(EconomyPlayer player:EconomyPlayerController.getAllEconomyPlayers()) {
				player.denyWildernessPermission();
			}
		}
		Ultimate_Economy.getInstance.saveConfig();
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
	 * @throws PlayerException
	 */
	public static void setMaxRentedDays(int days) throws PlayerException {
		if (days < 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, days);
		} else {
			Ultimate_Economy.getInstance.getConfig().set("MaxRentedDays", days);
			maxRentedDays = days;
		}
		Ultimate_Economy.getInstance.saveConfig();
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
	 * @throws PlayerException
	 */
	public static void setMaxPlayershops(int value) throws PlayerException {
		if (value < 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, value);
		} else {
			Ultimate_Economy.getInstance.getConfig().set("MaxPlayershops", value);
			maxPlayershops = value;
		}
		Ultimate_Economy.getInstance.saveConfig();
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
	 * @throws PlayerException
	 */
	public static void setMaxHomes(int value) throws PlayerException {
		if (value < 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, value);
		} else {
			Ultimate_Economy.getInstance.getConfig().set("MaxHomes", value);
			maxHomes = value;
		}
		Ultimate_Economy.getInstance.saveConfig();
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
	 * @throws PlayerException
	 */
	public static void setMaxJobs(int value) throws PlayerException {
		if (value < 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, value);
		} else {
			Ultimate_Economy.getInstance.getConfig().set("MaxJobs", value);
			maxJobs = value;
		}
		Ultimate_Economy.getInstance.saveConfig();
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
	 * @throws PlayerException
	 */
	public static void setMaxJoinedTowns(int value) throws PlayerException {
		if (value < 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, value);
		} else {
			Ultimate_Economy.getInstance.getConfig().set("MaxJoinedTowns", value);
			maxJoinedTowns = value;
		}
		Ultimate_Economy.getInstance.saveConfig();
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
		Ultimate_Economy.getInstance.getConfig().set("homes", Boolean.valueOf(value));
		homesSystem = value;
		Ultimate_Economy.getInstance.saveConfig();
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
	 * @return String
	 */
	public static String getCurrencyPl() {
		return currencyPl;
	}

	/**
	 * Set the curreny text plural.
	 * @param value
	 */
	public static void setCurrencyPl(String value) {
		Ultimate_Economy.getInstance.getConfig().set("currencyPl", value);
		currencyPl = value;
		Ultimate_Economy.getInstance.saveConfig();
	}

	/**
	 * Returns the currency text singular.
	 * @return String
	 */
	public static String getCurrencySg() {
		return currencySg;
	}

	/**
	 * Set the currency text singular.
	 * @param value
	 */
	public static void setCurrencySg(String value) {
		Ultimate_Economy.getInstance.getConfig().set("currencySg", value);
		currencySg = value;
		Ultimate_Economy.getInstance.saveConfig();
	}
	
	/**
	 * Returns the singular for amount = 1 and plural for != 1.
	 * @param value money amount
	 * @return
	 */
	public static String getCurrencyText(double value) {		
		if(value == 1) {
			return ConfigController.getCurrencySg();
		} else {
			return ConfigController.getCurrencyPl();
		}
	}
}
