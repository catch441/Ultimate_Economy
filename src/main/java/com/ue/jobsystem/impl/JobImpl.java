package com.ue.jobsystem.impl;

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

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.JobExceptionMessageEnum;
import com.ue.exceptions.JobSystemException;
import com.ue.jobsystem.api.Job;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobImpl implements Job {

    private List<String> blockList, entityList, fisherList;
    private File file;
    private FileConfiguration config;
    private String name;

    /**
     * Constructor to create a new or load a existing job.
     * 
     * @param name
     */
    public JobImpl(String name) {
	blockList = new ArrayList<>();
	entityList = new ArrayList<>();
	fisherList = new ArrayList<>();
	this.name = name;
	file = new File(UltimateEconomy.getInstance.getDataFolder(), name + "-Job.yml");
	if (!file.exists()) {
	    try {
		file.createNewFile();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    config = YamlConfiguration.loadConfiguration(file);
	    config.set("Jobname", name);
	    save();
	} else {
	    config = YamlConfiguration.loadConfiguration(file);
	    load();
	}
    }

    private void load() {
	config = YamlConfiguration.loadConfiguration(file);
	blockList = config.getStringList("Itemlist");
	entityList = config.getStringList("Entitylist");
	fisherList = config.getStringList("Fisherlist");
    }

    @Override
    public void addFisherLootType(String lootType, double price) throws JobSystemException, GeneralEconomyException {
	checkForValidFisherLootType(lootType);
	checkForPositivValue(price);
	checkForLoottypeNotInJob(lootType);
	config = YamlConfiguration.loadConfiguration(file);
	config.set("Fisher." + lootType, price);
	fisherList.add(lootType);
	removedoubleObjects(entityList);
	config.set("Fisherlist", fisherList);
	save();
    }

    @Override
    public void delFisherLootType(String lootType) throws JobSystemException, GeneralEconomyException {
	checkForValidFisherLootType(lootType);
	checkForLoottypeInJob(lootType);
	config = YamlConfiguration.loadConfiguration(file);
	config.set("Fisher." + lootType, null);
	fisherList.remove(lootType);
	removedoubleObjects(entityList);
	config.set("Fisherlist", fisherList);
	save();
    }

    @Override
    public void addMob(String entity, double price) throws JobSystemException, GeneralEconomyException {
	entity = entity.toUpperCase();
	checkForValidEntityType(entity);
	checkForEntityNotInJob(entity);
	checkForPositivValue(price);
	entityList.add(entity);
	config = YamlConfiguration.loadConfiguration(file);
	config.set("JobEntitys." + entity + ".killprice", price);
	removedoubleObjects(entityList);
	config.set("Entitylist", entityList);
	save();
    }

    @Override
    public void deleteMob(String entity) throws JobSystemException, GeneralEconomyException {
	entity = entity.toUpperCase();
	checkForValidEntityType(entity);
	checkForEntityInJob(entity);
	entityList.remove(entity);
	config = YamlConfiguration.loadConfiguration(file);
	config.set("JobEntitys." + entity, null);
	config.set("Entitylist", entityList);
	save();
    }

    @Override
    public void addBlock(String material, double price) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	checkForValidMaterial(material);
	checkForPositivValue(price);
	checkForBlockNotInJob(material);
	blockList.add(material);
	config = YamlConfiguration.loadConfiguration(file);
	config.set("JobItems." + material + ".breakprice", price);
	removedoubleObjects(blockList);
	config.set("Itemlist", blockList);
	save();
    }

    @Override
    public void deleteBlock(String material) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	checkForValidMaterial(material);
	checkForBlockInJob(material);
	blockList.remove(material);
	config = YamlConfiguration.loadConfiguration(file);
	config.set("JobItems." + material, null);
	config.set("Itemlist", blockList);
	save();
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public double getBlockPrice(String material) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	checkForValidMaterial(material);
	checkForBlockInJob(material);
	config = YamlConfiguration.loadConfiguration(file);
	double price = config.getDouble("JobItems." + material + ".breakprice");
	return price;
    }

    @Override
    public double getFisherPrice(String lootType) throws JobSystemException, GeneralEconomyException {
	checkForValidFisherLootType(lootType);
	checkForLoottypeInJob(lootType);
	config = YamlConfiguration.loadConfiguration(file);
	double price1 = config.getDouble("Fisher." + lootType);
	return price1;
    }

    @Override
    public double getKillPrice(String entityName) throws JobSystemException, GeneralEconomyException {
	entityName = entityName.toUpperCase();
	checkForValidEntityType(entityName);
	checkForEntityInJob(entityName);
	config = YamlConfiguration.loadConfiguration(file);
	double price2 = config.getDouble("JobEntitys." + entityName + ".killprice");
	return price2;
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

    @Override
    public void deleteJob() {
	file.delete();
    }

    @Override
    public List<String> getBlockList() {
	return blockList;
    }

    @Override
    public List<String> getEntityList() {
	return entityList;
    }

    @Override
    public List<String> getFisherList() {
	return fisherList;
    }

    /*
     * Validation methods
     * 
     */

    private void checkForValidMaterial(String material) throws GeneralEconomyException {
	if (Material.matchMaterial(material) == null) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, material);
	}
    }

    private void checkForValidEntityType(String entityName) throws GeneralEconomyException {
	try {
	    EntityType.valueOf(entityName);
	} catch (IllegalArgumentException e) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
		    entityName);
	}
    }

    private void checkForPositivValue(double value) throws GeneralEconomyException {
	if (value <= 0.0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
	}
    }

    private void checkForValidFisherLootType(String lootType) throws GeneralEconomyException {
	if (!"treasure".equals(lootType) && !"junk".equals(lootType) && !"fish".equals(lootType)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, lootType);
	}
    }

    private void checkForBlockNotInJob(String material) throws JobSystemException {
	if (blockList.contains(material)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.BLOCK_ALREADY_EXISTS);
	}
    }

    private void checkForBlockInJob(String material) throws JobSystemException {
	if (!blockList.contains(material)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.BLOCK_DOES_NOT_EXIST);
	}
    }

    private void checkForLoottypeNotInJob(String lootType) throws JobSystemException {
	if (fisherList.contains(lootType)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_ALREADY_EXISTS);
	}
    }

    private void checkForLoottypeInJob(String lootType) throws JobSystemException {
	if (!fisherList.contains(lootType)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_DOES_NOT_EXIST);
	}
    }

    private void checkForEntityNotInJob(String entity) throws JobSystemException {
	if (entityList.contains(entity)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_ALREADY_EXISTS);
	}
    }

    private void checkForEntityInJob(String entityName) throws JobSystemException {
	if (!entityList.contains(entityName)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_DOES_NOT_EXIST);
	}
    }
}
