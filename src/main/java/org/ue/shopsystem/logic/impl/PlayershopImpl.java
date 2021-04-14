package org.ue.shopsystem.logic.impl;

import java.util.List;

import javax.inject.Inject;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.ue.common.api.CustomSkullService;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.EconomyVillager;
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
	 * @param generalValidator
	 */
	@Inject
	public PlayershopImpl(ShopDao shopDao, ServerProvider serverProvider, CustomSkullService customSkullService,
			ShopValidationHandler validationHandler, EconomyPlayerManager ecoPlayerManager,
			MessageWrapper messageWrapper, ConfigManager configManager, TownworldManager townworldManager,
			PlayershopManager playershopManager, GeneralEconomyValidationHandler generalValidator) {
		super(shopDao, serverProvider, customSkullService, validationHandler, messageWrapper, configManager,
				generalValidator);
		this.ecoPlayerManager = ecoPlayerManager;
		this.townworldManager = townworldManager;
		this.playershopManager = playershopManager;
	}

	@Override
	public void setupNew(String name, EconomyPlayer owner, String shopId, Location spawnLocation, int size) {
		super.setupNew(name, shopId, spawnLocation, size);
		setupShopOwner(owner);
		getEditorHandler().setup(2);
		setupEconomyVillagerType();
	}

	@Override
	public void setupExisting(String name, String shopId)
			throws TownSystemException, ShopSystemException, GeneralEconomyException {
		super.setupExisting(name, shopId);
		getEditorHandler().setup(2);
		loadStock();
		loadOwner(name);
		setupEconomyVillagerType();
	}

	/**
	 * Overridden, because of reserved slots. {@inheritDoc}
	 */
	@Override
	public void openSlotEditor(Player player, int slot) throws ShopSystemException, GeneralEconomyException {
		generalValidator.checkForValidSlot(slot, getSize() - 1);
		getSlotEditorHandler().setSelectedSlot(slot);
		player.openInventory(getSlotEditorHandler().getSlotEditorInventory());
	}

	/**
	 * Overridden, because of the shop owner. {@inheritDoc}
	 */
	@Override
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		generalValidator.checkForValidSlot(slot, getSize() - 1);
		validationHandler.checkForPlayerIsOnline(ecoPlayer);
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
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
			throws GeneralEconomyException, ShopSystemException {
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
		generalValidator.checkForValidSlot(slot, getSize() - 1);
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
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
	 * Overridden, because of the number of reserved slots. {@inheritDoc}
	 */
	@Override
	public void changeShopSize(int newSize)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		generalValidator.checkForValidSize(newSize);
		validationHandler.checkForResizePossible(getShopInventory(), getSize(), newSize, 2);
		setSize(newSize);
		getShopDao().saveShopSize(newSize);
		setupShopInventory();
		getEditorHandler().setup(2);
		reloadShopItems();
	}

	/**
	 * Overridden, because of the permission validation. {@inheritDoc}
	 */
	@Override
	public void moveShop(Location location) throws TownSystemException, EconomyPlayerException {
		if (townworldManager.isTownWorld(location.getWorld().getName())) {
			validationHandler.checkForPlayerHasPermissionAtLocation(location, getOwner());
		}
		super.moveShop(location);
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
		setupShopName(name);
		getShopVillager().setCustomName(name + "_" + getOwner().getName());
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
		validationHandler.checkForSlotIsEmpty(slot, getShopInventory(), 2);
		super.addShopItem(slot, sellPrice, buyPrice, itemStack);
		ShopItem item = getShopItem(slot);
		getShopDao().saveStock(item.getItemHash(), 0);
		updateItemStock(slot);
	}

	/**
	 * Overidden, because of stockpile. {@inheritDoc}
	 */
	@Override
	public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException {
		generalValidator.checkForValidSlot(slot, getSize() - 1);
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
		super.removeShopItem(slot);
	}

	@Override
	public boolean isAvailable(int slot) throws ShopSystemException, GeneralEconomyException {
		generalValidator.checkForValidSlot(slot, getSize() - 1);
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
		getShopDao().saveOwner(newOwner);
		getShopVillager().setCustomName(getName() + "_" + newOwner.getName());
	}

	@Override
	public boolean isOwner(EconomyPlayer ecoPlayer) {
		if (ecoPlayer.equals(getOwner())) {
			return true;
		}
		return false;
	}

	@Override
	public void decreaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
		generalValidator.checkForPositiveValue(stock);
		generalValidator.checkForValidSlot(slot, getSize() - 1);
		ShopItem item = getShopItem(slot);
		int entireStock = item.getStock();
		validationHandler.checkForValidStockDecrease(entireStock, stock);
		item.setStock(entireStock - stock);
		getShopDao().saveStock(item.getItemHash(), item.getStock());
		updateItemStock(slot);
	}

	@Override
	public void increaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
		generalValidator.checkForPositiveValue(stock);
		generalValidator.checkForValidSlot(slot, getSize() - 1);
		ShopItem item = getShopItem(slot);
		int entireStock = item.getStock();
		item.setStock(entireStock + stock);
		getShopDao().saveStock(item.getItemHash(), item.getStock());
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

	private void updateItemStock(int slot) throws GeneralEconomyException, ShopSystemException {
		ItemStack original = getShopInventory().getItem(slot);
		if (original != null) {
			ItemStack stack = getShopInventory().getItem(slot);
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

	private void setupEconomyVillagerType() {
		getShopVillager().setMetadata("ue-type",
				new FixedMetadataValue(serverProvider.getJavaPluginInstance(), EconomyVillager.PLAYERSHOP));
	}

	private void setupShopOwner(EconomyPlayer owner) {
		if (owner != null) {
			setOwner(owner);
			getShopDao().saveOwner(owner);
			getShopVillager().setCustomName(getName() + "_" + owner.getName());
		}
	}

	private void loadStock() {
		try {
			for (ShopItem item : getItemList()) {
				item.setStock(getShopDao().loadStock(item.getItemHash()));
				updateItemStock(item.getSlot());
			}
		} catch (ShopSystemException | GeneralEconomyException e) {
			// only rentshop
		}
	}

	private void loadOwner(String oldName) throws GeneralEconomyException {
		String owner = getShopDao().loadOwner(oldName);
		if (owner != null && !"".equals(owner)) {
			setOwner(ecoPlayerManager.getEconomyPlayerByName(owner));
			getShopVillager().setCustomName(getName() + "_" + getOwner().getName());
		}
	}
}