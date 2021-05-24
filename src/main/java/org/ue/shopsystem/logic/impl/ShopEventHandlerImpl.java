package org.ue.shopsystem.logic.impl;

import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyvillager.logic.api.EconomyVillagerType;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopEventHandler;
import org.ue.shopsystem.logic.api.ShopsystemException;

public class ShopEventHandlerImpl implements ShopEventHandler {

	private final EconomyPlayerManager ecoPlayerManager;
	private final AdminshopManager adminshopManager;
	private final PlayershopManager playershopManager;
	private final RentshopManager rentshopManager;

	public ShopEventHandlerImpl(EconomyPlayerManager ecoPlayerManager, AdminshopManager adminshopManager,
			PlayershopManager playershopManager, RentshopManager rentshopManager) {
		this.ecoPlayerManager = ecoPlayerManager;
		this.adminshopManager = adminshopManager;
		this.playershopManager = playershopManager;
		this.rentshopManager = rentshopManager;
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() != null) {
			event.setCancelled(true);
			try {
				EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(event.getWhoClicked().getName());
				Entity entity = (Entity) event.getInventory().getHolder();
				EconomyVillagerType economyVillager = (EconomyVillagerType) entity.getMetadata("ue-type").get(0)
						.value();
				String shopId = (String) entity.getMetadata("ue-id").get(0).value();
				switch (economyVillager) {
				case PLAYERSHOP:
					handleShopInvClickEvent(ecoPlayer, playershopManager.getPlayerShopById(shopId), event);
					break;
				case ADMINSHOP:
					handleShopInvClickEvent(ecoPlayer, adminshopManager.getAdminShopById(shopId), event);
					break;
				case RENTSHOP:
					Rentshop rentshop = rentshopManager.getRentShopById(shopId);
					if (rentshop.isRentable()) {
						rentshop.getRentGuiHandler().handleInventoryClick(event.getClick(), event.getRawSlot(),
								ecoPlayer);
					} else {
						handleShopInvClickEvent(ecoPlayer, rentshop, event);
					}
					break;
				default:
					break;
				}
			} catch (ShopsystemException | EconomyPlayerException e) {
			}
		}
	}

	private void handleShopInvClickEvent(EconomyPlayer ecoPlayer, AbstractShop abstractShop, InventoryClickEvent event)
			throws ShopsystemException {
		String inventoryName = event.getView().getTitle();
		if ("Editor".equals(inventoryName)) {
			abstractShop.getEditorHandler().handleInventoryClick(event.getClick(), event.getRawSlot(), ecoPlayer);
		} else if ("SlotEditor".equals(inventoryName)) {
			abstractShop.getSlotEditorHandler(null).handleInventoryClick(event.getClick(),
					event.getRawSlot(), ecoPlayer);
		} else if ("Customize Villager".equals(inventoryName)) {
			abstractShop.getCustomizeGuiHandler().handleInventoryClick(event.getClick(), event.getRawSlot(), ecoPlayer);
		} else {
			abstractShop.handleInventoryClick(event.getClick(), event.getRawSlot(), ecoPlayer);
		}
	}

	@Override
	public void handleOpenInventory(PlayerInteractEntityEvent event) {
		event.setCancelled(true);
		Entity entity = event.getRightClicked();
		EconomyVillagerType economyVillager = EconomyVillagerType
				.getEnum(entity.getMetadata("ue-type").get(0).value().toString());
		String shopId = (String) entity.getMetadata("ue-id").get(0).value();
		try {
			switch (economyVillager) {
			case PLAYERSHOP:
				playershopManager.getPlayerShopById(shopId).openInventory(event.getPlayer());
				break;
			case ADMINSHOP:
				adminshopManager.getAdminShopById(shopId).openInventory(event.getPlayer());
				break;
			case RENTSHOP:
				Rentshop shop = rentshopManager.getRentShopById(shopId);
				if (shop.isRentable()) {
					shop.getRentGuiHandler().openInventory(event.getPlayer());
				} else {
					shop.openInventoryWithCheck(event.getPlayer());
				}
				break;
			default:
				break;
			}
		} catch (ShopsystemException e) {
		}
	}
}
