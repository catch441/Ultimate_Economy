package com.ue.common.api;

import org.bukkit.inventory.ItemStack;

public interface CustomSkullService {

	/**
	 * Loads all needed custom skulls from minecraft.
	 */
	public void setup();
	
	/**
	 * Returns a custom skull with a custom name.
	 * 
	 * @param skullTexture
	 * @param name
	 * @return skull
	 */
	public ItemStack getSkullWithName(SkullTextureEnum skullTexture, String name);
}
