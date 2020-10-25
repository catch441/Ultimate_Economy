package com.ue.economyplayer.logic.impl;

import com.ue.common.utils.MessageWrapper;

public class EconomyPlayerException extends Exception {

	private static final long serialVersionUID = 1L;
	private final MessageWrapper messageWrapper;
	
	private EconomyPlayerExceptionMessageEnum key;
	private Object[] params;

	/**
	 * Default constructor.
	 * @param key
	 * @param params
	 * @param messageWrapper
	 */
	public EconomyPlayerException(MessageWrapper messageWrapper, EconomyPlayerExceptionMessageEnum key, Object... params) {
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
	 * Returns the message key.
	 * @return key
	 */
	public EconomyPlayerExceptionMessageEnum getKey() {
		return key;
	}
}
