package com.ue.townsystem.logic.impl;

public enum TownworldCommandEnum {
	
	ENABLE,
	DISABLE,
	SETFOUNDATIONPRICE,
	SETEXPANDPRICE,
	UNKNOWN;

	/**
	 * Returns a enum. Returns TownworldCommandEnum.UNKNOWN, if no enum is found.
	 * 
	 * @param value
	 * @return town command enum
	 */
	public static TownworldCommandEnum getEnum(String value) {
		for (TownworldCommandEnum command : values()) {
			if (command.name().equalsIgnoreCase(value)) {
				return command;
			}
		}
		return TownworldCommandEnum.UNKNOWN;
	}
}
