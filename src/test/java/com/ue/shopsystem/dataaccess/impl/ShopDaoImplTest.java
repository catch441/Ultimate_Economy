package com.ue.shopsystem.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.ServerProvider;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.impl.ShopSystemException;
import com.ue.shopsystem.logic.to.ShopItem;
import com.ue.townsystem.logic.api.TownsystemValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;

@ExtendWith(MockitoExtension.class)
public class ShopDaoImplTest {

	@InjectMocks
	ShopDaoImpl shopDao;
	@Mock
	ServerProvider serverProvider;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	ShopValidationHandler validationHandler;
	@Mock
	TownsystemValidationHandler townsystemValidationHandler;
	@Mock
	Logger logger;

	/**
	 * Deletes the savefile.
	 */
	@AfterEach
	public void cleanUp() {
		File file = new File("src/A1.yml");
		file.delete();
	}

	@Test
	public void setupSavefileTest() {
		File result = new File("src/A1.yml");
		assertFalse(result.exists());
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		assertTrue(result.exists());
	}

	@Test
	public void setupSavefileLoadTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopName("myshop");
		;
		shopDao.setupSavefile("A1");
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertTrue(file.exists());
		assertTrue(config.isSet("ShopName"));
	}

	@Test
	public void saveShopNameTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopName("kthschnll");
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("kthschnll", config.getString("ShopName"));
	}

	@Test
	public void saveShopItemTest() {
		ItemStack stack = mock(ItemStack.class);
		ShopItem item = mock(ShopItem.class);
		when(item.getItemStack()).thenReturn(stack);
		Map<String, Object> map = new HashMap<>();
		map.put("==", "org.bukkit.inventory.ItemStack");
		map.put("v", 1976);
		map.put("type", "STONE");
		when(stack.serialize()).thenReturn(map);
		when(item.getItemString()).thenReturn("ItemStack{STONE x 1}");
		when(item.getSlot()).thenReturn(4);
		when(item.getAmount()).thenReturn(1);
		when(item.getSellPrice()).thenReturn(2.0);
		when(item.getBuyPrice()).thenReturn(3.0);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopItem(item, false);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("4", config.getString("ShopItems.ItemStack{STONE x 1}.Slot"));
		assertEquals("true", config.getString("ShopItems.ItemStack{STONE x 1}.newSaveMethod"));
		assertEquals("2.0", config.getString("ShopItems.ItemStack{STONE x 1}.sellPrice"));
		assertEquals("3.0", config.getString("ShopItems.ItemStack{STONE x 1}.buyPrice"));
		assertEquals("1", config.getString("ShopItems.ItemStack{STONE x 1}.Amount"));
	}

	@Test
	public void saveShopItemTestWithSpawner() {
		ItemStack stack = mock(ItemStack.class);
		ShopItem item = mock(ShopItem.class);
		when(item.getItemStack()).thenReturn(stack);
		when(item.getItemString()).thenReturn("SPAWNER_COW");
		when(item.getSlot()).thenReturn(4);
		when(item.getAmount()).thenReturn(1);
		when(item.getSellPrice()).thenReturn(2.0);
		when(item.getBuyPrice()).thenReturn(3.0);
		when(stack.getType()).thenReturn(Material.SPAWNER);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopItem(item, false);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("4", config.getString("ShopItems.SPAWNER_COW.Slot"));
		assertEquals("true", config.getString("ShopItems.SPAWNER_COW.newSaveMethod"));
		assertEquals("2.0", config.getString("ShopItems.SPAWNER_COW.sellPrice"));
		assertEquals("3.0", config.getString("ShopItems.SPAWNER_COW.buyPrice"));
		assertEquals("1", config.getString("ShopItems.SPAWNER_COW.Amount"));
		assertEquals("SPAWNER_COW", config.getString("ShopItems.SPAWNER_COW.Name"));

	}

	@Test
	public void saveShopItemTestWitDelete() {
		ItemStack stack = mock(ItemStack.class);
		ShopItem item = mock(ShopItem.class);
		when(item.getItemStack()).thenReturn(stack);
		Map<String, Object> map = new HashMap<>();
		map.put("==", "org.bukkit.inventory.ItemStack");
		map.put("v", 1976);
		map.put("type", "STONE");
		when(stack.serialize()).thenReturn(map);
		when(item.getItemString()).thenReturn("ItemStack{STONE x 1}");
		when(item.getSlot()).thenReturn(4);
		when(item.getAmount()).thenReturn(1);
		when(item.getSellPrice()).thenReturn(2.0);
		when(item.getBuyPrice()).thenReturn(3.0);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopItem(item, false);
		shopDao.saveShopItem(item, true);
		File file = new File("src/A1.yml");
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
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopSize(10);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("10", config.getString("ShopSize"));
	}

	@Test
	public void saveProfessionTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveProfession(Profession.ARMORER);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("ARMORER", config.getString("Profession"));
	}

	@Test
	public void saveStockTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveStock("kth", 6);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("6", config.getString("ShopItems.kth.stock"));
	}

	@Test
	public void saveRentUntilTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveRentUntil(10L);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("10", config.getString("RentUntil"));
	}

	@Test
	public void saveShopLocationTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		Location loc = new Location(world, 1, 2, 3);
		shopDao.saveShopLocation(loc);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("World", config.getString("ShopLocation.World"));
		assertEquals("1.0", config.getString("ShopLocation.x"));
		assertEquals("2.0", config.getString("ShopLocation.y"));
		assertEquals("3.0", config.getString("ShopLocation.z"));
	}

	@Test
	public void saveRentalFeeTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveRentalFee(2.5);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.5", config.getString("RentalFee"));
	}

	@Test
	public void saveRentableTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveRentable(true);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("true", config.getString("Rentable"));
	}

	@Test
	public void saveOwnerTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		when(ecoPlayer.getName()).thenReturn("kthschnll");
		shopDao.setupSavefile("A1");
		shopDao.saveOwner(ecoPlayer);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("kthschnll", config.getString("Owner"));
	}

	@Test
	public void saveOwnerTestWithDelete() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("kthschnll");
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveOwner(ecoPlayer);
		shopDao.saveOwner(null);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertNull(config.getString("Owner"));
	}

	@Test
	public void loadShopSizeTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopSize(10);
		assertEquals(10, shopDao.loadShopSize());
	}

	@Test
	public void loadShopNameTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopName("kth");
		assertEquals("kth", shopDao.loadShopName());
	}

	@Test
	public void loadShopVillagerProfessionTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveProfession(Profession.ARMORER);
		assertEquals(Profession.ARMORER, shopDao.loadShopVillagerProfession());
	}

	@Test
	public void loadShopVillagerProfessionTestWithNitwit() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		assertEquals(Profession.NITWIT, shopDao.loadShopVillagerProfession());
	}

	@Test
	public void loadShopLocationTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		Location loc = new Location(world, 1, 2, 3);
		shopDao.saveShopLocation(loc);
		when(serverProvider.getWorld("World")).thenReturn(world);
		Location loaded = assertDoesNotThrow(() -> shopDao.loadShopLocation());
		assertEquals("1.0", String.valueOf(loaded.getX()));
		assertEquals("2.0", String.valueOf(loaded.getY()));
		assertEquals("3.0", String.valueOf(loaded.getZ()));
		assertEquals(world, loaded.getWorld());
		assertDoesNotThrow(() -> verify(townsystemValidationHandler).checkForWorldExists("World"));
	}

	@Test
	public void loadShopLocationTestWithInvalidWorld() throws TownSystemException {
		doThrow(TownSystemException.class).when(townsystemValidationHandler).checkForWorldExists("World");
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		World world = mock(World.class);
		when(world.getName()).thenReturn("World");
		Location loc = new Location(world, 1, 2, 3);
		shopDao.saveShopLocation(loc);
		assertThrows(TownSystemException.class, () -> shopDao.loadShopLocation());
	}

	@Test
	public void loadItemTest() {
		ItemStack stack = mock(ItemStack.class);
		ShopItem item = mock(ShopItem.class);
		when(item.getItemStack()).thenReturn(stack);
		Map<String, Object> map = new HashMap<>();
		map.put("==", "org.bukkit.inventory.ItemStack");
		map.put("v", 1976);
		map.put("type", "STONE");
		when(stack.serialize()).thenReturn(map);
		when(item.getItemString()).thenReturn("ItemStack{STONE x 1}");
		when(item.getSlot()).thenReturn(4);
		when(item.getAmount()).thenReturn(1);
		when(item.getSellPrice()).thenReturn(2.0);
		when(item.getBuyPrice()).thenReturn(3.0);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopItem(item, false);
		ShopItem result = shopDao.loadItem("ItemStack{STONE x 1}");
		assertEquals(4, result.getSlot());
		assertEquals("2.0", String.valueOf(result.getSellPrice()));
		assertEquals("3.0", String.valueOf(result.getBuyPrice()));
		assertEquals(1, result.getAmount());
		
		//cannot be tested
		//assertEquals("ItemStack{STONE x 1}", result.getItemString());
		//assertNotNull(result.getItemStack());
	}

	@Test
	public void loadItemTestWithSpawner() {
		ItemStack stack = mock(ItemStack.class);
		ShopItem item = mock(ShopItem.class);
		when(item.getItemStack()).thenReturn(stack);
		when(item.getItemString()).thenReturn("SPAWNER_PIG");
		when(item.getSlot()).thenReturn(4);
		when(item.getAmount()).thenReturn(1);
		when(item.getSellPrice()).thenReturn(2.0);
		when(item.getBuyPrice()).thenReturn(3.0);
		when(stack.getType()).thenReturn(Material.SPAWNER);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopItem(item, false);
		ItemStack stackResult = mock(ItemStack.class);
		ItemStack stackResultCopy = mock(ItemStack.class);
		ItemStack stackResultCopyClone = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(serverProvider.createItemStack(Material.SPAWNER, 1)).thenReturn(stackResult);
		when(stackResult.getItemMeta()).thenReturn(meta);
		when(stackResult.clone()).thenReturn(stackResultCopy);
		when(stackResultCopy.clone()).thenReturn(stackResultCopyClone);
		when(stackResult.getType()).thenReturn(Material.SPAWNER);
		when(meta.getDisplayName()).thenReturn("PIG");
		ShopItem result = shopDao.loadItem("SPAWNER_PIG");
		verify(meta).setDisplayName("PIG");
		assertEquals("SPAWNER_PIG", result.getItemString());
		assertEquals(4, result.getSlot());
		assertEquals("2.0", String.valueOf(result.getSellPrice()));
		assertEquals("3.0", String.valueOf(result.getBuyPrice()));
		assertEquals(1, result.getAmount());
		assertEquals(stackResultCopyClone, result.getItemStack());
	}

	@Test
	public void loadItemNameListTest() {
		ItemStack stack = mock(ItemStack.class);
		ShopItem item = mock(ShopItem.class);
		when(item.getItemStack()).thenReturn(stack);
		Map<String, Object> map = new HashMap<>();
		map.put("==", "org.bukkit.inventory.ItemStack");
		map.put("v", 1976);
		map.put("type", "STONE");
		when(stack.serialize()).thenReturn(map);
		when(item.getItemString()).thenReturn("ItemStack{STONE x 1}");
		when(item.getSlot()).thenReturn(4);
		when(item.getAmount()).thenReturn(1);
		when(item.getSellPrice()).thenReturn(2.0);
		when(item.getBuyPrice()).thenReturn(3.0);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		assertEquals(0, shopDao.loadItemNameList().size());
		shopDao.saveShopItem(item, false);
		List<String> result = shopDao.loadItemNameList();
		assertEquals(1, result.size());
		assertEquals("ItemStack{STONE x 1}", result.get(0));
	}

	@Test
	public void changeSavefileNameTest() {
		shopDao.setupSavefile("A2");
		assertDoesNotThrow(() -> shopDao.changeSavefileName(new File("src"), "A3"));
		File result = new File("src/A2.yml");
		assertFalse(result.exists());
		File result2 = new File("src/A3.yml");
		assertTrue(result2.exists());
		assertDoesNotThrow(() -> verify(validationHandler).checkForRenamingSavefileIsPossible(any(File.class)));
		result2.delete();
		result.delete();
	}

	@Test
	public void changeSavefileNameTestWithNotPossible() throws ShopSystemException {
		doThrow(ShopSystemException.class).when(validationHandler).checkForRenamingSavefileIsPossible(any());
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A2");
		assertThrows(ShopSystemException.class, () -> shopDao.changeSavefileName(new File("src"), "A3"));
		File result = new File("src/A2.yml");
		assertTrue(result.exists());
		File result2 = new File("src/A3.yml");
		assertFalse(result2.exists());
		result2.delete();
		result.delete();
	}
	
	@Test
	public void deleteSavefileTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.deleteFile();
		File result = new File("src/A1.yml");
		assertFalse(result.exists());
	}

	@Test
	public void loadStockTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveStock("kth", 6);
		assertEquals(6, shopDao.loadStock("kth"));
	}

	@Test
	public void loadRentableTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveRentable(false);
		assertFalse(shopDao.loadRentable());
	}

	@Test
	public void loadRentUntilTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveRentUntil(10L);
		assertEquals(10L, shopDao.loadRentUntil());
	}

	@Test
	public void loadRentalFeeTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveRentalFee(4.6);
		assertEquals("4.6", String.valueOf(shopDao.loadRentalFee()));
	}

	@Test
	public void loadOwnerTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		when(ecoPlayer.getName()).thenReturn("kthschnll");
		shopDao.setupSavefile("A1");
		shopDao.saveOwner(ecoPlayer);
		assertEquals("kthschnll", shopDao.loadOwner(null));
	}

	@Test
	public void loadOwnerTestWithOldConvert() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		when(ecoPlayer.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenReturn(ecoPlayer));
		assertEquals("catch441", shopDao.loadOwner("myshop_catch441"));
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("catch441", config.getString("Owner"));
	}

	@Test
	public void loadOwnerTestWithInvalidPlayer() throws GeneralEconomyException {
		GeneralEconomyException e = mock(GeneralEconomyException.class);
		when(e.getMessage()).thenReturn("my error message");
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		when(ecoPlayerManager.getEconomyPlayerByName("catch441")).thenThrow(e);
		shopDao.setupSavefile("A1");
		shopDao.loadOwner("myshop_catch441");
		verify(logger).warn("[Ultimate_Economy] Error on save config to file");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
	}

	@Test
	public void loadItemNameListTestWithOldConvert() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		List<String> list = new ArrayList<>();
		list.add("kth1");
		list.add("kth2");
		list.add("ANVIL_0");
		list.add("CRAFTING_TABLE_0");
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopItemList", list);
		assertDoesNotThrow(() -> config.save(file));
		shopDao.setupSavefile("A1");
		List<String> result = shopDao.loadItemNameList();
		assertEquals(0, result.size());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void removeIfCorruptedTestFalse() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopItems.catch.Name", "value");
		assertDoesNotThrow(() -> config.save(file));
		shopDao.setupSavefile("A1");
		assertFalse(shopDao.removeIfCorrupted("catch"));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void removeIfCorruptedTestTrue() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		assertTrue(shopDao.removeIfCorrupted("catch"));
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config = YamlConfiguration.loadConfiguration(file);
		assertFalse(config.isSet("ShopItems.catch.Name"));
	}
}
