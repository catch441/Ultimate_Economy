package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopEditorHandler;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import org.ue.shopsystem.logic.api.ShopsystemException;

@ExtendWith(MockitoExtension.class)
public class ShopEditorHandlerImplTest {

	@Mock
	CustomSkullService skullService;
	@Mock
	ServerProvider serverProvider;
	@Mock
	AbstractShop shop;

	@Test
	public void setupTest() {
		Inventory inv = mock(Inventory.class);
		ItemStack customizeItem = mock(ItemStack.class);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackFilled = mock(ItemStack.class);
		ItemMeta customizeItemMeta = mock(ItemMeta.class);
		ShopItem item1 = mock(ShopItem.class);
		ShopItem item2 = mock(ShopItem.class);
		when(customizeItem.getItemMeta()).thenReturn(customizeItemMeta);
		when(item1.getSlot()).thenReturn(6);
		when(item2.getSlot()).thenReturn(7);
		when(shop.getSize()).thenReturn(9);
		when(shop.createVillagerInventory(9, "Editor")).thenReturn(inv);
		when(serverProvider.createItemStack(Material.CRAFTING_TABLE, 1)).thenReturn(customizeItem);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 1")).thenReturn(stack);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 2")).thenReturn(stack);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 3")).thenReturn(stack);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 4")).thenReturn(stack);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 5")).thenReturn(stack);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 6")).thenReturn(stack);
		when(shop.getItemList()).thenReturn(Arrays.asList(item1, item2));
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 7")).thenReturn(stackFilled);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 8")).thenReturn(stackFilled);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(serverProvider, skullService);
		handler.setup(shop, 1);
		verify(inv).setItem(0, stack);
		verify(inv).setItem(1, stack);
		verify(inv).setItem(2, stack);
		verify(inv).setItem(3, stack);
		verify(inv).setItem(4, stack);
		verify(inv).setItem(5, stack);
		verify(inv).setItem(6, stackFilled);
		verify(inv).setItem(7, stackFilled);
		verify(inv).setItem(8, customizeItem);
		verify(customizeItemMeta).setDisplayName(ChatColor.GOLD + "Customize Villager");
	}

	@Test
	public void setOccupiedTestTrue() {
		Inventory inv = mock(Inventory.class);
		ItemStack customizeItem = mock(ItemStack.class);
		ItemMeta customizeItemMeta = mock(ItemMeta.class);
		when(customizeItem.getItemMeta()).thenReturn(customizeItemMeta);
		when(serverProvider.createItemStack(Material.CRAFTING_TABLE, 1)).thenReturn(customizeItem);
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackEmpty = mock(ItemStack.class);
		when(shop.getSize()).thenReturn(9);
		when(shop.createVillagerInventory(9, "Editor")).thenReturn(inv);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(serverProvider, skullService);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 1")).thenReturn(stack);
		when(skullService.getSkullWithName(eq(SkullTextureEnum.SLOTEMPTY), anyString())).thenReturn(stackEmpty);
		handler.setup(shop, 1);
		handler.setOccupied(true, 0);
		verify(inv).setItem(0, stack);
	}

	@Test
	public void setOccupiedTestFalse() {
		Inventory inv = mock(Inventory.class);
		ItemStack stack = mock(ItemStack.class);
		ItemStack customizeItem = mock(ItemStack.class);
		ItemMeta customizeItemMeta = mock(ItemMeta.class);
		when(customizeItem.getItemMeta()).thenReturn(customizeItemMeta);
		when(serverProvider.createItemStack(Material.CRAFTING_TABLE, 1)).thenReturn(customizeItem);
		when(shop.getSize()).thenReturn(9);
		when(shop.createVillagerInventory(9, "Editor")).thenReturn(inv);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(serverProvider, skullService);
		handler.setup(shop, 1);
		handler.setOccupied(true, 0);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot 1")).thenReturn(stack);
		handler.setOccupied(false, 0);
		verify(inv).setItem(0, stack);
	}

	@Test
	public void handleInventoryClickTestFree() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ShopSlotEditorHandler slotHandler = mock(ShopSlotEditorHandler.class);
		ItemStack item = mock(ItemStack.class);
		ItemMeta customizeItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		when(shop.getSize()).thenReturn(9);
		when(item.getItemMeta()).thenReturn(customizeItemMeta);
		when(shop.createVillagerInventory(9, "Editor")).thenReturn(inv);
		when(serverProvider.createItemStack(Material.CRAFTING_TABLE, 1)).thenReturn(item);
		when(skullService.getSkullWithName(eq(SkullTextureEnum.SLOTEMPTY), anyString())).thenReturn(item);
		assertDoesNotThrow(() -> when(shop.getSlotEditorHandler(0)).thenReturn(slotHandler));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(serverProvider, skullService);
		handler.setup(shop, 1);
		handler.handleInventoryClick(ClickType.RIGHT, 0, ecoPlayer);
		assertDoesNotThrow(() -> verify(slotHandler).openInventory(player));
	}

	@Test
	public void handleInventoryClickTestCustomizeClick() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		ItemStack item = mock(ItemStack.class);
		ItemMeta customizeItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		when(item.getItemMeta()).thenReturn(customizeItemMeta);
		when(shop.getSize()).thenReturn(9);
		when(shop.createVillagerInventory(9, "Editor")).thenReturn(inv);
		when(serverProvider.createItemStack(Material.CRAFTING_TABLE, 1)).thenReturn(item);
		when(skullService.getSkullWithName(eq(SkullTextureEnum.SLOTEMPTY), anyString())).thenReturn(item);
		when(shop.getCustomizeGuiHandler()).thenReturn(customizer);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(serverProvider, skullService);
		handler.setup(shop, 1);
		handler.handleInventoryClick(ClickType.RIGHT, 8, ecoPlayer);
		assertDoesNotThrow(() -> verify(customizer).openInventory(player));
	}

	@Test
	public void handleInventoryClickTestWithException() throws ShopsystemException {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ShopSlotEditorHandler slotHandler = mock(ShopSlotEditorHandler.class);
		ItemStack item = mock(ItemStack.class);
		ItemMeta customizeItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		when(shop.getSize()).thenReturn(9);
		when(item.getItemMeta()).thenReturn(customizeItemMeta);
		when(shop.createVillagerInventory(9, "Editor")).thenReturn(inv);
		when(serverProvider.createItemStack(Material.CRAFTING_TABLE, 1)).thenReturn(item);
		doThrow(ShopsystemException.class).when(shop).getSlotEditorHandler(0);
		ShopEditorHandler handler = new ShopEditorHandlerImpl(serverProvider, skullService);
		handler.setup(shop, 1);
		assertDoesNotThrow(() -> handler.handleInventoryClick(ClickType.RIGHT, 0, ecoPlayer));
		verifyNoInteractions(slotHandler);
		
	}
}
