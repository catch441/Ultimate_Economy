package com.ue.jobsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class JobcenterControllerTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		player = server.addPlayer("catch441");
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
	public void getJobcenterByNameTest() {
		try {
			JobcenterController.createJobcenter("other", new Location(world, 10, 1, 1), 9);
			JobcenterController.createJobcenter("center", new Location(world, 1, 1, 1), 9);
			Jobcenter center = JobcenterController.getJobcenterByName("center");
			assertEquals("center", center.getName());
		} catch (JobSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getJobcenterByNameTestWithNoJobcenter() {
		try {
			JobcenterController.getJobcenterByName("center");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§c§4center§c does not exist!", e.getMessage());
		}
	}

	@Test
	public void getJobcenterNameListTest() {
		try {
			JobcenterController.createJobcenter("center", new Location(world, 1, 1, 1), 9);
			List<String> list = JobcenterController.getJobcenterNameList();
			assertEquals(1, list.size());
			assertEquals("center", list.get(0));
		} catch (JobSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getJobcenterListTest() {
		try {
			JobcenterController.createJobcenter("center", new Location(world, 1, 1, 1), 9);
			List<Jobcenter> centers = JobcenterController.getJobcenterList();
			assertEquals(1, centers.size());
			assertEquals("center", centers.get(0).getName());
		} catch (JobSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void despawnAllVillagersTest() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			JobcenterController.createJobcenter("center", loc, 9);
			assertEquals(1, world.getNearbyEntities(loc, 0, 0, 0).size());
			JobcenterController.despawnAllVillagers();
			assertEquals(0, world.getNearbyEntities(loc, 0, 0, 0).size());
		} catch (JobSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void deleteJobcenterTest() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			Location loc2 = new Location(world, 2, 1, 1);
			JobcenterController.createJobcenter("center", loc, 9);
			JobcenterController.createJobcenter("center1", loc2, 9);
			JobController.createJob("myjob");
			JobController.createJob("myjob1");
			Job job = JobController.getJobList().get(0);
			Job job1 = JobController.getJobList().get(1);
			Jobcenter center = JobcenterController.getJobcenterList().get(0);
			Jobcenter center1 = JobcenterController.getJobcenterList().get(1);
			center.addJob(job, "stone", 0);
			center.addJob(job1, "stone", 1);
			center1.addJob(job1, "stone", 0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getEconomyPlayerByName("catch441");
			ecoPlayer.joinJob(job, false);
			ecoPlayer.joinJob(job1, false);
			JobcenterController.deleteJobcenter(center);
			assertEquals(0, world.getNearbyEntities(loc, 0, 0, 0).size());
			List<String> list = UltimateEconomy.getInstance.getConfig().getStringList("JobCenterNames");
			assertEquals(1, list.size());
			assertEquals("center1", list.get(0));
			// check players
			assertEquals(1, ecoPlayer.getJobList().size());
			assertEquals(job1, ecoPlayer.getJobList().get(0));
		} catch (JobSystemException | GeneralEconomyException | PlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void createJobcenterTestWithAlreadyExists() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			JobcenterController.createJobcenter("center", loc, 9);
			JobcenterController.createJobcenter("center", loc, 9);
			assertTrue(false);
		} catch (JobSystemException | GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§c§4center§c already exists!", e.getMessage());
		}
	}

	@Test
	public void createJobcenterTestWithInvalidSize() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			JobcenterController.createJobcenter("center", loc, 6);
			assertTrue(false);
		} catch (JobSystemException | GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §46§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void createJobcenterTest() {
		try {
			Location loc = new Location(world, 1, 2, 3);
			JobcenterController.createJobcenter("center", loc, 9);
			Jobcenter center = JobcenterController.getJobcenterList().get(0);
			assertEquals("center", center.getName());
			assertEquals(0, center.getJobList().size());
			// check villager
			List<Entity> list = new ArrayList<Entity>(world.getNearbyEntities(loc, 0, 0, 0));
			assertEquals("center", list.get(0).getCustomName());
			// check inventory
			center.openInv(player);
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals(9, inv.getSize());
			assertEquals("center", inv.getName());
			assertEquals(Material.AIR, inv.getItem(0).getType());
			assertEquals(Material.AIR, inv.getItem(1).getType());
			assertEquals(Material.AIR, inv.getItem(2).getType());
			assertEquals(Material.AIR, inv.getItem(3).getType());
			assertEquals(Material.AIR, inv.getItem(4).getType());
			assertEquals(Material.AIR, inv.getItem(5).getType());
			assertEquals(Material.AIR, inv.getItem(6).getType());
			assertEquals(Material.AIR, inv.getItem(7).getType());
			assertEquals(Material.ANVIL, inv.getItem(8).getType());
			assertEquals("Info", inv.getItem(8).getItemMeta().getDisplayName());
			assertEquals(2, inv.getItem(8).getItemMeta().getLore().size());
			assertEquals("§6Leftclick: §aJoin", inv.getItem(8).getItemMeta().getLore().get(0));
			assertEquals("§6Rightclick: §cLeave", inv.getItem(8).getItemMeta().getLore().get(1));
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "center-JobCenter.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals("center", config.getString("JobCenterName"));
			assertEquals(9, config.getInt("JobCenterSize"));
			assertEquals("1.0", config.getString("JobcenterLocation.x"));
			assertEquals("2.0", config.getString("JobcenterLocation.y"));
			assertEquals("3.0", config.getString("JobcenterLocation.z"));
			assertEquals("World", config.getString("JobcenterLocation.World"));
		} catch (JobSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void loadAllJobcenterTest() {
		try {
			Location loc = new Location(world, 1, 2, 3);
			JobcenterController.createJobcenter("center", loc, 9);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			Jobcenter center = JobcenterController.getJobcenterList().get(0);
			center.addJob(job, "stone", 0);
			JobcenterController.getJobcenterList().clear();
			assertEquals(0, JobcenterController.getJobcenterList().size());
			JobcenterController.loadAllJobcenters();
			assertEquals(1, JobcenterController.getJobcenterList().size());
			Jobcenter result = JobcenterController.getJobcenterList().get(0);
			assertEquals("center", result.getName());
			assertEquals(1, result.getJobList().size());
			assertEquals(job, result.getJobList().get(0));
			// check inventory
			result.openInv(player);
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals(9, inv.getSize());
			assertEquals("center", inv.getName());
			assertEquals(Material.STONE, inv.getItem(0).getType());
			assertEquals("myjob", inv.getItem(0).getItemMeta().getDisplayName());
			assertEquals(Material.AIR, inv.getItem(1).getType());
			assertEquals(Material.AIR, inv.getItem(2).getType());
			assertEquals(Material.AIR, inv.getItem(3).getType());
			assertEquals(Material.AIR, inv.getItem(4).getType());
			assertEquals(Material.AIR, inv.getItem(5).getType());
			assertEquals(Material.AIR, inv.getItem(6).getType());
			assertEquals(Material.AIR, inv.getItem(7).getType());
			assertEquals(Material.ANVIL, inv.getItem(8).getType());
			assertEquals("Info", inv.getItem(8).getItemMeta().getDisplayName());
			assertEquals(2, inv.getItem(8).getItemMeta().getLore().size());
			assertEquals("§6Leftclick: §aJoin", inv.getItem(8).getItemMeta().getLore().get(0));
			assertEquals("§6Rightclick: §cLeave", inv.getItem(8).getItemMeta().getLore().get(1));
		} catch (JobSystemException | GeneralEconomyException | PlayerException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void loadAllJobcenterTestWithLoadingError() {
		try {
			Location loc = new Location(world, 1, 2, 3);
			JobcenterController.createJobcenter("center", loc, 9);
			JobcenterController.getJobcenterList().clear();
			assertEquals(0, JobcenterController.getJobcenterList().size());
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "center-JobCenter.yml");
			file.delete();
			JobcenterController.loadAllJobcenters();
			assertEquals(0, JobcenterController.getJobcenterList().size());
		} catch (JobSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}
}
