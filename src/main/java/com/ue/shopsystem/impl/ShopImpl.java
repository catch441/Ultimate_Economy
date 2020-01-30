package com.ue.shopsystem.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
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
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.Shop;
import com.ue.ultimate_economy.Ultimate_Economy;

public abstract class ShopImpl implements Shop {

	// minecraft skull texture links
	protected static final String PLUS = "http://textures.minecraft.net/texture/9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777";
	protected static final String MINUS = "http://textures.minecraft.net/texture/935e4e26eafc11b52c11668e1d6634e7d1d0d21c411cb085f9394268eb4cdfba";
	protected static final String ONE = "http://textures.minecraft.net/texture/d2a6f0e84daefc8b21aa99415b16ed5fdaa6d8dc0c3cd591f49ca832b575";
	protected static final String SEVEN = "http://textures.minecraft.net/texture/9e198fd831cb61f3927f21cf8a7463af5ea3c7e43bd3e8ec7d2948631cce879";
	private static final String TEN = "http://textures.minecraft.net/texture/b0cf9794fbc089dab037141f67875ab37fadd12f3b92dba7dd2288f1e98836";
	protected static final String SLOTFILLED = "http://textures.minecraft.net/texture/9e42f682e430b55b61204a6f8b76d5227d278ed9ec4d98bda4a7a4830a4b6";
	protected static final String SLOTEMPTY = "http://textures.minecraft.net/texture/b55d5019c8d55bcb9dc3494ccc3419757f89c3384cf3c9abec3f18831f35b0";
	private static final String TWENTY = "http://textures.minecraft.net/texture/f7b29a1bb25b2ad8ff3a7a38228189c9461f457a4da98dae29384c5c25d85";
	private static final String BUY = "http://textures.minecraft.net/texture/e5da4847272582265bdaca367237c96122b139f4e597fbc6667d3fb75fea7cf6";
	private static final String SELL = "http://textures.minecraft.net/texture/abae89e92ac362635ba3e9fb7c12b7ddd9b38adb11df8aa1aff3e51ac428a4";
	
	private static final String K_ON = "http://textures.minecraft.net/texture/d42a4802b6b2deb49cfbb4b7e267e2f9ad45da24c73286f97bef91d21616496";
	private static final String K_OFF = "http://textures.minecraft.net/texture/e883b5beb4e601c3cbf50505c8bd552e81b996076312cffe27b3cc1a29e3";

	public Villager villager;
	public FileConfiguration config;
	public File file;
	private String name;
	private final String shopId;
	public Location location;
	public Inventory inventory, editor, slotEditor;
	// size 9 = slots 0 - 8
	public int size;
	public List<String> itemNames;
	public int slotEditorSlot;

	/**
	 * Constructor for creating a new shop.
	 * No validation, if the shopId is unique.
	 * 
	 * @param dataFolder
	 * @param name
	 * @param shopId
	 * @param spawnLocation
	 * @param size
	 */
	public ShopImpl(File dataFolder, String name,String shopId, Location spawnLocation, int size) {
		itemNames = new ArrayList<>();
		file = new File(dataFolder, shopId + ".yml");
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopItemList", itemNames);
		save();
		this.shopId = shopId;
		saveLocationToFile(spawnLocation);
		saveShopNameToFile(name);
		saveShopSizeToFile(size);
		setupShopVillager();
	}

