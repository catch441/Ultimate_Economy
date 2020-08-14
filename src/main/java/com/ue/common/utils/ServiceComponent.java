package com.ue.common.utils;

import javax.inject.Singleton;

import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.ultimate_economy.UltimateEconomy;

import dagger.Component;

@Singleton
@Component(modules = { ProviderModule.class })
public interface ServiceComponent {

	/**
	 * Injects UltimateEconomy.
	 * 
	 * @param plugin
	 */
	void inject(UltimateEconomy plugin);
	
	JobcenterDao getJobcenterDao();
}
