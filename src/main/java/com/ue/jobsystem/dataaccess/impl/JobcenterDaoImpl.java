package com.ue.jobsystem.dataaccess.impl;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.SaveFileUtils;
import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.jobsystem.logic.api.Job;

public class JobcenterDaoImpl extends SaveFileUtils implements JobcenterDao {

	private final ServerProvider serverProvider;
	
	/**
	 * Inject constructor.
	 * 
	 * @param serverProvider
	 * @param logger
	 */
	@Inject
	public JobcenterDaoImpl(ServerProvider serverProvider) {
		this.serverProvider = serverProvider;
	}

	@Override
	public void setupSavefile(String name) {
		file = new File(serverProvider.getDataFolderPath(), name + "-JobCenter.yml");
		if (!file.exists()) {
			createFile(file);
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
		return new Location(serverProvider.getWorld(getConfig().getString("JobcenterLocation.World")),
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

	@Deprecated
	private void convertOldJobcenterLocation() {
		if (getConfig().isSet("ShopLocation.World")) {
			Location location = new Location(serverProvider.getWorld(getConfig().getString("ShopLocation.World")),
					getConfig().getDouble("ShopLocation.x"), getConfig().getDouble("ShopLocation.y"),
					getConfig().getDouble("ShopLocation.z"));
			getConfig().set("ShopLocation", null);
			save(getConfig(), getSavefile());
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
			save(getConfig(), getSavefile());
		}
	}
}
