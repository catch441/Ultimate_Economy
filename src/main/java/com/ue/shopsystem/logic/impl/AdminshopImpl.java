package com.ue.shopsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.slf4j.Logger;

import com.ue.common.api.CustomSkullService;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.EconomyVillager;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.Adminshop;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.impl.TownSystemException;

public class AdminshopImpl extends AbstractShopImpl implements Adminshop {

	private final AdminshopManager adminshopManager;

	/**
	 * Inject constructor.
	 * 
	 * @param shopDao
	 * @param serverProvider
	 * @param skullService
	 * @param logger
	 * @param adminshopManager
	 * @param validationHandler
	 * @param messageWrapper
	 * @param configManager
	 * @param generalValidator
	 */
	@Inject
	public AdminshopImpl(ShopDao shopDao, ServerProvider serverProvider, CustomSkullService skullService, Logger logger,
			AdminshopManager adminshopManager, ShopValidationHandler validationHandler, MessageWrapper messageWrapper,
			ConfigManager configManager, GeneralEconomyValidationHandler generalValidator) {
		super(shopDao, serverProvider, skullService, logger, validationHandler, messageWrapper, configManager,
				generalValidator);
		this.adminshopManager = adminshopManager;
	}

	@Override
	public void setupNew(String name, String shopId, Location spawnLocation, int size) {
		super.setupNew(name, shopId, spawnLocation, size);
		getShopVillager().setMetadata("ue-type",
				new FixedMetadataValue(serverProvider.getJavaPluginInstance(), EconomyVillager.ADMINSHOP));
	}

	@Override
	public void setupExisting(String name, String shopId)
			throws TownSystemException, ShopSystemException, GeneralEconomyException {
		super.setupExisting(name, shopId);
		getShopVillager().setMetadata("ue-type",
				new FixedMetadataValue(serverProvider.getJavaPluginInstance(), EconomyVillager.ADMINSHOP));
	}

	@Override
	public void changeShopName(String name) throws ShopSystemException, GeneralEconomyException {
		generalValidator.checkForValueNotInList(adminshopManager.getAdminshopNameList(), name);
		validationHandler.checkForValidShopName(name);
		setName(name);
		getShopDao().saveShopName(name);
		changeInventoryNames(name);
		getShopVillager().setCustomName(name);
	}

	/**
	 * Overridden, because of custom shopitem spawner name. {@inheritDoc}
	 */
	@Override
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		generalValidator.checkForValidSlot(slot, getSize() - 1);
		validationHandler.checkForPlayerIsOnline(ecoPlayer);
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
		validationHandler.checkForPlayerInventoryNotFull(ecoPlayer.getPlayer().getInventory());
		ShopItem shopItem = getShopItem(slot);
		if (shopItem.getBuyPrice() != 0.0) {
			// if player has not enough money, then the decrease method throws a
			// playerexception
			ecoPlayer.decreasePlayerAmount(shopItem.getBuyPrice(), true);
			ItemStack stack = shopItem.getItemStack();
			stack.setAmount(shopItem.getAmount());
			if (stack.getType() == Material.SPAWNER) {
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(meta.getDisplayName() + "-" + ecoPlayer.getName());
				stack.setItemMeta(meta);
			}
			ecoPlayer.getPlayer().getInventory().addItem(stack);
			if (sendMessage) {
				if (shopItem.getAmount() > 1) {
					ecoPlayer.getPlayer()
							.sendMessage(messageWrapper.getString("shop_buy_plural",
									String.valueOf(shopItem.getAmount()), shopItem.getBuyPrice(),
									configManager.getCurrencyText(shopItem.getBuyPrice())));
				} else {
					ecoPlayer.getPlayer()
							.sendMessage(messageWrapper.getString("shop_buy_singular",
									String.valueOf(shopItem.getAmount()), shopItem.getBuyPrice(),
									configManager.getCurrencyText(shopItem.getBuyPrice())));
				}
			}
		}
	}
}