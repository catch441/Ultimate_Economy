package org.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.jobsystem.dataaccess.api.JobDao;
import org.ue.jobsystem.logic.api.FishingLootTypeEnum;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobsystemException;
import org.ue.jobsystem.logic.api.JobsystemValidationHandler;

@ExtendWith(MockitoExtension.class)
public class JobImplTest {

	@Mock
	JobDao jobDao;
	@Mock
	JobsystemValidationHandler validationHandler;

	@Test
	public void constructorNewTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		verify(jobDao).setupSavefile("myJob");
		assertEquals("myJob", job.getName());
		verify(jobDao).saveJobName("myJob");
	}

	@Test
	public void loadExistingShopTest() {
		when(jobDao.loadJobName()).thenReturn("myJob");
		Map<String, Double> blocks = new HashMap<>();
		blocks.put("DIRT", 1.0);
		when(jobDao.loadBlockList()).thenReturn(blocks);
		Map<String, Double> mobs = new HashMap<>();
		mobs.put("COW", 3.0);
		when(jobDao.loadEntityList()).thenReturn(mobs);
		Map<String, Double> fisher = new HashMap<>();
		fisher.put("fish", 2.0);
		when(jobDao.loadFisherList()).thenReturn(fisher);
		Map<String, Double> breeder = new HashMap<>();
		breeder.put("COW", 1.5);
		when(jobDao.loadBreedableList()).thenReturn(breeder);
		Job job = new JobImpl(validationHandler, jobDao, "myJob", false);
		verify(jobDao).setupSavefile("myJob");
		assertEquals("myJob", job.getName());
		assertTrue(job.getBlockList().containsKey("DIRT"));
		assertTrue(job.getBlockList().containsValue(1.0));
		assertTrue(job.getFisherList().containsKey("fish"));
		assertTrue(job.getFisherList().containsValue(2.0));
		assertTrue(job.getEntityList().containsKey("COW"));
		assertTrue(job.getEntityList().containsValue(3.0));
		assertTrue(job.getBreedableList().containsKey("COW"));
		assertTrue(job.getBreedableList().containsValue(1.5));
	}

	@Test
	public void getBlockListTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBlock("stone", 1.5));
		Map<String, Double> list = job.getBlockList();
		assertEquals(1, list.size());
		assertTrue(list.containsKey("STONE"));
		assertTrue(list.containsValue(1.5));
	}

	@Test
	public void getEntityListTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addMob("cow", 1.5));
		Map<String, Double> list = job.getEntityList();
		assertEquals(1, list.size());
		assertTrue(list.containsKey("COW"));
		assertTrue(list.containsValue(1.5));
	}

	@Test
	public void getFisherListTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("treasure", 1.5));
		Map<String, Double> list = job.getFisherList();
		assertEquals(1, list.size());
		assertTrue(list.containsKey("treasure"));
		assertTrue(list.containsValue(1.5));
	}

	@Test
	public void getKillPriceTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addMob("cow", 1.5));
		assertEquals("1.5", assertDoesNotThrow(() -> String.valueOf(job.getKillPrice("cow"))));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(Arrays.asList("COW"), "COW"));
	}

	@Test
	public void getFisherPriceTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("fish", 1.5));
		assertEquals("1.5", String.valueOf(assertDoesNotThrow(() -> job.getFisherPrice("fish"))));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(Arrays.asList("fish"), "fish"));
	}
	
	@Test
	public void getBlockPriceTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBlock("stone", 1.5));
		assertDoesNotThrow(() -> assertEquals("1.5", String.valueOf(job.getBlockPrice("stone"))));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(Arrays.asList("STONE"), "STONE"));
	}

	@Test
	public void getBreedPriceTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBreedable(EntityType.COW, 1.5));
		assertDoesNotThrow(() -> assertEquals("1.5", String.valueOf(job.getBreedPrice(EntityType.COW))));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(Arrays.asList("COW"), "COW"));
	}

	@Test
	public void getNameTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertEquals("myJob", job.getName());
	}
	
	@Test
	public void addBreedableTestWithInvalidBreedable() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueInList(JobImpl.breedableMobs, EntityType.COD);
		assertThrows(JobsystemException.class, () -> job.addBreedable(EntityType.COD, 1.5));
		verify(jobDao, never()).saveBlockList(anyMap());
		assertEquals(0, job.getBlockList().size());
	}

	@Test
	public void addBreedableTestWithInvalidPrice() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueGreaterZero(-1.0);
		assertThrows(JobsystemException.class, () -> job.addBreedable(EntityType.COW, -1.0));
		verify(jobDao, never()).saveBreedableList(anyMap());
		assertEquals(0, job.getBreedableList().size());
	}

	@Test
	public void addBreedableTestWithAlreadyInJob() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueNotInList(anyList(), eq("COW"));
		assertThrows(JobsystemException.class, () -> job.addBreedable(EntityType.COW, 1.5));
		verify(jobDao, never()).saveBreedableList(anyMap());
		assertEquals(0, job.getBreedableList().size());
	}

	@Test
	public void addBreedableTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBreedable(EntityType.COW, 1.5));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(JobImpl.breedableMobs, EntityType.COW));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueGreaterZero(1.5));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueNotInList(anyList(), eq("COW")));
		verify(jobDao).saveBreedableList(anyMap());
		assertEquals(1, job.getBreedableList().size());
		assertTrue(job.getBreedableList().containsKey("COW"));
		assertTrue(job.getBreedableList().containsValue(1.5));
		Map<String, Double> list = job.getBreedableList();
		assertEquals(1, list.size());
		assertTrue(list.containsKey("COW"));
		assertTrue(list.containsValue(1.5));
	}

	@Test
	public void addBlockTestWithInvalidMaterial() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValidEnum(Material.values(), "DSADAS");
		assertThrows(JobsystemException.class, () -> job.addBlock("dsadas", 1.0));
		verify(jobDao, never()).saveBlockList(anyMap());
		assertEquals(0, job.getBlockList().size());
	}

	@Test
	public void addBlockTestWithInvalidPrice() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueGreaterZero(-1.0);
		assertThrows(JobsystemException.class, () -> job.addBlock("stone", -1.0));
		verify(jobDao, never()).saveBlockList(anyMap());
		assertEquals(0, job.getBlockList().size());
	}

	@Test
	public void addBlockTestWithAlreadyInJob() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueNotInList(anyList(), eq("STONE"));
		assertThrows(JobsystemException.class, () -> job.addBlock("stone", 1.0));
		verify(jobDao, never()).saveBlockList(anyMap());
		assertEquals(0, job.getBlockList().size());
	}

	@Test
	public void addBlockTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBlock("stone", 1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidEnum(Material.values(), "STONE"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueGreaterZero(1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueNotInList(anyList(), eq("STONE")));
		verify(jobDao).saveBlockList(anyMap());
		assertEquals(1, job.getBlockList().size());
		assertTrue(job.getBlockList().containsKey("STONE"));
		assertTrue(job.getBlockList().containsValue(1.0));
		Map<String, Double> list = job.getBlockList();
		assertEquals(1, list.size());
		assertTrue(list.containsKey("STONE"));
		assertTrue(list.containsValue(1.0));
	}

	@Test
	public void addMobTestWithInvalidEntity() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValidEnum(EntityType.values(), "DSADAS");
		assertThrows(JobsystemException.class, () -> job.addMob("dsadas", 1.0));
		verify(jobDao, never()).saveEntityList(anyMap());
		assertEquals(0, job.getEntityList().size());
	}

	@Test
	public void addMobTestWithInvalidPrice() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueGreaterZero(-1.0);
		assertThrows(JobsystemException.class, () -> job.addMob("cow", -1.0));
		verify(jobDao, never()).saveEntityList(anyMap());
		assertEquals(0, job.getEntityList().size());
	}

	@Test
	public void addMobTestWithAlreadyInJob() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueNotInList(anyList(), eq("COW"));
		assertThrows(JobsystemException.class, () -> job.addMob("cow", 1.0));
		verify(jobDao, never()).saveEntityList(anyMap());
		assertEquals(0, job.getEntityList().size());
	}

	@Test
	public void addMobTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addMob("cow", 1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidEnum(EntityType.values(), "COW"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueNotInList(anyList(), eq("COW")));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueGreaterZero(1.0));
		verify(jobDao).saveEntityList(anyMap());
		assertEquals(1, job.getEntityList().size());
		assertTrue(job.getEntityList().containsKey("COW"));
		assertTrue(job.getEntityList().containsValue(1.0));
	}

	@Test
	public void addFisherLootTypeTestWithInvalidType() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValidEnum(FishingLootTypeEnum.values(), "dsadas");
		assertThrows(JobsystemException.class, () -> job.addFisherLootType("dsadas", 1.0));
		verify(jobDao, never()).saveFisherList(anyMap());
		assertEquals(0, job.getFisherList().size());
	}

	@Test
	public void addFisherLootTypeTestWithInvalidPrice() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueGreaterZero(-1.0);
		assertThrows(JobsystemException.class, () -> job.addFisherLootType("fish", -1.0));
		verify(jobDao, never()).saveFisherList(anyMap());
		assertEquals(0, job.getFisherList().size());
	}

	@Test
	public void addFisherLootTypeTestWithAlreadyInJob() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueNotInList(anyList(), eq("fish"));
		assertThrows(JobsystemException.class, () -> job.addFisherLootType("fish", 1.0));
		verify(jobDao, never()).saveFisherList(anyMap());
		assertEquals(0, job.getFisherList().size());
	}

	@Test
	public void addFisherLootTypeTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("fish", 1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidEnum(FishingLootTypeEnum.values(), "fish"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueGreaterZero(1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueNotInList(anyList(), eq("fish")));
		verify(jobDao).saveFisherList(anyMap());
		assertEquals(1, job.getFisherList().size());
		assertTrue(job.getFisherList().containsKey("fish"));
		assertTrue(job.getFisherList().containsValue(1.0));
	}
	
	@Test
	public void deleteBreedableTestWithInvalidBreedable() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBreedable(EntityType.COD, 1.5));
		reset(jobDao);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueInList(JobImpl.breedableMobs, EntityType.COD);
		assertThrows(JobsystemException.class, () -> job.deleteBreedable(EntityType.COD));
		verify(jobDao, never()).saveBreedableList(anyMap());
		assertEquals(1, job.getBreedableList().size());
	}

	@Test
	public void deleteBreedableTestWithNotInJob() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBreedable(EntityType.COW, 1.5));
		reset(jobDao);
		doNothing().when(validationHandler).checkForValueInList(JobImpl.breedableMobs, EntityType.SHEEP);;
		doThrow(JobsystemException.class).when(validationHandler).checkForValueInList(anyList(), eq("SHEEP"));
		assertThrows(JobsystemException.class, () -> job.deleteBreedable(EntityType.SHEEP));
		verify(jobDao, never()).saveBreedableList(anyMap());
		assertEquals(1, job.getBreedableList().size());
	}

	@Test
	public void deleteBreedableTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBreedable(EntityType.COW, 1.5));
		reset(validationHandler);
		reset(jobDao);
		assertDoesNotThrow(() -> job.deleteBreedable(EntityType.COW));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(JobImpl.breedableMobs, EntityType.COW));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(anyList(), eq("COW")));
		verify(jobDao).saveBreedableList(anyMap());
		assertEquals(0, job.getBreedableList().size());
	}

	@Test
	public void deleteBlockTestWithInvalidBlock() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBlock("stone", 1.5));
		reset(jobDao);
		doThrow(JobsystemException.class).when(validationHandler).checkForValidEnum(Material.values(), "STONE");
		assertThrows(JobsystemException.class, () -> job.deleteBlock("stone"));
		verify(jobDao, never()).saveBlockList(anyMap());
		assertEquals(1, job.getBlockList().size());
	}

	@Test
	public void deleteBlockTestWithNotInJob() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBlock("stone", 1.5));
		reset(jobDao);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueInList(anyList(), eq("STONE"));
		assertThrows(JobsystemException.class, () -> job.deleteBlock("stone"));
		verify(jobDao, never()).saveBlockList(anyMap());
		assertEquals(1, job.getBlockList().size());
	}

	@Test
	public void deleteBlockTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBlock("stone", 1.0));
		reset(validationHandler);
		reset(jobDao);
		assertDoesNotThrow(() -> job.deleteBlock("stone"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidEnum(Material.values(), "STONE"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(anyList(), eq("STONE")));
		verify(jobDao).saveBlockList(anyMap());
		assertEquals(0, job.getBlockList().size());
	}

	@Test
	public void deleteMobTestWithInvalidMob() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addMob("cow", 1.5));
		reset(jobDao);
		doThrow(JobsystemException.class).when(validationHandler).checkForValidEnum(EntityType.values(), "BLA");
		assertThrows(JobsystemException.class, () -> job.deleteMob("bla"));
		verify(jobDao, never()).saveEntityList(anyMap());
		assertEquals(1, job.getEntityList().size());
	}

	@Test
	public void deleteMobTestWithNotInJob() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addMob("cow", 1.5));
		reset(jobDao);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueInList(anyList(), eq("COW"));
		assertThrows(JobsystemException.class, () -> job.deleteMob("COW"));
		verify(jobDao, never()).saveEntityList(anyMap());
		assertEquals(1, job.getEntityList().size());
	}

	@Test
	public void deleteMobTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addMob("cow", 1.0));
		reset(validationHandler);
		reset(jobDao);
		assertDoesNotThrow(() -> job.deleteMob("cow"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidEnum(EntityType.values(), "COW"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(anyList(), eq("COW")));
		verify(jobDao).saveEntityList(anyMap());
		assertEquals(0, job.getEntityList().size());
	}

	@Test
	public void delFisherLootTypeTestWithInvalidType() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("DDADA", 1.5));
		reset(jobDao);
		doThrow(JobsystemException.class).when(validationHandler).checkForValidEnum(FishingLootTypeEnum.values(), "DDADA");
		assertThrows(JobsystemException.class, () -> job.removeFisherLootType("DDADA"));
		verify(jobDao, never()).saveFisherList(anyMap());
		assertEquals(1, job.getFisherList().size());
	}

	@Test
	public void delFisherLootTypeTestWithNotInJob() throws JobsystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("DDADA", 1.5));
		reset(jobDao);
		doThrow(JobsystemException.class).when(validationHandler).checkForValueInList(anyList(), eq("DDADA"));
		assertThrows(JobsystemException.class, () -> job.removeFisherLootType("DDADA"));
		verify(jobDao, never()).saveFisherList(anyMap());
		assertEquals(1, job.getFisherList().size());
	}

	@Test
	public void delFisherLootTypeTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("fish", 1.0));
		reset(validationHandler);
		reset(jobDao);
		assertDoesNotThrow(() -> job.removeFisherLootType("fish"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidEnum(FishingLootTypeEnum.values(), "fish"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(anyList(), eq("fish")));
		verify(jobDao).saveFisherList(anyMap());
		assertEquals(0, job.getFisherList().size());
	}

	@Test
	public void deleteJobTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		job.deleteJob();
		verify(jobDao).deleteSavefile();
	}
}