	/**
	 * Constructor for loading an existing shop.
	 * No validation, if the shopId is unique.
	 * If name != null then use old loading otherwise use new loading
	 * 
	 * @param dataFolder
	 * @param name //deprecated
	 * @param shopId
	 * @throws TownSystemException 
	 */
	public ShopImpl(File dataFolder, String name,String shopId) throws TownSystemException {
		itemNames = new ArrayList<>();
		//old loading with names, can be deleted in the future
		if(name != null) {
			file = new File(dataFolder, name + ".yml");
			config = YamlConfiguration.loadConfiguration(file);
			this.name = name;
			try {
				changeSavefileName(dataFolder, shopId);
			} catch (ShopSystemException e) {
				//should never happen
			}
		} 
		//new loading with ids
		else {
			file = new File(dataFolder, shopId + ".yml");
			config = YamlConfiguration.loadConfiguration(file);
			loadShopName();
		}
		this.shopId = shopId;
		size = config.getInt("ShopSize");
		World world = Bukkit.getWorld(config.getString("ShopLocation.World"));
		if(world == null) {
			throw TownSystemException.getException(TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST, config.getString("ShopLocation.World"));
		}
		itemNames = config.getStringList("ShopItemList");
		location = new Location(world,
				config.getDouble("ShopLocation.x"), config.getDouble("ShopLocation.y"),
				config.getDouble("ShopLocation.z"));
		setupShopVillager();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////Setup Methods

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
		villager.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30000000, 30000000));
		villager.setVillagerLevel(2);
		villager.setMetadata("ue-id", new FixedMetadataValue(Ultimate_Economy.getInstance,shopId));
		villager.setCollidable(false);
		config = YamlConfiguration.loadConfiguration(file);
		if(config.isSet("Profession")) {
			villager.setProfession(Profession.valueOf(config.getString("Profession")));
		} else {
			villager.setProfession(Profession.NITWIT);
		}
		inventory = Bukkit.createInventory(villager, size, name);
		slotEditor = Bukkit.createInventory(villager, 27, name + "-SlotEditor");
		editor = Bukkit.createInventory(villager, size, name + "-Editor");
		setupShopItems();
	}

	/**
	 * --Setup method--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * Setup the info slot of the shop.
	 * 
	 */
	protected void setupShopItems() {
		int slot = size - 1;
		ItemStack anvil = new ItemStack(Material.ANVIL);
		ItemMeta meta = anvil.getItemMeta();
		meta.setDisplayName("Info");
		anvil.setItemMeta(meta);
		addShopItemToInv(anvil, 1, slot, 0.0, 0.0);
		itemNames.add("ANVIL_0");
	}

	private void setupSlotEditor(int slot) {
		double buyPrice = 0;
		double sellPrice = 0;
		try {
			if(!slotIsEmpty(slot)) {
				ItemStack itemStack = new ItemStack(inventory.getItem(slot - 1));
				itemStack.setAmount(1);
				ItemMeta itemMeta = itemStack.getItemMeta();
				List<String> loreList = itemMeta.getLore();
				Iterator<String> loreIter = loreList.iterator();
				while(loreIter.hasNext()) {
					String lore = loreIter.next();
					if(lore.contains(" buy for ") || lore.contains(" sell for ")) {
						loreIter.remove();
					}
				}
				itemMeta.setLore(loreList);
				itemStack.setItemMeta(itemMeta);
				String itemString =  itemStack.toString();
				buyPrice = getItemBuyPrice(itemString);
				sellPrice = getItemSellPrice(itemString);
			}
		} catch (ShopSystemException | PlayerException e) {}
		
		List<String> listBuy = new ArrayList<String>();
		List<String> listSell = new ArrayList<String>();
		listBuy.add(ChatColor.GOLD + "Price: " + buyPrice);
		listSell.add(ChatColor.GOLD + "Price: " + sellPrice);
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
		
		item = getSkull(K_OFF, "factor off");
		slotEditor.setItem(12, item);
		slotEditor.setItem(21, item);
		
		item = getSkull(ONE, "one");
		slotEditor.setItem(4, item);
		meta = item.getItemMeta();
		meta.setLore(listBuy);
		item.setItemMeta(meta);
		slotEditor.setItem(13, item);
		meta = item.getItemMeta();
		meta.setLore(listSell);
		item.setItemMeta(meta);
		slotEditor.setItem(22, item);
		item = getSkull(TEN, "ten");
		slotEditor.setItem(5, item);
		meta = item.getItemMeta();
		meta.setLore(listBuy);
		item.setItemMeta(meta);
		slotEditor.setItem(14, item);
		meta = item.getItemMeta();
		meta.setLore(listSell);
		item.setItemMeta(meta);
		slotEditor.setItem(23, item);
		item = getSkull(TWENTY, "twenty");
		slotEditor.setItem(6, item);
		meta = item.getItemMeta();
		meta.setLore(listBuy);
		item.setItemMeta(meta);
		slotEditor.setItem(15, item);
		meta = item.getItemMeta();
		meta.setLore(listSell);
		item.setItemMeta(meta);
		slotEditor.setItem(24, item);
		item = new ItemStack(Material.GREEN_WOOL);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "save changes");
		item.setItemMeta(meta);
		slotEditor.setItem(8, item);
		item = new ItemStack(Material.RED_WOOL);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "exit without save");
		item.setItemMeta(meta);
		slotEditor.setItem(7, item);
		item = new ItemStack(Material.BARRIER);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "remove item");
		item.setItemMeta(meta);
		slotEditor.setItem(26, item);
		item = getSkull(BUY, "buyprice");
		meta = item.getItemMeta();
		meta.setLore(listBuy);
		item.setItemMeta(meta);
		slotEditor.setItem(9, item);
		item = getSkull(SELL, "sellprice");
		meta = item.getItemMeta();
		meta.setLore(listSell);
		item.setItemMeta(meta);
		slotEditor.setItem(18, item);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////// Save file edit methods
	
	/**
	 * --Save file edit method--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * Save this location to the savefile of the shop.
	 * 
	 * @param location
	 */
	protected void saveLocationToFile(Location location) {
		this.location = location;
		config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopLocation.x", location.getBlockX());
		config.set("ShopLocation.y", location.getBlockY());
		config.set("ShopLocation.z", location.getBlockZ());
		config.set("ShopLocation.World", location.getWorld().getName());
		save();
	}
	
	/**
	 * --Save file edit method--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * Save this profession of the shop to the savefile.
	 * 
	 * @param profession
	 */
	protected void saveProfessionToFile(Profession profession) {
		config = YamlConfiguration.loadConfiguration(file);
		config.set("Profession", profession.name());
		save();
	}
	
	/**
	 * --Save file edit method--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * Save this name of the shop to the savefile.
	 * 
	 * @param name
	 */
	protected void saveShopNameToFile(String name) {
		this.name = name;
		config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopName", name);
		save();
	}
	
	/**
	 * --Save file edit method--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * Save this size of the shop to the savefile.
	 * 
	 * @param size
	 */
	protected void saveShopSizeToFile(int size) {
		this.size = size;
		config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopSize", size);
		save();
	}
	
	/**
	 * --Save file edit method--
	 * <p>
	 * Not for commercial use.
	 * Only to convert old save files to the new save system
	 * <p>
	 * Changes the name of the savefile
	 * 
	 * @param dataFolder
	 * @param newName
	 * @throws ShopSystemException
	 */
	private void changeSavefileName(File dataFolder, String newName) throws ShopSystemException {
		File newFile = new File(dataFolder, newName + ".yml");
		if(!newFile.exists()) {
			config = YamlConfiguration.loadConfiguration(file);
			file.delete();
			file = newFile;
			save();
		} else {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.ERROR_ON_RENAMING);
		}
	}
	
	/**
	 * --Save fileedit method--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * Saves a item to the savefile.
	 * 
	 * @param stack
	 * @param slot
	 * @param sellPrice
	 * @param buyPrice
	 */
	protected void saveShopItemToFile(ItemStack stack,int slot, double sellPrice, double buyPrice) {
		//create a new ItemStack to avoid changes to the original stack
		ItemStack itemStackCopy = new ItemStack(stack);
		int amount = itemStackCopy.getAmount();
		itemStackCopy.setAmount(1);
		String itemString = itemStackCopy.toString();
		config = YamlConfiguration.loadConfiguration(file);
		itemNames.add(itemString);
		removedoubleObjects(itemNames);
		config.set("ShopItems." + itemString + ".Name", itemStackCopy);
		config.set("ShopItems." + itemString + ".Amount", amount);
		config.set("ShopItems." + itemString + ".Slot", slot);
		config.set("ShopItems." + itemString + ".sellPrice", sellPrice);
		config.set("ShopItems." + itemString + ".buyPrice", buyPrice);
		config.set("ShopItems." + itemString + ".newSaveMethod", "true");
		config.set("ShopItemList", itemNames);
		save();
	}
	
	/**
	 * --Save file edit method--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * Deletes a shop item from the save file.
	 * 
	 * @param itemString
	 */
	protected void removeShopItemFromFile(String itemString) {
		if(itemNames.contains(itemString)) {
			config = YamlConfiguration.loadConfiguration(file);
			itemNames.remove(itemString);
			config.set("ShopItemList", itemNames);
			config.set("ShopItems." + itemString, null);
			save();
		}
	}
	
	/**
	 * --Save file edit method--
	 * <p>
	 * Saved the config into the savefile.
	 */
	protected void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////// Save file read / get methods
	
	public double getItemSellPrice(String itemName) throws ShopSystemException {
		if (itemNames.contains(itemName)) {
			config = YamlConfiguration.loadConfiguration(file);
			return config.getDouble("ShopItems." + itemName + ".sellPrice");
		} else {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
		}
	}
	
	public int getItemAmount(String itemName) throws ShopSystemException {
		config = YamlConfiguration.loadConfiguration(file);
		if (itemNames.contains(itemName)) {
			return config.getInt("ShopItems." + itemName + ".Amount");
		} else {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
		}
	}
	
	public double getItemBuyPrice(String itemName) throws ShopSystemException {
		config = YamlConfiguration.loadConfiguration(file);
		if (itemNames.contains(itemName)) {
			return config.getDouble("ShopItems." + itemName + ".buyPrice");
		} else {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
		}
	}
	
	public List<String> getItemList() {
		return itemNames;
	}
	
	/**
	 * --Save file read method--
	 * <p>
	 * This method loads the shop name from the save file.
	 */
	private void loadShopName() {
		config = YamlConfiguration.loadConfiguration(file);
		name = config.getString("ShopName");
	}
	
	public String getName() {
		return name;
	}
	
	public String getShopId() {
		return shopId;
	}

	public World getWorld() {
		return villager.getWorld();
	};
	
	public File getSaveFile() {
		return file;
	};

	public ItemStack getItem(int slot) {
		slot--;
		return inventory.getItem(slot);
	}
	
	public ItemStack getItemStack(String itemString) throws ShopSystemException {
		if(!itemNames.contains(itemString)) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
		}
		else {
			config = YamlConfiguration.loadConfiguration(file);
			return config.getItemStack("ShopItems." + itemString + ".Name");
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////// change methods
	
	public void changeProfession(Profession profession) {
		villager.setProfession(profession);
		saveProfessionToFile(profession);
	}
	
	/**
	 * --Change Methode--
	 * <p>
	 * Not for commercial use
	 * <p>
	 * Change the name of the inventory, shot editor and editor
	 * 
	 * @param name
	 */
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
	
	public void changeShopSize(int newSize) throws ShopSystemException, PlayerException {
		if(newSize % 9 != 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, size);
		} else {
			boolean possible = true;
			int diff = size - newSize;
			// number of reserved slots
			int temp = 1;
			if(inventory.getSize() > newSize) {
				for(int i = 1;i<=diff;i++) {
					ItemStack stack = inventory.getItem(size-i-temp);
					if(stack != null) {
						possible = false;
					}
				}
			}
			if(possible) {
				config = YamlConfiguration.loadConfiguration(file);
				saveShopSizeToFile(newSize);
				inventory = Bukkit.createInventory(null, size, name);
				reload();
				setupShopItems();
			} else {
				throw ShopSystemException.getException(ShopExceptionMessageEnum.RESIZING_FAILED);
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////// shopitem methods
	
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack) throws ShopSystemException, PlayerException {
		int amount = itemStack.getAmount();
		String itemString = itemStack.toString();
		if (itemStack.getType() == Material.SPAWNER) {
			ItemMeta meta = itemStack.getItemMeta();
			String entity = meta.getDisplayName();
			itemString = "SPAWNER_" + entity;
		}
		if (!slotIsEmpty(slot + 1)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED);
		} else if (sellPrice < 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, buyPrice);
		} else if (buyPrice < 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, buyPrice);
		} else if (buyPrice == 0 && sellPrice == 0) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_PRICES);
		} else if (itemNames.contains(itemString)) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_ALREADY_EXISTS);
		} else {
			saveShopItemToFile(itemStack, slot, sellPrice, buyPrice);
			addShopItemToInv(new ItemStack(itemStack), amount, slot, sellPrice, buyPrice);
		}
	}

	public String editShopItem(int slot, String amount, String sellPrice, String buyPrice) throws ShopSystemException, PlayerException {
		if (slotIsEmpty(slot)) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.INVENTORY_SLOT_EMPTY);
		} else if (!amount.equals("none") && (Integer.valueOf(amount) <= 0 || Integer.valueOf(amount) > 64)) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, amount);
		}
		if (!sellPrice.equals("none") && Double.valueOf(sellPrice) < 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, sellPrice);
		} 
		if (!buyPrice.equals("none") && Double.valueOf(buyPrice) < 0) {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, buyPrice);
		} else if (!sellPrice.equals("none") && !buyPrice.equals("none") && Double.valueOf(sellPrice) == 0
				&& Double.valueOf(buyPrice) == 0) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_PRICES);
		} else {
			ItemStack itemStack = inventory.getItem(slot - 1);
			itemStack.setAmount(1);
			ItemMeta itemMeta = itemStack.getItemMeta();
			List<String> loreList = itemMeta.getLore();
			Iterator<String> loreIter = loreList.iterator();
			while(loreIter.hasNext()) {
				String lore = loreIter.next();
				if(lore.contains(" buy for ") || lore.contains(" sell for ")) {
					loreIter.remove();
				}
			}
			itemMeta.setLore(loreList);
			itemStack.setItemMeta(itemMeta);
			String itemString =  itemStack.toString();
			
			String message = ChatColor.GOLD + "Updated ";
			config = YamlConfiguration.loadConfiguration(file);
			if (!amount.equals("none")) {
				config.set("ShopItems." + itemString + ".Amount", Integer.valueOf(amount));
				message = message + ChatColor.GREEN + "amount ";
			}
			if (!sellPrice.equals("none")) {
				config.set("ShopItems." + itemString + ".sellPrice", Double.valueOf(sellPrice));
				message = message + ChatColor.GREEN + "sellPrice ";
			}
			if (!buyPrice.equals("none")) {
				config.set("ShopItems." + itemString + ".buyPrice", Double.valueOf(buyPrice));
				message = message + ChatColor.GREEN + "buyPrice ";
			}
			save();
			loadShopItem(itemString);
			message = message + ChatColor.GOLD + "for item " + ChatColor.GREEN + itemStack.getType().name().toLowerCase();
			return message;
		}
	}

	/**
	 * --ShopItem Methode--
	 * <p>
	 * Loads a item by it's name. Not for commercial use.
	 * 
	 * @param itemString
	 * @throws ShopSystemException
	 * @throws PlayerException 
	 */
	public void loadShopItem(String itemString) throws ShopSystemException, PlayerException {
		config = YamlConfiguration.loadConfiguration(file);
		//new loading method for new save method
		if(!itemString.contains("SPAWNER_") && !itemString.equals("ANVIL_0") && !itemString.equals("CRAFTING_TABLE_0") && config.getString("ShopItems." + itemString + ".newSaveMethod") != null) {
			ItemStack itemStack = config.getItemStack("ShopItems." + itemString + ".Name");
			addShopItemToInv(itemStack, config.getInt("ShopItems." + itemString + ".Amount"),
					config.getInt("ShopItems." + itemString + ".Slot"),
					config.getDouble("ShopItems." + itemString + ".sellPrice"),
					config.getDouble("ShopItems." + itemString + ".buyPrice"));
		} 
		//load spawner
		else if(itemString.contains("SPAWNER_")) {
			String entityname = itemString.substring(8);
			ItemStack itemStack = new ItemStack(Material.SPAWNER);
			ItemMeta meta = itemStack.getItemMeta();
			meta.setDisplayName(entityname);
			itemStack.setItemMeta(meta);
			addShopItemToInv(itemStack, config.getInt("ShopItems." + itemString + ".Amount"),
					config.getInt("ShopItems." + itemString + ".Slot"),
					config.getDouble("ShopItems." + itemString + ".sellPrice"),
					config.getDouble("ShopItems." + itemString + ".buyPrice"));
		}
		// old loading method, only for old saved items, converts to the new save method
		// can be deleted in the future
		else if(!itemString.equals("ANVIL_0") && !itemString.equals("CRAFTING_TABLE_0")){
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
				// enchanted
				if (string.contains("#Enchanted_")) {
					itemStack = new ItemStack(Material.valueOf(string.substring(0, string.indexOf("#")).toUpperCase()),
							config.getInt("ShopItems." + itemString + ".Amount"));
					addEnchantments(itemStack,
							new ArrayList<String>(config.getStringList("ShopItems." + itemString + ".enchantments")));
				}
				// potion
				else if (string.contains("potion:")) {
					String name = config.getString("ShopItems." + itemString + ".Name");
					itemStack = new ItemStack(Material.valueOf(string.substring(0, string.indexOf(":")).toUpperCase()),
							config.getInt("ShopItems." + itemString + ".Amount"));
					PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
					boolean extended = false;
					boolean upgraded = false;
					String property = name.substring(name.indexOf("#") + 1);
					if (property.equalsIgnoreCase("extended")) {
						extended = true;
					} else if (property.equalsIgnoreCase("upgraded")) {
						upgraded = true;
					}
					meta.setBasePotionData(new PotionData(
							PotionType.valueOf(name.substring(name.indexOf(":") + 1, name.indexOf("#")).toUpperCase()),
							extended, upgraded));
					itemStack.setItemMeta(meta);
				}
				// all other
				else if (!string.contains("SPAWNER")) {
					itemStack = new ItemStack(Material.getMaterial(string),
							config.getInt("ShopItems." + itemString + ".Amount"));
				}
				if (itemStack != null) {
					ItemMeta meta2 = itemStack.getItemMeta();
					if (lore != null && !lore.isEmpty()) {
						meta2.setLore(lore);

					}
					if (!displayName.equals("default")) {
						meta2.setDisplayName(displayName);
					}
					if (damage > 0) {
						Damageable damageMeta = (Damageable) meta2;
						damageMeta.setDamage(damage);
						meta2 = (ItemMeta) damageMeta;
					}
					itemStack.setItemMeta(meta2);
					
					//convert to new save method 
					itemStack.setAmount(config.getInt("ShopItems." + itemString + ".Amount"));
					double sell = config.getDouble("ShopItems." + itemString + ".sellPrice");
					double buy = config.getDouble("ShopItems." + itemString + ".buyPrice");
					int slot = config.getInt("ShopItems." + itemString + ".Slot");
					//remove old item
					config = YamlConfiguration.loadConfiguration(file);
					itemNames.remove(itemString);
					config.set("ShopItemList", itemNames);
					config.set("ShopItems." + itemString, null);
					save();
					//add new item
					addShopItem(slot, sell , buy , itemStack);
				}
			} else if (!itemString.equals("ANVIL_0") && !itemString.equals("CRAFTING_TABLE_0")) {
				throw ShopSystemException.getException(ShopExceptionMessageEnum.CANNOT_LOAD_SHOPITEM, itemString);
			}
		}
	}

	public void removeShopItem(int slot) throws ShopSystemException, PlayerException {
		if (slotIsEmpty(slot + 1)) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.INVENTORY_SLOT_EMPTY);
		} else if ((slot + 1) != size && (slot + 1) <= size) {
			String itemString = "";
			ItemStack stack = inventory.getItem(slot);
			if(stack.getType().equals(Material.SPAWNER)) {
				itemString = "SPAWNER_" + stack.getItemMeta().getDisplayName();
			}
			else {
				ItemMeta itemMeta = stack.getItemMeta();
				List<String> loreList = itemMeta.getLore();
				Iterator<String> loreIter = loreList.iterator();
				while (loreIter.hasNext()) {
					String lore = loreIter.next();
					if (lore.contains(" buy for ") || lore.contains(" sell for ")) {
						loreIter.remove();
					}
				}
				itemMeta.setLore(loreList);
				stack.setItemMeta(itemMeta);
				stack.setAmount(1);
				itemString = stack.toString();
			}
			inventory.clear(slot);
			removeShopItemFromFile(itemString);
		} else if ((slot + 1) == size) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_CANNOT_BE_DELETED);
		}
	}
	
	/**
	 * --ShopItem Methode--
	 * <p>
	 * Not for commercial use.
	 * <p>
	 * This method adds a shopitem to the shop inventory.
	 * 
	 * @param itemStack
	 * @param amount
	 * @param slot
	 * @param sellPrice
	 * @param buyPrice
	 */
	protected void addShopItemToInv(ItemStack itemStack, int amount, int slot, double sellPrice, double buyPrice) {
		String displayName = itemStack.getItemMeta().getDisplayName();
		List<String> list = null;
		if (itemStack.getItemMeta().getLore() != null) {
			list = itemStack.getItemMeta().getLore();
		} else {
			list = new ArrayList<>();
		}
		ItemMeta meta = itemStack.getItemMeta();
		if (displayName.equals("Info")) {
			meta.setDisplayName("Info");
			list.add(ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "sell specified amount");
			list.add(ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "sell all");
			list.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "buy");
		} else if (displayName.equals("Stock")) {
			list.add(ChatColor.RED + "Only for Shopowner");
			list.add(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "open/close stockpile");
		} else if (sellPrice == 0.0) {
			list.add(ChatColor.GOLD + String.valueOf(amount) + " buy for " + ChatColor.GREEN + buyPrice + "$");
		} else if (buyPrice == 0.0) {
			list.add(ChatColor.GOLD + String.valueOf(amount) + " sell for " + ChatColor.GREEN + sellPrice + "$");
		} else {
			list.add(ChatColor.GOLD + String.valueOf(amount) + " buy for " + ChatColor.GREEN + buyPrice + "$");
			list.add(ChatColor.GOLD + String.valueOf(amount) + " sell for " + ChatColor.GREEN + sellPrice + "$");
		}
		meta.setLore(list);
		itemStack.setItemMeta(meta);
		itemStack.setAmount(amount);
		inventory.setItem(slot, itemStack);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////// editor methods
	
	public void openEditor(Player player) {
		// value = slots for other usage - 1
		int value = 1;
		for (int i = 0; i < (size - value); i++) {
			try {
				if (slotIsEmpty(i + 1)) {
					editor.setItem(i, getSkull(SLOTEMPTY, "Slot " + (i + 1)));
				} else {
					editor.setItem(i, getSkull(SLOTFILLED, "Slot " + (i + 1)));
				}
			} catch (PlayerException e) {
			}
		}
		player.openInventory(editor);
	}
	
	public void openSlotEditor(Player player, int slot) throws IllegalArgumentException, ShopSystemException, PlayerException {
		setupSlotEditor(slot);
		ItemStack item;
		slotEditorSlot = slot;
		if (slotIsEmpty(slot)) {
			item = new ItemStack(Material.BARRIER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "select item");
			item.setItemMeta(meta);
			slotEditor.setItem(0, item);
		} else {
			slot--;
			item = new ItemStack(inventory.getItem(slot));
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			if (lore != null) {
				Iterator<String> iterator = lore.iterator();
				while (iterator.hasNext()) {
					String string = iterator.next();
					if (string.contains("buy for") || string.contains("sell for")) {
						iterator.remove();
					}
				}
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
			slotEditor.setItem(0, item);
		}
		player.openInventory(slotEditor);
	}
	
	public void handleSlotEditor(InventoryClickEvent event) throws ShopSystemException, PlayerException {
		if (event.getCurrentItem().getItemMeta() != null) {
			Player player = (Player) event.getWhoClicked();
			ItemStack originStack = null;
			if (inventory.getItem(slotEditorSlot - 1) != null) {
				originStack = new ItemStack(inventory.getItem(slotEditorSlot - 1));
			}
			int slot = event.getSlot() + 1;
			String command = event.getCurrentItem().getItemMeta().getDisplayName();
			String operator = null;
			int factor = 1;
			if(event.getInventory().getItem(12).getItemMeta().getDisplayName().equals("factor on")) {
				factor = 1000;
			}
			double price = 0.0;
			switch (slot) {
				case 5:
				case 6:
				case 7:
					operator = event.getInventory().getItem(2).getItemMeta().getDisplayName();
					break;
				case 14:
				case 15:
				case 16:
					operator = event.getInventory().getItem(11).getItemMeta().getDisplayName();
					price = Double
							.valueOf(event.getInventory().getItem(9).getItemMeta().getLore().get(0).substring(9));
					break;
				case 23:
				case 24:
				case 25:
					operator = event.getInventory().getItem(20).getItemMeta().getDisplayName();
					price = Double
							.valueOf(event.getInventory().getItem(18).getItemMeta().getLore().get(0).substring(9));
					break;
			}
			ItemStack editorItemStack = slotEditor.getItem(0);
			if (command.equals("plus")) {
				switchPlusMinus(slot, "plus");
			} else if (command.equals("minus")) {
				switchPlusMinus(slot, "minus");
			} else if(command.equals("factor off")) {
				switchFactor(slot, "factor on");
			} else if(command.equals("factor on")) {
				switchFactor(slot, "factor off");
			} else if (command.equals("one")) {
				switch (slot) {
					case 5:
						if (editorItemStack != null && operator.equals("plus")
								&& (editorItemStack.getAmount() + 1 <= 64)) {
							editorItemStack.setAmount(editorItemStack.getAmount() + 1);
						} else if (editorItemStack != null && operator.equals("plus")
								&& (editorItemStack.getAmount() + 1 > 64)) {
							editorItemStack.setAmount(64);
						} else if (editorItemStack != null && editorItemStack.getAmount() > 1) {
							editorItemStack.setAmount(editorItemStack.getAmount() - 1);
						}
						break;
					case 14:
						if (price >= 1 && operator.equals("minus")) {
							updateEditorPrice(9, 11, 13, 14, 15, price - 1*factor);
						} else if (operator.equals("plus")) {
							updateEditorPrice(9, 11, 13, 14, 15, price + 1*factor);
						}
						break;
					case 23:
						if (price >= 1 && operator.equals("minus")) {
							updateEditorPrice(18, 20, 22, 23, 24, price - 1*factor);
						} else if (operator.equals("plus")) {
							updateEditorPrice(18, 20, 22, 23, 24, price + 1*factor);
						}
						break;
				}
			} else if (command.equals("ten")) {
				switch (slot) {
					case 6:
						if (editorItemStack != null && operator.equals("plus")
								&& (editorItemStack.getAmount() + 10 <= 64)) {
							editorItemStack.setAmount(editorItemStack.getAmount() + 10);
						} else if (editorItemStack != null && operator.equals("plus")
								&& (editorItemStack.getAmount() + 10 > 64)) {
							editorItemStack.setAmount(64);
						} else if (editorItemStack != null && editorItemStack.getAmount() > 10) {
							editorItemStack.setAmount(editorItemStack.getAmount() - 10);
						}
						break;
					case 15:
						if (price >= 10 && operator.equals("minus")) {
							updateEditorPrice(9, 11, 13, 14, 15, price - 10*factor);
						} else if (operator.equals("plus")) {
							updateEditorPrice(9, 11, 13, 14, 15, price + 10*factor);
						}
						break;
					case 24:
						if (price >= 10 && operator.equals("minus")) {
							updateEditorPrice(18, 20, 22, 23, 24, price - 10*factor);
						} else if (operator.equals("plus")) {
							updateEditorPrice(18, 20, 22, 23, 24, price + 10*factor);
						}
						break;
				}
			} else if (command.equals("twenty")) {
				switch (slot) {
					case 7:
						if (editorItemStack != null && operator.equals("plus")
								&& (editorItemStack.getAmount() + 20 <= 64)) {
							editorItemStack.setAmount(editorItemStack.getAmount() + 20);
						} else if (editorItemStack != null && operator.equals("plus")
								&& (editorItemStack.getAmount() + 20 > 64)) {
							editorItemStack.setAmount(64);
						} else if (editorItemStack != null && editorItemStack.getAmount() > 20) {
							editorItemStack.setAmount(editorItemStack.getAmount() - 20);
						}
						break;
					case 16:
						if (price >= 20 && operator.equals("minus")) {
							updateEditorPrice(9, 11, 13, 14, 15, price - 20*factor);
						} else if (operator.equals("plus")) {
							updateEditorPrice(9, 11, 13, 14, 15, price + 20*factor);
						}
						break;
					case 25:
						if (price >= 20 && operator.equals("minus")) {
							updateEditorPrice(18, 20, 22, 23, 24, price - 20*factor);
						} else if (operator.equals("plus")) {
							updateEditorPrice(18, 20, 22, 23, 24, price + 20*factor);
						}
						break;
				}
			} else if (command.equals(ChatColor.YELLOW + "save changes")) {
				double buyPrice = Double
						.valueOf(event.getInventory().getItem(9).getItemMeta().getLore().get(0).substring(9));
				double sellPrice = Double
						.valueOf(event.getInventory().getItem(18).getItemMeta().getLore().get(0).substring(9));
				if (buyPrice != 0 || sellPrice != 0) {
					ItemStack itemStack = event.getInventory().getItem(0);
					String originalStackString = "";
					// make a copy of the edited/created item
					ItemStack newItemStackCopy = new ItemStack(itemStack);
					if (originStack != null) {
						ItemStack originalItemStackCopy = new ItemStack(originStack);
						if (originalItemStackCopy.getItemMeta().getLore() != null) {
							List<String> loreList = originalItemStackCopy.getItemMeta().getLore();
							Iterator<String> iterator = loreList.iterator();
							while (iterator.hasNext()) {
								String lore = iterator.next();
								if (lore.contains("buy for") || lore.contains("sell for")) {
									iterator.remove();
								}
							}
							ItemMeta meta2 = originalItemStackCopy.getItemMeta();
							meta2.setLore(loreList);
							originalItemStackCopy.setItemMeta(meta2);
						}
						originalStackString = originalItemStackCopy.toString();
						newItemStackCopy.setAmount(originStack.getAmount());
					}
					// if the item changed
					if (!newItemStackCopy.toString().equals(originalStackString)) {
						newItemStackCopy.setAmount(1);
						// check, if this item already exists in a other slot
						if(itemNames.contains(newItemStackCopy.toString())) {
							player.sendMessage(MessageWrapper.getErrorString("item_already_exists_in_shop"));
						} 
						else {
							// the old item in the selected slot gets deleted
							if(originStack != null) {
								removeShopItem(slotEditorSlot - 1);
								player.sendMessage(MessageWrapper.getString("shop_removeItem", originStack.getType().toString().toLowerCase()));
							}
							addShopItem(slotEditorSlot - 1, sellPrice, buyPrice, itemStack);
							player.sendMessage(MessageWrapper.getString("shop_addItem", itemStack.getType().toString().toLowerCase()));
						}
					} 
					// if the item doesn't changed
					else {
						player.sendMessage(editShopItem(slotEditorSlot, String.valueOf(itemStack.getAmount()), String.valueOf(sellPrice),
								String.valueOf(buyPrice)));
					}
				} else {
					player.sendMessage(ChatColor.RED + "The sellprice and the buyprice are both 0!");
				}
			} else if (command.equals(ChatColor.RED + "remove item")) {
				removeShopItem(slotEditorSlot - 1);
				player.sendMessage(MessageWrapper.getString("shop_removeItem", originStack.getType().toString().toLowerCase()));
			} else if (!command.equals("buyprice") && !command.equals("sellprice")) {
				ItemStack editorItemStack2 = new ItemStack(event.getCurrentItem());
				editorItemStack2.setAmount(1);
				slotEditor.setItem(0, editorItemStack2);
			}
		}
	}
	
	private void switchPlusMinus(int slot, String state) {
		slot--;
		if (state.equals("plus")) {
			ItemStack item = getSkull(MINUS, "minus");
			slotEditor.setItem(slot, item);
		} else {
			ItemStack item = getSkull(PLUS, "plus");
			slotEditor.setItem(slot, item);
		}
	}
	
	private void switchFactor(int slot, String state) {
		slot--;
		if (state.equals("factor off")) {
			ItemStack item = getSkull(K_OFF, "factor off");
			slotEditor.setItem(slot, item);
		} else {
			ItemStack item = getSkull(K_ON, "factor on");
			slotEditor.setItem(slot, item);
		}
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
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////shop methods
	
	/**
	 * --Shop Method--
	 * <p>
	 * This method reloads all shopitems.
	 * 
	 * @throws ShopSystemException
	 * @throws PlayerException 
	 */
	public void reload() throws ShopSystemException, PlayerException {
		for (String item : itemNames) {
			loadShopItem(item);
		}
	}
	
	public void despawnVillager() {
		villager.remove();
	}
	
	public void openInv(Player p) {
		p.openInventory(inventory);
	}
	
	public void moveShop(Location location) throws TownSystemException, PlayerException {
		saveLocationToFile(location);
		villager.teleport(location);
	};
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////utils methods
	
	/**
	 * --Utils Method--
	 * <p>
	 * This method returns true if the slot is empty.
	 * 
	 * @param slot
	 * @return boolean
	 * @throws PlayerException 
	 */
	protected boolean slotIsEmpty(int slot) throws PlayerException {
		if (slot <= inventory.getSize() && slot > 0) {
			slot--;
			boolean isEmpty = false;
			if (inventory.getItem(slot) == null) {
				isEmpty = true;
			}
			return isEmpty;
		} else {
			throw PlayerException.getException(PlayerExceptionMessageEnum.INVALID_PARAMETER, slot);
		}
	}
	
	/**
	 * --Utils Method--
	 * <p>
	 * This Method adds a list of enchantments to a given itemstack and returns a list
	 * of all valid used enchantments.
	 * 
	 * @param itemStack
	 * @param enchantmentList
	 * @return
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
	
	/**
	 * --Utils Method--
	 * <p>
	 * Returns a custom playerhead
	 * 
	 * @param url
	 * @param name
	 * @return
	 */
	protected ItemStack getSkull(String url, String name) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		if (url.isEmpty())
			return head;
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
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		headMeta.setDisplayName(name);
		head.setItemMeta(headMeta);
		return head;
	}
	
	/**
	 * --Utils Method--
	 * <p>
	 * Removes double objects from a list.
	 * 
	 * @param list
	 * @return
	 */
	protected List<String> removedoubleObjects(List<String> list) {
		Set<String> set = new LinkedHashSet<String>(list);
		list = new ArrayList<String>(set);
		return list;
	}

}