package org.ue.spawnersystem.logic.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.ue.common.utils.ServerProvider;
import org.ue.spawnersystem.dataaccess.api.SpawnersystemDao;
import org.ue.spawnersystem.logic.api.SpawnerManager;

public class SpawnerManagerImpl implements SpawnerManager {

	private final SpawnersystemDao spawnerSystemDao;
	private final ServerProvider serverProvider;

	public SpawnerManagerImpl(SpawnersystemDao spawnerSystemDao, ServerProvider serverProvider) {
		this.spawnerSystemDao = spawnerSystemDao;
		this.serverProvider = serverProvider;
	}

	@Override
	public void removeSpawner(Location location) {
		String spawnername = String.valueOf((double) location.getBlockX())
				+ String.valueOf((double) location.getBlockY()) + String.valueOf((double) location.getBlockZ());
		spawnername = spawnername.replace(".", "-");
		spawnerSystemDao.saveRemoveSpawner(spawnername);
	}

	@Override
	public void addSpawner(String entity, Player player, Location location) {
		String spawnername = String.valueOf((double) location.getBlockX())
				+ String.valueOf((double) location.getBlockY()) + String.valueOf((double) location.getBlockZ());
		spawnername = spawnername.replace(".", "-");
		spawnerSystemDao.saveSpawnerLocation(location, spawnername, entity, player.getName());
	}

	@Override
	public void loadAllSpawners() {
		spawnerSystemDao.setupSavefile();
		for (String spawnername : spawnerSystemDao.loadSpawnerNames()) {
			Location location = spawnerSystemDao.loadSpawnerLocation(spawnername);
			location.getWorld().getBlockAt(location).setMetadata("name", new FixedMetadataValue(
					serverProvider.getJavaPluginInstance(), spawnerSystemDao.loadSpawnerOwner(spawnername)));
			location.getWorld().getBlockAt(location).setMetadata("entity", new FixedMetadataValue(
					serverProvider.getJavaPluginInstance(), spawnerSystemDao.loadSpawnerEntity(spawnername)));
		}
	}
}
