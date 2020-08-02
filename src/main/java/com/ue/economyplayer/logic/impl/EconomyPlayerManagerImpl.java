package com.ue.economyplayer.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;

public class EconomyPlayerManagerImpl implements EconomyPlayerManager {

	private List<EconomyPlayer> economyPlayers = new ArrayList<>();
	private final EconomyPlayerDao ecoPlayerDao;
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerValidationHandler validationHandler;
	private final BankManager bankManager;

	/**
	 * Inject constructor.
	 * 
	 * @param bankManager
	 * @param validationHandler
	 * @param messageWrapper
	 * @param ecoPlayerDao
	 */
	@Inject
	public EconomyPlayerManagerImpl(BankManager bankManager, EconomyPlayerValidationHandler validationHandler,
			MessageWrapper messageWrapper, EconomyPlayerDao ecoPlayerDao) {
		this.ecoPlayerDao = ecoPlayerDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.bankManager = bankManager;
	}

	@Override
	public List<String> getEconomyPlayerNameList() {
		List<String> list = new ArrayList<>();
		for (EconomyPlayer economyPlayer : getAllEconomyPlayers()) {
			list.add(economyPlayer.getName());
		}
		return list;
	}

	@Override
	public EconomyPlayer getEconomyPlayerByName(String name) throws EconomyPlayerException {
		for (EconomyPlayer economyPlayer : getAllEconomyPlayers()) {
			if (economyPlayer.getName().equals(name)) {
				return economyPlayer;
			}
		}
		throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.PLAYER_DOES_NOT_EXIST);
	}

	@Override
	public List<EconomyPlayer> getAllEconomyPlayers() {
		return economyPlayers;
	}

	@Override
	public void createEconomyPlayer(String playerName) throws EconomyPlayerException {
		validationHandler.checkForPlayerDoesNotExist(getEconomyPlayerNameList(), playerName);
		getAllEconomyPlayers().add(new EconomyPlayerImpl(playerName, true));
		ecoPlayerDao.savePlayerList(getEconomyPlayerNameList());
	}

	@Override
	public void deleteEconomyPlayer(EconomyPlayer player) {
		getAllEconomyPlayers().remove(player);
		ecoPlayerDao.savePlayerList(getEconomyPlayerNameList());
		ecoPlayerDao.deleteEconomyPlayer(player);
		bankManager.deleteBankAccount(player.getBankAccount());
		// to remove all references, just to be sure
		player = null;
	}

	@Override
	public void loadAllEconomyPlayers() {
		ecoPlayerDao.setupSavefile();
		List<String> playerList = ecoPlayerDao.loadPlayerList();
		for (String player : playerList) {
			getAllEconomyPlayers().add(new EconomyPlayerImpl(player, false));
		}
	}
}
