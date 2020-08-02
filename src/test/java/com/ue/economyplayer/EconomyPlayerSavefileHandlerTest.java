package com.ue.economyplayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.impl.BankManagerImpl;
import com.ue.economyplayer.dataaccess.impl.EconomyPlayerDaoImpl;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

public class EconomyPlayerSavefileHandlerTest {
	
	private EconomyPlayerDaoImpl savefileHandler;
	private static EconomyPlayer ecoPlayer;
	private static WorldMock world;
	private static ServerMock server;

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
		server.addPlayer("kthschnll");
		ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		UltimateEconomy.getInstance.getDataFolder().delete();
		server.setPlayers(0);
		EconomyPlayerManagerImpl.deleteEconomyPlayer(ecoPlayer);
		int size = JobController.getJobList().size();
		for(int i = 0; i<size; i++) {
			JobController.deleteJob(JobController.getJobList().get(0));
		}
		MockBukkit.unload();
	}
	
	/**
	 * Setup a new fresh player.
	 */
	@BeforeEach
	public void setupPlayer() {
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
		file.delete();
		savefileHandler = new EconomyPlayerDaoImpl(ecoPlayer);
	}
	
	@Test
	public void setupSavefileTest() {
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
		assertFalse(result.exists());
		EconomyPlayerDaoImpl.setupSavefile();
		assertTrue(result.exists());
	}
	
	@Test
	public void setupSavefileLoadTest() {
		EconomyPlayerDaoImpl.setupSavefile();
		EconomyPlayerDaoImpl.savePlayerList(new ArrayList<>());
		EconomyPlayerDaoImpl.setupSavefile();
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertTrue(config.isSet("Player"));
	}
	
	@Test
	public void savePlayerListTest() {
		EconomyPlayerDaoImpl.setupSavefile();
		List<String> list = new ArrayList<>();
		list.add("myKthschnll");
		EconomyPlayerDaoImpl.savePlayerList(list);
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals(1, config.getStringList("Player").size());
		assertEquals("myKthschnll", config.getStringList("Player").get(0));
	}
	
	@Test
	public void loadPlayerListTest() {
		EconomyPlayerDaoImpl.setupSavefile();
		List<String> list = new ArrayList<>();
		list.add("myKthschnll");
		EconomyPlayerDaoImpl.savePlayerList(list);
		List<String> result = EconomyPlayerDaoImpl.loadPlayerList();
		assertEquals(1, result.size());
		assertEquals("myKthschnll", result.get(0));
	}
	
	@Test
	public void deleteEconomyPlayer() {
		try {
			EconomyPlayerDaoImpl.setupSavefile();
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer2 = EconomyPlayerManagerImpl.getEconomyPlayerByName("catch441");
			File result = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(result);
			assertTrue(configBefore.isSet("catch441"));
			EconomyPlayerDaoImpl.deleteEconomyPlayer(ecoPlayer2);
			YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(result);
			assertFalse(configAfter.isSet("catch441"));
			EconomyPlayerManagerImpl.deleteEconomyPlayer(ecoPlayer2);
		} catch (EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void saveHomeTest() {
		EconomyPlayerDaoImpl.setupSavefile();
		Location loc = new Location(world,1,2,3);
		savefileHandler.saveHome("myNewHome", loc);
		File result = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals("myNewHome",config.getString("kthschnll.Home.myNewHome.Name"));
		assertEquals("World",config.getString("kthschnll.Home.myNewHome.World"));
		assertEquals("1.0",config.getString("kthschnll.Home.myNewHome.X"));
		assertEquals("2.0",config.getString("kthschnll.Home.myNewHome.Y"));
		assertEquals("3.0",config.getString("kthschnll.Home.myNewHome.Z"));
		savefileHandler.saveHome("myNewHome", null);
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(result);
		assertFalse(configAfter.isSet("kthschnll.Home.myNewHome"));
	}
	
	@Test
	public void loadHomeTest() {
		EconomyPlayerDaoImpl.setupSavefile();
		Location loc = new Location(world,1,2,3);
		savefileHandler.saveHome("myNewHome", loc);
		EconomyPlayerDaoImpl.setupSavefile();
		Location result = savefileHandler.loadHome("myNewHome");
		assertEquals("World",result.getWorld().getName());
		assertEquals("1.0",String.valueOf(result.getX()));
		assertEquals("2.0",String.valueOf(result.getY()));
		assertEquals("3.0",String.valueOf(result.getZ()));
	}
	
	@Test
	public void loadHomeListTest() {
		try {
			EconomyPlayerDaoImpl.setupSavefile();
			Location loc = new Location(world,1,2,3);
			ecoPlayer.addHome("myHome", loc, false);
			EconomyPlayerDaoImpl.setupSavefile();
			List<String> list = savefileHandler.loadHomeList();
			assertEquals(1,list.size());
			assertEquals("myHome",list.get(0));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void loadJoinedTownsTest() {
		try {
			EconomyPlayerDaoImpl.setupSavefile();
			ecoPlayer.addJoinedTown("mytown");
			EconomyPlayerDaoImpl.setupSavefile();
			List<String> list = savefileHandler.loadJoinedTowns();
			assertEquals(1,list.size());
			assertEquals("mytown",list.get(0));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void loadJobsListTest() {	
		try {
			EconomyPlayerDaoImpl.setupSavefile();
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, false);
			EconomyPlayerDaoImpl.setupSavefile();
			List<String> list = savefileHandler.loadJobsList();
			assertEquals(1,list.size());
			assertEquals("myjob",list.get(0));
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}
	
	@Test
	public void loadBankIbanTest() {
		try {
			EconomyPlayerDaoImpl.setupSavefile();
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
			configBefore.set("kthschnll.Iban", "myIban");
			configBefore.save(file);
			EconomyPlayerDaoImpl.setupSavefile();
			assertEquals("myIban", savefileHandler.loadBankIban());
		} catch (IOException e) {
			fail();
		}
	}
	
	@Test
	public void loadBankIbanOldTest() {
		try {
			EconomyPlayerDaoImpl.setupSavefile();
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
			configBefore.set("kthschnll.account amount", 20.5);
			configBefore.save(file);
			EconomyPlayerDaoImpl.setupSavefile();
			String iban = savefileHandler.loadBankIban();
			YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
			BankAccount account = BankManagerImpl.getBankAccountByIban(iban);
			assertFalse(configAfter.isSet("kthschnll.account amount"));
			assertEquals("20.5",String.valueOf(account.getAmount()));
		} catch (GeneralEconomyException | IOException e) {
			fail();
		}
	}
	
	@Test
	public void loadScoreboardDisabledTest() {
		try {
			EconomyPlayerDaoImpl.setupSavefile();
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
			configBefore.set("kthschnll.scoreboardDisabled", false);
			configBefore.save(file);
			EconomyPlayerDaoImpl.setupSavefile();
			assertFalse(savefileHandler.loadScoreboardDisabled());
		} catch (IOException e) {
			fail();
		}
	}
	
	@Test
	public void loadScoreboardDisabledOldTest() {
		try {
			EconomyPlayerDaoImpl.setupSavefile();
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
			configBefore.set("kthschnll.bank", false);
			configBefore.save(file);
			EconomyPlayerDaoImpl.setupSavefile();
			assertFalse(savefileHandler.loadScoreboardDisabled());
			YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
			assertFalse(configAfter.isSet("kthschnll.bank"));
			assertFalse(configAfter.getBoolean("kthschnll.scoreboardDisabled"));
		} catch (IOException e) {
			fail();
		}
	}
}
