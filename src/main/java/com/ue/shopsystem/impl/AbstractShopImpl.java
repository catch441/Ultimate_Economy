package com.ue.shopsystem.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.ue.config.api.ConfigController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.AbstractShop;
import com.ue.ultimate_economy.UltimateEconomy;

public abstract class AbstractShopImpl implements AbstractShop {

    // minecraft skull texture links
    protected static final String PLUS = "http://textures.minecraft.net/texture/"
	    + "9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777";
    protected static final String MINUS = "http://textures.minecraft.net/texture/"
	    + "935e4e26eafc11b52c11668e1d6634e7d1d0d21c411cb085f9394268eb4cdfba";
    protected static final String ONE = "http://textures.minecraft.net/texture/"
	    + "d2a6f0e84daefc8b21aa99415b16ed5fdaa6d8dc0c3cd591f49ca832b575";
    protected static final String SEVEN = "http://textures.minecraft.net/texture/"
	    + "9e198fd831cb61f3927f21cf8a7463af5ea3c7e43bd3e8ec7d2948631cce879";
    protected static final String SLOTFILLED = "http://textures.minecraft.net/texture/"
	    + "9e42f682e430b55b61204a6f8b76d5227d278ed9ec4d98bda4a7a4830a4b6";
    protected static final String SLOTEMPTY = "http://textures.minecraft.net/texture/"
	    + "b55d5019c8d55bcb9dc3494ccc3419757f89c3384cf3c9abec3f18831f35b0";
    private static final String TEN = "http://textures.minecraft.net/texture/"
	    + "b0cf9794fbc089dab037141f67875ab37fadd12f3b92dba7dd2288f1e98836";
    private static final String TWENTY = "http://textures.minecraft.net/texture/"
	    + "f7b29a1bb25b2ad8ff3a7a38228189c9461f457a4da98dae29384c5c25d85";
    private static final String BUY = "http://textures.minecraft.net/texture/"
	    + "e5da4847272582265bdaca367237c96122b139f4e597fbc6667d3fb75fea7cf6";
    private static final String SELL = "http://textures.minecraft.net/texture/"
	    + "abae89e92ac362635ba3e9fb7c12b7ddd9b38adb11df8aa1aff3e51ac428a4";
    private static final String K_ON = "http://textures.minecraft.net/texture/"
	    + "d42a4802b6b2deb49cfbb4b7e267e2f9ad45da24c73286f97bef91d21616496";
    private static final String K_OFF = "http://textures.minecraft.net/texture/"
	    + "e883b5beb4e601c3cbf50505c8bd552e81b996076312cffe27b3cc1a29e3";

    private Villager villager;
    private File file;
    private Location location;
    private Inventory inventory, editor, slotEditor;
    // size = 9 (means slots 0-8)
    private int size;
    private List<String> itemNames = new ArrayList<>();
    private int selectedEditorSlot;
    private String name;
    private String shopId;

    /**
     * Constructor for creating a new shop. No validation, if the shopId is unique.
     * 
     * @param name
     * @param shopId
     * @param spawnLocation
     * @param size
     */
    public AbstractShopImpl(String name, String shopId, Location spawnLocation, int size) {
	file = new File(UltimateEconomy.getInstance.getDataFolder(), shopId + ".yml");
	try {
	    file.createNewFile();
	    setupNewShop(name, shopId, spawnLocation, size);
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create savefile");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }

    /**
     * Constructor for loading an existing shop. No validation, if the shopId is
     * unique. If name != null then use old loading otherwise use new loading. If
     * you choose old loading, the savefile gets converted to the new save system.
     * 
     * @param name
     *            //deprecated
     * @param shopId
     * @throws TownSystemException
     */
    public AbstractShopImpl(String name, String shopId) throws TownSystemException {
	if (name != null) {
	    loadExistingShopOld();
	} else {
	    file = new File(UltimateEconomy.getInstance.getDataFolder(), shopId + ".yml");
	    loadExistingShop(shopId);
	}
    }

    @Override
    public double getItemSellPrice(int slot) throws ShopSystemException {
	String itemString = getItemString(inventory.getItem(slot), true);
	checkForItemExists(itemString);
	return YamlConfiguration.loadConfiguration(file).getDouble("ShopItems." + itemString + ".sellPrice");
    }

    @Override
    public int getItemAmount(int slot) throws ShopSystemException {
	String itemString = getItemString(inventory.getItem(slot), true);
	checkForItemExists(itemString);
	return YamlConfiguration.loadConfiguration(file).getInt("ShopItems." + itemString + ".Amount");
    }

    @Override
    public double getItemBuyPrice(int slot) throws ShopSystemException {
	String itemString = getItemString(inventory.getItem(slot), true);
	checkForItemExists(itemString);
	return YamlConfiguration.loadConfiguration(file).getDouble("ShopItems." + itemString + ".buyPrice");
    }

    @Override
    public List<String> getItemList() {
	return itemNames;
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
	return villager.getWorld();
    }

    @Override
    public File getSaveFile() {
	return file;
    }

    @Override
    public ItemStack getItem(int slot) {
	return inventory.getItem(slot);
    }

    @Override
    public ItemStack getItemStack(String itemString) throws ShopSystemException {
	checkForItemExists(itemString);
	return YamlConfiguration.loadConfiguration(file).getItemStack("ShopItems." + itemString + ".Name");
    }

    @Override
    public void changeProfession(Profession profession) {
	villager.setProfession(profession);
	saveProfession();
    }

    @Override
    public void changeShopSize(int newSize) throws ShopSystemException, GeneralEconomyException, PlayerException {
	checkForValidSize(newSize);
	checkForResizePossible(newSize);
	this.size = newSize;
	saveShopSize();
	setupShopInventory();
	reloadShopItems();
    }

    @Override
    public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	checkForSlotIsEmpty(slot);
	checkForValidSellPrice(String.valueOf(sellPrice));
	checkForValidBuyPrice(String.valueOf(buyPrice));
	checkForPricesGreaterThenZero(sellPrice, buyPrice);
	String itemString = getItemString(itemStack, true);
	checkForItemDoesNotExist(itemString);
	itemNames.add(itemString);
	editor.setItem(slot, getSkull(SLOTFILLED, "Slot " + slot));
	saveItemNames();
	saveShopItem(itemStack, slot, sellPrice, buyPrice, false);
	addShopItemToInv(new ItemStack(itemStack), itemStack.getAmount(), slot, sellPrice, buyPrice);
    }

    @Override
    public String editShopItem(int slot, String newAmount, String newSellPrice, String newBuyPrice)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	checkForSlotIsNotEmpty(slot);
	checkForValidAmount(newAmount);
	checkForValidSellPrice(newSellPrice);
	checkForValidBuyPrice(newBuyPrice);
	checkForOnePriceGreaterThenZero(newSellPrice, newBuyPrice);
	ItemStack itemStack = inventory.getItem(slot);
	String itemString = getItemString(itemStack, true);
	String message = ChatColor.GOLD + "Updated ";
	if (!"none".equals(newAmount)) {
	    saveShopItemAmount(itemString, Integer.valueOf(newAmount));
	    message = message + ChatColor.GREEN + "amount ";
	}
	if (!"none".equals(newSellPrice)) {
	    saveShopItemSellPrice(itemString, Integer.valueOf(newSellPrice));
	    message = message + ChatColor.GREEN + "sellPrice ";
	}
	if (!"none".equals(newBuyPrice)) {
	    saveShopItemBuyPrice(itemString, Integer.valueOf(newBuyPrice));
	    message = message + ChatColor.GREEN + "buyPrice ";
	}
	loadShopItem(itemString);
	message = message + ChatColor.GOLD + "for item " + ChatColor.GREEN + itemStack.getType().name().toLowerCase();
	return message;

    }

