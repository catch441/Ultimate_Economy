package com.ue.economyplayer.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.ServerProvider;
import com.ue.jobsystem.logic.api.Job;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerDaoImplTest {

	@InjectMocks
	EconomyPlayerDaoImpl ecoPlayerDao;
	@Mock
	BankManager bankManager;
	@Mock
	ServerProvider serverProvider;

	/**
	 * Delete savefile.
	 */
	@AfterEach
	public void cleanUp() {
		File file = new File("src/PlayerFile.yml");
		file.delete();
	}

	@Test
	public void setupSavefileTest() {
		File result = new File("src/PlayerFile.yml");
		assertFalse(result.exists());
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		assertTrue(result.exists());
	}

	@Test
	public void setupSavefileLoadTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.saveBankIban("myKthschnll", "myiban");
		ecoPlayerDao.setupSavefile();
		File result = new File("src/PlayerFile.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertTrue(config.isSet("myKthschnll"));
	}

	@Test
	public void loadPlayerListTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.saveBankIban("myKthschnll", "myiban");
		ecoPlayerDao.setupSavefile();
		List<String> result = ecoPlayerDao.loadPlayerList();
		assertEquals(1, result.size());
		assertEquals("myKthschnll", result.get(0));
	}
	
	@Test
	public void loadPlayerListOldTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		File file = new File("src/PlayerFile.yml");
		YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
		configBefore.set("Player", "myKthschnll");
		assertDoesNotThrow(() -> configBefore.save(file));
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.loadPlayerList();
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.contains("Player"));
	}

	@Test
	public void deleteEconomyPlayer() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		File file = new File("src/PlayerFile.yml");
		YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
		configBefore.set("catch441", "testvalue");
		assertDoesNotThrow(() -> configBefore.save(file));
		ecoPlayerDao.deleteEconomyPlayer("catch441");
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("catch441"));
	}

	@Test
	public void saveHomeTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");

		Location loc = new Location(world, 1, 2, 3);
		ecoPlayerDao.saveHome("kthschnll", "myNewHome", loc);
		File result = new File("src/PlayerFile.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals("myNewHome", config.getString("kthschnll.Home.myNewHome.Name"));
		assertEquals("World", config.getString("kthschnll.Home.myNewHome.World"));
		assertEquals("1.0", config.getString("kthschnll.Home.myNewHome.X"));
		assertEquals("2.0", config.getString("kthschnll.Home.myNewHome.Y"));
		assertEquals("3.0", config.getString("kthschnll.Home.myNewHome.Z"));
		ecoPlayerDao.saveHome("kthschnll", "myNewHome", null);
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(result);
		assertFalse(configAfter.isSet("kthschnll.Home.myNewHome"));
	}

	@Test
	public void loadHomeListTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		when(serverProvider.getWorld("World")).thenReturn(world);

		Location loc = new Location(world, 1, 2, 3);
		ecoPlayerDao.saveHome("kthschnll", "myHome", loc);
		ecoPlayerDao.setupSavefile();
		Map<String, Location> list = ecoPlayerDao.loadHomeList("kthschnll");
		Location result = list.get("myHome");
		assertEquals("World", result.getWorld().getName());
		assertEquals("1.0", String.valueOf(result.getX()));
		assertEquals("2.0", String.valueOf(result.getY()));
		assertEquals("3.0", String.valueOf(result.getZ()));
		assertEquals(1, list.size());
	}
	
	@Test
	public void loadHomeListOldTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		File file = new File("src/PlayerFile.yml");
		YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
		configBefore.set("kthschnll..Home.Homelist", Arrays.asList("myHome"));
		assertDoesNotThrow(() -> configBefore.save(file));
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.loadHomeList("kthschnll");
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("kthschnll..Home.Homelist"));
	}

	@Test
	public void loadJoinedTownsTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.saveJoinedTowns("kthschnll", Arrays.asList("mytown"));
		ecoPlayerDao.setupSavefile();
		List<String> list = ecoPlayerDao.loadJoinedTowns("kthschnll");
		assertEquals(1, list.size());
		assertEquals("mytown", list.get(0));
	}

	@Test
	public void loadJobsListTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");

		List<Job> jobs = new ArrayList<>();
		jobs.add(job);
		ecoPlayerDao.saveJoinedJobsList("kthschnll", jobs);
		ecoPlayerDao.setupSavefile();
		List<String> list = ecoPlayerDao.loadJobsList("kthschnll");
		assertEquals(1, list.size());
		assertEquals("myjob", list.get(0));
	}

	@Test
	public void loadBankIbanTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.saveBankIban("kthschnll", "myIban");
		ecoPlayerDao.setupSavefile();
		assertEquals("myIban", ecoPlayerDao.loadBankIban("kthschnll"));
	}

	@Test
	public void loadBankIbanOldTest() {
		BankAccount account = mock(BankAccount.class);
		when(bankManager.createBankAccount(20.5)).thenReturn(account);
		when(account.getIban()).thenReturn("myIban");
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		
		File file = new File("src/PlayerFile.yml");
		YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
		configBefore.set("kthschnll.account amount", 20.5);
		assertDoesNotThrow(() -> configBefore.save(file));
		ecoPlayerDao.setupSavefile();
		assertEquals("myIban", ecoPlayerDao.loadBankIban("kthschnll"));
		verify(bankManager).createBankAccount(20.5);
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("kthschnll.account amount"));
	}

	@Test
	public void loadScoreboardDisabledTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.saveScoreboardDisabled("kthschnll", false);
		ecoPlayerDao.setupSavefile();
		assertFalse(ecoPlayerDao.loadScoreboardDisabled("kthschnll"));
	}

	@Test
	public void loadScoreboardDisabledOldTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		File file = new File("src/PlayerFile.yml");
		YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
		configBefore.set("kthschnll.bank", false);
		assertDoesNotThrow(() -> configBefore.save(file));
		ecoPlayerDao.setupSavefile();
		assertFalse(ecoPlayerDao.loadScoreboardDisabled("kthschnll"));
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("kthschnll.bank"));
		assertFalse(configAfter.getBoolean("kthschnll.scoreboardDisabled"));
	}
}
