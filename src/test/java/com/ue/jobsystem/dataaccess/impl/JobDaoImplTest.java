package com.ue.jobsystem.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.BukkitService;
import com.ue.jobsystem.dataaccese.impl.JobDaoImpl;

@ExtendWith(MockitoExtension.class)
public class JobDaoImplTest {
	
	@InjectMocks
	JobDaoImpl jobDao;
	@Mock
	BukkitService bukkitService;
	
	/**
	 * Delete savefile.
	 */
	@AfterEach
	public void cleanUp() {
		File file = new File("src/kthjob-Job.yml");
		file.delete();
	}
	
	@Test
	public void setupSavefileTest() {
		File result = new File("src/kthjob-Job.yml");
		assertFalse(result.exists());
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		assertTrue(result.exists());
	}
	
	@Test
	public void setupSavefileLoadTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		jobDao.saveJobName("myjob");
		jobDao.setupSavefile("kthjob");
		File file = new File("src/kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(file.exists());
		assertTrue(config.isSet("Jobname"));
	}
	
	@Test
	public void deleteSavefileTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		File file = new File("src/kthjob-Job.yml");
		assertTrue(file.exists());
		jobDao.deleteSavefile();
		assertFalse(file.exists());
	}
	
	@Test
	public void saveJobNameTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		jobDao.saveJobName("myjob");
		File file = new File("src/kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("myjob", config.get("Jobname"));
	}
	
	@Test
	public void saveBlocklistTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		Map<String,Double> list = new HashMap<>();
		list.put("dirt", 2.0);
		jobDao.saveBlockList(list);
		File file = new File("src/kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.0", config.getString("BlockList.dirt"));
	}
	
	@Test
	public void saveFisherlistTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		Map<String,Double> list = new HashMap<>();
		list.put("fish", 2.0);
		jobDao.saveFisherList(list);
		File file = new File("src/kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.0", config.getString("FisherList.fish"));
	}
	
	@Test
	public void saveEntitylistTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		Map<String,Double> list = new HashMap<>();
		list.put("cow", 2.0);
		jobDao.saveEntityList(list);
		File file = new File("src/kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.0", config.getString("EntityList.cow"));
	}
	
	@Test
	public void loadJobNameTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		jobDao.saveJobName("myjob1");
		jobDao.setupSavefile("kthjob");
		assertEquals("myjob1", jobDao.loadJobName());
	}
	
	@Test
	public void loadBlocklistTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		assertTrue(jobDao.loadBlockList().isEmpty());
		Map<String,Double> list = new HashMap<>();
		list.put("dirt", 2.0);
		jobDao.saveBlockList(list);
		jobDao.setupSavefile("kthjob");
		Map<String,Double> result = jobDao.loadBlockList(); 
		assertTrue(result.containsKey("dirt"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
	}
	
	@Test
	public void loadFisherlistTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		assertTrue(jobDao.loadFisherList().isEmpty());
		Map<String,Double> list = new HashMap<>();
		list.put("fish1", 2.0);
		jobDao.saveFisherList(list);
		jobDao.setupSavefile("kthjob");
		Map<String,Double> result = jobDao.loadFisherList(); 
		assertTrue(result.containsKey("fish1"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
	}
	
	@Test
	public void loadEntitylistTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		assertTrue(jobDao.loadEntityList().isEmpty());
		Map<String,Double> list = new HashMap<>();
		list.put("cow", 2.0);
		jobDao.saveEntityList(list);
		jobDao.setupSavefile("kthjob");
		Map<String,Double> result = jobDao.loadEntityList(); 
		assertTrue(result.containsKey("cow"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
	}
	
	@Test
	public void loadBlocklistDeprecatedTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		File file = new File("src/kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		List<String> list = new ArrayList<>();
		list.add("dirt");
		config.set("Itemlist", list);
		config.set("JobItems.dirt.breakprice", 2.0);
		save(file,config);
		jobDao.setupSavefile("kthjob");
		Map<String,Double> result = jobDao.loadBlockList();
		assertTrue(result.containsKey("dirt"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("Itemlist"));
		assertFalse(configAfter.isSet("JobItems.dirt.breakprice"));
	}
	
	@Test
	public void loadFisherlistDeprecatedTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		File file = new File("src/kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		List<String> list = new ArrayList<>();
		list.add("fish");
		config.set("Fisherlist", list);
		config.set("Fisher.fish", 2.0);
		save(file,config);
		jobDao.setupSavefile("kthjob");
		Map<String,Double> result = jobDao.loadFisherList();
		assertTrue(result.containsKey("fish"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("Fisherlist"));
	}
	
	@Test
	public void loadEntitylistDeprecatedTest() {
		when(bukkitService.getDataFolderPath()).thenReturn("src");
		jobDao.setupSavefile("kthjob");
		File file = new File("src/kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		List<String> list = new ArrayList<>();
		list.add("cow");
		config.set("Entitylist", list);
		config.set("JobEntitys.cow.killprice", 2.0);
		save(file,config);
		jobDao.setupSavefile("kthjob");
		Map<String,Double> result = jobDao.loadEntityList();
		assertTrue(result.containsKey("cow"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
		YamlConfiguration configAfter = YamlConfiguration.loadConfiguration(file);
		assertFalse(configAfter.isSet("Entitylist"));
		assertFalse(configAfter.isSet("JobEntitys.cow.killprice"));
	}
	
	private void save(File file, YamlConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			fail();
		}
	}
}
