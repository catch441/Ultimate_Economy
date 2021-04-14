package org.ue.shopsystem.logic.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.ue.shopsystem.logic.api.ShopItem;

public class ShopItemImpl implements ShopItem {

	private int itemHash; // hashed item string
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
	 * @param slot
	 */
	public ShopItemImpl(ItemStack itemStack, int amount, double sellPrice, double buyPrice,int slot) {
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

	@Override
	public ItemStack getItemStack() {
		return itemStack.clone();
	}

	@Override
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public double getSellPrice() {
		return sellPrice;
	}

	@Override
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	@Override
	public double getBuyPrice() {
		return buyPrice;
	}

	@Override
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	@Override
	public int getItemHash() {
		return itemHash;
	}

	@Override
	public int getStock() {
		return stock;
	}

	@Override
	public void setStock(int stock) {
		this.stock = stock;
	}

	@Override
	public int getSlot() {
		return slot;
	}

	/**
	 * Set the slot.
	 * 
	 * @param slot
	 */
	private void setSlot(int slot) {
		this.slot = slot;
	}
	
	/**
	 * Set the unique item string.
	 * 
	 * @param itemString
	 */
	private void setItemString(String itemString) {
		this.itemHash = itemString.hashCode();
	}

}
