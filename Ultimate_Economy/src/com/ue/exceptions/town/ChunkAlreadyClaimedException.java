package com.ue.exceptions.town;

public class ChunkAlreadyClaimedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ChunkAlreadyClaimedException(String chunk) {
		super("The chunk " + chunk + " is already claimed!");
	}

}
