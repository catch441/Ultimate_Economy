package com.ue.exceptions;

import com.ue.language.MessageWrapper;

public class GeneralEconomyException extends Exception {

    private static final long serialVersionUID = 1L;

    private GeneralEconomyException(String msg) {
	super(msg);
    }

    /**
     * Returns a general economy exception with a formattet message for the minecraft chat.
     * @param key
     * @param params
     * @return general economy exception
     */
    public static GeneralEconomyException getException(GeneralEconomyMessageEnum key, Object... params) {
	return new GeneralEconomyException(MessageWrapper.getErrorString(key.getValue(), params));
    }
}
