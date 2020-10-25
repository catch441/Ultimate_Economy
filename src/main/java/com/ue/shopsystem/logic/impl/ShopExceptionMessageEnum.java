package com.ue.shopsystem.logic.impl;

public enum ShopExceptionMessageEnum {

	SHOP_CHANGEOWNER_ERROR("shop_changeOwner_error"), INVALID_CHAR_IN_SHOP_NAME("invalid_char_in_shop_name"),
	INVALID_PRICES("invalid_prices"), INVENTORY_SLOT_EMPTY("inventory_slot_empty"),
	ITEM_ALREADY_EXISTS("item_already_exists_in_shop"), ITEM_DOES_NOT_EXIST("item_does_not_exist_in_shop"),
	ITEM_CANNOT_BE_DELETED("item_cannot_be_deleted"), ITEM_UNAVAILABLE("item_unavailable"),
	ERROR_ON_RENAMING("error_on_rename"), ALREADY_RENTED("already_rented"), NOT_RENTED("not_rented"),
	RESIZING_FAILED("resizing_failed"), SHOPOWNER_NOT_ENOUGH_MONEY("shopowner_not_enough_money");

	private String value;

	private ShopExceptionMessageEnum(String value) {
		this.value = value;
	}

	/**
	 * Returns the value of this enum. The value is the name of the message in the
	 * language file.
	 * 
	 * @return string
	 */
	public String getValue() {
		return this.value;
	}
}
