package com.ue.exceptions;

public enum JobExceptionMessageEnum {

    ENTITY_ALREADY_EXISTS ("entity_already_exists"),
    ENTITY_DOES_NOT_EXIST ("entity_does_not_exist"),
    ITEM_ALREADY_EXISTS ("item_already_exists_in_job"),
    ITEM_DOES_NOT_EXIST ("item_does_not_exist_in_job"),
    JOB_NOT_EXIST_IN_JOBCENTER ("job_not_exist_in_jobcenter"),
    LOOTTYPE_ALREADY_EXISTS ("loottype_already_exists"),
    LOOTTYPE_DOES_NOT_EXIST ("loottype_does_not_exist"),
    JOB_ALREADY_EXIST_IN_JOBCENTER ("job_already_exists_in_jobcenter");
	
    private String value; 
    
    private JobExceptionMessageEnum(String value) { 
        this.value = value; 
    } 
  
    /**
     * Returns the value of this enum. The value is the name of the message in the language file.
     * @return string
     */
    public String getValue() { 
        return this.value; 
    } 
}
