package com.ue.exceptions.town;

public class PlayerIsAlreadyCoOwnerException extends Exception{

	private static final long serialVersionUID = 1L;

	public PlayerIsAlreadyCoOwnerException(String player) {
		super("The player " + player + " is already a CoOwner of this town!");
	}
}
