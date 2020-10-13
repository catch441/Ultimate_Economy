package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.utils.ComponentProvider;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.ServiceComponent;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.shopsystem.logic.api.PlayershopManager;
import com.ue.shopsystem.logic.api.Rentshop;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.townsystem.logic.impl.TownSystemException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

@ExtendWith(MockitoExtension.class)
public class RentshopManagerImplTest {

	@InjectMocks
	RentshopManagerImpl rentshopManager;
	@Mock
	ShopValidationHandler validationHandler;
	@Mock
	ServerProvider serverProvider;
	@Mock
	ComponentProvider componentProvider;
	@Mock
	Logger logger;
	@Mock
	CustomSkullService skullService;
	@Mock
	ConfigDao configDao;
	@Mock
	PlayershopManager playershopManager;
	
	@Test
	public void setupRentDailyTaskTest() {
		// TODO dont know how to test
	}

	@Test
	public void generateFreeRentShopIdTest() {
		String id1 = rentshopManager.generateFreeRentShopId();
		createRentshop();
		String id2 = rentshopManager.generateFreeRentShopId();
		assertEquals("R0", id1);
		assertEquals("R1", id2);
	}

	private Rentshop createRentshop() {
		Plugin plugin = mock(Plugin.class);
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
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(skullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);

		return assertDoesNotThrow(() -> rentshopManager.createRentShop(loc, 9, 2.5));
	}

	@Test
	public void getRentShopByIdTest() {
		createRentshop();
		Rentshop shop = assertDoesNotThrow(() -> rentshopManager.getRentShopById("R0"));
		assertNotNull(shop);
		assertEquals("R0", shop.getShopId());
	}

	@Test
	public void getRentShopByIdTestFail() {
		try {
			createRentshop();
			rentshopManager.getRentShopById("R1");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("R1", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void getRentShopUniqueNameListTest() {
		createRentshop();
		Rentshop shop = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> shop.rentShop(ecoPlayer, 1));
		List<String> list = rentshopManager.getRentShopUniqueNameList();
		assertEquals(2, list.size());
		assertEquals("RentShop#R0", list.get(0));
		assertEquals("Shop#R1_catch441", list.get(1));
	}

	@Test
	public void getRentShopByUniqueNameTestWithNotRented() {
		createRentshop();
		Rentshop shop = assertDoesNotThrow(() -> rentshopManager.getRentShopByUniqueName("RentShop#R0"));
		assertEquals("R0", shop.getShopId());
		assertEquals("RentShop#R0", shop.getName());
	}

	@Test
	public void getRentShopByUniqueNameTestWithRented() {
		Rentshop shop = createRentshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		assertDoesNotThrow(() -> shop.rentShop(ecoPlayer, 1));
		Rentshop result = assertDoesNotThrow(() -> rentshopManager.getRentShopByUniqueName("Shop#R0_catch441"));
		assertEquals("R0", result.getShopId());
		assertEquals("Shop#R0", result.getName());
	}

	@Test
	public void getRentShopByUniqueNameTestWithoutShop() {
		try {
			createRentshop();
			rentshopManager.getRentShopByUniqueName("RentShop#R1");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("RentShop#R1", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void getRentShopsTest() {
		Rentshop shop = createRentshop();
		Rentshop shop2 = createRentshop();
		List<Rentshop> shops = rentshopManager.getRentShops();
		assertEquals(shop, shops.get(0));
		assertEquals(shop2, shops.get(1));
	}

	@Test
	public void despawnAllVillagersTest() {
		Rentshop shop = createRentshop();
		Rentshop shop2 = createRentshop();
		rentshopManager.despawnAllVillagers();
		verify(shop.getShopVillager()).remove();
		verify(shop2.getShopVillager()).remove();
	}

	@Test
	public void deleteRentshopTest() {
		Plugin plugin = mock(Plugin.class);
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
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(skullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		Rentshop shop = assertDoesNotThrow(() -> rentshopManager.createRentShop(loc, 9, 2.5));
		
		rentshopManager.deleteRentShop(shop);
		// verify that the delete method of the shop is called
		verify(shopDao).deleteFile();
		verify(configDao).saveRentshopIds(new ArrayList<>());
		assertEquals(0, rentshopManager.getRentShops().size());
	}

	@Test
	public void createRentshopTestWithInvalidSize() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidSize(5);
		assertThrows(GeneralEconomyException.class, () -> rentshopManager.createRentShop(null, 5, 2.5));
		assertEquals(0, rentshopManager.getRentShops().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createRentshopTestWithInvalidRentalFee() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForPositiveValue(-2.5);
		assertThrows(GeneralEconomyException.class, () -> rentshopManager.createRentShop(null, 9, -2.5));
		assertEquals(0, rentshopManager.getRentShops().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createRentshopTest() {
		Plugin plugin = mock(Plugin.class);
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
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(skullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		assertDoesNotThrow(() -> rentshopManager.createRentShop(loc, 9, 2.5));
		
		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(2.5));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSize(9));
		verify(configDao).saveRentshopIds(anyList());
		assertEquals(1, rentshopManager.getRentShops().size());
		Rentshop shop = rentshopManager.getRentShops().get(0);
		assertEquals("RentShop#R0", shop.getName());
		assertEquals("R0", shop.getShopId());
		assertEquals(2.5, shop.getRentalFee());
	}

	@Test
	public void loadAllRentshopsTest() {
		Plugin plugin = mock(Plugin.class);
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
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(skullService.getSkullWithName(anyString(), anyString())).thenReturn(infoItem);
		when(configDao.loadRentshopIds()).thenReturn(Arrays.asList("R0"));

		rentshopManager.loadAllRentShops();
		assertEquals(1, rentshopManager.getRentShops().size());
		// to verify that the loading constructor of the shop is used
		verify(shopDao).loadShopName();
		verifyNoInteractions(logger);
	}

	@Test
	public void loadAllRentshopsTestWithLoadingError() throws TownSystemException {
		Location loc = mock(Location.class);
		ShopDao shopDao = mock(ShopDao.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		TownSystemException e = mock(TownSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopSize()).thenReturn(9);
		when(shopDao.loadShopName()).thenReturn("myshop");
		when(serviceComponent.getShopDao()).thenReturn(shopDao);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.loadRentshopIds()).thenReturn(Arrays.asList("R0"));
		when(shopDao.loadShopLocation()).thenThrow(e);
		rentshopManager.loadAllRentShops();
		assertEquals(0, rentshopManager.getRentShops().size());
		// to verify that the loading constructor of the shop is used
		verify(shopDao).loadShopName();
		verify(logger).warn("[Ultimate_Economy] Failed to load the shop R0");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
		verifyNoMoreInteractions(logger);
	}
}
