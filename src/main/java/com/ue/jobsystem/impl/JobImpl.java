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
import com.ue.exceptions.GeneralEconomyMessageEnum;
import com.ue.exceptions.JobExceptionMessageEnum;
import com.ue.exceptions.JobSystemException;
import com.ue.jobsystem.api.Job;

public class JobImpl implements Job {

    private List<String> itemList, entityList, fisherList;
    private File file;
    private FileConfiguration config;
    private String name;

    /**
     * Constructor to create a new or load a existing job.
     * @param dataFolder
     * @param name
     */
    public JobImpl(File dataFolder, String name) {
	itemList = new ArrayList<>();
	entityList = new ArrayList<>();
	fisherList = new ArrayList<>();
	this.name = name;
	file = new File(dataFolder, name + "-Job.yml");
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
	itemList = config.getStringList("Itemlist");
	entityList = config.getStringList("Entitylist");
	fisherList = config.getStringList("Fisherlist");
    }

    @Override
    public void addFisherLootType(String lootType, double price) throws JobSystemException, GeneralEconomyException {
	if (!"treasure".equals(lootType) && !"junk".equals(lootType) && !"fish".equals(lootType)) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, lootType);
	} else if (price <= 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, price);
	} else if (fisherList.contains(lootType)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_ALREADY_EXISTS);
	} else {
	    config = YamlConfiguration.loadConfiguration(file);
	    config.set("Fisher." + lootType, price);
	    fisherList.add(lootType);
	    removedoubleObjects(entityList);
	    config.set("Fisherlist", fisherList);
	    save();
	}
    }
    
    @Override
    public void delFisherLootType(String lootType) throws JobSystemException, GeneralEconomyException {
	if (!"treasure".equals(lootType) && !"junk".equals(lootType) && !"fish".equals(lootType)) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, lootType);
	} else if (!fisherList.contains(lootType)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_DOES_NOT_EXIST);
	} else {
	    config = YamlConfiguration.loadConfiguration(file);
	    config.set("Fisher." + lootType, null);
	    fisherList.remove(lootType);
	    removedoubleObjects(entityList);
	    config.set("Fisherlist", fisherList);
	    save();
	}

    }
    
    @Override
    public void addMob(String entity, double price) throws JobSystemException, GeneralEconomyException {
	entity = entity.toUpperCase();
	try {
	    EntityType.valueOf(entity);
	} catch (IllegalArgumentException e) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, entity);
	}
	if (entityList.contains(entity)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_ALREADY_EXISTS);
	} else if (price <= 0.0) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, price);
	} else {
	    entityList.add(entity);
	    config = YamlConfiguration.loadConfiguration(file);
	    config.set("JobEntitys." + entity + ".killprice", price);
	    removedoubleObjects(entityList);
	    config.set("Entitylist", entityList);
	    save();
	}
    }

    @Override
    public void deleteMob(String entity) throws JobSystemException, GeneralEconomyException {
	try {
	    EntityType.valueOf(entity.toUpperCase());
	} catch (IllegalArgumentException e) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, entity);
	}
	entity = entity.toUpperCase();
	if (entityList.contains(entity)) {
	    entityList.remove(entity);
	    config = YamlConfiguration.loadConfiguration(file);
	    config.set("JobEntitys." + entity, null);
	    config.set("Entitylist", entityList);
	    save();
	} else {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_DOES_NOT_EXIST);
	}
    }

    @Override
    public void addItem(String material, double price) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	if (price <= 0.0) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, price);
	} else if (Material.matchMaterial(material) == null) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, material);
	} else if (itemList.contains(material)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ITEM_ALREADY_EXISTS);
	} else {
	    itemList.add(material);
	    config = YamlConfiguration.loadConfiguration(file);
	    config.set("JobItems." + material + ".breakprice", price);
	    removedoubleObjects(itemList);
	    config.set("Itemlist", itemList);
	    save();
	}
    }

    @Override
    public void deleteItem(String material) throws JobSystemException, GeneralEconomyException {
	material = material.toUpperCase();
	if (Material.matchMaterial(material) == null) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, material);
	} else if (!itemList.contains(material)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
	} else {
	    itemList.remove(material);
	    config = YamlConfiguration.loadConfiguration(file);
	    config.set("JobItems." + material, null);
	    config.set("Itemlist", itemList);
	    save();
	}
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public double getItemPrice(String material) throws JobSystemException, GeneralEconomyException {
	if (Material.matchMaterial(material.toUpperCase()) == null) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, material);
	} else if (!itemList.contains(material)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
	} else {
	    config = YamlConfiguration.loadConfiguration(file);
	    double price = config.getDouble("JobItems." + material + ".breakprice");
	    return price;
	}
    }

    @Override
    public double getFisherPrice(String lootType) throws JobSystemException, GeneralEconomyException {
	if (!"treasure".equals(lootType) && !"junk".equals(lootType) && !"fish".equals(lootType)) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, lootType);
	} else if (!fisherList.contains(lootType)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_DOES_NOT_EXIST);
	} else {
	    config = YamlConfiguration.loadConfiguration(file);
	    double price1 = config.getDouble("Fisher." + lootType);
	    return price1;
	}
    }

    @Override
    public double getKillPrice(String entityName) throws JobSystemException, GeneralEconomyException {
	try {
	    EntityType.valueOf(entityName.toUpperCase());
	} catch (IllegalArgumentException e) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, entityName);
	}
	if (!entityList.contains(entityName)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_DOES_NOT_EXIST);
	} else {
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

    @Override
    public void deleteJob() {
	file.delete();
    }

    @Override
    public List<String> getItemList() {
	return itemList;
    }

    @Override
    public List<String> getEntityList() {
	return entityList;
    }

    @Override
    public List<String> getFisherList() {
	return fisherList;
    }
}
