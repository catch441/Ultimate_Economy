package com.ue.general.impl;

import javax.inject.Inject;
import org.bukkit.plugin.java.JavaPlugin;

import com.ue.common.utils.DaggerServiceComponent;
import com.ue.common.utils.ServiceComponent;

/**
 * @author Lukas Heubach (catch441)
 */
public class PluginImpl extends JavaPlugin {

	public static PluginImpl getInstance;
	public static ServiceComponent serviceComponent;
	@Inject
	UltimateEconomy ultimateEconomy;

	/**
	 * Default constructor.
	 */
	public PluginImpl() {
		super();
		getInstance = this;
		serviceComponent = DaggerServiceComponent.builder().build();
		serviceComponent.inject(this);
	}

	@Override
	public void onEnable() {
		ultimateEconomy.onEnable();
	}

	@Override
	public void onDisable() {
		ultimateEconomy.onDisable();
	}
}
