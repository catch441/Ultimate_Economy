package org.ue.shopsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.EconomyVillagerType;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.townsystem.logic.TownSystemException;

public class AdminshopImpl extends AbstractShopImpl implements Adminshop {

	private final AdminshopManager adminshopManager;

	/**
	 * Inject constructor.
	 * 
	 * @param shopDao
	 * @param serverProvider
	 * @param skullService
	 * @param adminshopManager
	 * @param validationHandler
	 * @param messageWrapper
	 * @param configManager
	 */
	@Inject
	public AdminshopImpl(ShopDao shopDao, ServerProvider serverProvider, CustomSkullService skullService,
			AdminshopManager adminshopManager, ShopValidationHandler validationHandler, MessageWrapper messageWrapper,
			ConfigManager configManager) {
		super(shopDao, serverProvider, skullService, validationHandler, messageWrapper, configManager);
		this.adminshopManager = adminshopManager;
	}

	@Override
	public void setupNew(String name, String shopId, Location spawnLocation, int size)
			throws GeneralEconomyException, EconomyPlayerException {
		setupNew(EconomyVillagerType.ADMINSHOP, name, shopId, spawnLocation, size, 1);
	}

	@Override
	public void setupExisting(String shopId)
			throws TownSystemException, GeneralEconomyException, EconomyPlayerException {
		setupExisting(EconomyVillagerType.ADMINSHOP, shopId, 1);
	}

	@Override
	public void changeShopName(String name) throws ShopSystemException, GeneralEconomyException {
		validationHandler.checkForValueNotInList(adminshopManager.getAdminshopNameList(), name);
		validationHandler.checkForValidShopName(name);
		this.name = name;
		shopDao.saveShopName(name);
		changeInventoryNames(name);
		getVillager().setCustomName(name);
	}

	/**
	 * Overridden, because of custom shopitem spawner name. {@inheritDoc}
	 */
	@Override
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		validationHandler.checkForPlayerIsOnline(ecoPlayer);
		validationHandler.checkForSlotIsNotEmpty(slot, getInventory());
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