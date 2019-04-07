package com.ue.exceptions.banksystem;

public class TownHasNotEnoughMoneyException extends Exception{

	private static final long serialVersionUID = 1L;

	public TownHasNotEnoughMoneyException(String town) {
		super("The town " + town + " has not enough money!");
	}
}
