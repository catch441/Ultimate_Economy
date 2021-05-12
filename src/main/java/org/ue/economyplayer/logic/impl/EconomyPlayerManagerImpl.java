package org.ue.economyplayer.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.ue.bank.logic.api.BankManager;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.api.EconomyPlayerValidator;
import org.ue.jobsystem.logic.api.JobManager;

import dagger.Lazy;

public class EconomyPlayerManagerImpl implements EconomyPlayerManager {

	private final EconomyPlayerDao ecoPlayerDao;
	private final MessageWrapper messageWrapper;
	private final EconomyPlayerValidator validationHandler;
	private final BankManager bankManager;
	private final ConfigManager configManager;
	// lazy because of circulating dependency, cannot resolved with refactoring
	// the object will never be created, thats just fine, because it is only used
	// during load player jobs and not during runtime
	private final Lazy<JobManager> jobManager;
	private final ServerProvider serverProvider;
	private Map<String, EconomyPlayer> economyPlayers = new HashMap<>();

	@Inject
	public EconomyPlayerManagerImpl(EconomyPlayerDao ecoPlayerDao, MessageWrapper messageWrapper,
			EconomyPlayerValidator validationHandler, BankManager bankManager, ConfigManager configManager,
			Lazy<JobManager> jobManager, ServerProvider serverProvider) {
		this.ecoPlayerDao = ecoPlayerDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.bankManager = bankManager;
		this.configManager = configManager;
		this.jobManager = jobManager;
		this.serverProvider = serverProvider;
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
		EconomyPlayer ecoPlayer = economyPlayers.get(name);
		validationHandler.checkForValueExists(ecoPlayer, name);
		return ecoPlayer;
	}

	@Override
	public List<EconomyPlayer> getAllEconomyPlayers() {
		return new ArrayList<>(economyPlayers.values());
	}

	@Override
	public void createEconomyPlayer(String playerName) throws EconomyPlayerException {
		validationHandler.checkForValueNotInList(getEconomyPlayerNameList(), playerName);
		EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao,
				messageWrapper, configManager, bankManager, jobManager.get(), serverProvider.getPlayer(playerName),
				playerName, true);
		economyPlayers.put(playerName, ecoPlayer);
	}

	@Override
	public void deleteEconomyPlayer(EconomyPlayer player) {
		economyPlayers.remove(player.getName());
		ecoPlayerDao.deleteEconomyPlayer(player.getName());
		bankManager.deleteBankAccount(player.getBankAccount());
	}

	@Override
	public void loadAllEconomyPlayers() {
		ecoPlayerDao.setupSavefile();
		List<String> playerList = ecoPlayerDao.loadPlayerList();
		for (String player : playerList) {
			EconomyPlayer ecoPlayer = new EconomyPlayerImpl(serverProvider, validationHandler, ecoPlayerDao,
					messageWrapper, configManager, bankManager, jobManager.get(), serverProvider.getPlayer(player),
					player, false);
			economyPlayers.put(player, ecoPlayer);
		}
	}
}
