package com.ue.exceptions.townsystem;

public class PlayerIsAlreadyCoOwnerException extends Exception{

	private static final long serialVersionUID = 1L;

	public PlayerIsAlreadyCoOwnerException(String player,String location) {
		super("The player " + player + " is already a CoOwner of this " + location + "!");
	}
}
