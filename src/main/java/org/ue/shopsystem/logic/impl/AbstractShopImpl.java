package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.EconomyVillagerType;
import org.ue.common.logic.impl.EconomyVillagerImpl;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.logic.ShopExceptionMessageEnum;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopEditorHandler;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.townsystem.logic.TownSystemException;

public abstract class AbstractShopImpl extends EconomyVillagerImpl implements AbstractShop {

	private static final Logger log = LoggerFactory.getLogger(AbstractShopImpl.class);
	protected final CustomSkullService skullService;
	protected final MessageWrapper messageWrapper;
	protected final ConfigManager configManager;
	protected final ShopValidationHandler validationHandler;
	protected final ShopDao shopDao;
	protected String name;
	private Map<Integer, ShopItem> shopItems = new HashMap<>();
	private String shopId;

	private ShopSlotEditorHandler slotEditorHandler;
	private ShopEditorHandler editorHandler;

	public AbstractShopImpl(ShopDao shopDao, ServerProvider serverProvider, CustomSkullService skullService,
			ShopValidationHandler validationHandler, MessageWrapper messageWrapper, ConfigManager configManager) {
		super(serverProvider, shopDao, validationHandler);
		this.shopDao = shopDao;
		this.skullService = skullService;
		this.validationHandler = validationHandler;
		this.messageWrapper = messageWrapper;
		this.configManager = configManager;
	}

	protected void setupNew(EconomyVillagerType ecoVillagerType, String name, String shopId, Location spawnLocation,
			int size, int reservedSlots) throws GeneralEconomyException, EconomyPlayerException {
		shopDao.setupSavefile(shopId);
		this.shopId = shopId;
		this.name = name;
		shopDao.saveShopName(name);
		setupNewEconomyVillager(spawnLocation, ecoVillagerType, name, size, reservedSlots);
		setupShopInvDefaultItems();
		slotEditorHandler = new ShopSlotEditorHandlerImpl(serverProvider, messageWrapper, validationHandler,
				skullService, this);
		editorHandler = new ShopEditorHandlerImpl(skullService, this);
	}

	protected void setupExisting(EconomyVillagerType ecoVillagerType, String shopId, int reservedSlots)
			throws TownSystemException, GeneralEconomyException, EconomyPlayerException {
		shopDao.setupSavefile(shopId);
		this.shopId = shopId;
		this.name = shopDao.loadShopName();
		setupExistingEconomyVillager(ecoVillagerType, name, reservedSlots);
		setupShopInvDefaultItems();
		slotEditorHandler = new ShopSlotEditorHandlerImpl(serverProvider, messageWrapper, validationHandler,
				skullService, this);
		loadShopItems();
		editorHandler = new ShopEditorHandlerImpl(skullService, this);
	}

	@Override
	public List<ShopItem> getItemList() throws ShopSystemException {
		return new ArrayList<>(shopItems.values());
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
	public ShopItem getShopItem(int slot) throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(slot, getInventory());

		return shopItems.get(slot);
	}

	@Override
	public ShopItem getShopItem(ItemStack stack) throws ShopSystemException {
		ItemStack original = stack.clone();
		ItemMeta itemMeta = original.getItemMeta();
		if (itemMeta.hasLore()) {
			List<String> loreList = itemMeta.getLore();
			loreList = removeShopItemPriceLore(loreList);
			itemMeta.setLore(loreList);
			original.setItemMeta(itemMeta);
		}
		original.setAmount(1);
		for (ShopItem item : shopItems.values()) {
			if (item.getItemHash() == original.toString().hashCode()) {
				return item;
			}
		}
		throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
	}

	@Override
	public void changeSize(int newSize) throws ShopSystemException, GeneralEconomyException {
		super.changeSize(newSize);
		getEditorHandler().setup(1);
	}

