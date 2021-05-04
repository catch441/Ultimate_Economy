package org.ue.shopsystem.logic.impl;

import java.util.List;

import javax.inject.Inject;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.EconomyVillagerType;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.shopsystem.logic.api.ShopsystemException;
import org.ue.townsystem.logic.api.TownworldManager;

public class PlayershopImpl extends AbstractShopImpl implements Playershop {

	protected final EconomyPlayerManager ecoPlayerManager;
	protected final TownworldManager townworldManager;
	protected final PlayershopManager playershopManager;
	private EconomyPlayer owner;

	/**
	 * Inject constructor.
	 * 
	 * @param shopDao
	 * @param serverProvider
	 * @param customSkullService
	 * @param validationHandler
	 * @param ecoPlayerManager
	 * @param messageWrapper
	 * @param configManager
	 * @param townworldManager
	 * @param playershopManager
	 */
	@Inject
	public PlayershopImpl(ShopDao shopDao, ServerProvider serverProvider, CustomSkullService customSkullService,
			ShopValidationHandler validationHandler, EconomyPlayerManager ecoPlayerManager,
			MessageWrapper messageWrapper, ConfigManager configManager, TownworldManager townworldManager,
			PlayershopManager playershopManager) {
		super(shopDao, serverProvider, customSkullService, validationHandler, messageWrapper, configManager);
		this.ecoPlayerManager = ecoPlayerManager;
		this.townworldManager = townworldManager;
		this.playershopManager = playershopManager;
	}

	@Override
	public void setupNew(String name, EconomyPlayer owner, String shopId, Location spawnLocation, int size) {
		setupNew(EconomyVillagerType.PLAYERSHOP, name, shopId, spawnLocation, size, 1);
		setupShopOwner(owner);
		getEditorHandler().setup(1);
	}

	@Override
	public void setupExisting(String shopId) throws EconomyPlayerException {
		setupExisting(EconomyVillagerType.PLAYERSHOP, shopId, 1);
		getEditorHandler().setup(1);
		loadStock();
		loadOwner();
	}

	/**
	 * Overridden, because of the shop owner. {@inheritDoc}
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
		int entireStock = shopItem.getStock();
		if (isOwner(ecoPlayer)) {
			buyItemAsOwner(slot, ecoPlayer, sendMessage, shopItem);
		} else if (shopItem.getBuyPrice() != 0.0) {
			validationHandler.checkForValidStockDecrease(entireStock, shopItem.getAmount());
			// if player has not enough money, then the decrease method throws a
			// playerexception
			ecoPlayer.decreasePlayerAmount(shopItem.getBuyPrice(), true);
			getOwner().increasePlayerAmount(shopItem.getBuyPrice(), false);
			ItemStack stack = shopItem.getItemStack();
			stack.setAmount(shopItem.getAmount());
			ecoPlayer.getPlayer().getInventory().addItem(stack);
			decreaseStock(slot, shopItem.getAmount());
			if (sendMessage) {
				sendBuySellPlayerMessage(shopItem.getAmount(), ecoPlayer, shopItem.getBuyPrice(), "buy");
			}
		}
	}

	private void buyItemAsOwner(int slot, EconomyPlayer ecoPlayer, boolean sendMessage, ShopItem shopItem)
			throws ShopsystemException {
		ItemStack stack = shopItem.getItemStack();
		int amount = shopItem.getAmount();
		if (shopItem.getStock() < shopItem.getAmount()) {
			amount = shopItem.getStock();
		}
		validationHandler.checkForValidStockDecrease(shopItem.getStock(), amount);
		stack.setAmount(amount);
		ecoPlayer.getPlayer().getInventory().addItem(stack);
		decreaseStock(slot, amount);
		if (sendMessage) {
			sendBuySellOwnerMessage(amount, "got");
		}
	}

	/**
	 * Overridden, because of the shop owner. {@inheritDoc}
	 * 
	 * @throws EconomyPlayerException
	 * @throws BankException
	 */
	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws ShopsystemException, BankException, EconomyPlayerException {
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(getOccupiedSlots(), slot);
		validationHandler.checkForPlayerIsOnline(ecoPlayer);
		ShopItem shopItem = getShopItem(slot);
		double sellPrice = shopItem.getSellPrice() / shopItem.getAmount() * amount;
		if (isOwner(ecoPlayer)) {
			increaseStock(slot, amount);
			removeItemFromInventory(ecoPlayer.getPlayer().getInventory(), shopItem.getItemStack(), amount);
			if (sendMessage) {
				sendBuySellOwnerMessage(amount, "added");
			}
		} else if (shopItem.getSellPrice() != 0.0) {
			validationHandler.checkForShopOwnerHasEnoughMoney(getOwner(), sellPrice);
			ecoPlayer.increasePlayerAmount(sellPrice, false);
			getOwner().decreasePlayerAmount(sellPrice, true);
			increaseStock(slot, amount);
			removeItemFromInventory(ecoPlayer.getPlayer().getInventory(), shopItem.getItemStack(), amount);
			sendBuySellPlayerMessage(amount, ecoPlayer, sellPrice, "sell");
		}
	}

