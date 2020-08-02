package com.ue.common.utils;

import javax.inject.Singleton;

import com.ue.bank.logic.api.BankAccount;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.townsystem.impl.PlotImpl;
import com.ue.townsystem.impl.TownsystemEventHandler;
import com.ue.ultimate_economy.UltimateEconomy;

import dagger.Component;

@Singleton
@Component(modules = {ProviderModule.class})
public interface ServiceComponent {

	/**
	 * Injects UltimateEconomy.
	 * @param plugin
	 */
	void inject(UltimateEconomy plugin);
	
	/**
	 * Injects the townsystem eventhandler.
	 * @param eventHandler
	 */
	void inject(TownsystemEventHandler eventHandler);
	
	/**
	 * Injects a bank account.
	 * @param account
	 */
	void inject(BankAccount account);
	
	/**
	 * Injects a economy player.
	 * @param ecoPlayer
	 */
	void inject(EconomyPlayer ecoPlayer);
	
	/*
	 * Temporal
	 */
	
	/**
	 * s.
	 * @param plot
	 */
	void inject(PlotImpl plot);
}
