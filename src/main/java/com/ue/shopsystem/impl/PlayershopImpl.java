package com.ue.shopsystem.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.Playershop;
import com.ue.townsystem.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

public class PlayershopImpl extends AbstractShopImpl implements Playershop {

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
	 */
	public PlayershopImpl(String name, EconomyPlayer owner, String shopId, Location spawnLocation, int size) {
		super(name, shopId, spawnLocation, size);
		setupPlayerShop(owner);
	}

	/**
	 * Constructor for loading an existing playershop. No validation, if the shopId
	 * is unique. If name != null then use old loading otherwise use new loading.
	 * 
	 * @param name   deprecated
	 * @param shopId
	 * @throws TownSystemException
	 * @throws PlayerException
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 */
	public PlayershopImpl(String name, String shopId)
			throws TownSystemException, PlayerException, GeneralEconomyException, ShopSystemException {
		super(name, shopId);
		loadExistingPlayerShop(name);
	}

	/*
	 * API methods
	 * 
	 */
	@Override
	public void openStockpile(Player player) throws ShopSystemException {
		player.openInventory(getStockpileInventory());
	}

	/**
	 * Overridden, because of the shop owner. {@inheritDoc}
	 */
	@Override
	public void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, PlayerException, ShopSystemException {
		super.buyShopItem(slot, ecoPlayer, false);
		ShopItem shopItem = getShopItem(slot);
		// throws an error, if stock is not available.
		decreaseStock(slot, shopItem.getAmount());
		if(!isOwner(ecoPlayer)) {
			getOwner().increasePlayerAmount(shopItem.getBuyPrice(), false);
		}
		if (sendMessage) {
			if (isOwner(ecoPlayer)) {
				if (shopItem.getAmount() > 1) {
					getOwner().getPlayer().sendMessage(
							MessageWrapper.getString("shop_got_item_plural", String.valueOf(shopItem.getAmount())));
				} else {
					getOwner().getPlayer().sendMessage(
							MessageWrapper.getString("shop_got_item_singular", String.valueOf(shopItem.getAmount())));
				}
			} else {
				if (shopItem.getAmount() > 1) {
					ecoPlayer.getPlayer()
							.sendMessage(MessageWrapper.getString("shop_buy_plural",
									String.valueOf(shopItem.getAmount()), shopItem.getBuyPrice(),
									ConfigController.getCurrencyText(shopItem.getBuyPrice())));
				} else {
					ecoPlayer.getPlayer()
							.sendMessage(MessageWrapper.getString("shop_buy_singular",
									String.valueOf(shopItem.getAmount()), shopItem.getBuyPrice(),
									ConfigController.getCurrencyText(shopItem.getBuyPrice())));
				}
			}
		}
	}
	
	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, ShopSystemException, PlayerException {
		ShopItem shopItem = getShopItem(slot);
		double sellPrice = shopItem.getSellPrice() / shopItem.getAmount() * amount;
		getValidationHandler().checkForShopOwnerHasEnoughMoney(getOwner(), sellPrice);
		super.sellShopItem(slot, amount, ecoPlayer, false);
		increaseStock(slot, amount);
		if(!isOwner(ecoPlayer)) {
			getOwner().decreasePlayerAmount(sellPrice, false);
		}
		if (sendMessage) {
			if (isOwner(ecoPlayer)) {
				if (shopItem.getAmount() > 1) {
					getOwner().getPlayer().sendMessage(
							MessageWrapper.getString("shop_added_item_plural", String.valueOf(shopItem.getAmount())));
				} else {
					getOwner().getPlayer().sendMessage(
							MessageWrapper.getString("shop_added_item_singular", String.valueOf(shopItem.getAmount())));
				}
			} else {
				if (shopItem.getAmount() > 1) {
					ecoPlayer.getPlayer()
							.sendMessage(MessageWrapper.getString("shop_sell_plural",
									String.valueOf(shopItem.getAmount()), shopItem.getBuyPrice(),
									ConfigController.getCurrencyText(shopItem.getBuyPrice())));
				} else {
					ecoPlayer.getPlayer()
							.sendMessage(MessageWrapper.getString("shop_sell_singular",
									String.valueOf(shopItem.getAmount()), shopItem.getBuyPrice(),
									ConfigController.getCurrencyText(shopItem.getBuyPrice())));
				}
			}
		}
	}

