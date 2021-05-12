package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.ServiceComponent;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.shopsystem.logic.api.Playershop;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;

@ExtendWith(MockitoExtension.class)
public class PlayershopManagerImplTest {

	@InjectMocks
	PlayershopManagerImpl playershopManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ShopValidator validationHandler;
	@Mock
	ConfigDao configDao;
	@Mock
	ServerProvider serverProvider;

	@Test
	public void createNewPlayershopTestWithInvalidSize() throws ShopsystemException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(ShopsystemException.class).when(validationHandler).checkForValidSize(5);
		assertThrows(ShopsystemException.class, () -> playershopManager.createPlayerShop("myshop", loc, 5, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewPlayershopTestWithExistingName() throws ShopsystemException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(ShopsystemException.class).when(validationHandler).checkForShopNameIsFree(anyList(), eq("myshop"),
				eq(ecoPlayer));
		assertThrows(ShopsystemException.class, () -> playershopManager.createPlayerShop("myshop", loc, 5, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewPlayershopTestWithInvalidName() throws ShopsystemException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(ShopsystemException.class).when(validationHandler).checkForValidShopName("myshop_");
		assertThrows(ShopsystemException.class, () -> playershopManager.createPlayerShop("myshop_", loc, 9, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewAdminshopTestWithMaxShopsReached() throws ShopsystemException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(ShopsystemException.class).when(validationHandler).checkForMaxPlayershopsForPlayer(anyList(),
				eq(ecoPlayer));
		assertThrows(ShopsystemException.class, () -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewPlayershopTestWithNoPlotPermission() throws ShopsystemException {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(ShopsystemException.class).when(validationHandler).checkForPlayerHasPermissionAtLocation(loc, ecoPlayer);
		assertThrows(ShopsystemException.class, () -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao, never()).savePlayershopIds(anyList());
	}

	@Test
	public void createNewPlayershopTest() {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Playershop shop = mock(Playershop.class);
		when(shop.getShopId()).thenReturn("P0");
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));

		assertDoesNotThrow(() -> verify(validationHandler).checkForValidShopName("myshop"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForMaxPlayershopsForPlayer(anyList(), eq(ecoPlayer)));
		assertDoesNotThrow(
				() -> verify(validationHandler).checkForShopNameIsFree(anyList(), eq("myshop"), eq(ecoPlayer)));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSize(9));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerHasPermissionAtLocation(loc, ecoPlayer));
		assertEquals(1, playershopManager.getPlayerShops().size());
		Playershop result = playershopManager.getPlayerShops().get(0);
		assertEquals(shop, result);
		verify(shop).setupNew("myshop", ecoPlayer, "P0", loc, 9);
		verify(configDao).savePlayershopIds(Arrays.asList("P0"));
	}

	@Test
	public void generateFreePlayerShopIdTest() {
		String id1 = playershopManager.generateFreePlayerShopId();
		createPlayershop("P0");
		String id2 = playershopManager.generateFreePlayerShopId();
		assertEquals("P0", id1);
		assertEquals("P1", id2);
	}

	private Playershop createPlayershop(String id) {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Playershop shop = mock(Playershop.class);
		when(shop.getShopId()).thenReturn(id);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		return shop;
	}

	@Test
	public void getPlayerShopUniqueNameListTest() {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Playershop shop = mock(Playershop.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		when(shop.getOwner()).thenReturn(ecoPlayer);
		when(shop.getName()).thenReturn("myshop");
		when(ecoPlayer.getName()).thenReturn("catch441");
		List<String> list = playershopManager.getPlayerShopUniqueNameList();
		assertEquals(1, list.size());
		assertEquals("myshop_catch441", list.get(0));
	}

	@Test
	public void getPlayerShopByUniqueNameTest() {
		Location loc = mock(Location.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Playershop shop = mock(Playershop.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> playershopManager.createPlayerShop("myshop", loc, 9, ecoPlayer));
		when(ecoPlayer.getName()).thenReturn("catch441");
		when(shop.getName()).thenReturn("myshop");
		when(shop.getOwner()).thenReturn(ecoPlayer);
		Playershop result = assertDoesNotThrow(() -> playershopManager.getPlayerShopByUniqueName("myshop_catch441"));
		assertNotNull(result);
		assertEquals(shop, result);
	}

	@Test
	public void getPlayerShopByUniqueNameTestWithNoShop() {
		ShopsystemException exception = assertThrows(ShopsystemException.class,
				() -> playershopManager.getPlayerShopByUniqueName("myshop_catch441"));
		assertEquals(ExceptionMessageEnum.DOES_NOT_EXIST, exception.getKey());
		assertEquals(1, exception.getParams().length);
		assertEquals("myshop_catch441", exception.getParams()[0]);
	}

	@Test
	public void getPlayerShopByIdTest() {
		Playershop shop1 = createPlayershop("P0");
		when(shop1.getOwner()).thenReturn(mock(EconomyPlayer.class));
		createPlayershop("P1");
		Playershop shop = assertDoesNotThrow(() -> playershopManager.getPlayerShopById("P1"));
		assertNotNull(shop);
		assertEquals("P1", shop.getShopId());
	}

	@Test
	public void getPlayerShopByIdTestWithNoShop() throws ShopsystemException {
		doThrow(ShopsystemException.class).when(validationHandler).checkForValueExists(null, "P0");
		assertThrows(ShopsystemException.class, () -> playershopManager.getPlayerShopById("P0"));
	}

	@Test
	public void getPlayershopIdListTest() {
		createPlayershop("P0");
		List<String> list = playershopManager.getPlayershopIdList();
		assertEquals(1, list.size());
		assertEquals("P0", list.get(0));
	}

	@Test
	public void despawnAllVillagersTest() {
		Playershop shop = createPlayershop("P0");
		playershopManager.despawnAllVillagers();
		verify(shop).despawn();
	}

	@Test
	public void deletePlayershopTest() {
		Playershop shop = createPlayershop("P0");
		playershopManager.deletePlayerShop(shop);
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(configDao).savePlayershopIds(new ArrayList<>());
		verify(shop).deleteShop();
	}

	@Test
	public void loadAllPlayerShopsTest() {
		Playershop shop = mock(Playershop.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.loadPlayershopIds()).thenReturn(Arrays.asList("P0"));
		playershopManager.loadAllPlayerShops();
		assertEquals(1, playershopManager.getPlayerShops().size());
		assertEquals(shop, playershopManager.getPlayerShops().get(0));
		assertDoesNotThrow(() -> verify(shop).setupExisting("P0"));
	}

	@Test
	public void loadAllPlayerShopsTestWithLoadingError() throws EconomyPlayerException {
		Playershop shop = mock(Playershop.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getPlayershop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("my error message");
		when(configDao.loadPlayershopIds()).thenReturn(Arrays.asList("P0"));
		doThrow(e).when(shop).setupExisting("P0");
		playershopManager.loadAllPlayerShops();
		assertEquals(0, playershopManager.getPlayerShops().size());
		verify(e).getMessage();
	}
}
