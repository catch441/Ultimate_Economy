package com.ue.economyplayer.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;

import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.jobsystem.logic.api.JobManager;

public class EconomyPlayerManagerImpl implements EconomyPlayerManager {

	@Inject
	EconomyPlayerDao ecoPlayerDao;
	@Inject
	MessageWrapper messageWrapper;
	@Inject
	EconomyPlayerValidationHandler validationHandler;
	@Inject
	BankManager bankManager;
	@Inject
	ConfigManager configManager;
	@Inject
	JobManager jobManager;
	private List<EconomyPlayer> economyPlayers = new ArrayList<>();

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
		getAllEconomyPlayers().add(new EconomyPlayerImpl(validationHandler, ecoPlayerDao, messageWrapper, configManager,
				bankManager, jobManager, Bukkit.getPlayer(playerName), playerName, true));
		ecoPlayerDao.savePlayerList(getEconomyPlayerNameList());
	}

	@Override
	public void deleteEconomyPlayer(EconomyPlayer player) {
		getAllEconomyPlayers().remove(player);
		ecoPlayerDao.savePlayerList(getEconomyPlayerNameList());
		ecoPlayerDao.deleteEconomyPlayer(player);
		bankManager.deleteBankAccount(player.getBankAccount());
	}

	@Override
	public void loadAllEconomyPlayers() {
		ecoPlayerDao.setupSavefile();
		List<String> playerList = ecoPlayerDao.loadPlayerList();
		for (String player : playerList) {
			getAllEconomyPlayers().add(new EconomyPlayerImpl(validationHandler, ecoPlayerDao, messageWrapper,
					configManager, bankManager, jobManager, Bukkit.getPlayer(player), player, false));
		}
	}
}
