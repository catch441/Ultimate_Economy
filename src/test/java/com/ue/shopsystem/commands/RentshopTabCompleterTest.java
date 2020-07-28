package com.ue.shopsystem.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Villager.Profession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.api.Rentshop;
import com.ue.shopsystem.api.RentshopController;
import com.ue.shopsystem.commands.rentshop.RentshopTabCompleter;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class RentshopTabCompleterTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;
	private static RentshopTabCompleter tabCompleter;

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
		EconomyPlayerController.getAllEconomyPlayers().clear();
		player = server.addPlayer("kthschnll");
		RentshopController.getRentShops().clear();
		tabCompleter = new RentshopTabCompleter();
		try {
			Rentshop shop1 = RentshopController.createRentShop(new Location(world, 1, 2, 3), 9, 0);
			Rentshop shop2 = RentshopController.createRentShop(new Location(world, 1, 2, 3), 9, 0);
			RentshopController.createRentShop(new Location(world, 1, 2, 3), 9, 0);
			shop1.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 1);
			shop2.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 1);
		} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
			fail();
		}
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
		int size = RentshopController.getRentShops().size();
		for (int i = 0; i < size; i++) {
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
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
	public void zeroArgsPlayerPermissionTest() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", false);
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(3,list.size());
		assertEquals("rename", list.get(0));
		assertEquals("editShop", list.get(1));
		assertEquals("changeProfession", list.get(2));
	}
	
	@Test
	public void zeroArgsAdminPermissionTest() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(7,list.size());
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
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", false);
		String[] args = { "n" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("rename", list.get(0));
		assertEquals("changeProfession", list.get(1));
	}
	
	@Test
	public void zeroArgsAdminPermissionTestWithMatching() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "r" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(4,list.size());
		assertEquals("rename", list.get(0));
		assertEquals("changeProfession", list.get(1));
		assertEquals("create", list.get(2));
		assertEquals("resize", list.get(3));
	}
	
	@Test
	public void zeroArgsWithMoreArgs() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "ds", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void createArgTest() {
		String[] args = { "create" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void createArgTestWithMoreArgs() {
		String[] args = { "create", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void changeProfessionTestWithTwoArgs() {
		String[] args = { "changeProfession", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("Shop#R0", list.get(0));
		assertEquals("Shop#R1", list.get(1));
	}
	@Test
	public void changeProfessionTestWithTwoArgsMatching() {
		String[] args = { "changeProfession", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1,list.size());
		assertEquals("Shop#R1", list.get(0));
	}
	
	@Test
	public void changeProfessionTestWithThreeArgs() {
		String[] args = { "changeProfession", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(Profession.values().length,list.size());
		for(int i = 0; i < Profession.values().length; i++) {
			assertEquals(Profession.values()[i].name().toLowerCase(), list.get(i));
		}
	}
	
	@Test
	public void changeProfessionTestWithThreeArgsMatching() {
		String[] args = { "changeProfession", "Shop#R0", "flet" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1,list.size());
		assertEquals("fletcher", list.get(0));
	}
	
	@Test
	public void changeProfessionTestWithMoreArgs() {
		String[] args = { "changeProfession", "Shop#R0", "fletcher", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void editShopArgTestWithTwoArgs() {
		String[] args = { "editShop", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("Shop#R0", list.get(0));
		assertEquals("Shop#R1", list.get(1));
	}
	
	@Test
	public void renameArgTestWithTwoArgs() {
		String[] args = { "rename", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("Shop#R0", list.get(0));
		assertEquals("Shop#R1", list.get(1));
	}
	
	@Test
	public void editShopArgTestWithMoreArgs() {
		String[] args = { "editShop", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void renameArgTestWithMoreArgs() {
		String[] args = { "rename", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void deleteArgTestWithMoreArgs() {
		String[] args = { "delete", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void moveArgTestWithMoreArgs() {
		String[] args = { "move", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void resizeArgTestWithMoreArgs() {
		String[] args = { "resize", "Shop#R0", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void deleteArgTestWithTwoArgs() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "delete", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(3,list.size());
		assertEquals("Shop#R0_kthschnll", list.get(0));
		assertEquals("Shop#R1_kthschnll", list.get(1));
		assertEquals("RentShop#R2", list.get(2));
	}
	
	@Test
	public void moveArgTestWithTwoArgs() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "move", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(3,list.size());
		assertEquals("Shop#R0_kthschnll", list.get(0));
		assertEquals("Shop#R1_kthschnll", list.get(1));
		assertEquals("RentShop#R2", list.get(2));
	}
	
	@Test
	public void resizeArgTestWithTwoArgs() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.rentshop.admin", true);
		String[] args = { "resize", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(3,list.size());
		assertEquals("Shop#R0_kthschnll", list.get(0));
		assertEquals("Shop#R1_kthschnll", list.get(1));
		assertEquals("RentShop#R2", list.get(2));
	}
}
