package org.ue.bank.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.dataaccess.api.BankDao;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankValidationHandler;
import org.ue.common.logic.api.GeneralValidationHandler;
import org.ue.general.GeneralEconomyException;

@ExtendWith(MockitoExtension.class)
public class BankAccountImplTest {

	@Mock
	BankDao bankDao;
	@Mock
	BankValidationHandler validationHandler;
	@Mock
	GeneralValidationHandler generalValidator;

	@Test
	public void constructorNewTest() {
		BankAccountImpl account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 2.5);
		assertEquals("2.5", String.valueOf(account.getAmount()));
		assertNotNull(account.getIban());
		verify(bankDao).saveAmount(account.getIban(), 2.5);
	}

	@Test
	public void constructorNewExternalIbanTest() {
		BankAccount account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 2.5, "myiban");
		assertEquals("2.5", String.valueOf(account.getAmount()));
		assertEquals("myiban", account.getIban());
		verify(bankDao).saveAmount("myiban", 2.5);
	}

	@Test
	public void constructorLoadTest() {
		when(bankDao.loadAmount("myiban")).thenReturn(2.5);
		BankAccount result = new BankAccountImpl(generalValidator, bankDao, validationHandler, "myiban");
		assertEquals("myiban", result.getIban());
		assertEquals("2.5", String.valueOf(result.getAmount()));
	}

	@Test
	public void decreaseAmountTest() throws GeneralEconomyException {
		BankAccountImpl account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 10.0);
		account.decreaseAmount(5.5);
		assertEquals("4.5", String.valueOf(account.getAmount()));
		verify(bankDao).saveAmount(account.getIban(), 4.5);
		verify(generalValidator).checkForPositiveValue(5.5);
		verify(validationHandler).checkForHasEnoughMoney(10.0, 5.5);
	}

	@Test
	public void decreaseAmountTestWithNegativeAmount() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForPositiveValue(-5.5);
		BankAccountImpl account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 10.0);
		assertThrows(GeneralEconomyException.class, () -> account.decreaseAmount(-5.5));
	}

	@Test
	public void decreaseAmountTestWithInvalidAmount() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForHasEnoughMoney(10.0, 20.0);
		BankAccountImpl account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 10.0);
		assertThrows(GeneralEconomyException.class, () -> account.decreaseAmount(20));
	}

	@Test
	public void increaseAmountAmountTest() throws GeneralEconomyException {
		BankAccountImpl account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 10.0);
		account.increaseAmount(5.5);
		assertEquals("15.5", String.valueOf(account.getAmount()));
		verify(bankDao).saveAmount(account.getIban(), 15.5);
		verify(generalValidator).checkForPositiveValue(5.5);
	}

	@Test
	public void increaseAmountTestWithNegativeAmount() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForPositiveValue(-5.5);
		BankAccountImpl account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 10.0);
		assertThrows(GeneralEconomyException.class, () -> account.increaseAmount(-5.5));
	}

	@Test
	public void hasAmountTest() throws GeneralEconomyException {
		BankAccountImpl account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 10.0);
		assertTrue(account.hasAmount(5.0));
		assertFalse(account.hasAmount(20.0));
	}

	@Test
	public void hasAmountTestWithNegativeAmount() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForPositiveValue(-5.0);
		BankAccountImpl account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 10.0);
		assertThrows(GeneralEconomyException.class, () -> account.hasAmount(-5.0));
	}

	@Test
	public void getAmountTest() {
		BankAccountImpl account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 10.0);
		assertEquals("10.0", String.valueOf(account.getAmount()));
	}

	@Test
	public void getIbanTest() {
		BankAccount account = new BankAccountImpl(generalValidator, bankDao, validationHandler, 2.5, "myiban");
		assertEquals("myiban", account.getIban());
	}
}
