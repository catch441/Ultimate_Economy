package com.ue.shopsystem.logic.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.ue.common.utils.MessageWrapper;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.logic.api.Adminshop;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;
import com.ue.ultimate_economy.UltimateEconomy;

public class AdminshopManagerImpl implements AdminshopManager {

	private List<Adminshop> adminShopList = new ArrayList<>();
	private final MessageWrapper messageWrapper;
	private final ShopValidationHandler validationHandler;

	/**
	 * Inject constructor.
	 * 
	 * @param validationHandler
	 * @param messageWrapper
	 */
	@Inject
	public AdminshopManagerImpl(ShopValidationHandler validationHandler, MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
		this.validationHandler = validationHandler;
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
		return adminShopList;
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
		getAdminshopList().add(new AdminshopImpl(name, generateFreeAdminShopId(), spawnLocation, size));
		UltimateEconomy.getInstance.getConfig().set("AdminShopIds", getAdminshopIdList());
		UltimateEconomy.getInstance.saveConfig();
	}

	@Override
	public void deleteAdminShop(Adminshop adminshop) throws ShopSystemException {
		getAdminshopList().remove(adminshop);
		adminshop.deleteShop();
		// to make sure that all references are no more available
		adminshop = null;
		UltimateEconomy.getInstance.getConfig().set("AdminShopIds", getAdminshopIdList());
		UltimateEconomy.getInstance.saveConfig();
	}

	@Override
	public void despawnAllVillagers() {
		for (Adminshop shop : getAdminshopList()) {
			shop.despawnVillager();
		}
	}

	@Override
	public void loadAllAdminShops() {
		// old load system, can be deleted in the future
		if (UltimateEconomy.getInstance.getConfig().contains("ShopNames")) {
			loadAllAdminshopsOld();
		}
		// new load system
		else {
			loadAllAdminshopsNew();
		}
	}

	private void loadAllAdminshopsNew() {
		// renaming, can be deleted later
		if (UltimateEconomy.getInstance.getConfig().contains("AdminshopIds")) {
			UltimateEconomy.getInstance.getConfig().set("AdminShopIds",
					UltimateEconomy.getInstance.getConfig().get("AdminshopIds"));
			UltimateEconomy.getInstance.getConfig().set("AdminshopIds", null);
			UltimateEconomy.getInstance.saveConfig();
		}

		for (String shopId : UltimateEconomy.getInstance.getConfig().getStringList("AdminShopIds")) {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), shopId + ".yml");
			if (file.exists()) {
				try {
					getAdminshopList().add(new AdminshopImpl(null, shopId));
				} catch (TownSystemException e) {
					Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
					Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
				}
			} else {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopId);
			}
		}
	}

	@Deprecated
	private void loadAllAdminshopsOld() {
		for (String shopName : UltimateEconomy.getInstance.getConfig().getStringList("ShopNames")) {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), shopName + ".yml");
			if (file.exists()) {
				try {
					getAdminshopList().add(new AdminshopImpl(shopName, generateFreeAdminShopId()));
				} catch (TownSystemException e) {
					Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopName);
					Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
				}
			} else {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the shop " + shopName);
			}
		}
		// convert to new shopId save system
		UltimateEconomy.getInstance.getConfig().set("ShopNames", null);
		UltimateEconomy.getInstance.getConfig().set("AdminShopIds", getAdminshopIdList());
		UltimateEconomy.getInstance.saveConfig();
	}
}
