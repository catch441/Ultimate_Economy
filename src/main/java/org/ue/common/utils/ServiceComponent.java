package org.ue.common.utils;

import javax.inject.Singleton;

import org.ue.general.impl.PluginImpl;
import org.ue.jobsystem.dataaccess.api.JobDao;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.shopsystem.logic.api.Adminshop;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.townsystem.dataaccess.api.TownworldDao;

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
