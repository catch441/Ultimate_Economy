package org.ue.shopsystem.logic.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.logic.impl.InventoryGuiHandlerImpl;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopEditorHandler;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopsystemException;

public class ShopEditorHandlerImpl extends InventoryGuiHandlerImpl implements ShopEditorHandler {

	private AbstractShop shop;

	/**
	 * Constructor for a new shop editor handler.
	 * 
	 * @param serverProvider
	 * @param customSkullService
	 * @param shop
	 */
	public ShopEditorHandlerImpl(ServerProvider serverProvider, CustomSkullService customSkullService,
			AbstractShop shop) {
		super(customSkullService, serverProvider, null);
		this.shop = shop;
		setup(1);
	}

	@Override
	public void setup(int reservedSlots) {
		inventory = shop.createVillagerInventory(shop.getSize(), "Editor");
		List<Integer> slots = IntStream.rangeClosed(0, shop.getSize() - 1 - reservedSlots).boxed()
				.collect(Collectors.toList());
		for (ShopItem item : shop.getItemList()) {
			setOccupied(true, item.getSlot());
			slots.remove((Integer) item.getSlot());
		}
		for (Integer i : slots) {
			setOccupied(false, i);
		}
		setItem(Material.CRAFTING_TABLE, null, ChatColor.GOLD + "Customize", shop.getSize() - 1);
	}

	@Override
	public void handleInventoryClick(ClickType clickType, int rawSlot, EconomyPlayer whoClicked) {
		if (rawSlot < (shop.getSize() - 1)) {
			try {		
				shop.getSlotEditorHandler(rawSlot).openInventory(whoClicked.getPlayer());	
			} catch (ShopsystemException e) {
			}
		} else if (rawSlot == (shop.getSize() - 1)) {
			shop.getCustomizeGuiHandler().openInventory(whoClicked.getPlayer());
		}
	}

	@Override
	public void setOccupied(boolean occupied, int slot) {
		// +1 for player readable
		if (occupied) {
			setSkull(SkullTextureEnum.SLOTFILLED, null, "Slot " + (slot + 1), slot);
		} else {
			setSkull(SkullTextureEnum.SLOTEMPTY, null, "Slot " + (slot + 1), slot);
		}
	}
}
