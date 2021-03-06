package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.ShopsystemException;

@ExtendWith(MockitoExtension.class)
public class PlayershopCommandExecutorImplTest {

	@InjectMocks
	PlayershopCommandExecutorImpl executor;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	PlayershopManager playershopManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	Player player;

	@Test
	public void zeroArgsTest() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void createCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		when(messageWrapper.getString(MessageEnum.CREATED, "myshop")).thenReturn("my message");
		when(player.getLocation()).thenReturn(loc);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "create", "myshop", "9" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(playershopManager).createPlayerShop("myshop", loc, 9, ecoPlayer));
		verify(player).sendMessage("my message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void createCommandTestWithInvalidArgumentNumber() {
		String[] args = { "create", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("/playershop create <shop> <size> <- size have to be a multible of 9");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void createCommandTestWithInvalidNumber() {
		when(messageWrapper.getErrorString(ExceptionMessageEnum.INVALID_PARAMETER, "dsa"))
				.thenReturn("my error message");
		String[] args = { "create", "myshop", "dsa" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void createCommandTestWithInvalidNumber2() throws ShopsystemException {
		ShopsystemException e = mock(ShopsystemException.class);
		doThrow(e).when(playershopManager).createPlayerShop("myshop", null, 6, null);
		when(e.getMessage()).thenReturn("my error message");
		String[] args = { "create", "myshop", "6" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void deleteCommandTestWithInvalidArgumentNumber() {
		String[] args = { "delete" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("/playershop delete <shop>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteCommandTest() {
		Playershop shop = mock(Playershop.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopByUniqueName("myshop_catch441")).thenReturn(shop));
		when(messageWrapper.getString(MessageEnum.DELETED, "myshop")).thenReturn("my message");
		String[] args = { "delete", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(playershopManager).deletePlayerShop(shop);
		verify(player).sendMessage("my message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void deleteCommandTestWithNoShop() throws ShopsystemException {
		ShopsystemException e = mock(ShopsystemException.class);
		when(player.getName()).thenReturn("catch441");
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(playershopManager).getPlayerShopByUniqueName("myshop_catch441");
		String[] args = { "delete", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void renameCommandTest() {
		Playershop shop = mock(Playershop.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopByUniqueName("myshop_catch441")).thenReturn(shop));
		when(messageWrapper.getString(MessageEnum.SHOP_RENAME, "myshop", "mynewshop")).thenReturn("my message");
		String[] args = { "rename", "myshop", "mynewshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).changeShopName("mynewshop"));
		verify(player).sendMessage("my message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void renameCommandTestWithInvalidName() throws ShopsystemException {
		ShopsystemException e = mock(ShopsystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		Playershop shop = mock(Playershop.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopByUniqueName("myshop_catch441")).thenReturn(shop));
		doThrow(e).when(shop).changeShopName("myshop");
		String[] args = { "rename", "myshop", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void renameCommandTestWithInvalidArgumentNumber() {
		String[] args = { "rename", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("/playershop rename <oldName> <newName>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void editShopCommandTest() {
		Playershop shop = mock(Playershop.class);
		InventoryGuiHandler handler = mock(InventoryGuiHandler.class);
		when(shop.getEditorHandler()).thenReturn(handler);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopByUniqueName("myshop_catch441")).thenReturn(shop));
		String[] args = { "editShop", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(handler).openInventory(player));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void editShopCommandTestWithInvalidArgsNumber() {
		String[] args = { "editShop", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("/playershop editShop <shop>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void unknownCommandTest() {
		String[] args = { "kth" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void moveCommandTestWithInvalidArgsNumber() {
		String[] args = { "move", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("/playershop move <shop>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void moveCommandTest() {
		Playershop shop = mock(Playershop.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopByUniqueName("myshop_catch441")).thenReturn(shop));
		Location loc = mock(Location.class);
		when(player.getLocation()).thenReturn(loc);
		String[] args = { "move", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).changeLocation(loc));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void changeOwnerCommandTestWithInvalidArgNumber() {
		String[] args = { "changeOwner", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("/playershop changeOwner <shop> <new owner>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void changeOwnerCommandTest() {
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		Player newPlayer = mock(Player.class);
		Playershop shop = mock(Playershop.class);
		when(messageWrapper.getString(MessageEnum.SHOP_CHANGEOWNER1, "kthschnll")).thenReturn("my message1");
		when(messageWrapper.getString(MessageEnum.SHOP_CHANGEOWNER, "myshop", "catch441")).thenReturn("my message2");
		when(newOwner.getPlayer()).thenReturn(newPlayer);
		when(newOwner.isOnline()).thenReturn(true);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopByUniqueName("myshop_catch441")).thenReturn(shop));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("kthschnll")).thenReturn(newOwner));
		String[] args = { "changeOwner", "myshop", "kthschnll" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).changeOwner(newOwner));
		verify(player).sendMessage("my message1");
		verify(player, times(1)).sendMessage(anyString());
		verify(newPlayer).sendMessage("my message2");
		verify(newPlayer, times(1)).sendMessage(anyString());
	}

	@Test
	public void changeOwnerCommandTestWithOtherNotOnline() {
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		Player newPlayer = mock(Player.class);
		Playershop shop = mock(Playershop.class);
		when(messageWrapper.getString(MessageEnum.SHOP_CHANGEOWNER1, "kthschnll")).thenReturn("my message1");
		when(newOwner.isOnline()).thenReturn(false);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopByUniqueName("myshop_catch441")).thenReturn(shop));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("kthschnll")).thenReturn(newOwner));
		String[] args = { "changeOwner", "myshop", "kthschnll" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).changeOwner(newOwner));
		verify(player).sendMessage("my message1");
		verify(player, times(1)).sendMessage(anyString());
		verifyNoInteractions(newPlayer);
	}

	@Test
	public void changeOwnerCommandTestWithHasSameShop() throws ShopsystemException {
		ShopsystemException e = mock(ShopsystemException.class);
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		Player newPlayer = mock(Player.class);
		Playershop shop = mock(Playershop.class);
		when(e.getMessage()).thenReturn("my error message");
		when(player.getName()).thenReturn("catch441");
		doThrow(e).when(shop).changeOwner(newOwner);
		assertDoesNotThrow(() -> when(playershopManager.getPlayerShopByUniqueName("myshop_catch441")).thenReturn(shop));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("kthschnll")).thenReturn(newOwner));
		String[] args = { "changeOwner", "myshop", "kthschnll" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verifyNoInteractions(newPlayer);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void deleteOtherCommandTestWithNoPermission() {
		when(player.hasPermission("ultimate_economy.adminshop")).thenReturn(false);
		String[] args = { "deleteOther", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void deleteOtherCommandTestWithInvalidArgNumber() {
		when(player.hasPermission("ultimate_economy.adminshop")).thenReturn(true);
		String[] args = { "deleteOther", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(player).sendMessage("/playershop deleteOther <shop>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteOtherCommandTest() {
		Playershop shop = mock(Playershop.class);
		when(messageWrapper.getString(MessageEnum.DELETED, "myshop_kthschnll")).thenReturn("my message");
		assertDoesNotThrow(
				() -> when(playershopManager.getPlayerShopByUniqueName("myshop_kthschnll")).thenReturn(shop));
		when(player.hasPermission("ultimate_economy.adminshop")).thenReturn(true);
		String[] args = { "deleteOther", "myshop_kthschnll" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		verify(playershopManager).deletePlayerShop(shop);
		verify(player).sendMessage("my message");
		verify(player, times(1)).sendMessage(anyString());
	}
}
