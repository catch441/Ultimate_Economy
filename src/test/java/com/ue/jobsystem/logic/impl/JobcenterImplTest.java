package com.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;

@ExtendWith(MockitoExtension.class)
public class JobcenterImplTest {

	@Mock
	JobManager jobManager;
	@Mock
	JobcenterManager jobcenterManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	JobsystemValidationHandler jobsystemValidationHandler;
	@Mock
	JobcenterDao jobcenterDao;
	@Mock
	ServerProvider serverProvider;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	Logger logger;
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@Test
	public void constructorNewTest() throws JobSystemException {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		Location location = mock(Location.class);
		World world = mock(World.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Chunk chunk = mock(Chunk.class);
		Entity entity = mock(Entity.class);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, 9, "center")).thenReturn(inventory);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(inventory.getSize()).thenReturn(9);
		when(world.getNearbyEntities(location, 10, 10, 10)).thenReturn(Arrays.asList(entity));
		when(entity.getCustomName()).thenReturn("center");
		Jobcenter center = new JobcenterImpl(logger, jobcenterDao, jobManager, jobcenterManager, ecoPlayerManager,
				jobsystemValidationHandler, serverProvider, "center", location, 9, generalValidator);
		verify(jobcenterDao).setupSavefile("center");
		verify(jobcenterDao).saveJobcenterName("center");
		verify(jobcenterDao).saveJobcenterSize(9);
		verify(jobcenterDao).saveJobcenterLocation(location);
		verify(villager).setCustomName("center");
		verify(villager).setMetadata(eq("ue-type"), any());
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setCollidable(false);
		verify(villager).setInvulnerable(true);
		verify(serverProvider).createInventory(villager, 9, "center");
		verify(meta).setLore(Arrays.asList(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "Join",
				ChatColor.GOLD + "Rightclick: " + ChatColor.RED + "Leave"));
		verify(meta).setDisplayName("Info");
		verify(inventory).setItem(eq(8), eq(stack));
		verify(entity).remove();
		assertEquals("center", center.getName());
		assertEquals(location, center.getJobcenterLocation());
	}

	@Test
	public void constructorLoadTest() {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		Location location = mock(Location.class);
		World world = mock(World.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Chunk chunk = mock(Chunk.class);
		Job job = mock(Job.class);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, 9, "center")).thenReturn(inventory);
		when(jobcenterDao.loadJobcenterLocation()).thenReturn(location);
		when(jobcenterDao.loadJobcenterSize()).thenReturn(9);
		when(inventory.getSize()).thenReturn(9);
		when(jobcenterDao.loadJobNameList()).thenReturn(Arrays.asList("myJob"));
		when(jobcenterDao.loadJobItemMaterial(job)).thenReturn(Material.STONE);
		when(jobcenterDao.loadJobSlot(job)).thenReturn(1);
		when(job.getName()).thenReturn("myJob");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myJob")).thenReturn(job));

		Jobcenter center = new JobcenterImpl(logger, jobcenterDao, jobManager, jobcenterManager, ecoPlayerManager,
				jobsystemValidationHandler, serverProvider, "center", generalValidator);
		verify(jobcenterDao).setupSavefile("center");
		verify(villager).setCustomName("center");
		verify(villager).setMetadata(eq("ue-type"), any());
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setCollidable(false);
		verify(villager).setInvulnerable(true);
		verify(meta).setLore(Arrays.asList(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "Join",
				ChatColor.GOLD + "Rightclick: " + ChatColor.RED + "Leave"));
		verify(meta).setDisplayName("Info");
		// jobitem
		verify(meta).setDisplayName("myJob");
		verify(inventory).setItem(eq(8), eq(stack));
		verify(inventory).setItem(eq(1), eq(stack));
		assertEquals("center", center.getName());

		assertEquals(location, center.getJobcenterLocation());
		assertEquals(1, center.getJobList().size());
		assertEquals(job, center.getJobList().get(0));
	}
	
	@Test
	public void constructorLoadTestWithFailedToLoadJob() throws GeneralEconomyException {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		Location location = mock(Location.class);
		World world = mock(World.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Chunk chunk = mock(Chunk.class);
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		doThrow(e).when(jobManager).getJobByName("myJob");
		when(e.getMessage()).thenReturn("my error message");
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, 9, "center")).thenReturn(inventory);
		when(jobcenterDao.loadJobcenterLocation()).thenReturn(location);
		when(jobcenterDao.loadJobcenterSize()).thenReturn(9);
		when(inventory.getSize()).thenReturn(9);
		when(jobcenterDao.loadJobNameList()).thenReturn(Arrays.asList("myJob"));
		assertThrows(GeneralEconomyException.class, () -> jobManager.getJobByName("myJob"));
		Jobcenter center = new JobcenterImpl(logger, jobcenterDao, jobManager, jobcenterManager, ecoPlayerManager,
				jobsystemValidationHandler, serverProvider, "center", generalValidator);
		verify(jobcenterDao).setupSavefile("center");
		verify(villager).setCustomName("center");
		verify(villager).setMetadata(eq("ue-type"), any());
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setCollidable(false);
		verify(villager).setInvulnerable(true);
		verify(meta).setLore(Arrays.asList(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "Join",
				ChatColor.GOLD + "Rightclick: " + ChatColor.RED + "Leave"));
		verify(meta).setDisplayName("Info");
		// jobitem
		verify(inventory).setItem(eq(8), eq(stack));
		assertEquals("center", center.getName());
		assertEquals(location, center.getJobcenterLocation());
		assertEquals(0, center.getJobList().size());
		verify(logger).warn("[Ultimate_Economy] Failed to load the job myJob for the jobcenter center");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
	}

	@Test
	public void getNameTest() {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		Jobcenter center = createJobcenter(null, villager, inventory);
		assertEquals("center", center.getName());
	}

	@Test
	public void hasJobTest() {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		Job job = mock(Job.class);
		Job job2 = mock(Job.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		Jobcenter center = createJobcenter(null, villager, inventory);
		assertDoesNotThrow(() -> center.addJob(job, "stone", 1));
		assertTrue(center.hasJob(job));
		assertFalse(center.hasJob(job2));
	}

	@Test
	public void getJobListTest() {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		Job job = mock(Job.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		Jobcenter center = createJobcenter(null, villager, inventory);
		assertDoesNotThrow(() -> center.addJob(job, "stone", 1));
		assertEquals(1, center.getJobList().size());
		assertEquals(job, center.getJobList().get(0));
	}

	@Test
	public void despawnVillagerTest() {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		Jobcenter center = createJobcenter(null, villager, inventory);
		center.despawnVillager();
		verify(villager).remove();
	}

	@Test
	public void moveJobcenterTest() {
		Inventory inventory = mock(Inventory.class);
		Location location = mock(Location.class);
		Villager villager = mock(Villager.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		Jobcenter center = createJobcenter(null, villager, inventory);
		center.moveJobcenter(location);
		verify(jobcenterDao).saveJobcenterLocation(location);
		verify(villager).teleport(location);
		assertEquals(location, center.getJobcenterLocation());
	}

	@Test
	public void openInvTest() {
		Inventory inventory = mock(Inventory.class);
		Player player = mock(Player.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		Jobcenter center = createJobcenter(null, mock(Villager.class), inventory);
		center.openInv(player);
		verify(player).openInventory(inventory);
	}

	@Test
	public void deleteJobcenterTest() {
		Villager villager = mock(Villager.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		Jobcenter center = createJobcenter(null, villager, mock(Inventory.class));
		center.deleteJobcenter();
		verify(jobcenterDao).deleteSavefile();
		verify(villager).remove();
	}

	@Test
	public void removeJobTest() {
		Inventory inventory = mock(Inventory.class);
		Job job = mock(Job.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		EconomyPlayer ecoPlayer2 = mock(EconomyPlayer.class);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer2, ecoPlayer));
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		when(ecoPlayer2.hasJob(job)).thenReturn(false);
		when(job.getName()).thenReturn("myJob");
		when(jobcenterDao.loadJobSlot(job)).thenReturn(4);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		Jobcenter center = createJobcenter(null, mock(Villager.class), inventory);
		assertDoesNotThrow(() -> center.addJob(job, "stone", 4));
		assertDoesNotThrow(() -> center.removeJob(job));

		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForJobExistsInJobcenter(anyList(), eq(job)));
		verify(jobcenterDao).saveJob(job, null, 0);
		verify(jobcenterDao).saveJobNameList(new ArrayList<>());
		verify(inventory).clear(4);
		assertDoesNotThrow(() -> verify(ecoPlayer).leaveJob(job, false));
		verifyNoMoreInteractions(ecoPlayer2);

		assertEquals(0, center.getJobList().size());
	}

	@Test
	public void removeJobTestWithFailedToLeaveJob() throws EconomyPlayerException {
		Inventory inventory = mock(Inventory.class);
		Job job = mock(Job.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		when(job.getName()).thenReturn("myJob");
		when(jobcenterDao.loadJobSlot(job)).thenReturn(4);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(ecoPlayer).leaveJob(job, false);
		Jobcenter center = createJobcenter(null, mock(Villager.class), inventory);
		assertDoesNotThrow(() -> center.addJob(job, "stone", 4));
		assertDoesNotThrow(() -> center.removeJob(job));
		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForJobExistsInJobcenter(anyList(), eq(job)));
		verify(jobcenterDao).saveJob(job, null, 0);
		verify(jobcenterDao).saveJobNameList(new ArrayList<>());
		verify(inventory).clear(4);
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.leaveJob(job, false));
		assertEquals(0, center.getJobList().size());
		verify(logger).warn("[Ultimate_Economy] Failed to leave the job myJob");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
	}

	@Test
	public void removeJobTestWithJobInOtherJocenter() {
		Inventory inventory = mock(Inventory.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		Jobcenter center = createJobcenter(null, mock(Villager.class), inventory);
		Jobcenter center2 = createJobcenter(null, mock(Villager.class), inventory);
		Job job = mock(Job.class);
		when(jobcenterManager.getJobcenterList()).thenReturn(Arrays.asList(center, center2));
		when(jobcenterDao.loadJobSlot(job)).thenReturn(4);
		assertDoesNotThrow(() -> center.addJob(job, "stone", 4));
		assertDoesNotThrow(() -> center2.addJob(job, "stone", 4));
		assertDoesNotThrow(() -> center.removeJob(job));
		verifyNoInteractions(ecoPlayerManager);
		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForJobExistsInJobcenter(anyList(), eq(job)));
		verify(jobcenterDao).saveJob(job, null, 0);
		verify(jobcenterDao).saveJobNameList(new ArrayList<>());
		verify(inventory).clear(4);
		assertEquals(0, center.getJobList().size());
	}

	@Test
	public void addJobTest() {
		Job job = mock(Job.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inventory = mock(Inventory.class);
		when(job.getName()).thenReturn("myJob");
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		Jobcenter center = createJobcenter(null, mock(Villager.class), inventory);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		assertDoesNotThrow(() -> center.addJob(job, "stone", 4));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(4, 8));
		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForFreeSlot(inventory, 4));
		assertDoesNotThrow(
				() -> verify(jobsystemValidationHandler).checkForJobDoesNotExistInJobcenter(anyList(), eq(job)));
		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForValidMaterial("STONE"));
		verify(jobcenterDao).saveJob(job, "STONE", 4);
		verify(jobcenterDao).saveJobNameList(Arrays.asList("myJob"));
		verify(meta).setDisplayName("myJob");
		verify(meta).addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		verify(inventory).setItem(eq(4), eq(stack));
		assertEquals(1, center.getJobList().size());
		assertEquals(job, center.getJobList().get(0));
	}

	private Jobcenter createJobcenter(Location location, Villager villager, Inventory invMock) {
		if (location == null) {
			location = mock(Location.class);
		}
		World world = mock(World.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Chunk chunk = mock(Chunk.class);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, 9, "center")).thenReturn(invMock);
		try {
			return new JobcenterImpl(logger, jobcenterDao, jobManager, jobcenterManager, ecoPlayerManager,
					jobsystemValidationHandler, serverProvider, "center", location, 9, generalValidator);
		} catch (JobSystemException e) {
			fail();
		}
		return null;
	}
}
