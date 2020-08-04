package com.ue.jobsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.jobsystem.dataaccese.impl.JobcenterDaoImpl;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobController;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

public class JobcenterSavefileHandlerTest {

	private static JobcenterDaoImpl savefileHandler;
	private static WorldMock world;
	private static ServerMock server;

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
		savefileHandler = new JobcenterDaoImpl("kthcenter", true);
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		UltimateEconomy.getInstance.getDataFolder().delete();
		MockBukkit.unload();
		savefileHandler.deleteSavefile();
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
		file.delete();
		savefileHandler = new JobcenterDaoImpl("kthcenter", true);
		int size = JobController.getJobList().size();
		for (int i = 0; i < size; i++) {
			JobController.deleteJob(JobController.getJobList().get(0));
		}
	}

	@Test
	public void constructorNewTest() {
		JobcenterDaoImpl savefileHandler1 = new JobcenterDaoImpl("kthcenter", true);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
		assertTrue(file.exists());
		savefileHandler1.deleteSavefile();
	}

	@Test
	public void constructorLoadTest() {
		try {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
			file.createNewFile();
			JobcenterDaoImpl savefileHandler1 = new JobcenterDaoImpl("kthcenter", false);
			File result = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
			assertTrue(result.exists());
			savefileHandler1.deleteSavefile();
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void saveJobcenterSizeTest() {
		savefileHandler.saveJobcenterSize(9);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(9, config.getInt("JobCenterSize"));
	}

	@Test
	public void saveJobcenterNameTest() {
		savefileHandler.saveJobcenterName("myname");
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("myname", config.getString("JobCenterName"));
	}

	@Test
	public void saveJobcenterLocationTest() {
		Location loc = new Location(world, 1, 2, 3);
		savefileHandler.saveJobcenterLocation(loc);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("World", config.getString("JobcenterLocation.World"));
		assertEquals(1, config.getInt("JobcenterLocation.x"));
		assertEquals(2, config.getInt("JobcenterLocation.y"));
		assertEquals(3, config.getInt("JobcenterLocation.z"));
	}

	@Test
	public void saveJobNameListTest() {
		List<String> list = new ArrayList<>();
		list.add("myjob");
		savefileHandler.saveJobNameList(list);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(1, config.getStringList("Jobnames").size());
		assertEquals("myjob", config.getStringList("Jobnames").get(0));
	}

	@Test
	public void saveJobTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			savefileHandler.saveJob(job, "stone", 4);
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals("stone", config.getString("Jobs.myjob.ItemMaterial"));
			assertEquals(4, config.getInt("Jobs.myjob.Slot"));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void saveJobTestWithDelete() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			savefileHandler.saveJob(job, "stone", 4);
			savefileHandler.saveJob(job, null, 4);
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertFalse(config.isSet("Jobs.myjob"));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void loadJobcenterSizeTest() {
		savefileHandler.saveJobcenterSize(9);
		savefileHandler = new JobcenterDaoImpl("kthcenter", false);
		assertEquals(9, savefileHandler.loadJobcenterSize());
	}

	@Test
	public void loadJobcenterLocationTest() {
		Location loc = new Location(world, 1, 2, 3);
		savefileHandler.saveJobcenterLocation(loc);
		savefileHandler = new JobcenterDaoImpl("kthcenter", false);
		Location result = savefileHandler.loadJobcenterLocation();
		assertEquals(world, result.getWorld());
		assertEquals("1.0", String.valueOf(result.getX()));
		assertEquals("2.0", String.valueOf(result.getY()));
		assertEquals("3.0", String.valueOf(result.getZ()));
	}
	
	@Test
	public void loadJobcenterLocationDeprecatedTest() {
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopLocation.World", "World");
		config.set("ShopLocation.x",1);
		config.set("ShopLocation.y",2);
		config.set("ShopLocation.z",3);
		save(file,config);
		savefileHandler = new JobcenterDaoImpl("kthcenter", false);
		Location result = savefileHandler.loadJobcenterLocation();
		assertEquals(world, result.getWorld());
		assertEquals("1.0", String.valueOf(result.getX()));
		assertEquals("2.0", String.valueOf(result.getY()));
		assertEquals("3.0", String.valueOf(result.getZ()));
	}

	@Test
	public void loadJobSlotTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			savefileHandler.saveJob(job, "stone", 3);
			savefileHandler = new JobcenterDaoImpl("kthcenter", false);
			assertEquals(3, savefileHandler.loadJobSlot(job));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void loadJobSlotDeprecatedTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "kthcenter-JobCenter.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			config.set("Jobs.myjob.ItemSlot", 9);
			save(file,config);
			savefileHandler = new JobcenterDaoImpl("kthcenter", false);
			assertEquals(8, savefileHandler.loadJobSlot(job));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void loadJobNameListTest() {
		List<String> list = new ArrayList<>();
		list.add("myjob");
		savefileHandler.saveJobNameList(list);
		savefileHandler = new JobcenterDaoImpl("kthcenter", false);
		assertEquals(1,savefileHandler.loadJobNameList().size());
		assertEquals("myjob",savefileHandler.loadJobNameList().get(0));
	}
	
	@Test
	public void loadJobItemMaterialTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			savefileHandler.saveJob(job, "STONE", 3);
			savefileHandler = new JobcenterDaoImpl("kthcenter", false);
			assertEquals(Material.STONE, savefileHandler.loadJobItemMaterial(job));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	private void save(File file, YamlConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			fail();
		}
	}
}
