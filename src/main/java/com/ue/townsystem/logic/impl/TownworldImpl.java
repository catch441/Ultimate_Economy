package com.ue.townsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.townsystem.dataaccess.api.TownsystemDao;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

public class TownworldImpl implements Townworld {

	private final TownsystemValidationHandler townsystemValidationHandler;
	private final EconomyPlayerValidationHandler ecoPlayerValidationHandler;
	private final TownworldManager townworldManager;
	private final MessageWrapper messageWrapper;
	private final BankManager bankManager;

	private final TownsystemDao townsystemDao;
	private double foundationPrice, expandPrice;
	private final String worldName;
	private Map<String, Town> towns = new HashMap<>();

	/**
	 * Constructor for a townworld.
	 * 
	 * @param world
	 * @param isNew
	 * @param townsystemDao
	 * @param townsystemValidationHandler
	 * @param ecoPlayerValidationHandler
	 * @param townworldManager
	 * @param messageWrapper
	 * @param bankManager
	 * @throws EconomyPlayerException
	 * @throws TownSystemException
	 * @throws GeneralEconomyException
	 */
	public TownworldImpl(String world, boolean isNew, TownsystemDao townsystemDao,
			TownsystemValidationHandler townsystemValidationHandler,
			EconomyPlayerValidationHandler ecoPlayerValidationHandler, TownworldManager townworldManager,
			MessageWrapper messageWrapper, BankManager bankManager)
			throws EconomyPlayerException, TownSystemException, GeneralEconomyException {
		this.townsystemDao = townsystemDao;
		this.townsystemValidationHandler = townsystemValidationHandler;
		this.ecoPlayerValidationHandler = ecoPlayerValidationHandler;
		this.townworldManager = townworldManager;
		this.messageWrapper = messageWrapper;
		this.bankManager = bankManager;
		worldName = world;
		if (isNew) {
			setupNewTownworld(world);
		} else {
			loadExistingTownworld();
		}
	}

	private void loadExistingTownworld() throws EconomyPlayerException, TownSystemException, GeneralEconomyException {
		foundationPrice = townsystemDao.loadFoundationPrice();
		expandPrice = townsystemDao.loadExpandPrice();
		for (String townName : getTownNameList()) {
			towns.put(townName, new TownImpl(townName, townworldManager, bankManager, townsystemValidationHandler,
					messageWrapper, townsystemDao, this));
		}
	}

	private void setupNewTownworld(String world) {
		foundationPrice = 0;
		expandPrice = 0;
		townsystemDao.saveExpandPrice(0);
		townsystemDao.saveFoundationPrice(0);
		townsystemDao.saveWorldName(world);
	}

	@Override
	public void delete() throws TownSystemException, EconomyPlayerException, GeneralEconomyException {
		townsystemDao.deleteSavefile();
		despawnAllTownVillagers();
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
				messageWrapper, townsystemDao, this);
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
	public void setFoundationPrice(double foundationPrice) {
		this.foundationPrice = foundationPrice;
		townsystemDao.saveFoundationPrice(foundationPrice);
	}

	@Override
	public double getExpandPrice() {
		return expandPrice;
	}

	@Override
	public void setExpandPrice(double expandPrice) {
		this.expandPrice = expandPrice;
		townsystemDao.saveExpandPrice(expandPrice);
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
