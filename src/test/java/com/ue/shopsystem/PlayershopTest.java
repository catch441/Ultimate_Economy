package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.api.Playershop;
import com.ue.shopsystem.controller.PlayershopController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

public class PlayershopTest {

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
	server.addPlayer("Wulfgar");
    }

    /**
     * Unload mock bukkit.
     */
    @AfterAll
    public static void deleteSavefiles() {
	UltimateEconomy.getInstance.getDataFolder().delete();
	server.setPlayers(0);
	MockBukkit.unload();
	EconomyPlayerController.getAllEconomyPlayers().clear();
    }

    /**
     * Unload all.
     */
    @AfterEach
    public void unload() {
	int size = PlayershopController.getPlayerShops().size();
	for (int i = 0; i < size; i++) {
	    try {
		PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
	    } catch (ShopSystemException e) {
		assertTrue(false);
	    }
	}
    }
    
    /**
     * Test is owner.
     * 
     */
    @Test
    public void isOwnerTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    assertTrue(shop.isOwner(EconomyPlayerController.getAllEconomyPlayers().get(0)));
	    assertFalse(shop.isOwner(EconomyPlayerController.getAllEconomyPlayers().get(1)));
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    /**
     * Test increase stock.
     * 
     */
    @Test
    public void increaseStockTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    ItemStack stack = new ItemStack(Material.STONE);
	    shop.addShopItem(0, 0, 1, stack);
	    shop.increaseStock(0, 30);
	    assertEquals("§a30§6 Items", shop.getStockpileInventory().getItem(0).getItemMeta().getLore().get(0));
	    // check save file
	    File saveFile = shop.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals(30, config.getInt("ShopItems." + stack.toString() + ".stock"));
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    /**
     * Test increase stock with invalid stock.
     * 
     */
    @Test
    public void increaseStockTestWithInvalidStock() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    ItemStack stack = new ItemStack(Material.STONE);
	    shop.addShopItem(0, 0, 1, stack);
	    shop.increaseStock(0, -30);
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §4-30.0§c is invalid!", e.getMessage());
	}
    }
    
    /**
     * Test decrease stock.
     * 
     */
    @Test
    public void decreaseStockTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    ItemStack stack = new ItemStack(Material.STONE);
	    shop.addShopItem(0, 0, 1, stack);
	    shop.increaseStock(0, 30);
	    shop.decreaseStock(0, 10);
	    assertEquals("§a20§6 Items", shop.getStockpileInventory().getItem(0).getItemMeta().getLore().get(0));
	    // check save file
	    File saveFile = shop.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals(20, config.getInt("ShopItems." + stack.toString() + ".stock"));
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    /**
     * Test decrease stock with invalid stock.
     * 
     */
    @Test
    public void decreaseStockTestWithInvalidStock() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    ItemStack stack = new ItemStack(Material.STONE);
	    shop.addShopItem(0, 0, 1, stack);
	    shop.decreaseStock(0, 10);
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §410§c is invalid!", e.getMessage());
	}
    }
    
    /**
     * Test decrease stock with invalid stock.
     * 
     */
    @Test
    public void decreaseStockTestWithNegativeStockStock() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    ItemStack stack = new ItemStack(Material.STONE);
	    shop.addShopItem(0, 0, 1, stack);
	    shop.increaseStock(0, 10);
	    shop.decreaseStock(0, -10);
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §4-10.0§c is invalid!", e.getMessage());
	}
    }
    
    /**
     * Test is available.
     * 
     */
    @Test
    public void isAvailableTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.addShopItem(0, 0, 1, new ItemStack(Material.STONE));
	    shop.addShopItem(1, 0, 1, new ItemStack(Material.ACACIA_WOOD));
	    shop.increaseStock(0, 30);
	    assertTrue(shop.isAvailable(0));
	    assertFalse(shop.isAvailable(1));
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    /**
     * Test is available with invalid slot.
     * 
     */
    @Test
    public void isAvailableTestWithInvalidSlot() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.addShopItem(0, 0, 1, new ItemStack(Material.STONE));
	    shop.increaseStock(0, 30);
	    shop.isAvailable(-10);
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §4-9§c is invalid!", e.getMessage());
	}
    }
    
    // addShopItem
    // removeShopItem
    // changeOwner
}
