package com.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopRentGuiHandler;
import com.ue.ultimate_economy.GeneralEconomyException;

public class RentshopRentGuiHandlerImpl implements RentshopRentGuiHandler {

	private final ConfigManager configManager;
	private final CustomSkullService skullService;
	private final EconomyPlayerManager ecoPlayerManager;
	private final MessageWrapper messageWrapper;
	private Rentshop shop;
	private Inventory rentShopGUIInv;

	/**
	 * Constructor for a new rent Gui handler.
	 * 
	 * @param messageWrapper
	 * @param ecoPlayerManager
	 * @param skullService
	 * @param configManager
	 * @param shop
	 */
	public RentshopRentGuiHandlerImpl(MessageWrapper messageWrapper, EconomyPlayerManager ecoPlayerManager,
			CustomSkullService skullService, ConfigManager configManager, Rentshop shop) {
		this.shop = shop;
		this.configManager = configManager;
		this.skullService = skullService;
		this.ecoPlayerManager = ecoPlayerManager;
		this.messageWrapper = messageWrapper;
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
		itemStack = skullService.getSkullWithName("PLUS", "plus");
		getRentGui().setItem(3, itemStack);
		itemStack = skullService.getSkullWithName("ONE", "one");
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		getRentGui().setItem(4, itemStack);
		itemStack = skullService.getSkullWithName("SEVEN", "seven");
		meta = itemStack.getItemMeta();
		meta.setLore(loreList);
		itemStack.setItemMeta(meta);
		getRentGui().setItem(5, itemStack);
	}

	@Override
	public Inventory getRentGui() {
		return rentShopGUIInv;
	}

	private Rentshop getShop() {
		return shop;
	}

	@Override
	public void handleRentShopGUIClick(InventoryClickEvent event) {
		if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
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
				handlePlusMinusValueGuiClick(1, duration, operation);
				break;
			case "seven":
				handlePlusMinusValueGuiClick(7, duration, operation);
				break;
			case "Rent":
				handleRentClick(event, duration);
				break;
			default:
				break;
			}
		}
	}

	private void handleRentClick(InventoryClickEvent event, int duration) {
		try {
			getShop().rentShop(ecoPlayerManager.getEconomyPlayerByName(event.getWhoClicked().getName()), duration);
			event.getWhoClicked().sendMessage(messageWrapper.getString("rent_rented"));
		} catch (ShopSystemException | GeneralEconomyException | EconomyPlayerException e) {
			event.getWhoClicked().sendMessage(e.getMessage());
		}
		event.getWhoClicked().closeInventory();
	}

	private void handlePlusMinusValueGuiClick(int value, int duration, String operation) {
		if ("plus".equals(operation)) {
			if (duration < configManager.getMaxRentedDays()) {
				duration += value;
				if (duration > configManager.getMaxRentedDays()) {
					duration = configManager.getMaxRentedDays();
				}
			}
		} else if (duration > value) {
			duration -= value;
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
			ItemStack item = skullService.getSkullWithName("MINUS", "minus");
			getRentGui().setItem(3, item);
		} else {
			ItemStack item = skullService.getSkullWithName("PLUS", "plus");
			getRentGui().setItem(3, item);
		}
	}
}