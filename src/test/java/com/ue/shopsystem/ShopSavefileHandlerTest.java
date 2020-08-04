package com.ue.shopsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.shopsystem.logic.impl.ShopDaoImpl;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.ultimate_economy.UltimateEconomy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

public class ShopSavefileHandlerTest {

	private static ShopDaoImpl savefileHandler;
	private static WorldMock world;

	/**
	 * Init shop for tests.
	 */
	@BeforeAll
	public static void initPlugin() {
		ServerMock server = MockBukkit.mock();
		Bukkit.getLogger().setLevel(Level.OFF);
		MockBukkit.load(UltimateEconomy.class);
		world = new WorldMock(Material.GRASS_BLOCK, 1);
		server.addWorld(world);
		server.addPlayer("kthschnll");
		savefileHandler = new ShopDaoImpl("A0");
	}

	/**
	 * Unload mock bukkit.
	 */
	@AfterAll
	public static void deleteSavefiles() {
		UltimateEconomy.getInstance.getDataFolder().delete();
		EconomyPlayerManagerImpl.deleteEconomyPlayer(EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0));
		MockBukkit.unload();
		savefileHandler.deleteFile();
	}

	/**
	 * Unload all.
	 */
	@AfterEach
	public void unload() {
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		file.delete();
		savefileHandler = new ShopDaoImpl("A0");
	}

	@Test
	public void constructorNewTest() {
		ShopDaoImpl savefileHandler1 = new ShopDaoImpl("A1");
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A1.yml");
		assertTrue(file.exists());
		savefileHandler1.deleteFile();
	}

	@Test
	public void constructorLoadTest() {
		try {
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A1.yml");
			file.createNewFile();
			ShopDaoImpl savefileHandler1 = new ShopDaoImpl("A1");
			File result = new File(UltimateEconomy.getInstance.getDataFolder(), "A1.yml");
			assertTrue(result.exists());
			savefileHandler1.deleteFile();
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void saveShopNameTest() {
		savefileHandler.saveShopName("kthschnll");
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("kthschnll", config.getString("ShopName"));
	}

	@Test
	public void saveShopItemTest() {
		ItemStack stack = new ItemStack(Material.STONE);
		ShopItem item = new ShopItem(stack, 1, 2, 3, 4);
		savefileHandler.saveShopItem(item, false);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("ItemStack{STONE x 1}", config.getString("ShopItems." + stack.toString() + ".Name"));
		assertEquals("4", config.getString("ShopItems." + stack.toString() + ".Slot"));
		assertEquals("true", config.getString("ShopItems." + stack.toString() + ".newSaveMethod"));
		assertEquals("2.0", config.getString("ShopItems." + stack.toString() + ".sellPrice"));
		assertEquals("3.0", config.getString("ShopItems." + stack.toString() + ".buyPrice"));
		assertEquals("1", config.getString("ShopItems." + stack.toString() + ".Amount"));
	}

	@Test
	public void saveShopItemTestWithSpawner() {
		ItemStack stack = new ItemStack(Material.SPAWNER);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("COW");
		stack.setItemMeta(meta);
		ShopItem item = new ShopItem(stack, 1, 2, 3, 4);
		savefileHandler.saveShopItem(item, false);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("SPAWNER_COW", config.getString("ShopItems." + item.getItemString() + ".Name"));
		assertEquals("4", config.getString("ShopItems." + item.getItemString() + ".Slot"));
		assertEquals("true", config.getString("ShopItems." + item.getItemString() + ".newSaveMethod"));
		assertEquals("2.0", config.getString("ShopItems." + item.getItemString() + ".sellPrice"));
		assertEquals("3.0", config.getString("ShopItems." + item.getItemString() + ".buyPrice"));
		assertEquals("1", config.getString("ShopItems." + item.getItemString() + ".Amount"));
	}

	@Test
	public void saveShopItemTestWitDelete() {
		ItemStack stack = new ItemStack(Material.SPAWNER);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("COW");
		stack.setItemMeta(meta);
		ShopItem item = new ShopItem(stack, 1, 2, 3, 4);
		savefileHandler.saveShopItem(item, false);
		savefileHandler.saveShopItem(item, true);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertNull(config.getString("ShopItems." + item.getItemString() + ".Name"));
		assertNull(config.getString("ShopItems." + item.getItemString() + ".Slot"));
		assertNull(config.getString("ShopItems." + item.getItemString() + ".newSaveMethod"));
		assertNull(config.getString("ShopItems." + item.getItemString() + ".sellPrice"));
		assertNull(config.getString("ShopItems." + item.getItemString() + ".buyPrice"));
		assertNull(config.getString("ShopItems." + item.getItemString() + ".Amount"));
	}

	@Test
	public void saveShopSizeTest() {
		savefileHandler.saveShopSize(10);
		;
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("10", config.getString("ShopSize"));
	}

	@Test
	public void saveProfessionTest() {
		savefileHandler.saveProfession(Profession.ARMORER);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("ARMORER", config.getString("Profession"));
	}

	@Test
	public void saveItemNamesTest() {
		List<String> list = new ArrayList<>();
		list.add("kth1");
		list.add("kth2");
		savefileHandler.saveItemNames(list);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals(2, config.getStringList("ShopItemList").size());
		assertEquals("kth1", config.getStringList("ShopItemList").get(0));
		assertEquals("kth2", config.getStringList("ShopItemList").get(1));
	}

	@Test
	public void saveStockTest() {
		savefileHandler.saveStock("kth", 6);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("6", config.getString("ShopItems.kth.stock"));
	}

	@Test
	public void saveRentUntilTest() {
		savefileHandler.saveRentUntil(10L);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("10", config.getString("RentUntil"));
	}

	@Test
	public void saveShopLocationTest() {
		Location loc = new Location(world, 1, 2, 3);
		savefileHandler.saveShopLocation(loc);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("World", config.getString("ShopLocation.World"));
		assertEquals("1.0", config.getString("ShopLocation.x"));
		assertEquals("2.0", config.getString("ShopLocation.y"));
		assertEquals("3.0", config.getString("ShopLocation.z"));
	}

	@Test
	public void saveRentalFeeTest() {
		savefileHandler.saveRentalFee(2.5);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.5", config.getString("RentalFee"));
	}

	@Test
	public void saveRentableTest() {
		savefileHandler.saveRentable(true);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("true", config.getString("Rentable"));
	}

	@Test
	public void saveOwnerTest() {
		EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
		savefileHandler.saveOwner(ecoPlayer);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("kthschnll", config.getString("Owner"));
	}

	@Test
	public void saveOwnerTestWithDelete() {
		EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
		savefileHandler.saveOwner(ecoPlayer);
		savefileHandler.saveOwner(null);
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertNull(config.getString("Owner"));
	}

	@Test
	public void loadShopSizeTest() {
		savefileHandler.saveShopSize(10);
		assertEquals(10, savefileHandler.loadShopSize());
	}

	@Test
	public void loadShopNameTest() {
		savefileHandler.saveShopName("kth");
		assertEquals("kth", savefileHandler.loadShopName());
	}

	@Test
	public void loadShopVillagerProfessionTest() {
		savefileHandler.saveProfession(Profession.ARMORER);
		assertEquals(Profession.ARMORER, savefileHandler.loadShopVillagerProfession());
	}

	@Test
	public void loadShopVillagerProfessionTestWithNitwit() {
		assertEquals(Profession.NITWIT, savefileHandler.loadShopVillagerProfession());
	}

	@Test
	public void loadShopLocationTest() {
		Location loc = new Location(world, 1, 2, 3);
		savefileHandler.saveShopLocation(loc);
		try {
			assertEquals("1.0", String.valueOf(savefileHandler.loadShopLocation().getX()));
			assertEquals("2.0", String.valueOf(savefileHandler.loadShopLocation().getY()));
			assertEquals("3.0", String.valueOf(savefileHandler.loadShopLocation().getZ()));
			assertEquals("World", String.valueOf(savefileHandler.loadShopLocation().getWorld().getName()));
		} catch (TownSystemException e) {
			fail();
		}
	}

	@Test
	public void loadShopLocationTestWithInvalidWorld() {
		WorldMock world2 = new WorldMock();
		world2.setName("newWorld");
		Location loc = new Location(world2, 1, 2, 3);
		savefileHandler.saveShopLocation(loc);
		try {
			savefileHandler.loadShopLocation();
			fail();
		} catch (TownSystemException e) {
			assertEquals("§cThe world §4<unknown>§c does not exist on this server!", e.getMessage());
		}
	}

	@Test
	public void loadItemTest() {
		ItemStack stack = new ItemStack(Material.STONE);
		ShopItem item = new ShopItem(stack, 1, 2, 3, 4);
		savefileHandler.saveShopItem(item, false);
		ShopItem result = savefileHandler.loadItem("ItemStack{STONE x 1}");
		assertEquals("ItemStack{STONE x 1}", result.getItemString());
		assertEquals(4, result.getSlot());
		assertEquals("2.0", String.valueOf(result.getSellPrice()));
		assertEquals("3.0", String.valueOf(result.getBuyPrice()));
		assertEquals(1, result.getAmount());
		assertEquals(Material.STONE, result.getItemStack().getType());
	}

	@Test
	public void loadItemTestWithSpawner() {
		ItemStack stack = new ItemStack(Material.SPAWNER);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName("PIG");
		stack.setItemMeta(meta);
		ShopItem item = new ShopItem(stack, 1, 2, 3, 4);
		savefileHandler.saveShopItem(item, false);
		ShopItem result = savefileHandler.loadItem("SPAWNER_PIG");
		assertEquals("SPAWNER_PIG", result.getItemString());
		assertEquals(4, result.getSlot());
		assertEquals("2.0", String.valueOf(result.getSellPrice()));
		assertEquals("3.0", String.valueOf(result.getBuyPrice()));
		assertEquals(1, result.getAmount());
		assertEquals(Material.SPAWNER, result.getItemStack().getType());
		assertEquals("PIG", result.getItemStack().getItemMeta().getDisplayName());
	}

	@Test
	public void loadItemNameListTest() {
		List<String> list = new ArrayList<>();
		list.add("kth1");
		list.add("kth2");
		savefileHandler.saveItemNames(list);
		List<String> result = savefileHandler.loadItemNameList();
		assertEquals(2, result.size());
		assertEquals("kth1", result.get(0));
		assertEquals("kth2", result.get(1));
	}

	@Test
	public void changeSavefileNameTest() {
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A2.yml");
		try {
			file.createNewFile();
			savefileHandler.changeSavefileName(UltimateEconomy.getInstance.getDataFolder(), "A3");
			File result = new File(UltimateEconomy.getInstance.getDataFolder(), "A2.yml");
			assertTrue(result.exists());
			result.delete();
		} catch (IOException | ShopSystemException e) {
			fail();
		}
	}

	@Test
	public void changeSavefileNameTestWithNotPossible() {
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A2.yml");
		try {
			file.createNewFile();
			savefileHandler.changeSavefileName(UltimateEconomy.getInstance.getDataFolder(), "A0");
			fail();
		} catch (IOException | ShopSystemException e) {
			assertTrue(e instanceof ShopSystemException);
			assertEquals("§cAn error occurred while renaming!", e.getMessage());
			file.delete();
		}
	}

	@Test
	public void loadStockTest() {
		savefileHandler.saveStock("kth", 6);
		assertEquals(6, savefileHandler.loadStock("kth"));
	}

	@Test
	public void loadRentableTest() {
		savefileHandler.saveRentable(false);
		assertFalse(savefileHandler.loadRentable());
	}

	@Test
	public void loadRentUntilTest() {
		savefileHandler.saveRentUntil(10L);
		assertEquals(10L, savefileHandler.loadRentUntil());
	}

	@Test
	public void loadRentalFeeTest() {
		savefileHandler.saveRentalFee(4.6);
		assertEquals("4.6", String.valueOf(savefileHandler.loadRentalFee()));
	}

	@Test
	public void loadOwnerTest() {
		EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
		savefileHandler.saveOwner(ecoPlayer);
		assertEquals("kthschnll", savefileHandler.loadOwner(null));
	}

	@Test
	public void loadOwnerTestWithOldConvert() {
		assertEquals("kthschnll", savefileHandler.loadOwner("kthschnll"));
		File file = new File(UltimateEconomy.getInstance.getDataFolder(), "A0.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("kthschnll", config.getString("Owner"));
	}

	@Test
	public void loadOwnerTestWithInvalidPlayer() {
		savefileHandler.loadOwner("myshop_catch441");
		// only logged
	}

	@Test
	public void loadItemNameListTestWithOldConvert() {
		List<String> list = new ArrayList<>();
		list.add("kth1");
		list.add("kth2");
		list.add("ANVIL_0");
		list.add("CRAFTING_TABLE_0");
		savefileHandler.saveItemNames(list);
		List<String> result = savefileHandler.loadItemNameList();
		assertEquals(2, result.size());
		assertEquals("kth1", result.get(0));
		assertEquals("kth2", result.get(1));
	}
}
