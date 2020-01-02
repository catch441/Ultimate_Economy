package com.ue.shopsystem.rentshop;

import org.bukkit.scheduler.BukkitRunnable;

public class RentDailyTask extends BukkitRunnable {

	@Override
	public void run() {
		for(Rentshop shop: Rentshop.getRentShops()) {
			shop.handleDaily();
		}
	}
}
