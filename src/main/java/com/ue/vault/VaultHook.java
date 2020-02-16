package com.ue.vault;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;

import com.ue.ultimate_economy.UltimateEconomy;

import net.milkbowl.vault.economy.Economy;

public class VaultHook {

    private UltimateEconomy plugin = UltimateEconomy.getInstance;

    private Economy provider;

    /**
     * Hooks UE into vault.
     */
    public void hook() {
	provider = plugin.economyImplementer;
	Bukkit.getServicesManager().register(Economy.class, this.provider, this.plugin, ServicePriority.Normal);
	Bukkit.getConsoleSender()
		.sendMessage(ChatColor.GREEN + "VaultAPI hooked into " + ChatColor.AQUA + plugin.getName());
    }

    /**
     * Unhooks UE from vault.
     */
    public void unhook() {
	Bukkit.getServicesManager().unregister(Economy.class, this.provider);
	Bukkit.getConsoleSender()
		.sendMessage(ChatColor.YELLOW + "VaultAPI unhooked from " + ChatColor.AQUA + plugin.getName());

    }
}
