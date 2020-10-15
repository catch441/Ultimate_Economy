package com.ue.spawnersystem.logic.impl;

import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.common.utils.ServerProvider;
import com.ue.spawnersystem.dataaccess.api.SpawnerSystemDao;
import com.ue.spawnersystem.logic.api.SpawnerManager;

public class SpawnerManagerImpl implements SpawnerManager {

	private final SpawnerSystemDao spawnerSystemDao;
	private final ServerProvider serverProvider;

	private List<String> spawnerlist;

	/**
	 * Inject constructor.
	 * 
	 * @param serverProvider
	 * @param spawnerSystemDao
	 */
	@Inject
	public SpawnerManagerImpl(ServerProvider serverProvider, SpawnerSystemDao spawnerSystemDao) {
		this.spawnerSystemDao = spawnerSystemDao;
		this.serverProvider = serverProvider;
	}

	@Override
	public void removeSpawner(Location location) {
		String spawnername = String.valueOf(location.getBlockX()) + String.valueOf(location.getBlockY())
				+ String.valueOf(location.getBlockZ());
		spawnername = spawnername.replace(".", "-");
		spawnerlist.remove(spawnername);
		spawnerSystemDao.saveRemoveSpawner(spawnername);
	}

	@Override
	public void addSpawner(String entity, Player player, Location location) {
		String spawnername = String.valueOf(location.getBlockX()) + String.valueOf(location.getBlockY())
				+ String.valueOf(location.getBlockZ());
		spawnername = spawnername.replace(".", "-");
		spawnerlist.add(spawnername);
		spawnerSystemDao.saveSpawnerLocation(location, spawnername, entity, player.getName());
	}

	@Override
	public void loadAllSpawners() {
		spawnerlist = spawnerSystemDao.loadSpawnerNames();
		for (String spawnername : spawnerlist) {
			Location location = spawnerSystemDao.loadSpawnerLocation(spawnername);
			location.getWorld().getBlockAt(location).setMetadata("name", new FixedMetadataValue(
					serverProvider.getPluginInstance(), spawnerSystemDao.loadSpawnerOwner(spawnername)));
			location.getWorld().getBlockAt(location).setMetadata("entity", new FixedMetadataValue(
					serverProvider.getPluginInstance(), spawnerSystemDao.loadSpawnerEntity(spawnername)));
		}
	}
}
