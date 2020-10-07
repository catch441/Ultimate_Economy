package com.ue.shopsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.logic.api.AbstractShop;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.shopsystem.logic.api.ShopEventHandler;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.ultimate_economy.EconomyVillager;
import com.ue.ultimate_economy.GeneralEconomyException;

public class ShopEventHandlerImpl implements ShopEventHandler {

	private final EconomyPlayerManager ecoPlayerManager;
	private final AdminshopManager adminshopManager;
	private final PlayershopManager playershopManager;
	private final RentshopManager rentshopManager;

	/**
	 * Inject constructor.
	 * 
	 * @param rentshopManager
	 * @param adminshopManager
	 * @param playershopManager
	 * @param ecoPlayerManager
	 */
	@Inject
	public ShopEventHandlerImpl(RentshopManager rentshopManager, AdminshopManager adminshopManager,
			PlayershopManager playershopManager, EconomyPlayerManager ecoPlayerManager) {
		this.ecoPlayerManager = ecoPlayerManager;
		this.playershopManager = playershopManager;
		this.adminshopManager = adminshopManager;
		this.rentshopManager = rentshopManager;
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() != null) {
			event.setCancelled(true);
			Entity entity = (Entity) event.getInventory().getHolder();
			EconomyVillager economyVillager = (EconomyVillager) entity.getMetadata("ue-type").get(0).value();
			String shopId = (String) entity.getMetadata("ue-id").get(0).value();
			try {
				switch (economyVillager) {
				case PLAYERSHOP:
					handleShopInvClickEvent(playershopManager.getPlayerShopById(shopId), event);
					break;
				case ADMINSHOP:
					handleShopInvClickEvent(adminshopManager.getAdminShopById(shopId), event);
					break;
				case PLAYERSHOP_RENTABLE:
					RentshopImpl rentshop = (RentshopImpl) rentshopManager.getRentShopById(shopId);
					if (rentshop.isRentable()) {
						rentshop.getRentGuiHandler().handleRentShopGUIClick(event);
					} else {
						handleShopInvClickEvent(rentshop, event);
					}
					break;
				default:
					break;
				}
			} catch (GeneralEconomyException e) {
			}
		}
	}

	private void handleShopInvClickEvent(AbstractShop abstractShop, InventoryClickEvent event) {
		String inventoryName = event.getView().getTitle();
		Player player = (Player) event.getWhoClicked();
		try {
			if ((abstractShop.getName() + "-Editor").equals(inventoryName)) {
				((AbstractShopImpl) abstractShop).getEditorHandler().handleInventoryClick(event);
			} else if ((abstractShop.getName() + "-SlotEditor").equals(inventoryName)) {
				((AbstractShopImpl) abstractShop).getSlotEditorHandler().handleSlotEditor(event);
			} else {
				EconomyPlayer ecoPlayer = ecoPlayerManager.getEconomyPlayerByName(player.getName());
				handleBuySell(abstractShop, event, ecoPlayer);
			}
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			player.sendMessage(e.getMessage());
		}
	}

	private void handleBuySell(AbstractShop abstractShop, InventoryClickEvent event, EconomyPlayer ecoPlayer)
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		Entity entity = (Entity) event.getInventory().getHolder();
		EconomyVillager economyVillager = EconomyVillager
				.getEnum(entity.getMetadata("ue-type").get(0).value().toString());
		int reservedSlots = 2;
		if (economyVillager == EconomyVillager.ADMINSHOP) {
			reservedSlots = 1;
		}
		if (event.getClick() == ClickType.MIDDLE) {
			String inventoryName = event.getView().getTitle();
			handleSwitchStockpile(abstractShop, ecoPlayer, economyVillager, inventoryName);
		} else if (event.getRawSlot() < (abstractShop.getSize() - reservedSlots)
				|| event.getRawSlot() >= abstractShop.getSize()) {
			switch (event.getClick()) {
			case LEFT:
				handleBuy(abstractShop, event, ecoPlayer);
				break;
			case RIGHT:
				handleSellSpecific(abstractShop, event, ecoPlayer);
				break;
			case SHIFT_RIGHT:
				handleSellAll(abstractShop, event, ecoPlayer);
				break;
			default:
				break;
			}
		}
	}

	private void handleSwitchStockpile(AbstractShop abstractShop, EconomyPlayer ecoPlayer,
			EconomyVillager economyVillager, String inventoryName) throws ShopSystemException {
		if (economyVillager == EconomyVillager.PLAYERSHOP || economyVillager == EconomyVillager.PLAYERSHOP_RENTABLE) {
			if ((abstractShop.getName() + "-Stock").equals(inventoryName)) {
				abstractShop.openShopInventory(ecoPlayer.getPlayer());
			} else {
				((Playershop) abstractShop).openStockpile(ecoPlayer.getPlayer());
			}
		}
	}

	private void handleBuy(AbstractShop abstractShop, InventoryClickEvent event, EconomyPlayer ecoPlayer)
			throws GeneralEconomyException, EconomyPlayerException, ShopSystemException {
		// check if click was inside the shop inventory, buying should be only possible
		// inside the
		// shop inventory, not the player inventory
		if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
			int slot = event.getSlot();
			abstractShop.buyShopItem(slot, ecoPlayer, true);
		}
	}

	private void handleSellSpecific(AbstractShop abstractShop, InventoryClickEvent event, EconomyPlayer ecoPlayer)
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		ShopItem shopItem = abstractShop.getShopItem(event.getCurrentItem());
		if (ecoPlayer.getPlayer().getInventory().containsAtLeast(shopItem.getItemStack(), shopItem.getAmount())) {
			abstractShop.sellShopItem(shopItem.getSlot(), shopItem.getAmount(), ecoPlayer, true);
		}
	}

	private void handleSellAll(AbstractShop abstractShop, InventoryClickEvent event, EconomyPlayer ecoPlayer)
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		ShopItem shopItem = abstractShop.getShopItem(event.getCurrentItem());
		if (ecoPlayer.getPlayer().getInventory().containsAtLeast(shopItem.getItemStack(), 1)) {
			int sellAmount = 0;
			for (ItemStack is : event.getWhoClicked().getInventory().getStorageContents()) {
				if (is != null) {
					if (is.isSimilar(shopItem.getItemStack())) {
						sellAmount = sellAmount + is.getAmount();
					}
				}
			}
			abstractShop.sellShopItem(shopItem.getSlot(), sellAmount, ecoPlayer, true);
		}
	}

	@Override
	public void handleOpenInventory(PlayerInteractEntityEvent event) {
		event.setCancelled(true);
		Entity entity = event.getRightClicked();
		EconomyVillager economyVillager = EconomyVillager
				.getEnum(entity.getMetadata("ue-type").get(0).value().toString());
		String shopId = (String) entity.getMetadata("ue-id").get(0).value();
		try {
			switch (economyVillager) {
			case PLAYERSHOP:
				playershopManager.getPlayerShopById(shopId).openShopInventory(event.getPlayer());
				break;
			case ADMINSHOP:
				adminshopManager.getAdminShopById(shopId).openShopInventory(event.getPlayer());
				break;
			case PLAYERSHOP_RENTABLE:
				Rentshop shop = rentshopManager.getRentShopById(shopId);
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
