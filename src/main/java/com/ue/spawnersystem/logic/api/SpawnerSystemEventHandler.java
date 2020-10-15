package com.ue.spawnersystem.logic.api;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface SpawnerSystemEventHandler {

	/**
	 * Prevents spawner to be renamed in an anvil.
	 * 
	 * @param event
	 */
	public void handleInventoryClick(InventoryClickEvent event);

	/**
	 * Handles the set block event.
	 * 
	 * @param event
	 */
	public void handleSetBlockEvent(BlockPlaceEvent event);

	/**
	 * Handles the break block event.
	 * 
	 * @param event
	 */
	public void handleBreakBlockEvent(BlockBreakEvent event);

}
