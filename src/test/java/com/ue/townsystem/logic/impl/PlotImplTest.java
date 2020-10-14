package com.ue.townsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.townsystem.dataaccess.api.TownworldDao;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;

@ExtendWith(MockitoExtension.class)
public class PlotImplTest {

	@Mock
	TownsystemValidationHandler validationHandler;
	@Mock
	TownworldDao townworldDao;
	@Mock
	ServerProvider serverProvider;

	private Plot createPlot() {
		Town town = mock(Town.class);
		when(town.getTownName()).thenReturn("mytown");
		EconomyPlayer owner = mock(EconomyPlayer.class);
		return new PlotImpl("1/2", validationHandler, townworldDao, town, owner, serverProvider);
	}

	private Plot createPlotForSale() {
		Town town = mock(Town.class);
		EconomyPlayer owner = mock(EconomyPlayer.class);
		EconomyPlayer resident = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Villager duplicated = mock(Villager.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory inv = mock(Inventory.class);
		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(owner.getName()).thenReturn("catch441");
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(villager, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(duplicated.getName()).thenReturn("Plot 1/2 For Sale!");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicated));
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(town.getTownName()).thenReturn("mytown");
		assertDoesNotThrow(() -> when(townworldDao.loadPlotVillagerLocation("mytown", "1/2")).thenReturn(loc));
		when(townworldDao.loadPlotSalePrice("mytown", "1/2")).thenReturn(2.5);
		when(townworldDao.loadResidents("mytown", "1/2")).thenReturn(Arrays.asList(resident));
		when(townworldDao.loadPlotIsForSale("mytown", "1/2")).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldDao.loadPlotOwner("mytown", "1/2")).thenReturn(owner));
		return assertDoesNotThrow(
				() -> new PlotImpl("1/2", validationHandler, townworldDao, town, serverProvider));
	}

	@Test
	public void constructorNewTest() {
		Town town = mock(Town.class);
		when(town.getTownName()).thenReturn("mytown");
		EconomyPlayer owner = mock(EconomyPlayer.class);
		Plot plot = new PlotImpl("1/2", validationHandler, townworldDao, town, owner, serverProvider);

		assertEquals(town, plot.getTown());
		assertEquals("1/2", plot.getChunkCoords());
		assertFalse(plot.isForSale());
		assertEquals("0.0", String.valueOf(plot.getSalePrice()));
		assertEquals(0, plot.getResidents().size());
		assertEquals(owner, plot.getOwner());

		verify(townworldDao).savePlotIsForSale("mytown", "1/2", false);
		verify(townworldDao).savePlotSalePrice("mytown", "1/2", 0.0);
		verify(townworldDao).savePlotOwner("mytown", "1/2", owner);
		verify(townworldDao).savePlotResidents("mytown", "1/2", new ArrayList<>());
	}

	@Test
	public void constructorLoadingTest() {
		Town town = mock(Town.class);
		EconomyPlayer owner = mock(EconomyPlayer.class);
		EconomyPlayer resident = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Villager duplicated = mock(Villager.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory inv = mock(Inventory.class);
		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(owner.getName()).thenReturn("catch441");
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(villager, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(duplicated.getName()).thenReturn("Plot 1/2 For Sale!");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicated));
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(town.getTownName()).thenReturn("mytown");
		assertDoesNotThrow(() -> when(townworldDao.loadPlotVillagerLocation("mytown", "1/2")).thenReturn(loc));
		when(townworldDao.loadPlotSalePrice("mytown", "1/2")).thenReturn(2.5);
		when(townworldDao.loadResidents("mytown", "1/2")).thenReturn(Arrays.asList(resident));
		when(townworldDao.loadPlotIsForSale("mytown", "1/2")).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldDao.loadPlotOwner("mytown", "1/2")).thenReturn(owner));
		Plot plot = assertDoesNotThrow(
				() -> new PlotImpl("1/2", validationHandler, townworldDao, town, serverProvider));

		assertEquals(Arrays.asList(resident), plot.getResidents());
		assertEquals(town, plot.getTown());
		assertEquals("1/2", plot.getChunkCoords());
		assertEquals("2.5", String.valueOf(plot.getSalePrice()));
		assertTrue(plot.isForSale());
		assertEquals(owner, plot.getOwner());
		verify(chunk).load();
		verify(duplicated).remove();
		verify(buyItemMeta).setDisplayName("Buy");
		verify(buyItemMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + ChatColor.GREEN + "2.5",
				ChatColor.GOLD + "Is sold by " + ChatColor.GREEN + "catch441"));
		verify(buyItem).setItemMeta(buyItemMeta);
		verify(inv).setItem(0, buyItem);

		verify(cancelItemMeta).setDisplayName("Cancel Sale");
		verify(cancelItemMeta).setLore(Arrays.asList(ChatColor.RED + "Only for plot owner!"));
		verify(cancelItem).setItemMeta(cancelItemMeta);
		verify(inv).setItem(8, cancelItem);

		verify(villager).setCustomName("Plot 1/2 For Sale!");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setCollidable(false);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager).addPotionEffect(any(PotionEffect.class));
	}

	@Test
	public void despawnSaleVillagerTestNotForSale() {
		Plot plot = createPlot();
		assertDoesNotThrow(() -> plot.despawnSaleVillager());
	}

	@Test
	public void despawnSaleVillagerTestForSale() {
		Town town = mock(Town.class);
		EconomyPlayer owner = mock(EconomyPlayer.class);
		EconomyPlayer resident = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Villager duplicated = mock(Villager.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory inv = mock(Inventory.class);
		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(owner.getName()).thenReturn("catch441");
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(villager, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(duplicated.getName()).thenReturn("Plot 1/2 For Sale!");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicated));
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(town.getTownName()).thenReturn("mytown");
		assertDoesNotThrow(() -> when(townworldDao.loadPlotVillagerLocation("mytown", "1/2")).thenReturn(loc));
		when(townworldDao.loadPlotSalePrice("mytown", "1/2")).thenReturn(2.5);
		when(townworldDao.loadResidents("mytown", "1/2")).thenReturn(Arrays.asList(resident));
		when(townworldDao.loadPlotIsForSale("mytown", "1/2")).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldDao.loadPlotOwner("mytown", "1/2")).thenReturn(owner));
		Plot plot = assertDoesNotThrow(
				() -> new PlotImpl("1/2", validationHandler, townworldDao, town, serverProvider));

		plot.despawnSaleVillager();
		verify(villager).remove();
	}

	@Test
	public void moveSaleVillagerTestWithNotForSale() throws TownSystemException {
		doThrow(TownSystemException.class).when(validationHandler).checkForPlotIsForSale(false);
		Plot plot = createPlot();
		assertThrows(TownSystemException.class, () -> plot.moveSaleVillager(null));
	}

	@Test
	public void moveSaleVillagerTestWithLocationNotInPlot() throws EconomyPlayerException {
		Location loc = mock(Location.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForLocationInsidePlot("1/2", loc);
		Plot plot = createPlot();
		assertThrows(EconomyPlayerException.class, () -> plot.moveSaleVillager(loc));
	}

	@Test
	public void moveSaleVillagerTest() {
		Town town = mock(Town.class);
		EconomyPlayer owner = mock(EconomyPlayer.class);
		EconomyPlayer resident = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory inv = mock(Inventory.class);
		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(owner.getName()).thenReturn("catch441");
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(villager, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(new ArrayList<>());
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(town.getTownName()).thenReturn("mytown");
		assertDoesNotThrow(() -> when(townworldDao.loadPlotVillagerLocation("mytown", "1/2")).thenReturn(loc));
		when(townworldDao.loadPlotSalePrice("mytown", "1/2")).thenReturn(2.5);
		when(townworldDao.loadResidents("mytown", "1/2")).thenReturn(Arrays.asList(resident));
		when(townworldDao.loadPlotIsForSale("mytown", "1/2")).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldDao.loadPlotOwner("mytown", "1/2")).thenReturn(owner));
		Plot plot = assertDoesNotThrow(
				() -> new PlotImpl("1/2", validationHandler, townworldDao, town, serverProvider));
		Location newLoc = mock(Location.class);

		assertDoesNotThrow(() -> plot.moveSaleVillager(newLoc));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlotIsForSale(true));
		assertDoesNotThrow(() -> verify(validationHandler).checkForLocationInsidePlot("1/2", newLoc));
		verify(townworldDao).savePlotVillagerLocation("mytown", "1/2", newLoc);
		verify(villager).teleport(newLoc);
	}

	@Test
	public void openSaleVillagerInvTestWithNotForSale() throws TownSystemException {
		doThrow(TownSystemException.class).when(validationHandler).checkForPlotIsForSale(false);
		Plot plot = createPlot();
		assertThrows(TownSystemException.class, () -> plot.openSaleVillagerInv(null));
	}

	@Test
	public void openSaleVillagerInvTest() {
		Town town = mock(Town.class);
		EconomyPlayer owner = mock(EconomyPlayer.class);
		EconomyPlayer resident = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Villager duplicated = mock(Villager.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory inv = mock(Inventory.class);
		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(owner.getName()).thenReturn("catch441");
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(villager, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(duplicated.getName()).thenReturn("Plot 1/2 For Sale!");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicated));
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(town.getTownName()).thenReturn("mytown");
		assertDoesNotThrow(() -> when(townworldDao.loadPlotVillagerLocation("mytown", "1/2")).thenReturn(loc));
		when(townworldDao.loadPlotSalePrice("mytown", "1/2")).thenReturn(2.5);
		when(townworldDao.loadResidents("mytown", "1/2")).thenReturn(Arrays.asList(resident));
		when(townworldDao.loadPlotIsForSale("mytown", "1/2")).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldDao.loadPlotOwner("mytown", "1/2")).thenReturn(owner));
		Plot plot = assertDoesNotThrow(
				() -> new PlotImpl("1/2", validationHandler, townworldDao, town, serverProvider));

		Player player = mock(Player.class);
		assertDoesNotThrow(() -> plot.openSaleVillagerInv(player));
		verify(player).openInventory(inv);
	}

	@Test
	public void getOwnerTest() {
		Town town = mock(Town.class);
		when(town.getTownName()).thenReturn("mytown");
		EconomyPlayer owner = mock(EconomyPlayer.class);
		Plot plot = new PlotImpl("1/2", validationHandler, townworldDao, town, owner, serverProvider);
		assertEquals(owner, plot.getOwner());
	}

	@Test
	public void setOwner() {
		Plot plot = createPlot();
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		plot.setOwner(newOwner);
		verify(townworldDao).savePlotOwner("mytown", "1/2", newOwner);
		assertEquals(newOwner, plot.getOwner());
	}

	@Test
	public void addResidentTestWithIsAlreadyResident() throws TownSystemException {
		Plot plot = createPlot();
		EconomyPlayer newResident = mock(EconomyPlayer.class);
		doThrow(TownSystemException.class).when(validationHandler).checkForPlayerIsNotResidentOfPlot(new ArrayList<>(),
				newResident);
		assertThrows(TownSystemException.class, () -> plot.addResident(newResident));
		assertEquals(0, plot.getResidents().size());
	}

	@Test
	public void addResidentTest() {
		Plot plot = createPlot();
		reset(townworldDao);
		EconomyPlayer newResident = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> plot.addResident(newResident));
		assertEquals(Arrays.asList(newResident), plot.getResidents());
		verify(townworldDao).savePlotResidents("mytown", "1/2", Arrays.asList(newResident));
	}

	@Test
	public void removeResidentTestWithIsNotResident() throws TownSystemException {
		Plot plot = createPlot();
		EconomyPlayer newResident = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> plot.addResident(newResident));
		doThrow(TownSystemException.class).when(validationHandler)
				.checkForPlayerIsResidentOfPlot(Arrays.asList(newResident), newResident);
		assertThrows(TownSystemException.class, () -> plot.removeResident(newResident));
		assertEquals(1, plot.getResidents().size());
	}

	@Test
	public void removeResidentTest() throws TownSystemException {
		Plot plot = createPlot();
		EconomyPlayer newResident = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> plot.addResident(newResident));
		reset(townworldDao);
		plot.removeResident(newResident);
		verify(validationHandler).checkForPlayerIsResidentOfPlot(anyList(), eq(newResident));
		assertEquals(0, plot.getResidents().size());
		verify(townworldDao).savePlotResidents("mytown", "1/2", new ArrayList<>());
	}

	@Test
	public void isOwnerTest() {
		Plot plot = createPlot();
		EconomyPlayer other = mock(EconomyPlayer.class);
		assertTrue(plot.isOwner(plot.getOwner()));
		assertFalse(plot.isOwner(other));
	}

	@Test
	public void isResidentTest() {
		Plot plot = createPlot();
		EconomyPlayer resident = mock(EconomyPlayer.class);
		EconomyPlayer other = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> plot.addResident(resident));
		assertTrue(plot.isResident(plot.getResidents().get(0)));
		assertFalse(plot.isResident(other));
	}

	@Test
	public void removeFromSaleTestWithIsNotPlotOwner() throws EconomyPlayerException {
		Plot plot = createPlotForSale();
		EconomyPlayer notOwner = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForIsPlotOwner(plot.getOwner(), notOwner);
		assertThrows(EconomyPlayerException.class, () -> plot.removeFromSale(notOwner));
	}
	
	@Test
	public void removeFromSaleTes() {
		Town town = mock(Town.class);
		EconomyPlayer owner = mock(EconomyPlayer.class);
		EconomyPlayer resident = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Villager duplicated = mock(Villager.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory inv = mock(Inventory.class);
		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(villager.getLocation()).thenReturn(loc);
		when(owner.getName()).thenReturn("catch441");
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(villager, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(duplicated.getName()).thenReturn("Plot 1/2 For Sale!");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicated));
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(town.getTownName()).thenReturn("mytown");
		assertDoesNotThrow(() -> when(townworldDao.loadPlotVillagerLocation("mytown", "1/2")).thenReturn(loc));
		when(townworldDao.loadPlotSalePrice("mytown", "1/2")).thenReturn(2.5);
		when(townworldDao.loadResidents("mytown", "1/2")).thenReturn(Arrays.asList(resident));
		when(townworldDao.loadPlotIsForSale("mytown", "1/2")).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldDao.loadPlotOwner("mytown", "1/2")).thenReturn(owner));
		Plot plot = assertDoesNotThrow(
				() -> new PlotImpl("1/2", validationHandler, townworldDao, town, serverProvider));
		
		assertDoesNotThrow(() -> plot.removeFromSale(plot.getOwner()));
		
		assertDoesNotThrow(() -> verify(validationHandler).checkForIsPlotOwner(plot.getOwner(), plot.getOwner()));
		assertFalse(plot.isForSale());
		verify(villager).remove();
		verify(world).save();
		assertEquals("0.0", String.valueOf(plot.getSalePrice()));
		verify(townworldDao).savePlotSalePrice("mytown", "1/2", 0.0);
		verify(townworldDao).savePlotIsForSale("mytown", "1/2", false);
	}
	
	@Test
	public void setForSaleTestWithIsNotPlotOwner() throws EconomyPlayerException {
		Plot plot = createPlotForSale();
		EconomyPlayer notOwner = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForIsPlotOwner(plot.getOwner(), notOwner);
		assertThrows(EconomyPlayerException.class, () -> plot.setForSale(1.0, null, notOwner));
	}
	
	@Test
	public void setForSaleTestWithAlreadyForSale() throws TownSystemException {
		Plot plot = createPlotForSale();
		doThrow(TownSystemException.class).when(validationHandler).checkForPlotIsNotForSale(true);
		assertThrows(TownSystemException.class, () -> plot.setForSale(1.0, null, plot.getOwner()));
	}
	
	@Test
	public void setForSaleTest() {
		Plot plot = createPlot();
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Villager duplicated = mock(Villager.class);
		Villager villager = mock(Villager.class);
		Plugin plugin = mock(Plugin.class);
		Inventory inv = mock(Inventory.class);
		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(plot.getOwner().getName()).thenReturn("catch441");
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(villager, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(duplicated.getName()).thenReturn("Plot 1/2 For Sale!");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicated));
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(villager.getLocation()).thenReturn(loc);
		assertDoesNotThrow(() -> plot.setForSale(1.5, loc, plot.getOwner()));
		
		verify(chunk).load();
		verify(duplicated).remove();
		verify(buyItemMeta).setDisplayName("Buy");
		verify(buyItemMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + ChatColor.GREEN + "1.5",
				ChatColor.GOLD + "Is sold by " + ChatColor.GREEN + "catch441"));
		verify(buyItem).setItemMeta(buyItemMeta);
		verify(inv).setItem(0, buyItem);

		verify(cancelItemMeta).setDisplayName("Cancel Sale");
		verify(cancelItemMeta).setLore(Arrays.asList(ChatColor.RED + "Only for plot owner!"));
		verify(cancelItem).setItemMeta(cancelItemMeta);
		verify(inv).setItem(8, cancelItem);

		verify(villager).setCustomName("Plot 1/2 For Sale!");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setCollidable(false);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager).addPotionEffect(any(PotionEffect.class));
		
		assertTrue(plot.isForSale());
		assertEquals("1.5", String.valueOf(plot.getSalePrice()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForIsPlotOwner(plot.getOwner(), plot.getOwner()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlotIsNotForSale(false));
		verify(townworldDao).savePlotSalePrice("mytown", "1/2", 1.5);
		verify(townworldDao).savePlotIsForSale("mytown", "1/2", true);
		verify(townworldDao).savePlotVillagerLocation("mytown", "1/2", loc);
	}
}
