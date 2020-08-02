package com.ue.config.logic.impl;

public enum ConfigCommandEnum {

	LANGUAGE,
	MAXHOMES,
	MAXRENTEDDAYS,
	MAXJOBS,
	MAXJOINEDTOWNS,
	HOMES,
	MAXPLAYERSHOPS,
	EXTENDEDINTERACTION,
	WILDERNESSINTERACTION,
	CURRENCY,
	UNKNOWN;

	/**
	 * Returns a enum. Returns ConfigCommandEnum.UNKNOWN, if no enum is found.
	 * 
	 * @param value
	 * @return config command enum
	 */
	public static ConfigCommandEnum getEnum(String value) {
		for (ConfigCommandEnum command : values()) {
			if (command.name().equalsIgnoreCase(value)) {
				return command;
			}
		}
		return ConfigCommandEnum.UNKNOWN;
	}
}
