package com.ue.jobsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.jobsystem.impl.JobSavefileHandler;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;

public class JobSavefileHandlerTest {
	
	private static JobSavefileHandler savefileHandler;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		savefileHandler = new JobSavefileHandler("kthjob", true);
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
		File file = new File(UltimateEconomy.getInstance.getDataFolder(),"kthjob-Job.yml");
		file.delete();
		savefileHandler = new JobSavefileHandler("kthjob", true);
	}
	
	@Test
	public void constructorNewTest() {
		JobSavefileHandler savefileHandler1 = new JobSavefileHandler("myjob",true);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(),"myjob-Job.yml");
		assertTrue(file.exists());
		savefileHandler1.deleteSavefile();
	}
	
	@Test
	public void constructorLoadTest() {
		try {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(),"myjob-Job.yml");
			file.createNewFile();
			JobSavefileHandler savefileHandler1 = new JobSavefileHandler("myjob",false);
			File result = new File(UltimateEconomy.getInstance.getDataFolder(),"myjob-Job.yml");
			assertTrue(result.exists());
			savefileHandler1.deleteSavefile();
		} catch (IOException e) {
			fail();
		}
	}
	
	@Test
	public void saveJobNameTest() {
		savefileHandler.saveJobName("myjob");
		File file = new File(UltimateEconomy.getInstance.getDataFolder(),"kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("myjob", config.get("Jobname"));
		savefileHandler.saveJobName(null);
	}
	
	@Test
	public void saveBlocklistTest() {
		Map<String,Double> list = new HashMap<>();
		list.put("dirt", 2.0);
		savefileHandler.saveBlockList(list);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(),"kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.0", config.getString("BlockList.dirt"));
	}
	
	@Test
	public void saveFisherlistTest() {
		Map<String,Double> list = new HashMap<>();
		list.put("fish", 2.0);
		savefileHandler.saveFisherList(list);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(),"kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.0", config.getString("FisherList.fish"));
	}
	
	@Test
	public void saveEntitylistTest() {
		Map<String,Double> list = new HashMap<>();
		list.put("cow", 2.0);
		savefileHandler.saveEntityList(list);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(),"kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.0", config.getString("EntityList.cow"));
	}
	
	@Test
	public void loadJobNameTest() {
		savefileHandler.saveJobName("myjob1");
		assertEquals("myjob1", savefileHandler.loadJobName());
		savefileHandler.saveJobName(null);
	}
	
	@Test
	public void loadBlocklistTest() {
		assertTrue(savefileHandler.loadBlockList().isEmpty());
		Map<String,Double> list = new HashMap<>();
		list.put("dirt", 2.0);
		savefileHandler.saveBlockList(list);
		savefileHandler = new JobSavefileHandler("kthjob", false);
		Map<String,Double> result = savefileHandler.loadBlockList(); 
		assertTrue(result.containsKey("dirt"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
	}
	
	@Test
	public void loadFisherlistTest() {
		assertTrue(savefileHandler.loadFisherList().isEmpty());
		Map<String,Double> list = new HashMap<>();
		list.put("fish1", 2.0);
		savefileHandler.saveFisherList(list);
		savefileHandler = new JobSavefileHandler("kthjob", false);
		Map<String,Double> result = savefileHandler.loadFisherList(); 
		assertTrue(result.containsKey("fish1"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
	}
	
	@Test
	public void loadEntitylistTest() {
		assertTrue(savefileHandler.loadEntityList().isEmpty());
		Map<String,Double> list = new HashMap<>();
		list.put("cow", 2.0);
		savefileHandler.saveEntityList(list);
		savefileHandler = new JobSavefileHandler("kthjob", false);
		Map<String,Double> result = savefileHandler.loadEntityList(); 
		assertTrue(result.containsKey("cow"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
	}
	
	@Test
	public void loadBlocklistDeprecatedTest() {
		File file = new File(UltimateEconomy.getInstance.getDataFolder(),"kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		List<String> list = new ArrayList<>();
		list.add("dirt");
		config.set("Itemlist", list);
		config.set("JobItems.dirt.breakprice", 2.0);
		save(file,config);
		savefileHandler = new JobSavefileHandler("kthjob", false);
		Map<String,Double> result = savefileHandler.loadBlockList();
		assertTrue(result.containsKey("dirt"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
	}
	
	@Test
	public void loadFisherlistDeprecatedTest() {
		File file = new File(UltimateEconomy.getInstance.getDataFolder(),"kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		List<String> list = new ArrayList<>();
		list.add("fish");
		config.set("Fisherlist", list);
		config.set("Fisher.fish", 2.0);
		save(file,config);
		savefileHandler = new JobSavefileHandler("kthjob", false);
		Map<String,Double> result = savefileHandler.loadFisherList();
		assertTrue(result.containsKey("fish"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
	}
	
	@Test
	public void loadEntitylistDeprecatedTest() {
		File file = new File(UltimateEconomy.getInstance.getDataFolder(),"kthjob-Job.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		List<String> list = new ArrayList<>();
		list.add("cow");
		config.set("Entitylist", list);
		config.set("JobEntitys.cow.killprice", 2.0);
		save(file,config);
		savefileHandler = new JobSavefileHandler("kthjob", false);
		Map<String,Double> result = savefileHandler.loadEntityList();
		assertTrue(result.containsKey("cow"));
		assertTrue(result.containsValue(2.0));
		assertEquals(1, result.size());
	}
	
	private void save(File file, YamlConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			fail();
		}
	}
}
