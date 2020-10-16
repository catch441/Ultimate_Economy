package com.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.bank.logic.api.BankAccount;
import com.ue.common.utils.MessageWrapper;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.jobsystem.logic.api.Job;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerValidationHandlerImplTest {

	@InjectMocks
	EconomyPlayerValidationHandlerImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void checkForEnoughMoneyTestPersonal() {
		try {
			BankAccount account = mock(BankAccount.class);
			assertDoesNotThrow(() -> when(account.hasAmount(10)).thenReturn(false));
			validationHandler.checkForEnoughMoney(account, 10, true);
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			EconomyPlayerException ex = (EconomyPlayerException) e;
			assertEquals(0, ex.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL, ex.getKey());
		}
	}

	@Test
	public void checkForEnoughMoneyTestNonPersonal() {
		try {
			BankAccount account = mock(BankAccount.class);
			assertDoesNotThrow(() -> when(account.hasAmount(10)).thenReturn(false));
			validationHandler.checkForEnoughMoney(account, 10, false);
			fail();
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			assertTrue(e instanceof EconomyPlayerException);
			EconomyPlayerException ex = (EconomyPlayerException) e;
			assertEquals(0, ex.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.NOT_ENOUGH_MONEY_NON_PERSONAL, ex.getKey());
		}
	}

	@Test
	public void checkForEnoughMoneyTestSuccess() {
		BankAccount account = mock(BankAccount.class);
		assertDoesNotThrow(() -> when(account.hasAmount(0.5)).thenReturn(true));
		assertDoesNotThrow(() -> validationHandler.checkForEnoughMoney(account, 0.5, false));
	}

	@Test
	public void checkForExistingHomeTestFail() {
		try {
			validationHandler.checkForExistingHome(new HashMap<>(), "myhome1");
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.HOME_DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void checkForExistingHomeTestSuccess() {
		Map<String, Location> homes = new HashMap<>();
		homes.put("myhome2", null);
		assertDoesNotThrow(() -> validationHandler.checkForExistingHome(homes, "myhome2"));
	}

	@Test
	public void checkForNotReachedMaxHomesTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForNotReachedMaxHomes(false));
	}

	@Test
	public void checkForNotReachedMaxHomesTestFail() {
		try {
			validationHandler.checkForNotReachedMaxHomes(true);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.MAX_REACHED, e.getKey());
		}
	}

	@Test
	public void checkForNotExistingHomeTestSuccess() {
		Map<String, Location> homes = new HashMap<>();
		assertDoesNotThrow(() -> validationHandler.checkForNotExistingHome(homes, "myhome1"));
	}

	@Test
	public void checkForNotExistingHomeTestFail() {
		try {
			Map<String, Location> homes = new HashMap<>();
			homes.put("myhome1", null);
			validationHandler.checkForNotExistingHome(homes, "myhome1");
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.HOME_ALREADY_EXIST, e.getKey());
		}
	}

	@Test
	public void checkForJobNotJoinedTestSuccess() {
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> validationHandler.checkForJobNotJoined(jobs, job));
	}

	@Test
	public void checkForJobNotJoinedTestFail() {
		try {
			List<Job> jobs = new ArrayList<>();
			Job job = mock(Job.class);
			jobs.add(job);
			validationHandler.checkForJobNotJoined(jobs, job);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.JOB_ALREADY_JOINED, e.getKey());
		}
	}

	@Test
	public void checkForJobJoinedTestSuccess() {
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		jobs.add(job);
		assertDoesNotThrow(() -> validationHandler.checkForJobJoined(jobs, job));
	}

	@Test
	public void checkForJobJoinedTestFail() {
		try {
			List<Job> jobs = new ArrayList<>();
			Job job = mock(Job.class);
			validationHandler.checkForJobJoined(jobs, job);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.JOB_NOT_JOINED, e.getKey());
		}
	}

	@Test
	public void checkForNotReachedMaxJoinedJobsTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForNotReachedMaxJoinedJobs(false));
	}

	@Test
	public void checkForNotReachedMaxJoinedJobsTestFail() {
		try {
			validationHandler.checkForNotReachedMaxJoinedJobs(true);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.MAX_REACHED, e.getKey());
		}
	}

	@Test
	public void checkForTownNotJoinedTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForTownNotJoined(new ArrayList<>(), "mytown"));
	}

	@Test
	public void checkForTownNotJoinedTestFail() {
		try {
			validationHandler.checkForTownNotJoined(Arrays.asList("mytown"), "mytown");
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.TOWN_ALREADY_JOINED, e.getKey());
		}
	}

	@Test
	public void checkForJoinedTownTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForJoinedTown(Arrays.asList("mytown"), "mytown"));
	}

	@Test
	public void checkForJoinedTownTestFail() {
		try {
			validationHandler.checkForJoinedTown(new ArrayList<>(), "mytown");
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.TOWN_NOT_JOINED, e.getKey());
		}
	}

	@Test
	public void checkForNotReachedMaxJoinedTownsTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForNotReachedMaxJoinedTowns(false));
	}

	@Test
	public void checkForNotReachedMaxJoinedTownsTestFail() {
		try {
			validationHandler.checkForNotReachedMaxJoinedTowns(true);
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.MAX_REACHED, e.getKey());
		}
	}

	@Test
	public void checkForPlayerDoesNotExistTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForPlayerDoesNotExist(new ArrayList<>(), "kthschnll"));
	}

	@Test
	public void checkForPlayerDoesNotExistTestFail() {
		try {
			validationHandler.checkForPlayerDoesNotExist(Arrays.asList("kthschnll"), "kthschnll");
			fail();
		} catch (EconomyPlayerException e) {
			assertEquals(0, e.getParams().length);
			assertEquals(EconomyPlayerExceptionMessageEnum.PLAYER_ALREADY_EXIST, e.getKey());
		}
	}
}
