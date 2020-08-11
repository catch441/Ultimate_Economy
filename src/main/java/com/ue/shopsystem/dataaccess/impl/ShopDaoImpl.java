package com.ue.shopsystem.dataaccess.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.common.utils.SaveFileUtils;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.ultimate_economy.UltimateEconomy;

public class ShopDaoImpl extends SaveFileUtils implements ShopDao {

	private File file;
	private YamlConfiguration config;
	@Inject
	EconomyPlayerManager ecoPlayerManager;
	@Inject
	ShopValidationHandler validationHandler;
	@Inject
	TownsystemValidationHandler townsystemValidationHandler;

	/**
	 * Constructor for a new shop savefile handler.
	 * 
	 * @param validationHandler
	 * @param messageWrapper
	 * @param ecoPlayerManager
	 * @param shopId
	 */
	public ShopDaoImpl(String shopId) {
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

	@Override
	public void saveShopName(String name) {
		getConfig().set("ShopName", name);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveShopItem(ShopItem shopItem, boolean delete) {
		if (delete) {
			getConfig().set("ShopItems." + shopItem.getItemString(), null);
			save(getConfig(), getSavefile());
		} else {
			if (shopItem.getItemStack().getType() == Material.SPAWNER) {
				getConfig().set("ShopItems." + shopItem.getItemString() + ".Name", shopItem.getItemString());
			} else {
				getConfig().set("ShopItems." + shopItem.getItemString() + ".Name", shopItem.getItemStack());
			}
			getConfig().set("ShopItems." + shopItem.getItemString() + ".Slot", shopItem.getSlot());
			getConfig().set("ShopItems." + shopItem.getItemString() + ".newSaveMethod", "true");
			save(getConfig(), getSavefile());
			saveShopItemSellPrice(shopItem.getItemString(), shopItem.getSellPrice());
			saveShopItemBuyPrice(shopItem.getItemString(), shopItem.getBuyPrice());
			saveShopItemAmount(shopItem.getItemString(), shopItem.getAmount());
		}
	}

	@Override
	public void saveShopItemSellPrice(String itemString, double sellPrice) {
		getConfig().set("ShopItems." + itemString + ".sellPrice", sellPrice);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveShopItemBuyPrice(String itemString, double buyPrice) {
		getConfig().set("ShopItems." + itemString + ".buyPrice", buyPrice);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveShopItemAmount(String itemString, int amount) {
		getConfig().set("ShopItems." + itemString + ".Amount", amount);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveShopSize(int size) {
		getConfig().set("ShopSize", size);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveShopLocation(Location location) {
		getConfig().set("ShopLocation.x", location.getX());
		getConfig().set("ShopLocation.y", location.getY());
		getConfig().set("ShopLocation.z", location.getZ());
		getConfig().set("ShopLocation.World", location.getWorld().getName());
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveItemNames(List<String> itemList) {
		getConfig().set("ShopItemList", itemList);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveProfession(Profession profession) {
		getConfig().set("Profession", profession.name());
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveStock(String itemString, int stock) {
		getConfig().set("ShopItems." + itemString + ".stock", stock);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveOwner(EconomyPlayer ecoPlayer) {
		if (ecoPlayer != null) {
			getConfig().set("Owner", ecoPlayer.getName());
		} else {
			getConfig().set("Owner", null);
		}
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveRentUntil(long rentUntil) {
		getConfig().set("RentUntil", rentUntil);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveRentalFee(double fee) {
		getConfig().set("RentalFee", fee);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveRentable(boolean isRentable) {
		getConfig().set("Rentable", isRentable);
		save(getConfig(), getSavefile());
	}

	@Override
	public void changeSavefileName(File dataFolder, String newName) throws ShopSystemException {
		File newFile = new File(dataFolder, newName + ".yml");
		validationHandler.checkForRenamingSavefileIsPossible(newFile);
		getSavefile().delete();
		file = newFile;
		save(getConfig(), getSavefile());
	}

	@Override
	public Profession loadShopVillagerProfession() {
		if (getConfig().isSet("Profession")) {
			return Profession.valueOf(config.getString("Profession"));
		} else {
			return Profession.NITWIT;
		}
	}

	@Override
	public int loadShopSize() {
		return getConfig().getInt("ShopSize");
	}

	@Override
	public String loadShopName() {
		return getConfig().getString("ShopName");
	}

	@Override
	public Location loadShopLocation() throws TownSystemException {
		String world = getConfig().getString("ShopLocation.World");
		townsystemValidationHandler.checkForWorldExists(world);
		return new Location(Bukkit.getWorld(world), getConfig().getDouble("ShopLocation.x"),
				getConfig().getDouble("ShopLocation.y"), getConfig().getDouble("ShopLocation.z"));
	}

	@Override
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

	@Override
	public List<String> loadItemNameList() {
		removeDefaultItemFromItemList();
		return getConfig().getStringList("ShopItemList");
	}

	@Override
	public int loadStock(String itemString) {
		return getConfig().getInt("ShopItems." + itemString + ".stock");
	}

	@Override
	public String loadOwner(String name) {
		convertToNewOwnerSaving(name);
		return getConfig().getString("Owner");
	}

	@Override
	public boolean loadRentable() {
		return getConfig().getBoolean("Rentable");
	}

	@Override
	public long loadRentUntil() {
		return getConfig().getLong("RentUntil");
	}

	@Override
	public double loadRentalFee() {
		return getConfig().getDouble("RentalFee");
	}

	private File getSavefile() {
		return file;
	}

	private YamlConfiguration getConfig() {
		return config;
	}

	@Override
	public void deleteFile() {
		getSavefile().delete();
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
				EconomyPlayer ecoPlayer = ecoPlayerManager
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
