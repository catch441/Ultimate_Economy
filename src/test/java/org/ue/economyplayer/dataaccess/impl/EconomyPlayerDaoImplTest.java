package org.ue.economyplayer.dataaccess.impl;

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
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.utils.ServerProvider;
import org.ue.jobsystem.logic.api.Job;

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
		ecoPlayerDao.saveBankIban("mycatch", "myiban");
		ecoPlayerDao.setupSavefile();
		File result = new File("src/PlayerFile.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertTrue(config.isSet("mycatch"));
	}

	@Test
	public void loadPlayerListTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.saveBankIban("mycatch", "myiban");
		ecoPlayerDao.setupSavefile();
		List<String> result = ecoPlayerDao.loadPlayerList();
		assertEquals(1, result.size());
		assertEquals("mycatch", result.get(0));
	}
	
	@Test
	public void loadPlayerListOldTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		File file = new File("src/PlayerFile.yml");
		YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
		configBefore.set("Player", "mycatch");
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
		ecoPlayerDao.saveHome("catch", "myNewHome", loc);
		File result = new File("src/PlayerFile.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertEquals("myNewHome", config.getString("catch.Home.myNewHome.Name"));
		assertEquals("World", config.getString("catch.Home.myNewHome.World"));
		assertEquals("1.0", config.getString("catch.Home.myNewHome.X"));
		assertEquals("2.0", config.getString("catch.Home.myNewHome.Y"));
		assertEquals("3.0", config.getString("catch.Home.myNewHome.Z"));
		ecoPlayerDao.saveHome("catch", "myNewHome", null);
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(result);
		assertFalse(configAfter.isSet("catch.Home.myNewHome"));
	}

	@Test
	public void loadHomeListTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		when(serverProvider.getWorld("World")).thenReturn(world);

		Location loc = new Location(world, 1, 2, 3);
		ecoPlayerDao.saveHome("catch", "myHome", loc);
		ecoPlayerDao.setupSavefile();
		Map<String, Location> list = ecoPlayerDao.loadHomeList("catch");
		Location result = list.get("myHome");
		assertEquals("World", result.getWorld().getName());
		assertEquals("1.0", String.valueOf(result.getX()));
		assertEquals("2.0", String.valueOf(result.getY()));
		assertEquals("3.0", String.valueOf(result.getZ()));
		assertEquals(1, list.size());
	}
	
	@Test
	public void loadHomeListTestWithoudHomes() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();

		Map<String, Location> list = ecoPlayerDao.loadHomeList("catch441");
		assertEquals(0, list.size());
	}
	
	@Test
	public void loadHomeListOldTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		File file = new File("src/PlayerFile.yml");
		YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
		configBefore.set("catch..Home.Homelist", Arrays.asList("myHome"));
		assertDoesNotThrow(() -> configBefore.save(file));
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.loadHomeList("catch");
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("catch..Home.Homelist"));
	}

	@Test
	public void loadJoinedTownsTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.saveJoinedTowns("catch", Arrays.asList("mytown"));
		ecoPlayerDao.setupSavefile();
		List<String> list = ecoPlayerDao.loadJoinedTowns("catch");
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
		ecoPlayerDao.saveJoinedJobsList("catch", jobs);
		ecoPlayerDao.setupSavefile();
		List<String> list = ecoPlayerDao.loadJobsList("catch");
		assertEquals(1, list.size());
		assertEquals("myjob", list.get(0));
	}

	@Test
	public void loadBankIbanTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.saveBankIban("catch", "myIban");
		ecoPlayerDao.setupSavefile();
		assertEquals("myIban", ecoPlayerDao.loadBankIban("catch"));
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
		configBefore.set("catch.account amount", 20.5);
		assertDoesNotThrow(() -> configBefore.save(file));
		ecoPlayerDao.setupSavefile();
		assertEquals("myIban", ecoPlayerDao.loadBankIban("catch"));
		verify(bankManager).createBankAccount(20.5);
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("catch.account amount"));
	}

	@Test
	public void loadScoreboardObjectiveVisibleTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		ecoPlayerDao.saveScoreboardObjectiveVisible("catch", true);
		ecoPlayerDao.setupSavefile();
		assertTrue(ecoPlayerDao.loadScoreboardObjectiveVisible("catch"));
	}

	@Test
	public void loadScoreboardObjectiveVisibleTestOld() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		ecoPlayerDao.setupSavefile();
		File file = new File("src/PlayerFile.yml");
		YamlConfiguration configBefore = YamlConfiguration.loadConfiguration(file);
		configBefore.set("catch.bank", false);
		assertDoesNotThrow(() -> configBefore.save(file));
		ecoPlayerDao.setupSavefile();
		assertTrue(ecoPlayerDao.loadScoreboardObjectiveVisible("catch"));
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("catch.bank"));
		assertFalse(configAfter.getBoolean("catch.scoreboardDisabled"));
	}
}
