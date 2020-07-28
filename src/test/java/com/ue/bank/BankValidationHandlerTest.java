package com.ue.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.bank.impl.BankValidationHandler;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;

public class BankValidationHandlerTest {
	
	private static BankValidationHandler validationHandler;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		validationHandler = new BankValidationHandler();
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
	}
	
	@Test
	public void checkForPositiveAmountTestFail() {
		try {
			validationHandler.checkForPositiveAmount(-10.0);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-10.0§c is invalid!", e.getMessage());
		}
	}
	
	@Test
	public void checkForPositiveAmountTestSuccess() {
		try {
			validationHandler.checkForPositiveAmount(10.0);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void checkForHasEnoughMoneyTestFail() {
		try {
			validationHandler.checkForHasEnoughMoney(10.0, 20.0);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe bank account does not have enough money!", e.getMessage());
		}
	}
	
	@Test
	public void checkForHasEnoughMoneyTestSuccess() {
		try {
			validationHandler.checkForHasEnoughMoney(10.0,5.0);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
}
