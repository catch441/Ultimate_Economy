package com.ue.townsystem.logic.impl;

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
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.townsystem.dataaccess.api.TownsystemDao;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

@ExtendWith(MockitoExtension.class)
public class TownworldImplTest {

	@Mock
	TownsystemValidationHandler townsystemValidationHandler;
	@Mock
	EconomyPlayerValidationHandler ecoPlayerValidationHandler;
	@Mock
	TownworldManagerImpl townworldManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	BankManager bankManager;
	@Mock
	ServerProvider serverProvider;
	@Mock
	Logger logger;
	@Mock
	TownsystemDao townsystemDao;

	private void setupMocksForLoadingTown(String townName) {
		Plugin plugin = mock(Plugin.class);
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
		assertDoesNotThrow(() -> when(townsystemDao.loadTownManagerLocation(townName)).thenReturn(loc));
		when(townsystemDao.loadTownBankIban(townName)).thenReturn("iban");
		assertDoesNotThrow(() -> when(townsystemDao.loadTownSpawn(townName)).thenReturn(spawn));
		assertDoesNotThrow(() -> when(townsystemDao.loadMayor(townName)).thenReturn(mayor));
		assertDoesNotThrow(() -> when(townsystemDao.loadDeputies(townName)).thenReturn(Arrays.asList(deputy)));
		assertDoesNotThrow(() -> when(townsystemDao.loadCitizens(townName)).thenReturn(Arrays.asList(mayor)));
		when(townsystemDao.loadTax(townName)).thenReturn(1.5);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(villager, 9, townName + " TownManager")).thenReturn(inv);
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("iban"))).thenReturn(account);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
	}
	
	private Location setupMocksForNewTown() {
		Plugin plugin = mock(Plugin.class);
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
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
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
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);

		assertEquals("world", townworld.getWorldName());
		assertEquals("0.0", String.valueOf(townworld.getExpandPrice()));
		assertEquals("0.0", String.valueOf(townworld.getFoundationPrice()));
		verify(townsystemDao).saveExpandPrice(0.0);
		verify(townsystemDao).saveFoundationPrice(0.0);
		verify(townsystemDao).saveWorldName("world");
	}

	@Test
	public void constructorLoadingTest() throws TownSystemException {
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(townsystemDao).loadTownManagerLocation("corrupted");
		setupMocksForLoadingTown("mytown");
		when(townsystemDao.loadFoundationPrice()).thenReturn(1.5);
		when(townsystemDao.loadExpandPrice()).thenReturn(2.5);
		when(townsystemDao.loadTownworldTownNames()).thenReturn(Arrays.asList("mytown", "corrupted"));
		Townworld townworld = new TownworldImpl("world", false, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);

		verify(logger).warn("[Ultimate_Economy] Failed to load town corrupted");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
		assertEquals("world", townworld.getWorldName());
		assertEquals("2.5", String.valueOf(townworld.getExpandPrice()));
		assertEquals("1.5", String.valueOf(townworld.getFoundationPrice()));

		assertEquals(1, townworld.getTownList().size());
		assertDoesNotThrow(() -> assertNotNull(townworld.getTownByName("mytown")));
	}

	@Test
	public void setFoundationPriceTestWithNegative() throws GeneralEconomyException {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		doThrow(GeneralEconomyException.class).when(townsystemValidationHandler).checkForPositiveAmount(-1.5);
		assertThrows(GeneralEconomyException.class, () -> townworld.setFoundationPrice(-1.5));
		assertEquals("0.0", String.valueOf(townworld.getFoundationPrice()));
	}

	@Test
	public void setFoundationPriceTest() {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		assertDoesNotThrow(() -> townworld.setFoundationPrice(1.5));
		assertDoesNotThrow(() -> verify(townsystemValidationHandler).checkForPositiveAmount(1.5));
		verify(townsystemDao).saveFoundationPrice(1.5);
		assertEquals("1.5", String.valueOf(townworld.getFoundationPrice()));
	}
	
	@Test
	public void setExpandPriceTestWithNegative() throws GeneralEconomyException {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		doThrow(GeneralEconomyException.class).when(townsystemValidationHandler).checkForPositiveAmount(-1.5);
		assertThrows(GeneralEconomyException.class, () -> townworld.setExpandPrice(-1.5));
		assertEquals("0.0", String.valueOf(townworld.getExpandPrice()));
	}

	@Test
	public void setExpandPriceTest() {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		assertDoesNotThrow(() -> townworld.setExpandPrice(1.5));
		assertDoesNotThrow(() -> verify(townsystemValidationHandler).checkForPositiveAmount(1.5));
		verify(townsystemDao).saveExpandPrice(1.5);
		assertEquals("1.5", String.valueOf(townworld.getExpandPrice()));
	}
	
	@Test
	public void foundTownTestWithAlreadyExists() throws GeneralEconomyException {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		when(townworldManager.getTownNameList()).thenReturn(Arrays.asList("mytown"));
		doThrow(GeneralEconomyException.class).when(townsystemValidationHandler).checkForTownDoesNotExist(Arrays.asList("mytown"), "mytown");
		assertThrows(GeneralEconomyException.class, () -> townworld.foundTown("mytown", null, null));
	}
	
	@Test
	public void foundTownTestWithChunkIsOccupied() throws TownSystemException {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		Location loc = mock(Location.class);
		doThrow(TownSystemException.class).when(townsystemValidationHandler).checkForChunkIsFree(townworld, loc);
		assertThrows(TownSystemException.class, () -> townworld.foundTown("mytown", loc, null));
	}
	
	@Test
	public void foundTownTestReachedMaxJoinedTowns() throws EconomyPlayerException {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.reachedMaxJoinedTowns()).thenReturn(true);
		doThrow(EconomyPlayerException.class).when(ecoPlayerValidationHandler).checkForNotReachedMaxJoinedTowns(true);
		assertThrows(EconomyPlayerException.class, () -> townworld.foundTown("mytown", null, ecoPlayer));
	}
	
	@Test
	public void foundTownTestWithNotEnoughMoney() throws EconomyPlayerException, GeneralEconomyException {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		doThrow(EconomyPlayerException.class).when(ecoPlayerValidationHandler).checkForEnoughMoney(account, 0.0, true);
		assertThrows(EconomyPlayerException.class, () -> townworld.foundTown("mytown", null, ecoPlayer));
	}
	
	@Test
	public void foundTownTest() {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
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
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		
		assertEquals(Arrays.asList("mytown"), townworld.getTownNameList());
	}
	
	@Test
	public void getTownByChunkTest() {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		
		Town town = assertDoesNotThrow(() -> townworld.getTownByChunk(loc.getChunk()));
		assertEquals("mytown", town.getTownName());
	}
	
	@Test
	public void getTownByChunkTestWithNoTown() {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		
		Chunk chunk = mock(Chunk.class);
		try {
			townworld.getTownByChunk(chunk);
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.CHUNK_NOT_CLAIMED, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}
	
	@Test
	public void getTownByNameTest() {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		
		Town town = assertDoesNotThrow(() -> townworld.getTownByName("mytown"));
		assertEquals("mytown", town.getTownName());
	}
	
	@Test
	public void getTownByNameTestWithNoTown() {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		
		try {
			townworld.getTownByName("mytown");
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("mytown", e.getParams()[0]);
		}
	}
	
	@Test
	public void isChunkFreeTest() {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		
		Chunk chunk = mock(Chunk.class);
		assertFalse(townworld.isChunkFree(loc.getChunk()));
		assertTrue(townworld.isChunkFree(chunk));
	}
	
	@Test
	public void despawnAllTownVillagersTest() {
		Plugin plugin = mock(Plugin.class);
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
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		
		townworld.despawnAllTownVillagers();
		verify(villager).remove();
	}
	
	@Test
	public void dissolveTownTestWithNotMayor() throws EconomyPlayerException {
		Location loc = setupMocksForNewTown();
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		Town town = townworld.getTownList().get(0);
		EconomyPlayer notMayor = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(townsystemValidationHandler).checkForPlayerIsMayor(ecoPlayer, notMayor);
		assertThrows(EconomyPlayerException.class, () -> townworld.dissolveTown(notMayor, town));
	}
	
	@Test
	public void dissolveTownTestWithInvalidTown() throws GeneralEconomyException {
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		Town town = mock(Town.class);
		when(town.getTownName()).thenReturn("mytown");
		doThrow(GeneralEconomyException.class).when(townsystemValidationHandler).checkForTownworldHasTown(new ArrayList<>(), "mytown");
		assertThrows(GeneralEconomyException.class, () -> townworld.dissolveTown(null, town));
	}
	
	@Test
	public void dissolveTownTest() {
		Plugin plugin = mock(Plugin.class);
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
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		Town town = townworld.getTownList().get(0);
		assertDoesNotThrow(() -> townworld.dissolveTown(ecoPlayer, town));
		
		assertEquals(0, townworld.getTownList().size());
		verify(villager).remove();
		assertDoesNotThrow(() -> verify(townworldManager, times(2)).performTownworldLocationCheckAllPlayers());
		assertDoesNotThrow(() -> verify(ecoPlayer).removeJoinedTown("mytown"));
		assertDoesNotThrow(() -> verify(townsystemValidationHandler).checkForPlayerIsMayor(ecoPlayer, ecoPlayer));
		assertDoesNotThrow(() -> verify(townsystemValidationHandler).checkForTownworldHasTown(Arrays.asList("mytown"), "mytown"));
	}
	
	@Test
	public void deleteTest() {
		Plugin plugin = mock(Plugin.class);
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
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		Townworld townworld = new TownworldImpl("world", true, townsystemDao, townsystemValidationHandler,
				ecoPlayerValidationHandler, townworldManager, messageWrapper, bankManager, logger, serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		
		assertDoesNotThrow(() -> townworld.delete());
		
		verify(townsystemDao).deleteSavefile();
		assertEquals(0, townworld.getTownList().size());
	}
}
