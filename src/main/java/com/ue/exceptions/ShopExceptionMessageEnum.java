package com.ue.exceptions;

public enum ShopExceptionMessageEnum {

    SHOP_CHANGEOWNER_ERROR ("shop_changeOwner_error"), 
    SHOP_DOES_NOT_EXIST ("shop_does_not_exist"), 
    SHOP_ALREADY_EXISTS ("shop_already_exists"), 
    INVALID_CHAR_IN_SHOP_NAME ("invalid_char_in_shop_name"), 
    INVALID_PRICES ("invalid_prices"), 
    INVENTORY_SLOT_EMPTY ("inventory_slot_empty"), 
    ITEM_ALREADY_EXISTS ("item_already_exists_in_shop"), 
    ITEM_DOES_NOT_EXIST ("item_does_not_exist_in_shop"), 
    ITEM_CANNOT_BE_DELETED ("item_cannot_be_deleted"), 
    ITEM_UNAVAILABLE ("item_unavailable"), 
    ERROR_ON_RENAMING ("error_on_rename"), 
    ALREADY_RENTED ("already_rented"), 
    RESIZING_FAILED ("resizing_failed"), 
    CANNOT_LOAD_SHOPITEM ("cannot_load_shopitem"), 
    CANNOT_LOAD_SHOP ("cannot_load_shop");

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
