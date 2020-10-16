package com.ue.config.logic.api;

import java.util.Locale;

import com.ue.general.impl.GeneralEconomyException;

public interface ConfigManager {

	/**
	 * Setup all configuration parameters of UltimateEconomy.
	 */
	public void setupConfig();

	/**
	 * This method sets the extended town interaction configuration.
	 * 
	 * @param value
	 */
	public void setExtendedInteraction(boolean value);

	/**
	 * Returns the extended town interaction configuration.
	 * 
	 * @return boolean
	 */
	public boolean isExtendedInteraction();

	/**
	 * This method enables/disables the wilderness interaction.
	 * 
	 * @param value
	 */
	public void setWildernessInteraction(boolean value);

	/**
	 * Returns true, if the wilderness is enabled.
	 * 
	 * @return boolean
	 */
	public boolean isWildernessInteraction();

	/**
	 * This method sets the maxRentedDays value.
	 * 
	 * @param days
	 * @throws GeneralEconomyException
	 */
	public void setMaxRentedDays(int days) throws GeneralEconomyException;

	/**
	 * Returns the max rented days.
	 * 
	 * @return int
	 */
	public int getMaxRentedDays();

	/**
	 * This method sets the maxPlayershops per player value.
	 * 
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void setMaxPlayershops(int value) throws GeneralEconomyException;

	/**
	 * Returns the maxPlayershops per player value.
	 * 
	 * @return int
	 */
	public int getMaxPlayershops();

	/**
	 * This method sets the maxHomes value.
	 * 
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void setMaxHomes(int value) throws GeneralEconomyException;

	/**
	 * Returns the max homes configuration.
	 * 
	 * @return int
	 */
	public int getMaxHomes();

	/**
	 * This method sets the maxJobs value.
	 * 
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void setMaxJobs(int value) throws GeneralEconomyException;

	/**
	 * Returns the max jobs configuration.
	 * 
	 * @return int
	 */
	public int getMaxJobs();

	/**
	 * This method sets the maxJoinedTowns value.
	 * 
	 * @param value
	 * @throws GeneralEconomyException
	 */
	public void setMaxJoinedTowns(int value) throws GeneralEconomyException;

	/**
	 * Returns the max joined towns configuration.
	 * 
	 * @return int
	 */
	public int getMaxJoinedTowns();

	/**
	 * Enables/disables the home system.
	 * 
	 * @param value
	 */
	public void setHomeSystem(boolean value);

	/**
	 * Returns true, if the home system is enabled.
	 * 
	 * @return boolean
	 */
	public boolean isHomeSystem();

	/**
	 * Returns the currency text plural.
	 * 
	 * @return String
	 */
	public String getCurrencyPl();

	/**
	 * Set the curreny text plural.
	 * 
	 * @param value
	 */
	public void setCurrencyPl(String value);

	/**
	 * Returns the currency text singular.
	 * 
	 * @return String
	 */
	public String getCurrencySg();


	/**
	 * Set the currency text singular.
	 * 
	 * @param value
	 */
	public void setCurrencySg(String value);

	/**
	 * Returns the singular for amount = 1 and plural for != 1.
	 * 
	 * @param value money amount
	 * @return currency text
	 */
	public String getCurrencyText(double value);

	/**
	 * Returns the actual locale.
	 * @return locale
	 */
	public Locale getLocale();

	/**
	 * Sets the current locale.
	 * @param language
	 * @param country
	 * @throws GeneralEconomyException 
	 */
	public void setLocale(String language, String country) throws GeneralEconomyException;
}
