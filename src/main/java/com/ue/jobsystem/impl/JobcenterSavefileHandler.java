package com.ue.jobsystem.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.jobsystem.api.Job;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobcenterSavefileHandler {

	private File file;
	private YamlConfiguration config;

	protected JobcenterSavefileHandler(String name, boolean createFile) {
		file = new File(UltimateEconomy.getInstance.getDataFolder(), name + "-JobCenter.yml");
		if (createFile) {
			try {
				getSavefile().createNewFile();
			} catch (IOException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create savefile");
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		config = YamlConfiguration.loadConfiguration(getSavefile());
	}

	protected void saveJobcenterSize(int size) {
		getConfig().set("JobCenterSize", size);
		save();
	}

	protected void saveJobcenterName(String name) {
		getConfig().set("JobCenterName", name);
		save();
	}

	protected void saveJobcenterLocation(Location location) {
		getConfig().set("JobcenterLocation.x", location.getX());
		getConfig().set("JobcenterLocation.y", location.getY());
		getConfig().set("JobcenterLocation.z", location.getZ());
		getConfig().set("JobcenterLocation.World", location.getWorld().getName());
		save();
	}

	protected void saveJobNameList(List<String> jobNameList) {
		getConfig().set("Jobnames", jobNameList);
		save();
	}

	protected void saveJob(Job job, String itemMaterial, int slot) {
		if (itemMaterial == null) {
			getConfig().set("Jobs." + job.getName(), null);
		} else {
			getConfig().set("Jobs." + job.getName() + ".ItemMaterial", itemMaterial);
			getConfig().set("Jobs." + job.getName() + ".Slot", slot);
		}
		save();
	}

	protected void deleteSavefile() {
		getSavefile().delete();
	}

	private File getSavefile() {
		return file;
	}

	private void save() {
		try {
			getConfig().save(getSavefile());
		} catch (IOException e) {
			Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
			Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}

	protected int loadJobcenterSize() {
		return getConfig().getInt("JobCenterSize");
	}

	protected Location loadJobcenterLocation() {
		convertOldJobcenterLocation();
		return new Location(                      
				UltimateEconomy.getInstance.getServer().getWorld(getConfig().getString("JobcenterLocation.World")),
				getConfig().getDouble("JobcenterLocation.x"), getConfig().getDouble("JobcenterLocation.y"),
				getConfig().getDouble("JobcenterLocation.z"));
	}

	protected int loadJobSlot(Job job) {
		convertSlot(job.getName());
		return getConfig().getInt("Jobs." + job.getName() + ".Slot");
	}

	protected List<String> loadJobNameList() {
		return config.getStringList("Jobnames");
	}

	protected Material loadJobItemMaterial(Job job) {
		return Material.valueOf(config.getString("Jobs." + job.getName() + ".ItemMaterial"));
	}

	private YamlConfiguration getConfig() {
		return config;
	}

	/*
	 * Deprecated
	 * 
	 */

	@Deprecated
	private void convertOldJobcenterLocation() {
		if (getConfig().isSet("ShopLocation.World")) {
			Location location = new Location(
					UltimateEconomy.getInstance.getServer().getWorld(getConfig().getString("ShopLocation.World")),
					getConfig().getDouble("ShopLocation.x"), getConfig().getDouble("ShopLocation.y"),
					getConfig().getDouble("ShopLocation.z"));
			saveJobcenterLocation(location);
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void convertSlot(String jobName) {
		if (getConfig().contains("Jobs." + jobName + ".ItemSlot")) {
			int slot = config.getInt("Jobs." + jobName + ".ItemSlot");
			slot--;
			getConfig().set("Jobs." + jobName + ".ItemSlot", null);
			getConfig().set("Jobs." + jobName + ".Slot", slot);
		}
	}
}
