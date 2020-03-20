package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.api.Adminshop;
import com.ue.shopsystem.controller.AdminshopController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class AdminshopTest {

    private static ServerMock server;
    private static UltimateEconomy plugin;
    private static WorldMock world;

    /**
     * Init shop for tests.
     */
    @BeforeAll
    public static void initPlugin() {
	server = MockBukkit.mock();
	plugin = (UltimateEconomy) MockBukkit.load(UltimateEconomy.class);
	world = new WorldMock(Material.GRASS_BLOCK, 1);
	server.addWorld(world);
    }

    /**
     * Unload mock bukkit.
     */
    @AfterAll
    public static void deleteSavefiles() {
	UltimateEconomy.getInstance.getDataFolder().delete();
	MockBukkit.unload();
    }

    /**
     * Unload all adminshopss.
     */
    @AfterEach
    public void unloadAdminshops() {
	int size = AdminshopController.getAdminshopList().size();
	for (int i = 1; i <= size; i++) {
	    try {
		AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
	    } catch (ShopSystemException e) {
		assertTrue(false);
	    }
	}
    }

    /**
     * Test add shop item with buy and sell price. Normal item.
     */
    // @Test TODO
    public void addShopItemTest() {
	ItemStack item = new ItemStack(Material.STONE, 16);

	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(0, 10, 20, item);
	    item.setAmount(1);
	    String itemString = item.toString();
	    Bukkit.getLogger().info(itemString);
	    Bukkit.getLogger().info(shop.getItemList().get(0));
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));

	    // check inventory

	    // check savefile
	    File saveFile = shop.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals(item, config.getItemStack("ShopItems." + itemString + ".Name"));

	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test move shop.
     */
    @Test
    public void moveShopTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    Location newLoction = new Location(world, 5.9, 1.0, 6.3);
	    shop.moveShop(newLoction);
	    assertEquals(newLoction, shop.getShopLocation());
	    assertEquals(0, world.getNearbyEntities(location, 0, 0, 0).size());
	    assertEquals(1, world.getNearbyEntities(newLoction, 0, 0, 0).size());
	    assertEquals(newLoction, shop.getShopVillager().getLocation());
	    // check savefile
	    File saveFile = shop.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("5.9", String.valueOf(config.getDouble("ShopLocation.x")));
	    assertEquals("1.0", String.valueOf(config.getDouble("ShopLocation.y")));
	    assertEquals("6.3", String.valueOf(config.getDouble("ShopLocation.z")));
	    assertEquals("World", config.getString("ShopLocation.World"));
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test change shop villager profession.
     */
    @Test
    public void changeProfessionTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.changeProfession(Profession.CARTOGRAPHER);
	    assertEquals(Profession.CARTOGRAPHER, shop.getShopVillager().getProfession());
	    // check savefile
	    File saveFile = shop.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("CARTOGRAPHER", config.getString("Profession"));
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }
    
    /**
     * Test change shop name.
     */
    @Test
    public void changeShopNameTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.changeShopName("newname");
	    assertEquals("newname", shop.getName());
	    assertEquals("newname", ((ChestInventoryMock) shop.getShopInventory()).getName());
	    assertEquals("newname-Editor", ((ChestInventoryMock) shop.getEditorInventory()).getName());
	    assertEquals("newname-SlotEditor", ((ChestInventoryMock) shop.getSlotEditorInventory()).getName());
	    // check savefile
	    File saveFile = shop.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("newname", config.getString("ShopName"));
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}	    
    }
    
    /**
     * Test change shop name with a invalid name.
     */
    @Test
    public void changeShopNameTestWithInvalidName() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.changeShopName("newname_");
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis shopname is invalid! Use a name without _!", e.getMessage());
	}	
    }
    
    /**
     * Test change shop name with existing name.
     */
    @Test
    public void changeShopNameTestWithExistingName() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    AdminshopController.createAdminShop("newname", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.changeShopName("newname");
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§c§4newname§c already exists!", e.getMessage());
	}
    }
}
