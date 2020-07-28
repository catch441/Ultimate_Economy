package com.ue.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.bank.api.BankAccount;
import com.ue.bank.api.BankController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;

public class BankControllerTest {

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
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
	public void createBankAccountTest() {
		BankAccount account = BankController.createBankAccount(10.0);
		assertEquals(account, BankController.getBankAccounts().get(0));
		assertEquals(1, BankController.getBankAccounts().size());
		assertEquals(1,BankController.getIbanList().size());
		assertEquals(account.getIban(), BankController.getIbanList().get(0));
	}
	
	@Test
	public void createExternalBankAccountTest() {
		try {
			BankController.createExternalBankAccount(10.0,"myiban");
			BankAccount account = BankController.getBankAccounts().get(0);
			assertEquals(account, BankController.getBankAccounts().get(0));
			assertEquals(1, BankController.getBankAccounts().size());
			assertEquals(1,BankController.getIbanList().size());
			assertEquals("myiban", BankController.getIbanList().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void createExternalBankAccountTestFail() {
		try {
			BankController.createExternalBankAccount(10.0,"myiban");
			BankController.createExternalBankAccount(10.0,"myiban");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§c§4myiban§c already exists!", e.getMessage());
		}
	}
	
	@Test
	public void deleteBankAccountTest() {
		BankAccount account = BankController.createBankAccount(10.0);
		BankController.deleteBankAccount(account);
		assertEquals(0, BankController.getBankAccounts().size());
		assertEquals(0,BankController.getIbanList().size());
	}
	
	@Test
	public void getIbanListTest() {
		BankAccount account1 = BankController.createBankAccount(10.0);
		BankAccount account2 = BankController.createBankAccount(10.0);
		assertEquals(2,BankController.getIbanList().size());
		assertEquals(account1.getIban(), BankController.getIbanList().get(0));
		assertEquals(account2.getIban(), BankController.getIbanList().get(1));
	}
	
	@Test
	public void getBankAccountsTest() {
		BankAccount account1 = BankController.createBankAccount(10.0);
		BankAccount account2 = BankController.createBankAccount(10.0);
		assertEquals(2,BankController.getBankAccounts().size());
		assertEquals(account1, BankController.getBankAccounts().get(0));
		assertEquals(account2, BankController.getBankAccounts().get(1));
	}
	
	@Test
	public void getBankAccountByIbanTest() {
		try {
			BankController.createBankAccount(10.0);
			BankAccount account2 = BankController.createBankAccount(10.0);
			BankAccount result = BankController.getBankAccountByIban(account2.getIban());
			assertEquals(account2, result);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void getBankAccountByIbanTestFail() {
		try {
			BankController.getBankAccountByIban("kthschnll");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§c§4kthschnll§c does not exist!", e.getMessage());
		}
	}
	
	@Test
	public void loadBankAccountsTest() {
		BankAccount account1 = BankController.createBankAccount(10.0);
		BankAccount account2 = BankController.createBankAccount(10.0);
		BankController.getBankAccounts().clear();
		assertEquals(0, BankController.getBankAccounts().size());
		BankController.loadBankAccounts();
		assertEquals(2, BankController.getBankAccounts().size());
		assertEquals(account1.getIban(),BankController.getBankAccounts().get(0).getIban());
		assertEquals(String.valueOf(account1.getAmount()),String.valueOf(BankController.getBankAccounts().get(0).getAmount()));
		assertEquals(account2.getIban(),BankController.getBankAccounts().get(1).getIban());
		assertEquals(String.valueOf(account2.getAmount()),String.valueOf(BankController.getBankAccounts().get(1).getAmount()));
	}
}
