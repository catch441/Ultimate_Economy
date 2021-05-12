package org.ue.shopsystem.logic.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopEditorHandler;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopsystemException;

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
		this.serverProvider = serverProvider;
		this.shop = shop;
		this.skullService = skullService;
		setup(1);
	}

	@Override
	public void setup(int reservedSlots) {
		editor = shop.createVillagerInventory(shop.getSize(), shop.getName() + "-Editor");
		List<Integer> slots = IntStream.rangeClosed(0, shop.getSize() - 1 - reservedSlots).boxed()
				.collect(Collectors.toList());
		for (ShopItem item : shop.getItemList()) {
			setOccupied(true, item.getSlot());
			slots.remove((Integer) item.getSlot());
		}
		for (Integer i : slots) {
			setOccupied(false, i);
		}

		setItem(Material.CRAFTING_TABLE, ChatColor.GOLD + "Customize", shop.getSize() - 1);
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (event.getRawSlot() < (shop.getSize()-1)) {
			ItemMeta clickedItemMeta = event.getCurrentItem().getItemMeta();
			int slot = Integer.valueOf(clickedItemMeta.getDisplayName().substring(5));
			try {
				shop.openSlotEditor(player, slot - 1);
			} catch (ShopsystemException e) {
			}
		} else if(event.getRawSlot() == (shop.getSize() - 1)) {
			shop.openCustomizer(player);
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

	private void setItem(Material material, String displayName, int slot) {
		ItemStack stack = serverProvider.createItemStack(material, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(displayName);
		stack.setItemMeta(meta);
		editor.setItem(slot, stack);
	}
}
