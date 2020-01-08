package com.ue.exceptions;

import com.ue.ultimate_economy.Ultimate_Economy;

public class JobSystemException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public static final String PRICE_IS_INVALID = Ultimate_Economy.messages.getString("price_is_invalid");
	public static final String LOOTTYPE_IS_INVALID = Ultimate_Economy.messages.getString("loottype_is_invalid");
	public static final String LOOTTYPE_ALREADY_EXISTS = Ultimate_Economy.messages.getString("loottype_already_exists");
	public static final String LOOTTYPE_DOES_NOT_EXISTS = Ultimate_Economy.messages.getString("loottype_does_not_exist");
	public static final String JOB_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("job_does_not_exist");
	public static final String JOB_ALREADY_EXIST = Ultimate_Economy.messages.getString("job_already_exists");
	public static final String ENTITY_IS_INVALID = Ultimate_Economy.messages.getString("entity_is_invalid");
	public static final String ITEM_IS_INVALID = Ultimate_Economy.messages.getString("item_is_invalid");
	public static final String ENTITY_ALREADY_EXISTS = Ultimate_Economy.messages.getString("entity_already_exists");
	public static final String ENTITY_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("entity_does_not_exist");
	public static final String ITEM_ALREADY_EXISTS = Ultimate_Economy.messages.getString("item_already_exists_in_job");
	public static final String ITEM_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("item_does_not_exist_in_job");
	public static final String CANNOT_LOAD_JOB = Ultimate_Economy.messages.getString("cannot_load_job");
	public static final String CANNOT_LOAD_JOBCENTER = Ultimate_Economy.messages.getString("cannot_load_jobcenter");
	public static final String JOB_NOT_EXIST_IN_JOBCENTER = Ultimate_Economy.messages.getString("job_not_exist_in_jobcenter");
	public static final String JOB_ALREADY_EXIST_IN_JOBCENTER = Ultimate_Economy.messages.getString("job_already_exists_in_jobcenter");
	public static final String JOBCENTER_ALREADY_EXIST = Ultimate_Economy.messages.getString("jobcenter_already_exist");
	public static final String JOBCENTER_DOES_NOT_EXIST = Ultimate_Economy.messages.getString("jobcenter_does_not_exist");
	public static final String INVALID_INVENTORY_SIZE = Ultimate_Economy.messages.getString("invalid_inventory_size");
	public static final String INVENTORY_SLOT_OCCUPIED = Ultimate_Economy.messages.getString("inventory_slot_occupied");
	public static final String INVENTORY_SLOT_INVALID = Ultimate_Economy.messages.getString("inventory_slot_invalid");
	
	public JobSystemException(String msg) {
		super(msg);
	}
}
