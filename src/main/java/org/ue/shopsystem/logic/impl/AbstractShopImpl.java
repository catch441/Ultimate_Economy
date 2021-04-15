package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.ue.common.api.CustomSkullService;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.api.GeneralEconomyValidationHandler;
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

public abstract class AbstractShopImpl implements AbstractShop {

	protected final ServerProvider serverProvider;
	protected final CustomSkullService skullService;
	protected final MessageWrapper messageWrapper;
	protected final ConfigManager configManager;
	protected final ShopValidationHandler validationHandler;
	protected final GeneralEconomyValidationHandler generalValidator;
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

	public AbstractShopImpl(ShopDao shopDao, ServerProvider serverProvider, CustomSkullService skullService,
			ShopValidationHandler validationHandler, MessageWrapper messageWrapper, ConfigManager configManager,
			GeneralEconomyValidationHandler generalValidator) {
		this.shopDao = shopDao;
		this.serverProvider = serverProvider;
		this.skullService = skullService;
		this.validationHandler = validationHandler;
		this.messageWrapper = messageWrapper;
		this.configManager = configManager;
		this.generalValidator = generalValidator;
	}

	@Override
	public void setupNew(String name, String shopId, Location spawnLocation, int size) {
		shopDao.setupSavefile(shopId);
		setShopId(shopId);
		setupShopLocation(spawnLocation);
		setupShopName(name);
		setupShopSize(size);
		setupShopVillager();
		setupShopInventory();
		slotEditorHandler = new ShopSlotEditorHandlerImpl(serverProvider, messageWrapper, validationHandler,
				skullService, this);
		editorHandler = new ShopEditorHandlerImpl(serverProvider, skullService, this);
	}

	@Override
	public void setupExisting(String name, String shopId)
			throws ShopSystemException, TownSystemException, GeneralEconomyException {
		shopDao.setupSavefile(shopId);
		if (name != null) {
			loadExistingShopOld(name, shopId);
		} else {
			loadExistingShop(shopId);
		}
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
	public ShopDao getShopDao() {
		return shopDao;
	}

	@Override
	public Villager getShopVillager() {
		return villager;
	}

	@Override
	public ShopItem getShopItem(int slot) throws GeneralEconomyException, ShopSystemException {
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
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
	public void changeProfession(Profession profession) {
		getShopVillager().setProfession(profession);
		getShopDao().saveProfession(profession);
	}

	@Override
	public void changeShopSize(int newSize)
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		generalValidator.checkForValidSize(newSize);
		validationHandler.checkForResizePossible(getShopInventory(), getSize(), newSize, 1);
		setSize(newSize);
		getShopDao().saveShopSize(newSize);
		setupShopInventory();
		reloadShopItems();
		getEditorHandler().setup(1);
	}

	@Override
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForSlotIsEmpty(slot, getShopInventory(), 1);
		validationHandler.checkForValidPrice(String.valueOf(sellPrice));
		validationHandler.checkForValidPrice(String.valueOf(buyPrice));
		validationHandler.checkForPricesGreaterThenZero(sellPrice, buyPrice);
		ShopItem shopItem = new ShopItemImpl(itemStack, itemStack.getAmount(), sellPrice, buyPrice, slot);
		int itemHash = shopItem.getItemHash();
		validationHandler.checkForItemDoesNotExist(itemHash, getItemList());
		shopItems.put(slot, shopItem);
		getEditorHandler().setOccupied(true, slot);
		getShopDao().saveShopItem(shopItem, false);
		addShopItemToInv(itemStack.clone(), shopItem.getAmount(), slot, sellPrice, buyPrice);
	}

