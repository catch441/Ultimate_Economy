package com.ue.jobsystem.logic.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.MessageWrapper;
import com.ue.config.logic.api.ConfigManager;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobcenterManager;

@ExtendWith(MockitoExtension.class)
public class JobCommandExecutorImplTest {

	@InjectMocks
	JobCommandExecutorImpl executor;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	JobcenterManager jobcenterManager;
	@Mock
	JobManager jobManager;
	@Mock
	ConfigManager configManager;

	@Test
	public void zeroArgs() {
		Player player = mock(Player.class);
		String[] args = {};
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}

	@Test
	public void invalidArg() {
		Player player = mock(Player.class);
		String[] args = { "foo" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertFalse(result);
		verifyNoInteractions(player);
	}
	
	@Test
	public void joblistCommandTest() {
		Player player = mock(Player.class);
		String[] args = { };
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("myjob"));
		when(messageWrapper.getString("joblist_info", "[myjob]")).thenReturn("my message");
		boolean result = executor.onCommand(player, null, "joblist", args);
		assertTrue(result);
		verify(player).sendMessage("my message");
		verifyNoMoreInteractions(player);
	}
	
	@Test
	public void joblistCommandTestWithMoreArgs() {
		Player player = mock(Player.class);
		String[] args = { ""};
		boolean result = executor.onCommand(player, null, "joblist", args);
		assertFalse(result);
	}
	
	@Test
	public void jobinfoCommandTestWithMoreArgs() {
		Player player = mock(Player.class);
		String[] args = { "myjob", "foo" };
		boolean result = executor.onCommand(player, null, "jobinfo", args);
		assertFalse(result);
	}
	
	@Test
	public void unknownCommandTestWithMoreArgs() {
		Player player = mock(Player.class);
		String[] args = { "myjob", "foo" };
		boolean result = executor.onCommand(player, null, "foo", args);
		assertFalse(result);
	}
	
