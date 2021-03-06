package org.ue.economyvillager.logic.impl;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.logic.impl.GeneralValidatorImpl;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyvillager.logic.api.EconomyVillagerValidator;

public abstract class EconomyVillagerValidatorImpl<T extends GeneralEconomyException>
		extends GeneralValidatorImpl<T> implements EconomyVillagerValidator<T> {

	protected final ServerProvider serverProvider;

	public EconomyVillagerValidatorImpl(ServerProvider serverProvider, MessageWrapper messageWrapper) {
		super(messageWrapper);
		this.serverProvider = serverProvider;
	}

	@Override
	public void checkForResizePossible(Inventory inventory, int oldSize, int newSize, int reservedSlots) throws T {
		int diff = oldSize - newSize;
		if (oldSize > newSize) {
			for (int i = 1; i <= diff; i++) {
				ItemStack stack = inventory.getItem(oldSize - i - reservedSlots);
				if (stack != null && stack.getType() != Material.AIR) {
					throw createNew(messageWrapper, ExceptionMessageEnum.RESIZING_FAILED);
				}
			}
		}
	}

	@Override
	public void checkForSlotIsEmpty(Set<Integer> occupiedSlots, int slot) throws T {
		if (occupiedSlots.contains(slot)) {
			// + 1 for human readability
			throw createNew(messageWrapper, ExceptionMessageEnum.SLOT_OCCUPIED, slot + 1);
		}
	}

	@Override
	public void checkForSlotIsNotEmpty(Set<Integer> occupiedSlots, int slot) throws T {
		if (!occupiedSlots.contains(slot)) {
			// + 1 for human readability
			throw createNew(messageWrapper, ExceptionMessageEnum.SLOT_EMPTY, slot + 1);
		}
	}
}
