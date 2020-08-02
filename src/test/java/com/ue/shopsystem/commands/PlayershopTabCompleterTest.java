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

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.api.PlayershopController;
import com.ue.shopsystem.commands.playershop.PlayershopTabCompleter;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class PlayershopTabCompleterTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;
	private static PlayershopTabCompleter tabCompleter;

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
		player = server.addPlayer("kthschnll1");
		tabCompleter = new PlayershopTabCompleter();
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			PlayershopController.createPlayerShop("myshop1", loc, 9, ecoPlayer);
			PlayershopController.createPlayerShop("myshop2", loc, 9, ecoPlayer);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException | TownSystemException e) {
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
		int size = PlayershopController.getPlayerShops().size();
		for (int i = 0; i < size; i++) {
			PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
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
	public void zeroArgsAdminPermissionTest() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.adminshop", true);
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(9,list.size());
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
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.adminshop", false);
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(8,list.size());
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
		assertEquals(3,list.size());
		assertEquals("rename", list.get(0));
		assertEquals("changeProfession", list.get(1));
		assertEquals("changeOwner", list.get(2));
	}
	
	@Test
	public void zeroArgsTestWithMoreArgs() {
		String[] args = { "kth", "schnll" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}

	@Test
	public void changeProfessionTestWithTwoArgs() {
		String[] args = { "changeProfession", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}
	@Test
	public void changeProfessionTestWithTwoArgsMatching() {
		String[] args = { "changeProfession", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1,list.size());
		assertEquals("myshop1", list.get(0));
	}
	
	@Test
	public void changeProfessionTestWithThreeArgs() {
		String[] args = { "changeProfession", "myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(Profession.values().length,list.size());
		for(int i = 0; i < Profession.values().length; i++) {
			assertEquals(Profession.values()[i].name().toLowerCase(), list.get(i));
		}
	}
	
	@Test
	public void changeProfessionTestWithThreeArgsMatching() {
		String[] args = { "changeProfession", "myshop1", "flet" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1,list.size());
		assertEquals("fletcher", list.get(0));
	}
	
	@Test
	public void changeProfessionTestWithMoreArgs() {
		String[] args = { "changeProfession", "myshop1", "fletcher", "" };
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
	public void deleteOtherArgTest() {
		String[] args = { "deleteOther" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void deleteOtherArgTestWithMoreArgs() {
		String[] args = { "deleteOther", "kth", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void deleteOtherArgTestWithTwoArgs() {
		String[] args = { "deleteOther", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("myshop1_kthschnll1", list.get(0));
		assertEquals("myshop2_kthschnll1", list.get(1));
	}
	
	@Test
	public void deleteArgTestWithTwoArgs() {
		String[] args = { "delete","" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}
	
	@Test
	public void deleteArgTestWithMoreArgs() {
		String[] args = { "delete","myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void deleteArgTestWithOneArg() {
		String[] args = { "delete" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void moveArgTestWithTwoArgs() {
		String[] args = { "move","" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}
	
	@Test
	public void moveArgTestWithMoreArgs() {
		String[] args = { "move","myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void moveArgTestWithOneArg() {
		String[] args = { "move" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void editShopArgTestWithTwoArgs() {
		String[] args = { "editShop","" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}
	
	@Test
	public void editShopArgTestWithMoreArgs() {
		String[] args = { "editShop","myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void editShopArgTestWithOneArg() {
		String[] args = { "editShop" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void resizeShopArgTestWithTwoArgs() {
		String[] args = { "resize","" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}
	
	@Test
	public void resizeShopArgTestWithMoreArgs() {
		String[] args = { "resize","myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void resizeShopArgTestWithOneArg() {
		String[] args = { "resize" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void changeOwnerShopArgTestWithTwoArgs() {
		String[] args = { "changeOwner","" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}
	
	@Test
	public void changeOwnerShopArgTestWithMoreArgs() {
		String[] args = { "changeOwner","myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void changeOwnerShopArgTestWithOneArg() {
		String[] args = { "changeOwner" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void renameOwnerShopArgTestWithTwoArgs() {
		String[] args = { "rename","" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2,list.size());
		assertEquals("myshop1", list.get(0));
		assertEquals("myshop2", list.get(1));
	}
	
	@Test
	public void renameOwnerShopArgTestWithMoreArgs() {
		String[] args = { "rename","myshop1", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
	
	@Test
	public void renameOwnerShopArgTestWithOneArg() {
		String[] args = { "rename" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0,list.size());
	}
}
