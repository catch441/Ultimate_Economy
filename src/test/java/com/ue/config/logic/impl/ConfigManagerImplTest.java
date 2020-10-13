package com.ue.config.logic.impl;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

@ExtendWith(MockitoExtension.class)
public class ConfigManagerImplTest {

	@InjectMocks
	ConfigManagerImpl manager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ConfigDao dao;
	@Mock
	EconomyPlayerManager ecoPlayerManager;

	@Test
	public void setupConfigInitTest() {
		manager.setupConfig();
		assertEquals(2, manager.getMaxJobs());
		assertEquals(1, manager.getMaxJoinedTowns());
		assertEquals(3, manager.getMaxPlayershops());
		assertTrue(manager.isHomeSystem());
		assertFalse(manager.isWildernessInteraction());
		assertFalse(manager.isExtendedInteraction());
		assertEquals("$", manager.getCurrencyPl());
		assertEquals("$", manager.getCurrencySg());
		assertEquals(14, manager.getMaxRentedDays());
		assertEquals(3, manager.getMaxHomes());
		assertEquals("US", manager.getLocale().getCountry());
		assertEquals("en", manager.getLocale().getLanguage());

		verify(dao).saveMaxJobs(2);
		verify(dao).saveMaxJoinedTowns(1);
		verify(dao).saveMaxPlayershops(3);
		verify(dao).saveHomesFeature(true);
		verify(dao).saveWildernessInteraction(false);
		verify(dao).saveExtendedInteraction(false);
		verify(dao).saveCurrencyPl("$");
		verify(dao).saveCurrencySg("$");
		verify(dao).saveMaxRentedDays(14);
		verify(dao).saveMaxHomes(3);
		verify(dao).saveLanguage("en");
		verify(dao).saveCountry("US");
	}

	@Test
	public void setupConfigAfterReloadWithoutChangesTest() {
		when(dao.hasCurrencyPl()).thenReturn(true);
		when(dao.hasCurrencySg()).thenReturn(true);
		when(dao.hasExtendedInteraction()).thenReturn(true);
		when(dao.hasHomesFeature()).thenReturn(true);
		when(dao.hasMaxHomes()).thenReturn(true);
		when(dao.hasMaxJobs()).thenReturn(true);
		when(dao.hasMaxJoinedTowns()).thenReturn(true);
		when(dao.hasMaxPlayershops()).thenReturn(true);
		when(dao.hasMaxRentedDays()).thenReturn(true);
		when(dao.hasWildernessInteraction()).thenReturn(true);
		when(dao.hasCountry()).thenReturn(true);

		when(dao.loadCurrencyPl()).thenReturn("$");
		when(dao.loadCurrencySg()).thenReturn("$");
		when(dao.loadExtendedInteraction()).thenReturn(false);
		when(dao.loadHomesFeature()).thenReturn(true);
		when(dao.loadMaxHomes()).thenReturn(3);
		when(dao.loadMaxJobs()).thenReturn(2);
		when(dao.loadMaxJoinedTowns()).thenReturn(1);
		when(dao.loadMaxPlayershops()).thenReturn(3);
		when(dao.loadMaxRentedDays()).thenReturn(14);
		when(dao.loadWildernessInteraction()).thenReturn(false);
		when(dao.loadLanguage()).thenReturn("en");
		when(dao.loadCountry()).thenReturn("US");

		manager.setupConfig();
		assertEquals(2, manager.getMaxJobs());
		assertEquals(1, manager.getMaxJoinedTowns());
		assertEquals(3, manager.getMaxPlayershops());
		assertTrue(manager.isHomeSystem());
		assertFalse(manager.isWildernessInteraction());
		assertFalse(manager.isExtendedInteraction());
		assertEquals("$", manager.getCurrencyPl());
		assertEquals("$", manager.getCurrencySg());
		assertEquals(14, manager.getMaxRentedDays());
		assertEquals(3, manager.getMaxHomes());
		assertEquals("US", manager.getLocale().getCountry());
		assertEquals("en", manager.getLocale().getLanguage());

		verify(dao, never()).saveMaxJobs(Mockito.anyInt());
		verify(dao, never()).saveMaxJoinedTowns(Mockito.anyInt());
		verify(dao, never()).saveMaxPlayershops(Mockito.anyInt());
		verify(dao, never()).saveHomesFeature(Mockito.anyBoolean());
		verify(dao, never()).saveWildernessInteraction(Mockito.anyBoolean());
		verify(dao, never()).saveExtendedInteraction(Mockito.anyBoolean());
		verify(dao, never()).saveCurrencyPl(Mockito.anyString());
		verify(dao, never()).saveCurrencySg(Mockito.anyString());
		verify(dao, never()).saveMaxRentedDays(Mockito.anyInt());
		verify(dao, never()).saveMaxHomes(Mockito.anyInt());
		verify(dao, never()).saveLanguage(Mockito.anyString());
		verify(dao, never()).saveCountry(Mockito.anyString());
	}

	@Test
	public void setExtendedInteractionTest() {
		manager.setupConfig();
		assertFalse(manager.isExtendedInteraction());
		manager.setExtendedInteraction(true);
		assertTrue(manager.isExtendedInteraction());
		verify(dao).saveExtendedInteraction(true);
	}

	@Test
	public void setWildernessInteractionTest() {
		assertFalse(manager.isWildernessInteraction());
		manager.setWildernessInteraction(true);
		assertTrue(manager.isWildernessInteraction());
		verify(dao).saveWildernessInteraction(true);
	}

