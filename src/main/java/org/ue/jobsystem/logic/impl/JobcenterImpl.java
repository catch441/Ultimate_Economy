package org.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.logic.api.EconomyVillagerType;
import org.ue.common.logic.impl.EconomyVillagerImpl;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.jobsystem.dataaccess.api.JobcenterDao;
import org.ue.jobsystem.logic.JobSystemException;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemValidationHandler;
import org.ue.townsystem.logic.TownSystemException;

public class JobcenterImpl extends EconomyVillagerImpl implements Jobcenter {

	private static final Logger log = LoggerFactory.getLogger(JobcenterImpl.class);
	private final JobManager jobManager;
	private final JobcenterManager jobcenterManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobsystemValidationHandler validationHandler;
	private final JobcenterDao jobcenterDao;
	private String name;
	private List<Job> jobs = new ArrayList<>();

	@Inject
	public JobcenterImpl(JobcenterDao jobcenterDao, JobManager jobManager, JobcenterManager jobcenterManager,
			EconomyPlayerManager ecoPlayerManager, JobsystemValidationHandler validationHandler,
			ServerProvider serverProvider) {
		super(serverProvider, jobcenterDao, validationHandler);
		this.jobManager = jobManager;
		this.jobcenterManager = jobcenterManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.jobcenterDao = jobcenterDao;
	}

	@Override
	public void setupNew(String name, Location spawnLocation, int size)
			throws GeneralEconomyException, EconomyPlayerException {
		jobcenterDao.setupSavefile(name);
		this.name = name;
		jobcenterDao.saveJobcenterName(name);
		setupNewEconomyVillager(spawnLocation, EconomyVillagerType.JOBCENTER, name, size, 1);
		setupDefaultJobcenterInventory();
	}

	@Override
	public void setupExisting(String name) throws TownSystemException, GeneralEconomyException, EconomyPlayerException {
		jobcenterDao.setupSavefile(name);
		this.name = name;
		setupExistingEconomyVillager(EconomyVillagerType.JOBCENTER, name, 1);
		setupDefaultJobcenterInventory();
		loadJobs();
	}

	@Override
	public void addJob(Job job, String itemMaterial, int slot)
			throws EconomyPlayerException, GeneralEconomyException, JobSystemException {
		validationHandler.checkForJobDoesNotExistInJobcenter(getJobList(), job);
		itemMaterial = itemMaterial.toUpperCase();
		validationHandler.checkForValidEnum(Material.values(), itemMaterial);

		ItemStack jobItem = serverProvider.createItemStack(Material.valueOf(itemMaterial), 1);
		ItemMeta meta = jobItem.getItemMeta();
		meta.setDisplayName(job.getName());
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		jobItem.setItemMeta(meta);
		// throws an exception if the slot is occupied
		addItemStack(jobItem, slot, false, false);

		getJobList().add(job);
		jobcenterDao.saveJobNameList(getJobNameList());
		jobcenterDao.saveJob(job, itemMaterial, slot);
	}

	@Override
	public void removeJob(Job job) throws JobSystemException {
		validationHandler.checkForJobExistsInJobcenter(getJobList(), job);
		getInventory().clear(getJobSlot(job));
		getJobList().remove(job);
		jobcenterDao.saveJob(job, null, 0);
		jobcenterDao.saveJobNameList(getJobNameList());
		if (!isJAvailableInOtherJobcenter(job)) {
			for (EconomyPlayer ecoPlayer : ecoPlayerManager.getAllEconomyPlayers()) {
				if (ecoPlayer.hasJob(job)) {
					try {
						ecoPlayer.leaveJob(job, false);
					} catch (EconomyPlayerException e) {
						log.warn("[Ultimate_Economy] Failed to leave the job " + job.getName());
						log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public List<Job> getJobList() {
		return jobs;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void deleteJobcenter() {
		jobcenterDao.deleteSavefile();
		despawn();
		getLocation().getWorld().save();
	}

	@Override
	public boolean hasJob(Job job) {
		if (getJobList().contains(job)) {
			return true;
		}
		return false;
	}

	private List<String> getJobNameList() {
		List<String> list = new ArrayList<>();
		for (Job job : getJobList()) {
			list.add(job.getName());
		}
		return list;
	}

	private int getJobSlot(Job job) {
		return jobcenterDao.loadJobSlot(job);
	}

	private boolean isJAvailableInOtherJobcenter(Job job) throws JobSystemException {
		for (Jobcenter jobcenter : jobcenterManager.getJobcenterList()) {
			if (jobcenter.hasJob(job)) {
				return true;
			}
		}
		return false;
	}

	private void setupDefaultJobcenterInventory() throws GeneralEconomyException, EconomyPlayerException {
		int slot = getSize() - 1;
		ItemStack info = serverProvider.createItemStack(Material.ANVIL, 1);
		ItemMeta meta = info.getItemMeta();
		meta.setDisplayName("Info");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "Join");
		lore.add(ChatColor.GOLD + "Rightclick: " + ChatColor.RED + "Leave");
		meta.setLore(lore);
		info.setItemMeta(meta);
		addItemStack(info, slot, true, true);
	}

	private void loadJobs() {
		for (String jobName : jobcenterDao.loadJobNameList()) {
			try {
				Job job = jobManager.getJobByName(jobName);
				ItemStack jobItem = serverProvider.createItemStack(jobcenterDao.loadJobItemMaterial(job), 1);
				ItemMeta meta = jobItem.getItemMeta();
				meta.setDisplayName(job.getName());
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				jobItem.setItemMeta(meta);
				addItemStack(jobItem, jobcenterDao.loadJobSlot(job), false, true);
				getJobList().add(job);
			} catch (GeneralEconomyException | EconomyPlayerException e) {
				log.warn("[Ultimate_Economy] Failed to load the job " + jobName + " for the jobcenter " + getName());
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}
}
