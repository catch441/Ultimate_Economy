package com.ue.shopsystem.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopItem {

	private String itemString;
	private ItemStack itemStack;
	private int amount;
	private double sellPrice;
	private double buyPrice;
	private int stock;
	private int slot;

	/**
	 * Creates a new ShopItem.
	 * 
	 * @param itemStack
	 * @param amount
	 * @param sellPrice
	 * @param buyPrice
	 */
	public ShopItem(ItemStack itemStack, int amount, double sellPrice, double buyPrice,int slot) {
		itemStack.setAmount(1);
		setAmount(amount);
		setBuyPrice(buyPrice);
		setSellPrice(sellPrice);
		setItemStack(itemStack.clone());
		setStock(0);
		setSlot(slot);
		if (itemStack.getType() == Material.SPAWNER) {
			setItemString("SPAWNER_" + itemStack.getItemMeta().getDisplayName());
		} else {
			setItemString(itemStack.toString());
		}
	}

	/**
	 * Returns the item stack with a amount of one.
	 * 
	 * @return ItemStack
	 */
	public ItemStack getItemStack() {
		return itemStack.clone();
	}

	/**
	 * Set the item stack with a amount of one.
	 * 
	 * @param itemStack
	 */
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	/**
	 * Returns the item amount.
	 * 
	 * @return integer
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Set the item amout.
	 * 
	 * @param amount
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Returns the sell price.
	 * 
	 * @return double
	 */
	public double getSellPrice() {
		return sellPrice;
	}

	/**
	 * Set the item sell price.
	 * 
	 * @param sellPrice
	 */
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	/**
	 * Returns the buy price.
	 * 
	 * @return double
	 */
	public double getBuyPrice() {
		return buyPrice;
	}

	/**
	 * Set the buy price.
	 * 
	 * @param buyPrice
	 */
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	/**
	 * Returns the unique item string.
	 * 
	 * @return item string
	 */
	public String getItemString() {
		return itemString;
	}

	/**
	 * Set the unique item string.
	 * 
	 * @param itemString
	 */
	public void setItemString(String itemString) {
		this.itemString = itemString;
	}

	/**
	 * Returns the item stock.
	 * 
	 * @return stock
	 */
	public int getStock() {
		return stock;
	}

	/**
	 * Set the item stock.
	 * 
	 * @param stock
	 */
	public void setStock(int stock) {
		this.stock = stock;
	}

	/**
	 * Returns the slot.
	 * 
	 * @return slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * Set the slot.
	 * 
	 * @param slot
	 */
	public void setSlot(int slot) {
		this.slot = slot;
	}

}
