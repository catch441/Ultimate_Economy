package com.ue.general.impl;

public enum GeneralEconomyExceptionMessageEnum {

	INVALID_PARAMETER("invalid_parameter"), DOES_NOT_EXIST("does_not_exist"), ALREADY_EXISTS("already_exists"),
	NOT_ENOUGH_MONEY("not_enough_money");

	private String value;

	private GeneralEconomyExceptionMessageEnum(String value) {
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
