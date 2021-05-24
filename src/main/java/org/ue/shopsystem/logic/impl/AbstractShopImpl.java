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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.economyvillager.logic.impl.EconomyVillagerImpl;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopEditorHandler;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;

public abstract class AbstractShopImpl extends EconomyVillagerImpl<ShopsystemException> implements AbstractShop {

	protected final ConfigManager configManager;
	protected final ShopValidator validationHandler;
	protected final ShopDao shopDao;
	protected final MessageWrapper messageWrapper;
	protected String name;
	private ShopEditorHandler editorHandler;
	private ShopSlotEditorHandler slotEditorHandler;
	private Map<Integer, ShopItem> shopItems = new HashMap<>();

	public AbstractShopImpl(ShopDao shopDao, ServerProvider serverProvider, CustomSkullService skullService,
			ShopValidator validationHandler, MessageWrapper messageWrapper, ConfigManager configManager) {
		super(serverProvider, shopDao, validationHandler, skullService);
		this.shopDao = shopDao;
		this.validationHandler = validationHandler;
		this.configManager = configManager;
		this.messageWrapper = messageWrapper;
	}

	protected void setupNew(EconomyVillagerType ecoVillagerType, String name, String shopId, Location spawnLocation,
			int size, int reservedSlots) {
		shopDao.setupSavefile(shopId);
		this.name = name;
		shopDao.saveShopName(name);
		setupNewEconomyVillager(spawnLocation, ecoVillagerType, name, shopId, size, reservedSlots, true, "");
		setItem(Material.ANVIL, createDefaultItemLoreList(), "Info", getSize() - 1);
		setupEditorGuiHandlers();

	}

	protected void setupExisting(EconomyVillagerType ecoVillagerType, String shopId, int reservedSlots) {
		shopDao.setupSavefile(shopId);
		this.name = shopDao.loadShopName();
		setupExistingEconomyVillager(ecoVillagerType, name, shopId, reservedSlots, "");
		setItem(Material.ANVIL, createDefaultItemLoreList(), "Info", getSize() - 1);
		loadShopItems();
		setupEditorGuiHandlers();
	}

	private void setupEditorGuiHandlers() {
		editorHandler = serverProvider.getProvider().createShopEditorHandler();
		editorHandler.setup(this, 1);
		slotEditorHandler = serverProvider.getProvider().createShopSlotEditorHandler(editorHandler.getInventory());
		slotEditorHandler.setupSlotEditor(this);
	}

