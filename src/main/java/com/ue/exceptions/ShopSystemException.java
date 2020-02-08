package com.ue.exceptions;

import com.ue.language.MessageWrapper;

public class ShopSystemException extends Exception {

    private static final long serialVersionUID = 1L;

    private ShopSystemException(String msg) {
	super(msg);
    }

    /**
     * Returns a shop system exception with a formattet message for the minecraft chat.
     * @param key
     * @param params
     * @return shop system exception
     */
    public static ShopSystemException getException(ShopExceptionMessageEnum key, Object... params) {
	switch (key) {
	case ALREADY_RENTED:
	    return new ShopSystemException(MessageWrapper.getErrorString("already_rented", params));
	case SHOP_CHANGEOWNER_ERROR:
	    return new ShopSystemException(MessageWrapper.getErrorString("shop_changeOwner_error"));
	case SHOP_DOES_NOT_EXIST:
	    return new ShopSystemException(MessageWrapper.getErrorString("shop_does_not_exist"));
	case SHOP_ALREADY_EXISTS:
	    return new ShopSystemException(MessageWrapper.getErrorString("shop_already_exists"));
	case INVALID_CHAR_IN_SHOP_NAME:
	    return new ShopSystemException(MessageWrapper.getErrorString("invalid_char_in_shop_name"));
	case INVALID_PRICES:
	    return new ShopSystemException(MessageWrapper.getErrorString("invalid_prices"));
	case INVENTORY_SLOT_EMPTY:
	    return new ShopSystemException(MessageWrapper.getErrorString("inventory_slot_empty"));
	case ITEM_ALREADY_EXISTS:
	    return new ShopSystemException(MessageWrapper.getErrorString("item_already_exists_in_shop"));
	case ITEM_DOES_NOT_EXIST:
	    return new ShopSystemException(MessageWrapper.getErrorString("item_does_not_exist_in_shop"));
	case ITEM_CANNOT_BE_DELETED:
	    return new ShopSystemException(MessageWrapper.getErrorString("item_cannot_be_deleted"));
	case ITEM_UNAVAILABLE:
	    return new ShopSystemException(MessageWrapper.getErrorString("item_unavailable"));
	case ERROR_ON_RENAMING:
	    return new ShopSystemException(MessageWrapper.getErrorString("error_on_rename"));
	case RESIZING_FAILED:
	    return new ShopSystemException(MessageWrapper.getErrorString("resizing_failed"));
	case CANNOT_LOAD_SHOPITEM:
	    return new ShopSystemException(MessageWrapper.getErrorString("cannot_load_shopitem"));
	case CANNOT_LOAD_SHOP:
	    return new ShopSystemException(MessageWrapper.getErrorString("cannot_load_shop"));
	default:
	    return null;
	}
    }

}
