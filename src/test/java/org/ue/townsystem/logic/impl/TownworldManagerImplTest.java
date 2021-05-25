package org.ue.townsystem.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.UltimateEconomyProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemValidator;
import org.ue.townsystem.logic.api.Townworld;

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
	TownsystemValidator townsystemValidationHandler;
	@Mock
	ConfigDao configDao;

	@Test
	public void createTownWorldTestWithWorldDoesNotExist() throws TownsystemException {
		doThrow(TownsystemException.class).when(townsystemValidationHandler).checkForWorldExists("world");
		assertThrows(TownsystemException.class, () -> townworldManager.createTownWorld("world"));
		verify(configDao, never()).saveTownworldNamesList(anyList());
	}

	@Test
	public void createTownWorldTestWithTownworldExists() throws TownsystemException {
		doThrow(TownsystemException.class).when(townsystemValidationHandler).checkForTownworldDoesNotExist(anyMap(),
				eq("world"));
		assertThrows(TownsystemException.class, () -> townworldManager.createTownWorld("world"));
		verify(configDao, never()).saveTownworldNamesList(anyList());
	}

	@Test
	public void createTownworldTest() {
		Townworld world = mock(Townworld.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTownWorld()).thenReturn(world);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
		verify(configDao).saveTownworldNamesList(Arrays.asList("world"));
		assertEquals(Arrays.asList("world"), townworldManager.getTownWorldNameList());
		verify(world).setupNew("world");
		assertDoesNotThrow(() -> assertEquals(world, townworldManager.getTownWorldByName("world")));
	}

	@Test
	public void deleteTownWorldTestWithWorldDoesNotExist() throws TownsystemException {
		doThrow(TownsystemException.class).when(townsystemValidationHandler).checkForWorldExists("world");
		assertThrows(TownsystemException.class, () -> townworldManager.deleteTownWorld("world"));
		verify(configDao, never()).saveTownworldNamesList(anyList());
	}

	@Test
	public void deleteTownWorldTestWithTownworldDoesNotExists() throws TownsystemException {
		doThrow(TownsystemException.class).when(townsystemValidationHandler).checkForTownworldExists(anyMap(),
				eq("world"));
		assertThrows(TownsystemException.class, () -> townworldManager.deleteTownWorld("world"));
		verify(configDao, never()).saveTownworldNamesList(anyList());
	}

	@Test
	public void deleteTownWorldTest() {
		Townworld world = mock(Townworld.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTownWorld()).thenReturn(world);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
		assertDoesNotThrow(() -> townworldManager.deleteTownWorld("world"));
		verify(world).delete();
		verify(configDao).saveTownworldNamesList(new ArrayList<>());
		assertEquals(0, townworldManager.getTownWorldList().size());
	}

	@Test
	public void loadAllTownWorldsTest() {
		Townworld world = mock(Townworld.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTownWorld()).thenReturn(world);
		when(configDao.loadTownworldNames()).thenReturn(Arrays.asList("myworld"));
		townworldManager.loadAllTownWorlds();
		verify(world).setupExisting("myworld");
		assertEquals(1, townworldManager.getTownWorldList().size());
		assertDoesNotThrow(() -> assertNotNull(townworldManager.getTownWorldByName("myworld")));
	}

	@Test
	public void isTownWorldTest() {
		Townworld world = mock(Townworld.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTownWorld()).thenReturn(world);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));

		assertTrue(townworldManager.isTownWorld("world"));
		assertFalse(townworldManager.isTownWorld("other"));
	}

	@Test
	public void getTownWorldByNameTest() {
		Townworld world1 = mock(Townworld.class);
		Townworld world2 = mock(Townworld.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTownWorld()).thenReturn(world1, world2);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
		assertDoesNotThrow(() -> townworldManager.createTownWorld("other"));

		Townworld townworld = assertDoesNotThrow(() -> townworldManager.getTownWorldByName("world"));
		assertEquals(world1, townworld);
	}

	@Test
	public void getTownWorldByNameTestWithNoTownworld() {
		TownsystemException e = assertThrows(TownsystemException.class,
				() -> townworldManager.getTownWorldByName("world"));
		assertEquals(ExceptionMessageEnum.TOWNWORLD_DOES_NOT_EXIST, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void getTownByNameTestWithNoTown() {
		TownsystemException e = assertThrows(TownsystemException.class, () -> townworldManager.getTownByName("town"));
		assertEquals(ExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		assertEquals(1, e.getParams().length);
		assertEquals("town", e.getParams()[0]);
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
		when(loc.getWorld()).thenReturn(world);
		when(player.getLocation()).thenReturn(loc);
		when(ecoPlayer.getBossBar()).thenReturn(bossbar);
		when(ecoPlayer.isOnline()).thenReturn(true);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer)));

		assertDoesNotThrow(() -> townworldManager.performTownworldLocationCheckAllPlayers());

		verify(bossbar).setVisible(false);
	}

	@Test
	public void performTownWorldLocationCheckTestWithNoTownworld() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BossBar bossbar = mock(BossBar.class);
		World world = mock(World.class);
		Location loc = mock(Location.class);
		when(loc.getWorld()).thenReturn(world);
		when(ecoPlayer.getBossBar()).thenReturn(bossbar);
		when(ecoPlayer.isOnline()).thenReturn(true);
		Player player = mock(Player.class);
		when(player.getLocation()).thenReturn(loc);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(ecoPlayer.isOnline()).thenReturn(true);
		when(world.getName()).thenReturn("world");
		townworldManager.performTownWorldLocationCheck(ecoPlayer, null);

		verify(bossbar).setVisible(false);
	}

	@Test
	public void performTownWorldLocationCheckTestWithWilderness() throws TownsystemException {
		Townworld townworld = mock(Townworld.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTownWorld()).thenReturn(townworld);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BossBar bossbar = mock(BossBar.class);
		when(ecoPlayer.getBossBar()).thenReturn(bossbar);

		Player player = mock(Player.class);
		World world = mock(World.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		when(loc.getWorld()).thenReturn(world);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(ecoPlayer.isOnline()).thenReturn(true);
		when(world.getName()).thenReturn("world");
		doThrow(TownsystemException.class).when(townworld).getTownByChunk(chunk);
		townworldManager.performTownWorldLocationCheck(ecoPlayer, loc);

		verify(bossbar).setTitle("Wilderness");
		verify(bossbar).setColor(BarColor.GREEN);
		verify(bossbar).setVisible(true);
	}

	@Test
	public void performTownWorldLocationCheckTestWithInTown() {
		Townworld townworld = mock(Townworld.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTownWorld()).thenReturn(townworld);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BossBar bossbar = mock(BossBar.class);
		when(ecoPlayer.getBossBar()).thenReturn(bossbar);

		World world = mock(World.class);
		Player player = mock(Player.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		when(player.getLocation()).thenReturn(loc);
		when(loc.getWorld()).thenReturn(world);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(ecoPlayer.isOnline()).thenReturn(true);
		when(world.getName()).thenReturn("world");
		
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		when(town.getTownName()).thenReturn("mytown");
		townworldManager.performTownWorldLocationCheck(ecoPlayer, null);

		verify(bossbar).setTitle("mytown");
		verify(bossbar).setColor(BarColor.RED);
		verify(bossbar).setVisible(true);
	}

	@Test
	public void getTownByNameTest() {
		Townworld townworld = mock(Townworld.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTownWorld()).thenReturn(townworld);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));
		Town town = mock(Town.class);
		Town other = mock(Town.class);
		when(townworld.getTownList()).thenReturn(Arrays.asList(other, town));
		when(town.getTownName()).thenReturn("mytown");
		when(other.getTownName()).thenReturn("otherName");

		Town result = assertDoesNotThrow(() -> townworldManager.getTownByName("mytown"));
		assertEquals(town, result);
	}

	@Test
	public void despawnAllVillagersTest() {
		Townworld townworld = mock(Townworld.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTownWorld()).thenReturn(townworld);
		assertDoesNotThrow(() -> townworldManager.createTownWorld("world"));

		townworldManager.despawnAllVillagers();

		verify(townworld).despawnAllTownVillagers();
	}
}
