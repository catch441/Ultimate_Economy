package org.ue.shopsystem.logic.impl;

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

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.ShopsystemException;

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
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	JobManager jobManager;
	@Mock
	ConfigManager configManager;

	@Test
	public void unknownCommandTest() {
		String[] args = { "stuff" };
		boolean result = executor.onCommand(player, null, "dontknow", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void shopCommandTestMoreArgs() {
		String[] args = { "stuff", "more" };
		boolean result = executor.onCommand(player, null, "shop", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void shopCommandTestOneArgNotJoined() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		when(ecoPlayer.hasJob(job)).thenReturn(false);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getErrorString(ExceptionMessageEnum.JOB_NOT_JOINED)).thenReturn("my message");
		String[] args = { "myjob" };
		boolean result = executor.onCommand(player, null, "shop", args);
		assertTrue(result);
		verify(player).sendMessage("my message");
	}
	
	@Test
	public void shopCommandTestOneArg() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Job job = mock(Job.class);
		Adminshop adminshop = mock(Adminshop.class);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myjob")).thenReturn(adminshop));
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		String[] args = { "myjob" };
		boolean result = executor.onCommand(player, null, "shop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(adminshop).openInventory(player));
		verify(player, never()).sendMessage(anyString());
	}
	
	@Test
	public void shopCommandTestOneArgAllowQuickshopTrue() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Adminshop adminshop = mock(Adminshop.class);
		when(configManager.isAllowQuickshop()).thenReturn(true);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(adminshop));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		String[] args = { "myshop" };
		boolean result = executor.onCommand(player, null, "shop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(adminshop).openInventory(player));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void shoplistCommandTestMoreArgs() {
		String[] args = { "stuff" };
		boolean result = executor.onCommand(player, null, "shoplist", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void shoplistCommandTest() {
		String[] args = {};
		when(adminshopManager.getAdminshopNameList()).thenReturn(Arrays.asList("myshop"));
		when(messageWrapper.getString(MessageEnum.SHOPLIST_INFO, "[myshop]")).thenReturn("my message");
		boolean result = executor.onCommand(player, null, "shoplist", args);
		assertTrue(result);
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void unknownArgCommandTest() {
		String[] args = { "stuff" };
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
		when(messageWrapper.getErrorString(ExceptionMessageEnum.INVALID_PARAMETER, "number")).thenReturn("my error message");
		String[] args = { "create", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void createCommandTest() {
		when(messageWrapper.getString(MessageEnum.CREATED, "myshop")).thenReturn("my message");
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
	public void deleteCommandTestWithNoShop() throws ShopsystemException {
		ShopsystemException e = mock(ShopsystemException.class);
		doThrow(e).when(adminshopManager).getAdminShopByName("myshop");
		String[] args = { "delete", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(adminshopManager, never()).deleteAdminShop(any(Adminshop.class)));
	}

	@Test
	public void deleteCommandTest() {
		when(messageWrapper.getString(MessageEnum.DELETED, "myshop")).thenReturn("my message");
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
		when(messageWrapper.getString(MessageEnum.SHOP_RENAME, "myshop", "kth")).thenReturn("my message");
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
		assertDoesNotThrow(() -> verify(shop).changeLocation(loc));
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
		InventoryGuiHandler editor = mock(InventoryGuiHandler.class);
		Adminshop shop = mock(Adminshop.class);
		when(shop.getEditorHandler()).thenReturn(editor);
		assertDoesNotThrow(() -> when(adminshopManager.getAdminShopByName("myshop")).thenReturn(shop));
		String[] args = { "editShop", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(editor).openInventory(player));
		verifyNoInteractions(player);
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
		when(messageWrapper.getErrorString(ExceptionMessageEnum.INVALID_PARAMETER, "Farmer")).thenReturn("my error message");
		String[] args = { "addSpawner", "myshop", "Farmer", "1", "1" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verify(player, times(1)).sendMessage(anyString());
	}

	@Test
	public void addSpawnerCommandTest() {
		when(messageWrapper.getString(MessageEnum.ADDED, "cow")).thenReturn("my message");
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
