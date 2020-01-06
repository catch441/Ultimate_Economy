package com.ue.shopsystem.rentshop.impl;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.ue.shopsystem.rentshop.api.Rentshop;
import com.ue.shopsystem.rentshop.api.RentshopController;

import ultimate_economy.Ultimate_Economy;

public class RentDailyTask extends BukkitRunnable {

	@Override
	public void run() {
		for(Rentshop shop: RentshopController.getRentShops()) {
			if(!shop.isRentable()) {
				if(Calendar.getInstance().getTimeInMillis() >= shop.getRentUntil()) {
					shop.resetShop();
				} else if((shop.getRentUntil() - Calendar.getInstance().getTimeInMillis()) < 600000) {
					if (Bukkit.getPlayer(shop.getOwner()) != null && Bukkit.getPlayer(shop.getOwner()).isOnline()) {
						Bukkit.getPlayer(shop.getOwner()).sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("rent_reminder"));
					}
				}
			}
		}
	}
}