    @Override
    public void removeShopItem(int slot) throws ShopSystemException, GeneralEconomyException {
	checkForValidSlot(slot);
	checkForSlotIsNotEmpty(slot);
	checkForItemCanBeDeleted(slot);
	inventory.clear(slot);
	String itemString = getItemString(inventory.getItem(slot), true);
	itemNames.remove(itemString);
	editor.setItem(slot, getSkull(SLOTEMPTY, "Slot " + slot));
	saveShopItem(inventory.getItem(slot), slot, 0, 0, true);
    }

    @Override
    public void openEditor(Player player) {
	player.openInventory(editor);
    }

    @Override
    public void openSlotEditor(Player player, int slot)
	    throws IllegalArgumentException, ShopSystemException, GeneralEconomyException {
	updateSlotEditorWithShopItemInformations(slot);
	selectedEditorSlot = slot;
	player.openInventory(slotEditor);
    }

    @Override
    public void despawnVillager() {
	villager.remove();
    }

    @Override
    public void openShopInventory(Player player) {
	player.openInventory(inventory);
    }

    @Override
    public void moveShop(Location location) throws TownSystemException, PlayerException {
	this.location = location;
	saveShopLocation();
	villager.teleport(location);
    }

    /**
     * --Utils Method--
     * <p>
     * This Method adds a list of enchantments to a given itemstack and returns a
     * list of all valid used enchantments.
     * 
     * @param itemStack
     * @param enchantmentList
     * @return list of used enachantments
     */
    public static ArrayList<String> addEnchantments(ItemStack itemStack, ArrayList<String> enchantmentList) {
	Enchantment e = null;
	int lvl = 0;
	ArrayList<String> newList = new ArrayList<>();
	for (String enchantment : enchantmentList) {
	    e = Enchantment.getByKey(NamespacedKey.minecraft(enchantment.substring(0, enchantment.indexOf("-"))));
	    lvl = Integer.valueOf(enchantment.substring(enchantment.indexOf("-") + 1));
	    if (e.getMaxLevel() < lvl) {
		lvl = e.getMaxLevel();
		enchantment = enchantment.substring(0, enchantment.indexOf("-") + 1) + String.valueOf(lvl);
	    }
	    if (itemStack.getType().toString().equals("ENCHANTED_BOOK")) {
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
		meta.addStoredEnchant(e, lvl, true);
		itemStack.setItemMeta(meta);
	    } else if (e.canEnchantItem(itemStack)) {
		itemStack.addEnchantment(e, lvl);
	    }
	}
	if (itemStack.getType().toString().equals("ENCHANTED_BOOK")) {
	    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
	    for (Entry<Enchantment, Integer> map : meta.getStoredEnchants().entrySet()) {
		newList.add(
			map.getKey().getKey().toString().substring(map.getKey().getKey().toString().indexOf(":") + 1)
				+ "-" + map.getValue().intValue());
		newList.sort(String.CASE_INSENSITIVE_ORDER);
	    }
	} else {
	    for (Entry<Enchantment, Integer> map : itemStack.getEnchantments().entrySet()) {
		newList.add(
			map.getKey().getKey().toString().substring(map.getKey().getKey().toString().indexOf(":") + 1)
				+ "-" + map.getValue().intValue());
		newList.sort(String.CASE_INSENSITIVE_ORDER);
	    }
	}
	return newList;
    }

    protected void setName(String name) {
	this.name = name;
    }

