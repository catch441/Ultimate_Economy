package com.ue.townsystem.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.never;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.ServiceComponent;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.townsystem.dataaccess.api.TownworldDao;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;

@ExtendWith(MockitoExtension.class)
public class TownworldManagerImplTest {

	@InjectMocks
	TownworldManagerImpl townworldManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ServerProvider serverProvider;
	@Mock
	TownsystemValidationHandler townsystemValidationHandler;
	@Mock
	Logger logger;
	@Mock
	BankManager bankManager;
	@Mock
	EconomyPlayerValidationHandler ecoPlayerValidationHandler;
	@Mock
	ConfigDao configDao;

	@Test
	public void createTownWorldTestWithWorldDoesNotExist() throws TownSystemException {
		doThrow(TownSystemException.class).when(townsystemValidationHandler).checkForWorldExists("world");
		assertThrows(TownSystemException.class, () -> townworldManager.createTownWorld("world"));
		verify(configDao, never()).saveTownworldNamesList(anyList());
	}

	@Test
	public void createTownWorldTestWithTownworldExists() throws TownSystemException {
		doThrow(TownSystemException.class).when(townsystemValidationHandler).checkForTownworldDoesNotExist(anyMap(),
				eq("world"));
		assertThrows(TownSystemException.class, () -> townworldManager.createTownWorld("world"));
		verify(configDao, never()).saveTownworldNamesList(anyList());
	}

	@Test
	public void createTownTest() {
		TownworldDao dao = mock(TownworldDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getTownworldDao()).thenReturn(dao);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
		verify(configDao).saveTownworldNamesList(Arrays.asList("world"));
		assertEquals(Arrays.asList("world"), townworldManager.getTownWorldNameList());
		verify(dao).setupSavefile("world");
	}

	@Test
	public void deleteTownWorldTestWithWorldDoesNotExist() throws TownSystemException {
		doThrow(TownSystemException.class).when(townsystemValidationHandler).checkForWorldExists("world");
		assertThrows(TownSystemException.class, () -> townworldManager.deleteTownWorld("world"));
		verify(configDao, never()).saveTownworldNamesList(anyList());
	}

	@Test
	public void deleteTownWorldTestWithTownworldDoesNotExists() throws TownSystemException {
		doThrow(TownSystemException.class).when(townsystemValidationHandler).checkForTownworldExists(anyMap(),
				eq("world"));
		assertThrows(TownSystemException.class, () -> townworldManager.deleteTownWorld("world"));
		verify(configDao, never()).saveTownworldNamesList(anyList());
	}

	@Test
	public void deleteTownWorldTest() {
		TownworldDao dao = mock(TownworldDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getTownworldDao()).thenReturn(dao);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
		assertDoesNotThrow(() -> townworldManager.deleteTownWorld("world"));
		verify(dao).deleteSavefile();
		verify(configDao).saveTownworldNamesList(new ArrayList<>());
		assertEquals(0, townworldManager.getTownWorldList().size());
	}

	@Test
	public void loadAllTownWorldsTest() {
		TownworldDao dao = mock(TownworldDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getTownworldDao()).thenReturn(dao);
		when(configDao.loadTownworldNames()).thenReturn(Arrays.asList("myworld"));
		townworldManager.loadAllTownWorlds();
		verify(dao).setupSavefile("myworld");
		assertEquals(1, townworldManager.getTownWorldList().size());
		assertDoesNotThrow(() -> assertNotNull(townworldManager.getTownWorldByName("myworld")));
	}

