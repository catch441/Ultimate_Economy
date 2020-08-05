package com.ue.shopsystem.logic.impl;

public enum AdminshopCommandEnum {

	CREATE,
	DELETE,
	RENAME,
	RESIZE,
	MOVE,
	EDITSHOP,
	CHANGEPROFESSION,
	ADDSPAWNER,
	UNKNOWN;

	/**
	 * Returns a enum. Returns AdminshopCommandEnum.UNKNOWN, if no enum is found.
	 * 
	 * @param value
	 * @return adminshop command enum
	 */
	public static AdminshopCommandEnum getEnum(String value) {
		for (AdminshopCommandEnum command : values()) {
			if (command.name().equalsIgnoreCase(value)) {
				return command;
			}
		}
		return AdminshopCommandEnum.UNKNOWN;
	}
}
