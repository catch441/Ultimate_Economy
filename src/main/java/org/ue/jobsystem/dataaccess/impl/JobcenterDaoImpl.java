package org.ue.jobsystem.dataaccess.impl;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ue.common.utils.SaveFileUtils;
import org.ue.common.utils.ServerProvider;
import org.ue.jobsystem.dataaccess.api.JobcenterDao;
import org.ue.jobsystem.logic.api.Job;

public class JobcenterDaoImpl extends SaveFileUtils implements JobcenterDao {

	private final ServerProvider serverProvider;
	
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
		config.set("JobCenterSize", size);
		save();
	}

	@Override
	public void saveJobcenterName(String name) {
		config.set("JobCenterName", name);
		save();
	}

	@Override
	public void saveJobcenterLocation(Location location) {
		config.set("JobcenterLocation.x", location.getX());
		config.set("JobcenterLocation.y", location.getY());
		config.set("JobcenterLocation.z", location.getZ());
		config.set("JobcenterLocation.World", location.getWorld().getName());
		save();
	}

	@Override
	public void saveJobNameList(List<String> jobNameList) {
		config.set("Jobnames", jobNameList);
		save();
	}

	@Override
	public void saveJob(Job job, String itemMaterial, int slot) {
		if (itemMaterial == null) {
			config.set("Jobs." + job.getName(), null);
		} else {
			config.set("Jobs." + job.getName() + ".ItemMaterial", itemMaterial);
			config.set("Jobs." + job.getName() + ".Slot", slot);
		}
		save();
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
		return config.getInt("JobCenterSize");
	}

	@Override
	public Location loadJobcenterLocation() {
		convertOldJobcenterLocation();
		return new Location(serverProvider.getWorld(config.getString("JobcenterLocation.World")),
				config.getDouble("JobcenterLocation.x"), config.getDouble("JobcenterLocation.y"),
				config.getDouble("JobcenterLocation.z"));
	}

	@Override
	public int loadJobSlot(Job job) {
		convertSlot(job.getName());
		return config.getInt("Jobs." + job.getName() + ".Slot");
	}

	@Override
	public List<String> loadJobNameList() {
		return config.getStringList("Jobnames");
	}

	@Override
	public Material loadJobItemMaterial(Job job) {
		return Material.valueOf(config.getString("Jobs." + job.getName() + ".ItemMaterial"));
	}

	@Deprecated
	private void convertOldJobcenterLocation() {
		if (config.isSet("ShopLocation.World")) {
			Location location = new Location(serverProvider.getWorld(config.getString("ShopLocation.World")),
					config.getDouble("ShopLocation.x"), config.getDouble("ShopLocation.y"),
					config.getDouble("ShopLocation.z"));
			config.set("ShopLocation", null);
			save();
			saveJobcenterLocation(location);
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void convertSlot(String jobName) {
		if (config.contains("Jobs." + jobName + ".ItemSlot")) {
			int slot = config.getInt("Jobs." + jobName + ".ItemSlot");
			slot--;
			config.set("Jobs." + jobName + ".ItemSlot", null);
			config.set("Jobs." + jobName + ".Slot", slot);
			save();
		}
	}
}
