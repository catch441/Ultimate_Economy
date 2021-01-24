package com.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.shopsystem.logic.api.Adminshop;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AdminshopManagerImpl implements AdminshopManager {

	private List<Adminshop> adminShopList = new ArrayList<>();
	private final MessageWrapper messageWrapper;
	private final ShopValidationHandler validationHandler;
	private final GeneralEconomyValidationHandler generalValidator;
	private final ServerProvider serverProvider;
	private final ConfigDao configDao;

	@Override
	public Adminshop getAdminShopByName(String name) throws GeneralEconomyException {
		for (Adminshop shop : adminShopList) {
			if (shop.getName().equals(name)) {
				return shop;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
	}

	@Override
	public Adminshop getAdminShopById(String id) throws GeneralEconomyException {
		for (Adminshop shop : adminShopList) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, id);
	}

	@Override
	public List<String> getAdminshopIdList() {
		List<String> list = new ArrayList<>();
		for (Adminshop shop : adminShopList) {
			list.add(shop.getShopId());
		}
		return list;
	}

	@Override
	public List<String> getAdminshopNameList() {
		List<String> list = new ArrayList<>();
		for (Adminshop shop : adminShopList) {
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
		generalValidator.checkForValidSize(size);
		generalValidator.checkForValueNotInList(getAdminshopNameList(), name);
		Adminshop shop = serverProvider.getServiceComponent().getAdminshop();
		shop.setupNew(name, generateFreeAdminShopId(), spawnLocation, size);
		adminShopList.add(shop);
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
		for (Adminshop shop : adminShopList) {
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
				adminShopList.add(shop);
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
				adminShopList.add(shop);
			} catch (TownSystemException | ShopSystemException | GeneralEconomyException e) {
				log.warn("[Ultimate_Economy] Failed to load the shop " + shopName);
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		configDao.removeDeprecatedAdminshopNames();
		configDao.saveAdminshopIds(getAdminshopIdList());
	}
}
