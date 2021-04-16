package org.ue.shopsystem.logic.impl;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.ue.common.logic.impl.EconomyVillagerValidationHandlerImpl;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.economyplayer.logic.EconomyPlayerExceptionMessageEnum;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.shopsystem.logic.ShopExceptionMessageEnum;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopValidationHandler;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;
import org.ue.townsystem.logic.TownSystemException;

public class ShopValidationHandlerImpl extends EconomyVillagerValidationHandlerImpl implements ShopValidationHandler {

	private final ConfigManager configManager;
	private final TownworldManager townworldManager;

	@Inject
	public ShopValidationHandlerImpl(MessageWrapper messageWrapper, ConfigManager configManager,
			TownworldManager townworldManager) {
		super(messageWrapper);
		this.configManager = configManager;
		this.townworldManager = townworldManager;
	}

	@Override
	public void checkForPricesGreaterThenZero(double sellPrice, double buyPrice) throws ShopSystemException {
		if (buyPrice == 0 && sellPrice == 0) {
			throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.INVALID_PRICES);
		}
	}

	@Override
	public void checkForValidAmount(int amount) throws GeneralEconomyException {
		if (amount <= 0 || Integer.valueOf(amount) > 64) {
			throw new GeneralEconomyException(messageWrapper, GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER,
					amount);
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
