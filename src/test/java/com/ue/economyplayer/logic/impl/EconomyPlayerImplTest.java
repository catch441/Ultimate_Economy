package com.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.bank.logic.impl.BankManagerImpl;
import com.ue.common.utils.BukkitService;
import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerImpl;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerImplTest {

	@Mock
	BukkitService bukkitService;
	@Mock
	ConfigManager configManager;
	@Mock
	BankManager bankManager;
	@Mock
	JobManager jobManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	EconomyPlayerDao ecoPlayerDao;
	@Mock
	EconomyPlayerValidationHandler validationHandler;

	/*
	 * try { EconomyPlayerManagerImpl.createEconomyPlayer("catch441"); EconomyPlayer
	 * ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
	 * assertEquals(1, EconomyPlayerManagerImpl.getAllEconomyPlayers().size());
	 * assertEquals("catch441", ecoPlayer.getName()); assertEquals(player,
	 * ecoPlayer.getPlayer()); assertTrue(ecoPlayer.getJobList().isEmpty());
	 * assertTrue(ecoPlayer.getHomeList().isEmpty());
	 * assertTrue(ecoPlayer.getJoinedTownList().isEmpty());
	 * assertEquals(BankManagerImpl.getBankAccounts().get(0),
	 * ecoPlayer.getBankAccount()); // check savefile File saveFile = new
	 * File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
	 * YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	 * assertEquals(1, config.getStringList("Player").size());
	 * assertEquals("catch441", config.getStringList("Player").get(0));
	 * assertEquals(BankManagerImpl.getBankAccounts().get(0).getIban(),
	 * config.getString("catch441.Iban"));
	 * assertTrue(config.getBoolean("catch441.scoreboardDisabled")); } catch
	 * (EconomyPlayerException e) { assertTrue(false); }
	 */

	@Test
	public void constructorLoadTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayerManagerImpl.createEconomyPlayer("kthschnll");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.addHome("myhome", new Location(world, 1, 2, 3), false);
			ecoPlayer.joinJob(job, false);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().clear();
			EconomyPlayer result = new EconomyPlayerImpl("kthschnll", false);
			assertEquals("kthschnll", result.getName());
			assertTrue(result.isScoreBoardDisabled());
			assertEquals(ecoPlayer.getBankAccount(), result.getBankAccount());
			assertTrue(result.getHomeList().containsKey("myhome"));
			assertEquals("1.0", String.valueOf(result.getHomeList().get("myhome").getX()));
			assertEquals("2.0", String.valueOf(result.getHomeList().get("myhome").getY()));
			assertEquals("3.0", String.valueOf(result.getHomeList().get("myhome").getZ()));
			assertEquals(1, result.getHomeList().size());
			assertEquals(1, result.getJobList().size());
			assertEquals(job, result.getJobList().get(0));
		} catch (EconomyPlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void constructorLoadTestWithInvalidBankAndJob() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("kthschnll");
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			List<String> list = new ArrayList<>();
			list.add("myjob");
			config.set("kthschnll.Jobs", list);
			config.set("kthschnll.Iban", "myiban123");
			config.save(saveFile);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().clear();
			BankManagerImpl.deleteBankAccount(BankManagerImpl.getBankAccounts().get(0));
			EconomyPlayer result = new EconomyPlayerImpl("kthschnll", false);
			assertEquals(0, result.getJobList().size());
			assertNull(result.getBankAccount());
		} catch (EconomyPlayerException | IOException e) {
			fail();
		}
	}

	@Test
	public void isOnlineTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		ecoPlayer.setPlayer(null);
		assertFalse(ecoPlayer.isOnline());
		ecoPlayer.setPlayer(mock(Player.class));
		assertTrue(ecoPlayer.isOnline());
	}

	@Test
	public void isScoreBoardDisabledTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertTrue(ecoPlayer.isScoreBoardDisabled());
		ecoPlayer.setScoreBoardDisabled(false);
		assertFalse(ecoPlayer.isScoreBoardDisabled());
	}

	@Test
	public void getNameTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertEquals("kthschnll", ecoPlayer.getName());
	}

	@Test
	public void addHomeTest() {
		Location location = mock(Location.class);
		when(configManager.getMaxHomes()).thenReturn(3);
		when(messageWrapper.getString("sethome", "myHome")).thenReturn("My message.");

		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> ecoPlayer.addHome("myHome", location, true));
		assertDoesNotThrow(() -> verify(validationHandler).checkForNotExistingHome(anyMap(), eq("myHome")));
		assertTrue(ecoPlayer.getHomeList().containsKey("myHome"));
		assertTrue(ecoPlayer.getHomeList().containsValue(location));
		assertEquals(1, ecoPlayer.getHomeList().size());
		assertDoesNotThrow(() -> verify(validationHandler).checkForNotReachedMaxHomes(false));
		verify(ecoPlayerDao).saveHome("kthschnll", "myHome", location);
		verify(ecoPlayer.getPlayer()).sendMessage("My message.");
	}

	@Test
	public void addHomeTestWithAlredyExists() throws EconomyPlayerException {
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForNotExistingHome(anyMap(), anyString());

		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.addHome("myhome", null, false));
		assertEquals(0, ecoPlayer.getHomeList().size());
		verify(ecoPlayer.getPlayer(), never()).sendMessage(anyString());
	}

	@Test
	public void addHomeTestWithReachedMaxHomes() throws EconomyPlayerException {
		when(configManager.getMaxHomes()).thenReturn(0);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForNotReachedMaxHomes(true);

		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.addHome("myhome", null, false));
		assertEquals(0, ecoPlayer.getHomeList().size());
		verify(ecoPlayer.getPlayer(), never()).sendMessage(anyString());
	}

	@Test
	public void getHomeTest() {
		Location location = mock(Location.class);
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> ecoPlayer.addHome("myhome", location, false));
		assertDoesNotThrow(() -> assertEquals(location, ecoPlayer.getHome("myhome")));
		Map<String, Location> homes = new HashMap<>();
		homes.put("myhome", location);
		assertDoesNotThrow(() -> verify(validationHandler).checkForExistingHome(homes, "myhome"));
	}

	@Test
	public void getHomeTestWithNoHomes() throws EconomyPlayerException {
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForExistingHome(anyMap(), anyString());
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.getHome("myhome"));
	}

	@Test
	public void removeHomeTest() {
		Location location = mock(Location.class);
		when(messageWrapper.getString("delhome", "myhome")).thenReturn("My message.");
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> ecoPlayer.addHome("myhome", location, false));
		assertDoesNotThrow(() -> ecoPlayer.removeHome("myhome", true));
		assertFalse(ecoPlayer.getHomeList().containsKey("myhome"));
		assertFalse(ecoPlayer.getHomeList().containsValue(location));
		assertEquals(0, ecoPlayer.getHomeList().size());
		verify(ecoPlayer.getPlayer()).sendMessage("My message.");
		Map<String, Location> homes = new HashMap<>();
		homes.put("myhome", location);
		assertDoesNotThrow(() -> verify(validationHandler).checkForExistingHome(anyMap(), eq("myhome")));
	}

	@Test
	public void removeHomeTestWithNoHomes() throws EconomyPlayerException {
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForExistingHome(anyMap(), anyString());
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.removeHome("myhome", true));
	}

	@Test
	public void reachedMaxHomesTest() {
		when(configManager.getMaxHomes()).thenReturn(1);
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertFalse(ecoPlayer.reachedMaxHomes());
		assertDoesNotThrow(() -> ecoPlayer.addHome("myhome", null, false));
		assertTrue(ecoPlayer.reachedMaxHomes());
	}

	@Test
	public void reachedMaxJoinedJobsTest() {
		when(configManager.getMaxJobs()).thenReturn(1);
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		Job job1 = mock(Job.class);
		assertFalse(ecoPlayer.reachedMaxJoinedJobs());
		assertDoesNotThrow(() -> ecoPlayer.joinJob(job1, false));
		assertTrue(ecoPlayer.reachedMaxJoinedJobs());
	}

	@Test
	public void getHomeListTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		Location location = mock(Location.class);
		assertDoesNotThrow(() -> ecoPlayer.addHome("myhome", location, false));
		Map<String, Location> list = ecoPlayer.getHomeList();
		assertEquals(1, list.size());
		assertTrue(list.containsKey("myhome"));
		assertTrue(list.containsValue(location));
	}

	@Test
	public void hasEnoughtMoneyTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> when(ecoPlayer.getBankAccount().hasAmount(10)).thenReturn(true));
		assertDoesNotThrow(() -> when(ecoPlayer.getBankAccount().hasAmount(20)).thenReturn(false));
		assertDoesNotThrow(() -> assertTrue(ecoPlayer.hasEnoughtMoney(10)));
		assertDoesNotThrow(() -> assertFalse(ecoPlayer.hasEnoughtMoney(20)));
	}

	@Test
	public void joinJobTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, true);
			assertEquals("§6You have joined the job §amyjob§6.", player.nextMessage());
			assertEquals(1, ecoPlayer.getJobList().size());
			assertEquals(job, ecoPlayer.getJobList().get(0));
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(1, config.getStringList("catch441.Jobs").size());
			assertEquals("myjob", config.getStringList("catch441.Jobs").get(0));
		} catch (EconomyPlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void joinJobTestWithAlreadyJoined() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, false);
			ecoPlayer.joinJob(job, false);
			fail();
		} catch (EconomyPlayerException | JobSystemException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou already joined this job!", e.getMessage());
		}
	}

	@Test
	public void joinJobTestWithMaxReached() {
		when(configManager.getMaxJobs()).thenReturn(0);
		assertDoesNotThrow(() -> doThrow(EconomyPlayerException.class).when(validationHandler)
				.checkForNotReachedMaxJoinedJobs(true));
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		Job job = mock(Job.class);
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.joinJob(job, true));
		assertEquals(0, ecoPlayer.getJobList().size());
		verify(ecoPlayerDao, never()).saveJoinedJobsList(anyString(), anyList());
		verify(ecoPlayer.getPlayer(), never()).sendMessage(anyString());
	}

	@Test
	public void hasJobTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		Job job = mock(Job.class);
		assertFalse(ecoPlayer.hasJob(job));
		assertDoesNotThrow(() -> ecoPlayer.joinJob(job, false));
		assertTrue(ecoPlayer.hasJob(job));
	}

	@Test
	public void leaveJobTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, false);
			ecoPlayer.leaveJob(job, true);
			assertEquals("§6You have left the job §amyjob§6.", player.nextMessage());
			assertEquals(0, ecoPlayer.getJobList().size());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(0, config.getStringList("catch441.Jobs").size());
		} catch (EconomyPlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void leaveJobTestWithJobNotJoined() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.leaveJob(job, false);
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou didnt join this job yet!", e.getMessage());
		}
	}

	@Test
	public void getJobListTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			List<Job> list = ecoPlayer.getJobList();
			assertEquals(1, list.size());
			assertEquals(JobController.getJobList().get(0), list.get(0));
		} catch (EconomyPlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}

	}

	@Test
	public void increasePlayerAmountTest() {
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		Score score = mock(Score.class);
		when(bukkitService.createScoreBoard()).thenReturn(board);
		when(board.registerNewObjective("bank", "dummy", "bank")).thenReturn(o);
		when(o.getScore("§6$")).thenReturn(score);

		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(configManager.getCurrencyText(0.0)).thenReturn("$");
		when(messageWrapper.getString("bank")).thenReturn("bank");
		when(messageWrapper.getString("got_money", 10.0, "$")).thenReturn("My message.");
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		ecoPlayer.setScoreBoardDisabled(false);
		// start test
		when(ecoPlayer.getBankAccount().getAmount()).thenReturn(10.0);
		reset(score);
		assertDoesNotThrow(() -> ecoPlayer.increasePlayerAmount(10.0, true));

		assertDoesNotThrow(() -> verify(ecoPlayer.getBankAccount()).increaseAmount(10.0));
		verify(ecoPlayer.getPlayer()).sendMessage("My message.");
		verify(score).setScore(10);
	}

	@Test
	public void decreasePlayerAmountTest() {
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		Score score = mock(Score.class);
		when(bukkitService.createScoreBoard()).thenReturn(board);
		when(board.registerNewObjective("bank", "dummy", "bank")).thenReturn(o);
		when(o.getScore("§6$")).thenReturn(score);

		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(configManager.getCurrencyText(0.0)).thenReturn("$");
		when(messageWrapper.getString("bank")).thenReturn("bank");
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		ecoPlayer.setScoreBoardDisabled(false);
		// start test
		when(ecoPlayer.getBankAccount().getAmount()).thenReturn(10.0);
		reset(score);
		reset(ecoPlayer.getPlayer());
		assertDoesNotThrow(() -> ecoPlayer.decreasePlayerAmount(10.0, true));

		assertDoesNotThrow(() -> verify(ecoPlayer.getBankAccount()).decreaseAmount(10.0));
		verify(ecoPlayer.getPlayer()).setScoreboard(board);
		verifyNoMoreInteractions(ecoPlayer.getPlayer());
		verify(score).setScore(10);
	}

	@Test
	public void decreasePlayerAmountTestWithNotEnoughMoneyPersonal() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.decreasePlayerAmount(10, true);
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou have not enough money!", e.getMessage());
		}
	}

	@Test
	public void decreasePlayerAmountTestWithNotEnoughMoney() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.decreasePlayerAmount(10, false);
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cThe player has not enough money!", e.getMessage());
		}
	}

	@Test
	public void getPlayerTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertEquals(ecoPlayer.getPlayer(), ecoPlayer.getPlayer());
	}

	@Test
	public void addWildernessPermissionTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.denyWildernessPermission();
			assertFalse(ecoPlayer.getPlayer().hasPermission("ultimate_economy.wilderness"));
			ecoPlayer.addWildernessPermission();
			assertTrue(ecoPlayer.getPlayer().hasPermission("ultimate_economy.wilderness"));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void denyWildernessPermissionTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.addWildernessPermission();
			assertTrue(ecoPlayer.getPlayer().hasPermission("ultimate_economy.wilderness"));
			ecoPlayer.denyWildernessPermission();
			assertFalse(ecoPlayer.getPlayer().hasPermission("ultimate_economy.wilderness"));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void payToOtherPlayerTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			PlayerMock recieverPlayer = server.addPlayer("kthschnll");
			EconomyPlayer reciever = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.setScoreBoardDisabled(false);
			reciever.setScoreBoardDisabled(false);
			ecoPlayer.increasePlayerAmount(10, false);
			ecoPlayer.payToOtherPlayer(reciever, 10, true);
			server.getScheduler().performOneTick();
			assertEquals("§6You got §a10.0§6 §a$§6 from §acatch441§6.", recieverPlayer.nextMessage());
			assertEquals("10.0", String.valueOf(reciever.getBankAccount().getAmount()));
			assertEquals("§6You gave §akthschnll§6 §a10.0§6 §a$§6.", player.nextMessage());
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			// check scoreboards
			assertEquals(1, recieverPlayer.getScoreboard().getObjectives().size());
			assertEquals("10", String.valueOf(
					recieverPlayer.getScoreboard().getObjective("bank").getScore(ChatColor.GOLD + "$").getScore()));
			assertEquals(1, player.getScoreboard().getObjectives().size());
			assertEquals("0", String
					.valueOf(player.getScoreboard().getObjective("bank").getScore(ChatColor.GOLD + "$").getScore()));
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void addJoinedTownTest() {
		when(configManager.getMaxJoinedTowns()).thenReturn(1);
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> ecoPlayer.addJoinedTown("mytown"));
		assertEquals(1, ecoPlayer.getJoinedTownList().size());
		assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForNotReachedMaxJoinedTowns(false));
		assertDoesNotThrow(() -> verify(validationHandler).checkForTownNotJoined(anyList(), eq("mytown")));
		verify(ecoPlayerDao).saveJoinedTowns("kthschnll", Arrays.asList("mytown"));
	}

	@Test
	public void addJoinedTownTestWithAlreadyJoined() throws EconomyPlayerException {
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForTownNotJoined(anyList(), eq("mytown"));
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.addJoinedTown("mytown"));
		assertEquals(0, ecoPlayer.getJoinedTownList().size());
		verify(ecoPlayerDao, never()).saveJoinedTowns(anyString(), anyList());
	}

	@Test
	public void addJoinedTownTestWithMaxTownsReached() throws EconomyPlayerException {
		when(configManager.getMaxJoinedTowns()).thenReturn(0);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForNotReachedMaxJoinedTowns(true);
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.addJoinedTown("mytown"));
		assertEquals(0, ecoPlayer.getJoinedTownList().size());
		verify(ecoPlayerDao, never()).saveJoinedTowns(anyString(), anyList());
	}

	@Test
	public void getJoinedTownListTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> ecoPlayer.addJoinedTown("mytown"));
		assertEquals(1, ecoPlayer.getJoinedTownList().size());
		assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
	}

	@Test
	public void reachedMaxJoinedTownsTest() {
		when(configManager.getMaxJoinedTowns()).thenReturn(1);
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertFalse(ecoPlayer.reachedMaxJoinedTowns());
		assertDoesNotThrow(() -> ecoPlayer.addJoinedTown("mytown"));
		assertTrue(ecoPlayer.reachedMaxJoinedTowns());
	}

	@Test
	public void removeJoinedTown() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> ecoPlayer.addJoinedTown("mytown"));
		reset(ecoPlayerDao);
		reset(validationHandler);
		assertDoesNotThrow(() -> ecoPlayer.removeJoinedTown("mytown"));
		assertEquals(0, ecoPlayer.getJoinedTownList().size());
		verify(ecoPlayerDao).saveJoinedTowns("kthschnll", new ArrayList<String>());
		assertDoesNotThrow(() -> verify(validationHandler).checkForJoinedTown(anyList(), eq("mytown")));
	}

	@Test
	public void removeJoinedTownWithTownNotJoined() throws EconomyPlayerException {
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForJoinedTown(anyList(), anyString());
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.removeJoinedTown("mytown"));
		verify(ecoPlayerDao, never()).saveJoinedTowns(anyString(), anyList());
		assertEquals(0, ecoPlayer.getJoinedTownList().size());
	}

	@Test
	public void setScoreBoardDisabledTestFalse() {
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		Score score = mock(Score.class);
		when(bukkitService.createScoreBoard()).thenReturn(board);
		when(board.registerNewObjective("bank", "dummy", "bank")).thenReturn(o);
		when(o.getScore("§6$")).thenReturn(score);

		when(configManager.getCurrencyText(0.0)).thenReturn("$");
		when(messageWrapper.getString("bank")).thenReturn("bank");
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		reset(ecoPlayer.getPlayer());
		ecoPlayer.setScoreBoardDisabled(false);
		assertFalse(ecoPlayer.isScoreBoardDisabled());
		verify(ecoPlayerDao).saveScoreboardDisabled("kthschnll", false);
		verify(ecoPlayer.getPlayer()).setScoreboard(board);
		verify(board).registerNewObjective("bank", "dummy", "bank");
	}

	@Test
	public void setScoreBoardDisabledTestTrue() {
		Scoreboard board = mock(Scoreboard.class);
		when(bukkitService.createScoreBoard()).thenReturn(board);

		// invoked at player creation
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertTrue(ecoPlayer.isScoreBoardDisabled());
		verify(ecoPlayerDao).saveScoreboardDisabled("kthschnll", true);
		verify(ecoPlayer.getPlayer()).setScoreboard(board);
		verify(board, never()).registerNewObjective("bank", "dummy", "bank");
	}

	private EconomyPlayer createEcoPlayerMock() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		Player player = mock(Player.class);
		when(bukkitService.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		return new EconomyPlayerImpl(bukkitService, validationHandler, ecoPlayerDao, messageWrapper, configManager,
				bankManager, jobManager, player, "kthschnll", true);
	}
}
