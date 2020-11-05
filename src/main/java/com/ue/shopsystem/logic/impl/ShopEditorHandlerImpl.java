package com.ue.shopsystem.logic.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.common.api.CustomSkullService;
import com.ue.common.api.SkullTextureEnum;
import com.ue.common.utils.ServerProvider;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.logic.api.AbstractShop;
import com.ue.shopsystem.logic.api.ShopEditorHandler;

public class ShopEditorHandlerImpl implements ShopEditorHandler {

	private final CustomSkullService skullService;
	private final ServerProvider serverProvider;
	private Inventory editor;
	private AbstractShop shop;

	/**
	 * Constructor for a new shop editor handler.
	 * 
	 * @param serverProvider
	 * @param skullService
	 * @param shop
	 */
	public ShopEditorHandlerImpl(ServerProvider serverProvider, CustomSkullService skullService, AbstractShop shop) {
		this.shop = shop;
		this.skullService = skullService;
		this.serverProvider = serverProvider;
		setup(1);
	}

	@Override
	public void setup(int reservedSlots) {
		editor = serverProvider.createInventory(getShop().getShopVillager(), getShop().getSize(),
				getShop().getName() + "-Editor");
		for (int i = 0; i < (getShop().getSize() - reservedSlots); i++) {
			if(getShop().getShopInventory().getItem(i) != null) {
				setOccupied(true, i);
			} else {
				setOccupied(false, i);
			}
		}
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent event) {
		if(event.getRawSlot() < getShop().getSize()) {
			ItemMeta clickedItemMeta = event.getCurrentItem().getItemMeta();
			int slot = Integer.valueOf(clickedItemMeta.getDisplayName().substring(5));
			try {
				getShop().openSlotEditor((Player) event.getWhoClicked(), slot- 1);
			} catch (ShopSystemException | GeneralEconomyException e) {
			}
		}
	}

	@Override
	public Inventory getEditorInventory() {
		return editor;
	}

	@Override
	public void changeInventoryName(String newName) {
		Inventory editorNew = serverProvider.createInventory(getShop().getShopVillager(), getShop().getSize(),
				newName + "-Editor");
		editorNew.setContents(editor.getContents());
		editor = editorNew;
	}

	@Override
	public void setOccupied(boolean occupied, int slot) {
		// +1 for player readable
		if (occupied) {
			getEditorInventory().setItem(slot, skullService.getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot " + (slot + 1)));
		} else {
			getEditorInventory().setItem(slot, skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot " + (slot + 1)));
		}
	}

	private AbstractShop getShop() {
		return shop;
	}
}
