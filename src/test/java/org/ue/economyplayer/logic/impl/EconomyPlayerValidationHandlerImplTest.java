package org.ue.economyplayer.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.bank.logic.api.BankAccount;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.jobsystem.logic.api.Job;

@ExtendWith(MockitoExtension.class)
public class EconomyPlayerValidationHandlerImplTest {

	@InjectMocks
	EconomyPlayerValidationHandlerImpl validationHandler;
	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void checkForEnoughMoneyTestPersonal() {
		BankAccount account = mock(BankAccount.class);
		when(account.getAmount()).thenReturn(5.0);
		EconomyPlayerException e = assertThrows(EconomyPlayerException.class,
				() -> validationHandler.checkForEnoughMoney(account, 10, true));
		assertEquals(0, e.getParams().length);
		assertEquals(ExceptionMessageEnum.NOT_ENOUGH_MONEY_PERSONAL, e.getKey());
	}

	@Test
	public void checkForEnoughMoneyTestNonPersonal() {
		BankAccount account = mock(BankAccount.class);
		when(account.getAmount()).thenReturn(5.0);
		EconomyPlayerException e = assertThrows(EconomyPlayerException.class,
				() -> validationHandler.checkForEnoughMoney(account, 10, false));
		assertEquals(0, e.getParams().length);
		assertEquals(ExceptionMessageEnum.NOT_ENOUGH_MONEY_NON_PERSONAL, e.getKey());
	}

	@Test
	public void checkForEnoughMoneyTestSuccess() {
		BankAccount account = mock(BankAccount.class);
		when(account.getAmount()).thenReturn(0.5);
		assertDoesNotThrow(() -> validationHandler.checkForEnoughMoney(account, 0.5, false));
	}

	@Test
	public void checkForJobNotJoinedTestSuccess() {
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> validationHandler.checkForJobNotJoined(jobs, job));
	}

	@Test
	public void checkForJobNotJoinedTestFail() {
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		jobs.add(job);
		EconomyPlayerException e = assertThrows(EconomyPlayerException.class,
				() -> validationHandler.checkForJobNotJoined(jobs, job));
		assertEquals(0, e.getParams().length);
		assertEquals(ExceptionMessageEnum.JOB_ALREADY_JOINED, e.getKey());
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
		List<Job> jobs = new ArrayList<>();
		Job job = mock(Job.class);
		EconomyPlayerException e = assertThrows(EconomyPlayerException.class,
				() -> validationHandler.checkForJobJoined(jobs, job));
		assertEquals(0, e.getParams().length);
		assertEquals(ExceptionMessageEnum.JOB_NOT_JOINED, e.getKey());
	}

	@Test
	public void checkForTownNotJoinedTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForTownNotJoined(new ArrayList<>(), "mytown"));
	}

	@Test
	public void checkForTownNotJoinedTestFail() {
		EconomyPlayerException e = assertThrows(EconomyPlayerException.class,
				() -> validationHandler.checkForTownNotJoined(Arrays.asList("mytown"), "mytown"));
		assertEquals(0, e.getParams().length);
		assertEquals(ExceptionMessageEnum.TOWN_ALREADY_JOINED, e.getKey());
	}

	@Test
	public void checkForJoinedTownTestSuccess() {
		assertDoesNotThrow(() -> validationHandler.checkForJoinedTown(Arrays.asList("mytown"), "mytown"));
	}

	@Test
	public void checkForJoinedTownTestFail() {
		EconomyPlayerException e = assertThrows(EconomyPlayerException.class,
				() -> validationHandler.checkForJoinedTown(new ArrayList<>(), "mytown"));
		assertEquals(0, e.getParams().length);
		assertEquals(ExceptionMessageEnum.TOWN_NOT_JOINED, e.getKey());
	}
}
