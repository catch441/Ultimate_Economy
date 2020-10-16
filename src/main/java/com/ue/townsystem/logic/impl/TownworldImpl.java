package com.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.slf4j.Logger;

import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.townsystem.dataaccess.api.TownworldDao;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.townsystem.logic.api.TownworldManager;

public class TownworldImpl implements Townworld {

	private final TownsystemValidationHandler townsystemValidationHandler;
	private final EconomyPlayerValidationHandler ecoPlayerValidationHandler;
	private final TownworldManager townworldManager;
	private final MessageWrapper messageWrapper;
	private final BankManager bankManager;
	private final ServerProvider serverProvider;
	private final Logger logger;
	private final TownworldDao townworldDao;

	private double foundationPrice, expandPrice;
	private final String worldName;
	private Map<String, Town> towns = new HashMap<>();

	/**
	 * Constructor for a townworld.
	 * 
	 * @param world
	 * @param isNew
	 * @param townworldDao
	 * @param townsystemValidationHandler
	 * @param ecoPlayerValidationHandler
	 * @param townworldManager
	 * @param messageWrapper
	 * @param bankManager
	 * @param logger
	 * @param serverProvider
	 */
	public TownworldImpl(String world, boolean isNew, TownworldDao townworldDao,
			TownsystemValidationHandler townsystemValidationHandler,
			EconomyPlayerValidationHandler ecoPlayerValidationHandler, TownworldManager townworldManager,
			MessageWrapper messageWrapper, BankManager bankManager, Logger logger, ServerProvider serverProvider) {
		this.townworldDao = townworldDao;
		this.townsystemValidationHandler = townsystemValidationHandler;
		this.ecoPlayerValidationHandler = ecoPlayerValidationHandler;
		this.townworldManager = townworldManager;
		this.messageWrapper = messageWrapper;
		this.bankManager = bankManager;
		this.logger = logger;
		this.serverProvider = serverProvider;
		worldName = world;
		if (isNew) {
			setupNewTownworld(world);
		} else {
			loadExistingTownworld();
		}
	}

	private void loadExistingTownworld() {
		foundationPrice = townworldDao.loadFoundationPrice();
		expandPrice = townworldDao.loadExpandPrice();
		for (String townName : townworldDao.loadTownworldTownNames()) {
			try {
				towns.put(townName, new TownImpl(townName, townworldManager, bankManager, townsystemValidationHandler,
						messageWrapper, townworldDao, this, serverProvider, logger));
			} catch (EconomyPlayerException | TownSystemException | GeneralEconomyException e) {
				logger.warn("[Ultimate_Economy] Failed to load town " + townName);
				logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	private void setupNewTownworld(String world) {
		foundationPrice = 0;
		expandPrice = 0;
		townworldDao.saveExpandPrice(0);
		townworldDao.saveFoundationPrice(0);
		townworldDao.saveWorldName(world);
	}

	@Override
	public void delete() throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		townworldDao.deleteSavefile();
		List<Town> listCopy = new ArrayList<>(getTownList());
		Iterator<Town> iter = listCopy.iterator();
		while (iter.hasNext()) {
			Town town = iter.next();
			dissolveTown(town.getMayor(), town);
		}
	}

	@Override
	public void foundTown(String townName, Location location, EconomyPlayer player)
			throws GeneralEconomyException, EconomyPlayerException, TownSystemException {
		List<String> townNames = townworldManager.getTownNameList();
		townsystemValidationHandler.checkForTownDoesNotExist(townNames, townName);
		townsystemValidationHandler.checkForChunkIsFree(this, location);
		ecoPlayerValidationHandler.checkForNotReachedMaxJoinedTowns(player.reachedMaxJoinedTowns());
		ecoPlayerValidationHandler.checkForEnoughMoney(player.getBankAccount(), getFoundationPrice(), true);
		Town town = new TownImpl(player, townName, location, townworldManager, bankManager, townsystemValidationHandler,
				messageWrapper, townworldDao, this, serverProvider, logger);
		towns.put(town.getTownName(), town);
		player.decreasePlayerAmount(getFoundationPrice(), true);
		townNames.add(townName);
		((TownworldManagerImpl) townworldManager).setTownNameList(townNames);
		townworldManager.performTownworldLocationCheckAllPlayers();
	}

	@Override
	public void dissolveTown(EconomyPlayer ecoPlayer, Town town)
			throws GeneralEconomyException, TownSystemException, EconomyPlayerException {
		townsystemValidationHandler.checkForPlayerIsMayor(town.getMayor(), ecoPlayer);
		townsystemValidationHandler.checkForTownworldHasTown(getTownNameList(), town.getTownName());
		for (EconomyPlayer citizen : town.getCitizens()) {
			citizen.removeJoinedTown(town.getTownName());
		}
		town.despawnAllVillagers();
		towns.remove(town.getTownName());
		townworldManager.performTownworldLocationCheckAllPlayers();
	}

	@Override
	public void despawnAllTownVillagers() {
		for (Entry<String, Town> town : towns.entrySet()) {
			town.getValue().despawnAllVillagers();
		}
	}

	@Override
	public List<String> getTownNameList() {
		return new ArrayList<>(towns.keySet());
	}

	@Override
	public double getFoundationPrice() {
		return foundationPrice;
	}

	@Override
	public void setFoundationPrice(double foundationPrice) throws GeneralEconomyException {
		townsystemValidationHandler.checkForPositiveAmount(foundationPrice);
		this.foundationPrice = foundationPrice;
		townworldDao.saveFoundationPrice(foundationPrice);
	}

	@Override
	public double getExpandPrice() {
		return expandPrice;
	}

	@Override
	public void setExpandPrice(double expandPrice) throws GeneralEconomyException {
		townsystemValidationHandler.checkForPositiveAmount(expandPrice);
		this.expandPrice = expandPrice;
		townworldDao.saveExpandPrice(expandPrice);
	}

	@Override
	public String getWorldName() {
		return worldName;
	}

	@Override
	public Town getTownByName(String townName) throws GeneralEconomyException {
		for (Entry<String, Town> town : towns.entrySet()) {
			if (town.getKey().equals(townName)) {
				return town.getValue();
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, townName);
	}

	@Override
	public boolean isChunkFree(Chunk chunk) {
		try {
			getTownByChunk(chunk);
			return false;
		} catch (TownSystemException e) {
			return true;
		}
	}

	@Override
	public List<Town> getTownList() {
		return new ArrayList<>(towns.values());
	}

	@Override
	public Town getTownByChunk(Chunk chunk) throws TownSystemException {
		for (Town town : towns.values()) {
			if (town.isClaimedByTown(chunk)) {
				return town;
			}
		}
		throw new TownSystemException(messageWrapper, TownExceptionMessageEnum.CHUNK_NOT_CLAIMED);
	}
}
