package com.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.jobsystem.logic.api.Job;

@ExtendWith(MockitoExtension.class)
public class JobSystemValidationHandlerTest {

	@InjectMocks
	JobsystemValidationHandlerImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void checkForValidMaterialTest() {
		try {
			validationHandler.checkForValidMaterial("invalid");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("invalid", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}

	@Test
	public void checkForValidMaterialTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidMaterial("dirt"));
	}

	@Test
	public void checkForValidEntityTypeTest() {
		try {
			validationHandler.checkForValidEntityType("invalid");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("invalid", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}

	@Test
	public void checkForValidEntityTypeTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidEntityType("COW"));
	}

	@Test
	public void checkForValidFisherLootTypeTest() {
		try {
			validationHandler.checkForValidFisherLootType("invalid");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("invalid", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}

	@Test
	public void checkForValidFisherLootTypeTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidFisherLootType("junk"));
		assertDoesNotThrow(() -> validationHandler.checkForValidFisherLootType("treasure"));
		assertDoesNotThrow(() -> validationHandler.checkForValidFisherLootType("fish"));
	}
	
	@Test
	public void checkForValidBreedableEntityTest() {
		try {
			validationHandler.checkForValidBreedableEntity(EntityType.COD);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("cod", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}

	@Test
	public void checkForValidBreedableEntityTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidBreedableEntity(EntityType.COW));
	}

	@Test
	public void checkForJobDoesNotExistInJobcenterTest() {
		try {
			List<Job> jobs = new ArrayList<>();
			Job job = mock(Job.class);
			jobs.add(job);
			validationHandler.checkForJobDoesNotExistInJobcenter(jobs, job);
			fail();
		} catch (JobSystemException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(JobExceptionMessageEnum.JOB_ALREADY_EXIST_IN_JOBCENTER, e.getKey());
		}
	}

	@Test
	public void checkForJobDoesNotExistInJobcenterTestValid() {
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> validationHandler.checkForJobDoesNotExistInJobcenter(jobs, job));
	}

	@Test
	public void checkForJobExistInJobcenterTest() {
		try {
			List<Job> jobs = new ArrayList<>();
			Job job = mock(Job.class);
			validationHandler.checkForJobExistsInJobcenter(jobs, job);
			fail();
		} catch (JobSystemException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(JobExceptionMessageEnum.JOB_NOT_EXIST_IN_JOBCENTER, e.getKey());
		}
	}

	@Test
	public void checkForJobExistsInJobcenterTestValid() {
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		jobs.add(job);
		assertDoesNotThrow(() -> validationHandler.checkForJobExistsInJobcenter(jobs, job));
	}

	@Test
	public void checkForSlotIsEmptyTest() {
		try {
			Inventory inv = mock(Inventory.class);
			ItemStack stack = mock(ItemStack.class);
			when(stack.getType()).thenReturn(Material.STONE);
			when(inv.getItem(0)).thenReturn(stack);
			validationHandler.checkForFreeSlot(inv, 0);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED, e.getKey());
		}
	}

	@Test
	public void checkForSlotIsEmptyTestValid() {
		Inventory inv = mock(Inventory.class);
		when(inv.getItem(0)).thenReturn(null);
		assertDoesNotThrow(() -> validationHandler.checkForFreeSlot(inv, 0));
	}
}
