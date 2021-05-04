package org.ue.bank.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.utils.api.MessageWrapper;

@ExtendWith(MockitoExtension.class)
public class BankValidationHandlerTest {

	@InjectMocks
	BankValidationHandlerImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void checkForHasEnoughMoneyTestFail() {
		try {
			validationHandler.checkForHasEnoughMoney(10.0, 20.0);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(ExceptionMessageEnum.NOT_ENOUGH_MONEY, e.getKey());
		}
	}

	@Test
	public void checkForHasEnoughMoneyTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForHasEnoughMoney(10.0, 5.0));
	}

	@Test
	public void createNewTest() {
		BankException exception = validationHandler.createNew(messageWrapper, ExceptionMessageEnum.NOT_ENOUGH_MONEY,
				"param");
		assertEquals(1, exception.getParams().length);
		assertEquals("param", exception.getParams()[0]);
		assertEquals(ExceptionMessageEnum.NOT_ENOUGH_MONEY, exception.getKey());
	}
}
