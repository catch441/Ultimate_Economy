package org.ue.townsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.general.GeneralEconomyException;
import org.ue.townsystem.logic.api.TownworldManager;

@ExtendWith(MockitoExtension.class)
public class TownTabCompleterImplTest {

	@InjectMocks
	TownTabCompleterImpl tabCompleter;
	@Mock
	TownworldManager townworldManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;

	@Test
	public void zeroArgsTest() {
		String[] args = { "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(13, list.size());
		assertEquals("create", list.get(0));
		assertEquals("delete", list.get(1));
		assertEquals("expand", list.get(2));
		assertEquals("addDeputy", list.get(3));
		assertEquals("removeDeputy", list.get(4));
		assertEquals("setTownSpawn", list.get(5));
		assertEquals("moveTownManager", list.get(6));
		assertEquals("plot", list.get(7));
		assertEquals("pay", list.get(8));
		assertEquals("tp", list.get(9));
		assertEquals("bank", list.get(10));
		assertEquals("withdraw", list.get(11));
		assertEquals("rename", list.get(12));
	}

	@Test
	public void zeroArgsTestWithMatching() {
		String[] args = { "r" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(5, list.size());
		assertEquals("create", list.get(0));
		assertEquals("removeDeputy", list.get(1));
		assertEquals("moveTownManager", list.get(2));
		assertEquals("withdraw", list.get(3));
		assertEquals("rename", list.get(4));
	}
	
	@Test
	public void twoArgsTestWithPlayerNotFoundError() throws GeneralEconomyException {
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("my error message");
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(e);
		String[] args = { "expand", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(0, list.size());
		verify(e).getMessage();
	}

	@Test
	public void createTest() {
		String[] args = { "create" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void createTestWithMoreArgs() {
		String[] args = { "create", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void expandTestWithZeroArgs() {
		String[] args = { "expand" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void expandTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "expand", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("town1", list.get(0));
		assertEquals("town2", list.get(1));
	}

	@Test
	public void expandTestWithMatching() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "expand", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("town1", list.get(0));
	}

	@Test
	public void expandTestWithMoreArgs() {
		String[] args = { "expand", "town1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void setTownSpawnTestWithZeroArgs() {
		String[] args = { "setTownSpawn" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void setTownSpawnTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "setTownSpawn", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("town1", list.get(0));
		assertEquals("town2", list.get(1));
	}

	@Test
	public void setTownSpawnTestWithMatching() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "setTownSpawn", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("town1", list.get(0));
	}

	@Test
	public void setTownSpawnTestWithMoreArgs() {
		String[] args = { "setTownSpawn", "town1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void bankTestWithZeroArgs() {
		String[] args = { "bank" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void bankTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "bank", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("town1", list.get(0));
		assertEquals("town2", list.get(1));
	}

	@Test
	public void bankTestWithMatching() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "bank", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("town1", list.get(0));
	}

	@Test
	public void bankTestWithMoreArgs() {
		String[] args = { "bank", "town1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void withdrawWithZeroArgs() {
		String[] args = { "withdraw" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void withdrawTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "withdraw", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("town1", list.get(0));
		assertEquals("town2", list.get(1));
	}

	@Test
	public void withdrawTestWithMatching() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "withdraw", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("town1", list.get(0));
	}

	@Test
	public void withdrawTestWithMoreArgs() {
		String[] args = { "bank", "town1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void renameWithZeroArgs() {
		String[] args = { "rename" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void renameTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "rename", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("town1", list.get(0));
		assertEquals("town2", list.get(1));
	}

	@Test
	public void renameTestWithMatching() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "rename", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("town1", list.get(0));
	}

	@Test
	public void renameTestWithMoreArgs() {
		String[] args = { "rename", "town1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void deleteWithZeroArgs() {
		String[] args = { "delete" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void deleteTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "delete", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("town1", list.get(0));
		assertEquals("town2", list.get(1));
	}

	@Test
	public void deleteTestWithMatching() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "delete", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("town1", list.get(0));
	}

	@Test
	public void deleteTestWithMoreArgs() {
		String[] args = { "delete", "town1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void addDeputyWithZeroArgs() {
		String[] args = { "addDeputy" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void addDeputyTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "addDeputy", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("town1", list.get(0));
		assertEquals("town2", list.get(1));
	}

	@Test
	public void addDeputyTestWithMatching() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "addDeputy", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("town1", list.get(0));
	}

	@Test
	public void addDeputyTestWithMoreArgs() {
		String[] args = { "addDeputy", "town1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void removeDeputyWithZeroArgs() {
		String[] args = { "removeDeputy" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void removeDeputyTest() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "removeDeputy", "" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(2, list.size());
		assertEquals("town1", list.get(0));
		assertEquals("town2", list.get(1));
	}

	@Test
	public void removeDeputyTestWithMatching() {
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getJoinedTownList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "removeDeputy", "1" };
		List<String> list = tabCompleter.onTabComplete(player, null, null, args);
		assertEquals(1, list.size());
		assertEquals("town1", list.get(0));
	}

	@Test
	public void removeDeputyTestWithMoreArgs() {
		String[] args = { "removeDeputy", "town1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void tpWithZeroArgs() {
		String[] args = { "tp" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void tpTest() {
		when(townworldManager.getTownNameList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "tp", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("town1", list.get(0));
		assertEquals("town2", list.get(1));
	}

	@Test
	public void tpTestWithMatching() {
		when(townworldManager.getTownNameList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "tp", "1" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("town1", list.get(0));
	}

	@Test
	public void tpTestWithMoreArgs() {
		String[] args = { "tp", "town1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void payWithZeroArgs() {
		String[] args = { "pay" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void payTest() {
		when(townworldManager.getTownNameList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "pay", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, list.size());
		assertEquals("town1", list.get(0));
		assertEquals("town2", list.get(1));
	}

	@Test
	public void payTestWithMatching() {
		when(townworldManager.getTownNameList()).thenReturn(Arrays.asList("town1", "town2"));
		String[] args = { "pay", "1" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("town1", list.get(0));
	}

	@Test
	public void payTestWithMoreArgs() {
		String[] args = { "pay", "town1", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void plotWithZeroArgs() {
		String[] args = { "plot" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}

	@Test
	public void plotTest() {
		String[] args = { "plot", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("setForSale", list.get(0));
	}

	@Test
	public void plotTestWithMatching() {
		String[] args = { "plot", "set" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, list.size());
		assertEquals("setForSale", list.get(0));
	}

	@Test
	public void plotTestWithMoreArgs() {
		String[] args = { "plot", "setForSale", "" };
		List<String> list = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, list.size());
	}
}
