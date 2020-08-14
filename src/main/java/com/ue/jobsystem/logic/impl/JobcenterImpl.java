package com.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ue.common.utils.BukkitService;
import com.ue.common.utils.DaggerServiceComponent;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.ultimate_economy.EconomyVillager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobcenterImpl implements Jobcenter {

	private final JobManager jobManager;
	private final JobcenterManager jobcenterManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final JobsystemValidationHandler validationHandler;
	private final JobcenterDao jobcenterDao;
	private final BukkitService bukkitService;
	private Villager villager;
	private Location location;
	private String name;
	private int size;
	private List<Job> jobs = new ArrayList<>();
	private Inventory inventory;

	/**
	 * Constructor for creating a new jobcenter.
	 * 
	 * @param jobManager
	 * @param jobcenterManager
	 * @param ecoPlayerManager
	 * @param validationHandler
	 * @param bukkitService
	 * @param name
	 * @param spawnLocation
	 * @param size
	 * @throws JobSystemException
	 */
	public JobcenterImpl(JobManager jobManager, JobcenterManager jobcenterManager,
			EconomyPlayerManager ecoPlayerManager, JobsystemValidationHandler validationHandler,
			BukkitService bukkitService, String name, Location spawnLocation, int size) throws JobSystemException {
		this.jobManager = jobManager;
		this.jobcenterManager = jobcenterManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.bukkitService = bukkitService;
		jobcenterDao = DaggerServiceComponent.builder().build().getJobcenterDao();
		jobcenterDao.setupSavefile(name);
		setupNewJobcenter(name, spawnLocation, size);
	}

	/**
	 * Constructor for loading an existing jobcenter.
	 * 
	 * @param jobManager
	 * @param jobcenterManager
	 * @param ecoPlayerManager
	 * @param validationHandler
	 * @param bukkitService
	 * @param name
	 */
	public JobcenterImpl(JobManager jobManager, JobcenterManager jobcenterManager,
			EconomyPlayerManager ecoPlayerManager, JobsystemValidationHandler validationHandler,
			BukkitService bukkitService, String name) {
		this.jobManager = jobManager;
		this.jobcenterManager = jobcenterManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.validationHandler = validationHandler;
		this.bukkitService = bukkitService;
		jobcenterDao = DaggerServiceComponent.builder().build().getJobcenterDao();
		jobcenterDao.setupSavefile(name);
		loadExistingJobcenter(name);
	}

	@Override
	public void addJob(Job job, String itemMaterial, int slot)
			throws EconomyPlayerException, GeneralEconomyException, JobSystemException {
		validationHandler.checkForValidSlot(slot, size);
		validationHandler.checkForFreeSlot(inventory, slot);
		validationHandler.checkForJobDoesNotExistInJobcenter(getJobList(), job);
		itemMaterial = itemMaterial.toUpperCase();
		validationHandler.checkForValidMaterial(itemMaterial);
		getJobList().add(job);
		jobcenterDao.saveJobNameList(getJobNameList());
		jobcenterDao.saveJob(job, itemMaterial, slot);
		ItemStack jobItem = new ItemStack(Material.valueOf(itemMaterial));
		ItemMeta meta = bukkitService.getItemMeta(jobItem);
		meta.setDisplayName(job.getName());
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		bukkitService.setItemMeta(jobItem, meta);
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
						Bukkit.getLogger().warning("[Ultimate_Economy] Failed to leave the job " + job.getName());
						Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
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

	private void setupNewJobcenter(String name, Location spawnLocation, int size) {
		this.name = name;
		this.size = size;
		System.out.println(size);
		location = spawnLocation;
		jobcenterDao.saveJobcenterName(name);
		jobcenterDao.saveJobcenterSize(size);
		jobcenterDao.saveJobcenterLocation(location);
		inventory = bukkitService.createInventory(villager, size, getName());
		setupDefaultJobcenterInventory();
		setupVillager();
	}

	private void setupDefaultJobcenterInventory() {
		int slot = inventory.getSize() - 1;
		ItemStack info = new ItemStack(Material.ANVIL);
		ItemMeta meta = bukkitService.getItemMeta(info);
		meta.setDisplayName("Info");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "Join");
		lore.add(ChatColor.GOLD + "Rightclick: " + ChatColor.RED + "Leave");
		meta.setLore(lore);
		bukkitService.setItemMeta(info, meta);
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
		villager.setMetadata("ue-type", new FixedMetadataValue(bukkitService.getPluginInstance(), EconomyVillager.JOBCENTER));
		villager.setCustomNameVisible(true);
		villager.setProfession(Villager.Profession.NITWIT);
		villager.setSilent(true);
		villager.setCollidable(false);
		villager.setInvulnerable(true);
		villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
		villager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000, 30000000));
	}

	private void loadExistingJobcenter(String name) {
		this.name = name;
		location = jobcenterDao.loadJobcenterLocation();
		size = jobcenterDao.loadJobcenterSize();
		setupVillager();
		inventory = bukkitService.createInventory(villager, size, getName());
		setupDefaultJobcenterInventory();
		loadJobs();
	}

	private void loadJobs() {
		for (String jobName : jobcenterDao.loadJobNameList()) {
			try {
				Job job = jobManager.getJobByName(jobName);
				getJobList().add(job);
				ItemStack jobItem = new ItemStack(jobcenterDao.loadJobItemMaterial(job));
				ItemMeta meta = bukkitService.getItemMeta(jobItem);
				meta.setDisplayName(job.getName());
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				bukkitService.setItemMeta(jobItem, meta);
				inventory.setItem(jobcenterDao.loadJobSlot(job), jobItem);
			} catch (GeneralEconomyException e) {
				Bukkit.getLogger().warning(
						"[Ultimate_Economy] Failed to load the job " + jobName + " for the jobcenter " + getName());
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}
}
