package com.ue.shopsystem.logic.api;

import org.bukkit.inventory.ItemStack;

public interface CustomSkullService {

	/**
	 * Loads all needed custom skulls from minecraft.
	 */
	public void setup();
	
	/**
	 * Returns a custom skull with a custom name.
	 * 
	 * @param skull
	 * @param name
	 * @return skull
	 */
	public ItemStack getSkullWithName(String skull, String name);
}
