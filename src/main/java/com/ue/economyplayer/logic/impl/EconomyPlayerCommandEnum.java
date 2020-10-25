package com.ue.economyplayer.logic.impl;

public enum EconomyPlayerCommandEnum {

	BANK,
	MONEY,
	MYJOBS,
	HOME,
	SETHOME,
	DELHOME,
	PAY,
	GIVEMONEY,
	UNKNOWN;

	/**
	 * Returns a enum. Returns EconomyPlayerCommandEnum.UNKNOWN, if no enum is
	 * found.
	 * 
	 * @param value
	 * @return player command enum
	 */
	public static EconomyPlayerCommandEnum getEnum(String value) {
		for (EconomyPlayerCommandEnum command : values()) {
			if (command.name().equalsIgnoreCase(value)) {
				return command;
			}
		}
		return EconomyPlayerCommandEnum.UNKNOWN;
	}
}