	/**
	 * Overridden, because of the permission validation. {@inheritDoc}
	 */
	@Override
	public void changeLocation(Location location) throws ShopsystemException {
		if (townworldManager.isTownWorld(location.getWorld().getName())) {
			validationHandler.checkForPlayerHasPermissionAtLocation(location, getOwner());
		}
		super.changeLocation(location);
	}

	protected void superChangeLocation(Location location) {
		try {
			super.changeLocation(location);
		} catch (ShopsystemException e) {
		}
	}

	/**
	 * Overridden, because of the naming convention. {@inheritDoc}
	 * 
	 */
	@Override
	public void changeShopName(String name) throws ShopsystemException {
		validationHandler.checkForShopNameIsFree(playershopManager.getPlayerShopUniqueNameList(), name, getOwner());
		validationHandler.checkForValidShopName(name);
		this.name = name;
		shopDao.saveShopName(name);
		String newName = name;
		if (getOwner() != null) {
			newName += "_" + getOwner().getName();
		}
		getVillager().setCustomName(newName);
		changeInventoryNames(name);
	}

	@Override
	public EconomyPlayer getOwner() {
		return owner;
	}

	/**
	 * Overridden, because of the stock value. {@inheritDoc}
	 */
	@Override
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
			throws ShopsystemException {
		super.addShopItem(slot, sellPrice, buyPrice, itemStack);
		ShopItem item = getShopItem(slot);
		shopDao.saveStock(item.getSlot(), 0);
		updateItemStock(slot);
	}

	@Override
	public boolean isAvailable(int slot) throws ShopsystemException {
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		ShopItem item = getShopItem(slot);
		if (item.getStock() >= item.getAmount()) {
			return true;
		}
		return false;
	}

	@Override
	public void changeOwner(EconomyPlayer newOwner) throws ShopsystemException {
		validationHandler.checkForChangeOwnerIsPossible(playershopManager.getPlayerShopUniqueNameList(), newOwner,
				getName());
		setOwner(newOwner);
		shopDao.saveOwner(newOwner);
		getVillager().setCustomName(getName() + "_" + newOwner.getName());
	}

	@Override
	public boolean isOwner(EconomyPlayer ecoPlayer) {
		if (ecoPlayer.equals(getOwner())) {
			return true;
		}
		return false;
	}

	@Override
	public void decreaseStock(int slot, int stock) throws ShopsystemException {
		validationHandler.checkForPositiveValue(Double.valueOf(stock));
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(getOccupiedSlots(), slot);
		ShopItem item = getShopItem(slot);
		int entireStock = item.getStock();
		validationHandler.checkForValidStockDecrease(entireStock, stock);
		item.setStock(entireStock - stock);
		shopDao.saveStock(item.getSlot(), item.getStock());
		updateItemStock(slot);
	}

	@Override
	public void increaseStock(int slot, int stock) throws ShopsystemException {
		validationHandler.checkForPositiveValue(Double.valueOf(stock));
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(getOccupiedSlots(), slot);
		ShopItem item = getShopItem(slot);
		int entireStock = item.getStock();
		item.setStock(entireStock + stock);
		shopDao.saveStock(item.getSlot(), item.getStock());
		updateItemStock(slot);
	}

	private void sendBuySellOwnerMessage(int amount, String gotAdded) {
		if (amount > 1) {
			getOwner().getPlayer()
					.sendMessage(messageWrapper.getString("shop_" + gotAdded + "_item_plural", String.valueOf(amount)));
		} else {
			getOwner().getPlayer().sendMessage(
					messageWrapper.getString("shop_" + gotAdded + "_item_singular", String.valueOf(amount)));
		}
	}

	private void updateItemStock(int slot) throws ShopsystemException {
		ItemStack original = getInventory().getItem(slot);
		if (original != null) {
			original = getInventory().getItem(slot);

			int stock = getShopItem(slot).getStock();
			ItemMeta meta = original.getItemMeta();
			List<String> list = removeStockFromLore(meta.getLore());
			if (stock != 1) {
				list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Items");
			} else {
				list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Item");
			}
			meta.setLore(list);
			original.setItemMeta(meta);
		}
	}

	private List<String> removeStockFromLore(List<String> lore) {
		int stockIndex = -1;
		for (String element : lore) {
			if (element.contains("§6 Item")) {
				stockIndex = lore.indexOf(element);
			}
		}
		if (stockIndex != -1) {
			lore.remove(stockIndex);
		}
		return lore;
	}

	protected void setOwner(EconomyPlayer owner) {
		this.owner = owner;
	}

	protected void loadStock() {
		try {
			for (ShopItem item : getItemList()) {
				item.setStock(shopDao.loadStock(item.getSlot()));
				updateItemStock(item.getSlot());
			}
		} catch (ShopsystemException e) {
			// only rentshop
		}
	}

	protected void loadOwner() throws EconomyPlayerException {
		String owner = shopDao.loadOwner();
		if (owner != null && !"".equals(owner)) {
			setOwner(ecoPlayerManager.getEconomyPlayerByName(owner));
			getVillager().setCustomName(getName() + "_" + getOwner().getName());
		}
	}

	private void setupShopOwner(EconomyPlayer owner) {
		setOwner(owner);
		shopDao.saveOwner(owner);
		getVillager().setCustomName(getName() + "_" + owner.getName());
	}
}