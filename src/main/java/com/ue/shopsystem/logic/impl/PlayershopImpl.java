package com.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.slf4j.Logger;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.EconomyVillager;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.townsystem.logic.impl.TownSystemException;

public class PlayershopImpl extends AbstractShopImpl implements Playershop {

	protected final EconomyPlayerManager ecoPlayerManager;
	protected final TownworldManager townworldManager;
	protected final PlayershopManager playershopManager;
	private EconomyPlayer owner;
	private Inventory stockPile;

	/**
	 * Constructor for creating a new playershop. No validation, if the shopId is
	 * unique.
	 * 
	 * @param name
	 * @param owner
	 * @param shopId
	 * @param spawnLocation
	 * @param size
	 * @param shopDao
	 * @param serverProvider
	 * @param customSkullService
	 * @param logger
	 * @param validationHandler
	 * @param ecoPlayerManager
	 * @param messageWrapper
	 * @param configManager
	 * @param townworldManager
	 * @param playershopManager
	 * @param generalValidator
	 */
	public PlayershopImpl(String name, EconomyPlayer owner, String shopId, Location spawnLocation, int size,
			ShopDao shopDao, ServerProvider serverProvider, CustomSkullService customSkullService, Logger logger,
			ShopValidationHandler validationHandler, EconomyPlayerManager ecoPlayerManager,
			MessageWrapper messageWrapper, ConfigManager configManager, TownworldManager townworldManager,
			PlayershopManager playershopManager, GeneralEconomyValidationHandler generalValidator) {
		super(name, shopId, spawnLocation, size, shopDao, serverProvider, customSkullService, logger, validationHandler,
				messageWrapper, configManager, generalValidator);
		this.ecoPlayerManager = ecoPlayerManager;
		this.townworldManager = townworldManager;
		this.playershopManager = playershopManager;
		setupPlayerShop(owner);
	}

	/**
	 * Constructor for loading an existing playershop. No validation, if the shopId
	 * is unique. If name != null then use old loading otherwise use new loading.
	 * 
	 * @param name               deprecated
	 * @param shopId
	 * @param shopDao
	 * @param serverProvider
	 * @param customSkullService
	 * @param logger
	 * @param validationHandler
	 * @param ecoPlayerManager
	 * @param messageWrapper
	 * @param configManager
	 * @param townworldManager
	 * @param playershopManager
	 * @param generalValidator
	 * @throws TownSystemException
	 * @throws EconomyPlayerException
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 */
	public PlayershopImpl(String name, String shopId, ShopDao shopDao, ServerProvider serverProvider,
			CustomSkullService customSkullService, Logger logger, ShopValidationHandler validationHandler,
			EconomyPlayerManager ecoPlayerManager, MessageWrapper messageWrapper, ConfigManager configManager,
			TownworldManager townworldManager, PlayershopManager playershopManager,
			GeneralEconomyValidationHandler generalValidator)
			throws TownSystemException, EconomyPlayerException, GeneralEconomyException, ShopSystemException {
		super(name, shopId, shopDao, serverProvider, customSkullService, logger, validationHandler, messageWrapper,
				configManager, generalValidator);
		this.ecoPlayerManager = ecoPlayerManager;
		this.townworldManager = townworldManager;
		this.playershopManager = playershopManager;
		loadExistingPlayerShop(name);
	}

	@Override
	public void openStockpile(Player player) throws ShopSystemException {
		player.openInventory(getStockpileInventory());
	}

	/**
	 * Overridden, because of reserved slots. {@inheritDoc}
	 */
	@Override
	public void openSlotEditor(Player player, int slot) throws ShopSystemException, GeneralEconomyException {
		generalValidator.checkForValidSlot(slot, getSize() - 2);
		getSlotEditorHandler().setSelectedSlot(slot);
		player.openInventory(getSlotEditorHandler().getSlotEditorInventory());
	}

