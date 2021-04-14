package org.ue.jobsystem.logic.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.economyplayer.logic.EconomyPlayerExceptionMessageEnum;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.jobsystem.logic.JobExceptionMessageEnum;
import org.ue.jobsystem.logic.JobSystemException;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobsystemValidationHandler;

public class JobsystemValidationHandlerImpl implements JobsystemValidationHandler {

	private final MessageWrapper messageWrapper;
	private final List<EntityType> breedableMobs = Arrays.asList(EntityType.BEE, EntityType.COW, EntityType.HOGLIN,
			EntityType.MUSHROOM_COW, EntityType.PIG, EntityType.SHEEP, EntityType.WOLF, EntityType.CAT,
			EntityType.DONKEY, EntityType.HORSE, EntityType.OCELOT, EntityType.POLAR_BEAR, EntityType.TURTLE,
			EntityType.CHICKEN, EntityType.FOX, EntityType.LLAMA, EntityType.PANDA, EntityType.RABBIT,
			EntityType.VILLAGER);
	
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
	public void checkForValidFisherLootType(String lootType) throws GeneralEconomyException {
		if (!"treasure".equals(lootType) && !"junk".equals(lootType) && !"fish".equals(lootType)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					lootType);
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