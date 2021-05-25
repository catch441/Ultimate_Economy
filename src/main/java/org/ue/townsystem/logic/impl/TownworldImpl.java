package org.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.townsystem.dataaccess.api.TownworldDao;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.TownsystemValidator;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;

public class TownworldImpl implements Townworld {

	private static final Logger log = LoggerFactory.getLogger(TownworldImpl.class);
	private final TownsystemValidator validationHandler;
	private final TownworldManager townworldManager;
	private final MessageWrapper messageWrapper;
	private final ServerProvider serverProvider;
	private final TownworldDao townworldDao;

	private double foundationPrice, expandPrice;
	private String worldName;
	private Map<String, Town> towns = new HashMap<>();

	/**
	 * Inject constructor.
	 * 
	 * @param townworldDao
	 * @param validationHandler
	 * @param townworldManager
	 * @param messageWrapper
	 * @param serverProvider
	 */
	public TownworldImpl(TownworldDao townworldDao, TownsystemValidator validationHandler,
			TownworldManager townworldManager, MessageWrapper messageWrapper, ServerProvider serverProvider) {
		this.townworldDao = townworldDao;
		this.validationHandler = validationHandler;
		this.townworldManager = townworldManager;
		this.messageWrapper = messageWrapper;
		this.serverProvider = serverProvider;
	}

	@Override
	public void setupNew(String worldName) {
		this.worldName = worldName;
		foundationPrice = 0;
		expandPrice = 0;
		townworldDao.setupSavefile(worldName);
		townworldDao.saveExpandPrice(0);
		townworldDao.saveFoundationPrice(0);
		townworldDao.saveWorldName(worldName);
	}

	@Override
	public void setupExisting(String worldName) {
		this.worldName = worldName;
		townworldDao.setupSavefile(worldName);
		foundationPrice = townworldDao.loadFoundationPrice();
		expandPrice = townworldDao.loadExpandPrice();
		for (String townName : townworldDao.loadTownworldTownNames()) {
			try {
				Town town = serverProvider.getProvider().createTown(this, townworldDao);
				town.setupExisting(this, townName);
				towns.put(townName, town);
			} catch (EconomyPlayerException | TownsystemException | BankException e) {
				log.warn("[Ultimate_Economy] Failed to load town " + townName);
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	@Override
	public void delete() {
		townworldDao.deleteSavefile();
		List<Town> listCopy = getTownList();
		Iterator<Town> iter = listCopy.iterator();
		while (iter.hasNext()) {
			Town town = iter.next();
			try {
				dissolveTown(town.getMayor(), town);
			} catch (TownsystemException | EconomyPlayerException e) {
			}
		}
	}

	@Override
	public void foundTown(String townName, Location location, EconomyPlayer player)
			throws EconomyPlayerException, TownsystemException, BankException {
		List<String> townNames = townworldManager.getTownNameList();
		validationHandler.checkForValueNotInList(townNames, townName);
		validationHandler.checkForChunkIsFree(this, location);
		validationHandler.checkForNotReachedMax(player.reachedMaxJoinedTowns());
		validationHandler.checkForEnoughMoney(player.getBankAccount(), foundationPrice, true);
		Town town = serverProvider.getProvider().createTown(this, townworldDao);
		town.setupNew(this, player, townName, location);
		towns.put(town.getTownName(), town);
		player.decreasePlayerAmount(foundationPrice, true);
		townNames.add(townName);
		((TownworldManagerImpl) townworldManager).setTownNameList(townNames);
		townworldManager.performTownworldLocationCheckAllPlayers();
	}

	@Override
	public void dissolveTown(EconomyPlayer ecoPlayer, Town town) throws TownsystemException, EconomyPlayerException {
		validationHandler.checkForPlayerIsMayor(town.getMayor(), ecoPlayer);
		validationHandler.checkForValueInList(getTownNameList(), town.getTownName());
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
	public void setFoundationPrice(double foundationPrice) throws TownsystemException {
		validationHandler.checkForPositiveValue(foundationPrice);
		this.foundationPrice = foundationPrice;
		townworldDao.saveFoundationPrice(foundationPrice);
	}

	@Override
	public double getExpandPrice() {
		return expandPrice;
	}

	@Override
	public void setExpandPrice(double expandPrice) throws TownsystemException {
		validationHandler.checkForPositiveValue(expandPrice);
		this.expandPrice = expandPrice;
		townworldDao.saveExpandPrice(expandPrice);
	}

	@Override
	public String getWorldName() {
		return worldName;
	}

	@Override
	public Town getTownByName(String townName) throws TownsystemException {
		Town town = towns.get(townName);
		validationHandler.checkForValueExists(town, townName);
		return town;
	}

	@Override
	public boolean isChunkFree(Chunk chunk) {
		try {
			getTownByChunk(chunk);
			return false;
		} catch (TownsystemException e) {
			return true;
		}
	}

	@Override
	public List<Town> getTownList() {
		return new ArrayList<>(towns.values());
	}

	@Override
	public Town getTownByChunk(Chunk chunk) throws TownsystemException {
		for (Town town : towns.values()) {
			if (town.isClaimedByTown(chunk)) {
				return town;
			}
		}
		throw new TownsystemException(messageWrapper, ExceptionMessageEnum.CHUNK_NOT_CLAIMED);
	}
}
