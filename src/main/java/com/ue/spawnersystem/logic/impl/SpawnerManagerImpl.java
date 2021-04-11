package com.ue.spawnersystem.logic.impl;

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

	@Inject
	public SpawnerManagerImpl(SpawnerSystemDao spawnerSystemDao, ServerProvider serverProvider) {
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
