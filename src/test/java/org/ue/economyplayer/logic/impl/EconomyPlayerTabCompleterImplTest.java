package org.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerTabCompleterImplTest {

	@InjectMocks
	EconomyPlayerTabCompleterImpl tabCompleter;
	@Mock
	EconomyPlayerManager ecoPlayerManager;

	@Test
	public void bankCommandWithZeroArgs() {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("bank");
		
		String[] args = { "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("on", result.get(0));
		assertEquals("off", result.get(1));
	}

	@Test
	public void bankCommandWithTabComplete1() {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("bank");

		String[] args = { "f" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("off", result.get(0));
	}

	@Test
	public void bankCommandWithTabComplete2() {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("bank");
		
		String[] args = { "n" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("on", result.get(0));
	}

	@Test
	public void bankCommandWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("bank");
		
		String[] args = { "on", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void homeCommandWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("home");
		
		String[] args = { "myhome", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void delhomeCommandWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("delhome");
		
		String[] args = { "myhome", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void homeCommandWithZeroArgs() {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("home");
		
		String[] args = { "" };
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Map<String, Location> homes = new HashMap<>();
		homes.put("myhome1", null);
		homes.put("myhome2", null);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getHomeList()).thenReturn(homes);
		
		
		List<String> result = tabCompleter.onTabComplete(player, command, null, args);
		assertEquals(2, result.size());
		assertEquals("myhome2", result.get(0));
		assertEquals("myhome1", result.get(1));
	}
	
	@Test
	public void homeCommandWithNoEcoPlayer() throws EconomyPlayerException {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("home");
		
		String[] args = { "" };
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(EconomyPlayerException.class);
		
		List<String> result = tabCompleter.onTabComplete(player, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void homeCommandWithTabComplete() {
		Command command = mock(Command.class);
		when(command.getName()).thenReturn("home");
		
		String[] args = { "2" };
		Player player = mock(Player.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Map<String, Location> homes = new HashMap<>();
		homes.put("myhome2", null);
		when(player.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayer.getHomeList()).thenReturn(homes);

		List<String> result = tabCompleter.onTabComplete(player, command, null, args);
		assertEquals(1, result.size());
		assertEquals("myhome2", result.get(0));
	}
}
