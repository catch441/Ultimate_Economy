package com.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.slf4j.Logger;

import com.ue.common.utils.ComponentProvider;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.Adminshop;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

public class AdminshopManagerImpl implements AdminshopManager {

	private List<Adminshop> adminShopList = new ArrayList<>();
	private final MessageWrapper messageWrapper;
	private final ShopValidationHandler validationHandler;
	private final ComponentProvider componentProvider;
	private final ServerProvider serverProvider;
	private final Logger logger;
	private final CustomSkullService skullService;
	private final ConfigDao configDao;

	/**
	 * Inject constructor.
	 * 
	 * @param componentProvider
	 * @param validationHandler
	 * @param messageWrapper
	 * @param logger
	 * @param serverProvider
	 * @param skullService
	 * @param configDao
	 */
	@Inject
	public AdminshopManagerImpl(ComponentProvider componentProvider, ShopValidationHandler validationHandler,
			MessageWrapper messageWrapper, Logger logger, ServerProvider serverProvider,
			CustomSkullService skullService, ConfigDao configDao) {
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.componentProvider = componentProvider;
		this.logger = logger;
		this.serverProvider = serverProvider;
		this.skullService = skullService;
		this.configDao = configDao;
	}

	@Override
	public Adminshop getAdminShopByName(String name) throws GeneralEconomyException {
		for (Adminshop shop : getAdminshopList()) {
			if (shop.getName().equals(name)) {
				return shop;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public Adminshop getAdminShopById(String id) throws GeneralEconomyException {
		for (Adminshop shop : getAdminshopList()) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, id);
	}

	@Override
	public List<String> getAdminshopIdList() {
		List<String> list = new ArrayList<>();
		for (Adminshop shop : getAdminshopList()) {
			list.add(shop.getShopId());
		}
		return list;
	}

	@Override
	public List<String> getAdminshopNameList() {
		List<String> list = new ArrayList<>();
		for (Adminshop shop : getAdminshopList()) {
			list.add(shop.getName());
		}
		return list;
	}

	@Override
	public List<Adminshop> getAdminshopList() {
		return new ArrayList<>(adminShopList);
	}

	@Override
	public String generateFreeAdminShopId() {
		int id = -1;
		boolean free = false;
		while (!free) {
			id++;
			if (!getAdminshopIdList().contains("A" + id)) {
				free = true;
			}
		}
		return "A" + id;
	}

	@Override
	public void createAdminShop(String name, Location spawnLocation, int size)
			throws ShopSystemException, GeneralEconomyException {
		validationHandler.checkForValidShopName(name);
		validationHandler.checkForValidSize(size);
		validationHandler.checkForShopNameIsFree(getAdminshopNameList(), name, null);
		ShopDao shopDao = componentProvider.getServiceComponent().getShopDao();
		adminShopList.add(new AdminshopImpl(name, generateFreeAdminShopId(), spawnLocation, size, shopDao,
				serverProvider, skullService, logger, this));
		configDao.saveAdminshopIds(getAdminshopIdList());
	}

	@Override
	public void deleteAdminShop(Adminshop adminshop) {
		adminShopList.remove(adminshop);
		adminshop.deleteShop();
		configDao.saveAdminshopIds(getAdminshopIdList());
	}

	@Override
	public void despawnAllVillagers() {
		for (Adminshop shop : getAdminshopList()) {
			shop.despawnVillager();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void loadAllAdminShops() {
		if (configDao.hasAdminShopNames()) {
			loadAllAdminshopsOld();
		} else {
			loadAllAdminshopsNew();
		}
	}

	private void loadAllAdminshopsNew() {
		for (String shopId : configDao.loadAdminshopIds()) {
			try {
				ShopDao shopDao = componentProvider.getServiceComponent().getShopDao();
				adminShopList.add(new AdminshopImpl(null, shopId, shopDao, serverProvider, skullService, logger, this));
			} catch (TownSystemException e) {
				logger.warn("[Ultimate_Economy] Failed to load the shop " + shopId);
				logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	@Deprecated
	private void loadAllAdminshopsOld() {
		for (String shopName : configDao.loadAdminShopNames()) {
			try {
				ShopDao shopDao = componentProvider.getServiceComponent().getShopDao();
				adminShopList.add(new AdminshopImpl(shopName, generateFreeAdminShopId(), shopDao, serverProvider,
						skullService, logger, this));
			} catch (TownSystemException e) {
				logger.warn("[Ultimate_Economy] Failed to load the shop " + shopName);
				logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		configDao.removeDeprecatedAdminshopNames();
		configDao.saveAdminshopIds(getAdminshopIdList());
	}
}
