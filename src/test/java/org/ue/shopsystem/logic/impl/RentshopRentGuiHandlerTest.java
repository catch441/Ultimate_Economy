package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.reset;

import java.util.Arrays;
import java.util.List;
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
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.ShopsystemException;

@ExtendWith(MockitoExtension.class)
public class RentshopRentGuiHandlerTest {

	@Mock
	ConfigManager configManager;
	@Mock
	CustomSkullService skullService;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ServerProvider serverProvider;
	@Mock
	Rentshop shop;

	private RentshopRentGuiHandlerImpl createHandler() {
		Inventory inv = mock(Inventory.class);
		ItemStack rentItem = mock(ItemStack.class);
		ItemStack plusItem = mock(ItemStack.class);
		ItemStack oneItem = mock(ItemStack.class);
		ItemStack sevenItem = mock(ItemStack.class);
		ItemStack clockItem = mock(ItemStack.class);
		ItemMeta rentItemMeta = mock(ItemMeta.class);
		ItemMeta clockItemMeta = mock(ItemMeta.class);
		ItemMeta oneItemMeta = mock(ItemMeta.class);
		ItemMeta sevenItemMeta = mock(ItemMeta.class);
		when(sevenItem.getItemMeta()).thenReturn(sevenItemMeta);
		when(oneItem.getItemMeta()).thenReturn(oneItemMeta);
		when(clockItem.getItemMeta()).thenReturn(clockItemMeta);
		when(rentItem.getItemMeta()).thenReturn(rentItemMeta);
		when(skullService.getSkullWithName(SkullTextureEnum.PLUS, "plus")).thenReturn(plusItem);
		when(skullService.getSkullWithName(SkullTextureEnum.ONE, "one")).thenReturn(oneItem);
		when(skullService.getSkullWithName(SkullTextureEnum.SEVEN, "seven")).thenReturn(sevenItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(rentItem);
		when(serverProvider.createItemStack(Material.CLOCK, 1)).thenReturn(clockItem);
		when(shop.getName()).thenReturn("Shop#R0");
		when(shop.getRentalFee()).thenReturn(5.5);
		when(shop.createVillagerInventory(9, "Shop#R0")).thenReturn(inv);
		return new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager, skullService, configManager, shop,
				serverProvider);
	}

	@Test
	public void constructorTest() {
		Inventory inv = mock(Inventory.class);
		ItemStack rentItem = mock(ItemStack.class);
		ItemStack plusItem = mock(ItemStack.class);
		ItemStack oneItem = mock(ItemStack.class);
		ItemStack sevenItem = mock(ItemStack.class);
		ItemStack clockItem = mock(ItemStack.class);
		ItemMeta rentItemMeta = mock(ItemMeta.class);
		ItemMeta clockItemMeta = mock(ItemMeta.class);
		ItemMeta oneItemMeta = mock(ItemMeta.class);
		ItemMeta sevenItemMeta = mock(ItemMeta.class);
		when(sevenItem.getItemMeta()).thenReturn(sevenItemMeta);
		when(oneItem.getItemMeta()).thenReturn(oneItemMeta);
		when(clockItem.getItemMeta()).thenReturn(clockItemMeta);
		when(rentItem.getItemMeta()).thenReturn(rentItemMeta);
		when(skullService.getSkullWithName(SkullTextureEnum.PLUS, "plus")).thenReturn(plusItem);
		when(skullService.getSkullWithName(SkullTextureEnum.ONE, "one")).thenReturn(oneItem);
		when(skullService.getSkullWithName(SkullTextureEnum.SEVEN, "seven")).thenReturn(sevenItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(rentItem);
		when(serverProvider.createItemStack(Material.CLOCK, 1)).thenReturn(clockItem);
		when(shop.getName()).thenReturn("Shop#R0");
		when(shop.getRentalFee()).thenReturn(5.5);
		when(shop.createVillagerInventory(9, "Shop#R0")).thenReturn(inv);
		new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager, skullService, configManager, shop,
				serverProvider);

		List<String> lore = Arrays.asList("§6Duration: §a1§6 Day");
		List<String> rentLore = Arrays.asList("§6RentalFee: §a5.5");

		verify(rentItemMeta).setDisplayName("§eRent");
		verify(rentItemMeta).setLore(rentLore);
		verify(rentItem).setItemMeta(rentItemMeta);
		verify(inv).setItem(0, rentItem);

		verify(clockItemMeta).setLore(lore);
		verify(clockItem).setItemMeta(clockItemMeta);
		verify(inv).setItem(1, clockItem);
		verify(plusItem, never()).setItemMeta(any(ItemMeta.class));
		verify(inv).setItem(3, plusItem);
		verify(oneItemMeta).setLore(lore);
		verify(oneItem).setItemMeta(oneItemMeta);
		verify(inv).setItem(4, oneItem);
		verify(sevenItemMeta).setLore(lore);
		verify(sevenItem).setItemMeta(sevenItemMeta);
		verify(inv).setItem(5, sevenItem);
	}

