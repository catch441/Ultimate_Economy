package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.ue.bank.logic.api.BankException;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.logic.impl.InventoryGuiHandlerImpl;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.ShopsystemException;

public class RentshopRentGuiHandlerImpl extends InventoryGuiHandlerImpl implements InventoryGuiHandler {

	private final MessageWrapper messageWrapper;
	private final ConfigManager configManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private Rentshop shop;

	private int selectedDuration = 1;
	private boolean operatorState = true;

	/**
	 * Constructor for a new rent Gui handler.
	 * 
	 * @param messageWrapper
	 * @param ecoPlayerManager
	 * @param skullService
	 * @param configManager
	 * @param shop
	 * @param serverProvider
	 */
	public RentshopRentGuiHandlerImpl(MessageWrapper messageWrapper, EconomyPlayerManager ecoPlayerManager,
			CustomSkullService skullService, ConfigManager configManager, Rentshop shop,
			ServerProvider serverProvider) {
		super(skullService, serverProvider, null);
		this.shop = shop;
		this.configManager = configManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.messageWrapper = messageWrapper;
		setupRentGui();
	}

	private void setupRentGui() {
		inventory = shop.createVillagerInventory(9, shop.getName());
		setupDefaultItems();
	}

	private void setupDefaultItems() {
		List<String> durationLore = createDurationLore();
		List<String> rentalFeeLore = Arrays
				.asList(ChatColor.GOLD + "RentalFee: " + ChatColor.GREEN + shop.getRentalFee());
		setItem(Material.GREEN_WOOL, rentalFeeLore, ChatColor.YELLOW + "Rent", 0);
		setItem(Material.CLOCK, durationLore, ChatColor.YELLOW + "Duration", 1);
		setSkull(SkullTextureEnum.PLUS, null, "plus", 3);
		setSkull(SkullTextureEnum.ONE, durationLore, "one", 4);
		setSkull(SkullTextureEnum.SEVEN, durationLore, "seven", 5);
	}

	private List<String> createDurationLore() {
		if(selectedDuration == 1) {
			return Arrays.asList(
					ChatColor.GOLD + "Duration: " + ChatColor.GREEN + selectedDuration + ChatColor.GOLD + " Day");
		} else {
			return Arrays.asList(
					ChatColor.GOLD + "Duration: " + ChatColor.GREEN + selectedDuration + ChatColor.GOLD + " Days");
		}
	}

	@Override
	public void handleInventoryClick(ClickType clickType, int rawSlot, EconomyPlayer whoClicked) {
		if (rawSlot < 6) {
			try {
				switch (rawSlot) {
				case 5:
					handlePlusMinusValue(7);
					break;
				case 4:
					handlePlusMinusValue(1);
					break;
				case 3:
					handleSwitchPlusMinus();
					break;
				case 0:
					handleRentClick(whoClicked.getPlayer());
					break;
				default:
					break;
				}
			} catch (ShopsystemException | BankException | EconomyPlayerException e) {
				whoClicked.getPlayer().sendMessage(e.getMessage());
				returnToBackLink(whoClicked.getPlayer());
			}
		}
	}

	private void handleRentClick(Player player)
			throws ShopsystemException, BankException, EconomyPlayerException {
		shop.rentShop(ecoPlayerManager.getEconomyPlayerByName(player.getName()), selectedDuration);
		player.sendMessage(messageWrapper.getString(MessageEnum.RENT_RENTED));
		returnToBackLink(player);
	}

	private void handlePlusMinusValue(int value) {
		if (operatorState) {
			selectedDuration += value;
			if (selectedDuration > configManager.getMaxRentedDays()) {
				selectedDuration = configManager.getMaxRentedDays();
			}
		} else {
			selectedDuration -= value;
			if (selectedDuration < 1) {
				selectedDuration = 1;
			}
		}
		refreshDefaultItems();
	}

	private void refreshDefaultItems() {
		List<String> durationLore = createDurationLore();
		List<String> rentalFeeLore = new ArrayList<>();
		rentalFeeLore.add(ChatColor.GOLD + "RentalFee: " + ChatColor.GREEN + (selectedDuration * shop.getRentalFee()));
		updateItemLore(0, rentalFeeLore);
		updateItemLore(1, durationLore);
		updateItemLore(4, durationLore);
		updateItemLore(5, durationLore);
	}

	private void handleSwitchPlusMinus() {
		if (operatorState) {
			setSkull(SkullTextureEnum.MINUS, null, "minus", 3);
		} else {
			setSkull(SkullTextureEnum.PLUS, null, "plus", 3);
		}
		operatorState = !operatorState;
	}
}
