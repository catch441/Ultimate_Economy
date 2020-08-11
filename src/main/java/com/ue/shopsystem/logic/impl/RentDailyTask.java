package com.ue.shopsystem.logic.impl;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.ue.common.utils.MessageWrapper;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;
import com.ue.ultimate_economy.GeneralEconomyException;

public class RentDailyTask extends BukkitRunnable {

	private final MessageWrapper messageWrapper;
	private final RentshopManager rentshopManager;

	/**
	 * Default constructor.
	 * 
	 * @param rentshopManager
	 * @param messageWrapper
	 */
	public RentDailyTask(RentshopManager rentshopManager, MessageWrapper messageWrapper) {
		this.messageWrapper = messageWrapper;
		this.rentshopManager= rentshopManager;
	}

	@Override
	public void run() {
		for (Rentshop shop : rentshopManager.getRentShops()) {
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
			shop.getOwner().getPlayer().sendMessage(messageWrapper.getString("rent_reminder"));
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
