package com.ue.common.utils;

import javax.inject.Singleton;

import org.bstats.bukkit.Metrics;

import com.ue.ultimate_economy.UltimateEconomy;

import dagger.Module;
import dagger.Provides;

@Module
public class ControllerModule {

	@Singleton
	@Provides
	Metrics provideMetrics() {
		return new Metrics(UltimateEconomy.getInstance, 4652);
	}
}
