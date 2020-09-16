package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.logic.api.Adminshop;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.impl.AdminshopCommandExecutorImpl;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;

@ExtendWith(MockitoExtension.class)
public class AdminshopCommandExecutorImplTest {

	@InjectMocks
	AdminshopCommandExecutorImpl executor;
	@Mock
	AdminshopManager adminshopManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ServerProvider serverProvider;
	@Mock
	Player player;

	@Test
	public void unknownCommandTest() {
		String[] args = { "kthschnll" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void zeroCommandTest() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void createCommandTestWithInvalidArgNumber() {
		String[] args = { "create" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("/adminshop create <shopname> <size> <- size have to be a multible of 9");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void createCommandTestWithInvalidNumber() {
		when(messageWrapper.getErrorString("invalid_parameter", "number")).thenReturn("my error message");
		String[] args = { "create", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void createCommandTest() {
		when(messageWrapper.getString("shop_create", "myshop")).thenReturn("my message");
		Location location = mock(Location.class);
		when(player.getLocation()).thenReturn(location);
		String[] args = { "create", "myshop", "9" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(adminshopManager).createAdminShop("myshop", location, 9));
		verify(player).sendMessage("my message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void deleteCommandTestWithInvalidArgNumber() {
		String[] args = { "delete", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("/adminshop delete <shopname>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteCommandTestWithNoShop() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		doThrow(e).when(adminshopManager).getAdminShopByName("myshop");
		String[] args = { "delete", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(adminshopManager, never()).deleteAdminShop(any(Adminshop.class)));
	}

	@Test
	public void deleteCommandTest() {
		when(messageWrapper.getString("shop_delete", "myshop")).thenReturn("my message");
		Adminshop shop = mock(Adminshop.class);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(shop));
		String[] args = { "delete", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(adminshopManager).deleteAdminShop(shop));
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void renameCommandTestWithInvalidArgNumber() {
		String[] args = { "rename", "myshop", "kth", "schnll" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("/adminshop rename <oldName> <newName>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void renameCommandTest() {
		when(messageWrapper.getString("shop_rename", "myshop", "kth")).thenReturn("my message");
		Adminshop shop = mock(Adminshop.class);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(shop));
		String[] args = { "rename", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).changeShopName("kth"));
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void resizeCommandTestWithInvalidArgNumber() {
		String[] args = { "resize", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("/adminshop resize <shopname> <new size>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void resizeCommandTestWithInvalidNumber()
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		Adminshop shop = mock(Adminshop.class);
		doThrow(e).when(shop).changeShopSize(2);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(shop));
		String[] args = { "resize", "myshop", "2" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void resizeCommandTest() {
		when(messageWrapper.getString("shop_resize", "18")).thenReturn("my message");
		Adminshop shop = mock(Adminshop.class);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(shop));
		String[] args = { "resize", "myshop", "18" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).changeShopSize(18));
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void moveCommandTestWithInvalidArgNumber() {
		String[] args = { "move", "myshop", "2" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("/adminshop move <shopname>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void moveCommandTest() {
		Adminshop shop = mock(Adminshop.class);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(shop));
		Location loc = mock(Location.class);
		when(player.getLocation()).thenReturn(loc);
		String[] args = { "move", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).moveShop(loc));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void editShopCommandTestWithInvalidArgNumber() {
		String[] args = { "editShop", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("/adminshop editShop <shopname>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void editShopCommandTest() {
		Adminshop shop = mock(Adminshop.class);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(shop));
		String[] args = { "editShop", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(shop).openEditor(player));
		verifyNoInteractions(player);
	}

	@Test
	public void changeProfessionCommandTestWithInvalidArgNumber() {
		String[] args = { "changeProfession", "myshop", "Farmer", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("/adminshop changeProfession <shopname> <profession>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void changeProfessionCommandTestWithInvalidProfession() {
		when(messageWrapper.getErrorString("invalid_parameter", "kth")).thenReturn("my error message");
		Adminshop shop = mock(Adminshop.class);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(shop));
		String[] args = { "changeProfession", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void changeProfessionCommandTest() {
		when(messageWrapper.getString("profession_changed")).thenReturn("my message");
		Adminshop shop = mock(Adminshop.class);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(shop));
		String[] args = { "changeProfession", "myshop", "Farmer" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(shop).changeProfession(Profession.FARMER);
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void addSpawnerCommandTestWithInvalidArgNumber() {
		String[] args = { "addSpawner", "myshop", "Farmer", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("/adminshop addSpawner <shopname> <entity type> <slot> <buyPrice>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void addSpawnerCommandTestWithInvalidEntity() {
		when(messageWrapper.getErrorString("invalid_parameter", "Farmer")).thenReturn("my error message");
		String[] args = { "addSpawner", "myshop", "Farmer", "1", "1" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void addSpawnerCommandTest() {
		when(messageWrapper.getString("shop_addSpawner", "cow")).thenReturn("my message");
		Adminshop shop = mock(Adminshop.class);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(shop));
		ItemMeta meta = mock(ItemMeta.class);
		ItemStack stack = mock(ItemStack.class);
		when(serverProvider.createItemStack(Material.SPAWNER, 1)).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		String[] args = { "addSpawner", "myshop", "cow", "1", "1" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(meta).setDisplayName("COW");
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
		assertDoesNotThrow(() -> verify(shop).addShopItem(eq(0), eq(0.0), eq(1.0), eq(stack)));
	}
}
