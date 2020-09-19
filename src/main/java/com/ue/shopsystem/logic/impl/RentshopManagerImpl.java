package com.ue.shopsystem.logic.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.common.utils.ComponentProvider;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;
import com.ue.ultimate_economy.UltimateEconomy;

public class RentshopManagerImpl implements RentshopManager {

	private List<Rentshop> rentShopList = new ArrayList<>();
	private final MessageWrapper messageWrapper;
	private final ShopValidationHandler validationHandler;
	private final ServerProvider serverProvider;
	private final ComponentProvider componentProvider;

	/**
	 * Inject constructor.
	 * 
	 * @param serverProvider
	 * @param validationHandler
	 * @param messageWrapper
	 * @param componentProvider
	 */
	@Inject
	public RentshopManagerImpl(ServerProvider serverProvider, ShopValidationHandler validationHandler,
			MessageWrapper messageWrapper, ComponentProvider componentProvider) {
		this.serverProvider = serverProvider;
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
		this.componentProvider = componentProvider;
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
	public List<String> getRentShopIdList() {
		List<String> list = new ArrayList<>();
		for (Rentshop shop : getRentShops()) {
			list.add(shop.getShopId());
		}
		return list;
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
	public Rentshop getRentShopByUniqueName(String name, Player player) throws GeneralEconomyException {
		for (Rentshop shop : getRentShops()) {
			if (shop.isRentable()) {
				if (("RentShop#" + shop.getShopId()).equals(name)) {
					return shop;
				}
			} else {
				if ((name + "_" + player.getName()).equals(shop.getName() + "_" + shop.getOwner().getName())) {
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
		return rentShopList;
	}

	@Override
	public Rentshop createRentShop(Location spawnLocation, int size, double rentalFee) throws GeneralEconomyException {
		validationHandler.checkForValidSize(size);
		validationHandler.checkForPositiveValue(rentalFee);
		ShopDao shopDao = componentProvider.getServiceComponent().getShopDao();
		Rentshop shop = new RentshopImpl(spawnLocation, size, generateFreeRentShopId(), rentalFee, shopDao);
		getRentShops().add(shop);
		UltimateEconomy.getInstance.getConfig().set("RentShopIds", getRentShopIdList());
		UltimateEconomy.getInstance.saveConfig();
		return shop;
	}

	@Override
	public void deleteRentShop(Rentshop rentshop) {
		getRentShops().remove(rentshop);
		rentshop.deleteShop();
		// to make sure that all references are no more available
		rentshop = null;
		UltimateEconomy.getInstance.getConfig().set("RentShopIds", getRentShopIdList());
		UltimateEconomy.getInstance.saveConfig();
	}

	@Override
	public void despawnAllVillagers() {
		for (Rentshop shop : rentShopList) {
			shop.despawnVillager();
		}
	}

	@Override
	public void loadAllRentShops() {
		for (String shopId : UltimateEconomy.getInstance.getConfig().getStringList("RentShopIds")) {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), shopId + ".yml");
			if (file.exists()) {
				try {
					ShopDao shopDao = componentProvider.getServiceComponent().getShopDao();
					getRentShops().add(new RentshopImpl(shopId, shopDao));
				} catch (TownSystemException | EconomyPlayerException | GeneralEconomyException
						| ShopSystemException e) {
					Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
					Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
				}
			} else {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
			}
		}
	}

	@Override
	public void setupRentDailyTask() {
		Logger logger = LoggerFactory.getLogger(RentDailyTask.class.getName());
		new RentDailyTask(logger, serverProvider, this, messageWrapper)
				.runTaskTimerAsynchronously(serverProvider.getPluginInstance(), 1, 1000);
	}
}
