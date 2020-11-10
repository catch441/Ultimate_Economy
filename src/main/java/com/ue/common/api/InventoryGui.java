package com.ue.common.api;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;

public interface InventoryGui {

	/**
	 * Setup a new gui inventory.
	 * @param invHolder
	 * @param size
	 * @param invName
	 */
	public void setup(InventoryHolder invHolder, int size, String invName);

	/**
	 * Add a new item with a meterial to the gui.
	 * @param slot
	 * @param lore
	 * @param name
	 * @param material
	 * @param amount
	 */
	public void setItem(int slot, List<String> lore, String name, Material material, int amount);

	/**
	 * Add a new item with a skull texture to the gui.
	 * @param slot
	 * @param lore
	 * @param name
	 * @param skullTexture
	 * @param amount
	 */
	public void setItem(int slot, List<String> lore, String name, SkullTextureEnum skullTexture, int amount);
}
