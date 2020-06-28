package com.ue.economyplayer.impl;

import org.bukkit.event.player.PlayerJoinEvent;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.PlayerException;

public class EconomyPlayerEventHandler {

	/**
	 * d.
	 * 
	 * @param event
	 * @throws PlayerException 
	 */
	public void handleJoin(PlayerJoinEvent event) throws PlayerException {
		String playername = event.getPlayer().getName();
		if (!EconomyPlayerController.getEconomyPlayerNameList().contains(playername)) {
			EconomyPlayerController.createEconomyPlayer(playername);
		}
		EconomyPlayer economyPlayer = EconomyPlayerController.getEconomyPlayerByName(playername);
		economyPlayer.setPlayer(event.getPlayer());
		if (ConfigController.isWildernessInteraction()) {
			economyPlayer.addWildernessPermission();
		}
	}
}
