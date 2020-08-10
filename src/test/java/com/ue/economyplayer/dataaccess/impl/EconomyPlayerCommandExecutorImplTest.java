package com.ue.economyplayer.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.logging.Level;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerCommandExecutorImpl;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerCommandExecutorImplTest {

	@InjectMocks
	EconomyPlayerCommandExecutorImpl executor;
	@Mock
	ConfigManager configManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	TownworldManager townworldManager;
	@Mock
	Player player;
	@Mock
	EconomyPlayer ecoPlayer;

	/**
	 * Setup.
	 */
	@BeforeEach
	public void setup() {
		when(player.getName()).thenReturn("kthschnll");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("kthschnll")).thenReturn(ecoPlayer));
	}

	@Test
	public void invalidCommandTest() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "kth", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void bankCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "bank", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void bankCommandTestInvalidArg() {
		String[] args = { "kth" };
		boolean result = executor.onCommand(player, null, "bank", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void bankCommandTestOn() {
		String[] args = { "on" };
		boolean result = executor.onCommand(player, null, "bank", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertTrue(result);
		verify(ecoPlayer).setScoreBoardDisabled(false);
	}

	@Test
	public void bankCommandTestOff() {
		String[] args = { "off" };
		boolean result = executor.onCommand(player, null, "bank", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertTrue(result);
		verify(ecoPlayer).setScoreBoardDisabled(true);
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
		when(player.hasPermission("Ultimate_Economy.adminpay")).thenReturn(false);
		String[] args = { "kth", "kth" };
		boolean result = executor.onCommand(player, null, "money", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void moneyCommandTestAdminMoreArgs() {
		when(player.hasPermission("Ultimate_Economy.adminpay")).thenReturn(true);
		String[] args = { "kth", "kth" };
		boolean result = executor.onCommand(player, null, "money", args);
		verify(player).sendMessage("/money or /money <player>");
		verifyNoMoreInteractions(player);
		assertTrue(result);
	}

	@Test
	public void moneyCommandTestAdminOneArg() {
		when(player.hasPermission("Ultimate_Economy.adminpay")).thenReturn(true);
		String[] args = { "catch441" };
		boolean result = executor.onCommand(player, null, "money", args);
		assertEquals("§6Money: §a21.12§6 §a$§6", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void moneyCommandTestAdminInvalidPlayer() {
		when(player.hasPermission("Ultimate_Economy.adminpay")).thenReturn(true);
		String[] args = { "kthschnll441" };
		boolean result = executor.onCommand(player, null, "money", args);
		assertEquals("§cThis player does not exist!", player.nextMessage());
		assertTrue(result);
	}

	@Test
	public void moneyCommandTestPlayerOneArg() {
		when(player.hasPermission("Ultimate_Economy.adminpay")).thenReturn(false);
		String[] args = { "catch441" };
		boolean result = executor.onCommand(player, null, "money", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void myJobsCommandTestZeroArgs() {
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job));
		when(messageWrapper.getString("myjobs_info", "[myjob]")).thenReturn("§6Joined jobs: §a[myjob]§6");

		String[] args = {};
		boolean result = executor.onCommand(player, null, "myjobs", args);
		verify(messageWrapper).getString("myjobs_info", "[myjob]");
		verify(player).sendMessage("§6Joined jobs: §a[myjob]§6");
		verifyNoMoreInteractions(player);
		assertTrue(result);
	}

	@Test
	public void myJobsCommandTestMoreArgs() {
		String[] args = { "kth" };
		boolean result = executor.onCommand(player, null, "myjobs", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void setHomeCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "sethome", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void setHomeCommandTestMoreArgs() {
		String[] args = { "kth", "kth" };
		boolean result = executor.onCommand(player, null, "sethome", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void setHomeCommandTestInvalidArg() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			String[] args = { "myhome" };
			boolean result = executor.onCommand(player, null, "sethome", args);
			assertEquals("§cThis home already exists!", player.nextMessage());
			assertTrue(result);
			ecoPlayer.removeHome("myhome", false);
		} catch (EconomyPlayerException e) {
			fail();
		}

	}

	@Test
	public void setHomeCommandTestOneArg() {
		EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
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
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void delHomeCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "delhome", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void delHomeCommandTestMoreArgs() {
		String[] args = { "kth", "kth" };
		boolean result = executor.onCommand(player, null, "delhome", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void delHomeCommandTestOneArg() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			String[] args = { "myhome" };
			boolean result = executor.onCommand(player, null, "delhome", args);
			assertEquals("§6Your home §amyhome§6 was deleted.", player.nextMessage());
			assertTrue(result);
			assertEquals(0, ecoPlayer.getHomeList().size());
		} catch (EconomyPlayerException e) {
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
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void payCommandTestOneArgs() {
		String[] args = { "kthschnll" };
		boolean result = executor.onCommand(player, null, "pay", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void payCommandTestMoreArgs() {
		String[] args = { "kthschnll", "10", "kth" };
		boolean result = executor.onCommand(player, null, "pay", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
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
					.valueOf(EconomyPlayerManagerImpl.getEconomyPlayerByName("catch441").getBankAccount().getAmount()));
			assertEquals("10.0", String.valueOf(
					EconomyPlayerManagerImpl.getEconomyPlayerByName("kthschnll").getBankAccount().getAmount()));
			assertTrue(result);
			EconomyPlayerManagerImpl.getEconomyPlayerByName("catch441").increasePlayerAmount(10, false);
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void givemoneyCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "givemoney", args);
		verifyNoInteractions(player);
		assertFalse(result);
	}

	@Test
	public void givemoneyCommandTestMoreArgs() {
		String[] args = { "kth", "kth", "kth" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		verifyNoInteractions(player);
		assertFalse(result);
	}

	@Test
	public void givemoneyCommandTestTwoArgsPositive() {
		String[] args = { "catch441", "10" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		assertEquals("§6You got §a10.0§6 §a$§6", player.nextMessage());
		assertTrue(result);
		try {
			EconomyPlayerManagerImpl.getEconomyPlayerByName("catch441").decreasePlayerAmount(10, false);
		} catch (GeneralEconomyException | EconomyPlayerException e) {
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
			EconomyPlayerManagerImpl.getEconomyPlayerByName("catch441").increasePlayerAmount(10, false);
		} catch (GeneralEconomyException | EconomyPlayerException e) {
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
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome1", loc, false);
			ecoPlayer.addHome("myhome2", loc, false);
			String[] args = {};
			boolean result = executor.onCommand(player, null, "home", args);
			assertEquals("§6Your homes: §a[myhome2, myhome1]§6", player.nextMessage());
			assertTrue(result);
			ecoPlayer.removeHome("myhome2", false);
			ecoPlayer.removeHome("myhome1", false);
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void homeCommandTestMoreArgs() {
		String[] args = { "kth", "kth" };
		boolean result = executor.onCommand(player, null, "home", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void homeCommandTestTwoArgs() {
		try {
			player.setLocation(new Location(world, 1, 2, 3));
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
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
		} catch (EconomyPlayerException e) {
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
