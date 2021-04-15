package org.ue.bank.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.dataaccess.api.BankDao;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankValidationHandler;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;

@ExtendWith(MockitoExtension.class)
public class BankManagerImplTest {

	@InjectMocks
	BankManagerImpl bankManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	BankDao bankDao;
	@Mock
	BankValidationHandler validationHandler;
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@AfterEach
	private void cleanUp() {
		for (BankAccount account : bankManager.getBankAccounts()) {
			bankManager.deleteBankAccount(account);
		}
	}

	@Test
	public void createBankAccountTest() {
		BankAccount account = bankManager.createBankAccount(10.0);
		assertEquals(account, bankManager.getBankAccounts().get(0));
		assertEquals(1, bankManager.getBankAccounts().size());
		assertEquals(1, bankManager.getIbanList().size());
		assertEquals(account.getIban(), bankManager.getIbanList().get(0));
		List<String> ibans = new ArrayList<>();
		ibans.add(account.getIban());
		assertTrue(bankManager.getBankAccounts().contains(account));
	}

	@Test
	public void createExternalBankAccountTest() {
		BankAccount account = assertDoesNotThrow(() -> bankManager.createExternalBankAccount(10.0, "myiban"));
		assertEquals(account, bankManager.getBankAccounts().get(0));
		assertEquals(1, bankManager.getBankAccounts().size());
		assertEquals(1, bankManager.getIbanList().size());
		assertEquals("myiban", bankManager.getIbanList().get(0));
		List<String> ibans = new ArrayList<>();
		ibans.add("myiban");
		assertTrue(bankManager.getBankAccounts().contains(account));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValueNotInList(new ArrayList<>(), "myiban"));
	}

	@Test
	public void createExternalBankAccountTestFail() throws GeneralEconomyException {
		List<String> ibans = new ArrayList<>();
		ibans.add("myiban");
		assertDoesNotThrow(() -> bankManager.createExternalBankAccount(10.0, "myiban"));
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValueNotInList(ibans, "myiban");
		assertThrows(GeneralEconomyException.class, () -> bankManager.createExternalBankAccount(10.0, "myiban"));
	}

	@Test
	public void deleteBankAccountTest() {
		BankAccount account = bankManager.createBankAccount(10.0);
		assertEquals(1, bankManager.getBankAccounts().size());
		assertEquals(1, bankManager.getIbanList().size());
		bankManager.deleteBankAccount(account);
		assertEquals(0, bankManager.getBankAccounts().size());
		assertEquals(0, bankManager.getIbanList().size());
		verify(bankDao).deleteAccount(account.getIban());
	}

	@Test
	public void getIbanListTest() {
		BankAccount account1 = bankManager.createBankAccount(10.0);
		BankAccount account2 = bankManager.createBankAccount(10.0);
		assertEquals(2, bankManager.getIbanList().size());
		assertTrue(bankManager.getIbanList().contains(account1.getIban()));
		assertTrue(bankManager.getIbanList().contains(account2.getIban()));
	}

	@Test
	public void getBankAccountsTest() {
		BankAccount account1 = bankManager.createBankAccount(10.0);
		BankAccount account2 = bankManager.createBankAccount(10.0);
		assertEquals(2, bankManager.getBankAccounts().size());
		assertTrue(bankManager.getBankAccounts().contains(account1));
		assertTrue(bankManager.getBankAccounts().contains(account2));
	}

	@Test
	public void getBankAccountByIbanTest() {
		try {
			bankManager.createBankAccount(10.0);
			BankAccount account2 = bankManager.createBankAccount(10.0);
			BankAccount result = bankManager.getBankAccountByIban(account2.getIban());
			assertEquals(account2, result);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void getBankAccountByIbanTestFail() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValueExists(null, "catch");
		assertThrows(GeneralEconomyException.class, () -> bankManager.getBankAccountByIban("catch"));
	}

	@Test
	public void loadBankAccountsTest() {
		BankAccount account1 = bankManager.createBankAccount(10.0);
		BankAccount account2 = bankManager.createBankAccount(10.0);
		when(bankDao.loadIbanList()).thenReturn(Arrays.asList(account1.getIban(), account2.getIban()));
		when(bankDao.loadAmount(account1.getIban())).thenReturn(10.0);
		when(bankDao.loadAmount(account2.getIban())).thenReturn(10.0);

		cleanUp();

		assertEquals(0, bankManager.getBankAccounts().size());
		bankManager.loadBankAccounts();
		assertEquals(2, bankManager.getBankAccounts().size());
		assertTrue(bankManager.getBankAccounts().get(1).getIban().equals(account2.getIban())
				|| bankManager.getBankAccounts().get(1).getIban().equals(account1.getIban()));
		assertTrue(bankManager.getBankAccounts().get(0).getIban().equals(account2.getIban())
				|| bankManager.getBankAccounts().get(0).getIban().equals(account1.getIban()));
		assertEquals("10.0", String.valueOf(bankManager.getBankAccounts().get(0).getAmount()));

		assertEquals("10.0", String.valueOf(bankManager.getBankAccounts().get(1).getAmount()));
		verify(bankDao).setupSavefile();
	}
}
