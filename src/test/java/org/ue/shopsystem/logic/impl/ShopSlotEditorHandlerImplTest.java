package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.shopsystem.logic.api.ShopsystemException;

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
		ItemStack k_off = mock(ItemStack.class);
		ItemStack save = mock(ItemStack.class);
		ItemStack exit = mock(ItemStack.class);
		ItemStack remove = mock(ItemStack.class);
		ItemMeta saveMeta = mock(ItemMeta.class);
		ItemMeta exitMeta = mock(ItemMeta.class);
		ItemMeta removeMeta = mock(ItemMeta.class);
		when(skullService.getSkullWithName(SkullTextureEnum.K_OFF, "factor off")).thenReturn(k_off);
		when(shop.getName()).thenReturn("myshop");
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(save);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(exit);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(remove);
		when(save.getItemMeta()).thenReturn(saveMeta);
		when(exit.getItemMeta()).thenReturn(exitMeta);
		when(remove.getItemMeta()).thenReturn(removeMeta);
		when(shop.createVillagerInventory(27, "myshop-SlotEditor")).thenReturn(inv);
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
		ItemStack k_off = mock(ItemStack.class);
		ItemStack save = mock(ItemStack.class);
		ItemStack exit = mock(ItemStack.class);
		ItemStack remove = mock(ItemStack.class);
		ItemMeta saveMeta = mock(ItemMeta.class);
		ItemMeta exitMeta = mock(ItemMeta.class);
		ItemMeta removeMeta = mock(ItemMeta.class);
		when(skullService.getSkullWithName(SkullTextureEnum.K_OFF, "factor off")).thenReturn(k_off);
		when(shop.getName()).thenReturn("myshop");
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(save);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(exit);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(remove);
		when(save.getItemMeta()).thenReturn(saveMeta);
		when(exit.getItemMeta()).thenReturn(exitMeta);
		when(remove.getItemMeta()).thenReturn(removeMeta);
		when(shop.createVillagerInventory(27, "myshop-SlotEditor")).thenReturn(inv);
		return new ShopSlotEditorHandlerImpl(serverProvider, messageWrapper, validationHandler, skullService, shop);
	}

	@Test
	public void changeInventoryNameTest() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		Inventory inv = mock(Inventory.class);
		Inventory oldInv = handler.getSlotEditorInventory();
		ItemStack[] contents = new ItemStack[27];
		when(oldInv.getContents()).thenReturn(contents);
		when(shop.createVillagerInventory(27, "catch-SlotEditor")).thenReturn(inv);
		handler.changeInventoryName("catch");
		verify(inv).setContents(contents);
		assertEquals(inv, handler.getSlotEditorInventory());
	}

	@Test
	public void setSelectedSlotTestEmpty() throws ShopsystemException {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();

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
		when(skullService.getSkullWithName(SkullTextureEnum.PLUS, "plus")).thenReturn(plus);
		when(skullService.getSkullWithName(SkullTextureEnum.TWENTY, "twenty")).thenReturn(twenty);
		when(skullService.getSkullWithName(SkullTextureEnum.TEN, "ten")).thenReturn(ten);
		when(skullService.getSkullWithName(SkullTextureEnum.ONE, "one")).thenReturn(one);
		when(skullService.getSkullWithName(SkullTextureEnum.SELL, "sellprice")).thenReturn(sell);
		when(skullService.getSkullWithName(SkullTextureEnum.BUY, "buyprice")).thenReturn(buy);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(empty);
		when(plus.getItemMeta()).thenReturn(plusMeta);
		when(twenty.getItemMeta()).thenReturn(twentyMeta);
		when(ten.getItemMeta()).thenReturn(tenMeta);
		when(one.getItemMeta()).thenReturn(oneMeta);
		when(sell.getItemMeta()).thenReturn(sellMeta);
		when(buy.getItemMeta()).thenReturn(buyMeta);
		when(empty.getItemMeta()).thenReturn(emptyMeta);
		doThrow(ShopsystemException.class).when(shop).getShopItem(1);

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
		verify(inv).setItem(18, sell);
		verify(inv).setItem(9, buy);
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

		ShopItemImpl shopItem = mock(ShopItemImpl.class);
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
		when(skullService.getSkullWithName(SkullTextureEnum.PLUS, "plus")).thenReturn(plus);
		when(skullService.getSkullWithName(SkullTextureEnum.TWENTY, "twenty")).thenReturn(twenty);
		when(skullService.getSkullWithName(SkullTextureEnum.TEN, "ten")).thenReturn(ten);
		when(skullService.getSkullWithName(SkullTextureEnum.ONE, "one")).thenReturn(one);
		when(skullService.getSkullWithName(SkullTextureEnum.SELL, "sellprice")).thenReturn(sell);
		when(skullService.getSkullWithName(SkullTextureEnum.BUY, "buyprice")).thenReturn(buy);
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
		verify(inv).setItem(18, sell);
		verify(inv).setItem(9, buy);
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
		ItemStack selectedItem = mock(ItemStack.class);
		Player player = mock(Player.class);
		when(handler.getSlotEditorInventory().getItem(0)).thenReturn(selectedItem);
		when(event.getCurrentItem()).thenReturn(selectedItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(0);

		handler.handleSlotEditor(event);

		verify(player, never()).sendMessage(anyString());
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestPlusAmountSwitchClick() {
		testPlusMinusSwitch(2, "plus", "minus", SkullTextureEnum.MINUS);
	}

	@Test
	public void handleSlotEditorTestPlusBuyPriceSwitchClick() {
		testPlusMinusSwitch(20, "plus", "minus", SkullTextureEnum.MINUS);
	}

	@Test
	public void handleSlotEditorTestPlusSellPriceSwitchClick() {
		testPlusMinusSwitch(11, "plus", "minus", SkullTextureEnum.MINUS);
	}

	@Test
	public void handleSlotEditorTestMinusPlusAmountSwitchClick() {
		testPlusMinusSwitch(2, "minus", "plus", SkullTextureEnum.PLUS);
	}

	@Test
	public void handleSlotEditorTestMinusSellPriceSwitchClick() {
		testPlusMinusSwitch(11, "minus", "plus", SkullTextureEnum.PLUS);
	}

	@Test
	public void handleSlotEditorTestMinusBuyPriceSwitchClick() {
		testPlusMinusSwitch(20, "minus", "plus", SkullTextureEnum.PLUS);
	}

	@Test
	public void handleSlotEditorTestFactorOffSellPriceSwitchClick() {
		testFactorSwitch(12, "factor off", "factor on", SkullTextureEnum.K_ON);
	}

	@Test
	public void handleSlotEditorTestFactorOffBuyPriceSwitchClick() {
		testFactorSwitch(21, "factor off", "factor on", SkullTextureEnum.K_ON);
	}

	@Test
	public void handleSlotEditorTestFactorOnSellPriceSwitchClick() {
		testFactorSwitch(12, "factor on", "factor off", SkullTextureEnum.K_OFF);
	}

	@Test
	public void handleSlotEditorTestFactorOnBuyPriceSwitchClick() {
		testFactorSwitch(21, "factor on", "factor off", SkullTextureEnum.K_OFF);
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
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack currentItemClone = mock(ItemStack.class);
		Player player = mock(Player.class);
		when(handler.getSlotEditorInventory().getItem(0)).thenReturn(selectedItem);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(currentItem.getType()).thenReturn(Material.STONE);
		when(currentItem.clone()).thenReturn(currentItemClone);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(0);

		handler.handleSlotEditor(event);

		verify(currentItemClone).setAmount(1);
		verify(handler.getSlotEditorInventory()).setItem(0, currentItemClone);
		verify(player, never()).sendMessage(anyString());
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestSelectItemClickSpawner() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack currentItem = mock(ItemStack.class);
		Player player = mock(Player.class);
		when(handler.getSlotEditorInventory().getItem(0)).thenReturn(selectedItem);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(currentItem.getType()).thenReturn(Material.SPAWNER);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(0);

		handler.handleSlotEditor(event);

		verify(currentItem, never()).clone();
		verify(player, never()).sendMessage(anyString());
		verify(event).setCancelled(true);
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
	public void handleSlotEditorTestSaveChangesNew() throws ShopsystemException {
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
		when(selectedItem.getType()).thenReturn(Material.STONE);
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
		when(shop.getShopItem(0)).thenThrow(ShopsystemException.class);
		when(messageWrapper.getString("added", "stone")).thenReturn("my message");

		handler.handleSlotEditor(event);

		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(3.0, 0.0));
		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		assertDoesNotThrow(() -> verify(shop).addShopItem(0, 3.0, 0.0, selectedItem));
		verify(player).sendMessage("my message");
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestSaveChangesNewNoSelectedItem() throws ShopsystemException {
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
		when(shop.getShopItem(0)).thenThrow(ShopsystemException.class);

		handler.handleSlotEditor(event);

		verify(validationHandler).checkForPricesGreaterThenZero(3.0, 0.0);
		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		assertDoesNotThrow(() -> verify(shop, never()).addShopItem(0, 3.0, 0.0, selectedItem));
		verify(player, never()).sendMessage(anyString());
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestSaveChangesNewWithNoPrices() throws ShopsystemException {
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
		ShopsystemException e = mock(ShopsystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(validationHandler).checkForPricesGreaterThenZero(0.0, 0.0);

		handler.handleSlotEditor(event);

		assertDoesNotThrow(() -> verify(shop, never()).getShopItem(0));
		assertDoesNotThrow(() -> verify(shop, never()).addShopItem(0, 0.0, 0.0, selectedItem));
		verify(player).sendMessage("my error message");
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestSaveChangesEditWithAllChanged() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack buyPriceItem = mock(ItemStack.class);
		ItemStack sellPriceItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		ItemMeta buyPriceItemMeta = mock(ItemMeta.class);
		ItemMeta sellPriceItemMeta = mock(ItemMeta.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(selectedItem.getAmount()).thenReturn(20);
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
		when(buyPriceItemMeta.getLore()).thenReturn(Arrays.asList(ChatColor.GOLD + "Price: 1.5"));
		assertDoesNotThrow(() -> when(shop.getShopItem(0)).thenReturn(shopItem));
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItemStack.isSimilar(selectedItem)).thenReturn(true);
		assertDoesNotThrow(() -> when(shop.editShopItem(0, 20, 3.0, 1.5)).thenReturn("edit message"));

		handler.handleSlotEditor(event);

		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(3.0, 1.5));
		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		verify(player).sendMessage("edit message");
		verify(event).setCancelled(true);
	}
	
	@Test
	public void handleSlotEditorTestSaveChangesEditWithNothingChanged() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack buyPriceItem = mock(ItemStack.class);
		ItemStack sellPriceItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		ItemMeta buyPriceItemMeta = mock(ItemMeta.class);
		ItemMeta sellPriceItemMeta = mock(ItemMeta.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(selectedItem.getAmount()).thenReturn(20);
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
		when(buyPriceItemMeta.getLore()).thenReturn(Arrays.asList(ChatColor.GOLD + "Price: 1.5"));
		assertDoesNotThrow(() -> when(shop.getShopItem(0)).thenReturn(shopItem));
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItem.getSellPrice()).thenReturn(3.0);
		when(shopItem.getBuyPrice()).thenReturn(1.5);
		when(shopItem.getAmount()).thenReturn(20);
		when(shopItemStack.isSimilar(selectedItem)).thenReturn(true);
		assertDoesNotThrow(() -> when(shop.editShopItem(0, null, null, null)).thenReturn("edit message"));

		handler.handleSlotEditor(event);

		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(3.0, 1.5));
		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		verify(player).sendMessage("edit message");
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestSaveChangesOtherItem() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack buyPriceItem = mock(ItemStack.class);
		ItemStack sellPriceItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		ItemMeta buyPriceItemMeta = mock(ItemMeta.class);
		ItemMeta sellPriceItemMeta = mock(ItemMeta.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(selectedItem.getType()).thenReturn(Material.STONE);
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
		assertDoesNotThrow(() -> when(shop.getShopItem(0)).thenReturn(shopItem));
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItemStack.isSimilar(selectedItem)).thenReturn(false);
		when(shopItemStack.getType()).thenReturn(Material.STICK);
		when(messageWrapper.getString("removed", "stick")).thenReturn("my message remove");
		when(messageWrapper.getString("added", "stone")).thenReturn("my message add");

		handler.handleSlotEditor(event);

		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(3.0, 0.0));
		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		assertDoesNotThrow(() -> verify(shop).addShopItem(0, 3.0, 0.0, selectedItem));
		verify(player).sendMessage("my message remove");
		verify(player).sendMessage("my message add");
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestSaveChangesOtherItemSpawner() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack buyPriceItem = mock(ItemStack.class);
		ItemStack sellPriceItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		ItemMeta shopItemStackMeta = mock(ItemMeta.class);
		ItemMeta buyPriceItemMeta = mock(ItemMeta.class);
		ItemMeta sellPriceItemMeta = mock(ItemMeta.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(selectedItem.getType()).thenReturn(Material.STONE);
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
		assertDoesNotThrow(() -> when(shop.getShopItem(0)).thenReturn(shopItem));
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItemStack.isSimilar(selectedItem)).thenReturn(false);
		when(shopItemStack.getType()).thenReturn(Material.SPAWNER);
		when(shopItemStack.getItemMeta()).thenReturn(shopItemStackMeta);
		when(shopItemStackMeta.getDisplayName()).thenReturn("cow");
		when(messageWrapper.getString("removed", "cow")).thenReturn("my message remove");
		when(messageWrapper.getString("added", "stone")).thenReturn("my message add");

		handler.handleSlotEditor(event);

		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(3.0, 0.0));
		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		assertDoesNotThrow(() -> verify(shop).addShopItem(0, 3.0, 0.0, selectedItem));
		verify(player).sendMessage("my message remove");
		verify(player).sendMessage("my message add");
		verify(event).setCancelled(true);
	}

	@Test
	public void handleSlotEditorTestRemoveItem() {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(handler.getSlotEditorInventory().getItem(0)).thenReturn(selectedItem);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(26);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn("remove item");
		assertDoesNotThrow(() -> when(shop.getShopItem(0)).thenReturn(shopItem));
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItemStack.getType()).thenReturn(Material.STONE);
		when(messageWrapper.getString("removed", "stone")).thenReturn("my message remove");

		handler.handleSlotEditor(event);

		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		verify(player).sendMessage("my message remove");
		assertDoesNotThrow(() -> verify(shop).removeShopItem(0));
		verify(event).setCancelled(true);
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

	private void testPlusMinusSwitch(int slot, String before, String after, SkullTextureEnum skullTypeAfter) {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack skull = mock(ItemStack.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(handler.getSlotEditorInventory().getItem(0)).thenReturn(selectedItem);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(slot);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn(before);
		when(skullService.getSkullWithName(skullTypeAfter, after)).thenReturn(skull);

		handler.handleSlotEditor(event);

		verify(handler.getSlotEditorInventory()).setItem(slot, skull);
		verify(handler.getSlotEditorInventory(), times(1)).setItem(anyInt(), eq(skull));
		verify(player, never()).sendMessage(anyString());
		verify(event).setCancelled(true);
	}

	private void testFactorSwitch(int slot, String before, String after, SkullTextureEnum skullType) {
		ShopSlotEditorHandlerImpl handler = createSlotEditorHandler();
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack skull = mock(ItemStack.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(handler.getSlotEditorInventory().getItem(0)).thenReturn(selectedItem);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getSlot()).thenReturn(slot);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn(before);
		when(skullService.getSkullWithName(skullType, after)).thenReturn(skull);

		handler.handleSlotEditor(event);

		verify(handler.getSlotEditorInventory()).setItem(slot, skull);
		verify(handler.getSlotEditorInventory(), times(1)).setItem(anyInt(), eq(skull));
		verify(player, never()).sendMessage(anyString());
		verify(event).setCancelled(true);
	}
}
