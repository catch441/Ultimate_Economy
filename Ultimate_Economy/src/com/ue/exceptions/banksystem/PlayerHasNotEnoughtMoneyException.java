package com.ue.exceptions.banksystem;

public class PlayerHasNotEnoughtMoneyException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public  PlayerHasNotEnoughtMoneyException(String player) {
		super("The player " + player + " has not enough money!");
	}
	/**
	 * Use this if the player with not enough money are you self
	 * @param player
	 * @param personal
	 */
	public PlayerHasNotEnoughtMoneyException(String player,boolean personal) {
		super("You have not enough money!");
	}

}
