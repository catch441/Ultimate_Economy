package org.ue.general.impl;

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
import org.bukkit.event.entity.EntityBreedEvent;
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
import org.ue.common.logic.api.EconomyVillagerType;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.utils.Updater;
import org.ue.common.utils.Updater.UpdateResult;
import org.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemEventHandler;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopEventHandler;
import org.ue.spawnersystem.logic.api.SpawnerSystemEventHandler;
import org.ue.townsystem.logic.api.TownsystemEventHandler;

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
	JobcenterManager jobcenterManager;
	@Mock
	AdminshopManager adminshopManager;
	@Mock
	RentshopManager rentshopManager;
	@Mock
	PlayershopManager playershopManager;

	private UltimateEconomyEventHandlerImpl getHandler() {
		return new UltimateEconomyEventHandlerImpl(jobcenterManager, rentshopManager, playershopManager,
				adminshopManager, updater, spawnerSystemEventHandler, townSystemEventHandler, shopEventHandler,
				jobsystemEventHandler, ecoPlayerEventHandler);
	}

	@Test
	public void onEntityBreedTest() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		EntityBreedEvent event = mock(EntityBreedEvent.class);
		handler.onEntityBreed(event);
		verify(jobsystemEventHandler).handleBreedEvent(event);
	}

	@Test
	public void onPlayerTeleportTest() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		PlayerTeleportEvent event = mock(PlayerTeleportEvent.class);
		handler.onPlayerTeleport(event);
		verify(townSystemEventHandler).handlePlayerTeleport(event);
	}

	@Test
	public void onPlayerInteractTest() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		PlayerInteractEvent event = mock(PlayerInteractEvent.class);
		handler.onPlayerInteract(event);
		verify(townSystemEventHandler).handlePlayerInteract(event);
	}

	@Test
	public void onEntityDeathTest() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		EntityDeathEvent event = mock(EntityDeathEvent.class);
		handler.onEntityDeath(event);
		verify(jobsystemEventHandler).handleEntityDeath(event);
	}

	@Test
	public void setBlockEventTest() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		BlockPlaceEvent event = mock(BlockPlaceEvent.class);
		handler.setBlockEvent(event);
		verify(spawnerSystemEventHandler).handleSetBlockEvent(event);
		verify(jobsystemEventHandler).handleSetBlock(event);
	}

	@Test
	public void onFishingEventTest() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		PlayerFishEvent event = mock(PlayerFishEvent.class);
		handler.onFishingEvent(event);
		verify(jobsystemEventHandler).handleFishing(event);
	}

	@Test
	public void onPlayerMoveEventTest() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		PlayerMoveEvent event = mock(PlayerMoveEvent.class);
		handler.onPlayerMoveEvent(event);
		verify(townSystemEventHandler).handlerPlayerMove(event);
	}

	@Test
	public void onEntityTransformTestNotVillager() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Entity entity = mock(Entity.class);
		EntityTransformEvent event = mock(EntityTransformEvent.class);
		when(event.getEntity()).thenReturn(entity);
		handler.onEntityTransform(event);
		verify(event, never()).setCancelled(true);
		;
	}

	@Test
	public void onEntityTransformTestVillagerNoMeta() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		EntityTransformEvent event = mock(EntityTransformEvent.class);
		when(event.getEntity()).thenReturn(entity);
		handler.onEntityTransform(event);
		verify(event, never()).setCancelled(true);
		;
	}

	@Test
	public void onEntityTransformTestVillager() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
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
		UltimateEconomyEventHandlerImpl handler = getHandler();
		BlockBreakEvent event = mock(BlockBreakEvent.class);
		when(event.isCancelled()).thenReturn(true);
		handler.breakBlockEvent(event);
		verifyNoInteractions(spawnerSystemEventHandler);
		verifyNoInteractions(jobsystemEventHandler);
	}

	@Test
	public void breakBlockEventTestNotSpawner() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
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
		UltimateEconomyEventHandlerImpl handler = getHandler();
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
		UltimateEconomyEventHandlerImpl handler = getHandler();
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
		UltimateEconomyEventHandlerImpl handler = getHandler();
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
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.JOBCENTER)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(shopEventHandler);
		verify(jobsystemEventHandler).handleOpenInventory(event);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onNPCOpenInvTestVillagerAdminshop() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.ADMINSHOP)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleOpenInventory(event);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onNPCOpenInvTestVillagerPlayershop() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.PLAYERSHOP)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleOpenInventory(event);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onNPCOpenInvTestVillagerRentshop() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type")).thenReturn(
				Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.RENTSHOP)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleOpenInventory(event);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onNPCOpenInvTestVillagerPlotSale() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.PLOTSALE)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(shopEventHandler);
		verify(townSystemEventHandler).handleOpenPlotSaleInventory(event);
	}

	@Test
	public void onNPCOpenInvTestVillagerTownmanager() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		PlayerInteractEntityEvent event = mock(PlayerInteractEntityEvent.class);
		when(event.getRightClicked()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.TOWNMANAGER)));
		handler.onNPCOpenInv(event);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(shopEventHandler);
		verify(townSystemEventHandler).handleOpenTownmanagerInventory(event);
	}

	@Test
	public void onInvClickEventTestNotVillager() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
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
		UltimateEconomyEventHandlerImpl handler = getHandler();
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
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.JOBCENTER)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(shopEventHandler);
		verify(jobsystemEventHandler).handleInventoryClick(event);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onInvClickEventTestVillagerAdminshop() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.ADMINSHOP)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleInventoryClick(event);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onInvClickEventTestVillagerPlayershop() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.PLAYERSHOP)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleInventoryClick(event);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onInvClickEventTestVillagerRentshop() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type")).thenReturn(
				Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.RENTSHOP)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(jobsystemEventHandler);
		verify(shopEventHandler).handleInventoryClick(event);
		verifyNoInteractions(townSystemEventHandler);
	}

	@Test
	public void onInvClickEventTestVillagerPlotSale() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.PLOTSALE)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(shopEventHandler);
		verify(townSystemEventHandler).handleInventoryClick(event);
	}

	@Test
	public void onInvClickEventTestVillagerTownmanager() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Villager entity = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getHolder()).thenReturn(entity);
		when(entity.hasMetadata("ue-type")).thenReturn(true);
		when(entity.getMetadata("ue-type"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), EconomyVillagerType.TOWNMANAGER)));
		handler.onInvClickEvent(event);
		verifyNoInteractions(jobsystemEventHandler);
		verifyNoInteractions(shopEventHandler);
		verify(townSystemEventHandler).handleInventoryClick(event);
	}

	@Test
	public void onJoinEventTestOpNewUpdate() {
		UltimateEconomyEventHandlerImpl handler = getHandler();
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
		UltimateEconomyEventHandlerImpl handler = getHandler();
		Player player = mock(Player.class);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		PlayerJoinEvent event = new PlayerJoinEvent(player, null);
		when(e.getMessage()).thenReturn("my error");
		doThrow(e).when(ecoPlayerEventHandler).handleJoin(event);
		when(updater.getUpdateResult()).thenReturn(UpdateResult.NO_UPDATE);
		when(player.isOp()).thenReturn(true);
		handler.onJoinEvent(event);
		assertDoesNotThrow(() -> verify(ecoPlayerEventHandler).handleJoin(event));
		verify(e).getMessage();
	}
}
