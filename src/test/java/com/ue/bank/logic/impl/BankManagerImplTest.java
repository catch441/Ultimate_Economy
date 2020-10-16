package com.ue.bank.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.ue.bank.dataaccess.api.BankDao;
import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankValidationHandler;
import com.ue.common.utils.MessageWrapper;
import com.ue.general.impl.GeneralEconomyException;

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

	@AfterEach
	private void cleanUp() {
		bankManager.getBankAccounts().clear();
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
		verify(bankDao).saveIbanList(ibans);
	}

	@Test
	public void createExternalBankAccountTest() {
		try {
			BankAccount account = bankManager.createExternalBankAccount(10.0, "myiban");
			assertEquals(account, bankManager.getBankAccounts().get(0));
			assertEquals(1, bankManager.getBankAccounts().size());
			assertEquals(1, bankManager.getIbanList().size());
			assertEquals("myiban", bankManager.getIbanList().get(0));
			List<String> ibans = new ArrayList<>();
			ibans.add("myiban");
			verify(bankDao).saveIbanList(ibans);
			verify(validationHandler).checkForIbanIsFree(new ArrayList<>(), "myiban");
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void createExternalBankAccountTestFail() {
		try {
			List<String> ibans = new ArrayList<>();
			ibans.add("myiban");
			bankManager.createExternalBankAccount(10.0, "myiban");
			doThrow(GeneralEconomyException.class).when(validationHandler).checkForIbanIsFree(ibans, "myiban");
			bankManager.createExternalBankAccount(10.0, "myiban");
			fail();
		} catch (GeneralEconomyException e) {
		}
	}

	@Test
	public void deleteBankAccountTest() {
		BankAccount account = bankManager.createBankAccount(10.0);
		assertEquals(1, bankManager.getBankAccounts().size());
		assertEquals(1, bankManager.getIbanList().size());
		bankManager.deleteBankAccount(account);
		assertEquals(0, bankManager.getBankAccounts().size());
		assertEquals(0, bankManager.getIbanList().size());
		verify(bankDao).saveIbanList(new ArrayList<>());
		verify(bankDao).deleteAccount(account.getIban());
	}

	@Test
	public void getIbanListTest() {
		BankAccount account1 = bankManager.createBankAccount(10.0);
		BankAccount account2 = bankManager.createBankAccount(10.0);
		assertEquals(2, bankManager.getIbanList().size());
		assertEquals(account1.getIban(), bankManager.getIbanList().get(0));
		assertEquals(account2.getIban(), bankManager.getIbanList().get(1));
	}

	@Test
	public void getBankAccountsTest() {
		BankAccount account1 = bankManager.createBankAccount(10.0);
		BankAccount account2 = bankManager.createBankAccount(10.0);
		assertEquals(2, bankManager.getBankAccounts().size());
		assertEquals(account1, bankManager.getBankAccounts().get(0));
		assertEquals(account2, bankManager.getBankAccounts().get(1));
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
	public void getBankAccountByIbanTestFail() {
		try {
			when(messageWrapper.getErrorString("does_not_exist", "kthschnll"))
					.thenReturn("§c§4kthschnll§c does not exist!");
			bankManager.getBankAccountByIban("kthschnll");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§c§4kthschnll§c does not exist!", e.getMessage());
		}
	}

	@Test
	public void loadBankAccountsTest() {
		BankAccount account1 = bankManager.createBankAccount(10.0);
		BankAccount account2 = bankManager.createBankAccount(10.0);
		when(bankDao.loadIbanList()).thenReturn(Arrays.asList(account1.getIban(), account2.getIban()));
		when(bankDao.loadAmount(account1.getIban())).thenReturn(10.0);
		when(bankDao.loadAmount(account2.getIban())).thenReturn(10.0);	
		bankManager.getBankAccounts().clear();
		
		assertEquals(0, bankManager.getBankAccounts().size());
		bankManager.loadBankAccounts();
		assertEquals(2, bankManager.getBankAccounts().size());
		assertEquals(account1.getIban(), bankManager.getBankAccounts().get(0).getIban());
		assertEquals("10.0",
				String.valueOf(bankManager.getBankAccounts().get(0).getAmount()));
		assertEquals(account2.getIban(), bankManager.getBankAccounts().get(1).getIban());
		assertEquals("10.0",
				String.valueOf(bankManager.getBankAccounts().get(1).getAmount()));
		verify(bankDao).setupSavefile();
	}
}
