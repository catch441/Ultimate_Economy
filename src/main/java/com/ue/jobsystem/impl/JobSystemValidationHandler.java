package com.ue.jobsystem.impl;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.JobExceptionMessageEnum;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.jobsystem.api.Job;

public class JobSystemValidationHandler {

    protected void checkForValidMaterial(String material) throws GeneralEconomyException {
	if (Material.matchMaterial(material) == null) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, material);
	}
    }

    protected void checkForValidEntityType(String entityName) throws GeneralEconomyException {
	try {
	    EntityType.valueOf(entityName);
	} catch (IllegalArgumentException e) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
		    entityName);
	}
    }

    protected void checkForPositivValue(double value) throws GeneralEconomyException {
	if (value <= 0.0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
	}
    }

    protected void checkForValidFisherLootType(String lootType) throws GeneralEconomyException {
	if (!"treasure".equals(lootType) && !"junk".equals(lootType) && !"fish".equals(lootType)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, lootType);
	}
    }

    protected void checkForBlockNotInJob(Map<String, Double> blockList, String material) throws JobSystemException {
	if (blockList.containsKey(material)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.BLOCK_ALREADY_EXISTS);
	}
    }

    protected void checkForBlockInJob(Map<String, Double> blockList, String material) throws JobSystemException {
	if (!blockList.containsKey(material)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.BLOCK_DOES_NOT_EXIST);
	}
    }

    protected void checkForLoottypeNotInJob(Map<String, Double> fisherList, String lootType) throws JobSystemException {
	if (fisherList.containsKey(lootType)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_ALREADY_EXISTS);
	}
    }

    protected void checkForLoottypeInJob(Map<String, Double> fisherList, String lootType) throws JobSystemException {
	if (!fisherList.containsKey(lootType)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.LOOTTYPE_DOES_NOT_EXIST);
	}
    }

    protected void checkForEntityNotInJob(Map<String, Double> entityList, String entity) throws JobSystemException {
	if (entityList.containsKey(entity)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_ALREADY_EXISTS);
	}
    }

    protected void checkForEntityInJob(Map<String, Double> entityList, String entityName) throws JobSystemException {
	if (!entityList.containsKey(entityName)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.ENTITY_DOES_NOT_EXIST);
	}
    }

    protected void checkForValidSlot(int slot, int size) throws GeneralEconomyException {
	if (slot < 0 || slot > size) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, slot);
	}
    }

    protected void checkForFreeSlot(Inventory inventory,int slot) throws PlayerException {
	if (!isSlotEmpty(inventory,slot)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED);
	}
    }

    protected void checkForJobDoesNotExistInJobcenter(List<Job> jobList,Job job) throws JobSystemException {
	if (jobList.contains(job)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.JOB_ALREADY_EXIST_IN_JOBCENTER);
	}
    }

    protected void checkForJobExistsInJobcenter(List<Job> jobList,Job job) throws JobSystemException {
	if (!jobList.contains(job)) {
	    throw JobSystemException.getException(JobExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER);
	}
    }
    
    private boolean isSlotEmpty(Inventory inventory,int slot) {
	if (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR) {
	    return true;
	}
	return false;
    }
}