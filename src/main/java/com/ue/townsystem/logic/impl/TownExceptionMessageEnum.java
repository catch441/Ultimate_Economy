package com.ue.townsystem.logic.impl;

public enum TownExceptionMessageEnum {

	PLOT_IS_ALREADY_FOR_SALE("plot_is_already_for_sale"), PLOT_IS_NOT_FOR_SALE("plot_is_not_for_sale"),
	CHUNK_IS_NOT_CONNECTED_WITH_TOWN("chunk_is_not_connected_with_town"),
	CHUNK_ALREADY_CLAIMED("chunk_already_claimed"), CHUNK_NOT_CLAIMED_BY_TOWN("chunk_not_claimed_by_town"),
	CHUNK_NOT_CLAIMED("chunk_not_claimed"), PLAYER_IS_NOT_MAYOR("player_is_not_mayor"),
	PLAYER_IS_NOT_CITIZEN("player_is_not_citizen"), LOCATION_NOT_IN_TOWN("location_not_in_town"),
	PLAYER_IS_ALREADY_CITIZEN("player_is_already_citizen"), TOWNWORLD_DOES_NOT_EXIST("townworld_does_not_exist"),
	TOWNWORLD_ALREADY_EXIST("townworld_already_exist"), WORLD_DOES_NOT_EXIST("world_does_not_exist"),
	PLAYER_IS_ALREADY_DEPUTY("player_is_already_deputy"), PLAYER_IS_NO_DEPUTY("player_is_no_deputy"),
	PLAYER_IS_ALREADY_RESIDENT("player_is_already_resident"), TOWN_HAS_NOT_ENOUGH_MONEY("town_has_not_enough_money"),
	PLAYER_IS_NO_RESIDENT("player_is_no_resident"),
	PLAYER_IS_ALREADY_OWNER("player_is_already_owner");

	private String value;

	private TownExceptionMessageEnum(String value) {
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
