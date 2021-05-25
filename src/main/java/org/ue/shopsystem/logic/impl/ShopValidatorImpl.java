package org.ue.shopsystem.logic.impl;

import java.io.File;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyvillager.logic.impl.EconomyVillagerValidatorImpl;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;
import org.ue.townsystem.logic.api.Town;
import org.ue.townsystem.logic.api.TownsystemException;
import org.ue.townsystem.logic.api.Townworld;
import org.ue.townsystem.logic.api.TownworldManager;

public class ShopValidatorImpl extends EconomyVillagerValidatorImpl<ShopsystemException>
		implements ShopValidator {

	private final ConfigManager configManager;
	private final TownworldManager townworldManager;

	public ShopValidatorImpl(ServerProvider serverProvider, MessageWrapper messageWrapper,
			ConfigManager configManager, TownworldManager townworldManager) {
		super(serverProvider, messageWrapper);
		this.configManager = configManager;
		this.townworldManager = townworldManager;
	}

	@Override
	protected ShopsystemException createNew(MessageWrapper messageWrapper, ExceptionMessageEnum key, Object... params) {
		return new ShopsystemException(messageWrapper, key, params);
	}

	@Override
	public void checkForPricesGreaterThenZero(double sellPrice, double buyPrice) throws ShopsystemException {
		if (buyPrice == 0 && sellPrice == 0) {
			throw createNew(messageWrapper, ExceptionMessageEnum.INVALID_PRICES);
		}
	}

	@Override
	public void checkForValidAmount(int amount) throws ShopsystemException {
		if (amount <= 0 || Integer.valueOf(amount) > 64) {
			throw createNew(messageWrapper, ExceptionMessageEnum.INVALID_PARAMETER, amount);
		}
	}

	@Override
	public void checkForItemCanBeDeleted(int slot, int size) throws ShopsystemException {
		if ((slot + 1) == size) {
			throw createNew(messageWrapper, ExceptionMessageEnum.ITEM_CANNOT_BE_DELETED);
		}
	}

	@Override
	public void checkForValidStockDecrease(int entireStock, int stock) throws ShopsystemException {
		if ((entireStock - stock) < 0 || entireStock == 0) {
			throw createNew(messageWrapper, ExceptionMessageEnum.ITEM_UNAVAILABLE);
		}
	}

	@Override
	public void checkForChangeOwnerIsPossible(List<String> uniqueShopNameList, EconomyPlayer newOwner, String shopName)
			throws ShopsystemException {
		if (uniqueShopNameList.contains(shopName + "_" + newOwner.getName())) {
			throw createNew(messageWrapper, ExceptionMessageEnum.SHOP_CHANGEOWNER_ERROR);
		}
	}

	@Override
	public void checkForValidShopName(String name) throws ShopsystemException {
		if (name.contains("_")) {
			throw createNew(messageWrapper, ExceptionMessageEnum.INVALID_CHAR_IN_SHOP_NAME);
		}
	}

	@Override
	public void checkForShopNameIsFree(List<String> shopNames, String name, EconomyPlayer owner)
			throws ShopsystemException {
		String suffix = "";
		if (owner != null) {
			suffix = "_" + owner.getName();
		}
		if (shopNames.contains(name + suffix)) {
			throw createNew(messageWrapper, ExceptionMessageEnum.ALREADY_EXISTS, name + suffix);
		}
	}

	@Override
	public void checkForPlayerHasPermissionAtLocation(Location location, EconomyPlayer owner)
			throws ShopsystemException {
		try {
			Townworld townworld = townworldManager.getTownWorldByName(location.getWorld().getName());
			if (townworld.isChunkFree(location.getChunk())) {
				throw createNew(messageWrapper, ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
			} else {
				Town town = townworld.getTownByChunk(location.getChunk());
				if (!town.hasBuildPermissions(owner,
						town.getPlotByChunk(location.getChunk().getX() + "/" + location.getChunk().getZ()))) {
					throw createNew(messageWrapper, ExceptionMessageEnum.YOU_HAVE_NO_PERMISSION);
				}
			}
		} catch (TownsystemException e) {
			// no townworld or has permission
		}
	}

	@Override
	public void checkForIsRentable(boolean isRentable) throws ShopsystemException {
		if (!isRentable) {
			throw createNew(messageWrapper, ExceptionMessageEnum.ALREADY_RENTED);
		}
	}

	@Override
	public void checkForIsRented(boolean isRentable) throws ShopsystemException {
		if (isRentable) {
			throw createNew(messageWrapper, ExceptionMessageEnum.NOT_RENTED);
		}
	}

	@Override
	public void checkForPlayerIsOnline(EconomyPlayer ecoPlayer) throws ShopsystemException {
		if (!ecoPlayer.isOnline()) {
			throw createNew(messageWrapper, ExceptionMessageEnum.NOT_ONLINE);
		}
	}

	@Override
	public void checkForPlayerInventoryNotFull(Inventory inventory) throws ShopsystemException {
		if (inventory.firstEmpty() == -1) {
			throw createNew(messageWrapper, ExceptionMessageEnum.INVENTORY_FULL);
		}
	}

	@Override
	public void checkForShopOwnerHasEnoughMoney(EconomyPlayer ecoPlayer, double money) throws ShopsystemException {
		if (ecoPlayer.getBankAccount().getAmount() < money) {
			throw createNew(messageWrapper, ExceptionMessageEnum.SHOPOWNER_NOT_ENOUGH_MONEY);
		}
	}

	@Override
	public void checkForRenamingSavefileIsPossible(File newFile) throws ShopsystemException {
		if (newFile.exists()) {
			throw createNew(messageWrapper, ExceptionMessageEnum.ERROR_ON_RENAMING);
		}
	}

	@Override
	public void checkForMaxPlayershopsForPlayer(List<Playershop> shopList, EconomyPlayer ecoPlayer)
			throws ShopsystemException {
		int actualNumber = 0;
		for (Playershop shop : shopList) {
			if (shop.isOwner(ecoPlayer)) {
				actualNumber++;
			}
		}
		if (actualNumber >= configManager.getMaxPlayershops()) {
			throw createNew(messageWrapper, ExceptionMessageEnum.MAX_REACHED);
		}
	}
}
