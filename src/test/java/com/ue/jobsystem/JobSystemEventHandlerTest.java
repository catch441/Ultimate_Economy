package com.ue.jobsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
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

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobController;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.jobsystem.logic.impl.JobcenterManagerImpl;
import com.ue.jobsystem.logic.impl.JobsystemEventHandlerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
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
	private static JobsystemEventHandlerImpl eventHandler;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		player = server.addPlayer("kthschnll");
		eventHandler = new JobsystemEventHandlerImpl();
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		int size2 = EconomyPlayerManagerImpl.getAllEconomyPlayers().size();
		for (int i = 0; i < size2; i++) {
			EconomyPlayerManagerImpl.deleteEconomyPlayer(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
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
		int size1 = JobcenterManagerImpl.getJobcenterList().size();
		for (int i = 0; i < size1; i++) {
			try {
				JobcenterManagerImpl.deleteJobcenter(JobcenterManagerImpl.getJobcenterList().get(0));
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
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
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
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleEntityDeathTestCreative() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addMob("cow", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.CREATIVE);
			LivingEntityMock entity = (LivingEntityMock) world.spawnEntity(new Location(world, 1, 2, 3),
					EntityType.COW);
			entity.setKiller(player);
			entity.setType(EntityType.COW);
			EntityDeathEvent event = new EntityDeathEvent(entity, new ArrayList<>());
			eventHandler.handleEntityDeath(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleEntityDeathTestEmptyJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			LivingEntityMock entity = (LivingEntityMock) world.spawnEntity(new Location(world, 1, 2, 3),
					EntityType.COW);
			entity.setKiller(player);
			entity.setType(EntityType.COW);
			EntityDeathEvent event = new EntityDeathEvent(entity, new ArrayList<>());
			eventHandler.handleEntityDeath(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestSurvival() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("Stone", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			Block block = new BlockMock(Material.STONE);
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("1.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			ecoPlayer.decreasePlayerAmount(1.0, true);
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestCreative() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("Stone", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.CREATIVE);
			Block block = new BlockMock(Material.STONE);
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestPlaced() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("Stone", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			Block block = new BlockMock(Material.STONE);
			block.setMetadata("placedBy", new FixedMetadataValue(UltimateEconomy.getInstance, "kthschnll"));
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestEmptyJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			Block block = new BlockMock(Material.STONE);
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestCrop() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("Potatoes", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
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
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleBreakBlockTestGrowingCrop() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("Potatoes", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			player.setGameMode(GameMode.SURVIVAL);
			Block block = new BlockMock(Material.POTATOES);
			AgeableMock ageable = new AgeableMock(10);
			ageable.setAge(5);
			block.setBlockData(ageable);
			BlockBreakEvent event = new BlockBreakEvent(block, player);
			eventHandler.handleBreakBlock(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestFish() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("fish", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			ItemMock item = new ItemMock(server, UUID.randomUUID());
			ItemStack stack = new ItemStack(Material.PUFFERFISH);
			item.setItemStack(stack);
			PlayerFishEvent event = new PlayerFishEvent(player, item, null, PlayerFishEvent.State.CAUGHT_FISH);
			eventHandler.handleFishing(event);
			assertEquals("1.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			ecoPlayer.decreasePlayerAmount(1.0, true);
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestTreasure() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("treasure", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			ItemMock item = new ItemMock(server, UUID.randomUUID());
			ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
			item.setItemStack(stack);
			PlayerFishEvent event = new PlayerFishEvent(player, item, null, PlayerFishEvent.State.CAUGHT_FISH);
			eventHandler.handleFishing(event);
			assertEquals("1.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			ecoPlayer.decreasePlayerAmount(1.0, true);
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestJunk() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addFisherLootType("junk", 1.0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			ItemMock item = new ItemMock(server, UUID.randomUUID());
			ItemStack stack = new ItemStack(Material.STICK);
			item.setItemStack(stack);
			PlayerFishEvent event = new PlayerFishEvent(player, item, null, PlayerFishEvent.State.CAUGHT_FISH);
			eventHandler.handleFishing(event);
			assertEquals("1.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
			ecoPlayer.decreasePlayerAmount(1.0, true);
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestEmptyJob() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			ItemMock item = new ItemMock(server, UUID.randomUUID());
			ItemStack stack = new ItemStack(Material.STICK);
			item.setItemStack(stack);
			PlayerFishEvent event = new PlayerFishEvent(player, item, null, PlayerFishEvent.State.CAUGHT_FISH);
			eventHandler.handleFishing(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestNullCaught() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayer.joinJob(job, false);
			PlayerFishEvent event = new PlayerFishEvent(player, null, null, PlayerFishEvent.State.CAUGHT_FISH);
			eventHandler.handleFishing(event);
			assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			fail();
		}
	}

	@Test
	public void handleFishingTestNoJobs() {
		EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
		PlayerFishEvent event = new PlayerFishEvent(player, null, null, PlayerFishEvent.State.CAUGHT_FISH);
		eventHandler.handleFishing(event);
		assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
	}

	@Test
	public void handleFishingTestFailedAttempt() {
		EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
		PlayerFishEvent event = new PlayerFishEvent(player, null, null, PlayerFishEvent.State.FAILED_ATTEMPT);
		eventHandler.handleFishing(event);
		assertEquals("0.0", String.valueOf(ecoPlayer.getBankAccount().getAmount()));
	}

	@Test
	public void handleOpenInventoryTest() {
		try {
			player.closeInventory();
			Location loc = new Location(world, 1, 2, 30);
			JobcenterManagerImpl.createJobcenter("myJobcenter", loc, 9);
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
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 16);
			JobcenterManagerImpl.createJobcenter("myJobcenter", loc, 9);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			Entity villager = new ArrayList<>(world.getNearbyEntities(loc, 0, 0, 0)).get(0);
			PlayerInteractEntityEvent eventBefore = new PlayerInteractEntityEvent(player, villager);
			eventHandler.handleOpenInventory(eventBefore);
			center.addJob(job, "Stone", 0);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
					ClickType.LEFT, InventoryAction.PICKUP_ALL);
			assertFalse(ecoPlayer.hasJob(job));
			eventHandler.handleInventoryClick(event);
			assertTrue(ecoPlayer.hasJob(job));
		} catch (JobSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestRightClick() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Location loc = new Location(world, 1, 2, 16);
			JobcenterManagerImpl.createJobcenter("myJobcenter", loc, 9);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, false);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			Entity villager = new ArrayList<>(world.getNearbyEntities(loc, 0, 0, 0)).get(0);
			PlayerInteractEntityEvent eventBefore = new PlayerInteractEntityEvent(player, villager);
			eventHandler.handleOpenInventory(eventBefore);
			center.addJob(job, "Stone", 0);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
					ClickType.RIGHT, InventoryAction.PICKUP_ALL);
			assertTrue(ecoPlayer.hasJob(job));
			eventHandler.handleInventoryClick(event);
			assertFalse(ecoPlayer.hasJob(job));
		} catch (JobSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestWithErrorMessage() {
		try {
			Location loc = new Location(world, 1, 2, 16);
			JobcenterManagerImpl.createJobcenter("myJobcenter", loc, 9);
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			Jobcenter center = JobcenterManagerImpl.getJobcenterList().get(0);
			Entity villager = new ArrayList<>(world.getNearbyEntities(loc, 0, 0, 0)).get(0);
			PlayerInteractEntityEvent eventBefore = new PlayerInteractEntityEvent(player, villager);
			eventHandler.handleOpenInventory(eventBefore);
			center.addJob(job, "Stone", 0);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0,
					ClickType.RIGHT, InventoryAction.PICKUP_ALL);
			eventHandler.handleInventoryClick(event);
			assertEquals("§c§cYou didnt join this job yet!", player.nextMessage());
		} catch (JobSystemException | GeneralEconomyException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestInfo() {
		try {
			Location loc = new Location(world, 1, 2, 16);
			JobcenterManagerImpl.createJobcenter("myJobcenter", loc, 9);
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
