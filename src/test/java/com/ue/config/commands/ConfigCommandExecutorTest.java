package com.ue.config.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class ConfigCommandExecutorTest {

	private static ServerMock server;
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
		player = server.addPlayer("catch441");
		executor = new ConfigCommandExecutor();
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
	public void zeroArgs() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertFalse(result);
		assertNull(player.nextMessage());
	}

	@Test
	public void invalidArg() {
		String[] args = { "foo" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertFalse(result);
		assertNull(player.nextMessage());
	}

	@Test
	public void currencyCommandTestWithOneArg() {
		String[] args = { "currency" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config currency <singular> <plural>", player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void currencyCommandTestWithTwoArg() {
		String[] args = { "currency", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config currency <singular> <plural>", player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void currencyCommandTestWithThreeArg() {
		String[] args = { "currency", "kth", "kths" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§6The configuration was changed to §akth kths§6.", player.nextMessage());
		assertEquals("§6Please restart the server!", player.nextMessage());
		assertNull(player.nextMessage());
		assertEquals("kth", ConfigController.getCurrencySg());
		assertEquals("kths", ConfigController.getCurrencyPl());
	}

	@Test
	public void languageCommandTestWithOneArg() {
		String[] args = { "language" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config language <language> <country>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void languageCommandTestWithTwoArg() {
		String[] args = { "language", "DE" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config language <language> <country>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void languageCommandTestWithMoreArgs() {
		String[] args = { "language", "de", "DE", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config language <language> <country>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void languageCommandTestWithTwoArgs() {
		String[] args = { "language", "de", "DE" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§6Please restart the server!",  player.nextMessage());
		assertNull(player.nextMessage());
		assertEquals("de", UltimateEconomy.getInstance.getConfig().getString("localeLanguage"));
		assertEquals("DE", UltimateEconomy.getInstance.getConfig().getString("localeCountry"));
	}
	
	@Test
	public void languageCommandTestWithTwoArgsCZ() {
		String[] args = { "language", "cs", "CZ" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§6Please restart the server!",  player.nextMessage());
		assertNull(player.nextMessage());
		assertEquals("cs", UltimateEconomy.getInstance.getConfig().getString("localeLanguage"));
		assertEquals("CZ", UltimateEconomy.getInstance.getConfig().getString("localeCountry"));
	}
	
	@Test
	public void languageCommandTestWithTwoArgsUS() {
		String[] args = { "language", "en", "US" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§6Please restart the server!",  player.nextMessage());
		assertNull(player.nextMessage());
		assertEquals("en", UltimateEconomy.getInstance.getConfig().getString("localeLanguage"));
		assertEquals("US", UltimateEconomy.getInstance.getConfig().getString("localeCountry"));
	}
	
	@Test
	public void languageCommandTestWithTwoArgsCN() {
		String[] args = { "language", "zh", "CN" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§6Please restart the server!",  player.nextMessage());
		assertNull(player.nextMessage());
		assertEquals("zh", UltimateEconomy.getInstance.getConfig().getString("localeLanguage"));
		assertEquals("CN", UltimateEconomy.getInstance.getConfig().getString("localeCountry"));
	}

	@Test
	public void languageCommandTestWithUnsupportedLanguage() {
		String[] args = { "language", "kth", "DE" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4kth§c is invalid!",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void languageCommandTestWithUnsupportedCountry() {
		String[] args = { "language", "de", "kth" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4kth§c is invalid!",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxRentedDaysCommandTestWithAll() {
		String[] args = { "maxRentedDays", "8" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals(8, ConfigController.getMaxRentedDays());
		assertEquals("§6The configuration was changed to §a8§6.",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxRentedDaysCommandTestWithInvalidArgumentNumber() {
		String[] args = { "maxRentedDays" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config maxRentedDays <number>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxRentedDaysCommandTestWithInvalidNumber() {
		String[] args = { "maxRentedDays", "-1" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§c§cThe parameter §4-1§c is invalid!",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxHomesCommandTestWithAll() {
		String[] args = { "maxHomes", "8" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals(8, ConfigController.getMaxHomes());
		assertEquals("§6The configuration was changed to §a8§6.",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxHomesCommandTestWithInvalidArgumentNumber() {
		String[] args = { "maxHomes" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config maxHomes <number>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxHomesCommandTestWithInvalidNumber() {
		String[] args = { "maxHomes", "-1" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§c§cThe parameter §4-1§c is invalid!",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxJobsCommandTestWithAll() {
		String[] args = { "maxJobs", "8" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals(8, ConfigController.getMaxJobs());
		assertEquals("§6The configuration was changed to §a8§6.",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxJobsCommandTestWithInvalidArgumentNumber() {
		String[] args = { "maxJobs" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config maxJobs <number>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxJobsCommandTestWithInvalidNumber() {
		String[] args = { "maxJobs", "-1" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§c§cThe parameter §4-1§c is invalid!",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxJoinedTownsCommandTestWithAll() {
		String[] args = { "maxJoinedTowns", "8" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals(8, ConfigController.getMaxJoinedTowns());
		assertEquals("§6The configuration was changed to §a8§6.",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxJoinedTownsCommandTestWithInvalidArgumentNumber() {
		String[] args = { "maxJoinedTowns" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config maxJoinedTowns <number>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxJoinedTownsCommandTestWithInvalidNumber() {
		String[] args = { "maxJoinedTowns", "-1" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§c§cThe parameter §4-1§c is invalid!",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxPlayershopsCommandTestWithAll() {
		String[] args = { "maxPlayershops", "8" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals(8, ConfigController.getMaxPlayershops());
		assertEquals("§6The configuration was changed to §a8§6.",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxPlayershopsCommandTestWithInvalidArgumentNumber() {
		String[] args = { "maxPlayershops" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config maxPlayershops <number>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void maxPlayershopsCommandTestWithInvalidNumber() {
		String[] args = { "maxPlayershops", "-1" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§c§cThe parameter §4-1§c is invalid!",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void homesCommandTestWithAll() {
		String[] args = { "homes", "false" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertFalse(ConfigController.isHomeSystem());
		assertEquals("§6The configuration was changed to §afalse§6.",  player.nextMessage());
		assertEquals("§6Please restart the server!",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void homesCommandTestWithInvalidArgumentNumber() {
		String[] args = { "homes" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config homes <true/false>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void homesCommandTestWithInvalidNumber() {
		String[] args = { "homes", "abc" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4abc§c is invalid!",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void extendedInteractionCommandTestWithAll() {
		String[] args = { "extendedInteraction", "false" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertFalse(ConfigController.isExtendedInteraction());
		assertEquals("§6The configuration was changed to §afalse§6.",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void extendedInteractionCommandTestWithInvalidArgumentNumber() {
		String[] args = { "extendedInteraction" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config extendedInteraction <true/false>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void extendedInteractionCommandTestWithInvalidNumber() {
		String[] args = { "extendedInteraction", "abc" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4abc§c is invalid!",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void wildernessInteractionCommandTestWithAll() {
		String[] args = { "wildernessInteraction", "false" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertFalse(ConfigController.isWildernessInteraction());
		assertEquals("§6The configuration was changed to §afalse§6.",  player.nextMessage());
		assertNull(player.nextMessage());
		String[] args1 = { "wildernessInteraction", "true" };
		boolean result1 = executor.onCommand(player, null, "ue-config", args1);
		assertTrue(result1);
		assertTrue(ConfigController.isWildernessInteraction());
		assertEquals("§6The configuration was changed to §atrue§6.",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void wildernessInteractionCommandTestWithInvalidArgumentNumber() {
		String[] args = { "wildernessInteraction" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("/ue-config wildernessInteraction <true/false>",  player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void wildernessInteractionCommandTestWithInvalidNumber() {
		String[] args = { "wildernessInteraction", "abc" };
		boolean result = executor.onCommand(player, null, "ue-config", args);
		assertTrue(result);
		assertEquals("§cThe parameter §4abc§c is invalid!",  player.nextMessage());
		assertNull(player.nextMessage());
	}
}
