package com.ue.jobsystem.logic.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

public class JobsystemValidationHandlerImpl implements JobsystemValidationHandler {

	private final MessageWrapper messageWrapper;

	/**
	 * Inject constructor.
	 * 
	 * @param messageWrapper
	 */
	@Inject
	public JobsystemValidationHandlerImpl(MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
	}

	@Override
	public void checkForValidMaterial(String material) throws GeneralEconomyException {
		if (Material.matchMaterial(material) == null) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					material);
		}
	}

	@Override
	public void checkForValidEntityType(String entityName) throws GeneralEconomyException {
		try {
			EntityType.valueOf(entityName);
		} catch (IllegalArgumentException e) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					entityName);
		}
	}

	@Override
	public void checkForPositivValue(double value) throws GeneralEconomyException {
		if (value <= 0.0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					value);
		}
	}

	@Override
	public void checkForValidFisherLootType(String lootType) throws GeneralEconomyException {
		if (!"treasure".equals(lootType) && !"junk".equals(lootType) && !"fish".equals(lootType)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					lootType);
		}
	}

	@Override
	public void checkForBlockNotInJob(Map<String, Double> blockList, String material) throws JobSystemException {
		if (blockList.containsKey(material)) {
			throw new JobSystemException(messageWrapper, JobExceptionMessageEnum.BLOCK_ALREADY_EXISTS);
		}
	}

	@Override
	public void checkForBlockInJob(Map<String, Double> blockList, String material) throws JobSystemException {
		if (!blockList.containsKey(material)) {
			throw new JobSystemException(messageWrapper, JobExceptionMessageEnum.BLOCK_DOES_NOT_EXIST);
		}
	}

	@Override
	public void checkForLoottypeNotInJob(Map<String, Double> fisherList, String lootType) throws JobSystemException {
		if (fisherList.containsKey(lootType)) {
			throw new JobSystemException(messageWrapper, JobExceptionMessageEnum.LOOTTYPE_ALREADY_EXISTS);
		}
	}

	@Override
	public void checkForLoottypeInJob(Map<String, Double> fisherList, String lootType) throws JobSystemException {
		if (!fisherList.containsKey(lootType)) {
			throw new JobSystemException(messageWrapper, JobExceptionMessageEnum.LOOTTYPE_DOES_NOT_EXIST);
		}
	}

	@Override
	public void checkForEntityNotInJob(Map<String, Double> entityList, String entity) throws JobSystemException {
		if (entityList.containsKey(entity)) {
			throw new JobSystemException(messageWrapper, JobExceptionMessageEnum.ENTITY_ALREADY_EXISTS);
		}
	}

	@Override
	public void checkForEntityInJob(Map<String, Double> entityList, String entityName) throws JobSystemException {
		if (!entityList.containsKey(entityName)) {
			throw new JobSystemException(messageWrapper, JobExceptionMessageEnum.ENTITY_DOES_NOT_EXIST);
		}
	}

	@Override
	public void checkForValidSlot(int slot, int size) throws GeneralEconomyException {
		// size -1 because of one reserved slot
		if (slot < 0 || slot >= (size - 1)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					slot);
		}
	}

	@Override
	public void checkForFreeSlot(Inventory inventory, int slot) throws EconomyPlayerException {
		if (!isSlotEmpty(inventory, slot)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED);
		}
	}

	@Override
	public void checkForJobDoesNotExistInJobcenter(List<Job> jobList, Job job) throws JobSystemException {
		if (jobList.contains(job)) {
			throw new JobSystemException(messageWrapper, JobExceptionMessageEnum.JOB_ALREADY_EXIST_IN_JOBCENTER);
		}
	}

	@Override
	public void checkForJobExistsInJobcenter(List<Job> jobList, Job job) throws JobSystemException {
		if (!jobList.contains(job)) {
			throw new JobSystemException(messageWrapper, JobExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER);
		}
	}
	
	@Override
	public void checkForJobNameDoesNotExist(List<String> jobList, String jobName) throws GeneralEconomyException {
		if (jobList.contains(jobName)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS,
					jobName);
		}
	}
	
	@Override
	public void checkForValidSize(int size) throws GeneralEconomyException {
		if (size % 9 != 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, size);
		}
	}

	@Override
	public void checkForJobcenterNameDoesNotExist(List<String> jobcenterList, String name) throws GeneralEconomyException {
		if (jobcenterList.contains(name)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, name);
		}
	}

	private boolean isSlotEmpty(Inventory inventory, int slot) {
		return (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR);
	}
}