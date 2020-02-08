package com.ue.bank.impl;

public class DefaultBankEntity extends AbstractBankEntity{

	private String owner;
	
	/**
	 * Constructor to create a new default bank entity.
	 * @param owner
	 */
	public DefaultBankEntity(String owner) {
		setOwner(owner);
	}

	/**
	 * Returns the name of the bank entity.
	 * @return name of owner.
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Sets the name of the bank entity.
	 * @param owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
}