	@Test
	public void setWildernessInteractionTestWithFalse() {
		assertFalse(manager.isWildernessInteraction());
		manager.setWildernessInteraction(false);
		assertFalse(manager.isWildernessInteraction());
		verify(dao).saveWildernessInteraction(false);
	}

	@Test
	public void setMaxRentedDaysTest() {
		manager.setupConfig();
		assertEquals(14, manager.getMaxRentedDays());
		assertDoesNotThrow(() -> manager.setMaxRentedDays(7));
		assertEquals(7, manager.getMaxRentedDays());
		verify(dao).saveMaxRentedDays(7);
	}

	@Test
	public void setMaxRentedDaysExceptionTest() {
		try {
			manager.setMaxRentedDays(-7);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals(-7, e.getParams()[0]);
		}
	}

	@Test
	public void setMaxPlayershopsTest() {
		manager.setupConfig();
		assertEquals(3, manager.getMaxPlayershops());
		assertDoesNotThrow(() -> manager.setMaxPlayershops(1));
		assertEquals(1, manager.getMaxPlayershops());
		verify(dao).saveMaxPlayershops(1);
	}

	@Test
	public void setMaxPlayershopsExceptionTest() {
		try {
			manager.setMaxPlayershops(-1);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals(-1, e.getParams()[0]);
		}
	}

	@Test
	public void setMaxHomesTest() {
		manager.setupConfig();
		assertEquals(3, manager.getMaxHomes());
		assertDoesNotThrow(() -> manager.setMaxHomes(1));
		assertEquals(1, manager.getMaxHomes());
		verify(dao).saveMaxHomes(1);
	}

	@Test
	public void setMaxHomesExceptionTest() {
		try {
			manager.setMaxHomes(-1);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals(-1, e.getParams()[0]);
		}
	}

	@Test
	public void setMaxJobsTest() {
		manager.setupConfig();
		assertEquals(2, manager.getMaxJobs());
		assertDoesNotThrow(() -> manager.setMaxJobs(1));
		assertEquals(1, manager.getMaxJobs());
		verify(dao).saveMaxJobs(1);
	}

	@Test
	public void setMaxJobsExceptionTest() {
		try {
			manager.setMaxJobs(-1);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals(-1, e.getParams()[0]);
		}
	}

	@Test
	public void setMaxJoinedTownesTest() {
		manager.setupConfig();
		assertEquals(1, manager.getMaxJoinedTowns());
		assertDoesNotThrow(() -> manager.setMaxJoinedTowns(3));
		assertEquals(3, manager.getMaxJoinedTowns());
		verify(dao).saveMaxJoinedTowns(3);
	}

	@Test
	public void setMaxJoinedTownesExceptionTest() {
		try {
			manager.setMaxJoinedTowns(-3);
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals(-3, e.getParams()[0]);
		}
	}

	@Test
	public void setHomeSystemTest() {
		manager.setupConfig();
		assertTrue(manager.isHomeSystem());
		manager.setHomeSystem(false);
		assertFalse(manager.isHomeSystem());
		verify(dao).saveHomesFeature(false);
	}

	@Test
	public void setCurrencyPlTest() {
		manager.setupConfig();
		assertEquals("$", manager.getCurrencyPl());
		manager.setCurrencyPl("Coins");
		assertEquals("Coins", manager.getCurrencyPl());
		verify(dao).saveCurrencyPl("Coins");
	}

	@Test
	public void setCurrencySgTest() {
		manager.setupConfig();
		assertEquals("$", manager.getCurrencySg());
		manager.setCurrencySg("Coin");
		assertEquals("Coin", manager.getCurrencySg());
		verify(dao).saveCurrencySg("Coin");
	}

	@Test
	public void getCurrencyTestPl() {
		manager.setCurrencyPl("Coins");
		String text = manager.getCurrencyText(10.0);
		assertEquals("Coins", text);
	}

	@Test
	public void getCurrencyTestSg() {
		manager.setCurrencySg("Coin");
		String text = manager.getCurrencyText(1.0);
		assertEquals("Coin", text);
	}

	@Test
	public void setLocaleTest() {
		assertDoesNotThrow(() -> manager.setLocale("de", "DE"));
		assertEquals("DE", manager.getLocale().getCountry());
		assertEquals("de", manager.getLocale().getLanguage());
		verify(dao).saveLanguage("de");
		verify(dao).saveCountry("DE");
	}

	@Test
	public void setLocaleCsCzTest() {
		assertDoesNotThrow(() -> manager.setLocale("cs", "CZ"));
		assertEquals("CZ", manager.getLocale().getCountry());
		assertEquals("cs", manager.getLocale().getLanguage());
		verify(dao).saveLanguage("cs");
		verify(dao).saveCountry("CZ");
	}

	@Test
	public void setLocaleZhCnTest() {
		assertDoesNotThrow(() -> manager.setLocale("zh", "CN"));
		assertEquals("CN", manager.getLocale().getCountry());
		assertEquals("zh", manager.getLocale().getLanguage());
		verify(dao).saveLanguage("zh");
		verify(dao).saveCountry("CN");
	}

	@Test
	public void setLocaleUnsupportedLanguage() {
		try {
			manager.setLocale("kth", "KTH");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("kth", e.getParams()[0]);
		}
	}

	@Test
	public void setLocaleCountryNotMatching() {
		try {
			manager.setLocale("de", "KTH");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, e.getKey());
			assertEquals(1, e.getParams().length);
			assertEquals("KTH", e.getParams()[0]);
		}
	}
}
