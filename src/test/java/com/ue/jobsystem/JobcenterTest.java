package com.ue.jobsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

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
    public void getNameTest() {
	try {
	    JobcenterController.createJobcenter("center", new Location(world, 1, 1, 1), 9);
	    Jobcenter center = JobcenterController.getJobcenterList().get(0);
	    assertEquals("center", center.getName());
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void hasJobTest() {
	try {
	    JobcenterController.createJobcenter("center", new Location(world, 1, 1, 1), 9);
	    Jobcenter center = JobcenterController.getJobcenterList().get(0);
	    JobController.createJob("myjob");
	    JobController.createJob("myjob1");
	    Job job = JobController.getJobList().get(0);
	    Job job1 = JobController.getJobList().get(1);
	    center.addJob(job, "stone", 0);
	    assertTrue(center.hasJob(job));
	    assertFalse(center.hasJob(job1));
	} catch (JobSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void getJobListTest() {
	try {
	    JobcenterController.createJobcenter("center", new Location(world, 1, 1, 1), 9);
	    Jobcenter center = JobcenterController.getJobcenterList().get(0);
	    JobController.createJob("myjob");
	    Job job = JobController.getJobList().get(0);
	    center.addJob(job, "stone", 0);
	    assertEquals(1, center.getJobList().size());
	    assertEquals(job, center.getJobList().get(0));
	} catch (JobSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void despawnVillagerTest() {
	try {
	    Location loc = new Location(world, 1, 1, 1);
	    JobcenterController.createJobcenter("center", loc, 9);
	    Jobcenter center = JobcenterController.getJobcenterList().get(0);
	    assertEquals(1, world.getNearbyEntities(loc, 0, 0, 0).size());
	    center.despawnVillager();
	    assertEquals(0, world.getNearbyEntities(loc, 0, 0, 0).size());
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void moveJobcenterTest() {
	try {
	    Location loc = new Location(world, 1, 1, 1);
	    JobcenterController.createJobcenter("center", loc, 9);
	    Jobcenter center = JobcenterController.getJobcenterList().get(0);
	    Location newLoc = new Location(world, 2, 2, 2);
	    center.moveJobcenter(newLoc);
	    assertEquals(newLoc, center.getJobcenterLocation());
	    assertEquals(0, world.getNearbyEntities(loc, 0, 0, 0).size());
	    assertEquals(1, world.getNearbyEntities(newLoc, 0, 0, 0).size());
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void openInvTest() {
	try {
	    Location loc = new Location(world, 1, 1, 1);
	    JobcenterController.createJobcenter("center", loc, 9);
	    Jobcenter center = JobcenterController.getJobcenterList().get(0);
	    center.openInv(player);
	    ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
	    assertEquals("center", inv.getName());
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void deleteJobcenterTest() {
	try {
	    Location loc = new Location(world, 1, 1, 1);
	    JobcenterController.createJobcenter("center", loc, 9);
	    Jobcenter center = JobcenterController.getJobcenterList().get(0);
	    center.deleteJobcenter();
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), "center-JobCenter.yml");
	    assertFalse(file.exists());
	    assertEquals(0, world.getNearbyEntities(loc, 0, 0, 0).size());
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void removeJobTest() {
	try {
	    EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	    Location loc = new Location(world, 1, 1, 1);
	    JobcenterController.createJobcenter("center", loc, 9);
	    Jobcenter center = JobcenterController.getJobcenterList().get(0);
	    JobController.createJob("myjob");
	    Job job = JobController.getJobList().get(0);
	    ecoPlayer.joinJob(job, false);
	    center.addJob(job, "stone", 0);
	    center.removeJob(job);
	    assertEquals(0, center.getJobList().size());
	    // check inventory
	    center.openInv(player);
	    ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
	    assertEquals(Material.AIR, inv.getItem(0).getType());
	    // check savefile
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), "center-JobCenter.yml");
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	    assertEquals(0, config.getStringList("Jobnames").size());
	    assertFalse(config.contains("Jobs.myjob"));
	    assertFalse(ecoPlayer.hasJob(job));
	} catch (JobSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void removeJobTestWithNoJob() {
	try {
	    Location loc = new Location(world, 1, 1, 1);
	    JobcenterController.createJobcenter("center", loc, 9);
	    JobController.createJob("myjob");
	    Job job = JobController.getJobList().get(0);
	    Jobcenter center = JobcenterController.getJobcenterList().get(0);
	    center.removeJob(job);
	    assertTrue(false);
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof JobSystemException);
	    assertEquals("§cThis job does not exist in this jobcenter!", e.getMessage());
	}
    }
    
    @Test
    public void addJobTest() {
	try {
	    Location loc = new Location(world, 1, 1, 1);
	    JobcenterController.createJobcenter("center", loc, 9);
	    JobController.createJob("myjob");
	    Job job = JobController.getJobList().get(0);
	    Jobcenter center = JobcenterController.getJobcenterList().get(0);
	    center.addJob(job, "stone", 0);
	    assertEquals(1, center.getJobList().size());
	    assertEquals(job, center.getJobList().get(0));
	    // check inventory
	    center.openInv(player);
	    ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
	    assertEquals(Material.STONE, inv.getItem(0).getType());
	    assertEquals("myjob",inv.getItem(0).getItemMeta().getDisplayName());
	    // check savefile
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(),"center-JobCenter.yml");
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	    assertEquals(1, config.getStringList("Jobnames").size());
	    assertEquals("myjob", config.getStringList("Jobnames").get(0));
	    assertEquals("STONE", config.getString("Jobs.myjob.ItemMaterial"));
	    assertEquals("0", config.getString("Jobs.myjob.Slot"));
	} catch (JobSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }
}
