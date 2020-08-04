package com.ue.jobsystem.logic.api;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public interface JobsystemEventHandler {

	/**
	 * Handles the set bloc kevent for the jobsystem.
	 * 
	 * @param event
	 */
	public void handleSetBlock(BlockPlaceEvent event);
	
	/**
	 * Handles the entity death event for the jobsystem.
	 * 
	 * @param event
	 */
	public void handleEntityDeath(EntityDeathEvent event);
	
	/**
	 * Handlers the break block event for the jobsystem.
	 * 
	 * @param event
	 */
	public void handleBreakBlock(BlockBreakEvent event);
	
	/**
	 * Handles the fishing event for the jobsystem.
	 * 
	 * @param event
	 */
	public void handleFishing(PlayerFishEvent event);
	
	/**
	 * Handles the jobcenter inventory click.
	 * 
	 * @param event
	 */
	public void handleInventoryClick(InventoryClickEvent event);
	
	/**
	 * Hnadles the open jobcenter inventory.
	 * 
	 * @param event
	 */
	public void handleOpenInventory(PlayerInteractEntityEvent event);
}
