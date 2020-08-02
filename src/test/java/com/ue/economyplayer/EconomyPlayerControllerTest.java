package com.ue.economyplayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.bank.logic.impl.BankManagerImpl;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class EconomyPlayerControllerTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;

	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		player = server.addPlayer("catch441");
		EconomyPlayerManagerImpl.deleteEconomyPlayer(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		UltimateEconomy.getInstance.getDataFolder().delete();
		server.setPlayers(0);
		MockBukkit.unload();
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		int size = EconomyPlayerManagerImpl.getAllEconomyPlayers().size();
		for (int i = 0; i < size; i++) {
			EconomyPlayerManagerImpl.deleteEconomyPlayer(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
		}
		int size2 = JobController.getJobList().size();
		for (int i = 0; i < size2; i++) {
			JobController.deleteJob(JobController.getJobList().get(0));
		}
	}

	@Test
	public void createEconomyPlayerTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			assertEquals(1, EconomyPlayerManagerImpl.getAllEconomyPlayers().size());
			assertEquals("catch441", ecoPlayer.getName());
			assertEquals(player, ecoPlayer.getPlayer());
			assertTrue(ecoPlayer.getJobList().isEmpty());
			assertTrue(ecoPlayer.getHomeList().isEmpty());
			assertTrue(ecoPlayer.getJoinedTownList().isEmpty());
			assertEquals(BankManagerImpl.getBankAccounts().get(0), ecoPlayer.getBankAccount());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(1, config.getStringList("Player").size());
			assertEquals("catch441", config.getStringList("Player").get(0));
			assertEquals(BankManagerImpl.getBankAccounts().get(0).getIban(), config.getString("catch441.Iban"));
			assertTrue(config.getBoolean("catch441.scoreboardDisabled"));
		} catch (EconomyPlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void createEconomyPlayerTestWithExistingName() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			assertTrue(false);
		} catch (EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cThis player already exists!", e.getMessage());
		}
	}

	@Test
	public void getEconomyPlayerNameListTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			List<String> list = EconomyPlayerManagerImpl.getEconomyPlayerNameList();
			assertEquals(1, list.size());
			assertEquals("catch441", list.get(0));
		} catch (EconomyPlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getEconomyPlayerByNameTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getEconomyPlayerByName("catch441");
			assertEquals("catch441", ecoPlayer.getName());
		} catch (EconomyPlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getEconomyPlayerByNameTestWithNoPlayer() {
		try {
			EconomyPlayerManagerImpl.getEconomyPlayerByName("catch441");
		} catch (EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cThis player does not exist!", e.getMessage());
		}
	}

	@Test
	public void getAllEconomyPlayersTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayerManagerImpl.createEconomyPlayer("Wulfgar");
			List<EconomyPlayer> list = EconomyPlayerManagerImpl.getAllEconomyPlayers();
			assertEquals(2, list.size());
			assertEquals("catch441", list.get(0).getName());
			assertEquals("Wulfgar", list.get(1).getName());
		} catch (EconomyPlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void deleteEconomyPlayerTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayerManagerImpl.deleteEconomyPlayer(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			assertEquals(0, EconomyPlayerManagerImpl.getAllEconomyPlayers().size());
			assertEquals(0, EconomyPlayerManagerImpl.getEconomyPlayerNameList().size());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(0, config.getStringList("Player").size());
			assertFalse(config.isSet("catch441"));
		} catch (EconomyPlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void loadAllEconomyPlayers() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 1, 1);
			ecoPlayer.addHome("myhome", loc, false);
			ecoPlayer.addJoinedTown("mytown");
			JobController.createJob("myjob");
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().clear();
			assertEquals(0, EconomyPlayerManagerImpl.getAllEconomyPlayers().size());
			EconomyPlayerManagerImpl.loadAllEconomyPlayers();
			List<EconomyPlayer> list = EconomyPlayerManagerImpl.getAllEconomyPlayers();
			assertEquals(1, list.size());
			assertEquals("catch441", list.get(0).getName());
			assertEquals(player, list.get(0).getPlayer());
			assertEquals(1, list.get(0).getJobList().size());
			assertEquals("myjob", list.get(0).getJobList().get(0).getName());
			assertEquals(1, list.get(0).getHomeList().size());
			assertEquals(loc, list.get(0).getHomeList().get("myhome"));
			assertEquals(1, list.get(0).getJoinedTownList().size());
			assertEquals("mytown", list.get(0).getJoinedTownList().get(0));
			assertEquals(BankManagerImpl.getBankAccounts().get(0), list.get(0).getBankAccount());
		} catch (EconomyPlayerException | JobSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}
}
