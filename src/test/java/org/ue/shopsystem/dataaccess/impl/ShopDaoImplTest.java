package org.ue.shopsystem.dataaccess.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.impl.ShopItemImpl;

@ExtendWith(MockitoExtension.class)
public class ShopDaoImplTest {

	@InjectMocks
	ShopDaoImpl shopDao;
	@Mock
	ServerProvider serverProvider;

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
		shopDao.saveShopName("catch");
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("catch", config.getString("ShopName"));
	}

	@Test
	public void saveShopItemTest() {
		ItemStack stack = mock(ItemStack.class);
		ShopItemImpl item = mock(ShopItemImpl.class);
		when(item.getItemStack()).thenReturn(stack);
		Map<String, Object> map = new HashMap<>();
		map.put("==", "org.bukkit.inventory.ItemStack");
		map.put("v", 1976);
		map.put("type", "STONE");
		when(stack.serialize()).thenReturn(map);
		when(item.getSlot()).thenReturn(4);
		when(item.getAmount()).thenReturn(1);
		when(item.getSellPrice()).thenReturn(2.0);
		when(item.getBuyPrice()).thenReturn(3.0);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopItem(item, false);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("2.0", config.getString("ShopItems.4.sellPrice"));
		assertEquals("3.0", config.getString("ShopItems.4.buyPrice"));
		assertEquals("1", config.getString("ShopItems.4.Amount"));
	}

	@Test
	public void saveShopItemTestWitDelete() {
		ItemStack stack = mock(ItemStack.class);
		ShopItemImpl item = mock(ShopItemImpl.class);
		when(item.getItemStack()).thenReturn(stack);
		Map<String, Object> map = new HashMap<>();
		map.put("==", "org.bukkit.inventory.ItemStack");
		map.put("v", 1976);
		map.put("type", "STONE");
		when(stack.serialize()).thenReturn(map);
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
		assertNull(config.getString("ShopItems.4.Name"));
		assertNull(config.getString("ShopItems.4.Slot"));
		assertNull(config.getString("ShopItems.4.newSaveMethod"));
		assertNull(config.getString("ShopItems.4.sellPrice"));
		assertNull(config.getString("ShopItems.4.buyPrice"));
		assertNull(config.getString("ShopItems.4.Amount"));
	}

	@Test
	public void saveStockTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveStock(5432423, 6);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("6", config.getString("ShopItems.5432423.stock"));
	}

	@Test
	public void saveRentUntilTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveExpiresAt(10L);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("10", config.getString("expiresAt"));
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
		when(ecoPlayer.getName()).thenReturn("catch");
		shopDao.setupSavefile("A1");
		shopDao.saveOwner(ecoPlayer);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertEquals("catch", config.getString("Owner"));
	}

	@Test
	public void saveOwnerTestWithDelete() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch");
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveOwner(ecoPlayer);
		shopDao.saveOwner(null);
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		assertNull(config.getString("Owner"));
	}

	@Test
	public void loadShopNameTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopName("stuff");
		assertEquals("stuff", shopDao.loadShopName());
	}

	@Test
	public void loadItemTest() {
		ItemStack stack = mock(ItemStack.class);
		ShopItemImpl item = mock(ShopItemImpl.class);
		when(item.getItemStack()).thenReturn(stack);
		Map<String, Object> map = new HashMap<>();
		map.put("==", "org.bukkit.inventory.ItemStack");
		map.put("v", 1976);
		map.put("type", "STONE");
		when(stack.serialize()).thenReturn(map);
		when(item.getSlot()).thenReturn(4);
		when(item.getAmount()).thenReturn(1);
		when(item.getSellPrice()).thenReturn(2.0);
		when(item.getBuyPrice()).thenReturn(3.0);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		shopDao.saveShopItem(item, false);
		ShopItem result = shopDao.loadItem(4);
		assertEquals(4, result.getSlot());
		assertEquals("2.0", String.valueOf(result.getSellPrice()));
		assertEquals("3.0", String.valueOf(result.getBuyPrice()));
		assertEquals(1, result.getAmount());

		// cannot be tested
		// assertEquals("ItemStack{STONE x 1}", result.getItemString());
		// assertNotNull(result.getItemStack());
	}

	@Test
	public void loadItemTestWithSpawnerConvert() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");

		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopItems.4.Name", "SPAWNER_PIG");
		config.set("ShopItems.4.newSaveMethod", true);
		assertDoesNotThrow(() -> config.save(file));
		
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Map<String, Object> map = new HashMap<>();
		map.put("==", "org.bukkit.inventory.ItemStack");
		map.put("v", 1976);
		map.put("type", "SPAWNER");
		when(stack.serialize()).thenReturn(map);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.SPAWNER, 1)).thenReturn(stack);
		shopDao.setupSavefile("A1");
		shopDao.loadItem(4);
		
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertFalse(config2.contains("ShopItems.4.newSaveMethod"));
		verify(meta).setDisplayName("PIG");
		verify(stack).setItemMeta(meta);
	}

	@Test
	public void loadItemSlotListTest() {
		ItemStack stack = mock(ItemStack.class);
		ShopItemImpl item = mock(ShopItemImpl.class);
		when(item.getItemStack()).thenReturn(stack);
		Map<String, Object> map = new HashMap<>();
		map.put("==", "org.bukkit.inventory.ItemStack");
		map.put("v", 1976);
		map.put("type", "STONE");
		when(stack.serialize()).thenReturn(map);
		when(item.getSlot()).thenReturn(4);
		when(item.getAmount()).thenReturn(1);
		when(item.getSellPrice()).thenReturn(2.0);
		when(item.getBuyPrice()).thenReturn(3.0);
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		assertEquals(0, shopDao.loadItemSlotList().size());
		shopDao.saveShopItem(item, false);
		List<Integer> result = shopDao.loadItemSlotList();
		assertEquals(1, result.size());
		assertEquals(4, result.get(0));
	}

	@Test
	public void loadItemHashListTestWithConvertToSlot() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");

		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopItems.itemString.name", "stuff");
		config.set("ShopItems.itemString.Slot", 4);
		config.set("ShopItems.itemString.name.lore", Arrays.asList("lorestuff"));
		assertDoesNotThrow(() -> config.save(file));

		shopDao.setupSavefile("A1");
		List<Integer> result = shopDao.loadItemSlotList();
		assertEquals(1, result.size());
		assertEquals(4, result.get(0));
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
		shopDao.saveStock(46464564, 6);
		assertEquals(6, shopDao.loadStock(46464564));
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
		shopDao.saveExpiresAt(10L);
		assertEquals(10L, shopDao.loadExpiresAt());
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
		when(ecoPlayer.getName()).thenReturn("catch");
		shopDao.setupSavefile("A1");
		shopDao.saveOwner(ecoPlayer);
		assertEquals("catch", shopDao.loadOwner());
	}

	@Test
	public void loadItemSlotListTestWithOldConvert() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		List<String> list = new ArrayList<>();
		list.add("stuff1");
		list.add("stuff2");
		list.add("ANVIL_0");
		list.add("CRAFTING_TABLE_0");
		File file = new File("src/A1.yml");
		YamlConfiguration config1 = YamlConfiguration.loadConfiguration(file);
		config1.set("ShopItemList", list);
		assertDoesNotThrow(() -> config1.save(file));
		shopDao.setupSavefile("A1");
		List<Integer> result = shopDao.loadItemSlotList();
		assertEquals(0, result.size());
		YamlConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		assertFalse(config2.contains("ShopItemList"));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void removeIfCorruptedTestFalse() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopItems.46464564.Name", "value");
		assertDoesNotThrow(() -> config.save(file));
		shopDao.setupSavefile("A1");
		assertFalse(shopDao.removeIfCorrupted(46464564));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void removeIfCorruptedTestTrue() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		shopDao.setupSavefile("A1");
		assertTrue(shopDao.removeIfCorrupted(46464564));
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config = YamlConfiguration.loadConfiguration(file);
		assertFalse(config.isSet("ShopItems.46464564.Name"));
	}

	@Test
	public void convertToIngameTimeTest() {
		when(serverProvider.getDataFolderPath()).thenReturn("src");
		when(serverProvider.getSystemTime()).thenReturn(33600000L);
		when(serverProvider.getWorldTime()).thenReturn(12000L);
		shopDao.setupSavefile("A1");
		File file = new File("src/A1.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("RentUntil", 120000000L);
		assertDoesNotThrow(() -> config.save(file));
		shopDao.setupSavefile("A1");
		assertEquals(1740000L, shopDao.loadExpiresAt());
		assertFalse(YamlConfiguration.loadConfiguration(file).isSet("RentUntil"));
	}
}
