package com.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.townsystem.logic.impl.TownSystemException;

public class RentshopManagerImpl implements RentshopManager {

	private List<Rentshop> rentShopList = new ArrayList<>();
	private final MessageWrapper messageWrapper;
	private final ShopValidationHandler validationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private final ServerProvider serverProvider;
	private final Logger logger;
	private final CustomSkullService skullService;
	private final EconomyPlayerManager ecoPlayerManager;
	private final ConfigManager configManager;
	private final ConfigDao configDao;
	private final TownworldManager townworldManager;
	private final PlayershopManager playershopManager;

	/**
	 * Inject constructor.
	 * 
	 * @param serverProvider
	 * @param validationHandler
	 * @param messageWrapper
	 * @param logger
	 * @param skullService
	 * @param ecoPlayerManager
	 * @param configManager
	 * @param townworldManager
	 * @param playershopManager
	 * @param configDao
	 * @param generalValidator
	 */
	@Inject
	public RentshopManagerImpl(ServerProvider serverProvider, ShopValidationHandler validationHandler,
			MessageWrapper messageWrapper, Logger logger, CustomSkullService skullService,
			EconomyPlayerManager ecoPlayerManager, ConfigManager configManager, TownworldManager townworldManager,
			PlayershopManager playershopManager, ConfigDao configDao,
			GeneralEconomyValidationHandler generalValidator) {
		this.serverProvider = serverProvider;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.logger = logger;
		this.skullService = skullService;
		this.ecoPlayerManager = ecoPlayerManager;
		this.configManager = configManager;
		this.townworldManager = townworldManager;
		this.playershopManager = playershopManager;
		this.configDao = configDao;
		this.generalValidator = generalValidator;
	}

	@Override
	public String generateFreeRentShopId() {
		int id = -1;
		boolean free = false;
		while (!free) {
			id++;
			if (!getRentShopIdList().contains("R" + id)) {
				free = true;
			}
		}
		return "R" + id;
	}

	@Override
	public Rentshop getRentShopById(String id) throws GeneralEconomyException {
		for (Rentshop shop : getRentShops()) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, id);
	}

	@Override
	public Rentshop getRentShopByUniqueName(String name) throws GeneralEconomyException {
		for (Rentshop shop : getRentShops()) {
			if (shop.isRentable()) {
				if (("RentShop#" + shop.getShopId()).equals(name)) {
					return shop;
				}
			} else {
				if ((name).equals(shop.getName() + "_" + shop.getOwner().getName())) {
					return shop;
				}
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public List<String> getRentShopUniqueNameList() {
		List<String> list = new ArrayList<>();
		for (Rentshop shop : getRentShops()) {
			if (shop.isRentable()) {
				list.add("RentShop#" + shop.getShopId());
			} else {
				list.add(shop.getName() + "_" + shop.getOwner().getName());
			}
		}
		return list;
	}

	@Override
	public List<Rentshop> getRentShops() {
		return new ArrayList<>(rentShopList);
	}

	@Override
	public Rentshop createRentShop(Location spawnLocation, int size, double rentalFee) throws GeneralEconomyException {
		generalValidator.checkForValidSize(size);
		generalValidator.checkForPositiveValue(rentalFee);
		ShopDao shopDao = serverProvider.getServiceComponent().getShopDao();
		Rentshop shop = new RentshopImpl(spawnLocation, size, generateFreeRentShopId(), rentalFee, shopDao,
				serverProvider, skullService, logger, validationHandler, ecoPlayerManager, messageWrapper,
				configManager, townworldManager, playershopManager, generalValidator);
		rentShopList.add(shop);
		configDao.saveRentshopIds(getRentShopIdList());
		return shop;
	}

	@Override
	public void deleteRentShop(Rentshop rentshop) {
		rentShopList.remove(rentshop);
		rentshop.deleteShop();
		configDao.saveRentshopIds(getRentShopIdList());
	}

	@Override
	public void despawnAllVillagers() {
		for (Rentshop shop : rentShopList) {
			shop.despawnVillager();
		}
	}

	@Override
	public void loadAllRentShops() {
		for (String shopId : configDao.loadRentshopIds()) {
			try {
				ShopDao shopDao = serverProvider.getServiceComponent().getShopDao();
				rentShopList.add(new RentshopImpl(shopId, shopDao, serverProvider, skullService, logger,
						validationHandler, ecoPlayerManager, messageWrapper, configManager, townworldManager,
						playershopManager, generalValidator));
			} catch (TownSystemException | EconomyPlayerException | GeneralEconomyException | ShopSystemException e) {
				logger.warn("[Ultimate_Economy] Failed to load the shop " + shopId);
				logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	@Override
	public void setupRentDailyTask() {
		Logger logger = LoggerFactory.getLogger(RentDailyTask.class.getName());
		new RentDailyTask(logger, serverProvider, this, messageWrapper)
				.runTaskTimerAsynchronously(serverProvider.getJavaPluginInstance(), 1, 1000);
	}

	private List<String> getRentShopIdList() {
		List<String> list = new ArrayList<>();
		for (Rentshop shop : getRentShops()) {
			list.add(shop.getShopId());
		}
		return list;
	}
}
