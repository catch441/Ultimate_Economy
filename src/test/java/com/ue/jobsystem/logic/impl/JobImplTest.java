package com.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.ultimate_economy.GeneralEconomyException;

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
		Job job = new JobImpl(validationHandler, jobDao, "myJob", false);
		verify(jobDao).setupSavefile("myJob");
		assertEquals("myJob", job.getName());
		assertTrue(job.getBlockList().containsKey("DIRT"));
		assertTrue(job.getBlockList().containsValue(1.0));
		assertTrue(job.getFisherList().containsKey("fish"));
		assertTrue(job.getFisherList().containsValue(2.0));
		assertTrue(job.getEntityList().containsKey("COW"));
		assertTrue(job.getEntityList().containsValue(3.0));
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
	}

	@Test
	public void getFisherPriceTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("fish", 1.5));
		assertEquals("1.5", String.valueOf(assertDoesNotThrow(() -> job.getFisherPrice("fish"))));
	}

	@Test
	public void getBlockPriceTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBlock("stone", 1.5));
		assertDoesNotThrow(() -> assertEquals("1.5", String.valueOf(job.getBlockPrice("stone"))));
	}

	@Test
	public void getNameTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertEquals("myJob", job.getName());
	}

	@Test
	public void addBlockTestWithInvalidMaterial() throws GeneralEconomyException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidMaterial("DSADAS");
		assertThrows(GeneralEconomyException.class, () -> job.addBlock("dsadas", 1.0));
		verify(jobDao, never()).saveBlockList(anyMap());
		assertEquals(0, job.getBlockList().size());
	}

	@Test
	public void addBlockTestWithInvalidPrice() throws GeneralEconomyException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForPositivValue(-1.0);
		assertThrows(GeneralEconomyException.class, () -> job.addBlock("stone", -1.0));
		verify(jobDao, never()).saveBlockList(anyMap());
		assertEquals(0, job.getBlockList().size());
	}

	@Test
	public void addBlockTestWithAlreadyInJob() throws JobSystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobSystemException.class).when(validationHandler).checkForBlockNotInJob(anyMap(), eq("STONE"));
		assertThrows(JobSystemException.class, () -> job.addBlock("stone", 1.0));
		verify(jobDao, never()).saveBlockList(anyMap());
		assertEquals(0, job.getBlockList().size());
	}

	@Test
	public void addBlockTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBlock("stone", 1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidMaterial("STONE"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositivValue(1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForBlockNotInJob(anyMap(), eq("STONE")));
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
	public void addMobTestWithInvalidEntity() throws GeneralEconomyException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidEntityType("DSADAS");
		assertThrows(GeneralEconomyException.class, () -> job.addMob("dsadas", 1.0));
		verify(jobDao, never()).saveEntityList(anyMap());
		assertEquals(0, job.getEntityList().size());
	}

	@Test
	public void addMobTestWithInvalidPrice() throws GeneralEconomyException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForPositivValue(-1.0);
		assertThrows(GeneralEconomyException.class, () -> job.addMob("cow", -1.0));
		verify(jobDao, never()).saveEntityList(anyMap());
		assertEquals(0, job.getEntityList().size());
	}

	@Test
	public void addMobTestWithAlreadyInJob() throws JobSystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobSystemException.class).when(validationHandler).checkForEntityNotInJob(anyMap(), eq("COW"));
		assertThrows(JobSystemException.class, () -> job.addMob("cow", 1.0));
		verify(jobDao, never()).saveEntityList(anyMap());
		assertEquals(0, job.getEntityList().size());
	}

	@Test
	public void addMobTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addMob("cow", 1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidEntityType("COW"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForEntityNotInJob(anyMap(), eq("COW")));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositivValue(1.0));
		verify(jobDao).saveEntityList(anyMap());
		assertEquals(1, job.getEntityList().size());
		assertTrue(job.getEntityList().containsKey("COW"));
		assertTrue(job.getEntityList().containsValue(1.0));
	}

	@Test
	public void addFisherLootTypeTestWithInvalidType() throws GeneralEconomyException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidFisherLootType("dsadas");
		assertThrows(GeneralEconomyException.class, () -> job.addFisherLootType("dsadas", 1.0));
		verify(jobDao, never()).saveFisherList(anyMap());
		assertEquals(0, job.getFisherList().size());
	}

	@Test
	public void addFisherLootTypeTestWithInvalidPrice() throws GeneralEconomyException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForPositivValue(-1.0);
		assertThrows(GeneralEconomyException.class, () -> job.addFisherLootType("fish", -1.0));
		verify(jobDao, never()).saveFisherList(anyMap());
		assertEquals(0, job.getFisherList().size());
	}

	@Test
	public void addFisherLootTypeTestWithAlreadyInJob() throws JobSystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		doThrow(JobSystemException.class).when(validationHandler).checkForLoottypeNotInJob(anyMap(), eq("fish"));
		assertThrows(JobSystemException.class, () -> job.addFisherLootType("fish", 1.0));
		verify(jobDao, never()).saveFisherList(anyMap());
		assertEquals(0, job.getFisherList().size());
	}

	@Test
	public void addFisherLootTypeTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("fish", 1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidFisherLootType("fish"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositivValue(1.0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForLoottypeNotInJob(anyMap(), eq("fish")));
		verify(jobDao).saveFisherList(anyMap());
		assertEquals(1, job.getFisherList().size());
		assertTrue(job.getFisherList().containsKey("fish"));
		assertTrue(job.getFisherList().containsValue(1.0));
	}

	@Test
	public void deleteBlockTestWithInvalidBlock() throws GeneralEconomyException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBlock("stone", 1.5));
		reset(jobDao);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidMaterial("STONE");
		assertThrows(GeneralEconomyException.class, () -> job.deleteBlock("stone"));
		verify(jobDao, never()).saveBlockList(anyMap());
		assertEquals(1, job.getBlockList().size());
	}

	@Test
	public void deleteBlockTestWithNotInJob() throws JobSystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addBlock("stone", 1.5));
		reset(jobDao);
		doThrow(JobSystemException.class).when(validationHandler).checkForBlockInJob(anyMap(), eq("STONE"));
		assertThrows(JobSystemException.class, () -> job.deleteBlock("stone"));
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
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidMaterial("STONE"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForBlockInJob(anyMap(), eq("STONE")));
		verify(jobDao).saveBlockList(anyMap());
		assertEquals(0, job.getBlockList().size());
	}

	@Test
	public void deleteMobTestWithInvalidMob() throws GeneralEconomyException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addMob("cow", 1.5));
		reset(jobDao);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidEntityType("BLA");
		assertThrows(GeneralEconomyException.class, () -> job.deleteMob("bla"));
		verify(jobDao, never()).saveEntityList(anyMap());
		assertEquals(1, job.getEntityList().size());
	}

	@Test
	public void deleteMobTestWithNotInJob() throws JobSystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addMob("cow", 1.5));
		reset(jobDao);
		doThrow(JobSystemException.class).when(validationHandler).checkForEntityInJob(anyMap(), eq("COW"));
		assertThrows(JobSystemException.class, () -> job.deleteMob("COW"));
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
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidEntityType("COW"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForEntityInJob(anyMap(), eq("COW")));
		verify(jobDao).saveEntityList(anyMap());
		assertEquals(0, job.getEntityList().size());
	}

	@Test
	public void delFisherLootTypeTestWithInvalidType() throws GeneralEconomyException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("DDADA", 1.5));
		reset(jobDao);
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidFisherLootType("DDADA");
		assertThrows(GeneralEconomyException.class, () -> job.delFisherLootType("DDADA"));
		verify(jobDao, never()).saveFisherList(anyMap());
		assertEquals(1, job.getFisherList().size());
	}

	@Test
	public void delFisherLootTypeTestWithNotInJob() throws JobSystemException {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("DDADA", 1.5));
		reset(jobDao);
		doThrow(JobSystemException.class).when(validationHandler).checkForLoottypeInJob(anyMap(), eq("DDADA"));
		assertThrows(JobSystemException.class, () -> job.delFisherLootType("DDADA"));
		verify(jobDao, never()).saveFisherList(anyMap());
		assertEquals(1, job.getFisherList().size());
	}

	@Test
	public void delFisherLootTypeTest() {
		Job job = new JobImpl(validationHandler, jobDao, "myJob", true);
		assertDoesNotThrow(() -> job.addFisherLootType("fish", 1.0));
		reset(validationHandler);
		reset(jobDao);
		assertDoesNotThrow(() -> job.delFisherLootType("fish"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidFisherLootType("fish"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForLoottypeInJob(anyMap(), eq("fish")));
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
