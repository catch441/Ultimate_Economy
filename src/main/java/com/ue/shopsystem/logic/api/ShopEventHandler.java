package com.ue.shopsystem.logic.api;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public interface ShopEventHandler {

	/**
	 * Handles the inventory click event.
	 * 
	 * @param event
	 */
	public void handleInventoryClick(InventoryClickEvent event);
	
	/**
	 * Handles open inventory for every shoptype.
	 * 
	 * @param event
	 */
	public void handleOpenInventory(PlayerInteractEntityEvent event);
}
