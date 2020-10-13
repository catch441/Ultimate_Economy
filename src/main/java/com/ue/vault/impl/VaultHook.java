package com.ue.vault.impl;

import javax.inject.Inject;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import com.ue.ultimate_economy.UltimateEconomy;

import net.milkbowl.vault.economy.Economy;

public class VaultHook {

    private UltimateEconomy plugin = UltimateEconomy.getInstance;

    private final Economy vaultEconomy;
    
    /**
     * Vault Hook constructor.
     * @param vaultEconomy
     */
    @Inject
    public VaultHook(Economy vaultEconomy) {
    	this.vaultEconomy = vaultEconomy;
    }

    /**
     * Hooks UE into vault.
     */
    public void hook() {
	Bukkit.getServicesManager().register(Economy.class, vaultEconomy, plugin, ServicePriority.Normal);
    }

    /**
     * Unhooks UE from vault.
     */
    public void unhook() {
	Bukkit.getServicesManager().unregister(Economy.class, vaultEconomy);
    }
}
