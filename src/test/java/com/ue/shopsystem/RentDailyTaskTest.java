package com.ue.shopsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Calendar;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.shopsystem.logic.impl.RentDailyTask;
import com.ue.shopsystem.logic.impl.RentshopImpl;
import com.ue.shopsystem.logic.impl.RentshopManagerImpl;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
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
		int size2 = EconomyPlayerManagerImpl.getAllEconomyPlayers().size();
		for (int i = 0; i < size2; i++) {
			EconomyPlayerManagerImpl.deleteEconomyPlayer(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
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
		int size = RentshopManagerImpl.getRentShops().size();
		for (int i = 0; i < size; i++) {
			RentshopManagerImpl.deleteRentShop(RentshopManagerImpl.getRentShops().get(0));
		}
	}

	@Test
	public void runTaskTestWithReminder() {
		try {
			RentshopManagerImpl.createRentShop(new Location(world, 5, 1, 7), 9, 5);
			RentshopImpl shop = (RentshopImpl) RentshopManagerImpl.createRentShop(new Location(world, 50, 1, 7), 9, 10);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(10, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
			shop.setRentUntil(Calendar.getInstance().getTimeInMillis() + 1000L);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
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
			RentshopManagerImpl.createRentShop(new Location(world, 5, 1, 7), 9, 5);
			RentshopImpl shop = (RentshopImpl) RentshopManagerImpl.createRentShop(new Location(world, 50, 1, 7), 9, 10);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(10, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
			shop.setRentUntil(10L);
			new RentDailyTask().run();
			assertTrue(shop.isRentable());
			assertNull(player.nextMessage());
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void runTaskTestWithNoAction() {
		try {
			RentshopManagerImpl.createRentShop(new Location(world, 5, 1, 7), 9, 5);
			RentshopImpl shop = (RentshopImpl) RentshopManagerImpl.createRentShop(new Location(world, 50, 1, 7), 9, 10);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(10, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
			new RentDailyTask().run();
			assertFalse(shop.isRentable());
			assertNull(player.nextMessage());
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
	}
	
	@Test
	public void runTaskTestWithReminderOffline() {
		try {
			RentshopManagerImpl.createRentShop(new Location(world, 5, 1, 7), 9, 5);
			RentshopImpl shop = (RentshopImpl) RentshopManagerImpl.createRentShop(new Location(world, 50, 1, 7), 9, 10);
			EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).increasePlayerAmount(10, false);
			shop.rentShop(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0), 1);
			shop.setRentUntil(Calendar.getInstance().getTimeInMillis() + 1000L);
		} catch (GeneralEconomyException | ShopSystemException | EconomyPlayerException e) {
			fail();
		}
		EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0).setPlayer(null);
		new RentDailyTask().run();
		assertNull(player.nextMessage());
	}
}
