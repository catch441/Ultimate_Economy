package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.api.Rentshop;
import com.ue.shopsystem.api.RentshopController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class RentshopTest {

    private static ServerMock server;
    private static WorldMock world;
    private static PlayerMock player;
    
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
	int size2 = EconomyPlayerController.getAllEconomyPlayers().size();
	for (int i = 0; i < size2; i++) {
	    EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
	}
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
    
    @Test
    public void moveShopTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    Location newLocation = new Location(world,10, 3, 20);
	    shop.moveShop(newLocation);
	    assertEquals(newLocation, shop.getShopLocation());
	} catch (GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void isRentableTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    assertTrue(shop.isRentable());
	    EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(100, false);
	    shop.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 10);
	    assertFalse(shop.isRentable());
	} catch (GeneralEconomyException | PlayerException | ShopSystemException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void changeShopNameTestWithNotRented() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    shop.changeShopName("newname");
	    assertEquals("newname",shop.getName());
	    assertEquals("newname#R0",shop.getShopVillager().getCustomName());
	    // check savefile
	    File saveFile = shop.getSavefileManager().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("newname", config.getString("ShopName"));
	} catch (GeneralEconomyException | ShopSystemException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void changeShopNameTestWithRented() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    EconomyPlayerController.getAllEconomyPlayers().get(0).increasePlayerAmount(100, false);
	    shop.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 10);
	    shop.changeShopName("newname");
	    assertEquals("newname",shop.getName());
	    assertEquals("newname_catch441",shop.getShopVillager().getCustomName());
	    // check savefile
	    File saveFile = shop.getSavefileManager().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("newname", config.getString("ShopName"));
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void rentShopTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	    ecoPlayer.increasePlayerAmount(100, false);
	    shop.rentShop(ecoPlayer, 10);
	    assertEquals("0.0",String.valueOf(ecoPlayer.getBankAccount().getAmount()));
	    assertEquals(ecoPlayer,shop.getOwner());
	    assertEquals("Shop#R0",shop.getName());
	    assertFalse(shop.isRentable());
	    // TODO rent until
	    // check savefile
	    File saveFile = shop.getSavefileManager().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertFalse(config.getBoolean("Rentable"));
	    assertNotNull(config.getString("RentUntil"));
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void rentShopTestWithNotEnoughMoney() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    shop.rentShop(EconomyPlayerController.getAllEconomyPlayers().get(0), 10);
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(e instanceof PlayerException);
	    assertEquals("§cYou have not enough money!", e.getMessage());
	}
    }
    
    @Test
    public void rentShopTestWithAlreadyRented() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	    ecoPlayer.increasePlayerAmount(100, false);
	    shop.rentShop(ecoPlayer, 10);
	    shop.rentShop(ecoPlayer, 10);
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis shop is rented!", e.getMessage());
	}
    }
    
    @Test
    public void resetShopTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	    ecoPlayer.increasePlayerAmount(100, false);
	    shop.rentShop(ecoPlayer, 10);
	    shop.resetShop();
	    assertNull(shop.getOwner());
	    assertEquals(0L,shop.getRentUntil());
	    assertTrue(shop.isRentable());
	    assertEquals(Profession.NITWIT,shop.getShopVillager().getProfession());
	    assertEquals("RentShop",shop.getName());
	    assertEquals("RentShop#R0",shop.getShopVillager().getCustomName());
	    assertEquals(0,shop.getItemList().size());
	    // check savefile
	    File saveFile = shop.getSavefileManager().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertTrue(config.getBoolean("Rentable"));
	    assertEquals(0L,config.getLong("RentUntil"));
	    assertNull(config.getString("Owner"));
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void changeRentalFee() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    shop.changeRentalFee(25);
	    assertEquals("25.0", String.valueOf(shop.getRentalFee()));
	    // check savefile
	    File saveFile = shop.getSavefileManager().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("25.0",config.getString("RentalFee"));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void changeRentalFeeWithNegativValue() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    shop.changeRentalFee(-25);
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §4-25.0§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void changeShopSizeTestWithNotRented() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    shop.changeShopSize(18);
	    assertEquals(18, shop.getSize());
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void changeShopSizeTestWithRented() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	    ecoPlayer.increasePlayerAmount(100, false);
	    shop.rentShop(ecoPlayer, 10);
	    shop.changeShopSize(18);
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis shop is rented!", e.getMessage());
	}
    }

    @Test
    public void getRentalFeeTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    assertEquals("10.0", String.valueOf(shop.getRentalFee()));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void openRentGUITest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    shop.openRentGUI(player);
	    assertEquals((ChestInventoryMock) shop.getRentShopGuiInventory(), player.getOpenInventory().getTopInventory());
	} catch (GeneralEconomyException | ShopSystemException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void openRentGUITestWithRented() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	    ecoPlayer.increasePlayerAmount(100, false);
	    shop.rentShop(ecoPlayer, 10);
	    shop.openRentGUI(player);
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis shop is rented!", e.getMessage());
	}
    }

    @Test
    public void changeOwnerTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	    shop.changeOwner(ecoPlayer);
	    assertEquals(ecoPlayer, shop.getOwner());
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void changeOwnerTestWithRented() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	    ecoPlayer.increasePlayerAmount(100, false);
	    shop.rentShop(ecoPlayer, 10);
	    shop.changeOwner(ecoPlayer);
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis shop is rented!", e.getMessage());
	}
    }
}
