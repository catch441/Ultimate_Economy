package com.ue.jobsystem.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

public class JobTabCompleterTest {

    private static ServerMock server;
    private static WorldMock world;
    private static JobTabCompleter tabCompleter;

    /**
     * Init shop for tests.
     */
    @BeforeAll
    public static void initPlugin() {
	server = MockBukkit.mock();
	MockBukkit.load(UltimateEconomy.class);
	world = new WorldMock(Material.GRASS_BLOCK, 1);
	server.addWorld(world);
	tabCompleter = new JobTabCompleter();
	;
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
    public void zeroArgsTest() {
	String[] args = { "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(6, result.size());
	assertEquals("create", result.get(0));
	assertEquals("delete", result.get(1));
	assertEquals("move", result.get(2));
	assertEquals("job", result.get(3));
	assertEquals("addJob", result.get(4));
	assertEquals("removeJob", result.get(5));
    }

    @Test
    public void zeroArgsTestWithMatching() {
	String[] args = { "e" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(4, result.size());
	assertEquals("create", result.get(0));
	assertEquals("delete", result.get(1));
	assertEquals("move", result.get(2));
	assertEquals("removeJob", result.get(3));
    }

    @Test
    public void createArgTest() {
	String[] args = { "create" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void removeJobArgTestWithEmptyThreeArgs() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "removeJob", "jobcenter", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(2, result.size());
	    assertEquals("job1", result.get(0));
	    assertEquals("job2", result.get(1));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void removeJobArgTestWithThreeArgs() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "removeJob", "jobcenter", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("job1", result.get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void removeJobArgTestWithMoreArgs() {
	String[] args = { "removeJob", "jobcenter", "myjob", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void removeJobArgTestWithTwoArgs() {
	try {
	    JobcenterController.createJobcenter("myJobcenter1", new Location(world, 1, 1, 1), 9);
	    JobcenterController.createJobcenter("myJobcenter2", new Location(world, 1, 1, 1), 9);
	    String[] args = { "removeJob", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(2, result.size());
	    assertEquals("myJobcenter1", result.get(0));
	    assertEquals("myJobcenter2", result.get(1));
	} catch (GeneralEconomyException | JobSystemException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void removeJobArgTestWithTwoArgsMatching() {
	try {
	    JobcenterController.createJobcenter("myJobcenter1", new Location(world, 1, 1, 1), 9);
	    JobcenterController.createJobcenter("myJobcenter2", new Location(world, 1, 1, 1), 9);
	    String[] args = { "removeJob", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("myJobcenter1", result.get(0));
	} catch (GeneralEconomyException | JobSystemException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobArgTest() {
	String[] args = { "job", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(8, result.size());
	assertEquals("create", result.get(0));
	assertEquals("delete", result.get(1));
	assertEquals("addItem", result.get(2));
	assertEquals("removeItem", result.get(3));
	assertEquals("addFisher", result.get(4));
	assertEquals("removeFisher", result.get(5));
	assertEquals("addMob", result.get(6));
	assertEquals("removeMob", result.get(7));
    }

    @Test
    public void jobArgTestWithMatching() {
	String[] args = { "job", "e" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(7, result.size());
	assertEquals("create", result.get(0));
	assertEquals("delete", result.get(1));
	assertEquals("addItem", result.get(2));
	assertEquals("removeItem", result.get(3));
	assertEquals("addFisher", result.get(4));
	assertEquals("removeFisher", result.get(5));
	assertEquals("removeMob", result.get(6));
    }

    @Test
    public void addJobArgTest() {
	try {
	    JobcenterController.createJobcenter("myJobcenter", new Location(world, 1, 1, 1), 9);
	    String[] args = { "addJob", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("myJobcenter", result.get(0));
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void addJobArgTestWithMatching() {
	try {
	    JobcenterController.createJobcenter("myJobcenter1", new Location(world, 1, 1, 1), 9);
	    JobcenterController.createJobcenter("myJobcenter2", new Location(world, 1, 1, 1), 9);
	    String[] args = { "addJob", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("myJobcenter1", result.get(0));
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void addJobArgTestThreeArgs() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "addJob", "myjobcenter", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(2, result.size());
	    assertEquals("job1", result.get(0));
	    assertEquals("job2", result.get(1));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void addJobArgTestThreeArgsMatching() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "addJob", "myjobcenter", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("job1", result.get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void addJobArgTestMaterialArg() {
	String[] args = { "addJob", "myjobcenter", "myjob", "iron_or" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(2, result.size());
	assertEquals("iron_ore", result.get(0));
	assertEquals("legacy_iron_ore", result.get(1));
    }

    @Test
    public void addJobArgTestMoreArgs() {
	String[] args = { "addJob", "myjobcenter", "myjob", "iron_ore", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void moveArgTest() {
	try {
	    JobcenterController.createJobcenter("myJobcenter", new Location(world, 1, 1, 1), 9);
	    String[] args = { "move", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("myJobcenter", result.get(0));
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void moveArgTestWithMatching() {
	try {
	    JobcenterController.createJobcenter("myJobcenter1", new Location(world, 1, 1, 1), 9);
	    JobcenterController.createJobcenter("myJobcenter2", new Location(world, 1, 1, 1), 9);
	    String[] args = { "move", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("myJobcenter1", result.get(0));
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void deleteArgTest() {
	try {
	    JobcenterController.createJobcenter("myJobcenter", new Location(world, 1, 1, 1), 9);
	    String[] args = { "delete", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("myJobcenter", result.get(0));
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void deleteArgTestWithMatching() {
	try {
	    JobcenterController.createJobcenter("myJobcenter1", new Location(world, 1, 1, 1), 9);
	    JobcenterController.createJobcenter("myJobcenter2", new Location(world, 1, 1, 1), 9);
	    String[] args = { "delete", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("myJobcenter1", result.get(0));
	} catch (JobSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void deleteArgTestWithMoreArgs() {
	String[] args = { "delete", "jobcenter", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void moveArgTestWithMoreArgs() {
	String[] args = { "move", "jobcenter", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void jobCreateArgTest() {
	String[] args = { "job", "create" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void jobDeleteArgTest() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "delete", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(2, result.size());
	    assertEquals("job1", result.get(0));
	    assertEquals("job2", result.get(1));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobDeleteArgTestWithMatching() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "delete", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("job1", result.get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobDeleteArgTestWithMoreArgs() {
	String[] args = { "job", "delete", "job", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void jobAddFisherArgTestWithThreeArgs() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "addFisher", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(2, result.size());
	    assertEquals("job1", result.get(0));
	    assertEquals("job2", result.get(1));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobAddFisherArgTestWithThreeArgsMatching() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "addFisher", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("job1", result.get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobAddFisherArgTestWithFourArgs() {
	String[] args = { "job", "addFisher", "myjob", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(3, result.size());
	assertEquals("fish", result.get(0));
	assertEquals("treasure", result.get(1));
	assertEquals("junk", result.get(2));
    }

    @Test
    public void jobAddFisherArgTestWithFourArgsMatching() {
	String[] args = { "job", "addFisher", "myjob", "s" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(2, result.size());
	assertEquals("fish", result.get(0));
	assertEquals("treasure", result.get(1));
    }

    @Test
    public void jobAddFisherArgTestWithMoreArgs() {
	String[] args = { "job", "addFisher", "myjob", "fish", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void jobRemoveFisherArgTestWithThreeArgs() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "removeFisher", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(2, result.size());
	    assertEquals("job1", result.get(0));
	    assertEquals("job2", result.get(1));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobRemoveFisherArgTestWithThreeArgsMatching() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "removeFisher", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("job1", result.get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobRemoveFisherArgTestWithFourArgs() {
	String[] args = { "job", "removeFisher", "myjob", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(3, result.size());
	assertEquals("fish", result.get(0));
	assertEquals("treasure", result.get(1));
	assertEquals("junk", result.get(2));
    }

    @Test
    public void jobRemoveFisherArgTestWithFourArgsMatching() {
	String[] args = { "job", "removeFisher", "myjob", "s" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(2, result.size());
	assertEquals("fish", result.get(0));
	assertEquals("treasure", result.get(1));
    }

    @Test
    public void jobRemoveFisherArgTestWithMoreArgs() {
	String[] args = { "job", "removeFisher", "myjob", "fish", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void jobAddItemArgTest() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "addItem", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(2, result.size());
	    assertEquals("job1", result.get(0));
	    assertEquals("job2", result.get(1));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobAddItemArgTestMatching() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "addItem", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("job1", result.get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobAddItemArgTestMatchingItem() {
	String[] args = { "job", "addItem", "myjob", "iron_or" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(2, result.size());
	assertEquals("iron_ore", result.get(0));
	assertEquals("legacy_iron_ore", result.get(1));
    }
    
    @Test
    public void jobAddItemArgTestItemArg() {
	String[] args = { "job", "addItem", "myjob", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(1438, result.size());
    }

    @Test
    public void jobAddItemArgTestMoreArgs() {
	String[] args = { "job", "addItem", "myjob", "iron_ore", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void jobRemoveItemArgTest() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "removeItem", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(2, result.size());
	    assertEquals("job1", result.get(0));
	    assertEquals("job2", result.get(1));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobRemoveItemArgTestMatching() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "removeItem", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("job1", result.get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobRemoveItemArgTestMatchingItem() {
	String[] args = { "job", "removeItem", "myjob", "iron_or" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(2, result.size());
	assertEquals("iron_ore", result.get(0));
	assertEquals("legacy_iron_ore", result.get(1));
    }

    @Test
    public void jobRemoveItemArgTestMoreArgs() {
	String[] args = { "job", "removeItem", "myjob", "iron_ore", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }

    @Test
    public void jobAddMobArgTest() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "addMob", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(2, result.size());
	    assertEquals("job1", result.get(0));
	    assertEquals("job2", result.get(1));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobAddMobArgTestWithMatching() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "addMob", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("job1", result.get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobAddMobArgTestWithMobArgMatching() {
	String[] args = { "job", "addMob", "myjob", "_drag" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(1, result.size());
	assertEquals("ender_dragon", result.get(0));
    }
    
    @Test
    public void jobAddMobArgTestWithMobArg() {
	String[] args = { "job", "addMob", "myjob", "" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(104, result.size());
    }
    
    @Test
    public void jobAddMobArgTestWithMoreArgs() {
	String[] args = { "job", "addMob", "myjob", "cow","" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }
    
    @Test
    public void jobRemoveMobArgTest() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "deleteMob", "" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(2, result.size());
	    assertEquals("job1", result.get(0));
	    assertEquals("job2", result.get(1));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobRemoveMobArgTestWithMatching() {
	try {
	    JobController.createJob("job1");
	    JobController.createJob("job2");
	    String[] args = { "job", "deleteMob", "1" };
	    List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	    assertEquals(1, result.size());
	    assertEquals("job1", result.get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void jobRemoveMobArgTestWithMobArgMatching() {
	String[] args = { "job", "deleteMob", "myjob", "_drag" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(1, result.size());
	assertEquals("ender_dragon", result.get(0));
    }
    
    @Test
    public void jobRemoveMobArgTestWithMoreArgs() {
	String[] args = { "job", "deleteMob", "myjob", "cow","" };
	List<String> result = tabCompleter.onTabComplete(null, null, null, args);
	assertEquals(0, result.size());
    }
}
