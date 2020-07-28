package com.ue.shopsystem.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Villager.Profession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.api.AdminshopController;
import com.ue.shopsystem.commands.adminshop.AdminshopCommandExecutor;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class AdminshopCommandExecutorTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;
	private static CommandExecutor executor;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		server.setPlayers(0);
		EconomyPlayerController.getAllEconomyPlayers().clear();
		player = server.addPlayer("kthschnll");
		executor = new AdminshopCommandExecutor();
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		int size2 = EconomyPlayerController.getAllEconomyPlayers().size();
		for (int i = 0; i < size2; i++) {
			EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
		}
		UltimateEconomy.getInstance.getDataFolder().delete();
		server.setPlayers(0);
		MockBukkit.unload();
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		int size = AdminshopController.getAdminshopList().size();
		for (int i = 0; i < size; i++) {
			try {
				AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
			} catch (ShopSystemException e) {
				fail();
			}
		}
	}
	
	@Test
	public void unknownCommandTest() {
		String[] args = { "kthschnll" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertFalse(result);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void zeroCommandTest() {
		String[] args = { };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertFalse(result);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void createCommandTestWithInvalidArgNumber() {
		String[] args = { "create" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("/adminshop create <shopname> <size> <- size have to be a multible of 9", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void createCommandTestWithInvalidNumber() {
		String[] args = { "create", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4number§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void createCommandTest() {
		String[] args = { "create", "myshop", "9" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals(1, AdminshopController.getAdminshopList().size());
		assertEquals("myshop", AdminshopController.getAdminshopList().get(0).getName());
		assertEquals(9, AdminshopController.getAdminshopList().get(0).getSize());
		assertEquals("§6The shop §amyshop§6 was created.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteCommandTestWithInvalidArgNumber() {
		String[] args = { "delete", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("/adminshop delete <shopname>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteCommandTestWithNoShop() {
		String[] args = { "delete", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("§c§4myshop§c does not exist!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteCommandTest() {
		createAdminshop();
		String[] args = { "delete", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals(0, AdminshopController.getAdminshopList().size());
		assertEquals("§6The shop §amyshop§6 was deleted.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void renameCommandTestWithInvalidArgNumber() {
		String[] args = { "rename", "myshop", "kth", "schnll" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("/adminshop rename <oldName> <newName>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void renameCommandTest() {
		createAdminshop();
		String[] args = { "rename", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("kth", AdminshopController.getAdminshopList().get(0).getName());
		assertEquals("§6You changed the shop name from §amyshop§6 to §akth§6.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTestWithInvalidArgNumber() {
		String[] args = { "resize", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("/adminshop resize <shopname> <new size>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTestWithInvalidNumber() {
		createAdminshop();
		String[] args = { "resize", "myshop", "2" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §42§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTest() {
		createAdminshop();
		String[] args = { "resize", "myshop", "18" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals(18, AdminshopController.getAdminshopList().get(0).getSize());
		assertEquals("§6You changed the shop size to §a18§6.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void moveCommandTestWithInvalidArgNumber() {
		String[] args = { "move", "myshop", "2" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("/adminshop move <shopname>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void moveCommandTest() {
		createAdminshop();
		Location loc = new Location(world, 5,5,5);
		player.setLocation(loc);
		String[] args = { "move", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals(loc, AdminshopController.getAdminshopList().get(0).getShopLocation());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void editShopCommandTestWithInvalidArgNumber() {
		String[] args = { "editShop", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("/adminshop editShop <shopname>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void editShopCommandTest() {
		createAdminshop();
		String[] args = { "editShop", "myshop" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("myshop-Editor", ((ChestInventoryMock) player.getOpenInventory().getTopInventory()).getName());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeProfessionCommandTestWithInvalidArgNumber() {
		String[] args = { "changeProfession", "myshop", "Farmer", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("/adminshop changeProfession <shopname> <profession>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeProfessionCommandTestWithInvalidProfession() {
		createAdminshop();
		String[] args = { "changeProfession", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4kth§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeProfessionCommandTest() {
		createAdminshop();
		String[] args = { "changeProfession", "myshop", "Farmer" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals(Profession.FARMER, AdminshopController.getAdminshopList().get(0).getShopVillager().getProfession());
		assertEquals("§6The profession of the shop villager has been successfully changed!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void addSpawnerCommandTestWithInvalidArgNumber() {
		String[] args = { "addSpawner", "myshop", "Farmer", "kth" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("/adminshop addSpawner <shopname> <entity type> <slot> <buyPrice>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void addSpawnerCommandTestWithInvalidEntity() {
		createAdminshop();
		String[] args = { "addSpawner", "myshop", "Farmer", "1", "1" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4Farmer§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void addSpawnerCommandTest() {
		createAdminshop();
		String[] args = { "addSpawner", "myshop", "cow", "1", "1" };
		boolean result = executor.onCommand(player, null, "adminshop", args);
		assertTrue(result);
		try {
			assertEquals("SPAWNER_COW", AdminshopController.getAdminshopList().get(0).getShopItem(0).getItemString());
			assertEquals("1.0", String.valueOf(AdminshopController.getAdminshopList().get(0).getShopItem(0).getBuyPrice()));
		} catch (GeneralEconomyException | ShopSystemException e) {
			fail();
		}
		assertEquals("§6The spawner §acow§6 was added to the shop.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	private void createAdminshop() {
		try {
			AdminshopController.createAdminShop("myshop", new Location(world, 8, 9, 1), 9);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}
}
