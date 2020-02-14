package com.ue.exceptions;

import com.ue.language.MessageWrapper;

public class JobSystemException extends Exception {

    private static final long serialVersionUID = 1L;

    private JobSystemException(String msg) {
	super(msg);
    }

    /**
     * Returns a job system exception with a formattet message for the minecraft chat.
     * @param key
     * @param params
     * @return job system exception
     */
    public static JobSystemException getException(JobExceptionMessageEnum key, Object... params) {
	return new JobSystemException(MessageWrapper.getErrorString(key.getValue(),params));
    }
}
