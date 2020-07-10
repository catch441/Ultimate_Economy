package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.shopsystem.impl.ShopItem;

import be.seeseemelk.mockbukkit.MockBukkit;

public class ShopItemTest {
	
	@BeforeAll
	public static void initPlugin() {
		MockBukkit.mock();
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		MockBukkit.unload();
	}
	
	@Test
	public void constructorTest() {
		ItemStack stack = new ItemStack(Material.STONE);
		stack.setAmount(10);
		ShopItem item = new ShopItem(stack, 10, 2, 3, 0);
		assertEquals(0, item.getSlot());
		assertEquals(10, item.getAmount());
		assertEquals("2.0", String.valueOf(item.getSellPrice()));
		assertEquals("3.0", String.valueOf(item.getBuyPrice()));
		assertEquals("ItemStack{STONE x 1}", item.getItemString());
		assertEquals(Material.STONE, item.getItemStack().getType());
		assertEquals(1, item.getItemStack().getAmount());
		assertEquals(0, item.getStock());
	}
	
	@Test
	public void constructorTestWithSpawner() {
		ItemStack stack = new ItemStack(Material.SPAWNER);
		stack.setAmount(10);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("COW");
		stack.setItemMeta(meta);
		ShopItem item = new ShopItem(stack, 10, 2, 3, 0);
		assertEquals(0, item.getSlot());
		assertEquals(10, item.getAmount());
		assertEquals("2.0", String.valueOf(item.getSellPrice()));
		assertEquals("3.0", String.valueOf(item.getBuyPrice()));
		assertEquals("SPAWNER_COW", item.getItemString());
		assertEquals(Material.SPAWNER, item.getItemStack().getType());
		assertEquals("COW", item.getItemStack().getItemMeta().getDisplayName());
		assertEquals(1, item.getItemStack().getAmount());
		assertEquals(0, item.getStock());
	}
}
