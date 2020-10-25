package com.ue.shopsystem.logic.impl;

import com.ue.common.utils.MessageWrapper;

public class ShopSystemException extends Exception {

	private static final long serialVersionUID = 1L;
	private final MessageWrapper messageWrapper;
	
	private ShopExceptionMessageEnum key;
	private Object[] params;

	/**
	 * Default constructor.
	 * @param key
	 * @param params
	 * @param messageWrapper
	 */
	public ShopSystemException(MessageWrapper messageWrapper, ShopExceptionMessageEnum key, Object... params) {
		super();
		this.key = key;
		this.params = params;
		this.messageWrapper = messageWrapper;
	}
	
	@Override
	public String getMessage() {
		return messageWrapper.getErrorString(key.getValue(), params);
	}
	
	/**
	 * Returns the params.
	 * @return object array
	 */
	public Object[] getParams() {
		return params;
	}
	
	/**
	 * Returns the message enum key.
	 * 
	 * @return key
	 */
	public ShopExceptionMessageEnum getKey() {
		return key;
	}
}
