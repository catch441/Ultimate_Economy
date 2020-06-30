package com.ue.eventhandling;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.shopsystem.api.AbstractShop;

public enum EconomyVillager {

	ADMINSHOP("adminshop"),
	PLAYERSHOP("playershop"),
	PLAYERSHOP_RENTABLE("playershop_rentable"),
	PLOTSALE("plotsale"),
	TOWNMANAGER("townmanager"),
	JOBCENTER("jobcenter"),
	UNDEFINED("undefined");

	private String value;

	private EconomyVillager(String value) {
		this.value = value;
	}

	private String getValue() {
		return value;
	}

	/**
	 * Returns a economy villager enum. Return UNDEFINED, if no enum found.
	 * 
	 * @param value
	 * @return economy villager type
	 */
	public static EconomyVillager getEnum(String value) {
		for (EconomyVillager v : values()) {
			if (v.getValue().equalsIgnoreCase(value)) {
				return v;
			}
		}	
		return EconomyVillager.UNDEFINED;
	}

	private static void handleBuySell(AbstractShop abstractShop, InventoryClickEvent event, Player playe)
			throws PlayerException, GeneralEconomyException {
		/*
		 * event.setCancelled(true); EconomyPlayer ecoPlayer =
		 * EconomyPlayerController.getEconomyPlayerByName(playe.getName()); boolean
		 * isPlayershop = false; boolean alreadysay = false; ClickType clickType =
		 * event.getClick(); Inventory inventoryplayer =
		 * event.getWhoClicked().getInventory(); Playershop playershop = null; if
		 * (abstractShop instanceof Playershop) { isPlayershop = true; playershop =
		 * (Playershop) abstractShop; } // Playershop if (isPlayershop && clickType ==
		 * ClickType.MIDDLE && playershop.isOwner(ecoPlayer)) {
		 * playershop.openStockpile(ecoPlayer.getPlayer()); } // else { for (String
		 * itemString : abstractShop.getItemList()) { // only relevant for adminshop
		 * boolean isSpawner = false; // standardize the itemstack for the string
		 * ItemStack clickedItemReal = event.getCurrentItem(); ItemStack clickedItem =
		 * new ItemStack(clickedItemReal); ItemMeta itemMeta =
		 * clickedItem.getItemMeta(); if (itemMeta.hasLore()) { List<String> loreList =
		 * itemMeta.getLore(); Iterator<String> loreIter = loreList.iterator(); while
		 * (loreIter.hasNext()) { String lore = loreIter.next(); if
		 * (lore.contains(" buy for ") || lore.contains(" sell for ")) {
		 * loreIter.remove(); } } itemMeta.setLore(loreList);
		 * clickedItem.setItemMeta(itemMeta); } clickedItem.setAmount(1); String
		 * clickedItemString = clickedItem.toString(); if
		 * (itemString.contains("blockMaterial=SPAWNER")) { isSpawner = true; } if
		 * (itemString.equals(clickedItemString)) { try { double sellprice =
		 * abstractShop.getItemSellPrice(itemString); double buyprice =
		 * abstractShop.getItemBuyPrice(itemString); int amount =
		 * abstractShop.getItemAmount(itemString); EconomyPlayer playerShopOwner = null;
		 * if (isPlayershop) { playerShopOwner = playershop.getOwner(); } if (clickType
		 * == ClickType.LEFT) { if (buyprice != 0.0 &&
		 * ecoPlayer.hasEnoughtMoney(buyprice) || (isPlayershop &&
		 * playe.getName().equals(playerShopOwner.getName()))) { if (!isPlayershop ||
		 * playershop.isAvailable(clickedItemString)) { if (inventoryplayer.firstEmpty()
		 * != -1) { // only adminshop if (isSpawner) { ItemStack stack = new
		 * ItemStack(Material.SPAWNER, amount); ItemMeta meta = stack.getItemMeta();
		 * meta.setDisplayName( clickedItem.getItemMeta().getDisplayName() + "-" +
		 * playe.getName()); stack.setItemMeta(meta); inventoryplayer.addItem(stack);
		 * ecoPlayer.decreasePlayerAmount(buyprice, true); if (amount > 1) {
		 * playe.sendMessage(MessageWrapper.getString("shop_buy_plural",
		 * String.valueOf(amount), buyprice,
		 * ConfigController.getCurrencyText(buyprice))); } else {
		 * playe.sendMessage(MessageWrapper.getString("shop_buy_singular",
		 * String.valueOf(amount), buyprice,
		 * ConfigController.getCurrencyText(buyprice))); } } // else if (!isSpawner) {
		 * ItemStack itemStack = abstractShop.getItemStack(itemString); if
		 * (isPlayershop) { playershop.decreaseStock(itemString, amount); // if the
		 * player is in stockpile mode, then the stockpile gets refreshed
		 * playershop.setupStockpile(); } itemStack.setAmount(amount);
		 * inventoryplayer.addItem(itemStack); if (!isPlayershop ||
		 * !playerShopOwner.getName().equals(playe.getName())) {
		 * ecoPlayer.decreasePlayerAmount(buyprice, true); // only playershop if
		 * (isPlayershop) { playerShopOwner.increasePlayerAmount(buyprice, false); } if
		 * (amount > 1) { playe.sendMessage(MessageWrapper.getString("shop_buy_plural",
		 * String.valueOf(amount), buyprice,
		 * ConfigController.getCurrencyText(buyprice))); } else {
		 * playe.sendMessage(MessageWrapper.getString("shop_buy_singular",
		 * String.valueOf(amount), buyprice,
		 * ConfigController.getCurrencyText(buyprice))); } } // only playershop else if
		 * (isPlayershop && playerShopOwner.getName().equals(playe.getName())) { if
		 * (amount > 1) {
		 * playe.sendMessage(MessageWrapper.getString("shop_got_item_plural",
		 * String.valueOf(amount))); } else {
		 * playe.sendMessage(MessageWrapper.getString("shop_got_item_singular",
		 * String.valueOf(amount))); } } break; } } else {
		 * playe.sendMessage(MessageWrapper.getErrorString("inventory_full")); } } //
		 * only playershop else if (isPlayershop) {
		 * playe.sendMessage(MessageWrapper.getErrorString("item_unavailable")); } }
		 * else if (!ecoPlayer.hasEnoughtMoney(buyprice) && !alreadysay) {
		 * playe.sendMessage(MessageWrapper.getErrorString("not_enough_money_personal"))
		 * ; alreadysay = true; } } else if (clickType == ClickType.RIGHT &&
		 * !itemString.contains("ANVIL_0") && !itemString.contains("CRAFTING_TABLE_0")
		 * && sellprice != 0.0 || clickType == ClickType.RIGHT && isPlayershop &&
		 * playe.getName().equals(playerShopOwner.getName()) &&
		 * inventoryplayer.containsAtLeast(clickedItem, amount)) { ItemStack itemStack =
		 * abstractShop.getItemStack(itemString); itemStack.setAmount(amount); if
		 * (inventoryContainsItems(inventoryplayer, itemStack, amount)) { if
		 * (isPlayershop && !playerShopOwner.getName().equals(playe.getName()) ||
		 * !isPlayershop) { if (!isPlayershop || (isPlayershop &&
		 * playerShopOwner.hasEnoughtMoney(sellprice))) {
		 * ecoPlayer.increasePlayerAmount(sellprice, false); // only playershop if
		 * (isPlayershop) { playerShopOwner.decreasePlayerAmount(sellprice, false);
		 * playershop.increaseStock(clickedItemString, amount); } if (amount > 1) {
		 * playe.sendMessage( MessageWrapper.getString("shop_sell_plural",
		 * String.valueOf(amount), sellprice,
		 * ConfigController.getCurrencyText(sellprice))); } else {
		 * playe.sendMessage(MessageWrapper.getString("shop_sell_singular",
		 * String.valueOf(amount), sellprice,
		 * ConfigController.getCurrencyText(sellprice))); }
		 * removeItemFromInventory(inventoryplayer, itemStack, amount); } // only
		 * playershop else if (isPlayershop) {
		 * playe.sendMessage(MessageWrapper.getErrorString("shopowner_not_enough_money")
		 * ); } } // only playershop else if (isPlayershop) { if (amount > 1) {
		 * playe.sendMessage(MessageWrapper.getString("shop_added_item_plural",
		 * String.valueOf(amount))); } else {
		 * playe.sendMessage(MessageWrapper.getString("shop_added_item_singular",
		 * String.valueOf(amount))); } playershop.increaseStock(clickedItemString,
		 * amount); // if the player is in stockpile mode, then the stockpile gets
		 * refreshed playershop.setupStockpile();
		 * removeItemFromInventory(inventoryplayer, itemStack, amount); } break; } }
		 * else if (clickType == ClickType.SHIFT_RIGHT && sellprice != 0.0 || clickType
		 * == ClickType.SHIFT_RIGHT && isPlayershop &&
		 * playe.getName().equals(playerShopOwner.getName()) &&
		 * inventoryplayer.containsAtLeast(clickedItem, amount)) { ItemStack itemStack =
		 * abstractShop.getItemStack(itemString); if
		 * (inventoryContainsItems(inventoryplayer, itemStack, 1)) { ItemStack[] i =
		 * inventoryplayer.getStorageContents(); int itemAmount = 0; double iA = 0.0;
		 * double newprice = 0; for (ItemStack is1 : i) { if (is1 != null) { ItemStack
		 * is = new ItemStack(is1); itemStack.setAmount(is.getAmount()); if
		 * (is.toString().equals(itemStack.toString())) { itemAmount = itemAmount +
		 * is.getAmount(); } } } iA = Double.valueOf(String.valueOf(itemAmount));
		 * newprice = sellprice / amount * iA; if (isPlayershop &&
		 * !playerShopOwner.getName().equals(playe.getName()) || !isPlayershop) { if
		 * ((isPlayershop && playerShopOwner.hasEnoughtMoney(newprice)) ||
		 * !isPlayershop) { if (itemAmount > 1) {
		 * playe.sendMessage(MessageWrapper.getString("shop_sell_plural",
		 * String.valueOf(itemAmount), newprice,
		 * ConfigController.getCurrencyText(newprice))); } else {
		 * playe.sendMessage(MessageWrapper.getString("shop_sell_singular",
		 * String.valueOf(itemAmount), newprice,
		 * ConfigController.getCurrencyText(newprice))); }
		 * ecoPlayer.increasePlayerAmount(newprice, false); // only playershop if
		 * (isPlayershop) { playerShopOwner.decreasePlayerAmount(newprice, false);
		 * playershop.increaseStock(clickedItemString, itemAmount); }
		 * itemStack.setAmount(itemAmount); removeItemFromInventory(inventoryplayer,
		 * itemStack, itemAmount); } // only playershop else if (isPlayershop) {
		 * playe.sendMessage(MessageWrapper.getErrorString("shopowner_not_enough_money")
		 * ); } } // only playershop else if (isPlayershop) { if (itemAmount > 1) {
		 * playe.sendMessage(MessageWrapper.getString("shop_added_item_plural",
		 * String.valueOf(itemAmount))); } else {
		 * playe.sendMessage(MessageWrapper.getString("shop_added_item_singular",
		 * String.valueOf(itemAmount))); } playershop.increaseStock(clickedItemString,
		 * itemAmount); // if the player is in stockpile mode, then the stockpile gets
		 * refreshed playershop.setupStockpile(); itemStack.setAmount(itemAmount);
		 * removeItemFromInventory(inventoryplayer, itemStack, itemAmount); } break; } }
		 * } catch (PlayerException | ShopSystemException e) {
		 * Bukkit.getLogger().warning("[Ultimate_Economy] " + e.getMessage()); } } } }
		 */
	}

