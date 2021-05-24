package org.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anySet;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.UltimateEconomyProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.jobsystem.dataaccess.api.JobcenterDao;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemException;
import org.ue.jobsystem.logic.api.JobsystemValidator;

@ExtendWith(MockitoExtension.class)
public class JobcenterImplTest {

	@InjectMocks
	JobcenterImpl jobcenter;
	@Mock
	JobManager jobManager;
	@Mock
	JobcenterManager jobcenterManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	JobsystemValidator jobsystemValidationHandler;
	@Mock
	JobcenterDao jobcenterDao;
	@Mock
	ServerProvider serverProvider;

	@Test
	public void setupNewTest() throws JobsystemException {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		Location location = mock(Location.class);
		World world = mock(World.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Chunk chunk = mock(Chunk.class);
		Entity entity = mock(Entity.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, 9, "center")).thenReturn(inventory);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(world.getNearbyEntities(location, 10, 10, 10)).thenReturn(Arrays.asList(entity));
		when(entity.getName()).thenReturn("center");
		jobcenter.setupNew("center", location, 9);
		verify(jobcenterDao).setupSavefile("center");
		verify(jobcenterDao).saveJobcenterName("center");
		verify(jobcenterDao).saveSize("", 9);
		verify(jobcenterDao).saveLocation("", location);
		verify(jobcenterDao).saveProfession("", Profession.NITWIT);
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
		assertEquals("center", jobcenter.getName());
		assertEquals(location, jobcenter.getLocation());
	}

	@Test
	public void setupExistingTest() {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		Location location = mock(Location.class);
		World world = mock(World.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Chunk chunk = mock(Chunk.class);
		Job job = mock(Job.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, 9, "center")).thenReturn(inventory);
		when(jobcenterDao.loadLocation("")).thenReturn(location);
		when(jobcenterDao.loadVisible("")).thenReturn(true);
		when(jobcenterDao.loadSize("")).thenReturn(9);
		when(jobcenterDao.loadJobNameList()).thenReturn(Arrays.asList("myJob"));
		when(jobcenterDao.loadJobItemMaterial(job)).thenReturn(Material.STONE);
		when(jobcenterDao.loadJobSlot(job)).thenReturn(1);
		when(jobcenterDao.loadProfession("")).thenReturn(Profession.NITWIT);
		when(job.getName()).thenReturn("myJob");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myJob")).thenReturn(job));
		assertDoesNotThrow(() -> jobcenter.setupExisting("center"));
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
		assertEquals("center", jobcenter.getName());

		assertEquals(location, jobcenter.getLocation());
		assertEquals(1, jobcenter.getJobList().size());
		assertEquals(job, jobcenter.getJobList().get(0));
	}

	@Test
	public void setupExistingTestWithFailedToLoadJob() throws JobsystemException {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		Location location = mock(Location.class);
		World world = mock(World.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Chunk chunk = mock(Chunk.class);
		JobsystemException e = mock(JobsystemException.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		doThrow(e).when(jobManager).getJobByName("myJob");
		when(e.getMessage()).thenReturn("my error message");
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, 9, "center")).thenReturn(inventory);
		when(jobcenterDao.loadLocation("")).thenReturn(location);
		when(jobcenterDao.loadVisible("")).thenReturn(true);
		when(jobcenterDao.loadSize("")).thenReturn(9);
		when(jobcenterDao.loadProfession("")).thenReturn(Profession.NITWIT);
		when(jobcenterDao.loadJobNameList()).thenReturn(Arrays.asList("myJob"));
		assertThrows(JobsystemException.class, () -> jobManager.getJobByName("myJob"));
		jobcenter.setupExisting("center");
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
		assertEquals("center", jobcenter.getName());
		assertEquals(location, jobcenter.getLocation());
		assertEquals(0, jobcenter.getJobList().size());
		verify(e).getMessage();
	}

