package org.ue.townsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankException;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.UltimateEconomyProvider;
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

	@Test
	public void setupNewTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		townworld.setupNew("world");

		assertEquals("world", townworld.getWorldName());
		assertEquals("0.0", String.valueOf(townworld.getExpandPrice()));
		assertEquals("0.0", String.valueOf(townworld.getFoundationPrice()));
		verify(townworldDao).saveExpandPrice(0.0);
		verify(townworldDao).saveFoundationPrice(0.0);
		verify(townworldDao).saveWorldName("world");
	}

	@Test
	public void constructorLoadingTest() throws EconomyPlayerException, TownsystemException, BankException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);

		Town town = mock(Town.class);
		Town corrupted = mock(Town.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTown(townworld, townworldDao)).thenReturn(town, corrupted);

		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(corrupted).setupExisting(townworld, "corrupted");

		when(townworldDao.loadFoundationPrice()).thenReturn(1.5);
		when(townworldDao.loadExpandPrice()).thenReturn(2.5);
		when(townworldDao.loadTownworldTownNames()).thenReturn(Arrays.asList("mytown", "corrupted"));

		townworld.setupExisting("world");

		verify(e).getMessage();

		assertEquals("world", townworld.getWorldName());
		assertEquals("2.5", String.valueOf(townworld.getExpandPrice()));
		assertEquals("1.5", String.valueOf(townworld.getFoundationPrice()));

		assertEquals(1, townworld.getTownList().size());
		assertDoesNotThrow(() -> assertNotNull(townworld.getTownByName("mytown")));
	}

	@Test
	public void setFoundationPriceTestWithNegative() throws TownsystemException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		doThrow(TownsystemException.class).when(validationHandler).checkForPositiveValue(-1.5);
		assertThrows(TownsystemException.class, () -> townworld.setFoundationPrice(-1.5));
		assertEquals("0.0", String.valueOf(townworld.getFoundationPrice()));
	}

	@Test
	public void setFoundationPriceTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		assertDoesNotThrow(() -> townworld.setFoundationPrice(1.5));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1.5));
		verify(townworldDao).saveFoundationPrice(1.5);
		assertEquals("1.5", String.valueOf(townworld.getFoundationPrice()));
	}

	@Test
	public void setExpandPriceTestWithNegative() throws TownsystemException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		doThrow(TownsystemException.class).when(validationHandler).checkForPositiveValue(-1.5);
		assertThrows(TownsystemException.class, () -> townworld.setExpandPrice(-1.5));
		assertEquals("0.0", String.valueOf(townworld.getExpandPrice()));
	}

	@Test
	public void setExpandPriceTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		assertDoesNotThrow(() -> townworld.setExpandPrice(1.5));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(1.5));
		verify(townworldDao).saveExpandPrice(1.5);
		assertEquals("1.5", String.valueOf(townworld.getExpandPrice()));
	}

	@Test
	public void foundTownTestWithAlreadyExists() throws TownsystemException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		when(townworldManager.getTownNameList()).thenReturn(Arrays.asList("mytown"));
		doThrow(TownsystemException.class).when(validationHandler).checkForValueNotInList(Arrays.asList("mytown"),
				"mytown");
		assertThrows(TownsystemException.class, () -> townworld.foundTown("mytown", null, null));
	}

	@Test
	public void foundTownTestWithChunkIsOccupied() throws TownsystemException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		Location loc = mock(Location.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForChunkIsFree(townworld, loc);
		assertThrows(TownsystemException.class, () -> townworld.foundTown("mytown", loc, null));
	}

	@Test
	public void foundTownTestReachedMaxJoinedTowns() throws TownsystemException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.reachedMaxJoinedTowns()).thenReturn(true);
		doThrow(TownsystemException.class).when(validationHandler).checkForNotReachedMax(true);
		assertThrows(TownsystemException.class, () -> townworld.foundTown("mytown", null, ecoPlayer));
	}

	@Test
	public void foundTownTestWithNotEnoughMoney() throws TownsystemException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		BankAccount account = mock(BankAccount.class);
		when(ecoPlayer.getBankAccount()).thenReturn(account);
		doThrow(TownsystemException.class).when(validationHandler).checkForEnoughMoney(account, 0.0, true);
		assertThrows(TownsystemException.class, () -> townworld.foundTown("mytown", null, ecoPlayer));
	}

	@Test
	public void foundTownTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);

		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTown(townworld, townworldDao)).thenReturn(town);
		when(town.getTownName()).thenReturn("mytown");

		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		assertDoesNotThrow(() -> verify(town).setupNew(townworld, ecoPlayer, "mytown", loc));
		assertEquals(1, townworld.getTownList().size());
		assertDoesNotThrow(() -> assertNotNull(townworld.getTownByName("mytown")));
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(0.0, true));
		verify(townworldManager).setTownNameList(Arrays.asList("mytown"));
		assertDoesNotThrow(() -> verify(townworldManager).performTownworldLocationCheckAllPlayers());
	}

	@Test
	public void getTownNameListTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTown(townworld, townworldDao)).thenReturn(town);
		when(town.getTownName()).thenReturn("mytown");

		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		assertEquals(Arrays.asList("mytown"), townworld.getTownNameList());
	}

	@Test
	public void getTownByChunkTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		Chunk chunk = mock(Chunk.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTown(townworld, townworldDao)).thenReturn(town);
		when(town.getTownName()).thenReturn("mytown");
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		when(town.isClaimedByTown(chunk)).thenReturn(true);

		Town result = assertDoesNotThrow(() -> townworld.getTownByChunk(chunk));
		assertEquals(town, result);
	}

	@Test
	public void getTownByChunkTestWithNoTown() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);

		Chunk chunk = mock(Chunk.class);
		TownsystemException e = assertThrows(TownsystemException.class, () -> townworld.getTownByChunk(chunk));
		assertEquals(ExceptionMessageEnum.CHUNK_NOT_CLAIMED, e.getKey());
		assertEquals(0, e.getParams().length);
	}

	@Test
	public void getTownByNameTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTown(townworld, townworldDao)).thenReturn(town);
		when(town.getTownName()).thenReturn("mytown");

		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		Town result = assertDoesNotThrow(() -> townworld.getTownByName("mytown"));
		assertEquals(town, result);
	}

	@Test
	public void getTownByNameTestWithNoTown() throws TownsystemException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		doThrow(TownsystemException.class).when(validationHandler).checkForValueExists(null, "mytown");
		assertThrows(TownsystemException.class, () -> townworld.getTownByName("mytown"));
	}

	@Test
	public void isChunkFreeTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTown(townworld, townworldDao)).thenReturn(town);
		when(town.getTownName()).thenReturn("mytown");

		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		Chunk free = mock(Chunk.class);
		Chunk claimed = mock(Chunk.class);
		when(town.isClaimedByTown(free)).thenReturn(false);
		when(town.isClaimedByTown(claimed)).thenReturn(true);
		assertFalse(townworld.isChunkFree(claimed));
		assertTrue(townworld.isChunkFree(free));
	}

	@Test
	public void despawnAllTownVillagersTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);

		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTown(townworld, townworldDao)).thenReturn(town);
		when(town.getTownName()).thenReturn("mytown");

		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		townworld.despawnAllTownVillagers();
		verify(town).despawnAllVillagers();
		;
	}

	@Test
	public void dissolveTownTestWithNotMayor() throws TownsystemException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		Town town = mock(Town.class);
		EconomyPlayer notMayor = mock(EconomyPlayer.class);
		EconomyPlayer mayor = mock(EconomyPlayer.class);
		when(town.getMayor()).thenReturn(mayor);

		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerIsMayor(mayor, notMayor);
		assertThrows(TownsystemException.class, () -> townworld.dissolveTown(notMayor, town));
	}

	@Test
	public void dissolveTownTestWithInvalidTown() throws TownsystemException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		Town town = mock(Town.class);
		when(town.getTownName()).thenReturn("mytown");
		doThrow(TownsystemException.class).when(validationHandler).checkForValueInList(new ArrayList<>(), "mytown");
		assertThrows(TownsystemException.class, () -> townworld.dissolveTown(null, town));
	}

	@Test
	public void dissolveTownTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTown(townworld, townworldDao)).thenReturn(town);
		when(town.getTownName()).thenReturn("mytown");
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));
		when(town.getCitizens()).thenReturn(Arrays.asList(ecoPlayer));
		when(town.getMayor()).thenReturn(ecoPlayer);
		
		assertDoesNotThrow(() -> townworld.dissolveTown(ecoPlayer, town));

		assertEquals(0, townworld.getTownList().size());
		verify(town).despawnAllVillagers();
		assertDoesNotThrow(() -> verify(townworldManager, times(2)).performTownworldLocationCheckAllPlayers());
		assertDoesNotThrow(() -> verify(ecoPlayer).removeJoinedTown("mytown"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsMayor(ecoPlayer, ecoPlayer));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueInList(Arrays.asList("mytown"), "mytown"));
	}

	@Test
	public void deleteTest() {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTown(townworld, townworldDao)).thenReturn(town);
		when(town.getTownName()).thenReturn("mytown");
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		assertDoesNotThrow(() -> townworld.delete());

		verify(townworldDao).deleteSavefile();
		assertEquals(0, townworld.getTownList().size());
	}
	
	@Test
	public void deleteTestWithError() throws TownsystemException {
		Townworld townworld = new TownworldImpl(townworldDao, validationHandler, townworldManager, messageWrapper,
				serverProvider);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		doThrow(TownsystemException.class).when(validationHandler).checkForPlayerIsMayor(ecoPlayer, ecoPlayer);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createTown(townworld, townworldDao)).thenReturn(town);
		when(town.getTownName()).thenReturn("mytown");
		when(town.getMayor()).thenReturn(ecoPlayer);
		assertDoesNotThrow(() -> townworld.foundTown("mytown", loc, ecoPlayer));

		assertDoesNotThrow(() -> townworld.delete());

		verify(townworldDao).deleteSavefile();
		assertEquals(1, townworld.getTownList().size());
	}
}
