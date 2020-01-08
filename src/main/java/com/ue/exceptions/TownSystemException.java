package com.ue.exceptions;

import com.ue.ultimate_economy.Ultimate_Economy;

public class TownSystemException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static final String CHUNK_IS_NOT_CONNECTED_WITH_TOWN = Ultimate_Economy.messages.getString("chunk_is_not_connected_with_town");
	public static final String CHUNK_ALREADY_CLAIMED = Ultimate_Economy.messages.getString("chunk_already_claimed");
	public static final String CHUNK_NOT_CLAIMED_BY_TOWN = Ultimate_Economy.messages.getString("chunk_not_claimed_by_town");
	public static final String CHUNK_NOT_CLAIMED = Ultimate_Economy.messages.getString("chunk_not_claimed");
	public static final String LOCATION_NOT_IN_TOWN = Ultimate_Economy.messages.getString("location_not_in_town");
	public static final String OUTSIDE_OF_THE_PLOT = Ultimate_Economy.messages.getString("outside_of_the_town");
	public static final String PLAYER_HAS_NO_PERMISSION = Ultimate_Economy.messages.getString("player_has_no_permission");
	public static final String PLAYER_IS_ALREADY_CITIZEN = Ultimate_Economy.messages.getString("player_is_already_citizen");
	public static final String PLAYER_IS_ALREADY_COOWNERN = Ultimate_Economy.messages.getString("player_is_already_coowner");
	public static final String PLAYER_IS_NO_COOWNER = Ultimate_Economy.messages.getString("player_is_no_coowner");
	public static final String PLAYER_IS_NOT_CITIZEN = Ultimate_Economy.messages.getString("player_is_not_citizen");
	public static final String PLAYER_IS_NOT_OWNER = Ultimate_Economy.messages.getString("player_is_not_owner");
	public static final String PLAYER_IS_ALREADY_OWNER = Ultimate_Economy.messages.getString("player_is_already_owner");
	public static final String YOU_ARE_NO_CITIZEN = Ultimate_Economy.messages.getString("you_are_no_citizen");
	public static final String YOU_ARE_ALREADY_CITIZEN = Ultimate_Economy.messages.getString("you_are_already_citizen");
	public static final String YOU_ARE_ALREADY_OWNER = Ultimate_Economy.messages.getString("you_are_already_owner");
	public static final String YOU_ARE_THE_OWNER = Ultimate_Economy.messages.getString("you_are_the_owner");
	public static final String YOU_ARE_NOT_OWNER = Ultimate_Economy.messages.getString("you_are_not_owner");
	public static final String PLOT_IS_ALREADY_FOR_SALE = Ultimate_Economy.messages.getString("plot_is_already_for_sale");
	public static final String PLOT_IS_NOT_FOR_SALE = Ultimate_Economy.messages.getString("plot_is_not_for_sale");
	public static final String TOWN_ALREADY_EXIST = Ultimate_Economy.messages.getString("town_already_exist");
	public static final String TOWN_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("town_does_not_exist");
	public static final String TOWN_HAS_NOT_ENOUGH_MONEY = Ultimate_Economy.messages.getString("town_has_not_enough_money");
	public static final String TOWNWORLD_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("townworld_does_not_exist");
	public static final String TOWNWORLD_ALREADY_EXIST = Ultimate_Economy.messages.getString("townworld_already_exist");
	public static final String WORLD_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("world_does_not_exist");
	
	public TownSystemException(String msg) {
		super(msg);
	}
	
}
