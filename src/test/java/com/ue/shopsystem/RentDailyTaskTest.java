package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.api.RentshopController;
import com.ue.shopsystem.impl.RentDailyTask;
import com.ue.shopsystem.impl.RentshopImpl;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class RentDailyTaskTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;

	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		player = server.addPlayer("catch441");
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		int size2 = EconomyPlayerController.getAllEconomyPlayers().size();
		for (int i = 0; i < size2; i++) {
			EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
		}
		UltimateEconomy.getInstance.getDataFolder().delete();
		server.setPlayers(0);
		MockBukkit.unload();
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		int size = RentshopController.getRentShops().size();
		for (int i = 0; i < size; i++) {
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		}
	}

	@Test
	public void runTaskTestWithReminder() {
		try {
			RentshopController.createRentShop(new Location(world, 5, 1, 7), 9, 5);
			RentshopImpl shop = (RentshopImpl) RentshopController.createRentShop(new Location(world, 50, 1, 7), 9, 10);
			EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(10, false);
			shop.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 1);
			shop.setRentUntil(Calendar.getInstance().getTimeInMillis() + 1000L);
		} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
			fail();
		}
		new RentDailyTask().run();
		assertEquals("§6The rent for your shop expires in 10 minutes! Make sure you take out all shop items!",
				player.nextMessage());
		assertNull(player.nextMessage());
	}

	@Test
	public void runTaskTestWithReset() {
		try {
			RentshopController.createRentShop(new Location(world, 5, 1, 7), 9, 5);
			RentshopImpl shop = (RentshopImpl) RentshopController.createRentShop(new Location(world, 50, 1, 7), 9, 10);
			EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(10, false);
			shop.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 1);
			shop.setRentUntil(10L);
			new RentDailyTask().run();
			assertTrue(shop.isRentable());
			assertNull(player.nextMessage());
		} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void runTaskTestWithNoAction() {
		try {
			RentshopController.createRentShop(new Location(world, 5, 1, 7), 9, 5);
			RentshopImpl shop = (RentshopImpl) RentshopController.createRentShop(new Location(world, 50, 1, 7), 9, 10);
			EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(10, false);
			shop.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 1);
			new RentDailyTask().run();
			assertFalse(shop.isRentable());
			assertNull(player.nextMessage());
		} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void runTaskTestWithReminderOffline() {
		try {
			RentshopController.createRentShop(new Location(world, 5, 1, 7), 9, 5);
			RentshopImpl shop = (RentshopImpl) RentshopController.createRentShop(new Location(world, 50, 1, 7), 9, 10);
			EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(10, false);
			shop.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 1);
			shop.setRentUntil(Calendar.getInstance().getTimeInMillis() + 1000L);
		} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
			fail();
		}
		EconomyPlayerController.getAllEconomyPlayers().get(0).setPlayer(null);
		new RentDailyTask().run();
		assertNull(player.nextMessage());
	}
}
