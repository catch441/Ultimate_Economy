package com.ue.shopsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.logic.api.Playershop;
import com.ue.shopsystem.logic.api.PlayershopController;
import com.ue.shopsystem.logic.impl.PlayershopImpl;
import com.ue.townsystem.api.TownController;
import com.ue.townsystem.api.TownworldController;
import com.ue.ultimate_economy.EconomyVillager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class PlayershopTest {

	private static final String SLOTEMPTY = "http://textures.minecraft.net/texture/"
			+ "b55d5019c8d55bcb9dc3494ccc3419757f89c3384cf3c9abec3f18831f35b0";
	private static ServerMock server;
	private static WorldMock world;
	private static PlayerMock owner, otherPlayer;

	@BeforeAll
	public static void initPlugin() {
		server = MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		owner = server.addPlayer("catch441");
		otherPlayer = server.addPlayer("Wulfgar");
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
		int size = PlayershopController.getPlayerShops().size();
		for (int i = 0; i < size; i++) {
			PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
		}
		if (TownworldController.getTownWorldList().size() != 0) {
			try {
				TownworldController.deleteTownWorld(world.getName());
			} catch (TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
				fail();
			}
		}
	}

	@Test
	public void loadConstructorTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			EconomyPlayer owner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE));
			shop.increaseStock(0, 10);
			Playershop loaded = new PlayershopImpl(null, "P0");
			assertEquals(1, loaded.getItemList().size());
			assertEquals(10, loaded.getShopItem(0).getStock());
			assertEquals(Material.STONE, loaded.getShopItem(0).getItemStack().getType());
			assertEquals("2.0", String.valueOf(loaded.getShopItem(0).getBuyPrice()));
			assertEquals("1.0", String.valueOf(loaded.getShopItem(0).getSellPrice()));
			assertEquals(owner, loaded.getOwner());
			assertEquals("myshop_catch441", loaded.getShopVillager().getCustomName());
			assertEquals(EconomyVillager.PLAYERSHOP, loaded.getShopVillager().getMetadata("ue-type").get(0).value());
			// check shop inventory
			ChestInventoryMock shopInv = (ChestInventoryMock) loaded.getShopInventory();
			assertEquals(9, shopInv.getSize());
			assertEquals("myshop", shopInv.getName());
			assertEquals(Material.CRAFTING_TABLE, shopInv.getItem(7).getType());
			assertEquals("Stock", shopInv.getItem(7).getItemMeta().getDisplayName());
			assertEquals(2, shopInv.getItem(7).getItemMeta().getLore().size());
			assertEquals(ChatColor.RED + "Only for Shopowner", shopInv.getItem(7).getItemMeta().getLore().get(0));
			assertEquals(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "open/close stockpile",
					shopInv.getItem(7).getItemMeta().getLore().get(1));
			assertEquals(Material.ANVIL, shopInv.getItem(8).getType());
			assertEquals("Info", shopInv.getItem(8).getItemMeta().getDisplayName());
			assertEquals("§6Rightclick: §asell specified amount", shopInv.getItem(8).getItemMeta().getLore().get(0));
			assertEquals("§6Shift-Rightclick: §asell all", shopInv.getItem(8).getItemMeta().getLore().get(1));
			assertEquals("§6Leftclick: §abuy", shopInv.getItem(8).getItemMeta().getLore().get(2));
			// check editor inventory
			loaded.openEditor(owner.getPlayer());
			ChestInventoryMock editor = (ChestInventoryMock) owner.getPlayer().getOpenInventory().getTopInventory();
			owner.getPlayer().closeInventory();
			assertEquals(loaded.getShopVillager(), editor.getHolder());
			assertEquals(9, editor.getSize());
			assertEquals("myshop-Editor", editor.getName());
			assertNull(editor.getItem(7));
			assertNull(editor.getItem(8));
			// check stock inventory
			ChestInventoryMock stock = (ChestInventoryMock) loaded.getStockpileInventory();
			assertEquals(loaded.getShopVillager(), stock.getHolder());
			assertEquals(9, stock.getSize());
			assertEquals("myshop-Stock", stock.getName());
			assertEquals(Material.STONE, stock.getItem(0).getType());
			assertEquals("§a10§6 Items", stock.getItem(0).getItemMeta().getLore().get(0));
			assertNull(stock.getItem(1));
			assertNull(stock.getItem(2));
			assertNull(stock.getItem(3));
			assertNull(stock.getItem(4));
			assertNull(stock.getItem(5));
			assertNull(stock.getItem(6));
			assertNull(stock.getItem(7));
			assertEquals("Infos", stock.getItem(8).getItemMeta().getDisplayName());
			assertEquals(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "close stockpile",
					stock.getItem(8).getItemMeta().getLore().get(0));
			assertEquals(ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "add specified amount",
					stock.getItem(8).getItemMeta().getLore().get(1));
			assertEquals(ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "add all",
					stock.getItem(8).getItemMeta().getLore().get(2));
			assertEquals(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "get specified amount",
					stock.getItem(8).getItemMeta().getLore().get(3));
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void buyShopItemTestWithTooSmallStock() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE));
			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			shop.buyShopItem(0, player, true);
			fail();
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThis item is unavailable!", e.getMessage());
		}
	}

	@Test
	public void buyShopItemTestWithInvalidSlot() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			shop.buyShopItem(10, player, true);
			fail();
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §411§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void buyShopItemTestWithEmptySlot() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			shop.buyShopItem(6, player, true);
			fail();
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThis slot is empty!", e.getMessage());
		}
	}

	@Test
	public void buyShopItemTestWithOfflinePlayer() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE));
			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			shop.increaseStock(0, 10);
			player.setPlayer(null);
			shop.buyShopItem(0, player, true);
			fail();
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cThe player is not online!", e.getMessage());
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			ecoPlayer.setPlayer(otherPlayer);
		}
	}

	@Test
	public void buyShopItemTestWithFullInventory() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.ACACIA_BOAT));
			shop.increaseStock(0, 10);
			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			player.increasePlayerAmount(10, false);
			player.getPlayer().getInventory().setItem(0, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(1, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(2, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(3, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(4, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(5, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(6, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(7, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(8, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(9, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(10, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(11, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(12, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(13, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(14, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(15, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(16, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(17, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(18, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(19, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(20, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(21, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(22, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(23, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(24, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(25, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(26, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(27, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(28, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(29, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(30, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(31, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(32, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(33, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(34, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(35, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(36, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(37, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(38, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(39, new ItemStack(Material.STONE));
			player.getPlayer().getInventory().setItem(40, new ItemStack(Material.STONE));
			shop.buyShopItem(0, player, true);
			fail();
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			assertEquals("§cThere is no free slot in your inventory!", e.getMessage());
			assertTrue(e instanceof EconomyPlayerException);

			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			try {
				player.decreasePlayerAmount(10, false);
			} catch (GeneralEconomyException | EconomyPlayerException e1) {
				fail();
			}
			player.getPlayer().getInventory().clear();
		}
	}

	@Test
	public void buyShopItemTestWithSingular() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE));
			EconomyPlayer ecoPlayerOwner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			player.increasePlayerAmount(10, false);
			shop.increaseStock(0, 10);
			shop.buyShopItem(0, player, true);
			assertEquals("§6§a1§6 item was bought for §a2.0§6 §a$§6.", ((PlayerMock) player.getPlayer()).nextMessage());
			assertNull(((PlayerMock) player.getPlayer()).nextMessage());
			assertNull(owner.nextMessage());
			assertEquals(9, shop.getShopItem(0).getStock());
			assertEquals("8.0", String.valueOf(player.getBankAccount().getAmount()));
			assertEquals("2.0", String.valueOf(ecoPlayerOwner.getBankAccount().getAmount()));
			assertEquals(Material.STONE, player.getPlayer().getInventory().getItem(0).getType());
			assertEquals(1, player.getPlayer().getInventory().getItem(0).getAmount());
			player.decreasePlayerAmount(8, false);
			ecoPlayerOwner.decreasePlayerAmount(2, false);
			player.getPlayer().getInventory().clear(0);
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void buyShopItemTestWithPlural() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			ItemStack item = new ItemStack(Material.STONE);
			item.setAmount(2);
			shop.addShopItem(0, 1, 4, item);
			EconomyPlayer ecoPlayerOwner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			player.increasePlayerAmount(10, false);
			shop.increaseStock(0, 10);
			shop.buyShopItem(0, player, true);
			assertEquals("§6§a2§6 items were bought for §a4.0§6 §a$§6.",
					((PlayerMock) player.getPlayer()).nextMessage());
			assertNull(((PlayerMock) player.getPlayer()).nextMessage());
			assertNull(owner.nextMessage());
			assertEquals(8, shop.getShopItem(0).getStock());
			assertEquals("6.0", String.valueOf(player.getBankAccount().getAmount()));
			assertEquals("4.0", String.valueOf(ecoPlayerOwner.getBankAccount().getAmount()));
			assertEquals(Material.STONE, player.getPlayer().getInventory().getItem(0).getType());
			assertEquals(2, player.getPlayer().getInventory().getItem(0).getAmount());
			player.decreasePlayerAmount(6, false);
			ecoPlayerOwner.decreasePlayerAmount(4, false);
			player.getPlayer().getInventory().clear(0);
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void buyShopItemTestWithNoBuyPrice() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			ItemStack item = new ItemStack(Material.STONE);
			item.setAmount(2);
			shop.addShopItem(0, 1, 0, item);
			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			player.increasePlayerAmount(10, false);
			shop.increaseStock(0, 10);
			shop.buyShopItem(0, player, true);
			assertNull(((PlayerMock) player.getPlayer()).nextMessage());
			assertNull(owner.nextMessage());
			player.decreasePlayerAmount(10, false);
			player.getPlayer().getInventory().clear(0);
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void buyShopItemTestWithSingularAsOwner() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE));
			EconomyPlayer ecoPlayerOwner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			shop.increaseStock(0, 10);
			shop.buyShopItem(0, ecoPlayerOwner, true);
			assertEquals("§6You got §a1§6 item from the shop.",
					((PlayerMock) ecoPlayerOwner.getPlayer()).nextMessage());
			assertNull(((PlayerMock) ecoPlayerOwner.getPlayer()).nextMessage());
			assertNull(owner.nextMessage());
			assertEquals(9, shop.getShopItem(0).getStock());
			assertEquals("0.0", String.valueOf(ecoPlayerOwner.getBankAccount().getAmount()));
			assertEquals(Material.STONE, ecoPlayerOwner.getPlayer().getInventory().getItem(0).getType());
			assertEquals(1, ecoPlayerOwner.getPlayer().getInventory().getItem(0).getAmount());
			ecoPlayerOwner.getPlayer().getInventory().clear(0);
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void buyShopItemTestWithPluralAsOwner() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE, 2));
			EconomyPlayer ecoPlayerOwner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			shop.increaseStock(0, 10);
			shop.buyShopItem(0, ecoPlayerOwner, true);
			assertEquals("§6You got §a2§6 items from the shop.",
					((PlayerMock) ecoPlayerOwner.getPlayer()).nextMessage());
			assertNull(((PlayerMock) ecoPlayerOwner.getPlayer()).nextMessage());
			assertNull(owner.nextMessage());
			assertEquals(8, shop.getShopItem(0).getStock());
			assertEquals("0.0", String.valueOf(ecoPlayerOwner.getBankAccount().getAmount()));
			assertEquals(Material.STONE, ecoPlayerOwner.getPlayer().getInventory().getItem(0).getType());
			assertEquals(2, ecoPlayerOwner.getPlayer().getInventory().getItem(0).getAmount());
			ecoPlayerOwner.getPlayer().getInventory().clear(0);
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void buyShopItemTestWithNotEnoughMoney() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE, 2));
			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			shop.increaseStock(0, 10);
			shop.buyShopItem(0, player, true);
			fail();
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou have not enough money!", e.getMessage());
		}
	}

	@Test
	public void sellShopItemTestWithInvalidSlot() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			shop.sellShopItem(8, 10, ecoPlayer, true);
			fail();
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §49§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void sellShopItemTestWithEmptySlot() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			shop.sellShopItem(0, 10, ecoPlayer, true);
			fail();
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThis slot is empty!", e.getMessage());
		}
	}

	@Test
	public void sellShopItemTestWithOfflinePlayer() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE));
			EconomyPlayer player = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			shop.increaseStock(0, 10);
			player.setPlayer(null);
			shop.sellShopItem(0, 1, player, true);
			fail();
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cThe player is not online!", e.getMessage());
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			ecoPlayer.setPlayer(otherPlayer);
		}
	}

	@Test
	public void sellShopItemTestWithOwnerNorEnoughMoney() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE));
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			shop.sellShopItem(0, 10, ecoPlayer, true);
			fail();
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe owner has not enough money to buy your items!", e.getMessage());
		}
	}

	@Test
	public void sellShopItemTestWithSingular() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE));
			EconomyPlayer ecoPlayerOwner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayerOwner.increasePlayerAmount(1, false);
			EconomyPlayer other = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			other.getPlayer().getInventory().setItem(0, new ItemStack(Material.STONE));
			shop.sellShopItem(0, 1, other, true);
			assertEquals("§6§a1§6 item was sold for §a1.0§6 §a$§6.", ((PlayerMock) other.getPlayer()).nextMessage());
			assertNull(((PlayerMock) other.getPlayer()).nextMessage());
			assertNull(owner.nextMessage());
			assertEquals(1, shop.getShopItem(0).getStock());
			assertEquals("0.0", String.valueOf(ecoPlayerOwner.getBankAccount().getAmount()));
			assertEquals("1.0", String.valueOf(other.getBankAccount().getAmount()));
			assertNull(other.getPlayer().getInventory().getItem(0));
			other.decreasePlayerAmount(1, false);
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}
	
	@Test
	public void sellShopItemTestOnyBuyPrice() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 0, 2, new ItemStack(Material.STONE));
			EconomyPlayer ecoPlayerOwner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			EconomyPlayer other = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			shop.sellShopItem(0, 1, other, true);
			assertNull(((PlayerMock) other.getPlayer()).nextMessage());
			assertNull(owner.nextMessage());
			assertEquals(0, shop.getShopItem(0).getStock());
			assertEquals("0.0", String.valueOf(ecoPlayerOwner.getBankAccount().getAmount()));
			assertEquals("0.0", String.valueOf(other.getBankAccount().getAmount()));
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void sellShopItemTestWithPlural() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE, 2));
			EconomyPlayer ecoPlayerOwner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayerOwner.increasePlayerAmount(1.5, false);
			EconomyPlayer other = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1);
			other.getPlayer().getInventory().setItem(0, new ItemStack(Material.STONE, 4));
			shop.sellShopItem(0, 3, other, true);
			assertEquals("§6§a3§6 items were sold for §a1.5§6 §a$§6.", ((PlayerMock) other.getPlayer()).nextMessage());
			assertNull(((PlayerMock) other.getPlayer()).nextMessage());
			assertNull(owner.nextMessage());
			assertEquals(3, shop.getShopItem(0).getStock());
			assertEquals("0.0", String.valueOf(ecoPlayerOwner.getBankAccount().getAmount()));
			assertEquals("1.5", String.valueOf(other.getBankAccount().getAmount()));
			assertEquals(Material.STONE, other.getPlayer().getInventory().getItem(0).getType());
			assertEquals(1, other.getPlayer().getInventory().getItem(0).getAmount());
			other.getPlayer().getInventory().clear(0);
			other.decreasePlayerAmount(1.5, false);
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void sellShopItemTestWithSingularAsOwner() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE, 2));
			EconomyPlayer ecoPlayerOwner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayerOwner.getPlayer().getInventory().setItem(0, new ItemStack(Material.STONE, 4));
			shop.sellShopItem(0, 1, ecoPlayerOwner, true);
			assertEquals("§6You added §a1§6 item to your shop.",
					((PlayerMock) ecoPlayerOwner.getPlayer()).nextMessage());
			assertNull(((PlayerMock) ecoPlayerOwner.getPlayer()).nextMessage());
			assertNull(owner.nextMessage());
			assertEquals(1, shop.getShopItem(0).getStock());
			assertEquals("0.0", String.valueOf(ecoPlayerOwner.getBankAccount().getAmount()));
			assertEquals(Material.STONE, ecoPlayerOwner.getPlayer().getInventory().getItem(0).getType());
			assertEquals(3, ecoPlayerOwner.getPlayer().getInventory().getItem(0).getAmount());
			ecoPlayerOwner.getPlayer().getInventory().clear(0);
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void sellShopItemTestWithPluralAsOwner() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 1, 2, new ItemStack(Material.STONE, 2));
			EconomyPlayer ecoPlayerOwner = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			ecoPlayerOwner.getPlayer().getInventory().setItem(0, new ItemStack(Material.STONE, 4));
			shop.sellShopItem(0, 3, ecoPlayerOwner, true);
			assertEquals("§6You added §a3§6 items to your shop.",
					((PlayerMock) ecoPlayerOwner.getPlayer()).nextMessage());
			assertNull(((PlayerMock) ecoPlayerOwner.getPlayer()).nextMessage());
			assertNull(owner.nextMessage());
			assertEquals(3, shop.getShopItem(0).getStock());
			assertEquals("0.0", String.valueOf(ecoPlayerOwner.getBankAccount().getAmount()));
			assertEquals(Material.STONE, ecoPlayerOwner.getPlayer().getInventory().getItem(0).getType());
			assertEquals(1, ecoPlayerOwner.getPlayer().getInventory().getItem(0).getAmount());
			ecoPlayerOwner.getPlayer().getInventory().clear(0);
		} catch (ShopSystemException | TownSystemException | EconomyPlayerException | GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void isOwnerTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			assertTrue(shop.isOwner(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0)));
			assertFalse(shop.isOwner(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1)));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void increaseStockTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			ItemStack stack = new ItemStack(Material.STONE);
			shop.addShopItem(0, 0, 1, stack);
			shop.increaseStock(0, 30);
			assertEquals("§a30§6 Items", shop.getStockpileInventory().getItem(0).getItemMeta().getLore().get(0));
			// check save file
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(30, config.getInt("ShopItems." + stack.toString() + ".stock"));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void increaseStockTestWithInvalidStock() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			ItemStack stack = new ItemStack(Material.STONE);
			shop.addShopItem(0, 0, 1, stack);
			shop.increaseStock(0, -30);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4-30.0§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void decreaseStockTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			ItemStack stack = new ItemStack(Material.STONE);
			shop.addShopItem(0, 0, 1, stack);
			shop.increaseStock(0, 30);
			shop.decreaseStock(0, 10);
			assertEquals("§a20§6 Items", shop.getStockpileInventory().getItem(0).getItemMeta().getLore().get(0));
			// check save file
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(20, config.getInt("ShopItems." + stack.toString() + ".stock"));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void decreaseStockTestWithToSingular() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			ItemStack stack = new ItemStack(Material.STONE);
			shop.addShopItem(0, 0, 1, stack);
			shop.increaseStock(0, 30);
			shop.decreaseStock(0, 29);
			assertEquals("§a1§6 Item", shop.getStockpileInventory().getItem(0).getItemMeta().getLore().get(0));
			// check save file
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals(1, config.getInt("ShopItems." + stack.toString() + ".stock"));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void decreaseStockTestWithInvalidStock() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			ItemStack stack = new ItemStack(Material.STONE);
			shop.addShopItem(0, 0, 1, stack);
			shop.decreaseStock(0, 10);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThis item is unavailable!", e.getMessage());
		}
	}

	@Test
	public void decreaseStockTestWithNegativeStockStock() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			ItemStack stack = new ItemStack(Material.STONE);
			shop.addShopItem(0, 0, 1, stack);
			shop.increaseStock(0, 10);
			shop.decreaseStock(0, -10);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4-10.0§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void isAvailableTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 0, 1, new ItemStack(Material.STONE));
			shop.addShopItem(1, 0, 1, new ItemStack(Material.ACACIA_WOOD));
			shop.increaseStock(0, 30);
			assertTrue(shop.isAvailable(0));
			assertFalse(shop.isAvailable(1));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void isAvailableTestWithInvalidSlot() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 0, 1, new ItemStack(Material.STONE));
			shop.increaseStock(0, 30);
			shop.isAvailable(-10);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§cThe parameter §4-9§c is invalid!", e.getMessage());
		}
	}

	@Test
	public void changeOwnerTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.changeOwner(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1));
			assertEquals(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1), shop.getOwner());
			// check save file
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals("Wulfgar", config.getString("Owner"));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void changeOwnerTestNoPossible() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.changeOwner(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(1));
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThe player has already a shop with the same name!", e.getMessage());
		}
	}

	@Test
	public void addItemTestWithReservedSlot() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 9,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(7, 1, 1, new ItemStack(Material.STONE));
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cThis slot is occupied!", e.getMessage());
		}
	}

	@Test
	public void changeSizeTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 18,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.changeShopSize(9);
			assertEquals(9, shop.getSize());
			Inventory shopInv = shop.getShopInventory();
			assertEquals(Material.ANVIL, shopInv.getItem(8).getType());
			assertEquals("Info", shopInv.getItem(8).getItemMeta().getDisplayName());
			assertEquals("§6Rightclick: §asell specified amount", shopInv.getItem(8).getItemMeta().getLore().get(0));
			assertEquals("§6Shift-Rightclick: §asell all", shopInv.getItem(8).getItemMeta().getLore().get(1));
			assertEquals("§6Leftclick: §abuy", shopInv.getItem(8).getItemMeta().getLore().get(2));
			assertEquals(Material.CRAFTING_TABLE, shopInv.getItem(7).getType());
			assertEquals("Stock", shopInv.getItem(7).getItemMeta().getDisplayName());
			assertEquals(2, shopInv.getItem(7).getItemMeta().getLore().size());
			assertEquals(ChatColor.RED + "Only for Shopowner", shopInv.getItem(7).getItemMeta().getLore().get(0));
			assertEquals(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "open/close stockpile",
					shopInv.getItem(7).getItemMeta().getLore().get(1));
			shop.openEditor(owner);
			Inventory editor = owner.getOpenInventory().getTopInventory();
			owner.closeInventory();
			assertEquals(9, editor.getSize());
			assertEquals(Material.PLAYER_HEAD, editor.getItem(0).getType());
			assertEquals(Material.PLAYER_HEAD, editor.getItem(1).getType());
			assertEquals(Material.PLAYER_HEAD, editor.getItem(2).getType());
			assertEquals(Material.PLAYER_HEAD, editor.getItem(3).getType());
			assertEquals(Material.PLAYER_HEAD, editor.getItem(4).getType());
			assertEquals(Material.PLAYER_HEAD, editor.getItem(5).getType());
			assertEquals(Material.PLAYER_HEAD, editor.getItem(6).getType());
			assertNull(editor.getItem(7));
			assertNull(editor.getItem(8));
			assertEquals("Slot 1", editor.getItem(0).getItemMeta().getDisplayName());
			assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
			assertEquals("Slot 3", editor.getItem(2).getItemMeta().getDisplayName());
			assertEquals("Slot 4", editor.getItem(3).getItemMeta().getDisplayName());
			assertEquals("Slot 5", editor.getItem(4).getItemMeta().getDisplayName());
			assertEquals("Slot 6", editor.getItem(5).getItemMeta().getDisplayName());
			assertEquals("Slot 7", editor.getItem(6).getItemMeta().getDisplayName());
			NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
			assertEquals(SLOTEMPTY,
					editor.getItem(0).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
			assertEquals(SLOTEMPTY,
					editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
			assertEquals(SLOTEMPTY,
					editor.getItem(2).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
			assertEquals(SLOTEMPTY,
					editor.getItem(3).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
			assertEquals(SLOTEMPTY,
					editor.getItem(4).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
			assertEquals(SLOTEMPTY,
					editor.getItem(5).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
			assertEquals(SLOTEMPTY,
					editor.getItem(6).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
			Inventory stockPile = shop.getStockpileInventory();
			assertEquals(9, stockPile.getSize());
			assertEquals(Material.CRAFTING_TABLE, stockPile.getItem(8).getType());
			assertEquals("Infos", stockPile.getItem(8).getItemMeta().getDisplayName());
			assertEquals(4, stockPile.getItem(8).getItemMeta().getLore().size());
			assertEquals(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "close stockpile",
					stockPile.getItem(8).getItemMeta().getLore().get(0));
			assertEquals(ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "add specified amount",
					stockPile.getItem(8).getItemMeta().getLore().get(1));
			assertEquals(ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "add all",
					stockPile.getItem(8).getItemMeta().getLore().get(2));
			assertEquals(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "get specified amount",
					stockPile.getItem(8).getItemMeta().getLore().get(3));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void changeSizeTestWithOccupiedSlot() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 18,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(7, 0, 1, new ItemStack(Material.STONE));
			shop.changeShopSize(9);
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cChanging the shop size has failed due to occupied slots!", e.getMessage());
		}
	}

	@Test
	public void openStockpileTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 18,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.openStockpile(owner);
			Inventory inv = owner.getOpenInventory().getTopInventory();
			assertNotNull(inv);
			assertEquals(inv, shop.getStockpileInventory());
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void moveShopTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 18,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			Location newLoction = new Location(world, 5.9, 1.0, 6.3);
			shop.moveShop(newLoction);
			assertEquals(newLoction, shop.getShopLocation());
			assertEquals(0, world.getNearbyEntities(location, 0, 0, 0).size());
			assertEquals(1, world.getNearbyEntities(newLoction, 0, 0, 0).size());
			assertEquals(newLoction, shop.getShopVillager().getLocation());
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals("5.9", String.valueOf(config.getDouble("ShopLocation.x")));
			assertEquals("1.0", String.valueOf(config.getDouble("ShopLocation.y")));
			assertEquals("6.3", String.valueOf(config.getDouble("ShopLocation.z")));
			assertEquals("World", config.getString("ShopLocation.World"));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void moveShopTestWithNoPlotPermission() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 18,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			TownworldController.createTownWorld(world.getName());
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.moveShop(new Location(world, 10, 10, 10));
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof EconomyPlayerException);
			assertEquals("§cYou dont have the permission to do that!", e.getMessage());
		}
	}

	@Test
	public void moveShopTestWithPermission() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			PlayershopController.createPlayerShop("myshop", location, 18, ecoPlayer);
			TownworldController.createTownWorld(world.getName());
			TownController.createTown(TownworldController.getTownWorldList().get(0), "newtown", location, ecoPlayer);
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			Location newLoc = new Location(world, 10, 10, 10);
			shop.moveShop(newLoc);
			assertEquals(newLoc, shop.getShopLocation());
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void moveShopTestWithNoPlotPermissionTown() {
		// TODO if town is testd
	}

	@Test
	public void changeShopNameTestWithInvalidName() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 18,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.changeShopName("invalid_name");
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cThis shopname is invalid! Use a name without _!", e.getMessage());
		}
	}

	@Test
	public void changeShopNameTestWithExistingName() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 18,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			PlayershopController.createPlayerShop("newshop", location, 18,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.changeShopName("newshop");
			fail();
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§c§4newshop_catch441§c already exists!", e.getMessage());
		}
	}

	@Test
	public void changeShopNameTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 18,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.changeShopName("newshop");
			assertEquals("newshop", shop.getName());
			assertEquals("newshop", ((ChestInventoryMock) shop.getShopInventory()).getName());
			shop.openEditor(owner);
			ChestInventoryMock editor = (ChestInventoryMock) owner.getOpenInventory().getTopInventory();
			owner.closeInventory();
			assertEquals("newshop-Editor", editor.getName());
			shop.openSlotEditor(owner, 0);
			ChestInventoryMock slotEditor = (ChestInventoryMock) owner.getOpenInventory().getTopInventory();
			assertEquals("newshop-SlotEditor", slotEditor.getName());
			owner.closeInventory();
			// check savefile
			File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			assertEquals("newshop", config.getString("ShopName"));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}

	@Test
	public void removeItemTest() {
		Location location = new Location(world, 1.5, 2.3, 6.9);
		try {
			PlayershopController.createPlayerShop("myshop", location, 18,
					EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
			Playershop shop = PlayershopController.getPlayerShops().get(0);
			shop.addShopItem(0, 0, 1, new ItemStack(Material.STONE));
			shop.removeShopItem(0);
			assertNull(shop.getShopInventory().getItem(0));
			Inventory stockpile = shop.getStockpileInventory();
			assertNull(stockpile.getItem(0));
		} catch (GeneralEconomyException | ShopSystemException | TownSystemException | EconomyPlayerException e) {
			fail();
		}
	}
}
