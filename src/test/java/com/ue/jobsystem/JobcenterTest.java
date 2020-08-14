package com.ue.jobsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobController;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.jobsystem.logic.impl.JobcenterImpl;
import com.ue.jobsystem.logic.impl.JobcenterManagerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class JobcenterTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;

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
		player = server.addPlayer("catch441");
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
		int size2 = JobcenterManagerImpl.getJobcenterList().size();
		for (int i = 0; i < size2; i++) {
			try {
				JobcenterManagerImpl.deleteJobcenter(JobcenterManagerImpl.getJobcenterList().get(0));
			} catch (JobSystemException e) {
				fail();
			}
		}
	}
	
	/**
	 * Location loc = new Location(world, 1, 2, 3);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
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
			assertNull(inv.getItem(0));
			assertNull(inv.getItem(1));
			assertNull(inv.getItem(2));
			assertNull(inv.getItem(3));
			assertNull(inv.getItem(4));
			assertNull(inv.getItem(5));
			assertNull(inv.getItem(6));
			assertNull(inv.getItem(7));
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
	 */
	
	@Test
	public void doubleVillagerTest() {
		try {
			Location loc = new Location(world, 5, 5, 5);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			JobcenterManagerImpl.getJobcenterList().clear();
			assertEquals(1, world.getNearbyEntities(loc, 0, 0, 0).size());
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			assertEquals(1, world.getNearbyEntities(loc, 0, 0, 0).size());
		} catch (JobSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void constructorLoadTest() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			center.addJob(job, "stone", 0);
			JobcenterManagerImpl.getJobcenterList().clear();
			Jobcenter result = new JobcenterImpl("center");
			assertEquals("center",result.getName());
			assertEquals(loc, result.getJobcenterLocation());
			// check inventory
			result.openInv(player);
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals(9, inv.getSize());
			assertEquals(Material.STONE,inv.getItem(0).getType());
			assertEquals("myjob",inv.getItem(0).getItemMeta().getDisplayName());
			assertEquals(Material.ANVIL,inv.getItem(8).getType());
			List<String> lore = inv.getItem(8).getItemMeta().getLore();
			assertEquals("§6Leftclick: §aJoin",lore.get(0));
			assertEquals("§6Rightclick: §cLeave",lore.get(1));
		} catch (JobSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void constructorLoadTestWithJobError() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			center.addJob(job, "stone", 0);
			JobcenterManagerImpl.getJobcenterList().clear();
			JobController.getJobList().clear();
			Jobcenter result = new JobcenterImpl("center");
			assertEquals(0, result.getJobList().size());
		} catch (JobSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void getNameTest() {
		try {
			JobcenterManagerImpl.createJobcenter("center", new Location(world, 1, 1, 1), 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			assertEquals("center", center.getName());
		} catch (JobSystemException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void hasJobTest() {
		try {
			JobcenterManagerImpl.createJobcenter("center", new Location(world, 1, 1, 1), 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			JobController.createJob("myjob");
			JobController.createJob("myjob1");
			Job job = JobController.getJobList().get(0);
			Job job1 = JobController.getJobList().get(1);
			center.addJob(job, "stone", 0);
			assertTrue(center.hasJob(job));
			assertFalse(center.hasJob(job1));
		} catch (JobSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void getJobListTest() {
		try {
			JobcenterManagerImpl.createJobcenter("center", new Location(world, 1, 1, 1), 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			center.addJob(job, "stone", 0);
			assertEquals(1, center.getJobList().size());
			assertEquals(job, center.getJobList().get(0));
		} catch (JobSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void despawnVillagerTest() {
		try {
			Location loc = new Location(world, 12, 12, 12);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			assertEquals(1, world.getNearbyEntities(loc, 0, 0, 0).size());
			center.despawnVillager();
			assertEquals(0, world.getNearbyEntities(loc, 0, 0, 0).size());
		} catch (JobSystemException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void moveJobcenterTest() {
		try {
			Location loc = new Location(world, 3, 3, 3);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			Location newLoc = new Location(world, 2, 2, 2);
			center.moveJobcenter(newLoc);
			assertEquals(newLoc, center.getJobcenterLocation());
			assertEquals(0, world.getNearbyEntities(loc, 0, 0, 0).size());
			assertEquals(1, world.getNearbyEntities(newLoc, 0, 0, 0).size());
		} catch (JobSystemException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void openInvTest() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			center.openInv(player);
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("center", inv.getName());
		} catch (JobSystemException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void deleteJobcenterTest() {
		try {
			Location loc = new Location(world, 4, 4, 4);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			center.deleteJobcenter();
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "center-JobCenter.yml");
			assertFalse(file.exists());
			assertEquals(0, world.getNearbyEntities(loc, 0, 0, 0).size());
		} catch (JobSystemException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void removeJobTest() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 1, 1);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, false);
			center.addJob(job, "stone", 0);
			center.removeJob(job);
			assertEquals(0, center.getJobList().size());
			// check inventory
			center.openInv(player);
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertNull(inv.getItem(0));
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "center-JobCenter.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals(0, config.getStringList("Jobnames").size());
			assertFalse(config.contains("Jobs.myjob"));
			assertFalse(ecoPlayer.hasJob(job));
		} catch (JobSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void removeJobTestWithPlayerLeaveJobError() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			center.removeJob(job);
			fail();
		} catch (JobSystemException | GeneralEconomyException e) {
			assertTrue(e instanceof JobSystemException);
			assertEquals("§cThis job does not exist in this jobcenter!", e.getMessage());
		}
	}
	
	@Test
	public void removeJobTestWithJobInOtherJocenter() {
		Location loc = new Location(world, 1, 1, 1);
		try {
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			JobcenterManagerImpl.createJobcenter("center2", loc.add(10, 10, 10), 9);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			Jobcenter center2 = JobcenterManagerImpl.getJobcenterList().get(1);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			center.addJob(job, "stone", 0);
			center2.addJob(job, "stone", 0);
			center.removeJob(job);
			assertEquals(0, center.getJobList().size());
			assertEquals(job, center2.getJobList().get(0));
		} catch (JobSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void addJobTest() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			JobcenterManagerImpl.createJobcenter("center", loc, 9);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			center.addJob(job, "stone", 0);
			assertEquals(1, center.getJobList().size());
			assertEquals(job, center.getJobList().get(0));
			// check inventory
			center.openInv(player);
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals(Material.STONE, inv.getItem(0).getType());
			assertEquals("myjob", inv.getItem(0).getItemMeta().getDisplayName());
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "center-JobCenter.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals(1, config.getStringList("Jobnames").size());
			assertEquals("myjob", config.getStringList("Jobnames").get(0));
			assertEquals("STONE", config.getString("Jobs.myjob.ItemMaterial"));
			assertEquals("0", config.getString("Jobs.myjob.Slot"));
		} catch (JobSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}
}
