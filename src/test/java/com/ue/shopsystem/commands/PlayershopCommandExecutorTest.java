package com.ue.shopsystem.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.api.Playershop;
import com.ue.shopsystem.api.PlayershopController;
import com.ue.shopsystem.commands.playershop.PlayershopCommandExecutor;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class PlayershopCommandExecutorTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player, other;
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
		player = server.addPlayer("1catch441");
		other = server.addPlayer("1kthschnll");
		executor = new PlayershopCommandExecutor();
		PlayershopController.getPlayerShops().clear();
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
		int size = PlayershopController.getPlayerShops().size();
		for (int i = 0; i < size; i++) {
			PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
		}
	}
	
	@Test
	public void zeroArgsTest() {
		String[] args = { };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertFalse(result);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void createCommandTest() {
		String[] args = { "create", "myshop", "9" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		Playershop shop = PlayershopController.getPlayerShops().get(0);
		assertTrue(result);
		assertEquals("myshop", shop.getName());
		assertEquals(9, shop.getSize());
		assertEquals(player.getName(), shop.getOwner().getName());
		assertEquals("§6The shop §amyshop§6 was created.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void createCommandTestWithInvalidArgumentNumber() {
		String[] args = { "create", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("/playershop create <shop> <size> <- size have to be a multible of 9", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void createCommandTestWithInvalidNumber() {
		String[] args = { "create", "myshop", "dsa" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4dsa§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void createCommandTestWithInvalidNumber2() {
		String[] args = { "create", "myshop", "6" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §46§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteCommandTestWithInvalidArgumentNumber() {
		String[] args = { "delete" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("/playershop delete <shop>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteCommandTest() {
		createPlayershop(0);
		String[] args = { "delete", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals(0, PlayershopController.getPlayerShops().size());
		assertEquals("§6The shop §amyshop§6 was deleted.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteCommandTestWithNoShop() {
		String[] args = { "delete", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("§c§4myshop_1catch441§c does not exist!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void renameCommandTest() {
		createPlayershop(0);
		String[] args = { "rename", "myshop", "mynewshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		Playershop shop = PlayershopController.getPlayerShops().get(0);
		assertEquals("mynewshop", shop.getName());
		assertEquals("§6You changed the shop name from §amyshop§6 to §amynewshop§6.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void renameCommandTestWithInvalidName() {
		createPlayershop(0);
		String[] args = { "rename", "myshop", "myshop" };
		assertNull(player.nextMessage());
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("§c§4myshop_1catch441§c already exists!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void renameCommandTestWithOtherPlayershop() {
		createPlayershop(1);
		String[] args = { "rename", "myshop", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("§c§4myshop_1catch441§c does not exist!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void renameCommandTestWithInvalidArgumentNumber() {
		String[] args = { "rename", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("/playershop rename <oldName> <newName>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTestWithInvalidArgumentNumber() {
		String[] args = { "resize", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("/playershop resize <shop> <new size>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTest() {
		createPlayershop(0);
		String[] args = { "resize", "myshop", "27" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		Playershop shop = PlayershopController.getPlayerShops().get(0);
		assertEquals(27, shop.getSize());
		assertEquals("§6You changed the shop size to §a27§6.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTestWithInvalidSize() {
		createPlayershop(0);
		String[] args = { "resize", "myshop", "7" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §47§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeProfessionCommandTestWithInvalidProfession() {
		createPlayershop(0);
		String[] args = { "changeProfession", "myshop", "kardoffl" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4kardoffl§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeProfessionCommandTestWithInvalidArgNumber() {
		String[] args = { "changeProfession", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("/playershop changeProfession <shop> <profession>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeProfessionCommandTest() {
		createPlayershop(0);
		String[] args = { "changeProfession", "myshop", "Farmer" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		Playershop shop = PlayershopController.getPlayerShops().get(0);
		assertEquals(Profession.FARMER, shop.getShopVillager().getProfession());
		assertEquals("§6The profession of the shop villager has been successfully changed!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void editShopCommandTest() {
		createPlayershop(0);
		String[] args = { "editShop", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("myshop-Editor", ((ChestInventoryMock) player.getOpenInventory().getTopInventory()).getName());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void editShopCommandTestWithInvalidArgsNumber() {
		String[] args = { "editShop", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("/playershop editShop <shop>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void unknownCommandTest() {
		String[] args = { "kth" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertFalse(result);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void moveCommandTestWithInvalidArgsNumber() {
		String[] args = { "move", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("/playershop move <shop>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void moveCommandTest() {
		createPlayershop(0);
		Location loc = new Location(world, 20,45,1);
		player.setLocation(loc);
		String[] args = { "move", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		Playershop shop = PlayershopController.getPlayerShops().get(0);
		assertEquals(loc, shop.getShopLocation());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeOwnerCommandTestWithInvalidArgNumber() {
		String[] args = { "changeOwner", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("/playershop changeOwner <shop> <new owner>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeOwnerCommandTest() {
		createPlayershop(0);
		String[] args = { "changeOwner", "myshop", "1kthschnll" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		Playershop shop = PlayershopController.getPlayerShops().get(0);
		try {
			assertEquals(EconomyPlayerController.getEconomyPlayerByName("1kthschnll"), shop.getOwner());
		} catch (PlayerException e) {
			fail();
		}
		assertEquals("§6You got the shop §amyshop§6 from §a1catch441§6.", other.nextMessage());
		assertNull(other.nextMessage());
		assertEquals("§6The new owner of your shop is §a1kthschnll§6.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeOwnerCommandTestWithOtherNotOnline() {
		createPlayershop(0);
		EconomyPlayerController.getAllEconomyPlayers().get(1).setPlayer(null);
		String[] args = { "changeOwner", "myshop", "1kthschnll" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		Playershop shop = PlayershopController.getPlayerShops().get(0);
		try {
			assertEquals(EconomyPlayerController.getEconomyPlayerByName("1kthschnll"), shop.getOwner());
		} catch (PlayerException e) {
			fail();
		}
		assertNull(other.nextMessage());
		assertEquals("§6The new owner of your shop is §a1kthschnll§6.", player.nextMessage());
		assertNull(player.nextMessage());
		EconomyPlayerController.getAllEconomyPlayers().get(0).setPlayer(other);
	}
	
	@Test
	public void changeOwnerCommandTestWithHasSameShop() {
		createPlayershop(0);
		createPlayershop(1);
		String[] args = { "changeOwner", "myshop", "1kthschnll" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertNull(other.nextMessage());
		assertEquals("§cThe player has already a shop with the same name!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteOtherCommandTestWithNoPermission() {
		createPlayershop(0);
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.adminshop", false);
		String[] args = { "deleteOther", "myshop" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteOtherCommandTestWithInvalidArgNumber() {
		createPlayershop(0);
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.adminshop", true);
		String[] args = { "deleteOther", "myshop", "kth" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals("/playershop deleteOther <shop>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteOtherCommandTest() {
		createPlayershop(0);
		createPlayershop(1);
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.adminshop", true);
		String[] args = { "deleteOther", "myshop_1kthschnll" };
		boolean result = executor.onCommand(player, null, "playershop", args);
		assertTrue(result);
		assertEquals(1, PlayershopController.getPlayerShops().size());
		Playershop shop = PlayershopController.getPlayerShops().get(0);
		assertEquals("1catch441", shop.getOwner().getName());
		assertEquals("§6The shop §amyshop_1kthschnll§6 was deleted.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	private void createPlayershop(int player) {
		Location loc = new Location(world, 1, 2, 30);
		try {
			PlayershopController.createPlayerShop("myshop", loc, 9, EconomyPlayerController.getAllEconomyPlayers().get(player));
		} catch (ShopSystemException | TownSystemException | PlayerException | GeneralEconomyException e) {
			fail();
		}
	}
}