	private static void removeItemFromInventory(Inventory inventory, ItemStack item, int amount) {
		int amount2 = amount;
		int amountStack, repairCosts = 0;
		boolean isRepairable = false;
		for (ItemStack s : inventory.getStorageContents()) {
			if (s != null) {
				ItemStack stack = new ItemStack(s);
				Repairable repairable = (Repairable) stack.getItemMeta();
				item.setAmount(1);
				amountStack = stack.getAmount();
				stack.setAmount(1);
				if (item.equals(stack) && amount2 != 0) {

					if (isRepairable) {
						repairable.setRepairCost(repairCosts);
						stack.setItemMeta((ItemMeta) repairable);
					}
					if (amount2 >= amountStack) {
						stack.setAmount(amountStack);
						inventory.removeItem(stack);
						amount2 -= amountStack;
					} else {
						stack.setAmount(amount2);
						inventory.removeItem(stack);
						amount2 -= amount2;
					}
				}
			}
		}
	}

	private static boolean inventoryContainsItems(Inventory inventory, ItemStack item, int amount) {
		boolean bool = false;
		int realAmount = 0;
		int amountStack = 0;
		for (ItemStack s : inventory.getStorageContents()) {
			if (s != null) {
				ItemStack stack = new ItemStack(s);
				item.setAmount(1);
				amountStack = stack.getAmount();
				stack.setAmount(1);
				if (item.equals(stack)) {
					realAmount += amountStack;
				}
			}
		}
		if (realAmount >= amount) {
			bool = true;
		}
		return bool;
	}
}
