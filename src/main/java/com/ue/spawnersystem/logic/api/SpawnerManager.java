package com.ue.spawnersystem.logic.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface SpawnerManager {

	/**
	 * Loads all spawners in all worlds.
	 */
	public void loadAllSpawners();

	/**
	 * Removes a spawner from the list of placed spawners.
	 * 
	 * @param location
	 */
	public void removeSpawner(Location location);

	/**
	 * Adds a spawner to the list of placed spawners.
	 * 
	 * @param entity
	 * @param player
	 * @param location
	 */
	public void addSpawner(String entity, Player player, Location location);

}
