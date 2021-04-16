package org.ue.shopsystem.logic.impl;

import java.util.List;

import javax.inject.Inject;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.EconomyVillagerType;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.townsystem.logic.api.TownworldManager;
import org.ue.townsystem.logic.TownSystemException;

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
	public void setupNew(String name, EconomyPlayer owner, String shopId, Location spawnLocation, int size)
			throws GeneralEconomyException, EconomyPlayerException {
		setupNew(EconomyVillagerType.PLAYERSHOP, name, shopId, spawnLocation, size, 1);
		setupShopOwner(owner);
		getEditorHandler().setup(2);
	}

	@Override
	public void setupExisting(String shopId)
			throws TownSystemException, GeneralEconomyException, EconomyPlayerException {
		setupExisting(EconomyVillagerType.PLAYERSHOP, shopId, 1);
		getEditorHandler().setup(1);
		loadStock();
		loadOwner();
	}

	/**
	 * Overridden, because of reserved slots. {@inheritDoc}
	 */
	@Override
	public void openSlotEditor(Player player, int slot) throws ShopSystemException, GeneralEconomyException {
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		getSlotEditorHandler().setSelectedSlot(slot);
		player.openInventory(getSlotEditorHandler().getSlotEditorInventory());
	}

	/**
	 * Overridden, because of the shop owner. {@inheritDoc}
	 */
	@Override
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		validationHandler.checkForPlayerIsOnline(ecoPlayer);
		validationHandler.checkForSlotIsNotEmpty(slot, getInventory());
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
			throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
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
	 */
	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(slot, getInventory());
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
	 * 
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 */
	@Override
	public void changeLocation(Location location) throws EconomyPlayerException, TownSystemException {
		if (townworldManager.isTownWorld(location.getWorld().getName())) {
			validationHandler.checkForPlayerHasPermissionAtLocation(location, getOwner());
		}
		super.changeLocation(location);
	}
	
	protected void superChangeLocation(Location location) throws EconomyPlayerException, TownSystemException {
		super.changeLocation(location);
	}

	/**
	 * Overridden, because of the naming convention. {@inheritDoc}
	 * 
	 * @throws GeneralEconomyException
	 */
	@Override
	public void changeShopName(String name) throws ShopSystemException, GeneralEconomyException {
		validationHandler.checkForShopNameIsFree(playershopManager.getPlayerShopUniqueNameList(), name, getOwner());
		validationHandler.checkForValidShopName(name);
		this.name = name;
		shopDao.saveShopName(name);
		getVillager().setCustomName(name + "_" + getOwner().getName());
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
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		validationHandler.checkForSlotIsEmpty(getInventory(), slot);
		super.addShopItem(slot, sellPrice, buyPrice, itemStack);
		ShopItem item = getShopItem(slot);
		shopDao.saveStock(item.getItemHash(), 0);
		updateItemStock(slot);
	}

	/**
	 * Overridden, because of stockpile. {@inheritDoc}
	 * 
	 */
	@Override
	public void removeShopItem(int slot) throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(slot, getInventory());
		super.removeShopItem(slot);
	}

	@Override
	public boolean isAvailable(int slot) throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		ShopItem item = getShopItem(slot);
		if (item.getStock() >= item.getAmount()) {
			return true;
		}
		return false;
	}

	@Override
	public void changeOwner(EconomyPlayer newOwner) throws EconomyPlayerException, ShopSystemException {
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
	public void decreaseStock(int slot, int stock)
			throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		validationHandler.checkForPositiveValue(stock);
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		ShopItem item = getShopItem(slot);
		int entireStock = item.getStock();
		validationHandler.checkForValidStockDecrease(entireStock, stock);
		item.setStock(entireStock - stock);
		shopDao.saveStock(item.getItemHash(), item.getStock());
		updateItemStock(slot);
	}

	@Override
	public void increaseStock(int slot, int stock)
			throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		validationHandler.checkForPositiveValue(stock);
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		ShopItem item = getShopItem(slot);
		int entireStock = item.getStock();
		item.setStock(entireStock + stock);
		shopDao.saveStock(item.getItemHash(), item.getStock());
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

	private void updateItemStock(int slot) throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		ItemStack original = getInventory().getItem(slot);
		if (original != null) {
			ItemStack stack = getInventory().getItem(slot);
			int stock = getShopItem(slot).getStock();
			ItemMeta meta = stack.getItemMeta();
			List<String> list = removeStockFromLore(meta.getLore());
			if (stock != 1) {
				list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Items");
			} else {
				list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Item");
			}
			meta.setLore(list);
			stack.setItemMeta(meta);
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
				item.setStock(shopDao.loadStock(item.getItemHash()));
				updateItemStock(item.getSlot());
			}
		} catch (ShopSystemException | GeneralEconomyException | EconomyPlayerException e) {
			// only rentshop
		}
	}

	protected void loadOwner() throws GeneralEconomyException {
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