package org.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;

@ExtendWith(MockitoExtension.class)
public class JobSystemEventHandlerImplTest {

	@InjectMocks
	JobsystemEventHandlerImpl eventHandler;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	JobManager jobManager;
	@Mock
	JobcenterManager jobcenterManager;
	@Mock
	ServerProvider serverProvider;
	
	@Test
	public void handleBreedEventTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Job job = mock(Job.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		EntityBreedEvent event = mock(EntityBreedEvent.class);
		when(event.getBreeder()).thenReturn(player);
		when(event.getEntityType()).thenReturn(EntityType.COW);
		assertDoesNotThrow(() -> when(job.getBreedPrice(EntityType.COW)).thenReturn(1.5));
		eventHandler.handleBreedEvent(event);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(1.5, false));
	}
	
	@Test
	public void handleBreedEventTestWithEmptyJob() throws GeneralEconomyException {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Job job = mock(Job.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(player.getName()).thenReturn("catch441");
		EntityBreedEvent event = mock(EntityBreedEvent.class);
		when(event.getBreeder()).thenReturn(player);
		when(event.getEntityType()).thenReturn(EntityType.COW);
		when(job.getBreedPrice(EntityType.COW)).thenThrow(GeneralEconomyException.class);
		eventHandler.handleBreedEvent(event);
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(1.5, false));
	}
	
	@Test
	public void handleBreedEventTestWithNoEcoPlayer() throws GeneralEconomyException {
		Player player = mock(Player.class);
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(GeneralEconomyException.class);
		when(player.getName()).thenReturn("catch441");
		EntityBreedEvent event = mock(EntityBreedEvent.class);
		when(event.getBreeder()).thenReturn(player);
		eventHandler.handleBreedEvent(event);
	}

	@Test
	public void handleSetBlockTestSurvival() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		BlockPlaceEvent event = new BlockPlaceEvent(block, null, null, null, player, true, null);
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		when(player.getName()).thenReturn("catch441");
		when(block.getType()).thenReturn(Material.STONE);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);

		eventHandler.handleSetBlock(event);
		verify(block).setMetadata(eq("placedBy"), any(FixedMetadataValue.class));
	}

	@Test
	public void handleSetBlockTestCreative() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		BlockPlaceEvent event = new BlockPlaceEvent(block, null, null, null, player, true, null);
		when(player.getGameMode()).thenReturn(GameMode.CREATIVE);

		eventHandler.handleSetBlock(event);
		verify(block, never()).setMetadata(eq("placedBy"), any(FixedMetadataValue.class));
	}

	@Test
	public void handleSetBlockTestSpawner() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		BlockPlaceEvent event = new BlockPlaceEvent(block, null, null, null, player, true, null);
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		when(block.getType()).thenReturn(Material.SPAWNER);

		eventHandler.handleSetBlock(event);
		verify(block, never()).setMetadata(eq("placedBy"), any(FixedMetadataValue.class));
	}

	@Test
	public void handleEntityDeathTestSurvival() {
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(job.getKillPrice("COW")).thenReturn(10.5));
		assertDoesNotThrow(() -> when(job0.getKillPrice("COW")).thenThrow(GeneralEconomyException.class));
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job0, job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		LivingEntity entity = mock(LivingEntity.class);
		when(entity.getKiller()).thenReturn(player);
		when(entity.getType()).thenReturn(EntityType.COW);

		EntityDeathEvent event = new EntityDeathEvent(entity, new ArrayList<>());
		eventHandler.handleEntityDeath(event);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(10.5, false));
		assertDoesNotThrow(() -> verify(job).getKillPrice("COW"));
	}

	@Test
	public void handleEntityDeathTestCreative() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
		LivingEntity entity = mock(LivingEntity.class);
		when(entity.getKiller()).thenReturn(player);

		EntityDeathEvent event = new EntityDeathEvent(entity, new ArrayList<>());
		eventHandler.handleEntityDeath(event);
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).getJobList());
	}

	@Test
	public void handleEntityDeathTestEmptyJob() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(new ArrayList<>());
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		LivingEntity entity = mock(LivingEntity.class);
		when(entity.getKiller()).thenReturn(player);

		EntityDeathEvent event = new EntityDeathEvent(entity, new ArrayList<>());
		eventHandler.handleEntityDeath(event);
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(10.5, false));
	}
	
	@Test
	public void handleEntityDeathTestWithError() throws GeneralEconomyException {
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(GeneralEconomyException.class);
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		LivingEntity entity = mock(LivingEntity.class);
		when(entity.getKiller()).thenReturn(player);

		EntityDeathEvent event = new EntityDeathEvent(entity, new ArrayList<>());
		assertDoesNotThrow(() -> eventHandler.handleEntityDeath(event));
	}

	@Test
	public void handleBreakBlockTestSurvival() {
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(job.getBlockPrice("STONE")).thenReturn(10.5));
		assertDoesNotThrow(() -> when(job0.getBlockPrice("STONE")).thenThrow(GeneralEconomyException.class));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job0, job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Block block = mock(Block.class);
		when(block.getType()).thenReturn(Material.STONE);

		BlockBreakEvent event = new BlockBreakEvent(block, player);
		eventHandler.handleBreakBlock(event);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(10.5, false));
	}

	@Test
	public void handleBreakBlockTestCreative() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);

		BlockBreakEvent event = new BlockBreakEvent(block, player);
		eventHandler.handleBreakBlock(event);
		assertDoesNotThrow(() -> verify(ecoPlayerManager, never()).getEconomyPlayerByName("catch441"));
	}

	@Test
	public void handleBreakBlockTestPlaced() {
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job0, job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Block block = mock(Block.class);
		when(block.getType()).thenReturn(Material.STONE);
		MetadataValue value = mock(MetadataValue.class);
		when(block.getMetadata("placedBy")).thenReturn(Arrays.asList(value));

		BlockBreakEvent event = new BlockBreakEvent(block, player);
		eventHandler.handleBreakBlock(event);
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(10.5, false));
	}

	@Test
	public void handleBreakBlockTestEmptyJob() {
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(new ArrayList<>());
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Block block = mock(Block.class);

		BlockBreakEvent event = new BlockBreakEvent(block, player);
		eventHandler.handleBreakBlock(event);
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(10.5, false));
	}

	@Test
	public void handleBreakBlockTestCrop() {
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(job.getBlockPrice("WHEAT")).thenReturn(10.5));
		assertDoesNotThrow(() -> when(job0.getBlockPrice("WHEAT")).thenThrow(GeneralEconomyException.class));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job0, job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Block block = mock(Block.class);
		when(block.getType()).thenReturn(Material.WHEAT);
		Ageable data = mock(Ageable.class);
		when(block.getBlockData()).thenReturn(data);
		when(data.getAge()).thenReturn(7);
		when(data.getMaximumAge()).thenReturn(7);

		BlockBreakEvent event = new BlockBreakEvent(block, player);
		eventHandler.handleBreakBlock(event);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(10.5, false));
	}

	@Test
	public void handleBreakBlockTestGrowingCrop() {
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job0, job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Block block = mock(Block.class);
		when(block.getType()).thenReturn(Material.WHEAT);
		Ageable data = mock(Ageable.class);
		when(block.getBlockData()).thenReturn(data);
		when(data.getAge()).thenReturn(3);
		when(data.getMaximumAge()).thenReturn(7);

		BlockBreakEvent event = new BlockBreakEvent(block, player);
		eventHandler.handleBreakBlock(event);
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(10.5, false));
	}
	
	@Test
	public void handleBreakBlockTestWithError() throws GeneralEconomyException {
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(GeneralEconomyException.class);

		BlockBreakEvent event = new BlockBreakEvent(null, player);
		assertDoesNotThrow(() -> eventHandler.handleBreakBlock(event));
	}

	@Test
	public void handleFishingTestFish() {
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(job.getFisherPrice("fish")).thenReturn(10.5));
		assertDoesNotThrow(() -> when(job0.getFisherPrice("fish")).thenThrow(GeneralEconomyException.class));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job0, job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Item caught = mock(Item.class);
		ItemStack stack = mock(ItemStack.class);
		when(caught.getItemStack()).thenReturn(stack);
		when(stack.getType()).thenReturn(Material.PUFFERFISH);

		PlayerFishEvent event = new PlayerFishEvent(player, caught, null, PlayerFishEvent.State.CAUGHT_FISH);
		eventHandler.handleFishing(event);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(10.5, false));
	}

	@Test
	public void handleFishingTestTreasure() {
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(job.getFisherPrice("treasure")).thenReturn(10.5));
		assertDoesNotThrow(() -> when(job0.getFisherPrice("treasure")).thenThrow(GeneralEconomyException.class));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job0, job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Item caught = mock(Item.class);
		ItemStack stack = mock(ItemStack.class);
		when(caught.getItemStack()).thenReturn(stack);
		when(stack.getType()).thenReturn(Material.ENCHANTED_BOOK);

		PlayerFishEvent event = new PlayerFishEvent(player, caught, null, PlayerFishEvent.State.CAUGHT_FISH);
		eventHandler.handleFishing(event);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(10.5, false));
	}

	@Test
	public void handleFishingTestJunk() {
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(job.getFisherPrice("junk")).thenReturn(10.5));
		assertDoesNotThrow(() -> when(job0.getFisherPrice("junk")).thenThrow(GeneralEconomyException.class));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job0, job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Item caught = mock(Item.class);
		ItemStack stack = mock(ItemStack.class);
		when(caught.getItemStack()).thenReturn(stack);
		when(stack.getType()).thenReturn(Material.STICK);

		PlayerFishEvent event = new PlayerFishEvent(player, caught, null, PlayerFishEvent.State.CAUGHT_FISH);
		eventHandler.handleFishing(event);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(10.5, false));
	}
	
	@Test
	public void handleFishingTestEntity() {
		Job job = mock(Job.class);
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		PlayerFishEvent event = new PlayerFishEvent(player, player, null, PlayerFishEvent.State.CAUGHT_FISH);
		assertDoesNotThrow(() -> eventHandler.handleFishing(event));
	}

	@Test
	public void handleFishingTestEmptyJob() {
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		Item caught = mock(Item.class);

		PlayerFishEvent event = new PlayerFishEvent(player, caught, null, PlayerFishEvent.State.CAUGHT_FISH);
		eventHandler.handleFishing(event);
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(10.5, false));
	}

	@Test
	public void handleFishingTestNullCaught() {
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(Arrays.asList(job0, job));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		PlayerFishEvent event = new PlayerFishEvent(player, null, null, PlayerFishEvent.State.CAUGHT_FISH);
		eventHandler.handleFishing(event);
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(10.5, false));
	}

	@Test
	public void handleFishingTestNoJobs() {
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getJobList()).thenReturn(new ArrayList<>());
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		PlayerFishEvent event = new PlayerFishEvent(player, null, null, PlayerFishEvent.State.CAUGHT_FISH);
		eventHandler.handleFishing(event);
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(10.5, false));
	}

	@Test
	public void handleFishingTestFailedAttempt() {
		Player player = mock(Player.class);
		PlayerFishEvent event = new PlayerFishEvent(player, null, null, PlayerFishEvent.State.FAILED_ATTEMPT);
		eventHandler.handleFishing(event);
		assertDoesNotThrow(() -> verify(player, never()).getName());
	}

	@Test
	public void handleOpenInventoryTest() {
		Player player = mock(Player.class);
		Entity villager = mock(Entity.class);
		when(villager.getCustomName()).thenReturn("kthcenter");
		Jobcenter center = mock(Jobcenter.class);
		assertDoesNotThrow(() -> when(jobcenterManager.getJobcenterByName("kthcenter")).thenReturn(center));

		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		eventHandler.handleOpenInventory(event);
		verify(center).openInv(player);
	}
	
	@Test
	public void handleOpenInventoryTestNoJobcenter() throws GeneralEconomyException {
		Player player = mock(Player.class);
		Entity villager = mock(Entity.class);
		when(villager.getCustomName()).thenReturn("center");
		when(jobcenterManager.getJobcenterByName("center")).thenThrow(GeneralEconomyException.class);

		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
		assertDoesNotThrow(() -> eventHandler.handleOpenInventory(event));
	}

	@Test
	public void handleInventoryClickTestLeftClick() {
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(meta.getDisplayName()).thenReturn("myjob");
		when(stack.getItemMeta()).thenReturn(meta);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getClick()).thenReturn(ClickType.LEFT);
		when(event.getCurrentItem()).thenReturn(stack);
		when(event.getWhoClicked()).thenReturn(player);

		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).leaveJob(job, true));
		assertDoesNotThrow(() -> verify(ecoPlayer).joinJob(job, true));
	}

	@Test
	public void handleInventoryClickTestRightClick() {
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(meta.getDisplayName()).thenReturn("myjob");
		when(stack.getItemMeta()).thenReturn(meta);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getCurrentItem()).thenReturn(stack);
		when(event.getWhoClicked()).thenReturn(player);

		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(ecoPlayer).leaveJob(job, true));
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).joinJob(job, true));
	}

	@Test
	public void handleInventoryClickTestWithErrorMessage() throws EconomyPlayerException {
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(meta.getDisplayName()).thenReturn("myjob");
		when(stack.getItemMeta()).thenReturn(meta);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getCurrentItem()).thenReturn(stack);
		when(event.getWhoClicked()).thenReturn(player);
		EconomyPlayerException exception = mock(EconomyPlayerException.class);
		when(exception.getMessage()).thenReturn("Error message.");
		doThrow(exception).when(ecoPlayer).leaveJob(job, true);

		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(player).sendMessage("§cError message.");
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).joinJob(job, true));
	}

	@Test
	public void handleInventoryClickTestInfo() {
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(stack.getItemMeta()).thenReturn(meta);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getCurrentItem()).thenReturn(stack);
		when(event.getWhoClicked()).thenReturn(player);

		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).leaveJob(any(Job.class), eq(true)));
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).joinJob(any(Job.class), eq(true)));
	}
	
	@Test
	public void handleInventoryClickTestWithNoJob() throws GeneralEconomyException {
		when(jobManager.getJobByName("myjob")).thenThrow(GeneralEconomyException.class);
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(meta.getDisplayName()).thenReturn("myjob");
		when(stack.getItemMeta()).thenReturn(meta);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getClick()).thenReturn(ClickType.RIGHT);
		when(event.getCurrentItem()).thenReturn(stack);
		when(event.getWhoClicked()).thenReturn(player);

		assertDoesNotThrow(() -> eventHandler.handleInventoryClick(event));
		verify(event).setCancelled(true);
		verify(player, never()).sendMessage(anyString());
	}
}