	@Override
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForPositiveValue(buyPrice);
		validationHandler.checkForPositiveValue(sellPrice);
		validationHandler.checkForPricesGreaterThenZero(sellPrice, buyPrice);
		ShopItem shopItem = new ShopItemImpl(itemStack, itemStack.getAmount(), sellPrice, buyPrice, slot);
		int itemHash = shopItem.getItemHash();
		validationHandler.checkForItemDoesNotExist(itemHash, getItemList());
		// throws an error if the slot is occupied or invalid
		addShopItemToInventory(itemStack.clone(), shopItem.getAmount(), slot, sellPrice, buyPrice);

		shopItems.put(slot, shopItem);
		getEditorHandler().setOccupied(true, slot);
		shopDao.saveShopItem(shopItem, false);

	}

	@Override
	public String editShopItem(int slot, Integer newAmount, Double newSellPrice, Double newBuyPrice)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		if (newSellPrice != null && newBuyPrice != null) {
			// at least one price > 0
			validationHandler.checkForPricesGreaterThenZero(newSellPrice, newBuyPrice);
		}
		ShopItem shopItem = getShopItem(slot);
		int itemHash = shopItem.getItemHash();
		String message = ChatColor.GOLD + "Updated ";
		if (newAmount != null) {
			validationHandler.checkForValidAmount(newAmount);
			shopItem.setAmount(newAmount);
			shopDao.saveShopItemAmount(itemHash, shopItem.getAmount());
			message += ChatColor.GREEN + "amount ";
		}
		if (newSellPrice != null) {
			validationHandler.checkForPositiveValue(newSellPrice);
			shopItem.setSellPrice(newSellPrice);
			shopDao.saveShopItemSellPrice(itemHash, shopItem.getSellPrice());
			message += ChatColor.GREEN + "sellPrice ";
		}
		if (newBuyPrice != null) {
			validationHandler.checkForPositiveValue(newBuyPrice);
			shopItem.setBuyPrice(newBuyPrice);
			shopDao.saveShopItemBuyPrice(itemHash, shopItem.getBuyPrice());
			message += ChatColor.GREEN + "buyPrice ";
		}
		// throws an error if the slot is invalid
		addShopItemToInventory(shopItem.getItemStack(), shopItem.getAmount(), slot, shopItem.getSellPrice(),
				shopItem.getBuyPrice());
		message += ChatColor.GOLD + "for item " + ChatColor.GREEN
				+ shopItem.getItemStack().getType().name().toLowerCase();
		return message;
	}

	@Override
	public void removeShopItem(int slot) throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		validationHandler.checkForItemCanBeDeleted(slot, getSize());
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(slot, getInventory());
		ShopItem shopItem = getShopItem(slot);
		getInventory().clear(slot);
		shopItems.remove(slot);
		getEditorHandler().setOccupied(false, slot);
		shopDao.saveShopItem(shopItem, true);
	}

	@Override
	public abstract void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException;

	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(slot, getInventory());
		validationHandler.checkForPlayerIsOnline(ecoPlayer);
		ShopItem shopItem = getShopItem(slot);
		if (shopItem.getSellPrice() != 0.0) {
			double sellPrice = shopItem.getSellPrice() / shopItem.getAmount() * amount;
			ecoPlayer.increasePlayerAmount(sellPrice, false);
			removeItemFromInventory(ecoPlayer.getPlayer().getInventory(), shopItem.getItemStack(), amount);
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
		validationHandler.checkForValidSlot(slot, getSize() - 1 - getReservedSlots());
		getSlotEditorHandler().setSelectedSlot(slot);
		player.openInventory(getSlotEditorHandler().getSlotEditorInventory());
	}

	@Override
	public void deleteShop() {
		despawn();
		shopDao.deleteFile();
		getLocation().getWorld().save();
	}

	/*
	 * Utility methods
	 * 
	 */

	protected Set<Integer> getOccupiedSlots() {
		return shopItems.keySet();
	}

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
		for (ItemStack s : inventory.getStorageContents()) {
			if (s != null) {
				ItemStack stack = s.clone();
				if (stackIsSimilar(item, s) && removeAmount != 0) {
					if (removeAmount >= stack.getAmount()) {
						inventory.removeItem(stack);
						removeAmount -= stack.getAmount();
					} else {
						stack.setAmount(removeAmount);
						inventory.removeItem(stack);
						break;
					}
				}
			}
		}
	}

	private boolean stackIsSimilar(ItemStack a, ItemStack b) {
		ItemStack aClone = a.clone();
		ItemStack bClone = b.clone();
		bClone.setAmount(1);
		aClone.setAmount(1);
		return aClone.toString().equals(bClone.toString());
	}

	protected ShopSlotEditorHandler getSlotEditorHandler() {
		return slotEditorHandler;
	}

	protected ShopEditorHandler getEditorHandler() {
		return editorHandler;
	}

	private List<Integer> getUniqueItemStringList() {
		List<Integer> list = new ArrayList<>();
		try {
			for (ShopItem item : getItemList()) {
				list.add(item.getItemHash());
			}
		} catch (ShopSystemException e) {
			// only rentshop
		}
		return list;
	}

	protected void reloadShopItems() throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		for (int item : getUniqueItemStringList()) {
			loadShopItem(item);
		}
	}

	protected void changeInventoryNames(String name) {
		changeInventoryName(name);
		getEditorHandler().changeInventoryName(name);
		getSlotEditorHandler().changeInventoryName(name);
	}

	protected List<String> removeShopItemPriceLore(List<String> loreList) {
		if (loreList != null) {
			Iterator<String> loreIter = loreList.iterator();
			while (loreIter.hasNext()) {
				String lore = loreIter.next();
				removeIfContains(lore, " buy for ", loreIter);
				removeIfContains(lore, " sell for ", loreIter);
				removeIfContains(lore, ChatColor.GOLD + " Item", loreIter);
			}
		}
		return loreList;
	}

	private void removeIfContains(String value, String arg, Iterator<String> list) {
		if (value.contains(arg)) {
			list.remove();
		}
	}

	protected void addShopItemToInventory(ItemStack itemStack, int amount, int slot, double sellPrice, double buyPrice)
			throws GeneralEconomyException, EconomyPlayerException {
		ItemMeta meta = itemStack.getItemMeta();
		List<String> loreList = createItemLoreList(meta, amount, sellPrice, buyPrice);
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		itemStack.setAmount(amount);
		addItemStack(itemStack, slot, false, true);
	}

	private List<String> createItemLoreList(ItemMeta meta, int amount, double sellPrice, double buyPrice) {
		if ("Info".equals(meta.getDisplayName())) {
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
		}
		return list;
	}

	protected void setupShopInvDefaultItems() throws GeneralEconomyException, EconomyPlayerException {
		int slot = getSize() - 1;
		ItemStack anvil = serverProvider.createItemStack(Material.ANVIL, 1);
		ItemMeta meta = anvil.getItemMeta();
		meta.setDisplayName("Info");
		anvil.setItemMeta(meta);
		addShopItemToInventory(anvil, 1, slot, 0.0, 0.0);
	}

	private void loadShopItems() {
		for (int item : shopDao.loadItemHashList()) {
			loadShopItem(item);
		}
	}

	@SuppressWarnings("deprecation")
	protected void loadShopItem(int itemHash) {
		if (!shopDao.removeIfCorrupted(itemHash)) {
			ShopItem shopItem = shopDao.loadItem(itemHash);
			try {
				addShopItemToInventory(shopItem.getItemStack(), shopItem.getAmount(), shopItem.getSlot(),
						shopItem.getSellPrice(), shopItem.getBuyPrice());
				shopItems.put(shopItem.getSlot(), shopItem);
			} catch (GeneralEconomyException | EconomyPlayerException e) {
				log.warn("[Ultimate_Economy] Failed to load the shop item "
						+ shopItem.getItemStack().getType().name().toLowerCase() + " for shop " + name);
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}

		}
	}
}