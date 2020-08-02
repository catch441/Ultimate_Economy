package com.ue.config.logic.impl;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.config.logic.impl.ConfigManagerImpl;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.GeneralEconomyExceptionMessageEnum;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

@ExtendWith(MockitoExtension.class)
public class ConfigManagerImplTest {

	@InjectMocks
	ConfigManagerImpl manager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ConfigDao dao;

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
		ServerMock server = MockBukkit.mock();
		server.addPlayer("catch441");
		try {
			EconomyPlayerManagerImpl.createEconomyPlayer("kthschnll");
			manager.setupConfig();
			assertFalse(manager.isWildernessInteraction());
			manager.setWildernessInteraction(true);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getEconomyPlayerByName("kthschnll");
			assertTrue(ecoPlayer.getPlayer().hasPermission("ultimate_economy.wilderness"));
			assertTrue(manager.isWildernessInteraction());
			verify(dao).saveWildernessInteraction(true);

			EconomyPlayerManagerImpl.deleteEconomyPlayer(ecoPlayer);
		} catch (EconomyPlayerException e) {
			fail();
		}
		MockBukkit.unload();
	}

	@Test
	public void setMaxRentedDaysTest() {
		try {
			manager.setupConfig();
			assertEquals(14, manager.getMaxRentedDays());
			manager.setMaxRentedDays(7);
			assertEquals(7, manager.getMaxRentedDays());
			verify(dao).saveMaxRentedDays(7);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void setMaxRentedDaysExceptionTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
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
		try {
			manager.setupConfig();
			assertEquals(3, manager.getMaxPlayershops());
			manager.setMaxPlayershops(1);
			assertEquals(1, manager.getMaxPlayershops());
			verify(dao).saveMaxPlayershops(1);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void setMaxPlayershopsExceptionTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
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
		try {
			manager.setupConfig();
			assertEquals(3, manager.getMaxHomes());
			manager.setMaxHomes(1);
			assertEquals(1, manager.getMaxHomes());
			verify(dao).saveMaxHomes(1);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void setMaxHomesExceptionTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
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
		try {
			manager.setupConfig();
			assertEquals(2, manager.getMaxJobs());
			manager.setMaxJobs(1);
			assertEquals(1, manager.getMaxJobs());
			verify(dao).saveMaxJobs(1);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void setMaxJobsExceptionTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
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
		try {
			manager.setupConfig();
			assertEquals(1, manager.getMaxJoinedTowns());
			manager.setMaxJoinedTowns(3);
			assertEquals(3, manager.getMaxJoinedTowns());
			verify(dao).saveMaxJoinedTowns(3);
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void setMaxJoinedTownesExceptionTest() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
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
		try {
			manager.setLocale("de", "DE");
			assertEquals("DE", manager.getLocale().getCountry());
			assertEquals("de", manager.getLocale().getLanguage());
			verify(dao).saveLanguage("de");
			verify(dao).saveCountry("DE");
		} catch (GeneralEconomyException e) {
			fail();
		}
	}

	@Test
	public void setLocaleCountryNotMatching() {
		messageWrapper.loadLanguage(new Locale("en", "US"));
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
