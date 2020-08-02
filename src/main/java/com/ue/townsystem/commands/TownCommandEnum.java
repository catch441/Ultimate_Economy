package com.ue.townsystem.commands;

public enum TownCommandEnum {

	CREATE,
	DELETE,
	EXPAND,
	RENAME,
	SETTOWNSPAWN,
	ADDDEPUTY,
	REMOVEDEPUTY,
	MOVETOWNMANAGER,
	TP,
	PAY,
	WITHDRAW,
	BANK,
	PLOT,
	PLOT_SETFORSALE,
	PLOT_SETFORRENT,
	UNKNOWN;

	/**
	 * Returns a enum. Returns TownCommandEnum.UNKNOWN, if no enum is found.
	 * 
	 * @param value
	 * @return town command enum
	 */
	public static TownCommandEnum getEnum(String value) {
		for (TownCommandEnum command : values()) {
			if (command.name().equalsIgnoreCase(value)) {
				return command;
			}
		}
		return TownCommandEnum.UNKNOWN;
	}
}
