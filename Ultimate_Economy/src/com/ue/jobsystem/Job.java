package com.ue.jobsystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.player.EconomyPlayer;

public class Job {
	
	private static List<Job> jobList = new ArrayList<>();

	private List<String> itemList,entityList,fisherList;
	private File file;
	private FileConfiguration config;
	private String name;
	
	private Job(File dataFolder, String name) {
		itemList = new ArrayList<>();
		entityList = new ArrayList<>();
		fisherList = new ArrayList<>();
		this.name = name;
		file = new File(dataFolder , name + "-Job.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			config = YamlConfiguration.loadConfiguration(file);
			config.set("Jobname", name);
			save();
		}
		else {
			config = YamlConfiguration.loadConfiguration(file);
			load();
		}
	}
    
	private  void load() {
		config = YamlConfiguration.loadConfiguration(file);
		itemList = config.getStringList("Itemlist");
		entityList = config.getStringList("Entitylist");
		fisherList = config.getStringList("Fisherlist");
	}
	
	/**
	 * This method adds a loottype with a price to this job. The loottype is for a fisherJob.
	 * It should be "treasure", "junk" or "fish".
	 * 
	 * @param lootType
	 * @param price
	 * @throws JobSystemException 
	 */
	public void addFisherLootType(String lootType, double price) throws JobSystemException {
		if(!lootType.equals("treasure") && !lootType.equals("junk") && !lootType.equals("fish")) {
			throw new JobSystemException(JobSystemException.LOOTTYPE_IS_INVALID);
		}
		else if(price <= 0) {
			throw new JobSystemException(JobSystemException.PRICE_IS_INVALID);
		}
		else if(fisherList.contains(lootType)) {
			throw new JobSystemException(JobSystemException.LOOTTYPE_ALREADY_EXISTS);
		}
		else {
			config = YamlConfiguration.loadConfiguration(file);
			config.set("Fisher." + lootType, price);
			fisherList.add(lootType);
			removedoubleObjects(entityList);
			config.set("Fisherlist", fisherList);
			save();
		}
	}
	
	/**
	 * This method removes a loottype from this job. The loottype is for a fisherJob.
	 * It should be "treasure", "junk" or "fish".
	 * 
	 * @param lootType
	 * @throws JobSystemException 
	 */
	public void delFisherLootType(String lootType) throws JobSystemException {
		if(!lootType.equals("treasure") && !lootType.equals("junk") && !lootType.equals("fish")) {
			throw new JobSystemException(JobSystemException.LOOTTYPE_IS_INVALID);
		}
		else if(!fisherList.contains(lootType)) {
			throw new JobSystemException(JobSystemException.LOOTTYPE_DOES_NOT_EXISTS);
		}
		else {
			config = YamlConfiguration.loadConfiguration(file);
			config.set("Fisher." + lootType,null);
			fisherList.remove(lootType);
			removedoubleObjects(entityList);
			config.set("Fisherlist", fisherList);
			save();
		}
		
	}
	
	/**
	 * This method adds a mob to a job.
	 * 
	 * @param entity
	 * @param price
	 * @throws JobSystemException
	 */
	public void addMob(String entity,double price) throws JobSystemException {
		entity = entity.toUpperCase();
		try {
			EntityType.valueOf(entity);
		} catch(IllegalArgumentException e) {
			throw new JobSystemException(JobSystemException.ENTITY_IS_INVALID);
		}
		if(entityList.contains(entity)) {
			throw new JobSystemException(JobSystemException.ENTITY_ALREADY_EXISTS);
		}
		else if(price <= 0.0) {
			throw new JobSystemException(JobSystemException.PRICE_IS_INVALID);
		}
		else {
			entityList.add(entity);
			config = YamlConfiguration.loadConfiguration(file);
			config.set("JobEntitys." + entity + ".killprice", price);
			removedoubleObjects(entityList);
			config.set("Entitylist", entityList);
			save();
		}
	}
	
