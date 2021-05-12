package org.ue.common.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyvillager.logic.impl.EconomyVillagerValidatorImpl;

@ExtendWith(MockitoExtension.class)
public class EconomyVillagerValidationHandlerTest {

	static AbstractValidator validator;

	private static class AbstractValidator extends EconomyVillagerValidatorImpl<AbstractException> {

		public AbstractValidator(ServerProvider serverProvider, MessageWrapper messageWrapper) {
			super(serverProvider, messageWrapper);
		}

		@Override
		protected AbstractException createNew(MessageWrapper messageWrapper, ExceptionMessageEnum key,
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
		validator = new AbstractValidator(mock(ServerProvider.class), mock(MessageWrapper.class));
	}
	
	@Test
	public void checkForResizePossibleTestValid() {
		Inventory inv = mock(Inventory.class);
		assertDoesNotThrow(() -> validator.checkForResizePossible(inv, 9, 27, 1));
	}
	
	@Test
	public void checkForResizePossibleTestValid2() {
		Inventory inv = mock(Inventory.class);
		when(inv.getItem(16)).thenReturn(null);
		when(inv.getItem(15)).thenReturn(null);
		when(inv.getItem(14)).thenReturn(null);
		when(inv.getItem(13)).thenReturn(null);
		when(inv.getItem(12)).thenReturn(null);
		when(inv.getItem(11)).thenReturn(null);
		when(inv.getItem(10)).thenReturn(null);
		when(inv.getItem(9)).thenReturn(null);
		assertDoesNotThrow(() -> validator.checkForResizePossible(inv, 18, 9, 1));
	}
	
	@Test
	public void checkForResizePossibleTestFail() {
		Inventory inv = mock(Inventory.class);
		ItemStack stack = mock(ItemStack.class);
		when(stack.getType()).thenReturn(Material.STONE);
		when(inv.getItem(15)).thenReturn(stack);
		when(inv.getItem(16)).thenReturn(null);
		AbstractException e = assertThrows(AbstractException.class,
				() -> validator.checkForResizePossible(inv, 18, 9, 1));
		assertEquals(0, e.getParams().length);
		assertEquals(ExceptionMessageEnum.RESIZING_FAILED, e.getKey());
	}

	@Test
	public void checkForSlotIsEmptyTestValid() {
		assertDoesNotThrow(() -> validator.checkForSlotIsEmpty(new HashSet<Integer>(), 1));
	}

	@Test
	public void checkForSlotIsEmptyTestFail() {
		AbstractException e = assertThrows(AbstractException.class,
				() -> validator.checkForSlotIsEmpty(new HashSet<Integer>(Arrays.asList(1)), 1));
		assertEquals(1, e.getParams().length);
		assertEquals(2, e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.SLOT_OCCUPIED, e.getKey());
	}
	
	@Test
	public void checkForSlotIsNotEmptyTestValid() {
		assertDoesNotThrow(() -> validator.checkForSlotIsNotEmpty(new HashSet<Integer>(Arrays.asList(1)), 1));
	}

	@Test
	public void checkForSlotIsNotEmptyTestFail() {
		AbstractException e = assertThrows(AbstractException.class,
				() -> validator.checkForSlotIsNotEmpty(new HashSet<Integer>(), 1));
		assertEquals(1, e.getParams().length);
		assertEquals(2, e.getParams()[0]);
		assertEquals(ExceptionMessageEnum.SLOT_EMPTY, e.getKey());
	}
}
