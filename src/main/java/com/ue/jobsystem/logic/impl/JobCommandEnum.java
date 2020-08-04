package com.ue.jobsystem.logic.impl;

public enum JobCommandEnum {

	CREATE,
	DELETE,
	MOVE,
	ADDJOB,
	REMOVEJOB,
	JOB_CREATE,
	JOB_DELETE,
	JOB_ADDFISHER,
	JOB_REMOVEFISHER,
	JOB_ADDITEM,
	JOB_REMOVEITEM,
	JOB_ADDMOB,
	JOB_REMOVEMOB,
	JOB,
	UNKNOWN;

	/**
	 * Returns a enum. Returns JobCommandEnum.UNKNOWN, if no enum is found.
	 * 
	 * @param value
	 * @return job command enum
	 */
	public static JobCommandEnum getEnum(String value) {
		for (JobCommandEnum command : values()) {
			if (command.name().equalsIgnoreCase(value)) {
				return command;
			}
		}
		return JobCommandEnum.UNKNOWN;
	}
}