	@Override
	public List<ShopItem> getItemList() {
		return new ArrayList<>(shopItems.values());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ShopItem getShopItem(int slot) throws ShopsystemException {
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(getOccupiedSlots(), slot);
		return shopItems.get(slot);
	}

	@Override
	public ShopItem getShopItem(ItemStack stack) throws ShopsystemException {
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
			if (item.getItemStack().toString().hashCode() == original.toString().hashCode()) {
				return item;
			}
		}
		throw new ShopsystemException(messageWrapper, ExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
	}

	@Override
	public void changeSize(int newSize) throws ShopsystemException {
		super.changeSize(newSize);
		editorHandler.setup(this, 1);
	}

	@Override
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
			throws ShopsystemException {
		validationHandler.checkForPositiveValue(buyPrice);
		validationHandler.checkForPositiveValue(sellPrice);
		validationHandler.checkForPricesGreaterThenZero(sellPrice, buyPrice);
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		validationHandler.checkForSlotIsEmpty(getOccupiedSlots(), slot);
		ShopItem shopItem = new ShopItemImpl(itemStack, itemStack.getAmount(), sellPrice, buyPrice, slot);
		addShopItemToInventory(itemStack.clone(), shopItem.getAmount(), slot, sellPrice, buyPrice);
		shopItems.put(slot, shopItem);
		editorHandler.setOccupied(true, slot);
		shopDao.saveShopItem(shopItem, false);
	}

	@Override
	public String editShopItem(int slot, Integer newAmount, Double newSellPrice, Double newBuyPrice)
			throws ShopsystemException {
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		validationHandler.checkForPositiveValue(newSellPrice);
		validationHandler.checkForPositiveValue(newBuyPrice);
		if (newSellPrice != null && newBuyPrice != null) {
			// at least one price > 0
			validationHandler.checkForPricesGreaterThenZero(newSellPrice, newBuyPrice);
		}
		ShopItem shopItem = getShopItem(slot);
		String message = ChatColor.GOLD + "Updated ";
		if (newAmount != null) {
			validationHandler.checkForValidAmount(newAmount);
			shopItem.setAmount(newAmount);
			shopDao.saveShopItemAmount(shopItem.getSlot(), shopItem.getAmount());
			message += ChatColor.GREEN + "amount ";
		}
		if (newSellPrice != null) {
			shopItem.setSellPrice(newSellPrice);
			shopDao.saveShopItemSellPrice(shopItem.getSlot(), shopItem.getSellPrice());
			message += ChatColor.GREEN + "sellPrice ";
		}
		if (newBuyPrice != null) {
			shopItem.setBuyPrice(newBuyPrice);
			shopDao.saveShopItemBuyPrice(shopItem.getSlot(), shopItem.getBuyPrice());
			message += ChatColor.GREEN + "buyPrice ";
		}
		addShopItemToInventory(shopItem.getItemStack(), shopItem.getAmount(), slot, shopItem.getSellPrice(),
				shopItem.getBuyPrice());
		message += ChatColor.GOLD + "for item " + ChatColor.GREEN
				+ shopItem.getItemStack().getType().name().toLowerCase();
		return message;
	}

	@Override
	public void removeShopItem(int slot) throws ShopsystemException {
		validationHandler.checkForItemCanBeDeleted(slot, getSize());
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(getOccupiedSlots(), slot);
		ShopItem shopItem = getShopItem(slot);
		getInventory().clear(slot);
		shopItems.remove(slot);
		editorHandler.setOccupied(false, slot);
		shopDao.saveShopItem(shopItem, true);
	}

	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws ShopsystemException, BankException, EconomyPlayerException {
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		validationHandler.checkForSlotIsNotEmpty(getOccupiedSlots(), slot);
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
	public InventoryGuiHandler getEditorHandler() {
		return editorHandler;
	}

	@Override
	public InventoryGuiHandler getSlotEditorHandler(int slot) throws ShopsystemException {
		validationHandler.checkForValidSlot(slot, getSize() - getReservedSlots());
		slotEditorHandler.setSelectedSlot(slot);
		return slotEditorHandler;
	}

	@Override
	public void deleteShop() {
		despawn();
		shopDao.deleteFile();
		getLocation().getWorld().save();
	}

	@Override
	public void handleInventoryClick(ClickType clickType, int rawSlot, EconomyPlayer whoClicked) {
		try {
			if (rawSlot < getSize() - getReservedSlots() || rawSlot >= getSize()) {
				switch (clickType) {
				case LEFT:
					// buying is only possible with a click inside the shop inventory
					if (rawSlot < getSize() - getReservedSlots()) {
						buyShopItem(rawSlot, whoClicked, true);
					}
					break;
				case RIGHT:
				case SHIFT_RIGHT:
					handleSell(clickType, rawSlot, whoClicked);
					break;
				default:
					break;
				}
			}
		} catch (ShopsystemException | BankException | EconomyPlayerException e) {
			whoClicked.getPlayer().sendMessage(e.getMessage());
		}
	}

	private void handleSell(ClickType clickType, int slot, EconomyPlayer whoClicked)
			throws ShopsystemException, BankException, EconomyPlayerException {
		ShopItem shopItem = getShopItem(whoClicked.getPlayer().getOpenInventory().getItem(slot));
		int amountInInv = getAmountInInventory(whoClicked.getPlayer().getInventory(), shopItem);
		int sellAmount = 0;
		if (clickType == ClickType.RIGHT) {
			sellAmount = shopItem.getAmount();
		}
		if (amountInInv > sellAmount) {
			sellShopItem(shopItem.getSlot(), sellAmount, whoClicked, true);
		}
	}

	/*
	 * Utility methods
	 * 
	 */

	private int getAmountInInventory(PlayerInventory inv, ShopItem shopItem) {
		int amount = 0;
		for (ItemStack is : inv.getStorageContents()) {
			if (is != null) {
				if (is.isSimilar(shopItem.getItemStack())) {
					amount = amount + is.getAmount();
				}
			}
		}
		return amount;
	}

	protected Set<Integer> getOccupiedSlots() {
		return shopItems.keySet();
	}

	protected void sendBuySellPlayerMessage(int amount, EconomyPlayer ecoPlayer, double price, String sellBuy) {
		if (amount > 1) {
			ecoPlayer.getPlayer().sendMessage(messageWrapper.getString(MessageEnum.getSellBuyPluralValue(sellBuy),
					String.valueOf(amount), price, configManager.getCurrencyText(price)));
		} else {
			ecoPlayer.getPlayer().sendMessage(messageWrapper.getString(MessageEnum.getSellBuySingularValue(sellBuy),
					String.valueOf(amount), price, configManager.getCurrencyText(price)));
		}
	}

	protected void removeItemFromInventory(Inventory inventory, ItemStack item, int removeAmount) {
		for (ItemStack s : inventory.getStorageContents()) {
			if (s != null) {
				ItemStack stack = s.clone();
				if (item.isSimilar(s) && removeAmount != 0) {
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

	private void addShopItemToInventory(ItemStack itemStack, int amount, int slot, double sellPrice, double buyPrice) {
		ItemMeta meta = itemStack.getItemMeta();
		List<String> loreList = createShopItemLoreList(meta, amount, sellPrice, buyPrice);
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		itemStack.setAmount(amount);
		getInventory().setItem(slot, itemStack);
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

	private List<String> createDefaultItemLoreList() {
		List<String> list = new ArrayList<>();
		list.add(ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "sell specified amount");
		list.add(ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "sell all");
		list.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "buy");
		return list;
	}

	protected void loadShopItems() {
		for (int item : shopDao.loadItemSlotList()) {
			loadShopItem(item);
		}
	}

	@SuppressWarnings("deprecation")
	private void loadShopItem(int slot) {
		if (!shopDao.removeIfCorrupted(slot)) {
			ShopItem shopItem = shopDao.loadItem(slot);
			addShopItemToInventory(shopItem.getItemStack(), shopItem.getAmount(), shopItem.getSlot(),
					shopItem.getSellPrice(), shopItem.getBuyPrice());
			shopItems.put(shopItem.getSlot(), shopItem);
		}
	}
}