	@Test
	public void handleRentShopGuiClickTestPlusClick() {
		RentshopRentGuiHandlerImpl handler = createHandler();
		ItemStack minusItem = mock(ItemStack.class);
		when(skullService.getSkullWithName(SkullTextureEnum.MINUS, "minus")).thenReturn(minusItem);
		handler.handleInventoryClick(ClickType.RIGHT, 3, null);
		verify(handler.getInventory()).setItem(3, minusItem);
	}

	@Test
	public void handleRentShopGuiClickTestMinusClick() {
		RentshopRentGuiHandlerImpl handler = createHandler();

		ItemStack plusItem = mock(ItemStack.class);
		when(skullService.getSkullWithName(SkullTextureEnum.PLUS, "plus")).thenReturn(plusItem);
		when(skullService.getSkullWithName(SkullTextureEnum.MINUS, "minus")).thenReturn(null);
		handler.handleInventoryClick(ClickType.RIGHT, 3, null);
		handler.handleInventoryClick(ClickType.RIGHT, 3, null);
		verify(handler.getInventory()).setItem(3, plusItem);
	}

	@Test
	public void handleRentShopGuiClickTestPlusOneClick() {
		when(configManager.getMaxRentedDays()).thenReturn(10);
		testPlusMinusDuration("Days", "Days", "plus", "one", 1, 2, 11.0, 4);
	}

	@Test
	public void handleRentShopGuiClickTestMinusOneClick() {
		when(configManager.getMaxRentedDays()).thenReturn(10);
		testPlusMinusDuration("Days", "Days", "minus", "one", 1, 7, 38.5, 4);
	}

	private void testPlusMinusDuration(String dayDaysOld, String dayDaysNew, String operation, String amount,
			int durationOld, int durationNew, double fee, int rawslot) {
		RentshopRentGuiHandlerImpl handler = createHandler();

		ItemStack loreUpdateItem = mock(ItemStack.class);
		ItemMeta loreUpdateItemMeta = mock(ItemMeta.class);
		when(loreUpdateItem.getItemMeta()).thenReturn(loreUpdateItemMeta);
		when(handler.getInventory().getItem(5)).thenReturn(loreUpdateItem);
		when(handler.getInventory().getItem(4)).thenReturn(loreUpdateItem);
		when(handler.getInventory().getItem(1)).thenReturn(loreUpdateItem);

		ItemStack loreUpdateRentItem = mock(ItemStack.class);
		ItemMeta loreUpdateRentItemMeta = mock(ItemMeta.class);
		when(loreUpdateRentItem.getItemMeta()).thenReturn(loreUpdateRentItemMeta);
		when(handler.getInventory().getItem(0)).thenReturn(loreUpdateRentItem);
		
		if (operation.equals("minus")) {
			handler.handleInventoryClick(ClickType.RIGHT, 5, null);
			handler.handleInventoryClick(ClickType.RIGHT, 3, null);
			reset(loreUpdateItem);
			reset(loreUpdateItemMeta);
			reset(loreUpdateRentItem);
			reset(loreUpdateRentItemMeta);
			when(loreUpdateItem.getItemMeta()).thenReturn(loreUpdateItemMeta);
			when(handler.getInventory().getItem(5)).thenReturn(loreUpdateItem);
			when(handler.getInventory().getItem(4)).thenReturn(loreUpdateItem);
			when(handler.getInventory().getItem(1)).thenReturn(loreUpdateItem);
			when(loreUpdateRentItem.getItemMeta()).thenReturn(loreUpdateRentItemMeta);
			when(handler.getInventory().getItem(0)).thenReturn(loreUpdateRentItem);
		}

		handler.handleInventoryClick(ClickType.RIGHT, rawslot, null);

		List<String> newLore = Arrays.asList("§6Duration: §a" + durationNew + "§6 " + dayDaysNew);
		List<String> newLoreFee = Arrays.asList("§6RentalFee: §a" + fee);
		verify(loreUpdateItemMeta, times(3)).setLore(newLore);
		verify(loreUpdateRentItemMeta).setLore(newLoreFee);
		verify(loreUpdateItem, times(3)).setItemMeta(loreUpdateItemMeta);
		verify(loreUpdateRentItem).setItemMeta(loreUpdateRentItemMeta);
	}

