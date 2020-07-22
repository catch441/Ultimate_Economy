package com.ue.economyplayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
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
import com.ue.economyplayer.impl.EconomyPlayerImpl;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class EconomyPlayerTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;

	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		EconomyPlayerController.getAllEconomyPlayers().clear();
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
		int size3 = BankController.getBankAccounts().size();
		for (int i = 0; i < size3; i++) {
			BankController.deleteBankAccount(BankController.getBankAccounts().get(0));
		}
	}
	
	@Test
	public void constructorLoadTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayerController.createEconomyPlayer("kthschnll");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.addHome("myhome", new Location(world, 1, 2, 3), false);
			ecoPlayer.joinJob(job, false);
			EconomyPlayerController.getAllEconomyPlayers().clear();
			EconomyPlayer result = new EconomyPlayerImpl("kthschnll", false);
			assertEquals("kthschnll", result.getName());
			assertTrue(result.isScoreBoardDisabled());
			assertEquals(ecoPlayer.getBankAccount(), result.getBankAccount());
			assertTrue(result.getHomeList().containsKey("myhome"));
			assertEquals("1.0",String.valueOf(result.getHomeList().get("myhome").getX()));
			assertEquals("2.0",String.valueOf(result.getHomeList().get("myhome").getY()));
			assertEquals("3.0",String.valueOf(result.getHomeList().get("myhome").getZ()));
			assertEquals(1, result.getHomeList().size());
			assertEquals(1, result.getJobList().size());
			assertEquals(job, result.getJobList().get(0));
		} catch (PlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}
	
	@Test
	public void constructorLoadTestWithInvalidBankAndJob() {
		try {
			EconomyPlayerController.createEconomyPlayer("kthschnll");
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			List<String> list = new ArrayList<>();
			list.add("myjob");
			config.set("kthschnll.Jobs", list);
			config.set("kthschnll.Iban", "myiban123");
			config.save(saveFile);
			EconomyPlayerController.getAllEconomyPlayers().clear();
			BankController.deleteBankAccount(BankController.getBankAccounts().get(0));
			EconomyPlayer result = new EconomyPlayerImpl("kthschnll", false);
			assertEquals(0, result.getJobList().size());
			assertNull(result.getBankAccount());
		} catch (PlayerException | IOException e) {
			fail();
		}
	}

	@Test
	public void isOnlineTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("Wulfgar");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			assertFalse(ecoPlayer.isOnline());
			ecoPlayer.setPlayer(player);
			assertTrue(ecoPlayer.isOnline());
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void isScoreBoardDisabledTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			assertTrue(ecoPlayer.isScoreBoardDisabled());
			ecoPlayer.setScoreBoardDisabled(false);
			assertFalse(ecoPlayer.isScoreBoardDisabled());
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void getNameTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			assertEquals("catch441", EconomyPlayerController.getAllEconomyPlayers().get(0).getName());
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void addHomeTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, true);
			assertEquals("§6You created the home §amyhome§6.", player.nextMessage());
			assertTrue(ecoPlayer.getHomeList().containsKey("myhome"));
			assertTrue(ecoPlayer.getHomeList().containsValue(loc));
			assertEquals(1, ecoPlayer.getHomeList().size());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(1, config.getStringList("catch441.Home.Homelist").size());
			assertEquals("myhome", config.getStringList("catch441.Home.Homelist").get(0));
			assertEquals("myhome", config.getString("catch441.Home.myhome.Name"));
			assertEquals("World", config.getString("catch441.Home.myhome.World"));
			assertEquals("1.0", config.getString("catch441.Home.myhome.X"));
			assertEquals("2.0", config.getString("catch441.Home.myhome.Y"));
			assertEquals("3.0", config.getString("catch441.Home.myhome.Z"));
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void addHomeTestWithAlredyExists() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			ecoPlayer.addHome("myhome", loc, false);
			fail();
		} catch (PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cThis home already exists!", e.getMessage());
		}
	}

	@Test
	public void addHomeTestWithReachedMaxHomes() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			ecoPlayer.addHome("myhome1", loc, false);
			ecoPlayer.addHome("myhome2", loc, false);
			ecoPlayer.addHome("myhome3", loc, false);
			fail();
		} catch (PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou have already reached the maximum!", e.getMessage());
		}
	}

	@Test
	public void getHomeTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			assertEquals(loc, ecoPlayer.getHome("myhome"));
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void getHomeTestWithNoHomes() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayerController.getAllEconomyPlayers().get(0).getHome("myhome");
			fail();
		} catch (PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cThis home does not exist!", e.getMessage());
		}
	}

	@Test
	public void removeHomeTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			ecoPlayer.removeHome("myhome", true);
			assertEquals("§6Your home §amyhome§6 was deleted.", player.nextMessage());
			assertFalse(ecoPlayer.getHomeList().containsKey("myhome"));
			assertFalse(ecoPlayer.getHomeList().containsValue(loc));
			assertEquals(0, ecoPlayer.getHomeList().size());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(0, config.getStringList("catch441.Home.Homelist").size());
			assertFalse(config.isSet("catch441.Home.myhome.Name"));
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void removeHomeTestWithNoHomes() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayerController.getAllEconomyPlayers().get(0).removeHome("myhome", true);
			fail();
		} catch (PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cThis home does not exist!", e.getMessage());
		}
	}

	@Test
	public void reachedMaxHomesTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			ecoPlayer.addHome("myhome1", loc, false);
			assertFalse(ecoPlayer.reachedMaxHomes());
			ecoPlayer.addHome("myhome2", loc, false);
			assertTrue(ecoPlayer.reachedMaxHomes());
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void reachedMaxJoinedJobsTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			JobController.createJob("job1");
			JobController.createJob("job2");
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			assertFalse(ecoPlayer.reachedMaxJoinedJobs());
			ecoPlayer.joinJob(JobController.getJobList().get(1), false);
			assertTrue(ecoPlayer.reachedMaxJoinedJobs());
		} catch (PlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void getHomeListTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 3);
			ecoPlayer.addHome("myhome", loc, false);
			Map<String, Location> list = ecoPlayer.getHomeList();
			assertEquals(1, list.size());
			assertTrue(list.containsKey("myhome"));
			assertTrue(list.containsValue(loc));
		} catch (PlayerException e) {
			fail();
		}

	}

	@Test
	public void getBankAccountTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			String iban = ecoPlayer.getBankAccount().getIban();
			assertEquals(BankController.getBankAccountByIban(iban), ecoPlayer.getBankAccount());
		} catch (PlayerException | GeneralEconomyException e) {
			fail();
		}

	}

	@Test
	public void hasEnoughtMoneyTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(10, false);
			assertTrue(ecoPlayer.hasEnoughtMoney(10));
			assertFalse(ecoPlayer.hasEnoughtMoney(20));
		} catch (PlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void joinJobTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, true);
			assertEquals("§6You have joined the job §amyjob§6.", player.nextMessage());
			assertEquals(1, ecoPlayer.getJobList().size());
			assertEquals(job, ecoPlayer.getJobList().get(0));
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(1, config.getStringList("catch441.Jobs").size());
			assertEquals("myjob", config.getStringList("catch441.Jobs").get(0));
		} catch (PlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void joinJobTestWithAlreadyJoined() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, false);
			ecoPlayer.joinJob(job, false);
			fail();
		} catch (PlayerException | JobSystemException | GeneralEconomyException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou already joined this job!", e.getMessage());
		}
	}

	@Test
	public void joinJobTestWithMaxReached() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob1");
			JobController.createJob("myjob2");
			JobController.createJob("myjob3");
			JobController.createJob("myjob4");
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			ecoPlayer.joinJob(JobController.getJobList().get(1), false);
			ecoPlayer.joinJob(JobController.getJobList().get(2), false);
			ecoPlayer.joinJob(JobController.getJobList().get(3), false);
			fail();
		} catch (PlayerException | JobSystemException | GeneralEconomyException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou have already reached the maximum!", e.getMessage());
		}
	}

	@Test
	public void hasJobTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			JobController.createJob("myjob2");
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			assertTrue(ecoPlayer.hasJob(JobController.getJobList().get(0)));
			assertFalse(ecoPlayer.hasJob(JobController.getJobList().get(1)));
		} catch (PlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void leaveJobTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, false);
			ecoPlayer.leaveJob(job, true);
			assertEquals("§6You have left the job §amyjob§6.", player.nextMessage());
			assertEquals(0, ecoPlayer.getJobList().size());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(0, config.getStringList("catch441.Jobs").size());
		} catch (PlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void leaveJobTestWithJobNotJoined() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.leaveJob(job, false);
			fail();
		} catch (PlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou didnt join this job yet!", e.getMessage());
		}
	}

	@Test
	public void getJobListTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			JobController.createJob("myjob");
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			List<Job> list = ecoPlayer.getJobList();
			assertEquals(1, list.size());
			assertEquals(JobController.getJobList().get(0), list.get(0));
		} catch (PlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}

	}

	@Test
	public void increasePlayerAmountTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.setScoreBoardDisabled(false);
			ecoPlayer.increasePlayerAmount(10, true);
			assertEquals("§6You got §a10.0§6 §a$§6", player.nextMessage());
			assertEquals("10.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			server.getScheduler().performOneTick();
			assertEquals("10.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			assertEquals(1, player.getScoreboard().getObjectives().size());
			assertEquals("10", String
					.valueOf(player.getScoreboard().getObjective("bank").getScore(ChatColor.GOLD + "$").getScore()));
		} catch (PlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void increasePlayerAmountTestWithInvalidAmount() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.increasePlayerAmount(-10, false);
			fail();
		} catch (PlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4-10.0§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void decreasePlayerAmountTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.setScoreBoardDisabled(false);
			ecoPlayer.increasePlayerAmount(10, false);
			ecoPlayer.decreasePlayerAmount(5, false);
			server.getScheduler().performOneTick();
			assertEquals("5.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			assertEquals(1, player.getScoreboard().getObjectives().size());
			assertEquals("5", String
					.valueOf(player.getScoreboard().getObjective("bank").getScore(ChatColor.GOLD + "$").getScore()));
			ecoPlayer.decreasePlayerAmount(5, false);
		} catch (PlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void decreasePlayerAmountTestWithNotEnoughMoneyPersonal() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.decreasePlayerAmount(10, true);
			fail();
		} catch (PlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou have not enough money!", e.getMessage());
		}
	}

	@Test
	public void decreasePlayerAmountTestWithNotEnoughMoney() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.decreasePlayerAmount(10, false);
			fail();
		} catch (PlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cThe player has not enough money!", e.getMessage());
		}
	}

	@Test
	public void getPlayerTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			assertEquals(player, ecoPlayer.getPlayer());
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void addWildernessPermissionTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.denyWildernessPermission();
			assertFalse(ecoPlayer.getPlayer().hasPermission("ultimate_economy.wilderness"));
			ecoPlayer.addWildernessPermission();
			assertTrue(ecoPlayer.getPlayer().hasPermission("ultimate_economy.wilderness"));
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void denyWildernessPermissionTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.addWildernessPermission();
			assertTrue(ecoPlayer.getPlayer().hasPermission("ultimate_economy.wilderness"));
			ecoPlayer.denyWildernessPermission();
			assertFalse(ecoPlayer.getPlayer().hasPermission("ultimate_economy.wilderness"));
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void payToOtherPlayerTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			PlayerMock recieverPlayer = server.addPlayer("kthschnll");
			EconomyPlayer reciever = EconomyPlayerController.getAllEconomyPlayers().get(1);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
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
		} catch (PlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void addJoinedTownTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.addJoinedTown("mytown");
			assertEquals(1, ecoPlayer.getJoinedTownList().size());
			assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(1, config.getStringList("catch441.joinedTowns").size());
			assertEquals("mytown", config.getStringList("catch441.joinedTowns").get(0));
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void addJoinedTownTestWithAlreadyJoined() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.addJoinedTown("mytown");
			ecoPlayer.addJoinedTown("mytown");
			fail();
		} catch (PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou already joined this town!", e.getMessage());
		}
	}

	@Test
	public void addJoinedTownTestWithMaxTownsReached() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.addJoinedTown("mytown");
			ecoPlayer.addJoinedTown("mytown1");
			fail();
		} catch (PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou have already reached the maximum!", e.getMessage());
		}
	}

	@Test
	public void getJoinedTownListTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.addJoinedTown("mytown");
			assertEquals(1, ecoPlayer.getJoinedTownList().size());
			assertEquals("mytown", ecoPlayer.getJoinedTownList().get(0));
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void reachedMaxJoinedTownsTest() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			assertFalse(ecoPlayer.reachedMaxJoinedTowns());
			ecoPlayer.addJoinedTown("mytown");
			assertTrue(ecoPlayer.reachedMaxJoinedTowns());
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void removeJoinedTown() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.addJoinedTown("mytown");
			ecoPlayer.removeJoinedTown("mytown");
			assertEquals(0, ecoPlayer.getJoinedTownList().size());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(0, config.getStringList("catch441.joinedTowns").size());
		} catch (PlayerException e) {
			fail();
		}
	}

	@Test
	public void removeJoinedTownWithTownNotJoined() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.removeJoinedTown("mytown");
			fail();
		} catch (PlayerException e) {
			assertTrue(e instanceof PlayerException);
			assertEquals("§cYou didnt join this town yet!", e.getMessage());
		}
	}

	@Test
	public void setScoreBoardDisabled() {
		try {
			EconomyPlayerController.createEconomyPlayer("catch441");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			assertTrue(ecoPlayer.isScoreBoardDisabled());
			ecoPlayer.setScoreBoardDisabled(false);
			assertFalse(ecoPlayer.isScoreBoardDisabled());
			server.getScheduler().performOneTick();
			assertEquals(1, player.getScoreboard().getObjectives().size());
			assertNotNull("0", String.valueOf(player.getScoreboard().getObjective("bank")));
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertFalse(config.getBoolean("catch441.bank"));
			ecoPlayer.setScoreBoardDisabled(true);
			server.getScheduler().performOneTick();
			assertTrue(ecoPlayer.isScoreBoardDisabled());
			assertEquals(0, player.getScoreboard().getObjectives().size());
			// check savefile
			File saveFile1 = new File(UltimateEconomy.getInstance.getDataFolder(),"PlayerFile.yml");
			YamlConfiguration config1 = YamlConfiguration.loadConfiguration(saveFile1);
			assertTrue(config1.getBoolean("catch441.scoreboardDisabled"));
		} catch (PlayerException e) {
			fail();
		}
	}
}
