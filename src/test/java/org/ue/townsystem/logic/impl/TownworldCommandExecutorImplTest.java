package org.ue.townsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;

@ExtendWith(MockitoExtension.class)
public class TownworldCommandExecutorImplTest {

	@InjectMocks
	TownworldCommandExecutorImpl executor;
	@Mock
	ConfigManager configManager;
	@Mock
	TownworldManager townworldManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	Player player;

	@Test
	public void unknownCommandTest() {
		String[] args = { "bla" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void zeroCommandTest() {
		String[] args = {};
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void disableCommandTestWithInvalidArgNumber() {
		String[] args = { "disable", "world", "" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		verify(player).sendMessage("/townworld disable <world>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void disableCommandTest() {
		when(messageWrapper.getString(MessageEnum.TOWNWORLD_DISABLE, "world")).thenReturn("my message");
		String[] args = { "disable", "world" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(townworldManager).deleteTownWorld("world"));
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void enableCommandTestWithInvalidArgNumber() {
		String[] args = { "enable", "world", "" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		verify(player).sendMessage("/townworld enable <world>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void enableCommandTest() {
		when(messageWrapper.getString(MessageEnum.TOWNWORLD_ENABLE, "world")).thenReturn("my message");
		String[] args = { "enable", "world" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(townworldManager).createTownWorld("world"));
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void setExpandPriceCommandTestWithInvalidArgNumber() {
		String[] args = { "setExpandPrice", "world", "7", "" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		verify(player).sendMessage("/townworld setExpandPrice <world> <price/chunk>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void setExpandPriceCommandTest() {
		Townworld world = mock(Townworld.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(world));
		when(messageWrapper.getString(MessageEnum.TOWNWORLD_SETEXPANDPRICE, "2", "$")).thenReturn("my message");
		when(configManager.getCurrencyText(2.0)).thenReturn("$");
		String[] args = { "setExpandPrice", "world", "2" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(world).setExpandPrice(2.0));
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void setExpandPriceCommandTestWithInvalidPrice() {
		when(messageWrapper.getErrorString(ExceptionMessageEnum.INVALID_PARAMETER, "two"))
				.thenReturn("my error message");
		String[] args = { "setExpandPrice", "world", "two" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void setExpandPriceCommandTestWithNoTownworld() {
		TownsystemException e = mock(TownsystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenThrow(e));
		String[] args = { "setExpandPrice", "world", "2" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void setFoundationPriceCommandTestWithInvalidArgNumber() {
		String[] args = { "setFoundationPrice", "world", "7", "" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		verify(player).sendMessage("/townworld setFoundationPrice <world> <price>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void setFoundationPriceCommandTest() {
		Townworld world = mock(Townworld.class);
		assertDoesNotThrow(() -> when(townworldManager.getTownWorldByName("world")).thenReturn(world));
		when(messageWrapper.getString(MessageEnum.TOWNWORLD_SETFOUNDATIONPRICE, "2", "$")).thenReturn("my message");
		when(configManager.getCurrencyText(2.0)).thenReturn("$");
		String[] args = { "setFoundationPrice", "world", "2" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(world).setFoundationPrice(2.0));
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void setFoundationPriceCommandTestWithInvalidPrice() {
		when(messageWrapper.getErrorString(ExceptionMessageEnum.INVALID_PARAMETER, "two"))
				.thenReturn("my error message");
		String[] args = { "setFoundationPrice", "world", "two" };
		boolean result = executor.onCommand(player, null, "townworld", args);
		assertTrue(result);
		verify(player).sendMessage("my error message");
		verifyNoMoreInteractions(player);
	}
}
