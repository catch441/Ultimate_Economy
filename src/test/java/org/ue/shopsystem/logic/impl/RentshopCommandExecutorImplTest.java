package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopsystemException;

@ExtendWith(MockitoExtension.class)
public class RentshopCommandExecutorImplTest {

	@InjectMocks
	RentshopCommandExecutorImpl executor;
	@Mock
	RentshopManager rentshopManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	Player player;

	@Test
	public void unknownCommandTestWithAdminPermission() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop [create/delete/move/resize/editShop]");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void unknownCommandTestWithPlayerPermission() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(false);
		String[] args = { "" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop [editShop]");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void zeroArgsTest() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void createCommandTestWithNoPermission() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(false);
		String[] args = { "create", "9", "10" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verifyNoInteractions(rentshopManager);
		verify(player).hasPermission("ultimate_economy.rentshop.admin");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void createCommandTestWithInvalidArgNumber() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "create", "9", "10", "catch" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop create <size> <rentalFee per 24h>");
		verifyNoMoreInteractions(player);
	}
	
	@Test
	public void createCommandTestWithInvalidNumberFormat() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "create", "invalid", "10"};
		when(messageWrapper.getErrorString(ExceptionMessageEnum.INVALID_PARAMETER, "number")).thenReturn("message");
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("message");
	}

	@Test
	public void createCommandTest() {
		Location loc = mock(Location.class);
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> when(rentshopManager.createRentShop(loc, 18, 4.0)).thenReturn(shop));
		when(shop.getName()).thenReturn("RentShop#R0");
		when(player.getLocation()).thenReturn(loc);
		when(messageWrapper.getString(MessageEnum.CREATED, "RentShop#R0")).thenReturn("my message");
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "create", "18", "4" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(rentshopManager).createRentShop(loc, 18, 4));
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteCommandTestWithNoPermission() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(false);
		String[] args = { "delete", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verifyNoInteractions(rentshopManager);
		verify(player).hasPermission("ultimate_economy.rentshop.admin");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteCommandTestWithInvalidArgNumber() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "delete", "RentShop#R0", "catch" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop delete <shopname>");
		verifyNoMoreInteractions(player);
	}
	
	@Test
	public void deleteCommandTestWithNoShop() throws ShopsystemException {
		ShopsystemException e = mock(ShopsystemException.class);
		when(rentshopManager.getRentShopByUniqueName("RentShop#R0")).thenThrow(e);
		when(e.getMessage()).thenReturn("message");
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "delete", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("message");
	}

	@Test
	public void deleteCommandTest() {
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("RentShop#R0")).thenReturn(shop));
		when(messageWrapper.getString(MessageEnum.DELETED, "RentShop#R0")).thenReturn("my message");
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "delete", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(rentshopManager).deleteRentShop(shop);
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void moveCommandTestWithNoPermission() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(false);
		String[] args = { "move", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verifyNoInteractions(rentshopManager);
		verify(player).hasPermission("ultimate_economy.rentshop.admin");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void moveCommandTestWithInvalidArgNumber() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "move", "RentShop#R0", "catch" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop move <shopname>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void moveCommandTest() {
		Rentshop shop = mock(Rentshop.class);
		Location loc = mock(Location.class);
		when(player.getLocation()).thenReturn(loc);
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("RentShop#R0")).thenReturn(shop));
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "move", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).changeLocation(loc));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void renameCommandTestWithInvalidArgNumber() {
		String[] args = { "rename", "Shop#R0_1catch441", "9", "catch" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop rename <oldName> <newName>");
		verifyNoMoreInteractions(player);
	}
	
	@Test
	public void renameCommandTestRented() throws ShopsystemException {
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("Shop#R0_catch441")).thenReturn(shop));
		when(messageWrapper.getString(MessageEnum.SHOP_RENAME, "Shop#R0", "NewName")).thenReturn("my message");
		when(player.getName()).thenReturn("catch441");
		String[] args = { "rename", "Shop#R0", "NewName" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).changeShopName("NewName"));
		verify(player).sendMessage("my message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void editShopCommandTestWithInvalidArgNumber() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "editShop", "Shop#R0_1catch441", "9", "catch" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop editShop <shopname>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void editShopCommandTest() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		Rentshop shop = mock(Rentshop.class);
		InventoryGuiHandler editor = mock(InventoryGuiHandler.class);
		when(shop.getEditorHandler()).thenReturn(editor);
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("Shop#R0_catch441")).thenReturn(shop));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "editShop", "Shop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(editor).openInventory(player));
		verify(player).getName();
		verifyNoMoreInteractions(player);
	}
	
	@Test
	public void editShopCommandTestWithNoPermission() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(false);
		String[] args = { "editShop", "Shop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verifyNoInteractions(rentshopManager);
		verify(player).hasPermission("ultimate_economy.rentshop.admin");
		verifyNoMoreInteractions(player);
	}
}
