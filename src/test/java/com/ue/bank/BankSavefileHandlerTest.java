package com.ue.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.bank.api.BankController;
import com.ue.bank.impl.BankSavefileHandler;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;

public class BankSavefileHandlerTest {

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		MockBukkit.mock();
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
		BankSavefileHandler.setupSavefile();
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertEquals(0, config.getStringList("Ibans").size());
	}
	
	@Test
	public void setupSavefileLoadTest() {
		BankSavefileHandler.setupSavefile();
		BankSavefileHandler.setupSavefile();
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertEquals(0, config.getStringList("Ibans").size());
	}
	
	@Test
	public void saveIbanListTest() {
		BankSavefileHandler.setupSavefile();
		List<String> list = new ArrayList<>();
		list.add("myiban");
		BankSavefileHandler.saveIbanList(list);
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals(1, config.getStringList("Ibans").size());
		assertEquals("myiban", config.getStringList("Ibans").get(0));
	}
	
	@Test
	public void loadIbanListTest() {
		BankSavefileHandler.setupSavefile();
		List<String> list = new ArrayList<>();
		list.add("myiban");
		BankSavefileHandler.saveIbanList(list);
		BankSavefileHandler.setupSavefile();
		assertEquals(1, BankSavefileHandler.loadIbanList().size());
		assertEquals("myiban", BankSavefileHandler.loadIbanList().get(0));
	}
	
	@Test
	public void deleteAccountTest() {
		try {
			BankSavefileHandler.setupSavefile();
			BankController.createExternalBankAccount(10.0, "myiban");
			File result = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
			assertTrue(config.contains("myiban.amount"));
			BankSavefileHandler.deleteAccount("myiban");
			config = YamlConfiguration.loadConfiguration(result);
			assertFalse(config.contains("myiban.amount"));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void saveAmountTest() {
		BankSavefileHandler.setupSavefile();
		BankSavefileHandler handler = new BankSavefileHandler("myiban");
		handler.saveAmount(12.34);
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals("12.34",config.getString("myiban.amount"));
	}
	
	@Test
	public void loadAmountTest() {
		BankSavefileHandler.setupSavefile();
		BankSavefileHandler handler = new BankSavefileHandler("myiban");
		handler.saveAmount(12.34);
		BankSavefileHandler.setupSavefile();
		assertEquals("12.34",String.valueOf(handler.loadAmount()));
	}
}
