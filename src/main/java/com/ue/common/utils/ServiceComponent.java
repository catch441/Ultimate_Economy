package com.ue.common.utils;

import javax.inject.Singleton;

import com.ue.general.impl.PluginImpl;
import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.shopsystem.logic.api.Adminshop;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.townsystem.dataaccess.api.TownworldDao;

import dagger.Component;

@Singleton
@Component(modules = { ProviderModule.class })
public interface ServiceComponent {

	/**
	 * Injects the plugin.
	 * 
	 * @param plugin
	 */
	void inject(PluginImpl plugin);

	/**
	 * Provides a jobcenter.
	 * 
	 * @return jobcenter
	 */
	Jobcenter getJobcenter();

	/**
	 * Provides a adminshop.
	 * 
	 * @return adminshop
	 */
	Adminshop getAdminshop();

	/**
	 * Provides a playershop.
	 * 
	 * @return playershop
	 */
	Playershop getPlayershop();
	
	/**
	 * Provides a rentshop.
	 * 
	 * @return rentshop
	 */
	Rentshop getRentshop();

	/**
	 * Provides a job data access object.
	 * 
	 * @return job dao
	 */
	JobDao getJobDao();

	/**
	 * Provides a townworld data access onject.
	 * 
	 * @return townsystem dao
	 */
	TownworldDao getTownworldDao();
}
