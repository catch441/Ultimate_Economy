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
	switch (key) {
	case JOB_DOES_NOT_EXISTS:
	    return new JobSystemException(MessageWrapper.getErrorString("job_does_not_exist"));
	case JOB_ALREADY_EXISTS:
	    return new JobSystemException(MessageWrapper.getErrorString("job_already_exists"));
	case JOBCENTER_ALREADY_EXISTS:
	    return new JobSystemException(MessageWrapper.getErrorString("jobcenter_already_exist"));
	case JOBCENTER_DOES_NOT_EXIST:
	    return new JobSystemException(MessageWrapper.getErrorString("jobcenter_does_not_exist"));
	case ENTITY_ALREADY_EXISTS:
	    return new JobSystemException(MessageWrapper.getErrorString("entity_already_exists"));
	case ENTITY_DOES_NOT_EXIST:
	    return new JobSystemException(MessageWrapper.getErrorString("entity_does_not_exist"));
	case ITEM_ALREADY_EXISTS:
	    return new JobSystemException(MessageWrapper.getErrorString("item_already_exists_in_job"));
	case ITEM_DOES_NOT_EXIST:
	    return new JobSystemException(MessageWrapper.getErrorString("item_does_not_exist_in_job"));
	case JOB_NOT_EXIST_IN_JOBCENTER:
	    return new JobSystemException(MessageWrapper.getErrorString("job_not_exist_in_jobcenter"));
	case LOOTTYPE_ALREADY_EXISTS:
	    return new JobSystemException(MessageWrapper.getErrorString("loottype_already_exists"));
	case LOOTTYPE_DOES_NOT_EXIST:
	    return new JobSystemException(MessageWrapper.getErrorString("loottype_does_not_exist"));
	case JOB_ALREADY_EXIST_IN_JOBCENTER:
	    return new JobSystemException(MessageWrapper.getErrorString("job_already_exists_in_jobcenter"));
	case CANNOT_LOAD_JOB:
	    return new JobSystemException(MessageWrapper.getErrorString("cannot_load_job"));
	case CANNOT_LOAD_JOBCENTER:
	    return new JobSystemException(MessageWrapper.getErrorString("cannot_load_jobcenter"));
	default:
	    return null;
	}
    }

}