    /*
     * Utility methods
     * 
     */

    protected ItemStack getSkull(String url, String name) {
	ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
	if (url.isEmpty()) {
	    return head;
	}
	SkullMeta headMeta = (SkullMeta) head.getItemMeta();
	GameProfile profile = new GameProfile(UUID.randomUUID(), null);
	byte[] encodedData = Base64.getEncoder()
		.encode((String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes()));
	profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
	Field profileField = null;
	try {
	    profileField = headMeta.getClass().getDeclaredField("profile");
	    profileField.setAccessible(true);
	    profileField.set(headMeta, profile);
	} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Failed to request skull texture from minecraft.");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
	headMeta.setDisplayName(name);
	head.setItemMeta(headMeta);
	return head;
    }

    protected boolean isSlotEmpty(int slot) throws GeneralEconomyException {
	checkForValidSlot(slot);
	boolean isEmpty = false;
	if (inventory.getItem(slot) == null) {
	    isEmpty = true;
	}
	return isEmpty;
    }

    protected void reloadShopItems() throws GeneralEconomyException, ShopSystemException, PlayerException {
	for (String item : itemNames) {
	    loadShopItem(item);
	}
    }

    protected void changeInventoryNames(String name) {
	Inventory inventoryNew = Bukkit.createInventory(villager, size, name);
	inventoryNew.setContents(inventory.getContents());
	inventory = inventoryNew;
	Inventory editorNew = Bukkit.createInventory(villager, this.size, name + "-Editor");
	editorNew.setContents(editor.getContents());
	editor = editorNew;
	Inventory slotEditorNew = Bukkit.createInventory(villager, 27, name + "-SlotEditor");
	slotEditorNew.setContents(slotEditor.getContents());
	slotEditor = slotEditorNew;
    }

    private String getItemString(ItemStack itemStack, boolean amountOne) {
	ItemStack itemStackCpy = new ItemStack(itemStack);
	if (amountOne) {
	    itemStackCpy.setAmount(1);
	}
	if (itemStack.getType() == Material.SPAWNER) {
	    return getItemStringForSpawner(itemStack.getItemMeta());
	} else {
	    return getItemStringForNonSpawner(itemStackCpy);
	}
    }

    private String getItemStringForNonSpawner(ItemStack itemStack) {
	ItemMeta itemMeta = itemStack.getItemMeta();
	List<String> loreList = removeShopItemPriceLore(itemMeta.getLore());
	itemMeta.setLore(loreList);
	itemStack.setItemMeta(itemMeta);
	return itemStack.toString();
    }

    private List<String> removeShopItemPriceLore(List<String> loreList) {
	Iterator<String> loreIter = loreList.iterator();
	while (loreIter.hasNext()) {
	    String lore = loreIter.next();
	    if (lore.contains(" buy for ") || lore.contains(" sell for ")) {
		loreIter.remove();
	    }
	}
	return loreList;
    }

    private String getItemStringForSpawner(ItemMeta meta) {
	return "SPAWNER_" + meta.getDisplayName();
    }

    private double getPriceForHandleSlotEditor(InventoryClickEvent event, int slot) {
	switch (slot) {
	case 14:
	case 15:
	case 16:
	    return Double.valueOf(event.getInventory().getItem(9).getItemMeta().getLore().get(0).substring(9));
	case 23:
	case 24:
	case 25:
	    return Double.valueOf(event.getInventory().getItem(18).getItemMeta().getLore().get(0).substring(9));
	default:
	    return 0.0;
	}
    }

    private String getOperatorForHandleSlotEditor(InventoryClickEvent event, int slot) {
	switch (slot) {
	case 5:
	case 6:
	case 7:
	    return event.getInventory().getItem(2).getItemMeta().getDisplayName();
	case 14:
	case 15:
	case 16:
	    return event.getInventory().getItem(11).getItemMeta().getDisplayName();
	case 23:
	case 24:
	case 25:
	    return event.getInventory().getItem(20).getItemMeta().getDisplayName();
	default:
	    return null;
	}
    }

