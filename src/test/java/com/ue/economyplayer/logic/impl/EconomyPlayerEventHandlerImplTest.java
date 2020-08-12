package com.ue.economyplayer.logic.impl;

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

import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerEventHandlerImpl;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerEventHandlerImplTest {

	@Mock
	ConfigManager configManager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;

	@Test
	public void handleJoinTest() {
		EconomyPlayerEventHandlerImpl eventHandler = new EconomyPlayerEventHandlerImpl(ecoPlayerManager, configManager);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("kthschnll")).thenReturn(ecoPlayer));
		when(ecoPlayerManager.getEconomyPlayerNameList()).thenReturn(new ArrayList<>());
		when(configManager.isWildernessInteraction()).thenReturn(true);
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("kthschnll");
		PlayerJoinEvent event = new PlayerJoinEvent(player, "");
		
		assertDoesNotThrow(() -> eventHandler.handleJoin(event));
		assertDoesNotThrow(() -> verify(ecoPlayerManager).createEconomyPlayer("kthschnll"));
		verify(ecoPlayer).addWildernessPermission();
		verify(ecoPlayer).setPlayer(player);
	}
}
