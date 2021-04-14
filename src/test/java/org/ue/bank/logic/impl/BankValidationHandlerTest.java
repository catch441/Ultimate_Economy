package org.ue.bank.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;

@ExtendWith(MockitoExtension.class)
public class BankValidationHandlerTest {

	@InjectMocks
	BankValidationHandlerImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	GeneralEconomyValidationHandler generalHandler;

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
	public void checkForHasEnoughMoneyTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForHasEnoughMoney(10.0, 5.0));
	}
	
}
