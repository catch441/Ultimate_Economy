package org.ue.jobsystem.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
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
import org.ue.common.utils.ServerProvider;
import org.ue.jobsystem.logic.api.Job;

@ExtendWith(MockitoExtension.class)
public class JobcenterDaoImplTest {

	@InjectMocks
	JobcenterDaoImpl jobcenterDao;
	@Mock
	ServerProvider serverProvider;

	/**
	 * Deletes the savefile.
	 */
	@AfterEach
	public void cleanUp() {
		File file = new File("src/catchcenter-JobCenter.yml");
		file.delete();
	}

	@Test
	public void deleteSavefileTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		File file = new File("src/catchcenter-JobCenter.yml");
		assertTrue(file.exists());
		jobcenterDao.deleteSavefile();
		assertFalse(file.exists());
	}

	@Test
	public void setupSavefileTest() {
		File result = new File("src/catchcenter-JobCenter.yml");
		assertFalse(result.exists());
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		assertTrue(result.exists());
	}

	@Test
	public void setupSavefileLoadTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		jobcenterDao.saveSize("", 9);
		jobcenterDao.setupSavefile("catchcenter");
		File file = new File("src/catchcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(file.exists());
		assertTrue(config.isSet("Size"));
	}

	@Test
	public void saveJobcenterSizeTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		jobcenterDao.saveSize("", 9);
		File file = new File("src/catchcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(9, config.getInt("Size"));
	}

	@Test
	public void saveJobcenterNameTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		jobcenterDao.saveJobcenterName("myname");
		File file = new File("src/catchcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("myname", config.getString("JobCenterName"));
	}

	@Test
	public void saveJobcenterLocationTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		Location loc = new Location(world, 1, 2, 3);
		jobcenterDao.saveLocation("", loc);
		File file = new File("src/catchcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("World", config.getString("Location.world"));
		assertEquals(1, config.getInt("Location.x"));
		assertEquals(2, config.getInt("Location.y"));
		assertEquals(3, config.getInt("Location.z"));
	}

	@Test
	public void saveJobNameListTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		List<String> list = new ArrayList<>();
		list.add("myjob");
		jobcenterDao.saveJobNameList(list);
		File file = new File("src/catchcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(1, config.getStringList("Jobnames").size());
		assertEquals("myjob", config.getStringList("Jobnames").get(0));
	}

	@Test
	public void saveJobTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		jobcenterDao.saveJob(job, "stone", 4);
		File file = new File("src/catchcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("stone", config.getString("Jobs.myjob.ItemMaterial"));
		assertEquals(4, config.getInt("Jobs.myjob.Slot"));
	}

	@Test
	public void saveJobTestWithDelete() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		jobcenterDao.saveJob(job, "stone", 4);
		jobcenterDao.saveJob(job, null, 4);
		File file = new File("src/catchcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertFalse(config.isSet("Jobs.myjob"));
	}

	@Test
	public void loadJobcenterSizeTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		jobcenterDao.saveSize("", 9);
		jobcenterDao.setupSavefile("catchcenter");
		assertEquals(9, jobcenterDao.loadSize(""));
	}

	@Test
	public void loadJobcenterLocationTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		when(serverProvider.getWorld("World")).thenReturn(world);
		Location loc = new Location(world, 1, 2, 3);
		jobcenterDao.saveLocation("", loc);
		jobcenterDao.setupSavefile("catchcenter");
		Location result = jobcenterDao.loadLocation("");
		assertEquals(world, result.getWorld());
		assertEquals("1.0", String.valueOf(result.getX()));
		assertEquals("2.0", String.valueOf(result.getY()));
		assertEquals("3.0", String.valueOf(result.getZ()));
	}

	@Test
	public void loadJobcenterLocationDeprecatedTest() {
		File file = new File("src/catchcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("JobcenterLocation.World", "World");
		config.set("JobcenterLocation.x", 1);
		config.set("JobcenterLocation.y", 2);
		config.set("JobcenterLocation.z", 3);
		save(file, config);
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		when(serverProvider.getWorld("World")).thenReturn(world);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		Location result = jobcenterDao.loadLocation("");
		assertEquals(world, result.getWorld());
		assertEquals("1.0", String.valueOf(result.getX()));
		assertEquals("2.0", String.valueOf(result.getY()));
		assertEquals("3.0", String.valueOf(result.getZ()));
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("JobcenterLocation"));
	}

	@Test
	public void loadJobSlotTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		jobcenterDao.saveJob(job, "stone", 3);
		jobcenterDao.setupSavefile("catchcenter");
		assertEquals(3, jobcenterDao.loadJobSlot(job));
	}

	@Test
	public void loadJobSlotDeprecatedTest() {
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		File file = new File("src/catchcenter-JobCenter.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("Jobs.myjob.ItemSlot", 9);
		save(file, config);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		assertEquals(8, jobcenterDao.loadJobSlot(job));
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("Jobs.myjob.ItemSlot"));
	}

	@Test
	public void loadJobNameListTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		List<String> list = new ArrayList<>();
		list.add("myjob");
		jobcenterDao.saveJobNameList(list);
		jobcenterDao.setupSavefile("catchcenter");
		assertEquals(1, jobcenterDao.loadJobNameList().size());
		assertEquals("myjob", jobcenterDao.loadJobNameList().get(0));
	}

	@Test
	public void loadJobItemMaterialTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		jobcenterDao.setupSavefile("catchcenter");
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		jobcenterDao.saveJob(job, "STONE", 3);
		jobcenterDao.setupSavefile("catchcenter");
		assertEquals(Material.STONE, jobcenterDao.loadJobItemMaterial(job));
	}

	private void save(File file, YamlConfiguration config) {
		assertDoesNotThrow(() -> config.save(file));
	}
}
