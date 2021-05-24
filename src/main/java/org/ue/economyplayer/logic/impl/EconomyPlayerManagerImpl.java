package org.ue.economyplayer.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ue.bank.logic.api.BankManager;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.api.EconomyPlayerValidator;

public class EconomyPlayerManagerImpl implements EconomyPlayerManager {

	private final EconomyPlayerDao ecoPlayerDao;
	private final EconomyPlayerValidator validationHandler;
	private final BankManager bankManager;
	private final ServerProvider serverProvider;
	private Map<String, EconomyPlayer> economyPlayers = new HashMap<>();

	/**
	 * Inject constructor.
	 * 
	 * @param ecoPlayerDao
	 * @param validationHandler
	 * @param bankManager
	 * @param serverProvider
	 */
	public EconomyPlayerManagerImpl(EconomyPlayerDao ecoPlayerDao, EconomyPlayerValidator validationHandler,
			BankManager bankManager, ServerProvider serverProvider) {
		this.ecoPlayerDao = ecoPlayerDao;
		this.validationHandler = validationHandler;
		this.bankManager = bankManager;
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
		EconomyPlayer ecoPlayer = serverProvider.getProvider().createEconomyPlayer();
		ecoPlayer.setupNew(serverProvider.getPlayer(playerName), playerName);
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
			EconomyPlayer ecoPlayer = serverProvider.getProvider().createEconomyPlayer();
			ecoPlayer.setupExisting(serverProvider.getPlayer(player), player);
			economyPlayers.put(player, ecoPlayer);
		}
	}
}
