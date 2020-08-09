package com.ue.config.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigDaoImplTest {

	private File file;
	private ConfigDaoImpl dao;

	@BeforeEach
	public void setup() {
		file = new File("src/BankAccounts.yml");
		try {
			file.createNewFile();
			dao = new ConfigDaoImpl(file);
		} catch (IOException e) {
			fail();
		}
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		file.delete();
	}

	@Test
	public void saveMaxRentedDaysTest() {
		dao.saveMaxRentedDays(4);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(4, config.getInt("MaxRentedDays"));
	}

	@Test
	public void loadMaxRentedDaysTest() {
		dao.saveMaxRentedDays(6);
		assertEquals(6, dao.loadMaxRentedDays());
	}

	@Test
	public void hasMaxRentedDaysTest() {
		dao.saveMaxRentedDays(6);
		assertTrue(dao.hasMaxRentedDays());
		dao.saveMaxRentedDays(null);
		assertFalse(dao.hasMaxRentedDays());
	}

	@Test
	public void saveExtendedInteractionTest() {
		dao.saveExtendedInteraction(true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(config.getBoolean("ExtendedInteraction"));
	}

	@Test
	public void loadExtendedInteractionTest() {
		dao.saveExtendedInteraction(false);
		assertFalse(dao.loadExtendedInteraction());
	}

	@Test
	public void hasExtendedInteractionTest() {
		dao.saveExtendedInteraction(false);
		assertTrue(dao.hasExtendedInteraction());
		dao.saveExtendedInteraction(null);
		assertFalse(dao.hasExtendedInteraction());
	}

	@Test
	public void saveWildernessInteractionTest() {
		dao.saveWildernessInteraction(true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(config.getBoolean("WildernessInteraction"));
	}

	@Test
	public void loadWildernessInteractionTest() {
		dao.saveWildernessInteraction(false);
		assertFalse(dao.loadWildernessInteraction());
	}

	@Test
	public void hasWildernessInteractionTest() {
		dao.saveWildernessInteraction(false);
		assertTrue(dao.hasWildernessInteraction());
		dao.saveWildernessInteraction(null);
		assertFalse(dao.hasWildernessInteraction());
	}

	@Test
	public void saveCurrencyPlTest() {
		dao.saveCurrencyPl("kths");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("kths", config.getString("currencyPl"));
	}

	@Test
	public void loadCurrencyPlTest() {
		dao.saveCurrencyPl("coins");
		assertEquals("coins", dao.loadCurrencyPl());
	}

	@Test
	public void hasCurrencyPlTest() {
		dao.saveCurrencyPl("kths");
		assertTrue(dao.hasCurrencyPl());
		dao.saveCurrencyPl(null);
		assertFalse(dao.hasCurrencyPl());
	}

	@Test
	public void saveCurrencySgTest() {
		dao.saveCurrencySg("kth");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("kth", config.getString("currencySg"));
	}

	@Test
	public void loadCurrencySgTest() {
		dao.saveCurrencySg("coin");
		assertEquals("coin", dao.loadCurrencySg());
	}

	@Test
	public void hasCurrencySgTest() {
		dao.saveCurrencySg("kth");
		assertTrue(dao.hasCurrencySg());
		dao.saveCurrencySg(null);
		assertFalse(dao.hasCurrencySg());
	}

	@Test
	public void saveHomesFeatureTest() {
		dao.saveHomesFeature(true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(config.getBoolean("homes"));
	}

	@Test
	public void loadHomesFeatureTest() {
		dao.saveHomesFeature(false);
		assertFalse(dao.loadHomesFeature());
	}

	@Test
	public void hasHomesFeatureTest() {
		dao.saveHomesFeature(false);
		assertTrue(dao.hasHomesFeature());
		dao.saveHomesFeature(null);
		assertFalse(dao.hasHomesFeature());
	}

	@Test
	public void saveMaxPlayershops() {
		dao.saveMaxPlayershops(4);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(4, config.getInt("MaxPlayershops"));
	}

	@Test
	public void loadMaxPlayershopsTest() {
		dao.saveMaxPlayershops(6);
		assertEquals(6, dao.loadMaxPlayershops());
	}

	@Test
	public void hasMaxPlayershopsTest() {
		dao.saveMaxPlayershops(6);
		assertTrue(dao.hasMaxPlayershops());
		dao.saveMaxPlayershops(null);
		assertFalse(dao.hasMaxPlayershops());
	}

	@Test
	public void saveMaxJoinedTownsTest() {
		dao.saveMaxJoinedTowns(4);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(4, config.getInt("MaxJoinedTowns"));
	}

	@Test
	public void loadMaxJoinedTownsTest() {
		dao.saveMaxJoinedTowns(6);
		assertEquals(6, dao.loadMaxJoinedTowns());
	}

	@Test
	public void hasMaxJoinedTownsTest() {
		dao.saveMaxJoinedTowns(6);
		assertTrue(dao.hasMaxJoinedTowns());
		dao.saveMaxJoinedTowns(null);
		assertFalse(dao.hasMaxJoinedTowns());
	}

	@Test
	public void saveMaxJobsTest() {
		dao.saveMaxJobs(4);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(4, config.getInt("MaxJobs"));
	}

	@Test
	public void loadMaxJobsTest() {
		dao.saveMaxJobs(6);
		assertEquals(6, dao.loadMaxJobs());
	}

	@Test
	public void hasMaxJobsTest() {
		dao.saveMaxJobs(6);
		assertTrue(dao.hasMaxJobs());
		dao.saveMaxJobs(null);
		assertFalse(dao.hasMaxJobs());
	}

	@Test
	public void saveMaxHomesTest() {
		dao.saveMaxHomes(4);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(4, config.getInt("MaxHomes"));
	}

	@Test
	public void loadMaxHomesTest() {
		dao.saveMaxHomes(6);
		assertEquals(6, dao.loadMaxHomes());
	}

	@Test
	public void hasMaxHomesTest() {
		dao.saveMaxHomes(6);
		assertTrue(dao.hasMaxHomes());
		dao.saveMaxHomes(null);
		assertFalse(dao.hasMaxHomes());
	}

	@Test
	public void saveCountryTest() {
		dao.saveCountry("DE");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("DE", config.getString("localeCountry"));
	}

	@Test
	public void loadCountryTest() {
		dao.saveCountry("DE");
		assertEquals("DE", dao.loadCountry());
	}

	@Test
	public void hasCountryTest() {
		dao.saveCountry("DE");
		assertTrue(dao.hasCountry());
		dao.saveCountry(null);
		assertFalse(dao.hasCountry());
	}

	@Test
	public void saveLanguageTest() {
		dao.saveLanguage("de");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("de", config.getString("localeLanguage"));
	}

	@Test
	public void loadLanguageTest() {
		dao.saveLanguage("de");
		assertEquals("de", dao.loadLanguage());
	}

	@Test
	public void hasLanguageTest() {
		dao.saveLanguage("de");
		assertTrue(dao.hasLanguage());
		dao.saveLanguage(null);
		assertFalse(dao.hasLanguage());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void removeDeprecatedTest() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("TownNames", Arrays.asList("town1", "town2"));
		assertDoesNotThrow(() -> config.save(file));
		dao = new ConfigDaoImpl(file);
		dao.removeDeprecated();
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertFalse(config2.contains("TownNames"));
	}
}
