package com.ue.exceptions;

public class ShopSystemException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static final String INVALID_SHOP_NAME = "This shopname is invalid! Use a name without '_','-' and not equals 'Spawner'.";
	public static final String INVALID_CHAR_IN_SHOP_NAME = "This shopname is invalid! Use a name without '_','-'.";
	public static final String INVALID_SELL_PRICE = "This sellprice is invalid!";
	public static final String INVALID_BUY_PRICE = "This buyprice is invalid!";
	public static final String INVALID_PRICES = "One of the prices have to be above 0!";
	public static final String INVALID_AMOUNT ="This amount is invalid!";
	public static final String INVALID_MATERIAL ="This material is invalid!";
	public static final String INVALID_POTIONTYPE ="This potiontype is invalid! Use potion/splash_potion/lingering_potion.";
	public static final String INVALID_POTION_PROPERTY = "This potion property is invalid! Use extended/upgraded/none.";
	public static final String INVENTORY_SLOT_INVALID = "This slot is invalid!";
	public static final String INVENTORY_SLOT_EMPTY = "This slot is empty!";
	public static final String INVENTORY_SLOT_OCCUPIED = "This slot is occupied!";
	public static final String ITEM_ALREADY_EXISTS = "This item already exists in this shop!";
	public static final String CANNOT_LOAD_SHOPITEM = "This item could not be loaded from the save file!";
	public static final String ITEM_CANNOT_BE_DELETED = "This item cannot be deleted!";
	public static final String CANNOT_LOAD_SHOP = "This shop could not be loaded from the save file!";
	public static final String SHOP_DOES_NOT_EXIST = "This shop does not exist!";
	public static final String SHOP_ALREADY_EXISTS = "This shop already exists!";
	public static final String INVALID_INVENTORY_SIZE = "The size is not a multiple of 9!";
	public static final String ITEM_DOES_NOT_EXIST = "This item does not exist!";

	
	public ShopSystemException (String msg) {
		super(msg);
	}

}
