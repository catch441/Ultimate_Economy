package com.ue.spawnersystem.dataaccess.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.ServerProvider;

@ExtendWith(MockitoExtension.class)
public class SpawnerSystemDaoImplTest {

	@InjectMocks
	SpawnerSystemDaoImpl dao;
	@Mock
	ServerProvider serverProvider;
	
	/**
	 * Delete savefile.
	 */
	@AfterEach
	public void cleanUp() {
		File file = new File("src/SpawnerLocations.yml");
		file.delete();
	}

	@Test
	public void setupSavefileTest() {
		File result = new File("src/SpawnerLocations.yml");
		assertFalse(result.exists());
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		assertTrue(result.exists());
	}

	@Test
	public void saveSpawnerLocationTest() {
		Location location = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(location.getWorld()).thenReturn(world);
		when(location.getBlockX()).thenReturn(1);
		when(location.getBlockY()).thenReturn(2);
		when(location.getBlockZ()).thenReturn(3);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.saveSpawnerLocation(location, "myspawner", "cow", "catch441");
		dao.setupSavefile();
		File result = new File("src/SpawnerLocations.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertTrue(result.exists());
		assertEquals("1", config.getString("myspawner.X"));
		assertEquals("2", config.getString("myspawner.Y"));
		assertEquals("3", config.getString("myspawner.Z"));
		assertEquals("world", config.getString("myspawner.World"));
		assertEquals("catch441", config.getString("myspawner.player"));
		assertEquals("COW", config.getString("myspawner.EntityType"));
	}
	
	@Test
	public void saveRemoveSpawnerTest() {
		Location location = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(location.getWorld()).thenReturn(world);
		when(location.getBlockX()).thenReturn(1);
		when(location.getBlockY()).thenReturn(2);
		when(location.getBlockZ()).thenReturn(3);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.saveSpawnerLocation(location, "myspawner", "cow", "catch441");
		dao.saveRemoveSpawner("myspawner");
		File result = new File("src/SpawnerLocations.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(result);
		assertFalse(config.isSet("myspawner"));
	}
	
	@Test
	public void loadSpawnerOwnerTest() {
		Location location = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(location.getWorld()).thenReturn(world);
		when(location.getBlockX()).thenReturn(1);
		when(location.getBlockY()).thenReturn(2);
		when(location.getBlockZ()).thenReturn(3);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.saveSpawnerLocation(location, "myspawner", "cow", "catch441");
		assertEquals("catch441", dao.loadSpawnerOwner("myspawner"));
	}
	
	@Test
	public void loadSpawnerEntityTest() {
		Location location = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(location.getWorld()).thenReturn(world);
		when(location.getBlockX()).thenReturn(1);
		when(location.getBlockY()).thenReturn(2);
		when(location.getBlockZ()).thenReturn(3);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.saveSpawnerLocation(location, "myspawner", "cow", "catch441");
		assertEquals("COW", dao.loadSpawnerEntity("myspawner"));
	}
	
	@Test
	public void loadSpawnerLocationTest() {
		Location location = mock(Location.class);
		World world = mock(World.class);
		when(serverProvider.getWorld("world")).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(location.getWorld()).thenReturn(world);
		when(location.getBlockX()).thenReturn(1);
		when(location.getBlockY()).thenReturn(2);
		when(location.getBlockZ()).thenReturn(3);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.saveSpawnerLocation(location, "myspawner", "cow", "catch441");
		Location result = dao.loadSpawnerLocation("myspawner");
		assertEquals(1, result.getBlockX());
		assertEquals(2, result.getBlockY());
		assertEquals(3, result.getBlockZ());
		assertEquals(world, result.getWorld());
	}
	
	@Test
	public void loadSpawnerNamesTest() {
		Location location = mock(Location.class);
		World world = mock(World.class);
		when(world.getName()).thenReturn("world");
		when(location.getWorld()).thenReturn(world);
		when(location.getBlockX()).thenReturn(1);
		when(location.getBlockY()).thenReturn(2);
		when(location.getBlockZ()).thenReturn(3);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		dao.setupSavefile();
		dao.saveSpawnerLocation(location, "myspawner", "cow", "catch441");
		dao.saveSpawnerLocation(location, "myspawner2", "cow", "catch441");
		assertEquals(Arrays.asList("myspawner", "myspawner2"), dao.loadSpawnerNames());
	}
}
