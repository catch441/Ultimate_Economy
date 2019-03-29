package com.ue.exceptions.townsystem;

public class PlayerIsNoCitizenInThisTownException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public  PlayerIsNoCitizenInThisTownException(String player) {
		super("The player " + player + " didn't joined this town yet!");
	}
	/**
	 * Use this if you are the player self
	 * @param player
	 * @param personal
	 */
	public PlayerIsNoCitizenInThisTownException(String player,boolean personal) {
		super("You didn't joined this town yet!");
	}
}
