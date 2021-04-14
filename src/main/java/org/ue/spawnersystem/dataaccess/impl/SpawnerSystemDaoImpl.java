package org.ue.spawnersystem.dataaccess.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ue.common.utils.SaveFileUtils;
import org.ue.common.utils.ServerProvider;
import org.ue.spawnersystem.dataaccess.api.SpawnerSystemDao;

public class SpawnerSystemDaoImpl extends SaveFileUtils implements SpawnerSystemDao {
	
	private final ServerProvider serverProvider;
	
	@Inject
	public SpawnerSystemDaoImpl(ServerProvider serverProvider) {
		this.serverProvider = serverProvider;
	}
	
	@Override
	public void setupSavefile() {
		file = new File(serverProvider.getDataFolderPath(), "SpawnerLocations.yml");
		if (!file.exists()) {
			createFile(file);
		}
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	@Override
	public void saveSpawnerLocation(Location location, String spawnerName, String entityType, String player) {
		config.set(spawnerName + ".X", location.getBlockX());
		config.set(spawnerName + ".Y", location.getBlockY());
		config.set(spawnerName + ".Z", location.getBlockZ());
		config.set(spawnerName + ".World", location.getWorld().getName());
		config.set(spawnerName + ".player", player);
		config.set(spawnerName + ".EntityType", entityType.toUpperCase());
		save(config, file);
	}
	
	@Override
	public void saveRemoveSpawner(String spawnerName) {
		config.set(spawnerName, null);
		save(config, file);
	}
	
	@Override
	public Location loadSpawnerLocation(String spawnerName) {
		String world = config.getString(spawnerName + ".World");
		return new Location(serverProvider.getWorld(world), config.getDouble(spawnerName + ".X"),
				config.getDouble(spawnerName + ".Y"), config.getDouble(spawnerName + ".Z"));
	}
	
	@Override
	public String loadSpawnerOwner(String spawnerName) {
		return config.getString(spawnerName + ".player");
	}
	
	@Override
	public String loadSpawnerEntity(String spawnerName) {
		return config.getString(spawnerName + ".EntityType");
	}
	
	@Override
	public List<String> loadSpawnerNames() {
		return new ArrayList<>(config.getConfigurationSection("").getKeys(false));
	}
	
	// TODO remove Spawnerlist from real config

}
