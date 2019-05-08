package com.ue.exceptions;

public class ShopSystemException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static final String INVALID_SELL_PRICE = "This sellprice is invalid! (price > 0 or 'none')";
	public static final String INVALID_BUY_PRICE = "This buyprice is invalid! (price > 0 or 'none')";
	public static final String INVALID_PRICES = "One of the prices have to be above 0!";
	public static final String INVALID_AMOUNT ="This amount is invalid! (amount > 0 or 'none')";
	public static final String INVENTORY_SLOT_INVALID = "This slot is invalid!";
	public static final String INVENTORY_SLOT_EMPTY = "This slot is empty!";
	
	public ShopSystemException (String msg) {
		super(msg);
	}

}