	@Test
	public void jobinfoCommandTest() {
		Player player = mock(Player.class);
		Job job = mock(Job.class);
		when(job.getName()).thenReturn("myjob");
		when(messageWrapper.getString("jobinfo_info", "myjob")).thenReturn("my message 1");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		when(messageWrapper.getString("jobinfo_fishingprice", "stuff", 1.5, "$")).thenReturn("my message 2");
		when(messageWrapper.getString("jobinfo_killprice", "stuff", 1.5, "$")).thenReturn("my message 3");
		Map<String, Double> map = new HashMap<>();
		map.put("stuff", 1.5);
		when(job.getBlockList()).thenReturn(map);
		when(job.getFisherList()).thenReturn(map);
		when(job.getEntityList()).thenReturn(map);
		when(configManager.getCurrencyText(1.5)).thenReturn("$");
		String[] args = { "myjob" };
		boolean result = executor.onCommand(player, null, "jobinfo", args);
		assertTrue(result);
		
		verify(player).sendMessage("my message 1");
		verify(player).sendMessage("§6stuff §a1.5$");
		verify(player).sendMessage("my message 2");
		verify(player).sendMessage("my message 3");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void createCommandTest() {
		Player player = mock(Player.class);
		Location location = mock(Location.class);
		when(player.getLocation()).thenReturn(location);
		when(messageWrapper.getString("jobcenter_create", "center")).thenReturn("My message.");
		String[] args = { "create", "center", "9" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(jobcenterManager).createJobcenter("center", location, 9));
		verify(messageWrapper).getString("jobcenter_create", "center");
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void createCommandTestWithInvalidInteger() {
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "number")).thenReturn("My message.");
		String[] args = { "create", "center", "a" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(messageWrapper).getErrorString("invalid_parameter", "number");
		verify(player).sendMessage("My message.");
		verify(player).getLocation();
		verifyNoMoreInteractions(player);
	}

	@Test
	public void createCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "create", "center" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter create <jobcenter> <size> <- size have to be a multible of 9");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteCommandTest() {
		Jobcenter center = mock(Jobcenter.class);
		assertDoesNotThrow(() -> when(jobcenterManager.getJobcenterByName("center")).thenReturn(center));
		Player player = mock(Player.class);
		when(messageWrapper.getString("jobcenter_delete", "center")).thenReturn("My message.");
		String[] args = { "delete", "center" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		assertDoesNotThrow(() -> verify(jobcenterManager).deleteJobcenter(center));
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "delete", "center", "test" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter delete <jobcenter>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteCommandTestWithInvalidJobcenter() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobcenterManager.getJobcenterByName("center")).thenThrow(exception));
		Player player = mock(Player.class);
		String[] args = { "delete", "center" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void moveCommandTest() {
		Jobcenter center = mock(Jobcenter.class);
		assertDoesNotThrow(() -> when(jobcenterManager.getJobcenterByName("center")).thenReturn(center));
		Player player = mock(Player.class);
		Location location = mock(Location.class);
		when(player.getLocation()).thenReturn(location);
		String[] args = { "move", "center" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(center).moveJobcenter(location);
		verify(player).getLocation();
		verifyNoMoreInteractions(player);
	}

	@Test
	public void moveCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "move", "center", "test" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter move <jobcenter>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void moveCommandTestWithInvalidJobcenter() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobcenterManager.getJobcenterByName("center")).thenThrow(exception));
		Player player = mock(Player.class);
		String[] args = { "move", "center" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void addJobCommandTest() {
		Jobcenter center = mock(Jobcenter.class);
		assertDoesNotThrow(() -> when(jobcenterManager.getJobcenterByName("center")).thenReturn(center));
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		String[] args = { "addJob", "center", "myjob", "stone", "1" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(center).addJob(job, "stone", 0));
		verify(player).sendMessage("§6The job §amyjob§6 was added to the JobCenter §acenter.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void addJobCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "addJob", "center", "test" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter addJob <jobcenter> <job> <material> <slot>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void addJobCommandTestWithInvalidInteger() {
		Jobcenter center = mock(Jobcenter.class);
		assertDoesNotThrow(() -> when(jobcenterManager.getJobcenterByName("center")).thenReturn(center));
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		when(messageWrapper.getErrorString("invalid_parameter", "number")).thenReturn("My message.");
		String[] args = { "addJob", "center", "myjob", "stone", "a" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		assertDoesNotThrow(() -> verify(center, never()).addJob(eq(job), eq("stone"), anyInt()));
		verify(player, never()).sendMessage("§6The job §amyjob§6 was added to the JobCenter §acenter.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void addJobCommandTestWithInvalidJob() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenThrow(exception));
		Player player = mock(Player.class);
		String[] args = { "addJob", "center", "myjob", "stone", "0" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void addJobCommandTestWithInvalidJobcenter() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobcenterManager.getJobcenterByName("center")).thenThrow(exception));
		Player player = mock(Player.class);
		String[] args = { "addJob", "center", "myjob", "stone", "0" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void removeJobCommandTest() {
		when(messageWrapper.getString("jobcenter_removeJob", "myjob")).thenReturn("My message.");
		Jobcenter center = mock(Jobcenter.class);
		assertDoesNotThrow(() -> when(jobcenterManager.getJobcenterByName("center")).thenReturn(center));
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		String[] args = { "removeJob", "center", "myjob" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(center).removeJob(job));
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void removeJobCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "removeJob", "center", "myjob", "test" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter removeJob <jobcenter> <job>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void removeJobCommandTestWithInvalidJobcenter() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobcenterManager.getJobcenterByName("center")).thenThrow(exception));
		Player player = mock(Player.class);
		String[] args = { "removeJob", "center", "myjob" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void removeJobCommandTestWithInvalidJob() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenThrow(exception));
		Player player = mock(Player.class);
		String[] args = { "removeJob", "center", "myjob" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void createJobCommandTest() {
		when(messageWrapper.getString("jobcenter_createJob", "myjob")).thenReturn("My message.");
		Player player = mock(Player.class);
		String[] args = { "job", "create", "myjob" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		assertDoesNotThrow(() -> verify(jobManager).createJob("myjob"));
		verifyNoMoreInteractions(player);
	}

	@Test
	public void createJobCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "job", "create" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter job create <job>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteJobCommandTest() {
		when(messageWrapper.getString("jobcenter_delJob", "myjob")).thenReturn("My message.");
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		String[] args = { "job", "delete", "myjob" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		assertDoesNotThrow(() -> verify(jobManager).deleteJob(job));
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteJobCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "job", "delete" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter job delete <job>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void deleteJobCommandTestWithInvalidJob() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenThrow(exception));
		Player player = mock(Player.class);
		String[] args = { "job", "delete", "myjob" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddFisherCommandTest() {
		when(messageWrapper.getString("jobcenter_addFisher", "fish")).thenReturn("My message.");
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		String[] args = { "job", "addFisher", "myjob", "fish", "1.5" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(job).addFisherLootType("fish", 1.5));
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddFisherCommandTestInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "job", "addFisher", "myjob", "fish" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter job addFisher <job> [fish/treasure/junk] <price>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddFisherCommandTestInvalidDouble() {
		when(messageWrapper.getErrorString("invalid_parameter", "number")).thenReturn("My exception.");
		Player player = mock(Player.class);
		String[] args = { "job", "addFisher", "myjob", "fish", "a" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My exception.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddFisherCommandTestInvalidFisherType() throws JobSystemException, GeneralEconomyException {
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My exception.");
		doThrow(exception).when(job).addFisherLootType("test", 1.5);
		
		Player player = mock(Player.class);
		String[] args = { "job", "addFisher", "myjob", "test", "1.5" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My exception.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddFisherCommandTestInvalidJob() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenThrow(exception));

		Player player = mock(Player.class);
		String[] args = { "job", "addFisher", "myjob", "fish", "1.5" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddItemCommandTest() {
		when(messageWrapper.getString("jobcenter_addItem", "stone")).thenReturn("My message.");
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		String[] args = { "job", "addItem", "myjob", "stone", "1.5" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(job).addBlock("stone", 1.5));
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddItemCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "job", "addItem", "myjob", "stone" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter job addItem <job> <material> <price>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddItemCommandTestWithInvalidDouble() {
		when(messageWrapper.getErrorString("invalid_parameter", "number")).thenReturn("My exception.");
		Player player = mock(Player.class);
		String[] args = { "job", "addItem", "myjob", "stone", "a" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My exception.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddItemCommandTestWithInvalidJob() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenThrow(exception));

		Player player = mock(Player.class);
		String[] args = { "job", "addItem", "myjob", "stone", "1.5" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddMobCommandTest() {
		when(messageWrapper.getString("jobcenter_addMob", "cow")).thenReturn("My message.");
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		String[] args = { "job", "addMob", "myjob", "cow", "1.5" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(job).addMob("cow", 1.5));
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddMobCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "job", "addMob", "myjob", "cow" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter job addMob <job> <entity> <price>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddMobCommandTestWithInvalidDouble() {
		when(messageWrapper.getErrorString("invalid_parameter", "number")).thenReturn("My exception.");
		Player player = mock(Player.class);
		String[] args = { "job", "addMob", "myjob", "cow", "a" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My exception.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddMobCommandTestWithInvalidEntity() throws JobSystemException, GeneralEconomyException {
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My exception.");
		doThrow(exception).when(job).addMob("test", 1.5);

		Player player = mock(Player.class);
		String[] args = { "job", "addMob", "myjob", "test", "1.5" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My exception.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobAddMobCommandTestWithInvalidJob() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenThrow(exception));

		Player player = mock(Player.class);
		String[] args = { "job", "addMob", "myjob", "test", "1.5" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveMobCommandTest() {
		when(messageWrapper.getString("jobcenter_removeMob", "cow")).thenReturn("My message.");
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		Player player = mock(Player.class);
		String[] args = { "job", "removeMob", "myjob", "cow" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(job).deleteMob("cow"));
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveMobCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "job", "removeMob", "myjob" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter job removeMob <jobname> <entity>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveMobCommandTestWithInvalidMob() throws JobSystemException, GeneralEconomyException {
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My exception.");
		doThrow(exception).when(job).deleteMob("test");

		Player player = mock(Player.class);
		String[] args = { "job", "removeMob", "myjob", "test" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My exception.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveMobCommandTestWithInvalidJob() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenThrow(exception));

		Player player = mock(Player.class);
		String[] args = { "job", "removeMob", "myjob", "cow" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveFisherCommandTest() {
		when(messageWrapper.getString("jobcenter_removeFisher", "fish")).thenReturn("My message.");
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));

		Player player = mock(Player.class);
		String[] args = { "job", "removeFisher", "myjob", "fish" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(job).delFisherLootType("fish"));
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveFisherCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "job", "removeFisher", "myjob" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter job removeFisher <jobname> <fish/treasure/junk>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveFisherCommandTestWithInvalidType() throws JobSystemException, GeneralEconomyException {
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My exception.");
		doThrow(exception).when(job).delFisherLootType("test");

		Player player = mock(Player.class);
		String[] args = { "job", "removeFisher", "myjob", "test" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My exception.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveFisherCommandTestWithInvalidJob() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenThrow(exception));

		Player player = mock(Player.class);
		String[] args = { "job", "removeFisher", "myjob", "fish" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveItemCommandTest() {
		when(messageWrapper.getString("jobcenter_removeItem", "stone")).thenReturn("My message.");
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));

		Player player = mock(Player.class);
		String[] args = { "job", "removeItem", "myjob", "stone" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		assertDoesNotThrow(() -> verify(job).deleteBlock("stone"));
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveItemCommandTestWithInvalidArgumentNumber() {
		Player player = mock(Player.class);
		String[] args = { "job", "removeItem", "myjob" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("/jobcenter job removeItem <job> <material>");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveItemCommandTestWithInvalidItem() throws JobSystemException, GeneralEconomyException {
		Job job = mock(Job.class);
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenReturn(job));
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My exception.");
		doThrow(exception).when(job).deleteBlock("test");
		Player player = mock(Player.class);
		String[] args = { "job", "removeItem", "myjob", "test" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My exception.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobRemoveItemCommandTestWithInvalidJob() {
		GeneralEconomyException exception = mock(GeneralEconomyException.class);
		when(exception.getMessage()).thenReturn("My message.");
		assertDoesNotThrow(() -> when(jobManager.getJobByName("myjob")).thenThrow(exception));

		Player player = mock(Player.class);
		String[] args = { "job", "removeItem", "myjob", "stone" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage("My message.");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobCommandTest() {
		Player player = mock(Player.class);
		String[] args = { "job" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage(
				"/jobcenter job [create/delete/addItem/removeItem/addMob/removeMob/addFisher/removeFisher]");
		verifyNoMoreInteractions(player);
	}

	@Test
	public void jobCommandTestWithInvalidCommand() {
		Player player = mock(Player.class);
		String[] args = { "job", "test" };
		boolean result = executor.onCommand(player, null, "jobcenter", args);
		assertTrue(result);
		verify(player).sendMessage(
				"/jobcenter job [create/delete/addItem/removeItem/addMob/removeMob/addFisher/removeFisher]");
		verifyNoMoreInteractions(player);
	}
}
