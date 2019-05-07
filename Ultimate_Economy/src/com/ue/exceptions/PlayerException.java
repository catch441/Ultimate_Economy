package com.ue.exceptions;

public class PlayerException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static final String INVALID_NUMBER = "Invalid number!";
	public static final String PLAYER_DOES_NOT_EXIST = "This player does not exist!";
	public static final String PLAYER_ALREADY_EXIST = "This player already exists!";
	public static final String JOB_ALREADY_JOINED = "You already joined this job!";
	public static final String JOB_NOT_JOINED = "You didn't joined this job yet!";
	public static final String TOWN_ALREADY_JOINED = "You already joined this town!";
	public static final String TOWN_NOT_JOINED = "You didn't joined this town yet!";
	public static final String HOME_DOES_NOT_EXIST = "This home does not exist!";
	public static final String HOME_ALREAD_EXIST = "This home already exist!";
	public static final String MAX_JOINED_JOBS = "You already reached the max joined jobs!";
	public static final String MAX_JOINED_TOWNS = "You already reached the max joined towns!";
	public static final String MAX_HOMES = "You already reached the max number of homes!";
	public static final String NOT_ENOUGH_MONEY_PERSONAL ="You have not enough money!";
	public static final String NOT_ENOUGH_MONEY_NON_PERSONAL ="The player have not enough money!";
	
	public PlayerException(String msg) {
		super(msg);
	}

}
