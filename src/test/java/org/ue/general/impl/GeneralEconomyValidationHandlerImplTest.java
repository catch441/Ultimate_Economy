package org.ue.general.impl;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.impl.GeneralValidationHandlerImpl;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;

@ExtendWith(MockitoExtension.class)
public class GeneralEconomyValidationHandlerImplTest {

	@InjectMocks
	GeneralValidationHandlerImpl generalValidator;
	@Mock
	MessageWrapper messageWrapper;
	
	@Test
	public void checkForPositiveValueTestValid() {
		assertDoesNotThrow(() -> generalValidator.checkForPositiveValue(1.5));
	}
	
	@Test
	public void checkForPositiveValueTestFail() {
		try {
			generalValidator.checkForPositiveValue(-1.5);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(-1.5, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}
	
	@Test
	public void checkForValueGreaterZeroTestValid() {
		assertDoesNotThrow(() -> generalValidator.checkForValueGreaterZero(1.5));
	}
	
	@Test
	public void checkForValueGreaterZeroTestFail() {
		try {
			generalValidator.checkForValueGreaterZero(0);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(0.0, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}
	
	@Test
	public void checkForValueNotInListTestValid() {
		assertDoesNotThrow(() -> generalValidator.checkForValueNotInList(new ArrayList<>(), "value"));
	}
	
	@Test
	public void checkForValueNotInListTestFail() {
		try {
			generalValidator.checkForValueNotInList(Arrays.asList("value"), "value");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("value", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, e.getKey());
		}
	}
	
	@Test
	public void checkForValueInListTestValid() {
		assertDoesNotThrow(() -> generalValidator.checkForValueInList(Arrays.asList("value"), "value"));
	}
	
	@Test
	public void checkForValueInListTestFail() {
		try {
			generalValidator.checkForValueInList(new ArrayList<>(), "value");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("value", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}
	
	@Test
	public void checkForValidSizeTestValid() {
		assertDoesNotThrow(() -> generalValidator.checkForValidSize(9));
	}
	
	@Test
	public void checkForValidSizeTestFail1() {
		try {
			generalValidator.checkForValidSize(-9);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(-9, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}
	
	@Test
	public void checkForValidSizeTestFail2() {
		try {
			generalValidator.checkForValidSize(15);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(15, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}
	
	@Test
	public void checkForValidSizeTestFail3() {
		try {
			generalValidator.checkForValidSize(90);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(90, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}
	
	@Test
	public void checkForValidSlotTestValid() {
		assertDoesNotThrow(() -> generalValidator.checkForValidSlot(1, 8));
	}
	
	@Test
	public void checkForValidSlotTestFail1() {
		try {
			generalValidator.checkForValidSlot(-5, 8);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(-4, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}
	
	@Test
	public void checkForValidSlotTestFail2() {
		try {
			generalValidator.checkForValidSlot(8, 8);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(9, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}
}
