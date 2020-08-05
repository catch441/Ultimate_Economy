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

import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.impl.RentshopCommandExecutorImpl;
import com.ue.shopsystem.logic.impl.RentshopManagerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class RentshopCommandExecutorTest {

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
		EconomyPlayerManagerImpl.getAllEconomyPlayers().clear();
		player = server.addPlayer("kthschnll");
		other = server.addPlayer("catch441");
		executor = new RentshopCommandExecutorImpl();
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		int size2 = EconomyPlayerManagerImpl.getAllEconomyPlayers().size();
		for (int i = 0; i < size2; i++) {
			EconomyPlayerManagerImpl.deleteEconomyPlayer(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
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
		int size = RentshopManagerImpl.getRentShops().size();
		for (int i = 0; i < size; i++) {
			RentshopManagerImpl.deleteRentShop(RentshopManagerImpl.getRentShops().get(0));
		}
	}
	
	@Test
	public void zeroArgsTestWithAdminPermission() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("/rentshop [create/delete/move/resize/editShop]", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void zeroArgsTestWithPlayerPermission() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", false);
		String[] args = { };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("/rentshop [editShop]", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void unknownCommandTest() {
		String[] args = { "kthschnll" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertFalse(result);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void createCommandTestWithNoPermission() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", false);
		String[] args = { "create", "9", "10" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void createCommandTestWithInvalidArgNumber() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "create", "9", "10", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("/rentshop create <size> <rentalFee per 24h>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void createCommandTest() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "create", "18", "4" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		Rentshop shop = RentshopManagerImpl.getRentShops().get(0);
		assertEquals(18, shop.getSize());
		assertEquals("4.0", String.valueOf(shop.getRentalFee()));
		assertEquals(1, RentshopManagerImpl.getRentShops().size());
		assertEquals("§6The shop §aRentShop#R0§6 was created.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteCommandTestWithNoPermission() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", false);
		String[] args = { "delete", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteCommandTestWithInvalidArgNumber() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "delete", "RentShop#R0", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("/rentshop delete <shopname>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void deleteCommandTest() {
		createRentshop();
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "delete", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals(0, RentshopManagerImpl.getRentShops().size());
		assertEquals("§6The shop §aRentShop#R0§6 was deleted.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void moveCommandTestWithNoPermission() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", false);
		String[] args = { "move", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void moveCommandTestWithInvalidArgNumber() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "move", "RentShop#R0", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("/rentshop move <shopname>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void moveCommandTest() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "move", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		Rentshop shop = RentshopManagerImpl.getRentShops().get(0);
		assertEquals(loc, shop.getShopLocation());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTestWithNoPermission() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", false);
		String[] args = { "resize", "RentShop#R0", "9" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTestWithInvalidArgNumber() {
		createRentshop();
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "resize", "RentShop#R0", "9", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("/rentshop resize <shopname> <new size>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTestWithInvalidSize() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "resize", "RentShop#R0", "16" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §416§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTestWithInvalidSize2() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "resize", "RentShop#R0", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4number§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void resizeCommandTest() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "resize", "RentShop#R0", "9" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		Rentshop shop = RentshopManagerImpl.getRentShops().get(0);
		assertEquals(9, shop.getSize());
		assertEquals("§6You changed the shop size to §a9§6.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeProfessionCommandTestWithInvalidArgNumber() {
		String[] args = { "changeProfession", "Shop#R0_1catch441", "9", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("/rentshop changeProfession <shopname> <profession>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeProfessionCommandTestWithInvalidProfession() {
		createRentshop();
		String[] args = { "changeProfession", "RentShop#R0", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4kth§c is invalid!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeProfessionCommandTestRented() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		String[] args = { "changeProfession", "Shop#R0", "Farmer" };
		Rentshop shop = RentshopManagerImpl.getRentShops().get(0);
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(100, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("§6The profession of the shop villager has been successfully changed!", player.nextMessage());
		assertEquals(Profession.FARMER, shop.getShopVillager().getProfession());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void changeProfessionCommandTestNotRented() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		String[] args = { "changeProfession", "RentShop#R0", "Farmer" };
		Rentshop shop = RentshopManagerImpl.getRentShops().get(0);
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("§6The profession of the shop villager has been successfully changed!", player.nextMessage());
		assertEquals(Profession.FARMER, shop.getShopVillager().getProfession());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void renameCommandTestWithInvalidArgNumber() {
		String[] args = { "rename", "Shop#R0_1catch441", "9", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("/rentshop rename <oldName> <newName>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void renameCommandTestRented() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		String[] args = { "rename", "Shop#R0", "NewName" };
		Rentshop shop = RentshopManagerImpl.getRentShops().get(0);
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(100, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("NewName", shop.getName());
		assertEquals("§6You changed the shop name from §aShop#R0§6 to §aNewName§6.", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void renameCommandTestRentedWithNotOwner() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		String[] args = { "rename", "Shop#R0", "NewName" };
		Rentshop shop = RentshopManagerImpl.getRentShops().get(0);
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(100, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
		boolean result = executor.onCommand(other, null, "rentshop", args);
		assertTrue(result);
		assertEquals("Shop#R0", shop.getName());
		assertEquals("§c§4Shop#R0§c does not exist!", other.nextMessage());
		assertNull(other.nextMessage());
	}
	
	@Test
	public void renameCommandTestNotRented() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		String[] args = { "rename", "RentShop#R0", "NewName" };
		Rentshop shop = RentshopManagerImpl.getRentShops().get(0);
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("§6You changed the shop name from §aRentShop#R0§6 to §aNewName§6.", player.nextMessage());
		assertEquals("NewName", shop.getName());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void editShopCommandTestWithInvalidArgNumber() {
		String[] args = { "editShop", "Shop#R0_1catch441", "9", "kth" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("/rentshop editShop <shopname>", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	@Test
	public void editShopCommandTestRented() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		String[] args = { "editShop", "Shop#R0" };
		Rentshop shop = RentshopManagerImpl.getRentShops().get(0);
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(100, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertEquals("Shop#R0-Editor", ((ChestInventoryMock) player.getOpenInventory().getTopInventory()).getName());
		assertNull(player.nextMessage());
		player.closeInventory();
	}
	
	@Test
	public void editShopCommandTestNotOwner() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		String[] args = { "editShop", "Shop#R0" };
		Rentshop shop = RentshopManagerImpl.getRentShops().get(0);
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(100, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
		boolean result = executor.onCommand(other, null, "rentshop", args);
		assertTrue(result);
		assertNull(other.getOpenInventory().getTopInventory());
		assertEquals("§c§4Shop#R0§c does not exist!", other.nextMessage());
		assertNull(other.nextMessage());
	}
	
	@Test
	public void editShopCommandTestNotRented() {
		createRentshop();
		Location loc = new Location(world, 10,11,12);
		player.setLocation(loc);
		String[] args = { "editShop", "RentShop#R0" };
		boolean result = executor.onCommand(player, null, "rentshop", args);
		assertTrue(result);
		assertNull(player.getOpenInventory().getTopInventory());
		assertEquals("§cThe shop is not rented!", player.nextMessage());
		assertNull(player.nextMessage());
	}
	
	private void createRentshop() {
		try {
			RentshopManagerImpl.createRentShop(new Location(world, 6, 5, 2), 18, 4);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
}