	@Test
	public void getNameTest() {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		setupNewJobcenter(null, villager, inventory);
		assertEquals("center", jobcenter.getName());
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
		setupNewJobcenter(null, villager, inventory);
		assertDoesNotThrow(() -> jobcenter.addJob(job, "stone", 1));
		assertTrue(jobcenter.hasJob(job));
		assertFalse(jobcenter.hasJob(job2));
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
		setupNewJobcenter(null, villager, inventory);
		assertDoesNotThrow(() -> jobcenter.addJob(job, "stone", 1));
		assertEquals(1, jobcenter.getJobList().size());
		assertEquals(job, jobcenter.getJobList().get(0));
	}

	@Test
	public void despawnVillagerTest() {
		Inventory inventory = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		setupNewJobcenter(null, villager, inventory);
		jobcenter.despawn();
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
		setupNewJobcenter(null, villager, inventory);
		assertDoesNotThrow(() -> jobcenter.changeLocation(location));
		verify(jobcenterDao).saveLocation("", location);
		verify(villager).teleport(location);
		assertEquals(location, jobcenter.getLocation());
	}

	@Test
	public void openInvTest() {
		Inventory inventory = mock(Inventory.class);
		Player player = mock(Player.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		setupNewJobcenter(null, mock(Villager.class), inventory);
		assertDoesNotThrow(() -> jobcenter.openInventory(player));
		verify(player).openInventory(inventory);
	}

	@Test
	public void deleteJobcenterTest() {
		Villager villager = mock(Villager.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		setupNewJobcenter(null, villager, mock(Inventory.class));
		jobcenter.deleteJobcenter();
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
		setupNewJobcenter(null, mock(Villager.class), inventory);
		assertDoesNotThrow(() -> jobcenter.addJob(job, "stone", 4));
		assertDoesNotThrow(() -> jobcenter.removeJob(job));

		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForJobExistsInJobcenter(anyList(), eq(job)));
		verify(jobcenterDao).saveJob(job, null, 0);
		verify(jobcenterDao).saveJobNameList(new ArrayList<>());
		verify(inventory).clear(4);
		assertDoesNotThrow(() -> verify(ecoPlayer).leaveJob(job, false));
		verifyNoMoreInteractions(ecoPlayer2);

		assertEquals(0, jobcenter.getJobList().size());
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
		setupNewJobcenter(null, mock(Villager.class), inventory);
		assertDoesNotThrow(() -> jobcenter.addJob(job, "stone", 4));
		assertDoesNotThrow(() -> jobcenter.removeJob(job));
		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForJobExistsInJobcenter(anyList(), eq(job)));
		verify(jobcenterDao).saveJob(job, null, 0);
		verify(jobcenterDao).saveJobNameList(new ArrayList<>());
		verify(inventory).clear(4);
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.leaveJob(job, false));
		assertEquals(0, jobcenter.getJobList().size());
		verify(e).getMessage();
	}

	@Test
	public void removeJobTestWithJobInOtherJocenter() {
		Inventory inventory = mock(Inventory.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		setupNewJobcenter(null, mock(Villager.class), inventory);
		Jobcenter center2 = mock(Jobcenter.class);
		Job job = mock(Job.class);
		when(jobcenterManager.getJobcenterList()).thenReturn(Arrays.asList(center2));
		when(jobcenterDao.loadJobSlot(job)).thenReturn(4);
		assertDoesNotThrow(() -> jobcenter.addJob(job, "stone", 4));
		when(center2.hasJob(job)).thenReturn(true);
		assertDoesNotThrow(() -> jobcenter.removeJob(job));
		verifyNoInteractions(ecoPlayerManager);
		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForJobExistsInJobcenter(anyList(), eq(job)));
		verify(jobcenterDao).saveJob(job, null, 0);
		verify(jobcenterDao).saveJobNameList(new ArrayList<>());
		verify(inventory).clear(4);
		assertEquals(0, jobcenter.getJobList().size());
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
		setupNewJobcenter(null, mock(Villager.class), inventory);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		when(stack.getItemMeta()).thenReturn(meta);
		assertDoesNotThrow(() -> jobcenter.addJob(job, "stone", 4));
		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForValidSlot(4, 8));
		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForSlotIsEmpty(anySet(), eq(4)));
		assertDoesNotThrow(
				() -> verify(jobsystemValidationHandler).checkForJobDoesNotExistInJobcenter(anyList(), eq(job)));
		assertDoesNotThrow(() -> verify(jobsystemValidationHandler).checkForValidEnum(Material.values(), "STONE"));
		verify(jobcenterDao).saveJob(job, "STONE", 4);
		verify(jobcenterDao).saveJobNameList(Arrays.asList("myJob"));
		verify(meta).setDisplayName("myJob");
		verify(meta, times(2)).addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		verify(inventory).setItem(eq(4), eq(stack));
		assertEquals(1, jobcenter.getJobList().size());
		assertEquals(job, jobcenter.getJobList().get(0));
	}
	
	@Test
	public void handleInventoryClickTestJobNotJoined() throws EconomyPlayerException {
		Inventory inventory = mock(Inventory.class);
		Job job = mock(Job.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		doThrow(e).when(whoClicked).leaveJob(job, true);
		when(e.getMessage()).thenReturn("message");
		when(whoClicked.getPlayer()).thenReturn(player);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		setupNewJobcenter(null, mock(Villager.class), inventory);
		assertDoesNotThrow(() -> jobcenter.addJob(job, "stone", 1));
		jobcenter.handleInventoryClick(ClickType.RIGHT, 1, whoClicked);
		verify(player).sendMessage("message");
	}
	
	@Test
	public void handleInventoryClickTestLeave() {
		Inventory inventory = mock(Inventory.class);
		Job job = mock(Job.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		setupNewJobcenter(null, mock(Villager.class), inventory);
		assertDoesNotThrow(() -> jobcenter.addJob(job, "stone", 1));
		jobcenter.handleInventoryClick(ClickType.RIGHT, 1, whoClicked);
		assertDoesNotThrow(() -> verify(whoClicked).leaveJob(job, true));
	}
	
	@Test
	public void handleInventoryClickTestJoin() {
		Inventory inventory = mock(Inventory.class);
		Job job = mock(Job.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		setupNewJobcenter(null, mock(Villager.class), inventory);
		assertDoesNotThrow(() -> jobcenter.addJob(job, "stone", 1));
		jobcenter.handleInventoryClick(ClickType.LEFT, 1, whoClicked);
		assertDoesNotThrow(() -> verify(whoClicked).joinJob(job, true));
	}
	
	@Test
	public void handleInventoryClickTestOutsideSlot() {
		Inventory inventory = mock(Inventory.class);
		Job job = mock(Job.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		setupNewJobcenter(null, mock(Villager.class), inventory);
		assertDoesNotThrow(() -> jobcenter.addJob(job, "stone", 1));
		jobcenter.handleInventoryClick(ClickType.LEFT, 20, whoClicked);
		verifyNoInteractions(whoClicked);
	}
	
	@Test
	public void handleInventoryClickTestEmptySlott() {
		Inventory inventory = mock(Inventory.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(stack);
		setupNewJobcenter(null, mock(Villager.class), inventory);
		jobcenter.handleInventoryClick(ClickType.LEFT, 1, whoClicked);
		verifyNoInteractions(whoClicked);
	}

	private void setupNewJobcenter(Location location, Villager villager, Inventory invMock) {
		if (location == null) {
			location = mock(Location.class);
		}
		World world = mock(World.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Chunk chunk = mock(Chunk.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(location.getChunk()).thenReturn(chunk);
		when(location.getWorld()).thenReturn(world);
		when(world.spawnEntity(location, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createInventory(villager, 9, "center")).thenReturn(invMock);
		jobcenter.setupNew("center", location, 9);
	}
}
