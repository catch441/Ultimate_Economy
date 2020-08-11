package com.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

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
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		when(bukkitService.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);

		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(bukkitService, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager, null, "kthschnll", true);
		assertFalse(ecoPlayer.isOnline());
		ecoPlayer.setPlayer(mock(Player.class));
		assertTrue(ecoPlayer.isOnline());
	}

	@Test
	public void isScoreBoardDisabledTest() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		when(bukkitService.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);

		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(bukkitService, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager, null, "kthschnll", true);
		assertTrue(ecoPlayer.isScoreBoardDisabled());
		ecoPlayer.setScoreBoardDisabled(false);
		assertFalse(ecoPlayer.isScoreBoardDisabled());
	}

	@Test
	public void getNameTest() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		when(bukkitService.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);

		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(bukkitService, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager, null, "kthschnll", true);
		assertEquals("kthschnll", ecoPlayer.getName());
	}

	@Test
	public void addHomeTest() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		//Location location = mock(Location.class);
		Player player = mock(Player.class);
		when(bukkitService.createBossBar()).thenReturn(bossBar);
		when(configManager.getMaxHomes()).thenReturn(3);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		when(messageWrapper.getString("sethome", "myHome")).thenReturn("My message.");

		EconomyPlayerImpl ecoPlayer = new EconomyPlayerImpl(bukkitService, validationHandler, ecoPlayerDao, messageWrapper,
				configManager, bankManager, jobManager, player, "kthschnll", true);
		try {
			ecoPlayer.addHome("myHome", null, true);
		} catch (EconomyPlayerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String, Location> empty = new HashMap<String, Location>();
		try {
			verify(validationHandler).checkForNotExistingHome(empty, "myHome");
		} catch (EconomyPlayerException e) {
			fail();
		}
		assertTrue(ecoPlayer.getHomeList().containsKey("myHome"));
		//assertTrue(ecoPlayer.getHomeList().containsValue(location));
		assertEquals(1, ecoPlayer.getHomeList().size());
		//assertDoesNotThrow(() -> verify(validationHandler).checkForNotReachedMaxHomes(false));
		//verify(ecoPlayerDao).saveHome("kthschnll", "myHome", location);
		verify(player).sendMessage("My message.");
	}

	@Test
	public void addHomeTestWithAlredyExists() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			ecoPlayer.addHome("myhome", loc, false);
			fail();
		} catch (EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cThis home already exists!", e.getMessage());
		}
	}

	@Test
	public void addHomeTestWithReachedMaxHomes() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			ecoPlayer.addHome("myhome1", loc, false);
			ecoPlayer.addHome("myhome2", loc, false);
			ecoPlayer.addHome("myhome3", loc, false);
			fail();
		} catch (EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou have already reached the maximum!", e.getMessage());
		}
	}

	@Test
	public void getHomeTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			assertEquals(loc, ecoPlayer.getHome("myhome"));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void getHomeTestWithNoHomes() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).getHome("myhome");
			fail();
		} catch (EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cThis home does not exist!", e.getMessage());
		}
	}

	@Test
	public void removeHomeTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			ecoPlayer.removeHome("myhome", true);
			assertEquals("§6Your home §amyhome§6 was deleted.", player.nextMessage());
			assertFalse(ecoPlayer.getHomeList().containsKey("myhome"));
			assertFalse(ecoPlayer.getHomeList().containsValue(loc));
			assertEquals(0, ecoPlayer.getHomeList().size());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(0, config.getStringList("catch441.Home.Homelist").size());
			assertFalse(config.isSet("catch441.Home.myhome.Name"));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void removeHomeTestWithNoHomes() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).removeHome("myhome", true);
			fail();
		} catch (EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cThis home does not exist!", e.getMessage());
		}
	}

	@Test
	public void reachedMaxHomesTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			ecoPlayer.addHome("myhome1", loc, false);
			assertFalse(ecoPlayer.reachedMaxHomes());
			ecoPlayer.addHome("myhome2", loc, false);
			assertTrue(ecoPlayer.reachedMaxHomes());
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void reachedMaxJoinedJobsTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			JobController.createJob("job1");
			JobController.createJob("job2");
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			assertFalse(ecoPlayer.reachedMaxJoinedJobs());
			ecoPlayer.joinJob(JobController.getJobList().get(1), false);
			assertTrue(ecoPlayer.reachedMaxJoinedJobs());
		} catch (EconomyPlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void getHomeListTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			Map<String, Location> list = ecoPlayer.getHomeList();
			assertEquals(1, list.size());
			assertTrue(list.containsKey("myhome"));
			assertTrue(list.containsValue(loc));
		} catch (EconomyPlayerException e) {
			fail();
		}

	}

	@Test
	public void getBankAccountTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			String iban = ecoPlayer.getBankAccount().getIban();
			assertEquals(BankManagerImpl.getBankAccountByIban(iban), ecoPlayer.getBankAccount());
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}

	}

	@Test
	public void hasEnoughtMoneyTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(10, false);
			assertTrue(ecoPlayer.hasEnoughtMoney(10));
			assertFalse(ecoPlayer.hasEnoughtMoney(20));
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
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
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob1");
			JobController.createJob("myjob2");
			JobController.createJob("myjob3");
			JobController.createJob("myjob4");
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			ecoPlayer.joinJob(JobController.getJobList().get(1), false);
			ecoPlayer.joinJob(JobController.getJobList().get(2), false);
			ecoPlayer.joinJob(JobController.getJobList().get(3), false);
			fail();
		} catch (EconomyPlayerException | JobSystemException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou have already reached the maximum!", e.getMessage());
		}
	}

	@Test
	public void hasJobTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			JobController.createJob("myjob2");
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			assertTrue(ecoPlayer.hasJob(JobController.getJobList().get(0)));
			assertFalse(ecoPlayer.hasJob(JobController.getJobList().get(1)));
		} catch (EconomyPlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
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
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.setScoreBoardDisabled(false);
			ecoPlayer.increasePlayerAmount(10, true);
			assertEquals("§6You got §a10.0§6 §a$§6", player.nextMessage());
			assertEquals("10.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			server.getScheduler().performOneTick();
			assertEquals("10.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			assertEquals(1, player.getScoreboard().getObjectives().size());
			assertEquals("10", String
					.valueOf(player.getScoreboard().getObjective("bank").getScore(ChatColor.GOLD + "$").getScore()));
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void increasePlayerAmountTestWithInvalidAmount() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(-10, false);
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4-10.0§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void decreasePlayerAmountTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.setScoreBoardDisabled(false);
			ecoPlayer.increasePlayerAmount(10, false);
			ecoPlayer.decreasePlayerAmount(5, false);
			server.getScheduler().performOneTick();
			assertEquals("5.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			assertEquals(1, player.getScoreboard().getObjectives().size());
			assertEquals("5", String
					.valueOf(player.getScoreboard().getObjective("bank").getScore(ChatColor.GOLD + "$").getScore()));
			ecoPlayer.decreasePlayerAmount(5, false);
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
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
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			assertEquals(player, ecoPlayer.getPlayer());
		} catch (EconomyPlayerException e) {
			fail();
		}
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
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.addJoinedTown("mytown");
			assertEquals(1, ecoPlayer.getJoinedTownList().size());
			assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(1, config.getStringList("catch441.joinedTowns").size());
			assertEquals("mytown", config.getStringList("catch441.joinedTowns").get(0));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void addJoinedTownTestWithAlreadyJoined() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.addJoinedTown("mytown");
			ecoPlayer.addJoinedTown("mytown");
			fail();
		} catch (EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou already joined this town!", e.getMessage());
		}
	}

	@Test
	public void addJoinedTownTestWithMaxTownsReached() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.addJoinedTown("mytown");
			ecoPlayer.addJoinedTown("mytown1");
			fail();
		} catch (EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou have already reached the maximum!", e.getMessage());
		}
	}

	@Test
	public void getJoinedTownListTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.addJoinedTown("mytown");
			assertEquals(1, ecoPlayer.getJoinedTownList().size());
			assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void reachedMaxJoinedTownsTest() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			assertFalse(ecoPlayer.reachedMaxJoinedTowns());
			ecoPlayer.addJoinedTown("mytown");
			assertTrue(ecoPlayer.reachedMaxJoinedTowns());
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void removeJoinedTown() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.addJoinedTown("mytown");
			ecoPlayer.removeJoinedTown("mytown");
			assertEquals(0, ecoPlayer.getJoinedTownList().size());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(0, config.getStringList("catch441.joinedTowns").size());
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void removeJoinedTownWithTownNotJoined() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.removeJoinedTown("mytown");
			fail();
		} catch (EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou didnt join this town yet!", e.getMessage());
		}
	}

	@Test
	public void setScoreBoardDisabled() {
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			assertTrue(ecoPlayer.isScoreBoardDisabled());
			ecoPlayer.setScoreBoardDisabled(false);
			assertFalse(ecoPlayer.isScoreBoardDisabled());
			server.getScheduler().performOneTick();
			assertEquals(1, player.getScoreboard().getObjectives().size());
			assertNotNull("0", String.valueOf(player.getScoreboard().getObjective("bank")));
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertFalse(config.getBoolean("catch441.bank"));
			ecoPlayer.setScoreBoardDisabled(true);
			server.getScheduler().performOneTick();
			assertTrue(ecoPlayer.isScoreBoardDisabled());
			assertEquals(0, player.getScoreboard().getObjectives().size());
			// check savefile
			File saveFile1 = new File(UltimateEconomy.getInstance.getDataFolder(), "PlayerFile.yml");
			YamlConfiguration config1 = YamlConfiguration.loadConfiguration(saveFile1);
			assertTrue(config1.getBoolean("catch441.scoreboardDisabled"));
		} catch (EconomyPlayerException e) {
			fail();
		}
	}
}
