package com.ue.economyplayer.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.PlayerException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class EconomyPlayerTabCompleterTest {

	private static EconomyPlayerTabCompleter tabCompleter;
	private static PlayerMock player;
	private static ServerMock server;
	private static WorldMock world;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		tabCompleter = new EconomyPlayerTabCompleter();
		player = server.addPlayer("kthschnll");
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		try {
			EconomyPlayerController.getAllEconomyPlayers().get(0).addHome("myhome1", new Location(world,1,2,3), false);
			EconomyPlayerController.getAllEconomyPlayers().get(0).addHome("myhome2", new Location(world,1,2,3), false);
		} catch (PlayerException e) {
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
		EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
		MockBukkit.unload();
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
	}
	
	@Test
	public void bankCommandWithZeroArgs() {
		Command command = UltimateEconomy.getInstance.getCommand("bank");
		String[] args = {""};
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("on", result.get(0));
		assertEquals("off", result.get(1));
	}
	
	@Test
	public void bankCommandWithTabComplete1() {
		Command command = UltimateEconomy.getInstance.getCommand("bank");
		String[] args = {"f"};
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("off", result.get(0));
	}
	
	@Test
	public void bankCommandWithTabComplete2() {
		Command command = UltimateEconomy.getInstance.getCommand("bank");
		String[] args = {"n"};
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("on", result.get(0));
	}
	
	@Test
	public void bankCommandWithMoreArgs() {
		Command command = UltimateEconomy.getInstance.getCommand("bank");
		String[] args = {"on",""};
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}
	
	@Test
	public void homeCommandWithMoreArgs() {
		Command command = new DummyCommand("home");
		String[] args = {"myhome",""};
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}
	
	@Test
	public void delhomeCommandWithMoreArgs() {
		Command command = new DummyCommand("delhome");
		String[] args = {"myhome",""};
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}
	
	@Test
	public void homeCommandWithZeroArgs() {
		Command command = new DummyCommand("home");
		String[] args = {""};
		List<String> result = tabCompleter.onTabComplete(player, command, null, args);
		assertEquals(2, result.size());
		assertEquals("myhome2", result.get(0));
		assertEquals("myhome1", result.get(1));
	}
	
	@Test
	public void homeCommandWithTabComplete() {
		Command command = new DummyCommand("home");
		String[] args = {"2"};
		List<String> result = tabCompleter.onTabComplete(player, command, null, args);
		assertEquals(1, result.size());
		assertEquals("myhome2", result.get(0));
	}
	
	private class DummyCommand extends Command {

		protected DummyCommand(String name) {
			super(name);
		}

		@Override
		public boolean execute(CommandSender sender,String commandLabel,String[] args) {
			return false;
		}
	}
}
