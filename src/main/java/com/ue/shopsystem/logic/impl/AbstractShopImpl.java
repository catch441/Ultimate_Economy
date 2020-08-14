package com.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.dataaccess.impl.ShopDaoImpl;
import com.ue.shopsystem.logic.api.AbstractShop;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.ShopEditorHandler;
import com.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

public abstract class AbstractShopImpl implements AbstractShop {

	@Inject
	ConfigManager configManager;
	@Inject
	ShopValidationHandler validationHandler;
	@Inject
	MessageWrapper messageWrapper;
	@Inject
	EconomyPlayerManager ecoPlayerManager;
	@Inject
	CustomSkullService skullService;
	private Villager villager;
	private Location location;
	private Inventory shopInventory;
	private int size;
	private Map<Integer, ShopItem> shopItems = new HashMap<>();
	private String name;
	private String shopId;
	private final ShopDao shopDao;
	private ShopSlotEditorHandler slotEditorHandler;
	private ShopEditorHandler editorHandler;

	/**
	 * Constructor for creating a new shop. No validation, if the shopId is unique.
	 * 
	 * @param name
	 * @param shopId
	 * @param spawnLocation
	 * @param size
	 */
	public AbstractShopImpl(String name, String shopId, Location spawnLocation, int size) {
		shopDao = new ShopDaoImpl(shopId);
		setupNewShop(name, shopId, spawnLocation, size);
		slotEditorHandler = new ShopSlotEditorHandlerImpl(messageWrapper, validationHandler, skullService, this);
		editorHandler = new ShopEditorHandlerImpl(skullService, this);
	}

	/**
	 * Constructor for loading an existing shop. No validation, if the shopId is
	 * unique. If name != null then use old loading otherwise use new loading. If
	 * you choose old loading, the savefile gets converted to the new save system.
	 * 
	 * @param name   deprecated
	 * @param shopId
	 * @throws TownSystemException
	 */
	public AbstractShopImpl(String name, String shopId) throws TownSystemException {
		shopDao = new ShopDaoImpl(shopId);
		if (name != null) {
			loadExistingShopOld(name, shopId);
		} else {
			loadExistingShop(shopId);
		}
	}

	/*
	 * API methods
	 * 
	 */

	@Override
	public List<ShopItem> getItemList() throws ShopSystemException {
		return new ArrayList<>(getShopItemMap().values());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getShopId() {
		return shopId;
	}

	@Override
	public World getWorld() {
		return getShopLocation().getWorld();
	}

	@Override
	public ShopDao getShopDao() {
		return shopDao;
	}

	@Override
	public Villager getShopVillager() {
		return villager;
	}

	@Override
	public ShopItem getShopItem(int slot) throws GeneralEconomyException, ShopSystemException {
		getValidationHandler().checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
		return getShopItemMap().get(slot);
	}

	@Override
	public ShopItem getShopItem(ItemStack stack) throws ShopSystemException {
		ItemStack original = new ItemStack(stack);
		ItemMeta itemMeta = original.getItemMeta();
		if (itemMeta.hasLore()) {
			List<String> loreList = itemMeta.getLore();
			loreList = removeShopItemPriceLore(loreList);
			itemMeta.setLore(loreList);
			original.setItemMeta(itemMeta);
		}
		original.setAmount(1);
		String clickedItemString = original.toString();
		for (ShopItem item : getShopItemMap().values()) {
			if (item.getItemString().equals(clickedItemString)) {
				return item;
			}
		}
		throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
	}

	@Override
	public void changeProfession(Profession profession) {
		getShopVillager().setProfession(profession);
		getShopDao().saveProfession(profession);
	}

	@Override
	public void changeShopSize(int newSize)
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		getValidationHandler();
		getValidationHandler().checkForValidSize(newSize);
		getValidationHandler().checkForResizePossible(getShopInventory(), getSize(), newSize, 1);
		setSize(newSize);
		getShopDao().saveShopSize(newSize);
		setupShopInventory();
		getEditorHandler().setup(1);
		reloadShopItems();
	}

