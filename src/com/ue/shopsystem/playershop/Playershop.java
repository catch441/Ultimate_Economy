package com.ue.shopsystem.playershop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.EconomyPlayer;
import com.ue.shopsystem.Shop;
import com.ue.townsystem.Town;
import com.ue.townsystem.TownWorld;

import ultimate_economy.UEVillagerType;
import ultimate_economy.Ultimate_Economy;

public class Playershop extends Shop {

	private static List<Playershop> playerShopList = new ArrayList<>();

	// true = shop, false = stock
	private boolean shopMode;
	protected String owner;

	/**
	 * Constructor for creating a new playershop.
	 * No validation, if the shopId is unique.

	 * 
	 * @param dataFolder
	 * @param server
	 * @param name
	 * @param owner
	 * @param shopId
	 * @param spawnLocation
	 * @param size
	 */
	protected Playershop(File dataFolder, String name, String owner, String shopId, Location spawnLocation, int size) {
		super(dataFolder, name, shopId, spawnLocation, size);
		shopMode = true;
		saveOwnerToFile(owner);
		// set the type of the villager
		villager.setMetadata("ue-type", new FixedMetadataValue(Ultimate_Economy.getInstance, UEVillagerType.PLAYERSHOP));
		villager.setCustomName(name + "_" + owner);
	}