	@Override
	public String editShopItem(int slot, String newAmount, String newSellPrice, String newBuyPrice)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
		validationHandler.checkForValidAmount(newAmount);
		validationHandler.checkForValidPrice(newSellPrice);
		validationHandler.checkForValidPrice(newBuyPrice);
		validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable(newSellPrice, newBuyPrice);
		ShopItem shopItem = getShopItem(slot);
		int itemHash = shopItem.getItemHash();
		String message = ChatColor.GOLD + "Updated ";
		if (!"none".equals(newAmount)) {
			shopItem.setAmount(Integer.valueOf(newAmount));
			getShopDao().saveShopItemAmount(itemHash, shopItem.getAmount());
			message = message + ChatColor.GREEN + "amount ";
		}
		if (!"none".equals(newSellPrice)) {
			shopItem.setSellPrice(Double.valueOf(newSellPrice));
			getShopDao().saveShopItemSellPrice(itemHash, shopItem.getSellPrice());
			message = message + ChatColor.GREEN + "sellPrice ";
		}
		if (!"none".equals(newBuyPrice)) {
			shopItem.setBuyPrice(Double.valueOf(newBuyPrice));
			getShopDao().saveShopItemBuyPrice(itemHash, shopItem.getBuyPrice());
			message = message + ChatColor.GREEN + "buyPrice ";
		}
		addShopItemToInv(shopItem.getItemStack(), shopItem.getAmount(), shopItem.getSlot(), shopItem.getSellPrice(),
				shopItem.getBuyPrice());
		message = message + ChatColor.GOLD + "for item " + ChatColor.GREEN
				+ shopItem.getItemStack().getType().name().toLowerCase();
		return message;

	}

	@Override
	public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException {
		validationHandler.checkForItemCanBeDeleted(slot, getSize());
		generalValidator.checkForValidSlot(slot, getSize() - 1);
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
		ShopItem shopItem = getShopItem(slot);
		getShopInventory().clear(slot);
		shopItems.remove(slot);
		getEditorHandler().setOccupied(false, slot);
		getShopDao().saveShopItem(shopItem, true);
	}

	@Override
	public abstract void buyShopItem(int slot, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException;

	@Override
	public void sellShopItem(int slot, int amount, EconomyPlayer ecoPlayer, boolean sendMessage)
			throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		generalValidator.checkForValidSlot(slot, getSize() - 1);
		validationHandler.checkForSlotIsNotEmpty(slot, getShopInventory(), 1);
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
		generalValidator.checkForValidSlot(slot, getSize() - 1);
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
		getShopLocation().getWorld().save();
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

	protected void setName(String name) {
		this.name = name;
	}

	protected void setSize(int size) {
		this.size = size;
	}

	protected void reloadShopItems() throws GeneralEconomyException, ShopSystemException, EconomyPlayerException {
		for (int item : getUniqueItemStringList()) {
			loadShopItem(item);
		}
	}

	protected void changeInventoryNames(String name) {
		Inventory inventoryNew = serverProvider.createInventory(getShopVillager(), getSize(), name);
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

	protected void addShopItemToInv(ItemStack itemStack, int amount, int slot, double sellPrice, double buyPrice) {
		ItemMeta meta = itemStack.getItemMeta();
		List<String> loreList = createItemLoreList(meta, amount, sellPrice, buyPrice);
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		itemStack.setAmount(amount);
		getShopInventory().setItem(slot, itemStack);
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

	private void setShopId(String shopId) {
		this.shopId = shopId;
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
		getShopVillager().setMetadata("ue-id",
				new FixedMetadataValue(serverProvider.getJavaPluginInstance(), getShopId()));
		getShopVillager().setCollidable(false);
		getShopVillager().setInvulnerable(true);
		getShopVillager().setProfession(Profession.NITWIT);
	}

	protected void setupShopInventory() {
		shopInventory = serverProvider.createInventory(getShopVillager(), getSize(), getName());
		setupShopInvDefaultItems();
	}

	protected void setupShopInvDefaultItems() {
		int slot = getSize() - 1;
		ItemStack anvil = serverProvider.createItemStack(Material.ANVIL, 1);
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
		slotEditorHandler = new ShopSlotEditorHandlerImpl(serverProvider, messageWrapper, validationHandler,
				skullService, this);
		loadShopItems();
		editorHandler = new ShopEditorHandlerImpl(serverProvider, skullService, this);
	}

	private void loadShopItems() {
		for (int item : getShopDao().loadItemHashList()) {
			loadShopItem(item);
		}
	}

	@SuppressWarnings("deprecation")
	protected void loadShopItem(int itemHash) {
		if (!getShopDao().removeIfCorrupted(itemHash)) {
			ShopItem shopItem = getShopDao().loadItem(itemHash);
			shopItems.put(shopItem.getSlot(), shopItem);
			addShopItemToInv(shopItem.getItemStack(), shopItem.getAmount(), shopItem.getSlot(), shopItem.getSellPrice(),
					shopItem.getBuyPrice());
		}
	}

	/*
	 * Deprecated
	 * 
	 */

	@Deprecated
	private void loadExistingShopOld(String name, String shopId) throws TownSystemException, ShopSystemException {
		getShopDao().changeSavefileName(serverProvider.getPluginInstance().getDataFolder(), shopId);
		loadExistingShop(shopId);
	}
}