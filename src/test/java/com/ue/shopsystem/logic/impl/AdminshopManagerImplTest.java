package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.ServiceComponent;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.Adminshop;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;

@ExtendWith(MockitoExtension.class)
public class AdminshopManagerImplTest {

	@InjectMocks
	AdminshopManagerImpl adminshopManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ShopValidationHandler validationHandler;
	@Mock
	ServerProvider serverProvider;
	@Mock
	Logger logger;
	@Mock
	CustomSkullService skullService;
	@Mock
	ConfigDao configDao;
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@Test
	public void createNewAdminshopTestWithInvalidSize() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSize(5);
		assertThrows(GeneralEconomyException.class, () -> adminshopManager.createAdminShop("myshop", null, 5));
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createNewAdminshopTestWithExistingName() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValueNotInList(anyList(), eq("my_shop"));
		assertThrows(GeneralEconomyException.class, () -> adminshopManager.createAdminShop("my_shop", null, 5));
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createNewAdminshopTestWithInvalidName() throws ShopSystemException {
		doThrow(ShopSystemException.class).when(validationHandler).checkForValidShopName("my_shop");
		assertThrows(ShopSystemException.class, () -> adminshopManager.createAdminShop("my_shop", null, 5));
		assertEquals(0, adminshopManager.getAdminshopList().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createNewAdminshopTestSuccess() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), anyString())).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(skullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		assertDoesNotThrow(() -> adminshopManager.createAdminShop("myshop", loc, 9));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidShopName("myshop"));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSize(9));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValueNotInList(anyList(), eq("myshop")));
		verify(configDao).saveAdminshopIds(anyList());
		assertEquals(1, adminshopManager.getAdminshopList().size());
		Adminshop shop = adminshopManager.getAdminshopList().get(0);
		assertEquals("myshop", shop.getName());
		assertEquals("A0", shop.getShopId());
	}

	@Test
	public void deleteAdminshopTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), anyString())).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(skullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		assertDoesNotThrow(() -> adminshopManager.createAdminShop("myshop", loc, 9));
		Adminshop shop = adminshopManager.getAdminshopList().get(0);
		adminshopManager.deleteAdminShop(shop);
		// verify that the delete method of the shop is called
		verify(shopDao).deleteFile();
		verify(configDao).saveAdminshopIds(new ArrayList<>());
		assertEquals(0, adminshopManager.getAdminshopList().size());
	}

	@Test
	public void getAdminShopByNameFailTest() {
		try {
			createAdminshop();
			adminshopManager.getAdminShopByName("myshop2");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("myshop2", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void getAdminShopByNameTest() {
		createAdminshop();
		Adminshop shop = assertDoesNotThrow(() -> adminshopManager.getAdminShopByName("myshop"));
		assertNotNull(shop);
		assertEquals("myshop", shop.getName());
	}

	@Test
	public void getAdminshopByIdTest() {
		createAdminshop();
		Adminshop shop = assertDoesNotThrow(() -> adminshopManager.getAdminShopById("A0"));
		assertNotNull(shop);
		assertEquals("A0", shop.getShopId());
	}

	@Test
	public void getAdminshopByIdFailTest() {
		try {
			createAdminshop();
			adminshopManager.getAdminShopById("A1");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("A1", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void generateFreeAdminShopIdTest() {
		String id1 = adminshopManager.generateFreeAdminShopId();
		createAdminshop();
		String id2 = adminshopManager.generateFreeAdminShopId();
		assertEquals("A0", id1);
		assertEquals("A1", id2);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllAdminShopsTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopSize()).thenReturn(9);
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), anyString())).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(skullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		when(configDao.hasAdminShopNames()).thenReturn(false);
		when(configDao.loadAdminshopIds()).thenReturn(Arrays.asList("A0"));

		adminshopManager.loadAllAdminShops();
		assertEquals(1, adminshopManager.getAdminshopList().size());
		// to verify that the loading constructor of the shop is used
		verify(shopDao).loadShopName();
		verifyNoInteractions(logger);
	}

	@Test
	public void despawnAllVillagersTest() {
		createAdminshop();
		createAdminshop();
		Adminshop shop1 = assertDoesNotThrow(() -> adminshopManager.getAdminshopList().get(0));
		Adminshop shop2 = assertDoesNotThrow(() -> adminshopManager.getAdminshopList().get(0));
		adminshopManager.despawnAllVillagers();
		verify(shop1.getShopVillager()).remove();
		verify(shop2.getShopVillager()).remove();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllAdminShopsTestWithLoadingError() throws TownSystemException {
		Location loc = mock(Location.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopSize()).thenReturn(9);
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.hasAdminShopNames()).thenReturn(false);
		when(configDao.loadAdminshopIds()).thenReturn(Arrays.asList("A0"));
		when(shopDao.loadShopLocation()).thenThrow(e);
		adminshopManager.loadAllAdminShops();
		assertEquals(0, adminshopManager.getAdminshopList().size());
		// to verify that the loading constructor of the shop is used
		verify(shopDao).loadShopName();
		verify(logger).warn("[Ultimate_Economy] Failed to load the shop A0");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
		verifyNoMoreInteractions(logger);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllAdminshopsOldTest() {
		Plugin plugin = mock(Plugin.class);
		JavaPlugin javaPlugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopSize()).thenReturn(9);
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), anyString())).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(serverProvider.getJavaPluginInstance()).thenReturn(javaPlugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(skullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		when(configDao.hasAdminShopNames()).thenReturn(true);
		when(configDao.loadAdminShopNames()).thenReturn(Arrays.asList("myshop"));
		adminshopManager.loadAllAdminShops();
		assertEquals(1, adminshopManager.getAdminshopList().size());
		assertEquals("A0", adminshopManager.getAdminshopList().get(0).getShopId());
		assertEquals("myshop", adminshopManager.getAdminshopList().get(0).getName());
		// to verify that the loading constructor of the shop is used
		verify(shopDao).loadShopName();
		verify(configDao).removeDeprecatedAdminshopNames();
		verify(configDao).saveAdminshopIds(anyList());
		verifyNoInteractions(logger);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadAllAdminshopsOldTestWithLoadingError() throws TownSystemException {
		Plugin plugin = mock(Plugin.class);
		Location loc = mock(Location.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopSize()).thenReturn(9);
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.hasAdminShopNames()).thenReturn(true);
		when(configDao.loadAdminShopNames()).thenReturn(Arrays.asList("myshop"));
		when(shopDao.loadShopLocation()).thenThrow(e);
		adminshopManager.loadAllAdminShops();
		assertEquals(0, adminshopManager.getAdminshopList().size());
		// to verify that the loading constructor of the shop is used
		verify(shopDao).loadShopName();
		verify(logger).warn("[Ultimate_Economy] Failed to load the shop myshop");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
		verifyNoMoreInteractions(logger);
		verify(configDao).removeDeprecatedAdminshopNames();
		verify(configDao).saveAdminshopIds(anyList());
	}

	private void createAdminshop() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), anyString())).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(skullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		assertDoesNotThrow(() -> adminshopManager.createAdminShop("myshop", loc, 9));
	}
}
