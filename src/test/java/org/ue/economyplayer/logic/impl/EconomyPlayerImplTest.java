package org.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankException;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerValidator;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.JobsystemException;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerImplTest {

	@Mock
	ServerProvider serverProvider;
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
	EconomyPlayerValidator validationHandler;

	@Test
	public void constructorNewTest() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		Player player = mock(Player.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		when(account.getIban()).thenReturn("myiban");
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager);
		ecoPlayer.setupNew(player, "catch441");
		
		assertEquals("catch441", ecoPlayer.getName());
		assertEquals(player, ecoPlayer.getPlayer());
		assertTrue(ecoPlayer.getJobList().isEmpty());
		assertTrue(ecoPlayer.getHomeList().isEmpty());
		assertTrue(ecoPlayer.getJoinedTownList().isEmpty());
		assertEquals(account, ecoPlayer.getBankAccount());
		verify(bankManager).createBankAccount(0.0);
		verify(ecoPlayerDao).saveBankIban("catch441", "myiban");
		verify(ecoPlayerDao).saveScoreboardObjectiveVisible("catch441", false);
		verify(serverProvider).createBossBar();
		verify(bossBar).setVisible(false);
	}

	@Test
	public void constructorLoadTest() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		Player player = mock(Player.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("myiban")).thenReturn(account));
		when(ecoPlayerDao.loadBankIban("catch441")).thenReturn("myiban");
		when(ecoPlayerDao.loadScoreboardObjectiveVisible("catch441")).thenReturn(true);
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		Score score = mock(Score.class);
		when(account.getAmount()).thenReturn(10.5);
		when(serverProvider.createScoreboard()).thenReturn(board);
		when(player.getScoreboard()).thenReturn(board);
		when(board.registerNewObjective("bank", "dummy", null)).thenReturn(o);
		when(board.getObjective(DisplaySlot.SIDEBAR)).thenReturn(o);
		when(o.getScore("§6null")).thenReturn(score);
		Location location = mock(Location.class);
		Map<String, Location> homes = new HashMap<>();
		homes.put("myhome", location);
		when(ecoPlayerDao.loadHomeList("catch441")).thenReturn(homes);
		when(ecoPlayerDao.loadJobsList("catch441")).thenReturn(Arrays.asList("myjob"));
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		when(ecoPlayerDao.loadJoinedTowns("catch441")).thenReturn(Arrays.asList("mytown"));
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager);
		ecoPlayer.setupExisting(player, "catch441");

		assertEquals("catch441", ecoPlayer.getName());
		assertTrue(ecoPlayer.isScoreBoardObjectiveVisible());
		assertEquals(ecoPlayer.getBankAccount(), ecoPlayer.getBankAccount());
		assertTrue(ecoPlayer.getHomeList().containsKey("myhome"));
		assertEquals(location, ecoPlayer.getHomeList().get("myhome"));
		assertEquals(1, ecoPlayer.getHomeList().size());
		assertEquals(1, ecoPlayer.getJobList().size());
		assertEquals(job, ecoPlayer.getJobList().get(0));
		assertEquals(1, ecoPlayer.getJoinedTownList().size());
		assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
		verify(player).setScoreboard(board);
		verify(score).setScore(10);
		verify(serverProvider).createBossBar();
		verify(bossBar).setVisible(false);
		verify(ecoPlayerDao).loadScoreboardObjectiveVisible("catch441");
		verify(ecoPlayerDao).loadJoinedTowns("catch441");
		verify(ecoPlayerDao).loadHomeList("catch441");
		verify(ecoPlayerDao).loadJobsList("catch441");
		verify(ecoPlayerDao).loadBankIban("catch441");
		verify(account).getAmount();
		assertDoesNotThrow(() -> verify(jobManager).getJobByName("myjob"));
		assertDoesNotThrow(() -> verify(bankManager).getBankAccountByIban("myiban"));
	}

	@Test
	public void constructorLoadTestWithLoadingErrors() throws JobsystemException, BankException {
		BossBar bossBar = mock(BossBar.class);
		Player player = mock(Player.class);
		JobsystemException e = mock(JobsystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		BankException e2 = mock(BankException.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		doThrow(e2).when(bankManager).getBankAccountByIban("myiban");
		doThrow(e).when(jobManager).getJobByName("myjob");
		when(ecoPlayerDao.loadBankIban("catch441")).thenReturn("myiban");
		when(ecoPlayerDao.loadScoreboardObjectiveVisible("catch441")).thenReturn(true);
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		Score score = mock(Score.class);
		when(serverProvider.createScoreboard()).thenReturn(board);
		when(player.getScoreboard()).thenReturn(board);
		when(board.registerNewObjective("bank", "dummy", null)).thenReturn(o);
		when(board.getObjective(DisplaySlot.SIDEBAR)).thenReturn(o);
		when(o.getScore("§6null")).thenReturn(score);
		Location location = mock(Location.class);
		Map<String, Location> homes = new HashMap<>();
		homes.put("myhome", location);
		when(ecoPlayerDao.loadHomeList("catch441")).thenReturn(homes);
		when(ecoPlayerDao.loadJobsList("catch441")).thenReturn(Arrays.asList("myjob"));
		when(ecoPlayerDao.loadJoinedTowns("catch441")).thenReturn(Arrays.asList("mytown"));
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager);
		ecoPlayer.setupExisting(player, "catch441");

		assertEquals("catch441", ecoPlayer.getName());
		assertTrue(ecoPlayer.isScoreBoardObjectiveVisible());
		assertEquals(ecoPlayer.getBankAccount(), ecoPlayer.getBankAccount());
		assertTrue(ecoPlayer.getHomeList().containsKey("myhome"));
		assertEquals(location, ecoPlayer.getHomeList().get("myhome"));
		assertEquals(1, ecoPlayer.getHomeList().size());
		assertEquals(1, ecoPlayer.getJoinedTownList().size());
		assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
		assertNull(ecoPlayer.getBankAccount());
		verify(serverProvider).createBossBar();
		verify(bossBar).setVisible(false);
		verify(ecoPlayerDao).loadScoreboardObjectiveVisible("catch441");
		verify(ecoPlayerDao).loadJoinedTowns("catch441");
		verify(ecoPlayerDao).loadHomeList("catch441");
		verify(ecoPlayerDao).loadJobsList("catch441");
		verify(ecoPlayerDao).loadBankIban("catch441");
		JobsystemException ex = assertThrows(JobsystemException.class, () -> jobManager.getJobByName("myjob"));
		assertThrows(BankException.class, () -> bankManager.getBankAccountByIban("myiban"));
		verify(ex).getMessage();
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
		assertFalse(ecoPlayer.isScoreBoardObjectiveVisible());
		ecoPlayer.setScoreBoardObjectiveVisible(false);
		assertFalse(ecoPlayer.isScoreBoardObjectiveVisible());
	}

	@Test
	public void getNameTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertEquals("catch441", ecoPlayer.getName());
	}

	@Test
	public void addHomeTest() {
		Location location = mock(Location.class);
		when(configManager.getMaxHomes()).thenReturn(3);
		when(messageWrapper.getString(MessageEnum.CREATED, "myHome")).thenReturn("My message.");

		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> ecoPlayer.addHome("myHome", location, true));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueNotInList(anyList(), eq("myHome")));
		assertTrue(ecoPlayer.getHomeList().containsKey("myHome"));
		assertTrue(ecoPlayer.getHomeList().containsValue(location));
		assertEquals(1, ecoPlayer.getHomeList().size());
		assertDoesNotThrow(() -> verify(validationHandler).checkForNotReachedMax(false));
		verify(ecoPlayerDao).saveHome("catch441", "myHome", location);
		verify(ecoPlayer.getPlayer()).sendMessage("My message.");
	}

	@Test
	public void addHomeTestWithAlredyExists() throws EconomyPlayerException {
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForValueNotInList(anyList(), anyString());

		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.addHome("myhome", null, false));
		assertEquals(0, ecoPlayer.getHomeList().size());
		verify(ecoPlayer.getPlayer(), never()).sendMessage(anyString());
	}

	@Test
	public void addHomeTestWithReachedMaxHomes() throws EconomyPlayerException {
		when(configManager.getMaxHomes()).thenReturn(0);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForNotReachedMax(true);

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
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(Arrays.asList("myhome"), "myhome"));
	}

	@Test
	public void getHomeTestWithNoHomes() throws EconomyPlayerException {
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForValueInList(anyList(), anyString());
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.getHome("myhome"));
	}

	@Test
	public void removeHomeTest() {
		Location location = mock(Location.class);
		when(messageWrapper.getString(MessageEnum.DELETED, "myhome")).thenReturn("My message.");
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> ecoPlayer.addHome("myhome", location, false));
		assertDoesNotThrow(() -> ecoPlayer.removeHome("myhome", true));
		assertFalse(ecoPlayer.getHomeList().containsKey("myhome"));
		assertFalse(ecoPlayer.getHomeList().containsValue(location));
		assertEquals(0, ecoPlayer.getHomeList().size());
		verify(ecoPlayer.getPlayer()).sendMessage("My message.");
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(Arrays.asList("myhome"), "myhome"));
	}

	@Test
	public void removeHomeTestWithNoHomes() throws EconomyPlayerException {
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForValueInList(anyList(), anyString());
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
		when(ecoPlayer.getBankAccount().getAmount()).thenReturn(10.0);
		assertDoesNotThrow(() -> assertTrue(ecoPlayer.hasEnoughtMoney(10)));
		assertDoesNotThrow(() -> assertFalse(ecoPlayer.hasEnoughtMoney(20)));
		
	}

	@Test
	public void joinJobTest() {
		when(configManager.getMaxJobs()).thenReturn(1);
		when(messageWrapper.getString(MessageEnum.JOB_JOIN, "myjob")).thenReturn("My message.");
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		assertDoesNotThrow(() -> ecoPlayer.joinJob(job, true));

		assertEquals(1, ecoPlayer.getJobList().size());
		assertEquals(job, ecoPlayer.getJobList().get(0));
		verify(ecoPlayer.getPlayer()).sendMessage("My message.");
		assertDoesNotThrow(() -> verify(validationHandler).checkForNotReachedMax(false));
		verify(ecoPlayerDao).saveJoinedJobsList(eq("catch441"), eq(Arrays.asList(job)));
		assertDoesNotThrow(() -> verify(validationHandler).checkForJobNotJoined(anyList(), eq(job)));
	}

	@Test
	public void joinJobTestWithAlreadyJoined() throws EconomyPlayerException {
		Job job = mock(Job.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForJobNotJoined(anyList(), eq(job));
		EconomyPlayer ecoPlayer = createEcoPlayerMock();

		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.joinJob(job, false));
		verify(ecoPlayerDao, never()).saveJoinedJobsList(eq("catch441"), anyList());
	}

	@Test
	public void joinJobTestWithMaxReached() {
		when(configManager.getMaxJobs()).thenReturn(0);
		assertDoesNotThrow(() -> doThrow(EconomyPlayerException.class).when(validationHandler)
				.checkForNotReachedMax(true));
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
		when(messageWrapper.getString(MessageEnum.JOB_LEFT, "myjob")).thenReturn("My message.");
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		assertDoesNotThrow(() -> ecoPlayer.joinJob(job, false));
		reset(ecoPlayerDao);
		reset(validationHandler);
		assertDoesNotThrow(() -> ecoPlayer.leaveJob(job, true));
		verify(ecoPlayerDao).saveJoinedJobsList(eq("catch441"), anyList());
		assertDoesNotThrow(() -> verify(validationHandler).checkForJobJoined(eq(new ArrayList<>()), eq(job)));
		verify(ecoPlayer.getPlayer()).sendMessage("My message.");
		assertEquals(0, ecoPlayer.getJobList().size());
	}

	@Test
	public void leaveJobTestWithJobNotJoined() throws EconomyPlayerException {
		Job job = mock(Job.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForJobJoined(anyList(), eq(job));
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.leaveJob(job, false));
		verify(ecoPlayerDao, never()).saveJoinedJobsList(anyString(), anyList());
	}

	@Test
	public void getJobListTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> ecoPlayer.joinJob(job, false));
		List<Job> list = ecoPlayer.getJobList();
		assertEquals(1, list.size());
		assertEquals(job, list.get(0));
	}

	@Test
	public void increasePlayerAmountTest() {
		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(configManager.getCurrencyText(0.0)).thenReturn("$");
		when(messageWrapper.getString(MessageEnum.BANK)).thenReturn("Bank");
		when(messageWrapper.getString(MessageEnum.GOT_MONEY, 10.0, "$")).thenReturn("My message.");
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		Player player = mock(Player.class);
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		when(board.registerNewObjective("bank", "dummy", "Bank")).thenReturn(o);
		when(board.getObjective(DisplaySlot.SIDEBAR)).thenReturn(o);
		when(serverProvider.createScoreboard()).thenReturn(board);
		when(player.getScoreboard()).thenReturn(board);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);

		Score score = mock(Score.class);
		when(o.getScore("§6$")).thenReturn(score);

		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager);
		ecoPlayer.setupNew(player, "catch441");
		
		ecoPlayer.setScoreBoardObjectiveVisible(true);
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
		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(configManager.getCurrencyText(0.0)).thenReturn("$");
		when(messageWrapper.getString(MessageEnum.BANK)).thenReturn("Bank");

		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		Player player = mock(Player.class);
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		when(board.registerNewObjective("bank", "dummy", "Bank")).thenReturn(o);
		when(board.getObjective(DisplaySlot.SIDEBAR)).thenReturn(o);
		when(player.getScoreboard()).thenReturn(board);
		when(serverProvider.createScoreboard()).thenReturn(board);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		Score score = mock(Score.class);
		when(o.getScore("§6$")).thenReturn(score);
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager);
		ecoPlayer.setupNew(player, "catch441");

		ecoPlayer.setScoreBoardObjectiveVisible(true);
		verify(player).setScoreboard(board);
		// start test
		when(ecoPlayer.getBankAccount().getAmount()).thenReturn(10.0);

		assertDoesNotThrow(() -> ecoPlayer.decreasePlayerAmount(10.0, true));

		assertDoesNotThrow(() -> verify(ecoPlayer.getBankAccount()).decreaseAmount(10.0));
		verifyNoMoreInteractions(ecoPlayer.getPlayer());
		verify(score).setScore(10);
	}

	@Test
	public void decreasePlayerAmountTestWithNotEnoughMoney() throws EconomyPlayerException {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForEnoughMoney(ecoPlayer.getBankAccount(),
				10, true);
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.decreasePlayerAmount(10, true));
		assertDoesNotThrow(() -> verify(ecoPlayer.getBankAccount(), never()).decreaseAmount(10));
	}

	@Test
	public void getPlayerTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertEquals(ecoPlayer.getPlayer(), ecoPlayer.getPlayer());
	}

	@Test
	public void addWildernessPermissionTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		PermissionAttachment att = mock(PermissionAttachment.class);
		when(ecoPlayer.getPlayer().addAttachment(null)).thenReturn(att);
		ecoPlayer.addWildernessPermission();
		verify(att).setPermission("ultimate_economy.wilderness", true);
	}

	@Test
	public void denyWildernessPermissionTest() {
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		PermissionAttachment att = mock(PermissionAttachment.class);
		when(ecoPlayer.getPlayer().addAttachment(null)).thenReturn(att);
		ecoPlayer.denyWildernessPermission();
		verify(att).setPermission("ultimate_economy.wilderness", false);
	}

	@Test
	public void payToOtherPlayerTest() {
		when(configManager.getCurrencyText(10)).thenReturn("$");
		when(messageWrapper.getString(MessageEnum.GOT_MONEY_WITH_SENDER, 10.0, "$", "catch441")).thenReturn("My message.");
		when(messageWrapper.getString(MessageEnum.GAVE_MONEY, "catch441", 10.0, "$")).thenReturn("My message. 2");
		EconomyPlayer reciever = createEcoPlayerMock();
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> ecoPlayer.payToOtherPlayer(reciever, 10.0, true));
		verify(reciever.getPlayer()).sendMessage(eq("My message."));
		verify(ecoPlayer.getPlayer()).sendMessage(eq("My message. 2"));
		assertDoesNotThrow(() -> verify(reciever.getBankAccount()).increaseAmount(eq(10.0)));
		assertDoesNotThrow(() -> verify(ecoPlayer.getBankAccount()).decreaseAmount(eq(10.0)));
	}

	@Test
	public void addJoinedTownTest() {
		when(configManager.getMaxJoinedTowns()).thenReturn(1);
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		assertDoesNotThrow(() -> ecoPlayer.addJoinedTown("mytown"));
		assertEquals(1, ecoPlayer.getJoinedTownList().size());
		assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForNotReachedMax(false));
		assertDoesNotThrow(() -> verify(validationHandler).checkForTownNotJoined(anyList(), eq("mytown")));
		verify(ecoPlayerDao).saveJoinedTowns("catch441", Arrays.asList("mytown"));
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
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForNotReachedMax(true);
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
		verify(ecoPlayerDao).saveJoinedTowns("catch441", new ArrayList<String>());
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
	public void setScoreBoardObjectiveVisibleTestTrue() {
		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(messageWrapper.getString(MessageEnum.BANK)).thenReturn("bank");
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		Player player = mock(Player.class);
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		Score score = mock(Score.class);
		when(serverProvider.createScoreboard()).thenReturn(board);
		when(player.getScoreboard()).thenReturn(board);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		when(account.getAmount()).thenReturn(10.0);
		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(board.registerNewObjective("bank", "dummy", "bank")).thenReturn(o);
		when(board.getObjective(DisplaySlot.SIDEBAR)).thenReturn(o);
		when(o.getScore("§6$")).thenReturn(score);
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager);
		ecoPlayer.setupNew(player, "catch441");

		ecoPlayer.setScoreBoardObjectiveVisible(true);
		assertTrue(ecoPlayer.isScoreBoardObjectiveVisible());
		verify(ecoPlayerDao).saveScoreboardObjectiveVisible("catch441", true);
		verify(board).registerNewObjective("bank", "dummy", "bank");
		verify(o).setDisplaySlot(DisplaySlot.SIDEBAR);
		verify(score).setScore(10);
	}

	@Test
	public void setScoreBoardObjectiveVisibleTestFalse() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		Player player = mock(Player.class);
		Scoreboard board = mock(Scoreboard.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager);
		ecoPlayer.setupNew(player, "catch441");

		assertFalse(ecoPlayer.isScoreBoardObjectiveVisible());
		verify(ecoPlayerDao).saveScoreboardObjectiveVisible("catch441", false);
		verify(board, never()).registerNewObjective("bank", "dummy", "bank");
	}
	
	@Test
	public void setScoreBoardObjectiveVisibleTestFalseWithOldTrue() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		Player player = mock(Player.class);
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		Score score = mock(Score.class);
		when(messageWrapper.getString(MessageEnum.BANK)).thenReturn("bank");
		when(serverProvider.createScoreboard()).thenReturn(board);
		when(player.getScoreboard()).thenReturn(board);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		when(account.getAmount()).thenReturn(10.0);
		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(board.registerNewObjective("bank", "dummy", "bank")).thenReturn(o);
		when(o.getScore("§6$")).thenReturn(score);
		when(board.getObjective(DisplaySlot.SIDEBAR)).thenReturn(o);
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager);
		ecoPlayer.setupNew(player, "catch441");
		reset(ecoPlayerDao);
		ecoPlayer.setScoreBoardObjectiveVisible(true);
		reset(board);
		when(board.getObjective(DisplaySlot.SIDEBAR)).thenReturn(o);
		ecoPlayer.setScoreBoardObjectiveVisible(false);
		
		assertFalse(ecoPlayer.isScoreBoardObjectiveVisible());
		verify(ecoPlayerDao).saveScoreboardObjectiveVisible("catch441", false);
		verify(board, never()).registerNewObjective("bank", "dummy", "bank");
		verify(o).unregister();
	}

	private EconomyPlayer createEcoPlayerMock() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		Player player = mock(Player.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager);
		ecoPlayer.setupNew(player, "catch441");
		return ecoPlayer;
	}
}
