package com.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyMap;
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
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.ultimate_economy.GeneralEconomyException;

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
	EconomyPlayerValidationHandler validationHandler;
	@Mock
	Logger logger;

	@Test
	public void constructorNewTest() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		Player player = mock(Player.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		when(account.getIban()).thenReturn("myiban");
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(logger, serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager, player, "kthschnll", true);

		assertEquals("kthschnll", ecoPlayer.getName());
		assertEquals(player, ecoPlayer.getPlayer());
		assertTrue(ecoPlayer.getJobList().isEmpty());
		assertTrue(ecoPlayer.getHomeList().isEmpty());
		assertTrue(ecoPlayer.getJoinedTownList().isEmpty());
		assertEquals(account, ecoPlayer.getBankAccount());
		verify(bankManager).createBankAccount(0.0);
		verify(ecoPlayerDao).saveBankIban("kthschnll", "myiban");
		verify(ecoPlayerDao).saveScoreboardDisabled("kthschnll", true);
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
		when(ecoPlayerDao.loadBankIban("kthschnll")).thenReturn("myiban");
		when(ecoPlayerDao.loadScoreboardDisabled("kthschnll")).thenReturn(false);
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		Score score = mock(Score.class);
		when(serverProvider.createScoreBoard()).thenReturn(board);
		when(board.registerNewObjective("bank", "dummy", null)).thenReturn(o);
		when(o.getScore("§6null")).thenReturn(score);
		Location location = mock(Location.class);
		Map<String, Location> homes = new HashMap<>();
		homes.put("myhome", location);
		when(ecoPlayerDao.loadHomeList("kthschnll")).thenReturn(homes);
		when(ecoPlayerDao.loadJobsList("kthschnll")).thenReturn(Arrays.asList("myjob"));
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		when(ecoPlayerDao.loadJoinedTowns("kthschnll")).thenReturn(Arrays.asList("mytown"));
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(logger, serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager, player, "kthschnll", false);
		
		assertEquals("kthschnll", ecoPlayer.getName());
		assertFalse(ecoPlayer.isScoreBoardDisabled());
		assertEquals(ecoPlayer.getBankAccount(), ecoPlayer.getBankAccount());
		assertTrue(ecoPlayer.getHomeList().containsKey("myhome"));
		assertEquals(location, ecoPlayer.getHomeList().get("myhome"));
		assertEquals(1, ecoPlayer.getHomeList().size());
		assertEquals(1, ecoPlayer.getJobList().size());
		assertEquals(job, ecoPlayer.getJobList().get(0));
		assertEquals(1, ecoPlayer.getJoinedTownList().size());
		assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
		verify(serverProvider).createBossBar();
		verify(bossBar).setVisible(false);
		verify(ecoPlayerDao).loadScoreboardDisabled("kthschnll");
		verify(ecoPlayerDao).loadJoinedTowns("kthschnll");
		verify(ecoPlayerDao).loadHomeList("kthschnll");
		verify(ecoPlayerDao).loadJobsList("kthschnll");
		verify(ecoPlayerDao).loadBankIban("kthschnll");
		verify(account).getAmount();
		assertDoesNotThrow(() -> verify(jobManager).getJobByName("myjob"));
		assertDoesNotThrow(() -> verify(bankManager).getBankAccountByIban("myiban"));
	}
	
	@Test
	public void constructorLoadTestWithLoadingErrors() throws GeneralEconomyException {
		BossBar bossBar = mock(BossBar.class);
		Player player = mock(Player.class);
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("my error message");
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		doThrow(e).when(bankManager).getBankAccountByIban("myiban");
		doThrow(e).when(jobManager).getJobByName("myjob");
		when(ecoPlayerDao.loadBankIban("kthschnll")).thenReturn("myiban");
		when(ecoPlayerDao.loadScoreboardDisabled("kthschnll")).thenReturn(false);
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		Score score = mock(Score.class);
		when(serverProvider.createScoreBoard()).thenReturn(board);
		when(board.registerNewObjective("bank", "dummy", null)).thenReturn(o);
		when(o.getScore("§6null")).thenReturn(score);
		Location location = mock(Location.class);
		Map<String, Location> homes = new HashMap<>();
		homes.put("myhome", location);
		when(ecoPlayerDao.loadHomeList("kthschnll")).thenReturn(homes);
		when(ecoPlayerDao.loadJobsList("kthschnll")).thenReturn(Arrays.asList("myjob"));
		when(ecoPlayerDao.loadJoinedTowns("kthschnll")).thenReturn(Arrays.asList("mytown"));
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(logger, serverProvider, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager, player, "kthschnll", false);
		
		assertEquals("kthschnll", ecoPlayer.getName());
		assertFalse(ecoPlayer.isScoreBoardDisabled());
		assertEquals(ecoPlayer.getBankAccount(), ecoPlayer.getBankAccount());
		assertTrue(ecoPlayer.getHomeList().containsKey("myhome"));
		assertEquals(location, ecoPlayer.getHomeList().get("myhome"));
		assertEquals(1, ecoPlayer.getHomeList().size());
		assertEquals(1, ecoPlayer.getJoinedTownList().size());
		assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
		verify(serverProvider).createBossBar();
		verify(bossBar).setVisible(false);
		verify(ecoPlayerDao).loadScoreboardDisabled("kthschnll");
		verify(ecoPlayerDao).loadJoinedTowns("kthschnll");
		verify(ecoPlayerDao).loadHomeList("kthschnll");
		verify(ecoPlayerDao).loadJobsList("kthschnll");
		verify(ecoPlayerDao).loadBankIban("kthschnll");
		assertThrows(GeneralEconomyException.class, () -> jobManager.getJobByName("myjob"));
		assertThrows(GeneralEconomyException.class, () -> bankManager.getBankAccountByIban("myiban"));
		verify(logger).warn("[Ultimate_Economy] Failed to load the bank account myiban for the player kthschnll");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
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
		ecoPlayer.setScoreBoardDisabled(true);
		assertTrue(ecoPlayer.isScoreBoardDisabled());
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
		when(configManager.getMaxJobs()).thenReturn(1);
		when(messageWrapper.getString("job_join", "myjob")).thenReturn("My message.");
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		assertDoesNotThrow(() -> ecoPlayer.joinJob(job, true));

		assertEquals(1, ecoPlayer.getJobList().size());
		assertEquals(job, ecoPlayer.getJobList().get(0));
		verify(ecoPlayer.getPlayer()).sendMessage("My message.");
		assertDoesNotThrow(() -> verify(validationHandler).checkForNotReachedMaxJoinedJobs(false));
		verify(ecoPlayerDao).saveJoinedJobsList(eq("kthschnll"), eq(Arrays.asList(job)));
		assertDoesNotThrow(() -> verify(validationHandler).checkForJobNotJoined(anyList(), eq(job)));
	}

	@Test
	public void joinJobTestWithAlreadyJoined() throws EconomyPlayerException {
		Job job = mock(Job.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForJobNotJoined(anyList(), eq(job));
		EconomyPlayer ecoPlayer = createEcoPlayerMock();

		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.joinJob(job, false));
		verify(ecoPlayerDao, never()).saveJoinedJobsList(eq("kthschnll"), anyList());
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
		when(messageWrapper.getString("job_left", "myjob")).thenReturn("My message.");
		EconomyPlayer ecoPlayer = createEcoPlayerMock();
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		assertDoesNotThrow(() -> ecoPlayer.joinJob(job, false));
		reset(ecoPlayerDao);
		reset(validationHandler);
		assertDoesNotThrow(() -> ecoPlayer.leaveJob(job, true));
		verify(ecoPlayerDao).saveJoinedJobsList(eq("kthschnll"), anyList());
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
		Scoreboard board = mock(Scoreboard.class);
		Objective o = mock(Objective.class);
		Score score = mock(Score.class);
		when(serverProvider.createScoreBoard()).thenReturn(board);
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
		when(serverProvider.createScoreBoard()).thenReturn(board);
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
	public void decreasePlayerAmountTestWithNotEnoughMoney() throws EconomyPlayerException, GeneralEconomyException {
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
		when(messageWrapper.getString("got_money_with_sender", 10.0, "$", "kthschnll")).thenReturn("My message.");
		when(messageWrapper.getString("gave_money", "kthschnll", 10.0, "$")).thenReturn("My message. 2");
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
		when(serverProvider.createScoreBoard()).thenReturn(board);
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
		when(serverProvider.createScoreBoard()).thenReturn(board);

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
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		return new EconomyPlayerImpl(logger, serverProvider, validationHandler, ecoPlayerDao, messageWrapper, configManager,
				bankManager, jobManager, player, "kthschnll", true);
	}
}
