package org.ue.general.impl;

import org.bukkit.plugin.java.JavaPlugin;
import org.ue.common.utils.UltimateEconomyProvider;

/**
 * @author Lukas Heubach (catch441)
 */
public class PluginImpl extends JavaPlugin {

	public static PluginImpl getInstance;
	private final UltimateEconomy ultimateEconomy;
	public static UltimateEconomyProvider provider;

	/**
	 * Default constructor.
	 */
	public PluginImpl() {
		super();
		getInstance = this;
		provider = UltimateEconomyProvider.build();
		ultimateEconomy = new UltimateEconomy(provider);
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
