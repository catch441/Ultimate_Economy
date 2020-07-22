package com.ue.shopsystem.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.ShopSystemException;

public class ShopEditorHandler {

	private Inventory editor;
	private AbstractShopImpl shop;

	/**
	 * Constructor for a new shop editor handler.
	 * 
	 * @param shop
	 */
	public ShopEditorHandler(AbstractShopImpl shop) {
		this.shop = shop;
		setup(1);
	}

	/**
	 * Setup a new editor inventory.
	 * 
	 * @param reservedSlots
	 */
	public void setup(int reservedSlots) {
		editor = Bukkit.createInventory(getShop().getShopVillager(), getShop().getSize(),
				getShop().getName() + "-Editor");
		for (int i = 0; i < (getShop().getSize() - reservedSlots); i++) {
			// +1 for player readable
			getEditorInventory().setItem(i, CustomSkullService.getSkullWithName("SLOTEMPTY", "Slot " + (i + 1)));
		}
	}

	/**
	 * Handles a click in the shop inventory.
	 * 
	 * @param event
	 */
	public void handleInventoryClick(InventoryClickEvent event) {
		if(event.getRawSlot() < getShop().getSize()) {
			ItemMeta clickedItemMeta = event.getCurrentItem().getItemMeta();
			int slot = Integer.valueOf(clickedItemMeta.getDisplayName().substring(5));
			try {
				getShop().openSlotEditor((Player) event.getWhoClicked(), slot- 1);
			} catch (ShopSystemException | GeneralEconomyException e) {
			}
		}
	}

	/**
	 * Returns the editor inventory.
	 * 
	 * @return editor inventory
	 */
	public Inventory getEditorInventory() {
		return editor;
	}

	/**
	 * Changes the name of the editor inventory.
	 * 
	 * @param newName
	 */
	public void changeInventoryName(String newName) {
		Inventory editorNew = Bukkit.createInventory(getShop().getShopVillager(), getShop().getSize(),
				newName + "-Editor");
		editorNew.setContents(editor.getContents());
		editor = editorNew;
	}

	/**
	 * Set a slot as occupied or free.
	 * 
	 * @param occupied
	 * @param slot
	 */
	public void setOccupied(boolean occupied, int slot) {
		// +1 for player readable
		if (occupied) {
			getEditorInventory().setItem(slot, CustomSkullService.getSkullWithName("SLOTFILLED", "Slot " + (slot + 1)));
		} else {
			getEditorInventory().setItem(slot, CustomSkullService.getSkullWithName("SLOTEMPTY", "Slot " + (slot + 1)));
		}
	}

	private AbstractShopImpl getShop() {
		return shop;
	}
}
