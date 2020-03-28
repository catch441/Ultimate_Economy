package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.player.api.EconomyPlayer;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.api.Rentshop;
import com.ue.shopsystem.controller.RentshopController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class RentshopControllerTest {

    private static final String ONE = "http://textures.minecraft.net/texture/"
	    + "d2a6f0e84daefc8b21aa99415b16ed5fdaa6d8dc0c3cd591f49ca832b575";
    private static final String SEVEN = "http://textures.minecraft.net/texture/"
	    + "9e198fd831cb61f3927f21cf8a7463af5ea3c7e43bd3e8ec7d2948631cce879";
    private static final String PLUS = "http://textures.minecraft.net/texture/"
	    + "9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777";
    private static ServerMock server;
    private static WorldMock world;

    @BeforeAll
    public static void initPlugin() {
	server = MockBukkit.mock();
	MockBukkit.load(UltimateEconomy.class);
	world = new WorldMock(Material.GRASS_BLOCK, 1);
	server.addWorld(world);
	server.addPlayer("catch441");
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

    @Test
    public void despawnAllVillagersTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 10);
	    Collection<Entity> entities1 = world.getNearbyEntities(shop.getShopLocation(), 0, 0, 0);
	    RentshopController.despawnAllVillagers();
	    Collection<Entity> entities2 = world.getNearbyEntities(shop.getShopLocation(), 0, 0, 0);
	    assertEquals(1, entities1.size());
	    assertEquals(0, entities2.size());
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

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

    @Test
    public void createRentshopTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 5);
	    assertEquals(world, shop.getWorld());
	    assertEquals("R0", shop.getShopId());
	    assertEquals("RentShop#R0", shop.getName());
	    assertNull(shop.getOwner());
	    assertEquals(EconomyVillager.PLAYERSHOP_RENTABLE,
		    shop.getShopVillager().getMetadata("ue-type").get(0).value());
	    assertEquals("5.0", String.valueOf(shop.getRentalFee()));
	    assertTrue(shop.isRentable());
	    assertEquals(0L, shop.getRentUntil());
	    // check rentshop gui
	    ChestInventoryMock gui = (ChestInventoryMock) shop.getRentShopGuiInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("RentShop#R0", gui.getName());
	    assertEquals(9, gui.getSize());
	    assertEquals(Material.GREEN_WOOL, gui.getItem(0).getType());
	    assertEquals(ChatColor.YELLOW + "Rent", gui.getItem(0).getItemMeta().getDisplayName());
	    assertEquals(ChatColor.GOLD + "RentalFee: " + ChatColor.GREEN + 5.0,
		    gui.getItem(0).getItemMeta().getLore().get(0));
	    assertEquals(Material.CLOCK, gui.getItem(1).getType());
	    assertEquals(1, gui.getItem(1).getItemMeta().getLore().size());
	    assertEquals(ChatColor.YELLOW + "Duration", gui.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + 1 + ChatColor.GOLD + " Day",
		    gui.getItem(1).getItemMeta().getLore().get(0));
	    assertEquals(Material.PLAYER_HEAD, gui.getItem(3).getType());
	    assertNull(gui.getItem(3).getItemMeta().getLore());
	    assertEquals("plus", gui.getItem(3).getItemMeta().getDisplayName());
	    assertEquals(PLUS,
		    gui.getItem(3).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    assertEquals(Material.PLAYER_HEAD, gui.getItem(4).getType());
	    assertEquals(1, gui.getItem(4).getItemMeta().getLore().size());
	    assertEquals("one", gui.getItem(4).getItemMeta().getDisplayName());
	    assertEquals(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + 1 + ChatColor.GOLD + " Day",
		    gui.getItem(4).getItemMeta().getLore().get(0));
	    assertEquals(ONE,
		    gui.getItem(4).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    assertEquals(Material.PLAYER_HEAD, gui.getItem(4).getType());
	    assertEquals(1, gui.getItem(5).getItemMeta().getLore().size());
	    assertEquals("seven", gui.getItem(5).getItemMeta().getDisplayName());
	    assertEquals(ChatColor.GOLD + "Duration: " + ChatColor.GREEN + 1 + ChatColor.GOLD + " Day",
		    gui.getItem(5).getItemMeta().getLore().get(0));
	    assertEquals(SEVEN,
		    gui.getItem(5).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("5.0", config.getString("RentalFee"));
	    assertTrue(config.getBoolean("Rentable"));
	    assertNull(config.getString("RentUntil"));
	    assertEquals(1, UltimateEconomy.getInstance.getConfig().getStringList("RentShopIds").size());
	    assertEquals("R0", UltimateEconomy.getInstance.getConfig().getStringList("RentShopIds").get(0));
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void loadAllRentshopsTestWithNotRented() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 5);
	    assertEquals(1,RentshopController.getRentShops().size());
	    RentshopController.despawnAllVillagers();
	    RentshopController.getRentShops().clear();
	    assertEquals(0,RentshopController.getRentShops().size());
	    assertEquals(world, shop.getWorld());
	    assertEquals("R0", shop.getShopId());
	    assertEquals("RentShop#R0", shop.getName());
	    assertNull(shop.getOwner());
	    assertEquals(EconomyVillager.PLAYERSHOP_RENTABLE,
		    shop.getShopVillager().getMetadata("ue-type").get(0).value());
	    assertEquals("5.0", String.valueOf(shop.getRentalFee()));
	    assertTrue(shop.isRentable());
	    assertEquals(0L, shop.getRentUntil());
	} catch (GeneralEconomyException e) {
	    assertTrue(false);
	}
    }
    
    @Test
    public void loadAllRentshopsTestWithRented() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    Rentshop shop = RentshopController.createRentShop(location, 9, 5);
	    EconomyPlayer ecoPlayer = EconomyPlayerController.getAllEconomyPlayers().get(0);
	    ecoPlayer.increasePlayerAmount(100, false);
	    shop.rentShop(ecoPlayer, 10);
	    shop.addShopItem(0, 1, 1, new ItemStack(Material.STONE));    
	    assertEquals(1,RentshopController.getRentShops().size());
	    RentshopController.despawnAllVillagers();
	    RentshopController.getRentShops().clear();
	    assertEquals(0,RentshopController.getRentShops().size());
	    assertEquals(world, shop.getWorld());
	    assertEquals("R0", shop.getShopId());
	    assertEquals("Shop#R0", shop.getName());
	    assertEquals(ecoPlayer, shop.getOwner());
	    assertEquals(EconomyVillager.PLAYERSHOP_RENTABLE,
		    shop.getShopVillager().getMetadata("ue-type").get(0).value());
	    assertEquals("5.0", String.valueOf(shop.getRentalFee()));
	    assertFalse(shop.isRentable());
	    assertNotNull(shop.getRentUntil());
	    assertEquals(1, shop.getItemList().size());
	    assertEquals("ItemStack{STONE x 1}", shop.getItemList().get(0));
	    assertEquals(Material.STONE, shop.getShopInventory().getItem(0).getType());
	} catch (GeneralEconomyException | ShopSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
}