package com.ue.jobsystem.logic.impl;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyInt;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.ServiceComponent;
import com.ue.common.utils.MessageWrapper;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;

import dagger.Lazy;

@ExtendWith(MockitoExtension.class)
public class JobcenterManagerImplTest {

	@InjectMocks
	JobcenterManagerImpl manager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	JobsystemValidationHandler validationHandler;
	@Mock
	ServerProvider serverProvider;
	@Mock
	Lazy<JobManager> jobManager;
	@Mock
	ConfigDao configDao;
	@Mock
	Logger logger;
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@Test
	public void getJobcenterByNameTest() {
		createJobcenter("other");
		createJobcenter("center");
		Jobcenter result = assertDoesNotThrow(() -> manager.getJobcenterByName("center"));
		assertEquals("center", result.getName());

		new File("src/other-JobCenter.yml").delete();
		new File("src/center-JobCenter.yml").delete();
	}

	private void createJobcenter(String name) {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		when(serverProvider.createInventory(villager, 9, name)).thenReturn(inventory);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		JavaPlugin plugin = mock(JavaPlugin.class);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		Location location = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getJobcenterDao()).thenReturn(mock(JobcenterDao.class));
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> manager.createJobcenter(name, location, 9));
	}

	@Test
	public void getJobcenterByNameTestWithNoJobcenter() {
		try {
			manager.getJobcenterByName("center");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("center", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void getJobcenterNameListTest() {
		createJobcenter("center");
		List<String> list = manager.getJobcenterNameList();
		assertEquals(1, list.size());
		assertEquals("center", list.get(0));
		new File("src/center-JobCenter.yml").delete();
	}

	@Test
	public void getJobcenterListTest() {
		createJobcenter("center");
		List<Jobcenter> centers = manager.getJobcenterList();
		assertEquals(1, centers.size());
		assertEquals("center", centers.get(0).getName());
		new File("src/center-JobCenter.yml").delete();
	}

	@Test
	public void despawnAllVillagersTest() {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		when(serverProvider.createInventory(villager, 9, "center")).thenReturn(inventory);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		JavaPlugin plugin = mock(JavaPlugin.class);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		Location location = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getJobcenterDao()).thenReturn(mock(JobcenterDao.class));
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> manager.createJobcenter("center", location, 9));

		manager.despawnAllVillagers();
		verify(villager).remove();
		new File("src/center-JobCenter.yml").delete();
	}

	@Test
	public void deleteJobcenterTest() {
		Jobcenter jobcenter = mock(Jobcenter.class);
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		when(jobcenter.getJobList()).thenReturn(Arrays.asList(job0, job));
		manager.getJobcenterList().add(jobcenter);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		when(ecoPlayer.hasJob(job0)).thenReturn(true);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		assertDoesNotThrow(() -> manager.deleteJobcenter(jobcenter));
		verify(jobcenter).deleteJobcenter();
		assertEquals(0, manager.getJobcenterList().size());
		assertDoesNotThrow(() -> verify(ecoPlayer).leaveJob(job, false));
		verify(configDao).saveJobcenterList(new ArrayList<>());
	}
	
	@Test
	public void deleteJobcenterTestWithFailedToLeaveJob() throws EconomyPlayerException {
		Jobcenter jobcenter = mock(Jobcenter.class);
		Job job = mock(Job.class);
		when(jobcenter.getJobList()).thenReturn(Arrays.asList(job));
		manager.getJobcenterList().add(jobcenter);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(job.getName()).thenReturn("myJob");
		when(e.getMessage()).thenReturn("my error message");
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		doThrow(e).when(ecoPlayer).leaveJob(job, false);
		assertDoesNotThrow(() -> manager.deleteJobcenter(jobcenter));
		verify(jobcenter).deleteJobcenter();
		assertEquals(0, manager.getJobcenterList().size());
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.leaveJob(job, false));
		verify(configDao).saveJobcenterList(new ArrayList<>());
		verify(logger).warn("[Ultimate_Economy] Failed to leave the job myJob");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
	}
	
	@Test
	public void deleteJobcenterTestWithJobInOtherJobcenter() {
		Jobcenter jobcenter0 = mock(Jobcenter.class);
		when(jobcenter0.getName()).thenReturn("center0");
		Jobcenter jobcenter = mock(Jobcenter.class);
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		when(jobcenter0.hasJob(job)).thenReturn(true);
		when(jobcenter0.hasJob(job0)).thenReturn(true);
		when(jobcenter.getJobList()).thenReturn(Arrays.asList(job0, job));
		manager.getJobcenterList().add(jobcenter);
		manager.getJobcenterList().add(jobcenter0);
		assertDoesNotThrow(() -> manager.deleteJobcenter(jobcenter));
		verify(jobcenter).deleteJobcenter();
		assertEquals(1, manager.getJobcenterList().size());
		verify(configDao).saveJobcenterList(Arrays.asList("center0"));
		assertDoesNotThrow(() -> verify(ecoPlayerManager, never()).getAllEconomyPlayers());
	}

	@Test
	public void createJobcenterTestWithAlreadyExists() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValueNotInList(new ArrayList<>(), "center");
		assertThrows(GeneralEconomyException.class, () -> manager.createJobcenter("center", null, 9));
		assertEquals(0, manager.getJobcenterList().size());
		verify(configDao, never()).saveJobcenterList(anyList());
	}

	@Test
	public void createJobcenterTestWithInvalidSize() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSize(9);
		assertThrows(GeneralEconomyException.class, () -> manager.createJobcenter("center", null, 9));
		assertEquals(0, manager.getJobcenterList().size());
		verify(configDao, never()).saveJobcenterList(anyList());
	}

	@Test
	public void createJobcenterTest() {
		createJobcenter("center");
		assertDoesNotThrow(
				() -> verify(generalValidator).checkForValueNotInList(new ArrayList<>(), "center"));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSize(9));
		verify(configDao).saveJobcenterList(Arrays.asList("center"));
		Jobcenter center = manager.getJobcenterList().get(0);
		assertEquals("center", center.getName());
	}

	@Test
	public void loadAllJobcenterTest() {
		Villager villager = mock(Villager.class);
		Inventory inventory = mock(Inventory.class);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("center"))).thenReturn(inventory);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		JavaPlugin plugin = mock(JavaPlugin.class);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		Location location = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		when(configDao.loadJobcenterList()).thenReturn(Arrays.asList("center"));
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		JobcenterDao jobcenterDao = mock(JobcenterDao.class);
		when(jobcenterDao.loadJobcenterLocation()).thenReturn(location);
		when(serviceComponent.getJobcenterDao()).thenReturn(jobcenterDao);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		manager.loadAllJobcenters();
		
		Jobcenter center = manager.getJobcenterList().get(0);
		assertEquals("center", center.getName());
		verify(jobcenterDao).loadJobcenterSize();

		new File("src/center-JobCenter.yml").delete();
	}
}
