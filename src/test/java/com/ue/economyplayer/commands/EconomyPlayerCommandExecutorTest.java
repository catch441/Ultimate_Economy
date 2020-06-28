package com.ue.economyplayer.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class EconomyPlayerCommandExecutorTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;
	private static PlayerMock kth;
	private static CommandExecutor executor;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		player = server.addPlayer("catch441");
		kth = server.addPlayer("kthschnll");
		executor = new EconomyPlayerCommandExecutor();
		try {
			EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(21.125, false);
		} catch (GeneralEconomyException e) {
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
		MockBukkit.unload();
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {

	}
	
	@Test
	public void invalidCommandTest() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "kth", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void bankCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "bank", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void bankCommandTestInvalidArg() {
		String[] args = { "kth" };
		boolean result = executor.onCommand(player, null, "bank", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void bankCommandTestOn() {
		EconomyPlayerController.getAllEconomyPlayers().get(0).setScoreBoardDisabled(true);
		String[] args = { "on" };
		boolean result = executor.onCommand(player, null, "bank", args);
		assertNull(player.nextMessage());
		assertTrue(result);
		assertFalse(EconomyPlayerController.getAllEconomyPlayers().get(0).isScoreBoardDisabled());
	}

	@Test
	public void bankCommandTestOff() {
		EconomyPlayerController.getAllEconomyPlayers().get(0).setScoreBoardDisabled(false);
		String[] args = { "off" };
		boolean result = executor.onCommand(player, null, "bank", args);
		assertNull(player.nextMessage());
		assertTrue(result);
		assertTrue(EconomyPlayerController.getAllEconomyPlayers().get(0).isScoreBoardDisabled());
	}

	@Test
	public void moneyCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "money", args);
		assertEquals("§6Money: §a21.12§6 §a$§6", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void moneyCommandTestMoreArgs() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.adminpay", false);
		String[] args = { "kth", "kth" };
		boolean result = executor.onCommand(player, null, "money", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void moneyCommandTestAdminMoreArgs() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.adminpay", true);
		String[] args = { "kth", "kth" };
		boolean result = executor.onCommand(player, null, "money", args);
		assertEquals("/money or /money <player>", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void moneyCommandTestAdminOneArg() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.adminpay", true);
		String[] args = { "catch441" };
		boolean result = executor.onCommand(player, null, "money", args);
		assertEquals("§6Money: §a21.12§6 §a$§6", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void moneyCommandTestAdminInvalidPlayer() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.adminpay", true);
		String[] args = { "kthschnll441" };
		boolean result = executor.onCommand(player, null, "money", args);
		assertEquals("§cThis player does not exist!", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void moneyCommandTestPlayerOneArg() {
		player.addAttachment(UltimateEconomy.getInstance).setPermission("ultimate_economy.adminpay", false);
		String[] args = { "catch441" };
		boolean result = executor.onCommand(player, null, "money", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void myJobsCommandTestZeroArgs() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayerController.getAllEconomyPlayers().get(0).joinJob(job, false);
			String[] args = {};
			boolean result = executor.onCommand(player, null, "myjobs", args);
			assertEquals("§6Joined jobs: §a[myjob]§6 ", player.nextMessage());
			assertTrue(result);
			JobController.deleteJob(job);
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void myJobsCommandTestMoreArgs() {
		String[] args = { "kth" };
		boolean result = executor.onCommand(player, null, "myjobs", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void setHomeCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "sethome", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void setHomeCommandTestMoreArgs() {
		String[] args = { "kth", "kth" };
		boolean result = executor.onCommand(player, null, "sethome", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void setHomeCommandTestInvalidArg() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			String[] args = { "myhome" };
			boolean result = executor.onCommand(player, null, "sethome", args);
			assertEquals("§cThis home already exists!", player.nextMessage());
			assertTrue(result);
			ecoPlayer.removeHome("myhome", false);
		} catch (PlayerException e) {
			fail();
		}

	}

	@Test
	public void setHomeCommandTestOneArg() {
		EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
		String[] args = { "myhome" };
		Location loc = new Location(world, 1, 2, 3);
		player.setLocation(loc);
		assertEquals(0, ecoPlayer.getHomeList().size());
		boolean result = executor.onCommand(player, null, "sethome", args);
		assertEquals("§6You created the home §amyhome§6.", player.nextMessage());
		assertTrue(result);
		try {
			assertEquals(loc, ecoPlayer.getHome("myhome"));
			ecoPlayer.removeHome("myhome", false);
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void delHomeCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "delhome", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void delHomeCommandTestMoreArgs() {
		String[] args = { "kth", "kth" };
		boolean result = executor.onCommand(player, null, "delhome", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void delHomeCommandTestOneArg() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			String[] args = { "myhome" };
			boolean result = executor.onCommand(player, null, "delhome", args);
			assertEquals("§6Your home §amyhome§6 was deleted.", player.nextMessage());
			assertTrue(result);
			assertEquals(0, ecoPlayer.getHomeList().size());
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void delHomeCommandTestInvalidArg() {
		String[] args = { "myhome" };
		boolean result = executor.onCommand(player, null, "delhome", args);
		assertEquals("§cThis home does not exist!", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void payCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "pay", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void payCommandTestOneArgs() {
		String[] args = { "kthschnll" };
		boolean result = executor.onCommand(player, null, "pay", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void payCommandTestMoreArgs() {
		String[] args = { "kthschnll", "10", "kth" };
		boolean result = executor.onCommand(player, null, "pay", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void payCommandTestInvalidNumber() {
		String[] args = { "kthschnll", "-10" };
		boolean result = executor.onCommand(player, null, "pay", args);
		assertEquals("§cThe parameter §4-10.0§c is invalid!", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void payCommandTestInvalidPlayer() {
		String[] args = { "kth", "-10" };
		boolean result = executor.onCommand(player, null, "pay", args);
		assertEquals("§cThis player does not exist!", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void payCommandTestTwoArgs() {
		try {
			String[] args = { "kthschnll", "10" };
			boolean result = executor.onCommand(player, null, "pay", args);
			assertEquals("§6You gave §akthschnll§6 §a10.0§6 §a$§6.", player.nextMessage());
			assertEquals("§6You got §a10.0§6 §a$§6 from §acatch441§6.", kth.nextMessage());
			assertEquals("11.125", String
					.valueOf(EconomyPlayerController.getEconomyPlayerByName("catch441").getBankAccount().getAmount()));
			assertEquals("10.0", String
					.valueOf(EconomyPlayerController.getEconomyPlayerByName("kthschnll").getBankAccount().getAmount()));
			assertTrue(result);
			EconomyPlayerController.getEconomyPlayerByName("catch441").increasePlayerAmount(10, false);
		} catch (PlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void givemoneyCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "givemoney", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void givemoneyCommandTestMoreArgs() {
		String[] args = { "kth", "kth", "kth" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void givemoneyCommandTestTwoArgsPositive() {
		String[] args = { "catch441", "10" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		assertEquals("§6You got §a10.0§6 §a$§6", player.nextMessage());
		assertTrue(result);
		try {
			EconomyPlayerController.getEconomyPlayerByName("catch441").decreasePlayerAmount(10, false);
		} catch (GeneralEconomyException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void givemoneyCommandTestTwoArgsNegative() {
		String[] args = { "catch441", "-10" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		assertNull(player.nextMessage());
		assertTrue(result);
		try {
			EconomyPlayerController.getEconomyPlayerByName("catch441").increasePlayerAmount(10, false);
		} catch (GeneralEconomyException | PlayerException e) {
			fail();
		}
	}

	@Test
	public void givemoneyCommandTestInvalidNumber() {
		String[] args = { "catch441", "kth" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		assertEquals("§cThe parameter §4kth§c is invalid!", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void givemoneyCommandTestInvalidPlayer() {
		String[] args = { "kth", "10" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		assertEquals("§cThis player does not exist!", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void homeCommandTestZeroArgs() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			String[] args = {};
			boolean result = executor.onCommand(player, null, "home", args);
			assertEquals("§6Your homes: §amyhome§6", player.nextMessage());
			assertTrue(result);
			ecoPlayer.removeHome("myhome", false);
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void homeCommandTestMoreArgs() {
		String[] args = { "kth", "kth" };
		boolean result = executor.onCommand(player, null, "home", args);
		assertNull(player.nextMessage());
		assertFalse(result);
	}

	@Test
	public void homeCommandTestTwoArgs() {
		try {
			player.setLocation(new Location(world, 1, 2, 3));
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 4, 5, 6);
			ecoPlayer.addHome("myhome", loc, false);
			String[] args = { "myhome" };
			boolean result = executor.onCommand(player, null, "home", args);
			assertNull(player.nextMessage());
			assertTrue(result);
			assertEquals("4.0", String.valueOf(player.getLocation().getX()));
			assertEquals("5.0", String.valueOf(player.getLocation().getY()));
			assertEquals("6.0", String.valueOf(player.getLocation().getZ()));
			ecoPlayer.removeHome("myhome", false);
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void homeCommandTestInvalidHome() {
		String[] args = { "myhome" };
		boolean result = executor.onCommand(player, null, "home", args);
		assertEquals("§cThis home does not exist!", player.nextMessage());
		assertTrue(result);
	}
}
