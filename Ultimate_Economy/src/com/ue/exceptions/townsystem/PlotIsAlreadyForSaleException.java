package com.ue.exceptions.townsystem;

public class PlotIsAlreadyForSaleException extends Exception{

	private static final long serialVersionUID = 1L;

	public PlotIsAlreadyForSaleException(String plot) {
		super("The plot " + plot + " is not for sale!");
	}
}
