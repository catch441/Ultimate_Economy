package com.ue.common.utils;

public class ComponentProvider {

	/**
	 * Returns the service component.
	 * 
	 * @return service component
	 */
	public ServiceComponent getServiceComponent() {
		return DaggerServiceComponent.builder().build();
	}
}