	@Test
	public void handleRentShopGuiClickTestMinusOneClickMore() {
		testPlusMinusDuration("Day", "Day", "minus", "one", 1, 1, 5.5, 4);
	}

	@Test
	public void handleRentShopGuiClickTestPlusOneClickMore() {
		when(configManager.getMaxRentedDays()).thenReturn(-10);
		testPlusMinusDuration("Days", "Days", "plus", "one", 1, -10, -55.0, 4);
	}

	@Test
	public void handleRentShopGuiClickTestPlusSevenClick() {
		when(configManager.getMaxRentedDays()).thenReturn(10);
		testPlusMinusDuration("Day", "Days", "plus", "seven", 1, 8, 44.0, 5);
	}

	@Test
	public void handleRentShopGuiClickTestMinusSevenClick() {
		when(configManager.getMaxRentedDays()).thenReturn(10);
		testPlusMinusDuration("Days", "Day", "minus", "seven", 1, 1, 5.5, 5);
	}

	@Test
	public void handleRentShopGuiClickTestMinusSevenClickMore() {
		testPlusMinusDuration("Days", "Day", "minus", "seven", 5, 1, 5.5, 5);
	}

	@Test
	public void handleRentShopGuiClickTestPlusSevenClickMore() {
		when(configManager.getMaxRentedDays()).thenReturn(5);
		testPlusMinusDuration("Days", "Days", "plus", "seven", 1, 5, 27.5, 5);
	}

