package com.ue.jobsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobController;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.jobsystem.logic.impl.JobsystemValidationHandlerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

public class JobSystemValidationHandlerTest {
	
	private static JobsystemValidationHandlerImpl validationHandler;
	private static ServerMock server;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		server.addPlayer("catch441");
		validationHandler = new JobsystemValidationHandlerImpl();
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
		int size = JobController.getJobList().size();
		for (int i = 0; i < size; i++) {
			JobController.deleteJob(JobController.getJobList().get(0));
		}
	}
	
	@Test
	public void checkForValidMaterialTest() {
		try {
			validationHandler.checkForValidMaterial("invalid");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4invalid§c is invalid!", e.getMessage());
		}
	}
	
	@Test
	public void checkForValidMaterialTestValid() {
		try {
			validationHandler.checkForValidMaterial("dirt");
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForValidEntityTypeTest() {
		try {
			validationHandler.checkForValidEntityType("invalid");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4invalid§c is invalid!", e.getMessage());
		}
	}
	
	@Test
	public void checkForValidEntityTypeTestValid() {
		try {
			validationHandler.checkForValidEntityType("COW");
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForPositivValueTest() {
		try {
			validationHandler.checkForPositivValue(-10);
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-10.0§c is invalid!", e.getMessage());
		}
	}
	
	@Test
	public void checkForPositivValueTestValid() {
		try {
			validationHandler.checkForPositivValue(10);
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForValidFisherLootTypeTest() {
		try {
			validationHandler.checkForValidFisherLootType("invalid");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4invalid§c is invalid!", e.getMessage());
		}
	}
	
	@Test
	public void checkForValidFisherLootTypeTestValid() {
		try {
			validationHandler.checkForValidFisherLootType("junk");
			validationHandler.checkForValidFisherLootType("treasure");
			validationHandler.checkForValidFisherLootType("fish");
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForBlockNotInJobTest() {
		try {
			Map<String, Double> blockList = new HashMap<>();
			blockList.put("dirt", 10.0);
			validationHandler.checkForBlockNotInJob(blockList,"dirt");
			assertTrue(false);
		} catch (JobSystemException e) {
			assertEquals("§cThis item already exists in this job!", e.getMessage());
		}
	}
	
	@Test
	public void checkForBlockNotInJobTestValid() {
		try {
			Map<String, Double> blockList = new HashMap<>();
			validationHandler.checkForBlockNotInJob(blockList,"dirt");
		} catch (JobSystemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForBlockInJobTest() {
		try {
			Map<String, Double> blockList = new HashMap<>();	
			validationHandler.checkForBlockInJob(blockList,"dirt");
			assertTrue(false);
		} catch (JobSystemException e) {
			assertEquals("§cThis item does not exist in this job!", e.getMessage());
		}
	}
	
	@Test
	public void checkForBlockInJobTestValid() {
		try {
			Map<String, Double> blockList = new HashMap<>();
			blockList.put("dirt", 10.0);
			validationHandler.checkForBlockInJob(blockList,"dirt");
		} catch (JobSystemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForLoottypeNotInJobTest() {
		try {
			Map<String, Double> fisherList = new HashMap<>();
			fisherList.put("treasure", 10.0);
			validationHandler.checkForLoottypeNotInJob(fisherList,"treasure");
			assertTrue(false);
		} catch (JobSystemException e) {
			assertEquals("§cThis loottype for a fisherjob already exists in this job!", e.getMessage());
		}
	}
	
	@Test
	public void checkForLoottypeNotInJobTestValid() {
		try {
			Map<String, Double> fisherList = new HashMap<>();
			validationHandler.checkForLoottypeNotInJob(fisherList,"treasure");
		} catch (JobSystemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForLoottypeInJobTest() {
		try {
			Map<String, Double> fisherList = new HashMap<>();
			validationHandler.checkForLoottypeInJob(fisherList,"treasure");
			assertTrue(false);
		} catch (JobSystemException e) {
			assertEquals("§cThis loottype for a fisherjob does not exist in this job!", e.getMessage());
		}
	}
	
	@Test
	public void checkForLoottypeInJobTestValid() {
		try {
			Map<String, Double> fisherList = new HashMap<>();
			fisherList.put("treasure", 10.0);
			validationHandler.checkForLoottypeInJob(fisherList,"treasure");
		} catch (JobSystemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForEntityNotInJobTest() {
		try {
			Map<String, Double> entityList = new HashMap<>();
			entityList.put("COW", 10.0);
			validationHandler.checkForEntityNotInJob(entityList,"COW");
			assertTrue(false);
		} catch (JobSystemException e) {
			assertEquals("§cThis entity already exists in this job!", e.getMessage());
		}
	}
	
	@Test
	public void checkForEntityNotInJobTestValid() {
		try {
			Map<String, Double> entityList = new HashMap<>();
			validationHandler.checkForEntityNotInJob(entityList,"COW");
		} catch (JobSystemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForEntityInJobTest() {
		try {
			Map<String, Double> entityList = new HashMap<>();
			validationHandler.checkForEntityInJob(entityList,"COW");
			assertTrue(false);
		} catch (JobSystemException e) {
			assertEquals("§cThis entity does not exist in this job!", e.getMessage());
		}
	}
	
	@Test
	public void checkForEntityInJobTestValid() {
		try {
			Map<String, Double> entityList = new HashMap<>();
			entityList.put("COW", 10.0);
			validationHandler.checkForEntityInJob(entityList,"COW");
		} catch (JobSystemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForValidSlotTest1() {
		try {
			validationHandler.checkForValidSlot(9,9);
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §49§c is invalid!", e.getMessage());
		}
	}
	
	@Test
	public void checkForValidSlotTest2() {
		try {
			validationHandler.checkForValidSlot(-9,9);
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-9§c is invalid!", e.getMessage());
		}
	}
	
	@Test
	public void checkForValidSlotTestValid() {
		try {
			validationHandler.checkForValidSlot(5,9);
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForJobDoesNotExistInJobcenterTest() {
		try {
			List<Job> jobs = new ArrayList<>();
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			jobs.add(job);
			validationHandler.checkForJobDoesNotExistInJobcenter(jobs,job);
			assertTrue(false);
		} catch (GeneralEconomyException | JobSystemException e) {
			assertEquals("§cThis job already exists in this jobcenter!", e.getMessage());
		}
	}
	
	@Test
	public void checkForJobDoesNotExistInJobcenterTestValid() {
		try {
			List<Job> jobs = new ArrayList<>();
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			validationHandler.checkForJobDoesNotExistInJobcenter(jobs,job);
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForJobExistInJobcenterTest() {
		try {
			List<Job> jobs = new ArrayList<>();
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			validationHandler.checkForJobExistsInJobcenter(jobs,job);
			assertTrue(false);
		} catch (GeneralEconomyException | JobSystemException e) {
			assertEquals("§cThis job does not exist in this jobcenter!", e.getMessage());
		}
	}
	
	@Test
	public void checkForJobExistsInJobcenterTestValid() {
		try {
			List<Job> jobs = new ArrayList<>();
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			jobs.add(job);
			validationHandler.checkForJobExistsInJobcenter(jobs,job);
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForSlotIsEmptyTest() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			inv.setItem(0, new ItemStack(Material.STONE));
			validationHandler.checkForFreeSlot(inv,0);
			assertTrue(false);
		} catch (EconomyPlayerException e) {
			assertEquals("§cThis slot is occupied!", e.getMessage());
		}
	}
	
	@Test
	public void checkForSlotIsEmptyTestValid() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			validationHandler.checkForFreeSlot(inv,0);
		} catch (EconomyPlayerException e) {
			assertTrue(false);
		}
	}
}
