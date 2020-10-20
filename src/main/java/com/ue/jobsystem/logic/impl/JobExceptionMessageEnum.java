package com.ue.jobsystem.logic.impl;

public enum JobExceptionMessageEnum {

	JOB_NOT_EXIST_IN_JOBCENTER("job_not_exist_in_jobcenter"),
	JOB_ALREADY_EXIST_IN_JOBCENTER("job_already_exists_in_jobcenter");

	private String value;

	private JobExceptionMessageEnum(String value) {
		this.value = value;
	}

	/**
	 * Returns the value of this enum. The value is the name of the message in the
	 * language file.
	 * 
	 * @return string
	 */
	public String getValue() {
		return this.value;
	}
}
