package com.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.boss.BossBar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.dataaccess.api.EconomyPlayerDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerValidationHandler;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.jobsystem.logic.api.JobManager;

import dagger.Lazy;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerManagerImplTest {

	@InjectMocks
	EconomyPlayerManagerImpl ecoPlayerManager;
	@Mock
	EconomyPlayerDao ecoPlayerDao;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	EconomyPlayerValidationHandler validationHandler;
	@Mock
	BankManager bankManager;
	@Mock
	ConfigManager configManager;
	@Mock
	Lazy<JobManager> jobManager;
	@Mock
	ServerProvider serverProvider;
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@Test
	public void createEconomyPlayerTest() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);
		assertDoesNotThrow(() -> ecoPlayerManager.createEconomyPlayer("catch441"));
		assertEquals(1, ecoPlayerManager.getAllEconomyPlayers().size());
		assertEquals("catch441", ecoPlayerManager.getAllEconomyPlayers().get(0).getName());
		assertDoesNotThrow(() -> verify(generalValidator).checkForValueNotInList(new ArrayList<>(), "catch441"));
	}

	@Test
	public void createEconomyPlayerTestWithExistingName() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValueNotInList(new ArrayList<>(),
				"catch441");
		assertThrows(GeneralEconomyException.class, () -> ecoPlayerManager.createEconomyPlayer("catch441"));
		assertEquals(0, ecoPlayerManager.getAllEconomyPlayers().size());
	}

	@Test
	public void getEconomyPlayerNameListTest() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);

		assertDoesNotThrow(() -> ecoPlayerManager.createEconomyPlayer("catch441"));
		List<String> list = ecoPlayerManager.getEconomyPlayerNameList();
		assertEquals(1, list.size());
		assertEquals("catch441", list.get(0));
	}

	@Test
	public void getEconomyPlayerByNameTest() throws EconomyPlayerException {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);

		assertDoesNotThrow(() -> ecoPlayerManager.createEconomyPlayer("catch441"));
		EconomyPlayer ecoPlayer = assertDoesNotThrow(() -> ecoPlayerManager.getEconomyPlayerByName("catch441"));
		assertEquals("catch441", ecoPlayer.getName());
	}

	@Test
	public void getEconomyPlayerByNameTestWithNoPlayer() {
		try {
			ecoPlayerManager.getEconomyPlayerByName("catch441");
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("catch441", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void getAllEconomyPlayersTest() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);

		assertDoesNotThrow(() -> ecoPlayerManager.createEconomyPlayer("catch441"));
		assertDoesNotThrow(() -> ecoPlayerManager.createEconomyPlayer("Wulfgar"));
		List<EconomyPlayer> list = ecoPlayerManager.getAllEconomyPlayers();
		assertEquals(2, list.size());
		assertEquals("catch441", list.get(0).getName());
		assertEquals("Wulfgar", list.get(1).getName());
	}

	@Test
	public void deleteEconomyPlayerTest() {
		BossBar bossBar = mock(BossBar.class);
		BankAccount account = mock(BankAccount.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(bankManager.createBankAccount(0.0)).thenReturn(account);

		assertDoesNotThrow(() -> ecoPlayerManager.createEconomyPlayer("catch441"));
		ecoPlayerManager.deleteEconomyPlayer(ecoPlayerManager.getAllEconomyPlayers().get(0));
		assertEquals(0, ecoPlayerManager.getAllEconomyPlayers().size());
		verify(ecoPlayerDao).deleteEconomyPlayer("catch441");
		verify(bankManager).deleteBankAccount(account);
	}

	@Test
	public void loadAllEconomyPlayers() {
		BossBar bossBar = mock(BossBar.class);
		when(serverProvider.createBossBar()).thenReturn(bossBar);
		when(ecoPlayerDao.loadPlayerList()).thenReturn(Arrays.asList("catch441"));
		
		ecoPlayerManager.getAllEconomyPlayers().clear();
		ecoPlayerManager.loadAllEconomyPlayers();
		List<EconomyPlayer> list = ecoPlayerManager.getAllEconomyPlayers();
		assertEquals(1, list.size());
		assertEquals("catch441", list.get(0).getName());
		verify(ecoPlayerDao).setupSavefile();
	}
}