	/**
	 * Overridden, because of the shop owner. {@inheritDoc}
	 */
	@Override
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		generalValidator.checkForValidSlot(slot, getSize() - 2);
		validationHandler.checkForPlayerIsOnline(ecoPlayer);
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 2);
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
		generalValidator.checkForValidSlot(slot, getSize() - 2);
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 2);
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
		setupShopInvDefaultStockItem();
		getEditorHandler().setup(2);
		reloadShopItems();
		loadStockpile();
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

		setupStockpile();
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
		getShopDao().saveStock(item.getItemString(), 0);
		updateItemInStockpile(slot);
	}

	/**
	 * Overidden, because of stockpile. {@inheritDoc}
	 */
	@Override
	public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException {
		generalValidator.checkForValidSlot(slot, getSize() - 2);
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 2);
		super.removeShopItem(slot);
		updateItemInStockpile(slot);
	}

	@Override
	public boolean isAvailable(int slot) throws ShopSystemException, GeneralEconomyException {
		generalValidator.checkForValidSlot(slot, getSize() - 2);
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
		generalValidator.checkForValidSlot(slot, getSize() - 2);
		ShopItem item = getShopItem(slot);
		int entireStock = item.getStock();
		validationHandler.checkForValidStockDecrease(entireStock, stock);
		item.setStock(entireStock - stock);
		getShopDao().saveStock(item.getItemString(), item.getStock());
		updateItemInStockpile(slot);
	}

	@Override
	public void increaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
		generalValidator.checkForPositiveValue(stock);
		generalValidator.checkForValidSlot(slot, getSize() - 2);
		ShopItem item = getShopItem(slot);
		int entireStock = item.getStock();
		item.setStock(entireStock + stock);
		getShopDao().saveStock(item.getItemString(), item.getStock());
		updateItemInStockpile(slot);
	}

	@Override
	public Inventory getStockpileInventory() throws ShopSystemException {
		return stockPile;
	}

	/*
	 * Utility methods
	 * 
	 */

	private void sendBuySellOwnerMessage(int amount, String gotAdded) {
		if (amount > 1) {
			getOwner().getPlayer()
					.sendMessage(messageWrapper.getString("shop_" + gotAdded + "_item_plural", String.valueOf(amount)));
		} else {
			getOwner().getPlayer().sendMessage(
					messageWrapper.getString("shop_" + gotAdded + "_item_singular", String.valueOf(amount)));
		}
	}

	private void updateItemInStockpile(int slot) throws GeneralEconomyException, ShopSystemException {
		ItemStack original = getShopInventory().getItem(slot);
		if (original != null && original.getType() != Material.AIR) {
			ItemStack stack = getShopInventory().getItem(slot).clone();
			int stock = getShopItem(slot).getStock();
			ItemMeta meta = stack.getItemMeta();
			List<String> list = removeShopItemPriceLore(meta.getLore());
			if (stock != 1) {
				list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Items");
			} else {
				list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Item");
			}
			meta.setLore(list);
			stack.setItemMeta(meta);
			getStockpileInventory().setItem(slot, stack);
		} else {
			getStockpileInventory().clear(slot);
		}
	}

	protected void setOwner(EconomyPlayer owner) {
		this.owner = owner;
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupPlayerShop(EconomyPlayer owner) {
		setupShopInvDefaultStockItem();
		setupShopOwner(owner);
		getEditorHandler().setup(2);
		setupStockpile();
		setupEconomyVillagerType();
	}

	protected void setupStockpile() {
		stockPile = serverProvider.createInventory(getShopVillager(), getSize(), getName() + "-Stock");
		ItemStack stockpileSwitchItem = serverProvider.createItemStack(Material.CRAFTING_TABLE, 1);
		ItemMeta meta = stockpileSwitchItem.getItemMeta();
		List<String> infos = new ArrayList<>();
		infos.add(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "close stockpile");
		infos.add(ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "add specified amount");
		infos.add(ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "add all");
		infos.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "get specified amount");
		meta.setLore(infos);
		meta.setDisplayName("Infos");
		stockpileSwitchItem.setItemMeta(meta);
		try {
			getStockpileInventory().setItem(getSize() - 1, stockpileSwitchItem);
		} catch (ShopSystemException e) {
			// only rentshop
		}
	}

	private void setupEconomyVillagerType() {
		getShopVillager().setMetadata("ue-type",
				new FixedMetadataValue(serverProvider.getJavaPluginInstance(), EconomyVillager.PLAYERSHOP));
	}

	private void setupShopInvDefaultStockItem() {
		ItemStack itemStack = serverProvider.createItemStack(Material.CRAFTING_TABLE, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName("Stock");
		itemStack.setItemMeta(meta);
		addShopItemToInv(itemStack, 1, getSize() - 2, 0.0, 0.0);
	}

	private void setupShopOwner(EconomyPlayer owner) {
		if (owner != null) {
			setOwner(owner);
			getShopDao().saveOwner(owner);
			getShopVillager().setCustomName(getName() + "_" + owner.getName());
		}
	}

	/*
	 * Loading methods
	 * 
	 */

	private void loadExistingPlayerShop(String name)
			throws EconomyPlayerException, GeneralEconomyException, ShopSystemException {
		setupShopInvDefaultStockItem();
		getEditorHandler().setup(2);
		loadStock();
		loadOwner(name);
		loadStockpile();
		setupEconomyVillagerType();
	}

	private void loadStock() {
		try {
			for (ShopItem item : getItemList()) {
				item.setStock(getShopDao().loadStock(item.getItemString()));
			}
		} catch (ShopSystemException e) {
			// only rentshop
		}
	}

	private void loadStockpile() throws GeneralEconomyException, ShopSystemException {
		setupStockpile();
		for (int i = 0; i < (getSize() - 2); i++) {
			updateItemInStockpile(i);
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