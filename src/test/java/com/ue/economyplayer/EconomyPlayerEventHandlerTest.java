package com.ue.economyplayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.economyplayer.impl.EconomyPlayerEventHandler;
import com.ue.exceptions.PlayerException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class EconomyPlayerEventHandlerTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;
	private static EconomyPlayerEventHandler eventHandler;

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
		player = server.addPlayer("kthschnll");
		eventHandler = new EconomyPlayerEventHandler();
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		int size = EconomyPlayerController.getAllEconomyPlayers().size();
		for (int i = 0; i < size; i++) {
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
	public void handleJoinTestNew() {
		EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
		PlayerJoinEvent event = new PlayerJoinEvent(player, "");
		try {
			assertTrue(EconomyPlayerController.getAllEconomyPlayers().isEmpty());
			eventHandler.handleJoin(event);
			assertEquals("kthschnll", EconomyPlayerController.getAllEconomyPlayers().get(0).getName());
			assertEquals(player, EconomyPlayerController.getAllEconomyPlayers().get(0).getPlayer());
			assertFalse(player.hasPermission("ultimate_economy.wilderness"));
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void handleJoinTestLoad() {
		EconomyPlayerController.getAllEconomyPlayers().clear();
		EconomyPlayerController.loadAllEconomyPlayers();
		PlayerJoinEvent event = new PlayerJoinEvent(player, "");
		try {
			EconomyPlayerController.getAllEconomyPlayers().get(0).setPlayer(null);
			EconomyPlayerController.getAllEconomyPlayers().get(0).setScoreBoardDisabled(false);
			eventHandler.handleJoin(event);
			assertEquals("kthschnll", EconomyPlayerController.getAllEconomyPlayers().get(0).getName());
			assertEquals(player, EconomyPlayerController.getAllEconomyPlayers().get(0).getPlayer());
			assertFalse(player.hasPermission("ultimate_economy.wilderness"));
			server.getScheduler().performOneTick();
			assertEquals(1, player.getScoreboard().getObjectives().size());
			assertNotNull("0", String.valueOf(player.getScoreboard().getObjective("bank")));
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void handleJoinTestWithWildernessInteraction() {
		try {
			EconomyPlayerController.getAllEconomyPlayers().clear();
			ConfigController.setWildernessInteraction(true);
			EconomyPlayerController.loadAllEconomyPlayers();
			PlayerJoinEvent event = new PlayerJoinEvent(player, "");
			assertFalse(player.hasPermission("ultimate_economy.wilderness"));
			eventHandler.handleJoin(event);
			assertTrue(player.hasPermission("ultimate_economy.wilderness"));
			ConfigController.setWildernessInteraction(false);
		} catch (PlayerException e) {
			fail();
		}
	}
}
