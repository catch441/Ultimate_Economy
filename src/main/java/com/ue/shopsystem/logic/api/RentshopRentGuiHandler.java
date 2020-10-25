package com.ue.shopsystem.logic.api;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface RentshopRentGuiHandler {

	/**
	 * Returns the rent gui inventory.
	 * 
	 * @return gui inventory
	 */
	public Inventory getRentGui();
	
	/**
	 * Handles a click in the rentshop GUI.
	 * 
	 * @param event
	 */
	public void handleRentShopGUIClick(InventoryClickEvent event);
}
