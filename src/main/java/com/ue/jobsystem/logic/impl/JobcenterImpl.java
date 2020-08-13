package com.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

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

	@Inject
	JobManager jobManager;
	@Inject
	JobcenterManager jobcenterManager;
	@Inject
	EconomyPlayerManager ecoPlayerManager;
	@Inject
	JobsystemValidationHandler validationHandler;
	@Inject
	JobcenterDao jobcenterDao;
	@Inject
	JobcenterDao savefileHandler;
	private Villager villager;
	private Location location;
	private String name;
	private int size;
	private List<Job> jobs = new ArrayList<>();
	private Inventory inventory;

	/**
	 * Constructor for creating a new jobcenter.
	 * 
	 * @param name
	 * @param spawnLocation
	 * @param size
	 * @throws JobSystemException
	 */
	public JobcenterImpl(String name, Location spawnLocation, int size) throws JobSystemException {
		savefileHandler.setupSavefile(name);
		setupNewJobcenter(name, spawnLocation, size);
	}

	/**
	 * Constructor for loading an existing jobcenter.
	 * 
	 * @param name
	 */
	public JobcenterImpl(String name) {
		savefileHandler.setupSavefile(name);
		loadExistingJobcenter(name);
	}

	/*
	 * API methods
	 * 
	 */

	@Override
	public void addJob(Job job, String itemMaterial, int slot)
			throws EconomyPlayerException, GeneralEconomyException, JobSystemException {
		getValidationHandler().checkForValidSlot(slot, getSize());
		getValidationHandler().checkForFreeSlot(getInventory(), slot);
		getValidationHandler().checkForJobDoesNotExistInJobcenter(getJobList(), job);
		itemMaterial = itemMaterial.toUpperCase();
		getValidationHandler().checkForValidMaterial(itemMaterial);
		getJobList().add(job);
		getSavefileHandler().saveJobNameList(getJobNameList());
		getSavefileHandler().saveJob(job, itemMaterial, slot);
		ItemStack jobItem = new ItemStack(Material.valueOf(itemMaterial));
		ItemMeta meta = jobItem.getItemMeta();
		meta.setDisplayName(job.getName());
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		jobItem.setItemMeta(meta);
		getInventory().setItem(slot, jobItem);
	}

	@Override
	public void removeJob(Job job) throws JobSystemException {
		getValidationHandler().checkForJobExistsInJobcenter(getJobList(), job);
		getInventory().clear(getJobSlot(job));
		getJobList().remove(job);
		getSavefileHandler().saveJob(job, null, 0);
		getSavefileHandler().saveJobNameList(getJobNameList());
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
		getVillager().teleport(location);
		this.location = location;
		getSavefileHandler().saveJobcenterLocation(getJobcenterLocation());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void despawnVillager() {
		getVillager().remove();
	}

	@Override
	public void deleteJobcenter() {
		getSavefileHandler().deleteSavefile();
		World world = getJobcenterLocation().getWorld();
		getVillager().remove();
		world.save();
	}

	@Override
	public void openInv(Player player) {
		player.openInventory(getInventory());
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

	/*
	 * Utility methods
	 * 
	 */

	private JobcenterDao getSavefileHandler() {
		return savefileHandler;
	}

	private JobsystemValidationHandler getValidationHandler() {
		return validationHandler;
	}

	private void setName(String name) {
		this.name = name;
	}

	private Villager getVillager() {
		return villager;
	}

	private Inventory getInventory() {
		return inventory;
	}

	private int getSize() {
		return size;
	}

	private List<String> getJobNameList() {
		List<String> list = new ArrayList<>();
		for (Job job : getJobList()) {
			list.add(job.getName());
		}
		return list;
	}

	private int getJobSlot(Job job) {
		return getSavefileHandler().loadJobSlot(job);
	}

	private boolean isJAvailableInOtherJobcenter(Job job) throws JobSystemException {
		for (Jobcenter jobcenter : jobcenterManager.getJobcenterList()) {
			if (jobcenter.hasJob(job)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupNewJobcenter(String name, Location spawnLocation, int size) {
		setupJobcenterName(name);
		setupJobcenterSize(size);
		setupJobcenterLocation(spawnLocation);
		setupVillager();
		setupInventory();
	}

	private void setupJobcenterSize(int size) {
		this.size = size;
		getSavefileHandler().saveJobcenterSize(size);
	}

	private void setupInventory() {
		inventory = Bukkit.createInventory(getVillager(), getSize(), getName());
		setupDefaultJobcenterInventory();
	}

	private void setupJobcenterName(String name) {
		setName(name);
		getSavefileHandler().saveJobcenterName(getName());
	}

	private void setupJobcenterLocation(Location spawnLocation) {
		location = spawnLocation;
		getSavefileHandler().saveJobcenterLocation(getJobcenterLocation());
	}

	private void setupDefaultJobcenterInventory() {
		int slot = getInventory().getSize() - 1;
		ItemStack info = new ItemStack(Material.ANVIL);
		ItemMeta meta = info.getItemMeta();
		meta.setDisplayName("Info");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "Join");
		lore.add(ChatColor.GOLD + "Rightclick: " + ChatColor.RED + "Leave");
		meta.setLore(lore);
		info.setItemMeta(meta);
		getInventory().setItem(slot, info);
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
		getVillager().setCustomName(name);
		getVillager().setMetadata("ue-type",
				new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.JOBCENTER));
		getVillager().setCustomNameVisible(true);
		getVillager().setProfession(Villager.Profession.NITWIT);
		getVillager().setSilent(true);
		getVillager().setCollidable(false);
		getVillager().setInvulnerable(true);
		getVillager().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
		getVillager().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000, 30000000));
	}

	/*
	 * Loading methods
	 * 
	 */

	private void loadExistingJobcenter(String name) {
		setName(name);
		location = getSavefileHandler().loadJobcenterLocation();
		size = getSavefileHandler().loadJobcenterSize();
		setupVillager();
		setupInventory();
		loadJobs();
	}

	private void loadJobs() {
		for (String jobName : getSavefileHandler().loadJobNameList()) {
			try {
				Job job = jobManager.getJobByName(jobName);
				getJobList().add(job);
				ItemStack jobItem = new ItemStack(getSavefileHandler().loadJobItemMaterial(job));
				ItemMeta meta = jobItem.getItemMeta();
				meta.setDisplayName(job.getName());
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				jobItem.setItemMeta(meta);
				getInventory().setItem(getSavefileHandler().loadJobSlot(job), jobItem);
			} catch (GeneralEconomyException e) {
				Bukkit.getLogger().warning(
						"[Ultimate_Economy] Failed to load the job " + jobName + " for the jobcenter " + getName());
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}
}
