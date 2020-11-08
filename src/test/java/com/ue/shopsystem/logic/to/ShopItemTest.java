package com.ue.shopsystem.logic.to;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShopItemTest {
	
	@Test
	public void constructorTest() {
		ItemStack stack = mock(ItemStack.class);
		ItemStack clone = mock(ItemStack.class);
		ItemStack cloneClone = mock(ItemStack.class);
		when(stack.toString()).thenReturn("ItemStack{STONE x 1}");
		when(stack.clone()).thenReturn(clone);
		when(clone.clone()).thenReturn(cloneClone);
		ShopItem item = new ShopItem(stack, 10, 2, 3, 0);
		assertEquals(0, item.getSlot());
		assertEquals(10, item.getAmount());
		assertEquals("2.0", String.valueOf(item.getSellPrice()));
		assertEquals("3.0", String.valueOf(item.getBuyPrice()));
		assertEquals("ItemStack{STONE x 1}".hashCode(), item.getItemHash());
		verify(stack).setAmount(1);
		assertEquals(cloneClone, item.getItemStack());
		assertEquals(0, item.getStock());
	}
	
	@Test
	public void constructorTestWithSpawner() {
		ItemStack stack = mock(ItemStack.class);
		ItemStack clone = mock(ItemStack.class);
		ItemStack cloneClone = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getType()).thenReturn(Material.SPAWNER);
		when(stack.getItemMeta()).thenReturn(meta);
		when(meta.getDisplayName()).thenReturn("COW");
		when(stack.clone()).thenReturn(clone);
		when(clone.clone()).thenReturn(cloneClone);
		ShopItem item = new ShopItem(stack, 10, 2, 3, 0);
		assertEquals(0, item.getSlot());
		assertEquals(10, item.getAmount());
		assertEquals("2.0", String.valueOf(item.getSellPrice()));
		assertEquals("3.0", String.valueOf(item.getBuyPrice()));
		assertEquals("SPAWNER_COW".hashCode(), item.getItemHash());
		verify(stack).setAmount(1);
		assertEquals(cloneClone, item.getItemStack());
		assertEquals(0, item.getStock());
	}
}