	@Override
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		getValidationHandler().checkForSlotIsEmpty(slot, getShopInventory(), 1);
		getValidationHandler().checkForValidPrice(String.valueOf(sellPrice));
		getValidationHandler().checkForValidPrice(String.valueOf(buyPrice));
		getValidationHandler().checkForPricesGreaterThenZero(sellPrice, buyPrice);
		ShopItem shopItem = new ShopItem(itemStack, itemStack.getAmount(), sellPrice, buyPrice, slot);
		String itemString = shopItem.getItemString();
		getValidationHandler().checkForItemDoesNotExist(itemString, getItemList());
		getShopItemMap().put(slot, shopItem);
		getEditorHandler().setOccupied(true, slot);
		getShopDao().saveItemNames(getUniqueItemStringList());
		getShopDao().saveShopItem(shopItem, false);
		addShopItemToInv(itemStack.clone(), shopItem.getAmount(), slot, sellPrice, buyPrice);
	}

	@Override
	public String editShopItem(int slot, String newAmount, String newSellPrice, String newBuyPrice)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		getValidationHandler().checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
		getValidationHandler().checkForValidAmount(newAmount);
		getValidationHandler().checkForValidPrice(newSellPrice);
		getValidationHandler().checkForValidPrice(newBuyPrice);
		getValidationHandler().checkForOnePriceGreaterThenZeroIfBothAvailable(newSellPrice, newBuyPrice);
		ShopItem shopItem = getShopItem(slot);
		String itemString = shopItem.getItemString();
		String message = ChatColor.GOLD + "Updated ";
		if (!"none".equals(newAmount)) {
			shopItem.setAmount(Integer.valueOf(newAmount));
			getShopDao().saveShopItemAmount(itemString, shopItem.getAmount());
			message = message + ChatColor.GREEN + "amount ";
		}
		if (!"none".equals(newSellPrice)) {
			shopItem.setSellPrice(Double.valueOf(newSellPrice));
			getShopDao().saveShopItemSellPrice(itemString, shopItem.getSellPrice());
			message = message + ChatColor.GREEN + "sellPrice ";
		}
		if (!"none".equals(newBuyPrice)) {
			shopItem.setBuyPrice(Double.valueOf(newBuyPrice));
			getShopDao().saveShopItemBuyPrice(itemString, shopItem.getBuyPrice());
			message = message + ChatColor.GREEN + "buyPrice ";
		}
		loadShopItem(itemString);
		message = message + ChatColor.GOLD + "for item " + ChatColor.GREEN
				+ shopItem.getItemStack().getType().name().toLowerCase();
		return message;

	}

	@Override
	public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException {
		getValidationHandler().checkForItemCanBeDeleted(slot, getSize());
		getValidationHandler().checkForValidSlot(slot, getSize(), 1);
		getValidationHandler().checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
		ShopItem shopItem = getShopItem(slot);
		getShopInventory().clear(slot);
		getShopItemMap().remove(slot);
		getShopDao().saveItemNames(getUniqueItemStringList());
		getEditorHandler().setOccupied(false, slot);
		getShopDao().saveShopItem(shopItem, true);
	}

	@Override
	public abstract void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException;

	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		getValidationHandler().checkForValidSlot(slot, getSize(), 1);
		getValidationHandler().checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
		getValidationHandler().checkForPlayerIsOnline(ecoPlayer);
		ShopItem shopItem = getShopItem(slot);
		if (shopItem.getSellPrice() != 0.0) {
			double sellPrice = shopItem.getSellPrice() / shopItem.getAmount() * amount;
			ecoPlayer.increasePlayerAmount(sellPrice, false);
			removeItemFromInventory(ecoPlayer.getPlayer().getInventory(), shopItem.getItemStack().clone(), amount);
			if (sendMessage) {
				sendBuySellPlayerMessage(amount, ecoPlayer, sellPrice, "sell");
			}
		}
	}

	@Override
	public void openEditor(Player player) throws ShopSystemException {
		player.openInventory(getEditorHandler().getEditorInventory());
	}

	@Override
	public void openSlotEditor(Player player, int slot) throws ShopSystemException, GeneralEconomyException {
		getSlotEditorHandler().setSelectedSlot(slot);
		player.openInventory(getSlotEditorHandler().getSlotEditorInventory());
	}

	@Override
	public void despawnVillager() {
		getShopVillager().remove();
	}

	@Override
	public void deleteShop() {
		despawnVillager();
		getShopDao().deleteFile();
		getWorld().save();
	}

	@Override
	public void openShopInventory(Player player) throws ShopSystemException {
		player.openInventory(getShopInventory());
	}

	@Override
	public void moveShop(Location location) throws TownSystemException, EconomyPlayerException {
		this.location = location;
		getShopDao().saveShopLocation(location);
		getShopVillager().teleport(location);
	}

	@Override
	public Location getShopLocation() {
		return location;
	}

	@Override
	public Inventory getShopInventory() {
		return shopInventory;
	}

	@Override
	public int getSize() {
		return size;
	}

	/*
	 * Utility methods
	 * 
	 */

	protected void sendBuySellPlayerMessage(int amount, EconomyPlayer ecoPlayer, double price, String sellBuy) {
		if (amount > 1) {
			ecoPlayer.getPlayer().sendMessage(messageWrapper.getString("shop_" + sellBuy + "_plural",
					String.valueOf(amount), price, configManager.getCurrencyText(price)));
		} else {
			ecoPlayer.getPlayer().sendMessage(messageWrapper.getString("shop_" + sellBuy + "_singular",
					String.valueOf(amount), price, configManager.getCurrencyText(price)));
		}
	}

	protected void removeItemFromInventory(Inventory inventory, ItemStack item, int removeAmount) {
		ItemStack original = item.clone();
		for (ItemStack s : inventory.getStorageContents()) {
			if (s != null) {
				ItemStack stack = s.clone();
				original.setAmount(1);
				int amountStack = stack.getAmount();
				stack.setAmount(1);
				if (item.equals(stack) && removeAmount != 0) {
					if (removeAmount >= amountStack) {
						stack.setAmount(amountStack);
						inventory.removeItem(stack);
						removeAmount -= amountStack;
					} else {
						stack.setAmount(removeAmount);
						inventory.removeItem(stack);
						break;
					}
				}
			}
		}
	}

	protected ShopValidationHandler getValidationHandler() {
		return validationHandler;
	}

	protected ShopSlotEditorHandler getSlotEditorHandler() {
		return slotEditorHandler;
	}

	protected ShopEditorHandler getEditorHandler() {
		return editorHandler;
	}

	private Map<Integer, ShopItem> getShopItemMap() {
		return shopItems;
	}

	private List<String> getUniqueItemStringList() {
		List<String> list = new ArrayList<>();
		try {
			for (ShopItem item : getItemList()) {
				list.add(item.getItemString());
			}
		} catch (ShopSystemException e) {
			// only rentshop
		}
		return list;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setSize(int size) {
		this.size = size;
	}

	protected void reloadShopItems() throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		for (String item : getUniqueItemStringList()) {
			loadShopItem(item);
		}
	}

	protected void changeInventoryNames(String name) {
		Inventory inventoryNew = Bukkit.createInventory(getShopVillager(), getSize(), name);
		inventoryNew.setContents(shopInventory.getContents());
		shopInventory = inventoryNew;
		getEditorHandler().changeInventoryName(name);
		getSlotEditorHandler().changeInventoryName(name);
	}

	protected List<String> removeShopItemPriceLore(List<String> loreList) {
		if (loreList != null) {
			Iterator<String> loreIter = loreList.iterator();
			while (loreIter.hasNext()) {
				String lore = loreIter.next();
				if (lore.contains(" buy for ") || lore.contains(" sell for ")) {
					loreIter.remove();
				}
			}
		}
		return loreList;
	}

	protected void addShopItemToInv(ItemStack itemStack, int amount, int slot, double sellPrice, double buyPrice) {
		ItemMeta meta = itemStack.getItemMeta();
		List<String> loreList = createItemLoreList(meta, amount, sellPrice, buyPrice);
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		itemStack.setAmount(amount);
		getShopInventory().setItem(slot, itemStack);
	}

	private List<String> createItemLoreList(ItemMeta meta, int amount, double sellPrice, double buyPrice) {
		if ("Info".equals(meta.getDisplayName()) || "Stock".equals(meta.getDisplayName())) {
			return createDefaultItemLoreList(meta);
		} else {
			return createShopItemLoreList(meta, amount, sellPrice, buyPrice);
		}
	}

	private List<String> createShopItemLoreList(ItemMeta meta, int amount, double sellPrice, double buyPrice) {
		List<String> list = new ArrayList<>();
		if (meta.getLore() != null) {
			list.addAll(meta.getLore());
		}
		if (sellPrice == 0.0) {
			list.add(ChatColor.GOLD + String.valueOf(amount) + " buy for " + ChatColor.GREEN + buyPrice + " "
					+ configManager.getCurrencyText(buyPrice));
		} else if (buyPrice == 0.0) {
			list.add(ChatColor.GOLD + String.valueOf(amount) + " sell for " + ChatColor.GREEN + sellPrice + " "
					+ configManager.getCurrencyText(sellPrice));
		} else {
			list.add(ChatColor.GOLD + String.valueOf(amount) + " buy for " + ChatColor.GREEN + buyPrice + " "
					+ configManager.getCurrencyText(buyPrice));
			list.add(ChatColor.GOLD + String.valueOf(amount) + " sell for " + ChatColor.GREEN + sellPrice + " "
					+ configManager.getCurrencyText(sellPrice));
		}
		return list;
	}

	private List<String> createDefaultItemLoreList(ItemMeta meta) {
		List<String> list = new ArrayList<>();
		if ("Info".equals(meta.getDisplayName())) {
			list.add(ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "sell specified amount");
			list.add(ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "sell all");
			list.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "buy");
		} else if ("Stock".equals(meta.getDisplayName())) {
			list.add(ChatColor.RED + "Only for Shopowner");
			list.add(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "open/close stockpile");
		}
		return list;
	}

	private void setShopId(String shopId) {
		this.shopId = shopId;
	}

	/*
	 * Setup methods
	 * 
	 */

	private void setupNewShop(String name, String shopId, Location spawnLocation, int size) {
		getShopDao().saveItemNames(new ArrayList<>());
		setShopId(shopId);
		setupShopLocation(spawnLocation);
		setupShopName(name);
		setupShopSize(size);
		setupShopVillager();
		setupShopInventory();
	}

	protected void setupShopLocation(Location location) {
		this.location = location;
		getShopDao().saveShopLocation(location);
	}

	protected void setupShopName(String name) {
		setName(name);
		getShopDao().saveShopName(name);
	}

	protected void setupShopSize(int size) {
		setSize(size);
		getShopDao().saveShopSize(size);
	}

	private void setupShopVillager() {
		getShopLocation().getChunk().load();
		Collection<Entity> entitys = getShopLocation().getWorld().getNearbyEntities(getShopLocation(), 10, 10, 10);
		// TODO find a better solution without "contains"
		for (Entity entity : entitys) {
			if (getName() != null && entity.getCustomName() != null && entity.getCustomName().contains(getName())) {
				entity.remove();
			}
		}
		villager = (Villager) getShopLocation().getWorld().spawnEntity(getShopLocation(), EntityType.VILLAGER);
		getShopVillager().setCustomName(getName());
		getShopVillager().setCustomNameVisible(true);
		getShopVillager().setSilent(true);
		getShopVillager().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
		getShopVillager().setVillagerLevel(2);
		getShopVillager().setMetadata("ue-id", new FixedMetadataValue(UltimateEconomy.getInstance, getShopId()));
		getShopVillager().setCollidable(false);
		getShopVillager().setInvulnerable(true);
		getShopVillager().setProfession(Profession.NITWIT);
	}

	protected void setupShopInventory() {
		shopInventory = Bukkit.createInventory(getShopVillager(), getSize(), getName());
		setupShopInvDefaultItems();
	}

	protected void setupShopInvDefaultItems() {
		int slot = getSize() - 1;
		ItemStack anvil = new ItemStack(Material.ANVIL);
		ItemMeta meta = anvil.getItemMeta();
		meta.setDisplayName("Info");
		anvil.setItemMeta(meta);
		addShopItemToInv(anvil, 1, slot, 0.0, 0.0);
	}

	/*
	 * Loading methods
	 * 
	 */

	private void loadExistingShop(String shopId) throws TownSystemException {
		setShopId(shopId);
		setName(getShopDao().loadShopName());
		setSize(getShopDao().loadShopSize());
		location = getShopDao().loadShopLocation();
		setupShopVillager();
		getShopVillager().setProfession(getShopDao().loadShopVillagerProfession());
		setupShopInventory();
		loadShopItems();
		editorHandler = new ShopEditorHandlerImpl(skullService, this);
		slotEditorHandler = new ShopSlotEditorHandlerImpl(messageWrapper, validationHandler, skullService, this);
	}

	private void loadShopItems() {
		for (String item : getShopDao().loadItemNameList()) {
			loadShopItem(item);
		}
	}

	protected void loadShopItem(String itemString) {
		ShopItem shopItem = getShopDao().loadItem(itemString);
		getShopItemMap().put(shopItem.getSlot(), shopItem);
		addShopItemToInv(shopItem.getItemStack(), shopItem.getAmount(), shopItem.getSlot(), shopItem.getSellPrice(),
				shopItem.getBuyPrice());
	}

	/*
	 * Deprecated
	 * 
	 */

	@Deprecated
	private void loadExistingShopOld(String name, String shopId) throws TownSystemException {
		try {
			getShopDao().changeSavefileName(UltimateEconomy.getInstance.getDataFolder(), shopId);
			loadExistingShop(shopId);
		} catch (ShopSystemException e) {
			Bukkit.getLogger().warning("[Ultimate_Economy] Failed to change savefile name to new save system");
			Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}
}