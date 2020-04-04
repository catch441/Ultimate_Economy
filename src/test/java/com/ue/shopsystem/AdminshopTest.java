package com.ue.shopsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.player.api.EconomyPlayerController;
import com.ue.shopsystem.api.Adminshop;
import com.ue.shopsystem.api.AdminshopController;
import com.ue.shopsystem.impl.AdminshopImpl;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;
import be.seeseemelk.mockbukkit.inventory.meta.CraftMetaItemMock;
import be.seeseemelk.mockbukkit.inventory.meta.CraftMetaPotionMock;

public class AdminshopTest {

    private static final String SLOTEMPTY = "http://textures.minecraft.net/texture/"
	    + "b55d5019c8d55bcb9dc3494ccc3419757f89c3384cf3c9abec3f18831f35b0";
    private static final String SLOTFILLED = "http://textures.minecraft.net/texture/"
	    + "9e42f682e430b55b61204a6f8b76d5227d278ed9ec4d98bda4a7a4830a4b6";
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
	player = server.addPlayer();
	}

    /**
     * Unload mock bukkit.
     */
    @AfterAll
    public static void deleteSavefiles() {
	UltimateEconomy.getInstance.getDataFolder().delete();
	server.setPlayers(0);
	MockBukkit.unload();
    }

    /**
     * Unload all adminshopss.
     */
    @AfterEach
    public void unloadAdminshops() {
	int size = AdminshopController.getAdminshopList().size();
	for (int i = 0; i < size; i++) {
	    try {
		AdminshopController.deleteAdminShop(AdminshopController.getAdminshopList().get(0));
	    } catch (ShopSystemException e) {
		assertTrue(false);
	    }
	}
	int size2 = EconomyPlayerController.getAllEconomyPlayers().size();
	for (int i = 0; i < size2; i++) {
	    EconomyPlayerController.deleteEconomyPlayer(EconomyPlayerController.getAllEconomyPlayers().get(0));
	}
    }

    @Test
    public void addShopItemTest() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    item.setAmount(1);
	    String itemString = item.toString();
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.STONE, shopItem.getType());
	    assertEquals(16, shopItem.getAmount());
	    assertEquals(2, shopItem.getItemMeta().getLore().size());
	    assertEquals("§616 buy for §a20.0 $", shopItem.getItemMeta().getLore().get(0));
	    assertEquals("§616 sell for §a10.0 $", shopItem.getItemMeta().getLore().get(1));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("10.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("20.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(16, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(item, config.getItemStack("ShopItems." + itemString + ".Name"));
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void addShopItemTestWithLoreAndCustomName() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	ItemMeta meta = item.getItemMeta();
	meta.setDisplayName("custom name");
	List<String> lore = new ArrayList<>();
	lore.add("my lore");
	meta.setLore(lore);
	item.setItemMeta(meta);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    item.setAmount(1);
	    String itemString = item.toString();
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.STONE, shopItem.getType());
	    assertEquals(16, shopItem.getAmount());
	    assertEquals(3, shopItem.getItemMeta().getLore().size());
	    assertEquals("custom name", shopItem.getItemMeta().getDisplayName());
	    assertEquals("my lore", shopItem.getItemMeta().getLore().get(0));
	    assertEquals("§616 buy for §a20.0 $", shopItem.getItemMeta().getLore().get(1));
	    assertEquals("§616 sell for §a10.0 $", shopItem.getItemMeta().getLore().get(2));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("10.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("20.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(16, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(item.toString(), config.getItemStack("ShopItems." + itemString + ".Name").toString());
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void addShopItemTestWithOnlyBuyPrice() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 0, 20, item);
	    item.setAmount(1);
	    String itemString = item.toString();
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.STONE, shopItem.getType());
	    assertEquals(16, shopItem.getAmount());
	    assertEquals(1, shopItem.getItemMeta().getLore().size());
	    assertEquals("§616 buy for §a20.0 $", shopItem.getItemMeta().getLore().get(0));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("0.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("20.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(16, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(item, config.getItemStack("ShopItems." + itemString + ".Name"));
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void addShopItemTestWithOnlySellPrice() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 0, item);
	    item.setAmount(1);
	    String itemString = item.toString();
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.STONE, shopItem.getType());
	    assertEquals(16, shopItem.getAmount());
	    assertEquals(1, shopItem.getItemMeta().getLore().size());
	    assertEquals("§616 sell for §a10.0 $", shopItem.getItemMeta().getLore().get(0));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("10.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("0.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(16, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(item, config.getItemStack("ShopItems." + itemString + ".Name"));
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void addShopItemTestWithPotion() {
	ItemStack item = new ItemStack(Material.POTION, 16);
	PotionMeta meta = (PotionMeta) item.getItemMeta();
	PotionData data = new PotionData(PotionType.REGEN, true, false);
	meta.setBasePotionData(data);
	item.setItemMeta(meta);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 0, item);
	    item.setAmount(1);
	    String itemString = item.toString();
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.POTION, shopItem.getType());
	    assertEquals(data, ((CraftMetaPotionMock) shopItem.getItemMeta()).getBasePotionData());
	    assertEquals(16, shopItem.getAmount());
	    assertEquals(1, shopItem.getItemMeta().getLore().size());
	    assertEquals("§616 sell for §a10.0 $", shopItem.getItemMeta().getLore().get(0));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("10.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("0.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(16, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(item.toString(), config.getItemStack("ShopItems." + itemString + ".Name").toString());
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void addShopItemTestWithEnchantedTool() {
	ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE, 16);
	item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
	CraftMetaItemMock meta = (CraftMetaItemMock) item.getItemMeta();
	meta.setDamage(10);
	item.setItemMeta(meta);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = (AdminshopImpl) AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 0, item);
	    item.setAmount(1);
	    String itemString = item.toString();
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.DIAMOND_PICKAXE, shopItem.getType());
	    assertEquals(1, shopItem.getEnchantments().size());
	    assertTrue(shopItem.getEnchantments().containsKey(Enchantment.DURABILITY));
	    assertTrue(shopItem.getEnchantments().containsValue(1));
	    assertEquals(10, ((CraftMetaItemMock) item.getItemMeta()).getDamage());
	    assertEquals(16, shopItem.getAmount());
	    assertEquals(1, shopItem.getItemMeta().getLore().size());
	    assertEquals("§616 sell for §a10.0 $", shopItem.getItemMeta().getLore().get(0));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("10.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("0.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(16, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(item.toString(), config.getItemStack("ShopItems." + itemString + ".Name").toString());
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void addShopItemTestWithInvalidPrice() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, -10, 20, item);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §4-10.0§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void addShopItemTestWithoutPrices() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 0, 0, item);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cOne of the prices have to be above 0!", e.getMessage());
	}
    }

    @Test
    public void addShopItemTestWithInvalidSlot() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(10, 10, 20, item);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §411§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void addShopItemTestWithOccupiedSlot() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, new ItemStack(Material.COBBLESTONE, 16));
	    shop.addShopItem(1, 10, 20, item);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof PlayerException);
	    assertEquals("§cThis slot is occupied!", e.getMessage());
	}
    }

    @Test
    public void addShopItemTestWithItemAlreadyExists() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    shop.addShopItem(2, 10, 20, item);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis item already exists in this shop!", e.getMessage());
	}
    }

    @Test
    public void editShopItemTest() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    String response = shop.editShopItem(1, "8", "15.0", "25.0");
	    item.setAmount(1);
	    String itemString = item.toString();
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));
	    assertEquals("§6Updated §aamount §asellPrice §abuyPrice §6for item §astone", response);
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.STONE, shopItem.getType());
	    assertEquals(8, shopItem.getAmount());
	    assertEquals(2, shopItem.getItemMeta().getLore().size());
	    assertEquals("§68 buy for §a25.0 $", shopItem.getItemMeta().getLore().get(0));
	    assertEquals("§68 sell for §a15.0 $", shopItem.getItemMeta().getLore().get(1));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("15.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("25.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(8, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(item, config.getItemStack("ShopItems." + itemString + ".Name"));
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void editShopItemTestWithOnlyAmount() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    String response = shop.editShopItem(1, "8", "none", "none");
	    item.setAmount(1);
	    String itemString = item.toString();
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));
	    assertEquals("§6Updated §aamount §6for item §astone", response);
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.STONE, shopItem.getType());
	    assertEquals(8, shopItem.getAmount());
	    assertEquals(2, shopItem.getItemMeta().getLore().size());
	    assertEquals("§68 buy for §a20.0 $", shopItem.getItemMeta().getLore().get(0));
	    assertEquals("§68 sell for §a10.0 $", shopItem.getItemMeta().getLore().get(1));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("10.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("20.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(8, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(item, config.getItemStack("ShopItems." + itemString + ".Name"));
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void editShopItemTestWithOnlySellPrice() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    String response = shop.editShopItem(1, "none", "15", "none");
	    item.setAmount(1);
	    String itemString = item.toString();
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));
	    assertEquals("§6Updated §asellPrice §6for item §astone", response);
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.STONE, shopItem.getType());
	    assertEquals(16, shopItem.getAmount());
	    assertEquals(2, shopItem.getItemMeta().getLore().size());
	    assertEquals("§616 buy for §a20.0 $", shopItem.getItemMeta().getLore().get(0));
	    assertEquals("§616 sell for §a15.0 $", shopItem.getItemMeta().getLore().get(1));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("15.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("20.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(16, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(item, config.getItemStack("ShopItems." + itemString + ".Name"));
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void editShopItemTestWithOnlyBuyPrice() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    String response = shop.editShopItem(1, "none", "none", "25");
	    item.setAmount(1);
	    String itemString = item.toString();
	    assertEquals(1, shop.getItemList().size());
	    assertEquals(itemString, shop.getItemList().get(0));
	    assertEquals("§6Updated §abuyPrice §6for item §astone", response);
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.STONE, shopItem.getType());
	    assertEquals(16, shopItem.getAmount());
	    assertEquals(2, shopItem.getItemMeta().getLore().size());
	    assertEquals("§616 buy for §a25.0 $", shopItem.getItemMeta().getLore().get(0));
	    assertEquals("§616 sell for §a10.0 $", shopItem.getItemMeta().getLore().get(1));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("10.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("25.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(16, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(item, config.getItemStack("ShopItems." + itemString + ".Name"));
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void editShopItemTestWithEmptySlot() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.editShopItem(1, "8", "10", "25.0");
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis slot is empty!", e.getMessage());
	}
    }

    @Test
    public void editShopItemTestWithInvalidBuyPrice() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    shop.editShopItem(1, "8", "-10", "25.0");
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §4-10§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void editShopItemTestWithZeroPrices() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    shop.editShopItem(1, "8", "0", "0");
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cOne of the prices have to be above 0!", e.getMessage());
	}
    }

    @Test
    public void editShopItemTestWithInvalidAmount() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    shop.editShopItem(1, "100", "10.0", "25.0");
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §4100§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void editShopItemTestWithInvalidSellPrice() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    shop.editShopItem(1, "8", "10.0", "-25.0");
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §4-25.0§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void addSpawnerTest() {
	ItemStack item = new ItemStack(Material.SPAWNER, 1);
	ItemMeta meta = item.getItemMeta();
	meta.setDisplayName("COW");
	item.setItemMeta(meta);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = (AdminshopImpl) AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 0, 10, item);
	    String itemString = "SPAWNER_COW";
	    assertEquals(1, shop.getItemList().size());
	    assertEquals("SPAWNER_COW", shop.getItemList().get(0));
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.SPAWNER, shopItem.getType());
	    assertEquals(1, shopItem.getAmount());
	    assertEquals("COW", shopItem.getItemMeta().getDisplayName());
	    assertEquals(1, shopItem.getItemMeta().getLore().size());
	    assertEquals("§61 buy for §a10.0 $", shopItem.getItemMeta().getLore().get(0));
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTFILLED,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("0.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".sellPrice")));
	    assertEquals("10.0", String.valueOf(config.getDouble("ShopItems." + itemString + ".buyPrice")));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Slot"));
	    assertEquals(1, config.getInt("ShopItems." + itemString + ".Amount"));
	    assertEquals(itemString, config.getString("ShopItems." + itemString + ".Name").toString());
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void removeItemTest() {
	ItemStack item = new ItemStack(Material.STONE, 16);
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(1, 10, 20, item);
	    shop.removeShopItem(1);
	    assertEquals(0, shop.getItemList().size());
	    // check shop inventory
	    Inventory inv = shop.getShopInventory();
	    ItemStack shopItem = inv.getItem(1);
	    assertEquals(Material.AIR, shopItem.getType());
	    // check editor inventory
	    Inventory editor = shop.getEditorInventory();
	    NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals(SLOTEMPTY,
		    editor.getItem(1).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals(0, config.getStringList("ShopItemList").size());
	    assertFalse(config.isSet("ShopItens." + item.toString()));
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void removeItemTestWithEmptySlot() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.removeShopItem(0);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis slot is empty!", e.getMessage());
	}
    }

    @Test
    public void removeItemTestWithInvalidSlot() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.removeShopItem(10);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §411§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void removeItemTestWithInvalidItem() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.removeShopItem(8);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cThis item cannot be deleted!", e.getMessage());
	}
    }

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
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("5.9", String.valueOf(config.getDouble("ShopLocation.x")));
	    assertEquals("1.0", String.valueOf(config.getDouble("ShopLocation.y")));
	    assertEquals("6.3", String.valueOf(config.getDouble("ShopLocation.z")));
	    assertEquals("World", config.getString("ShopLocation.World"));
	} catch (ShopSystemException | GeneralEconomyException | TownSystemException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void changeProfessionTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.changeProfession(Profession.CARTOGRAPHER);
	    assertEquals(Profession.CARTOGRAPHER, shop.getShopVillager().getProfession());
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("CARTOGRAPHER", config.getString("Profession"));
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

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
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals("newname", config.getString("ShopName"));
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

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

    @Test
    public void changeShopSizeTestWithGreaterSize() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.changeShopSize(18);
	    assertEquals(18, shop.getSize());
	    // check inventories
	    assertEquals(18, shop.getShopInventory().getSize());
	    assertEquals(Material.ANVIL, shop.getShopInventory().getItem(17).getType());
	    assertEquals("Info", shop.getShopInventory().getItem(17).getItemMeta().getDisplayName());
	    assertEquals("§6Rightclick: §asell specified amount",
		    shop.getShopInventory().getItem(17).getItemMeta().getLore().get(0));
	    assertEquals("§6Shift-Rightclick: §asell all",
		    shop.getShopInventory().getItem(17).getItemMeta().getLore().get(1));
	    assertEquals("§6Leftclick: §abuy", shop.getShopInventory().getItem(17).getItemMeta().getLore().get(2));
	    Inventory editor = shop.getEditorInventory();
	    assertEquals(18, shop.getEditorInventory().getSize());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(0).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(1).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(2).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(3).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(4).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(5).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(6).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(7).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(8).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(9).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(10).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(11).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(12).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(13).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(14).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(15).getType());
	    assertEquals(Material.PLAYER_HEAD, editor.getItem(16).getType());
	    assertEquals(Material.AIR, editor.getItem(17).getType());
	    assertEquals("Slot 1", editor.getItem(0).getItemMeta().getDisplayName());
	    assertEquals("Slot 2", editor.getItem(1).getItemMeta().getDisplayName());
	    assertEquals("Slot 3", editor.getItem(2).getItemMeta().getDisplayName());
	    assertEquals("Slot 4", editor.getItem(3).getItemMeta().getDisplayName());
	    assertEquals("Slot 5", editor.getItem(4).getItemMeta().getDisplayName());
	    assertEquals("Slot 6", editor.getItem(5).getItemMeta().getDisplayName());
	    assertEquals("Slot 7", editor.getItem(6).getItemMeta().getDisplayName());
	    assertEquals("Slot 8", editor.getItem(7).getItemMeta().getDisplayName());
	    assertEquals("Slot 9", editor.getItem(8).getItemMeta().getDisplayName());
	    assertEquals("Slot 10", editor.getItem(9).getItemMeta().getDisplayName());
	    assertEquals("Slot 11", editor.getItem(10).getItemMeta().getDisplayName());
	    assertEquals("Slot 12", editor.getItem(11).getItemMeta().getDisplayName());
	    assertEquals("Slot 13", editor.getItem(12).getItemMeta().getDisplayName());
	    assertEquals("Slot 14", editor.getItem(13).getItemMeta().getDisplayName());
	    assertEquals("Slot 15", editor.getItem(14).getItemMeta().getDisplayName());
	    assertEquals("Slot 16", editor.getItem(15).getItemMeta().getDisplayName());
	    assertEquals("Slot 17", editor.getItem(16).getItemMeta().getDisplayName());
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
	    assertEquals(SLOTEMPTY,
		    editor.getItem(8).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    assertEquals(SLOTEMPTY,
		    editor.getItem(9).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    assertEquals(SLOTEMPTY,
		    editor.getItem(10).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    assertEquals(SLOTEMPTY,
		    editor.getItem(11).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    assertEquals(SLOTEMPTY,
		    editor.getItem(12).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    assertEquals(SLOTEMPTY,
		    editor.getItem(13).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    assertEquals(SLOTEMPTY,
		    editor.getItem(14).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    assertEquals(SLOTEMPTY,
		    editor.getItem(15).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    assertEquals(SLOTEMPTY,
		    editor.getItem(16).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
	    // check savefile
	    File saveFile = shop.getSavefileHandler().getSaveFile();
	    YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
	    assertEquals(18, config.getInt("ShopSize"));
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void changeShopSizeTestWithSmallerSize() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 18);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.changeShopSize(9);
	    assertEquals(9, shop.getSize());
	    assertEquals(Material.ANVIL, shop.getShopInventory().getItem(8).getType());
	    assertEquals("Info", shop.getShopInventory().getItem(8).getItemMeta().getDisplayName());
	    assertEquals("§6Rightclick: §asell specified amount",
		    shop.getShopInventory().getItem(8).getItemMeta().getLore().get(0));
	    assertEquals("§6Shift-Rightclick: §asell all",
		    shop.getShopInventory().getItem(8).getItemMeta().getLore().get(1));
	    assertEquals("§6Leftclick: §abuy", shop.getShopInventory().getItem(8).getItemMeta().getLore().get(2));
	    Inventory editor = shop.getEditorInventory();
	    assertEquals(9, shop.getEditorInventory().getSize());
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
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void changeShopSizeTestWithInvalidSize() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.changeShopSize(5);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §45§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void changeShopSizeTestWithOccupiedSlots() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 18);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(15, 0, 1, new ItemStack(Material.STONE));
	    shop.changeShopSize(9);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(e instanceof ShopSystemException);
	    assertEquals("§cChanging the shop size has failed due to occupied slots!", e.getMessage());
	}
    }

    @Test
    public void despawnVillagerTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    assertEquals(1, world.getNearbyEntities(location, 0, 0, 0).size());
	    shop.despawnVillager();
	    assertEquals(0, world.getNearbyEntities(location, 0, 0, 0).size());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void getEditorInventoryTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    ChestInventoryMock editor = (ChestInventoryMock) shop.getEditorInventory();
	    assertNotNull(editor);
	    assertEquals("myshop-Editor", editor.getName());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void getItemTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(0, 0, 1, new ItemStack(Material.STONE));
	    ItemStack item = shop.getShopItem(0);
	    assertEquals(Material.STONE, item.getType());
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void getWorldTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    World world1 = shop.getWorld();
	    assertEquals(world, world1);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void openSlotEditorTestWithEmptySlot() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.openSlotEditor(player, 0);
	    Inventory inv = player.getOpenInventory().getTopInventory();
	    assertNotNull(inv);
	    assertEquals(inv, shop.getSlotEditorInventory());
	    assertEquals(Material.BARRIER, inv.getItem(0).getType());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void openSlotEditorTestWithOccupiedSlot() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.addShopItem(0, 1, 2, new ItemStack(Material.COAL, 3));
	    shop.openSlotEditor(player, 0);
	    Inventory inv = player.getOpenInventory().getTopInventory();
	    assertNotNull(inv);
	    assertEquals(inv, shop.getSlotEditorInventory());
	    assertEquals(Material.COAL, shop.getSlotEditorInventory().getItem(0).getType());
	    assertEquals(3, shop.getSlotEditorInventory().getItem(0).getAmount());
	    assertNull(shop.getSlotEditorInventory().getItem(0).getItemMeta().getLore());
	} catch (ShopSystemException | GeneralEconomyException | PlayerException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void openSlotEditorTestWithInvalidSlot() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.openSlotEditor(player, 12);
	    assertTrue(false);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(e instanceof GeneralEconomyException);
	    assertEquals("§cThe parameter §413§c is invalid!", e.getMessage());
	}
    }

    @Test
    public void openShopInventoryTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.openShopInventory(player);
	    Inventory inv = player.getOpenInventory().getTopInventory();
	    assertNotNull(inv);
	    assertEquals(inv, shop.getShopInventory());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void openEditorInventoryTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    shop.openEditor(player);
	    Inventory inv = player.getOpenInventory().getTopInventory();
	    assertNotNull(inv);
	    assertEquals(inv, shop.getEditorInventory());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void getSaveFileTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    File file = shop.getSavefileHandler().getSaveFile();
	    assertEquals("A0.yml", file.getName());
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void getSizeTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    int size = shop.getSize();
	    assertEquals(9, size);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void getShopIdTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    String id = shop.getShopId();
	    assertEquals("A0", id);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }

    @Test
    public void getShopLocationTest() {
	Location location = new Location(world, 1.5, 2.3, 6.9);
	try {
	    AdminshopController.createAdminShop("myshop", location, 9);
	    Adminshop shop = AdminshopController.getAdminshopList().get(0);
	    Location loc = shop.getShopLocation();
	    assertEquals(location, loc);
	} catch (ShopSystemException | GeneralEconomyException e) {
	    assertTrue(false);
	}
    }
}
