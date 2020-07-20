package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.api.EconomyPlayerController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.api.AdminshopController;
import com.ue.shopsystem.api.PlayershopController;
import com.ue.shopsystem.api.Rentshop;
import com.ue.shopsystem.api.RentshopController;
import com.ue.shopsystem.impl.ShopEventHandler;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class ShopEventHandlerTest {

	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock player;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		server.setPlayers(0);
		EconomyPlayerController.getAllEconomyPlayers().clear();
		player = server.addPlayer("kthschnll");
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
	
	@Test
	public void handleOpenInventoryTestPlayershop() {
		try {
			PlayershopController.createPlayerShop("myshop", new Location(world, 9, 9, 1), 9, EconomyPlayerController.getAllEconomyPlayers().get(0));
			ShopEventHandler handler = new ShopEventHandler();
			PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, PlayershopController.getPlayerShops().get(0).getShopVillager());
			handler.handleOpenInventory(event);
			assertTrue(event.isCancelled());
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop", inv.getName());
			assertEquals(PlayershopController.getPlayerShops().get(0).getShopVillager(), inv.getHolder());
			PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
		} catch (ShopSystemException | TownSystemException | PlayerException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleOpenInventoryTestAdminshop() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			ShopEventHandler handler = new ShopEventHandler();
			PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, AdminshopController.getAdminshopList().get(0).getShopVillager());
			handler.handleOpenInventory(event);
			assertTrue(event.isCancelled());
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("myshop1", inv.getName());
			assertEquals(AdminshopController.getAdminshopList().get(0).getShopVillager(), inv.getHolder());
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleOpenInventoryTestRentshopNotRented() {
		try {
			RentshopController.createRentShop(new Location(world, 9, 9, 1), 9, 10);
			ShopEventHandler handler = new ShopEventHandler();
			PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, RentshopController.getRentShops().get(0).getShopVillager());
			handler.handleOpenInventory(event);
			assertTrue(event.isCancelled());
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("RentShop#R0", inv.getName());
			assertEquals(RentshopController.getRentShops().get(0).getShopVillager(), inv.getHolder());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleOpenInventoryTestRentshopRented() {
		try {
			Rentshop shop = RentshopController.createRentShop(new Location(world, 9, 9, 1), 9, 7);
			EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(7, false);
			shop.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 1);
			ShopEventHandler handler = new ShopEventHandler();
			PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, RentshopController.getRentShops().get(0).getShopVillager());
			handler.handleOpenInventory(event);
			assertTrue(event.isCancelled());
			ChestInventoryMock inv = (ChestInventoryMock) player.getOpenInventory().getTopInventory();
			assertEquals("Shop#R0", inv.getName());
			assertEquals(RentshopController.getRentShops().get(0).getShopVillager(), inv.getHolder());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestAdminshopLeftClickError() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			ShopEventHandler handler = new ShopEventHandler();
			AdminshopController.getAdminshopList().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertTrue(event.isCancelled());
			assertEquals("§cYou have not enough money!", player.nextMessage());
			assertNull(player.nextMessage());
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestAdminshopLeftClick() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			ShopEventHandler handler = new ShopEventHandler();
			EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(1, false);
			AdminshopController.getAdminshopList().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertTrue(event.isCancelled());
			assertEquals("§6§a1§6 item was bought for §a1.0§6 §a$§6.", player.nextMessage());
			assertNull(player.nextMessage());
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestAdminshopLeftClickInOwnInventory() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			ShopEventHandler handler = new ShopEventHandler();
			AdminshopController.getAdminshopList().get(0).openShopInventory(player);
			player.getInventory().setItem(0, new ItemStack(Material.STONE));
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 36, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertTrue(event.isCancelled());
			assertNull(player.nextMessage());
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestPlayershopLeftClick() {
		try {
			PlayershopController.createPlayerShop("myshop", new Location(world, 9, 9, 1), 9, EconomyPlayerController.getAllEconomyPlayers().get(0));
			PlayershopController.getPlayerShops().get(0).addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			ShopEventHandler handler = new ShopEventHandler();
			PlayershopController.getPlayerShops().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertTrue(event.isCancelled());
			assertEquals("§cThis item is unavailable!", player.nextMessage());
			assertNull(player.nextMessage());
			PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException | TownSystemException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestRentshopLeftClick() {
		try {
			Rentshop shop = RentshopController.createRentShop(new Location(world, 9, 9, 1), 9, 7);
			EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(7, false);
			shop.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 1);
			shop.addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			ShopEventHandler handler = new ShopEventHandler();
			RentshopController.getRentShops().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertTrue(event.isCancelled());
			assertEquals("§cThis item is unavailable!", player.nextMessage());
			assertNull(player.nextMessage());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handlerInventoryClickTestRentshopNotRented() {
		try {
			RentshopController.createRentShop(new Location(world, 9, 9, 1), 9, 7);
			ShopEventHandler handler = new ShopEventHandler();
			EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(7, false);
			RentshopController.getRentShops().get(0).openRentGUI(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertTrue(event.isCancelled());
			assertEquals("§6You rented this shop.", player.nextMessage());
			assertNull(player.nextMessage());
			RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestNullItemClick() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			ShopEventHandler handler = new ShopEventHandler();
			AdminshopController.getAdminshopList().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertFalse(event.isCancelled());
			assertNull(player.nextMessage());
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void handleInventoryClickTestAdminshopEditor() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			ShopEventHandler handler = new ShopEventHandler();
			AdminshopController.getAdminshopList().get(0).openEditor(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertEquals("myshop1-SlotEditor", player.getOpenInventory().getTitle());
			assertTrue(event.isCancelled());
			assertNull(player.nextMessage());
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestAdminshopSlotEditor() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			ShopEventHandler handler = new ShopEventHandler();
			AdminshopController.getAdminshopList().get(0).openSlotEditor(player, 0);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 7, ClickType.LEFT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertEquals("myshop1-Editor", player.getOpenInventory().getTitle());
			assertTrue(event.isCancelled());
			assertNull(player.nextMessage());
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestPlayershopSwitchStockpile() {
		try {
			PlayershopController.createPlayerShop("myshop", new Location(world, 9, 9, 1), 9, EconomyPlayerController.getAllEconomyPlayers().get(0));
			ShopEventHandler handler = new ShopEventHandler();
			PlayershopController.getPlayerShops().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 7, ClickType.MIDDLE, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertEquals("myshop-Stock", player.getOpenInventory().getTitle());
			assertTrue(event.isCancelled());
			assertNull(player.nextMessage());
			PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
		} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestAdminshopSwitchStockpile() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			ShopEventHandler handler = new ShopEventHandler();
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			AdminshopController.getAdminshopList().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0, ClickType.MIDDLE, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertEquals("myshop1", player.getOpenInventory().getTitle());
			assertTrue(event.isCancelled());
			assertNull(player.nextMessage());
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestAdminshopSellSpecific() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			ShopEventHandler handler = new ShopEventHandler();
			ItemStack stack = new ItemStack(Material.STONE);
			stack.setAmount(2);
			player.getInventory().setItem(0,stack);
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			AdminshopController.getAdminshopList().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0, ClickType.RIGHT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertEquals("§6§a1§6 item was sold for §a1.0§6 §a$§6.", player.nextMessage());
			assertTrue(event.isCancelled());
			assertNull(player.nextMessage());
			assertEquals(1,player.getInventory().getItem(0).getAmount());
			player.getInventory().clear();
			EconomyPlayerController.getAllEconomyPlayers().get(0).decreasePlayerAmount(1, false);
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestAdminshopSellAll() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			ShopEventHandler handler = new ShopEventHandler();
			ItemStack stack = new ItemStack(Material.STONE);
			stack.setAmount(2);
			player.getInventory().setItem(0,stack);
			player.getInventory().setItem(1,new ItemStack(Material.ACACIA_DOOR));
			player.getInventory().setItem(2,stack);
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			AdminshopController.getAdminshopList().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 0, ClickType.SHIFT_RIGHT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertEquals("§6§a4§6 items were sold for §a4.0§6 §a$§6.", player.nextMessage());
			assertTrue(event.isCancelled());
			assertNull(player.nextMessage());
			assertNull(player.getInventory().getItem(0));
			assertNull(player.getInventory().getItem(2));
			player.getInventory().clear();
			EconomyPlayerController.getAllEconomyPlayers().get(0).decreasePlayerAmount(4, false);
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestAdminshopSellSpecificOwnInvClick() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			ShopEventHandler handler = new ShopEventHandler();
			ItemStack stack = new ItemStack(Material.STONE);
			stack.setAmount(2);
			player.getInventory().setItem(0,stack);
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			AdminshopController.getAdminshopList().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 36, ClickType.RIGHT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertEquals("§6§a1§6 item was sold for §a1.0§6 §a$§6.", player.nextMessage());
			assertTrue(event.isCancelled());
			assertNull(player.nextMessage());
			assertEquals(1,player.getInventory().getItem(0).getAmount());
			player.getInventory().clear();
			EconomyPlayerController.getAllEconomyPlayers().get(0).decreasePlayerAmount(1, false);
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
	
	@Test
	public void handleInventoryClickTestAdminshopSellAllOwnInvClick() {
		try {
			AdminshopController.createAdminShop("myshop1", new Location(world, 9, 9, 1), 9);
			ShopEventHandler handler = new ShopEventHandler();
			ItemStack stack = new ItemStack(Material.STONE);
			stack.setAmount(2);
			player.getInventory().setItem(0,stack);
			player.getInventory().setItem(1,new ItemStack(Material.ACACIA_DOOR));
			player.getInventory().setItem(2,stack);
			AdminshopController.getAdminshopList().get(0).addShopItem(0, 1, 1, new ItemStack(Material.STONE));
			AdminshopController.getAdminshopList().get(0).openShopInventory(player);
			InventoryClickEvent event = new InventoryClickEvent(player.getOpenInventory(), SlotType.CONTAINER, 36, ClickType.SHIFT_RIGHT, InventoryAction.PICKUP_ONE);
			handler.handleInventoryClick(event);
			assertEquals("§6§a4§6 items were sold for §a4.0§6 §a$§6.", player.nextMessage());
			assertTrue(event.isCancelled());
			assertNull(player.nextMessage());
			assertNull(player.getInventory().getItem(0));
			assertNull(player.getInventory().getItem(2));
			player.getInventory().clear();
			EconomyPlayerController.getAllEconomyPlayers().get(0).decreasePlayerAmount(4, false);
			AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
		} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
			fail();
		}
	}
}
