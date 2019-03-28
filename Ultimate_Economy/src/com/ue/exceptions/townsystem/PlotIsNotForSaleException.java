package com.ue.exceptions.townsystem;

public class PlotIsNotForSaleException extends Exception{

	private static final long serialVersionUID = 1L;

	public PlotIsNotForSaleException(String plot) {
		super("The plot " + plot + " is not for sale!");
	}
}