    private void addShopItemToInv(ItemStack itemStack, int amount, int slot, double sellPrice, double buyPrice) {
	ItemMeta meta = itemStack.getItemMeta();
	List<String> loreList = createItemLoreList(meta, amount, sellPrice, buyPrice);
	meta.setLore(loreList);
	itemStack.setItemMeta(meta);
	itemStack.setAmount(amount);
	inventory.setItem(slot, itemStack);
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
		    + ConfigController.getCurrencyText(buyPrice));
	} else if (buyPrice == 0.0) {
	    list.add(ChatColor.GOLD + String.valueOf(amount) + " sell for " + ChatColor.GREEN + sellPrice + " "
		    + ConfigController.getCurrencyText(sellPrice));
	} else {
	    list.add(ChatColor.GOLD + String.valueOf(amount) + " buy for " + ChatColor.GREEN + buyPrice + " "
		    + ConfigController.getCurrencyText(buyPrice));
	    list.add(ChatColor.GOLD + String.valueOf(amount) + " sell for " + ChatColor.GREEN + sellPrice + " "
		    + ConfigController.getCurrencyText(sellPrice));
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
     * Handle editor methods
     * 
     */

    /*
     * Handle slot editor methods
     * 
     */

    @Override
    public void handleSlotEditor(InventoryClickEvent event) {
	if (event.getCurrentItem().getItemMeta() != null) {
	    Player player = (Player) event.getWhoClicked();
	    ItemStack originStack = new ItemStack(inventory.getItem(selectedEditorSlot - 1));
	    int slot = event.getSlot();
	    int factor = 1;
	    if (event.getInventory().getItem(12).getItemMeta().getDisplayName().equals("factor on")) {
		factor = 1000;
	    }
	    String operator = getOperatorForHandleSlotEditor(event, slot);
	    double price = getPriceForHandleSlotEditor(event, slot);
	    ItemStack editorItemStack = slotEditor.getItem(0);
	    String command = event.getCurrentItem().getItemMeta().getDisplayName();
	    try {
		handleSlotEditorCommand(event, player, originStack, slot, factor, operator, price, editorItemStack,
			command);
	    } catch (ShopSystemException | PlayerException | GeneralEconomyException e) {
		player.sendMessage(e.getMessage());
	    }
	}
    }

    private void handleSlotEditorCommand(InventoryClickEvent event, Player player, ItemStack originStack, int slot,
	    int factor, String operator, double price, ItemStack editorItemStack, String command)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	command = ChatColor.stripColor(command);
	switch (command) {
	case "minus":
	case "plus":
	    handleSwitchPlusMinus(slot, command);
	    break;
	case "factor off":
	case "factor on":
	    handleSwitchFactor(slot, command);
	    break;
	case "one":
	    handlePlusMinusOne(slot, factor, operator, price, editorItemStack);
	    break;
	case "ten":
	    handlePlusMinusTen(slot, factor, operator, price, editorItemStack);
	    break;
	case "twenty":
	    handlePlusMinusTwenty(slot, factor, operator, price, editorItemStack);
	    break;
	case "save changes":
	    handleSaveChanges(event.getInventory(), player, originStack);
	    break;
	case "remove item":
	    handleRemoveItem(player, originStack);
	    break;
	default:
	    if (!"buyprice".equals(command) && !"sellprice".equals(command)) {
		handleAddItemToSlotEditor(event.getCurrentItem());
	    }
	    break;
	}
    }

    private void handleAddItemToSlotEditor(ItemStack clickedItem) {
	ItemStack editorItemStack2 = new ItemStack(clickedItem);
	editorItemStack2.setAmount(1);
	slotEditor.setItem(0, editorItemStack2);
    }

    private void handleSaveChanges(Inventory inv, Player player, ItemStack originStack)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	double buyPrice = Double.valueOf(inv.getItem(9).getItemMeta().getLore().get(0).substring(9));
	double sellPrice = Double.valueOf(inv.getItem(18).getItemMeta().getLore().get(0).substring(9));
	checkForPricesGreaterThenZero(sellPrice, buyPrice);
	ItemStack itemStack = inv.getItem(0);
	// make a copy of the edited/created item
	ItemStack newItemStackCopy = new ItemStack(itemStack);
	String originalStackString = getItemString(originStack, false);
	newItemStackCopy.setAmount(originStack.getAmount());
	// if the item changed
	if (!newItemStackCopy.toString().equals(originalStackString)) {
	    newItemStackCopy.setAmount(1);
	    checkForItemDoesNotExist(newItemStackCopy.toString());
	    // the old item in the selected slot gets deleted
	    handleRemoveItem(player, originStack);
	    addShopItem(selectedEditorSlot - 1, sellPrice, buyPrice, itemStack);
	    player.sendMessage(MessageWrapper.getString("shop_addItem", itemStack.getType().toString().toLowerCase()));
	}
	// if the item doesn't changed
	else {
	    player.sendMessage(editShopItem(selectedEditorSlot, String.valueOf(itemStack.getAmount()),
		    String.valueOf(sellPrice), String.valueOf(buyPrice)));
	}
    }

    private void handleRemoveItem(Player player, ItemStack originStack)
	    throws ShopSystemException, GeneralEconomyException {
	removeShopItem(selectedEditorSlot - 1);
	player.sendMessage(MessageWrapper.getString("shop_removeItem", originStack.getType().toString().toLowerCase()));
    }

    private void handleSwitchFactor(int slot, String state) {
	if ("factor off".equals(state)) {
	    ItemStack item = getSkull(K_ON, "factor on");
	    slotEditor.setItem(slot, item);
	} else {
	    ItemStack item = getSkull(K_OFF, "factor off");
	    slotEditor.setItem(slot, item);
	}
    }

    private void handleSwitchPlusMinus(int slot, String state) {
	if ("plus".equals(state)) {
	    ItemStack item = getSkull(MINUS, "minus");
	    slotEditor.setItem(slot, item);
	} else {
	    ItemStack item = getSkull(PLUS, "plus");
	    slotEditor.setItem(slot, item);
	}
    }

    private void handlePlusMinusOne(int slot, int factor, String operator, double price, ItemStack editorItemStack) {
	switch (slot) {
	case 4:
	    handlePlusMinusAmount(1, operator, editorItemStack);
	    break;
	case 13:
	    handlePlusMinusSellPrice(1, factor, operator, price);
	    break;
	case 22:
	    handlePlusMinusBuyPrice(1, factor, operator, price);
	    break;
	default:
	    break;
	}
    }

    private void handlePlusMinusTen(int slot, int factor, String operator, double price, ItemStack editorItemStack) {
	switch (slot) {
	case 5:
	    handlePlusMinusAmount(10, operator, editorItemStack);
	    break;
	case 14:
	    handlePlusMinusSellPrice(10, factor, operator, price);
	    break;
	case 23:
	    handlePlusMinusBuyPrice(10, factor, operator, price);
	    break;
	default:
	    break;
	}
    }

    private void handlePlusMinusTwenty(int slot, int factor, String operator, double price, ItemStack editorItemStack) {
	switch (slot) {
	case 6:
	    handlePlusMinusAmount(20, operator, editorItemStack);
	    break;
	case 15:
	    handlePlusMinusSellPrice(20, factor, operator, price);
	    break;
	case 24:
	    handlePlusMinusBuyPrice(20, factor, operator, price);
	    break;
	default:
	    break;
	}
    }

    private void handlePlusMinusAmount(int value, String operator, ItemStack editorItemStack) {
	if (editorItemStack != null) {
	    if ("plus".equals(operator)) {
		if ((editorItemStack.getAmount() + value <= 64)) {
		    editorItemStack.setAmount(editorItemStack.getAmount() + value);
		} else {
		    editorItemStack.setAmount(64);
		}
	    } else {
		if (editorItemStack.getAmount() > value) {
		    editorItemStack.setAmount(editorItemStack.getAmount() - value);
		}
	    }
	}
    }

    private void handlePlusMinusSellPrice(int value, int factor, String operator, double price) {
	if (price >= value && "minus".equals(operator)) {
	    updateEditorPrice(9, 11, 13, 14, 15, price - value * factor);
	} else if ("plus".equals(operator)) {
	    updateEditorPrice(9, 11, 13, 14, 15, price + value * factor);
	}
    }

    private void handlePlusMinusBuyPrice(int value, int factor, String operator, double price) {
	if (price >= value && "minus".equals(operator)) {
	    updateEditorPrice(18, 20, 22, 23, 24, price - value * factor);
	} else if ("plus".equals(operator)) {
	    updateEditorPrice(18, 20, 22, 23, 24, price + value * factor);
	}
    }

    /*
     * Slot editor utility methods
     * 
     */

    private void updateSlotEditorWithShopItemInformations(int slot)
	    throws ShopSystemException, GeneralEconomyException {
	double buyPrice = 0;
	double sellPrice = 0;
	if (!isSlotEmpty(slot)) {
	    buyPrice = getItemBuyPrice(slot);
	    sellPrice = getItemSellPrice(slot);
	}
	List<String> listBuy = new ArrayList<String>();
	List<String> listSell = new ArrayList<String>();
	listBuy.add(ChatColor.GOLD + "Price: " + buyPrice);
	listSell.add(ChatColor.GOLD + "Price: " + sellPrice);
	setupPlusItemInSlotEditor(listBuy, listSell);
	setupOneNumberItemsInSlotEditor(listBuy, listSell);
	setupTenNumberItemsInSlotEditor(listBuy, listSell);
	setupTwentyNumberItemsInSlotEditor(listBuy, listSell);
	addSkullToSlotEditor("buyprice", 9, listBuy, BUY);
	addSkullToSlotEditor("sellprice", 18, listSell, SELL);
	setupSlotItemInSlotEditor(slot);
    }

    private void setupSlotItemInSlotEditor(int slot) throws GeneralEconomyException {
	if (isSlotEmpty(slot)) {
	    ItemStack item = new ItemStack(Material.BARRIER);
	    ItemMeta meta = item.getItemMeta();
	    meta.setDisplayName(ChatColor.GREEN + "select item");
	    item.setItemMeta(meta);
	    slotEditor.setItem(0, item);
	} else {
	    ItemStack item = new ItemStack(inventory.getItem(slot));
	    ItemMeta meta = item.getItemMeta();
	    List<String> lore = removeShopItemPriceLore(meta.getLore());
	    meta.setLore(lore);
	    item.setItemMeta(meta);
	    slotEditor.setItem(0, item);
	}
    }

    private void addSkullToSlotEditor(String displayName, int slot, List<String> loreList, String skullAdress) {
	ItemStack item = getSkull(skullAdress, displayName);
	ItemMeta meta = item.getItemMeta();
	meta.setLore(loreList);
	item.setItemMeta(meta);
	slotEditor.setItem(slot, item);
    }

    private void setupPlusItemInSlotEditor(List<String> listBuy, List<String> listSell) {
	ItemStack item = getSkull(PLUS, "plus");
	slotEditor.setItem(2, item);
	ItemMeta meta = item.getItemMeta();
	meta.setLore(listBuy);
	item.setItemMeta(meta);
	slotEditor.setItem(11, item);
	meta = item.getItemMeta();
	meta.setLore(listSell);
	item.setItemMeta(meta);
	slotEditor.setItem(20, item);
    }

    private void setupTwentyNumberItemsInSlotEditor(List<String> listBuy, List<String> listSell) {
	ItemStack item = getSkull(TWENTY, "twenty");
	slotEditor.setItem(6, item);
	ItemMeta meta = item.getItemMeta();
	meta.setLore(listBuy);
	item.setItemMeta(meta);
	slotEditor.setItem(15, item);
	meta = item.getItemMeta();
	meta.setLore(listSell);
	item.setItemMeta(meta);
	slotEditor.setItem(24, item);
    }

    private void setupTenNumberItemsInSlotEditor(List<String> listBuy, List<String> listSell) {
	ItemStack item = getSkull(TEN, "ten");
	slotEditor.setItem(5, item);
	ItemMeta meta = item.getItemMeta();
	meta.setLore(listBuy);
	item.setItemMeta(meta);
	slotEditor.setItem(14, item);
	meta = item.getItemMeta();
	meta.setLore(listSell);
	item.setItemMeta(meta);
	slotEditor.setItem(23, item);
    }

    private void setupOneNumberItemsInSlotEditor(List<String> listBuy, List<String> listSell) {
	ItemStack item = getSkull(ONE, "one");
	slotEditor.setItem(4, item);
	ItemMeta meta = item.getItemMeta();
	meta.setLore(listBuy);
	item.setItemMeta(meta);
	slotEditor.setItem(13, item);
	meta = item.getItemMeta();
	meta.setLore(listSell);
	item.setItemMeta(meta);
	slotEditor.setItem(22, item);
    }

    private void updateEditorPrice(int a, int b, int c, int d, int e, Double price) {
	List<String> list = new ArrayList<>();
	list.add(ChatColor.GOLD + "Price: " + price);
	ItemMeta meta = slotEditor.getItem(a).getItemMeta();
	meta.setLore(list);
	slotEditor.getItem(a).setItemMeta(meta);
	meta = slotEditor.getItem(b).getItemMeta();
	meta.setLore(list);
	slotEditor.getItem(b).setItemMeta(meta);
	meta = slotEditor.getItem(c).getItemMeta();
	meta.setLore(list);
	slotEditor.getItem(c).setItemMeta(meta);
	meta = slotEditor.getItem(d).getItemMeta();
	meta.setLore(list);
	slotEditor.getItem(d).setItemMeta(meta);
	meta = slotEditor.getItem(e).getItemMeta();
	meta.setLore(list);
	slotEditor.getItem(e).setItemMeta(meta);
    }

    /*
     * Save methods
     * 
     */

    protected void saveShopName() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	config.set("ShopName", name);
	save(config);
    }

    protected void save(YamlConfiguration config) {
	try {
	    config.save(file);
	} catch (IOException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Error an save config to file");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }

    private void saveShopItem(ItemStack stack, int slot, double sellPrice, double buyPrice, boolean delete) {
	ItemStack itemStackCopy = new ItemStack(stack);
	int amount = itemStackCopy.getAmount();
	itemStackCopy.setAmount(1);
	String itemString = itemStackCopy.toString();
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	if (delete) {
	    config.set("ShopItems." + itemString, null);
	    save(config);
	} else {
	    // create a new ItemStack to avoid changes to the original stack
	    config.set("ShopItems." + itemString + ".Name", itemStackCopy);
	    config.set("ShopItems." + itemString + ".Slot", slot);
	    config.set("ShopItems." + itemString + ".newSaveMethod", "true");
	    save(config);
	    saveShopItemSellPrice(itemString, sellPrice);
	    saveShopItemBuyPrice(itemString, sellPrice);
	    saveShopItemAmount(itemString, amount);
	}
    }

    private void saveShopItemSellPrice(String itemString, double sellPrice) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	config.set("ShopItems." + itemString + ".sellPrice", sellPrice);
	save(config);
    }

    private void saveShopItemBuyPrice(String itemString, double buyPrice) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	config.set("ShopItems." + itemString + ".buyPrice", buyPrice);

    }

    private void saveShopItemAmount(String itemString, int amount) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	config.set("ShopItems." + itemString + ".Amount", amount);
	save(config);
    }

    private void saveShopSize() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	config.set("ShopSize", size);
	save(config);
    }

    private void saveShopLocation() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	config.set("ShopLocation.x", location.getX());
	config.set("ShopLocation.y", location.getY());
	config.set("ShopLocation.z", location.getZ());
	config.set("ShopLocation.World", location.getWorld().getName());
	save(config);
    }

    private void saveItemNames() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("ShopItemList", itemNames);
	save(config);
    }

    private void saveProfession() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSaveFile());
	config.set("Profession", villager.getProfession());
	save(config);
    }

    private void changeSavefileName(File dataFolder, String newName) throws ShopSystemException {
	File newFile = new File(dataFolder, newName + ".yml");
	checkForRenamingSavefileIsPossible(newFile);
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	file.delete();
	file = newFile;
	save(config);
    }

    /*
     * Setup methods
     * 
     */

    private void setupNewShop(String name, String shopId, Location spawnLocation, int size) {
	saveItemNames();
	setShopId(shopId);
	setupShopLocation(spawnLocation);
	setupShopName(name);
	setupShopSize(size);
	setupShopVillager();
	setupShopInventory();
	setupSlotEditor();
	setupEditor();
    }

    private void setupShopLocation(Location location) {
	this.location = location;
	saveShopLocation();
    }

    private void setupShopName(String name) {
	setName(name);
	saveShopName();
    }

    private void setupShopSize(int size) {
	this.size = size;
	saveShopSize();
    }

    private void setupShopVillager() {
	location.getChunk().load();
	Collection<Entity> entitys = location.getWorld().getNearbyEntities(location, 10, 10, 10);
	for (Entity entity : entitys) {
	    if (entity.getName().contains(name)) {
		entity.remove();
	    }
	}
	villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
	villager.setCustomName(name);
	villager.setCustomNameVisible(true);
	villager.setSilent(true);
	villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30000000, 30000000));
	villager.setVillagerLevel(2);
	villager.setMetadata("ue-id", new FixedMetadataValue(UltimateEconomy.getInstance, shopId));
	villager.setCollidable(false);
	villager.setInvulnerable(true);
	villager.setProfession(Profession.NITWIT);
    }

    private void setupEditor() {
	editor = Bukkit.createInventory(villager, size, name + "-Editor");
    }

    private void setupSlotEditor() {
	slotEditor = Bukkit.createInventory(villager, 27, getName() + "-SlotEditor");
	setupFactorItem();
	setupSaveItem();
	setupExitItem();
	setupRemoveItem();
    }

    private void setupFactorItem() {
	ItemStack item = getSkull(K_OFF, "factor off");
	slotEditor.setItem(12, item);
	slotEditor.setItem(21, item);
    }

    private void setupRemoveItem() {
	ItemStack item = new ItemStack(Material.BARRIER);
	ItemMeta meta = item.getItemMeta();
	meta.setDisplayName(ChatColor.RED + "remove item");
	item.setItemMeta(meta);
	slotEditor.setItem(26, item);
    }

    private void setupExitItem() {
	ItemStack item = new ItemStack(Material.RED_WOOL);
	ItemMeta meta = item.getItemMeta();
	meta.setDisplayName(ChatColor.RED + "exit without save");
	item.setItemMeta(meta);
	slotEditor.setItem(7, item);
    }

    private void setupSaveItem() {
	ItemStack item = new ItemStack(Material.GREEN_WOOL);
	ItemMeta meta = item.getItemMeta();
	meta.setDisplayName(ChatColor.YELLOW + "save changes");
	item.setItemMeta(meta);
	slotEditor.setItem(8, item);
    }

    private void setupShopInventory() {
	inventory = Bukkit.createInventory(villager, size, name);
	setupShopInvDefaultItems();
    }

    private void setupShopInvDefaultItems() {
	int slot = size - 1;
	ItemStack anvil = new ItemStack(Material.ANVIL);
	ItemMeta meta = anvil.getItemMeta();
	meta.setDisplayName("Info");
	anvil.setItemMeta(meta);
	addShopItemToInv(anvil, 1, slot, 0.0, 0.0);
	itemNames.add("ANVIL_0");
    }

    /*
     * Loading methods
     * 
     */

    private void loadExistingShop(String shopId) throws TownSystemException {
	setShopId(shopId);
	loadShopName();
	loadShopSize();
	loadShopItems();
	loadShopLocation();
	setupShopVillager();
	loadShopVillagerProfession();
    }

    private void loadShopItems() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	itemNames = config.getStringList("ShopItemList");
	for (String item : itemNames) {
	    try {
		loadShopItem(item);
	    } catch (ShopSystemException | PlayerException | GeneralEconomyException e) {
		Bukkit.getLogger().warning("[Ultimate_Economy] " + "Failed to load shop item");
		Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage());
	    }
	}
    }

    private void loadShopVillagerProfession() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	villager.setProfession(Profession.valueOf(config.getString("Profession")));
    }

    private void loadShopLocation() throws TownSystemException {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	World world = Bukkit.getWorld(config.getString("ShopLocation.World"));
	checkForWorldExists(world);
	location = new Location(world, config.getDouble("ShopLocation.x"), config.getDouble("ShopLocation.y"),
		config.getDouble("ShopLocation.z"));
    }

    private void loadShopSize() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	size = config.getInt("ShopSize");
    }

    private void loadShopName() {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	setName(config.getString("ShopName"));
    }

    private void loadShopItem(String itemString) throws ShopSystemException, PlayerException, GeneralEconomyException {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	if (!"ANVIL_0".equals(itemString) && !"CRAFTING_TABLE_0".equals(itemString)) {
	    if (config.getString("ShopItems." + itemString + ".newSaveMethod") != null) {
		if (!itemString.contains("SPAWNER_")) {
		    loadItemNew(itemString);
		} else {
		    loadSpawner(itemString);
		}
	    } else {
		// old loading, converts to new system
		loadItemOld(itemString);
	    }
	}
    }

    private void loadItemNew(String itemString) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	ItemStack itemStack = config.getItemStack("ShopItems." + itemString + ".Name");
	addShopItemToInv(itemStack, config.getInt("ShopItems." + itemString + ".Amount"),
		config.getInt("ShopItems." + itemString + ".Slot"),
		config.getDouble("ShopItems." + itemString + ".sellPrice"),
		config.getDouble("ShopItems." + itemString + ".buyPrice"));
    }

    private void loadSpawner(String itemString) {
	String entityname = itemString.substring(8);
	ItemStack itemStack = new ItemStack(Material.SPAWNER);
	ItemMeta meta = itemStack.getItemMeta();
	meta.setDisplayName(entityname);
	itemStack.setItemMeta(meta);
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	addShopItemToInv(itemStack, config.getInt("ShopItems." + itemString + ".Amount"),
		config.getInt("ShopItems." + itemString + ".Slot"),
		config.getDouble("ShopItems." + itemString + ".sellPrice"),
		config.getDouble("ShopItems." + itemString + ".buyPrice"));
    }

    /*
     * Validation check methods
     * 
     */

    private void checkForOnePriceGreaterThenZero(String sellPrice, String buyPrice) throws ShopSystemException {
	if (!"none".equals(sellPrice) && !"none".equals(buyPrice) && Double.valueOf(sellPrice) == 0
		&& Double.valueOf(buyPrice) == 0) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_PRICES);
	}
    }

    private void checkForPricesGreaterThenZero(double sellPrice, double buyPrice) throws ShopSystemException {
	if (buyPrice == 0 && sellPrice == 0) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_PRICES);
	}
    }

    private void checkForSlotIsNotEmpty(int slot) throws GeneralEconomyException, ShopSystemException {
	if (isSlotEmpty(slot)) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVENTORY_SLOT_EMPTY);
	}
    }

    private void checkForSlotIsEmpty(int slot) throws PlayerException, GeneralEconomyException {
	if (!isSlotEmpty(slot)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED);
	}
    }

    private void checkForValidAmount(String amount) throws GeneralEconomyException {
	if (!"none".equals(amount) && (Integer.valueOf(amount) <= 0 || Integer.valueOf(amount) > 64)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, amount);
	}
    }

    private void checkForValidBuyPrice(String buyPrice) throws GeneralEconomyException {
	if (!"none".equals(buyPrice) && Double.valueOf(buyPrice) < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, buyPrice);
	}
    }

    private void checkForValidSellPrice(String sellPrice) throws GeneralEconomyException {
	checkForValidBuyPrice(sellPrice);
    }

    private void checkForItemExists(String itemName) throws ShopSystemException {
	if (!itemNames.contains(itemName)) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
	}
    }

    private void checkForValidSize(int newSize) throws GeneralEconomyException {
	if (newSize % 9 != 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, size);
	}
    }

    private void checkForValidSlot(int slot) throws GeneralEconomyException {
	if (slot > (size - 1)) {
	    // +1 for player readable style
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, slot + 1);
	}
    }

    private void checkForResizePossible(int newSize) throws ShopSystemException {
	boolean possible = true;
	int diff = size - newSize;
	// number of reserved slots
	int temp = 1;
	if (inventory.getSize() > newSize) {
	    for (int i = 1; i <= diff; i++) {
		ItemStack stack = inventory.getItem(size - i - temp);
		if (stack != null) {
		    possible = false;
		}
	    }
	}
	if (!possible) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.RESIZING_FAILED);
	}
    }

    private void checkForWorldExists(World world) throws TownSystemException {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	if (world == null) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST,
		    config.getString("ShopLocation.World"));
	}
    }

    private void checkForItemDoesNotExist(String itemString) throws ShopSystemException {
	if (itemNames.contains(itemString)) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_ALREADY_EXISTS);
	}
    }

    private void checkForItemCanBeDeleted(int slot) throws ShopSystemException {
	if ((slot + 1) == size) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_CANNOT_BE_DELETED);
	}
    }

    private void checkForRenamingSavefileIsPossible(File newFile) throws ShopSystemException {
	if (newFile.exists()) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ERROR_ON_RENAMING);
	}
    }

    /*
     * Deprecated
     * 
     */

    @Deprecated
    private void loadItemOld(String itemString) throws ShopSystemException, PlayerException, GeneralEconomyException {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	if (config.getString("ShopItems." + itemString + ".Name") != null) {
	    String string = config.getString("ShopItems." + itemString + ".Name");
	    List<String> lore = config.getStringList("ShopItems." + itemString + ".lore");
	    int damage = config.getInt("ShopItems." + itemString + ".damage");
	    String displayName = "default";
	    if (string.contains("|")) {
		displayName = string.substring(0, string.indexOf("|"));
		string = string.substring(string.indexOf("|") + 1);
	    }
	    ItemStack itemStack = null;
	    if (string.contains("#Enchanted_")) {
		itemStack = getEnchantedItemStackOld(itemString, string);
	    } else if (string.contains("potion:")) {
		itemStack = getPotionStackOld(itemString, string);
	    } else if (!string.contains("SPAWNER")) {
		itemStack = new ItemStack(Material.getMaterial(string),
			config.getInt("ShopItems." + itemString + ".Amount"));
	    }
	    if (itemStack != null) {
		ItemMeta meta2 = itemStack.getItemMeta();
		if (lore != null && !lore.isEmpty()) {
		    meta2.setLore(lore);
		}
		if (!"default".equals(displayName)) {
		    meta2.setDisplayName(displayName);
		}
		if (damage > 0) {
		    Damageable damageMeta = (Damageable) meta2;
		    damageMeta.setDamage(damage);
		    meta2 = (ItemMeta) damageMeta;
		}
		itemStack.setItemMeta(meta2);
		// convert to new save method
		itemStack.setAmount(config.getInt("ShopItems." + itemString + ".Amount"));
		double sell = config.getDouble("ShopItems." + itemString + ".sellPrice");
		double buy = config.getDouble("ShopItems." + itemString + ".buyPrice");
		int slot = config.getInt("ShopItems." + itemString + ".Slot");
		saveShopItem(itemStack, slot, 0, 0, true);
		// add new item
		addShopItem(slot, sell, buy, itemStack);
	    }
	}
    }

    @Deprecated
    private void loadExistingShopOld() throws TownSystemException {
	file = new File(UltimateEconomy.getInstance.getDataFolder(), name + ".yml");
	try {
	    changeSavefileName(UltimateEconomy.getInstance.getDataFolder(), shopId);
	    loadExistingShop(shopId);
	} catch (ShopSystemException e) {
	    Bukkit.getLogger().warning("[Ultimate_Economy] Failed to change savefile name to new save system");
	    Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
	}
    }

    @Deprecated
    private ItemStack getEnchantedItemStackOld(String itemString, String string) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	ItemStack itemStack = new ItemStack(Material.valueOf(string.substring(0, string.indexOf("#")).toUpperCase()),
		config.getInt("ShopItems." + itemString + ".Amount"));
	addEnchantments(itemStack,
		new ArrayList<String>(config.getStringList("ShopItems." + itemString + ".enchantments")));
	return itemStack;
    }

    @Deprecated
    private ItemStack getPotionStackOld(String itemString, String string) {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
	String name = config.getString("ShopItems." + itemString + ".Name");
	ItemStack itemStack = new ItemStack(Material.valueOf(string.substring(0, string.indexOf(":")).toUpperCase()),
		config.getInt("ShopItems." + itemString + ".Amount"));
	PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
	boolean extended = false;
	boolean upgraded = false;
	String property = name.substring(name.indexOf("#") + 1);
	if ("extended".equalsIgnoreCase(property)) {
	    extended = true;
	} else if ("upgraded".equalsIgnoreCase(property)) {
	    upgraded = true;
	}
	meta.setBasePotionData(new PotionData(
		PotionType.valueOf(name.substring(name.indexOf(":") + 1, name.indexOf("#")).toUpperCase()), extended,
		upgraded));
	itemStack.setItemMeta(meta);
	return itemStack;
    }
}