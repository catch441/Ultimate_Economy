package com.ue.bank.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

@ExtendWith(MockitoExtension.class)
public class BankValidationHandlerTest {

	@InjectMocks
	BankValidationHandlerImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void checkForPositiveAmountTestFail() {
		try {
			validationHandler.checkForPositiveAmount(-10.0);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(-10.0, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}

	@Test
	public void checkForPositiveAmountTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForPositiveAmount(10.0));
	}

	@Test
	public void checkForHasEnoughMoneyTestFail() {
		try {
			validationHandler.checkForHasEnoughMoney(10.0, 20.0);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(GeneralEconomyExceptionMessageEnum.NOT_ENOUGH_MONEY, e.getKey());
		}
	}

	@Test
	public void checkForHasEnoughMoneyTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForHasEnoughMoney(10.0, 5.0));
	}

	@Test
	public void checkForIbanIsFreeTestFail() {
		try {
			validationHandler.checkForIbanIsFree(Arrays.asList("myiban"), "myiban");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("myiban", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, e.getKey());
		}
	}

	@Test
	public void checkForIbanIsFreeTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForIbanIsFree(new ArrayList<>(), "myiban"));
	}
}
