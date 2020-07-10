package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
import com.ue.townsystem.api.TownController;
import com.ue.townsystem.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

public class ShopValidationHandlerTest {

	private static ShopValidationHandler validationHandler;
	private static ServerMock server;
	private static WorldMock world;
	private static Player player;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		player = server.addPlayer("catch441");
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
				fail();
			}
		}
	}

	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTest() {
		try {
			validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("0", "0");
			fail();
		} catch (ShopSystemException e) {
			assertEquals("§cOne of the prices have to be above 0!", e.getMessage());
		}
	}

	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTestValid1() {
		try {
			validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("none", "0");
		} catch (ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTestValid2() {
		try {
			validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("0", "none");
		} catch (ShopSystemException e) {
			fail();
		}
	}
	
	@Test
	public void checkForOnePriceGreaterThenZeroIfBothAvailableTestValid3() {
		try {
			validationHandler.checkForOnePriceGreaterThenZeroIfBothAvailable("1", "1");
		} catch (ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void checkForPricesGreaterThenZeroTest() {
		try {
			validationHandler.checkForPricesGreaterThenZero(0, 0);
			fail();
		} catch (ShopSystemException e) {
			assertEquals("§cOne of the prices have to be above 0!", e.getMessage());
		}
	}

	@Test
	public void checkForPricesGreaterThenZeroTestValid1() {
		try {
			validationHandler.checkForPricesGreaterThenZero(1, 2);
		} catch (ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void checkForPricesGreaterThenZeroTestValid2() {
		try {
			validationHandler.checkForPricesGreaterThenZero(1, 0);
		} catch (ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void checkForSlotIsNotEmptyTest() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			validationHandler.checkForSlotIsNotEmpty(0, inv, 0);
			fail();
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
			fail();
		}
	}

	@Test
	public void checkForSlotIsEmptyTest() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			inv.setItem(0, new ItemStack(Material.STONE));
			validationHandler.checkForSlotIsEmpty(0, inv, 0);
			fail();
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
			fail();
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
			fail();
		}
	}

	@Test
	public void checkForValidAmountTest1() {
		try {
			validationHandler.checkForValidAmount("70");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §470§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidAmountTest2() {
		try {
			validationHandler.checkForValidAmount("-10");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-10§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidAmountTestValid1() {
		try {
			validationHandler.checkForValidAmount("none");
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void checkForValidAmountTestValid2() {
		try {
			validationHandler.checkForValidAmount("20");
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void checkForValidPriceTest1() {
		try {
			validationHandler.checkForValidPrice("-10");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-10§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidPriceTestValid1() {
		try {
			validationHandler.checkForValidPrice("none");
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void checkForValidPriceTestValid2() {
		try {
			validationHandler.checkForValidPrice("10");
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void checkForValidSizeTest1() {
		try {
			validationHandler.checkForValidSize(90);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §490§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidSizeTest2() {
		try {
			validationHandler.checkForValidSize(5);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §45§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidSizeTestValid() {
		try {
			validationHandler.checkForValidSize(18);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void checkForValidSlotTest1() {
		try {
			validationHandler.checkForValidSlot(17, 9, 2);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §418§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidSlotTest2() {
		try {
			validationHandler.checkForValidSlot(-17, 9, 2);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-16§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForItemCanBeDeletedTest() {
		try {
			validationHandler.checkForItemCanBeDeleted(8, 9);
			fail();
		} catch (ShopSystemException e) {
			assertEquals("§cThis item cannot be deleted!", e.getMessage());
		}
	}

	@Test
	public void checkForItemCanBeDeletedTestValid() {
		try {
			validationHandler.checkForItemCanBeDeleted(5, 9);
		} catch (ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void checkForPositiveValueTest() {
		try {
			validationHandler.checkForPositiveValue(-9);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §4-9.0§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForPositiveValueTestValid() {
		try {
			validationHandler.checkForPositiveValue(9);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void checkForValidStockDecreaseTest() {
		try {
			validationHandler.checkForValidStockDecrease(10, 20);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§cThe parameter §420§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void checkForValidStockDecreaseTestValid() {
		try {
			validationHandler.checkForValidStockDecrease(10, 5);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void checkForValidShopNameTest() {
		try {
			validationHandler.checkForValidShopName("invalid_");
			fail();
		} catch (ShopSystemException e) {
			assertEquals("§cThis shopname is invalid! Use a name without _!", e.getMessage());
		}
	}

	@Test
	public void checkForValidShopNameTestValid() {
		try {
			validationHandler.checkForValidShopName("valid");
		} catch (ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void checkForIsRentableTest() {
		try {
			validationHandler.checkForIsRentable(false);
			fail();
		} catch (ShopSystemException e) {
			assertEquals("§cThis shop is rented!", e.getMessage());
		}
	}

	@Test
	public void checkForIsRentableTestValid() {
		try {
			validationHandler.checkForIsRentable(true);
		} catch (ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void checkForResizePossibleTest() {
		try {
			Inventory inv = Bukkit.createInventory(null, 18);
			inv.setItem(14, new ItemStack(Material.STONE));
			validationHandler.checkForResizePossible(inv, 18, 9, 1);
			fail();
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
			fail();
		}
	}

	@Test
	public void checkForResizePossibleTestValid2() {
		try {
			Inventory inv = Bukkit.createInventory(null, 9);
			validationHandler.checkForResizePossible(inv, 9, 18, 1);
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void checkForItemDoesNotExistTest() {
		try {
			List<ShopItem> items = new ArrayList<>();
			ShopItem item = new ShopItem(new ItemStack(Material.STONE), 1, 1, 1,0);
			ShopItem item2 = new ShopItem(new ItemStack(Material.DIRT), 1, 1, 1,1);
			items.add(item);
			items.add(item2);
			validationHandler.checkForItemDoesNotExist(item2.getItemString(), items);
			fail();
		} catch (ShopSystemException e) {
			assertEquals("§cThis item already exists in this shop!", e.getMessage());
		}
	}

	@Test
	public void checkForItemDoesNotExistTestValid() {
		try {
			List<ShopItem> items = new ArrayList<>();
			ShopItem item = new ShopItem(new ItemStack(Material.STONE), 1, 1, 1,0);
			ShopItem item2 = new ShopItem(new ItemStack(Material.DIRT), 1, 1, 1,1);
			items.add(item);
			validationHandler.checkForItemDoesNotExist(item2.getItemString(), items);
		} catch (ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void checkForChangeOwnerIsPossibleTest() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			PlayershopController.createPlayerShop("myshop", new Location(world, 1, 1, 1), 9, ecoPlayer);
			validationHandler.checkForChangeOwnerIsPossible(ecoPlayer, "myshop");
			fail();
		} catch (ShopSystemException e) {
			assertEquals("§cThe player has already a shop with the same name!", e.getMessage());
		} catch (PlayerException | TownSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void checkForChangeOwnerIsPossibleTestValid() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			PlayershopController.createPlayerShop("myshop1", new Location(world, 1, 1, 1), 9, ecoPlayer);
			validationHandler.checkForChangeOwnerIsPossible(ecoPlayer, "myshop");
		} catch (ShopSystemException | PlayerException | TownSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void checkForShopNameIsFreeTest1() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			PlayershopController.createPlayerShop("myshop", new Location(world, 1, 1, 1), 9, ecoPlayer);
			validationHandler.checkForShopNameIsFree("myshop",ecoPlayer);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals("§c§4myshopcatch441§c already exists!", e.getMessage());
		} catch (PlayerException | TownSystemException | ShopSystemException e) {
			fail();
		}
	}
	
	@Test
	public void checkForShopNameIsFreeTestValid() {
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			validationHandler.checkForShopNameIsFree("myshop",ecoPlayer);	
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void checkForPlayerHasPermissionAtLocationTest1() {
		try {
			Location loc = new Location(world, 1, 1, 1);
			TownworldController.createTownWorld(world.getName());
			EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
			validationHandler.checkForPlayerHasPermissionAtLocation(loc, ecoPlayer);
			fail();
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
			fail();
		} catch (PlayerException | TownSystemException e) {
			assertEquals("§cYou dont have the permission to do that!", e.getMessage());
		} catch (GeneralEconomyException e) {
			fail();
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
			fail();
		}
	}
	
	@Test
	public void checkForIsRentedTestValid() {
		try {
			validationHandler.checkForIsRented(false);
		} catch (ShopSystemException e) {
			fail();
		}
	}
	
	@Test
	public void checkForIsRentedTest() {
		try {
			validationHandler.checkForIsRented(true);
			fail();
		} catch (ShopSystemException e) {
			assertEquals("§cThe shop is not rented!", e.getMessage());
		}
	}
	
	@Test
	public void checkForPlayerIsOnlineTestValid() {
		EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
		try {
			validationHandler.checkForPlayerIsOnline(ecoPlayer);
		} catch (PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void checkForPlayerIsOnlineTest() {
		EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
		ecoPlayer.setPlayer(null);
		try {
			validationHandler.checkForPlayerIsOnline(ecoPlayer);
			fail();
		} catch (PlayerException e) {
			assertEquals("§cThe player is not online!", e.getMessage());
			ecoPlayer.setPlayer(player);
		}
	}
	
	@Test
	public void checkForShopOwnerHasEnoughMoneyTestValid() {
		EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
		try {
			ecoPlayer.increasePlayerAmount(1, false);
			validationHandler.checkForShopOwnerHasEnoughMoney(ecoPlayer, 1);
			ecoPlayer.decreasePlayerAmount(1, false);
		} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void checkForShopOwnerHasEnoughMoneyTest() {
		EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
		try {
			validationHandler.checkForShopOwnerHasEnoughMoney(ecoPlayer, 1);
			fail();
		} catch (GeneralEconomyException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe owner has not enough money to buy your items!", e.getMessage());
		}
	}
	
	@Test
	public void checkForPlayerInventoryNotFullTestValid() {
		try {
			validationHandler.checkForPlayerInventoryNotFull(player.getInventory());
		} catch (PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void checkForPlayerInventoryNotFullTest() {
		try {
			player.getInventory().setItem(0, new ItemStack(Material.STONE));
			player.getInventory().setItem(1, new ItemStack(Material.STONE));
			player.getInventory().setItem(2, new ItemStack(Material.STONE));
			player.getInventory().setItem(3, new ItemStack(Material.STONE));
			player.getInventory().setItem(4, new ItemStack(Material.STONE));
			player.getInventory().setItem(5, new ItemStack(Material.STONE));
			player.getInventory().setItem(6, new ItemStack(Material.STONE));
			player.getInventory().setItem(7, new ItemStack(Material.STONE));
			player.getInventory().setItem(8, new ItemStack(Material.STONE));
			player.getInventory().setItem(9, new ItemStack(Material.STONE));
			player.getInventory().setItem(10, new ItemStack(Material.STONE));
			player.getInventory().setItem(11, new ItemStack(Material.STONE));
			player.getInventory().setItem(12, new ItemStack(Material.STONE));
			player.getInventory().setItem(13, new ItemStack(Material.STONE));
			player.getInventory().setItem(14, new ItemStack(Material.STONE));
			player.getInventory().setItem(15, new ItemStack(Material.STONE));
			player.getInventory().setItem(16, new ItemStack(Material.STONE));
			player.getInventory().setItem(17, new ItemStack(Material.STONE));
			player.getInventory().setItem(18, new ItemStack(Material.STONE));
			player.getInventory().setItem(19, new ItemStack(Material.STONE));
			player.getInventory().setItem(20, new ItemStack(Material.STONE));
			player.getInventory().setItem(21, new ItemStack(Material.STONE));
			player.getInventory().setItem(22, new ItemStack(Material.STONE));
			player.getInventory().setItem(23, new ItemStack(Material.STONE));
			player.getInventory().setItem(24, new ItemStack(Material.STONE));
			player.getInventory().setItem(25, new ItemStack(Material.STONE));
			player.getInventory().setItem(26, new ItemStack(Material.STONE));
			player.getInventory().setItem(27, new ItemStack(Material.STONE));
			player.getInventory().setItem(28, new ItemStack(Material.STONE));
			player.getInventory().setItem(29, new ItemStack(Material.STONE));
			player.getInventory().setItem(30, new ItemStack(Material.STONE));
			player.getInventory().setItem(31, new ItemStack(Material.STONE));
			player.getInventory().setItem(32, new ItemStack(Material.STONE));
			player.getInventory().setItem(33, new ItemStack(Material.STONE));
			player.getInventory().setItem(34, new ItemStack(Material.STONE));
			player.getInventory().setItem(35, new ItemStack(Material.STONE));
			player.getInventory().setItem(36, new ItemStack(Material.STONE));
			player.getInventory().setItem(37, new ItemStack(Material.STONE));
			player.getInventory().setItem(38, new ItemStack(Material.STONE));
			player.getInventory().setItem(39, new ItemStack(Material.STONE));
			player.getInventory().setItem(40, new ItemStack(Material.STONE));
			validationHandler.checkForPlayerInventoryNotFull(player.getInventory());
			fail();
		} catch (PlayerException e) {
			assertEquals("§cThere is no free slot in your inventory!", e.getMessage());
			player.getInventory().clear();
		}
	}
}