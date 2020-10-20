package com.ue.general.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.Updater;
import com.ue.common.utils.Updater.UpdateResult;
import com.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.jobsystem.logic.api.JobsystemEventHandler;
import com.ue.shopsystem.logic.api.ShopEventHandler;
import com.ue.spawnersystem.logic.api.SpawnerSystemEventHandler;
import com.ue.townsystem.logic.api.TownsystemEventHandler;

@ExtendWith(MockitoExtension.class)
public class UltimateEconomyEventHandlerImplTest {

	@Mock
	EconomyPlayerEventHandler ecoPlayerEventHandler;
	@Mock
	JobsystemEventHandler jobsystemEventHandler;
	@Mock
	ShopEventHandler shopEventHandler;
	@Mock
	TownsystemEventHandler townSystemEventHandler;
	@Mock
	SpawnerSystemEventHandler spawnerSystemEventHandler;
	@Mock
	Updater updater;
	@Mock
	Logger logger;

	@Test
	public void onPlayerTeleportTest() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		PlayerTeleportEvent event = mock(PlayerTeleportEvent.class);
		handler.onPlayerTeleport(event);
		verify(townSystemEventHandler).handlePlayerTeleport(event);
	}

	@Test
	public void onPlayerInteractTest() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		PlayerInteractEvent event = mock(PlayerInteractEvent.class);
		handler.onPlayerInteract(event);
		verify(townSystemEventHandler).handlePlayerInteract(event);
	}

	@Test
	public void onEntityDeathTest() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		EntityDeathEvent event = mock(EntityDeathEvent.class);
		handler.onEntityDeath(event);
		verify(jobsystemEventHandler).handleEntityDeath(event);
	}

	@Test
	public void setBlockEventTest() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		BlockPlaceEvent event = mock(BlockPlaceEvent.class);
		handler.setBlockEvent(event);
		verify(spawnerSystemEventHandler).handleSetBlockEvent(event);
		verify(jobsystemEventHandler).handleSetBlock(event);
	}

	@Test
	public void onFishingEventTest() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		PlayerFishEvent event = mock(PlayerFishEvent.class);
		handler.onFishingEvent(event);
		verify(jobsystemEventHandler).handleFishing(event);
	}

	@Test
	public void onPlayerMoveEventTest() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		PlayerMoveEvent event = mock(PlayerMoveEvent.class);
		handler.onPlayerMoveEvent(event);
		verify(townSystemEventHandler).handlerPlayerMove(event);
	}

	@Test
	public void onEntityTransformTestNotVillager() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Entity entity = mock(Entity.class);
		EntityTransformEvent event = mock(EntityTransformEvent.class);
		when(event.getEntity()).thenReturn(entity);
		handler.onEntityTransform(event);
		verify(event, never()).setCancelled(true);
		;
	}

	@Test
	public void onEntityTransformTestVillagerNoMeta() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		EntityTransformEvent event = mock(EntityTransformEvent.class);
		when(event.getEntity()).thenReturn(entity);
		handler.onEntityTransform(event);
		verify(event, never()).setCancelled(true);
		;
	}

	@Test
	public void onEntityTransformTestVillager() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		EntityTransformEvent event = mock(EntityTransformEvent.class);
		when(event.getEntity()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		handler.onEntityTransform(event);
		verify(event).setCancelled(true);
		;
	}

	@Test
	public void breakBlockEventTestAlreadyCancelled() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		BlockBreakEvent event = mock(BlockBreakEvent.class);
		when(event.isCancelled()).thenReturn(true);
		handler.breakBlockEvent(event);
		verifyNoInteractions(spawnerSystemEventHandler);
		verifyNoInteractions(jobsystemEventHandler);
	}

	@Test
	public void breakBlockEventTestNotSpawner() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Block block = mock(Block.class);
		BlockData blockData = mock(BlockData.class);
		BlockBreakEvent event = new BlockBreakEvent(block, null);
		when(block.getBlockData()).thenReturn(blockData);
		when(blockData.getMaterial()).thenReturn(Material.STONE);
		handler.breakBlockEvent(event);
		verifyNoInteractions(spawnerSystemEventHandler);
		verify(jobsystemEventHandler).handleBreakBlock(event);
	}

	@Test
	public void breakBlockEventTestSpawner() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Block block = mock(Block.class);
		BlockData blockData = mock(BlockData.class);
		BlockBreakEvent event = new BlockBreakEvent(block, null);
		when(block.getBlockData()).thenReturn(blockData);
		when(blockData.getMaterial()).thenReturn(Material.SPAWNER);
		handler.breakBlockEvent(event);
		verify(spawnerSystemEventHandler).handleBreakBlockEvent(event);
		;
		verifyNoInteractions(jobsystemEventHandler);
	}

	@Test
	public void onNPCOpenInvTestNotVillager() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Entity entity = mock(Entity.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		handler.onNPCOpenInv(event);
		verifyNoInteractions(shopEventHandler);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onNPCOpenInvTestVillagerNotFromPlugin() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		handler.onNPCOpenInv(event);
		verifyNoInteractions(shopEventHandler);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onNPCOpenInvTestVillagerJobcenter() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.JOBCENTER)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(shopEventHandler);
		verify(jobsystemEventHandler).handleOpenInventory(event);
		verifyNoInteractions(townSystemEventHandler);
	}
	
	@Test
	public void onNPCOpenInvTestVillagerAdminshop() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.ADMINSHOP)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleOpenInventory(event);
		verifyNoInteractions(townSystemEventHandler);
	}
	
	@Test
	public void onNPCOpenInvTestVillagerPlayershop() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.PLAYERSHOP)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleOpenInventory(event);
		verifyNoInteractions(townSystemEventHandler);
	}
	
	@Test
	public void onNPCOpenInvTestVillagerRentshop() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.PLAYERSHOP_RENTABLE)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleOpenInventory(event);
		verifyNoInteractions(townSystemEventHandler);
	}
	
	@Test
	public void onNPCOpenInvTestVillagerPlotSale() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.PLOTSALE)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(shopEventHandler);
		verify(townSystemEventHandler).handleOpenPlotSaleInventory(event);
	}
	
	@Test
	public void onNPCOpenInvTestVillagerTownmanager() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.TOWNMANAGER)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(shopEventHandler);
		verify(townSystemEventHandler).handleOpenTownmanagerInventory(event);
	}
	
	@Test
	public void onInvClickEventTestNotVillager() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		InventoryHolder entity = mock(InventoryHolder.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		handler.onInvClickEvent(event);
		verifyNoInteractions(shopEventHandler);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onInvClickEventTestVillagerNotFromPlugin() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		handler.onInvClickEvent(event);
		verifyNoInteractions(shopEventHandler);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onInvClickEventTestVillagerJobcenter() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.JOBCENTER)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(shopEventHandler);
		verify(jobsystemEventHandler).handleInventoryClick(event);
		verifyNoInteractions(townSystemEventHandler);
	}
	
	@Test
	public void onInvClickEventTestVillagerAdminshop() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.ADMINSHOP)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleInventoryClick(event);
		verifyNoInteractions(townSystemEventHandler);
	}
	
	@Test
	public void onInvClickEventTestVillagerPlayershop() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.PLAYERSHOP)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleInventoryClick(event);
		verifyNoInteractions(townSystemEventHandler);
	}
	
	@Test
	public void onInvClickEventTestVillagerRentshop() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.PLAYERSHOP_RENTABLE)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleInventoryClick(event);
		verifyNoInteractions(townSystemEventHandler);
	}
	
	@Test
	public void onInvClickEventTestVillagerPlotSale() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.PLOTSALE)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(shopEventHandler);
		verify(townSystemEventHandler).handleInventoryClick(event);
	}
	
	@Test
	public void onInvClickEventTestVillagerTownmanager() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillager.TOWNMANAGER)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(shopEventHandler);
		verify(townSystemEventHandler).handleInventoryClick(event);
	}
	
	@Test
	public void onJoinEventTestOpNewUpdate() {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Player player = mock(Player.class);
		PlayerJoinEvent event = new PlayerJoinEvent(player, null);
		when(updater.getUpdateResult()).thenReturn(UpdateResult.UPDATE_AVAILABLE);
		when(player.isOp()).thenReturn(true);
		handler.onJoinEvent(event);
		assertDoesNotThrow(() -> verify(ecoPlayerEventHandler).handleJoin(event));
		verify(townSystemEventHandler).handlePlayerJoin(event);
		verify(player).sendMessage("§6There is a newer version of §aUltimate_Economy §6available!");
	}
	
	@Test
	public void onJoinEventTestOpNoUpdateAndError() throws EconomyPlayerException, GeneralEconomyException {
		UltimateEconomyEventHandlerImpl handler = new UltimateEconomyEventHandlerImpl(logger, updater,
				spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler, jobsystemEventHandler,
				ecoPlayerEventHandler);
		Player player = mock(Player.class);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		PlayerJoinEvent event = new PlayerJoinEvent(player, null);
		when(e.getMessage()).thenReturn("my error");
		doThrow(e).when(ecoPlayerEventHandler).handleJoin(event);
		when(updater.getUpdateResult()).thenReturn(UpdateResult.NO_UPDATE);
		when(player.isOp()).thenReturn(true);
		handler.onJoinEvent(event);
		assertDoesNotThrow(() -> verify(ecoPlayerEventHandler).handleJoin(event));
		verify(logger).warn("[Ultimate_Economy] my error");
	}
}