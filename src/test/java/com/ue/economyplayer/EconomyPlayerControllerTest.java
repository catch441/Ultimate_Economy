package com.ue.economyplayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import com.ue.bank.api.BankController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.JobController;
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
		EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
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
		int size = EconomyPlayerController.getAllEconomyPlayers().size();
		for (int i = 0; i < size; i++) {
			EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
		}
		int size2 = JobController.getJobList().size();
		for (int i = 0; i < size2; i++) {
			JobController.deleteJob(JobController.getJobList().get(0));
		}
	}

	@Test
	public void createEconomyPlayerTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			assertEquals(1, EconomyPlayerController.getAllEconomyPlayers().size());
			assertEquals("catch441", ecoPlayer.getName());
			assertEquals(player, ecoPlayer.getPlayer());
			assertTrue(ecoPlayer.getJobList().isEmpty());
			assertTrue(ecoPlayer.getHomeList().isEmpty());
			assertTrue(ecoPlayer.getJoinedTownList().isEmpty());
			assertEquals(BankController.getBankAccounts().get(0), ecoPlayer.getBankAccount());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(1, config.getStringList("Player").size());
			assertEquals("catch441", config.getStringList("Player").get(0));
			assertEquals(BankController.getBankAccounts().get(0).getIban(), config.getString("catch441.Iban"));
			assertTrue(config.getBoolean("catch441.scoreboardDisabled"));
		} catch (PlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void createEconomyPlayerTestWithExistingName() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayerController.createEconomyPlayer("catch441");
			assertTrue(false);
		} catch (PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cThis player already exists!", e.getMessage());
		}
	}

	@Test
	public void getEconomyPlayerNameListTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			List<String> list = EconomyPlayerController.getEconomyPlayerNameList();
			assertEquals(1, list.size());
			assertEquals("catch441", list.get(0));
		} catch (PlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getEconomyPlayerByNameTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName("catch441");
			assertEquals("catch441", ecoPlayer.getName());
		} catch (PlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getEconomyPlayerByNameTestWithNoPlayer() {
		try {
			EconomyPlayerController.getEconomyPlayerByName("catch441");
		} catch (PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cThis player does not exist!", e.getMessage());
		}
	}

	@Test
	public void getAllEconomyPlayersTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayerController.createEconomyPlayer("Wulfgar");
			List<EconomyPlayer> list = EconomyPlayerController.getAllEconomyPlayers();
			assertEquals(2, list.size());
			assertEquals("catch441", list.get(0).getName());
			assertEquals("Wulfgar", list.get(1).getName());
		} catch (PlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void deleteEconomyPlayerTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
			assertEquals(0, EconomyPlayerController.getAllEconomyPlayers().size());
			assertEquals(0, EconomyPlayerController.getEconomyPlayerNameList().size());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(0, config.getStringList("Player").size());
			assertFalse(config.isSet("catch441"));
		} catch (PlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void loadAllEconomyPlayers() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 1, 1);
			ecoPlayer.addHome("myhome", loc, false);
			ecoPlayer.addJoinedTown("mytown");
			JobController.createJob("myjob");
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			EconomyPlayerController.getAllEconomyPlayers().clear();
			assertEquals(0, EconomyPlayerController.getAllEconomyPlayers().size());
			EconomyPlayerController.loadAllEconomyPlayers();
			List<EconomyPlayer> list = EconomyPlayerController.getAllEconomyPlayers();
			assertEquals(1, list.size());
			assertEquals("catch441", list.get(0).getName());
			assertEquals(player, list.get(0).getPlayer());
			assertEquals(1, list.get(0).getJobList().size());
			assertEquals("myjob", list.get(0).getJobList().get(0).getName());
			assertEquals(1, list.get(0).getHomeList().size());
			assertEquals(loc, list.get(0).getHomeList().get("myhome"));
			assertEquals(1, list.get(0).getJoinedTownList().size());
			assertEquals("mytown", list.get(0).getJoinedTownList().get(0));
			assertEquals(BankController.getBankAccounts().get(0), list.get(0).getBankAccount());
		} catch (PlayerException | JobSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}
}
