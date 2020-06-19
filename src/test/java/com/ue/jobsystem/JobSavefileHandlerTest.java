package com.ue.jobsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

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
		assertEquals("kthjob", savefileHandler.loadJobName());
	}
}
