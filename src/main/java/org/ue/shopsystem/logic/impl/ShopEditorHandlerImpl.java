package org.ue.shopsystem.logic.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopEditorHandler;

public class ShopEditorHandlerImpl implements ShopEditorHandler {

	private final CustomSkullService skullService;
	private Inventory editor;
	private AbstractShop shop;

	/**
	 * Constructor for a new shop editor handler.
	 * 
	 * @param skullService
	 * @param shop
	 */
	public ShopEditorHandlerImpl(CustomSkullService skullService, AbstractShop shop) {
		this.shop = shop;
		this.skullService = skullService;
		setup(1);
	}

	@Override
	public void setup(int reservedSlots) {
		editor = shop.createVillagerInventory(shop.getSize(), shop.getName() + "-Editor");
		for (int i = 0; i < (shop.getSize() - reservedSlots); i++) {
			try {
				shop.getShopItem(i);
				setOccupied(true, i);
			} catch (GeneralEconomyException | EconomyPlayerException | ShopSystemException e) {
				setOccupied(false, i);
			}
		}
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent event) {
		if (event.getRawSlot() < shop.getSize()) {
			ItemMeta clickedItemMeta = event.getCurrentItem().getItemMeta();
			int slot = Integer.valueOf(clickedItemMeta.getDisplayName().substring(5));
			try {
				shop.openSlotEditor((Player) event.getWhoClicked(), slot - 1);
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
		Inventory editorNew = shop.createVillagerInventory(shop.getSize(), newName + "-Editor");
		editorNew.setContents(editor.getContents());
		editor = editorNew;
	}

	@Override
	public void setOccupied(boolean occupied, int slot) {
		// +1 for player readable
		if (occupied) {
			getEditorInventory().setItem(slot,
					skullService.getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot " + (slot + 1)));
		} else {
			getEditorInventory().setItem(slot,
					skullService.getSkullWithName(SkullTextureEnum.SLOTEMPTY, "Slot " + (slot + 1)));
		}
	}
}
