package com.ue.shopsystem.dataaccess.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.common.utils.SaveFileUtils;
import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ShopDaoImpl extends SaveFileUtils implements ShopDao {

	private final ServerProvider serverProvider;
	private final EconomyPlayerManager ecoPlayerManager;
	private final ShopValidationHandler validationHandler;
	private final TownsystemValidationHandler townsystemValidationHandler;

	@Override
	public void setupSavefile(String shopId) {
		file = new File(serverProvider.getDataFolderPath(), shopId + ".yml");
		if (!file.exists()) {
			createFile(file);
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
			getConfig().set("ShopItems." + shopItem.getItemHash(), null);
			save(getConfig(), getSavefile());
		} else {
			if (shopItem.getItemStack().getType() == Material.SPAWNER) {
				String entityType = shopItem.getItemStack().getItemMeta().getDisplayName();
				getConfig().set("ShopItems." + shopItem.getItemHash() + ".Name", "SPAWNER_" + entityType);
			} else {
				getConfig().set("ShopItems." + shopItem.getItemHash() + ".Name", shopItem.getItemStack());
			}
			getConfig().set("ShopItems." + shopItem.getItemHash() + ".Slot", shopItem.getSlot());
			getConfig().set("ShopItems." + shopItem.getItemHash() + ".newSaveMethod", "true");
			save(getConfig(), getSavefile());
			saveShopItemSellPrice(shopItem.getItemHash(), shopItem.getSellPrice());
			saveShopItemBuyPrice(shopItem.getItemHash(), shopItem.getBuyPrice());
			saveShopItemAmount(shopItem.getItemHash(), shopItem.getAmount());
		}
	}

	@Override
	public void saveShopItemSellPrice(int itemHash, double sellPrice) {
		getConfig().set("ShopItems." + itemHash + ".sellPrice", sellPrice);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveShopItemBuyPrice(int itemHash, double buyPrice) {
		getConfig().set("ShopItems." + itemHash + ".buyPrice", buyPrice);
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveShopItemAmount(int itemHash, int amount) {
		getConfig().set("ShopItems." + itemHash + ".Amount", amount);
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
	public void saveProfession(Profession profession) {
		getConfig().set("Profession", profession.name());
		save(getConfig(), getSavefile());
	}

	@Override
	public void saveStock(int itemHash, int stock) {
		getConfig().set("ShopItems." + itemHash + ".stock", stock);
		save(getConfig(), getSavefile());
	}

	// TODO UE-142
	/*@Override
	/**
	 * Saves a shopitem player specific. Not possible with spawners.
	 * 
	 * @param shopItem
	 * @param playerName
	 * @param delete
	 *
	public void saveShopItemStockPlayerSpecific(ShopItem shopItem, String playerName, boolean delete) {
		if(delete) {
			getConfig().set("Storage." + playerName + shopItem.getItemHash(), null);
		} else {
			getConfig().set("Storage." + playerName + shopItem.getItemHash() + ".Name", shopItem.getItemStack());
			getConfig().set("Storage." + playerName + shopItem.getItemHash() + ".Slot", shopItem.getSlot());
			getConfig().set("Storage." + playerName + + shopItem.getItemHash() + ".stock", shopItem.getStock());
			getConfig().set("Storage." + playerName + + shopItem.getItemHash() + ".Amount", shopItem.getAmount());
			getConfig().set("Storage." + playerName + + shopItem.getItemHash() + ".sellPrice", shopItem.getSellPrice());
			getConfig().set("Storage." + playerName + + shopItem.getItemHash() + ".buyPrice", shopItem.getBuyPrice());
		}
		save(getConfig(), getSavefile());
	}
	
	@Override
	/**
	 * . Not possible with spawners.
	 * @param playerName
	 * @param itemHash
	 * @return
	 *
	public ShopItem loadItemPlayerSpecific(String playerName, int itemHash) {
		ItemStack stack = null;
		if (getConfig().getString("ShopItems." + itemHash + ".Name").contains("SPAWNER_")) {
			stack = serverProvider.createItemStack(Material.SPAWNER, 1);
			ItemMeta meta = stack.getItemMeta();
			String name = getConfig().getString("ShopItems." + itemHash + ".Name");
			meta.setDisplayName(name.substring(8));
			stack.setItemMeta(meta);
		} else {
			stack = getConfig().getItemStack("ShopItems." + itemHash + ".Name");
		}
		int amount = getConfig().getInt("ShopItems." + itemHash + ".Amount");
		double sellPrice = getConfig().getInt("ShopItems." + itemHash + ".sellPrice");
		double buyPrice = getConfig().getInt("ShopItems." + itemHash + ".buyPrice");
		int slot = getConfig().getInt("ShopItems." + itemHash + ".Slot");
		return new ShopItem(stack, amount, sellPrice, buyPrice, slot);
	}*/

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
	public void saveExpiresAt(long rentUntil) {
		getConfig().set("expiresAt", rentUntil);
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
		return new Location(serverProvider.getWorld(world), getConfig().getDouble("ShopLocation.x"),
				getConfig().getDouble("ShopLocation.y"), getConfig().getDouble("ShopLocation.z"));
	}

	@Override
	public ShopItem loadItem(int itemHash) {
		ItemStack stack = null;
		if (getConfig().getString("ShopItems." + itemHash + ".Name").contains("SPAWNER_")) {
			stack = serverProvider.createItemStack(Material.SPAWNER, 1);
			ItemMeta meta = stack.getItemMeta();
			String name = getConfig().getString("ShopItems." + itemHash + ".Name");
			meta.setDisplayName(name.substring(8));
			stack.setItemMeta(meta);
		} else {
			stack = getConfig().getItemStack("ShopItems." + itemHash + ".Name");
		}
		int amount = getConfig().getInt("ShopItems." + itemHash + ".Amount");
		double sellPrice = getConfig().getInt("ShopItems." + itemHash + ".sellPrice");
		double buyPrice = getConfig().getInt("ShopItems." + itemHash + ".buyPrice");
		int slot = getConfig().getInt("ShopItems." + itemHash + ".Slot");
		return new ShopItem(stack, amount, sellPrice, buyPrice, slot);
	}

	@Override
	public List<Integer> loadItemHashList() {
		removeShopItemList();
		convertToItemHash();
		if (config.getConfigurationSection("ShopItems") != null) {
			Set<String> keySet = config.getConfigurationSection("ShopItems").getKeys(false);
			Set<Integer> setOfInteger = keySet.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toSet());
			return new ArrayList<Integer>(setOfInteger);
		}
		return new ArrayList<>();
	}

	@Override
	public int loadStock(int itemHash) {
		return getConfig().getInt("ShopItems." + itemHash + ".stock");
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
	public long loadExpiresAt() {
		convertToIngameTime();
		return getConfig().getLong("expiresAt");
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
	
	/**
	 * @since 1.2.7
	 * @param rentUntil
	 */
	@Deprecated
	private void convertToIngameTime() {
		if (!getConfig().isSet("expiresAt")) {
			long oldTime = getConfig().getLong("RentUntil");
			long yetSystem = serverProvider.getSystemTime();
			long yetMc = serverProvider.getWorldTime();
			long newTime = (long) ((oldTime-yetSystem)*0.02)+yetMc;
			getConfig().set("RentUntil", null);
			getConfig().set("expiresAt", newTime);
			save(getConfig(), getSavefile());
		}
	}

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
			} catch (GeneralEconomyException e) {
				log.warn("[Ultimate_Economy] Error on save config to file");
				log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
			}
		}
	}

	/**
	 * @since 1.2.6
	 * @deprecated can be removed later
	 */
	@Deprecated
	private void removeShopItemList() {
		getConfig().set("ShopItemList", null);
		save(getConfig(), getSavefile());
	}

	@Override
	@Deprecated
	public boolean removeIfCorrupted(int itemHash) {
		if (!getConfig().isSet("ShopItems." + itemHash + ".Name")) {
			getConfig().set("ShopItems." + itemHash, null);
			save(getConfig(), getSavefile());
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
	private void convertToItemHash() {
		if (getConfig().getConfigurationSection("ShopItems") != null) {
			Set<String> keySet = config.getConfigurationSection("ShopItems").getKeys(false);
			for (String key : keySet) {
				try {
					Integer.valueOf(key);
				} catch (NumberFormatException e) {
					// convert to new format
					Map<String, Object> vals = config.getConfigurationSection("ShopItems." + key).getValues(true);
					for (String s : vals.keySet()) {
						Object val = vals.get(s);
						if (val instanceof List) {
							val = new ArrayList<Object>((List<Object>) val);
						}
						getConfig().set("ShopItems." + key.hashCode() + "." + s, val);
					}
					getConfig().set("ShopItems." + key, null);
					save(getConfig(), getSavefile());
				}
			}
		}
	}
}
