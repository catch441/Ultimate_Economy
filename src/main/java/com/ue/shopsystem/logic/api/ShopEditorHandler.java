package com.ue.shopsystem.logic.api;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface ShopEditorHandler {

	/**
	 * Setup a new editor inventory.
	 * 
	 * @param reservedSlots
	 */
	public void setup(int reservedSlots);
	
	/**
	 * Handles a click in the shop inventory.
	 * 
	 * @param event
	 */
	public void handleInventoryClick(InventoryClickEvent event);
	
	/**
	 * Returns the editor inventory.
	 * 
	 * @return editor inventory
	 */
	public Inventory getEditorInventory();
	
	/**
	 * Changes the name of the editor inventory.
	 * 
	 * @param newName
	 */
	public void changeInventoryName(String newName);
	
	/**
	 * Set a slot as occupied or free.
	 * 
	 * @param occupied
	 * @param slot
	 */
	public void setOccupied(boolean occupied, int slot);
}
