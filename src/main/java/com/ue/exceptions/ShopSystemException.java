package com.ue.exceptions;

import com.ue.language.MessageWrapper;

public class ShopSystemException extends Exception {

	private static final long serialVersionUID = 1L;

	private ShopSystemException(String msg) {
		super(msg);
	}

	/**
	 * Returns a shop system exception with a formattet message for the minecraft
	 * chat.
	 * 
	 * @param key
	 * @param params
	 * @return shop system exception
	 */
	public static ShopSystemException getException(ShopExceptionMessageEnum key, Object... params) {
		return new ShopSystemException(MessageWrapper.getErrorString(key.getValue(), params));
	}
}
