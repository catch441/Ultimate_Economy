package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;

@ExtendWith(MockitoExtension.class)
public class RentshopTabCompleterImplTest {

	@InjectMocks
	RentshopTabCompleterImpl tabCompleter;
	@Mock
	RentshopManager rentshopManager;
	@Mock
	Player player;

	@Test
	public void zeroArgsPlayerPermissionTest() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(false);
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("rename", list.get(0));
	}

	@Test
	public void zeroArgsAdminPermissionTest() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(5, list.size());
		assertEquals("rename", list.get(0));
		assertEquals("create", list.get(1));
		assertEquals("delete", list.get(2));
		assertEquals("move", list.get(3));
		assertEquals("editShop", list.get(4));
	}

	@Test
	public void zeroArgsPlayerPermissionTestWithMatching() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(false);
		String[] args = { "n" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("rename", list.get(0));
	}

	@Test
	public void zeroArgsAdminPermissionTestWithMatching() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "r" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("rename", list.get(0));
		assertEquals("create", list.get(1));	
	}

	@Test
	public void zeroArgsWithMoreArgs() {
		String[] args = { "ds", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void createArgTest() {
		String[] args = { "create" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void createArgTestWithMoreArgs() {
		String[] args = { "create", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void editShopArgTestWithTwoArgs() {
		prepareTwoShops();
		String[] args = { "editShop", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("Shop#R0", list.get(0));
		assertEquals("Shop#R1", list.get(1));
	}

	@Test
	public void renameArgTestWithTwoArgs() {
		prepareTwoShops();
		String[] args = { "rename", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("Shop#R0", list.get(0));
		assertEquals("Shop#R1", list.get(1));
	}

	@Test
	public void editShopArgTestWithMoreArgs() {
		String[] args = { "editShop", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void renameArgTestWithMoreArgs() {
		String[] args = { "rename", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void deleteArgTestWithMoreArgs() {
		String[] args = { "delete", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void moveArgTestWithMoreArgs() {
		String[] args = { "move", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void deleteArgTestWithTwoArgs() {
		when(rentshopManager.getRentShopUniqueNameList())
		.thenReturn(Arrays.asList("Shop#R0_catch441", "Shop#R1_catch441", "RentShop#R2"));
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "delete", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(3, list.size());
		assertEquals("Shop#R0_catch441", list.get(0));
		assertEquals("Shop#R1_catch441", list.get(1));
		assertEquals("RentShop#R2", list.get(2));
	}

	@Test
	public void moveArgTestWithTwoArgs() {
		when(rentshopManager.getRentShopUniqueNameList())
		.thenReturn(Arrays.asList("Shop#R0_catch441", "Shop#R1_catch441", "RentShop#R2"));
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "move", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(3, list.size());
		assertEquals("Shop#R0_catch441", list.get(0));
		assertEquals("Shop#R1_catch441", list.get(1));
		assertEquals("RentShop#R2", list.get(2));
	}

	private void prepareTwoShops() {
		Rentshop shop1 = mock(Rentshop.class);
		Rentshop shop2 = mock(Rentshop.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(rentshopManager.getRentShops()).thenReturn(Arrays.asList(shop1, shop2));
		when(shop1.getOwner()).thenReturn(ecoPlayer);
		when(shop2.getOwner()).thenReturn(ecoPlayer);
		when(ecoPlayer.getName()).thenReturn("catch441");
		when(player.getName()).thenReturn("catch441");
		when(shop1.isRentable()).thenReturn(false);
		when(shop2.isRentable()).thenReturn(false);
		when(shop1.getName()).thenReturn("Shop#R0");
		when(shop2.getName()).thenReturn("Shop#R1");
	}
}
