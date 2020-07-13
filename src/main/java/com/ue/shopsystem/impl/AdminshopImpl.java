package com.ue.shopsystem.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.Adminshop;
import com.ue.shopsystem.api.AdminshopController;
import com.ue.ultimate_economy.UltimateEconomy;

public class AdminshopImpl extends AbstractShopImpl implements Adminshop {

	/**
	 * Constructor for creating a new adminShop. No validation, if the shopId is
	 * unique.
	 * 
	 * @param name
	 * @param shopId
	 * @param spawnLocation
	 * @param size
	 */
	public AdminshopImpl(String name, String shopId, Location spawnLocation, int size) {
		super(name, shopId, spawnLocation, size);
		getShopVillager().setMetadata("ue-type",
				new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.ADMINSHOP));
	}

	/**
	 * Constructor for loading an existing adminShop. No validation, if the shopId
	 * is unique. If name != null then use old loading otherwise use new loading
	 * 
	 * @param name
	 * @param shopId
	 * @throws TownSystemException
	 */
	public AdminshopImpl(String name, String shopId) throws TownSystemException {
		super(name, shopId);
		getShopVillager().setMetadata("ue-type",
				new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.ADMINSHOP));
	}

	/*
	 * API methods
	 * 
	 */

	@Override
	public void changeShopName(String name) throws ShopSystemException, GeneralEconomyException {
		checkForShopNameDoesNotExist(name);
		ShopValidationHandler.checkForValidShopName(name);
		setName(name);
		getSavefileHandler().saveShopName(name);
		changeInventoryNames(name);
		getShopVillager().setCustomName(name);
	}

	/**
	 * Overridden, because of custom shopitem spawner name. {@inheritDoc}
	 */
	@Override
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, PlayerException, ShopSystemException {
		getValidationHandler().checkForValidSlot(slot, getSize(), 1);
		getValidationHandler().checkForPlayerIsOnline(ecoPlayer);
		getValidationHandler().checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
		getValidationHandler().checkForPlayerInventoryNotFull(ecoPlayer.getPlayer().getInventory());
		ShopItem shopItem = getShopItem(slot);
		// if player has not enough money, then the decrease method throws a
		// playerexception
		ecoPlayer.decreasePlayerAmount(shopItem.getBuyPrice(), true);
		ItemStack stack = shopItem.getItemStack().clone();
		if(stack.getType() == Material.SPAWNER) {
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(meta.getDisplayName() + "-" + ecoPlayer.getName());
			stack.setItemMeta(meta);
		}
		ecoPlayer.getPlayer().getInventory().addItem(stack);
		if (sendMessage) {
			if (shopItem.getAmount() > 1) {
				ecoPlayer.getPlayer()
						.sendMessage(MessageWrapper.getString("shop_buy_plural", String.valueOf(shopItem.getAmount()),
								shopItem.getBuyPrice(), ConfigController.getCurrencyText(shopItem.getBuyPrice())));
			} else {
				ecoPlayer.getPlayer()
						.sendMessage(MessageWrapper.getString("shop_buy_singular", String.valueOf(shopItem.getAmount()),
								shopItem.getBuyPrice(), ConfigController.getCurrencyText(shopItem.getBuyPrice())));
			}
		}
	}

	/*
	 * Validation check methods
	 * 
	 */

	private void checkForShopNameDoesNotExist(String name) throws GeneralEconomyException {
		if (AdminshopController.getAdminshopNameList().contains(name)) {
			throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, name);
		}
	}
}