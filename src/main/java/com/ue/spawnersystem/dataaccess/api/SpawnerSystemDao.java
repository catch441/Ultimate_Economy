package com.ue.spawnersystem.dataaccess.api;

import java.util.List;

import org.bukkit.Location;

public interface SpawnerSystemDao {

	/**
	 * Saves a spawner location with entity and player.
	 * 
	 * @param location
	 * @param spawnerName
	 * @param entityType
	 * @param player
	 */
	public void saveSpawnerLocation(Location location, String spawnerName, String entityType, String player);

	/**
	 * Returns all spawner names.
	 * 
	 * @return spawner names
	 */
	public List<String> loadSpawnerNames();

	/**
	 * Returns the spawner location.
	 * 
	 * @param spawnerName
	 * @return location
	 */
	public Location loadSpawnerLocation(String spawnerName);

	/**
	 * Returns the spawner owner.
	 * 
	 * @param spawnerName
	 * @return player name
	 */
	public String loadSpawnerOwner(String spawnerName);

	/**
	 * Retur@Override
	ns the spawner entity.
	 * 
	 * @param spawnerName
	 * @return entity
	 */
	public String loadSpawnerEntity(String spawnerName);

	/**
	 * Deletes a spawner.
	 * 
	 * @param spawnerName
	 */
	public void saveRemoveSpawner(String spawnerName);

	/**
	 * Setup the savefile.
	 */
	public void setupSavefile();

}
