package org.ue.bank.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.ServerProvider;

@ExtendWith(MockitoExtension.class)
public class BankDaoImplTest {

	@InjectMocks
	BankDaoImpl dao;
	@Mock
	ServerProvider serverProvider;

	/**
	 * Delete savefile.
	 */
	@AfterEach
	public void cleanUp() {
		File file = new File("src/BankAccounts.yml");
		file.delete();
	}

	@Test
	public void setupSavefileTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		File result = new File("src/BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertEquals(0, config.getStringList("Ibans").size());
	}

	@Test
	public void setupSavefileLoadTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.setupSavefile();
		File result = new File("src/BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertEquals(0, config.getStringList("Ibans").size());
	}

	@Test
	public void loadIbanListTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.saveAmount("myiban", 0.0);
		dao.setupSavefile();
		assertEquals(1, dao.loadIbanList().size());
		assertEquals("myiban", dao.loadIbanList().get(0));
	}

	@Test
	public void deleteAccountTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.saveAmount("myiban", 20.0);
		File result = new File("src/BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(config.contains("myiban"));
		dao.deleteAccount("myiban");
		config = YamlConfiguration.loadConfiguration(result);
		assertFalse(config.contains("myiban"));
	}

	@Test
	public void saveAmountTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.saveAmount("myiban", 12.34);
		File result = new File("src/BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals("12.34", config.getString("myiban.amount"));
	}

	@Test
	public void loadAmountTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.saveAmount("myiban", 12.34);
		dao.setupSavefile();
		assertEquals("12.34", String.valueOf(dao.loadAmount("myiban")));
	}

	@Test
	public void removeOldIbanListTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		File file = new File("src/BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		List<String> list = Arrays.asList("myiban");
		config.set("Ibans", list);
		config.set("myiban", 100.0);
		save(file, config);

		assertTrue(config.contains("Ibans"));
		dao.setupSavefile();
		List<String> result = dao.loadIbanList();
		assertEquals("myiban", result.get(0));
		config = YamlConfiguration.loadConfiguration(file);
		assertFalse(config.contains("Ibans"));
	}

	private void save(File file, YamlConfiguration config) {
		assertDoesNotThrow(() -> config.save(file));
	}
}
