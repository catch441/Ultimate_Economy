package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayer;
import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.api.PlayershopController;
import com.ue.shopsystem.impl.ShopItem;
import com.ue.shopsystem.impl.ShopValidationHandler;
import com.ue.townsystem.town.api.TownController;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

public class ShopValidationHandlerTest {

	private static ShopValidationHandler validationHandler;
	private static ServerMock server;
	private static WorldMock world;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		server.addPlayer("catch441");
		validationHandler = new ShopValidationHandler();
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
		int size = PlayershopController.getPlayerShops().size();
		for (int i = 0; i < size; i++) {
			PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
		}
		if (TownworldController.getTownWorldList().size() != 0) {
			try {
				TownworldController.deleteTownWorld(world.getName());
			} catch (TownSystemException | PlayerException | GeneralEconomyException e) {
				assertTrue(false);
			}
		}
	}

	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTest() {
		try {
			validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("0", "0");
			assertTrue(false);
		} catch (ShopSystemException e) {
			assertEquals("§cOne of the prices have to be above 0!", e.getMessage());
		}
	}

	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTestValid1() {
		try {
			validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("none", "0");
		} catch (ShopSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTestValid2() {
		try {
			validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("0", "none");
		} catch (ShopSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForPricesGreaterThenZeroTest() {
		try {
			validationHandler.checkForPricesGreaterThenZero(0, 0);
			assertTrue(false);
		} catch (ShopSystemException e) {
			assertEquals("§cOne of the prices have to be above 0!", e.getMessage());
		}
	}

	@Test
	public void checkForPricesGreaterThenZeroTestValid1() {
		try {
			validationHandler.checkForPricesGreaterThenZero(1, 2);
		} catch (ShopSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForPricesGreaterThenZeroTestValid2() {
		try {
			validationHandler.checkForPricesGreaterThenZero(1, 0);
		} catch (ShopSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForSlotIsNotEmptyTest() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			validationHandler.checkForSlotIsNotEmpty(0, inv, 0);
			assertTrue(false);
		} catch (ShopSystemException | GeneralEconomyException e) {
			assertEquals("§cThis slot is empty!", e.getMessage());
		}
	}

	@Test
	public void checkForSlotIsNotEmptyTestValid() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			inv.setItem(0, new ItemStack(Material.STONE));
			validationHandler.checkForSlotIsNotEmpty(0, inv, 0);
		} catch (ShopSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForSlotIsEmptyTest() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			inv.setItem(0, new ItemStack(Material.STONE));
			validationHandler.checkForSlotIsEmpty(0, inv, 0);
			assertTrue(false);
		} catch (GeneralEconomyException | PlayerException e) {
			assertEquals("§cThis slot is occupied!", e.getMessage());
		}
	}

	@Test
	public void checkForSlotIsEmptyTestValid() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			validationHandler.checkForSlotIsEmpty(0, inv, 0);
		} catch (GeneralEconomyException | PlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void isSlotEmpty() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			inv.setItem(0, new ItemStack(Material.STONE));
			inv.setItem(1, null);
			inv.setItem(2, new ItemStack(Material.AIR));
			// false false
			assertFalse(validationHandler.isSlotEmpty(0, inv, 0));
			// true false
			assertTrue(validationHandler.isSlotEmpty(1, inv, 0));
			// false true
			assertTrue(validationHandler.isSlotEmpty(2, inv, 0));
			// true true, not possible to produce
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForValidAmountTest1() {
		try {
			validationHandler.checkForValidAmount("70");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §470§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidAmountTest2() {
		try {
			validationHandler.checkForValidAmount("-10");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-10§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidAmountTestValid1() {
		try {
			validationHandler.checkForValidAmount("none");
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForValidAmountTestValid2() {
		try {
			validationHandler.checkForValidAmount("20");
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForValidPriceTest1() {
		try {
			validationHandler.checkForValidPrice("-10");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-10§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidPriceTestValid1() {
		try {
			validationHandler.checkForValidPrice("none");
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForValidPriceTestValid2() {
		try {
			validationHandler.checkForValidPrice("10");
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForValidSizeTest1() {
		try {
			validationHandler.checkForValidSize(90);
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §490§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidSizeTest2() {
		try {
			validationHandler.checkForValidSize(5);
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §45§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidSizeTestValid() {
		try {
			validationHandler.checkForValidSize(18);
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForValidSlotTest1() {
		try {
			validationHandler.checkForValidSlot(17, 9, 2);
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §418§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidSlotTest2() {
		try {
			validationHandler.checkForValidSlot(-17, 9, 2);
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-16§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForItemCanBeDeletedTest() {
		try {
			validationHandler.checkForItemCanBeDeleted(8, 9);
			assertTrue(false);
		} catch (ShopSystemException e) {
			assertEquals("§cThis item cannot be deleted!", e.getMessage());
		}
	}

	@Test
	public void checkForItemCanBeDeletedTestValid() {
		try {
			validationHandler.checkForItemCanBeDeleted(5, 9);
		} catch (ShopSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForPositiveValueTest() {
		try {
			validationHandler.checkForPositiveValue(-9);
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-9.0§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForPositiveValueTestValid() {
		try {
			validationHandler.checkForPositiveValue(9);
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForValidStockDecreaseTest() {
		try {
			validationHandler.checkForValidStockDecrease(10, 20);
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §420§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidStockDecreaseTestValid() {
		try {
			validationHandler.checkForValidStockDecrease(10, 5);
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForValidShopNameTest() {
		try {
			validationHandler.checkForValidShopName("invalid_");
			assertTrue(false);
		} catch (ShopSystemException e) {
			assertEquals("§cThis shopname is invalid! Use a name without _!", e.getMessage());
		}
	}

	@Test
	public void checkForValidShopNameTestValid() {
		try {
			validationHandler.checkForValidShopName("valid");
		} catch (ShopSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForIsRentableTest() {
		try {
			validationHandler.checkForIsRentable(false);
			assertTrue(false);
		} catch (ShopSystemException e) {
			assertEquals("§cThis shop is rented!", e.getMessage());
		}
	}

	@Test
	public void checkForIsRentableTestValid() {
		try {
			validationHandler.checkForIsRentable(true);
		} catch (ShopSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForResizePossibleTest() {
		try {
			Inventory inv = Bukkit.createInventory(null, 18);
			inv.setItem(14, new ItemStack(Material.STONE));
			validationHandler.checkForResizePossible(inv, 18, 9, 1);
			assertTrue(false);
		} catch (ShopSystemException | GeneralEconomyException e) {
			assertEquals("§cChanging the shop size has failed due to occupied slots!", e.getMessage());
		}
	}

	@Test
	public void checkForResizePossibleTestValid1() {
		try {
			Inventory inv = Bukkit.createInventory(null, 18);

			validationHandler.checkForResizePossible(inv, 18, 9, 1);
		} catch (ShopSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForResizePossibleTestValid2() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			validationHandler.checkForResizePossible(inv, 9, 18, 1);
		} catch (ShopSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForItemDoesNotExistTest() {
		try {
			List<ShopItem> items = new ArrayList<>();
			ShopItem item = new ShopItem(new ItemStack(Material.STONE), 1, 1, 1);
			ShopItem item2 = new ShopItem(new ItemStack(Material.DIRT), 1, 1, 1);
			items.add(item);
			items.add(item2);
			validationHandler.checkForItemDoesNotExist(item2.getItemString(), items);
			assertTrue(false);
		} catch (ShopSystemException e) {
			assertEquals("§cThis item already exists in this shop!", e.getMessage());
		}
	}

	@Test
	public void checkForItemDoesNotExistTestValid() {
		try {
			List<ShopItem> items = new ArrayList<>();
			ShopItem item = new ShopItem(new ItemStack(Material.STONE), 1, 1, 1);
			ShopItem item2 = new ShopItem(new ItemStack(Material.DIRT), 1, 1, 1);
			items.add(item);
			validationHandler.checkForItemDoesNotExist(item2.getItemString(), items);
		} catch (ShopSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void checkForChangeOwnerIsPossibleTest() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			PlayershopController.createPlayerShop("myshop", new Location(world, 1, 1, 1), 9, ecoPlayer);
			validationHandler.checkForChangeOwnerIsPossible(ecoPlayer, "myshop");
			assertTrue(false);
		} catch (ShopSystemException e) {
			assertEquals("§cThe player has already a shop with the same name!", e.getMessage());
		} catch (PlayerException | TownSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForChangeOwnerIsPossibleTestValid() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			PlayershopController.createPlayerShop("myshop1", new Location(world, 1, 1, 1), 9, ecoPlayer);
			validationHandler.checkForChangeOwnerIsPossible(ecoPlayer, "myshop");
		} catch (ShopSystemException | PlayerException | TownSystemException | GeneralEconomyException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForShopNameIsFreeTest1() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			PlayershopController.createPlayerShop("myshop", new Location(world, 1, 1, 1), 9, ecoPlayer);
			validationHandler.checkForShopNameIsFree("myshop",ecoPlayer);
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertEquals("§c§4myshopcatch441§c already exists!", e.getMessage());
		} catch (PlayerException | TownSystemException | ShopSystemException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForShopNameIsFreeTestValid() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			validationHandler.checkForShopNameIsFree("myshop",ecoPlayer);	
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForPlayerHasPermissionAtLocationTest1() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			TownworldController.createTownWorld(world.getName());
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			validationHandler.checkForPlayerHasPermissionAtLocation(loc, ecoPlayer);
			assertTrue(false);
		} catch (PlayerException | TownSystemException e) {
			assertEquals("§cYou dont have the permission to do that!", e.getMessage());
		}
	}
	
	@Test
	public void checkForPlayerHasPermissionAtLocationTest2() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			TownworldController.createTownWorld(world.getName());
			EconomyPlayerController.createEconomyPlayer("katharina");
			EconomyPlayer ecoPlayer1 = EconomyPlayerController.getAllEconomyPlayers().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(1);
			TownController.createTown(TownworldController.getTownWorldList().get(0), "kthschnll", loc, ecoPlayer);
			validationHandler.checkForPlayerHasPermissionAtLocation(loc, ecoPlayer1);
			assertTrue(false);
		} catch (PlayerException | TownSystemException e) {
			assertEquals("§cYou dont have the permission to do that!", e.getMessage());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void checkForPlayerHasPermissionAtLocationTestValid() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			TownworldController.createTownWorld(world.getName());
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			TownController.createTown(TownworldController.getTownWorldList().get(0), "kthschnll", loc, ecoPlayer);
			validationHandler.checkForPlayerHasPermissionAtLocation(loc, ecoPlayer);
		} catch (GeneralEconomyException | PlayerException | TownSystemException e) {
			assertTrue(false);
		}
	}
}