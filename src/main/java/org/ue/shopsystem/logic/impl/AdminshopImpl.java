package org.ue.shopsystem.logic.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;

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
	public AdminshopImpl(ShopDao shopDao, ServerProvider serverProvider, CustomSkullService skullService,
			AdminshopManager adminshopManager, ShopValidator validationHandler, MessageWrapper messageWrapper,
			ConfigManager configManager) {
		super(shopDao, serverProvider, skullService, validationHandler, messageWrapper, configManager);
		this.adminshopManager = adminshopManager;
	}

	@Override
	public void setupNew(String name, String shopId, Location spawnLocation, int size) {
		setupNew(EconomyVillagerType.ADMINSHOP, name, shopId, spawnLocation, size, 1);
	}

	@Override
	public void setupExisting(String shopId) {
		setupExisting(EconomyVillagerType.ADMINSHOP, shopId, 1);
	}

	@Override
	public void changeShopName(String name) throws ShopsystemException {
		validationHandler.checkForValueNotInList(adminshopManager.getAdminshopNameList(), name);
		validationHandler.checkForValidShopName(name);
		this.name = name;
		shopDao.saveShopName(name);
		changeInventoryName(name);
		getVillager().setCustomName(name);
	}

	/**
	 * Overridden, because of custom shopitem spawner name. {@inheritDoc}
	 * 
	 * @throws EconomyPlayerException
	 * @throws BankException
	 */
	@Override
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws ShopsystemException, BankException, EconomyPlayerException {
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		validationHandler.checkForPlayerIsOnline(ecoPlayer);
		validationHandler.checkForSlotIsNotEmpty(getOccupiedSlots(), slot);
		validationHandler.checkForPlayerInventoryNotFull(ecoPlayer.getPlayer().getInventory());
		ShopItem shopItem = getShopItem(slot);
		if (shopItem.getBuyPrice() != 0.0) {
			// enough money check is performed inside the decrease method
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
							.sendMessage(messageWrapper.getString(MessageEnum.SHOP_BUY_PLURAL,
									String.valueOf(shopItem.getAmount()), shopItem.getBuyPrice(),
									configManager.getCurrencyText(shopItem.getBuyPrice())));
				} else {
					ecoPlayer.getPlayer()
							.sendMessage(messageWrapper.getString(MessageEnum.SHOP_BUY_SINGULAR,
									String.valueOf(shopItem.getAmount()), shopItem.getBuyPrice(),
									configManager.getCurrencyText(shopItem.getBuyPrice())));
				}
			}
		}
	}
}