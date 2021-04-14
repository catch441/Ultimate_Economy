package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.townsystem.logic.api.TownsystemValidationHandler;
import org.ue.townsystem.logic.TownSystemException;

public class PlayershopManagerImpl implements PlayershopManager {

	private static final Logger log = LoggerFactory.getLogger(PlayershopManagerImpl.class);
	private final MessageWrapper messageWrapper;
	private final ShopValidationHandler validationHandler;
	private final TownsystemValidationHandler townsystemValidationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private final ConfigDao configDao;
	private final ServerProvider serverProvider;
	private List<Playershop> playerShopList = new ArrayList<>();
	
	@Inject
	public PlayershopManagerImpl(ConfigDao configDao, TownsystemValidationHandler townsystemValidationHandler,
			ShopValidationHandler validationHandler, MessageWrapper messageWrapper,
			ServerProvider serverProvider,
			GeneralEconomyValidationHandler generalValidator) {
		this.configDao = configDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.townsystemValidationHandler = townsystemValidationHandler;
		this.serverProvider = serverProvider;
		this.generalValidator = generalValidator;
	}

	@Override
	public String generateFreePlayerShopId() {
		int id = -1;
		boolean free = false;
		while (!free) {
			id++;
			if (!getPlayershopIdList().contains("P" + id)) {
				free = true;
			}
		}
		return "P" + id;
	}

	@Override
	public List<String> getPlayerShopUniqueNameList() {
		List<String> list = new ArrayList<>();
		for (Playershop shop : playerShopList) {
			list.add(shop.getName() + "_" + shop.getOwner().getName());
		}
		return list;
	}

	@Override
	public Playershop getPlayerShopByUniqueName(String name) throws GeneralEconomyException {
		for (Playershop shop : playerShopList) {
			if (name.equals(shop.getName() + "_" + shop.getOwner().getName())) {
				return shop;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public Playershop getPlayerShopById(String id) throws GeneralEconomyException {
		for (Playershop shop : playerShopList) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, id);
	}

	@Override
	public List<Playershop> getPlayerShops() {
		return new ArrayList<>(playerShopList);
	}

	@Override
	public List<String> getPlayershopIdList() {
		List<String> list = new ArrayList<>();
		for (Playershop shop : playerShopList) {
			list.add(shop.getShopId());
		}
		return list;
	}

	@Override
	public void createPlayerShop(String name, Location spawnLocation, int size, EconomyPlayer ecoPlayer)
			throws ShopSystemException, TownSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForValidShopName(name);
		validationHandler.checkForMaxPlayershopsForPlayer(getPlayerShops(), ecoPlayer);
		townsystemValidationHandler.checkForTownworldPlotPermission(spawnLocation, ecoPlayer);
		validationHandler.checkForShopNameIsFree(getPlayerShopUniqueNameList(), name, ecoPlayer);
		generalValidator.checkForValidSize(size);
		Playershop shop = serverProvider.getServiceComponent().getPlayershop();
		shop.setupNew(name, ecoPlayer, generateFreePlayerShopId(), spawnLocation, size);
		playerShopList.add(shop);
		configDao.savePlayershopIds(getPlayershopIdList());
	}

	@Override
	public void deletePlayerShop(Playershop playershop) {
		playerShopList.remove(playershop);
		playershop.deleteShop();
		configDao.savePlayershopIds(getPlayershopIdList());
	}

	@Override
	public void despawnAllVillagers() {
		for (Playershop shop : playerShopList) {
			shop.despawnVillager();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void loadAllPlayerShops() {
		if (configDao.hasPlayerShopNames()) {
			playerShopsOldLoadingAll();
		} else {
			playerShopsNewLoadingAll();
		}
	}

	private void playerShopsNewLoadingAll() {
		for (String shopId : configDao.loadPlayershopIds()) {
			try {
				Playershop shop = serverProvider.getServiceComponent().getPlayershop();
				shop.setupExisting(null, shopId);
				playerShopList.add(shop);
			} catch (TownSystemException | GeneralEconomyException | ShopSystemException e) {
				log.warn("[Ultimate_Economy] Failed to load the shop " + shopId);
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	@Deprecated
	private void playerShopsOldLoadingAll() {
		for (String shopName : configDao.loadPlayerShopNames()) {
			String shopId = generateFreePlayerShopId();
			try {
				Playershop shop = serverProvider.getServiceComponent().getPlayershop();
				shop.setupExisting(shopName, shopId);
				playerShopList.add(shop);
			} catch (TownSystemException | GeneralEconomyException | ShopSystemException e) {
				log.warn("[Ultimate_Economy] Failed to load the shop " + shopName);
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		configDao.removeDeprecatedPlayerShopNames();
		configDao.savePlayershopIds(getPlayershopIdList());
	}
}
