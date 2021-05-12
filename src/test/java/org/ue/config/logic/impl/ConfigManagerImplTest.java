package org.ue.config.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.config.logic.api.ConfigException;
import org.ue.config.logic.api.ConfigValidator;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;

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
	@Mock
	ConfigValidator validationHandler;

	@Test
	public void setupConfigInitTest() {
		manager.setupConfig();
		assertEquals(2, manager.getMaxJobs());
		assertEquals(1, manager.getMaxJoinedTowns());
		assertEquals(3, manager.getMaxPlayershops());
		assertTrue(manager.isHomeSystem());
		assertFalse(manager.isAllowQuickshop());
		assertFalse(manager.isWildernessInteraction());
		assertFalse(manager.isExtendedInteraction());
		assertEquals("$", manager.getCurrencyPl());
		assertEquals("$", manager.getCurrencySg());
		assertEquals(14, manager.getMaxRentedDays());
		assertEquals(3, manager.getMaxHomes());
		assertEquals("US", manager.getLocale().getCountry());
		assertEquals("en", manager.getLocale().getLanguage());
		assertEquals("0.0", String.valueOf(manager.getStartAmount()));

		verify(dao).saveMaxJobs(2);
		verify(dao).saveMaxJoinedTowns(1);
		verify(dao).saveMaxPlayershops(3);
		verify(dao).saveHomesFeature(true);
		verify(dao).saveAllowQuickshop(false);
		verify(dao).saveWildernessInteraction(false);
		verify(dao).saveExtendedInteraction(false);
		verify(dao).saveCurrencyPl("$");
		verify(dao).saveCurrencySg("$");
		verify(dao).saveMaxRentedDays(14);
		verify(dao).saveMaxHomes(3);
		verify(dao).saveLanguage("en");
		verify(dao).saveCountry("US");
		verify(dao).saveStartAmount(0.0);
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
		when(dao.hasStartAmount()).thenReturn(true);
		when(dao.hasAllowQuickshop()).thenReturn(true);

		when(dao.loadCurrencyPl()).thenReturn("$");
		when(dao.loadCurrencySg()).thenReturn("$");
		when(dao.loadAllowQuickshop()).thenReturn(false);
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
		when(dao.loadStartAmount()).thenReturn(1.5);

		manager.setupConfig();
		assertEquals(2, manager.getMaxJobs());
		assertEquals(1, manager.getMaxJoinedTowns());
		assertEquals(3, manager.getMaxPlayershops());
		assertTrue(manager.isHomeSystem());
		assertFalse(manager.isAllowQuickshop());
		assertFalse(manager.isWildernessInteraction());
		assertFalse(manager.isExtendedInteraction());
		assertEquals("$", manager.getCurrencyPl());
		assertEquals("$", manager.getCurrencySg());
		assertEquals(14, manager.getMaxRentedDays());
		assertEquals(3, manager.getMaxHomes());
		assertEquals("US", manager.getLocale().getCountry());
		assertEquals("en", manager.getLocale().getLanguage());
		assertEquals("1.5", String.valueOf(manager.getStartAmount()));

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
		verify(dao, never()).saveStartAmount(Mockito.anyDouble());
		verify(dao, never()).saveAllowQuickshop(Mockito.anyBoolean());
	}

	@Test
	public void setupConfigTestWithSetupFail() throws ConfigException {
		when(dao.hasMaxHomes()).thenReturn(false);
		// 3 is positive, but this is the only way to trigger the exception for the
		// setup method
		doThrow(ConfigException.class).when(validationHandler).checkForPositiveValue(3.0);

		manager.setupConfig();
		
		verifyNoMoreInteractions(dao);
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
	public void setAllowQuickshopTest() {
		assertFalse(manager.isAllowQuickshop());
		manager.setAllowQuickshop(true);
		assertTrue(manager.isAllowQuickshop());
		verify(dao).saveAllowQuickshop(true);
	}

	@Test
	public void setStartAmountTest() {
		manager.setupConfig();
		assertEquals("0.0", String.valueOf(manager.getStartAmount()));
		assertDoesNotThrow(() -> manager.setStartAmount(1.5));
		assertEquals("1.5", String.valueOf(manager.getStartAmount()));
		verify(dao).saveStartAmount(1.5);
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
	public void setMaxRentedDaysExceptionTest() throws ConfigException {
		doThrow(ConfigException.class).when(validationHandler).checkForValueGreaterZero(-7);
		assertThrows(ConfigException.class, () -> manager.setMaxRentedDays(-7));
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
	public void setMaxPlayershopsExceptionTest() throws ConfigException {
		doThrow(ConfigException.class).when(validationHandler).checkForPositiveValue(-1.0);
		assertThrows(ConfigException.class, () -> manager.setMaxPlayershops(-1));
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
	public void setMaxHomesExceptionTest() throws ConfigException {
		doThrow(ConfigException.class).when(validationHandler).checkForPositiveValue(-1.0);
		assertThrows(ConfigException.class, () -> manager.setMaxHomes(-1));
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
	public void setMaxJobsExceptionTest() throws ConfigException {
		doThrow(ConfigException.class).when(validationHandler).checkForPositiveValue(-1.0);
		assertThrows(ConfigException.class, () -> manager.setMaxJobs(-1));
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
	public void setMaxJoinedTownesExceptionTest() throws ConfigException {
		doThrow(ConfigException.class).when(validationHandler).checkForPositiveValue(-3.0);
		assertThrows(ConfigException.class, () -> manager.setMaxJoinedTowns(-3));
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
	public void setLocaleTest() throws ConfigException {
		assertDoesNotThrow(() -> manager.setLocale("de", "DE"));
		assertEquals("DE", manager.getLocale().getCountry());
		assertEquals("de", manager.getLocale().getLanguage());
		verify(dao).saveLanguage("de");
		verify(dao).saveCountry("DE");
		verify(validationHandler).checkForSupportedLanguage("de");
		verify(validationHandler).checkForCountryMatching("de", "DE");
	}

	@Test
	public void setLocaleUnsupportedLanguage() throws ConfigException {
		doThrow(ConfigException.class).when(validationHandler).checkForSupportedLanguage("catch");
		assertThrows(ConfigException.class, () -> manager.setLocale("catch", "CATCH"));
		verifyNoMoreInteractions(dao);
	}

	@Test
	public void setLocaleCountryNotMatching() throws ConfigException {
		doThrow(ConfigException.class).when(validationHandler).checkForCountryMatching("catch", "CATCH");
		assertThrows(ConfigException.class, () -> manager.setLocale("catch", "CATCH"));
		verifyNoMoreInteractions(dao);
	}
}
