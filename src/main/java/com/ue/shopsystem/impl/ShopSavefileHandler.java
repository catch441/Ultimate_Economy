package com.ue.shopsystem.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.common.utils.SaveFileUtils;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.ultimate_economy.UltimateEconomy;

public class ShopSavefileHandler extends SaveFileUtils {

	private File file;
	private YamlConfiguration config;

	/**
	 * Constructor for a new shop savefile handler.
	 * @param shopId
	 */
	public ShopSavefileHandler(String shopId) {
		file = new File(UltimateEconomy.getInstance.getDataFolder(), shopId + ".yml");
		if (!getSavefile().exists()) {
			try {
				getSavefile().createNewFile();
			} catch (IOException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] Failed to create savefile");
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
		config = YamlConfiguration.loadConfiguration(getSavefile());
	}

	/**
	 * Saves the shopname.
	 * @param name
	 */
	public void saveShopName(String name) {
		getConfig().set("ShopName", name);
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves a shopitem.
	 * @param shopItem
	 * @param delete
	 */
	public void saveShopItem(ShopItem shopItem, boolean delete) {
		if (delete) {
			getConfig().set("ShopItems." + shopItem.getItemString(), null);
			save(getConfig(),getSavefile());
		} else {
			if (shopItem.getItemStack().getType() == Material.SPAWNER) {
				getConfig().set("ShopItems." + shopItem.getItemString() + ".Name", shopItem.getItemString());
			} else {
				getConfig().set("ShopItems." + shopItem.getItemString() + ".Name", shopItem.getItemStack());
			}
			getConfig().set("ShopItems." + shopItem.getItemString() + ".Slot", shopItem.getSlot());
			getConfig().set("ShopItems." + shopItem.getItemString() + ".newSaveMethod", "true");
			save(getConfig(),getSavefile());
			saveShopItemSellPrice(shopItem.getItemString(), shopItem.getSellPrice());
			saveShopItemBuyPrice(shopItem.getItemString(), shopItem.getBuyPrice());
			saveShopItemAmount(shopItem.getItemString(), shopItem.getAmount());
		}
	}

	/**
	 * Saves a shop item sell price.
	 * @param itemString
	 * @param sellPrice
	 */
	public void saveShopItemSellPrice(String itemString, double sellPrice) {
		getConfig().set("ShopItems." + itemString + ".sellPrice", sellPrice);
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves a shop item buy price.
	 * @param itemString
	 * @param buyPrice
	 */
	public void saveShopItemBuyPrice(String itemString, double buyPrice) {
		getConfig().set("ShopItems." + itemString + ".buyPrice", buyPrice);
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves a shop item amount.
	 * @param itemString
	 * @param amount
	 */
	public void saveShopItemAmount(String itemString, int amount) {
		getConfig().set("ShopItems." + itemString + ".Amount", amount);
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves the shop size.
	 * @param size
	 */
	public void saveShopSize(int size) {
		getConfig().set("ShopSize", size);
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves the shop location.
	 * @param location
	 */
	public void saveShopLocation(Location location) {
		getConfig().set("ShopLocation.x", location.getX());
		getConfig().set("ShopLocation.y", location.getY());
		getConfig().set("ShopLocation.z", location.getZ());
		getConfig().set("ShopLocation.World", location.getWorld().getName());
		save(getConfig(),getSavefile());
	}
	
	/**
	 * Saves the item name list.
	 * @param itemList
	 */
	public void saveItemNames(List<String> itemList) {
		getConfig().set("ShopItemList", itemList);
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves the shop villager profession.
	 * @param profession
	 */
	public void saveProfession(Profession profession) {
		getConfig().set("Profession", profession.name());
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves the stock for a item.
	 * @param itemString
	 * @param stock
	 */
	public void saveStock(String itemString, int stock) {
		getConfig().set("ShopItems." + itemString + ".stock", stock);
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves the shop owner.
	 * @param ecoPlayer
	 */
	public void saveOwner(EconomyPlayer ecoPlayer) {
		if (ecoPlayer != null) {
			getConfig().set("Owner", ecoPlayer.getName());
		} else {
			getConfig().set("Owner", null);
		}
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves the rent until time.
	 * @param rentUntil
	 */
	public void saveRentUntil(long rentUntil) {
		getConfig().set("RentUntil", rentUntil);
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves the rental fee.
	 * @param fee
	 */
	public void saveRentalFee(double fee) {
		getConfig().set("RentalFee", fee);
		save(getConfig(),getSavefile());
	}

	/**
	 * Saves if the shop is rentable.
	 * @param isRentable
	 */
	public void saveRentable(boolean isRentable) {
		getConfig().set("Rentable", isRentable);
		save(getConfig(),getSavefile());
	}

	/**
	 * Changes the savefile name.
	 * @param dataFolder
	 * @param newName
	 * @throws ShopSystemException
	 */
	public void changeSavefileName(File dataFolder, String newName) throws ShopSystemException {
		File newFile = new File(dataFolder, newName + ".yml");
		checkForRenamingSavefileIsPossible(newFile);
		getSavefile().delete();
		file = newFile;
		save(getConfig(),getSavefile());
	}

	/*
	 * Loading methods
	 */

	/**
	 * Loads the shop villager profession.
	 * @return profession
	 */
	public Profession loadShopVillagerProfession() {
		if (getConfig().isSet("Profession")) {
			return Profession.valueOf(config.getString("Profession"));
		} else {
			return Profession.NITWIT;
		}
	}

	/**
	 * Loads the shop size.
	 * @return shop size
	 */
	public int loadShopSize() {
		return getConfig().getInt("ShopSize");
	}

	/**
	 * Loads the shop name.
	 * @return shop name
	 */
	public String loadShopName() {
		return getConfig().getString("ShopName");
	}

	/**
	 * Loads the shop location.
	 * @return location
	 * @throws TownSystemException
	 */
	public Location loadShopLocation() throws TownSystemException {
		World world = Bukkit.getWorld(getConfig().getString("ShopLocation.World"));
		checkForWorldExists(world);
		return new Location(world, getConfig().getDouble("ShopLocation.x"), getConfig().getDouble("ShopLocation.y"),
				getConfig().getDouble("ShopLocation.z"));
	}

	/**
	 * Loads a shop item.
	 * @param itemString
	 * @return shop item
	 */
	public ShopItem loadItem(String itemString) {
		ItemStack stack = null;
		if (itemString.contains("SPAWNER_")) {
			stack = new ItemStack(Material.SPAWNER, 1);
			ItemMeta meta = stack.getItemMeta();
			String name = getConfig().getString("ShopItems." + itemString + ".Name");
			meta.setDisplayName(name.substring(8));
			stack.setItemMeta(meta);
		} else {
			stack = getConfig().getItemStack("ShopItems." + itemString + ".Name");
		}
		int amount = getConfig().getInt("ShopItems." + itemString + ".Amount");
		double sellPrice = getConfig().getInt("ShopItems." + itemString + ".sellPrice");
		double buyPrice = getConfig().getInt("ShopItems." + itemString + ".buyPrice");
		int slot = getConfig().getInt("ShopItems." + itemString + ".Slot");
		return new ShopItem(stack, amount, sellPrice, buyPrice, slot);
	}

	/**
	 * Loads the item name list.
	 * @return list of strings
	 */
	public List<String> loadItemNameList() {
		removeDefaultItemFromItemList();
		return getConfig().getStringList("ShopItemList");
	}

	/**
	 * Loads the stock of a item.
	 * @param itemString
	 * @return stock
	 */
	public int loadStock(String itemString) {
		return getConfig().getInt("ShopItems." + itemString + ".stock");
	}

	/**
	 * Loads the shop owner.
	 * @param name
	 * @return owner name
	 */
	public String loadOwner(String name) {
		convertToNewOwnerSaving(name);
		return getConfig().getString("Owner");
	}

	/**
	 * Loads if the shop is rentable.
	 * @return rentable
	 */
	public boolean loadRentable() {
		return getConfig().getBoolean("Rentable");
	}

	/**
	 * Loads the rent until time.
	 * @return rent until
	 */
	public long loadRentUntil() {
		return getConfig().getLong("RentUntil");
	}

	/**
	 * Loads the rental fee.
	 * @return rental fee
	 */
	public double loadRentalFee() {
		return getConfig().getDouble("RentalFee");
	}

	private File getSavefile() {
		return file;
	}

	private YamlConfiguration getConfig() {
		return config;
	}

	/**
	 * Deletes the savefile.
	 */
	public void deleteFile() {
		getSavefile().delete();
	}

	private void checkForRenamingSavefileIsPossible(File newFile) throws ShopSystemException {
		if (newFile.exists()) {
			throw ShopSystemException.getException(ShopExceptionMessageEnum.ERROR_ON_RENAMING);
		}
	}

	private void checkForWorldExists(World world) throws TownSystemException {
		if (world == null) {
			throw TownSystemException.getException(TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST, "<unknown>");
		}
	}

	/*
	 * Deprecated methods
	 */

	/**
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void convertToNewOwnerSaving(String name) {
		if (name != null) {
			try {
				EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl
						.getEconomyPlayerByName(name.substring(name.indexOf("_") + 1));
				saveOwner(ecoPlayer);
			} catch (EconomyPlayerException e) {
				Bukkit.getLogger().warning("[Ultimate_Economy] Error on save config to file");
				Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void removeDefaultItemFromItemList() {
		List<String> list = getConfig().getStringList("ShopItemList");
		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			String element = iterator.next();
			if ("ANVIL_0".equals(element) || "CRAFTING_TABLE_0".equals(element)) {
				iterator.remove();
			}
		}
		saveItemNames(list);
	}
}
