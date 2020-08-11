package com.ue.common.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import com.ue.ultimate_economy.UltimateEconomy;

public class BukkitService {

	/**
	 * Returns a bukkit world or null, if no world with the given name exists.
	 * 
	 * @param world
	 * @return world
	 */
	public World getWorld(String world) {
		return Bukkit.getWorld(world);
	}

	/**
	 * Returns a bukkit player or null, if player is not online.
	 * 
	 * @param name
	 * @return
	 */
	public Player getPlayer(String name) {
		return Bukkit.getPlayer(name);
	}

	/**
	 * Returns a bukkit boss bar.
	 * 
	 * @return boss bar
	 */
	public BossBar createBossBar() {
		return Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
	}

	/**
	 * Returns the bukkit plugin data folder path.
	 * 
	 * @return path
	 */
	public String getDataFolderPath() {
		return UltimateEconomy.getInstance.getDataFolder().getPath();
	}

	/**
	 * Returns a new bukkit scoreboard.
	 * 
	 * @return scoreboard
	 */
	public Scoreboard createScoreBoard() {
		return Bukkit.getScoreboardManager().getNewScoreboard();
	}
}
