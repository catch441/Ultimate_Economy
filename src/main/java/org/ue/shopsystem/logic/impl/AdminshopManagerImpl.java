package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.townsystem.logic.TownSystemException;

public class AdminshopManagerImpl implements AdminshopManager {

	private static final Logger log = LoggerFactory.getLogger(AdminshopManagerImpl.class);
	private final MessageWrapper messageWrapper;
	private final ShopValidationHandler validationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private final ServerProvider serverProvider;
	private final ConfigDao configDao;
	private Map<String, Adminshop> adminShopList = new HashMap<>();
	
	@Inject
	public AdminshopManagerImpl(ShopValidationHandler validationHandler, MessageWrapper messageWrapper,
			ServerProvider serverProvider, ConfigDao configDao,
			GeneralEconomyValidationHandler generalValidator) {
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.serverProvider = serverProvider;
		this.configDao = configDao;
		this.generalValidator = generalValidator;
	}

	@Override
	public Adminshop getAdminShopByName(String name) throws GeneralEconomyException {
		for (Adminshop shop : adminShopList.values()) {
			if (shop.getName().equals(name)) {
				return shop;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public Adminshop getAdminShopById(String id) throws GeneralEconomyException {
		Adminshop shop = adminShopList.get(id);
		generalValidator.checkForValueExists(shop, id);
		return shop;
	}

	@Override
	public List<String> getAdminshopIdList() {
		return new ArrayList<>(adminShopList.keySet());
	}

	@Override
	public List<String> getAdminshopNameList() {
		List<String> list = new ArrayList<>();
		for (Adminshop shop : adminShopList.values()) {
			list.add(shop.getName());
		}
		return list;
	}

	@Override
	public List<Adminshop> getAdminshopList() {
		return new ArrayList<>(adminShopList.values());
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
		generalValidator.checkForValidSize(size);
		generalValidator.checkForValueNotInList(getAdminshopNameList(), name);
		Adminshop shop = serverProvider.getServiceComponent().getAdminshop();
		shop.setupNew(name, generateFreeAdminShopId(), spawnLocation, size);
		adminShopList.put(shop.getShopId(), shop);
		configDao.saveAdminshopIds(getAdminshopIdList());
	}

	@Override
	public void deleteAdminShop(Adminshop adminshop) {
		adminShopList.remove(adminshop.getShopId());
		adminshop.deleteShop();
		configDao.saveAdminshopIds(getAdminshopIdList());
	}

	@Override
	public void despawnAllVillagers() {
		for (Adminshop shop : adminShopList.values()) {
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
				Adminshop shop = serverProvider.getServiceComponent().getAdminshop();
				shop.setupExisting(null, shopId);
				adminShopList.put(shopId, shop);
			} catch (TownSystemException | ShopSystemException | GeneralEconomyException e) {
				log.warn("[Ultimate_Economy] Failed to load the shop " + shopId);
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	@Deprecated
	private void loadAllAdminshopsOld() {
		for (String shopName : configDao.loadAdminShopNames()) {
			try {
				Adminshop shop = serverProvider.getServiceComponent().getAdminshop();
				shop.setupExisting(shopName, generateFreeAdminShopId());
				adminShopList.put(shop.getShopId(), shop);
			} catch (TownSystemException | ShopSystemException | GeneralEconomyException e) {
				log.warn("[Ultimate_Economy] Failed to load the shop " + shopName);
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		configDao.removeDeprecatedAdminshopNames();
		configDao.saveAdminshopIds(getAdminshopIdList());
	}
}
