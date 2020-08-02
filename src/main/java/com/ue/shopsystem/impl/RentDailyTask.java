package com.ue.shopsystem.impl;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.ue.common.utils.MessageWrapper;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.api.Rentshop;
import com.ue.shopsystem.api.RentshopController;
import com.ue.ultimate_economy.GeneralEconomyException;

public class RentDailyTask extends BukkitRunnable {

	@Override
	public void run() {
		for (Rentshop shop : RentshopController.getRentShops()) {
			if (!shop.isRentable()) {
				if (getActualTime() >= shop.getRentUntil()) {
					resetShop(shop);
				} else if ((shop.getRentUntil() - getActualTime()) < 600000) {
					sendReminder(shop);
				}
			}
		}
	}

	private void sendReminder(Rentshop shop) {
		if (shop.getOwner().isOnline()) {
			shop.getOwner().getPlayer().sendMessage(MessageWrapper.getString("rent_reminder"));
		}
	}

	private void resetShop(Rentshop shop) {
		try {
			shop.resetShop();
		} catch (ShopSystemException | GeneralEconomyException e) {
			Bukkit.getLogger().warning("[Ultimate_Economy] Error on rent task");
			Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}

	private long getActualTime() {
		return Calendar.getInstance().getTimeInMillis();
	}
}
