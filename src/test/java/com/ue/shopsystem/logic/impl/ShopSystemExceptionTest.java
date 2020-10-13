package com.ue.shopsystem.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;

@ExtendWith(MockitoExtension.class)
public class ShopSystemExceptionTest {

	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void constructorTest() {
		when(messageWrapper.getErrorString("already_rented", "param1", 2)).thenReturn("my error message");
		ShopSystemException e = new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.ALREADY_RENTED,
				"param1", 2);
		
		assertEquals(ShopExceptionMessageEnum.ALREADY_RENTED, e.getKey());
		assertEquals(2, e.getParams().length);
		assertEquals("param1", e.getParams()[0]);
		assertEquals(2, e.getParams()[1]);
		assertEquals("my error message", e.getMessage());
	}
}
