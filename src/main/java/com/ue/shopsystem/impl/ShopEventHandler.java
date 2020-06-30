package com.ue.shopsystem.impl;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.api.AbstractShop;
import com.ue.shopsystem.api.AdminshopController;
import com.ue.shopsystem.api.PlayershopController;
import com.ue.shopsystem.api.Rentshop;
import com.ue.shopsystem.api.RentshopController;

public class ShopEventHandler {

	/**
	 * Handles the inventory click event.
	 * 
	 * @param event
	 */
	public void handleInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
			event.setCancelled(true);
			Entity entity = (Entity) event.getInventory().getHolder();
			EconomyVillager economyVillager = (EconomyVillager) entity.getMetadata("ue-type").get(0).value();
			String shopId = (String) entity.getMetadata("ue-id").get(0).value();
			try {
				switch (economyVillager) {
				case PLAYERSHOP:
					handleShopInvClickEvent(PlayershopController.getPlayerShopById(shopId), event);
					break;
				case ADMINSHOP:
					handleShopInvClickEvent(AdminshopController.getAdminShopById(shopId), event);
					break;
				case PLAYERSHOP_RENTABLE:
					RentshopImpl rentshop = (RentshopImpl) RentshopController.getRentShopById(shopId);
					if (rentshop.isRentable()) {
						rentshop.getRentGuiHandler().handleRentShopGUIClick(event);
					} else {
						handleShopInvClickEvent(rentshop, event);
					}
					break;
				default:
					break;
				}
			} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
			}
		}
	}

	private void handleShopInvClickEvent(AbstractShop abstractShop, InventoryClickEvent event) {
		String inventoryName = event.getView().getTitle();
		Player player = (Player) event.getWhoClicked();
		ItemStack clickedItem = event.getCurrentItem();
		ItemMeta clickedItemMeta = clickedItem.getItemMeta();
		try {
			if ((abstractShop.getName() + "-Editor").equals(inventoryName)) {
				int slot = Integer.valueOf(clickedItemMeta.getDisplayName().substring(5));
				abstractShop.openSlotEditor(player, slot);
			} else if ((abstractShop.getName() + "-SlotEditor").equals(inventoryName)) {
				((AbstractShopImpl) abstractShop).getSlotEditorHandler().handleSlotEditor(event);
				String command = clickedItemMeta.getDisplayName();
				if ((ChatColor.RED + "remove item").equals(command)
						|| (ChatColor.RED + "exit without save").equals(command)
						|| (ChatColor.YELLOW + "save changes").equals(command)) {
					abstractShop.openEditor(player);
				}
			} else {
				// TODO [UE-61]
				// handleBuySell(abstractShop, event, player);
			}
		} catch (GeneralEconomyException | ShopSystemException e) {
		}
	}

	/**
	 * Handles open inventory for every shoptype.
	 * 
	 * @param event
	 */
	public void handleOpenInventory(PlayerInteractEntityEvent event) {
		event.setCancelled(true);
		Entity entity = event.getRightClicked();
		EconomyVillager economyVillager = (EconomyVillager) entity.getMetadata("ue-type").get(0).value();
		String shopId = (String) entity.getMetadata("ue-id").get(0).value();
		try {
			switch (economyVillager) {
			case PLAYERSHOP:
				PlayershopController.getPlayerShopById(shopId).openShopInventory(event.getPlayer());
				break;
			case ADMINSHOP:
				AdminshopController.getAdminShopById(shopId).openShopInventory(event.getPlayer());
				break;
			case PLAYERSHOP_RENTABLE:
				Rentshop shop = RentshopController.getRentShopById(shopId);
				if (shop.isRentable()) {
					shop.openRentGUI(event.getPlayer());
				} else {
					shop.openShopInventory(event.getPlayer());
				}
				break;
			default:
				break;
			}
		} catch (GeneralEconomyException | ShopSystemException e) {
		}
	}
}
