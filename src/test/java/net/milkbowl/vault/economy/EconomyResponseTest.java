package net.milkbowl.vault.economy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

@ExtendWith(MockitoExtension.class)
public class EconomyResponseTest {

	@Test
	public void transactionSuccessTest() {
		EconomyResponse r = new EconomyResponse(0, 0, ResponseType.SUCCESS, "");
		assertTrue(r.transactionSuccess());
		r = new EconomyResponse(0, 0, ResponseType.FAILURE, "");
		assertFalse(r.transactionSuccess());
	}
	
	@Test
	public void responseEnumTest() {
		assertEquals(1, ResponseType.SUCCESS.getId());
		assertEquals(2, ResponseType.FAILURE.getId());
		assertEquals(3, ResponseType.NOT_IMPLEMENTED.getId());
	}
}
