package com.ue.jobsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

public class JobControllerTest {

	private static ServerMock server;
	private static WorldMock world;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		server.addPlayer("catch441");
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		int size2 = EconomyPlayerController.getAllEconomyPlayers().size();
		for (int i = 0; i < size2; i++) {
			EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
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
		int size = JobController.getJobList().size();
		for (int i = 0; i < size; i++) {
			JobController.deleteJob(JobController.getJobList().get(0));
		}
		int size2 = JobcenterController.getJobcenterList().size();
		for (int i = 0; i < size2; i++) {
			try {
				JobcenterController.deleteJobcenter(JobcenterController.getJobcenterList().get(0));
			} catch (JobSystemException e) {
				assertTrue(false);
			}
		}
	}

	@Test
	public void loadAllJobsTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("STONE", 1);
			job.addFisherLootType("fish", 2);
			job.addMob("COW", 3);
			JobController.getJobList().clear();
			assertEquals(0, JobController.getJobList().size());
			JobController.loadAllJobs();
			assertEquals(1, JobController.getJobList().size());
			Job result = JobController.getJobList().get(0);
			assertEquals("myjob", result.getName());
			assertEquals(1, result.getBlockList().size());
			assertTrue(result.getBlockList().containsKey("STONE"));
			assertTrue(result.getBlockList().containsValue(1.0));
			assertEquals(1, result.getEntityList().size());
			assertTrue(result.getEntityList().containsKey("COW"));
			assertTrue(result.getEntityList().containsValue(3.0));
			assertEquals(1, result.getFisherList().size());
			assertTrue(result.getFisherList().containsKey("fish"));
			assertTrue(result.getFisherList().containsValue(2.0));
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void loadAllJobsTestWithLoadingError() {
		try {
			JobController.createJob("myjob");
			JobController.getJobList().get(0);
			JobController.getJobList().clear();
			assertEquals(0, JobController.getJobList().size());
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			file.delete();
			JobController.loadAllJobs();
			assertEquals(0, JobController.getJobList().size());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void createJobTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			assertEquals(1, JobController.getJobList().size());
			List<String> list = UltimateEconomy.getInstance.getConfig().getStringList("JobList");
			assertEquals(1, list.size());
			assertEquals("myjob", list.get(0));
			// check job
			assertEquals("myjob", job.getName());
			assertEquals(0, job.getBlockList().size());
			assertEquals(0, job.getEntityList().size());
			assertEquals(0, job.getFisherList().size());
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals("myjob", config.getString("Jobname"));
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void createJobTestWithSameName() {
		try {
			JobController.createJob("myjob");
			JobController.createJob("myjob");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§c§4myjob§c already exists!", e.getMessage());
		}
	}

	@Test
	public void removeJobFromAllPlayers() {
		try {
			JobController.createJob("myjob");
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, false);
			JobController.removeJobFromAllPlayers(job);
			assertEquals(0, ecoPlayer.getJobList().size());
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void deleteJobTest() {
		try {
			JobcenterController.createJobcenter("jobcenter", new Location(world, 1, 1, 1), 9);
			JobcenterController.createJobcenter("other", new Location(world, 10, 1, 1), 9);
			JobController.createJob("myjob");
			Jobcenter jobcenter = JobcenterController.getJobcenterList().get(0);
			Job job = JobController.getJobList().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			jobcenter.addJob(job, "STONE", 0);
			ecoPlayer.joinJob(job, false);
			JobController.deleteJob(job);
			assertEquals(0, JobController.getJobList().size());
			assertEquals(0, jobcenter.getJobList().size());
			assertEquals(0, ecoPlayer.getJobList().size());
			// check savefile
			List<String> list = UltimateEconomy.getInstance.getConfig().getStringList("JobList");
			assertEquals(0, list.size());
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			if (file.exists()) {
				assertTrue(false);
			}
		} catch (JobSystemException | GeneralEconomyException | PlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getJobListTest() {
		try {
			JobController.createJob("myjob");
			List<Job> list = JobController.getJobList();
			assertEquals(1, list.size());
			assertEquals("myjob", list.get(0).getName());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getJobNameList() {
		try {
			JobController.createJob("myjob");
			JobController.createJob("myjob2");
			List<String> list = JobController.getJobNameList();
			assertEquals(2, list.size());
			assertEquals("myjob", list.get(0));
			assertEquals("myjob2", list.get(1));
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getJobByNameTest() {
		try {
			JobController.createJob("other");
			JobController.createJob("myjob");
			Job job = JobController.getJobByName("myjob");
			assertEquals(JobController.getJobList().get(1), job);
			assertEquals("myjob", job.getName());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getJobByNameTestWithNoJob() {
		try {
			JobController.getJobByName("myjob");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§c§4myjob§c does not exist!", e.getMessage());
		}
	}
}
