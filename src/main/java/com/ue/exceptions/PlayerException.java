package com.ue.exceptions;

import com.ue.language.MessageWrapper;

public class PlayerException extends Exception {

	private static final long serialVersionUID = 1L;

	private PlayerException(String msg) {
		super(msg);
	}

	/**
	 * Returns a player exception with a error message.
	 * 
	 * @param key
	 * @param params
	 * @return player exception
	 */
	public static PlayerException getException(PlayerExceptionMessageEnum key, Object... params) {
		return new PlayerException(MessageWrapper.getErrorString(key.getValue(), params));
	}
}
