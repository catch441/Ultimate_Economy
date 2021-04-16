package org.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankAccount;
import org.ue.common.logic.api.GeneralValidationHandler;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.jobsystem.logic.api.Job;
import org.ue.townsystem.logic.api.TownworldManager;

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
	@Mock
	GeneralValidationHandler generalValidator;

	@Test
	public void invalidCommandTest() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "stuff", args);
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
		String[] args = { "stuff" };
		boolean result = executor.onCommand(player, null, "bank", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void bankCommandTestOn() {
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		String[] args = { "on" };
		boolean result = executor.onCommand(player, null, "bank", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertTrue(result);
		verify(ecoPlayer).setScoreBoardObjectiveVisible(true);
	}

	@Test
	public void bankCommandTestOff() {
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		String[] args = { "off" };
		boolean result = executor.onCommand(player, null, "bank", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertTrue(result);
		verify(ecoPlayer).setScoreBoardObjectiveVisible(false);
	}

	@Test
	public void moneyCommandTestZeroArgs() {
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(21.12);
		when(messageWrapper.getString("money_info", "21.12", "$")).thenReturn("§6Money: §a21.12§6 §a$§6");
		when(configManager.getCurrencyText(21.12)).thenReturn("$");

		String[] args = {};
		boolean result = executor.onCommand(player, null, "money", args);
		verify(player).getName();
		verify(messageWrapper).getString("money_info", "21.12", "$");
		verify(player).sendMessage("§6Money: §a21.12§6 §a$§6");
		verifyNoMoreInteractions(player);
		assertTrue(result);
	}

	@Test
	public void moneyCommandTestMoreArgs() {
		when(player.hasPermission("Ultimate_Economy.adminpay")).thenReturn(false);
		String[] args = { "stuff", "stuff" };
		boolean result = executor.onCommand(player, null, "money", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void moneyCommandTestAdminMoreArgs() {
		when(player.hasPermission("Ultimate_Economy.adminpay")).thenReturn(true);
		String[] args = { "stuff", "stuff" };
		boolean result = executor.onCommand(player, null, "money", args);
		verify(player).sendMessage("/money or /money <player>");
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertTrue(result);
	}

	@Test
	public void moneyCommandTestAdminOneArg() {
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(account.getAmount()).thenReturn(21.12);
		when(messageWrapper.getString("money_info", "21.12", "$")).thenReturn("§6Money: §a21.12§6 §a$§6");
		when(configManager.getCurrencyText(21.12)).thenReturn("$");
		when(player.hasPermission("Ultimate_Economy.adminpay")).thenReturn(true);
		when(player.getName()).thenReturn("catch441");

		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		String[] args = { "catch441" };
		boolean result = executor.onCommand(player, null, "money", args);
		verify(player).getName();
		verify(messageWrapper).getString("money_info", "21.12", "$");
		verify(player).sendMessage("§6Money: §a21.12§6 §a$§6");
		verifyNoMoreInteractions(player);
		assertDoesNotThrow(() -> verify(ecoPlayerManager, times(2)).getEconomyPlayerByName("catch441"));
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
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
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
		String[] args = { "stuff" };
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
		String[] args = { "stuff", "stuff" };
		boolean result = executor.onCommand(player, null, "sethome", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void setHomeCommandTestOneArg() {
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		String[] args = { "myhome" };
		Location location = mock(Location.class);
		when(player.getLocation()).thenReturn(location);

		assertEquals(0, ecoPlayer.getHomeList().size());
		boolean result = executor.onCommand(player, null, "sethome", args);
		assertDoesNotThrow(() -> verify(ecoPlayer).addHome("myhome", location, true));
		assertTrue(result);
	}
	
	@Test
	public void setHomeCommandTestWithException() throws GeneralEconomyException, EconomyPlayerException {
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		String[] args = { "myhome" };
		when(messageWrapper.getErrorString("already_exists", "myhome")).thenReturn("§cMy error message!");
		doThrow(new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, "myhome"))
				.when(ecoPlayer).addHome("myhome", null, true);

		assertEquals(0, ecoPlayer.getHomeList().size());
		boolean result = executor.onCommand(player, null, "sethome", args);
		verify(player).sendMessage("§cMy error message!");
		assertTrue(result);
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
		String[] args = { "stuff", "stuff" };
		boolean result = executor.onCommand(player, null, "delhome", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void delHomeCommandTestOneArg() {
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		String[] args = { "myhome" };
		boolean result = executor.onCommand(player, null, "delhome", args);
		assertDoesNotThrow(() -> verify(ecoPlayer).removeHome("myhome", true));
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
		String[] args = { "catch441" };
		boolean result = executor.onCommand(player, null, "pay", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void payCommandTestMoreArgs() {
		String[] args = { "catch441", "10", "stuff" };
		boolean result = executor.onCommand(player, null, "pay", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void payCommandTestInvalidInteger() {
		when(messageWrapper.getErrorString("invalid_parameter", "stuff")).thenReturn("My error message!");
		when(messageWrapper.getErrorString("invalid_parameter", "stuff"))
				.thenReturn("§c§cThe parameter §4stuff§c is invalid!");

		String[] args = { "catch441", "stuff" };
		boolean result = executor.onCommand(player, null, "pay", args);
		verify(player).sendMessage("§c§cThe parameter §4stuff§c is invalid!");
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertTrue(result);
	}

	@Test
	public void payCommandTestTwoArgs() {
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		EconomyPlayer reciever = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("Wulfgar")).thenReturn(reciever));

		String[] args = { "Wulfgar", "10" };
		boolean result = executor.onCommand(player, null, "pay", args);
		assertDoesNotThrow(() -> verify(ecoPlayer).payToOtherPlayer(reciever, 10.0, true));
		verifyNoMoreInteractions(player);
		verifyNoInteractions(reciever);
		assertTrue(result);
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
		String[] args = { "stuff", "stuff", "stuff" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		verifyNoInteractions(player);
		assertFalse(result);
	}

	@Test
	public void givemoneyCommandTestTwoArgsPositive() {
		EconomyPlayer receiver = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(receiver));

		String[] args = { "catch441", "10" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		assertDoesNotThrow(() -> verify(receiver).increasePlayerAmount(10, true));
		verifyNoInteractions(player);
		assertTrue(result);
	}

	@Test
	public void givemoneyCommandTestTwoArgsNegative() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(generalValidator).checkForPositiveValue(-10.0);
		
		String[] args = { "catch441", "-10" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		verify(player).sendMessage("my error message");
		assertTrue(result);
	}

	@Test
	public void givemoneyCommandTestInvalidInteger() {
		when(messageWrapper.getErrorString("invalid_parameter", "stuff")).thenReturn("My error message!");
		when(messageWrapper.getErrorString("invalid_parameter", "stuff"))
				.thenReturn("§c§cThe parameter §4stuff§c is invalid!");

		String[] args = { "catch441", "stuff" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		verify(player).sendMessage("§c§cThe parameter §4stuff§c is invalid!");
		verifyNoMoreInteractions(player);
		assertTrue(result);
	}

	@Test
	public void homeCommandTestZeroArgs() {
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Map<String, Location> homes = new HashMap<>();
		homes.put("myhome1", null);
		homes.put("myhome2", null);
		when(ecoPlayer.getHomeList()).thenReturn(homes);
		when(messageWrapper.getString("home_info", "[myhome2, myhome1]"))
				.thenReturn("§6Your homes: §a[myhome2, myhome1]§6");

		String[] args = {};
		boolean result = executor.onCommand(player, null, "home", args);
		verify(player).sendMessage("§6Your homes: §a[myhome2, myhome1]§6");
		assertTrue(result);
	}

	@Test
	public void homeCommandTestMoreArgs() {
		String[] args = { "stuff", "stuff" };
		boolean result = executor.onCommand(player, null, "home", args);
		verify(player).getName();
		verifyNoMoreInteractions(player);
		assertFalse(result);
	}

	@Test
	public void homeCommandTestOneArgs() {
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Location location = mock(Location.class);
		World world = mock(World.class);
		Chunk chunk = mock(Chunk.class);
		when(location.getChunk()).thenReturn(chunk);
		when(player.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("World");
		when(player.getLocation()).thenReturn(location);
		assertDoesNotThrow(() -> when(ecoPlayer.getHome("myhome")).thenReturn(location));

		String[] args = { "myhome" };
		boolean result = executor.onCommand(player, null, "home", args);
		verify(player).teleport(location);
		verify(townworldManager).performTownWorldLocationCheck("World", chunk, ecoPlayer);
		verifyNoMoreInteractions(player);
		assertTrue(result);
	}
	
	@Test
	public void removemoneyCommandTestZeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "removemoney", args);
		verifyNoInteractions(player);
		assertFalse(result);
	}

	@Test
	public void removemoneyCommandTestMoreArgs() {
		String[] args = { "stuff", "stuff", "stuff" };
		boolean result = executor.onCommand(player, null, "givemoney", args);
		verifyNoInteractions(player);
		assertFalse(result);
	}

	@Test
	public void removemoneyCommandTestTwoArgsPositive() {
		EconomyPlayer receiver = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(receiver));

		String[] args = { "catch441", "10" };
		boolean result = executor.onCommand(player, null, "removemoney", args);
		assertDoesNotThrow(() -> verify(receiver).decreasePlayerAmount(10, false));
		verifyNoInteractions(player);
		assertTrue(result);
	}

	@Test
	public void removemoneyCommandTestTwoArgsNegative() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(generalValidator).checkForPositiveValue(-10.0);
		
		String[] args = { "catch441", "-10" };
		boolean result = executor.onCommand(player, null, "removemoney", args);
		verify(player).sendMessage("my error message");
		assertTrue(result);
	}

	@Test
	public void removemoneyCommandTestInvalidInteger() {
		when(messageWrapper.getErrorString("invalid_parameter", "stuff")).thenReturn("My error message!");
		when(messageWrapper.getErrorString("invalid_parameter", "stuff"))
				.thenReturn("§c§cThe parameter §4stuff§c is invalid!");

		String[] args = { "catch441", "stuff" };
		boolean result = executor.onCommand(player, null, "removemoney", args);
		verify(player).sendMessage("§c§cThe parameter §4stuff§c is invalid!");
		verifyNoMoreInteractions(player);
		assertTrue(result);
	}
}
