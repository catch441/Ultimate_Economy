package com.ue.jobsystem.logic.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;

public class JobsystemValidationHandlerImpl implements JobsystemValidationHandler {

	private final MessageWrapper messageWrapper;
	private final List<EntityType> breedableMobs = Arrays.asList(EntityType.BEE, EntityType.COW, EntityType.HOGLIN,
			EntityType.MUSHROOM_COW, EntityType.PIG, EntityType.SHEEP, EntityType.WOLF, EntityType.CAT,
			EntityType.DONKEY, EntityType.HORSE, EntityType.OCELOT, EntityType.POLAR_BEAR, EntityType.TURTLE,
			EntityType.CHICKEN, EntityType.FOX, EntityType.LLAMA, EntityType.PANDA, EntityType.RABBIT,
			EntityType.VILLAGER);

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
	public void checkForDoesNotExist(Map<String, Double> list, String value) throws GeneralEconomyException {
		if (list.containsKey(value)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS,
					value);
		}
	}
	
	@Override
	public void checkForDoesExist(Map<String, Double> list, String value) throws GeneralEconomyException {
		if (!list.containsKey(value)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST,
					value);
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
		if (size % 9 != 0 || size > 54) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					size);
		}
	}

	@Override
	public void checkForJobcenterNameDoesNotExist(List<String> jobcenterList, String name)
			throws GeneralEconomyException {
		if (jobcenterList.contains(name)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, name);
		}
	}

	@Override
	public void checkForValidBreedableEntity(EntityType breedable) throws GeneralEconomyException {
		if(!breedableMobs.contains(breedable)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					breedable.toString().toLowerCase());
		}
	}

	private boolean isSlotEmpty(Inventory inventory, int slot) {
		return (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR);
	}
}