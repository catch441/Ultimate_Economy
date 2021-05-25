package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;

@ExtendWith(MockitoExtension.class)
public class ShopSlotEditorHandlerImplTest {

	@Mock
	CustomSkullService skullService;
	@Mock
	ShopValidator validationHandler;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ServerProvider serverProvider;
	@Mock
	AbstractShop shop;
	@Mock
	Inventory backLink;

	@Test
	public void setupSlotEditorTest() {
		Inventory inv = mock(Inventory.class);
		ItemStack k_off = mock(ItemStack.class);
		ItemStack save = mock(ItemStack.class);
		ItemStack exit = mock(ItemStack.class);
		ItemStack remove = mock(ItemStack.class);
		ItemMeta saveMeta = mock(ItemMeta.class);
		ItemMeta exitMeta = mock(ItemMeta.class);
		ItemMeta removeMeta = mock(ItemMeta.class);
		when(skullService.getSkullWithName(SkullTextureEnum.K_OFF, "factor off")).thenReturn(k_off);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(save);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(exit);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(remove);
		when(save.getItemMeta()).thenReturn(saveMeta);
		when(exit.getItemMeta()).thenReturn(exitMeta);
		when(remove.getItemMeta()).thenReturn(removeMeta);
		when(shop.createVillagerInventory(27, "SlotEditor")).thenReturn(inv);
		ShopSlotEditorHandler handler = new ShopSlotEditorHandlerImpl(serverProvider, messageWrapper, validationHandler,
				skullService, backLink);
		handler.setupSlotEditor(shop);
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

	private ShopSlotEditorHandler createSlotEditorHandler() {
		Inventory inv = mock(Inventory.class);
		ItemStack k_off = mock(ItemStack.class);
		ItemStack save = mock(ItemStack.class);
		ItemStack exit = mock(ItemStack.class);
		ItemStack remove = mock(ItemStack.class);
		ItemMeta saveMeta = mock(ItemMeta.class);
		ItemMeta exitMeta = mock(ItemMeta.class);
		ItemMeta removeMeta = mock(ItemMeta.class);
		when(skullService.getSkullWithName(SkullTextureEnum.K_OFF, "factor off")).thenReturn(k_off);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(save);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(exit);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(remove);
		when(save.getItemMeta()).thenReturn(saveMeta);
		when(exit.getItemMeta()).thenReturn(exitMeta);
		when(remove.getItemMeta()).thenReturn(removeMeta);
		when(shop.createVillagerInventory(27, "SlotEditor")).thenReturn(inv);
		ShopSlotEditorHandler handler = new ShopSlotEditorHandlerImpl(serverProvider, messageWrapper, validationHandler,
				skullService, backLink);
		handler.setupSlotEditor(shop);
		return handler;
	}

	@Test
	public void setSelectedSlotTestEmpty() throws ShopsystemException {
		ShopSlotEditorHandler handler = createSlotEditorHandler();

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
		Inventory inv = handler.getInventory();
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
		ShopSlotEditorHandler handler = createSlotEditorHandler();

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
		Inventory inv = handler.getInventory();
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
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack selectedItemClone = mock(ItemStack.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView playerInv = mock(InventoryView.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getOpenInventory()).thenReturn(playerInv);
		when(selectedItem.clone()).thenReturn(selectedItemClone);
		when(playerInv.getItem(28)).thenReturn(selectedItem);

		handler.handleInventoryClick(ClickType.RIGHT, 28, ecoPlayer);

		verify(selectedItemClone).setAmount(1);
		verify(handler.getInventory()).setItem(0, selectedItemClone);
		verify(player, never()).sendMessage(anyString());
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
		testSellPrice("factor off", 22, 0, 1.0, "plus", "one");
	}

	@Test
	public void handleSlotEditorTestPlusOneBuyPriceClick() {
		testBuyPrice("factor off", 13, 0, 1, "plus", "one");
	}

	@Test
	public void handleSlotEditorTestMinusOneAmountClick() {
		testAmount(4, 2, 1, "minus", "one");
	}

	@Test
	public void handleSlotEditorTestMinusOneSellPriceClick() {
		testSellPrice("factor off", 22, 0, 29, "minus", "one");
	}

	@Test
	public void handleSlotEditorTestMinusOneBuyPriceClick() {
		testBuyPrice("factor off", 13, 0, 29, "minus", "one");
	}

	@Test
	public void handleSlotEditorTestPlusTenAmountClick() {
		testAmount(5, 2, 12, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestPlusTenSellPriceClick() {
		testSellPrice("factor off", 23, 0, 10, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestPlusTenBuyPriceClick() {
		testBuyPrice("factor off", 14, 0, 10, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestMinusTenAmountClick() {
		testAmount(5, 20, 10, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestMinusTenSellPriceClick() {
		testSellPrice("factor off", 23, 0, 20, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestMinusTenBuyPriceClick() {
		testBuyPrice("factor off", 14, 0, 20, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestPlusTwentyAmountClick() {
		testAmount(6, 10, 30, "plus", "twenty");
	}

	@Test
	public void handleSlotEditorTestPlusTwentySellPriceClick() {
		testSellPrice("factor off", 24, 0, 20, "plus", "twenty");
	}

	@Test
	public void handleSlotEditorTestPlusTwentyBuyPriceClick() {
		testBuyPrice("factor off", 15, 0, 20, "plus", "twenty");
	}

	@Test
	public void handleSlotEditorTestMinusTwentyAmountClick() {
		testAmount(6, 30, 10, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestMinusTwentySellPriceClick() {
		testSellPrice("factor off", 24, 0, 10, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestMinusTwentyBuyPriceClick() {
		testBuyPrice("factor off", 15, 0, 10, "minus", "twenty");
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
		testSellPrice("factor off", 24, -1, 0, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestMinusMoreBuyPrice() {
		testBuyPrice("factor off", 15, -1, 0, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestExitWithoutSave() {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		Player player = mock(Player.class);

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		handler.handleInventoryClick(ClickType.RIGHT, 7, ecoPlayer);

		verify(player).closeInventory();
		verify(player).openInventory(backLink);
	}

	@Test
	public void handleSlotEditorTestSaveChangesNew() throws ShopsystemException {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		ItemStack anyItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack selectedItemClone = mock(ItemStack.class);
		ItemMeta anyItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		InventoryView playerInv = mock(InventoryView.class);
		when(player.getOpenInventory()).thenReturn(playerInv);
		when(selectedItem.clone()).thenReturn(selectedItemClone);
		when(selectedItem.getType()).thenReturn(Material.STONE);
		when(playerInv.getItem(27)).thenReturn(selectedItem);
		when(anyItem.getItemMeta()).thenReturn(anyItemMeta);
		when(shop.getShopItem(0)).thenThrow(ShopsystemException.class);
		when(messageWrapper.getString(MessageEnum.ADDED, "stone")).thenReturn("my message");

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		// add selected item
		handler.handleInventoryClick(ClickType.RIGHT, 27, ecoPlayer);
		reset(handler.getInventory());
		when(handler.getInventory().getItem(anyInt())).thenReturn(anyItem);
		// set buy price
		handler.handleInventoryClick(ClickType.RIGHT, 14, ecoPlayer);
		// set sell price
		handler.handleInventoryClick(ClickType.RIGHT, 24, ecoPlayer);
		when(handler.getInventory().getItem(0)).thenReturn(selectedItem);
		handler.handleInventoryClick(ClickType.RIGHT, 8, ecoPlayer);

		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(20.0, 10.0));
		verify(player).closeInventory();
		verify(player).openInventory(backLink);
		assertDoesNotThrow(() -> verify(shop).addShopItem(0, 20.0, 10.0, selectedItem));
		verify(player).sendMessage("my message");
	}

	@Test
	public void handleSlotEditorTestSaveChangesNewNoSelectedItem() throws ShopsystemException {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		ItemStack anyItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemMeta anyItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);

		when(selectedItem.getType()).thenReturn(Material.BARRIER);
		when(anyItem.getItemMeta()).thenReturn(anyItemMeta);
		when(shop.getShopItem(0)).thenThrow(ShopsystemException.class);

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(handler.getInventory().getItem(anyInt())).thenReturn(anyItem);
		// set buy price
		handler.handleInventoryClick(ClickType.RIGHT, 14, ecoPlayer);
		// set sell price
		handler.handleInventoryClick(ClickType.RIGHT, 24, ecoPlayer);
		when(handler.getInventory().getItem(0)).thenReturn(selectedItem);
		handler.handleInventoryClick(ClickType.RIGHT, 8, ecoPlayer);

		verify(validationHandler).checkForPricesGreaterThenZero(20.0, 10.0);
		verify(player).closeInventory();
		verify(player).openInventory(backLink);
		assertDoesNotThrow(() -> verify(shop, never()).addShopItem(0, 20.0, 10.0, selectedItem));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void handleSlotEditorTestSaveChangesNewWithNoPrices() throws ShopsystemException {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		Player player = mock(Player.class);
		ShopsystemException e = mock(ShopsystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(validationHandler).checkForPricesGreaterThenZero(0.0, 0.0);

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		handler.handleInventoryClick(ClickType.RIGHT, 8, ecoPlayer);

		assertDoesNotThrow(() -> verify(shop, never()).getShopItem(0));
		assertDoesNotThrow(() -> verify(shop, never()).addShopItem(eq(0), eq(0.0), eq(0.0), any()));
		verify(player).sendMessage("my error message");
	}

	@Test
	public void handleSlotEditorTestSaveChangesEditWithAllChanged() {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		ItemStack anyItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		ItemMeta anyItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		when(selectedItem.getAmount()).thenReturn(20);
		when(anyItem.getItemMeta()).thenReturn(anyItemMeta);
		assertDoesNotThrow(() -> when(shop.getShopItem(0)).thenReturn(shopItem));
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItem.getSellPrice()).thenReturn(3.0);
		when(shopItem.getBuyPrice()).thenReturn(4.0);
		when(shopItemStack.isSimilar(selectedItem)).thenReturn(true);
		when(handler.getInventory().getItem(anyInt())).thenReturn(anyItem);
		assertDoesNotThrow(() -> when(shop.editShopItem(0, 20, 20.0, 10.0)).thenReturn("edit message"));

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		// set buy price
		handler.handleInventoryClick(ClickType.RIGHT, 14, ecoPlayer);
		// set sell price
		handler.handleInventoryClick(ClickType.RIGHT, 24, ecoPlayer);
		when(handler.getInventory().getItem(0)).thenReturn(selectedItem);
		handler.handleInventoryClick(ClickType.RIGHT, 8, ecoPlayer);

		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(20.0, 10.0));
		verify(player).closeInventory();
		verify(player).openInventory(backLink);
		verify(player).sendMessage("edit message");
	}

	@Test
	public void handleSlotEditorTestSaveChangesEditWithNothingChanged() {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		ItemStack anyItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		Player player = mock(Player.class);
		when(selectedItem.getAmount()).thenReturn(0);
		assertDoesNotThrow(() -> when(shop.getShopItem(0)).thenReturn(shopItem));
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItem.getSellPrice()).thenReturn(0.0);
		when(shopItem.getBuyPrice()).thenReturn(0.0);
		when(shopItemStack.isSimilar(selectedItem)).thenReturn(true);
		when(handler.getInventory().getItem(anyInt())).thenReturn(anyItem);
		assertDoesNotThrow(() -> when(shop.editShopItem(0, null, null, null)).thenReturn("edit message"));

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);

		when(handler.getInventory().getItem(0)).thenReturn(selectedItem);
		handler.handleInventoryClick(ClickType.RIGHT, 8, ecoPlayer);

		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(0.0, 0));
		verify(player).closeInventory();
		verify(player).openInventory(backLink);
		verify(player).sendMessage("edit message");
	}

	@Test
	public void handleSlotEditorTestSaveChangesOtherItem() {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		ItemStack anyItem = mock(ItemStack.class);
		ItemStack selectedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		ItemMeta anyItemMeta = mock(ItemMeta.class);
		Player player = mock(Player.class);
		InventoryView playerInv = mock(InventoryView.class);
		when(player.getOpenInventory()).thenReturn(playerInv);
		when(anyItem.getItemMeta()).thenReturn(anyItemMeta);
		assertDoesNotThrow(() -> when(shop.getShopItem(0)).thenReturn(shopItem));
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(selectedItem.getType()).thenReturn(Material.DIRT);
		when(shopItemStack.isSimilar(selectedItem)).thenReturn(false);
		when(shopItemStack.getType()).thenReturn(Material.STONE);
		when(messageWrapper.getString(MessageEnum.REMOVED, "stone")).thenReturn("my message remove");
		when(messageWrapper.getString(MessageEnum.ADDED, "dirt")).thenReturn("my message add");

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		// add selected item
		handler.handleInventoryClick(ClickType.RIGHT, 27, ecoPlayer);
		reset(handler.getInventory());
		when(handler.getInventory().getItem(anyInt())).thenReturn(anyItem);
		// set buy price
		handler.handleInventoryClick(ClickType.RIGHT, 14, ecoPlayer);
		// set sell price
		handler.handleInventoryClick(ClickType.RIGHT, 24, ecoPlayer);
		reset(player);
		when(handler.getInventory().getItem(0)).thenReturn(selectedItem);
		handler.handleInventoryClick(ClickType.RIGHT, 8, ecoPlayer);

		assertDoesNotThrow(() -> verify(shop).removeShopItem(0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(20.0, 10.0));
		verify(player).closeInventory();
		verify(player).openInventory(backLink);
		assertDoesNotThrow(() -> verify(shop).addShopItem(0, 20.0, 10.0, selectedItem));
		verify(player).sendMessage("my message remove");
		verify(player).sendMessage("my message add");
	}

	@Test
	public void handleSlotEditorTestRemoveItem() {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		ItemStack shopItemStack = mock(ItemStack.class);
		ShopItemImpl shopItem = mock(ShopItemImpl.class);
		Player player = mock(Player.class);
		assertDoesNotThrow(() -> when(shop.getShopItem(0)).thenReturn(shopItem));
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItemStack.getType()).thenReturn(Material.SPAWNER);
		when(messageWrapper.getString(MessageEnum.REMOVED, "spawner")).thenReturn("my message remove");

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		handler.handleInventoryClick(ClickType.RIGHT, 26, ecoPlayer);

		verify(player).closeInventory();
		verify(player).openInventory(backLink);
		verify(player).sendMessage("my message remove");
		assertDoesNotThrow(() -> verify(shop).removeShopItem(0));
	}

	@Test
	public void handleSlotEditorTestFactorMinusTwentySellPriceClick() {
		testSellPrice("factor on", 24, 0, 10000, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestFactorMinusTwentyBuyPriceClick() {
		testBuyPrice("factor on", 15, 0, 10000, "minus", "twenty");
	}

	@Test
	public void handleSlotEditorTestFactorPlusTwentySellPriceClick() {
		testSellPrice("factor on", 24, 0, 20000, "plus", "twenty");
	}

	@Test
	public void handleSlotEditorTestFactorPlusTwentyBuyPriceClick() {
		testBuyPrice("factor on", 15, 0, 20000, "plus", "twenty");
	}

	@Test
	public void handleSlotEditorTestFactorMinusTenBuyPriceClick() {
		testBuyPrice("factor on", 14, 0, 20000, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestFactorMinusTenSellPriceClick() {
		testSellPrice("factor on", 23, 0, 20000, "minus", "ten");
	}

	@Test
	public void handleSlotEditorTestFactorPlusTenSellPriceClick() {
		testSellPrice("factor on", 23, 0, 10000, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestFactorPlusTenBuyPriceClick() {
		testBuyPrice("factor on", 14, 0, 10000, "plus", "ten");
	}

	@Test
	public void handleSlotEditorTestFactorMinusOneSellPriceClick() {
		testSellPrice("factor on", 22, 0, 29000, "minus", "one");
	}

	@Test
	public void handleSlotEditorTestFactorMinusOneClick2() {
		testBuyPrice("factor on", 13, 0, 29000, "minus", "one");
	}

	@Test
	public void handleSlotEditorTestFactorPlusOneSellPriceClick() {
		testSellPrice("factor on", 22, 0, 1000, "plus", "one");
	}

	@Test
	public void handleSlotEditorTestFactorPlusOneBuyPriceClick() {
		testBuyPrice("factor on", 13, 0, 1000, "plus", "one");
	}

	private void testAmount(int clickedSlot, int amount, int verifyAmount, String operator, String change) {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		ItemStack selectedItem = mock(ItemStack.class);
		Player player = mock(Player.class);
		when(handler.getInventory().getItem(0)).thenReturn(selectedItem);
		when(selectedItem.getAmount()).thenReturn(amount);

		if (operator.equals("minus")) {
			handler.handleInventoryClick(ClickType.RIGHT, 2, null);
		}

		handler.handleInventoryClick(ClickType.RIGHT, clickedSlot, null);

		verify(selectedItem).setAmount(verifyAmount);
		verify(player, never()).sendMessage(anyString());
	}

	private void testSellPrice(String factor, int clickedSlot, double price, double verifyPrice, String operator,
			String change) {
		ShopSlotEditorHandler handler = createSlotEditorHandler();

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

		Player player = mock(Player.class);
		Inventory editorInv = handler.getInventory();
		when(editorInv.getItem(18)).thenReturn(sellPriceItem);
		when(editorInv.getItem(20)).thenReturn(plus);

		when(editorInv.getItem(22)).thenReturn(one);
		when(editorInv.getItem(23)).thenReturn(ten);
		when(editorInv.getItem(24)).thenReturn(twenty);

		when(plus.getItemMeta()).thenReturn(plusMeta);
		when(sellPriceItem.getItemMeta()).thenReturn(sellPriceItemMeta);
		when(one.getItemMeta()).thenReturn(oneMeta);
		when(ten.getItemMeta()).thenReturn(tenMeta);
		when(twenty.getItemMeta()).thenReturn(twentyMeta);

		if (factor.equals("factor on")) {
			handler.handleInventoryClick(ClickType.RIGHT, 21, null);
		}
		if (operator.equals("minus")) {
			if (price >= 0) {
				handler.handleInventoryClick(ClickType.RIGHT, 24, null);
				handler.handleInventoryClick(ClickType.RIGHT, 23, null);
			}
			handler.handleInventoryClick(ClickType.RIGHT, 20, null);
			reset(oneMeta);
			reset(tenMeta);
			reset(twentyMeta);
			reset(sellPriceItemMeta);
			reset(plusMeta);
		}
		handler.handleInventoryClick(ClickType.RIGHT, clickedSlot, null);

		verify(sellPriceItemMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(oneMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(tenMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(twentyMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(plusMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(player, never()).sendMessage(anyString());
	}

	private void testBuyPrice(String factor, int clickedSlot, double price, double verifyPrice, String operator,
			String change) {
		ShopSlotEditorHandler handler = createSlotEditorHandler();

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

		Player player = mock(Player.class);
		Inventory editorInv = handler.getInventory();
		when(editorInv.getItem(9)).thenReturn(buyPriceItem);
		when(editorInv.getItem(11)).thenReturn(plus);

		when(editorInv.getItem(13)).thenReturn(one);
		when(editorInv.getItem(14)).thenReturn(ten);
		when(editorInv.getItem(15)).thenReturn(twenty);

		when(plus.getItemMeta()).thenReturn(plusMeta);
		when(buyPriceItem.getItemMeta()).thenReturn(buyPriceItemMeta);
		when(one.getItemMeta()).thenReturn(oneMeta);
		when(ten.getItemMeta()).thenReturn(tenMeta);
		when(twenty.getItemMeta()).thenReturn(twentyMeta);

		if (factor.equals("factor on")) {
			handler.handleInventoryClick(ClickType.RIGHT, 12, null);
		}
		if (operator.equals("minus")) {
			if (price >= 0) {
				handler.handleInventoryClick(ClickType.RIGHT, 15, null);
				handler.handleInventoryClick(ClickType.RIGHT, 14, null);
			}
			handler.handleInventoryClick(ClickType.RIGHT, 11, null);
			reset(oneMeta);
			reset(tenMeta);
			reset(twentyMeta);
			reset(buyPriceItemMeta);
			reset(plusMeta);
		}

		handler.handleInventoryClick(ClickType.RIGHT, clickedSlot, null);

		verify(buyPriceItemMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(oneMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(tenMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(twentyMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(plusMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + verifyPrice));
		verify(player, never()).sendMessage(anyString());
	}

	private void testPlusMinusSwitch(int slot, String before, String after, SkullTextureEnum skullTypeAfter) {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		ItemStack skull = mock(ItemStack.class);
		Player player = mock(Player.class);
		when(skullService.getSkullWithName(skullTypeAfter, after)).thenReturn(skull);
		if (before.equals("minus")) {
			when(skullService.getSkullWithName(SkullTextureEnum.MINUS, "minus")).thenReturn(mock(ItemStack.class));
			handler.handleInventoryClick(ClickType.RIGHT, slot, null);
		}
		handler.handleInventoryClick(ClickType.RIGHT, slot, null);

		verify(handler.getInventory()).setItem(slot, skull);
		verify(handler.getInventory(), times(1)).setItem(anyInt(), eq(skull));
		verify(player, never()).sendMessage(anyString());
	}

	private void testFactorSwitch(int slot, String before, String after, SkullTextureEnum skullType) {
		ShopSlotEditorHandler handler = createSlotEditorHandler();
		ItemStack skull = mock(ItemStack.class);
		Player player = mock(Player.class);
		when(skullService.getSkullWithName(skullType, after)).thenReturn(skull);

		if (before.equals("factor on")) {
			when(skullService.getSkullWithName(SkullTextureEnum.K_ON, "factor on")).thenReturn(mock(ItemStack.class));
			handler.handleInventoryClick(ClickType.RIGHT, slot, null);
		}

		handler.handleInventoryClick(ClickType.RIGHT, slot, null);

		verify(handler.getInventory()).setItem(slot, skull);
		verify(handler.getInventory(), times(1)).setItem(anyInt(), eq(skull));
		verify(player, never()).sendMessage(anyString());
	}
}
