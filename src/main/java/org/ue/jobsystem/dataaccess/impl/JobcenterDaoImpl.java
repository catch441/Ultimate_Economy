package org.ue.jobsystem.dataaccess.impl;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ue.common.dataaccess.impl.EconomyVillagerDaoImpl;
import org.ue.common.utils.ServerProvider;
import org.ue.jobsystem.dataaccess.api.JobcenterDao;
import org.ue.jobsystem.logic.api.Job;

public class JobcenterDaoImpl extends EconomyVillagerDaoImpl implements JobcenterDao {

	@Inject
	public JobcenterDaoImpl(ServerProvider serverProvider) {
		super(serverProvider);
	}

	@Override
	public void setupSavefile(String name) {
		file = new File(serverProvider.getDataFolderPath(), name + "-JobCenter.yml");
		if (!file.exists()) {
			createFile(file);
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public void saveJobcenterName(String name) {
		config.set("JobCenterName", name);
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
		file.delete();
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
