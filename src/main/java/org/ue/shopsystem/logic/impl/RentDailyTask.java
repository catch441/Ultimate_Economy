package org.ue.shopsystem.logic.impl;

import javax.inject.Inject;

import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.RentshopManager;
import org.ue.shopsystem.logic.api.ShopsystemException;

public class RentDailyTask extends BukkitRunnable {

	private static final Logger log = LoggerFactory.getLogger(RentDailyTask.class);
	private static final int TEN_MIN = 12000;
	private final MessageWrapper messageWrapper;
	private final RentshopManager rentshopManager;
	private final ServerProvider serverProvider;

	@Inject
	public RentDailyTask(MessageWrapper messageWrapper, RentshopManager rentshopManager,
			ServerProvider serverProvider) {
		this.messageWrapper = messageWrapper;
		this.rentshopManager = rentshopManager;
		this.serverProvider = serverProvider;
	}

	@Override
	public void run() {
		for (Rentshop shop : rentshopManager.getRentShops()) {
			if (!shop.isRentable()) {
				if (serverProvider.getWorldTime() >= shop.getExpiresAt()) {
					resetShop(shop);
				} else if ((shop.getExpiresAt() - serverProvider.getWorldTime()) < TEN_MIN) {
					sendReminder(shop);
				}
			}
		}
	}

	private void sendReminder(Rentshop shop) {
		if (shop.getOwner().isOnline()) {
			shop.getOwner().getPlayer().sendMessage(messageWrapper.getString(MessageEnum.RENT_REMINDER));
		}
	}

	private void resetShop(Rentshop shop) {
		try {
			shop.resetShop();
		} catch (ShopsystemException e) {
			log.warn("[Ultimate_Economy] Error on rent task: reset shop");
			log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
	}
}
