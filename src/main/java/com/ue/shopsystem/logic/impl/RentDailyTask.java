package com.ue.shopsystem.logic.impl;

import org.bukkit.scheduler.BukkitRunnable;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.RentshopManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RentDailyTask extends BukkitRunnable {

	private final MessageWrapper messageWrapper;
	private final RentshopManager rentshopManager;
	private final ServerProvider serverProvider;

	/**
	 * Default constructor.
	 * 
	 * @param serverProvider
	 * @param rentshopManager
	 * @param messageWrapper
	 */
	public RentDailyTask(ServerProvider serverProvider, RentshopManager rentshopManager,
			MessageWrapper messageWrapper) {
		this.serverProvider = serverProvider;
		this.messageWrapper = messageWrapper;
		this.rentshopManager = rentshopManager;
	}

	@Override
	public void run() {
		for (Rentshop shop : rentshopManager.getRentShops()) {
			if (!shop.isRentable()) {
				if (serverProvider.getActualTime() >= shop.getRentUntil()) {
					resetShop(shop);
				} else if ((shop.getRentUntil() - serverProvider.getActualTime()) < 600000) {
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
			log.warn("[Ultimate_Economy] Error on rent task: reset shop");
			log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}
}
