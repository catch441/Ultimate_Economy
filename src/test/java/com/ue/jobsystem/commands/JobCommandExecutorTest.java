package com.ue.jobsystem.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class JobCommandExecutorTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;
	private static CommandExecutor executor;

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
		executor = new JobCommandExecutor();
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
	public void zeroArgs() {
		String[] args = {};
		executor.onCommand(player, null, "jobcenter", args);
		assertNull(player.nextMessage());
	}
	
	@Test
	public void invalidArg() {
		String[] args = {"foo"};
		executor.onCommand(player, null, "jobcenter", args);
		assertNull(player.nextMessage());
	}

	@Test
	public void createCommandTest() {
		String[] args = { "create", "center", "9" };
		executor.onCommand(player, null, "jobcenter", args);
		Jobcenter center = JobcenterController.getJobcenterList().get(0);
		assertEquals("center", center.getName());
		String message = player.nextMessage();
		assertEquals("§6The jobcenter §acenter§6 was created.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void createCommandTestWithInvalidInteger() {
		String[] args = { "create", "center", "a" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§cThe parameter §4number§c is invalid!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void createCommandTestWithInvalidArgumentNumber() {
		String[] args = { "create", "center" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter create <jobcenter> <size> <- size have to be a multible of 9", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void deleteCommandTest() {
		createJobcenter();
		String[] args = { "delete", "center" };
		executor.onCommand(player, null, "jobcenter", args);
		assertEquals(0, JobcenterController.getJobcenterList().size());
		String message = player.nextMessage();
		assertEquals("§6The jobcenter §acenter§6 was deleted.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void deleteCommandTestWithInvalidArgumentNumber() {
		String[] args = { "delete", "center", "test" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter delete <jobcenter>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void deleteCommandTestWithInvalidJobcenter() {
		String[] args = { "delete", "center" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4center§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void moveCommandTest() {
		Location loc = new Location(world, 1, 1, 1);
		player.setLocation(loc);
		createJobcenter();
		String[] args = { "move", "center" };
		executor.onCommand(player, null, "jobcenter", args);
		assertEquals(loc, JobcenterController.getJobcenterList().get(0).getJobcenterLocation());
		assertNull(player.nextMessage());
	}

	@Test
	public void moveCommandTestWithInvalidArgumentNumber() {
		String[] args = { "move", "center", "test" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter move <jobcenter>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void moveCommandTestWithInvalidJobcenter() {
		String[] args = { "move", "center" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4center§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void addJobCommandTest() {
		createJobcenter();
		createJob();
		String[] args = { "addJob", "center", "myjob", "stone", "1" };
		executor.onCommand(player, null, "jobcenter", args);
		try {
			assertEquals("myjob", JobcenterController.getJobcenterByName("center").getJobList().get(0).getName());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
		String message = player.nextMessage();
		assertEquals("§6The job §amyjob§6 was added to the JobCenter §acenter.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void addJobCommandTestWithInvalidArgumentNumber() {
		String[] args = { "addJob", "center", "test" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter addJob <jobcenter> <job> <material> <slot>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void addJobCommandTestWithInvalidInteger() {
		createJobcenter();
		createJob();
		String[] args = { "addJob", "center", "myjob", "stone", "a" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§cThe parameter §4number§c is invalid!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void addJobCommandTestWithInvalidJob() {
		createJobcenter();
		String[] args = { "addJob", "center", "myjob", "stone", "0" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4myjob§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void addJobCommandTestWithInvalidJobcenter() {
		String[] args = { "addJob", "center", "myjob", "stone", "0" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4center§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void removeJobCommandTest() {
		createJobcenter();
		createJob();
		addJob();
		String[] args = { "removeJob", "center", "myjob" };
		executor.onCommand(player, null, "jobcenter", args);
		try {
			assertEquals(0, JobcenterController.getJobcenterByName("center").getJobList().size());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
		String message = player.nextMessage();
		assertEquals("§6The job §amyjob§6 was removed.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void removeJobCommandTestWithInvalidArgumentNumber() {
		String[] args = { "removeJob", "center", "myjob", "test" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter removeJob <jobcenter> <job>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void removeJobCommandTestWithInvalidJobcenter() {
		String[] args = { "removeJob", "center", "myjob" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4center§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void removeJobCommandTestWithInvalidJob() {
		createJobcenter();
		String[] args = { "removeJob", "center", "myjob" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4myjob§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void createJobCommandTest() {
		String[] args = { "job", "create", "myjob" };
		executor.onCommand(player, null, "jobcenter", args);
		try {
			assertEquals("myjob", JobController.getJobByName("myjob").getName());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
		String message = player.nextMessage();
		assertEquals("§6The job §amyjob§6 was created.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void createJobCommandTestWithInvalidArgumentNumber() {
		String[] args = { "job", "create" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter job create <job>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void deleteJobCommandTest() {
		createJob();
		String[] args = { "job", "delete", "myjob" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§6The job §amyjob§6 was deleted.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void deleteJobCommandTestWithInvalidArgumentNumber() {
		createJob();
		String[] args = { "job", "delete" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter job delete <job>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void deleteJobCommandTestWithInvalidJob() {
		String[] args = { "job", "delete", "myjob" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4myjob§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddFisherCommandTest() {
		createJob();
		String[] args = { "job", "addFisher", "myjob", "fish", "1.5" };
		executor.onCommand(player, null, "jobcenter", args);
		try {
			assertEquals("1.5", String.valueOf(JobController.getJobByName("myjob").getFisherList().get("fish")));
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
		String message = player.nextMessage();
		assertEquals("§6The loottype §afish§6 was added to the job.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddFisherCommandTestInvalidArgumentNumber() {
		createJob();
		String[] args = { "job", "addFisher", "myjob", "fish" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter job addFisher <job> [fish/treasure/junk] <price>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddFisherCommandTestInvalidDouble() {
		createJob();
		String[] args = { "job", "addFisher", "myjob", "fish", "a" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§cThe parameter §4number§c is invalid!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddFisherCommandTestInvalidFisherType() {
		createJob();
		String[] args = { "job", "addFisher", "myjob", "test", "1.5" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§cThe parameter §4test§c is invalid!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddFisherCommandTestInvalidJob() {
		String[] args = { "job", "addFisher", "myjob", "fish", "1.5" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4myjob§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddItemCommandTest() {
		createJob();
		String[] args = { "job", "addItem", "myjob", "stone", "1.5" };
		executor.onCommand(player, null, "jobcenter", args);
		try {
			assertEquals("1.5", String.valueOf(JobController.getJobByName("myjob").getBlockList().get("STONE")));
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
		String message = player.nextMessage();
		assertEquals("§6The item §astone§6 was added to the job.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddItemCommandTestWithInvalidArgumentNumber() {
		createJob();
		String[] args = { "job", "addItem", "myjob", "stone" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter job addItem <job> <material> <price>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddItemCommandTestWithInvalidDouble() {
		createJob();
		String[] args = { "job", "addItem", "myjob", "stone", "a" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§cThe parameter §4number§c is invalid!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddItemCommandTestWithInvalidJob() {
		String[] args = { "job", "addItem", "myjob", "stone", "1.5" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4myjob§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddMobCommandTest() {
		createJob();
		String[] args = { "job", "addMob", "myjob", "cow", "1.5" };
		executor.onCommand(player, null, "jobcenter", args);
		try {
			assertEquals("1.5", String.valueOf(JobController.getJobByName("myjob").getEntityList().get("COW")));
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
		String message = player.nextMessage();
		assertEquals("§6The entity §acow§6 was added to the job.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddMobCommandTestWithInvalidArgumentNumber() {
		String[] args = { "job", "addMob", "myjob", "cow" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter job addMob <job> <entity> <price>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddMobCommandTestWithInvalidDouble() {
		createJob();
		String[] args = { "job", "addMob", "myjob", "cow", "a" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§cThe parameter §4number§c is invalid!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddMobCommandTestWithInvalidEntity() {
		createJob();
		String[] args = { "job", "addMob", "myjob", "test", "1.5" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§cThe parameter §4TEST§c is invalid!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobAddMobCommandTestWithInvalidJob() {
		String[] args = { "job", "addMob", "myjob", "test", "1.5" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4myjob§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveMobCommandTest() {
		createJob();
		addMob();
		String[] args = { "job", "removeMob", "myjob", "cow" };
		executor.onCommand(player, null, "jobcenter", args);
		try {
			assertEquals(0, JobController.getJobByName("myjob").getEntityList().size());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
		String message = player.nextMessage();
		assertEquals("§6The entity §acow§6 was removed from the job.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveMobCommandTestWithInvalidArgumentNumber() {
		createJob();
		addMob();
		String[] args = { "job", "removeMob", "myjob" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter job removeMob <jobname> <entity>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveMobCommandTestWithInvalidMob() {
		createJob();
		String[] args = { "job", "removeMob", "myjob", "cow" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§cThis entity does not exist in this job!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveMobCommandTestWithInvalidJob() {
		String[] args = { "job", "removeMob", "myjob", "cow" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4myjob§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveFisherCommandTest() {
		createJob();
		addFisher();
		String[] args = { "job", "removeFisher", "myjob", "fish" };
		executor.onCommand(player, null, "jobcenter", args);
		try {
			assertEquals(0, JobController.getJobByName("myjob").getFisherList().size());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
		String message = player.nextMessage();
		assertEquals("§6The loottype §afish§6 was removed from the job.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveFisherCommandTestWithInvalidArgumentNumber() {
		createJob();
		addFisher();
		String[] args = { "job", "removeFisher", "myjob" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter job removeFisher <jobname> <fish/treasure/junk>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveFisherCommandTestWithInvalidType() {
		createJob();
		String[] args = { "job", "removeFisher", "myjob", "test" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§cThe parameter §4test§c is invalid!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveFisherCommandTestWithInvalidJob() {
		String[] args = { "job", "removeFisher", "myjob", "fish" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4myjob§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveItemCommandTest() {
		createJob();
		addItem();
		String[] args = { "job", "removeItem", "myjob", "stone" };
		executor.onCommand(player, null, "jobcenter", args);
		try {
			assertEquals(0, JobController.getJobByName("myjob").getBlockList().size());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
		String message = player.nextMessage();
		assertEquals("§6The item §astone§6 was deleted from the job.", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveItemCommandTestWithInvalidArgumentNumber() {
		createJob();
		addItem();
		String[] args = { "job", "removeItem", "myjob" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter job removeItem <job> <material>", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveItemCommandTestWithInvalidItem() {
		createJob();
		String[] args = { "job", "removeItem", "myjob", "test" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§cThe parameter §4TEST§c is invalid!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobRemoveItemCommandTestWithInvalidJob() {
		String[] args = { "job", "removeItem", "myjob", "stone" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("§c§4myjob§c does not exist!", message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobCommandTest() {
		String[] args = { "job" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter job [create/delete/addItem/removeItem/addMob/removeMob/addFisher/removeFisher]",
				message);
		assertNull(player.nextMessage());
	}

	@Test
	public void jobCommandTestWithInvalidCommand() {
		String[] args = { "job", "test" };
		executor.onCommand(player, null, "jobcenter", args);
		String message = player.nextMessage();
		assertEquals("/jobcenter job [create/delete/addItem/removeItem/addMob/removeMob/addFisher/removeFisher]",
				message);
		assertNull(player.nextMessage());
	}

	private void addItem() {
		String[] args0 = { "job", "addItem", "myjob", "stone", "1.5" };
		executor.onCommand(player, null, "jobcenter", args0);
		// skip add message
		player.nextMessage();
	}

	private void addFisher() {
		String[] args0 = { "job", "addFisher", "myjob", "fish", "1.5" };
		executor.onCommand(player, null, "jobcenter", args0);
		// skip add message
		player.nextMessage();
	}

	private void addMob() {
		String[] args0 = { "job", "addMob", "myjob", "cow", "1.5" };
		executor.onCommand(player, null, "jobcenter", args0);
		// skip add message
		player.nextMessage();
	}

	private void createJob() {
		String[] args1 = { "job", "create", "myjob" };
		executor.onCommand(player, null, "jobcenter", args1);
		// skip create message
		player.nextMessage();
	}

	private void createJobcenter() {
		String[] args0 = { "create", "center", "9" };
		executor.onCommand(player, null, "jobcenter", args0);
		// skip create message
		player.nextMessage();
	}

	private void addJob() {
		String[] args2 = { "addJob", "center", "myjob", "stone", "1" };
		executor.onCommand(player, null, "jobcenter", args2);
		// skip create message
		player.nextMessage();
	}
}
