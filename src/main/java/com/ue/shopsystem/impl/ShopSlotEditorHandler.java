package com.ue.shopsystem.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.language.MessageWrapper;

public class ShopSlotEditorHandler {

	private Inventory slotEditor;
	private int selectedEditorSlot;
	private AbstractShopImpl shop;

	/**
	 * Constructor for a new Slot Editor handler.
	 * 
	 * @param shop
	 */
	public ShopSlotEditorHandler(AbstractShopImpl shop) {
		this.shop = shop;
		selectedEditorSlot = 0;
		setupSlotEditor();
	}

	private void setupSlotEditor() {
		slotEditor = Bukkit.createInventory(getShop().getShopVillager(), 27, getShop().getName() + "-SlotEditor");
		setupFactorItem();
		setupSaveItem();
		setupExitItem();
		setupRemoveItem();
	}

	private void setupFactorItem() {
		ItemStack item = CustomSkullService.getSkullWithName("K_OFF", "factor off");
		getSlotEditorInventory().setItem(12, item);
		getSlotEditorInventory().setItem(21, item);
	}

	private void setupRemoveItem() {
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "remove item");
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(26, item);
	}

	private void setupExitItem() {
		ItemStack item = new ItemStack(Material.RED_WOOL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "exit without save");
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(7, item);
	}

	private void setupSaveItem() {
		ItemStack item = new ItemStack(Material.GREEN_WOOL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "save changes");
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(8, item);
	}

	/**
	 * Returns the slot editor inventory.
	 * 
	 * @return slot editor inventory
	 */
	public Inventory getSlotEditorInventory() {
		return slotEditor;
	}

	/**
	 * Renames the slot editor inventory.
	 * 
	 * @param newName
	 */
	public void changeInventoryName(String newName) {
		Inventory slotEditorNew = Bukkit.createInventory(getShop().getShopVillager(), 27, newName + "-SlotEditor");
		slotEditorNew.setContents(getSlotEditorInventory().getContents());
		slotEditor = slotEditorNew;
	}

	/**
	 * Set the selected editor slot.
	 * 
	 * @param slot
	 */
	public void setSelectedSlot(int slot) {
		selectedEditorSlot = slot;
	}

	private int getSelectedSlot() {
		return selectedEditorSlot;
	}

	private AbstractShopImpl getShop() {
		return shop;
	}

	/**
	 * Setups the slot editor for a specific slot.
	 * 
	 * @param slot
	 * @throws ShopSystemException
	 * @throws GeneralEconomyException
	 */
	public void setupSlotEditorWithShopItemInformations(int slot) throws ShopSystemException, GeneralEconomyException {
		double buyPrice = 0;
		double sellPrice = 0;
		if (!getShop().getValidationHandler().isSlotEmpty(slot, getShop().getShopInventory(), 1)) {
			ShopItem item = getShop().getShopItem(slot);
			buyPrice = item.getBuyPrice();
			sellPrice = item.getSellPrice();
		}
		List<String> listBuy = new ArrayList<String>();
		List<String> listSell = new ArrayList<String>();
		listBuy.add(ChatColor.GOLD + "Price: " + buyPrice);
		listSell.add(ChatColor.GOLD + "Price: " + sellPrice);
		setupItemsInSlotEditor(Arrays.asList(2,11,20),"plus",listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(6,15,24),"twenty",listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(5,14,23),"ten",listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(4,13,22),"one",listBuy, listSell);
		addSkullToSlotEditor("buyprice", 9, listBuy, "BUY");
		addSkullToSlotEditor("sellprice", 18, listSell, "SELL");
		setupSlotItemInSlotEditor(slot);
	}

	private void setupSlotItemInSlotEditor(int slot) throws GeneralEconomyException, ShopSystemException {
		if (getShop().getValidationHandler().isSlotEmpty(slot, getShop().getShopInventory(), 1)) {
			ItemStack item = new ItemStack(Material.BARRIER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "select item");
			item.setItemMeta(meta);
			getSlotEditorInventory().setItem(0, item);
		} else {
			ShopItem item = getShop().getShopItem(slot);
			ItemStack stack = item.getItemStack();
			stack.setAmount(item.getAmount());
			getSlotEditorInventory().setItem(0, stack);
		}
	}

	private void addSkullToSlotEditor(String displayName, int slot, List<String> loreList, String skull) {
		ItemStack item = CustomSkullService.getSkullWithName(skull, displayName);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(loreList);
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(slot, item);
	}

	private void setupItemsInSlotEditor(List<Integer> slots, String skullName, List<String> listBuy,
			List<String> listSell) {
		ItemStack item = CustomSkullService.getSkullWithName(skullName.toUpperCase(), skullName.toLowerCase());
		getSlotEditorInventory().setItem(slots.get(0), item);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(listBuy);
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(slots.get(1), item);
		meta = item.getItemMeta();
		meta.setLore(listSell);
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(slots.get(2), item);
	}

	/**
	 * This method handles the SlotEditor for the InventoryClickEvent.
	 * 
	 * @param event
	 */
	public void handleSlotEditor(InventoryClickEvent event) {
		if (event.getCurrentItem().getItemMeta() != null) {
			Player player = (Player) event.getWhoClicked();
			try {
				ItemStack originStack = new ItemStack(getShop().getShopInventory().getItem(getSelectedSlot()));
				int slot = event.getSlot();
				int factor = 1;
				if (event.getInventory().getItem(12).getItemMeta().getDisplayName().equals("factor on")) {
					factor = 1000;
				}
				String operator = getOperatorForHandleSlotEditor(event, slot);
				double price = getPriceForHandleSlotEditor(event, slot);
				ItemStack editorItemStack = slotEditor.getItem(0);
				String command = event.getCurrentItem().getItemMeta().getDisplayName();

				handleSlotEditorCommand(event, player, originStack, slot, factor, operator, price, editorItemStack,
						command);
			} catch (ShopSystemException | PlayerException | GeneralEconomyException e) {
				player.sendMessage(e.getMessage());
			}
		}
	}

	private void handleSlotEditorCommand(InventoryClickEvent event, Player player, ItemStack originStack, int slot,
			int factor, String operator, double price, ItemStack editorItemStack, String command)
			throws ShopSystemException, PlayerException, GeneralEconomyException {
		switch (ChatColor.stripColor(command)) {
		case "minus":
		case "plus":
			handleSwitchPlusMinus(slot, command);
			break;
		case "factor off":
		case "factor on":
			handleSwitchFactor(slot, command);
			break;
		case "one":
			handlePlusMinusNumber(Arrays.asList(4,13,22), slot, factor, operator, price, editorItemStack);
			break;
		case "ten":
			handlePlusMinusNumber(Arrays.asList(5,14,23), slot, factor, operator, price, editorItemStack);
			break;
		case "twenty":
			handlePlusMinusNumber(Arrays.asList(6,15,24), slot, factor, operator, price, editorItemStack);
			break;
		case "save changes":
			handleSaveChanges(event.getInventory(), player, originStack);
			getShop().openEditor(player);
			break;
		case "remove item":
			handleRemoveItem(player, originStack);
		case "exit without save":
			getShop().openEditor(player);
			break;
		default:
			if (!"buyprice".equals(command) && !"sellprice".equals(command)) {
				handleAddItemToSlotEditor(event.getCurrentItem());
			}
			break;
		}
	}

	private void handleAddItemToSlotEditor(ItemStack clickedItem) {
		ItemStack editorItemStack = new ItemStack(clickedItem);
		editorItemStack.setAmount(1);
		getSlotEditorInventory().setItem(0, editorItemStack);
	}

	private void handleSaveChanges(Inventory inv, Player player, ItemStack originStack)
			throws ShopSystemException, PlayerException, GeneralEconomyException {
		double buyPrice = Double.valueOf(inv.getItem(9).getItemMeta().getLore().get(0).substring(9));
		double sellPrice = Double.valueOf(inv.getItem(18).getItemMeta().getLore().get(0).substring(9));
		getShop().getValidationHandler().checkForPricesGreaterThenZero(sellPrice, buyPrice);
		ItemStack itemStack = inv.getItem(0);
		// make a copy of the edited/created item
		ItemStack newItemStackCopy = new ItemStack(itemStack);
		ItemMeta itemMeta = originStack.getItemMeta();
		if (itemMeta != null && itemMeta.hasLore()) {
			List<String> loreList = getShop().removeShopItemPriceLore(itemMeta.getLore());
			itemMeta.setLore(loreList);
			originStack.setItemMeta(itemMeta);
		}
		String originalStackString = originStack.toString();
		newItemStackCopy.setAmount(originStack.getAmount());
		// if the item changed
		if (!newItemStackCopy.toString().equals(originalStackString)) {
			newItemStackCopy.setAmount(1);
			getShop().getValidationHandler().checkForItemDoesNotExist(newItemStackCopy.toString(),
					getShop().getItemList());
			// the old item in the selected slot gets deleted
			handleRemoveItem(player, originStack);
			getShop().addShopItem(getSelectedSlot(), sellPrice, buyPrice, itemStack);
			if (itemStack.getType() != Material.SPAWNER) {
				player.sendMessage(
						MessageWrapper.getString("shop_addItem", itemStack.getType().toString().toLowerCase()));
			} else {
				player.sendMessage(MessageWrapper.getString("shop_removeSpawner",
						itemStack.getItemMeta().getDisplayName().toLowerCase()));
			}
		}
		// if the item doesn't changed
		else {
			player.sendMessage(getShop().editShopItem(getSelectedSlot(), String.valueOf(itemStack.getAmount()),
					String.valueOf(sellPrice), String.valueOf(buyPrice)));
		}
	}

	private void handleRemoveItem(Player player, ItemStack originStack)
			throws ShopSystemException, GeneralEconomyException {
		getShop().removeShopItem(getSelectedSlot() - 1);
		player.sendMessage(MessageWrapper.getString("shop_removeItem", originStack.getType().toString().toLowerCase()));
	}

	private void handleSwitchFactor(int slot, String state) {
		if ("factor off".equals(state)) {
			ItemStack item = CustomSkullService.getSkullWithName("K_ON", "factor on");
			getSlotEditorInventory().setItem(slot, item);
		} else {
			ItemStack item = CustomSkullService.getSkullWithName("K_OFF", "factor off");
			getSlotEditorInventory().setItem(slot, item);
		}
	}

	private void handleSwitchPlusMinus(int slot, String state) {
		if ("plus".equals(state)) {
			ItemStack item = CustomSkullService.getSkullWithName("MINUS", "minus");
			getSlotEditorInventory().setItem(slot, item);
		} else {
			ItemStack item = CustomSkullService.getSkullWithName("PLUS", "plus");
			getSlotEditorInventory().setItem(slot, item);
		}
	}

	private void handlePlusMinusNumber(List<Integer> slots, int slot, int factor, String operator, double price, ItemStack editorItemStack) {
		if(slots.get(0) == slot) {
			handlePlusMinusAmount(1, operator, editorItemStack);
		} else if(slots.get(1) == slot) {
			handlePlusMinusSellPrice(1, factor, operator, price);
		} else if(slots.get(1) == slot) {
			handlePlusMinusBuyPrice(1, factor, operator, price);
		}
	}

	private void handlePlusMinusAmount(int value, String operator, ItemStack editorItemStack) {
		if (editorItemStack != null) {
			if ("plus".equals(operator)) {
				if ((editorItemStack.getAmount() + value <= 64)) {
					editorItemStack.setAmount(editorItemStack.getAmount() + value);
				} else {
					editorItemStack.setAmount(64);
				}
			} else {
				if (editorItemStack.getAmount() > value) {
					editorItemStack.setAmount(editorItemStack.getAmount() - value);
				}
			}
		}
	}

	private void handlePlusMinusSellPrice(int value, int factor, String operator, double price) {
		if (price >= value && "minus".equals(operator)) {
			updateEditorPrice(9, 11, 13, 14, 15, price - value * factor);
		} else if ("plus".equals(operator)) {
			updateEditorPrice(9, 11, 13, 14, 15, price + value * factor);
		}
	}

	private void handlePlusMinusBuyPrice(int value, int factor, String operator, double price) {
		if (price >= value && "minus".equals(operator)) {
			updateEditorPrice(18, 20, 22, 23, 24, price - value * factor);
		} else if ("plus".equals(operator)) {
			updateEditorPrice(18, 20, 22, 23, 24, price + value * factor);
		}
	}

	private double getPriceForHandleSlotEditor(InventoryClickEvent event, int slot) {
		switch (slot) {
		case 14:
		case 15:
		case 16:
			return Double.valueOf(event.getInventory().getItem(9).getItemMeta().getLore().get(0).substring(9));
		case 23:
		case 24:
		case 25:
			return Double.valueOf(event.getInventory().getItem(18).getItemMeta().getLore().get(0).substring(9));
		default:
			return 0.0;
		}
	}

	private String getOperatorForHandleSlotEditor(InventoryClickEvent event, int slot) {
		switch (slot) {
		case 5:
		case 6:
		case 7:
			return event.getInventory().getItem(2).getItemMeta().getDisplayName();
		case 14:
		case 15:
		case 16:
			return event.getInventory().getItem(11).getItemMeta().getDisplayName();
		case 23:
		case 24:
		case 25:
			return event.getInventory().getItem(20).getItemMeta().getDisplayName();
		default:
			return null;
		}
	}

	private void updateEditorPrice(int a, int b, int c, int d, int e, Double price) {
		List<String> list = new ArrayList<>();
		list.add(ChatColor.GOLD + "Price: " + price);
		ItemMeta meta = getSlotEditorInventory().getItem(a).getItemMeta();
		meta.setLore(list);
		getSlotEditorInventory().getItem(a).setItemMeta(meta);
		meta = getSlotEditorInventory().getItem(b).getItemMeta();
		meta.setLore(list);
		getSlotEditorInventory().getItem(b).setItemMeta(meta);
		meta = getSlotEditorInventory().getItem(c).getItemMeta();
		meta.setLore(list);
		getSlotEditorInventory().getItem(c).setItemMeta(meta);
		meta = getSlotEditorInventory().getItem(d).getItemMeta();
		meta.setLore(list);
		getSlotEditorInventory().getItem(d).setItemMeta(meta);
		meta = getSlotEditorInventory().getItem(e).getItemMeta();
		meta.setLore(list);
		getSlotEditorInventory().getItem(e).setItemMeta(meta);
	}
}
