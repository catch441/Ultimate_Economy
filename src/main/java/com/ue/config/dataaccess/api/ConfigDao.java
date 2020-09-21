package com.ue.config.dataaccess.api;

import java.util.List;

public interface ConfigDao {

	/**
	 * Loads the max rented days value.
	 * 
	 * @return max rented days
	 */
	public int loadMaxRentedDays();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasMaxRentedDays();

	/**
	 * Saves the max rented days value.
	 * 
	 * @param maxRentedDays
	 */
	public void saveMaxRentedDays(Integer maxRentedDays);

	/**
	 * Loads the extended interaction value.
	 * 
	 * @return true, if active, false if not
	 */
	public boolean loadExtendedInteraction();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasExtendedInteraction();

	/**
	 * Saves the extended interaction value.
	 * 
	 * @param extendedInteraction
	 */
	public void saveExtendedInteraction(Boolean extendedInteraction);

	/**
	 * Loads the wildernes interaction value.
	 * 
	 * @return true, if active, false if not
	 */
	public boolean loadWildernessInteraction();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasWildernessInteraction();

	/**
	 * Saves the wilderness interaction value.
	 * 
	 * @param wildernessInteraction
	 */
	public void saveWildernessInteraction(Boolean wildernessInteraction);

	/**
	 * Loads the currency pl string.
	 * 
	 * @return currency string
	 */
	public String loadCurrencyPl();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasCurrencyPl();

	/**
	 * Saves the currency pl string.
	 * 
	 * @param currencyPl
	 */
	public void saveCurrencyPl(String currencyPl);

	/**
	 * Loads the currency sg string.
	 * 
	 * @return currency string
	 */
	public String loadCurrencySg();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasCurrencySg();

	/**
	 * Saves the currency sg string.
	 * 
	 * @param currencySg
	 */
	public void saveCurrencySg(String currencySg);

	/**
	 * Loads the homes feature value.
	 * 
	 * @return true, if the ue home system is enabled
	 */
	public boolean loadHomesFeature();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasHomesFeature();

	/**
	 * Saves homes feature value.
	 * 
	 * @param homesFeature
	 */
	public void saveHomesFeature(Boolean homesFeature);

	/**
	 * Loads the max playershops value.
	 * 
	 * @return max playershops
	 */
	public int loadMaxPlayershops();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasMaxPlayershops();

	/**
	 * Saves the max playershops value.
	 * 
	 * @param maxPlayershops
	 */
	public void saveMaxPlayershops(Integer maxPlayershops);

	/**
	 * Load the max joined towns value.
	 * 
	 * @return max joined towns
	 */
	public int loadMaxJoinedTowns();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasMaxJoinedTowns();

	/**
	 * Saves max joined towns value.
	 * 
	 * @param maxJoinedTowns
	 */
	public void saveMaxJoinedTowns(Integer maxJoinedTowns);

	/**
	 * Load the max jobs value.
	 * 
	 * @return max joined towns
	 */
	public int loadMaxJobs();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasMaxJobs();

	/**
	 * Saves the max jobs value.
	 * 
	 * @param maxJobs
	 */
	public void saveMaxJobs(Integer maxJobs);

	/**
	 * Load the max homes value.
	 * 
	 * @return max homes
	 */
	public int loadMaxHomes();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasMaxHomes();

	/**
	 * Saves the max homes value.
	 * 
	 * @param maxHomes
	 */
	public void saveMaxHomes(Integer maxHomes);

	/**
	 * Load the country string.
	 * 
	 * @return country
	 */
	public String loadCountry();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasCountry();

	/**
	 * Saves the country string.
	 * 
	 * @param country
	 */
	public void saveCountry(String country);

	/**
	 * Load language string.
	 * 
	 * @return language
	 */
	public String loadLanguage();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @return boolean
	 */
	public boolean hasLanguage();

	/**
	 * Saves the language string.
	 * 
	 * @param language
	 */
	public void saveLanguage(String language);

	/**
	 * Saves the jobcenter list.
	 * 
	 * @param jobcenters
	 */
	public void saveJobcenterList(List<String> jobcenters);

	/**
	 * Loads the jobcenter list.
	 * 
	 * @return jobcenter names
	 */
	public List<String> loadJobcenterList();
	
	/**
	 * Saves the job list.
	 * 
	 * @param jobs
	 */
	public void saveJobList(List<String> jobs);
	
	/**
	 * Loads the job list.
	 * 
	 * @return job names
	 */
	public List<String> loadJobList();
	
	/**
	 * Loads the playershop ids.
	 * @return id list
	 */
	public List<String> loadPlayershopIds();
	
	/**
	 * Saves the playershop ids.
	 * @param ids
	 */
	public void savePlayershopIds(List<String> ids);
	
	/**
	 * Loads the adminshop ids.
	 * @return id list
	 */
	public List<String> loadAdminshopIds();
	
	/**
	 * Saves the adminshop ids.
	 * @param ids
	 */
	public void saveAdminshopIds(List<String> ids);

	/**
	 * Remove deprecated stuff.
	 * 
	 * @deprecated
	 * @since 1.2.6
	 */
	@Deprecated
	public void removeDeprecatedTownNames();

	/**
	 * Remove deprecated stuff.
	 * 
	 * @deprecated
	 * @since 1.2.6
	 */
	@Deprecated
	public void removeDeprecatedPlayerShopNames();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @deprecated
	 * @since 1.2.6
	 * @return boolean
	 */
	@Deprecated
	public boolean hasPlayerShopNames();

	/**
	 * Loads the playershop names.
	 * 
	 * @deprecated
	 * @since 1.2.6
	 * @return playershop names
	 */
	@Deprecated
	public List<String> loadPlayerShopNames();

	/**
	 * Loads the adminshop names.
	 * 
	 * @deprecated
	 * @since 1.2.6
	 * @return adminshop names
	 */
	@Deprecated
	public List<String> loadAdminShopNames();

	/**
	 * Returns true, if the value exists.
	 * 
	 * @deprecated
	 * @since 1.2.6
	 * @return boolean
	 */
	@Deprecated
	public boolean hasAdminShopNames();

	/**
	 * Remove deprecated stuff.
	 * 
	 * @deprecated
	 * @since 1.2.6
	 */
	@Deprecated
	public void removeDeprecatedAdminshopNames();
}
