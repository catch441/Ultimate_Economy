package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.api.Rentshop;
import com.ue.shopsystem.controller.RentshopController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

public class RentshopControllerTest {

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
	player = server.addPlayer("catch441");
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
	int size = RentshopController.getRentShops().size();
	for (int i = 0; i < size; i++) {
	    try {
		RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
	    } catch (ShopSystemException e) {
		assertTrue(false);
	    }
	}
    }

    /**
     * Test generate free rentshop id.
     * 
     */
    @Test
    public void generateFreeRentShopIdTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    String id1 = RentshopController.generateFreeRentShopId();
	    RentshopController.createRentShop(location, 9, 10);
	    String id2 = RentshopController.generateFreeRentShopId();
	    assertEquals("R0", id1);
	    assertEquals("R1", id2);
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get rentshop id list.
     * 
     */
    @Test
    public void getRentShopIdListTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    RentshopController.createRentShop(location, 9, 10);
	    List<String> list = RentshopController.getRentShopIdList();
	    assertEquals(1, list.size());
	    assertEquals("R0", list.get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get rentshop by shop id.
     * 
     */
    @Test
    public void getRentShopByIdTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    Rentshop response = RentshopController.getRentShopById("R0");
	    assertEquals(shop, response);
	    assertEquals("R0", response.getShopId());
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get rentshop by shop id with no shops.
     * 
     */
    @Test
    public void getRentShopByIdTestWithNoShop() {
	try {
	    RentshopController.getRentShopById("R0");
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§c§4R0§c does not exist!", e.getMessage());
	}
    }

    /**
     * Test get unique rentshop name list.
     * 
     */
    @Test
    public void getRentShopUniqueNameListTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    RentshopController.createRentShop(location, 9, 10);
	    RentshopController.createRentShop(location, 9, 0);
	    RentshopController.getRentShops().get(1).rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0),
		    10);
	    List<String> list = RentshopController.getRentShopUniqueNameList();
	    assertEquals(2, list.size());
	    assertEquals("RentShop#R0", list.get(0));
	    assertEquals("Shop#R1_catch441", list.get(1));
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get rentshop by unique name.
     * 
     */
    @Test
    public void getRentShopByUniqueNameTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    Rentshop shop2 = RentshopController.createRentShop(location, 9, 0);
	    RentshopController.getRentShops().get(1).rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0),
		    10);
	    Rentshop response = RentshopController.getRentShopByUniqueName("RentShop#R0");
	    Rentshop response2 = RentshopController.getRentShopByUniqueName("Shop#R1_catch441");
	    assertEquals(shop, response);
	    assertEquals("R0", response.getShopId());
	    assertEquals("RentShop#R0", response.getName());
	    assertEquals(shop2, response2);
	    assertEquals("R1", response2.getShopId());
	    assertEquals("Shop#R1", response2.getName());
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    /**
     * Test get rentshop by unique name without shop.
     * 
     */
    @Test
    public void getRentShopByUniqueNameTestWithoutShop() {
	try {
	    RentshopController.getRentShopByUniqueName("RentShop#R0");
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§c§4RentShop#R0§c does not exist!", e.getMessage());
	}
    }
    
    /**
     * Test get all rentshops.
     * 
     */
    @Test
    public void getRentShopsTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    Rentshop shop2 = RentshopController.createRentShop(location, 9, 0);
	    RentshopController.getRentShops().get(1).rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0),
		    10);
	    List<Rentshop> shops = RentshopController.getRentShops();
	    assertEquals(shop, shops.get(0));
	    assertEquals(shop2, shops.get(1));
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    /**
     * Test despawn all rentshop villagers.
     * 
     */
    @Test
    public void despawnAllVillagersTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    Collection<Entity> entities1 = world
		    .getNearbyEntities(shop.getShopLocation(), 0, 0, 0);
	    RentshopController.despawnAllVillagers();
	    Collection<Entity> entities2 = world
		    .getNearbyEntities(shop.getShopLocation(), 0, 0, 0);
	    assertEquals(1, entities1.size());
	    assertEquals(0, entities2.size());
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }
    
    /**
     * Test delete a rentshop.
     * 
     */
    @Test
    public void deleteRentshopTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    RentshopController.createRentShop(location, 9, 10);
	    RentshopController.deleteRentShop(RentshopController.getRentShops().get(0));
	    File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "R0.yml");
	    assertFalse(saveFile.exists());
	    assertEquals(0, UltimateEconomy.getInstance.getConfig().getStringList("RentShopIds").size());
	    assertEquals(0, RentshopController.getRentShops().size());
	    assertEquals(0, RentshopController.getRentShopIdList().size());
	    assertEquals(0, RentshopController.getRentShopUniqueNameList().size());
	} catch (GeneralEconomyException | ShopSystemException e) {
	    assertTrue(false);
	}
    }
    
    /**
     * Test create rentshop with a invalid size.
     * 
     */
    @Test
    public void createRentshopTestWithInvalidSize() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    RentshopController.createRentShop(location, -13, 10);
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §4-13§c is invalid!", e.getMessage());
	}
    }
    
    /**
     * Test create rentshop with a invalid rental fee.
     * 
     */
    @Test
    public void createRentshopTestWithInvalidRentalFee() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    RentshopController.createRentShop(location, 9, -5);
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §4-5.0§c is invalid!", e.getMessage());
	}
    }
    
    /**
     * Test create rentshop.
     * 
     */
    @Test
    public void createRentshopTest() {
	
    }
}
