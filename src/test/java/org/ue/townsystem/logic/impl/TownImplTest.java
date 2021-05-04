package org.ue.townsystem.logic.impl;

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
import static org.mockito.Mockito.reset;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.townsystem.dataaccess.api.TownworldDao;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemValidationHandler;
import org.ue.townsystem.logic.api.Townworld;

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
	TownworldDao townworldDao;

	private class Values {
		Town town;
		Villager villager;
		EconomyPlayer mayor;
		BankAccount account;
		Inventory inv, plotInv;
		Location loc;
		Chunk chunk;
		World world;
		JavaPlugin plugin;
		Townworld townworld;

		public Values(Town town, Villager villager, EconomyPlayer mayor, BankAccount account, Inventory inv,
				Inventory plotInv, Location loc, Chunk chunk, World world, JavaPlugin plugin, Townworld townworld) {
			this.town = town;
			this.villager = villager;
			this.mayor = mayor;
			this.account = account;
			this.inv = inv;
			this.loc = loc;
			this.chunk = chunk;
			this.world = world;
			this.plugin = plugin;
			this.townworld = townworld;
			this.plotInv = plotInv;
		}
	}

	private Values createTown() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		BankAccount account = mock(BankAccount.class);
		Inventory inv = mock(Inventory.class);
		Inventory plotInv = mock(Inventory.class);
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
		when(serverProvider.createInventory(null, 9, "Plot 1/2")).thenReturn(plotInv);
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		when(bankManager.createBankAccount(0)).thenReturn(account);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(chunk.getWorld()).thenReturn(world);
		when(world.getHighestBlockYAt(any(Location.class))).thenReturn(60);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		Town town = assertDoesNotThrow(() -> new TownImpl(true, mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider));
		return new Values(town, villager, mayor, account, inv, plotInv, loc, chunk, world, plugin, townworld);
	}

	@Test
	public void constructorNewTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
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
		Inventory plotInv = mock(Inventory.class);
		when(account.getIban()).thenReturn("myiban");
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(null, 9, "Plot 1/2")).thenReturn(plotInv);
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
		Town town = assertDoesNotThrow(() -> new TownImpl(true, mayor, "mytown", loc, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider));

		verify(villager).setCustomName("mytown TownManager");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.NITWIT);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager).addPotionEffect(any(PotionEffect.class));

		verify(duplicated).remove();
		verify(chunk).load();
		verify(joinItemMeta).setDisplayName("Join");
		verify(joinItem, times(2)).setItemMeta(joinItemMeta);
		verify(inv).setItem(0, joinItem);

		verify(leaveItemMeta).setDisplayName("Leave");
		verify(leaveItem, times(2)).setItemMeta(leaveItemMeta);
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
		verify(townworldDao).saveLocation("Towns.mytown.TownManagerVillager", loc);
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
	public void constructorLoadingTest() throws TownsystemException, EconomyPlayerException {
		JavaPlugin plugin = mock(JavaPlugin.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		EconomyPlayer deputy = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		Location spawn = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		BankAccount account = mock(BankAccount.class);
		Inventory inv = mock(Inventory.class);
		Inventory plotInv = mock(Inventory.class);
		Villager villager = mock(Villager.class);
		ItemStack joinItem = mock(ItemStack.class);
		ItemStack leaveItem = mock(ItemStack.class);
		ItemMeta joinItemMeta = mock(ItemMeta.class);
		ItemMeta leaveItemMeta = mock(ItemMeta.class);
		assertDoesNotThrow(() -> when(townworldDao.loadLocation("Towns.mytown.TownManagerVillager")).thenReturn(loc));
		when(townworldDao.loadTownBankIban("mytown")).thenReturn("iban");
		assertDoesNotThrow(() -> when(townworldDao.loadTownSpawn("mytown")).thenReturn(spawn));
		assertDoesNotThrow(() -> when(townworldDao.loadMayor("mytown")).thenReturn(mayor));
		assertDoesNotThrow(() -> when(townworldDao.loadDeputies("mytown")).thenReturn(Arrays.asList(deputy)));
		assertDoesNotThrow(() -> when(townworldDao.loadCitizens("mytown")).thenReturn(Arrays.asList(mayor)));
		when(townworldDao.loadTax("mytown")).thenReturn(1.5);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(joinItem.getItemMeta()).thenReturn(joinItemMeta);
		when(leaveItem.getItemMeta()).thenReturn(leaveItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(leaveItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(joinItem);
		when(serverProvider.createInventory(null, 9, "Plot 1/2")).thenReturn(plotInv);
		when(serverProvider.createInventory(villager, 9, "mytown TownManager")).thenReturn(inv);
		assertDoesNotThrow(() -> when(bankManager.getBankAccountByIban("iban"))).thenReturn(account);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(townworldDao).loadPlotOwner("mytown", "2/3");
		when(townworldDao.loadPlotOwner("mytown", "1/2")).thenReturn(mayor);
		when(townworldDao.loadTownPlotCoords("mytown")).thenReturn(Arrays.asList("1/2", "2/3"));
		when(townworldDao.loadSize("Towns.mytown.TownManagerVillager")).thenReturn(9);
		when(townworldDao.loadSize("Towns.mytown.Plots.1/2.SaleVillager")).thenReturn(9);
		when(townworldDao.loadVisible("Towns.mytown.TownManagerVillager")).thenReturn(true);
		when(townworldDao.loadProfession("Towns.mytown.TownManagerVillager")).thenReturn(Profession.FARMER);
		Town town = assertDoesNotThrow(() -> new TownImpl(false, null, "mytown", null, townworldManager, bankManager,
				validationHandler, messageWrapper, townworldDao, townworld, serverProvider));

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

		verify(e).getMessage();

		verify(joinItemMeta).setDisplayName("Join");
		verify(joinItem, times(2)).setItemMeta(joinItemMeta);
		verify(inv).setItem(0, joinItem);

		verify(leaveItemMeta).setDisplayName("Leave");
		verify(leaveItem, times(2)).setItemMeta(leaveItemMeta);
		verify(inv).setItem(1, leaveItem);
		verify(chunk).load();
		verify(villager).setCustomName("mytown TownManager");
		verify(villager).setCustomNameVisible(true);
		verify(villager).setProfession(Profession.FARMER);
		verify(villager).setSilent(true);
		verify(villager).setInvulnerable(true);
		verify(villager).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		verify(villager).addPotionEffect(any(PotionEffect.class));
	}

	@Test
	public void joinTownTestWithMaxTowns() throws TownsystemException {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerDidNotReachedMaxTowns(ecoPlayer);
		assertThrows(TownsystemException.class, () -> values.town.joinTown(ecoPlayer));
		assertEquals(1, values.town.getCitizens().size());
	}

	@Test
	public void joinTownTestWitAlreadyJoined() throws TownsystemException {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler)
				.checkForPlayerIsNotCitizenPersonal(values.town.getCitizens(), ecoPlayer);
		assertThrows(TownsystemException.class, () -> values.town.joinTown(ecoPlayer));
		assertEquals(1, values.town.getCitizens().size());
	}

	@Test
	public void joinTownTest() {
		Values values = createTown();
		reset(townworldDao);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> values.town.joinTown(ecoPlayer));

		assertDoesNotThrow(
				() -> verify(validationHandler).checkForPlayerIsNotCitizenPersonal(anyList(), eq(ecoPlayer)));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerDidNotReachedMaxTowns(ecoPlayer));

		assertEquals(2, values.town.getCitizens().size());
		assertEquals(ecoPlayer, values.town.getCitizens().get(1));
		verify(townworldDao).saveCitizens("mytown", values.town.getCitizens());
		assertDoesNotThrow(() -> verify(ecoPlayer).addJoinedTown("mytown"));
	}

	@Test
	public void isPlayerCitizenTest() {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertTrue(values.town.isPlayerCitizen(values.town.getMayor()));
		assertFalse(values.town.isPlayerCitizen(ecoPlayer));
	}

	@Test
	public void isMayorTest() {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertTrue(values.town.isMayor(values.town.getMayor()));
		assertFalse(values.town.isMayor(ecoPlayer));
	}

	@Test
	public void increaseTownBankAmountTest() {
		Values values = createTown();

		assertDoesNotThrow(() -> values.town.increaseTownBankAmount(1.5));
		assertDoesNotThrow(() -> verify(values.account).increaseAmount(1.5));
	}

	@Test
	public void decreaseTownBankAmountTestWithNotEnoughMoney() throws TownsystemException {
		Values values = createTown();
		doThrow(TownsystemException.class).when(validationHandler).checkForTownHasEnoughMoney(0.0, 1.5);
		assertThrows(TownsystemException.class, () -> values.town.decreaseTownBankAmount(1.5));
	}

	@Test
	public void decreaseTownBankAmountTest() {
		Values values = createTown();

		assertDoesNotThrow(() -> values.town.decreaseTownBankAmount(1.5));
		assertDoesNotThrow(() -> verify(validationHandler).checkForTownHasEnoughMoney(0.0, 1.5));
		assertDoesNotThrow(() -> verify(values.account).decreaseAmount(1.5));
	}

	@Test
	public void setTaxTestWithInvalidValue() throws TownsystemException {
		Values values = createTown();
		doThrow(TownsystemException.class).when(validationHandler).checkForPositiveValue(-1.5);
		assertThrows(TownsystemException.class, () -> values.town.setTax(-1.5));
	}

	@Test
	public void setTaxTest() {
		Values values = createTown();
		assertDoesNotThrow(() -> values.town.setTax(1.5));

		assertEquals("1.5", String.valueOf(values.town.getTax()));
		verify(townworldDao).saveTax("mytown", 1.5);
	}

	@Test
	public void hasEnoughMoneyTest() {
		Values values = createTown();
		when(values.account.getAmount()).thenReturn(1.0);

		assertTrue(values.town.hasEnoughMoney(1.0));
		assertFalse(values.town.hasEnoughMoney(1.5));
	}

	@Test
	public void addDeputyTestWithIsAlreadyDeputy() throws TownsystemException {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerIsNotDeputy(new ArrayList<>(),
				ecoPlayer);
		assertThrows(TownsystemException.class, () -> values.town.addDeputy(ecoPlayer));
	}

	@Test
	public void addDeputyTest() {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> values.town.addDeputy(ecoPlayer));

		assertTrue(values.town.isPlayerCitizen(ecoPlayer));
		assertEquals(1, values.town.getDeputies().size());
		assertEquals(ecoPlayer, values.town.getDeputies().get(0));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsNotDeputy(anyList(), eq(ecoPlayer)));
		verify(townworldDao).saveDeputies("mytown", Arrays.asList(ecoPlayer));
	}

	@Test
	public void removeDeputyTestWithIsNoDeputy() throws TownsystemException {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerIsDeputy(values.town.getDeputies(),
				ecoPlayer);
		assertThrows(TownsystemException.class, () -> values.town.removeDeputy(ecoPlayer));
	}

	@Test
	public void removeDeputyTest() {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> values.town.addDeputy(ecoPlayer));
		reset(townworldDao);
		assertDoesNotThrow(() -> values.town.removeDeputy(ecoPlayer));
		assertEquals(0, values.town.getDeputies().size());
		verify(townworldDao).saveDeputies("mytown", new ArrayList<>());
	}

	@Test
	public void hasDeputyPermissionsTest() {
		Values values = createTown();
		EconomyPlayer ecoPlayer1 = mock(EconomyPlayer.class);
		EconomyPlayer ecoPlayer2 = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> values.town.addDeputy(ecoPlayer1));
		assertTrue(values.town.hasDeputyPermissions(ecoPlayer1));
		assertFalse(values.town.hasDeputyPermissions(ecoPlayer2));
		assertTrue(values.town.hasDeputyPermissions(values.town.getMayor()));
	}

	@Test
	public void expandTownTestWithChunkAlreadyClaimed() throws TownsystemException {
		Values values = createTown();
		Chunk chunk = mock(Chunk.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForChunkNotClaimed(values.town.getTownworld(),
				chunk);
		assertThrows(TownsystemException.class, () -> values.town.expandTown(chunk, null));
	}

	@Test
	public void expandTownTestWithNoPermission() throws TownsystemException {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerHasDeputyPermission(false);
		assertThrows(TownsystemException.class, () -> values.town.expandTown(null, ecoPlayer));
	}

	@Test
	public void expandTownTestWithNotConnectedToTown() throws TownsystemException {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Chunk chunk = mock(Chunk.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		doThrow(TownsystemException.class).when(validationHandler).checkForChunkIsConnectedToTown(false);
		assertThrows(TownsystemException.class, () -> values.town.expandTown(chunk, ecoPlayer));
	}

	@Test
	public void expandTownTest() {
		Values values = createTown();
		Inventory plotInv = mock(Inventory.class);
		Chunk newChunk = mock(Chunk.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(newChunk.getX()).thenReturn(1);
		when(newChunk.getZ()).thenReturn(3);
		when(values.townworld.getExpandPrice()).thenReturn(2.5);
		when(serverProvider.createInventory(null, 9, "Plot 1/3")).thenReturn(plotInv);
		assertDoesNotThrow(() -> values.town.expandTown(newChunk, ecoPlayer));
		assertDoesNotThrow(() -> verify(values.account).decreaseAmount(2.5));

		Plot plot = assertDoesNotThrow(() -> values.town.getPlotByChunk("1/3"));
		assertEquals("1/3", plot.getChunkCoords());
		assertEquals(values.town, plot.getTown());
		assertDoesNotThrow(() -> verify(townworldManager).performTownworldLocationCheckAllPlayers());
	}

	@Test
	public void isClaimedByTownTest() {
		Values values = createTown();
		Chunk chunk = mock(Chunk.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		Chunk chunk2 = mock(Chunk.class);
		when(chunk2.getX()).thenReturn(1);
		when(chunk2.getZ()).thenReturn(3);
		assertTrue(values.town.isClaimedByTown(chunk));
		assertFalse(values.town.isClaimedByTown(chunk2));
	}

	@Test
	public void hasBuildPermissionsTest() {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Plot plot = assertDoesNotThrow(() -> values.town.getPlotByChunk("1/2"));
		assertFalse(values.town.hasBuildPermissions(ecoPlayer, plot));
		assertTrue(values.town.hasBuildPermissions(values.town.getMayor(), plot));
		assertDoesNotThrow(() -> values.town.joinTown(ecoPlayer));
		assertDoesNotThrow(() -> plot.addResident(ecoPlayer));
		assertTrue(values.town.hasBuildPermissions(ecoPlayer, plot));
	}

	@Test
	public void getPlotByChunkTestWithNotClaimed() {
		Values values = createTown();
		assertThrows(TownsystemException.class, () -> values.town.getPlotByChunk("2/3"));
	}

	@Test
	public void getPlotByChunkTest() {
		Values values = createTown();
		Plot plot = assertDoesNotThrow(() -> values.town.getPlotByChunk("1/2"));
		assertEquals("1/2", plot.getChunkCoords());
	}

	@Test
	public void changeTownSpawnTestWithNoPermission() {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> doThrow(TownsystemException.class).when(validationHandler)
				.checkForPlayerHasDeputyPermission(false));
		assertThrows(TownsystemException.class, () -> values.town.changeTownSpawn(null, ecoPlayer));
	}

	@Test
	public void changeTownSpawnTestWithLocationNotInTown() {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		assertDoesNotThrow(() -> doThrow(TownsystemException.class).when(validationHandler)
				.checkForLocationIsInTown(anyMap(), eq(loc)));
		assertThrows(TownsystemException.class, () -> values.town.changeTownSpawn(loc, ecoPlayer));
	}

	@Test
	public void changeTownSpawnTest() {
		Values values = createTown();
		Location loc = mock(Location.class);
		assertDoesNotThrow(() -> values.town.changeTownSpawn(loc, values.town.getMayor()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerHasDeputyPermission(true));
		assertDoesNotThrow(() -> verify(validationHandler).checkForLocationIsInTown(anyMap(), eq(loc)));
		assertEquals(loc, values.town.getTownSpawn());
		verify(townworldDao).saveTownSpawn("mytown", loc);
	}

	@Test
	public void leaveTownTestWithIsMayor() throws TownsystemException {
		Values values = createTown();
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerIsNotMayor(values.town.getMayor(),
				values.town.getMayor());
		assertThrows(TownsystemException.class, () -> values.town.leaveTown(values.town.getMayor()));
	}

	@Test
	public void leaveTownTestWithIsNotCitizen() throws TownsystemException {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler)
				.checkForPlayerIsCitizenPersonalError(values.town.getCitizens(), ecoPlayer);
		assertThrows(TownsystemException.class, () -> values.town.leaveTown(ecoPlayer));
	}

	@Test
	public void leaveTownTest() {
		Values values = createTown();
		reset(townworldDao);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> values.town.joinTown(ecoPlayer));
		assertDoesNotThrow(() -> values.town.addDeputy(ecoPlayer));
		Plot plot = assertDoesNotThrow(() -> values.town.getPlotByChunk("1/2"));
		assertDoesNotThrow(() -> plot.addResident(ecoPlayer));

		assertDoesNotThrow(() -> values.town.leaveTown(ecoPlayer));

		assertFalse(plot.isResident(ecoPlayer));
		assertFalse(values.town.isPlayerCitizen(ecoPlayer));
		assertFalse(values.town.isDeputy(ecoPlayer));
		assertDoesNotThrow(() -> verify(ecoPlayer).removeJoinedTown("mytown"));
		verify(townworldDao, times(2)).saveCitizens("mytown", Arrays.asList(values.town.getMayor()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsNotMayor(values.town.getMayor(), ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForPlayerIsCitizenPersonalError(anyList(), eq(ecoPlayer)));
	}

	@Test
	public void leaveTownTestWithOwningPlot() {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> values.town.joinTown(ecoPlayer));
		assertDoesNotThrow(() -> values.town.addDeputy(ecoPlayer));
		Plot plot = assertDoesNotThrow(() -> values.town.getPlotByChunk("1/2"));
		assertDoesNotThrow(() -> plot.setOwner(ecoPlayer));
		reset(townworldDao);
		assertDoesNotThrow(() -> values.town.leaveTown(ecoPlayer));

		assertFalse(plot.isOwner(ecoPlayer));
		assertFalse(values.town.isPlayerCitizen(ecoPlayer));
		assertFalse(values.town.isDeputy(ecoPlayer));
		assertDoesNotThrow(() -> verify(ecoPlayer).removeJoinedTown("mytown"));
		verify(townworldDao).saveCitizens("mytown", Arrays.asList(values.town.getMayor()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsNotMayor(values.town.getMayor(), ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForPlayerIsCitizenPersonalError(anyList(), eq(ecoPlayer)));
	}

	@Test
	public void moveTownManagerVillagerTestWithLocationNotInTown() throws TownsystemException {
		Values values = createTown();
		Location loc = mock(Location.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForLocationIsInTown(anyMap(), eq(loc));
		assertThrows(TownsystemException.class, () -> values.town.changeLocation(loc, null));
	}

	@Test
	public void moveTownManagerVillagerTestWithNotMayor() throws TownsystemException {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerIsMayor(values.town.getMayor(),
				ecoPlayer);
		assertThrows(TownsystemException.class, () -> values.town.changeLocation(null, ecoPlayer));
	}

	@Test
	public void moveTownManagerVillagerTest() {
		Values values = createTown();
		Location newLoc = mock(Location.class);
		assertDoesNotThrow(() -> values.town.changeLocation(newLoc, values.town.getMayor()));

		verify(values.villager).teleport(newLoc);
		verify(townworldDao).saveLocation("Towns.mytown.TownManagerVillager", newLoc);
		assertDoesNotThrow(() -> verify(validationHandler).checkForLocationIsInTown(anyMap(), eq(newLoc)));
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForPlayerIsMayor(values.town.getMayor(), values.town.getMayor()));
	}

	@Test
	public void deletePlotTestNotClaimed() throws TownsystemException {
		Values values = createTown();
		Plot plot = mock(Plot.class);
		when(plot.getChunkCoords()).thenReturn("1/3");
		doThrow(TownsystemException.class).when(validationHandler).checkForChunkIsClaimedByThisTown(anyMap(),
				eq("1/3"));
		assertThrows(TownsystemException.class, () -> values.town.deletePlot(plot));
	}

	@Test
	public void deletePlotTest() {
		Values values = createTown();
		Chunk chunk = mock(Chunk.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		assertDoesNotThrow(() -> values.town.deletePlot(values.town.getPlotByChunk("1/2")));
		verify(townworldDao).saveRemovePlot("mytown", "1/2");
		assertFalse(values.town.isClaimedByTown(chunk));
		assertDoesNotThrow(() -> verify(validationHandler).checkForChunkIsClaimedByThisTown(anyMap(), eq("1/2")));
	}

	@Test
	public void despawnAllVillagersTest() {
		Values values = createTown();

		ItemStack buyItem = mock(ItemStack.class);
		ItemStack cancelItem = mock(ItemStack.class);
		ItemMeta cancelItemMeta = mock(ItemMeta.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(cancelItem.getItemMeta()).thenReturn(cancelItemMeta);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(cancelItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(buyItem);
		when(serverProvider.createInventory(null, 9, "Plot 1/3")).thenReturn(values.inv);
		when(serverProvider.createInventory(values.villager, 9, "Plot 1/3")).thenReturn(values.inv);
		when(serverProvider.getJavaPluginInstance()).thenReturn(values.plugin);
		when(values.world.spawnEntity(values.loc, EntityType.VILLAGER)).thenReturn(values.villager);
		when(values.loc.getWorld()).thenReturn(values.world);
		when(values.loc.getChunk()).thenReturn(values.chunk);

		Chunk chunk2 = mock(Chunk.class);
		when(chunk2.getX()).thenReturn(1);
		when(chunk2.getZ()).thenReturn(3);
		when(values.inv.getItem(0)).thenReturn(buyItem);
		when(buyItemMeta.getLore()).thenReturn(Arrays.asList("fdsf"));
		assertDoesNotThrow(() -> values.town.expandTown(chunk2, values.town.getMayor()));
		assertDoesNotThrow(() -> values.town.getPlotByChunk("1/3").setForSale(1.0, values.loc, values.town.getMayor()));

		values.town.despawnAllVillagers();

		verify(values.villager, times(2)).remove();
	}

	@Test
	public void buyPlotTestWithNotForSale() throws TownsystemException {
		Values values = createTown();
		doThrow(TownsystemException.class).when(validationHandler).checkForPlotIsForSale(false);
		assertThrows(TownsystemException.class, () -> values.town.buyPlot(null, 1, 2));
	}

	@Test
	public void buyPlotTestWithPlotOwner() throws TownsystemException {
		Values values = createTown();
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerIsNotPlotOwner(values.town.getMayor(),
				values.town.getPlotByChunk("1/2"));
		assertThrows(TownsystemException.class, () -> values.town.buyPlot(values.town.getMayor(), 1, 2));
	}

	@Test
	public void buyPlotTest() {
		Values values = createTown();

		when(serverProvider.createInventory(values.villager, 9, "Plot 1/2")).thenReturn(values.inv);
		when(serverProvider.getJavaPluginInstance()).thenReturn(values.plugin);
		when(values.world.spawnEntity(values.loc, EntityType.VILLAGER)).thenReturn(values.villager);
		when(values.loc.getWorld()).thenReturn(values.world);
		when(values.loc.getChunk()).thenReturn(values.chunk);
		ItemStack buyItem = mock(ItemStack.class);
		ItemMeta buyItemMeta = mock(ItemMeta.class);
		when(buyItem.getItemMeta()).thenReturn(buyItemMeta);
		when(serverProvider.getJavaPluginInstance()).thenReturn(values.plugin);
		when(values.world.spawnEntity(values.loc, EntityType.VILLAGER)).thenReturn(values.villager);
		when(values.loc.getWorld()).thenReturn(values.world);
		when(values.loc.getChunk()).thenReturn(values.chunk);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Plot plot = assertDoesNotThrow(() -> values.town.getPlotByChunk("1/2"));
		when(values.plotInv.getItem(0)).thenReturn(buyItem);
		when(buyItemMeta.getLore()).thenReturn(Arrays.asList("fdsf"));
		assertDoesNotThrow(() -> plot.addResident(values.town.getMayor()));
		assertDoesNotThrow(() -> plot.setForSale(1.5, values.loc, values.town.getMayor()));

		assertDoesNotThrow(() -> values.town.buyPlot(ecoPlayer, 1, 2));

		assertTrue(plot.isOwner(ecoPlayer));
		assertEquals(0, plot.getResidents().size());
		assertFalse(plot.isForSale());
	}

	@Test
	public void openTownManagerVillagerInvTest() {
		Values values = createTown();

		Player player = mock(Player.class);
		assertDoesNotThrow(() -> values.town.openInventory(player));
		verify(player).openInventory(values.inv);
	}

	@Test
	public void renameTownTestWithAlreadyExists() throws TownsystemException {
		Values values = createTown();
		when(townworldManager.getTownNameList()).thenReturn(Arrays.asList("mytown1"));
		doThrow(TownsystemException.class).when(validationHandler).checkForValueNotInList(Arrays.asList("mytown1"),
				"mytown1");
		assertThrows(TownsystemException.class, () -> values.town.renameTown("mytown1", values.town.getMayor()));
	}

	@Test
	public void renameTownTestWithNotMayor() throws TownsystemException {
		Values values = createTown();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerIsMayor(values.town.getMayor(),
				ecoPlayer);
		assertThrows(TownsystemException.class, () -> values.town.renameTown("mytown1", ecoPlayer));
	}

	@Test
	public void renameTownTest() {
		Values values = createTown();

		assertDoesNotThrow(() -> values.town.renameTown("newname", values.mayor));

		assertEquals("newname", values.town.getTownName());
		assertDoesNotThrow(() -> verify(values.mayor).removeJoinedTown("mytown"));
		assertDoesNotThrow(() -> verify(values.mayor).addJoinedTown("newname"));
		verify(townworldManager).setTownNameList(Arrays.asList("newname"));
		verify(values.villager).setCustomName("newname TownManager");
		verify(townworldDao).saveRenameTown("mytown", "newname");
	}
}
