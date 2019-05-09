package com.ue.exceptions;

public class JobSystemException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static final String PRICE_IS_INVALID = "The price is invalid!";
	public static final String LOOTTYPE_IS_INVALID = "This loottype is invalid! Use 'treasure','junk' or 'fish'.";
	public static final String LOOTTYPE_ALREADY_EXISTS = "This loottype for a fisherjob already exists in this shop!";
	public static final String LOOTTYPE_DOES_NOT_EXISTS = "This loottype for a fisherjob does not exist in this shop!";
	public static final String JOB_DOES_NOT_EXIST = "This job does not exist!";
	public static final String JOB_ALREADY_EXIST = "This job already exist!";
	public static final String ENTITY_IS_INVALID = "Invalid entity!";
	public static final String ITEM_IS_INVALID = "Invalid item!";
	public static final String ENTITY_ALREADY_EXISTS = "This entity already exists in this job!";
	public static final String ENTITY_DOES_NOT_EXIST = "This entity does not in this job!";
	public static final String ITEM_ALREADY_EXIST = "This item already exists in this job!";
	public static final String ITEM_DOES_NOT_EXIST = "This item does not exist in this job!";
	public static final String CANNOT_LOAD_JOB = "This job could not be loaded from the save file!";
	public static final String CANNOT_LOAD_JOBCENTER = "This jobcenter could not be loaded from the save file!";
	public static final String JOB_NOT_EXIST_IN_JOBCENTER = "This job does not exist in this jobcenter!";
	public static final String JOB_ALREADY_EXIST_IN_JOBCENTER = "This job already exists in this jobcenter!";
	public static final String JOBCENTER_ALREADY_EXIST = "This jobcenter already exist!";
	public static final String JOBCENTER_DOES_NOT_EXIST = "This jobcenter does not exist!";
	public static final String INVALID_INVENTORY_SIZE = "The size is not a multiple of 9!";
	public static final String INVENTORY_SLOT_OCCUPIED = "This slot is occupied!";
	public static final String INVENTORY_SLOT_INVALID = "This slot is invalid!";
	
	public JobSystemException(String msg) {
		super(msg);
	}
}
