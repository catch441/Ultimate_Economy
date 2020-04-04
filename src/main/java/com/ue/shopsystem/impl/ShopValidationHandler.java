package com.ue.shopsystem.impl;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.ShopExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownExceptionMessageEnum;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.shopsystem.api.PlayershopController;
import com.ue.townsystem.town.api.Town;
import com.ue.townsystem.townworld.api.Townworld;
import com.ue.townsystem.townworld.api.TownworldController;

public class ShopValidationHandler {

    /**
     * Check for one price greater then zero if both prices are available.
     * @param sellPrice
     * @param buyPrice
     * @throws ShopSystemException
     */
    public void checkForOnePriceGreaterThenZeroIfBothAvailable(String sellPrice, String buyPrice)
	    throws ShopSystemException {
	if (!"none".equals(sellPrice) && !"none".equals(buyPrice) && Double.valueOf(sellPrice) == 0
		&& Double.valueOf(buyPrice) == 0) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_PRICES);
	}
    }

    /**
     * Check for at least one price greater then zero.
     * @param sellPrice
     * @param buyPrice
     * @throws ShopSystemException
     */
    public void checkForPricesGreaterThenZero(double sellPrice, double buyPrice) throws ShopSystemException {
	if (buyPrice == 0 && sellPrice == 0) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_PRICES);
	}
    }

    public void checkForSlotIsNotEmpty(int slot) throws GeneralEconomyException, ShopSystemException {
	if (isSlotEmpty(slot)) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVENTORY_SLOT_EMPTY);
	}
    }

    public void checkForSlotIsEmpty(int slot) throws PlayerException, GeneralEconomyException, ShopSystemException {
	if (!isSlotEmpty(slot)) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED);
	}
    }

    /**
     * Check for valid amount.
     * @param amount
     * @throws GeneralEconomyException
     */
    public void checkForValidAmount(String amount) throws GeneralEconomyException {
	if (!"none".equals(amount) && (Integer.valueOf(amount) <= 0 || Integer.valueOf(amount) > 64)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, amount);
	}
    }

    /**
     * Check for valid price.
     * @param price
     * @throws GeneralEconomyException
     */
    public void checkForValidBuyPrice(String price) throws GeneralEconomyException {
	if (!"none".equals(price) && Double.valueOf(price) < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, price);
	}
    }

    public void checkForItemExists(String itemName) throws ShopSystemException {
	if (!getItemList().contains(itemName)) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_DOES_NOT_EXIST);
	}
    }

    /**
     * Check for valid size.
     * @param size
     * @throws GeneralEconomyException
     */
    public void checkForValidSize(int size) throws GeneralEconomyException {
	if (size % 9 != 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, size);
	}
    }

    public void checkForValidSlot(int slot) throws GeneralEconomyException {
	if (slot > (getSize() - 1)) {
	    // +1 for player readable style
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, slot + 1);
	}
    }

    public void checkForResizePossible(int newSize, int reservedSlots)
	    throws ShopSystemException, GeneralEconomyException {
	int diff = getSize() - newSize;
	// number of reserved slots
	if (getSize() > newSize) {
	    for (int i = 1; i <= diff; i++) {
		ItemStack stack = getShopInventory().getItem(getSize() - i - reservedSlots);
		if (stack != null && stack.getType() != Material.AIR) {
		    throw ShopSystemException.getException(ShopExceptionMessageEnum.RESIZING_FAILED);
		}
	    }
	}
    }

    /**
     * Check for world exists.
     * @param world
     * @throws TownSystemException
     */
    public void checkForWorldExists(World world) throws TownSystemException {
	YamlConfiguration config = YamlConfiguration.loadConfiguration(getSavefileHandler().getSaveFile());
	if (world == null) {
	    throw TownSystemException.getException(TownExceptionMessageEnum.WORLD_DOES_NOT_EXIST,
		    config.getString("ShopLocation.World"));
	}
    }

    /**
     * Check for item does not exist in shop.
     * @param itemString
     * @param itemList
     * @throws ShopSystemException
     */
    public void checkForItemDoesNotExist(String itemString, List<String> itemList) throws ShopSystemException {
	if (itemList.contains(itemString)) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_ALREADY_EXISTS);
	}
    }

    public void checkForItemCanBeDeleted(int slot) throws ShopSystemException {
	if ((slot + 1) == getSize()) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ITEM_CANNOT_BE_DELETED);
	}
    }
    
    /**
     * Check for positive value.
     * @param value
     * @throws GeneralEconomyException
     */
    public void checkForPositiveValue(double value) throws GeneralEconomyException {
	if (value < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, value);
	}
    }

    /**
     * Check for valid stock decrease.
     * @param entireStock
     * @param stock
     * @throws GeneralEconomyException
     */
    public void checkForValidStockDecrease(int entireStock, int stock) throws GeneralEconomyException {
	if ((entireStock - stock) < 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, stock);
	}
    }

    public void checkForChangeOwnerIsPossible(EconomyPlayer newOwner) throws ShopSystemException {
	if (PlayershopController.getPlayerShopUniqueNameList().contains(getName() + "_" + newOwner.getName())) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.SHOP_CHANGEOWNER_ERROR);
	}
    }

    /**
     * Check for valid shop name.
     * @param name
     * @throws ShopSystemException
     */
    public void checkForValidShopName(String name) throws ShopSystemException {
	if (name.contains("_")) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
	}
    }

    public void checkForShopNameIsFree(String name) throws GeneralEconomyException {
	if (PlayershopController.getPlayerShopUniqueNameList().contains(name + "_" + getOwner().getName())
		|| PlayershopController.getPlayerShopUniqueNameList().contains(name)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS,
		    name + getOwner().getName());
	}
    }

    public void checkForPlayerHasPermissionInLocation(Location location) throws PlayerException, TownSystemException {
	Townworld townworld = TownworldController.getTownWorldByName(location.getWorld().getName());
	if (townworld.isChunkFree(location.getChunk())) {
	    throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	} else {
	    Town town = townworld.getTownByChunk(location.getChunk());
	    if (!town.hasBuildPermissions(getOwner(),
		    town.getPlotByChunk(location.getChunk().getX() + "/" + location.getChunk().getZ()))) {
		throw PlayerException.getException(PlayerExceptionMessageEnum.NO_PERMISSION);
	    }
	}
    }

    public void checkForValidSlot(int slot) throws GeneralEconomyException {
	if (slot > (getSize() - 2) || slot < 0) {
	    // +1 for player readable style
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, slot + 1);
	}
    }
    
    /**
     * Check for is rentable.
     * @param isRentable
     * @throws ShopSystemException
     */
    public void checkForIsRentable(boolean isRentable) throws ShopSystemException {
	if (!isRentable) {
	    throw ShopSystemException.getException(ShopExceptionMessageEnum.ALREADY_RENTED);
	}
    }
}
