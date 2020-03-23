package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.eventhandling.EconomyVillager;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.shopsystem.api.Adminshop;
import com.ue.shopsystem.controller.AdminshopController;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;
import be.seeseemelk.mockbukkit.inventory.meta.CraftMetaItemMock;

public class AdminshopControllerTest {

    private static final String SLOTEMPTY = "http://textures.minecraft.net/texture/"
	    + "b55d5019c8d55bcb9dc3494ccc3419757f89c3384cf3c9abec3f18831f35b0";
    private static final String SLOTFILLED = "http://textures.minecraft.net/texture/"
	    + "9e42f682e430b55b61204a6f8b76d5227d278ed9ec4d98bda4a7a4830a4b6";
    private static final String K_OFF = "http://textures.minecraft.net/texture/"
	    + "e883b5beb4e601c3cbf50505c8bd552e81b996076312cffe27b3cc1a29e3";
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
     * Test create new adminshop with invalid size.
     */
    @Test
    public void createNewAdminshopTestFail1() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 5);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §45§c is invalid!", e.getMessage());
	}
    }

    /**
     * Test create new adminshop with existing name.
     */
    @Test
    public void createNewAdminshopTestFail2() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    AdminshopController.createAdminShop("myshop", location, 9);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§c§4myshop§c already exists!", e.getMessage());
	}
    }

    /**
     * Test create new adminshop with invalid name.
     */
    @Test
    public void createNewAdminshopTestFail3() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("my_shop", location, 9);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis shopname is invalid! Use a name without _!", e.getMessage());
	}
    }

    /**
     * Test create new adminshop.
     */
    @Test
    public void createNewAdminshopTestSuccess() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    List<Adminshop> list = AdminshopController.getAdminshopList();
	    assertEquals(1, list.size());
	    Adminshop shop = list.get(0);
	    assertEquals(world, shop.getWorld());
	    assertEquals("A0", shop.getShopId());
	    assertEquals("myshop", shop.getName());
	    assertEquals(EconomyVillager.ADMINSHOP, shop.getShopVillager().getMetadata("ue-type").get(0).value());
	    assertEquals(0, shop.getItemList().size());
	    assertEquals(location, shop.getShopLocation());
	    assertEquals(Profession.NITWIT, shop.getShopVillager().getProfession());
	    assertEquals(location, shop.getShopVillager().getLocation());
	    // check inventory
	    ChestInventoryMock shopInv = (ChestInventoryMock) shop.getShopInventory();
	    assertEquals(9, shopInv.getSize());
	    assertEquals("myshop", shopInv.getName());
	    assertEquals(Material.AIR, shopInv.getItem(0).getType());
	    assertEquals(Material.AIR, shopInv.getItem(1).getType());
	    assertEquals(Material.AIR, shopInv.getItem(2).getType());
	    assertEquals(Material.AIR, shopInv.getItem(3).getType());
	    assertEquals(Material.AIR, shopInv.getItem(4).getType());
	    assertEquals(Material.AIR, shopInv.getItem(5).getType());
	    assertEquals(Material.AIR, shopInv.getItem(6).getType());
	    assertEquals(Material.AIR, shopInv.getItem(7).getType());
	    assertEquals(Material.ANVIL, shopInv.getItem(8).getType());
	    assertEquals("Info", shopInv.getItem(8).getItemMeta().getDisplayName());
	    assertEquals("§6Rightclick: §asell specified amount", shopInv.getItem(8).getItemMeta().getLore().get(0));
	    assertEquals("§6Shift-Rightclick: §asell all", shopInv.getItem(8).getItemMeta().getLore().get(1));
	    assertEquals("§6Leftclick: §abuy", shopInv.getItem(8).getItemMeta().getLore().get(2));
	    // check editor inventory
	    ChestInventoryMock editor = (ChestInventoryMock) shop.getEditorInventory();
	    assertEquals(9, editor.getSize());
	    assertEquals("myshop-Editor", editor.getName());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(0).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(1).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(2).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(3).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(4).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(5).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(6).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(7).getType());
	    assertEquals(Material.AIR, editor.getItem(8).getType());
	    assertEquals("Slot 1", editor.getItem(0).getItemMeta().getDisplayName());
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals("Slot 3", editor.getItem(2).getItemMeta().getDisplayName());
	    assertEquals("Slot 4", editor.getItem(3).getItemMeta().getDisplayName());
	    assertEquals("Slot 5", editor.getItem(4).getItemMeta().getDisplayName());
	    assertEquals("Slot 6", editor.getItem(5).getItemMeta().getDisplayName());
	    assertEquals("Slot 7", editor.getItem(6).getItemMeta().getDisplayName());
	    assertEquals("Slot 8", editor.getItem(7).getItemMeta().getDisplayName());
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
	    assertEquals(SLOTEMPTY,
		    editor.getItem(7).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check slot editor inventory
	    ChestInventoryMock slotEditor = (ChestInventoryMock) shop.getSlotEditorInventory();
	    assertEquals(27, slotEditor.getSize());
	    assertEquals("myshop-SlotEditor", slotEditor.getName());
	    assertEquals(Material.RED_WOOL, slotEditor.getItem(7).getType());
	    assertEquals(Material.GREEN_WOOL, slotEditor.getItem(8).getType());
	    assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(12).getType());
	    assertEquals(Material.PLAYER_HEAD, slotEditor.getItem(21).getType());
	    assertEquals(Material.BARRIER, slotEditor.getItem(26).getType());
	    assertEquals("§cexit without save", slotEditor.getItem(7).getItemMeta().getDisplayName());
	    assertEquals("§esave changes", slotEditor.getItem(8).getItemMeta().getDisplayName());
	    assertEquals("factor off", slotEditor.getItem(12).getItemMeta().getDisplayName());
	    assertEquals("factor off", slotEditor.getItem(21).getItemMeta().getDisplayName());
	    assertEquals("§cremove item", slotEditor.getItem(26).getItemMeta().getDisplayName());
	    assertEquals(K_OFF, slotEditor.getItem(12).getItemMeta().getPersistentDataContainer().get(key,
		    PersistentDataType.STRING));
	    assertEquals(K_OFF, slotEditor.getItem(21).getItemMeta().getPersistentDataContainer().get(key,
		    PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals(0, config.getStringList("ShopItemList").size());
	    assertEquals("1.5", String.valueOf(config.getDouble("ShopLocation.x")));
	    assertEquals("2.3", String.valueOf(config.getDouble("ShopLocation.y")));
	    assertEquals("6.9", String.valueOf(config.getDouble("ShopLocation.z")));
	    assertEquals("World", config.getString("ShopLocation.World"));
	    assertEquals("myshop", config.getString("ShopName"));
	    assertEquals(9, config.getInt("ShopSize"));
	    assertEquals(1, UltimateEconomy.getInstance.getConfig().getStringList("AdminShopIds").size());
	    assertEquals("A0", UltimateEconomy.getInstance.getConfig().getStringList("AdminShopIds").get(0));
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test delete a adminshop.
     */
    @Test
    public void deleteAdminshopTest() {
	try {
	    AdminshopController.createAdminShop("myshop", new Location(world, 1.5, 2.3, 6.9), 9);
	    AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
	    File saveFile = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
	    assertFalse(saveFile.exists());
	    assertEquals(0, UltimateEconomy.getInstance.getConfig().getStringList("AdminShopIds").size());
	    assertEquals(0, AdminshopController.getAdminshopList().size());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get adminshop by name with shop does not exist.
     */
    @Test
    public void getAdminShopByNameFailTest() {
	try {
	    AdminshopController.createAdminShop("myshop", new Location(world, 1.5, 2.3, 6.9), 9);
	    AdminshopController.getAdminShopByName("myshop2");
	    assertTrue(false);
	} catch (GeneralEconomyException | ShopSystemException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§c§4myshop2§c does not exist!", e.getMessage());
	}
    }

    /**
     * Test get adminshop by name.
     */
    @Test
    public void getAdminShopByNameTest() {
	try {
	    AdminshopController.createAdminShop("myshop", new Location(world, 1.5, 2.3, 6.9), 9);
	    AdminshopController.createAdminShop("myshop2", new Location(world, 1.5, 2.3, 6.9), 9);
	    Adminshop shop = AdminshopController.getAdminShopByName("myshop");
	    assertNotNull(shop);
	    assertEquals("myshop", shop.getName());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get adminshop by id.
     */
    @Test
    public void getAdminshopByIdTest() {
	try {
	    AdminshopController.createAdminShop("myshop", new Location(world, 1.5, 2.3, 6.9), 9);
	    Adminshop shop = AdminshopController.getAdminShopById("A0");
	    assertNotNull(shop);
	    assertEquals("A0", shop.getShopId());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test get adminshop by id with shop does not exist.
     */
    @Test
    public void getAdminshopByIdFailTest() {
	try {
	    AdminshopController.createAdminShop("myshop", new Location(world, 1.5, 2.3, 6.9), 9);
	    AdminshopController.getAdminShopById("A1");
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§c§4A1§c does not exist!", e.getMessage());
	}
    }

    /**
     * Test generate free adminshop id.
     */
    @Test
    public void generateFreeAdminShopIdTest() {
	try {
	    String id1 = AdminshopController.generateFreeAdminShopId();
	    AdminshopController.createAdminShop("myshop", new Location(world, 1.5, 2.3, 6.9), 9);
	    String id2 = AdminshopController.generateFreeAdminShopId();
	    assertEquals("A0", id1);
	    assertEquals("A1", id2);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test loading all adminshops (empty).
     */
    @Test
    public void loadAllAdminShopsTest() {
	try {
	    Location location = new Location(world, 1.5, 2.3, 6.9);
	    AdminshopController.createAdminShop("myshop", location, 9);
	    assertEquals(1, AdminshopController.getAdminshopList().size());
	    AdminshopController.getAdminshopList().get(0).despawnVillager();
	    AdminshopController.getAdminshopList().clear();
	    assertEquals(0, AdminshopController.getAdminshopList().size());
	    AdminshopController.loadAllAdminShops();
	    assertEquals(1, AdminshopController.getAdminshopList().size());
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    assertEquals(world, shop.getWorld());
	    assertEquals("A0", shop.getShopId());
	    assertEquals("myshop", shop.getName());
	    assertEquals(EconomyVillager.ADMINSHOP, shop.getShopVillager().getMetadata("ue-type").get(0).value());
	    assertEquals(0, shop.getItemList().size());
	    assertEquals(location, shop.getShopLocation());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }
    
    /**
     * Test loading all adminshops with custom profession.
     */
    @Test
    public void loadAllAdminShopsTestWithProfession() {
	try {
	    Location location = new Location(world, 1.5, 2.3, 6.9);
	    AdminshopController.createAdminShop("myshop", location, 9);
	    AdminshopController.getAdminshopList().get(0).changeProfession(Profession.ARMORER);
	    assertEquals(1, AdminshopController.getAdminshopList().size());
	    AdminshopController.getAdminshopList().get(0).despawnVillager();
	    AdminshopController.getAdminshopList().clear();
	    assertEquals(0, AdminshopController.getAdminshopList().size());
	    AdminshopController.loadAllAdminShops();
	    assertEquals(1, AdminshopController.getAdminshopList().size());
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    assertEquals(world, shop.getWorld());
	    assertEquals("A0", shop.getShopId());
	    assertEquals("myshop", shop.getName());
	    assertEquals(Profession.ARMORER, shop.getShopVillager().getProfession());
	    assertEquals(EconomyVillager.ADMINSHOP, shop.getShopVillager().getMetadata("ue-type").get(0).value());
	    assertEquals(0, shop.getItemList().size());
	    assertEquals(location, shop.getShopLocation());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test loading all adminshops with no shops.
     */
    @Test
    public void loadAllAdminShopsNoShopTest() {
	AdminshopController.loadAllAdminShops();
    }
    
    /**
     * Test reload shop items.
     * 
     */
    @Test
    public void loadAllAdminShopsTestWithItems() {
	ItemStack spawner = new ItemStack(Material.SPAWNER, 1);
	ItemMeta spawnerMeta = spawner.getItemMeta();
	spawnerMeta.setDisplayName("COW");
	spawner.setItemMeta(spawnerMeta);
	ItemStack enchantedTool = new ItemStack(Material.DIAMOND_PICKAXE, 16);
	enchantedTool.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
	CraftMetaItemMock enchantedToolMeta = (CraftMetaItemMock) enchantedTool.getItemMeta();
	enchantedToolMeta.setDamage(10);
	enchantedTool.setItemMeta(enchantedToolMeta);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(0, 5, 10, enchantedTool);
	    shop.addShopItem(1, 0, 20, spawner);
	    AdminshopController.getAdminshopList().get(0).despawnVillager();
	    AdminshopController.getAdminshopList().clear();
	    AdminshopController.loadAllAdminShops();
	    Adminshop response = AdminshopController.getAdminshopList().get(0);
	    enchantedTool.setAmount(1);
	    String itemString = enchantedTool.toString();
	    String itemStringSpawner = "SPAWNER_COW";
	    assertEquals(2, response.getItemList().size());
	    assertEquals(itemString, response.getItemList().get(0));
	    // check shop inventory
	    Inventory inv = response.getShopInventory();
	    ItemStack shopItem = inv.getItem(0);
	    assertEquals(Material.DIAMOND_PICKAXE, shopItem.getType());
	    assertEquals(1, shopItem.getEnchantments().size());
	    assertTrue(shopItem.getEnchantments().containsKey(Enchantment.DURABILITY));
	    assertTrue(shopItem.getEnchantments().containsValue(1));
	    assertEquals(10, ((CraftMetaItemMock) enchantedTool.getItemMeta()).getDamage());
	    assertEquals(16, shopItem.getAmount());
	    assertEquals(2, shopItem.getItemMeta().getLore().size());
	    assertEquals("§616 buy for §a10.0 $", shopItem.getItemMeta().getLore().get(0));
	    assertEquals("§616 sell for §a5.0 $", shopItem.getItemMeta().getLore().get(1));
	    // check editor inventory
	    Inventory editor = response.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 1", editor.getItem(0).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = response.getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("5.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("10.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(0, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(16, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(enchantedTool.toString(), config.getItemStack("ShopItems." + itemString + ".Name").toString());
	    
	    
	    assertEquals("SPAWNER_COW", response.getItemList().get(1));
	    // check shop inventory
	    ItemStack shopItemSpawner = inv.getItem(1);
	    assertEquals(Material.SPAWNER, shopItemSpawner.getType());
	    assertEquals(1, shopItemSpawner.getAmount());
	    assertEquals("COW", shopItemSpawner.getItemMeta().getDisplayName());
	    assertEquals(1, shopItemSpawner.getItemMeta().getLore().size());
	    assertEquals("§61 buy for §a20.0 $", shopItemSpawner.getItemMeta().getLore().get(0));
	    // check editor inventory
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    assertEquals("0.0", String.valueOf(config.getDouble("ShopItems." + itemStringSpawner + ".sellPrice")));
	    assertEquals("20.0", String.valueOf(config.getDouble("ShopItems." + itemStringSpawner + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemStringSpawner + ".Slot"));
	    assertEquals(1, config.getInt("ShopItems." + itemStringSpawner + ".Amount"));
	    assertEquals(itemStringSpawner, config.getString("ShopItems." + itemStringSpawner + ".Name").toString());
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    /**
     * Test despawn all shop villagers.
     */
    @Test
    public void despawnAllVillagersTest() {
	try {
	    AdminshopController.createAdminShop("myshop", new Location(world, 1.5, 2.3, 6.9), 9);
	    Collection<Entity> entities1 = world
		    .getNearbyEntities(AdminshopController.getAdminshopList().get(0).getShopLocation(), 0, 0, 0);
	    AdminshopController.despawnAllVillagers();
	    Collection<Entity> entities2 = world
		    .getNearbyEntities(AdminshopController.getAdminshopList().get(0).getShopLocation(), 0, 0, 0);
	    assertEquals(1, entities1.size());
	    assertEquals(0, entities2.size());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }
}
