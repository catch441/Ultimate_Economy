package com.ue.shopsystem.impl;

import java.util.Calendar;

import org.bukkit.scheduler.BukkitRunnable;

import com.ue.language.MessageWrapper;
import com.ue.shopsystem.api.Rentshop;
import com.ue.shopsystem.controller.RentshopController;

public class RentDailyTask extends BukkitRunnable {

    @Override
    public void run() {
	for (Rentshop shop : RentshopController.getRentShops()) {
	    if (!shop.isRentable()) {
		if (Calendar.getInstance().getTimeInMillis() >= shop.getRentUntil()) {
		    shop.resetShop();
		} else if ((shop.getRentUntil() - Calendar.getInstance().getTimeInMillis()) < 600000) {
		    if (shop.getOwner().isOnline()) {
			shop.getOwner().getPlayer().sendMessage(MessageWrapper.getString("rent_reminder"));
		    }
		}
	    }
	}
    }
}
