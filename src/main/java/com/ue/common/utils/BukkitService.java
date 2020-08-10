package com.ue.common.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class BukkitService {

	/**
	 * Returns a bukkit world or nullm if no world with the given name exists.
	 * @param world
	 * @return world
	 */
	public World getWorld(String world) {
		return Bukkit.getWorld(world);
	}
}
