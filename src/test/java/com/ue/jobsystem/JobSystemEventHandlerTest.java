package com.ue.jobsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.jobsystem.impl.JobSystemEventHandler;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.AgeableMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.ItemMock;
import be.seeseemelk.mockbukkit.entity.LivingEntityMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class JobSystemEventHandlerTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;
	private static JobSystemEventHandler eventHandler;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		player = server.addPlayer("kthschnll");
		eventHandler = new JobSystemEventHandler();
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		int size2 = EconomyPlayerController.getAllEconomyPlayers().size();
		for (int i = 0; i < size2; i++) {
			EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
		}
		UltimateEconomy.getInstance.getDataFolder().delete();
		server.setPlayers(0);
		MockBukkit.unload();
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		int size = JobController.getJobList().size();
		for (int i = 0; i < size; i++) {
			JobController.deleteJob(JobController.getJobList().get(0));
		}
		int size1 = JobcenterController.getJobcenterList().size();
		for (int i = 0; i < size1; i++) {
			try {
				JobcenterController.deleteJobcenter(JobcenterController.getJobcenterList().get(0));
			} catch (JobSystemException e) {
				fail();
			}
		}
	}

	@Test
	public void handleSetBlockTestSurvival() {
		player.setGameMode(GameMode.SURVIVAL);
		Block block = new BlockMock();
		block.setType(Material.STONE);
		/// new Blo
		BlockPlaceEvent event = new BlockPlaceEvent(block, block.getState(), new BlockMock(),
				new ItemStack(Material.STONE), player, true, EquipmentSlot.HAND);
		eventHandler.handleSetBlock(event);
		List<MetadataValue> list = block.getMetadata("placedBy");
		assertEquals(1, list.size());
		assertEquals("kthschnll", list.get(0).asString());
	}

	@Test
	public void handleSetBlockTestCreative() {
		player.setGameMode(GameMode.CREATIVE);
		Block block = new BlockMock();
		block.setType(Material.STONE);
		BlockPlaceEvent event = new BlockPlaceEvent(block, block.getState(), new BlockMock(),
				new ItemStack(Material.STONE), player, true, EquipmentSlot.HAND);
		eventHandler.handleSetBlock(event);
		List<MetadataValue> list = block.getMetadata("placedBy");
		assertEquals(0, list.size());
	}

	@Test
	public void handleSetBlockTestSpawner() {
		player.setGameMode(GameMode.SURVIVAL);
		Block block = new BlockMock();
		block.setType(Material.SPAWNER);
		BlockPlaceEvent event = new BlockPlaceEvent(block, block.getState(), new BlockMock(),
				new ItemStack(Material.STONE), player, true, EquipmentSlot.HAND);
		eventHandler.handleSetBlock(event);
		List<MetadataValue> list = block.getMetadata("placedBy");
		assertEquals(0, list.size());
	}

	@Test
	public void handleEntityDeathTestSurvival() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addMob("cow", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			LivingEntityMock entity = (LivingEntityMock) world.spawnEntity(new Location(world, 1, 2, 3),
					EntityType.COW);
			entity.setKiller(player);
			entity.setType(EntityType.COW);
			EntityDeathEvent event = new EntityDeathEvent(entity, new ArrayList<>());
			eventHandler.handleEntityDeath(event);
			assertEquals("1.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			ecoPlayer.decreasePlayerAmount(1.0, true);
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleEntityDeathTestCreative() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addMob("cow", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.CREATIVE);
			LivingEntityMock entity = (LivingEntityMock) world.spawnEntity(new Location(world, 1, 2, 3),
					EntityType.COW);
			entity.setKiller(player);
			entity.setType(EntityType.COW);
			EntityDeathEvent event = new EntityDeathEvent(entity, new ArrayList<>());
			eventHandler.handleEntityDeath(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleEntityDeathTestEmptyJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			LivingEntityMock entity = (LivingEntityMock) world.spawnEntity(new Location(world, 1, 2, 3),
					EntityType.COW);
			entity.setKiller(player);
			entity.setType(EntityType.COW);
			EntityDeathEvent event = new EntityDeathEvent(entity, new ArrayList<>());
			eventHandler.handleEntityDeath(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestSurvival() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("Stone", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			Block block = new BlockMock(Material.STONE);
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("1.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			ecoPlayer.decreasePlayerAmount(1.0, true);
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestCreative() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("Stone", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.CREATIVE);
			Block block = new BlockMock(Material.STONE);
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestPlaced() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("Stone", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			Block block = new BlockMock(Material.STONE);
			block.setMetadata("placedBy", new FixedMetadataValue(UltimateEconomy.getInstance, "kthschnll"));
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestEmptyJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			Block block = new BlockMock(Material.STONE);
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestCrop() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("Potatoes", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			Block block = new BlockMock(Material.POTATOES);
			AgeableMock ageable = new AgeableMock(10);
			ageable.setAge(10);
			block.setBlockData(ageable);
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("1.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			ecoPlayer.decreasePlayerAmount(1.0, true);
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestGrowingCrop() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("Potatoes", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			Block block = new BlockMock(Material.POTATOES);
			AgeableMock ageable = new AgeableMock(10);
			ageable.setAge(5);
			block.setBlockData(ageable);
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestFish() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("fish", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			ItemMock item = new ItemMock(server, UUID.randomUUID());
			ItemStack stack = new ItemStack(Material.PUFFERFISH);
			item.setItemStack(stack);
			PlayerFishEvent event = new PlayerFishEvent(player, item, null, PlayerFishEvent.State.CAUGHT_FISH);
			eventHandler.handleFishing(event);
			assertEquals("1.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			ecoPlayer.decreasePlayerAmount(1.0, true);
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestTreasure() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("treasure", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			ItemMock item = new ItemMock(server, UUID.randomUUID());
			ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
			item.setItemStack(stack);
			PlayerFishEvent event = new PlayerFishEvent(player, item, null, PlayerFishEvent.State.CAUGHT_FISH);
			eventHandler.handleFishing(event);
			assertEquals("1.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			ecoPlayer.decreasePlayerAmount(1.0, true);
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestJunk() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("junk", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			ItemMock item = new ItemMock(server, UUID.randomUUID());
			ItemStack stack = new ItemStack(Material.STICK);
			item.setItemStack(stack);
			PlayerFishEvent event = new PlayerFishEvent(player, item, null, PlayerFishEvent.State.CAUGHT_FISH);
			eventHandler.handleFishing(event);
			assertEquals("1.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			ecoPlayer.decreasePlayerAmount(1.0, true);
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestEmptyJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			ItemMock item = new ItemMock(server, UUID.randomUUID());
			ItemStack stack = new ItemStack(Material.STICK);
			item.setItemStack(stack);
			PlayerFishEvent event = new PlayerFishEvent(player, item, null, PlayerFishEvent.State.CAUGHT_FISH);
			eventHandler.handleFishing(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestNullCaught() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			PlayerFishEvent event = new PlayerFishEvent(player, null, null, PlayerFishEvent.State.CAUGHT_FISH);
			eventHandler.handleFishing(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | PlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestNoJobs() {
		EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
		PlayerFishEvent event = new PlayerFishEvent(player, null, null, PlayerFishEvent.State.CAUGHT_FISH);
		eventHandler.handleFishing(event);
		assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
	}

	@Test
	public void handleFishingTestFailedAttempt() {
		EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
		PlayerFishEvent event = new PlayerFishEvent(player, null, null, PlayerFishEvent.State.FAILED_ATTEMPT);
		eventHandler.handleFishing(event);
		assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
	}

	@Test
	public void handleOpenInventoryTest() {
		try {
			player.closeInventory();
			Location loc = new Location(world, 1, 2, 30);
			JobcenterController.createJobcenter("myJobcenter", loc, 9);
			Entity villager = new ArrayList<>(world.getNearbyEntities(loc, 0, 0, 0)).get(0);
			assertNull(player.getOpenInventory().getTopInventory());
			PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, villager);
			eventHandler.handleOpenInventory(event);
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myJobcenter", inv.getName());
		} catch (JobSystemException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void handleInventoryClickTestLeftClick() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 16);
			JobcenterController.createJobcenter("myJobcenter", loc, 9);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			Jobcenter center = JobcenterController.getJobcenterList().get(0);
			Entity villager = new ArrayList<>(world.getNearbyEntities(loc, 0, 0, 0)).get(0);
			PlayerInteractEntityEvent eventBefore = new PlayerInteractEntityEvent(player, villager);
			eventHandler.handleOpenInventory(eventBefore);
			center.addJob(job, "Stone", 0);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
					ClickType.LEFT, InventoryAction.PICKUP_ALL);
			assertFalse(ecoPlayer.hasJob(job));
			eventHandler.handleInventoryClick(event);
			assertTrue(ecoPlayer.hasJob(job));
		} catch (JobSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestRightClick() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 16);
			JobcenterController.createJobcenter("myJobcenter", loc, 9);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, false);
			Jobcenter center = JobcenterController.getJobcenterList().get(0);
			Entity villager = new ArrayList<>(world.getNearbyEntities(loc, 0, 0, 0)).get(0);
			PlayerInteractEntityEvent eventBefore = new PlayerInteractEntityEvent(player, villager);
			eventHandler.handleOpenInventory(eventBefore);
			center.addJob(job, "Stone", 0);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
					ClickType.RIGHT, InventoryAction.PICKUP_ALL);
			assertTrue(ecoPlayer.hasJob(job));
			eventHandler.handleInventoryClick(event);
			assertFalse(ecoPlayer.hasJob(job));
		} catch (JobSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestWithErrorMessage() {
		try {
			Location loc = new Location(world, 1, 2, 16);
			JobcenterController.createJobcenter("myJobcenter", loc, 9);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			Jobcenter center = JobcenterController.getJobcenterList().get(0);
			Entity villager = new ArrayList<>(world.getNearbyEntities(loc, 0, 0, 0)).get(0);
			PlayerInteractEntityEvent eventBefore = new PlayerInteractEntityEvent(player, villager);
			eventHandler.handleOpenInventory(eventBefore);
			center.addJob(job, "Stone", 0);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
					ClickType.RIGHT, InventoryAction.PICKUP_ALL);
			eventHandler.handleInventoryClick(event);
			assertEquals("§c§cYou didnt join this job yet!", player.nextMessage());
		} catch (JobSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestInfo() {
		try {
			Location loc = new Location(world, 1, 2, 16);
			JobcenterController.createJobcenter("myJobcenter", loc, 9);
			JobController.createJob("myjob");
			Entity villager = new ArrayList<>(world.getNearbyEntities(loc, 0, 0, 0)).get(0);
			PlayerInteractEntityEvent eventBefore = new PlayerInteractEntityEvent(player, villager);
			eventHandler.handleOpenInventory(eventBefore);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 8,
					ClickType.RIGHT, InventoryAction.PICKUP_ALL);
			eventHandler.handleInventoryClick(event);
		} catch (JobSystemException | GeneralEconomyException e) {
			fail();
		}
	}
}