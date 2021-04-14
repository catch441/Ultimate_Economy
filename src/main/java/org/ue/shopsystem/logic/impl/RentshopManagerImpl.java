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
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.townsystem.logic.TownSystemException;

public class RentshopManagerImpl implements RentshopManager {

	private static final Logger log = LoggerFactory.getLogger(RentshopManagerImpl.class);
	private final MessageWrapper messageWrapper;
	private final GeneralEconomyValidationHandler generalValidator;
	private final ServerProvider serverProvider;
	private final ConfigDao configDao;
	private Map<String, Rentshop> rentShopList = new HashMap<>();

	@Inject
	public RentshopManagerImpl(MessageWrapper messageWrapper, GeneralEconomyValidationHandler generalValidator,
			ServerProvider serverProvider, ConfigDao configDao) {
		this.messageWrapper = messageWrapper;
		this.generalValidator = generalValidator;
		this.serverProvider = serverProvider;
		this.configDao = configDao;
	}

	@Override
	public String generateFreeRentShopId() {
		int id = -1;
		boolean free = false;
		while (!free) {
			id++;
			if (!rentShopList.containsKey("R" + id)) {
				free = true;
			}
		}
		return "R" + id;
	}

	@Override
	public Rentshop getRentShopById(String id) throws GeneralEconomyException {
		Rentshop shop = rentShopList.get(id);
		generalValidator.checkForValueExists(shop, id);
		return shop;
	}

	@Override
	public Rentshop getRentShopByUniqueName(String name) throws GeneralEconomyException {
		for (Rentshop shop : getRentShops()) {
			if (shop.isRentable()) {
				if (("RentShop#" + shop.getShopId()).equals(name)) {
					return shop;
				}
			} else {
				if (name.equals(shop.getName() + "_" + shop.getOwner().getName())) {
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
		return new ArrayList<>(rentShopList.values());
	}

	@Override
	public Rentshop createRentShop(Location spawnLocation, int size, double rentalFee) throws GeneralEconomyException {
		generalValidator.checkForValidSize(size);
		generalValidator.checkForPositiveValue(rentalFee);
		Rentshop shop = serverProvider.getServiceComponent().getRentshop();
		shop.setupNew(generateFreeRentShopId(), spawnLocation, size, rentalFee);
		rentShopList.put(shop.getShopId(), shop);
		configDao.saveRentshopIds(getRentShopIdList());
		return shop;
	}

	@Override
	public void deleteRentShop(Rentshop rentshop) {
		rentShopList.remove(rentshop.getShopId());
		rentshop.deleteShop();
		configDao.saveRentshopIds(getRentShopIdList());
	}

	@Override
	public void despawnAllVillagers() {
		for (Rentshop shop : rentShopList.values()) {
			shop.despawnVillager();
		}
	}

	@Override
	public void loadAllRentShops() {
		for (String shopId : configDao.loadRentshopIds()) {
			try {
				Rentshop shop = serverProvider.getServiceComponent().getRentshop();
				shop.setupExisting(null, shopId);
				rentShopList.put(shopId, shop);
			} catch (TownSystemException | GeneralEconomyException | ShopSystemException e) {
				log.warn("[Ultimate_Economy] Failed to load the shop " + shopId);
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	@Override
	public void setupRentDailyTask() {
		new RentDailyTask(messageWrapper, this, serverProvider)
				.runTaskTimerAsynchronously(serverProvider.getJavaPluginInstance(), 1, 1000);
	}

	private List<String> getRentShopIdList() {
		return new ArrayList<>(rentShopList.keySet());
	}
}
