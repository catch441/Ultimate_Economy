package com.ue.exceptions.townsystem;

public class PlayerIsNotCitizenException extends Exception{

	private static final long serialVersionUID = 1L;

	public PlayerIsNotCitizenException(String player) {
		super("The player " + player + " is not a citizen from this town!");
	}
}
