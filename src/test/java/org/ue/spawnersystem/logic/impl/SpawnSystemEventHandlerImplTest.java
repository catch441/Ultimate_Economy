package org.ue.spawnersystem.logic.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.spawnersystem.logic.api.SpawnerManager;

@ExtendWith(MockitoExtension.class)
public class SpawnSystemEventHandlerImplTest {

	@InjectMocks
	SpawnerystemEventHandlerImpl eventHandler;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ServerProvider serverProvider;
	@Mock
	SpawnerManager spawnerManager;

	@Test
	public void handleInventoryClickTestAnvilSpawner() {
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack item = mock(ItemStack.class);
		Inventory inv = mock(Inventory.class);
		when(event.getCurrentItem()).thenReturn(item);
		when(item.getType()).thenReturn(Material.SPAWNER);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getType()).thenReturn(InventoryType.ANVIL);
		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
	}

	@Test
	public void handleInventoryClickTestAnvilOther() {
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack item = mock(ItemStack.class);
		Inventory inv = mock(Inventory.class);
		when(event.getCurrentItem()).thenReturn(item);
		when(item.getType()).thenReturn(Material.STONE);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getType()).thenReturn(InventoryType.ANVIL);
		eventHandler.handleInventoryClick(event);
		verify(event, never()).setCancelled(true);
	}

	@Test
	public void handleInventoryClickTestChestSpawner() {
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		ItemStack item = mock(ItemStack.class);
		Inventory inv = mock(Inventory.class);
		when(event.getCurrentItem()).thenReturn(item);
		when(event.getInventory()).thenReturn(inv);
		when(inv.getType()).thenReturn(InventoryType.CHEST);
		eventHandler.handleInventoryClick(event);
		verify(event, never()).setCancelled(true);
	}

	@Test
	public void handleInventoryClickTestNoItem() {
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		when(event.getCurrentItem()).thenReturn(null);
		eventHandler.handleInventoryClick(event);
		verify(event, never()).setCancelled(true);
	}

	@Test
	public void handleSetBlockEventTestCreative() {
		BlockPlaceEvent event = mock(BlockPlaceEvent.class);
		Player player = mock(Player.class);
		when(event.getPlayer()).thenReturn(player);
		when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
		eventHandler.handleSetBlockEvent(event);
		verify(event, never()).setCancelled(true);
		verifyNoInteractions(spawnerManager);
	}

	@Test
	public void handleSetBlockEventTestNotSpawner() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		BlockData data = mock(BlockData.class);
		BlockPlaceEvent event = new BlockPlaceEvent(block, null, null, null, player, true, null);
		when(data.getMaterial()).thenReturn(Material.STONE);
		when(block.getBlockData()).thenReturn(data);
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		eventHandler.handleSetBlockEvent(event);
		verifyNoInteractions(spawnerManager);
		assertFalse(event.isCancelled());
	}

	@Test
	public void handleSetBlockEventTestNotPluginSpawner() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		BlockData data = mock(BlockData.class);
		ItemStack item = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		BlockPlaceEvent event = new BlockPlaceEvent(block, null, null, item, player, true, null);
		when(item.getItemMeta()).thenReturn(meta);
		when(meta.getDisplayName()).thenReturn("something");
		when(data.getMaterial()).thenReturn(Material.SPAWNER);
		when(block.getBlockData()).thenReturn(data);
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		eventHandler.handleSetBlockEvent(event);
		verifyNoInteractions(spawnerManager);
		assertFalse(event.isCancelled());
	}

	@Test
	public void handleSetBlockEventTestNotOwner() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		BlockData data = mock(BlockData.class);
		ItemStack item = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		BlockPlaceEvent event = new BlockPlaceEvent(block, null, null, item, player, true, null);
		when(item.getItemMeta()).thenReturn(meta);
		when(meta.getDisplayName()).thenReturn("COW-Wulfgar");
		when(data.getMaterial()).thenReturn(Material.SPAWNER);
		when(block.getBlockData()).thenReturn(data);
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		when(player.getName()).thenReturn("catch441");
		when(messageWrapper.getErrorString(ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION.getValue()))
				.thenReturn("my error");
		eventHandler.handleSetBlockEvent(event);
		verifyNoInteractions(spawnerManager);
		assertTrue(event.isCancelled());
		verify(player).sendMessage("my error");
	}

	@Test
	public void handleSetBlockEventTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		BlockData data = mock(BlockData.class);
		CreatureSpawner blockState = mock(CreatureSpawner.class);
		ItemStack item = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Location loc = mock(Location.class);
		BlockPlaceEvent event = new BlockPlaceEvent(block, null, null, item, player, true, null);
		when(item.getItemMeta()).thenReturn(meta);
		when(meta.getDisplayName()).thenReturn("COW-catch441");
		when(data.getMaterial()).thenReturn(Material.SPAWNER);
		when(block.getBlockData()).thenReturn(data);
		when(block.getState()).thenReturn(blockState);
		when(block.getLocation()).thenReturn(loc);
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		when(player.getName()).thenReturn("catch441");
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		eventHandler.handleSetBlockEvent(event);
		verify(block).setMetadata(eq("name"), any(FixedMetadataValue.class));
		verify(blockState).setSpawnedType(EntityType.COW);
		assertFalse(event.isCancelled());
		verify(player, never()).sendMessage(anyString());
		verify(blockState).update();
		verify(spawnerManager).addSpawner("COW", player, loc);
	}

	@Test
	public void handleBreakBlockEventTestCreativeNotSpawner() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		when(block.getMetadata("entity")).thenReturn(new ArrayList<>());
		when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
		eventHandler.handleBreakBlockEvent(event);
		verifyNoInteractions(spawnerManager);
	}

	@Test
	public void handleBreakBlockEventTestCreativeSpawner() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		Location loc = mock(Location.class);
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		when(block.getMetadata("entity")).thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), "")));
		when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
		when(block.getLocation()).thenReturn(loc);
		eventHandler.handleBreakBlockEvent(event);
		verify(spawnerManager).removeSpawner(loc);
	}

	@Test
	public void handleBreakBlockEventTestSurvivalNoSpawner() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		when(block.getMetadata("name")).thenReturn(new ArrayList<>());
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		eventHandler.handleBreakBlockEvent(event);
		verifyNoInteractions(spawnerManager);
		assertFalse(event.isCancelled());
	}

	@Test
	public void handleBreakBlockEventTestSurvivalInvFull() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		when(block.getMetadata("name")).thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), "")));
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		when(inv.firstEmpty()).thenReturn(-1);
		when(player.getInventory()).thenReturn(inv);
		when(messageWrapper.getErrorString("inventory_full")).thenReturn("my error");
		eventHandler.handleBreakBlockEvent(event);
		verifyNoInteractions(spawnerManager);
		assertTrue(event.isCancelled());
		verify(player).sendMessage("my error");
	}

	@Test
	public void handleBreakBlockEventTestSurvivalNotOwner() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		when(block.getMetadata("name"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), "catch441")));
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		when(inv.firstEmpty()).thenReturn(1);
		when(player.getName()).thenReturn("Wulfgar");
		when(player.getInventory()).thenReturn(inv);
		when(messageWrapper.getErrorString(ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION.getValue()))
				.thenReturn("my error");
		eventHandler.handleBreakBlockEvent(event);
		verifyNoInteractions(spawnerManager);
		assertTrue(event.isCancelled());
		verify(player).sendMessage("my error");
	}

	@Test
	public void handleBreakBlockEventTestSurvival() {
		Player player = mock(Player.class);
		Block block = mock(Block.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Location loc = mock(Location.class);
		when(block.getLocation()).thenReturn(loc);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.SPAWNER, 1)).thenReturn(stack);
		when(block.getMetadata("entity")).thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), "COW")));
		when(block.getMetadata("name"))
				.thenReturn(Arrays.asList(new FixedMetadataValue(mock(Plugin.class), "catch441")));
		when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
		when(inv.firstEmpty()).thenReturn(1);
		when(player.getInventory()).thenReturn(inv);
		when(player.getName()).thenReturn("catch441");
		eventHandler.handleBreakBlockEvent(event);
		verify(spawnerManager).removeSpawner(loc);
		assertFalse(event.isCancelled());
		verify(meta).setDisplayName("COW-catch441");
		verify(stack).setItemMeta(meta);
		verify(inv).addItem(stack);
	}
}
