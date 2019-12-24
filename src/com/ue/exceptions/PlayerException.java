package com.ue.exceptions;

import ultimate_economy.Ultimate_Economy;

public class PlayerException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static final String INVALID_NUMBER = Ultimate_Economy.messages.getString("invalid_number");
	public static final String PLAYER_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("player_does_not_exist");
	public static final String PLAYER_ALREADY_EXIST = Ultimate_Economy.messages.getString("player_already_exist");
	public static final String JOB_ALREADY_JOINED = Ultimate_Economy.messages.getString("job_already_joined");
	public static final String JOB_NOT_JOINED = Ultimate_Economy.messages.getString("job_not_joined");
	public static final String TOWN_ALREADY_JOINED = Ultimate_Economy.messages.getString("town_already_joined");
	public static final String TOWN_NOT_JOINED = Ultimate_Economy.messages.getString("town_not_joined");
	public static final String HOME_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("home_does_not_exist");
	public static final String HOME_ALREADY_EXIST = Ultimate_Economy.messages.getString("home_already_exist");
	public static final String MAX_JOINED_JOBS = Ultimate_Economy.messages.getString("max_joined_jobs");
	public static final String MAX_JOINED_TOWNS = Ultimate_Economy.messages.getString("max_joined_towns");
	public static final String MAX_HOMES = Ultimate_Economy.messages.getString("max_homes");
	public static final String NOT_ENOUGH_MONEY_PERSONAL = Ultimate_Economy.messages.getString("not_enough_money_personal");
	public static final String NOT_ENOUGH_MONEY_NON_PERSONAL = Ultimate_Economy.messages.getString("not_enough_money_non_personal");
	
	public PlayerException(String msg) {
		super(msg);
	}

}
