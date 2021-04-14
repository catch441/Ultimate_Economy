package org.ue.townsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankAccount;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;
import org.ue.townsystem.logic.TownSystemException;

@ExtendWith(MockitoExtension.class)
public class TownsystemEventHandlerImplTest {

	@InjectMocks
	TownsystemEventHandlerImpl eventHandler;
	@Mock
	ConfigManager configManager;
	@Mock
	TownworldManager townworldManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	EconomyPlayerValidationHandler ecoPlayerValidationHandler;

	@Test
	public void handlePlayerTeleportTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		World world = mock(World.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		when(world.getName()).thenReturn("world");
		when(player.getWorld()).thenReturn(world);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		PlayerTeleportEvent event = new PlayerTeleportEvent(player, null, loc);
		eventHandler.handlePlayerTeleport(event);
		verify(townworldManager).performTownWorldLocationCheck("world", chunk, ecoPlayer);
	}

	@Test
	public void handlePlayerTeleportTestWithError() throws GeneralEconomyException {
		Player player = mock(Player.class);
		Location loc = mock(Location.class);
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(GeneralEconomyException.class);

		PlayerTeleportEvent event = new PlayerTeleportEvent(player, null, loc);
		assertDoesNotThrow(() -> eventHandler.handlePlayerTeleport(event));
	}

	@Test
	public void handlePlayerJoinTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		World world = mock(World.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		when(world.getName()).thenReturn("world");
		when(player.getWorld()).thenReturn(world);
		when(player.getName()).thenReturn("catch441");
		when(player.getLocation()).thenReturn(loc);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		PlayerJoinEvent event = new PlayerJoinEvent(player, null);
		eventHandler.handlePlayerJoin(event);
		verify(townworldManager).performTownWorldLocationCheck("world", chunk, ecoPlayer);
	}

	@Test
	public void handlePlayerJoinTestWithError() throws GeneralEconomyException {
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(GeneralEconomyException.class);

		PlayerJoinEvent event = new PlayerJoinEvent(player, null);
		assertDoesNotThrow(() -> eventHandler.handlePlayerJoin(event));
	}