	@Test
	public void handleRentShopGuiClickTestRentClickError()
			throws ShopsystemException, BankException, EconomyPlayerException {
		Inventory inv = mock(Inventory.class);
		ItemStack rentItem = mock(ItemStack.class);
		ItemStack plusItem = mock(ItemStack.class);
		ItemStack oneItem = mock(ItemStack.class);
		ItemStack sevenItem = mock(ItemStack.class);
		ItemStack clockItem = mock(ItemStack.class);
		ItemMeta rentItemMeta = mock(ItemMeta.class);
		ItemMeta clockItemMeta = mock(ItemMeta.class);
		ItemMeta oneItemMeta = mock(ItemMeta.class);
		ItemMeta sevenItemMeta = mock(ItemMeta.class);
		when(sevenItem.getItemMeta()).thenReturn(sevenItemMeta);
		when(oneItem.getItemMeta()).thenReturn(oneItemMeta);
		when(clockItem.getItemMeta()).thenReturn(clockItemMeta);
		when(rentItem.getItemMeta()).thenReturn(rentItemMeta);
		when(skullService.getSkullWithName(SkullTextureEnum.PLUS, "plus")).thenReturn(plusItem);
		when(skullService.getSkullWithName(SkullTextureEnum.ONE, "one")).thenReturn(oneItem);
		when(skullService.getSkullWithName(SkullTextureEnum.SEVEN, "seven")).thenReturn(sevenItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(rentItem);
		when(serverProvider.createItemStack(Material.CLOCK, 1)).thenReturn(clockItem);
		when(shop.getName()).thenReturn("Shop#R0");
		when(shop.getRentalFee()).thenReturn(5.5);
		when(shop.createVillagerInventory(9, "Shop#R0")).thenReturn(inv);
		RentshopRentGuiHandlerImpl handler = new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager,
				skullService, configManager, shop, serverProvider);

		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		ShopsystemException e = mock(ShopsystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(shop).rentShop(ecoPlayer, 1);

		handler.handleInventoryClick(ClickType.RIGHT, 0, ecoPlayer);

		verify(player).sendMessage("my error message");
		verify(player).closeInventory();
	}

	@Test
	public void handleRentShopGuiClickTestRentClick() {
		Inventory inv = mock(Inventory.class);
		ItemStack rentItem = mock(ItemStack.class);
		ItemStack plusItem = mock(ItemStack.class);
		ItemStack oneItem = mock(ItemStack.class);
		ItemStack sevenItem = mock(ItemStack.class);
		ItemStack clockItem = mock(ItemStack.class);
		ItemMeta rentItemMeta = mock(ItemMeta.class);
		ItemMeta clockItemMeta = mock(ItemMeta.class);
		ItemMeta oneItemMeta = mock(ItemMeta.class);
		ItemMeta sevenItemMeta = mock(ItemMeta.class);
		when(sevenItem.getItemMeta()).thenReturn(sevenItemMeta);
		when(oneItem.getItemMeta()).thenReturn(oneItemMeta);
		when(clockItem.getItemMeta()).thenReturn(clockItemMeta);
		when(rentItem.getItemMeta()).thenReturn(rentItemMeta);
		when(skullService.getSkullWithName(SkullTextureEnum.PLUS, "plus")).thenReturn(plusItem);
		when(skullService.getSkullWithName(SkullTextureEnum.ONE, "one")).thenReturn(oneItem);
		when(skullService.getSkullWithName(SkullTextureEnum.SEVEN, "seven")).thenReturn(sevenItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(rentItem);
		when(serverProvider.createItemStack(Material.CLOCK, 1)).thenReturn(clockItem);
		when(shop.getName()).thenReturn("Shop#R0");
		when(shop.getRentalFee()).thenReturn(5.5);
		when(shop.createVillagerInventory(9, "Shop#R0")).thenReturn(inv);
		RentshopRentGuiHandlerImpl handler = new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager,
				skullService, configManager, shop, serverProvider);

		ItemStack durationItem = mock(ItemStack.class);
		when(handler.getInventory().getItem(1)).thenReturn(durationItem);

		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(messageWrapper.getString(MessageEnum.RENT_RENTED)).thenReturn("my message");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		// plus 1
		ItemStack loreUpdateItem = mock(ItemStack.class);
		ItemMeta loreUpdateItemMeta = mock(ItemMeta.class);
		when(loreUpdateItem.getItemMeta()).thenReturn(loreUpdateItemMeta);
		when(handler.getInventory().getItem(5)).thenReturn(loreUpdateItem);
		when(handler.getInventory().getItem(4)).thenReturn(loreUpdateItem);
		when(handler.getInventory().getItem(1)).thenReturn(loreUpdateItem);
		ItemStack loreUpdateRentItem = mock(ItemStack.class);
		ItemMeta loreUpdateRentItemMeta = mock(ItemMeta.class);
		when(loreUpdateRentItem.getItemMeta()).thenReturn(loreUpdateRentItemMeta);
		when(handler.getInventory().getItem(0)).thenReturn(loreUpdateRentItem);
		when(configManager.getMaxRentedDays()).thenReturn(10);
		handler.handleInventoryClick(ClickType.RIGHT, 4, null);
		
		handler.handleInventoryClick(ClickType.RIGHT, 0, ecoPlayer);

		assertDoesNotThrow(() -> verify(shop).rentShop(ecoPlayer, 2));
		verify(player).sendMessage("my message");
		verify(player).closeInventory();
	}
}
