package com.ue.jobsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

public class JobTest {

	@SuppressWarnings("unused")
	private static ServerMock server;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		UltimateEconomy.getInstance.getDataFolder().delete();
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
	}
	
	@Test
	public void loadExistingShopTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("dirt", 1.0);
			job.addFisherLootType("fish", 2.0);
			job.addMob("cow", 3.0);
			JobController.getJobList().clear();
			JobController.createJob("myjob");
			Job result = JobController.getJobList().get(0);
			assertTrue(result.getBlockList().containsKey("DIRT"));
			assertTrue(result.getBlockList().containsValue(1.0));
			assertTrue(result.getFisherList().containsKey("fish"));
			assertTrue(result.getFisherList().containsValue(2.0));
			assertTrue(result.getEntityList().containsKey("COW"));
			assertTrue(result.getEntityList().containsValue(3.0));
			assertEquals("myjob",result.getName());
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void getBlockListTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("stone", 1);
			Map<String, Double> list = job.getBlockList();
			assertEquals(1, list.size());
			assertTrue(list.containsKey("STONE"));
			assertTrue(list.containsValue(1.0));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void getEntityListTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addMob("cow", 1.0);
			Map<String, Double> list = job.getEntityList();
			assertEquals(1, list.size());
			assertTrue(list.containsKey("COW"));
			assertTrue(list.containsValue(1.0));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void getFisherListTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("treasure", 1.0);
			Map<String, Double> list = job.getFisherList();
			assertEquals(1, list.size());
			assertTrue(list.containsKey("treasure"));
			assertTrue(list.containsValue(1.0));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void getKillPriceTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addMob("cow", 1.0);
			assertEquals("1.0", String.valueOf(job.getKillPrice("cow")));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void getFisherPriceTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("fish", 1.0);
			assertEquals("1.0", String.valueOf(job.getFisherPrice("fish")));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void getBlockPriceTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("stone", 1.0);
			assertEquals("1.0", String.valueOf(job.getBlockPrice("stone")));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void getNameTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			assertEquals("myjob", job.getName());
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void addBlockTestWithInvalidMaterial() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("dsadas", 1.0);
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4DSADAS§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void addBlockTestWithInvalidPrice() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("stone", -1.0);
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4-1.0§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void addBlockTestWithAlreadyInJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("stone", 1.0);
			job.addBlock("stone", 1.0);
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof JobSystemException);
			assertEquals("§cThis item already exists in this job!", e.getMessage());
		}
	}

	@Test
	public void addBlockTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("stone", 1.0);
			assertEquals(1, job.getBlockList().size());
			assertTrue(job.getBlockList().containsKey("STONE"));
			assertTrue(job.getBlockList().containsValue(1.0));
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals("1.0", config.getString("BlockList.STONE"));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void addMobTestWithInvalidEntity() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addMob("dsadas", 1.0);
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4DSADAS§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void addMobTestWithInvalidPrice() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addMob("cow", -1.0);
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4-1.0§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void addMobTestWithAlreadyInJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addMob("cow", 1.0);
			job.addMob("cow", 1.0);
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof JobSystemException);
			assertEquals("§cThis entity already exists in this job!", e.getMessage());
		}
	}

	@Test
	public void addMobTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addMob("cow", 1.0);
			assertEquals(1, job.getEntityList().size());
			assertTrue(job.getEntityList().containsKey("COW"));
			assertTrue(job.getEntityList().containsValue(1.0));
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals("1.0", config.getString("EntityList.COW"));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void addFisherLootTypeTestWithInvalidType() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("dsadas", 1.0);
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4dsadas§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void addFisherLootTypeTestWithInvalidPrice() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("fish", -1.0);
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4-1.0§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void addFisherLootTypeTestWithAlreadyInJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("fish", 1.0);
			job.addFisherLootType("fish", 1.0);
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof JobSystemException);
			assertEquals("§cThis loottype for a fisherjob already exists in this job!", e.getMessage());
		}
	}

	@Test
	public void addFisherLootTypeTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("fish", 1.0);
			assertEquals(1, job.getFisherList().size());
			assertTrue(job.getFisherList().containsKey("fish"));
			assertTrue(job.getFisherList().containsValue(1.0));
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals("1.0", config.getString("FisherList.fish"));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void deleteBlockTestWithInvalidBlock() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.deleteBlock("ddada");
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4DDADA§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void deleteBlockTestWithNotInJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.deleteBlock("stone");
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof JobSystemException);
			assertEquals("§cThis item does not exist in this job!", e.getMessage());
		}
	}

	@Test
	public void deleteBlockTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("stone", 1.0);
			job.deleteBlock("stone");
			assertEquals(0, job.getBlockList().size());
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertFalse(config.contains("BlockList.STONE"));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void deleteMobTestWithInvalidMob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.deleteMob("DDADA");
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4DDADA§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void deleteMobTestWithNotInJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.deleteMob("cow");
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof JobSystemException);
			assertEquals("§cThis entity does not exist in this job!", e.getMessage());
		}
	}

	@Test
	public void deleteMobTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addMob("cow", 1.0);
			job.deleteMob("cow");
			assertEquals(0, job.getEntityList().size());
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertFalse(config.contains("EntityList.COW"));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void delFisherLootTypeTestWithInvalidType() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.delFisherLootType("DDADA");
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4DDADA§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void delFisherLootTypeTestWithNotInJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.delFisherLootType("fish");
			fail();
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(e instanceof JobSystemException);
			assertEquals("§cThis loottype for a fisherjob does not exist in this job!", e.getMessage());
		}
	}

	@Test
	public void delFisherLootTypeTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("fish", 1.0);
			job.delFisherLootType("fish");
			assertEquals(0, job.getFisherList().size());
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertFalse(config.contains("FisherList.fish"));
		} catch (GeneralEconomyException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void deleteJobTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.deleteJob();
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			if (file.exists()) {
				fail();
			}
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
}
