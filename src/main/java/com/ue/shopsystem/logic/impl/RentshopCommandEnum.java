package com.ue.shopsystem.logic.impl;

public enum RentshopCommandEnum {

	CREATE,
	DELETE,
	MOVE,
	RESIZE,
	CHANGEPROFESSION,
	RENAME,
	EDITSHOP,
	UNKNOWN;

	/**
	 * Returns a enum. Returns RentshopCommandEnum.UNKNOWN , if no enum is found.
	 * 
	 * @param value
	 * @return rentshop command enum
	 */
	public static RentshopCommandEnum getEnum(String value) {
		for (RentshopCommandEnum command : values()) {
			if (command.name().equalsIgnoreCase(value)) {
				return command;
			}
		}
		return RentshopCommandEnum.UNKNOWN;
	}
}
