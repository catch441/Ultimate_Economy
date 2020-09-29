package com.ue.shopsystem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.shopsystem.logic.api.AbstractShop;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.impl.AbstractShopImpl;
import com.ue.shopsystem.logic.impl.AdminshopManagerImpl;
import com.ue.shopsystem.logic.impl.ShopSlotEditorHandlerImpl;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

@ExtendWith(MockitoExtension.class)
public class ShopSlotEditorHandlerImplTest {

	@Mock
	CustomSkullService skullService;
	@Mock
	ShopValidationHandler validationHandler;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ServerProvider serverProvider;
	@Mock
	AbstractShop shop;

	@Test
	public void constructorSetupTest() {
		Inventory inv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack k_off = mock(ItemStack.class);
		ItemStack save = mock(ItemStack.class);
		ItemStack exit = mock(ItemStack.class);
		ItemStack remove = mock(ItemStack.class);
		ItemMeta saveMeta = mock(ItemMeta.class);
		ItemMeta exitMeta = mock(ItemMeta.class);
		ItemMeta removeMeta = mock(ItemMeta.class);
		when(skullService.getSkullWithName("K_OFF", "factor off")).thenReturn(k_off);
		when(shop.getName()).thenReturn("myshop");
		when(shop.getShopVillager()).thenReturn(villager);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(save);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(exit);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(remove);
		when(save.getItemMeta()).thenReturn(saveMeta);
		when(exit.getItemMeta()).thenReturn(exitMeta);
		when(remove.getItemMeta()).thenReturn(removeMeta);
		when(serverProvider.createInventory(villager, 27, "myshop-SlotEditor")).thenReturn(inv);
		new ShopSlotEditorHandlerImpl(serverProvider, messageWrapper, validationHandler, skullService, shop);
		verify(inv).setItem(12, k_off);
		verify(inv).setItem(21, k_off);
		verify(inv).setItem(8, save);
		verify(inv).setItem(7, exit);
		verify(inv).setItem(26, remove);
		verify(saveMeta).setDisplayName(ChatColor.YELLOW + "save changes");
		verify(exitMeta).setDisplayName(ChatColor.RED + "exit without save");
		verify(removeMeta).setDisplayName(ChatColor.RED + "remove item");
		verifyNoInteractions(k_off);
	}

	private ShopSlotEditorHandlerImpl createSlotEditorHandler() {
		Inventory inv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack k_off = mock(ItemStack.class);
		ItemStack save = mock(ItemStack.class);
		ItemStack exit = mock(ItemStack.class);
		ItemStack remove = mock(ItemStack.class);
		ItemMeta saveMeta = mock(ItemMeta.class);
		ItemMeta exitMeta = mock(ItemMeta.class);
		ItemMeta removeMeta = mock(ItemMeta.class);
		when(skullService.getSkullWithName("K_OFF", "factor off")).thenReturn(k_off);
		when(shop.getName()).thenReturn("myshop");
		when(shop.getShopVillager()).thenReturn(villager);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(save);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(exit);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(remove);
		when(save.getItemMeta()).thenReturn(saveMeta);
		when(exit.getItemMeta()).thenReturn(exitMeta);
		when(remove.getItemMeta()).thenReturn(removeMeta);
		when(serverProvider.createInventory(villager, 27, "myshop-SlotEditor")).thenReturn(inv);
		return new ShopSlotEditorHandlerImpl(serverProvider, messageWrapper, validationHandler, skullService, shop);
	}

