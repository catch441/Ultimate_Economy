package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.EconomyVillager;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.logic.api.Adminshop;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.shopsystem.logic.api.RentshopRentGuiHandler;
import com.ue.shopsystem.logic.to.ShopItem;

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
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopById("P0")).thenReturn(shop));
		eventHandler.handleOpenInventory(event);
		assertTrue(event.isCancelled());
		assertDoesNotThrow(() -> verify(shop).openShopInventory(player));
	}
	
	@Test
	public void handleOpenInventoryTestWithInvalidEconomyVillagerType() {
		Plugin plugin = mock(Plugin.class);
		Villager villager = mock(Villager.class);
		Player player = mock(Player.class);
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.TOWNMANAGER);
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
	public void handleOpenInventoryTestWithPlayershopNotExists() throws GeneralEconomyException {
		Plugin plugin = mock(Plugin.class);
		Villager villager = mock(Villager.class);
		Player player = mock(Player.class);
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		doThrow(GeneralEconomyException.class).when(playershopManager).getPlayerShopById("P0");
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
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopById("A0")).thenReturn(shop));
		eventHandler.handleOpenInventory(event);
		assertTrue(event.isCancelled());
		assertDoesNotThrow(() -> verify(shop).openShopInventory(player));
	}

	@Test
	public void handleOpenInventoryTestRentshopNotRented() {
		Plugin plugin = mock(Plugin.class);
		Rentshop shop = mock(Rentshop.class);
		Villager villager = mock(Villager.class);
		Player player = mock(Player.class);
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP_RENTABLE);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "R0");
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopById("R0")).thenReturn(shop));
		when(shop.isRentable()).thenReturn(true);
		eventHandler.handleOpenInventory(event);
		assertTrue(event.isCancelled());
		assertDoesNotThrow(() -> verify(shop).openRentGUI(player));
	}

	@Test
	public void handleOpenInventoryTestRentshopRented() {
		Plugin plugin = mock(Plugin.class);
		Rentshop shop = mock(Rentshop.class);
		Villager villager = mock(Villager.class);
		Player player = mock(Player.class);
		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP_RENTABLE);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "R0");
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopById("R0")).thenReturn(shop));
		when(shop.isRentable()).thenReturn(false);
		eventHandler.handleOpenInventory(event);
		assertTrue(event.isCancelled());
		assertDoesNotThrow(() -> verify(shop).openShopInventory(player));
	}

	@Test
	public void handleInventoryClickTestNoShop()
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		PlayerInventory playerInv = mock(PlayerInventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
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
		when(event.getSlot()).thenReturn(0);
		when(event.getClickedInventory()).thenReturn(shopInv);
		when(player.getInventory()).thenReturn(playerInv);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(shop).buyShopItem(0, ecoPlayer, true);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(player).sendMessage("my error message");
		verifyNoInteractions(ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestAdminshopLeftClickBuyError()
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		doThrow(GeneralEconomyException.class).when(adminshopManager).getAdminShopById("A0");
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verifyNoInteractions(player);
		verifyNoInteractions(ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestAdminshopLeftClick() {
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		PlayerInventory playerInv = mock(PlayerInventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
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
		when(event.getSlot()).thenReturn(0);
		when(event.getClickedInventory()).thenReturn(shopInv);
		when(player.getInventory()).thenReturn(playerInv);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop).buyShopItem(0, ecoPlayer, true));
		verify(player, never()).sendMessage(anyString());
		verifyNoInteractions(ecoPlayer);
	}
	
	@Test
	public void handleInventoryClickTestAdminshopShiftLeftClick() {
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
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
		when(event.getClick()).thenReturn(ClickType.SHIFT_LEFT);
		when(event.getRawSlot()).thenReturn(0);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop, never()).buyShopItem(0, ecoPlayer, true));
		verify(player, never()).sendMessage(anyString());
		verifyNoInteractions(ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestAdminshopLeftClickInOwnInventory() {
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		PlayerInventory playerInv = mock(PlayerInventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
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
		when(event.getRawSlot()).thenReturn(36);
		when(event.getClickedInventory()).thenReturn(playerInv);
		when(player.getInventory()).thenReturn(playerInv);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop, never()).buyShopItem(anyInt(), eq(ecoPlayer), anyBoolean()));
		verify(player, never()).sendMessage(anyString());
		verifyNoInteractions(ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestPlayershopLeftClick() {
		Playershop shop = mock(Playershop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		PlayerInventory playerInv = mock(PlayerInventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP);
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
		when(event.getSlot()).thenReturn(0);
		when(event.getClickedInventory()).thenReturn(shopInv);
		when(player.getInventory()).thenReturn(playerInv);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop).buyShopItem(0, ecoPlayer, true));
		verify(player, never()).sendMessage(anyString());
		verifyNoInteractions(ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestRentshopLeftClick() {
		RentshopImpl shop = mock(RentshopImpl.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		PlayerInventory playerInv = mock(PlayerInventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "R0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP_RENTABLE);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopById("R0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shop.isRentable()).thenReturn(false);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getWhoClicked()).thenReturn(player);
		when(event.getClick()).thenReturn(ClickType.LEFT);
		when(event.getRawSlot()).thenReturn(0);
		when(event.getSlot()).thenReturn(0);
		when(event.getClickedInventory()).thenReturn(shopInv);
		when(player.getInventory()).thenReturn(playerInv);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop).buyShopItem(0, ecoPlayer, true));
		verify(player, never()).sendMessage(anyString());
		verifyNoInteractions(ecoPlayer);
	}

	@Test
	public void handlerInventoryClickTestRentshopNotRentedAndRentClick() {
		RentshopImpl shop = mock(RentshopImpl.class);
		RentshopRentGuiHandler rentGuiHandler = mock(RentshopRentGuiHandler.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "R0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP_RENTABLE);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopById("R0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shop.isRentable()).thenReturn(true);
		when(shop.getRentGuiHandler()).thenReturn(rentGuiHandler);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(rentGuiHandler).handleRentShopGUIClick(event);
		verify(player, never()).sendMessage(anyString());
		verifyNoInteractions(ecoPlayer);
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
		ShopEditorHandlerImpl editor = mock(ShopEditorHandlerImpl.class);
		AdminshopImpl shop = mock(AdminshopImpl.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopById("A0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop-Editor");
		when(shop.getName()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getWhoClicked()).thenReturn(player);
		when(shop.getEditorHandler()).thenReturn(editor);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(editor).handleInventoryClick(event);
		verify(player, never()).sendMessage(anyString());
		verifyNoInteractions(ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestAdminshopSlotEditor() {
		ShopSlotEditorHandlerImpl slotEditor = mock(ShopSlotEditorHandlerImpl.class);
		AdminshopImpl shop = mock(AdminshopImpl.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopById("A0")).thenReturn(shop));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop-SlotEditor");
		when(shop.getName()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getWhoClicked()).thenReturn(player);
		when(shop.getSlotEditorHandler()).thenReturn(slotEditor);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(slotEditor).handleSlotEditor(event);
		verify(player, never()).sendMessage(anyString());
		verifyNoInteractions(ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestPlayershopSwitchStockpile() {
		PlayershopImpl shop = mock(PlayershopImpl.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopById("P0")).thenReturn(shop));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop");
		when(shop.getName()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getClick()).thenReturn(ClickType.MIDDLE);
		when(event.getWhoClicked()).thenReturn(player);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop).openStockpile(player));
		verify(player, never()).sendMessage(anyString());
	}
	
	@Test
	public void handleInventoryClickTestRentshopSwitchStockpile() {
		RentshopImpl shop = mock(RentshopImpl.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "R0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP_RENTABLE);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopById("R0"))).thenReturn(shop);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop");
		when(shop.getName()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getClick()).thenReturn(ClickType.MIDDLE);
		when(event.getWhoClicked()).thenReturn(player);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(shop.isRentable()).thenReturn(false);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop).openStockpile(player));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void handleInventoryClickTestPlayershopSwitchStockpileBack() {
		PlayershopImpl shop = mock(PlayershopImpl.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopById("P0")).thenReturn(shop));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop-Stock");
		when(shop.getName()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getClick()).thenReturn(ClickType.MIDDLE);
		when(event.getWhoClicked()).thenReturn(player);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop).openShopInventory(player));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void handleInventoryClickTestPlayershopSwitchStockpileItemLeftClick() {
		PlayershopImpl shop = mock(PlayershopImpl.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopById("P0")).thenReturn(shop));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop");
		when(shop.getName()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getRawSlot()).thenReturn(8);
		when(shop.getSize()).thenReturn(9);
		when(event.getClick()).thenReturn(ClickType.LEFT);
		when(event.getWhoClicked()).thenReturn(player);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop, never()).openStockpile(player));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void handleInventoryClickTestPlayershopSwitchStockpileItemRightClick() {
		PlayershopImpl shop = mock(PlayershopImpl.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopById("P0")).thenReturn(shop));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop");
		when(shop.getName()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getRawSlot()).thenReturn(8);
		when(shop.getSize()).thenReturn(9);
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getWhoClicked()).thenReturn(player);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop, never()).openStockpile(player));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void handleInventoryClickTestPlayershopSwitchStockpileItemShiftRightClick() {
		PlayershopImpl shop = mock(PlayershopImpl.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "P0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.PLAYERSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopById("P0")).thenReturn(shop));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop");
		when(shop.getName()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getRawSlot()).thenReturn(8);
		when(shop.getSize()).thenReturn(9);
		when(event.getClick()).thenReturn(ClickType.SHIFT_RIGHT);
		when(event.getWhoClicked()).thenReturn(player);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop, never()).openStockpile(player));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void handleInventoryClickTestAdminshopSwitchStockpile() {
		AdminshopImpl shop = mock(AdminshopImpl.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopById("A0")).thenReturn(shop));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		when(view.getTitle()).thenReturn("myshop");
		when(shop.getName()).thenReturn("myshop");
		when(event.getView()).thenReturn(view);
		when(event.getClick()).thenReturn(ClickType.MIDDLE);
		when(event.getWhoClicked()).thenReturn(player);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(shop, times(2)).getName();
		verifyNoMoreInteractions(shop);
		verify(player, never()).sendMessage(anyString());
	}
	
	@Test
	public void handleInventoryClickTestWithInvalidEconomyVillagerType() {
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		Player player = mock(Player.class);
		ItemStack clickedItem = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.TOWNMANAGER);
		when(villager.getMetadata("ue-type")).thenReturn(Arrays.asList(metaData));
		when(villager.getMetadata("ue-id")).thenReturn(Arrays.asList(metaDataId));
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(shopInv);
		when(shopInv.getHolder()).thenReturn(villager);
		when(event.getCurrentItem()).thenReturn(clickedItem);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verifyNoMoreInteractions(playershopManager);
		verifyNoMoreInteractions(adminshopManager);
		verifyNoMoreInteractions(rentshopManager);
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void handleInventoryClickTestAdminshopSellSpecific() {
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		PlayerInventory playerInv = mock(PlayerInventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ShopItem shopItem = mock(ShopItem.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
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
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getRawSlot()).thenReturn(4);
		when(player.getInventory()).thenReturn(playerInv);
		assertDoesNotThrow(() -> when(shop.getShopItem(clickedItem)).thenReturn(shopItem));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(shopItem.getAmount()).thenReturn(10);
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItem.getSlot()).thenReturn(4);
		when(shop.getSize()).thenReturn(9);
		when(playerInv.containsAtLeast(shopItemStack, 10)).thenReturn(true);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop).sellShopItem(4, 10, ecoPlayer, true));
		verify(player, never()).sendMessage(anyString());
		verify(ecoPlayer).getPlayer();
		verifyNoMoreInteractions(ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestAdminshopSellSpecificWithoutItem() {
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		PlayerInventory playerInv = mock(PlayerInventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ShopItem shopItem = mock(ShopItem.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
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
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getRawSlot()).thenReturn(4);
		when(player.getInventory()).thenReturn(playerInv);
		assertDoesNotThrow(() -> when(shop.getShopItem(clickedItem)).thenReturn(shopItem));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(shopItem.getAmount()).thenReturn(10);
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(playerInv.containsAtLeast(shopItemStack, 10)).thenReturn(false);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop, never()).sellShopItem(anyInt(), anyInt(), eq(ecoPlayer), anyBoolean()));
		verify(player, never()).sendMessage(anyString());
		verify(ecoPlayer).getPlayer();
		verifyNoMoreInteractions(ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestAdminshopSellAll() {
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		PlayerInventory playerInv = mock(PlayerInventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ShopItem shopItem = mock(ShopItem.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
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
		when(event.getClick()).thenReturn(ClickType.SHIFT_RIGHT);
		when(event.getRawSlot()).thenReturn(4);
		when(player.getInventory()).thenReturn(playerInv);
		assertDoesNotThrow(() -> when(shop.getShopItem(clickedItem)).thenReturn(shopItem));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItem.getSlot()).thenReturn(4);
		when(playerInv.containsAtLeast(shopItemStack, 1)).thenReturn(true);
		ArrayList<ItemStack> contents = new ArrayList<>();
		ItemStack playerStack = mock(ItemStack.class);
		ItemStack playerStack2 = mock(ItemStack.class);
		when(playerStack.isSimilar(shopItemStack)).thenReturn(false);
		when(playerStack.isSimilar(shopItemStack)).thenReturn(true);
		when(playerStack.getAmount()).thenReturn(3);
		ItemStack[] array = new ItemStack[4];
		contents.add(null);
		contents.add(playerStack);
		contents.add(playerStack2);
		contents.add(playerStack);
		array = contents.toArray(array);
		when(playerInv.getStorageContents()).thenReturn(array);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop).sellShopItem(4, 6, ecoPlayer, true));
		verify(player, never()).sendMessage(anyString());
		verify(ecoPlayer).getPlayer();
		verifyNoMoreInteractions(ecoPlayer);
	}

	@Test
	public void handleInventoryClickTestAdminshopSellSpecificOwnInvClick() {
		Adminshop shop = mock(Adminshop.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory shopInv = mock(Inventory.class);
		PlayerInventory playerInv = mock(PlayerInventory.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ShopItem shopItem = mock(ShopItem.class);
		InventoryView view = mock(InventoryView.class);
		ItemStack clickedItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		FixedMetadataValue metaDataId = new FixedMetadataValue(plugin, "A0");
		FixedMetadataValue metaData = new FixedMetadataValue(plugin, EconomyVillager.ADMINSHOP);
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
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getRawSlot()).thenReturn(36);
		when(player.getInventory()).thenReturn(playerInv);
		assertDoesNotThrow(() -> when(shop.getShopItem(clickedItem)).thenReturn(shopItem));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(shopItem.getAmount()).thenReturn(10);
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(shopItem.getSlot()).thenReturn(4);
		when(playerInv.containsAtLeast(shopItemStack, 10)).thenReturn(true);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(shop).sellShopItem(4, 10, ecoPlayer, true));
		verify(player, never()).sendMessage(anyString());
		verify(ecoPlayer).getPlayer();
		verifyNoMoreInteractions(ecoPlayer);
	}
}
