package com.ue.economyplayer.logic.impl;

import javax.inject.Inject;

import org.bukkit.event.player.PlayerJoinEvent;

import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.general.impl.GeneralEconomyException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class EconomyPlayerEventHandlerImpl implements EconomyPlayerEventHandler {

	private final ConfigManager configManager;
	private final EconomyPlayerManager ecoPlayerManager;

	@Override
	public void handleJoin(PlayerJoinEvent event) throws EconomyPlayerException, GeneralEconomyException {
		String playername = event.getPlayer().getName();
		if (!ecoPlayerManager.getEconomyPlayerNameList().contains(playername)) {
			ecoPlayerManager.createEconomyPlayer(playername);
			EconomyPlayer economyPlayer = ecoPlayerManager.getEconomyPlayerByName(playername);
			economyPlayer.increasePlayerAmount(configManager.getStartAmount(), false);
		}
		EconomyPlayer economyPlayer = ecoPlayerManager.getEconomyPlayerByName(playername);
		economyPlayer.setPlayer(event.getPlayer());
		if (configManager.isWildernessInteraction()) {
			economyPlayer.addWildernessPermission();
		}
	}
}
