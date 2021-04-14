package org.ue.economyplayer.logic.impl;

import javax.inject.Inject;

import org.bukkit.event.player.PlayerJoinEvent;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerEventHandler;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.general.GeneralEconomyException;

public class EconomyPlayerEventHandlerImpl implements EconomyPlayerEventHandler {

	private final ConfigManager configManager;
	private final EconomyPlayerManager ecoPlayerManager;

	@Inject
	public EconomyPlayerEventHandlerImpl(ConfigManager configManager, EconomyPlayerManager ecoPlayerManager) {
		this.configManager = configManager;
		this.ecoPlayerManager = ecoPlayerManager;
	}

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
