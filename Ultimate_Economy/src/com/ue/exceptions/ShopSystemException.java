package com.ue.exceptions;

import ultimate_economy.Ultimate_Economy;

public class ShopSystemException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static final String INVALID_SHOP_NAME = Ultimate_Economy.messages.getString("invalid_shop_name");
	public static final String INVALID_CHAR_IN_SHOP_NAME = Ultimate_Economy.messages.getString("invalid_char_in_shop_name");
	public static final String INVALID_SELL_PRICE = Ultimate_Economy.messages.getString("invalid_sell_price");
	public static final String INVALID_BUY_PRICE = Ultimate_Economy.messages.getString("invalid_buy_price");
	public static final String INVALID_PRICES = Ultimate_Economy.messages.getString("invalid_prices");
	public static final String INVALID_AMOUNT = Ultimate_Economy.messages.getString("invalid_amount");
	public static final String INVALID_MATERIAL = Ultimate_Economy.messages.getString("invalid_material");
	public static final String INVALID_POTIONTYPE = Ultimate_Economy.messages.getString("invalid_potiontype");
	public static final String INVALID_POTION_PROPERTY = Ultimate_Economy.messages.getString("invalid_potion_property");
	public static final String INVENTORY_SLOT_INVALID = Ultimate_Economy.messages.getString("inventory_slot_invalid");
	public static final String INVENTORY_SLOT_EMPTY = Ultimate_Economy.messages.getString("inventory_slot_empty");
	public static final String INVENTORY_SLOT_OCCUPIED = Ultimate_Economy.messages.getString("inventory_slot_occupied");
	public static final String ITEM_ALREADY_EXISTS = Ultimate_Economy.messages.getString("item_already_exists_in_shop");
	public static final String CANNOT_LOAD_SHOPITEM = Ultimate_Economy.messages.getString("cannot_load_shopitem");
	public static final String ITEM_CANNOT_BE_DELETED = Ultimate_Economy.messages.getString("item_cannot_be_deleted");
	public static final String CANNOT_LOAD_SHOP = Ultimate_Economy.messages.getString("cannot_load_shop");
	public static final String SHOP_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("shop_does_not_exist");
	public static final String SHOP_ALREADY_EXISTS = Ultimate_Economy.messages.getString("shop_already_exists");
	public static final String INVALID_INVENTORY_SIZE = Ultimate_Economy.messages.getString("invalid_inventory_size");
	public static final String ITEM_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("item_does_not_exist_in_shop");
	public static final String ERROR_ON_RENAMING = Ultimate_Economy.messages.getString("error_on_rename");
	public static final String RESIZING_FAILED = Ultimate_Economy.messages.getString("resizing_failed");

	
	public ShopSystemException (String msg) {
		super(msg);
	}

}
