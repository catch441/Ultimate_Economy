package com.ue.exceptions.townsystem;

public class TownSystemException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static final String CHUNK_IS_NOT_CONNECTED_WITH_TOWN = "The chunk is not connected to the town!";
	public static final String CHUNK_ALREADY_CLAIMED = "The chunk is already claimed!";
	public static final String CHUNK_NOT_CLAIMED_BY_TOWN = "The chunk is not claimed by this town!";
	public static final String CHUNK_NOT_CLAIMED = "The chunk is not claimed!";
	public static final String LOCATION_NOT_IN_TOWN = "The location is not in this town!";
	public static final String OUTSIDE_OF_THE_PLOT = "You are outside the plot!";
	public static final String PLAYER_HAS_NO_PERMISSION = "You don't have the permission to do that!";
	public static final String PLAYER_IS_ALREADY_CITIZEN = "The player is already a citizen in this town!";
	public static final String PLAYER_IS_ALREADY_COOWNERN = "The player is already a CoOwner of this location!";
	public static final String PLAYER_IS_NO_COOWNER = "The player is no CoOwner of this location!";
	public static final String PLAYER_IS_NOT_CITIZEN = "The player is not a citizen of this town!";
	public static final String PLAYER_IS_NOT_OWNER = "The player is not the owner of this location!";
	public static final String PLAYER_REACHED_MAX_JOINED_TOWNS = "You have reached the max number of joined towns!";
	public static final String PLAYER_IS_ALREADY_OWNER = "The player is already the owner of this location!";
	public static final String YOU_ARE_NO_CITIZEN = "You are not a citizen of this town!";
	public static final String YOU_ARE_ALREADY_CITIZEN = "You are already a citizen in this town!";
	public static final String YOU_ARE_ALREADY_OWNER = "You are already the owner of this location!";
	public static final String YOU_ARE_THE_OWNER = "You are the owner of this location!";
	public static final String YOU_ARE_NOT_OWNER = "You are not the owner of this location!";
	public static final String PLOT_ALREADY_SOLD = "The plot is already sold!";
	public static final String PLOT_IS_ALREADY_FOR_SALE = "The plot is already for sale!";
	public static final String PLOT_IS_NOT_FOR_SALE = "The plot is not for sale!";
	public static final String TOWN_ALREADY_EXISTS = "The town already exists in this townworld!";
	public static final String TOWN_DOES_NOT_EXISTS = "The town does not exist in this townworld!";
	public static final String TOWNWORLD_DOES_NOT_EXIST = "The townworld does not exist!";
	
	public TownSystemException(String msg) {
		super(msg);
	}
	
}
