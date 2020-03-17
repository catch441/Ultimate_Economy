package com.ue.jobsystem.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.JobExceptionMessageEnum;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobcenterImpl implements Jobcenter {

    private File file;
    private Villager villager;
    private Location location;
    private String name;
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
	file = new File(UltimateEconomy.getInstance.getDataFolder(), name + "-JobCenter.yml");
	try {
	    file.createNewFile();
	    setupNewJobcenter(name, spawnLocation, size);
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create savefile");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }

    /**
     * Constructor for loading an existing jobcenter.
     * 
     * @param name
     */
    public JobcenterImpl(String name) {
	file = new File(UltimateEconomy.getInstance.getDataFolder(), name + "-JobCenter.yml");
	loadExistingJobcenter(name);
    }

    private void setName(String name) {
	this.name = name;
    }

    @Override
    public void addJob(Job job, String itemMaterial, int slot)
	    throws JobSystemException, GeneralEconomyException, PlayerException {
	checkForValidSlot(slot);
	checkForFreeSlot(slot);
	checkForJobDoesNotExistInJobcenter(job);
	itemMaterial = itemMaterial.toUpperCase();
	checkForValidMaterial(itemMaterial);
	getJobList().add(job);
	saveJobNameList();
	saveJob(job, itemMaterial, slot);
	ItemStack jobItem = new ItemStack(Material.valueOf(itemMaterial));
	ItemMeta meta = jobItem.getItemMeta();
	meta.setDisplayName(job.getName());
	meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
	jobItem.setItemMeta(meta);
	inventory.setItem(slot - 1, jobItem);
    }

    @Override
    public void removeJob(Job job) throws JobSystemException {
	checkForJobExistsInJobcenter(job);
	inventory.clear(getJobSlot(job) - 1);
	jobs.remove(job);
	saveJob(job, null, 0);
	saveJobNameList();
	if (isJAvailableInOtherJobcenter(job)) {
	    for (EconomyPlayer ecoPlayer : EconomyPlayerController.getAllEconomyPlayers()) {
		if (ecoPlayer.hasJob(job)) {
		    try {
			ecoPlayer.leaveJob(job, false);
		    } catch (PlayerException e) {
		    }
		}
	    }
	}
    }

    private int getJobSlot(Job job) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	return config.getInt("Jobs." + job.getName() + ".ItemSlot");
    }

    private boolean isJAvailableInOtherJobcenter(Job job) throws JobSystemException {
	for (Jobcenter jobcenter : JobcenterController.getJobCenterList()) {
	    if (jobcenter.hasJob(job)) {
		return true;
	    }
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

    @Override
    public List<Job> getJobList() {
	return jobs;
    }

    @Override
    public void moveJobCenter(Location location) {
	saveJobcenterLocation(location);
	villager.teleport(location);
    }

    @Override
    public String getName() {
	return name;
    }

    private void save(FileConfiguration config) {
	try {
	    config.save(file);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void despawnVillager() {
	villager.remove();
    }

    @Override
    public void deleteJobCenter() {
	file.delete();
	World world = villager.getLocation().getWorld();
	villager.remove();
	world.save();
    }

    @Override
    public void openInv(Player player) {
	player.openInventory(inventory);
    }

    @Override
    public boolean hasJob(Job job) throws JobSystemException {
	if (getJobList().contains(job)) {
	    return true;
	}
	return false;
    }

    private boolean isSlotEmpty(int slot) {
	slot--;
	boolean isEmpty = false;
	if (inventory.getItem(slot) == null) {
	    isEmpty = true;
	}
	return isEmpty;
    }

    /*
     * Save methods
     * 
     */

    private void saveJobcenterSize(int size) {
	FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	config.set("JobCenterSize", size);
	save(config);
    }

    private void saveJobcenterName(String name) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	config.set("JobCenterName", name);
	save(config);
    }

    private void saveJobcenterLocation(Location location) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	config.set("JobcenterLocation.x", location.getX());
	config.set("JobcenterLocation.y", location.getY());
	config.set("JobcenterLocation.z", location.getZ());
	config.set("JobcenterLocation.World", location.getWorld().getName());
	save(config);
    }

    private void saveJobNameList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	config.set("Jobnames", getJobNameList());
	save(config);
    }

    private void saveJob(Job job, String itemMaterial, int slot) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	if (itemMaterial == null) {
	    config.set("Jobs." + job.getName(), null);
	} else {
	    config.set("Jobs." + job.getName() + ".ItemMaterial", itemMaterial);
	    config.set("Jobs." + job.getName() + ".ItemSlot", slot);
	}
	save(config);
    }

    /*
     * Setup methods
     * 
     */

    private void setupNewJobcenter(String name, Location spawnLocation, int size) {
	setupJobcenterName(name);
	saveJobcenterSize(size);
	setupJobcenterLocation(spawnLocation);
	setupVillager();
	setupInventory(size);
	setupJobcenterInventory();
    }

    private void setupInventory(int size) {
	inventory = Bukkit.createInventory(villager, size, getName());
    }

    private void setupJobcenterName(String name) {
	setName(name);
	saveJobcenterName(name);
    }

    private void setupJobcenterLocation(Location spawnLocation) {
	location = spawnLocation;
	saveJobcenterLocation(spawnLocation);
    }

    private void setupJobcenterInventory() {
	int slot = inventory.getSize();
	ItemStack info = new ItemStack(Material.ANVIL);
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
	location.getChunk().load();
	Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 10, 10, 10);
	for (Entity e : entitys) {
	    if (e.getName().equals(name)) {
		e.remove();
	    }
	}
	villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
	villager.setCustomName(name);
	villager.setMetadata("ue-type", new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.JOBCENTER));
	villager.setCustomNameVisible(true);
	villager.setProfession(Villager.Profession.NITWIT);
	villager.setSilent(true);
  villager.setCollidable(false);
  villager.setInvulnerable(true);
	villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
	villager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000, 30000000));
    }

    /*
     * Loading methods
     * 
     */

    private void loadExistingJobcenter(String name) {
	setName(name);
	loadJobcenterLocation();
	setupVillager();
	loadInventory();
	loadJobs();
	setupJobcenterInventory();
    }

    private void loadInventory() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	inventory = Bukkit.createInventory(villager, config.getInt("JobCenterSize"), getName());
    }

    private void loadJobcenterLocation() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	if (config.isSet("ShopLocation.World")) {
	    oldLoadJobcenterLocation(config);
	    saveJobcenterLocation(location);
	} else {
	    location = new Location(
		    UltimateEconomy.getInstance.getServer().getWorld(config.getString("JobcenterLocation.World")),
		    config.getDouble("JobcenterLocation.x"), config.getDouble("JobcenterLocation.y"),
		    config.getDouble("JobcenterLocation.z"));
	}
    }

    @Deprecated
    private void oldLoadJobcenterLocation(YamlConfiguration config) {
	location = new Location(
		UltimateEconomy.getInstance.getServer().getWorld(config.getString("ShopLocation.World")),
		config.getDouble("ShopLocation.x"), config.getDouble("ShopLocation.y"),
		config.getDouble("ShopLocation.z"));
    }

    private void loadJobs() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	for (String jobName : config.getStringList("Jobnames")) {
	    try {
		Job job = JobController.getJobByName(jobName);
		getJobList().add(job);
		ItemStack jobItem = new ItemStack(
			Material.valueOf(config.getString("Jobs." + job.getName() + ".ItemMaterial")));
		ItemMeta meta = jobItem.getItemMeta();
		meta.setDisplayName(job.getName());
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		jobItem.setItemMeta(meta);
		inventory.setItem(config.getInt("Jobs." + job.getName() + ".ItemSlot") - 1, jobItem);
	    } catch (GeneralEconomyException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the job " + jobName
			+ " for the jobcenter " + getName());
		Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	    }
	}
    }

    /*
     * Validation check methods
     * 
     */

    private void checkForValidSlot(int slot) throws GeneralEconomyException {
	if (slot < 0 || slot > inventory.getSize()) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, slot);
	}
    }

    private void checkForFreeSlot(int slot) throws PlayerException {
	if (!isSlotEmpty(slot)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED);
	}
    }

    private void checkForValidMaterial(String itemMaterial) throws GeneralEconomyException {
	if (Material.matchMaterial(itemMaterial) == null) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
		    itemMaterial);
	}
    }

    private void checkForJobDoesNotExistInJobcenter(Job job) throws JobSystemException {
	if (getJobList().contains(job)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.JOB_ALREADY_EXIST_IN_JOBCENTER);
	}
    }

    private void checkForJobExistsInJobcenter(Job job) throws JobSystemException {
	if (!getJobList().contains(job)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER);
	}
    }
}
