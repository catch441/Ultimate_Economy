package com.ue.common.utils;

import javax.inject.Singleton;

import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsyste.dataaccess.api.JobcenterDao;
import com.ue.shopsystem.dataaccess.api.ShopDao;
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

	/**
	 * Provides a jobcenter data access object.
	 * 
	 * @return jobcenter dao
	 */
	JobcenterDao getJobcenterDao();
	
	/**
	 * Provides a job data access object.
	 * 
	 * @return job dao
	 */
	JobDao getJobDao();
	
	/**
	 * Provides a shop data access object.
	 * 
	 * @return shop dao
	 */
	ShopDao getShopDao();
}
