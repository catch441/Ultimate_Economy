package org.ue.townsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.townsystem.dataaccess.api.TownworldDao;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemValidator;
import org.ue.townsystem.logic.api.Townworld;

@ExtendWith(MockitoExtension.class)
public class TownworldImplTest {

	@Mock
	TownsystemValidator validationHandler;
	@Mock
	TownworldManagerImpl townworldManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	BankManager bankManager;
	@Mock
	ServerProvider serverProvider;
	@Mock
	TownworldDao townworldDao;

	private void setupMocksForLoadingTown(String townName) {
		JavaPlugin plugin = mock(JavaPlugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		EconomyPlayer deputy = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Location spawn = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		BankAccount account = mock(BankAccount.class);
		Inventory inv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack joinItem = mock(ItemStack.class);
		ItemStack leaveItem = mock(ItemStack.class);
		ItemMeta joinItemMeta = mock(ItemMeta.class);
		ItemMeta leaveItemMeta = mock(ItemMeta.class);
		assertDoesNotThrow(
				() -> when(townworldDao.loadLocation("Towns." + townName + ".TownManagerVillager")).thenReturn(loc));
		when(townworldDao.loadTownBankIban(townName)).thenReturn("iban");
		assertDoesNotThrow(() -> when(townworldDao.loadTownSpawn(townName)).thenReturn(spawn));
		assertDoesNotThrow(() -> when(townworldDao.loadMayor(townName)).thenReturn(mayor));
		assertDoesNotThrow(() -> when(townworldDao.loadDeputies(townName)).thenReturn(Arrays.asList(deputy)));
		assertDoesNotThrow(() -> when(townworldDao.loadCitizens(townName)).thenReturn(Arrays.asList(mayor)));
		when(townworldDao.loadTax(townName)).thenReturn(1.5);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(villager, 9, townName + " TownManager")).thenReturn(inv);
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("iban"))).thenReturn(account);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		when(townworldDao.loadVisible("Towns." + townName + ".TownManagerVillager")).thenReturn(true);
		when(townworldDao.loadSize("Towns." + townName + ".TownManagerVillager")).thenReturn(9);
	}

	private Location setupMocksForNewTown() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		BankAccount account = mock(BankAccount.class);
		Inventory inv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack joinItem = mock(ItemStack.class);
		ItemStack leaveItem = mock(ItemStack.class);
		ItemMeta joinItemMeta = mock(ItemMeta.class);
		ItemMeta leaveItemMeta = mock(ItemMeta.class);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(null, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		return loc;
	}

	@Test
	public void constructorNewTest() {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);

		assertEquals("world", townworld.getWorldName());
		assertEquals("0.0", String.valueOf(townworld.getExpandPrice()));
		assertEquals("0.0", String.valueOf(townworld.getFoundationPrice()));
		verify(townworldDao).saveExpandPrice(0.0);
		verify(townworldDao).saveFoundationPrice(0.0);
		verify(townworldDao).saveWorldName("world");
	}

	@Test
	public void constructorLoadingTest() throws EconomyPlayerException {
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(townworldDao).loadMayor("corrupted");
		setupMocksForLoadingTown("mytown");
		when(townworldDao.loadFoundationPrice()).thenReturn(1.5);
		when(townworldDao.loadExpandPrice()).thenReturn(2.5);
		when(townworldDao.loadTownworldTownNames()).thenReturn(Arrays.asList("mytown", "corrupted"));
		Townworld townworld = new TownworldImpl("world", false, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);

		verify(e).getMessage();

		assertEquals("world", townworld.getWorldName());
		assertEquals("2.5", String.valueOf(townworld.getExpandPrice()));
		assertEquals("1.5", String.valueOf(townworld.getFoundationPrice()));

		assertEquals(1, townworld.getTownList().size());
		assertDoesNotThrow(() -> assertNotNull(townworld.getTownByName("mytown")));
	}

	@Test
	public void setFoundationPriceTestWithNegative() throws TownsystemException {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		doThrow(TownsystemException.class).when(validationHandler).checkForPositiveValue(-1.5);
		assertThrows(TownsystemException.class, () -> townworld.setFoundationPrice(-1.5));
		assertEquals("0.0", String.valueOf(townworld.getFoundationPrice()));
	}

	@Test
	public void setFoundationPriceTest() {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		assertDoesNotThrow(() -> townworld.setFoundationPrice(1.5));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1.5));
		verify(townworldDao).saveFoundationPrice(1.5);
		assertEquals("1.5", String.valueOf(townworld.getFoundationPrice()));
	}

	@Test
	public void setExpandPriceTestWithNegative() throws TownsystemException {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		doThrow(TownsystemException.class).when(validationHandler).checkForPositiveValue(-1.5);
		assertThrows(TownsystemException.class, () -> townworld.setExpandPrice(-1.5));
		assertEquals("0.0", String.valueOf(townworld.getExpandPrice()));
	}

	@Test
	public void setExpandPriceTest() {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		assertDoesNotThrow(() -> townworld.setExpandPrice(1.5));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1.5));
		verify(townworldDao).saveExpandPrice(1.5);
		assertEquals("1.5", String.valueOf(townworld.getExpandPrice()));
	}

	@Test
	public void foundTownTestWithAlreadyExists() throws TownsystemException {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		when(townworldManager.getTownNameList()).thenReturn(Arrays.asList("mytown"));
		doThrow(TownsystemException.class).when(validationHandler).checkForValueNotInList(Arrays.asList("mytown"),
				"mytown");
		assertThrows(TownsystemException.class, () -> townworld.foundTown("mytown", null, null));
	}

	@Test
	public void foundTownTestWithChunkIsOccupied() throws TownsystemException {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		Location loc = mock(Location.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForChunkIsFree(townworld, loc);
		assertThrows(TownsystemException.class, () -> townworld.foundTown("mytown", loc, null));
	}

	@Test
	public void foundTownTestReachedMaxJoinedTowns() throws TownsystemException {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.reachedMaxJoinedTowns()).thenReturn(true);
		doThrow(TownsystemException.class).when(validationHandler).checkForNotReachedMax(true);
		assertThrows(TownsystemException.class, () -> townworld.foundTown("mytown", null, ecoPlayer));
	}

	@Test
	public void foundTownTestWithNotEnoughMoney() throws TownsystemException {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		doThrow(TownsystemException.class).when(validationHandler).checkForEnoughMoney(account, 0.0, true);
		assertThrows(TownsystemException.class, () -> townworld.foundTown("mytown", null, ecoPlayer));
	}

	@Test
	public void foundTownTest() {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);

		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		assertEquals(1, townworld.getTownList().size());
		assertDoesNotThrow(() -> assertNotNull(townworld.getTownByName("mytown")));
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(0.0, true));
		verify(townworldManager).setTownNameList(Arrays.asList("mytown"));
		assertDoesNotThrow(() -> verify(townworldManager).performTownworldLocationCheckAllPlayers());
	}

	@Test
	public void getTownNameListTest() {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		assertEquals(Arrays.asList("mytown"), townworld.getTownNameList());
	}

	@Test
	public void getTownByChunkTest() {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		Town town = assertDoesNotThrow(() -> townworld.getTownByChunk(loc.getChunk()));
		assertEquals("mytown", town.getTownName());
	}

	@Test
	public void getTownByChunkTestWithNoTown() {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);

		Chunk chunk = mock(Chunk.class);
		TownsystemException e = assertThrows(TownsystemException.class, () -> townworld.getTownByChunk(chunk));
		assertEquals(ExceptionMessageEnum.CHUNK_NOT_CLAIMED, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void getTownByNameTest() {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		Town town = assertDoesNotThrow(() -> townworld.getTownByName("mytown"));
		assertEquals("mytown", town.getTownName());
	}

	@Test
	public void getTownByNameTestWithNoTown() throws TownsystemException {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		doThrow(TownsystemException.class).when(validationHandler).checkForValueExists(null, "mytown");
		assertThrows(TownsystemException.class, () -> townworld.getTownByName("mytown"));
	}

	@Test
	public void isChunkFreeTest() {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		Chunk chunk = mock(Chunk.class);
		assertFalse(townworld.isChunkFree(loc.getChunk()));
		assertTrue(townworld.isChunkFree(chunk));
	}

	@Test
	public void despawnAllTownVillagersTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		BankAccount account = mock(BankAccount.class);
		Inventory inv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack joinItem = mock(ItemStack.class);
		ItemStack leaveItem = mock(ItemStack.class);
		ItemMeta joinItemMeta = mock(ItemMeta.class);
		ItemMeta leaveItemMeta = mock(ItemMeta.class);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(null, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		townworld.despawnAllTownVillagers();
		verify(villager).remove();
	}

	@Test
	public void dissolveTownTestWithNotMayor() throws TownsystemException {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		Town town = townworld.getTownList().get(0);
		EconomyPlayer notMayor = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerIsMayor(ecoPlayer, notMayor);
		assertThrows(TownsystemException.class, () -> townworld.dissolveTown(notMayor, town));
	}

	@Test
	public void dissolveTownTestWithInvalidTown() throws TownsystemException {
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		Town town = mock(Town.class);
		when(town.getTownName()).thenReturn("mytown");
		doThrow(TownsystemException.class).when(validationHandler).checkForValueInList(new ArrayList<>(), "mytown");
		assertThrows(TownsystemException.class, () -> townworld.dissolveTown(null, town));
	}

	@Test
	public void dissolveTownTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		BankAccount account = mock(BankAccount.class);
		Inventory inv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack joinItem = mock(ItemStack.class);
		ItemStack leaveItem = mock(ItemStack.class);
		ItemMeta joinItemMeta = mock(ItemMeta.class);
		ItemMeta leaveItemMeta = mock(ItemMeta.class);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(null, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		Town town = townworld.getTownList().get(0);
		assertDoesNotThrow(() -> townworld.dissolveTown(ecoPlayer, town));

		assertEquals(0, townworld.getTownList().size());
		verify(villager).remove();
		assertDoesNotThrow(() -> verify(townworldManager, times(2)).performTownworldLocationCheckAllPlayers());
		assertDoesNotThrow(() -> verify(ecoPlayer).removeJoinedTown("mytown"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsMayor(ecoPlayer, ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(Arrays.asList("mytown"), "mytown"));
	}

	@Test
	public void deleteTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		BankAccount account = mock(BankAccount.class);
		Inventory inv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack joinItem = mock(ItemStack.class);
		ItemStack leaveItem = mock(ItemStack.class);
		ItemMeta joinItemMeta = mock(ItemMeta.class);
		ItemMeta leaveItemMeta = mock(ItemMeta.class);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(null, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		Townworld townworld = new TownworldImpl("world", true, townworldDao, validationHandler, townworldManager,
				messageWrapper, bankManager, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		assertDoesNotThrow(() -> townworld.delete());

		verify(townworldDao).deleteSavefile();
		assertEquals(0, townworld.getTownList().size());
	}
}
