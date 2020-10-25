package com.ue.general.impl;

import com.ue.common.utils.MessageWrapper;

public class GeneralEconomyException extends Exception {

	private static final long serialVersionUID = 1L;
	private final MessageWrapper messageWrapper;
	
	private GeneralEconomyExceptionMessageEnum key;
	private Object[] params;

	/**
	 * Default constructor.
	 * @param key
	 * @param params
	 * @param messageWrapper
	 */
	public GeneralEconomyException(MessageWrapper messageWrapper, GeneralEconomyExceptionMessageEnum key, Object... params) {
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
	 * Returns the message key.
	 * @return key
	 */
	public GeneralEconomyExceptionMessageEnum getKey() {
		return key;
	}
	
	/**
	 * Returns the params.
	 * @return object array
	 */
	public Object[] getParams() {
		return params;
	}
}
