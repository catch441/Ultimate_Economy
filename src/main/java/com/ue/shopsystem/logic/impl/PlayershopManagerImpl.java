package com.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.common.utils.ComponentProvider;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

public class PlayershopManagerImpl implements PlayershopManager {

	private List<Playershop> playerShopList = new ArrayList<>();
	private final Logger logger;
	private final MessageWrapper messageWrapper;
	private final ShopValidationHandler validationHandler;
	private final TownsystemValidationHandler townsystemValidationHandler;
	private final TownworldManager townworldManager;
	private final ComponentProvider componentProvider;
	private final ConfigDao configDao;
	private final ConfigManager configManager;
	private final ServerProvider serverProvider;
	private final CustomSkullService customSkullService;
	private final EconomyPlayerManager ecoPlayerManager;

	/**
	 * Inject constructor.
	 * 
	 * @param configDao
	 * @param townsystemValidationHandler
	 * @param validationHandler
	 * @param messageWrapper
	 * @param componentProvider
	 * @param logger
	 * @param serverProvider
	 * @param customSkullService
	 * @param ecoPlayerManager
	 * @param configManager
	 * @param townworldManager
	 */
	@Inject
	public PlayershopManagerImpl(ConfigDao configDao, TownsystemValidationHandler townsystemValidationHandler,
			ShopValidationHandler validationHandler, MessageWrapper messageWrapper, ComponentProvider componentProvider,
			Logger logger, ServerProvider serverProvider, CustomSkullService customSkullService,
			EconomyPlayerManager ecoPlayerManager, ConfigManager configManager, TownworldManager townworldManager) {
		this.configDao = configDao;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.townsystemValidationHandler = townsystemValidationHandler;
		this.componentProvider = componentProvider;
		this.logger = logger;
		this.serverProvider = serverProvider;
		this.customSkullService = customSkullService;
		this.ecoPlayerManager = ecoPlayerManager;
		this.configManager = configManager;
		this.townworldManager = townworldManager;
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
		validationHandler.checkForValidSize(size);
		Logger logger = LoggerFactory.getLogger(PlayershopImpl.class);
		ShopDao shopDao = componentProvider.getServiceComponent().getShopDao();
		playerShopList.add(new PlayershopImpl(name, ecoPlayer, generateFreePlayerShopId(), spawnLocation, size, shopDao,
				serverProvider, customSkullService, logger, validationHandler, ecoPlayerManager, messageWrapper,
				configManager, townworldManager, this));
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
				Logger logger = LoggerFactory.getLogger(PlayershopImpl.class);
				ShopDao shopDao = componentProvider.getServiceComponent().getShopDao();
				playerShopList.add(new PlayershopImpl(null, shopId, shopDao, serverProvider, customSkullService, logger,
						validationHandler, ecoPlayerManager, messageWrapper, configManager, townworldManager, this));
			} catch (TownSystemException | EconomyPlayerException | GeneralEconomyException | ShopSystemException e) {
				logger.warn("[Ultimate_Economy] Failed to load the shop " + shopId);
				logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	@Deprecated
	private void playerShopsOldLoadingAll() {
		for (String shopName : configDao.loadPlayerShopNames()) {
			String shopId = generateFreePlayerShopId();
			try {
				Logger logger = LoggerFactory.getLogger(PlayershopImpl.class);
				ShopDao shopDao = componentProvider.getServiceComponent().getShopDao();
				playerShopList.add(new PlayershopImpl(shopName, shopId, shopDao, serverProvider, customSkullService,
						logger, validationHandler, ecoPlayerManager, messageWrapper, configManager, townworldManager, this));
			} catch (TownSystemException | EconomyPlayerException | GeneralEconomyException | ShopSystemException e) {
				logger.warn("[Ultimate_Economy] Failed to load the shop " + shopName);
				logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		configDao.removeDeprecatedPlayerShopNames();
		configDao.savePlayershopIds(getPlayershopIdList());
	}
}
