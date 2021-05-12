package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;

public class AdminshopManagerImpl implements AdminshopManager {

	private static final Logger log = LoggerFactory.getLogger(AdminshopManagerImpl.class);
	private final MessageWrapper messageWrapper;
	private final ShopValidator validationHandler;
	private final ServerProvider serverProvider;
	private final ConfigDao configDao;
	private Map<String, Adminshop> adminShopList = new HashMap<>();

	@Inject
	public AdminshopManagerImpl(ShopValidator validationHandler, MessageWrapper messageWrapper,
			ServerProvider serverProvider, ConfigDao configDao) {
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.serverProvider = serverProvider;
		this.configDao = configDao;
	}

	@Override
	public Adminshop getAdminShopByName(String name) throws ShopsystemException {
		for (Adminshop shop : adminShopList.values()) {
			if (shop.getName().equals(name)) {
				return shop;
			}
		}
		throw new ShopsystemException(messageWrapper, ExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public Adminshop getAdminShopById(String id) throws ShopsystemException {
		Adminshop shop = adminShopList.get(id);
		validationHandler.checkForValueExists(shop, id);
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
	public void createAdminShop(String name, Location spawnLocation, int size) throws ShopsystemException {
		validationHandler.checkForValidShopName(name);
		validationHandler.checkForValidSize(size);
		validationHandler.checkForValueNotInList(getAdminshopNameList(), name);
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
			shop.despawn();
		}
	}

	@Override
	public void loadAllAdminShops() {
		for (String shopId : configDao.loadAdminshopIds()) {
			try {
				Adminshop shop = serverProvider.getServiceComponent().getAdminshop();
				shop.setupExisting(shopId);
				adminShopList.put(shopId, shop);
			} catch (EconomyPlayerException e) {
				log.warn("[Ultimate_Economy] Failed to load the shop " + shopId);
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}
}
