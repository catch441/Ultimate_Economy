package com.ue.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.bank.dataaccess.impl.BankDaoImpl;
import com.ue.bank.logic.impl.BankManagerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;

public class BankSavefileHandlerTest {

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		file.delete();
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		UltimateEconomy.getInstance.getDataFolder().delete();
		MockBukkit.unload();
		int size = BankManagerImpl.getBankAccounts().size();
		for (int i = 0; i < size; i++) {
			BankManagerImpl.deleteBankAccount(BankManagerImpl.getBankAccounts().get(0));
		}
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		file.delete();
	}
	
	@Test
	public void setupSavefileTest() {
		BankDaoImpl.setupSavefile();
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertEquals(0, config.getStringList("Ibans").size());
	}
	
	@Test
	public void setupSavefileLoadTest() {
		BankDaoImpl.setupSavefile();
		BankDaoImpl.setupSavefile();
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertEquals(0, config.getStringList("Ibans").size());
	}
	
	@Test
	public void saveIbanListTest() {
		BankDaoImpl.setupSavefile();
		List<String> list = new ArrayList<>();
		list.add("myiban");
		BankDaoImpl.saveIbanList(list);
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals(1, config.getStringList("Ibans").size());
		assertEquals("myiban", config.getStringList("Ibans").get(0));
	}
	
	@Test
	public void loadIbanListTest() {
		BankDaoImpl.setupSavefile();
		List<String> list = new ArrayList<>();
		list.add("myiban");
		BankDaoImpl.saveIbanList(list);
		BankDaoImpl.setupSavefile();
		assertEquals(1, BankDaoImpl.loadIbanList().size());
		assertEquals("myiban", BankDaoImpl.loadIbanList().get(0));
	}
	
	@Test
	public void deleteAccountTest() {
		try {
			BankDaoImpl.setupSavefile();
			BankManagerImpl.createExternalBankAccount(10.0, "myiban");
			File result = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
			assertTrue(config.contains("myiban.amount"));
			BankDaoImpl.deleteAccount("myiban");
			config = YamlConfiguration.loadConfiguration(result);
			assertFalse(config.contains("myiban.amount"));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void saveAmountTest() {
		BankDaoImpl.setupSavefile();
		BankDaoImpl handler = new BankDaoImpl("myiban");
		handler.saveAmount(12.34);
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals("12.34",config.getString("myiban.amount"));
	}
	
	@Test
	public void loadAmountTest() {
		BankDaoImpl.setupSavefile();
		BankDaoImpl handler = new BankDaoImpl("myiban");
		handler.saveAmount(12.34);
		BankDaoImpl.setupSavefile();
		assertEquals("12.34",String.valueOf(handler.loadAmount()));
	}
}
