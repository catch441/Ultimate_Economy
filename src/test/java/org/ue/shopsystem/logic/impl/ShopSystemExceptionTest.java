package org.ue.shopsystem.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.shopsystem.logic.api.ShopsystemException;

@ExtendWith(MockitoExtension.class)
public class ShopSystemExceptionTest {

	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void constructorTest() {	
		when(messageWrapper.getErrorString(ExceptionMessageEnum.ALREADY_RENTED, "param1", 2)).thenReturn("my error message");
		ShopsystemException e = new ShopsystemException(messageWrapper, ExceptionMessageEnum.ALREADY_RENTED,
				"param1", 2);
		
		assertEquals(ExceptionMessageEnum.ALREADY_RENTED, e.getKey());
		assertEquals(2, e.getParams().length);
		assertEquals("param1", e.getParams()[0]);
		assertEquals(2, e.getParams()[1]);
		assertEquals("my error message", e.getMessage());
	}
}