	/**
	 * Constructor for loading an existing playershop.
	 * No validation, if the shopId is unique.
	 * If name != null then use old loading otherwise use new loading.
	 * 
	 * @param dataFolder
	 * @param server
	 * @param name
	 * @param shopId
	 */
	protected Playershop(File dataFolder, Server server, String name, String shopId) {
		super(dataFolder, server, name, shopId);
		shopMode = true;
		//old loading, can be deleted in the future
		if(name != null) {
			saveOwnerToFile(name.substring(name.indexOf("_") + 1));
			saveShopNameToFile(name.substring(0,name.indexOf("_")));
		} 
		//new loading
		else {
			loadOwner();
		}
		// set the type of the villager
		villager.setMetadata("ue-type", new FixedMetadataValue(Ultimate_Economy.getInstance, UEVillagerType.PLAYERSHOP));
		// update villager name to naming convention
		villager.setCustomName(getName() + "_" + owner);
		// load shop items
		for (String item : itemNames) {
			try {
				loadShopItem(item);
			} catch (ShopSystemException e) {
				Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////overridden
	
	/**
	 * Overridden, because of the stock item.
	 */
	@Override
	public void setupShopItems() {
		super.setupShopItems();
		ItemStack itemStack = new ItemStack(Material.CRAFTING_TABLE);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName("Stock");
		itemStack.setItemMeta(meta);
		addShopItemToInv(itemStack, 1, size - 2, 0.0, 0.0);
		itemNames.add("CRAFTING_TABLE_0");
	}
	
	/**
	 * Overridden, because of the stock value
	 */
	@Override
	public void addShopItem(int slot, double sellPrice, double buyPrice, ItemStack itemStack) throws ShopSystemException {
		super.addShopItem(slot, sellPrice, buyPrice, itemStack);
		//create a new ItemStack to avoid changes to the original stack
		ItemStack itemStackCopy = new ItemStack(itemStack);
		String itemString = itemStackCopy.toString();
		//set and save shop item stock to 0
		config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopItems." + itemString + ".stock", 0);
		save();		
	}
	
	/**
	 * Overridden, because of the number of reserved slots.
	 */
	@Override
	public void changeShopSize(int newSize) throws ShopSystemException {
		if(newSize % 9 != 0) {
			throw new ShopSystemException(ShopSystemException.INVALID_INVENTORY_SIZE);
		} else {
			boolean possible = true;
			int diff = size - newSize;
			// number or reserved slots
			int temp = 2;
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
				inventory = Bukkit.createInventory(null, size, getName());
				reload();
				setupShopItems();
			} else {
				throw new ShopSystemException(ShopSystemException.RESIZING_FAILED);
			}
		}
	}
	
	/**
	 * Overridden, because of the naming convention
	 * <p>
	 * name_owner
	 */
	@Override
	public void changeShopName(String name) throws ShopSystemException {
		if(Playershop.getPlayerShopUniqueNameList().contains(name + owner)) {
			throw new ShopSystemException(ShopSystemException.SHOP_ALREADY_EXISTS);
		} else if(Playershop.getPlayerShopUniqueNameList().contains(name)) {
			throw new ShopSystemException(ShopSystemException.SHOP_ALREADY_EXISTS);
		} else if(name.contains("_")) {
			throw new ShopSystemException(ShopSystemException.INVALID_CHAR_IN_SHOP_NAME);
		} else {
			saveShopNameToFile(name);
			villager.setCustomName(name + "_" + owner);
			changeInventoryNames(name);
		}
	}
	
	/**
	 * Overridden, because of the permission validation.
	 */
	@Override
	public void moveShop(Location location) throws TownSystemException {
		if(TownWorld.isTownWorld(location.getWorld().getName())) {
			TownWorld townWorld = null;
			try {
				townWorld = TownWorld.getTownWorldByName(location.getWorld().getName());
			} catch (TownSystemException e) {
				//should never happen
			}
				if(townWorld.chunkIsFree(location.getChunk())) {
					throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
				} else {
					Town town = townWorld.getTownByChunk(location.getChunk());
					if(!town.hasBuildPermissions(owner, location.getChunk().getX() + "/" + location.getChunk().getZ())) {
						throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
					}
				}
		}
		saveLocationToFile(location);
		villager.teleport(location);
	}
	
	/**
	 * Overridden, because if the reserved slots
	 */
	@Override
	public void openEditor(Player player) {
		// value = reserved slots
		int value = 2;
		for (int i = 0; i < (size - value); i++) {
			try {
				if (slotIsEmpty(i + 1)) {
					editor.setItem(i, getSkull(SLOTEMPTY, "Slot " + (i + 1)));
				} else {
					editor.setItem(i, getSkull(SLOTFILLED, "Slot " + (i + 1)));
				}
			} catch (ShopSystemException e) {
			}
		}
		player.openInventory(editor);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////Save file edit methods
	
	/**
	 * --Save file edit method--
	 * <p>
	 * For commercial use.
	 * <p>
	 * This method increases the stock of an shopitem in a playershop.
	 * 
	 * @param itemString
	 * @param stock (only positive)
	 */
	public void increaseStock(String itemString, int stock) {
		if(stock >= 0) {
			config = YamlConfiguration.loadConfiguration(file);
			config.set("ShopItems." + itemString + ".stock", (config.getInt("ShopItems." + itemString + ".stock") + stock));
			save();
		}
	}

	/**
	 * --Save file edit method--
	 * <p>
	 * For commercial use.
	 * <p>
	 * This method decreases the stock of an shopitem in a playershop.
	 * 
	 * @param itemString
	 * @param stock (only positive)
	 */
	public void decreaseStock(String itemString, int stock) {
		if(stock >= 0) {
			config = YamlConfiguration.loadConfiguration(file);
			if ((config.getInt("ShopItems." + itemString + ".stock") - stock) >= 0) {
				config.set("ShopItems." + itemString + ".stock",
						(config.getInt("ShopItems." + itemString + ".stock") - stock));
			}
			save();
		}
	}
	
	/**
	 * --Save file edit method--
	 * <p>
	 * NOT FOR COMMERCIAL USE.
	 * <p>
	 * Saves the owner to the savefile.
	 * 
	 * @param owner
	 */
	protected void saveOwnerToFile(String owner) {
		this.owner = owner;
		config = YamlConfiguration.loadConfiguration(file);
		config.set("Owner", owner);
		save();
	}
		
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////save file read/ get methods
	
	/**
	 * --Get Method--
	 * <p>
	 * Returns the name of the shop owner.
	 * 
	 * @return
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * --Save file read method--
	 * <p>
	 * Loads the shop owner from the savefile.
	 * 
	 */
	private void loadOwner() {
		config = YamlConfiguration.loadConfiguration(file);
		owner = config.getString("Owner");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////change methods
	
	/**
	 * --Change Method--
	 * <p>
	 * This method sets the owner of this shop.
	 * The owner and the shopname are validated.
	 * 
	 * @param newOwner
	 * @throws PlayerException
	 * @throws ShopSystemException 
	 */
	public void changeOwner(String newOwner) throws PlayerException, ShopSystemException {
		if (!EconomyPlayer.getEconomyPlayerNameList().contains(newOwner)) {
			throw new PlayerException(PlayerException.PLAYER_DOES_NOT_EXIST);
		} else {
			// validation, check if the new owner has already a shop with this name.
			if (Playershop.getPlayerShopUniqueNameList().contains(getName() + "_" + newOwner)) {
				throw new ShopSystemException(ChatColor.RED + Ultimate_Economy.messages.getString("shop_changeOwner1")
													+ " " + ChatColor.GREEN + newOwner + ChatColor.RED + " "
													+ Ultimate_Economy.messages.getString("shop_changeOwner2"));
			} else {
				saveOwnerToFile(newOwner);
				villager.setCustomName(getName() + "_" + newOwner);
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////stockpile methods
	
	/**
	 * --Stockpile Method--
	 * <p>
	 * This method switch in the playershop between the shop and the stockpile.
	 * 
	 */
	public void switchStockpile() {
		// switch to stockpile
		if (shopMode) {
			shopMode = false;
			inventory.clear();
			setupStockpile();
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
			inventory.setItem(size - 1, stockpileSwitchItem);
		} 
		// switch back to the shop
		else {
			shopMode = true;
			inventory.clear();
			for (String item : itemNames) {
				if (!item.equals("ANVIL_0") && !item.equals("CRAFTING_TABLE_0")) {
					try {
						loadShopItem(item);
					} catch (ShopSystemException e) {
						Bukkit.getLogger().info(e.getMessage());
					}
				}
			}
			setupShopItems();
		}
	}
	
	/**
	 * --Stockpile Method--
	 * <p>
	 * Setup/Build the stockpile GUI, if the stockpile mode is activated. (mode = false)
	 * 
	 */
	public void setupStockpile() {
		if (!shopMode) {
			for (String item : itemNames) {
				if (!item.equals("ANVIL_0") && !item.equals("CRAFTING_TABLE_0")) {
					config = YamlConfiguration.loadConfiguration(file);
					ItemStack itemStack = config.getItemStack("ShopItems." + item + ".Name");
					int slot = config.getInt("ShopItems." + item + ".Slot");
					int stock = config.getInt("ShopItems." + item + ".stock");
					ItemMeta meta = itemStack.getItemMeta();
					List<String> list = new ArrayList<>();
					if (meta.hasLore()) {
						list.addAll(meta.getLore());
					}
					if (stock != 1) {
						list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Items");
					} else {
						list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Item");
					}
					meta.setLore(list);
					itemStack.setItemMeta(meta);
					inventory.setItem(slot, itemStack);
				}
			}
		}
	}
	
	/**
	 * --Stockpile Method--
	 * <p>
	 * This method returns true, if the stock of this item is positiv.
	 * 
	 * @param itemString
	 * @return booelan
	 */
	public boolean available(String itemString) {
		boolean available = false;
		config = YamlConfiguration.loadConfiguration(file);
		if (config.getInt("ShopItems." + itemString + ".stock") >= config
				.getInt("ShopItems." + itemString + ".Amount")) {
			available = true;
		}
		return available;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////// static methods

	/**
	 * Returns a free unique id for a playershop.
	 * 
	 * @return String
	 */
	public static String generateFreePlayerShopId() {
		int id = -1;
		boolean free = false;
		while(!free) {
			id++;
			if(!getPlayershopIdList().contains("P" + id)) {
				free = true;
			}
		}
		return "P" + id;
	}
	
	/**
	 * This method returns a list of playershop names.
	 * name = name_owner for unique names
	 * 
	 * @return List of Strings
	 */
	public static List<String> getPlayerShopUniqueNameList() {
		List<String> list = new ArrayList<>();
		for (Playershop shop : playerShopList) {
			list.add(shop.getName() + "_" + shop.getOwner());
		}
		return list;
	}

	/**
	 * ONLY FOR COMMANDS
	 * 
	 * This method returns a playershop by it's name.
	 * name = name_owner unique names
	 * 
	 * @param name
	 * @return PlayerShop
	 * @throws ShopSystemException
	 */
	public static Playershop getPlayerShopByUniqueName(String name) throws ShopSystemException {
		for (Playershop shop : playerShopList) {
			if (name.equals(shop.getName() + "_" + shop.getOwner())) {
				return shop;
			}
		}
		throw new ShopSystemException(ShopSystemException.SHOP_DOES_NOT_EXIST);
	}
	
	/**
	 * This method returns a PlayerShop by it's id.
	 * 
	 * @param id
	 * @return PlayerShop
	 * @throws ShopSystemException
	 */
	public static Playershop getPlayerShopById(String id) throws ShopSystemException {
		for (Playershop shop : playerShopList) {
			if (shop.getShopId().equals(id)) {
				return shop;
			}
		}
		throw new ShopSystemException(ShopSystemException.SHOP_DOES_NOT_EXIST);
	}
	
	/*
	 * Returns all player shops
	 * @return List<PlayerShop>
	 */
	public static List<Playershop> getPlayerShops() {
		return playerShopList;
	}
	
	/**
	 * Returns a list of all player shop ids.
	 * 
	 * @return
	 */
	public static List<String> getPlayershopIdList() {
		List<String> list = new ArrayList<>();
		for (Playershop shop : playerShopList) {
			list.add(shop.getShopId());
		}
		return list;
	}

	/**
	 * This method should be used to create a new playershop.
	 * 
	 * @param dataFolder
	 * @param name
	 * @param spawnLocation
	 * @param size
	 * @param playerName
	 * @throws ShopSystemException
	 * @throws TownSystemException 
	 */
   	 public static void createPlayerShop(File dataFolder, String name, Location spawnLocation, int size, String playerName)
			throws ShopSystemException, TownSystemException {
		if(name.contains("_" )) {
			throw new ShopSystemException(ShopSystemException.INVALID_CHAR_IN_SHOP_NAME);

		}		
		if(TownWorld.isTownWorld(spawnLocation.getWorld().getName())) {
			TownWorld townWorld = null;
			try {
				townWorld = TownWorld.getTownWorldByName(spawnLocation.getWorld().getName());
			} catch (TownSystemException e) {
				//should never happen
			}
				if(townWorld.chunkIsFree(spawnLocation.getChunk())) {
					throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
				} else {
					Town town = townWorld.getTownByChunk(spawnLocation.getChunk());
					if(!town.hasBuildPermissions(playerName, spawnLocation.getChunk().getX() + "/" + spawnLocation.getChunk().getZ())) {
						throw new TownSystemException(TownSystemException.PLAYER_HAS_NO_PERMISSION);
					}
				}
		}
		if (getPlayerShopUniqueNameList().contains(name + "_" + playerName)) {
			throw new ShopSystemException(ShopSystemException.SHOP_ALREADY_EXISTS);
		} else if (size % 9 != 0) {
			throw new ShopSystemException(ShopSystemException.INVALID_INVENTORY_SIZE);
		} else {
			playerShopList.add(new Playershop(dataFolder, name,playerName, generateFreePlayerShopId(), spawnLocation, size));
		}
	}

	/**
	 * This method should be used to delete a playershop.
	 * 
	 * @param name
	 * @throws ShopSystemException
	 */
	public static void deletePlayerShop(String name) throws ShopSystemException {
		Playershop shop = getPlayerShopByUniqueName(name);
		playerShopList.remove(shop);
		shop.deleteShop();
	}

	/**
	 * This method despawns all playershop villager.
	 */
	public static void despawnAllVillagers() {
		for (Playershop shop : playerShopList) {
			shop.despawnVillager();
		}
	}

	/**
	 * This method loads all playerShops.
	 * 
	 * @param fileConfig
	 * @param dataFolder
	 * @param server
	 */
	public static void loadAllPlayerShops(FileConfiguration fileConfig, File dataFolder, Server server) {
		//old load system, can be deleted in the future
		if(fileConfig.contains("PlayerShopNames")) {
			for (String shopName : fileConfig.getStringList("PlayerShopNames")) {
				File file = new File(dataFolder, shopName + ".yml");
				if (file.exists()) {
					String shopId = generateFreePlayerShopId();
					playerShopList.add(new Playershop(dataFolder, server, shopName, shopId));
				} else {
					Bukkit.getLogger().log(Level.WARNING, ShopSystemException.CANNOT_LOAD_SHOP,
							new ShopSystemException(ShopSystemException.CANNOT_LOAD_SHOP));
				}
			}
			//convert to new shopId save system
			fileConfig.set("PlayerShopNames", null);
			fileConfig.set("PlayerShopIds", Playershop.getPlayershopIdList());
		} 
		//new load system
		else {
			for (String shopId : fileConfig.getStringList("PlayerShopIds")) {
				File file = new File(dataFolder, shopId + ".yml");
				if (file.exists()) {
					playerShopList.add(new Playershop(dataFolder, server, null, shopId));
				} else {
					Bukkit.getLogger().log(Level.WARNING, ShopSystemException.CANNOT_LOAD_SHOP,
							new ShopSystemException(ShopSystemException.CANNOT_LOAD_SHOP));
				}
			}
		}
	}
}