package org.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerEventHandlerImplTest {

	@Mock
	ConfigManager configManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;

	@Test
	public void handleJoinTest() {
		EconomyPlayerEventHandlerImpl eventHandler = new EconomyPlayerEventHandlerImpl(configManager, ecoPlayerManager);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(new ArrayList<>());
		when(configManager.isWildernessInteraction()).thenReturn(true);
		when(configManager.getStartAmount()).thenReturn(1.5);
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("catch441");
		PlayerJoinEvent event = new PlayerJoinEvent(player, "");
		
		assertDoesNotThrow(() -> eventHandler.handleJoin(event));
		assertDoesNotThrow(() -> verify(ecoPlayerManager).createEconomyPlayer("catch441"));
		verify(ecoPlayer).addWildernessPermission();
		verify(ecoPlayer).setPlayer(player);
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(1.5, false));
	}
}