	@Test
	public void changeInventoryNameTest() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		Inventory inv = mock(Inventory.class);
		Inventory oldInv = handler.getSlotEditorInventory();
		ItemStack[] contents = new ItemStack[27];
		when(oldInv.getContents()).thenReturn(contents);
		when(serverProvider.createInventory(shop.getShopVillager(), 27, "kth-SlotEditor")).thenReturn(inv);
		handler.changeInventoryName("kth");
		verify(inv).setContents(contents);
		assertEquals(inv, handler.getSlotEditorInventory());
	}

	@Test
	public void setSelectedSlotTestEmpty() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		assertDoesNotThrow(() -> when(validationHandler.isSlotEmpty(1, shop.getShopInventory(), 1)).thenReturn(true));

		ItemStack plus = mock(ItemStack.class);
		ItemStack twenty = mock(ItemStack.class);
		ItemStack ten = mock(ItemStack.class);
		ItemStack one = mock(ItemStack.class);
		ItemStack sell = mock(ItemStack.class);
		ItemStack buy = mock(ItemStack.class);
		ItemStack empty = mock(ItemStack.class);
		ItemMeta plusMeta = mock(ItemMeta.class);
		ItemMeta twentyMeta = mock(ItemMeta.class);
		ItemMeta tenMeta = mock(ItemMeta.class);
		ItemMeta oneMeta = mock(ItemMeta.class);
		ItemMeta sellMeta = mock(ItemMeta.class);
		ItemMeta buyMeta = mock(ItemMeta.class);
		ItemMeta emptyMeta = mock(ItemMeta.class);
		when(skullService.getSkullWithName("PLUS", "plus")).thenReturn(plus);
		when(skullService.getSkullWithName("TWENTY", "twenty")).thenReturn(twenty);
		when(skullService.getSkullWithName("TEN", "ten")).thenReturn(ten);
		when(skullService.getSkullWithName("ONE", "one")).thenReturn(one);
		when(skullService.getSkullWithName("SELL", "sellprice")).thenReturn(sell);
		when(skullService.getSkullWithName("BUY", "buyprice")).thenReturn(buy);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(empty);
		when(plus.getItemMeta()).thenReturn(plusMeta);
		when(twenty.getItemMeta()).thenReturn(twentyMeta);
		when(ten.getItemMeta()).thenReturn(tenMeta);
		when(one.getItemMeta()).thenReturn(oneMeta);
		when(sell.getItemMeta()).thenReturn(sellMeta);
		when(buy.getItemMeta()).thenReturn(buyMeta);
		when(empty.getItemMeta()).thenReturn(emptyMeta);

		assertDoesNotThrow(() -> handler.setSelectedSlot(1));

		List<String> lore = Arrays.asList(ChatColor.GOLD + "Price: 0.0");
		verify(plusMeta, times(2)).setLore(lore);
		verify(twentyMeta, times(2)).setLore(lore);
		verify(tenMeta, times(2)).setLore(lore);
		verify(oneMeta, times(2)).setLore(lore);
		verify(emptyMeta).setDisplayName(ChatColor.GREEN + "select item");
		Inventory inv = handler.getSlotEditorInventory();
		verify(inv).setItem(2, plus);
		verify(inv).setItem(11, plus);
		verify(inv).setItem(20, plus);
		verify(inv).setItem(6, twenty);
		verify(inv).setItem(15, twenty);
		verify(inv).setItem(24, twenty);
		verify(inv).setItem(5, ten);
		verify(inv).setItem(14, ten);
		verify(inv).setItem(23, ten);
		verify(inv).setItem(4, one);
		verify(inv).setItem(13, one);
		verify(inv).setItem(22, one);
		verify(inv).setItem(9, sell);
		verify(inv).setItem(18, buy);
		verify(inv).setItem(0, empty);
		verify(plus, times(2)).setItemMeta(plusMeta);
		verify(twenty, times(2)).setItemMeta(twentyMeta);
		verify(ten, times(2)).setItemMeta(tenMeta);
		verify(one, times(2)).setItemMeta(oneMeta);
		verify(sell).setItemMeta(sellMeta);
		verify(buy).setItemMeta(buyMeta);
		verify(empty).setItemMeta(emptyMeta);
	}

	@Test
	public void setSelectedSlotTestFilled() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		assertDoesNotThrow(() -> when(validationHandler.isSlotEmpty(1, shop.getShopInventory(), 1)).thenReturn(false));

		ShopItem shopItem = mock(ShopItem.class);
		assertDoesNotThrow(() -> when(shop.getShopItem(1)).thenReturn(shopItem));
		when(shopItem.getSellPrice()).thenReturn(1.5);
		when(shopItem.getBuyPrice()).thenReturn(2.2);
		when(shopItem.getAmount()).thenReturn(3);
		ItemStack filled = mock(ItemStack.class);
		when(shopItem.getItemStack()).thenReturn(filled);

		ItemStack plus = mock(ItemStack.class);
		ItemStack twenty = mock(ItemStack.class);
		ItemStack ten = mock(ItemStack.class);
		ItemStack one = mock(ItemStack.class);
		ItemStack sell = mock(ItemStack.class);
		ItemStack buy = mock(ItemStack.class);
		ItemMeta plusMeta = mock(ItemMeta.class);
		ItemMeta twentyMeta = mock(ItemMeta.class);
		ItemMeta tenMeta = mock(ItemMeta.class);
		ItemMeta oneMeta = mock(ItemMeta.class);
		ItemMeta sellMeta = mock(ItemMeta.class);
		ItemMeta buyMeta = mock(ItemMeta.class);
		when(skullService.getSkullWithName("PLUS", "plus")).thenReturn(plus);
		when(skullService.getSkullWithName("TWENTY", "twenty")).thenReturn(twenty);
		when(skullService.getSkullWithName("TEN", "ten")).thenReturn(ten);
		when(skullService.getSkullWithName("ONE", "one")).thenReturn(one);
		when(skullService.getSkullWithName("SELL", "sellprice")).thenReturn(sell);
		when(skullService.getSkullWithName("BUY", "buyprice")).thenReturn(buy);
		when(plus.getItemMeta()).thenReturn(plusMeta);
		when(twenty.getItemMeta()).thenReturn(twentyMeta);
		when(ten.getItemMeta()).thenReturn(tenMeta);
		when(one.getItemMeta()).thenReturn(oneMeta);
		when(sell.getItemMeta()).thenReturn(sellMeta);
		when(buy.getItemMeta()).thenReturn(buyMeta);

		assertDoesNotThrow(() -> handler.setSelectedSlot(1));

		List<String> buyLore = Arrays.asList(ChatColor.GOLD + "Price: 2.2");
		List<String> sellLore = Arrays.asList(ChatColor.GOLD + "Price: 1.5");
		verify(plusMeta).setLore(buyLore);
		verify(twentyMeta).setLore(buyLore);
		verify(tenMeta).setLore(buyLore);
		verify(oneMeta).setLore(buyLore);
		verify(plusMeta).setLore(sellLore);
		verify(twentyMeta).setLore(sellLore);
		verify(tenMeta).setLore(sellLore);
		verify(oneMeta).setLore(sellLore);
		Inventory inv = handler.getSlotEditorInventory();
		verify(inv).setItem(2, plus);
		verify(inv).setItem(11, plus);
		verify(inv).setItem(20, plus);
		verify(inv).setItem(6, twenty);
		verify(inv).setItem(15, twenty);
		verify(inv).setItem(24, twenty);
		verify(inv).setItem(5, ten);
		verify(inv).setItem(14, ten);
		verify(inv).setItem(23, ten);
		verify(inv).setItem(4, one);
		verify(inv).setItem(13, one);
		verify(inv).setItem(22, one);
		verify(inv).setItem(9, sell);
		verify(inv).setItem(18, buy);
		verify(inv).setItem(0, filled);
		verify(plus, times(2)).setItemMeta(plusMeta);
		verify(twenty, times(2)).setItemMeta(twentyMeta);
		verify(ten, times(2)).setItemMeta(tenMeta);
		verify(one, times(2)).setItemMeta(oneMeta);
		verify(sell).setItemMeta(sellMeta);
		verify(buy).setItemMeta(buyMeta);

		verify(filled).setAmount(3);
	}

	@Test
	public void handleSlotEditorTestSelectedItemClick() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();

		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		Player player = mock(Player.class);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(0);

		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
			player.closeInventory();
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 2, ClickType.LEFT,
				InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
	}

	// TODO sollte eigentlich click auf plus sein
	@Test
	public void handleSlotEditorTestPlusOneAmountClickv() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack plus = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemMeta plusMeta = mock(ItemMeta.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(handler.getSlotEditorInventory().getItem(0)).thenReturn(selectedItem);
		when(selectedItem.getAmount()).thenReturn(1);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(4);
		when(handler.getSlotEditorInventory().getItem(2)).thenReturn(plus);
		when(plus.getItemMeta()).thenReturn(plusMeta);
		when(plusMeta.getDisplayName()).thenReturn("plus");
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn("one");

		handler.handleSlotEditor(event);

		verify(selectedItem).setAmount(2);
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void handleSlotEditorTestPlusClick2() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT,
				InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(11).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(MINUS,
				slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("minus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(20).getType());
		assertEquals(PLUS,
				slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(PLUS,
				slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestPlusClick3() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT,
				InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(PLUS,
				slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(PLUS,
				slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(MINUS,
				slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("minus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestMinusClick1() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 2, ClickType.LEFT,
				InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(PLUS,
				slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(11).getType());
		assertEquals(PLUS,
				slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(20).getType());
		assertEquals(PLUS,
				slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestMinusClick2() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 11, ClickType.LEFT,
				InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(11).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(PLUS,
				slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(20).getType());
		assertEquals(PLUS,
				slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(PLUS,
				slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestMinusClick3() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 20, ClickType.LEFT,
				InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(PLUS,
				slotEditor.getItem(2).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(2).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(PLUS,
				slotEditor.getItem(11).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(11).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(2).getType());
		assertEquals(PLUS,
				slotEditor.getItem(20).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("plus", slotEditor.getItem(20).getItemMeta().getDisplayName());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestFactorOffClick1() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 12, ClickType.LEFT,
				InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(12).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(K_ON,
				slotEditor.getItem(12).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("factor on", slotEditor.getItem(12).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(21).getType());
		assertEquals(K_OFF,
				slotEditor.getItem(21).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(21).getItemMeta().getDisplayName());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestFactorOffClick2() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 21, ClickType.LEFT,
				InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(21).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(K_ON,
				slotEditor.getItem(21).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("factor on", slotEditor.getItem(21).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(12).getType());
		assertEquals(K_OFF,
				slotEditor.getItem(12).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(12).getItemMeta().getDisplayName());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestFactorOnClick1() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 12, ClickType.LEFT,
				InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(12).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(K_OFF,
				slotEditor.getItem(12).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(12).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(21).getType());
		assertEquals(K_OFF,
				slotEditor.getItem(21).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(21).getItemMeta().getDisplayName());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestFactorOnClick2() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, handler.getSlotEditorInventory());
		InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 21, ClickType.LEFT,
				InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(21).getType());
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		assertEquals(K_OFF,
				slotEditor.getItem(21).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(21).getItemMeta().getDisplayName());
		assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(12).getType());
		assertEquals(K_OFF,
				slotEditor.getItem(12).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
		assertEquals("factor off", slotEditor.getItem(12).getItemMeta().getDisplayName());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestPlusOneAmountClick() {
		testAmount(4, 1, 2, "plus", "one");
	}

	@Test
	public void handleSlotEditorTestPlusOneSellPriceClick() {
		testSellPrice("factor off", 13, 2.0, 3.0, "plus", "one");
	}

	@Test
	public void handleSlotEditorTestPlusOneBuyPriceClick() {
		testBuyPrice("factor off", 22, 2, 3, "plus", "one");
	}

	@Test
	public void handleSlotEditorTestMinusOneAmountClick() {
		testAmount(4, 2, 1, "minus", "one");
	}

	@Test
	public void handleSlotEditorTestMinusOneSellPriceClick() {
		testSellPrice("factor off", 13, 3.0, 2.0, "minus", "one");
	}

	@Test
	public void handleSlotEditorTestMinusOneBuyPriceClick() {
		testBuyPrice("factor off", 22, 3, 2, "minus", "one");
	}

	@Test
	public void handleSlotEditorTestPlusTenAmountClick() {
		testAmount(5, 2, 12, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestPlusTenSellPriceClick() {
		testSellPrice("factor off", 14, 3.5, 13.5, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestPlusTenBuyPriceClick() {
		testBuyPrice("factor off", 23, 1.5, 11.5, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestMinusTenAmountClick() {
		testAmount(5, 20, 10, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestMinusTenSellPriceClick() {
		testSellPrice("factor off", 14, 13.5, 3.5, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestMinusTenBuyPriceClick() {
		testBuyPrice("factor off", 23, 11.5, 1.5, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestPlusTwentyAmountClick() {
		testAmount(6, 10, 30, "plus", "twenty");
	}

	@Test
	public void handleSlotEditorTestPlusTwentySellPriceClick() {
		testSellPrice("factor off", 15, 3.5, 23.5, "plus", "twenty");
	}

	@Test
	public void handleSlotEditorTestPlusTwentyBuyPriceClick() {
		testBuyPrice("factor off", 24, 1.5, 21.5, "plus", "twenty");
	}

	@Test
	public void handleSlotEditorTestMinusTwentyAmountClick() {
		testAmount(6, 30, 10, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestMinusTwentySellPriceClick() {
		testSellPrice("factor off", 15, 23.5, 3.5, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestMinusTwentyBuyPriceClick() {
		testBuyPrice("factor off", 24, 21.5, 1.5, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestAddMoreAmount() {
		testAmount(5, 60, 64, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestRemoveMoreAmount() {
		testAmount(5, 4, 1, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestMinusMoreSellPrice() {
		testSellPrice("factor off", 15, 13.6, 0, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestMinusMoreBuyPrice() {
		testBuyPrice("factor off", 24, 1.5, 0, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestSelectItemClick() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		ItemStack stack = new ItemStack(Material.STONE);
		stack.setAmount(20);
		player.getInventory().setItem(0, stack);
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54,
				ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.STONE, slotEditor.getItem(0).getType());
		assertEquals(1, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestSelectItemClickSpawner() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
		ItemStack stack = new ItemStack(Material.SPAWNER);
		player.getInventory().setItem(0, stack);
		InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54,
				ClickType.LEFT, InventoryAction.PICKUP_ONE);
		handler.handleSlotEditor(event);
		ChestInventoryMock slotEditor = (ChestInventoryMock) handler.getSlotEditorInventory();
		assertEquals(Material.BARRIER, slotEditor.getItem(0).getType());
		assertEquals(1, slotEditor.getItem(0).getAmount());
		player.closeInventory();
	}

	@Test
	public void handleSlotEditorTestExitWithoutSave() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(7);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn("exit without save");

		handler.handleSlotEditor(event);

		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestSaveChangesNew() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
			ItemStack stack = new ItemStack(Material.STONE);
			stack.setAmount(20);
			player.getInventory().setItem(0, stack);
			InventoryClickEvent addItem = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addItem);
			InventoryClickEvent increaseAmount = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER,
					6, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(increaseAmount);
			InventoryClickEvent addBuyPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 24,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addBuyPrice);
			InventoryClickEvent addSellPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER,
					14, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addSellPrice);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 8,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(event);
			assertEquals(1, AdminshopManagerImpl.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
			ShopItem shopItem = AdminshopManagerImpl.getAdminshopList().get(0).getItemList().get(0);
			assertEquals(21, shopItem.getAmount());
			assertEquals("20.0", String.valueOf(shopItem.getSellPrice()));
			assertEquals("10.0", String.valueOf(shopItem.getBuyPrice()));
			assertEquals(0, shopItem.getSlot());
			stack.setAmount(1);
			assertEquals(stack, shopItem.getItemStack());
			assertEquals("ItemStack{STONE x 1}", shopItem.getItemString());
			assertEquals("§6The item §astone§6 was added to the shop.", player.nextMessage());
			assertNull(player.nextMessage());
			player.closeInventory();
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void handleSlotEditorTestSaveChangesNewNoSelectedItem() throws GeneralEconomyException, ShopSystemException {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack buyPriceItem = mock(ItemStack.class);
		ItemStack sellPriceItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemMeta buyPriceItemMeta = mock(ItemMeta.class);
		ItemMeta sellPriceItemMeta = mock(ItemMeta.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(selectedItem.toString()).thenReturn("item string");
		when(selectedItem.getType()).thenReturn(Material.BARRIER);
		when(handler.getSlotEditorInventory().getItem(9)).thenReturn(buyPriceItem);
		when(handler.getSlotEditorInventory().getItem(18)).thenReturn(sellPriceItem);
		when(handler.getSlotEditorInventory().getItem(0)).thenReturn(selectedItem);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(8);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(buyPriceItem.getItemMeta()).thenReturn(buyPriceItemMeta);
		when(sellPriceItem.getItemMeta()).thenReturn(sellPriceItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn("save changes");
		when(sellPriceItemMeta.getLore()).thenReturn(Arrays.asList(ChatColor.GOLD + "Price: 3.0"));
		when(buyPriceItemMeta.getLore()).thenReturn(Arrays.asList(ChatColor.GOLD + "Price: 0.0"));
		when(shop.getItemList()).thenReturn(new ArrayList<>());
		when(shop.getShopItem(0)).thenThrow(ShopSystemException.class);

		handler.handleSlotEditor(event);

		verify(validationHandler).checkForItemDoesNotExist("item string", new ArrayList<>());
		verify(validationHandler).checkForPricesGreaterThenZero(3.0, 0.0);
		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		assertDoesNotThrow(() -> verify(shop, never()).addShopItem(0, 3.0, 0.0, selectedItem));
		verify(player, never()).sendMessage(anyString());
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestSaveChangesNewWithNoPrices() throws ShopSystemException {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack buyPriceItem = mock(ItemStack.class);
		ItemStack sellPriceItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemMeta buyPriceItemMeta = mock(ItemMeta.class);
		ItemMeta sellPriceItemMeta = mock(ItemMeta.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(handler.getSlotEditorInventory().getItem(9)).thenReturn(buyPriceItem);
		when(handler.getSlotEditorInventory().getItem(18)).thenReturn(sellPriceItem);
		when(handler.getSlotEditorInventory().getItem(0)).thenReturn(selectedItem);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(8);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(buyPriceItem.getItemMeta()).thenReturn(buyPriceItemMeta);
		when(sellPriceItem.getItemMeta()).thenReturn(sellPriceItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn("save changes");
		when(sellPriceItemMeta.getLore()).thenReturn(Arrays.asList(ChatColor.GOLD + "Price: 0.0"));
		when(buyPriceItemMeta.getLore()).thenReturn(Arrays.asList(ChatColor.GOLD + "Price: 0.0"));
		ShopSystemException e = mock(ShopSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(validationHandler).checkForPricesGreaterThenZero(0.0, 0.0);
		;

		handler.handleSlotEditor(event);

		assertDoesNotThrow(() -> verify(shop, never()).getShopItem(0));
		assertDoesNotThrow(() -> verify(shop, never()).addShopItem(0, 0.0, 0.0, selectedItem));
		verify(player).sendMessage("my error message");
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestSaveChangesEdit() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).addShopItem(0, 4.0, 19.0, new ItemStack(Material.STONE));
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
			ItemStack stack = new ItemStack(Material.STONE);
			stack.setAmount(20);
			player.getInventory().setItem(0, stack);
			InventoryClickEvent addItem = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addItem);
			InventoryClickEvent increaseAmount = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER,
					6, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(increaseAmount);
			InventoryClickEvent addBuyPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 24,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addBuyPrice);
			InventoryClickEvent addSellPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER,
					14, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addSellPrice);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 8,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(event);
			assertEquals("§6Updated §aamount §asellPrice §abuyPrice §6for item §astone", player.nextMessage());
			assertNull(player.nextMessage());
			assertEquals(1, AdminshopManagerImpl.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
			ShopItem shopItem = AdminshopManagerImpl.getAdminshopList().get(0).getItemList().get(0);
			assertEquals(21, shopItem.getAmount());
			assertEquals("39.0", String.valueOf(shopItem.getSellPrice()));
			assertEquals("14.0", String.valueOf(shopItem.getBuyPrice()));
			assertEquals(0, shopItem.getSlot());
			stack.setAmount(1);
			assertEquals(stack, shopItem.getItemStack());
			assertEquals("ItemStack{STONE x 1}", shopItem.getItemString());
			player.closeInventory();
		} catch (ShopSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void handleSlotEditorTestSaveChangesOtherItem() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).addShopItem(0, 4.0, 19.0, new ItemStack(Material.STONE));
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
			ItemStack stack = new ItemStack(Material.ACACIA_BOAT);
			stack.setAmount(1);
			player.getInventory().setItem(0, stack);
			InventoryClickEvent addItem = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addItem);
			InventoryClickEvent increaseAmount = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER,
					6, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(increaseAmount);
			InventoryClickEvent addBuyPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 24,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addBuyPrice);
			InventoryClickEvent addSellPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER,
					14, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addSellPrice);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 8,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(event);
			assertEquals("§6The item §astone§6 was removed from the shop.", player.nextMessage());
			assertEquals("§6The item §aacacia_boat§6 was added to the shop.", player.nextMessage());
			assertNull(player.nextMessage());
			assertEquals(1, AdminshopManagerImpl.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
			ShopItem shopItem = AdminshopManagerImpl.getAdminshopList().get(0).getItemList().get(0);
			assertEquals(21, shopItem.getAmount());
			assertEquals("39.0", String.valueOf(shopItem.getSellPrice()));
			assertEquals("14.0", String.valueOf(shopItem.getBuyPrice()));
			assertEquals(0, shopItem.getSlot());
			stack.setAmount(1);
			assertEquals(stack, shopItem.getItemStack());
			assertEquals("ItemStack{ACACIA_BOAT x 1}", shopItem.getItemString());
			player.closeInventory();
		} catch (ShopSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void handleSlotEditorTestSaveChangesOtherItemSpawner() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			ItemStack spawner = new ItemStack(Material.SPAWNER);
			ItemMeta meta = spawner.getItemMeta();
			meta.setDisplayName("COW");
			spawner.setItemMeta(meta);
			AdminshopManagerImpl.getAdminshopList().get(0).addShopItem(0, 4.0, 19.0, spawner);
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
			ItemStack stack = new ItemStack(Material.ACACIA_BOAT);
			stack.setAmount(1);
			player.getInventory().setItem(0, stack);
			InventoryClickEvent addItem = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 54,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addItem);
			InventoryClickEvent increaseAmount = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER,
					6, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(increaseAmount);
			InventoryClickEvent addBuyPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 24,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addBuyPrice);
			InventoryClickEvent addSellPrice = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER,
					14, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(addSellPrice);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 8,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(event);
			assertEquals("§6The spawner §acow§6 was removed from shop.", player.nextMessage());
			assertEquals("§6The item §aacacia_boat§6 was added to the shop.", player.nextMessage());
			assertNull(player.nextMessage());
			assertEquals(1, AdminshopManagerImpl.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
			ShopItem shopItem = AdminshopManagerImpl.getAdminshopList().get(0).getItemList().get(0);
			assertEquals(21, shopItem.getAmount());
			assertEquals("39.0", String.valueOf(shopItem.getSellPrice()));
			assertEquals("14.0", String.valueOf(shopItem.getBuyPrice()));
			assertEquals(0, shopItem.getSlot());
			stack.setAmount(1);
			assertEquals(stack, shopItem.getItemStack());
			assertEquals("ItemStack{ACACIA_BOAT x 1}", shopItem.getItemString());
			player.closeInventory();
		} catch (ShopSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void handleSlotEditorTestRemoveItem() {
		ShopSlotEditorHandlerImpl handler = ((AbstractShopImpl) AdminshopManagerImpl.getAdminshopList().get(0))
				.getSlotEditorHandler();
		try {
			AdminshopManagerImpl.getAdminshopList().get(0).openSlotEditor(player, 0);
			AdminshopManagerImpl.getAdminshopList().get(0).addShopItem(0, 4.0, 19.0, new ItemStack(Material.STONE));
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 26,
					ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleSlotEditor(event);
			assertEquals(0, AdminshopManagerImpl.getAdminshopList().get(0).getItemList().size());
			ChestInventoryMock editor = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop-Editor", editor.getName());
			assertEquals("§6The item §astone§6 was removed from the shop.", player.nextMessage());
			assertNull(player.nextMessage());
		} catch (ShopSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void handleSlotEditorTestFactorMinusTwentySellPriceClick() {
		testSellPrice("factor on", 15, 20300.5, 300.5, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestFactorMinusTwentyBuyPriceClick() {
		testBuyPrice("factor on", 24, 20001.5, 1.5, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestFactorPlusTwentySellPriceClick() {
		testSellPrice("factor on", 15, 300.5, 20300.5, "plus", "twenty");
	}

	@Test
	public void handleSlotEditorTestFactorPlusTwentyBuyPriceClick() {
		testBuyPrice("factor on", 24, 1.5, 20001.5, "plus", "twenty");
	}

	@Test
	public void handleSlotEditorTestFactorMinusTenBuyPriceClick() {
		testBuyPrice("factor on", 23, 10001.5, 1.5, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestFactorMinusTenSellPriceClick() {
		testSellPrice("factor on", 14, 10300.5, 300.5, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestFactorPlusTenSellPriceClick() {
		testSellPrice("factor on", 14, 300.5, 10300.5, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestFactorPlusTenBuyPriceClick() {
		testBuyPrice("factor on", 23, 1.5, 10001.5, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestFactorMinusOneSellPriceClick() {
		testSellPrice("factor on", 13, 1300.5, 300.5, "minus", "one");
	}

	@Test
	public void handleSlotEditorTestFactorMinusOneClick2() {
		testBuyPrice("factor on", 22, 3000, 2000, "minus", "one");
	}

	@Test
	public void handleSlotEditorTestFactorPlusOneSellPriceClick() {
		testSellPrice("factor on", 13, 300.5, 1300.5, "plus", "one");
	}

	@Test
	public void handleSlotEditorTestFactorPlusOneBuyPriceClick() {
		testBuyPrice("factor on", 22, 2000, 3000, "plus", "one");
	}

	private void testAmount(int clickedSlot, int amount, int verifyAmount, String operator, String change) {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack plus = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemMeta plusMeta = mock(ItemMeta.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(handler.getSlotEditorInventory().getItem(0)).thenReturn(selectedItem);
		when(selectedItem.getAmount()).thenReturn(amount);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(clickedSlot);
		when(handler.getSlotEditorInventory().getItem(2)).thenReturn(plus);
		when(plus.getItemMeta()).thenReturn(plusMeta);
		when(plusMeta.getDisplayName()).thenReturn(operator);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn(change);

		handler.handleSlotEditor(event);

		verify(selectedItem).setAmount(verifyAmount);
		verify(player, never()).sendMessage(anyString());
		verify(event).setCancelled(true);
	}

	private void testSellPrice(String factor, int clickedSlot, double price, double verifyPrice, String operator,
			String change) {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack factorItem = mock(ItemStack.class);

		ItemStack plus = mock(ItemStack.class);
		ItemStack sellPriceItem = mock(ItemStack.class);
		ItemStack one = mock(ItemStack.class);
		ItemStack ten = mock(ItemStack.class);
		ItemStack twenty = mock(ItemStack.class);

		ItemMeta sellPriceItemMeta = mock(ItemMeta.class);
		ItemMeta plusMeta = mock(ItemMeta.class);
		ItemMeta oneMeta = mock(ItemMeta.class);
		ItemMeta tenMeta = mock(ItemMeta.class);
		ItemMeta twentyMeta = mock(ItemMeta.class);

		ItemMeta factorItemMeta = mock(ItemMeta.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		Inventory editorInv = handler.getSlotEditorInventory();
		when(editorInv.getItem(9)).thenReturn(sellPriceItem);
		when(editorInv.getItem(12)).thenReturn(factorItem);
		when(editorInv.getItem(11)).thenReturn(plus);
		when(editorInv.getItem(0)).thenReturn(null);

		when(editorInv.getItem(13)).thenReturn(one);
		when(editorInv.getItem(14)).thenReturn(ten);
		when(editorInv.getItem(15)).thenReturn(twenty);

		when(sellPriceItemMeta.getLore()).thenReturn(Arrays.asList(ChatColor.GOLD + "Price: " + price));
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(clickedSlot);
		when(plus.getItemMeta()).thenReturn(plusMeta);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(sellPriceItem.getItemMeta()).thenReturn(sellPriceItemMeta);
		when(factorItem.getItemMeta()).thenReturn(factorItemMeta);
		when(one.getItemMeta()).thenReturn(oneMeta);
		when(ten.getItemMeta()).thenReturn(tenMeta);
		when(twenty.getItemMeta()).thenReturn(twentyMeta);
		when(currentItemMeta.getDisplayName()).thenReturn(change);
		when(plusMeta.getDisplayName()).thenReturn(operator);
		when(factorItemMeta.getDisplayName()).thenReturn(factor);

		handler.handleSlotEditor(event);

		verify(sellPriceItemMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(oneMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(tenMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(twentyMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(plusMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(sellPriceItem).setItemMeta(sellPriceItemMeta);
		verify(plus).setItemMeta(plusMeta);
		verify(one).setItemMeta(oneMeta);
		verify(ten).setItemMeta(tenMeta);
		verify(twenty).setItemMeta(twentyMeta);
		verify(player, never()).sendMessage(anyString());
		verify(event).setCancelled(true);
	}

	private void testBuyPrice(String factor, int clickedSlot, double price, double verifyPrice, String operator,
			String change) {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack factorItem = mock(ItemStack.class);

		ItemStack plus = mock(ItemStack.class);
		ItemStack buyPriceItem = mock(ItemStack.class);
		ItemStack one = mock(ItemStack.class);
		ItemStack ten = mock(ItemStack.class);
		ItemStack twenty = mock(ItemStack.class);

		ItemMeta buyPriceItemMeta = mock(ItemMeta.class);
		ItemMeta plusMeta = mock(ItemMeta.class);
		ItemMeta oneMeta = mock(ItemMeta.class);
		ItemMeta tenMeta = mock(ItemMeta.class);
		ItemMeta twentyMeta = mock(ItemMeta.class);

		ItemMeta factorItemMeta = mock(ItemMeta.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		Inventory editorInv = handler.getSlotEditorInventory();
		when(editorInv.getItem(18)).thenReturn(buyPriceItem);
		when(editorInv.getItem(21)).thenReturn(factorItem);
		when(editorInv.getItem(20)).thenReturn(plus);
		when(editorInv.getItem(0)).thenReturn(null);

		when(editorInv.getItem(22)).thenReturn(one);
		when(editorInv.getItem(23)).thenReturn(ten);
		when(editorInv.getItem(24)).thenReturn(twenty);

		when(buyPriceItemMeta.getLore()).thenReturn(Arrays.asList(ChatColor.GOLD + "Price: " + price));
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(clickedSlot);
		when(plus.getItemMeta()).thenReturn(plusMeta);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(buyPriceItem.getItemMeta()).thenReturn(buyPriceItemMeta);
		when(factorItem.getItemMeta()).thenReturn(factorItemMeta);
		when(one.getItemMeta()).thenReturn(oneMeta);
		when(ten.getItemMeta()).thenReturn(tenMeta);
		when(twenty.getItemMeta()).thenReturn(twentyMeta);
		when(currentItemMeta.getDisplayName()).thenReturn(change);
		when(plusMeta.getDisplayName()).thenReturn(operator);
		when(factorItemMeta.getDisplayName()).thenReturn(factor);

		handler.handleSlotEditor(event);

		verify(buyPriceItemMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(oneMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(tenMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(twentyMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(plusMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(buyPriceItem).setItemMeta(buyPriceItemMeta);
		verify(plus).setItemMeta(plusMeta);
		verify(one).setItemMeta(oneMeta);
		verify(ten).setItemMeta(tenMeta);
		verify(twenty).setItemMeta(twentyMeta);
		verify(player, never()).sendMessage(anyString());
		verify(event).setCancelled(true);
	}
}
