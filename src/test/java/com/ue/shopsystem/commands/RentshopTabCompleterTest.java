package com.ue.shopsystem.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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

import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.impl.RentshopManagerImpl;
import com.ue.shopsystem.logic.impl.RentshopTabCompleterImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class RentshopTabCompleterTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;
	private static RentshopTabCompleterImpl tabCompleter;

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
		EconomyPlayerManagerImpl.getAllEconomyPlayers().clear();
		player = server.addPlayer("kthschnll");
		RentshopManagerImpl.getRentShops().clear();
		tabCompleter = new RentshopTabCompleterImpl();
		try {
			Rentshop shop1 = RentshopManagerImpl.createRentShop(new Location(world, 1, 2, 3), 9, 0);
			Rentshop shop2 = RentshopManagerImpl.createRentShop(new Location(world, 1, 2, 3), 9, 0);
			RentshopManagerImpl.createRentShop(new Location(world, 1, 2, 3), 9, 0);
			shop1.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
			shop2.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
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
		int size = RentshopManagerImpl.getRentShops().size();
		for (int i = 0; i < size; i++) {
			RentshopManagerImpl.deleteRentShop(RentshopManagerImpl.getRentShops().get(0));
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
