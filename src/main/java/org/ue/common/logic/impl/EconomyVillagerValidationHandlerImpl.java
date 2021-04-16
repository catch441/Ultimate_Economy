package org.ue.common.logic.impl;

import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ue.common.logic.api.EconomyVillagerValidationHandler;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.economyplayer.logic.EconomyPlayerExceptionMessageEnum;
import org.ue.general.GeneralEconomyException;
import org.ue.shopsystem.logic.ShopExceptionMessageEnum;
import org.ue.shopsystem.logic.ShopSystemException;

public class EconomyVillagerValidationHandlerImpl extends GeneralValidationHandlerImpl
		implements EconomyVillagerValidationHandler {

	@Inject
	public EconomyVillagerValidationHandlerImpl(MessageWrapper messageWrapper) {
		super(messageWrapper);
	}
	
	@Override
	public void checkForSlotIsEmpty(Inventory inventory, int slot) throws EconomyPlayerException {
		if (!isSlotEmpty(inventory, slot)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.INVENTORY_SLOT_OCCUPIED);
		}
	}
	
	@Override
	public void checkForSlotIsNotEmpty(int slot, Inventory inventory)
			throws EconomyPlayerException {
		if (isSlotEmpty(inventory, slot)) {
			throw new EconomyPlayerException(messageWrapper, EconomyPlayerExceptionMessageEnum.INVENTORY_SLOT_EMPTY);
		}
	}
	
	@Override
	public void checkForResizePossible(Inventory inventory, int oldSize, int newSize, int reservedSlots)
			throws ShopSystemException, GeneralEconomyException {
		int diff = oldSize - newSize;
		checkForValidSize(newSize);
		if (oldSize > newSize) {
			for (int i = 1; i <= diff; i++) {
				ItemStack stack = inventory.getItem(oldSize - i - reservedSlots);
				if (stack != null && stack.getType() != Material.AIR) {
					throw new ShopSystemException(messageWrapper, ShopExceptionMessageEnum.RESIZING_FAILED);
				}
			}
		}
	}

	protected boolean isSlotEmpty(Inventory inventory, int slot) {
		return (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR);
	}
}
