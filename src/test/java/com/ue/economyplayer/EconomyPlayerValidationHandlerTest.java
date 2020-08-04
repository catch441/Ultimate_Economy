package com.ue.economyplayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.economyplayer.logic.impl.EconomyPlayerValidationHandlerImpl;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobController;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

public class EconomyPlayerValidationHandlerTest {

	private static EconomyPlayerValidationHandlerImpl validationHandler;
	private static ServerMock server;
	private static WorldMock world;

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
		server.addPlayer("catch441");
		validationHandler = new EconomyPlayerValidationHandlerImpl(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		int size2 = EconomyPlayerManagerImpl.getAllEconomyPlayers().size();
		for (int i = 0; i < size2; i++) {
			EconomyPlayerManagerImpl.deleteEconomyPlayer(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
		}
		UltimateEconomy.getInstance.getDataFolder().delete();
		server.setPlayers(0);
		MockBukkit.unload();
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {

	}

	@Test
	public void checkForEnoughMoneyTestPersonal() {
		try {
			validationHandler.checkForEnoughMoney(10, true);
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou have not enough money!", e.getMessage());
		}
	}

	@Test
	public void checkForEnoughMoneyTestNonPersonal() {
		try {
			validationHandler.checkForEnoughMoney(10, false);
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cThe player has not enough money!", e.getMessage());
		}
	}

	@Test
	public void checkForEnoughMoneyTestSuccess() {
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(1.0, false);
			validationHandler.checkForEnoughMoney(1.0, false);
			validationHandler.checkForEnoughMoney(0.5, false);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).decreasePlayerAmount(1.0, false);
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void checkForExistingHomeTestFail() {
		try {
			validationHandler.checkForExistingHome("myhome1");
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals("§cThis home does not exist!", e.getMessage());
		}
	}

	@Test
	public void checkForExistingHomeTestSuccess() {
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).addHome("myhome2", new Location(world, 1, 2, 3),
					false);
			validationHandler.checkForExistingHome("myhome2");
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).removeHome("myhome2", false);
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void checkForNotReachedMaxHomesTestSuccess() {
		try {
			validationHandler.checkForNotReachedMaxHomes();
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void checkForNotReachedMaxHomesTestFail() {
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).addHome("myhome0", new Location(world, 1, 2, 3),
					false);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).addHome("myhome2", new Location(world, 1, 2, 3),
					false);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).addHome("myhome3", new Location(world, 1, 2, 3),
					false);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).addHome("myhome4", new Location(world, 1, 2, 3),
					false);
			validationHandler.checkForNotReachedMaxHomes();
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals("§cYou have already reached the maximum!", e.getMessage());
			try {
				EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).removeHome("myhome0", false);
				EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).removeHome("myhome2", false);
				EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).removeHome("myhome3", false);
			} catch (EconomyPlayerException e1) {
				fail();
			}
		}
	}

	@Test
	public void checkForNotExistingHomeTestSuccess() {
		try {
			validationHandler.checkForNotExistingHome("myhome1");
		} catch (EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void checkForNotExistingHomeTestFail() {

		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).addHome("myhome1", new Location(world, 1, 2, 3),
					false);
			validationHandler.checkForNotExistingHome("myhome1");
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals("§cThis home already exists!", e.getMessage());
			try {
				EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).removeHome("myhome1", false);
			} catch (EconomyPlayerException e1) {
				fail();
			}
		}
	}

	@Test
	public void checkForJobNotJoinedTestSuccess() {
		try {
			JobController.createJob("myjob1");
			Job job = JobController.getJobList().get(0);
			validationHandler.checkForJobNotJoined(job);
			JobController.deleteJob(job);
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void checkForJobNotJoinedTestFail() {
		Job job = null;
		try {
			JobController.createJob("myjob1");
			job = JobController.getJobList().get(0);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).joinJob(job, false);
			validationHandler.checkForJobNotJoined(job);
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou already joined this job!", e.getMessage());
			JobController.deleteJob(job);
		}
	}

	@Test
	public void checkForJobJoinedTestSuccess() {
		try {
			JobController.createJob("myjob1");
			Job job = JobController.getJobList().get(0);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).joinJob(job, false);
			validationHandler.checkForJobJoined(job);
			JobController.deleteJob(job);
		} catch (EconomyPlayerException | GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void checkForJobJoinedTestFail() {
		Job job = null;
		try {
			JobController.createJob("myjob1");
			job = JobController.getJobList().get(0);
			validationHandler.checkForJobJoined(job);
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou didnt join this job yet!", e.getMessage());
			JobController.deleteJob(job);
		}
	}
	
	@Test
	public void checkForNotReachedMaxJoinedJobsTestSuccess() {
		try {
			validationHandler.checkForNotReachedMaxJoinedJobs();
		} catch (EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void checkForNotReachedMaxJoinedJobsTestFail() {
		try {
			JobController.createJob("myjob1");
			JobController.createJob("myjob2");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(JobController.getJobList().get(0), false);
			ecoPlayer.joinJob(JobController.getJobList().get(1), false);
			validationHandler.checkForNotReachedMaxJoinedJobs();
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou have already reached the maximum!", e.getMessage());
			JobController.deleteJob(JobController.getJobList().get(0));
			JobController.deleteJob(JobController.getJobList().get(0));
		}
	}
	
	@Test
	public void checkForTownNotJoinedTestSuccess() {
		try {
			validationHandler.checkForTownNotJoined("mytown");
		} catch (EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void checkForTownNotJoinedTestFail() {
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).addJoinedTown("mytown");
			validationHandler.checkForTownNotJoined("mytown");
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals("§cYou already joined this town!", e.getMessage());
			try {
				EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).removeJoinedTown("mytown");
			} catch (EconomyPlayerException e1) {
				fail();
			};
		}
	}
	
	@Test
	public void checkForJoinedTownTestSuccess() {
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).addJoinedTown("mytown");
			validationHandler.checkForJoinedTown("mytown");
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).removeJoinedTown("mytown");
		} catch (EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void checkForJoinedTownTestFail() {
		try {
			validationHandler.checkForJoinedTown("mytown");
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals("§cYou didnt join this town yet!", e.getMessage());
		}
	}
	
	@Test
	public void checkForNotReachedMaxJoinedTownsTestSuccess() {
		try {
			validationHandler.checkForNotReachedMaxJoinedTowns();
		} catch (EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void checkForNotReachedMaxJoinedTownsTestFail() {
		try {
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).addJoinedTown("mytown");
			validationHandler.checkForNotReachedMaxJoinedTowns();
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals("§cYou have already reached the maximum!", e.getMessage());
			try {
				EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).removeJoinedTown("mytown");
			} catch (EconomyPlayerException e1) {
				fail();
			};
		}
	}
}
