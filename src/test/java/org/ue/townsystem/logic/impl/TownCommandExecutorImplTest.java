package org.ue.townsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.townsystem.logic.api.Plot;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemValidationHandler;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;
import org.ue.townsystem.logic.TownSystemException;

@ExtendWith(MockitoExtension.class)
public class TownCommandExecutorImplTest {

	@InjectMocks
	TownCommandExecutorImpl executor;
	@Mock
	ConfigManager configManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	TownworldManager townworldManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	TownsystemValidationHandler townsystemValidationHandler;
	@Mock
	Player player;

	@Test
	public void unknownCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		String[] args = { "bla" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertFalse(result);
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void zeroCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		String[] args = {};
		boolean result = executor.onCommand(player, null, "town", args);
		assertFalse(result);
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void addDeputyCommandTestWithInvalidArgNumber() {
		String[] args = { "addDeputy", "mytown" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town addDeputy <town> <player>");
	}

	@Test
	public void addDeputyCommandTestWithPermissions() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		EconomyPlayer otherPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		when(town.getMayor()).thenReturn(ecoPlayer);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getString("added", "otherPlayer")).thenReturn("my message");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("otherPlayer")).thenReturn(otherPlayer));

		String[] args = { "addDeputy", "mytown", "otherPlayer" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(town).addDeputy(otherPlayer));
		assertDoesNotThrow(() -> verify(townsystemValidationHandler).checkForPlayerIsMayor(ecoPlayer, ecoPlayer));
		verify(player).sendMessage("my message");
	}

	@Test
	public void addDeputyCommandTestWithoutPermissions() throws EconomyPlayerException {
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		EconomyPlayer otherPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		when(e.getMessage()).thenReturn("my error message");
		when(town.getMayor()).thenReturn(otherPlayer);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		doThrow(e).when(townsystemValidationHandler).checkForPlayerIsMayor(otherPlayer, ecoPlayer);

		String[] args = { "addDeputy", "mytown", "otherPlayer" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(town, never()).addDeputy(any(EconomyPlayer.class)));
		verify(player).sendMessage("my error message");
	}

	@Test
	public void bankCommandTestWithInvalidArgNumber() {
		String[] args = { "bank", "mytown", "more" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town bank <town>");
	}

	@Test
	public void bankCommandTestWithPermissions() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(town.hasDeputyPermissions(ecoPlayer)).thenReturn(true);
		when(town.getTownBankAmount()).thenReturn(2.5);
		when(configManager.getCurrencyText(2.5)).thenReturn("$");
		when(messageWrapper.getString("town_bank", 2.5, "$")).thenReturn("my message");

		String[] args = { "bank", "mytown" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("my message");
	}

	@Test
	public void bankCommandTestWithoutPermissions() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(town.hasDeputyPermissions(ecoPlayer)).thenReturn(false);

		String[] args = { "bank", "mytown" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void createCommandTestWithInvalidArgNumber() {
		String[] args = { "create", "mytown", "more" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town create <town>");
	}

	@Test
	public void createCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Townworld townworld = mock(Townworld.class);
		World world = mock(World.class);
		Location loc = mock(Location.class);
		when(player.getLocation()).thenReturn(loc);
		when(player.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		when(messageWrapper.getString("town_create", "mytown")).thenReturn("my message");
		String[] args = { "create", "mytown" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(townworld).foundTown("mytown", loc, ecoPlayer));
		verify(player).sendMessage("my message");
	}

	@Test
	public void deleteCommandTestWithInvalidArgNumber() {
		String[] args = { "delete", "mytown", "more" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town delete <town>");
	}

	@Test
	public void deleteCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Townworld townworld = mock(Townworld.class);
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(town.getTownworld()).thenReturn(townworld);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		when(messageWrapper.getString("town_delete", "mytown")).thenReturn("my message");
		String[] args = { "delete", "mytown" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(townworld).dissolveTown(ecoPlayer, town));
		verify(player).sendMessage("my message");
	}

	@Test
	public void expandCommandTestWithInvalidArgNumber() {
		String[] args = { "expand", "mytown", "more" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town expand <town>");
	}

	@Test
	public void expandCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		when(player.getLocation()).thenReturn(loc);
		when(loc.getChunk()).thenReturn(chunk);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getString("town_expand")).thenReturn("my message");
		
		String[] args = { "expand", "mytown" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(town).expandTown(chunk, ecoPlayer));
		verify(player).sendMessage("my message");
	}

	@Test
	public void moveTownManagerCommandTestWithInvalidArgNumber() {
		String[] args = { "moveTownManager", "mytown" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town moveTownManager");
	}

	@Test
	public void moveTownManagerCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Townworld townworld = mock(Townworld.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		when(player.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(player.getLocation()).thenReturn(loc);
		when(loc.getChunk()).thenReturn(chunk);
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		String[] args = { "moveTownManager" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(town).moveTownManagerVillager(loc, ecoPlayer));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void payCommandTestWithInvalidArgNumber() {
		String[] args = { "pay", "mytown" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town pay <town> <amount>");
	}

	@Test
	public void payCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getString("town_pay", "mytown", 2.5, "$")).thenReturn("my message");
		when(configManager.getCurrencyText(2.5)).thenReturn("$");

		String[] args = { "pay", "mytown", "2.5" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(2.5, true));
		assertDoesNotThrow(() -> verify(town).increaseTownBankAmount(2.5));
		verify(player).sendMessage("my message");
	}

	@Test
	public void payCommandTestWithInvalidNumber() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getErrorString("invalid_parameter")).thenReturn("my error message");

		String[] args = { "pay", "mytown", "invalid" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verifyNoInteractions(ecoPlayer);
		assertDoesNotThrow(() -> verify(town, never()).increaseTownBankAmount(anyDouble()));
		verify(player).sendMessage("my error message");
	}

	@Test
	public void payCommandTestWithNotEnoughMoney() throws GeneralEconomyException, EconomyPlayerException {
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("my error message");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		doThrow(e).when(ecoPlayer).decreasePlayerAmount(2.5, true);

		String[] args = { "pay", "mytown", "2.5" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(town, never()).increaseTownBankAmount(anyDouble()));
		verify(player).sendMessage("my error message");
	}

	@Test
	public void removeDeputyCommandTestWithInvalidArgNumber() {
		String[] args = { "removeDeputy", "mytown", "player", "" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town removeDeputy <town> <player>");
	}

	@Test
	public void removeDeputyCommandTestWithPermissions() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		EconomyPlayer otherPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		when(town.getMayor()).thenReturn(ecoPlayer);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getString("removed", "otherPlayer")).thenReturn("my message");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("otherPlayer")).thenReturn(otherPlayer));

		String[] args = { "removeDeputy", "mytown", "otherPlayer" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(town).removeDeputy(otherPlayer));
		assertDoesNotThrow(() -> verify(townsystemValidationHandler).checkForPlayerIsMayor(ecoPlayer, ecoPlayer));
		verify(player).sendMessage("my message");
	}

	@Test
	public void removeDeputyCommandTestWithoutPermissions() throws EconomyPlayerException {
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		EconomyPlayer otherPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		when(e.getMessage()).thenReturn("my error message");
		when(town.getMayor()).thenReturn(otherPlayer);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		doThrow(e).when(townsystemValidationHandler).checkForPlayerIsMayor(otherPlayer, ecoPlayer);

		String[] args = { "removeDeputy", "mytown", "otherPlayer" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(town, never()).removeDeputy(any(EconomyPlayer.class)));
		verify(player).sendMessage("my error message");
	}

	@Test
	public void plotCommandTestWithOneArg() {
		String[] args = { "plot" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town plot [setForSale/setForRent]");
	}
	
	@Test
	public void setForSaleCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Townworld townworld = mock(Townworld.class);
		Plot plot = mock(Plot.class);
		Chunk chunk = mock(Chunk.class);
		World world = mock(World.class);
		Location loc = mock(Location.class);
		when(player.getLocation()).thenReturn(loc);
		when(loc.getChunk()).thenReturn(chunk);
		when(player.getWorld()).thenReturn(world);
		when(world.getName()).thenReturn("world");
		when(chunk.getX()).thenReturn(1);
		when(chunk.getZ()).thenReturn(2);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(townworld));
		assertDoesNotThrow(() -> when(town.getPlotByChunk("1/2")).thenReturn(plot));
		assertDoesNotThrow(() -> when(townworld.getTownByChunk(chunk)).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		when(messageWrapper.getString("town_plot_setForSale")).thenReturn("my message");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		String[] args = { "plot", "setForSale", "2.5" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(plot).setForSale(2.5, loc, ecoPlayer));
		verify(player).sendMessage("my message");
	}
	
	@Test
	public void setForSaleCommandTestWithInvalidArgNumber() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		String[] args = { "plot", "setForSale", "2", "more" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town plot setForSale <price>");
	}
	
	@Test
	public void plotSetForRentCommandTestWithInvalidArgNumber() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		String[] args = { "plot", "setForRent", "2", "more" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town plot setForRent <price/24h>");
	}
	
	@Test
	public void plotSetForRentCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		// TODO UE-116
		String[] args = { "plot", "setForRent", "mytown", "2" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
	}

	@Test
	public void renameCommandTestWithInvalidArgNumber() {
		String[] args = { "rename" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town rename <old name> <new name>");
	}

	@Test
	public void renameCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getString("town_rename", "mytown", "newname")).thenReturn("my message");
		
		String[] args = { "rename", "mytown", "newname" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(town).renameTown("newname", ecoPlayer));
		assertDoesNotThrow(() -> verify(townworldManager).performTownworldLocationCheckAllPlayers());
		verify(player).sendMessage("my message");
	}

	@Test
	public void setTownSpawnCommandTestWithInvalidArgNumber() {
		String[] args = { "setTownSpawn" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town setTownSpawn <town>");
	}

	@Test
	public void setTownSpawnCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		when(loc.getX()).thenReturn(1.0);
		when(loc.getY()).thenReturn(2.0);
		when(loc.getZ()).thenReturn(3.0);
		when(player.getLocation()).thenReturn(loc);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getString("town_setTownSpawn", 1, 2, 3)).thenReturn("my message");
		
		String[] args = { "setTownSpawn", "mytown" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(town).changeTownSpawn(loc, ecoPlayer));
		
		verify(player).sendMessage("my message");
	}

	@Test
	public void tpCommandTestWithInvalidArgNumber() {
		String[] args = { "tp", "mytown", "more" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town tp <town>");
	}

	@Test
	public void tpCommandTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		Location loc = mock(Location.class);
		when(town.getTownSpawn()).thenReturn(loc);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		String[] args = { "tp", "mytown" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).teleport(loc);
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void withdrawCommandTestWithInvalidArgNumber() {
		String[] args = { "withdraw", "mytown", "2", "more" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(player).sendMessage("/town withdraw <town> <amount>");
	}

	@Test
	public void withdrawCommandTestWithPermission() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		when(town.hasDeputyPermissions(ecoPlayer)).thenReturn(true);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));

		String[] args = { "withdraw", "mytown", "2.5" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(townsystemValidationHandler).checkForPlayerHasDeputyPermission(true));
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(2.5, true));
		assertDoesNotThrow(() -> verify(town).decreaseTownBankAmount(2.5));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void withdrawCommandTestWithoutPermission() throws EconomyPlayerException {
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		when(e.getMessage()).thenReturn("my error message");
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		when(town.hasDeputyPermissions(ecoPlayer)).thenReturn(false);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		doThrow(e).when(townsystemValidationHandler).checkForPlayerHasDeputyPermission(false);

		String[] args = { "withdraw", "mytown", "2.5" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(2.5, true));
		assertDoesNotThrow(() -> verify(town, never()).decreaseTownBankAmount(2.5));
		verify(player).sendMessage("my error message");
	}

	@Test
	public void withdrawCommandTestWithInvalidNumber() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(messageWrapper.getErrorString("invalid_parameter")).thenReturn("my error message");

		String[] args = { "withdraw", "mytown", "invalid" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verifyNoInteractions(ecoPlayer);
		assertDoesNotThrow(() -> verify(town, never()).increaseTownBankAmount(anyDouble()));
		verify(player).sendMessage("my error message");
	}

	@Test
	public void withdrawCommandTestWithNotEnoughMoney()
			throws GeneralEconomyException, EconomyPlayerException, TownSystemException {
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Town town = mock(Town.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownByName("mytown")).thenReturn(town));
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		doThrow(e).when(town).decreaseTownBankAmount(2.5);

		String[] args = { "withdraw", "mytown", "2.5" };
		boolean result = executor.onCommand(player, null, "town", args);
		assertTrue(result);
		verify(ecoPlayer, never()).increasePlayerAmount(2.5, true);
		verify(player).sendMessage("my error message");
	}
}
