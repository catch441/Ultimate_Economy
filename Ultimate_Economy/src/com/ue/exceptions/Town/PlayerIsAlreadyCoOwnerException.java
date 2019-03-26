package com.ue.exceptions.Town;

public class PlayerIsAlreadyCoOwnerException extends Exception{

	private static final long serialVersionUID = 1L;

	public PlayerIsAlreadyCoOwnerException(String player) {
		super("The player " + player + " is already a CoOwner of this town!");
	}
}
