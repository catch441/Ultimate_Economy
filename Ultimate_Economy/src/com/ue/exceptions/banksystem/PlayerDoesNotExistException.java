package com.ue.exceptions.banksystem;

public class PlayerDoesNotExistException extends Exception{

	private static final long serialVersionUID = 1L;

	public  PlayerDoesNotExistException(String player) {
		super("The player " + player + " does not exist!");
	}
}
