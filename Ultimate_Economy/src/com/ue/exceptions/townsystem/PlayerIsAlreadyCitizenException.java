package com.ue.exceptions.townsystem;

public class PlayerIsAlreadyCitizenException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public PlayerIsAlreadyCitizenException(String player) {
		super("The player " + player + " is already a citizen in this town!");
	}

}
