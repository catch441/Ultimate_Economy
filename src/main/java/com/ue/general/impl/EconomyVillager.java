package com.ue.general.impl;

public enum EconomyVillager {

	ADMINSHOP("adminshop"),
	PLAYERSHOP("playershop"),
	PLAYERSHOP_RENTABLE("playershop_rentable"),
	PLOTSALE("plotsale"),
	TOWNMANAGER("townmanager"),
	JOBCENTER("jobcenter"),
	UNDEFINED("undefined");

	private String value;

	private EconomyVillager(String value) {
		this.value = value;
	}

	private String getValue() {
		return value;
	}

	/**
	 * Returns a economy villager enum. Return UNDEFINED, if no enum found.
	 * 
	 * @param value
	 * @return economy villager type
	 */
	public static EconomyVillager getEnum(String value) {
		for (EconomyVillager v : values()) {
			if (v.getValue().equalsIgnoreCase(value)) {
				return v;
			}
		}	
		return EconomyVillager.UNDEFINED;
	}
}
