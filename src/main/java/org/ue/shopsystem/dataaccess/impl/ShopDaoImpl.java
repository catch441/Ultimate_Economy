package org.ue.shopsystem.dataaccess.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyvillager.dataaccess.impl.EconomyVillagerDaoImpl;
import org.ue.shopsystem.dataaccess.api.ShopDao;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.impl.ShopItemImpl;

public class ShopDaoImpl extends EconomyVillagerDaoImpl implements ShopDao {

	public ShopDaoImpl(ServerProvider serverProvider) {
		super(serverProvider);
	}

	@Override
	public void setupSavefile(String shopId) {
		file = new File(serverProvider.getDataFolderPath(), shopId + ".yml");
		if (!file.exists()) {
			createFile(file);
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public void saveShopName(String name) {
		config.set("ShopName", name);
		save();
	}

	@Override
	public void saveShopItem(ShopItem shopItem, boolean delete) {
		if (delete) {
			config.set("ShopItems." + shopItem.getSlot(), null);
			save();
		} else {
			config.set("ShopItems." + shopItem.getSlot() + ".Name", shopItem.getItemStack());
			save();
			saveShopItemSellPrice(shopItem.getSlot(), shopItem.getSellPrice());
			saveShopItemBuyPrice(shopItem.getSlot(), shopItem.getBuyPrice());
			saveShopItemAmount(shopItem.getSlot(), shopItem.getAmount());
		}
	}

	@Override
	public void saveShopItemSellPrice(int slot, double sellPrice) {
		config.set("ShopItems." + slot + ".sellPrice", sellPrice);
		save();
	}

	@Override
	public void saveShopItemBuyPrice(int slot, double buyPrice) {
		config.set("ShopItems." + slot + ".buyPrice", buyPrice);
		save();
	}

	@Override
	public void saveShopItemAmount(int slot, int amount) {
		config.set("ShopItems." + slot + ".Amount", amount);
		save();
	}

	@Override
	public void saveStock(int slot, int stock) {
		config.set("ShopItems." + slot + ".stock", stock);
		save();
	}

	// TODO UE-142
	/*
	 * @Override /** Saves a shopitem player specific. Not possible with spawners.
	 * 
	 * @param shopItem
	 * 
	 * @param playerName
	 * 
	 * @param delete
	 *
	 * public void saveShopItemStockPlayerSpecific(ShopItem shopItem, String
	 * playerName, boolean delete) { if(delete) { config.set("Storage." +
	 * playerName + shopItem.getItemHash(), null); } else {
	 * config.set("Storage." + playerName + shopItem.getItemHash() + ".Name",
	 * shopItem.getItemStack()); config.set("Storage." + playerName +
	 * shopItem.getItemHash() + ".Slot", shopItem.getSlot());
	 * config.set("Storage." + playerName + + shopItem.getItemHash() +
	 * ".stock", shopItem.getStock()); config.set("Storage." + playerName + +
	 * shopItem.getItemHash() + ".Amount", shopItem.getAmount());
	 * config.set("Storage." + playerName + + shopItem.getItemHash() +
	 * ".sellPrice", shopItem.getSellPrice()); config.set("Storage." +
	 * playerName + + shopItem.getItemHash() + ".buyPrice", shopItem.getBuyPrice());
	 * } save(); }
	 * 
	 * @Override /** . Not possible with spawners.
	 * 
	 * @param playerName
	 * 
	 * @param itemHash
	 * 
	 * @return
	 *
	 * public ShopItem loadItemPlayerSpecific(String playerName, int itemHash) {
	 * ItemStack stack = null; if (config.getString("ShopItems." + itemHash +
	 * ".Name").contains("SPAWNER_")) { stack =
	 * serverProvider.createItemStack(Material.SPAWNER, 1); ItemMeta meta =
	 * stack.getItemMeta(); String name = config.getString("ShopItems." +
	 * itemHash + ".Name"); meta.setDisplayName(name.substring(8));
	 * stack.setItemMeta(meta); } else { stack =
	 * config.getItemStack("ShopItems." + itemHash + ".Name"); } int amount =
	 * config.getInt("ShopItems." + itemHash + ".Amount"); double sellPrice =
	 * config.getInt("ShopItems." + itemHash + ".sellPrice"); double buyPrice =
	 * config.getInt("ShopItems." + itemHash + ".buyPrice"); int slot =
	 * config.getInt("ShopItems." + itemHash + ".Slot"); return new
	 * ShopItem(stack, amount, sellPrice, buyPrice, slot); }
	 */

	@Override
	public void saveOwner(EconomyPlayer ecoPlayer) {
		if (ecoPlayer != null) {
			config.set("Owner", ecoPlayer.getName());
		} else {
			config.set("Owner", null);
		}
		save();
	}

	@Override
	public void saveExpiresAt(long rentUntil) {
		config.set("expiresAt", rentUntil);
		save();
	}

	@Override
	public void saveRentalFee(double fee) {
		config.set("RentalFee", fee);
		save();
	}

	@Override
	public void saveRentable(boolean isRentable) {
		config.set("Rentable", isRentable);
		save();
	}

	@Override
	public String loadShopName() {
		return config.getString("ShopName");
	}

	@Override
	public ShopItem loadItem(int slot) {
		convertToSpawnerItemSave(slot);
		ItemStack stack = config.getItemStack("ShopItems." + slot + ".Name");
		int amount = config.getInt("ShopItems." + slot + ".Amount");
		double sellPrice = config.getInt("ShopItems." + slot + ".sellPrice");
		double buyPrice = config.getInt("ShopItems." + slot + ".buyPrice");
		return new ShopItemImpl(stack, amount, sellPrice, buyPrice, slot);
	}
	
	/**
	 * @since 1.2.7
	 * @param slot
	 */
	@Deprecated
	private void convertToSpawnerItemSave(int slot) {
		if (config.getItemStack("ShopItems." + slot + ".Name") == null) {
			String name = config.getString("ShopItems." + slot + ".Name");
			if(name.contains("SPAWNER_")) {
				ItemStack stack1 = serverProvider.createItemStack(Material.SPAWNER, 1);
				ItemMeta meta = stack1.getItemMeta();
				meta.setDisplayName(name.substring(8));
				stack1.setItemMeta(meta);	
				config.set("ShopItems." + slot + ".Name", stack1);
			}
			config.set("ShopItems." + slot + ".newSaveMethod", null);
			save();
		}
	}

	@Override
	public List<Integer> loadItemSlotList() {
		removeShopItemList();
		convertToItemSlot();
		if (config.getConfigurationSection("ShopItems") != null) {
			Set<String> keySet = config.getConfigurationSection("ShopItems").getKeys(false);
			Set<Integer> setOfInteger = keySet.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toSet());
			return new ArrayList<Integer>(setOfInteger);
		}
		return new ArrayList<>();
	}

	@Override
	public int loadStock(int itemHash) {
		return config.getInt("ShopItems." + itemHash + ".stock");
	}

	@Override
	public String loadOwner() {
		return config.getString("Owner");
	}

	@Override
	public boolean loadRentable() {
		return config.getBoolean("Rentable");
	}

	@Override
	public long loadExpiresAt() {
		convertToIngameTime();
		return config.getLong("expiresAt");
	}

	@Override
	public double loadRentalFee() {
		return config.getDouble("RentalFee");
	}

	@Override
	public void deleteFile() {
		file.delete();
	}

	/**
	 * @since 1.2.7
	 * @param rentUntil
	 */
	@Deprecated
	private void convertToIngameTime() {
		if (!config.isSet("expiresAt")) {
			long oldTime = config.getLong("RentUntil");
			long yetSystem = serverProvider.getSystemTime();
			long yetMc = serverProvider.getWorldTime();
			long newTime = (long) ((oldTime - yetSystem) * 0.02) + yetMc;
			config.set("RentUntil", null);
			config.set("expiresAt", newTime);
			save();
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void removeShopItemList() {
		config.set("ShopItemList", null);
		save();
	}

	@Override
	@Deprecated
	public boolean removeIfCorrupted(int slot) {
		if (!config.isSet("ShopItems." + slot + ".Name")) {
			config.set("ShopItems." + slot, null);
			save();
			return true;
		}
		return false;
	}

	/**
	 * @since 1.2.7
	 * @deprecated can be removed later
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	private void convertToItemSlot() {
		if (config.getConfigurationSection("ShopItems") != null) {
			Set<String> keySet = config.getConfigurationSection("ShopItems").getKeys(false);
			for (String key : keySet) {
				try {
					Integer.valueOf(key);
				} catch (NumberFormatException e) {
					// convert to new format
					Map<String, Object> vals = config.getConfigurationSection("ShopItems." + key).getValues(true);
					int slot = config.getInt("ShopItems." + key + ".Slot");
					for (String s : vals.keySet()) {
						Object val = vals.get(s);
						if (val instanceof List) {
							val = new ArrayList<Object>((List<Object>) val);
						}
						config.set("ShopItems." + slot + "." + s, val);
					}
					config.set("ShopItems." + key, null);
					config.set("ShopItems." + slot + ".Slot", null);
					save();
				}
			}
		}
	}
}