	@Test
	public void handlerPlayerMoveTestCrossChunk() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		World world = mock(World.class);
		Location from = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		Location to = mock(Location.class);
		Chunk toChunk = mock(Chunk.class);
		when(toChunk.getX()).thenReturn(1);
		when(toChunk.getZ()).thenReturn(2);
		when(to.getChunk()).thenReturn(toChunk);

		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(1);
		when(from.getChunk()).thenReturn(chunk);
		when(world.getName()).thenReturn("world");
		when(to.getWorld()).thenReturn(world);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
		eventHandler.handlerPlayerMove(event);
		verify(townworldManager).performTownWorldLocationCheck("world", toChunk, ecoPlayer);
	}

	@Test
	public void handlerPlayerMoveTestInChunk() {
		Location from = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		Location to = mock(Location.class);
		Chunk toChunk = mock(Chunk.class);
		when(toChunk.getX()).thenReturn(1);
		when(toChunk.getZ()).thenReturn(1);
		when(to.getChunk()).thenReturn(toChunk);

		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(1);
		when(from.getChunk()).thenReturn(chunk);

		PlayerMoveEvent event = new PlayerMoveEvent(null, from, to);
		assertDoesNotThrow(() -> eventHandler.handlerPlayerMove(event));
	}

	@Test
	public void handlerPlayerMoveTestError() throws GeneralEconomyException {
		Player player = mock(Player.class);
		Location from = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		Location to = mock(Location.class);
		Chunk toChunk = mock(Chunk.class);
		when(toChunk.getX()).thenReturn(1);
		when(toChunk.getZ()).thenReturn(2);
		when(to.getChunk()).thenReturn(toChunk);

		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(1);
		when(from.getChunk()).thenReturn(chunk);
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(GeneralEconomyException.class);

		PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
		assertDoesNotThrow(() -> eventHandler.handlerPlayerMove(event));
	}

	@Test
	public void handleOpenTownmanagerInventoryTest() {
		Player who = mock(Player.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Town town = mock(Town.class);
		Villager villager = mock(Villager.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		when(villager.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(villager.getLocation()).thenReturn(loc);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));

		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(who, villager);
		eventHandler.handleOpenTownmanagerInventory(event);
		verify(town).openTownManagerVillagerInv(who);
	}

	@Test
	public void handleOpenTownmanagerInventoryTestWithError() throws TownSystemException {
		Player who = mock(Player.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		when(villager.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(townworldManager.getTownWorldByName("world")).thenThrow(TownSystemException.class);

		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(who, villager);
		assertDoesNotThrow(() -> eventHandler.handleOpenTownmanagerInventory(event));
	}

	@Test
	public void handleOpenPlotSaleInventoryTest() {
		Player who = mock(Player.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Town town = mock(Town.class);
		Villager villager = mock(Villager.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		Plot plot = mock(Plot.class);
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(loc.getChunk()).thenReturn(chunk);
		when(villager.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(villager.getLocation()).thenReturn(loc);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));

		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(who, villager);
		assertDoesNotThrow(() -> eventHandler.handleOpenPlotSaleInventory(event));
	}

	@Test
	public void handleOpenPlotSaleInventoryTestWithError() throws TownSystemException {
		Player who = mock(Player.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		when(villager.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(townworldManager.getTownWorldByName("world")).thenThrow(TownSystemException.class);

		PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(who, villager);
		assertDoesNotThrow(() -> eventHandler.handleOpenPlotSaleInventory(event));
	}
	
	@Test
	public void handleInventoryClickTestWithError() throws TownSystemException {
		Player player = mock(Player.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		World world = mock(World.class);
		Inventory inv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		when(inv.getHolder()).thenReturn(villager);
		when(event.getClickedInventory()).thenReturn(inv);
		when(world.getName()).thenReturn("world");
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(event.getWhoClicked()).thenReturn(player);
		when(player.getWorld()).thenReturn(world);

		when(townworldManager.getTownWorldByName("world")).thenThrow(TownSystemException.class);

		assertDoesNotThrow(() -> eventHandler.handleInventoryClick(event));
	}

	@Test
	public void handleInventoryClickTestLeave() {
		Player player = mock(Player.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Villager villager = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(loc.getChunk()).thenReturn(chunk);
		when(villager.getLocation()).thenReturn(loc);
		when(inv.getHolder()).thenReturn(villager);
		when(world.getName()).thenReturn("world");
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn("Leave");
		when(event.getWhoClicked()).thenReturn(player);
		when(player.getWorld()).thenReturn(world);
		when(player.getName()).thenReturn("catch441");
		when(event.getClickedInventory()).thenReturn(inv);
		when(town.getTownName()).thenReturn("town");

		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));

		eventHandler.handleInventoryClick(event);
		assertDoesNotThrow(() -> verify(town).leaveTown(ecoPlayer));
		verify(player).sendMessage(ChatColor.GOLD + "You left the town town.");
	}

	@Test
	public void handleInventoryClickTestBuy() {
		Player player = mock(Player.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Villager villager = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		EconomyPlayer plotOwner = mock(EconomyPlayer.class);
		when(plot.getOwner()).thenReturn(plotOwner);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(plot.getSalePrice()).thenReturn(1.5);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(loc.getChunk()).thenReturn(chunk);
		when(villager.getLocation()).thenReturn(loc);
		when(inv.getHolder()).thenReturn(villager);
		when(world.getName()).thenReturn("world");
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn("Buy");
		when(event.getWhoClicked()).thenReturn(player);
		when(player.getWorld()).thenReturn(world);
		when(player.getName()).thenReturn("catch441");
		when(event.getClickedInventory()).thenReturn(inv);

		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));

		eventHandler.handleInventoryClick(event);

		assertDoesNotThrow(() -> verify(ecoPlayerValidationHandler).checkForEnoughMoney(account, 1.5, true));
		assertDoesNotThrow(() -> verify(ecoPlayer).payToOtherPlayer(plotOwner, 1.5, false));
		assertDoesNotThrow(() -> verify(town).buyPlot(ecoPlayer, 1, 2));
		verify(player).sendMessage(ChatColor.GOLD + "Congratulation! You bought this plot!");
	}

	@Test
	public void handleInventoryClickTestJoin() {
		Player player = mock(Player.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Villager villager = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(loc.getChunk()).thenReturn(chunk);
		when(villager.getLocation()).thenReturn(loc);
		when(inv.getHolder()).thenReturn(villager);
		when(world.getName()).thenReturn("world");
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn("Join");
		when(event.getWhoClicked()).thenReturn(player);
		when(player.getWorld()).thenReturn(world);
		when(player.getName()).thenReturn("catch441");
		when(event.getClickedInventory()).thenReturn(inv);
		when(town.getTownName()).thenReturn("town");

		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));

		eventHandler.handleInventoryClick(event);
		assertDoesNotThrow(() -> verify(town).joinTown(ecoPlayer));
		verify(player).sendMessage(ChatColor.GOLD + "You joined the town town.");
	}

	@Test
	public void handleInventoryClickTestCancelSale() {
		Player player = mock(Player.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Villager villager = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(loc.getChunk()).thenReturn(chunk);
		when(villager.getLocation()).thenReturn(loc);
		when(inv.getHolder()).thenReturn(villager);
		when(world.getName()).thenReturn("world");
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn("Cancel Sale");
		when(event.getWhoClicked()).thenReturn(player);
		when(player.getWorld()).thenReturn(world);
		when(player.getName()).thenReturn("catch441");
		when(event.getClickedInventory()).thenReturn(inv);
		when(plot.isOwner(ecoPlayer)).thenReturn(true);

		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));

		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		assertDoesNotThrow(() -> verify(plot).removeFromSale(ecoPlayer));
		verify(player).sendMessage(ChatColor.GOLD + "You removed this plot from sale!");
	}
	
	@Test
	public void handleInventoryClickTestPlayerInv() {
		Player player = mock(Player.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		Inventory inv = mock(Inventory.class);
		when(inv.getHolder()).thenReturn(player);
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(event.getClickedInventory()).thenReturn(inv);

		eventHandler.handleInventoryClick(event);
		verify(event).setCancelled(true);
		verify(player, never()).sendMessage(anyString());
	}
	
	@Test
	public void handleInventoryClickWithEcoPlayerError() throws EconomyPlayerException, GeneralEconomyException {
		Player player = mock(Player.class);
		ItemStack currentItem = mock(ItemStack.class);
		ItemMeta currentItemMeta = mock(ItemMeta.class);
		InventoryClickEvent event = mock(InventoryClickEvent.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Villager villager = mock(Villager.class);
		Inventory inv = mock(Inventory.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		when(plot.getSalePrice()).thenReturn(1.5);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(loc.getChunk()).thenReturn(chunk);
		when(villager.getLocation()).thenReturn(loc);
		when(inv.getHolder()).thenReturn(villager);
		when(world.getName()).thenReturn("world");
		when(event.getCurrentItem()).thenReturn(currentItem);
		when(currentItem.getItemMeta()).thenReturn(currentItemMeta);
		when(currentItemMeta.getDisplayName()).thenReturn("Buy");
		when(event.getWhoClicked()).thenReturn(player);
		when(player.getWorld()).thenReturn(world);
		when(player.getName()).thenReturn("catch441");
		when(event.getClickedInventory()).thenReturn(inv);

		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));

		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(ecoPlayerValidationHandler).checkForEnoughMoney(account, 1.5, true);
		
		eventHandler.handleInventoryClick(event);

		assertDoesNotThrow(() -> verify(town, never()).buyPlot(ecoPlayer, 1, 2));
		verify(player).sendMessage("my error message");
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void handlePlayerInteractTestWilderness() {
		Block block = mock(Block.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Chunk chunk = mock(Chunk.class);
		
		PlayerInteractEvent event = new PlayerInteractEvent(player, null, null, block, null);
		
		when(block.getLocation()).thenReturn(loc);
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(world.getName()).thenReturn("world");
		when(player.getName()).thenReturn("catch441");
		when(townworld.isChunkFree(chunk)).thenReturn(true);
		when(player.hasPermission("ultimate_economy.wilderness")).thenReturn(false);
		
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getErrorString("wilderness")).thenReturn("my error message");
		
		eventHandler.handlePlayerInteract(event);
		
		assertTrue(event.isCancelled());
		verify(player).sendMessage("my error message");
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void handlePlayerInteractTestTownWithBuildPermissions() {
		Block block = mock(Block.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		
		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, null, block, null);
		
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(block.getLocation()).thenReturn(loc);
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(world.getName()).thenReturn("world");
		when(player.getName()).thenReturn("catch441");
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		when(player.hasPermission("ultimate_economy.towninteract")).thenReturn(false);
		when(town.hasBuildPermissions(ecoPlayer, plot)).thenReturn(true);
		
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		
		eventHandler.handlePlayerInteract(event);
		
		assertFalse(event.isCancelled());
		verify(player, never()).sendMessage(anyString());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void handlePlayerInteractTestTownWithInteractPermissions() {
		Block block = mock(Block.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		
		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, null, block, null);
		
		when(block.getLocation()).thenReturn(loc);
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(world.getName()).thenReturn("world");
		when(player.getName()).thenReturn("catch441");
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		when(player.hasPermission("ultimate_economy.towninteract")).thenReturn(true);
		
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		
		eventHandler.handlePlayerInteract(event);
		
		assertFalse(event.isCancelled());
		verify(player, never()).sendMessage(anyString());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void handlePlayerInteractTestTownWithExtendedInteractionFalse() {
		Block block = mock(Block.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		
		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, block, null);
		
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(block.getLocation()).thenReturn(loc);
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(world.getName()).thenReturn("world");
		when(player.getName()).thenReturn("catch441");
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		when(player.hasPermission("ultimate_economy.towninteract")).thenReturn(false);
		when(town.hasBuildPermissions(ecoPlayer, plot)).thenReturn(true);
		when(configManager.isExtendedInteraction()).thenReturn(false);
		when(block.getType()).thenReturn(Material.ACACIA_DOOR);
		
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		
		eventHandler.handlePlayerInteract(event);
		
		assertFalse(event.isCancelled());
		verify(player, never()).sendMessage(anyString());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void handlePlayerInteractTestTownWithExtendedInteractionTrue() {
		Block block = mock(Block.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		
		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, block, null);
		
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(block.getLocation()).thenReturn(loc);
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(world.getName()).thenReturn("world");
		when(player.getName()).thenReturn("catch441");
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		when(player.hasPermission("ultimate_economy.towninteract")).thenReturn(false);
		when(town.hasBuildPermissions(ecoPlayer, plot)).thenReturn(true);
		when(configManager.isExtendedInteraction()).thenReturn(true);
		when(block.getType()).thenReturn(Material.ACACIA_FENCE_GATE);
		
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getErrorString("no_permission_on_plot")).thenReturn("my error message");
		
		eventHandler.handlePlayerInteract(event);
		
		assertTrue(event.isCancelled());
		verify(player).sendMessage("my error message");
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void handlePlayerInteractTestTownWithExtendedInteractionTrueNoPerms() {
		Block block = mock(Block.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		
		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, block, null);
		
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(block.getLocation()).thenReturn(loc);
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(world.getName()).thenReturn("world");
		when(player.getName()).thenReturn("catch441");
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		when(player.hasPermission("ultimate_economy.towninteract")).thenReturn(false);
		when(town.hasBuildPermissions(ecoPlayer, plot)).thenReturn(true);
		when(block.getType()).thenReturn(Material.STONE);
		
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		
		eventHandler.handlePlayerInteract(event);
		
		assertFalse(event.isCancelled());
		verify(player, never()).sendMessage(anyString());
	}
	
	@Test
	public void handlePlayerInteractTestWithError() throws TownSystemException {
		Block block = mock(Block.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Player player = mock(Player.class);
		
		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, block, null);
		
		when(block.getLocation()).thenReturn(loc);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		
		when(townworldManager.getTownWorldByName("world")).thenThrow(TownSystemException.class);
		
		assertDoesNotThrow(() -> eventHandler.handlePlayerInteract(event));
	}
}
