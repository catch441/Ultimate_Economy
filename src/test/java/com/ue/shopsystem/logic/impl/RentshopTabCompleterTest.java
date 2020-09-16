package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.shopsystem.logic.impl.RentshopTabCompleterImpl;

@ExtendWith(MockitoExtension.class)
public class RentshopTabCompleterTest {

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
		assertEquals(3, list.size());
		assertEquals("rename", list.get(0));
		assertEquals("editShop", list.get(1));
		assertEquals("changeProfession", list.get(2));
	}

	@Test
	public void zeroArgsAdminPermissionTest() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(7, list.size());
		assertEquals("rename", list.get(0));
		assertEquals("editShop", list.get(1));
		assertEquals("changeProfession", list.get(2));
		assertEquals("create", list.get(3));
		assertEquals("delete", list.get(4));
		assertEquals("move", list.get(5));
		assertEquals("resize", list.get(6));
	}

	@Test
	public void zeroArgsPlayerPermissionTestWithMatching() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(false);
		String[] args = { "n" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("rename", list.get(0));
		assertEquals("changeProfession", list.get(1));
	}

	@Test
	public void zeroArgsAdminPermissionTestWithMatching() {
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "r" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(4, list.size());
		assertEquals("rename", list.get(0));
		assertEquals("changeProfession", list.get(1));
		assertEquals("create", list.get(2));
		assertEquals("resize", list.get(3));
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
	public void changeProfessionTestWithTwoArgs() {
		prepareTwoShops();
		String[] args = { "changeProfession", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("Shop#R0", list.get(0));
		assertEquals("Shop#R1", list.get(1));
	}

	@Test
	public void changeProfessionTestWithTwoArgsMatching() {
		prepareTwoShops();
		String[] args = { "changeProfession", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("Shop#R1", list.get(0));
	}

	@Test
	public void changeProfessionTestWithThreeArgs() {
		String[] args = { "changeProfession", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(Profession.values().length, list.size());
		for (int i = 0; i < Profession.values().length; i++) {
			assertEquals(Profession.values()[i].name().toLowerCase(), list.get(i));
		}
	}

	@Test
	public void changeProfessionTestWithThreeArgsMatching() {
		String[] args = { "changeProfession", "Shop#R0", "flet" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("fletcher", list.get(0));
	}

	@Test
	public void changeProfessionTestWithMoreArgs() {
		String[] args = { "changeProfession", "Shop#R0", "fletcher", "" };
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
	public void resizeArgTestWithMoreArgs() {
		String[] args = { "resize", "Shop#R0", "" };
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

	@Test
	public void resizeArgTestWithTwoArgs() {
		when(rentshopManager.getRentShopUniqueNameList())
				.thenReturn(Arrays.asList("Shop#R0_catch441", "Shop#R1_catch441", "RentShop#R2"));
		when(player.hasPermission("ultimate_economy.rentshop.admin")).thenReturn(true);
		String[] args = { "resize", "" };
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
