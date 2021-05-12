package org.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.ServiceComponent;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.shopsystem.logic.api.Rentshop;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;

@ExtendWith(MockitoExtension.class)
public class RentshopManagerImplTest {

	@InjectMocks
	RentshopManagerImpl rentshopManager;
	@Mock
	ShopValidator validationHandler;
	@Mock
	ServerProvider serverProvider;
	@Mock
	ConfigDao configDao;

	@BeforeEach
	public void cleanUp() {
		for (Rentshop shop : rentshopManager.getRentShops()) {
			rentshopManager.deleteRentShop(shop);
		}
	}

	@Test
	public void setupRentDailyTaskTest() {
		// TODO dont know how to test
	}

	@Test
	public void generateFreeRentShopIdTest() {
		String id1 = rentshopManager.generateFreeRentShopId();
		createRentshop("R0");
		String id2 = rentshopManager.generateFreeRentShopId();
		assertEquals("R0", id1);
		assertEquals("R1", id2);
	}

	private Rentshop createRentshop(String id) {
		Rentshop shop = mock(Rentshop.class);
		Location loc = mock(Location.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(shop.getShopId()).thenReturn(id);
		when(serviceComponent.getRentshop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		return assertDoesNotThrow(() -> rentshopManager.createRentShop(loc, 9, 2.5));
	}

	@Test
	public void getRentShopByIdTest() {
		Rentshop shop = createRentshop("R0");
		Rentshop result = assertDoesNotThrow(() -> rentshopManager.getRentShopById("R0"));
		assertNotNull(shop);
		assertEquals(shop, result);
	}

	@Test
	public void getRentShopByIdTestFail() throws ShopsystemException {
		createRentshop("R0");
		doThrow(ShopsystemException.class).when(validationHandler).checkForValueExists(null, "R1");
		assertThrows(ShopsystemException.class, () -> rentshopManager.getRentShopById("R1"));
	}

	@Test
	public void getRentShopUniqueNameListTest() {
		Rentshop shop0 = createRentshop("R0");
		Rentshop shop = createRentshop("R1");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(shop0.isRentable()).thenReturn(true);
		when(shop.isRentable()).thenReturn(false);
		when(shop.getOwner()).thenReturn(ecoPlayer);
		when(shop.getName()).thenReturn("Shop#R1");
		when(ecoPlayer.getName()).thenReturn("catch441");
		List<String> list = rentshopManager.getRentShopUniqueNameList();
		assertEquals(2, list.size());
		assertEquals("RentShop#R0", list.get(0));
		assertEquals("Shop#R1_catch441", list.get(1));
	}

	@Test
	public void getRentShopByUniqueNameTestWithNotRented() {
		Rentshop shop = createRentshop("R0");
		when(shop.isRentable()).thenReturn(true);
		Rentshop result = assertDoesNotThrow(() -> rentshopManager.getRentShopByUniqueName("RentShop#R0"));
		assertEquals(shop, result);
	}

	@Test
	public void getRentShopByUniqueNameTestWithRented() {
		Rentshop shop = createRentshop("R0");
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(shop.isRentable()).thenReturn(false);
		when(shop.getOwner()).thenReturn(ecoPlayer);
		when(shop.getName()).thenReturn("Shop#R0");
		when(ecoPlayer.getName()).thenReturn("catch441");
		Rentshop result = assertDoesNotThrow(() -> rentshopManager.getRentShopByUniqueName("Shop#R0_catch441"));
		assertEquals(shop, result);
	}

	@Test
	public void getRentShopByUniqueNameTestWithoutShop() {
		ShopsystemException exception = assertThrows(ShopsystemException.class,
				() -> rentshopManager.getRentShopByUniqueName("RentShop#R1"));
		assertEquals(1, exception.getParams().length);
		assertEquals("RentShop#R1", exception.getParams()[0]);
		assertEquals(ExceptionMessageEnum.DOES_NOT_EXIST, exception.getKey());
	}

	@Test
	public void getRentShopsTest() {
		Rentshop shop = createRentshop("R0");
		Rentshop shop2 = createRentshop("R1");
		List<Rentshop> shops = rentshopManager.getRentShops();
		assertEquals(shop, shops.get(0));
		assertEquals(shop2, shops.get(1));
	}

	@Test
	public void despawnAllVillagersTest() {
		Rentshop shop = createRentshop("R0");
		Rentshop shop2 = createRentshop("R1");
		rentshopManager.despawnAllVillagers();
		verify(shop).despawn();
		verify(shop2).despawn();
	}

	@Test
	public void deleteRentshopTest() {
		Rentshop shop = createRentshop("R0");
		rentshopManager.deleteRentShop(shop);
		verify(shop).deleteShop();
		verify(configDao).saveRentshopIds(new ArrayList<>());
		assertEquals(0, rentshopManager.getRentShops().size());
	}

	@Test
	public void createRentshopTestWithInvalidSize() throws ShopsystemException {
		doThrow(ShopsystemException.class).when(validationHandler).checkForValidSize(5);
		assertThrows(ShopsystemException.class, () -> rentshopManager.createRentShop(null, 5, 2.5));
		assertEquals(0, rentshopManager.getRentShops().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createRentshopTestWithInvalidRentalFee() throws ShopsystemException {
		doThrow(ShopsystemException.class).when(validationHandler).checkForPositiveValue(-2.5);
		assertThrows(ShopsystemException.class, () -> rentshopManager.createRentShop(null, 9, -2.5));
		assertEquals(0, rentshopManager.getRentShops().size());
		verifyNoInteractions(configDao);
	}

	@Test
	public void createRentshopTest() {
		Rentshop shop = mock(Rentshop.class);
		Location loc = mock(Location.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getRentshop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> rentshopManager.createRentShop(loc, 9, 2.5));

		assertDoesNotThrow(() -> verify(validationHandler).checkForPositiveValue(2.5));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidSize(9));
		verify(configDao).saveRentshopIds(anyList());
		assertEquals(1, rentshopManager.getRentShops().size());
		Rentshop result = rentshopManager.getRentShops().get(0);
		assertEquals(shop, result);
		verify(shop).setupNew("R0", loc, 9, 2.5);
	}

	@Test
	public void loadAllRentshopsTest() {
		Rentshop shop = mock(Rentshop.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getRentshop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.loadRentshopIds()).thenReturn(Arrays.asList("R0"));
		rentshopManager.loadAllRentShops();
		assertEquals(1, rentshopManager.getRentShops().size());
		assertDoesNotThrow(() -> verify(shop).setupExisting("R0"));
	}

	@Test
	public void loadAllRentshopsTestWithLoadingError() throws EconomyPlayerException {
		Rentshop shop = mock(Rentshop.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serviceComponent.getRentshop()).thenReturn(shop);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.loadRentshopIds()).thenReturn(Arrays.asList("R0"));
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(e.getMessage()).thenReturn("my error message");
		when(configDao.loadRentshopIds()).thenReturn(Arrays.asList("R0"));
		doThrow(e).when(shop).setupExisting("R0");
		rentshopManager.loadAllRentShops();
		assertEquals(0, rentshopManager.getRentShops().size());
		verify(e).getMessage();
	}
}
