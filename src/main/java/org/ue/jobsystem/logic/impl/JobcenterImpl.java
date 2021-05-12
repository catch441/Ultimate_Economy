package org.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.economyvillager.logic.impl.EconomyVillagerImpl;
import org.ue.jobsystem.dataaccess.api.JobcenterDao;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemException;
import org.ue.jobsystem.logic.api.JobsystemValidator;

public class JobcenterImpl extends EconomyVillagerImpl<JobsystemException> implements Jobcenter {

	private static final Logger log = LoggerFactory.getLogger(JobcenterImpl.class);
	private final JobManager jobManager;
	private final JobcenterManager jobcenterManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobsystemValidator validationHandler;
	private final JobcenterDao jobcenterDao;
	private String name;
	private Map<Integer, Job> jobs = new HashMap<>();

	@Inject
	public JobcenterImpl(JobcenterDao jobcenterDao, JobManager jobManager, JobcenterManager jobcenterManager,
			EconomyPlayerManager ecoPlayerManager, JobsystemValidator validationHandler,
			ServerProvider serverProvider, CustomSkullService skullService, MessageWrapper messageWrapper) {
		super(messageWrapper, serverProvider, jobcenterDao, validationHandler, skullService, "");
		this.jobManager = jobManager;
		this.jobcenterManager = jobcenterManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.jobcenterDao = jobcenterDao;
	}

	@Override
	public void setupNew(String name, Location spawnLocation, int size) {
		jobcenterDao.setupSavefile(name);
		this.name = name;
		jobcenterDao.saveJobcenterName(name);
		setupNewEconomyVillager(spawnLocation, EconomyVillagerType.JOBCENTER, name, size, 1, true);
		setupDefaultJobcenterInventory();
	}

	@Override
	public void setupExisting(String name) {
		jobcenterDao.setupSavefile(name);
		this.name = name;
		setupExistingEconomyVillager(EconomyVillagerType.JOBCENTER, name, 1);
		setupDefaultJobcenterInventory();
		loadJobs();
	}

	@Override
	public void addJob(Job job, String itemMaterial, int slot) throws JobsystemException {
		validationHandler.checkForJobDoesNotExistInJobcenter(getJobList(), job);
		itemMaterial = itemMaterial.toUpperCase();
		validationHandler.checkForValidEnum(Material.values(), itemMaterial);
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		validationHandler.checkForSlotIsEmpty(jobs.keySet(), slot);

		ItemStack jobItem = serverProvider.createItemStack(Material.valueOf(itemMaterial), 1);
		ItemMeta meta = jobItem.getItemMeta();
		meta.setDisplayName(job.getName());
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		jobItem.setItemMeta(meta);
		getInventory().setItem(slot, jobItem);
		jobs.put(slot, job);
		jobcenterDao.saveJobNameList(getJobNameList());
		jobcenterDao.saveJob(job, itemMaterial, slot);
	}

	@Override
	public void removeJob(Job job) throws JobsystemException {
		validationHandler.checkForJobExistsInJobcenter(getJobList(), job);
		getInventory().clear(getJobSlot(job));
		jobs.values().removeIf(value -> value.equals(job));
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
		return new ArrayList<>(jobs.values());
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

	private boolean isJAvailableInOtherJobcenter(Job job) throws JobsystemException {
		for (Jobcenter jobcenter : jobcenterManager.getJobcenterList()) {
			if (jobcenter.hasJob(job)) {
				return true;
			}
		}
		return false;
	}

	private void setupDefaultJobcenterInventory() {
		int slot = getSize() - 1;
		ItemStack info = serverProvider.createItemStack(Material.ANVIL, 1);
		ItemMeta meta = info.getItemMeta();
		meta.setDisplayName("Info");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "Join");
		lore.add(ChatColor.GOLD + "Rightclick: " + ChatColor.RED + "Leave");
		meta.setLore(lore);
		info.setItemMeta(meta);
		getInventory().setItem(slot, info);
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
				int slot = jobcenterDao.loadJobSlot(job);
				getInventory().setItem(slot, jobItem);
				jobs.put(slot, job);
			} catch (JobsystemException e) {
				log.warn("[Ultimate_Economy] Failed to load the job " + jobName + " for the jobcenter " + getName());
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}
}
