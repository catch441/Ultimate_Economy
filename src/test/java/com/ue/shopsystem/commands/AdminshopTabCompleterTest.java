package com.ue.shopsystem.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.api.AdminshopController;
import com.ue.shopsystem.commands.adminshop.AdminshopTabCompleter;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class AdminshopTabCompleterTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;
	private static AdminshopTabCompleter tabCompleter;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		player = server.addPlayer("kthschnll1");
		tabCompleter = new AdminshopTabCompleter();
		Location loc = new Location(world, 1, 2, 3);
		try {
			AdminshopController.createAdminShop("myshop1", loc, 9);
			AdminshopController.createAdminShop("myshop2", loc, 9);
		} catch (ShopSystemException | GeneralEconomyException e1) {
			fail();
		}
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		UltimateEconomy.getInstance.getDataFolder().delete();
		server.setPlayers(0);
		int size = AdminshopController.getAdminshopList().size();
		for (int i = 0; i < size; i++) {
			try {
				AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
			} catch (ShopSystemException e) {
				fail();
			}
		}
		MockBukkit.unload();
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
	}

	@Test
	public void zeroArgsTest() {
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
		assertEquals("addSpawner", list.get(7));
		assertEquals("removeSpawner", list.get(8));
	}

	@Test
	public void zeroArgsTestWithMatching() {
		String[] args = { "r" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(6, list.size());
		assertEquals("create", list.get(0));
		assertEquals("rename", list.get(1));
		assertEquals("resize", list.get(2));
		assertEquals("changeProfession", list.get(3));
		assertEquals("addSpawner", list.get(4));
		assertEquals("removeSpawner", list.get(5));
	}

	@Test
	public void deleteTest() {
		String[] args = { "delete", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void deleteTestWithMatching() {
		String[] args = { "delete", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void deleteTestWithMoreArgs() {
		String[] args = { "delete", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void editShopTest() {
		String[] args = { "editShop", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void editShopTestWithMatching() {
		String[] args = { "editShop", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void editShopTestWithMoreArgs() {
		String[] args = { "editShop", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void resizeTest() {
		String[] args = { "resize", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void resizeTestWithMatching() {
		String[] args = { "resize", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void resizeTestWithMoreArgs() {
		String[] args = { "resize", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void moveTest() {
		String[] args = { "move", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void moveTestWithMatching() {
		String[] args = { "move", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void moveTestWithMoreArgs() {
		String[] args = { "move", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void renameTest() {
		String[] args = { "rename", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void renameTestWithMatching() {
		String[] args = { "rename", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void renameTestWithMoreArgs() {
		String[] args = { "rename", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void removeSpawnerItemTest() {
		String[] args = { "removeSpawner", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void removeSpawnerTestWithMatching() {
		String[] args = { "removeSpawner", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void removeSpawnerTestWithMoreArgs() {
		String[] args = { "removeSpawner", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void addSpawnerTestTwoArgs() {
		String[] args = { "addSpawner", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void addSpawnerTestWithTwoArgsMatching() {
		String[] args = { "addSpawner", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void addSpawnerTestThreeArgs() {
		String[] args = { "addSpawner", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(108, list.size());
	}

	@Test
	public void addSpawnerTestWithThreeArgsMatching() {
		String[] args = { "addSpawner", "myshop1", "spide" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("spider", list.get(0));
		assertEquals("cave_spider", list.get(1));
	}

	@Test
	public void addSpawnerTestWithMoreArgs() {
		String[] args = { "addSpawner", "myshop1", "cow", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void changeProfessionTestTwoArgs() {
		String[] args = { "changeProfession", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}

	@Test
	public void changeProfessionTestWithTwoArgsMatching() {
		String[] args = { "changeProfession", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("myshop1", list.get(0));
	}

	@Test
	public void changeProfessionTestThreeArgs() {
		String[] args = { "changeProfession", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(15, list.size());
	}

	@Test
	public void changeProfessionTestWithThreeArgsMatching() {
		String[] args = { "changeProfession", "myshop1", "fletch" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("fletcher", list.get(0));
	}

	@Test
	public void changeProfessionTestWithMoreArgs() {
		String[] args = { "changeProfession", "myshop1", "fletcher", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
	}
}
