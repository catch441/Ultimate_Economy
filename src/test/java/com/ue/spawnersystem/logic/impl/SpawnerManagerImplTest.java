package com.ue.spawnersystem.logic.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.ServerProvider;
import com.ue.spawnersystem.dataaccess.api.SpawnerSystemDao;

@ExtendWith(MockitoExtension.class)
public class SpawnerManagerImplTest {

	@InjectMocks
	SpawnerManagerImpl spawnerManager;
	@Mock
	SpawnerSystemDao spawnerSystemDao;
	@Mock
	ServerProvider serverProvider;
	
	@Test
	public void addSpawnerTest() {
		Location loc = mock(Location.class);
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(loc.getBlockX()).thenReturn(1);
		when(loc.getBlockY()).thenReturn(1);
		when(loc.getBlockZ()).thenReturn(1);
		spawnerManager.addSpawner("cow", player, loc);
		verify(spawnerSystemDao).saveSpawnerLocation(loc, "1-01-01-0", "cow", "catch441");
	}
	
	@Test
	public void removeSpawnerTest() {
		Location loc = mock(Location.class);
		when(loc.getBlockX()).thenReturn(1);
		when(loc.getBlockY()).thenReturn(1);
		when(loc.getBlockZ()).thenReturn(1);
		spawnerManager.removeSpawner(loc);
		verify(spawnerSystemDao).saveRemoveSpawner("1-01-01-0");
	}
	
	@Test
	public void loadAllSpawnersTest() {
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Block block = mock(Block.class);
		Plugin plugin = mock(Plugin.class);
		when(spawnerSystemDao.loadSpawnerOwner("myspawner")).thenReturn("catch441");
		when(spawnerSystemDao.loadSpawnerEntity("myspawner")).thenReturn("cow");
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(loc.getWorld()).thenReturn(world);
		when(world.getBlockAt(loc)).thenReturn(block);
		when(spawnerSystemDao.loadSpawnerLocation("myspawner")).thenReturn(loc);
		when(spawnerSystemDao.loadSpawnerNames()).thenReturn(Arrays.asList("myspawner"));
		
		spawnerManager.loadAllSpawners();
		verify(spawnerSystemDao).setupSavefile();
		verify(block).setMetadata(eq("name"), any(FixedMetadataValue.class));
		verify(block).setMetadata(eq("entity"), any(FixedMetadataValue.class));
	}
}
