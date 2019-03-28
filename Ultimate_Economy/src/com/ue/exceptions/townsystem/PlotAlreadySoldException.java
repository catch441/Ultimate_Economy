package com.ue.exceptions.townsystem;

public class PlotAlreadySoldException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public PlotAlreadySoldException(String plot) {
		super("The plot " + plot + " is already sold!");
	}

}
