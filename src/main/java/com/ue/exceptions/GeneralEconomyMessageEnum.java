package com.ue.exceptions;

public enum GeneralEconomyMessageEnum {

    INVALID_PARAMETER ("invalid_parameter");
	
    private String value; 
    
    private GeneralEconomyMessageEnum(String value) { 
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
