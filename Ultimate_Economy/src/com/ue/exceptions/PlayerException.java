package com.ue.exceptions;

public class PlayerException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static final String PLAYER_DOES_NOT_EXIST = "This player does not exist!";
	public static final String PLAYER_ALREADY_EXIST = "This player already exists!";
	public static final String JOB_ALREADY_JOINED = "You already joined this job!";
	public static final String JOB_NOT_JOINED = "You didn't joint this job yet!";
	
	public PlayerException(String msg) {
		super(msg);
	}

}
