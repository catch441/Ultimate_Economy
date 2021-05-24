package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopEditorHandler;
import org.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import org.ue.shopsystem.logic.api.ShopsystemException;

@ExtendWith(MockitoExtension.class)
public class ShopEventHandlerImplTest {

	@InjectMocks
	ShopEventHandlerImpl eventHandler;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	AdminshopManager adminshopManager;
	@Mock
	PlayershopManager playershopManager;
	@Mock
	RentshopManager rentshopManager;

	@Test
	public void handleOpenInventoryTestPlayershop() {
		Plugin plugin = mock(Plugin.class);
		Playershop shop = mock(Playershop.class);
		Villager villager = mock(Villager.class);
		Player player = mock(Player.class);
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.PLAYERSHOP);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopById("P0")).thenReturn(shop));
		eventHandler.handleOpenInventory(event);
		assertTrue(event.isCancelled());
		assertDoesNotThrow(() -> verify(shop).openInventory(player));
	}

	@Test
	public void handleOpenInventoryTestWithInvalidEconomyVillagerType() {
		Plugin plugin = mock(Plugin.class);
		Villager villager = mock(Villager.class);
		Player player = mock(Player.class);
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.TOWNMANAGER);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		eventHandler.handleOpenInventory(event);
		assertTrue(event.isCancelled());
		verifyNoInteractions(playershopManager);
		verifyNoInteractions(adminshopManager);
		verifyNoInteractions(rentshopManager);
	}

	@Test
	public void handleOpenInventoryTestWithPlayershopNotExists() throws ShopsystemException {
		Plugin plugin = mock(Plugin.class);
		Villager villager = mock(Villager.class);
		Player player = mock(Player.class);
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.PLAYERSHOP);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		doThrow(ShopsystemException.class).when(playershopManager).getPlayerShopById("P0");
		eventHandler.handleOpenInventory(event);
		assertTrue(event.isCancelled());
	}

	@Test
	public void handleOpenInventoryTestAdminshop() {
		Plugin plugin = mock(Plugin.class);
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Player player = mock(Player.class);
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.ADMINSHOP);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopById("A0")).thenReturn(shop));
		eventHandler.handleOpenInventory(event);
		assertTrue(event.isCancelled());
		assertDoesNotThrow(() -> verify(shop).openInventory(player));
	}

	@Test
	public void handleOpenInventoryTestRentshopNotRented() {
		Plugin plugin = mock(Plugin.class);
		Rentshop shop = mock(Rentshop.class);
		Villager villager = mock(Villager.class);
		Player player = mock(Player.class);
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.RENTSHOP);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "R0");
		InventoryGuiHandler handler = mock(InventoryGuiHandler.class);
		assertDoesNotThrow(() -> when(shop.getRentGuiHandler()).thenReturn(handler));
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopById("R0")).thenReturn(shop));
		when(shop.isRentable()).thenReturn(true);
		eventHandler.handleOpenInventory(event);
		assertTrue(event.isCancelled());
		assertDoesNotThrow(() -> verify(handler).openInventory(player));
	}

	@Test
	public void handleOpenInventoryTestRentshopRented() {
		Plugin plugin = mock(Plugin.class);
		Rentshop shop = mock(Rentshop.class);
		Villager villager = mock(Villager.class);
		Player player = mock(Player.class);
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.RENTSHOP);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "R0");
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopById("R0")).thenReturn(shop));
		when(shop.isRentable()).thenReturn(false);
		eventHandler.handleOpenInventory(event);
		assertTrue(event.isCancelled());
		assertDoesNotThrow(() -> verify(shop).openInventoryWithCheck(player));
	}

	@Test
	public void handleInventoryClickTestRentshopWithError() throws ShopsystemException {
		Rentshop shop = mock(Rentshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "R0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.RENTSHOP);
		doThrow(ShopsystemException.class).when(shop).getRentGuiHandler();
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopById("R0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shop.isRentable()).thenReturn(true);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
	}

	@Test
	public void handleInventoryClickTestAdminshopLeftClick() {
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.ADMINSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopById("A0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getClick()).thenReturn(ClickType.LEFT);
		when(event.getRawSlot()).thenReturn(0);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(shop).handleInventoryClick(ClickType.LEFT, 0, ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestPlayershopLeftClick() {
		Playershop shop = mock(Playershop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.PLAYERSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopById("P0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getClick()).thenReturn(ClickType.LEFT);
		when(event.getRawSlot()).thenReturn(0);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(shop).handleInventoryClick(ClickType.LEFT, 0, ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestRentshopLeftClick() {
		Rentshop shop = mock(Rentshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "R0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.RENTSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopById("R0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getClick()).thenReturn(ClickType.LEFT);
		when(event.getRawSlot()).thenReturn(0);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(shop).handleInventoryClick(ClickType.LEFT, 0, ecoPlayer);
	}

	@Test
	public void handlerInventoryClickTestRentshopNotRentedAndRentClick() {
		Rentshop shop = mock(Rentshop.class);
		InventoryGuiHandler rentGuiHandler = mock(InventoryGuiHandler.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "R0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.RENTSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopById("R0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shop.isRentable()).thenReturn(true);
		assertDoesNotThrow(() -> when(shop.getRentGuiHandler()).thenReturn(rentGuiHandler));
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getRawSlot()).thenReturn(1);
		when(player.getName()).thenReturn("catch441");
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(rentGuiHandler).handleInventoryClick(ClickType.RIGHT, 1, ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestNullItemClick() {
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		eventHandler.handleInventoryClick(event);
		verify(event).getCurrentItem();
		verifyNoMoreInteractions(event);
	}

	@Test
	public void handleInventoryClickTestAdminshopEditor() {
		ShopEditorHandler editor = mock(ShopEditorHandler.class);
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.ADMINSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopById("A0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("Editor");
		when(event.getView()).thenReturn(view);
		when(event.getRawSlot()).thenReturn(1);
		when(player.getName()).thenReturn("catch441");
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getWhoClicked()).thenReturn(player);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(shop.getEditorHandler()).thenReturn(editor);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(editor).handleInventoryClick(ClickType.RIGHT, 1, ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestAdminshopSlotEditor() {
		ShopSlotEditorHandler slotEditor = mock(ShopSlotEditorHandler.class);
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.ADMINSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopById("A0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("SlotEditor");
		when(event.getView()).thenReturn(view);
		when(event.getRawSlot()).thenReturn(1);
		when(player.getName()).thenReturn("catch441");
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getWhoClicked()).thenReturn(player);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		assertDoesNotThrow(() -> when(shop.getSlotEditorHandler(null)).thenReturn(slotEditor));
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(slotEditor).handleInventoryClick(ClickType.RIGHT, 1, ecoPlayer);
	}
	
	@Test
	public void handleInventoryClickTestAdminshopCustomizer() {
		InventoryGuiHandler customizer = mock(InventoryGuiHandler.class);
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.ADMINSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopById("A0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("Customize Villager");
		when(event.getView()).thenReturn(view);
		when(event.getRawSlot()).thenReturn(1);
		when(player.getName()).thenReturn("catch441");
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getWhoClicked()).thenReturn(player);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		assertDoesNotThrow(() -> when(shop.getCustomizeGuiHandler()).thenReturn(customizer));
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(customizer).handleInventoryClick(ClickType.RIGHT, 1, ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestWithInvalidEconomyVillagerType() {
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		ItemStack clickedItem = mock(ItemStack.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillagerType.TOWNMANAGER);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(player.getName()).thenReturn("catch441");
		when(event.getWhoClicked()).thenReturn(player);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verifyNoMoreInteractions(playershopManager);
		verifyNoMoreInteractions(adminshopManager);
		verifyNoMoreInteractions(rentshopManager);
		verify(player, never()).sendMessage(anyString());
	}
}
