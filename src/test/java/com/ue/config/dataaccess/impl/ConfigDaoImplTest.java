package com.ue.config.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.BukkitService;
import com.ue.config.dataaccess.api.ConfigDao;

@ExtendWith(MockitoExtension.class)
public class ConfigDaoImplTest {

	@Mock
	BukkitService bukkitService;

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		new File("src/config.yml").delete();
	}

	@Test
	public void saveMaxRentedDaysTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveMaxRentedDays(4);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(4, config.getInt("MaxRentedDays"));
	}

	@Test
	public void loadMaxRentedDaysTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveMaxRentedDays(6);
		assertEquals(6, dao.loadMaxRentedDays());
	}

	@Test
	public void hasMaxRentedDaysTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveMaxRentedDays(6);
		assertTrue(dao.hasMaxRentedDays());
		dao.saveMaxRentedDays(null);
		assertFalse(dao.hasMaxRentedDays());
	}

	@Test
	public void saveExtendedInteractionTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveExtendedInteraction(true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(config.getBoolean("ExtendedInteraction"));
	}

	@Test
	public void loadExtendedInteractionTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveExtendedInteraction(false);
		assertFalse(dao.loadExtendedInteraction());
	}

	@Test
	public void hasExtendedInteractionTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveExtendedInteraction(false);
		assertTrue(dao.hasExtendedInteraction());
		dao.saveExtendedInteraction(null);
		assertFalse(dao.hasExtendedInteraction());
	}

	@Test
	public void saveWildernessInteractionTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveWildernessInteraction(true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(config.getBoolean("WildernessInteraction"));
	}

	@Test
	public void loadWildernessInteractionTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveWildernessInteraction(false);
		assertFalse(dao.loadWildernessInteraction());
	}

	@Test
	public void hasWildernessInteractionTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveWildernessInteraction(false);
		assertTrue(dao.hasWildernessInteraction());
		dao.saveWildernessInteraction(null);
		assertFalse(dao.hasWildernessInteraction());
	}

	@Test
	public void saveCurrencyPlTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveCurrencyPl("kths");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("kths", config.getString("currencyPl"));
	}

	@Test
	public void loadCurrencyPlTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveCurrencyPl("coins");
		assertEquals("coins", dao.loadCurrencyPl());
	}

	@Test
	public void hasCurrencyPlTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveCurrencyPl("kths");
		assertTrue(dao.hasCurrencyPl());
		dao.saveCurrencyPl(null);
		assertFalse(dao.hasCurrencyPl());
	}

	@Test
	public void saveCurrencySgTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveCurrencySg("kth");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("kth", config.getString("currencySg"));
	}

	@Test
	public void loadCurrencySgTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveCurrencySg("coin");
		assertEquals("coin", dao.loadCurrencySg());
	}

	@Test
	public void hasCurrencySgTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveCurrencySg("kth");
		assertTrue(dao.hasCurrencySg());
		dao.saveCurrencySg(null);
		assertFalse(dao.hasCurrencySg());
	}

	@Test
	public void saveHomesFeatureTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveHomesFeature(true);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(config.getBoolean("homes"));
	}

	@Test
	public void loadHomesFeatureTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveHomesFeature(false);
		assertFalse(dao.loadHomesFeature());
	}

	@Test
	public void hasHomesFeatureTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveHomesFeature(false);
		assertTrue(dao.hasHomesFeature());
		dao.saveHomesFeature(null);
		assertFalse(dao.hasHomesFeature());
	}

	@Test
	public void saveMaxPlayershops() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveMaxPlayershops(4);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(4, config.getInt("MaxPlayershops"));
	}

	@Test
	public void loadMaxPlayershopsTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveMaxPlayershops(6);
		assertEquals(6, dao.loadMaxPlayershops());
	}

	@Test
	public void hasMaxPlayershopsTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveMaxPlayershops(6);
		assertTrue(dao.hasMaxPlayershops());
		dao.saveMaxPlayershops(null);
		assertFalse(dao.hasMaxPlayershops());
	}

	@Test
	public void saveMaxJoinedTownsTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveMaxJoinedTowns(4);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(4, config.getInt("MaxJoinedTowns"));
	}

	@Test
	public void loadMaxJoinedTownsTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveMaxJoinedTowns(6);
		assertEquals(6, dao.loadMaxJoinedTowns());
	}

	@Test
	public void hasMaxJoinedTownsTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveMaxJoinedTowns(6);
		assertTrue(dao.hasMaxJoinedTowns());
		dao.saveMaxJoinedTowns(null);
		assertFalse(dao.hasMaxJoinedTowns());
	}

	@Test
	public void saveMaxJobsTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveMaxJobs(4);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(4, config.getInt("MaxJobs"));
	}

	@Test
	public void loadMaxJobsTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveMaxJobs(6);
		assertEquals(6, dao.loadMaxJobs());
	}

	@Test
	public void hasMaxJobsTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveMaxJobs(6);
		assertTrue(dao.hasMaxJobs());
		dao.saveMaxJobs(null);
		assertFalse(dao.hasMaxJobs());
	}

	@Test
	public void saveMaxHomesTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveMaxHomes(4);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(4, config.getInt("MaxHomes"));
	}

	@Test
	public void loadMaxHomesTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveMaxHomes(6);
		assertEquals(6, dao.loadMaxHomes());
	}

	@Test
	public void hasMaxHomesTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveMaxHomes(6);
		assertTrue(dao.hasMaxHomes());
		dao.saveMaxHomes(null);
		assertFalse(dao.hasMaxHomes());
	}

	@Test
	public void saveCountryTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveCountry("DE");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("DE", config.getString("localeCountry"));
	}

	@Test
	public void loadCountryTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveCountry("DE");
		assertEquals("DE", dao.loadCountry());
	}

	@Test
	public void hasCountryTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveCountry("DE");
		assertTrue(dao.hasCountry());
		dao.saveCountry(null);
		assertFalse(dao.hasCountry());
	}

	@Test
	public void saveLanguageTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		dao.saveLanguage("de");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("de", config.getString("localeLanguage"));
	}

	@Test
	public void loadLanguageTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao = new ConfigDaoImpl(bukkitService);
		dao.saveLanguage("de");
		assertEquals("de", dao.loadLanguage());
	}

	@Test
	public void hasLanguageTest() {
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		dao.saveLanguage("de");
		assertTrue(dao.hasLanguage());
		dao.saveLanguage(null);
		assertFalse(dao.hasLanguage());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void removeDeprecatedTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		ConfigDao dao = new ConfigDaoImpl(bukkitService);
		File file = new File("src/config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("TownNames", Arrays.asList("town1", "town2"));
		assertDoesNotThrow(() -> config.save(file));
		dao = new ConfigDaoImpl(bukkitService);
		dao.removeDeprecated();
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertFalse(config2.isSet("TownNames"));
	}
}
