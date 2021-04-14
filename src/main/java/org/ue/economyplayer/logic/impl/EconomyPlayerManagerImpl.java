package org.ue.economyplayer.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.jobsystem.logic.api.JobManager;

import dagger.Lazy;

public class EconomyPlayerManagerImpl implements EconomyPlayerManager {

	private final EconomyPlayerDao ecoPlayerDao;
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerValidationHandler validationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private final BankManager bankManager;
	private final ConfigManager configManager;
	// lazy because of circulating dependency, cannot resolved with refactoring
	// the object will never be created, thats just fine, because it is only used
	// during load player jobs and not during runtime
	private final Lazy<JobManager> jobManager;
	private final ServerProvider serverProvider;
	private List<EconomyPlayer> economyPlayers = new ArrayList<>();
	
	@Inject
	public EconomyPlayerManagerImpl(GeneralEconomyValidationHandler generalValidator, EconomyPlayerDao ecoPlayerDao,
			MessageWrapper messageWrapper, EconomyPlayerValidationHandler validationHandler, BankManager bankManager,
			ConfigManager configManager, Lazy<JobManager> jobManager, ServerProvider serverProvider) {
		this.ecoPlayerDao = ecoPlayerDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.bankManager = bankManager;
		this.configManager = configManager;
		this.jobManager = jobManager;
		this.serverProvider = serverProvider;
		this.generalValidator = generalValidator;
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
	public EconomyPlayer getEconomyPlayerByName(String name) throws GeneralEconomyException {
		for (EconomyPlayer economyPlayer : getAllEconomyPlayers()) {
			if (economyPlayer.getName().equals(name)) {
				return economyPlayer;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public List<EconomyPlayer> getAllEconomyPlayers() {
		return economyPlayers;
	}

	@Override
	public void createEconomyPlayer(String playerName) throws GeneralEconomyException {
		generalValidator.checkForValueNotInList(getEconomyPlayerNameList(), playerName);
		Logger logger = LoggerFactory.getLogger(EconomyPlayerImpl.class);
		getAllEconomyPlayers().add(new EconomyPlayerImpl(generalValidator, logger, serverProvider, validationHandler,
				ecoPlayerDao, messageWrapper, configManager, bankManager, jobManager.get(),
				serverProvider.getPlayer(playerName), playerName, true));
	}

	@Override
	public void deleteEconomyPlayer(EconomyPlayer player) {
		getAllEconomyPlayers().remove(player);
		ecoPlayerDao.deleteEconomyPlayer(player.getName());
		bankManager.deleteBankAccount(player.getBankAccount());
	}

	@Override
	public void loadAllEconomyPlayers() {
		ecoPlayerDao.setupSavefile();
		List<String> playerList = ecoPlayerDao.loadPlayerList();
		for (String player : playerList) {
			Logger logger = LoggerFactory.getLogger(EconomyPlayerImpl.class);
			getAllEconomyPlayers().add(new EconomyPlayerImpl(generalValidator, logger, serverProvider,
					validationHandler, ecoPlayerDao, messageWrapper, configManager, bankManager, jobManager.get(),
					serverProvider.getPlayer(player), player, false));
		}
	}
}
