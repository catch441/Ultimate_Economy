package com.ue.shopsystem.logic.impl;

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

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.shopsystem.logic.api.AbstractShop;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.ultimate_economy.GeneralEconomyException;

public class ShopSlotEditorHandlerImpl implements ShopSlotEditorHandler {

	private Inventory slotEditor;
	private int selectedEditorSlot;
	private AbstractShop shop;
	private final CustomSkullService skullService;
	private final ShopValidationHandler validationHandler;
	private final MessageWrapper messageWrapper;

	/**
	 * Constructor for a new Slot Editor handler.
	 * 
	 * @param messageWrapper
	 * @param validationHandler
	 * @param skullService
	 * @param shop
	 */
	public ShopSlotEditorHandlerImpl(MessageWrapper messageWrapper, ShopValidationHandler validationHandler,
			CustomSkullService skullService, AbstractShop shop) {
		this.shop = shop;
		this.skullService = skullService;
		this.validationHandler = validationHandler;
		this.messageWrapper = messageWrapper;
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
		ItemStack item = skullService.getSkullWithName("K_OFF", "factor off");
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

	@Override
	public Inventory getSlotEditorInventory() {
		return slotEditor;
	}

	@Override
	public void changeInventoryName(String newName) {
		Inventory slotEditorNew = Bukkit.createInventory(getShop().getShopVillager(), 27, newName + "-SlotEditor");
		slotEditorNew.setContents(getSlotEditorInventory().getContents());
		slotEditor = slotEditorNew;
	}

	@Override
	public void setSelectedSlot(int slot) throws ShopSystemException, GeneralEconomyException {
		selectedEditorSlot = slot;
		setupSlotEditorWithShopItemInformations(slot);
	}

	private int getSelectedSlot() {
		return selectedEditorSlot;
	}

	private AbstractShop getShop() {
		return shop;
	}

	private void setupSlotEditorWithShopItemInformations(int slot) throws ShopSystemException, GeneralEconomyException {
		double buyPrice = 0;
		double sellPrice = 0;
		if (!validationHandler.isSlotEmpty(slot, getShop().getShopInventory(), 1)) {
			ShopItem item = getShop().getShopItem(slot);
			buyPrice = item.getBuyPrice();
			sellPrice = item.getSellPrice();
		}
		List<String> listBuy = new ArrayList<String>();
		List<String> listSell = new ArrayList<String>();
		listBuy.add(ChatColor.GOLD + "Price: " + buyPrice);
		listSell.add(ChatColor.GOLD + "Price: " + sellPrice);
		setupItemsInSlotEditor(Arrays.asList(2, 11, 20), "plus", listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(6, 15, 24), "twenty", listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(5, 14, 23), "ten", listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(4, 13, 22), "one", listBuy, listSell);
		addSkullToSlotEditor("sellprice", 9, listSell, "SELL");
		addSkullToSlotEditor("buyprice", 18, listBuy, "BUY");
		setupSlotItemInSlotEditor(slot);
	}

	private void setupSlotItemInSlotEditor(int slot) throws GeneralEconomyException, ShopSystemException {
		if (validationHandler.isSlotEmpty(slot, getShop().getShopInventory(), 1)) {
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
		ItemStack item = skullService.getSkullWithName(skull, displayName);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(loreList);
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(slot, item);
	}

	private void setupItemsInSlotEditor(List<Integer> slots, String skullName, List<String> listBuy,
			List<String> listSell) {
		ItemStack item = skullService.getSkullWithName(skullName.toUpperCase(), skullName.toLowerCase());
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

	@Override
	public void handleSlotEditor(InventoryClickEvent event) {
		if (event.getCurrentItem() != null) {
			Player player = (Player) event.getWhoClicked();
			try {
				int slot = event.getSlot();
				String operator = getOperatorForHandleSlotEditor(event, slot);
				double price = getPriceForHandleSlotEditor(event, slot);
				ItemStack editorItemStack = slotEditor.getItem(0);
				String command = null;
				if (event.getCurrentItem().getItemMeta() != null) {
					command = event.getCurrentItem().getItemMeta().getDisplayName();
				}
				handleSlotEditorCommand(event, player, slot, operator, price, editorItemStack, command);
			} catch (ShopSystemException | EconomyPlayerException | GeneralEconomyException e) {
				player.sendMessage(e.getMessage());
			}
		}
	}

	private void handleSlotEditorCommand(InventoryClickEvent event, Player player, int slot, String operator,
			double price, ItemStack editorItemStack, String command)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
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
			handlePlusMinusNumber(1, Arrays.asList(4, 13, 22), slot, operator, price, editorItemStack);
			break;
		case "ten":
			handlePlusMinusNumber(10, Arrays.asList(5, 14, 23), slot, operator, price, editorItemStack);
			break;
		case "twenty":
			handlePlusMinusNumber(20, Arrays.asList(6, 15, 24), slot, operator, price, editorItemStack);
			break;
		case "save changes":
			handleSaveChanges(player);
			getShop().openEditor(player);
			break;
		case "remove item":
			handleRemoveItem(player);
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
		if (clickedItem.getType() != Material.SPAWNER) {
			ItemStack editorItemStack = new ItemStack(clickedItem);
			editorItemStack.setAmount(1);
			getSlotEditorInventory().setItem(0, editorItemStack);
		}
	}

	private void handleSaveChanges(Player player)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		double buyPrice = Double
				.valueOf(getSlotEditorInventory().getItem(9).getItemMeta().getLore().get(0).substring(9));
		double sellPrice = Double
				.valueOf(getSlotEditorInventory().getItem(18).getItemMeta().getLore().get(0).substring(9));
		validationHandler.checkForPricesGreaterThenZero(sellPrice, buyPrice);
		ItemStack itemStack = getSlotEditorInventory().getItem(0);
		// make a copy of the edited/created item
		ItemStack stackInEditor = new ItemStack(itemStack);
		stackInEditor.setAmount(1);
		try {
			ShopItem shopItem = getShop().getShopItem(getSelectedSlot());
			// slot is occupied, check if item changed
			if (!shopItem.getItemString().equals(stackInEditor.toString())) {
				// remove and add
				handleRemoveItem(player);
				getShop().addShopItem(getSelectedSlot(), sellPrice, buyPrice, itemStack);
				player.sendMessage(
						messageWrapper.getString("shop_addItem", itemStack.getType().toString().toLowerCase()));
			} else {
				// edit
				player.sendMessage(getShop().editShopItem(getSelectedSlot(), String.valueOf(itemStack.getAmount()),
						String.valueOf(sellPrice), String.valueOf(buyPrice)));
			}
		} catch (GeneralEconomyException | ShopSystemException e) {
			// item is new
			validationHandler.checkForItemDoesNotExist(stackInEditor.toString(), getShop().getItemList());
			if (itemStack.getType() != Material.BARRIER) {
				getShop().addShopItem(getSelectedSlot(), sellPrice, buyPrice, itemStack);
				player.sendMessage(
						messageWrapper.getString("shop_addItem", itemStack.getType().toString().toLowerCase()));
			}
		}
	}

	private void handleRemoveItem(Player player) throws ShopSystemException, GeneralEconomyException {
		ItemStack item = getShop().getShopItem(getSelectedSlot()).getItemStack();
		String deletedIem = item.getType().toString().toLowerCase();
		getShop().removeShopItem(getSelectedSlot());
		if (item.getType() == Material.SPAWNER) {
			player.sendMessage(
					messageWrapper.getString("shop_removeSpawner", item.getItemMeta().getDisplayName().toLowerCase()));
		} else {
			player.sendMessage(messageWrapper.getString("shop_removeItem", deletedIem));
		}
	}

	private void handleSwitchFactor(int slot, String state) {
		if ("factor off".equals(state)) {
			ItemStack item = skullService.getSkullWithName("K_ON", "factor on");
			getSlotEditorInventory().setItem(slot, item);
		} else {
			ItemStack item = skullService.getSkullWithName("K_OFF", "factor off");
			getSlotEditorInventory().setItem(slot, item);
		}
	}

	private void handleSwitchPlusMinus(int slot, String state) {
		if ("plus".equals(state)) {
			ItemStack item = skullService.getSkullWithName("MINUS", "minus");
			getSlotEditorInventory().setItem(slot, item);
		} else {
			ItemStack item = skullService.getSkullWithName("PLUS", "plus");
			getSlotEditorInventory().setItem(slot, item);
		}
	}

	private void handlePlusMinusNumber(int amount, List<Integer> slots, int slot, String operator, double price,
			ItemStack editorItemStack) {
		if (slots.get(0) == slot) {
			handlePlusMinusAmount(amount, operator, editorItemStack);
		} else if (slots.get(1) == slot) {
			handlePlusMinusPrice(Arrays.asList(9, 11, 13, 14, 15), amount, getFactor(12), operator, price);
		} else if (slots.get(2) == slot) {
			handlePlusMinusPrice(Arrays.asList(18, 20, 22, 23, 24), amount, getFactor(21), operator, price);
		}
	}

	private int getFactor(int slot) {
		int factor = 1;
		if (getSlotEditorInventory().getItem(slot).getItemMeta().getDisplayName().equals("factor on")) {
			factor = 1000;
		}
		return factor;
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

	private void handlePlusMinusPrice(List<Integer> slots, int value, int factor, String operator, double price) {
		if (price >= (value * factor) && "minus".equals(operator)) {
			updateEditorPrice(slots.get(0), slots.get(1), slots.get(2), slots.get(3), slots.get(4),
					price - value * factor);
		} else if ("plus".equals(operator)) {
			updateEditorPrice(slots.get(0), slots.get(1), slots.get(2), slots.get(3), slots.get(4),
					price + value * factor);
		} else {
			updateEditorPrice(slots.get(0), slots.get(1), slots.get(2), slots.get(3), slots.get(4), 0.0);
		}
	}

	private double getPriceForHandleSlotEditor(InventoryClickEvent event, int slot) {
		switch (slot) {
		case 13:
		case 14:
		case 15:
			return Double.valueOf(event.getInventory().getItem(9).getItemMeta().getLore().get(0).substring(9));
		case 22:
		case 23:
		case 24:
			return Double.valueOf(event.getInventory().getItem(18).getItemMeta().getLore().get(0).substring(9));
		default:
			return 0.0;
		}
	}

	private String getOperatorForHandleSlotEditor(InventoryClickEvent event, int slot) {
		switch (slot) {
		case 4:
		case 5:
		case 6:
			return event.getInventory().getItem(2).getItemMeta().getDisplayName();
		case 13:
		case 14:
		case 15:
			return event.getInventory().getItem(11).getItemMeta().getDisplayName();
		case 22:
		case 23:
		case 24:
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
