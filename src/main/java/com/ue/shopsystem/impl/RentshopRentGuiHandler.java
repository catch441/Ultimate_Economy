package com.ue.shopsystem.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.config.api.ConfigController;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.Rentshop;

public class RentshopRentGuiHandler {
	
	private Rentshop shop;
	private Inventory rentShopGUIInv;
	
	/**
	 * Constructor for a new rent Gui handler.
	 * @param shop
	 */
	public RentshopRentGuiHandler(Rentshop shop) {
		this.shop = shop;
		setupRentGui();
	}
	
	private void setupRentGui() {
		rentShopGUIInv = Bukkit.createInventory(getShop().getShopVillager(), 9, getShop().getName());
		List<String> loreList = new ArrayList<>();
		loreList.add(ChatColor.GOLD + "RentalFee: " + ChatColor.GREEN + getShop().getRentalFee());
		ItemStack itemStack = new ItemStack(Material.GREEN_WOOL, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Rent");
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		getRentGui().setItem(0, itemStack);
		loreList.clear();
		loreList.add(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + 1 + ChatColor.GOLD + " Day");
		itemStack.setType(Material.CLOCK);
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		meta.setDisplayName(ChatColor.YELLOW + "Duration");
		itemStack.setItemMeta(meta);
		getRentGui().setItem(1, itemStack);
		itemStack = CustomSkullService.getSkullWithName("PLUS", "plus");
		getRentGui().setItem(3, itemStack);
		itemStack = CustomSkullService.getSkullWithName("ONE", "one");
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		getRentGui().setItem(4, itemStack);
		itemStack = CustomSkullService.getSkullWithName("SEVEN", "seven");
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		getRentGui().setItem(5, itemStack);
	}
	
	/**
	 * Returns the rent gui inventory.
	 * @return gui inventory
	 */
	protected Inventory getRentGui() {
		return rentShopGUIInv;
	}
	
	private Rentshop getShop() {
		return shop;
	}
	
	/**
	 * Handles a click in the rentshop GUI.
	 * @param event
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 * @throws PlayerException
	 */
	public void handleRentShopGUIClick(InventoryClickEvent event)
			throws ShopSystemException, GeneralEconomyException, PlayerException {
		if (event.getCurrentItem().getItemMeta() != null) {
			String durationString = event.getInventory().getItem(1).getItemMeta().getLore().get(0);
			durationString = ChatColor.stripColor(durationString);
			int duration = Integer.valueOf(
					durationString.substring(durationString.indexOf(" ") + 1, durationString.lastIndexOf(" ")));
			String operation = event.getInventory().getItem(3).getItemMeta().getDisplayName();
			String command = event.getCurrentItem().getItemMeta().getDisplayName();
			command = ChatColor.stripColor(command);
			switch (command) {
			case "plus":
				switchPlusMinusRentGUI("plus");
				break;
			case "minus":
				switchPlusMinusRentGUI("minus");
				break;
			case "one":
				handlePlusMinusOneGuiClick(duration, operation);
				break;
			case "seven":
				handlePlusMinusSevenGuiClick(duration, operation);
				break;
			case "Rent":
				getShop().rentShop(EconomyPlayerController.getEconomyPlayerByName(event.getWhoClicked().getName()), duration);
				event.getWhoClicked().sendMessage(MessageWrapper.getString("rent_rented"));
				event.getWhoClicked().closeInventory();
				break;
			default:
				break;
			}
		}
	}

	private void handlePlusMinusSevenGuiClick(int duration, String operation) {
		if ("plus".equals(operation)) {
			if (duration < ConfigController.getMaxRentedDays()) {
				duration += 7;
				if (duration > ConfigController.getMaxRentedDays()) {
					duration = ConfigController.getMaxRentedDays();
				}
			}
		} else if (duration > 7) {
			duration -= 7;
		}
		refreshDurationOnRentGUI(duration);
		refreshRentalFeeOnRentGUI(duration);
	}

	private void handlePlusMinusOneGuiClick(int duration, String operation) {
		if ("plus".equals(operation)) {
			if (duration < ConfigController.getMaxRentedDays()) {
				duration++;
			}
		} else if (duration > 1) {
			duration--;
		}
		refreshDurationOnRentGUI(duration);
		refreshRentalFeeOnRentGUI(duration);
	}

	private void refreshRentalFeeOnRentGUI(int duration) {
		List<String> loreList = new ArrayList<>();
		loreList.add(ChatColor.GOLD + "RentalFee: " + ChatColor.GREEN + (duration * getShop().getRentalFee()));
		ItemStack stack = getRentGui().getItem(0);
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
	}

	private void refreshDurationOnRentGUI(int duration) {
		List<String> loreList = new ArrayList<>();
		if (duration > 1) {
			loreList.add(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + duration + ChatColor.GOLD + " Days");

		} else {
			loreList.add(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + duration + ChatColor.GOLD + " Day");
		}
		ItemStack stack = getRentGui().getItem(5);
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
		stack = getRentGui().getItem(4);
		meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
		stack = getRentGui().getItem(1);
		meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
	}

	private void switchPlusMinusRentGUI(String state) {
		if ("plus".equals(state)) {
			ItemStack item = CustomSkullService.getSkullWithName("MINUS", "minus");
			getRentGui().setItem(3, item);
		} else {
			ItemStack item = CustomSkullService.getSkullWithName("PLUS", "plus");
			getRentGui().setItem(3, item);
		}
	}
}
