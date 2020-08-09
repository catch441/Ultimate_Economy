package com.ue.townsystem.logic.api;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public interface TownmanagerEventHandler {

	/**
	 * Handles the player teleport event for the townsystem.
	 * 
	 * @param event
	 */
	public void handlePlayerTeleport(PlayerTeleportEvent event);
	
	/**
	 * Handles the player join event for the townsystem.
	 * 
	 * @param event
	 */
	public void handlePlayerJoin(PlayerJoinEvent event);
	
	/**
	 * Handles the player move event for the townsystem.
	 * 
	 * @param event
	 */
	public void handlerPlayerMove(PlayerMoveEvent event);
	
	/**
	 * Handles the open townmanager inventory event.
	 * 
	 * @param event
	 */
	public void handleOpenTownmanagerInventory(PlayerInteractEntityEvent event);
	
	/**
	 * Handles the open plot sale inventory event.
	 * 
	 * @param event
	 */
	public void handleOpenPlotSaleInventory(PlayerInteractEntityEvent event);
	
	/**
	 * Handles the townmanager and plot seller villager inventory click. TODO
	 * [UE-76]
	 * 
	 * @param event
	 */
	public void handleInventoryClick(InventoryClickEvent event);
	
	/**
	 * Handles the player interact event for the townsystem.
	 * 
	 * @param event
	 */
	public void handlePlayerInteract(PlayerInteractEvent event);
	
	
}
