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
