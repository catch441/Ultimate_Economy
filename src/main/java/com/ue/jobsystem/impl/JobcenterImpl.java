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
	file = new File(UltimateEconomy.getInstance.getDataFolder(), name + "-JobCenter.yml");
	try {
	    getSavefile().createNewFile();
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

    /*
     * API methods
     * 
     */

    @Override
    public void addJob(Job job, String itemMaterial, int slot)
	    throws PlayerException, GeneralEconomyException, JobSystemException {
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
	getInventory().setItem(slot, jobItem);
    }

    @Override
    public void removeJob(Job job) throws JobSystemException {
	checkForJobExistsInJobcenter(job);
	getInventory().clear(getJobSlot(job));
	getJobList().remove(job);
	saveJob(job, null, 0);
	saveJobNameList();
	if (isJAvailableInOtherJobcenter(job)) {
	    for (EconomyPlayer ecoPlayer : EconomyPlayerController.getAllEconomyPlayers()) {
		if (ecoPlayer.hasJob(job)) {
		    try {
			ecoPlayer.leaveJob(job, false);
		    } catch (PlayerException e) {
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
    public void moveJobCenter(Location location) {
	setupJobcenterLocation(location);
	saveJobcenterLocation();
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
    public void deleteJobCenter() {
	getSavefile().delete();
	World world = getLocation().getWorld();
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

    /*
     * Utility methods
     * 
     */

    private boolean isSlotEmpty(int slot) {
	if (getInventory().getItem(slot) == null || getInventory().getItem(slot).getType() == Material.AIR) {
	    return true;
	}
	return false;
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

    private Location getLocation() {
	return location;
    }

    private File getSavefile() {
	return file;
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
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefile());
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

    /*
     * Save methods
     * 
     */

    private void saveJobcenterSize(int size) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefile());
	config.set("JobCenterSize", getSize());
	save(config);
    }

    private void saveJobcenterName() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefile());
	config.set("JobCenterName", getName());
	save(config);
    }

    private void saveJobcenterLocation() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefile());
	config.set("JobcenterLocation.x", getLocation().getX());
	config.set("JobcenterLocation.y", getLocation().getY());
	config.set("JobcenterLocation.z", getLocation().getZ());
	config.set("JobcenterLocation.World", getLocation().getWorld().getName());
	save(config);
    }

    private void saveJobNameList() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefile());
	config.set("Jobnames", getJobNameList());
	save(config);
    }

    private void saveJob(Job job, String itemMaterial, int slot) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefile());
	if (itemMaterial == null) {
	    config.set("Jobs." + job.getName(), null);
	} else {
	    config.set("Jobs." + job.getName() + ".ItemMaterial", itemMaterial);
	    config.set("Jobs." + job.getName() + ".ItemSlot", slot);
	}
	save(config);
    }

    private void save(FileConfiguration config) {
	try {
	    config.save(getSavefile());
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
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
	saveJobcenterSize(size);
    }

    private void setupInventory() {
	inventory = Bukkit.createInventory(getVillager(), getSize(), getName());
	setupDefaultJobcenterInventory();
    }

    private void setupJobcenterName(String name) {
	setName(name);
	saveJobcenterName();
    }

    private void setupJobcenterLocation(Location spawnLocation) {
	location = spawnLocation;
	saveJobcenterLocation();
    }

    private void setupDefaultJobcenterInventory() {
	int slot = getInventory().getSize()-1;
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
	getLocation().getChunk().load();
	Collection<Entity> entitys = getLocation().getWorld().getNearbyEntities(getLocation(), 10, 10, 10);
	for (Entity e : entitys) {
	    if (e.getName().equals(getName())) {
		e.remove();
	    }
	}
	villager = (Villager) getLocation().getWorld().spawnEntity(location, EntityType.VILLAGER);
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
	loadJobcenterLocation();
	loadJobcenterSize();
	setupVillager();
	setupInventory();
	loadJobs();
    }

    private void loadJobcenterSize() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefile());
	size = config.getInt("JobCenterSize");
    }

    private void loadJobcenterLocation() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefile());
	if (config.isSet("ShopLocation.World")) {
	    oldLoadJobcenterLocation(config);
	    saveJobcenterLocation();
	} else {
	    location = new Location(
		    UltimateEconomy.getInstance.getServer().getWorld(config.getString("JobcenterLocation.World")),
		    config.getDouble("JobcenterLocation.x"), config.getDouble("JobcenterLocation.y"),
		    config.getDouble("JobcenterLocation.z"));
	}
    }

    private void loadJobs() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefile());
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
		config = convertSlot(jobName, config);
		getInventory().setItem(config.getInt("Jobs." + job.getName() + ".Slot"), jobItem);
	    } catch (GeneralEconomyException e) {
		Bukkit.getLogger().warning(
			"[Ultimate_Economy] Failed to load the job " + jobName + " for the jobcenter " + getName());
		Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	    }
	}
    }

    /*
     * Validation check methods
     * 
     */

    private void checkForValidSlot(int slot) throws GeneralEconomyException {
	if (slot < 0 || slot > getSize()) {
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

    /*
     * Deprecated
     * 
     */

    @Deprecated
    private void oldLoadJobcenterLocation(YamlConfiguration config) {
	location = new Location(
		UltimateEconomy.getInstance.getServer().getWorld(config.getString("ShopLocation.World")),
		config.getDouble("ShopLocation.x"), config.getDouble("ShopLocation.y"),
		config.getDouble("ShopLocation.z"));
    }

    /**
     * @since 1.2.6
     * @deprecated can be removed later
     */
    @Deprecated
    private YamlConfiguration convertSlot(String jobName, YamlConfiguration config) {
	if (config.contains("Jobs." + jobName + ".ItemSlot")) {
	    int slot = config.getInt("Jobs." + jobName + ".ItemSlot");
	    slot--;
	    config.set("Jobs." + jobName + ".ItemSlot", null);
	    config.set("Jobs." + jobName + ".Slot", slot);
	}
	return config;
    }
}
