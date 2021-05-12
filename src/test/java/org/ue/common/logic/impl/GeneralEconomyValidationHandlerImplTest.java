package org.ue.common.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyvillager.logic.api.EconomyVillagerType;

@ExtendWith(MockitoExtension.class)
public class GeneralEconomyValidationHandlerImplTest {

	static AbstractValidator validator;

	private static class AbstractValidator extends GeneralValidatorImpl<GeneralEconomyException> {

		public AbstractValidator(MessageWrapper messageWrapper) {
			super(messageWrapper);
		}

		@Override
		protected GeneralEconomyException createNew(MessageWrapper messageWrapper, ExceptionMessageEnum key,
				Object... params) {
			return new AbstractException(messageWrapper, key, params);
		}

	}

	private static class AbstractException extends GeneralEconomyException {
		private static final long serialVersionUID = 1L;

		public AbstractException(MessageWrapper messageWrapper, ExceptionMessageEnum key, Object[] params) {
			super(messageWrapper, key, params);
		}
	}

	@BeforeAll
	public static void setup() {
		validator = new AbstractValidator(mock(MessageWrapper.class));
	}

	@Test
	public void checkForValidEnumTestValid() {
		assertDoesNotThrow(
				() -> validator.checkForValidEnum(EconomyVillagerType.values(), "adminshop"));
	}
	
	@Test
	public void checkForValidEnumTestFail() {
		AbstractException e = assertThrows(AbstractException.class, () -> validator.checkForValidEnum(EconomyVillagerType.values(), "catch"));
		assertEquals(1, e.getParams().length);
		assertEquals("catch", e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
	}

	@Test
	public void checkForNotReachedMaxTestValid() {
		assertDoesNotThrow(() -> validator.checkForNotReachedMax(false));
	}

	@Test
	public void checkForNotReachedMaxTestFail() {
		AbstractException e = assertThrows(AbstractException.class, () -> validator.checkForNotReachedMax(true));
		assertEquals(0, e.getParams().length);
		assertEquals(ExceptionMessageEnum.MAX_REACHED, e.getKey());
	}

	@Test
	public void checkForValueExistsTestValid() {
		assertDoesNotThrow(() -> validator.checkForValueExists("value", "name"));
	}

	@Test
	public void checkForValueExistsTestFail() {
		AbstractException e = assertThrows(AbstractException.class, () -> validator.checkForValueExists(null, "name"));
		assertEquals(1, e.getParams().length);
		assertEquals("name", e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
	}

	@Test
	public void checkForPositiveValueTestValid() {
		assertDoesNotThrow(() -> validator.checkForPositiveValue(1.5));
	}

	@Test
	public void checkForPositiveValueTestFail() {
		AbstractException e = assertThrows(AbstractException.class, () -> validator.checkForPositiveValue(-1.5));
		assertEquals(1, e.getParams().length);
		assertEquals(-1.5, e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
	}

	@Test
	public void checkForValueGreaterZeroTestValid() {
		assertDoesNotThrow(() -> validator.checkForValueGreaterZero(1.5));
	}

	@Test
	public void checkForValueGreaterZeroTestFail() {
		AbstractException e = assertThrows(AbstractException.class, () -> validator.checkForValueGreaterZero(0));
		assertEquals(1, e.getParams().length);
		assertEquals(0.0, e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
	}

	@Test
	public void checkForValueNotInListTestValid() {
		assertDoesNotThrow(() -> validator.checkForValueNotInList(new ArrayList<>(), "value"));
	}

	@Test
	public void checkForValueNotInListTestFail() {
		AbstractException e = assertThrows(AbstractException.class,
				() -> validator.checkForValueNotInList(Arrays.asList("value"), "value"));
		assertEquals(1, e.getParams().length);
		assertEquals("value", e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.ALREADY_EXISTS, e.getKey());
	}

	@Test
	public void checkForValueInListTestValid() {
		assertDoesNotThrow(() -> validator.checkForValueInList(Arrays.asList("value"), "value"));
	}

	@Test
	public void checkForValueInListTestFail() {
		AbstractException e = assertThrows(AbstractException.class,
				() -> validator.checkForValueInList(new ArrayList<>(), "value"));
		assertEquals(1, e.getParams().length);
		assertEquals("value", e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
	}

	@Test
	public void checkForValidSizeTestValid() {
		assertDoesNotThrow(() -> validator.checkForValidSize(9));
	}

	@Test
	public void checkForValidSizeTestFail1() {
		AbstractException e = assertThrows(AbstractException.class, () -> validator.checkForValidSize(-9));
		assertEquals(1, e.getParams().length);
		assertEquals(-9, e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
	}

	@Test
	public void checkForValidSizeTestFail2() {
		AbstractException e = assertThrows(AbstractException.class, () -> validator.checkForValidSize(15));
		assertEquals(1, e.getParams().length);
		assertEquals(15, e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
	}

	@Test
	public void checkForValidSizeTestFail3() {
		AbstractException e = assertThrows(AbstractException.class, () -> validator.checkForValidSize(90));
		assertEquals(1, e.getParams().length);
		assertEquals(90, e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
	}

	@Test
	public void checkForValidSlotTestValid() {
		assertDoesNotThrow(() -> validator.checkForValidSlot(1, 8));
	}

	@Test
	public void checkForValidSlotTestFail1() {
		AbstractException e = assertThrows(AbstractException.class, () -> validator.checkForValidSlot(-5, 8));
		assertEquals(1, e.getParams().length);
		assertEquals(-4, e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
	}

	@Test
	public void checkForValidSlotTestFail2() {
		AbstractException e = assertThrows(AbstractException.class, () -> validator.checkForValidSlot(8, 8));
		assertEquals(1, e.getParams().length);
		assertEquals(9, e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
	}
}
