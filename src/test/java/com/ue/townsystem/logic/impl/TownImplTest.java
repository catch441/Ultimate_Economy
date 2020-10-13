package com.ue.townsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyList;
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
import org.slf4j.Logger;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.townsystem.dataaccess.api.TownworldDao;
import com.ue.townsystem.logic.api.Plot;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.ultimate_economy.GeneralEconomyException;

@ExtendWith(MockitoExtension.class)
public class TownImplTest {

	@Mock
	MessageWrapper messageWrapper;
	@Mock
	TownsystemValidationHandler validationHandler;
	@Mock
	BankManager bankManager;
	@Mock
	TownworldManagerImpl townworldManager;
	@Mock
	ServerProvider serverProvider;
	@Mock
	Logger logger;
	@Mock
	TownworldDao townworldDao;

	private Town createTown() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
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
		return assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));
	}

	@Test
	public void constructorNewTest() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		BankAccount account = mock(BankAccount.class);
		Inventory inv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		Villager duplicated = mock(Villager.class);
		ItemStack joinItem = mock(ItemStack.class);
		ItemStack leaveItem = mock(ItemStack.class);
		ItemMeta joinItemMeta = mock(ItemMeta.class);
		ItemMeta leaveItemMeta = mock(ItemMeta.class);
		when(account.getIban()).thenReturn("myiban");
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
		when(duplicated.getName()).thenReturn("mytown TownManager");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(duplicated));
		Town town = assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));

		verify(villager).setCustomName("mytown TownManager");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager, times(2)).addPotionEffect(any(PotionEffect.class));

		verify(duplicated).remove();
		verify(chunk).load();
		verify(joinItemMeta).setDisplayName("Join");
		verify(joinItem).setItemMeta(joinItemMeta);
		verify(inv).setItem(0, joinItem);

		verify(leaveItemMeta).setDisplayName("Leave");
		verify(leaveItem).setItemMeta(leaveItemMeta);
		verify(inv).setItem(1, leaveItem);

		assertEquals("mytown", town.getTownName());
		assertEquals(townworld, town.getTownworld());
		assertEquals(Arrays.asList(mayor), town.getCitizens());
		assertEquals(0, town.getDeputies().size());
		assertEquals(mayor, town.getMayor());
		assertEquals("0.0", String.valueOf(town.getTax()));
		verify(townworldDao).saveTax("mytown", 0);
		verify(townworldDao).saveMayor("mytown", mayor);
		assertDoesNotThrow(() -> verify(mayor).addJoinedTown("mytown"));
		verify(townworldDao).saveCitizens("mytown", Arrays.asList(mayor));
		verify(townworldDao).saveTownManagerLocation("mytown", loc);
		verify(townworldDao).saveTownSpawn("mytown", town.getTownSpawn());
		verify(townworldDao).saveTownBankIban("mytown", "myiban");
		Plot plot = assertDoesNotThrow(() -> town.getPlotByChunk("1/2"));
		assertEquals(town, plot.getTown());
		assertEquals("1/2", plot.getChunkCoords());
		Location spawn = town.getTownSpawn();
		assertEquals("23.0", String.valueOf(spawn.getX()));
		assertEquals("60.0", String.valueOf(spawn.getY()));
		assertEquals("39.0", String.valueOf(spawn.getZ()));
	}

	@Test
	public void constructorLoadingTest() throws EconomyPlayerException {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		EconomyPlayer deputy = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Location spawn = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		BankAccount account = mock(BankAccount.class);
		Inventory inv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack joinItem = mock(ItemStack.class);
		ItemStack leaveItem = mock(ItemStack.class);
		ItemMeta joinItemMeta = mock(ItemMeta.class);
		ItemMeta leaveItemMeta = mock(ItemMeta.class);
		assertDoesNotThrow(() -> when(townworldDao.loadTownManagerLocation("mytown")).thenReturn(loc));
		when(townworldDao.loadTownBankIban("mytown")).thenReturn("iban");
		assertDoesNotThrow(() -> when(townworldDao.loadTownSpawn("mytown")).thenReturn(spawn));
		assertDoesNotThrow(() -> when(townworldDao.loadMayor("mytown")).thenReturn(mayor));
		assertDoesNotThrow(() -> when(townworldDao.loadDeputies("mytown")).thenReturn(Arrays.asList(deputy)));
		assertDoesNotThrow(() -> when(townworldDao.loadCitizens("mytown")).thenReturn(Arrays.asList(mayor)));
		when(townworldDao.loadTax("mytown")).thenReturn(1.5);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("iban"))).thenReturn(account);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(townworldDao).loadPlotOwner("mytown", "2/3");
		when(townworldDao.loadPlotOwner("mytown", "1/2")).thenReturn(null);
		when(townworldDao.loadTownPlotCoords("mytown")).thenReturn(Arrays.asList("1/2", "2/3"));
		Town town = assertDoesNotThrow(() -> new TownImpl("mytown", townworldManager, bankManager, validationHandler,
				messageWrapper, townworldDao, townworld, serverProvider, logger));

		assertEquals("mytown", town.getTownName());
		assertEquals(townworld, town.getTownworld());
		assertEquals(spawn, town.getTownSpawn());
		assertEquals(mayor, town.getMayor());
		assertEquals(Arrays.asList(deputy), town.getDeputies());
		assertEquals(Arrays.asList(mayor), town.getCitizens());
		assertEquals("1.5", String.valueOf(town.getTax()));
		Plot plot = assertDoesNotThrow(() -> town.getPlotByChunk("1/2"));
		assertEquals(town, plot.getTown());
		assertEquals("1/2", plot.getChunkCoords());
		verify(logger).warn("[Ultimate_Economy] Failed to load plot 2/3 of town mytown");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");

		verify(joinItemMeta).setDisplayName("Join");
		verify(joinItem).setItemMeta(joinItemMeta);
		verify(inv).setItem(0, joinItem);

		verify(leaveItemMeta).setDisplayName("Leave");
		verify(leaveItem).setItemMeta(leaveItemMeta);
		verify(inv).setItem(1, leaveItem);
		verify(chunk).load();
		verify(villager).setCustomName("mytown TownManager");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager, times(2)).addPotionEffect(any(PotionEffect.class));
	}

	@Test
	public void joinTownTestWithMaxTowns() throws EconomyPlayerException {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerDidNotReachedMaxTowns(ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> town.joinTown(ecoPlayer));
		assertEquals(1, town.getCitizens().size());
	}

	@Test
	public void joinTownTestWitAlreadyJoined() throws EconomyPlayerException {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler)
				.checkForPlayerIsNotCitizenPersonal(town.getCitizens(), ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> town.joinTown(ecoPlayer));
		assertEquals(1, town.getCitizens().size());
	}

	@Test
	public void joinTownTest() {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> town.joinTown(ecoPlayer));

		assertDoesNotThrow(() -> verify(validationHandler)
				.checkForPlayerIsNotCitizenPersonal(Arrays.asList(town.getMayor()), ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerDidNotReachedMaxTowns(ecoPlayer));

		assertEquals(2, town.getCitizens().size());
		assertEquals(ecoPlayer, town.getCitizens().get(1));
		verify(townworldDao).saveCitizens("mytown", town.getCitizens());
		assertDoesNotThrow(() -> verify(ecoPlayer).addJoinedTown("mytown"));
	}

	@Test
	public void isPlayerCitizenTest() {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertTrue(town.isPlayerCitizen(town.getMayor()));
		assertFalse(town.isPlayerCitizen(ecoPlayer));
	}

	@Test
	public void isMayorTest() {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertTrue(town.isMayor(town.getMayor()));
		assertFalse(town.isMayor(ecoPlayer));
	}

	@Test
	public void increaseTownBankAmountTest() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
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
		Town town = assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));

		assertDoesNotThrow(() -> town.increaseTownBankAmount(1.5));
		assertDoesNotThrow(() -> verify(account).increaseAmount(1.5));
	}

	@Test
	public void decreaseTownBankAmountTestWithNotEnoughMoney() throws TownSystemException {
		Town town = createTown();
		doThrow(TownSystemException.class).when(validationHandler).checkForTownHasEnoughMoney(0.0, 1.5);
		assertThrows(TownSystemException.class, () -> town.decreaseTownBankAmount(1.5));
	}

	@Test
	public void decreaseTownBankAmountTest() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
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
		Town town = assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));

		assertDoesNotThrow(() -> town.decreaseTownBankAmount(1.5));
		assertDoesNotThrow(() -> verify(validationHandler).checkForTownHasEnoughMoney(0.0, 1.5));
		assertDoesNotThrow(() -> verify(account).decreaseAmount(1.5));
	}

	@Test
	public void setTaxTestWithInvalidValue() throws GeneralEconomyException {
		Town town = createTown();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForPositiveAmount(-1.5);
		assertThrows(GeneralEconomyException.class, () -> town.setTax(-1.5));
	}

	@Test
	public void setTaxTest() {
		Town town = createTown();
		assertDoesNotThrow(() -> town.setTax(1.5));

		assertEquals("1.5", String.valueOf(town.getTax()));
		verify(townworldDao).saveTax("mytown", 1.5);
	}

	@Test
	public void hasEnoughMoneyTest() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
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
		Town town = assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));
		when(account.getAmount()).thenReturn(1.0);

		assertTrue(town.hasEnoughMoney(1.0));
		assertFalse(town.hasEnoughMoney(1.5));
	}

	@Test
	public void addDeputyTestWithIsAlreadyDeputy() throws TownSystemException {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(TownSystemException.class).when(validationHandler).checkForPlayerIsNotDeputy(new ArrayList<>(),
				ecoPlayer);
		assertThrows(TownSystemException.class, () -> town.addDeputy(ecoPlayer));
	}

	@Test
	public void addDeputyTest() {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> town.addDeputy(ecoPlayer));

		assertTrue(town.isPlayerCitizen(ecoPlayer));
		assertEquals(1, town.getDeputies().size());
		assertEquals(ecoPlayer, town.getDeputies().get(0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsNotDeputy(new ArrayList<>(), ecoPlayer));
		verify(townworldDao).saveDeputies("mytown", Arrays.asList(ecoPlayer));
	}

	@Test
	public void removeDeputyTestWithIsNoDeputy() throws TownSystemException {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(TownSystemException.class).when(validationHandler).checkForPlayerIsDeputy(town.getDeputies(),
				ecoPlayer);
		assertThrows(TownSystemException.class, () -> town.removeDeputy(ecoPlayer));
	}

	@Test
	public void removeDeputyTest() {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> town.addDeputy(ecoPlayer));
		assertDoesNotThrow(() -> town.removeDeputy(ecoPlayer));
		assertEquals(0, town.getDeputies().size());
		verify(townworldDao).saveDeputies("mytown", new ArrayList<>());
	}

	@Test
	public void hasDeputyPermissionsTest() {
		Town town = createTown();
		EconomyPlayer ecoPlayer1 = mock(EconomyPlayer.class);
		EconomyPlayer ecoPlayer2 = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> town.addDeputy(ecoPlayer1));
		assertTrue(town.hasDeputyPermissions(ecoPlayer1));
		assertFalse(town.hasDeputyPermissions(ecoPlayer2));
		assertTrue(town.hasDeputyPermissions(town.getMayor()));
	}

	@Test
	public void expandTownTestWithChunkAlreadyClaimed() throws TownSystemException {
		Town town = createTown();
		Chunk chunk = mock(Chunk.class);
		doThrow(TownSystemException.class).when(validationHandler).checkForChunkNotClaimed(town.getTownworld(), chunk);
		assertThrows(TownSystemException.class, () -> town.expandTown(chunk, null));
	}

	@Test
	public void expandTownTestWithNoPermission() throws EconomyPlayerException {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerHasDeputyPermission(false);
		assertThrows(EconomyPlayerException.class, () -> town.expandTown(null, ecoPlayer));
	}

	@Test
	public void expandTownTestWithNotConnectedToTown() throws TownSystemException {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Chunk chunk = mock(Chunk.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		doThrow(TownSystemException.class).when(validationHandler).checkForChunkIsConnectedToTown(false);
		assertThrows(TownSystemException.class, () -> town.expandTown(chunk, ecoPlayer));
	}

	@Test
	public void expandTownTest() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
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
		when(townworld.getExpandPrice()).thenReturn(2.5);
		Town town = assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));

		Chunk newChunk = mock(Chunk.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(newChunk.getX()).thenReturn(1);
		when(newChunk.getZ()).thenReturn(3);
		assertDoesNotThrow(() -> town.expandTown(newChunk, ecoPlayer));
		assertDoesNotThrow(() -> verify(account).decreaseAmount(2.5));

		Plot plot = assertDoesNotThrow(() -> town.getPlotByChunk("1/3"));
		assertEquals("1/3", plot.getChunkCoords());
		assertEquals(town, plot.getTown());
		assertDoesNotThrow(() -> verify(townworldManager).performTownworldLocationCheckAllPlayers());
	}

	@Test
	public void isClaimedByTownTest() {
		Town town = createTown();
		Chunk chunk = mock(Chunk.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		Chunk chunk2 = mock(Chunk.class);
		when(chunk2.getX()).thenReturn(1);
		when(chunk2.getZ()).thenReturn(3);
		assertTrue(town.isClaimedByTown(chunk));
		assertFalse(town.isClaimedByTown(chunk2));
	}

	@Test
	public void hasBuildPermissionsTest() {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Plot plot = assertDoesNotThrow(() -> town.getPlotByChunk("1/2"));
		assertFalse(town.hasBuildPermissions(ecoPlayer, plot));
		assertTrue(town.hasBuildPermissions(town.getMayor(), plot));
		assertDoesNotThrow(() -> town.joinTown(ecoPlayer));
		assertDoesNotThrow(() -> plot.addResident(ecoPlayer));
		assertTrue(town.hasBuildPermissions(ecoPlayer, plot));
	}

	@Test
	public void getPlotByChunkTestWithNotClaimed() {
		Town town = createTown();
		assertThrows(TownSystemException.class, () -> town.getPlotByChunk("2/3"));
	}

	@Test
	public void getPlotByChunkTest() {
		Town town = createTown();
		Plot plot = assertDoesNotThrow(() -> town.getPlotByChunk("1/2"));
		assertEquals("1/2", plot.getChunkCoords());
	}

	@Test
	public void changeTownSpawnTestWithNoPermission() {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> doThrow(EconomyPlayerException.class).when(validationHandler)
				.checkForPlayerHasDeputyPermission(false));
		assertThrows(EconomyPlayerException.class, () -> town.changeTownSpawn(null, ecoPlayer));
	}

	@Test
	public void changeTownSpawnTestWithLocationNotInTown() {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		assertDoesNotThrow(() -> doThrow(TownSystemException.class).when(validationHandler)
				.checkForLocationIsInTown(anyMap(), eq(loc)));
		assertThrows(TownSystemException.class, () -> town.changeTownSpawn(loc, ecoPlayer));
	}

	@Test
	public void changeTownSpawnTest() {
		Town town = createTown();
		Location loc = mock(Location.class);
		assertDoesNotThrow(() -> town.changeTownSpawn(loc, town.getMayor()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerHasDeputyPermission(true));
		assertDoesNotThrow(() -> verify(validationHandler).checkForLocationIsInTown(anyMap(), eq(loc)));
		assertEquals(loc, town.getTownSpawn());
		verify(townworldDao).saveTownSpawn("mytown", loc);
	}

	@Test
	public void leaveTownTestWithIsMayor() throws EconomyPlayerException {
		Town town = createTown();
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerIsNotMayor(town.getMayor(),
				town.getMayor());
		assertThrows(EconomyPlayerException.class, () -> town.leaveTown(town.getMayor()));
	}

	@Test
	public void leaveTownTestWithIsNotCitizen() throws EconomyPlayerException {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler)
				.checkForPlayerIsCitizenPersonalError(town.getCitizens(), ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> town.leaveTown(ecoPlayer));
	}

	@Test
	public void leaveTownTest() {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> town.joinTown(ecoPlayer));
		assertDoesNotThrow(() -> town.addDeputy(ecoPlayer));
		Plot plot = assertDoesNotThrow(() -> town.getPlotByChunk("1/2"));
		assertDoesNotThrow(() -> plot.addResident(ecoPlayer));

		assertDoesNotThrow(() -> town.leaveTown(ecoPlayer));

		assertFalse(plot.isResident(ecoPlayer));
		assertFalse(town.isPlayerCitizen(ecoPlayer));
		assertFalse(town.isDeputy(ecoPlayer));
		assertDoesNotThrow(() -> verify(ecoPlayer).removeJoinedTown("mytown"));
		verify(townworldDao, times(2)).saveCitizens("mytown", Arrays.asList(town.getMayor()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsNotMayor(town.getMayor(), ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForPlayerIsCitizenPersonalError(anyList(), eq(ecoPlayer)));
	}

	@Test
	public void leaveTownTestWithOwningPlot() {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> town.joinTown(ecoPlayer));
		assertDoesNotThrow(() -> town.addDeputy(ecoPlayer));
		Plot plot = assertDoesNotThrow(() -> town.getPlotByChunk("1/2"));
		assertDoesNotThrow(() -> plot.setOwner(ecoPlayer));

		assertDoesNotThrow(() -> town.leaveTown(ecoPlayer));

		assertFalse(plot.isOwner(ecoPlayer));
		assertFalse(town.isPlayerCitizen(ecoPlayer));
		assertFalse(town.isDeputy(ecoPlayer));
		assertDoesNotThrow(() -> verify(ecoPlayer).removeJoinedTown("mytown"));
		verify(townworldDao, times(2)).saveCitizens("mytown", Arrays.asList(town.getMayor()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsNotMayor(town.getMayor(), ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForPlayerIsCitizenPersonalError(anyList(), eq(ecoPlayer)));
	}

	@Test
	public void moveTownManagerVillagerTestWithLocationNotInTown() throws TownSystemException {
		Town town = createTown();
		Location loc = mock(Location.class);
		doThrow(TownSystemException.class).when(validationHandler).checkForLocationIsInTown(anyMap(), eq(loc));
		assertThrows(TownSystemException.class, () -> town.moveTownManagerVillager(loc, null));
	}

	@Test
	public void moveTownManagerVillagerTestWithNotMayor() throws EconomyPlayerException {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerIsMayor(town.getMayor(), ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> town.moveTownManagerVillager(null, ecoPlayer));
	}

	@Test
	public void moveTownManagerVillagerTest() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
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
		Town town = assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));
		Location newLoc = mock(Location.class);
		assertDoesNotThrow(() -> town.moveTownManagerVillager(newLoc, town.getMayor()));

		verify(villager).teleport(newLoc);
		verify(townworldDao).saveTownManagerLocation("mytown", newLoc);
		assertDoesNotThrow(() -> verify(validationHandler).checkForLocationIsInTown(anyMap(), eq(newLoc)));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsMayor(town.getMayor(), town.getMayor()));
	}

	@Test
	public void deletePlotTestNotClaimed() throws TownSystemException {
		Town town = createTown();
		Plot plot = mock(Plot.class);
		when(plot.getChunkCoords()).thenReturn("1/3");
		doThrow(TownSystemException.class).when(validationHandler).checkForChunkIsClaimedByThisTown(anyMap(),
				eq("1/3"));
		assertThrows(TownSystemException.class, () -> town.deletePlot(plot));
	}

	@Test
	public void deletePlotTest() {
		Town town = createTown();
		Chunk chunk = mock(Chunk.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		assertDoesNotThrow(() -> town.deletePlot(town.getPlotByChunk("1/2")));
		verify(townworldDao).saveRemovePlot("mytown", "1/2");
		assertFalse(town.isClaimedByTown(chunk));
		assertDoesNotThrow(() -> verify(validationHandler).checkForChunkIsClaimedByThisTown(anyMap(), eq("1/2")));
	}

	@Test
	public void despawnAllVillagersTest() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
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
		Town town = assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));

		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(villager, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(villager.getLocation()).thenReturn(loc);

		Chunk chunk2 = mock(Chunk.class);
		when(chunk2.getX()).thenReturn(1);
		when(chunk2.getZ()).thenReturn(3);
		assertDoesNotThrow(() -> town.expandTown(chunk2, town.getMayor()));
		assertDoesNotThrow(() -> town.getPlotByChunk("1/3").setForSale(1.0, loc, town.getMayor()));

		town.despawnAllVillagers();

		verify(villager, times(2)).remove();
	}

	@Test
	public void buyPlotTestWithNotForSale() throws TownSystemException {
		Town town = createTown();
		doThrow(TownSystemException.class).when(validationHandler).checkForPlotIsForSale(false);
		assertThrows(TownSystemException.class, () -> town.buyPlot(null, 1, 2));
	}

	@Test
	public void buyPlotTestWithPlotOwner() throws EconomyPlayerException, TownSystemException {
		Town town = createTown();
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerIsNotPlotOwner(town.getMayor(),
				town.getPlotByChunk("1/2"));
		assertThrows(EconomyPlayerException.class, () -> town.buyPlot(town.getMayor(), 1, 2));
	}
	
	@Test
	public void buyPlotTest() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
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
		Town town = assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));

		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(villager, 9, "Plot 1/2")).thenReturn(inv);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getWorld()).thenReturn(world);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(villager.getLocation()).thenReturn(loc);
		
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Plot plot = assertDoesNotThrow(() -> town.getPlotByChunk("1/2"));
		assertDoesNotThrow(() -> plot.addResident(town.getMayor()));
		assertDoesNotThrow(() -> plot.setForSale(1.5, loc, town.getMayor()));
		
		assertDoesNotThrow(() -> town.buyPlot(ecoPlayer, 1, 2));
		
		assertTrue(plot.isOwner(ecoPlayer));
		assertEquals(0, plot.getResidents().size());
		assertFalse(plot.isForSale());
	}
	
	@Test
	public void openTownManagerVillagerInvTest() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
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
		Town town = assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));
		
		Player player = mock(Player.class);
		town.openTownManagerVillagerInv(player);
		verify(player).openInventory(inv);
	}
	
	@Test
	public void renameTownTestWithAlreadyExists() throws GeneralEconomyException {
		Town town = createTown();
		when(townworldManager.getTownNameList()).thenReturn(Arrays.asList("mytown1"));
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForTownDoesNotExist(Arrays.asList("mytown1"),
				"mytown1");
		assertThrows(GeneralEconomyException.class, () -> town.renameTown("mytown1", town.getMayor()));
	}
	
	@Test
	public void renameTownTestWithNotMayor() throws EconomyPlayerException {
		Town town = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerIsMayor(town.getMayor(), ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> town.renameTown("mytown1", ecoPlayer));
	}
	
	@Test
	public void renameTownTest() {
		Plugin plugin = mock(Plugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
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
		Town town = assertDoesNotThrow(() -> new TownImpl(mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider, logger));
		
		assertDoesNotThrow(() -> town.renameTown("newname", mayor));
		
		assertEquals("newname", town.getTownName());
		assertDoesNotThrow(() -> verify(mayor).removeJoinedTown("mytown"));
		assertDoesNotThrow(() -> verify(mayor).addJoinedTown("newname"));
		verify(townworldManager).setTownNameList(Arrays.asList("newname"));
		verify(villager).setCustomName("newname TownManager");
		verify(townworldDao).saveRenameTown("mytown", "newname");
	}
}
