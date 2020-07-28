package com.ue.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.bank.api.BankAccount;
import com.ue.bank.api.BankController;
import com.ue.bank.impl.BankAccountImpl;
import com.ue.bank.impl.BankSavefileHandler;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;

public class BankAccountTest {

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		BankSavefileHandler.setupSavefile();
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
		int size = BankController.getBankAccounts().size();
		for (int i = 0; i < size; i++) {
			BankController.deleteBankAccount(BankController.getBankAccounts().get(0));
		}
	}

	@Test
	public void constructorNewTest() {
		BankAccount account = new BankAccountImpl(2.5);
		assertEquals("2.5", String.valueOf(account.getAmount()));
		assertNotNull(account.getIban());
		// check savefile
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.5", config.getString(account.getIban() + ".amount"));
	}

	@Test
	public void constructorNewExternalIbanTest() {
		BankAccount account = new BankAccountImpl(2.5, "myiban");
		assertEquals("2.5", String.valueOf(account.getAmount()));
		assertEquals("myiban", account.getIban());
		// check savefile
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.5", config.getString("myiban.amount"));
	}

	@Test
	public void constructorLoadTest() {
		new BankAccountImpl(2.5, "myiban");
		BankAccount result = new BankAccountImpl("myiban");
		assertEquals("myiban", result.getIban());
		assertEquals("2.5", String.valueOf(result.getAmount()));
	}

	@Test
	public void decreaseAmountTest() {
		try {
			BankAccount account = BankController.createBankAccount(10.0);
			account.decreaseAmount(5.5);
			assertEquals("4.5", String.valueOf(account.getAmount()));
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals("4.5", config.getString(account.getIban() + ".amount"));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void decreaseAmountTestWithNegativeAmount() {
		try {
			BankAccount account = BankController.createBankAccount(10.0);
			account.decreaseAmount(-5.5);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-5.5§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void decreaseAmountTestWithInvalidAmount() {
		try {
			BankAccount account = BankController.createBankAccount(10.0);
			account.decreaseAmount(20);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe bank account does not have enough money!", e.getMessage());
		}
	}

	@Test
	public void increaseAmountAmountTest() {
		try {
			BankAccount account = BankController.createBankAccount(10.0);
			account.increaseAmount(5.5);
			assertEquals("15.5", String.valueOf(account.getAmount()));
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "BankAccounts.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals("15.5", config.getString(account.getIban() + ".amount"));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void increaseAmountTestWithNegativeAmount() {
		try {
			BankAccount account = BankController.createBankAccount(10.0);
			account.increaseAmount(-5.5);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-5.5§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void hasAmountTest() {
		try {
			BankAccount account = BankController.createBankAccount(10.0);
			assertTrue(account.hasAmount(5.0));
			assertFalse(account.hasAmount(20.0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void hasAmountTestWithNegativeAmount() {
		try {
			BankAccount account = BankController.createBankAccount(10.0);
			account.hasAmount(-5.0);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-5.0§c is invalid!", e.getMessage());
		}
	}
	
	@Test
	public void getAmountTest() {
		BankAccount account = BankController.createBankAccount(10.0);
		assertEquals("10.0", String.valueOf(account.getAmount()));
	}
	
	@Test
	public void getIbanTest() {
		try {
			BankController.createExternalBankAccount(10.0,"myiban");
			BankAccount account = BankController.getBankAccounts().get(0);
			assertEquals("10.0", String.valueOf(account.getAmount()));
		} catch (GeneralEconomyException e) {
			fail();
		}	
	}
}
