package com.ue.jobsystem.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.BukkitService;
import com.ue.jobsystem.dataaccess.impl.JobcenterDaoImpl;
import com.ue.jobsystem.logic.api.Job;

@ExtendWith(MockitoExtension.class)
public class JobcenterDaoImplTest {

	@InjectMocks
	JobcenterDaoImpl jobcenterDao;
	@Mock
	BukkitService bukkitService;

	/**
	 * Deletes the savefile.
	 */
	@AfterEach
	public void cleanUp() {
		File file = new File("src/kthcenter-JobCenter.yml");
		file.delete();
	}
	
	@Test
	public void deleteSavefileTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		File file = new File("src/kthcenter-JobCenter.yml");
		assertTrue(file.exists());
		jobcenterDao.deleteSavefile();
		assertFalse(file.exists());
	}

	@Test
	public void setupSavefileTest() {
		File result = new File("src/kthcenter-JobCenter.yml");
		assertFalse(result.exists());
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		assertTrue(result.exists());
	}

	@Test
	public void constructorLoadTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		jobcenterDao.saveJobcenterSize(9);
		jobcenterDao.setupSavefile("kthjob");
		File file = new File("src/kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(file.exists());
		assertTrue(config.isSet("JobCenterSize"));
	}

	@Test
	public void saveJobcenterSizeTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		jobcenterDao.saveJobcenterSize(9);
		File file = new File("src/kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(9, config.getInt("JobCenterSize"));
	}

	@Test
	public void saveJobcenterNameTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		jobcenterDao.saveJobcenterName("myname");
		File file = new File("src/kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("myname", config.getString("JobCenterName"));
	}

	@Test
	public void saveJobcenterLocationTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		Location loc = new Location(world, 1, 2, 3);
		jobcenterDao.saveJobcenterLocation(loc);
		File file = new File("src/kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("World", config.getString("JobcenterLocation.World"));
		assertEquals(1, config.getInt("JobcenterLocation.x"));
		assertEquals(2, config.getInt("JobcenterLocation.y"));
		assertEquals(3, config.getInt("JobcenterLocation.z"));
	}

	@Test
	public void saveJobNameListTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		List<String> list = new ArrayList<>();
		list.add("myjob");
		jobcenterDao.saveJobNameList(list);
		File file = new File("src/kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(1, config.getStringList("Jobnames").size());
		assertEquals("myjob", config.getStringList("Jobnames").get(0));
	}

	@Test
	public void saveJobTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		jobcenterDao.saveJob(job, "stone", 4);
		File file = new File("src/kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("stone", config.getString("Jobs.myjob.ItemMaterial"));
		assertEquals(4, config.getInt("Jobs.myjob.Slot"));
	}

	@Test
	public void saveJobTestWithDelete() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		jobcenterDao.saveJob(job, "stone", 4);
		jobcenterDao.saveJob(job, null, 4);
		File file = new File("src/kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertFalse(config.isSet("Jobs.myjob"));
	}

	@Test
	public void loadJobcenterSizeTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		jobcenterDao.saveJobcenterSize(9);
		jobcenterDao.setupSavefile("kthcenter");
		assertEquals(9, jobcenterDao.loadJobcenterSize());
	}

	@Test
	public void loadJobcenterLocationTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		when(bukkitService.getWorld("World")).thenReturn(world);
		Location loc = new Location(world, 1, 2, 3);
		jobcenterDao.saveJobcenterLocation(loc);
		jobcenterDao.setupSavefile("kthcenter");
		Location result = jobcenterDao.loadJobcenterLocation();
		assertEquals(world, result.getWorld());
		assertEquals("1.0", String.valueOf(result.getX()));
		assertEquals("2.0", String.valueOf(result.getY()));
		assertEquals("3.0", String.valueOf(result.getZ()));
	}

	@Test
	public void loadJobcenterLocationDeprecatedTest() {
		File file = new File("src/kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopLocation.World", "World");
		config.set("ShopLocation.x", 1);
		config.set("ShopLocation.y", 2);
		config.set("ShopLocation.z", 3);
		save(file, config);
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		when(bukkitService.getWorld("World")).thenReturn(world);
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		Location result = jobcenterDao.loadJobcenterLocation();
		assertEquals(world, result.getWorld());
		assertEquals("1.0", String.valueOf(result.getX()));
		assertEquals("2.0", String.valueOf(result.getY()));
		assertEquals("3.0", String.valueOf(result.getZ()));
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("ShopLocation"));
	}

	@Test
	public void loadJobSlotTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		jobcenterDao.saveJob(job, "stone", 3);
		jobcenterDao.setupSavefile("kthcenter");
		assertEquals(3, jobcenterDao.loadJobSlot(job));
	}

	@Test
	public void loadJobSlotDeprecatedTest() {
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		File file = new File("src/kthcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Jobs.myjob.ItemSlot", 9);
		save(file, config);
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		assertEquals(8, jobcenterDao.loadJobSlot(job));
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("Jobs.myjob.ItemSlot"));
	}

	@Test
	public void loadJobNameListTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		List<String> list = new ArrayList<>();
		list.add("myjob");
		jobcenterDao.saveJobNameList(list);
		jobcenterDao.setupSavefile("kthcenter");
		assertEquals(1, jobcenterDao.loadJobNameList().size());
		assertEquals("myjob", jobcenterDao.loadJobNameList().get(0));
	}

	@Test
	public void loadJobItemMaterialTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("kthcenter");
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		jobcenterDao.saveJob(job, "STONE", 3);
		jobcenterDao.setupSavefile("kthcenter");
		assertEquals(Material.STONE, jobcenterDao.loadJobItemMaterial(job));
	}

	private void save(File file, YamlConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			fail();
		}
	}
}