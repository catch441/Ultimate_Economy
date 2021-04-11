package com.ue.shopsystem.logic.impl;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerExceptionMessageEnum;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.api.Town;
import com.ue.townsystem.logic.api.Townworld;
import com.ue.townsystem.logic.api.TownworldManager;
import com.ue.townsystem.logic.impl.TownSystemException;

public class ShopValidationHandlerImpl implements ShopValidationHandler {

	private final MessageWrapper messageWrapper;
	private final ConfigManager configManager;
	private final TownworldManager townworldManager;
	private final GeneralEconomyValidationHandler generalValiator;

	@Inject
	public ShopValidationHandlerImpl(MessageWrapper messageWrapper, ConfigManager configManager,
			TownworldManager townworldManager, GeneralEconomyValidationHandler generalValiator) {
		this.messageWrapper = messageWrapper;
		this.configManager = configManager;
		this.townworldManager = townworldManager;
		this.generalValiator = generalValiator;
	}

	@Override
	public void checkForOnePriceGreaterThenZeroIfBothAvailable(String sellPrice, String buyPrice)
			throws ShopSystemException {
		if (!"none".equals(sellPrice) && !"none".equals(buyPrice)) {
			checkForPricesGreaterThenZero(Double.valueOf(sellPrice), Double.valueOf(buyPrice));
		}
	}

	@Override
	public void checkForPricesGreaterThenZero(double sellPrice, double buyPrice) throws ShopSystemException {
		if (buyPrice == 0 && sellPrice == 0) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.INVALID_PRICES);
		}
	}

	@Override
	public void checkForSlotIsNotEmpty(int slot, Inventory inventory, int reservedSlots)
			throws GeneralEconomyException, ShopSystemException {
		if (isSlotEmpty(slot, inventory, reservedSlots)) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.INVENTORY_SLOT_EMPTY);
		}
	}

	@Override
	public void checkForSlotIsEmpty(int slot, Inventory inventory, int reservedSlots)
			throws GeneralEconomyException, EconomyPlayerException {
		if (!isSlotEmpty(slot, inventory, reservedSlots)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED);
		}
	}

	@Override
	public boolean isSlotEmpty(int slot, Inventory inventory, int reservedSlots) throws GeneralEconomyException {
		generalValiator.checkForValidSlot(slot, inventory.getSize() - reservedSlots);
		boolean isEmpty = false;
		if (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR) {
			isEmpty = true;
		}
		return isEmpty;
	}

	@Override
	public void checkForValidAmount(String amount) throws GeneralEconomyException {
		if (!"none".equals(amount) && (Integer.valueOf(amount) <= 0 || Integer.valueOf(amount) > 64)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					amount);
		}
	}

	@Override
	public void checkForValidPrice(String price) throws GeneralEconomyException {
		if (!"none".equals(price) && Double.valueOf(price) < 0) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					price);
		}
	}

	@Override
	public void checkForResizePossible(Inventory inventory, int oldSize, int newSize, int reservedSlots)
			throws ShopSystemException, GeneralEconomyException {
		int diff = oldSize - newSize;
		generalValiator.checkForValidSize(newSize);
		if (oldSize > newSize) {
			for (int i = 1; i <= diff; i++) {
				ItemStack stack = inventory.getItem(oldSize - i - reservedSlots);
				if (stack != null && stack.getType() != Material.AIR) {
					throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.RESIZING_FAILED);
				}
			}
		}
	}

	@Override
	public void checkForItemDoesNotExist(int itemHash, List<ShopItem> itemList) throws ShopSystemException {
		for (ShopItem item : itemList) {
			if (item.getItemHash() == itemHash) {
				throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.ITEM_ALREADY_EXISTS);
			}
		}
	}

	@Override
	public void checkForItemCanBeDeleted(int slot, int size) throws ShopSystemException {
		if ((slot + 1) == size) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.ITEM_CANNOT_BE_DELETED);
		}
	}

	@Override
	public void checkForValidStockDecrease(int entireStock, int stock) throws ShopSystemException {
		if ((entireStock - stock) < 0 || entireStock == 0) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.ITEM_UNAVAILABLE);
		}
	}

	@Override
	public void checkForChangeOwnerIsPossible(List<String> uniqueShopNameList, EconomyPlayer newOwner, String shopName)
			throws ShopSystemException {
		if (uniqueShopNameList.contains(shopName + "_" + newOwner.getName())) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.SHOP_CHANGEOWNER_ERROR);
		}
	}

	@Override
	public void checkForValidShopName(String name) throws ShopSystemException {
		if (name.contains("_")) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
		}
	}

	@Override
	public void checkForShopNameIsFree(List<String> shopNames, String name, EconomyPlayer owner)
			throws GeneralEconomyException {
		String suffix = "";
		if (owner != null) {
			suffix = "_" + owner.getName();
		}
		if (shopNames.contains(name + suffix)) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS,
					name + suffix);
		}
	}

	@Override
	public void checkForPlayerHasPermissionAtLocation(Location location, EconomyPlayer owner)
			throws EconomyPlayerException, TownSystemException {
		Townworld townworld = townworldManager.getTownWorldByName(location.getWorld().getName());
		if (townworld.isChunkFree(location.getChunk())) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
		} else {
			Town town = townworld.getTownByChunk(location.getChunk());
			if (!town.hasBuildPermissions(owner,
					town.getPlotByChunk(location.getChunk().getX() + "/" + location.getChunk().getZ()))) {
				throw new EconomyPlayerException(messageWrapper,
						EconomyPlayerExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
			}
		}
	}

	@Override
	public void checkForIsRentable(boolean isRentable) throws ShopSystemException {
		if (!isRentable) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.ALREADY_RENTED);
		}
	}

	@Override
	public void checkForIsRented(boolean isRentable) throws ShopSystemException {
		if (isRentable) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.NOT_RENTED);
		}
	}

	@Override
	public void checkForPlayerIsOnline(EconomyPlayer ecoPlayer) throws EconomyPlayerException {
		if (!ecoPlayer.isOnline()) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.NOT_ONLINE);
		}
	}

	@Override
	public void checkForPlayerInventoryNotFull(Inventory inventory) throws EconomyPlayerException {
		if (inventory.firstEmpty() == -1) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.INVENTORY_FULL);
		}
	}

	@Override
	public void checkForShopOwnerHasEnoughMoney(EconomyPlayer ecoPlayer, double money)
			throws GeneralEconomyException, ShopSystemException {
		if (!ecoPlayer.hasEnoughtMoney(money)) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.SHOPOWNER_NOT_ENOUGH_MONEY);
		}
	}

	@Override
	public void checkForRenamingSavefileIsPossible(File newFile) throws ShopSystemException {
		if (newFile.exists()) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.ERROR_ON_RENAMING);
		}
	}

	@Override
	public void checkForMaxPlayershopsForPlayer(List<Playershop> shopList, EconomyPlayer ecoPlayer)
			throws EconomyPlayerException {
		int actualNumber = 0;
		for (Playershop shop : shopList) {
			if (shop.isOwner(ecoPlayer)) {
				actualNumber++;
			}
		}
		if (actualNumber >= configManager.getMaxPlayershops()) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.MAX_REACHED);
		}
	}
}
