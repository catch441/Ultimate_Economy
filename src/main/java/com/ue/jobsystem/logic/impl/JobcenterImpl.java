package com.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.EconomyVillager;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;

public class JobcenterImpl implements Jobcenter {

	private static final Logger log = LoggerFactory.getLogger(JobcenterImpl.class);
	private final JobManager jobManager;
	private final JobcenterManager jobcenterManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobsystemValidationHandler validationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private final JobcenterDao jobcenterDao;
	private final ServerProvider serverProvider;
	private Villager villager;
	private Location location;
	private String name;
	private int size;
	private List<Job> jobs = new ArrayList<>();
	private Inventory inventory;

	@Inject
	public JobcenterImpl(JobcenterDao jobcenterDao, JobManager jobManager, JobcenterManager jobcenterManager,
			EconomyPlayerManager ecoPlayerManager, JobsystemValidationHandler validationHandler,
			ServerProvider serverProvider, GeneralEconomyValidationHandler generalValidator) {
		this.jobManager = jobManager;
		this.jobcenterManager = jobcenterManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.serverProvider = serverProvider;
		this.jobcenterDao = jobcenterDao;
		this.generalValidator = generalValidator;
	}

	@Override
	public void setupNew(String name, Location spawnLocation, int size) {
		jobcenterDao.setupSavefile(name);
		this.name = name;
		this.size = size;
		location = spawnLocation;
		jobcenterDao.saveJobcenterName(name);
		jobcenterDao.saveJobcenterSize(size);
		jobcenterDao.saveJobcenterLocation(location);
		setupVillager();
		inventory = serverProvider.createInventory(villager, size, getName());
		setupDefaultJobcenterInventory();
	}

	@Override
	public void setupExisting(String name) {
		jobcenterDao.setupSavefile(name);
		this.name = name;
		location = jobcenterDao.loadJobcenterLocation();
		size = jobcenterDao.loadJobcenterSize();
		setupVillager();
		inventory = serverProvider.createInventory(villager, size, getName());
		setupDefaultJobcenterInventory();
		loadJobs();
	}

	@Override
	public void addJob(Job job, String itemMaterial, int slot)
			throws EconomyPlayerException, GeneralEconomyException, JobSystemException {
		// -1 because of reserved slots
		generalValidator.checkForValidSlot(slot, size - 1);
		validationHandler.checkForFreeSlot(inventory, slot);
		validationHandler.checkForJobDoesNotExistInJobcenter(getJobList(), job);
		itemMaterial = itemMaterial.toUpperCase();
		validationHandler.checkForValidMaterial(itemMaterial);
		getJobList().add(job);
		jobcenterDao.saveJobNameList(getJobNameList());
		jobcenterDao.saveJob(job, itemMaterial, slot);
		ItemStack jobItem = serverProvider.createItemStack(Material.valueOf(itemMaterial), 1);
		ItemMeta meta = jobItem.getItemMeta();
		meta.setDisplayName(job.getName());
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		jobItem.setItemMeta(meta);
		inventory.setItem(slot, jobItem);
	}

	@Override
	public void removeJob(Job job) throws JobSystemException {
		validationHandler.checkForJobExistsInJobcenter(getJobList(), job);
		inventory.clear(getJobSlot(job));
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
	public void moveJobcenter(Location location) {
		villager.teleport(location);
		this.location = location;
		jobcenterDao.saveJobcenterLocation(getJobcenterLocation());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void despawnVillager() {
		villager.remove();
	}

	@Override
	public void deleteJobcenter() {
		jobcenterDao.deleteSavefile();
		World world = getJobcenterLocation().getWorld();
		despawnVillager();
		world.save();
	}

	@Override
	public void openInv(Player player) {
		player.openInventory(inventory);
	}

	@Override
	public boolean hasJob(Job job) {
		if (getJobList().contains(job)) {
			return true;
		}
		return false;
	}

	@Override
	public Location getJobcenterLocation() {
		return location;
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

	private void setupDefaultJobcenterInventory() {
		int slot = inventory.getSize() - 1;
		ItemStack info = serverProvider.createItemStack(Material.ANVIL, 1);
		ItemMeta meta = info.getItemMeta();
		meta.setDisplayName("Info");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "Join");
		lore.add(ChatColor.GOLD + "Rightclick: " + ChatColor.RED + "Leave");
		meta.setLore(lore);
		info.setItemMeta(meta);
		inventory.setItem(slot, info);
	}

	private void setupVillager() {
		getJobcenterLocation().getChunk().load();
		Collection<Entity> entitys = getJobcenterLocation().getWorld().getNearbyEntities(getJobcenterLocation(), 10, 10,
				10);
		for (Entity e : entitys) {
			if (getName().equals(e.getCustomName())) {
				e.remove();
			}
		}
		villager = (Villager) getJobcenterLocation().getWorld().spawnEntity(location, EntityType.VILLAGER);
		villager.setCustomName(name);
		villager.setMetadata("ue-type",
				new FixedMetadataValue(serverProvider.getJavaPluginInstance(), EconomyVillager.JOBCENTER));
		villager.setCustomNameVisible(true);
		villager.setProfession(Profession.NITWIT);
		villager.setSilent(true);
		villager.setCollidable(false);
		villager.setInvulnerable(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
	}

	private void loadJobs() {
		for (String jobName : jobcenterDao.loadJobNameList()) {
			try {
				Job job = jobManager.getJobByName(jobName);
				getJobList().add(job);
				ItemStack jobItem = serverProvider.createItemStack(jobcenterDao.loadJobItemMaterial(job), 1);
				ItemMeta meta = jobItem.getItemMeta();
				meta.setDisplayName(job.getName());
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				jobItem.setItemMeta(meta);
				inventory.setItem(jobcenterDao.loadJobSlot(job), jobItem);
			} catch (GeneralEconomyException e) {
				log.warn("[Ultimate_Economy] Failed to load the job " + jobName + " for the jobcenter " + getName());
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}
}
