package com.ue.exceptions;

import com.ue.language.MessageWrapper;

public class TownSystemException extends Exception {

	private static final long serialVersionUID = 1L;

	TownSystemException(String msg) {
		super(msg);
	}

	/**
	 * Returns a town system exception with a error message.
	 * 
	 * @param key
	 * @param params
	 * @return town system exception
	 */
	public static TownSystemException getException(TownExceptionMessageEnum key, Object... params) {
		return new TownSystemException(MessageWrapper.getErrorString(key.getValue(), params));
	}

}
