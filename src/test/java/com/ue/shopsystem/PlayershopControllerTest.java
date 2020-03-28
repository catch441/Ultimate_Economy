package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.config.api.ConfigController;
import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.api.Playershop;
import com.ue.shopsystem.controller.PlayershopController;
import com.ue.townsystem.townworld.api.TownworldController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class PlayershopControllerTest {

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
	int size = PlayershopController.getPlayerShops().size();
	for (int i = 0; i < size; i++) {
	    try {
		PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
	    } catch (ShopSystemException e) {
		assertTrue(false);
	    }
	}
	if (TownworldController.getTownWorldList().size() != 0) {
	    try {
		TownworldController.deleteTownWorld(world.getName());
	    } catch (TownSystemException | PlayerException | GeneralEconomyException e) {
		assertTrue(false);
	    }
	}
    }

    /**
     * Test create new playershop with invalid size.
     */
    @Test
    public void createNewPlayershopTestWithInvalidSize() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 5,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §45§c is invalid!", e.getMessage());
	}
    }

    /**
     * Test create new playershop with existing name.
     */
    @Test
    public void createNewAdminshopTestWithExistingName() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    ConfigController.setMaxPlayershops(2);
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§c§4myshop_catch441§c already exists!", e.getMessage());
	}
    }

    /**
     * Test create new playershop with invalid name.
     */
    @Test
    public void createNewAdminshopTestWithInvalidName() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop_", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis shopname is invalid! Use a name without _!", e.getMessage());
	}
    }

    /**
     * Test create new playershop with invalid name.
     */
    @Test
    public void createNewAdminshopTestWithMaxShopsReached() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    ConfigController.setMaxPlayershops(1);
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    PlayershopController.createPlayerShop("myshop1", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof PlayerException);
	    assertEquals("§cYou have already reached the maximum!", e.getMessage());
	}
    }

    /**
     * Test create new playershop with no plot permission.
     */
    @Test
    public void createNewAdminshopTestWithNoPlotPermission() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    TownworldController.createTownWorld(world.getName());
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof PlayerException);
	    assertEquals("§cYou dont have the permission to do that!", e.getMessage());
	}
    }

    /**
     * Test create new playershop.
     */
    @Test
    public void createNewPlayershopTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop response = PlayershopController.getPlayerShops().get(0);
	    assertEquals(world, response.getWorld());
	    assertEquals("P0", response.getShopId());
	    assertEquals("myshop", response.getName());
	    assertEquals(EconomyPlayerController.getAllEconomyPlayers().get(0), response.getOwner());
	    assertEquals(EconomyVillager.PLAYERSHOP, response.getShopVillager().getMetadata("ue-type").get(0).value());
	    // check shop inventory
	    ChestInventoryMock shopInv = (ChestInventoryMock) response.getShopInventory();
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
	    ChestInventoryMock editor = (ChestInventoryMock) response.getEditorInventory();
	    assertEquals(9, editor.getSize());
	    assertEquals("myshop-Editor", editor.getName());
	    assertEquals(Material.AIR, editor.getItem(7).getType());
	    assertEquals(Material.AIR, editor.getItem(8).getType());
	    // check stock inventory
	    ChestInventoryMock stock = (ChestInventoryMock) response.getStockpileInventory();
	    assertEquals(9, stock.getSize());
	    assertEquals("Inventory", stock.getName());
	    assertEquals(Material.AIR, stock.getItem(0).getType());
	    assertEquals(Material.AIR, stock.getItem(1).getType());
	    assertEquals(Material.AIR, stock.getItem(2).getType());
	    assertEquals(Material.AIR, stock.getItem(3).getType());
	    assertEquals(Material.AIR, stock.getItem(4).getType());
	    assertEquals(Material.AIR, stock.getItem(5).getType());
	    assertEquals(Material.AIR, stock.getItem(6).getType());
	    assertEquals(Material.AIR, stock.getItem(7).getType());
	    assertEquals("Infos", stock.getItem(8).getItemMeta().getDisplayName());
	    assertEquals(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "close stockpile",
		    stock.getItem(8).getItemMeta().getLore().get(0));
	    assertEquals(ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "add specified amount",
		    stock.getItem(8).getItemMeta().getLore().get(1));
	    assertEquals(ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "add all",
		    stock.getItem(8).getItemMeta().getLore().get(2));
	    assertEquals(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "get specified amount",
		    stock.getItem(8).getItemMeta().getLore().get(3));
	    // check savefile
	    File saveFile = response.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("catch441", config.getString("Owner"));
	    assertEquals(1, UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopIds").size());
	    assertEquals("P0", UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopIds").get(0));
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test generate free playershop id.
     */
    @Test
    public void generateFreePlayerShopIdTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    String id1 = PlayershopController.generateFreePlayerShopId();
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    String id2 = PlayershopController.generateFreePlayerShopId();
	    assertEquals("P0", id1);
	    assertEquals("P1", id2);
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get playershop unique name list.
     */
    @Test
    public void getPlayerShopUniqueNameListTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    List<String> list = PlayershopController.getPlayerShopUniqueNameList();
	    assertEquals(1, list.size());
	    assertEquals("myshop_catch441", list.get(0));
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get playershop by unique name.
     */
    @Test
    public void getPlayerShopByUniqueNameTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShopByUniqueName("myshop_catch441");
	    assertNotNull(shop);
	    assertEquals("P0", shop.getShopId());
	    assertEquals("myshop", shop.getName());
	    assertEquals("catch441", shop.getOwner().getName());
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get playershop by unique name with no shop.
     */
    @Test
    public void getPlayerShopByUniqueNameTestWithNoShop() {
	try {
	    PlayershopController.getPlayerShopByUniqueName("myshop_catch441");
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§c§4myshop_catch441§c does not exist!", e.getMessage());
	}
    }

    /**
     * Test get playershop by shopid.
     */
    @Test
    public void getPlayerShopByIdTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShopById("P0");
	    assertNotNull(shop);
	    assertEquals("P0", shop.getShopId());
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get playershop by shopid with no shop.
     */
    @Test
    public void getPlayerShopByIdTestWithNoShop() {
	try {
	    PlayershopController.getPlayerShopById("P0");
	    assertTrue(false);
	} catch (GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§c§4P0§c does not exist!", e.getMessage());
	}
    }

    /**
     * Test get id list.
     */
    @Test
    public void getPlayershopIdListTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    List<String> list = PlayershopController.getPlayershopIdList();
	    assertEquals(1, list.size());
	    assertEquals("P0", list.get(0));
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test despawn villagers.
     */
    @Test
    public void despawnAllVillagersTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Collection<Entity> entities1 = world
		    .getNearbyEntities(PlayershopController.getPlayerShops().get(0).getShopLocation(), 0, 0, 0);
	    PlayershopController.despawnAllVillagers();
	    Collection<Entity> entities2 = world
		    .getNearbyEntities(PlayershopController.getPlayerShops().get(0).getShopLocation(), 0, 0, 0);
	    assertEquals(1, entities1.size());
	    assertEquals(0, entities2.size());
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test delete a playershop.
     */
    @Test
    public void deletePlayershopTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    PlayershopController.deletePlayerShop(PlayershopController.getPlayerShops().get(0));
	    File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "P0.yml");
	    assertFalse(saveFile.exists());
	    assertEquals(0, UltimateEconomy.getInstance.getConfig().getStringList("PlayerShopIds").size());
	    assertEquals(0, PlayershopController.getPlayerShops().size());
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test load all shops.
     * 
     */
    @Test
    public void loadAllPlayerShopsTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    PlayershopController.getPlayerShops().get(0).despawnVillager();
	    assertEquals(1, PlayershopController.getPlayerShops().size());
	    PlayershopController.getPlayerShops().clear();
	    assertEquals(0, PlayershopController.getPlayerShops().size());
	    PlayershopController.loadAllPlayerShops();
	    assertEquals(1, PlayershopController.getPlayerShops().size());
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    assertEquals(world, shop.getWorld());
	    assertEquals("P0", shop.getShopId());
	    assertEquals("myshop", shop.getName());
	    assertEquals(EconomyVillager.PLAYERSHOP, shop.getShopVillager().getMetadata("ue-type").get(0).value());
	    assertEquals(0, shop.getItemList().size());
	    assertEquals(location, shop.getShopLocation());
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test load all shops with items.
     * 
     */
    @Test
    public void loadAllPlayerShopsTestWithItem() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.addShopItem(0, 10, 10, new ItemStack(Material.STONE));
	    shop.increaseStock(0, 25);
	    shop.despawnVillager();
	    PlayershopController.getPlayerShops().clear();
	    assertEquals(0, PlayershopController.getPlayerShops().size());
	    PlayershopController.loadAllPlayerShops();
	    assertEquals(1, PlayershopController.getPlayerShops().size());
	    Playershop response = PlayershopController.getPlayerShops().get(0);
	    assertEquals(world, response.getWorld());
	    assertEquals("P0", response.getShopId());
	    assertEquals("myshop", response.getName());
	    assertEquals(EconomyVillager.PLAYERSHOP, response.getShopVillager().getMetadata("ue-type").get(0).value());
	    assertEquals(1, response.getItemList().size());
	    assertEquals(location, response.getShopLocation());
	    assertEquals(ChatColor.GREEN + "25" + ChatColor.GOLD + " Items",
		    response.getStockpileInventory().getItem(0).getItemMeta().getLore().get(0));
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
}