	/**
	 * Overridden, because of the number of reserved slots. {@inheritDoc}
	 */
	@Override
	public void changeShopSize(int newSize) throws ShopSystemException, PlayerException, GeneralEconomyException {
		getValidationHandler().checkForValidSize(newSize);
		getValidationHandler().checkForResizePossible(getShopInventory(), getSize(), newSize, 2);
		setSize(newSize);
		getSavefileHandler().saveShopSize(newSize);
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
	public void moveShop(Location location) throws TownSystemException, PlayerException {
		if (TownworldController.isTownWorld(location.getWorld().getName())) {
			getValidationHandler().checkForPlayerHasPermissionAtLocation(location, getOwner());
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
		getValidationHandler().checkForShopNameIsFree(name, getOwner());
		getValidationHandler().checkForValidShopName(name);
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
			throws ShopSystemException, PlayerException, GeneralEconomyException {
		super.addShopItem(slot, sellPrice, buyPrice, itemStack);
		ShopItem item = getShopItem(slot);
		getSavefileHandler().saveStock(item.getItemString(), 0);
		updateItemInStockpile(slot);
	}

	/**
	 * Overidden, because of stockpile. {@inheritDoc}
	 */
	@Override
	public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException {
		super.removeShopItem(slot);
		updateItemInStockpile(slot);
	}

	@Override
	public boolean isAvailable(int slot) throws ShopSystemException, GeneralEconomyException {
		getValidationHandler().checkForValidSlot(slot, getSize(), 2);
		ShopItem item = getShopItem(slot);
		if (item.getStock() >= item.getAmount()) {
			return true;
		}
		return false;
	}

	@Override
	public void changeOwner(EconomyPlayer newOwner) throws PlayerException, ShopSystemException {
		getValidationHandler().checkForChangeOwnerIsPossible(newOwner, getName());
		setOwner(newOwner);
		getSavefileHandler().saveOwner(newOwner);
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
		getValidationHandler().checkForPositiveValue(stock);
		getValidationHandler().checkForValidSlot(slot, getSize(), 2);
		ShopItem item = getShopItem(slot);
		int entireStock = item.getStock();
		getValidationHandler().checkForValidStockDecrease(entireStock, stock);
		item.setStock(entireStock - stock);
		getSavefileHandler().saveStock(item.getItemString(), item.getStock());
		updateItemInStockpile(slot);
	}

	@Override
	public void increaseStock(int slot, int stock) throws GeneralEconomyException, ShopSystemException {
		getValidationHandler().checkForPositiveValue(stock);
		getValidationHandler().checkForValidSlot(slot, getSize(), 2);
		ShopItem item = getShopItem(slot);
		int entireStock = item.getStock();
		item.setStock(entireStock + stock);
		getSavefileHandler().saveStock(item.getItemString(), item.getStock());
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

	private void updateItemInStockpile(int slot) throws GeneralEconomyException, ShopSystemException {
		ItemStack stack = getShopInventory().getItem(slot).clone();
		if (stack != null && stack.getType() != Material.AIR) {
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

	private void setupStockpile() {
		stockPile = Bukkit.createInventory(getShopVillager(), getSize());
		ItemStack stockpileSwitchItem = new ItemStack(Material.CRAFTING_TABLE, 1);
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
				new FixedMetadataValue(UltimateEconomy.getInstance, EconomyVillager.PLAYERSHOP));
	}

	private void setupShopInvDefaultStockItem() {
		ItemStack itemStack = new ItemStack(Material.CRAFTING_TABLE);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName("Stock");
		itemStack.setItemMeta(meta);
		addShopItemToInv(itemStack, 1, getSize() - 2, 0.0, 0.0);
	}

	private void setupShopOwner(EconomyPlayer owner) {
		if (owner != null) {
			setOwner(owner);
			getSavefileHandler().saveOwner(owner);
			getShopVillager().setCustomName(getName() + "_" + owner.getName());
		}
	}

	/*
	 * Loading methods
	 * 
	 */

	private void loadExistingPlayerShop(String name)
			throws PlayerException, GeneralEconomyException, ShopSystemException {
		loadStock();
		loadOwner(name);
		loadStockpile();
		setupEconomyVillagerType();
	}

	private void loadStock() {
		try {
			for (ShopItem item : getItemList()) {
				item.setStock(getSavefileHandler().loadStock(item.getItemString()));
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

	private void loadOwner(String oldName) throws PlayerException {
		String owner = getSavefileHandler().loadOwner(oldName);
		if (owner != null && !"".equals(owner)) {
			setOwner(EconomyPlayerController.getEconomyPlayerByName(owner));
			getShopVillager().setCustomName(getName() + "_" + getOwner().getName());
		}
	}
}