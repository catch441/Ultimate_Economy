package org.ue.shopsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.EconomyVillagerType;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.AdminshopManager;
import org.ue.shopsystem.logic.api.PlayershopManager;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopEventHandler;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopsystemException;

public class ShopEventHandlerImpl implements ShopEventHandler {

	private final EconomyPlayerManager ecoPlayerManager;
	private final AdminshopManager adminshopManager;
	private final PlayershopManager playershopManager;
	private final RentshopManager rentshopManager;

	@Inject
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
			Entity entity = (Entity) event.getInventory().getHolder();
			EconomyVillagerType economyVillager = (EconomyVillagerType) entity.getMetadata("ue-type").get(0).value();
			String shopId = (String) entity.getMetadata("ue-id").get(0).value();
			try {
				switch (economyVillager) {
				case PLAYERSHOP:
					handleShopInvClickEvent(playershopManager.getPlayerShopById(shopId), event);
					break;
				case ADMINSHOP:
					handleShopInvClickEvent(adminshopManager.getAdminShopById(shopId), event);
					break;
				case RENTSHOP:
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
		} catch (ShopsystemException | BankException | EconomyPlayerException e) {
			player.sendMessage(e.getMessage());
		}
	}

	private void handleBuySell(AbstractShop abstractShop, InventoryClickEvent event, EconomyPlayer ecoPlayer)
			throws ShopsystemException, BankException, EconomyPlayerException {
		Entity entity = (Entity) event.getInventory().getHolder();
		EconomyVillagerType economyVillager = EconomyVillagerType
				.getEnum(entity.getMetadata("ue-type").get(0).value().toString());
		int reservedSlots = 2;
		if (economyVillager == EconomyVillagerType.ADMINSHOP) {
			reservedSlots = 1;
		}
		if (event.getRawSlot() < (abstractShop.getSize() - reservedSlots)
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

	private void handleBuy(AbstractShop abstractShop, InventoryClickEvent event, EconomyPlayer ecoPlayer)
			throws ShopsystemException, BankException, EconomyPlayerException {
		// check if click was inside the shop inventory, buying should be only possible
		// inside the
		// shop inventory, not the player inventory
		if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
			int slot = event.getSlot();
			abstractShop.buyShopItem(slot, ecoPlayer, true);
		}
	}

	private void handleSellSpecific(AbstractShop abstractShop, InventoryClickEvent event, EconomyPlayer ecoPlayer)
			throws ShopsystemException, BankException, EconomyPlayerException {
		ShopItem shopItem = abstractShop.getShopItem(event.getCurrentItem());
		int amount = getAmountInInventory(event.getWhoClicked().getInventory(), shopItem);
		if (amount >= shopItem.getAmount()) {
			abstractShop.sellShopItem(shopItem.getSlot(), shopItem.getAmount(), ecoPlayer, true);
		}
	}

	private void handleSellAll(AbstractShop abstractShop, InventoryClickEvent event, EconomyPlayer ecoPlayer)
			throws ShopsystemException, BankException, EconomyPlayerException {
		ShopItem shopItem = abstractShop.getShopItem(event.getCurrentItem());
		int amount = getAmountInInventory(event.getWhoClicked().getInventory(), shopItem);
		if (amount > 0) {
			abstractShop.sellShopItem(shopItem.getSlot(), amount, ecoPlayer, true);
		}
	}

	private int getAmountInInventory(PlayerInventory inv, ShopItem shopItem) {
		int amount = 0;
		for (ItemStack is : inv.getStorageContents()) {
			if (is != null) {
				if (stackIsSimilar(is, shopItem.getItemStack())) {
					amount = amount + is.getAmount();
				}
			}
		}
		return amount;
	}

	private boolean stackIsSimilar(ItemStack a, ItemStack b) {
		ItemStack aClone = a.clone();
		ItemStack bClone = b.clone();
		aClone.setAmount(1);
		bClone.setAmount(1);
		return aClone.toString().equals(bClone.toString());
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
					shop.openRentGUI(event.getPlayer());
				} else {
					shop.openInventory(event.getPlayer());
				}
				break;
			default:
				break;
			}
		} catch (ShopsystemException e) {
		}
	}
}