	@Test
	public void isTownWorldTest() {
		TownworldDao dao = mock(TownworldDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getTownworldDao()).thenReturn(dao);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));

		assertTrue(townworldManager.isTownWorld("world"));
		assertFalse(townworldManager.isTownWorld("other"));
	}

	@Test
	public void getTownWorldByNameTest() {
		TownworldDao dao = mock(TownworldDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getTownworldDao()).thenReturn(dao);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));

		Townworld townworld = assertDoesNotThrow(() -> townworldManager.getTownWorldByName("world"));
		assertEquals("world", townworld.getWorldName());
	}

	@Test
	public void getTownWorldByNameTestWithNoTownworld() {
		try {
			townworldManager.getTownWorldByName("world");
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.TOWNWORLD_DOES_NOT_EXIST, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void getTownByNameTestWithNoTown() {
		try {
			townworldManager.getTownByName("town");
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("town", e.getParams()[0]);
		}
	}

	@Test
	public void getTownNameListTest() {
		townworldManager.setTownNameList(Arrays.asList("mytown1"));
		assertEquals(Arrays.asList("mytown1"), townworldManager.getTownNameList());
	}

	@Test
	public void performTownworldLocationCheckAllPlayersTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		World world = mock(World.class);
		BossBar bossbar = mock(BossBar.class);
		Location loc = mock(Location.class);
		when(player.getLocation()).thenReturn(loc);
		when(ecoPlayer.getBossBar()).thenReturn(bossbar);
		when(player.getWorld()).thenReturn(world);
		when(player.getName()).thenReturn("catch441");
		when(serverProvider.getOnlinePlayers()).thenReturn(Arrays.asList(player));
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		
		assertDoesNotThrow(() -> townworldManager.performTownworldLocationCheckAllPlayers());
		
		verify(bossbar).setVisible(false);
	}

	@Test
	public void performTownWorldLocationCheckTestWithNoTownworld() {
		Chunk chunk = mock(Chunk.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BossBar bossbar = mock(BossBar.class);
		when(ecoPlayer.getBossBar()).thenReturn(bossbar);
		townworldManager.performTownWorldLocationCheck("myworld", chunk, ecoPlayer);
	
		verify(bossbar).setVisible(false);
	}
	
	@Test
	public void performTownWorldLocationCheckTestWithWilderness() {
		TownworldDao dao = mock(TownworldDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getTownworldDao()).thenReturn(dao);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
		Chunk chunk = mock(Chunk.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BossBar bossbar = mock(BossBar.class);
		when(ecoPlayer.getBossBar()).thenReturn(bossbar);
		townworldManager.performTownWorldLocationCheck("world", chunk, ecoPlayer);
		
		verify(bossbar).setTitle("Wilderness");
		verify(bossbar).setColor(BarColor.GREEN);
		verify(bossbar).setVisible(true);
	}
	
	@Test
	public void performTownWorldLocationCheckTestWithInTown() {
		TownworldDao dao = mock(TownworldDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getTownworldDao()).thenReturn(dao);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
		BossBar bossbar = mock(BossBar.class);				
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
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Townworld townworld = assertDoesNotThrow(() -> townworldManager.getTownWorldByName("world"));
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		when(ecoPlayer.getBossBar()).thenReturn(bossbar);
		townworldManager.performTownWorldLocationCheck("world", chunk, ecoPlayer);
		
		verify(bossbar).setTitle("mytown");
		verify(bossbar).setColor(BarColor.RED);
		verify(bossbar).setVisible(true);
	}

	@Test
	public void getTownByNameTest() {
		TownworldDao dao = mock(TownworldDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getTownworldDao()).thenReturn(dao);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
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
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Townworld townworld = assertDoesNotThrow(() -> townworldManager.getTownWorldByName("world"));
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		Town town = assertDoesNotThrow(() -> townworldManager.getTownByName("mytown"));
		assertEquals("mytown", town.getTownName());
	}
	
	@Test
	public void despawnAllVillagersTest() {
		TownworldDao dao = mock(TownworldDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getTownworldDao()).thenReturn(dao);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
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
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Townworld townworld = assertDoesNotThrow(() -> townworldManager.getTownWorldByName("world"));
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		
		townworldManager.despawnAllVillagers();
		verify(villager).remove();
	}
}
