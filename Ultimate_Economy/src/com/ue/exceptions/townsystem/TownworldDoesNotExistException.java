package com.ue.exceptions.townsystem;

public class TownworldDoesNotExistException extends Exception{

	private static final long serialVersionUID = 1L;

	public TownworldDoesNotExistException(String townworld) {
		super("The townworld " + townworld + " does not exist!");
	}
}
