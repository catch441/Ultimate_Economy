package com.ue.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.impl.BankManagerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
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
		int size = BankManagerImpl.getBankAccounts().size();
		for (int i = 0; i < size; i++) {
			BankManagerImpl.deleteBankAccount(BankManagerImpl.getBankAccounts().get(0));
		}
	}
	
	@Test
	public void createBankAccountTest() {
		BankAccount account = BankManagerImpl.createBankAccount(10.0);
		assertEquals(account, BankManagerImpl.getBankAccounts().get(0));
		assertEquals(1, BankManagerImpl.getBankAccounts().size());
		assertEquals(1,BankManagerImpl.getIbanList().size());
		assertEquals(account.getIban(), BankManagerImpl.getIbanList().get(0));
	}
	
	@Test
	public void createExternalBankAccountTest() {
		try {
			BankManagerImpl.createExternalBankAccount(10.0,"myiban");
			BankAccount account = BankManagerImpl.getBankAccounts().get(0);
			assertEquals(account, BankManagerImpl.getBankAccounts().get(0));
			assertEquals(1, BankManagerImpl.getBankAccounts().size());
			assertEquals(1,BankManagerImpl.getIbanList().size());
			assertEquals("myiban", BankManagerImpl.getIbanList().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void createExternalBankAccountTestFail() {
		try {
			BankManagerImpl.createExternalBankAccount(10.0,"myiban");
			BankManagerImpl.createExternalBankAccount(10.0,"myiban");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§c§4myiban§c already exists!", e.getMessage());
		}
	}
	
	@Test
	public void deleteBankAccountTest() {
		BankAccount account = BankManagerImpl.createBankAccount(10.0);
		BankManagerImpl.deleteBankAccount(account);
		assertEquals(0, BankManagerImpl.getBankAccounts().size());
		assertEquals(0,BankManagerImpl.getIbanList().size());
	}
	
	@Test
	public void getIbanListTest() {
		BankAccount account1 = BankManagerImpl.createBankAccount(10.0);
		BankAccount account2 = BankManagerImpl.createBankAccount(10.0);
		assertEquals(2,BankManagerImpl.getIbanList().size());
		assertEquals(account1.getIban(), BankManagerImpl.getIbanList().get(0));
		assertEquals(account2.getIban(), BankManagerImpl.getIbanList().get(1));
	}
	
	@Test
	public void getBankAccountsTest() {
		BankAccount account1 = BankManagerImpl.createBankAccount(10.0);
		BankAccount account2 = BankManagerImpl.createBankAccount(10.0);
		assertEquals(2,BankManagerImpl.getBankAccounts().size());
		assertEquals(account1, BankManagerImpl.getBankAccounts().get(0));
		assertEquals(account2, BankManagerImpl.getBankAccounts().get(1));
	}
	
	@Test
	public void getBankAccountByIbanTest() {
		try {
			BankManagerImpl.createBankAccount(10.0);
			BankAccount account2 = BankManagerImpl.createBankAccount(10.0);
			BankAccount result = BankManagerImpl.getBankAccountByIban(account2.getIban());
			assertEquals(account2, result);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void getBankAccountByIbanTestFail() {
		try {
			BankManagerImpl.getBankAccountByIban("kthschnll");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§c§4kthschnll§c does not exist!", e.getMessage());
		}
	}
	
	@Test
	public void loadBankAccountsTest() {
		BankAccount account1 = BankManagerImpl.createBankAccount(10.0);
		BankAccount account2 = BankManagerImpl.createBankAccount(10.0);
		BankManagerImpl.getBankAccounts().clear();
		assertEquals(0, BankManagerImpl.getBankAccounts().size());
		BankManagerImpl.loadBankAccounts();
		assertEquals(2, BankManagerImpl.getBankAccounts().size());
		assertEquals(account1.getIban(),BankManagerImpl.getBankAccounts().get(0).getIban());
		assertEquals(String.valueOf(account1.getAmount()),String.valueOf(BankManagerImpl.getBankAccounts().get(0).getAmount()));
		assertEquals(account2.getIban(),BankManagerImpl.getBankAccounts().get(1).getIban());
		assertEquals(String.valueOf(account2.getAmount()),String.valueOf(BankManagerImpl.getBankAccounts().get(1).getAmount()));
	}
}
