package com.ue.config.logic.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

@ExtendWith(MockitoExtension.class)
public class ConfigCommandExecutorImplTest {

	@InjectMocks
	ConfigCommandExecutorImpl executor;
	@Mock
	ConfigManager configManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	EconomyPlayerManager ecoPlayerManager;

	@Test
	public void zeroArgs() {
		Player player = mock(Player.class);
		String[] args = {};
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void invalidArg() {
		Player player = mock(Player.class);
		String[] args = { "foo" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void currencyCommandTestWithOneArg() {
		Player player = mock(Player.class);
		String[] args = { "currency" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config currency <singular> <plural>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void currencyCommandTestWithTwoArg() {
		Player player = mock(Player.class);
		String[] args = { "currency", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config currency <singular> <plural>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void currencyCommandTestWithThreeArg() {
		Player player = mock(Player.class);
		when(messageWrapper.getString("config_change", "kth kths"))
				.thenReturn("§6The configuration was changed to §akth kths§6.");
		when(messageWrapper.getString("restart")).thenReturn("§6Please restart the server!");

		String[] args = { "currency", "kth", "kths" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§6The configuration was changed to §akth kths§6.");
		verify(player).sendMessage("§6Please restart the server!");
		verifyNoMoreInteractions(player);
		verify(configManager).setCurrencyPl("kths");
		verify(configManager).setCurrencySg("kth");
	}

	@Test
	public void languageCommandTestWithOneArg() {
		Player player = mock(Player.class);
		String[] args = { "language" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config language <language> <country>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void languageCommandTestWithTwoArg() {
		Player player = mock(Player.class);
		String[] args = { "language", "DE" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config language <language> <country>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void languageCommandTestWithMoreArgs() {
		Player player = mock(Player.class);
		String[] args = { "language", "de", "DE", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config language <language> <country>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void languageCommandTestWithTwoArgs() {
		Player player = mock(Player.class);
		when(messageWrapper.getString("restart")).thenReturn("§6Please restart the server!");

		String[] args = { "language", "de", "DE" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§6Please restart the server!");
		verifyNoMoreInteractions(player);
		assertDoesNotThrow(() -> verify(configManager).setLocale("de", "DE"));
	}

	@Test
	public void languageCommandTestWithException() throws GeneralEconomyException {
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "kth")).thenReturn("My error message!");
		doThrow(new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, "kth"))
				.when(configManager).setLocale("de", "DE");

		String[] args = { "language", "de", "DE" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§cMy error message!");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void maxRentedDaysCommandTestWithAll() {
		Player player = mock(Player.class);
		when(messageWrapper.getString("config_change", "8")).thenReturn("§6The configuration was changed to §a8§6.");

		String[] args = { "maxRentedDays", "8" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§6The configuration was changed to §a8§6.");
		verifyNoMoreInteractions(player);
		assertDoesNotThrow(() -> verify(configManager).setMaxRentedDays(8));
	}

	@Test
	public void maxRentedDaysCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "maxRentedDays" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config maxRentedDays <number>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void maxRentedDaysCommandTestWithInvalidInteger() {
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "kth"))
				.thenReturn("§c§cThe parameter §4kth§c is invalid!");

		String[] args = { "maxRentedDays", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§c§cThe parameter §4kth§c is invalid!");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void maxHomesCommandTestWithAll() {
		Player player = mock(Player.class);
		when(messageWrapper.getString("config_change", "8")).thenReturn("§6The configuration was changed to §a8§6.");

		String[] args = { "maxHomes", "8" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§6The configuration was changed to §a8§6.");
		verifyNoMoreInteractions(player);
		assertDoesNotThrow(() -> verify(configManager).setMaxHomes(8));
	}

	@Test
	public void maxHomesCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "maxHomes" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config maxHomes <number>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void maxHomesCommandTestWithInvalidInteger() {
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "kth"))
				.thenReturn("§c§cThe parameter §4kth§c is invalid!");

		String[] args = { "maxHomes", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§c§cThe parameter §4kth§c is invalid!");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void maxJobsCommandTestWithAll() {
		Player player = mock(Player.class);
		when(messageWrapper.getString("config_change", "8")).thenReturn("§6The configuration was changed to §a8§6.");

		String[] args = { "maxJobs", "8" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§6The configuration was changed to §a8§6.");
		verifyNoMoreInteractions(player);
		assertDoesNotThrow(() -> verify(configManager).setMaxJobs(8));
	}

	@Test
	public void maxJobsCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "maxJobs" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config maxJobs <number>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void maxJobsCommandTestWithInvalidInteger() {
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "kth"))
				.thenReturn("§c§cThe parameter §4kth§c is invalid!");

		String[] args = { "maxJobs", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§c§cThe parameter §4kth§c is invalid!");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void maxJoinedTownsCommandTestWithAll() {
		Player player = mock(Player.class);
		when(messageWrapper.getString("config_change", "8")).thenReturn("§6The configuration was changed to §a8§6.");

		String[] args = { "maxJoinedTowns", "8" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§6The configuration was changed to §a8§6.");
		verifyNoMoreInteractions(player);
		assertDoesNotThrow(() -> verify(configManager).setMaxJoinedTowns(8));
	}

	@Test
	public void maxJoinedTownsCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "maxJoinedTowns" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config maxJoinedTowns <number>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void maxJoinedTownsCommandTestWithInvalidInteger() {
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "kth"))
				.thenReturn("§c§cThe parameter §4kth§c is invalid!");

		String[] args = { "maxJoinedTowns", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§c§cThe parameter §4kth§c is invalid!");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void maxPlayershopsCommandTestWithAll() {
		Player player = mock(Player.class);
		when(messageWrapper.getString("config_change", "8")).thenReturn("§6The configuration was changed to §a8§6.");

		String[] args = { "maxPlayershops", "8" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§6The configuration was changed to §a8§6.");
		verifyNoMoreInteractions(player);
		assertDoesNotThrow(() -> verify(configManager).setMaxPlayershops(8));
	}

	@Test
	public void maxPlayershopsCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "maxPlayershops" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config maxPlayershops <number>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void maxPlayershopsCommandTestWithInvalidInteger() {
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "kth"))
				.thenReturn("§c§cThe parameter §4kth§c is invalid!");

		String[] args = { "maxPlayershops", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§c§cThe parameter §4kth§c is invalid!");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void homesCommandTestWithAll() {
		Player player = mock(Player.class);
		when(messageWrapper.getString("config_change", "false"))
				.thenReturn("§6The configuration was changed to §afalse§6.");
		when(messageWrapper.getString("restart")).thenReturn("§6Please restart the server!");

		String[] args = { "homes", "false" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§6The configuration was changed to §afalse§6.");
		verify(player).sendMessage("§6Please restart the server!");
		verifyNoMoreInteractions(player);
		verify(configManager).setHomeSystem(false);
	}

	@Test
	public void homesCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "homes" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config homes <true/false>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void homesCommandTestWithInvalidNumber() {
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "kth"))
				.thenReturn("§cThe parameter §4kth§c is invalid!");

		String[] args = { "homes", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§cThe parameter §4kth§c is invalid!");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void extendedInteractionCommandTestWithAll() {
		Player player = mock(Player.class);
		when(messageWrapper.getString("config_change", "false"))
				.thenReturn("§6The configuration was changed to §afalse§6.");

		String[] args = { "extendedInteraction", "false" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§6The configuration was changed to §afalse§6.");
		verifyNoMoreInteractions(player);
		verify(configManager).setExtendedInteraction(false);
	}

	@Test
	public void extendedInteractionCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "extendedInteraction" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config extendedInteraction <true/false>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void extendedInteractionCommandTestWithInvalidInteger() {
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "abc"))
				.thenReturn("§cThe parameter §4abc§c is invalid!");

		String[] args = { "extendedInteraction", "abc" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§cThe parameter §4abc§c is invalid!");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void wildernessInteractionCommandTestWithAll() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		Player player = mock(Player.class);
		when(messageWrapper.getString("config_change", "false"))
				.thenReturn("§6The configuration was changed to §afalse§6.");
		when(messageWrapper.getString("config_change", "true"))
				.thenReturn("§6The configuration was changed to §atrue§6.");

		String[] args = { "wildernessInteraction", "false" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§6The configuration was changed to §afalse§6.");
		verifyNoMoreInteractions(player);
		verify(configManager).setWildernessInteraction(false);
		verify(ecoPlayer).denyWildernessPermission();
		String[] args1 = { "wildernessInteraction", "true" };
		boolean result1 = executor.onCommand(player, null, "ue-config", args1);
		assertTrue(result1);
		verify(player).sendMessage("§6The configuration was changed to §atrue§6.");
		verifyNoMoreInteractions(player);
		verify(configManager).setWildernessInteraction(true);
		verify(ecoPlayer).addWildernessPermission();
	}

	@Test
	public void wildernessInteractionCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "wildernessInteraction" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("/ue-config wildernessInteraction <true/false>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void wildernessInteractionCommandTestWithInvalidBoolean() {
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "kth"))
				.thenReturn("§cThe parameter §4kth§c is invalid!");

		String[] args = { "wildernessInteraction", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		verify(player).sendMessage("§cThe parameter §4kth§c is invalid!");
		verifyNoMoreInteractions(player);
	}
}
