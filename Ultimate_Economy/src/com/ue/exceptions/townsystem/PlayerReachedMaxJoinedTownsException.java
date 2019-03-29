package com.ue.exceptions.townsystem;

public class PlayerReachedMaxJoinedTownsException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public PlayerReachedMaxJoinedTownsException(String player) {
		super("The player " + player + " has reached the max number of joined towns!");
	}
	
	/**
	 * Use this if the player is the same as the command executor
	 * @param player
	 * @param personal
	 */
	public PlayerReachedMaxJoinedTownsException(String player,boolean personal) {
		super("You have reached the max number of joined towns!");
	}

}
