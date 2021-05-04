package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopEditorHandler;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopsystemException;

@ExtendWith(MockitoExtension.class)
public class ShopEditorHandlerImplTest {

	@Mock
	CustomSkullService skullService;
	@Mock
	AbstractShop shop;

	@Test
	public void constructorTest() {
		Inventory inv = mock(Inventory.class);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackFilled = mock(ItemStack.class);
		ShopItem item1 = mock(ShopItem.class);
		ShopItem item2 = mock(ShopItem.class);
		when(item1.getSlot()).thenReturn(6);
		when(item2.getSlot()).thenReturn(7);
		when(shop.getSize()).thenReturn(9);
		when(shop.getName()).thenReturn("myshop");
		when(shop.createVillagerInventory(9, "myshop-Editor")).thenReturn(inv);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 1")).thenReturn(stack);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 2")).thenReturn(stack);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 3")).thenReturn(stack);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 4")).thenReturn(stack);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 5")).thenReturn(stack);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 6")).thenReturn(stack);
		when(shop.getItemList()).thenReturn(Arrays.asList(item1, item2));
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 7")).thenReturn(stackFilled);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 8")).thenReturn(stackFilled);
		new ShopEditorHandlerImpl(skullService, shop);
		verify(inv).setItem(0, stack);
		verify(inv).setItem(1, stack);
		verify(inv).setItem(2, stack);
		verify(inv).setItem(3, stack);
		verify(inv).setItem(4, stack);
		verify(inv).setItem(5, stack);
		verify(inv).setItem(6, stackFilled);
		verify(inv).setItem(7, stackFilled);
		verify(inv, never()).setItem(eq(8), any());
	}

	@Test
	public void setOccupiedTestTrue() {
		Inventory inv = mock(Inventory.class);
		ItemStack stack = mock(ItemStack.class);
		when(shop.getSize()).thenReturn(9);
		when(shop.getName()).thenReturn("myshop");
		when(shop.createVillagerInventory(9, "myshop-Editor")).thenReturn(inv);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(skullService, shop);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 1")).thenReturn(stack);
		handler.setOccupied(true, 0);
		verify(inv).setItem(0, stack);
	}

	@Test
	public void setOccupiedTestFalse() {
		Inventory inv = mock(Inventory.class);
		ItemStack stack = mock(ItemStack.class);
		when(shop.getSize()).thenReturn(9);
		when(shop.getName()).thenReturn("myshop");
		when(shop.createVillagerInventory(9, "myshop-Editor")).thenReturn(inv);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(skullService, shop);
		handler.setOccupied(true, 0);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 1")).thenReturn(stack);
		handler.setOccupied(false, 0);
		verify(inv).setItem(0, stack);
	}

	@Test
	public void changeInventoryNameTest() {
		Inventory inv = mock(Inventory.class);
		Inventory newInv = mock(Inventory.class);
		when(shop.getSize()).thenReturn(9);
		when(shop.getName()).thenReturn("myshop");
		when(shop.createVillagerInventory(9, "myshop-Editor")).thenReturn(inv);
		when(shop.createVillagerInventory(9, "catch-Editor")).thenReturn(newInv);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(skullService, shop);

		handler.changeInventoryName("catch");
		verify(newInv).setContents(any());
		verify(inv).getContents();
		assertEquals(newInv, handler.getEditorInventory());
	}

	@Test
	public void handleInventoryClickTestFree() {
		Inventory inv = mock(Inventory.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(shop.getSize()).thenReturn(9);
		when(shop.getName()).thenReturn("myshop");
		when(shop.createVillagerInventory(9, "myshop-Editor")).thenReturn(inv);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(skullService, shop);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getRawSlot()).thenReturn(0);
		when(event.getCurrentItem()).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		when(meta.getDisplayName()).thenReturn("Slot 1");
		when(event.getWhoClicked()).thenReturn(player);
		handler.handleInventoryClick(event);
		assertDoesNotThrow(() -> verify(shop).openSlotEditor(player, 0));
	}

	@Test
	public void handleInventoryClickTestBottomInvClick() {
		Inventory inv = mock(Inventory.class);
		when(shop.getSize()).thenReturn(9);
		when(shop.getName()).thenReturn("myshop");
		when(shop.createVillagerInventory(9, "myshop-Editor")).thenReturn(inv);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(skullService, shop);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getRawSlot()).thenReturn(14);
		reset(shop);
		handler.handleInventoryClick(event);
		verify(shop).getSize();
		verifyNoMoreInteractions(shop);
	}

	@Test
	public void handleInventoryClickTestWithException() throws ShopsystemException {
		Inventory inv = mock(Inventory.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(shop.getSize()).thenReturn(9);
		when(shop.getName()).thenReturn("myshop");
		when(shop.createVillagerInventory(9, "myshop-Editor")).thenReturn(inv);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(skullService, shop);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getRawSlot()).thenReturn(0);
		when(event.getCurrentItem()).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		when(meta.getDisplayName()).thenReturn("Slot 1");
		when(event.getWhoClicked()).thenReturn(player);
		doThrow(ShopsystemException.class).when(shop).openSlotEditor(player, 0);;
		handler.handleInventoryClick(event);
		assertDoesNotThrow(() -> verify(shop).openSlotEditor(player, 0));
		verifyNoInteractions(player);
		
	}
}