	/**
	 * This method removes a mob from a job.
	 * 
	 * @param entity
	 * @throws JobSystemException
	 */
	public void deleteMob(String entity) throws JobSystemException {
		try {
			EntityType.valueOf(entity.toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new JobSystemException(JobSystemException.ENTITY_IS_INVALID);
		}
		if(entityList.contains(entity)) {
			entityList.remove(entity);
			config = YamlConfiguration.loadConfiguration(file);
			config.set("JobEntitys." + entity, null);
			config.set("Entitylist", entityList);
			save();
		}
		else {
			throw new JobSystemException(JobSystemException.ENTITY_DOES_NOT_EXIST);
		}
	}
	
	/**
	 * This method adds a item to a job.
	 * 
	 * @param material
	 * @param price
	 * @throws JobSystemException
	 */
	public void addItem(String material,double price) throws JobSystemException {
		material = material.toUpperCase();;
		if(price <= 0.0) {
			throw new JobSystemException(JobSystemException.PRICE_IS_INVALID);
		}
		else if(Material.matchMaterial(material) == null) {
			throw new JobSystemException(JobSystemException.ITEM_IS_INVALID);
		}
		else if(itemList.contains(material)){
			throw new JobSystemException(JobSystemException.ITEM_ALREADY_EXIST);
		}
		else {
			itemList.add(material);
			config = YamlConfiguration.loadConfiguration(file);
			config.set("JobItems." + material + ".breakprice", price);
			removedoubleObjects(itemList);
			config.set("Itemlist", itemList);
			save();
		}
	}
	
	/**
	 * This method removes a item from a job.
	 * 
	 * @param material
	 * @throws JobSystemException
	 */
	public void deleteItem(String material) throws JobSystemException {
		if(Material.matchMaterial(material.toUpperCase()) == null) {
			throw new JobSystemException(JobSystemException.ITEM_IS_INVALID);
		}
		else if(!itemList.contains(material)) {
			throw new JobSystemException(JobSystemException.ITEM_DOES_NOT_EXIST);
		}
		else {
			itemList.remove(material);
			config = YamlConfiguration.loadConfiguration(file);
			config.set("JobItems." + material, null);
			config.set("Itemlist", itemList);
			save();
		}
	}
	
	/**
	 * This method returns the name of this job.
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * This method returns the price of a item in this job.
	 * 
	 * @param material
	 * @return double
	 * @throws JobSystemException
	 */
	public double getItemPrice(String material) throws JobSystemException {
		if(Material.matchMaterial(material.toUpperCase()) == null) {
			throw new JobSystemException(JobSystemException.ITEM_IS_INVALID);
		}
		else if(!itemList.contains(material)) {
			throw new JobSystemException(JobSystemException.ITEM_DOES_NOT_EXIST);
		}
		else {
			config = YamlConfiguration.loadConfiguration(file);
			double price = config.getDouble("JobItems." + material + ".breakprice");
			return price;
		}
	}
	
	/**
	 * This method returns the price of a fisher lootType.
	 * 
	 * @param lootType
	 * @return double
	 * @throws JobSystemException
	 */
	public double getFisherPrice(String lootType) throws JobSystemException {
		if(!lootType.equals("treasure") && !lootType.equals("junk") && !lootType.equals("fish")) {
			throw new JobSystemException(JobSystemException.LOOTTYPE_IS_INVALID);
		}
		else if(!fisherList.contains(lootType)) {
			throw new JobSystemException(JobSystemException.LOOTTYPE_DOES_NOT_EXISTS);
		}
		else {
			config = YamlConfiguration.loadConfiguration(file);
			double price1 = config.getDouble("Fisher." + lootType);
			return price1;
		}
	}
	
	/**
	 * This method returns the price for killing a entity.
	 * 
	 * @param entityName
	 * @return double
	 * @throws JobSystemException
	 */
	public double getKillPrice(String entityName) throws JobSystemException {
		try {
			EntityType.valueOf(entityName.toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new JobSystemException(JobSystemException.ENTITY_IS_INVALID);
		}
		if(!entityList.contains(entityName)) {
			throw new JobSystemException(JobSystemException.ENTITY_DOES_NOT_EXIST);
		}
		else {
			config = YamlConfiguration.loadConfiguration(file);
			double price2 = config.getDouble("JobEntitys." + entityName + ".killprice");
			return price2;
		}
		
	}	
	
	private void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> removedoubleObjects(List<String> list) {
		Set<String> set = new LinkedHashSet<String>(list);
		list = new ArrayList<String>(set);
		return list;
	}
	
	/**
	 * This method deletes the job saveFile.
	 */
	private void deleteJob() {
		file.delete();
	}
	
	/**
	 * This method returns the itemList.
	 * 
	 * @return List of Strings
	 */
	public List<String> getItemList() {
		return itemList;
	}
	
	/**
	 * This method returns the entityList.
	 * 
	 * @return List of Strings
	 */
	public List<String> getEntityList() {
		return entityList;
	}
	
	/**
	 * This method returns the fisherList.
	 * 
	 * @return List of Strings
	 */
	public List<String> getFisherList() {
		return fisherList;
	}
	
	public static List<Job> getJobList() {
		return jobList;
	}
	
	public static List<String> getJobNameList() {
		List<String> jobNames = new ArrayList<>();
		for(Job job:jobList) {
			jobNames.add(job.getName());
		}
		return jobNames;
	}
	
	/**
	 * This method returns a job by it's name.
	 * 
	 * @param jobName
	 * @return Job
	 * @throws JobSystemException
	 */
	public static Job getJobByName(String jobName) throws JobSystemException {
		for(Job job:jobList) {
			if(job.getName().equals(jobName)) {
				return job;
			}
		}
		throw new JobSystemException(JobSystemException.JOB_DOES_NOT_EXIST);
	}
	
	/**
	 * This method deletes a job.
	 * 
	 * @param jobName
	 * @throws JobSystemException
	 */
	public static void deleteJob(String jobName) throws JobSystemException {
		Job job = getJobByName(jobName);
		List<JobCenter> jobCenterList = JobCenter.getJobCenterList();
		for(JobCenter jobCenter:jobCenterList) {
			try {
			jobCenter.removeJob(jobName);
			} catch (JobSystemException e) {}
		}
		for(EconomyPlayer ecoPlayer : EconomyPlayer.getAllEconomyPlayers()) {
			if(ecoPlayer.hasJob(jobName)) {
				try {
					ecoPlayer.removeJob(jobName);
				} catch (PlayerException e) {}
			}
		}
		jobList.remove(job);
		job.deleteJob();
	}
	
	/**
	 * This method should be used to create a new Job.
	 * 
	 * @param dataFolder
	 * @param jobName
	 * @throws JobSystemException
	 */
	public static void createJob(File dataFolder,String jobName) throws JobSystemException {
		if(getJobNameList().contains(jobName)) {
			throw new JobSystemException(JobSystemException.JOB_ALREADY_EXIST);
		}
		else {
			jobList.add(new Job(dataFolder, jobName));
		}
	}
	
	/**
	 * This method loads all Jobs from the save files.
	 * 
	 * @param dataFolder
	 * @param fileConfig
	 * @throws JobSystemException
	 */
	public static void loadAllJobs(File dataFolder,FileConfiguration fileConfig) throws JobSystemException {
		for(String jobName:fileConfig.getStringList("JobList")) {
			File file = new File(dataFolder , jobName + "-Job.yml");
			if(file.exists()) {
				jobList.add(new Job(dataFolder, jobName));
			}
			else {
				throw new JobSystemException(JobSystemException.CANNOT_LOAD_JOB);
			}
			
		}
	}
}
