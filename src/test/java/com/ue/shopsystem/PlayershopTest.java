package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

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
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;

public class PlayershopTest {

    private static final String SLOTEMPTY = "http://textures.minecraft.net/texture/"
	    + "b55d5019c8d55bcb9dc3494ccc3419757f89c3384cf3c9abec3f18831f35b0";
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

    @Test
    public void changeOwnerTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.changeOwner(EconomyPlayerController.getAllEconomyPlayers().get(1));
	    assertEquals(EconomyPlayerController.getAllEconomyPlayers().get(1), shop.getOwner());
	    // check save file
	    File saveFile = shop.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("Wulfgar", config.getString("Owner"));
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void changeOwnerTestNoPossible() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(1));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.changeOwner(EconomyPlayerController.getAllEconomyPlayers().get(1));
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThe player has already a shop with the same name!", e.getMessage());
	}
    }

    @Test
    public void addItemTestWithReservedSlot() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 9,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    ;
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.addShopItem(7, 1, 1, new ItemStack(Material.STONE));
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof PlayerException);
	    assertEquals("§cThis slot is occupied!", e.getMessage());
	}
    }

    @Test
    public void changeSizeTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 18,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    ;
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
	    Inventory editor = shop.getEditorInventory();
	    assertEquals(9, editor.getSize());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(0).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(1).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(2).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(3).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(4).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(5).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(6).getType());
	    assertEquals(Material.AIR, editor.getItem(7).getType());
	    assertEquals(Material.AIR, editor.getItem(8).getType());
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
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void changeSizeTestWithOccupiedSlot() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 18,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    ;
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.addShopItem(7, 0, 1, new ItemStack(Material.STONE));
	    shop.changeShopSize(9);
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cChanging the shop size has failed due to occupied slots!", e.getMessage());
	}
    }

    @Test
    public void openStockpileTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 18,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    ;
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.openStockpile(player);
	    Inventory inv = player.getOpenInventory().getTopInventory();
	    assertNotNull(inv);
	    assertEquals(inv, shop.getStockpileInventory());
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void moveShopTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 18,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
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
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void moveShopTestWithNoPlotPermission() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 18,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    TownworldController.createTownWorld(world.getName());
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.moveShop(new Location(world, 10, 10, 10));
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof PlayerException);
	    assertEquals("§cYou dont have the permission to do that!", e.getMessage());
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
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.changeShopName("invalid_name");
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis shopname is invalid! Use a name without _!", e.getMessage());
	}
    }

    @Test
    public void changeShopNameTestWithExistingName() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 18,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    PlayershopController.createPlayerShop("newshop", location, 18,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.changeShopName("newshop");
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§c§4newshopcatch441§c already exists!", e.getMessage());
	}
    }

    @Test
    public void changeShopNameTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 18,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.changeShopName("newshop");
	    assertEquals("newshop", shop.getName());
	    assertEquals("newshop", ((ChestInventoryMock) shop.getShopInventory()).getName());
	    assertEquals("newshop-Editor", ((ChestInventoryMock) shop.getEditorInventory()).getName());
	    assertEquals("newshop-SlotEditor", ((ChestInventoryMock) shop.getSlotEditorInventory()).getName());
	    // check savefile
	    File saveFile = shop.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("newshop", config.getString("ShopName"));
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void removeItemTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    PlayershopController.createPlayerShop("myshop", location, 18,
		    EconomyPlayerController.getAllEconomyPlayers().get(0));
	    Playershop shop = PlayershopController.getPlayerShops().get(0);
	    shop.addShopItem(0, 0, 1, new ItemStack(Material.STONE));
	    shop.removeShopItem(0);
	    assertEquals(Material.AIR, shop.getShopInventory().getItem(0).getType());
	    Inventory stockpile = shop.getStockpileInventory();
	    assertEquals(Material.AIR, stockpile.getItem(0).getType());
	} catch (GeneralEconomyException | ShopSystemException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }
}
