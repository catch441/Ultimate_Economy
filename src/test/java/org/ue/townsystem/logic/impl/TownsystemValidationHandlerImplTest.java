package org.ue.townsystem.logic.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.economyplayer.logic.EconomyPlayerExceptionMessageEnum;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;
import org.ue.townsystem.logic.TownExceptionMessageEnum;
import org.ue.townsystem.logic.TownSystemException;

import dagger.Lazy;

@ExtendWith(MockitoExtension.class)
public class TownsystemValidationHandlerImplTest {

	@InjectMocks
	TownsystemValidationHandlerImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	Lazy<TownworldManager> townworldManagerLazy;
	@Mock
	TownworldManager townworldManager;
	@Mock
	ServerProvider serverProvider;

	@Test
	public void checkForLocationInsidePlotTestValid() {
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		assertDoesNotThrow(() -> validationHandler.checkForLocationInsidePlot("1/4", loc));
	}

	@Test
	public void checkForLocationInsidePlotTestFail() {
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		try {
			validationHandler.checkForLocationInsidePlot("1/2", loc);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.OUTSIDE_OF_THE_PLOT, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerIsNotResidentOfPlotTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsNotResidentOfPlot(new ArrayList<>(), ecoPlayer));
	}

	@Test
	public void checkForPlayerIsNotResidentOfPlotTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		List<EconomyPlayer> list = Arrays.asList(ecoPlayer);
		try {
			validationHandler.checkForPlayerIsNotResidentOfPlot(list, ecoPlayer);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.PLAYER_IS_ALREADY_RESIDENT, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerIsResidentOfPlotTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		List<EconomyPlayer> list = Arrays.asList(ecoPlayer);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsResidentOfPlot(list, ecoPlayer));
	}

	@Test
	public void checkForPlayerIsResidentOfPlotTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		try {
			validationHandler.checkForPlayerIsResidentOfPlot(new ArrayList<>(), ecoPlayer);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.PLAYER_IS_NO_RESIDENT, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForIsPlotOwnerTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> validationHandler.checkForIsPlotOwner(ecoPlayer, ecoPlayer));
	}

	@Test
	public void checkForIsPlotOwnerTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		EconomyPlayer owner = mock(EconomyPlayer.class);
		try {
			validationHandler.checkForIsPlotOwner(owner, ecoPlayer);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlotIsNotForSaleTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForPlotIsNotForSale(false));
	}

	@Test
	public void checkForPlotIsNotForSaleTestFail() {
		try {
			validationHandler.checkForPlotIsNotForSale(true);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.PLOT_IS_ALREADY_FOR_SALE, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerIsDeputyTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		List<EconomyPlayer> list = Arrays.asList(ecoPlayer);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsDeputy(list, ecoPlayer));
	}

	@Test
	public void checkForPlayerIsDeputyTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		try {
			validationHandler.checkForPlayerIsDeputy(new ArrayList<>(), ecoPlayer);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.PLAYER_IS_NO_DEPUTY, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerIsCitizenTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		List<EconomyPlayer> list = Arrays.asList(ecoPlayer);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsCitizen(list, ecoPlayer));
	}

	@Test
	public void checkForPlayerIsCitizenTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		try {
			validationHandler.checkForPlayerIsCitizen(new ArrayList<>(), ecoPlayer);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.PLAYER_IS_NOT_CITIZEN, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForTownHasEnoughMoneyTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForTownHasEnoughMoney(2.5, 2.0));
	}

	@Test
	public void checkForTownHasEnoughMoneyTestValid2() {
		assertDoesNotThrow(() -> validationHandler.checkForTownHasEnoughMoney(2.5, 2.5));
	}

	@Test
	public void checkForTownHasEnoughMoneyTestFail() {
		try {
			validationHandler.checkForTownHasEnoughMoney(2.0, 3.5);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.TOWN_HAS_NOT_ENOUGH_MONEY, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerHasDeputyPermissionTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForPlayerHasDeputyPermission(true));
	}

	@Test
	public void checkForPlayerHasDeputyPermissionTestFail() {
		try {
			validationHandler.checkForPlayerHasDeputyPermission(false);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlotIsForSaleTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForPlotIsForSale(true));
	}

	@Test
	public void checkForPlotIsForSaleTestFail() {
		try {
			validationHandler.checkForPlotIsForSale(false);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.PLOT_IS_NOT_FOR_SALE, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerIsMayorTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsMayor(ecoPlayer, ecoPlayer));
	}

	@Test
	public void checkForPlayerIsMayorTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		try {
			validationHandler.checkForPlayerIsMayor(mayor, ecoPlayer);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerIsNotMayorTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsNotMayor(mayor, ecoPlayer));
	}

	@Test
	public void checkForPlayerIsNotMayorTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		try {
			validationHandler.checkForPlayerIsNotMayor(ecoPlayer, ecoPlayer);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_ARE_THE_OWNER, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerIsNotDeputyTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsNotDeputy(new ArrayList<>(), ecoPlayer));
	}

	@Test
	public void checkForPlayerIsNotDeputyTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		try {
			validationHandler.checkForPlayerIsNotDeputy(Arrays.asList(ecoPlayer), ecoPlayer);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.PLAYER_IS_ALREADY_DEPUTY, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerIsNotCitizenPersonalTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsNotCitizenPersonal(new ArrayList<>(), ecoPlayer));
	}

	@Test
	public void checkForPlayerIsNotCitizenPersonalTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		try {
			validationHandler.checkForPlayerIsNotCitizenPersonal(Arrays.asList(ecoPlayer), ecoPlayer);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_ARE_ALREADY_CITIZEN, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForLocationIsInTownTestValid() {
		Map<String, Plot> chunkList = new HashMap<>();
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		chunkList.put("1/2", null);
		assertDoesNotThrow(() -> validationHandler.checkForLocationIsInTown(chunkList, loc));
	}

	@Test
	public void checkForLocationIsInTownTestFail() {
		Map<String, Plot> chunkList = new HashMap<>();
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		try {
			validationHandler.checkForLocationIsInTown(chunkList, loc);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.LOCATION_NOT_IN_TOWN, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForChunkNotClaimedTestValid() {
		Townworld townworld = mock(Townworld.class);
		Chunk chunk = mock(Chunk.class);
		when(townworld.isChunkFree(chunk)).thenReturn(true);
		assertDoesNotThrow(() -> validationHandler.checkForChunkNotClaimed(townworld, chunk));
	}

	@Test
	public void checkForChunkNotClaimedTestFail() {
		Townworld townworld = mock(Townworld.class);
		Chunk chunk = mock(Chunk.class);
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		try {
			validationHandler.checkForChunkNotClaimed(townworld, chunk);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerIsCitizenPersonalErrorTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		List<EconomyPlayer> list = Arrays.asList(ecoPlayer);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsCitizenPersonalError(list, ecoPlayer));
	}

	@Test
	public void checkForPlayerIsCitizenPersonalErrorTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		try {
			validationHandler.checkForPlayerIsCitizenPersonalError(new ArrayList<>(), ecoPlayer);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_ARE_NO_CITIZEN, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerIsNotPlotOwnerTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Plot plot = mock(Plot.class);
		when(plot.isOwner(ecoPlayer)).thenReturn(false);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerIsNotPlotOwner(ecoPlayer, plot));
	}

	@Test
	public void checkForPlayerIsNotPlotOwnerTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Plot plot = mock(Plot.class);
		when(plot.isOwner(ecoPlayer)).thenReturn(true);
		try {
			validationHandler.checkForPlayerIsNotPlotOwner(ecoPlayer, plot);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_ARE_THE_OWNER, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForPlayerDidNotReachedMaxTownsTestValid() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.reachedMaxJoinedTowns()).thenReturn(false);
		assertDoesNotThrow(() -> validationHandler.checkForPlayerDidNotReachedMaxTowns(ecoPlayer));
	}

	@Test
	public void checkForPlayerDidNotReachedMaxTownsTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.reachedMaxJoinedTowns()).thenReturn(true);
		try {
			validationHandler.checkForPlayerDidNotReachedMaxTowns(ecoPlayer);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(EconomyPlayerExceptionMessageEnum.MAX_REACHED, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForChunkIsConnectedToTownTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForChunkIsConnectedToTown(true));
	}

	@Test
	public void checkForChunkIsConnectedToTownTestFail() {
		try {
			validationHandler.checkForChunkIsConnectedToTown(false);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.CHUNK_IS_NOT_CONNECTED_WITH_TOWN, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForChunkIsNotClaimedByThisTownTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForChunkIsNotClaimedByThisTown(new HashMap<>(), "1/2"));
	}

	@Test
	public void checkForChunkIsNotClaimedByThisTownTestFail() {
		Map<String, Plot> map = new HashMap<>();
		map.put("1/2", null);
		try {
			validationHandler.checkForChunkIsNotClaimedByThisTown(map, "1/2");
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForChunkIsClaimedByThisTownTestValid() {
		Map<String, Plot> map = new HashMap<>();
		map.put("1/2", null);
		assertDoesNotThrow(() -> validationHandler.checkForChunkIsClaimedByThisTown(map, "1/2"));
	}

	@Test
	public void checkForChunkIsClaimedByThisTownTestFail() {
		try {
			validationHandler.checkForChunkIsClaimedByThisTown(new HashMap<>(), "1/2");
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.CHUNK_NOT_CLAIMED_BY_TOWN, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForTownworldDoesNotExistTestValid() {
		assertDoesNotThrow(() -> validationHandler.checkForTownworldDoesNotExist(new HashMap<>(), "world"));
	}

	@Test
	public void checkForTownworldDoesNotExistTestFail() {
		Map<String, Townworld> map = new HashMap<>();
		map.put("world", null);
		try {
			validationHandler.checkForTownworldDoesNotExist(map, "world");
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.TOWNWORLD_ALREADY_EXIST, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForTownworldExistsTestValid() {
		Map<String, Townworld> map = new HashMap<>();
		map.put("world", null);
		assertDoesNotThrow(() -> validationHandler.checkForTownworldExists(map, "world"));
	}

	@Test
	public void checkForTownworldExistsTestFail() {
		try {
			validationHandler.checkForTownworldExists(new HashMap<>(), "world");
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.TOWNWORLD_DOES_NOT_EXIST, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForChunkIsFreeTestValid() {
		Townworld townworld = mock(Townworld.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		when(townworld.isChunkFree(chunk)).thenReturn(true);
		assertDoesNotThrow(() -> validationHandler.checkForChunkIsFree(townworld, loc));
	}

	@Test
	public void checkForChunkIsFreeTestFailed() {
		Townworld townworld = mock(Townworld.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getChunk()).thenReturn(chunk);
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		try {
			validationHandler.checkForChunkIsFree(townworld, loc);
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.CHUNK_ALREADY_CLAIMED, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void checkForWorldExistsTestValid() {
		World world = mock(World.class);
		when(serverProvider.getWorld("world")).thenReturn(world);
		assertDoesNotThrow(() -> validationHandler.checkForWorldExists("world"));
	}

	@Test
	public void checkForWorldExistsTestFail() {
		when(serverProvider.getWorld("world")).thenReturn(null);
		try {
			validationHandler.checkForWorldExists("world");
			fail();
		} catch (TownSystemException e) {
			assertEquals(TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("world", e.getParams()[0]);
		}
	}

	@Test
	public void checkForTownworldPlotPermissionTestValidNoTownworld() {
		Location loc = mock(Location.class);
		World world = mock(World.class);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(townworldManagerLazy.get()).thenReturn(townworldManager);
		when(townworldManager.isTownWorld("world")).thenReturn(false);
		assertDoesNotThrow(() -> validationHandler.checkForTownworldPlotPermission(loc, null));
	}

	@Test
	public void checkForTownworldPlotPermissionTestFailFreeChunk() {
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(loc.getChunk()).thenReturn(chunk);
		when(townworldManagerLazy.get()).thenReturn(townworldManager);
		when(townworldManager.isTownWorld("world")).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		when(townworld.isChunkFree(chunk)).thenReturn(true);
		try {
			validationHandler.checkForTownworldPlotPermission(loc, null);
			fail();
		} catch (EconomyPlayerException | TownSystemException e) {
			assertTrue(e instanceof EconomyPlayerException);
			EconomyPlayerException ex = (EconomyPlayerException) e;
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION, ex.getKey());
			assertEquals(0, ex.getParams().length);
		}
	}

	@Test
	public void checkForTownworldPlotPermissionTestValidWithPermission() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(loc.getChunk()).thenReturn(chunk);
		when(townworldManagerLazy.get()).thenReturn(townworldManager);
		when(townworldManager.isTownWorld("world")).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		when(town.hasBuildPermissions(ecoPlayer, plot)).thenReturn(true);
		assertDoesNotThrow(() -> validationHandler.checkForTownworldPlotPermission(loc, ecoPlayer));
	}

	@Test
	public void checkForTownworldPlotPermissionTestFailWithoutPermission() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Chunk chunk = mock(Chunk.class);
		Town town = mock(Town.class);
		Plot plot = mock(Plot.class);
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(loc.getChunk()).thenReturn(chunk);
		when(townworldManagerLazy.get()).thenReturn(townworldManager);
		when(townworldManager.isTownWorld("world")).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		when(townworld.isChunkFree(chunk)).thenReturn(false);
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		when(town.hasBuildPermissions(ecoPlayer, plot)).thenReturn(false);
		try {
			validationHandler.checkForTownworldPlotPermission(loc, ecoPlayer);
			fail();
		} catch (EconomyPlayerException | TownSystemException e) {
			assertTrue(e instanceof EconomyPlayerException);
			EconomyPlayerException ex = (EconomyPlayerException) e;
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION, ex.getKey());
			assertEquals(0, ex.getParams().length);
		}
	}

	@Test
	public void checkForTownworldPlotPermissionTestFail() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		Chunk chunk = mock(Chunk.class);
		when(loc.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(loc.getChunk()).thenReturn(chunk);
		when(townworldManagerLazy.get()).thenReturn(townworldManager);
		when(townworldManager.isTownWorld("world")).thenReturn(true);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		when(townworld.isChunkFree(chunk)).thenReturn(true);
		try {
			validationHandler.checkForTownworldPlotPermission(loc, ecoPlayer);
			fail();
		} catch (EconomyPlayerException | TownSystemException e) {
			assertTrue(e instanceof EconomyPlayerException);
			EconomyPlayerException ex = (EconomyPlayerException) e;
			assertEquals(EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION, ex.getKey());
			assertEquals(0, ex.getParams().length);
		}
	}
}
