package com.ue.economyplayer.api;

import java.util.ArrayList;
import java.util.List;

import com.ue.bank.api.BankController;
import com.ue.economyplayer.impl.EconomyPlayerImpl;
import com.ue.economyplayer.impl.EconomyPlayerSavefileHandler;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;

public class EconomyPlayerController {

	private static List<EconomyPlayer> economyPlayers = new ArrayList<>();

	/**
	 * This method returns a list of all player names.
	 * 
	 * @return list of player names
	 */
	public static List<String> getEconomyPlayerNameList() {
		List<String> list = new ArrayList<>();
		for (EconomyPlayer economyPlayer : getAllEconomyPlayers()) {
			list.add(economyPlayer.getName());
		}
		return list;
	}

	/**
	 * This method returns a economyplayer by it's name.
	 * 
	 * @param name
	 * @return EconomyPlayer
	 * @throws PlayerException
	 */
	public static EconomyPlayer getEconomyPlayerByName(String name) throws PlayerException {
		for (EconomyPlayer economyPlayer : getAllEconomyPlayers()) {
			if (economyPlayer.getName().equals(name)) {
				return economyPlayer;
			}
		}
		throw PlayerException.getException(PlayerExceptionMessageEnum.PLAYER_DOES_NOT_EXIST);
	}

	/**
	 * This method returns all economyPlayers.
	 * 
	 * @return List of EcnomyPlayers
	 */
	public static List<EconomyPlayer> getAllEconomyPlayers() {
		return economyPlayers;
	}

	/**
	 * This method should be used to create a new EconomyPlayer.
	 * 
	 * @param playerName
	 * @throws PlayerException
	 */
	public static void createEconomyPlayer(String playerName) throws PlayerException {
		checkForPlayerDoesNotExist(playerName);
		getAllEconomyPlayers().add(new EconomyPlayerImpl(playerName, true));
		EconomyPlayerSavefileHandler.savePlayerList(getEconomyPlayerNameList());
	}

	/**
	 * Deletes an economy player.
	 * 
	 * @param player
	 */
	public static void deleteEconomyPlayer(EconomyPlayer player) {
		getAllEconomyPlayers().remove(player);
		EconomyPlayerSavefileHandler.savePlayerList(getEconomyPlayerNameList());
		EconomyPlayerSavefileHandler.deleteEconomyPlayer(player);
		BankController.deleteBankAccount(player.getBankAccount());
		// to remove all references, just to be sure
		player = null;
	}

	/**
	 * This method loads all economyPlayers. !!! The jobs have to be loaded first.
	 * The bank accounts have to be loaded first too.
	 * 
	 */
	public static void loadAllEconomyPlayers() {
		EconomyPlayerSavefileHandler.setupSavefile();
		List<String> playerList = EconomyPlayerSavefileHandler.loadPlayerList();
		for (String player : playerList) {
			getAllEconomyPlayers().add(new EconomyPlayerImpl(player, false));
		}
	}

	/*
	 * Validation methods
	 * 
	 */

	private static void checkForPlayerDoesNotExist(String playerName) throws PlayerException {
		if (getEconomyPlayerNameList().contains(playerName)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.PLAYER_ALREADY_EXIST);
		}
	}
}
