package com.ue.exceptions.town;

public class TownAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public TownAlreadyExistsException(String town) {
		super("The town " + town + " already exists in this townworld!");
	}

}
