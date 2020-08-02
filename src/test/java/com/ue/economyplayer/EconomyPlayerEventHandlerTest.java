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

import com.ue.config.logic.impl.ConfigManagerImpl;
import com.ue.economyplayer.logic.impl.EconomyPlayerEventHandlerImpl;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class EconomyPlayerEventHandlerTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;
	private static EconomyPlayerEventHandlerImpl eventHandler;

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
		eventHandler = new EconomyPlayerEventHandlerImpl();
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		int size = EconomyPlayerManagerImpl.getAllEconomyPlayers().size();
		for (int i = 0; i < size; i++) {
			EconomyPlayerManagerImpl.deleteEconomyPlayer(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
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
		EconomyPlayerManagerImpl.deleteEconomyPlayer(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
		PlayerJoinEvent event = new PlayerJoinEvent(player, "");
		try {
			assertTrue(EconomyPlayerManagerImpl.getAllEconomyPlayers().isEmpty());
			eventHandler.handleJoin(event);
			assertEquals("kthschnll", EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).getName());
			assertEquals(player, EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).getPlayer());
			assertFalse(player.hasPermission("ultimate_economy.wilderness"));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void handleJoinTestLoad() {
		EconomyPlayerManagerImpl.getAllEconomyPlayers().clear();
		EconomyPlayerManagerImpl.loadAllEconomyPlayers();
		PlayerJoinEvent event = new PlayerJoinEvent(player, "");
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).setPlayer(null);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).setScoreBoardDisabled(false);
			eventHandler.handleJoin(event);
			assertEquals("kthschnll", EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).getName());
			assertEquals(player, EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).getPlayer());
			assertFalse(player.hasPermission("ultimate_economy.wilderness"));
			server.getScheduler().performOneTick();
			assertEquals(1, player.getScoreboard().getObjectives().size());
			assertNotNull("0", String.valueOf(player.getScoreboard().getObjective("bank")));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void handleJoinTestWithWildernessInteraction() {
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().clear();
			ConfigManagerImpl.setWildernessInteraction(true);
			EconomyPlayerManagerImpl.loadAllEconomyPlayers();
			PlayerJoinEvent event = new PlayerJoinEvent(player, "");
			assertFalse(player.hasPermission("ultimate_economy.wilderness"));
			eventHandler.handleJoin(event);
			assertTrue(player.hasPermission("ultimate_economy.wilderness"));
			ConfigManagerImpl.setWildernessInteraction(false);
		} catch (EconomyPlayerException e) {
			fail();
		}
	}
}
