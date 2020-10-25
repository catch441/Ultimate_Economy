package com.ue.shopsystem.logic.api;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.logic.impl.ShopSystemException;

public interface ShopSlotEditorHandler {

	/**
	 * Returns the slot editor inventory.
	 * 
	 * @return slot editor inventory
	 */
	public Inventory getSlotEditorInventory();
	
	/**
	 * Renames the slot editor inventory.
	 * 
	 * @param newName
	 */
	public void changeInventoryName(String newName);
	
	/**
	 * Set the selected editor slot.
	 * 
	 * @param slot
	 * @throws GeneralEconomyException
	 * @throws ShopSystemException
	 */
	public void setSelectedSlot(int slot) throws ShopSystemException, GeneralEconomyException;
	
	/**
	 * This method handles the SlotEditor for the InventoryClickEvent.
	 * 
	 * @param event
	 */
	public void handleSlotEditor(InventoryClickEvent event);
}
