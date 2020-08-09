package com.ue.bank.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.bank.dataaccess.impl.BankDaoImpl;

@ExtendWith(MockitoExtension.class)
public class BankDaoImplTest {

	@InjectMocks
	BankDaoImpl dao;

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
		dao.setupSavefile("src");
		File result = new File("src/BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertEquals(0, config.getStringList("Ibans").size());
	}

	@Test
	public void setupSavefileLoadTest() {
		dao.setupSavefile("src");
		dao.setupSavefile("src");
		File result = new File("src/BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertEquals(0, config.getStringList("Ibans").size());
	}

	@Test
	public void saveIbanListTest() {
		dao.setupSavefile("src");
		List<String> list = new ArrayList<>();
		list.add("myiban");
		dao.saveIbanList(list);
		File result = new File("src/BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals(1, config.getStringList("Ibans").size());
		assertEquals("myiban", config.getStringList("Ibans").get(0));
	}

	@Test
	public void loadIbanListTest() {
		dao.setupSavefile("src");
		List<String> list = new ArrayList<>();
		list.add("myiban");
		dao.saveIbanList(list);
		dao.setupSavefile("src");
		assertEquals(1, dao.loadIbanList().size());
		assertEquals("myiban", dao.loadIbanList().get(0));
	}

	@Test
	public void deleteAccountTest() {
		dao.setupSavefile("src");
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
		dao.setupSavefile("src");
		dao.saveAmount("myiban", 12.34);
		File result = new File("src/BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals("12.34", config.getString("myiban.amount"));
	}

	@Test
	public void loadAmountTest() {
		dao.setupSavefile("src");
		dao.saveAmount("myiban", 12.34);
		dao.setupSavefile("src");
		assertEquals("12.34", String.valueOf(dao.loadAmount("myiban")));
	}
}
