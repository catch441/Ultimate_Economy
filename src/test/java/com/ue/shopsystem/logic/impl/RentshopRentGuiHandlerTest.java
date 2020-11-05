package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.api.CustomSkullService;
import com.ue.common.api.SkullTextureEnum;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.logic.api.Rentshop;

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
		Villager villager = mock(Villager.class);
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
		when(shop.getShopVillager()).thenReturn(villager);
		when(shop.getName()).thenReturn("Shop#R0");
		when(shop.getRentalFee()).thenReturn(5.5);
		when(serverProvider.createInventory(villager, 9, "Shop#R0")).thenReturn(inv);
		return new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager,
				skullService, configManager, shop, serverProvider);
	}

	@Test
	public void constructorTest() {
		Villager villager = mock(Villager.class);
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
		when(shop.getShopVillager()).thenReturn(villager);
		when(shop.getName()).thenReturn("Shop#R0");
		when(shop.getRentalFee()).thenReturn(5.5);
		when(serverProvider.createInventory(villager, 9, "Shop#R0")).thenReturn(inv);
		new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager,
				skullService, configManager, shop, serverProvider);

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
		
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		
		ItemStack clickedItem = mock(ItemStack.class);
		ItemMeta clickedItemMeta = mock(ItemMeta.class);
		when(clickedItem.getItemMeta()).thenReturn(clickedItemMeta);
		when(clickedItemMeta.getDisplayName()).thenReturn("plus");
		
		ItemStack operationItem = mock(ItemStack.class);
		ItemMeta operationItemMeta = mock(ItemMeta.class);
		when(operationItem.getItemMeta()).thenReturn(operationItemMeta);
		when(handler.getRentGui().getItem(3)).thenReturn(operationItem);
		when(operationItemMeta.getDisplayName()).thenReturn("plus");
		
		ItemStack durationItem = mock(ItemStack.class);
		ItemMeta durationItemMeta = mock(ItemMeta.class);
		when(durationItem.getItemMeta()).thenReturn(durationItemMeta);
		when(handler.getRentGui().getItem(1)).thenReturn(durationItem);
		List<String> lore = Arrays.asList("§6Duration: §a1§6 Day");
		when(durationItemMeta.getLore()).thenReturn(lore);
			
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(event.getInventory()).thenReturn(handler.getRentGui());
		
		ItemStack minusItem = mock(ItemStack.class);
		when(skullService.getSkullWithName(SkullTextureEnum.MINUS, "minus")).thenReturn(minusItem);
		
		handler.handleRentShopGUIClick(event);
		
		verify(event).setCancelled(true);
		verify(handler.getRentGui()).setItem(3, minusItem);
	}

	@Test
	public void handleRentShopGuiClickTestMinusClick() {
		RentshopRentGuiHandlerImpl handler = createHandler();
		
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		
		ItemStack clickedItem = mock(ItemStack.class);
		ItemMeta clickedItemMeta = mock(ItemMeta.class);
		when(clickedItem.getItemMeta()).thenReturn(clickedItemMeta);
		when(clickedItemMeta.getDisplayName()).thenReturn("minus");
		
		ItemStack operationItem = mock(ItemStack.class);
		ItemMeta operationItemMeta = mock(ItemMeta.class);
		when(operationItem.getItemMeta()).thenReturn(operationItemMeta);
		when(handler.getRentGui().getItem(3)).thenReturn(operationItem);
		when(operationItemMeta.getDisplayName()).thenReturn("minus");
		
		ItemStack durationItem = mock(ItemStack.class);
		ItemMeta durationItemMeta = mock(ItemMeta.class);
		when(durationItem.getItemMeta()).thenReturn(durationItemMeta);
		when(handler.getRentGui().getItem(1)).thenReturn(durationItem);
		List<String> lore = Arrays.asList("§6Duration: §a1§6 Day");
		when(durationItemMeta.getLore()).thenReturn(lore);
			
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(event.getInventory()).thenReturn(handler.getRentGui());
		
		ItemStack plusItem = mock(ItemStack.class);
		when(skullService.getSkullWithName(SkullTextureEnum.PLUS, "plus")).thenReturn(plusItem);
		
		handler.handleRentShopGUIClick(event);
		
		verify(event).setCancelled(true);
		verify(handler.getRentGui(), times(1)).setItem(3, plusItem);
	}

	@Test
	public void handleRentShopGuiClickTestPlusOneClick() {
		when(configManager.getMaxRentedDays()).thenReturn(10);
		testPlusMinusDuration("Days", "Days", "plus", "one", 1, 2, 11.0);
	}

	@Test
	public void handleRentShopGuiClickTestMinusOneClick() {
		testPlusMinusDuration("Days", "Days", "minus", "one", 3, 2, 11.0);
	}

	private void testPlusMinusDuration(String dayDaysOld, String dayDaysNew, String operation, String amount, int durationOld, int durationNew, double fee) {
		RentshopRentGuiHandlerImpl handler = createHandler();
		
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		
		ItemStack clickedItem = mock(ItemStack.class);
		ItemMeta clickedItemMeta = mock(ItemMeta.class);
		when(clickedItem.getItemMeta()).thenReturn(clickedItemMeta);
		when(clickedItemMeta.getDisplayName()).thenReturn(amount);
		
		ItemStack operationItem = mock(ItemStack.class);
		ItemMeta operationItemMeta = mock(ItemMeta.class);
		when(operationItem.getItemMeta()).thenReturn(operationItemMeta);
		when(handler.getRentGui().getItem(3)).thenReturn(operationItem);
		when(operationItemMeta.getDisplayName()).thenReturn(operation);
		
		ItemStack durationItem = mock(ItemStack.class);
		ItemMeta durationItemMeta = mock(ItemMeta.class);
		when(durationItem.getItemMeta()).thenReturn(durationItemMeta);
		when(handler.getRentGui().getItem(1)).thenReturn(durationItem);
		List<String> lore = Arrays.asList("§6Duration: §a" + durationOld + "§6 "+ dayDaysOld);
		when(durationItemMeta.getLore()).thenReturn(lore);
			
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(event.getInventory()).thenReturn(handler.getRentGui());
				
		ItemStack loreUpdateItem = mock(ItemStack.class);
		ItemMeta loreUpdateItemMeta = mock(ItemMeta.class);
		when(loreUpdateItem.getItemMeta()).thenReturn(loreUpdateItemMeta);
		when(handler.getRentGui().getItem(5)).thenReturn(loreUpdateItem);
		when(handler.getRentGui().getItem(4)).thenReturn(loreUpdateItem);
		
		ItemStack loreUpdateRentItem = mock(ItemStack.class);
		ItemMeta loreUpdateRentItemMeta = mock(ItemMeta.class);
		when(loreUpdateRentItem.getItemMeta()).thenReturn(loreUpdateRentItemMeta);
		when(handler.getRentGui().getItem(0)).thenReturn(loreUpdateRentItem);
				
		handler.handleRentShopGUIClick(event);
		
		verify(event).setCancelled(true);
		List<String> newLore = Arrays.asList("§6Duration: §a" + durationNew + "§6 " + dayDaysNew);
		List<String> newLoreFee = Arrays.asList("§6RentalFee: §a" + fee);
		verify(loreUpdateItemMeta, times(2)).setLore(newLore);
		verify(loreUpdateRentItemMeta).setLore(newLoreFee);
		verify(handler.getRentGui().getItem(1).getItemMeta()).setLore(newLore);
	}

	@Test
	public void handleRentShopGuiClickTestMinusOneClickMore() {
		testPlusMinusDuration("Day", "Day", "minus", "one", 1, 1, 5.5);
	}

	@Test
	public void handleRentShopGuiClickTestPlusOneClickMore() {
		when(configManager.getMaxRentedDays()).thenReturn(10);
		testPlusMinusDuration("Days", "Days", "plus", "one", 10, 10, 55.0);
	}

	@Test
	public void handleRentShopGuiClickTestPlusSevenClick() {
		when(configManager.getMaxRentedDays()).thenReturn(10);
		testPlusMinusDuration("Day", "Days", "plus", "seven", 1, 8, 44.0);
	}

	@Test
	public void handleRentShopGuiClickTestMinusSevenClick() {
		testPlusMinusDuration("Days", "Days", "minus", "seven", 9, 2, 11.0);
	}

	@Test
	public void handleRentShopGuiClickTestMinusSevenClickMore() {
		testPlusMinusDuration("Days", "Day", "minus", "seven", 5, 1, 5.5);
	}

	@Test
	public void handleRentShopGuiClickTestPlusSevenClickMore() {
		when(configManager.getMaxRentedDays()).thenReturn(10);
		testPlusMinusDuration("Days", "Days", "plus", "seven", 6, 10, 55.0);
	}

	@Test
	public void handleRentShopGuiClickTestRentClickError() throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		Villager villager = mock(Villager.class);
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
		when(shop.getShopVillager()).thenReturn(villager);
		when(shop.getName()).thenReturn("Shop#R0");
		when(shop.getRentalFee()).thenReturn(5.5);
		when(serverProvider.createInventory(villager, 9, "Shop#R0")).thenReturn(inv);
		RentshopRentGuiHandlerImpl handler = new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager,
				skullService, configManager, shop, serverProvider);
		
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		
		ItemStack clickedItem = mock(ItemStack.class);
		ItemMeta clickedItemMeta = mock(ItemMeta.class);
		when(clickedItem.getItemMeta()).thenReturn(clickedItemMeta);
		when(clickedItemMeta.getDisplayName()).thenReturn("Rent");
		
		ItemStack operationItem = mock(ItemStack.class);
		ItemMeta operationItemMeta = mock(ItemMeta.class);
		when(operationItem.getItemMeta()).thenReturn(operationItemMeta);
		when(handler.getRentGui().getItem(3)).thenReturn(operationItem);
		when(operationItemMeta.getDisplayName()).thenReturn("plus");
		
		ItemStack durationItem = mock(ItemStack.class);
		ItemMeta durationItemMeta = mock(ItemMeta.class);
		when(durationItem.getItemMeta()).thenReturn(durationItemMeta);
		when(handler.getRentGui().getItem(1)).thenReturn(durationItem);
		List<String> lore = Arrays.asList("§6Duration: §a2§6 Days");
		when(durationItemMeta.getLore()).thenReturn(lore);

		Player player = mock(Player.class);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(event.getInventory()).thenReturn(handler.getRentGui());
		when(event.getWhoClicked()).thenReturn(player);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
			
		
		ShopSystemException e = mock(ShopSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(shop).rentShop(ecoPlayer, 2);
		
		handler.handleRentShopGUIClick(event);
		
		verify(event).setCancelled(true);
		verify(player).sendMessage("my error message");
		verify(player).closeInventory();
	}

	@Test
	public void handleRentShopGuiClickTestRentClick() {
		Villager villager = mock(Villager.class);
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
		when(shop.getShopVillager()).thenReturn(villager);
		when(shop.getName()).thenReturn("Shop#R0");
		when(shop.getRentalFee()).thenReturn(5.5);
		when(serverProvider.createInventory(villager, 9, "Shop#R0")).thenReturn(inv);
		RentshopRentGuiHandlerImpl handler = new RentshopRentGuiHandlerImpl(messageWrapper, ecoPlayerManager,
				skullService, configManager, shop, serverProvider);
		
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		
		ItemStack clickedItem = mock(ItemStack.class);
		ItemMeta clickedItemMeta = mock(ItemMeta.class);
		when(clickedItem.getItemMeta()).thenReturn(clickedItemMeta);
		when(clickedItemMeta.getDisplayName()).thenReturn("Rent");
		
		ItemStack operationItem = mock(ItemStack.class);
		ItemMeta operationItemMeta = mock(ItemMeta.class);
		when(operationItem.getItemMeta()).thenReturn(operationItemMeta);
		when(handler.getRentGui().getItem(3)).thenReturn(operationItem);
		when(operationItemMeta.getDisplayName()).thenReturn("plus");
		
		ItemStack durationItem = mock(ItemStack.class);
		ItemMeta durationItemMeta = mock(ItemMeta.class);
		when(durationItem.getItemMeta()).thenReturn(durationItemMeta);
		when(handler.getRentGui().getItem(1)).thenReturn(durationItem);
		List<String> lore = Arrays.asList("§6Duration: §a2§6 Days");
		when(durationItemMeta.getLore()).thenReturn(lore);

		Player player = mock(Player.class);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(event.getInventory()).thenReturn(handler.getRentGui());
		when(event.getWhoClicked()).thenReturn(player);
		when(player.getName()).thenReturn("catch441");
		when(messageWrapper.getString("rent_rented")).thenReturn("my message");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
				
		handler.handleRentShopGUIClick(event);
		
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop).rentShop(ecoPlayer, 2));
		verify(player).sendMessage("my message");
		verify(player).closeInventory();
	}
}
