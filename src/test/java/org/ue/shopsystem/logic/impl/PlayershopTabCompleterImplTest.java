package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.ue.shopsystem.logic.api.PlayershopManager;

@ExtendWith(MockitoExtension.class)
public class PlayershopTabCompleterImplTest {

	@InjectMocks
	PlayershopTabCompleterImpl tabCompleter;
	@Mock
	PlayershopManager playershopManager;
	@Mock
	Player player;

	@Test
	public void zeroArgsAdminPermissionTest() {
		when(player.hasPermission("ultimate_economy.adminshop")).thenReturn(true);
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(9, list.size());
		assertEquals("create", list.get(0));
		assertEquals("delete", list.get(1));
		assertEquals("move", list.get(2));
		assertEquals("editShop", list.get(3));
		assertEquals("rename", list.get(4));
		assertEquals("resize", list.get(5));
		assertEquals("changeProfession", list.get(6));
		assertEquals("changeOwner", list.get(7));
		assertEquals("deleteOther", list.get(8));
	}

	@Test
	public void zeroArgsPlayerPermissionTest() {
		when(player.hasPermission("ultimate_economy.adminshop")).thenReturn(false);
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(8, list.size());
		assertEquals("create", list.get(0));
		assertEquals("delete", list.get(1));
		assertEquals("move", list.get(2));
		assertEquals("editShop", list.get(3));
		assertEquals("rename", list.get(4));
		assertEquals("resize", list.get(5));
		assertEquals("changeProfession", list.get(6));
		assertEquals("changeOwner", list.get(7));
	}

	@Test
	public void zeroArgsPlayerPermissionTestWithMatching() {
		String[] args = { "n" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(3, list.size());
		assertEquals("rename", list.get(0));
		assertEquals("changeProfession", list.get(1));
		assertEquals("changeOwner", list.get(2));
	}

	@Test
	public void zeroArgsTestWithMoreArgs() {
		String[] args = { "kth", "schnll" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void changeProfessionTestWithTwoArgs() {
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(Arrays.asList("myshop1_catch441", "myshop2_catch441", "myshop3_kthschnll"));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "changeProfession", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void changeProfessionTestWithTwoArgsMatching() {
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(Arrays.asList("myshop1_catch441", "myshop2_kthschnll", "myshop3_kthschnll"));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "changeProfession", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void changeProfessionTestWithThreeArgs() {
		String[] args = { "changeProfession", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(Profession.values().length, list.size());
		for (int i = 0; i < Profession.values().length; i++) {
			assertEquals(Profession.values()[i].name().toLowerCase(), list.get(i));
		}
	}

	@Test
	public void changeProfessionTestWithThreeArgsMatching() {
		String[] args = { "changeProfession", "myshop1", "flet" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("fletcher", list.get(0));
	}

	@Test
	public void changeProfessionTestWithMoreArgs() {
		String[] args = { "changeProfession", "myshop1", "fletcher", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void createArgTest() {
		String[] args = { "create" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void deleteOtherArgTest() {
		String[] args = { "deleteOther" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void deleteOtherArgTestWithMoreArgs() {
		String[] args = { "deleteOther", "kth", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void deleteOtherArgTestWithTwoArgs() {
		when(playershopManager.getPlayerShopUniqueNameList()).thenReturn(Arrays.asList("myshop1_kthschnll", "myshop2_kthschnll"));
		String[] args = { "deleteOther", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1_kthschnll", list.get(0));
		assertEquals("myshop2_kthschnll", list.get(1));
	}

	@Test
	public void deleteArgTestWithTwoArgs() {
		when(playershopManager.getPlayerShopUniqueNameList())
				.thenReturn(Arrays.asList("myshop1_catch441", "myshop2_catch441", "myshop3_kthschnll"));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "delete", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void deleteArgTestWithMoreArgs() {
		String[] args = { "delete", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void deleteArgTestWithOneArg() {
		String[] args = { "delete" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void moveArgTestWithTwoArgs() {
		when(playershopManager.getPlayerShopUniqueNameList())
				.thenReturn(Arrays.asList("myshop1_catch441", "myshop2_catch441", "myshop3_kthschnll"));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "move", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void moveArgTestWithMoreArgs() {
		String[] args = { "move", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void moveArgTestWithOneArg() {
		String[] args = { "move" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void editShopArgTestWithTwoArgs() {
		when(playershopManager.getPlayerShopUniqueNameList())
				.thenReturn(Arrays.asList("myshop1_catch441", "myshop2_catch441", "myshop3_kthschnll"));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "editShop", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void editShopArgTestWithMoreArgs() {
		String[] args = { "editShop", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void editShopArgTestWithOneArg() {
		String[] args = { "editShop" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void resizeShopArgTestWithTwoArgs() {
		when(playershopManager.getPlayerShopUniqueNameList())
				.thenReturn(Arrays.asList("myshop1_catch441", "myshop2_catch441", "myshop3_kthschnll"));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "resize", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void resizeShopArgTestWithMoreArgs() {
		String[] args = { "resize", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void resizeShopArgTestWithOneArg() {
		String[] args = { "resize" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void changeOwnerShopArgTestWithTwoArgs() {
		when(playershopManager.getPlayerShopUniqueNameList())
				.thenReturn(Arrays.asList("myshop1_catch441", "myshop2_catch441", "myshop3_kthschnll"));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "changeOwner", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void changeOwnerShopArgTestWithMoreArgs() {
		String[] args = { "changeOwner", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void changeOwnerShopArgTestWithOneArg() {
		String[] args = { "changeOwner" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void renameOwnerShopArgTestWithTwoArgs() {
		when(playershopManager.getPlayerShopUniqueNameList())
				.thenReturn(Arrays.asList("myshop1_catch441", "myshop2_catch441", "myshop3_kthschnll"));
		when(player.getName()).thenReturn("catch441");
		String[] args = { "rename", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void renameOwnerShopArgTestWithMoreArgs() {
		String[] args = { "rename", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void renameOwnerShopArgTestWithOneArg() {
		String[] args = { "rename" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}
}
