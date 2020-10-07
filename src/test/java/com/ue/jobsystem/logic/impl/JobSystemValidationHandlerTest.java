package com.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
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
import com.ue.jobsystem.logic.api.Job;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

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
	public void checkForPositivValueTest() {
		try {
			validationHandler.checkForPositivValue(-10);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(-10.0, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}

	@Test
	public void checkForPositivValueTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForPositivValue(10));
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
	public void checkForBlockNotInJobTest() {
		try {
			Map<String, Double> blockList = new HashMap<>();
			blockList.put("dirt", 10.0);
			validationHandler.checkForBlockNotInJob(blockList, "dirt");
			fail();
		} catch (JobSystemException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(JobExceptionMessageEnum.BLOCK_ALREADY_EXISTS, e.getKey());
		}
	}

	@Test
	public void checkForBlockNotInJobTestValid() {
		Map<String, Double> blockList = new HashMap<>();
		assertDoesNotThrow(() -> validationHandler.checkForBlockNotInJob(blockList, "dirt"));
	}

	@Test
	public void checkForBlockInJobTest() {
		try {
			Map<String, Double> blockList = new HashMap<>();
			validationHandler.checkForBlockInJob(blockList, "dirt");
			fail();
		} catch (JobSystemException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(JobExceptionMessageEnum.BLOCK_DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void checkForBlockInJobTestValid() {
		Map<String, Double> blockList = new HashMap<>();
		blockList.put("dirt", 10.0);
		assertDoesNotThrow(() -> validationHandler.checkForBlockInJob(blockList, "dirt"));
	}

	@Test
	public void checkForLoottypeNotInJobTest() {
		try {
			Map<String, Double> fisherList = new HashMap<>();
			fisherList.put("treasure", 10.0);
			validationHandler.checkForLoottypeNotInJob(fisherList, "treasure");
			fail();
		} catch (JobSystemException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(JobExceptionMessageEnum.LOOTTYPE_ALREADY_EXISTS, e.getKey());
		}
	}

	@Test
	public void checkForLoottypeNotInJobTestValid() {
		Map<String, Double> fisherList = new HashMap<>();
		assertDoesNotThrow(() -> validationHandler.checkForLoottypeNotInJob(fisherList, "treasure"));
	}

	@Test
	public void checkForLoottypeInJobTest() {
		try {
			Map<String, Double> fisherList = new HashMap<>();
			validationHandler.checkForLoottypeInJob(fisherList, "treasure");
			assertTrue(false);
		} catch (JobSystemException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(JobExceptionMessageEnum.LOOTTYPE_DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void checkForLoottypeInJobTestValid() {
		Map<String, Double> fisherList = new HashMap<>();
		fisherList.put("treasure", 10.0);
		assertDoesNotThrow(() -> validationHandler.checkForLoottypeInJob(fisherList, "treasure"));
	}

	@Test
	public void checkForEntityNotInJobTest() {
		try {
			Map<String, Double> entityList = new HashMap<>();
			entityList.put("COW", 10.0);
			validationHandler.checkForEntityNotInJob(entityList, "COW");
			fail();
		} catch (JobSystemException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(JobExceptionMessageEnum.ENTITY_ALREADY_EXISTS, e.getKey());
		}
	}

	@Test
	public void checkForEntityNotInJobTestValid() {
		Map<String, Double> entityList = new HashMap<>();
		assertDoesNotThrow(() -> validationHandler.checkForEntityNotInJob(entityList, "COW"));
	}

	@Test
	public void checkForEntityInJobTest() {
		try {
			Map<String, Double> entityList = new HashMap<>();
			validationHandler.checkForEntityInJob(entityList, "COW");
			fail();
		} catch (JobSystemException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(JobExceptionMessageEnum.ENTITY_DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void checkForValidSizeTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidSize(18));
	}

	@Test
	public void checkForValidSizeTest() {
		try {
			validationHandler.checkForValidSize(14);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(14, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}
	
	@Test
	public void checkForValidSizeTestGreater() {
		try {
			validationHandler.checkForValidSize(63);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(63, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}

	@Test
	public void checkForJobNameDoesNotExistTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForJobNameDoesNotExist(new ArrayList<>(), "myJob"));
	}

	@Test
	public void checkForJobNameDoesNotExistTest() {
		try {
			validationHandler.checkForJobNameDoesNotExist(Arrays.asList("myJob"), "myJob");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("myJob", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, e.getKey());
		}
	}
	
	@Test
	public void checkForJobcenterNameDoesNotExistTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForJobcenterNameDoesNotExist(new ArrayList<>(), "center"));
	}

	@Test
	public void checkForJobcenterNameDoesNotExistTest() {
		try {
			validationHandler.checkForJobcenterNameDoesNotExist(Arrays.asList("center"), "center");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("center", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, e.getKey());
		}
	}

	@Test
	public void checkForEntityInJobTestValid() {
		Map<String, Double> entityList = new HashMap<>();
		entityList.put("COW", 10.0);
		assertDoesNotThrow(() -> validationHandler.checkForEntityInJob(entityList, "COW"));
	}

	@Test
	public void checkForValidSlotTest1() {
		try {
			validationHandler.checkForValidSlot(9, 9);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(9, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}

	@Test
	public void checkForValidSlotTest2() {
		try {
			validationHandler.checkForValidSlot(-9, 9);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals(-9, e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
		}
	}

	@Test
	public void checkForValidSlotTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForValidSlot(5, 9));
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
