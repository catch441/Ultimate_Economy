package org.ue.shopsystem.logic.to;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.shopsystem.logic.impl.ShopItemImpl;

@ExtendWith(MockitoExtension.class)
public class ShopItemTest {
	
	@Test
	public void constructorTest() {
		ItemStack stack = mock(ItemStack.class);
		ItemStack clone = mock(ItemStack.class);
		ItemStack cloneClone = mock(ItemStack.class);
		when(stack.clone()).thenReturn(clone);
		when(clone.clone()).thenReturn(cloneClone);
		ShopItemImpl item = new ShopItemImpl(stack, 10, 2, 3, 0);
		assertEquals(0, item.getSlot());
		assertEquals(10, item.getAmount());
		assertEquals("2.0", String.valueOf(item.getSellPrice()));
		assertEquals("3.0", String.valueOf(item.getBuyPrice()));
		verify(stack).setAmount(1);
		assertEquals(cloneClone, item.getItemStack());
		assertEquals(0, item.getStock());
	}
}
