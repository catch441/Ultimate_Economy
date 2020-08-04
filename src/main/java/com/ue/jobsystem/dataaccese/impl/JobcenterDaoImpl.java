package com.ue.jobsystem.dataaccese.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.common.utils.SaveFileUtils;
import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobcenterDaoImpl extends SaveFileUtils implements JobcenterDao {

	private File file;
	private YamlConfiguration config;

	/**
	 * Constructor for creating a new jonbcenter savefile handler.
	 * @param name
	 * @param createFile
	 */
	public JobcenterDaoImpl(String name, boolean createFile) {
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

	@Override
	public void saveJobcenterSize(int size) {
		getConfig().set("JobCenterSize", size);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveJobcenterName(String name) {
		getConfig().set("JobCenterName", name);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveJobcenterLocation(Location location) {
		getConfig().set("JobcenterLocation.x", location.getX());
		getConfig().set("JobcenterLocation.y", location.getY());
		getConfig().set("JobcenterLocation.z", location.getZ());
		getConfig().set("JobcenterLocation.World", location.getWorld().getName());
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveJobNameList(List<String> jobNameList) {
		getConfig().set("Jobnames", jobNameList);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveJob(Job job, String itemMaterial, int slot) {
		if (itemMaterial == null) {
			getConfig().set("Jobs." + job.getName(), null);
		} else {
			getConfig().set("Jobs." + job.getName() + ".ItemMaterial", itemMaterial);
			getConfig().set("Jobs." + job.getName() + ".Slot", slot);
		}
		save(getConfig(), getSavefile());
	}

	@Override
	public void deleteSavefile() {
		getSavefile().delete();
	}

	private File getSavefile() {
		return file;
	}

	@Override
	public int loadJobcenterSize() {
		return getConfig().getInt("JobCenterSize");
	}

	@Override
	public Location loadJobcenterLocation() {
		convertOldJobcenterLocation();
		return new Location(                      
				UltimateEconomy.getInstance.getServer().getWorld(getConfig().getString("JobcenterLocation.World")),
				getConfig().getDouble("JobcenterLocation.x"), getConfig().getDouble("JobcenterLocation.y"),
				getConfig().getDouble("JobcenterLocation.z"));
	}

	@Override
	public int loadJobSlot(Job job) {
		convertSlot(job.getName());
		return getConfig().getInt("Jobs." + job.getName() + ".Slot");
	}

	@Override
	public List<String> loadJobNameList() {
		return config.getStringList("Jobnames");
	}

	@Override
	public Material loadJobItemMaterial(Job job) {
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
