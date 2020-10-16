package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;

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
		String[] args = { "create", "9", "10", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop create <size> <rentalFee per 24h>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void createCommandTest() {
		Location loc = mock(Location.class);
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> when(rentshopManager.createRentShop(loc, 18, 4.0)).thenReturn(shop));
		when(shop.getName()).thenReturn("RentShop#R0");
		when(player.getLocation()).thenReturn(loc);
		when(messageWrapper.getString("shop_create", "RentShop#R0")).thenReturn("my message");
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
		String[] args = { "delete", "RentShop#R0", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop delete <shopname>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteCommandTest() {
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("RentShop#R0")).thenReturn(shop));
		when(messageWrapper.getString("shop_delete", "RentShop#R0")).thenReturn("my message");
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
		String[] args = { "move", "RentShop#R0", "kth" };
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
		assertDoesNotThrow(() -> verify(shop).moveShop(loc));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void resizeCommandTestWithNoPermission() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(false);
		String[] args = { "resize", "RentShop#R0", "9" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verifyNoInteractions(rentshopManager);
		verify(player).hasPermission("ultimate_economy.rentshop.admin");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void resizeCommandTestWithInvalidArgNumber() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "resize", "RentShop#R0", "9", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop resize <shopname> <new size>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void resizeCommandTestWithInvalidSize()
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		Rentshop shop = mock(Rentshop.class);
		doThrow(e).when(shop).changeShopSize(16);
		when(e.getMessage()).thenReturn("my error message");
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("RentShop#R0")).thenReturn(shop));
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "resize", "RentShop#R0", "16" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void resizeCommandTestWithInvalidSize2() {
		Rentshop shop = mock(Rentshop.class);
		when(messageWrapper.getErrorString("invalid_parameter", "number")).thenReturn("my error message");
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("RentShop#R0")).thenReturn(shop));
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "resize", "RentShop#R0", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void resizeCommandTest() {
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("RentShop#R0")).thenReturn(shop));
		when(messageWrapper.getString("shop_resize", "9")).thenReturn("my message");
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "resize", "RentShop#R0", "9" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).changeShopSize(9));
		verify(player).sendMessage("my message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void changeProfessionCommandTestWithInvalidArgNumber() {
		String[] args = { "changeProfession", "Shop#R0_1catch441", "9", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop changeProfession <shopname> <profession>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void changeProfessionCommandTestWithInvalidProfession() {
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("Shop#R0_catch441")).thenReturn(shop));
		when(messageWrapper.getErrorString("invalid_parameter", "kth")).thenReturn("my error message");
		when(player.getName()).thenReturn("catch441");
		String[] args = { "changeProfession", "Shop#R0", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(shop, never()).changeProfession(any());
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}
	
	@Test
	public void changeProfessionCommandTestRented() {
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("Shop#R0_catch441")).thenReturn(shop));
		when(messageWrapper.getString("profession_changed")).thenReturn("my message");
		when(player.getName()).thenReturn("catch441");
		String[] args = { "changeProfession", "Shop#R0", "Farmer" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(shop).changeProfession(Profession.FARMER);
		verify(player).sendMessage("my message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void renameCommandTestWithInvalidArgNumber() {
		String[] args = { "rename", "Shop#R0_1catch441", "9", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop rename <oldName> <newName>");
		verifyNoMoreInteractions(player);
	}
	
	@Test
	public void renameCommandTestRented() throws GeneralEconomyException {
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("Shop#R0_catch441")).thenReturn(shop));
		when(messageWrapper.getString("shop_rename", "Shop#R0", "NewName")).thenReturn("my message");
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
		String[] args = { "editShop", "Shop#R0_1catch441", "9", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("/rentshop editShop <shopname>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void editShopCommandTest() {
		Rentshop shop = mock(Rentshop.class);
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("Shop#R0_catch441")).thenReturn(shop));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "editShop", "Shop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		verify(player).getName();
		verifyNoMoreInteractions(player);
	}

	@Test
	public void editShopCommandTestWithNotRentedError() throws ShopSystemException {
		ShopSystemException e = mock(ShopSystemException.class);
		Rentshop shop = mock(Rentshop.class);
		doThrow(e).when(shop).openEditor(player);
		when(e.getMessage()).thenReturn("my error message");
		assertDoesNotThrow(() -> when(rentshopManager.getRentShopByUniqueName("RentShop#R0_catch441")).thenReturn(shop));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "editShop", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}
}
