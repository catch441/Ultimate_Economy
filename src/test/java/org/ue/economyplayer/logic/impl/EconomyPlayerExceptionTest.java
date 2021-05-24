package org.ue.economyplayer.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayerException;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerExceptionTest {

	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void constructorTest() {
		when(messageWrapper.getErrorString(ExceptionMessageEnum.INVENTORY_FULL, "param1", 2)).thenReturn("my error message");
		EconomyPlayerException e = new EconomyPlayerException(messageWrapper, ExceptionMessageEnum.INVENTORY_FULL,
				"param1", 2);
		
		assertEquals(ExceptionMessageEnum.INVENTORY_FULL, e.getKey());
		assertEquals(2, e.getParams().length);
		assertEquals("param1", e.getParams()[0]);
		assertEquals(2, e.getParams()[1]);
		assertEquals("my error message", e.getMessage());
	}
}
