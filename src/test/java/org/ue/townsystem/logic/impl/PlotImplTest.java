package org.ue.townsystem.logic.impl;

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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.townsystem.dataaccess.api.TownworldDao;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemValidator;

@ExtendWith(MockitoExtension.class)
public class PlotImplTest {

	@Mock
	TownsystemValidator validationHandler;
	@Mock
	TownworldDao townworldDao;
	@Mock
	ServerProvider serverProvider;

	private class Values {
		Plot plot;
		Inventory inv;
		Villager villager;
		EconomyPlayer owner;

		public Values(Plot plot, Inventory inv, Villager villager, EconomyPlayer owner) {
			this.plot = plot;
			this.inv = inv;
			this.villager = villager;
			this.owner = owner;
		}
	}

	private Values createPlot() {
		Inventory inv = mock(Inventory.class);
		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(null, 9, "Plot 1/2")).thenReturn(inv);

		Town town = mock(Town.class);
		when(town.getTownName()).thenReturn("mytown");
		EconomyPlayer owner = mock(EconomyPlayer.class);
		Plot plot = new PlotImpl("1/2", validationHandler, townworldDao, town, owner, serverProvider);
		return new Values(plot, inv, null, owner);
	}

	private Values createPlotForSale() {
		Town town = mock(Town.class);
		EconomyPlayer owner = mock(EconomyPlayer.class);
		EconomyPlayer resident = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Villager duplicated = mock(Villager.class);
		Villager villager = mock(Villager.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
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
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicated));
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(duplicated.getName()).thenReturn("Plot 1/2");
		when(town.getTownName()).thenReturn("mytown");
		when(townworldDao.loadVisible("Towns.mytown.Plots.1/2.SaleVillager")).thenReturn(true);
		when(townworldDao.loadPlotSalePrice("mytown", "1/2")).thenReturn(2.5);
		when(townworldDao.loadResidents("mytown", "1/2")).thenReturn(Arrays.asList(resident));
		when(townworldDao.loadPlotIsForSale("mytown", "1/2")).thenReturn(true);
		when(townworldDao.loadLocation("Towns.mytown.Plots.1/2.SaleVillager")).thenReturn(loc);
		when(townworldDao.loadSize("Towns.mytown.Plots.1/2.SaleVillager")).thenReturn(9);
		assertDoesNotThrow(() -> when(townworldDao.loadPlotOwner("mytown", "1/2")).thenReturn(owner));
		Plot plot = assertDoesNotThrow(
				() -> new PlotImpl("1/2", validationHandler, townworldDao, town, serverProvider));
		return new Values(plot, inv, villager, owner);
	}

	@Test
	public void constructorNewTest() {
		Inventory inv = mock(Inventory.class);
		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(null, 9, "Plot 1/2")).thenReturn(inv);

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
		JavaPlugin plugin = mock(JavaPlugin.class);
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
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(duplicated.getName()).thenReturn("Plot 1/2");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicated));
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(town.getTownName()).thenReturn("mytown");
		when(townworldDao.loadProfession("Towns.mytown.Plots.1/2.SaleVillager")).thenReturn(Profession.ARMORER);
		when(townworldDao.loadLocation("Towns.mytown.Plots.1/2.SaleVillager")).thenReturn(loc);
		when(townworldDao.loadVisible("Towns.mytown.Plots.1/2.SaleVillager")).thenReturn(true);
		when(townworldDao.loadSize("Towns.mytown.Plots.1/2.SaleVillager")).thenReturn(9);
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

		verify(villager).setCustomName("Plot 1/2");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.ARMORER);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setCollidable(false);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager).addPotionEffect(any(PotionEffect.class));
	}

	@Test
	public void despawnSaleVillagerTestNotForSale() {
		Values values = createPlot();
		assertDoesNotThrow(() -> values.plot.despawn());
	}

	@Test
	public void despawnSaleVillagerTestForSale() {
		Values values = createPlotForSale();

		values.plot.despawn();
		verify(values.villager).remove();
	}

	@Test
	public void moveSaleVillagerTestWithNotForSale() throws TownsystemException {
		doThrow(TownsystemException.class).when(validationHandler).checkForPlotIsForSale(false);
		Values values = createPlot();
		assertThrows(TownsystemException.class, () -> values.plot.changeLocation(null));
	}

	@Test
	public void moveSaleVillagerTestWithLocationNotInPlot() throws TownsystemException {
		Location loc = mock(Location.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForLocationInsidePlot("1/2", loc);
		Values values = createPlot();
		assertThrows(TownsystemException.class, () -> values.plot.changeLocation(loc));
	}

	@Test
	public void moveSaleVillagerTest() {
		Values values = createPlotForSale();

		Location newLoc = mock(Location.class);

		assertDoesNotThrow(() -> values.plot.changeLocation(newLoc));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlotIsForSale(true));
		assertDoesNotThrow(() -> verify(validationHandler).checkForLocationInsidePlot("1/2", newLoc));
		verify(townworldDao).saveLocation("Towns.mytown.Plots.1/2.SaleVillager", newLoc);
		verify(values.villager).teleport(newLoc);
	}

	@Test
	public void openSaleVillagerInvTestWithNotForSale() throws TownsystemException {
		doThrow(TownsystemException.class).when(validationHandler).checkForPlotIsForSale(false);
		Values values = createPlot();
		assertThrows(TownsystemException.class, () -> values.plot.openInventory(null));
	}

	@Test
	public void openSaleVillagerInvTest() {
		Values values = createPlotForSale();

		Player player = mock(Player.class);
		assertDoesNotThrow(() -> values.plot.openInventory(player));
		verify(player).openInventory(values.inv);
	}

	@Test
	public void getOwnerTest() {
		Values values = createPlot();
		assertEquals(values.owner, values.plot.getOwner());
	}

	@Test
	public void setOwner() {
		Values values = createPlot();
		EconomyPlayer newOwner = mock(EconomyPlayer.class);
		values.plot.setOwner(newOwner);
		verify(townworldDao).savePlotOwner("mytown", "1/2", newOwner);
		assertEquals(newOwner, values.plot.getOwner());
	}

	@Test
	public void addResidentTestWithIsAlreadyResident() throws TownsystemException {
		Values values = createPlot();
		EconomyPlayer newResident = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerIsNotResidentOfPlot(new ArrayList<>(),
				newResident);
		assertThrows(TownsystemException.class, () -> values.plot.addResident(newResident));
		assertEquals(0, values.plot.getResidents().size());
	}

	@Test
	public void addResidentTest() {
		Values values = createPlot();
		reset(townworldDao);
		EconomyPlayer newResident = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> values.plot.addResident(newResident));
		assertEquals(Arrays.asList(newResident), values.plot.getResidents());
		verify(townworldDao).savePlotResidents("mytown", "1/2", Arrays.asList(newResident));
	}

	@Test
	public void removeResidentTestWithIsNotResident() throws TownsystemException {
		Values values = createPlot();
		EconomyPlayer newResident = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> values.plot.addResident(newResident));
		doThrow(TownsystemException.class).when(validationHandler)
				.checkForPlayerIsResidentOfPlot(Arrays.asList(newResident), newResident);
		assertThrows(TownsystemException.class, () -> values.plot.removeResident(newResident));
		assertEquals(1, values.plot.getResidents().size());
	}

	@Test
	public void removeResidentTest() throws TownsystemException {
		Values values = createPlot();
		EconomyPlayer newResident = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> values.plot.addResident(newResident));
		reset(townworldDao);
		values.plot.removeResident(newResident);
		verify(validationHandler).checkForPlayerIsResidentOfPlot(anyList(), eq(newResident));
		assertEquals(0, values.plot.getResidents().size());
		verify(townworldDao).savePlotResidents("mytown", "1/2", new ArrayList<>());
	}

	@Test
	public void isOwnerTest() {
		Values values = createPlot();
		EconomyPlayer other = mock(EconomyPlayer.class);
		assertTrue(values.plot.isOwner(values.plot.getOwner()));
		assertFalse(values.plot.isOwner(other));
	}

	@Test
	public void isResidentTest() {
		Values values = createPlot();
		EconomyPlayer resident = mock(EconomyPlayer.class);
		EconomyPlayer other = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> values.plot.addResident(resident));
		assertTrue(values.plot.isResident(values.plot.getResidents().get(0)));
		assertFalse(values.plot.isResident(other));
	}

	@Test
	public void removeFromSaleTestWithIsNotPlotOwner() throws TownsystemException {
		Values values = createPlotForSale();
		EconomyPlayer notOwner = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForIsPlotOwner(values.plot.getOwner(),
				notOwner);
		assertThrows(TownsystemException.class, () -> values.plot.removeFromSale(notOwner));
	}

	@Test
	public void removeFromSaleTest() {
		Values values = createPlotForSale();

		assertDoesNotThrow(() -> values.plot.removeFromSale(values.plot.getOwner()));

		assertDoesNotThrow(
				() -> verify(validationHandler).checkForIsPlotOwner(values.plot.getOwner(), values.plot.getOwner()));
		assertFalse(values.plot.isForSale());
		verify(values.villager).remove();
		assertEquals("0.0", String.valueOf(values.plot.getSalePrice()));
		verify(townworldDao).savePlotSalePrice("mytown", "1/2", 0.0);
		verify(townworldDao).savePlotIsForSale("mytown", "1/2", false);
	}

	@Test
	public void setForSaleTestWithIsNotPlotOwner() throws TownsystemException {
		Values values = createPlotForSale();
		EconomyPlayer notOwner = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForIsPlotOwner(values.plot.getOwner(),
				notOwner);
		assertThrows(TownsystemException.class, () -> values.plot.setForSale(1.0, null, notOwner));
	}

	@Test
	public void setForSaleTestWithAlreadyForSale() throws TownsystemException {
		Values values = createPlotForSale();
		doThrow(TownsystemException.class).when(validationHandler).checkForPlotIsNotForSale(true);
		assertThrows(TownsystemException.class, () -> values.plot.setForSale(1.0, null, values.plot.getOwner()));
	}

	@Test
	public void setForSaleTest() {
		Values values = createPlot();
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Villager duplicated = mock(Villager.class);
		Villager villager = mock(Villager.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		Inventory inv = mock(Inventory.class);
		ItemStack buyItem = mock(ItemStack.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(buyItemMeta.getLore()).thenReturn(Arrays.asList("test1", "test2"));
		when(values.inv.getItem(0)).thenReturn(buyItem);
		when(serverProvider.createInventory(villager, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(duplicated.getName()).thenReturn("Plot 1/2");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicated));
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		assertDoesNotThrow(() -> values.plot.setForSale(1.5, loc, values.plot.getOwner()));

		verify(chunk).load();
		verify(duplicated).remove();
		verify(buyItemMeta).setLore(Arrays.asList(ChatColor.GOLD + "Price: " + ChatColor.GREEN + "1.5", "test2"));
		verify(buyItem).setItemMeta(buyItemMeta);
		verify(values.inv).setItem(0, buyItem);

		verify(villager).setCustomName("Plot 1/2");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setCollidable(false);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager).addPotionEffect(any(PotionEffect.class));

		assertTrue(values.plot.isForSale());
		assertEquals("1.5", String.valueOf(values.plot.getSalePrice()));
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForIsPlotOwner(values.plot.getOwner(), values.plot.getOwner()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlotIsNotForSale(false));
		verify(townworldDao).savePlotSalePrice("mytown", "1/2", 1.5);
		verify(townworldDao).savePlotIsForSale("mytown", "1/2", true);
		verify(townworldDao).saveLocation("Towns.mytown.Plots.1/2.SaleVillager", loc);
	}
}
