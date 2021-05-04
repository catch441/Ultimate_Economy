package org.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.JobcenterManager;

@ExtendWith(MockitoExtension.class)
public class JobTabCompleterImplTest {

	@InjectMocks
	JobTabCompleterImpl tabCompleter;
	@Mock
	JobcenterManager jobcenterManager;
	@Mock
	JobManager jobManager;
	
	@Test
	public void unknownCommandTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("something");
		String[] args = { "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}
	
	@Test
	public void jobinfoCommandTestMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobinfo");
		String[] args = { "", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}
	
	@Test
	public void jobinfoCommandTestMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobinfo");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}
	
	@Test
	public void jobinfoCommandTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobinfo");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}
	
	@Test
	public void nullArgsTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void zeroArgsTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(6, result.size());
		assertEquals("create", result.get(0));
		assertEquals("delete", result.get(1));
		assertEquals("move", result.get(2));
		assertEquals("job", result.get(3));
		assertEquals("addJob", result.get(4));
		assertEquals("removeJob", result.get(5));
	}

	@Test
	public void zeroArgsTestWithMatching1() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "e" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(4, result.size());
		assertEquals("create", result.get(0));
		assertEquals("delete", result.get(1));
		assertEquals("move", result.get(2));
		assertEquals("removeJob", result.get(3));
	}

	@Test
	public void zeroArgsTestWithMatching2() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "Jo" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("addJob", result.get(0));
		assertEquals("removeJob", result.get(1));
	}

	@Test
	public void oneArgJobTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void createArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "create" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void removeJobArgTestWithEmptyThreeArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "removeJob", "jobcenter", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void removeJobArgTestWithThreeArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "removeJob", "jobcenter", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void removeJobArgTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "removeJob", "jobcenter", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void removeJobArgTestWithTwoArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter1", "myJobcenter2"));
		String[] args = { "removeJob", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("myJobcenter1", result.get(0));
		assertEquals("myJobcenter2", result.get(1));
	}

	@Test
	public void removeJobArgTestWithTwoArgsMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter1", "myJobcenter2"));
		String[] args = { "removeJob", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter1", result.get(0));
	}

	@Test
	public void jobArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(10, result.size());
		assertEquals("create", result.get(0));
		assertEquals("delete", result.get(1));
		assertEquals("addItem", result.get(2));
		assertEquals("removeItem", result.get(3));
		assertEquals("addFisher", result.get(4));
		assertEquals("removeFisher", result.get(5));
		assertEquals("addMob", result.get(6));
		assertEquals("removeMob", result.get(7));
		assertEquals("addBreedable", result.get(8));
		assertEquals("removeBreedable", result.get(9));
	}

	@Test
	public void jobArgTestWithMatching1() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "e" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(9, result.size());
		assertEquals("create", result.get(0));
		assertEquals("delete", result.get(1));
		assertEquals("addItem", result.get(2));
		assertEquals("removeItem", result.get(3));
		assertEquals("addFisher", result.get(4));
		assertEquals("removeFisher", result.get(5));
		assertEquals("removeMob", result.get(6));
		assertEquals("addBreedable", result.get(7));
		assertEquals("removeBreedable", result.get(8));
	}

	@Test
	public void jobArgTestWithMatching2() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "dM" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("addMob", result.get(0));
	}

	@Test
	public void addJobArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter"));
		String[] args = { "addJob", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter", result.get(0));
	}

	@Test
	public void addJobArgTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter1", "myJobcenter2"));
		String[] args = { "addJob", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter1", result.get(0));
	}

	@Test
	public void addJobArgTestThreeArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "addJob", "myjobcenter", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void addJobArgTestThreeArgsMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "addJob", "myjobcenter", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void addJobArgTestMaterialArg() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "addJob", "myjobcenter", "myjob", "iron_or" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("iron_ore", result.get(0));
		assertEquals("legacy_iron_ore", result.get(1));
	}

	@Test
	public void addJobArgTestMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "addJob", "myjobcenter", "myjob", "iron_ore", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void moveArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter"));
		String[] args = { "move", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter", result.get(0));
	}

	@Test
	public void moveArgTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter1", "myJobcenter2"));
		String[] args = { "move", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter1", result.get(0));
	}

	@Test
	public void deleteArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter"));
		String[] args = { "delete", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter", result.get(0));
	}

	@Test
	public void deleteArgTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter1", "myJobcenter2"));
		String[] args = { "delete", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter1", result.get(0));
	}

	@Test
	public void deleteArgTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "delete", "jobcenter", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void moveArgTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "move", "jobcenter", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobCreateArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "create" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobDeleteArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "delete", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobDeleteArgTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "delete", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobDeleteArgTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "delete", "job", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobAddFisherArgTestWithThreeArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addFisher", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobAddFisherArgTestWithThreeArgsMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addFisher", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobAddFisherArgTestWithFourArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addFisher", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(3, result.size());
		assertEquals("fish", result.get(0));
		assertEquals("treasure", result.get(1));
		assertEquals("junk", result.get(2));
	}

	@Test
	public void jobAddFisherArgTestWithFourArgsMatching1() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addFisher", "myjob", "s" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("fish", result.get(0));
		assertEquals("treasure", result.get(1));
	}

	@Test
	public void jobAddFisherArgTestWithFourArgsMatching2() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addFisher", "myjob", "j" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("junk", result.get(0));
	}

	@Test
	public void jobAddFisherArgTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addFisher", "myjob", "fish", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobRemoveFisherArgTestWithThreeArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeFisher", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobRemoveFisherArgTestWithThreeArgsMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeFisher", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobRemoveFisherArgTestWithFourArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "removeFisher", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(3, result.size());
		assertEquals("fish", result.get(0));
		assertEquals("treasure", result.get(1));
		assertEquals("junk", result.get(2));
	}

	@Test
	public void jobRemoveFisherArgTestWithFourArgsMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "removeFisher", "myjob", "s" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("fish", result.get(0));
		assertEquals("treasure", result.get(1));
	}

	@Test
	public void jobRemoveFisherArgTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "removeFisher", "myjob", "fish", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobAddItemArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addItem", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobAddItemArgTestMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addItem", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobAddItemArgTestMatchingItem() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addItem", "myjob", "iron_or" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("iron_ore", result.get(0));
		assertEquals("legacy_iron_ore", result.get(1));
	}

	@Test
	public void jobAddItemArgTestItemArg() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addItem", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1540, result.size());
	}

	@Test
	public void jobAddItemArgTestMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addItem", "myjob", "iron_ore", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobRemoveItemArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeItem", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobRemoveItemArgTestMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeItem", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobRemoveItemArgTestMatchingItem() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "removeItem", "myjob", "iron_or" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("iron_ore", result.get(0));
		assertEquals("legacy_iron_ore", result.get(1));
	}

	@Test
	public void jobRemoveItemArgTestMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "removeItem", "myjob", "iron_ore", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobAddMobArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addMob", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobAddMobArgTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addMob", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobAddMobArgTestWithMobArgMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addMob", "myjob", "_drag" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("ender_dragon", result.get(0));
	}

	@Test
	public void jobAddMobArgTestWithMobArg() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addMob", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(109, result.size());
	}

	@Test
	public void jobAddMobArgTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addMob", "myjob", "cow", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobRemoveMobArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeMob", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobRemoveMobArgTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeMob", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobRemoveMobArgTestWithMobArgMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "removeMob", "myjob", "_drag" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("ender_dragon", result.get(0));
	}

	@Test
	public void jobRemoveMobArgTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "removeMob", "myjob", "cow", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}
	
	@Test
	public void jobAddBreedableArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addBreedable", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobAddBreedableArgTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addBreedable", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobAddBreedableArgTestWithMobArgMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addBreedable", "myjob", "chick" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("chicken", result.get(0));
	}

	@Test
	public void jobAddBreedableArgTestWithMobArg() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addBreedable", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(19, result.size());
	}

	@Test
	public void jobAddBreedableArgTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "addBreedable", "myjob", "cow", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobRemoveBreedableArgTest() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeBreedable", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobRemoveBreedableArgTestWithMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeBreedable", "1" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobRemoveBreedableArgTestWithMobArgMatching() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "removeBreedable", "myjob", "chick" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(1, result.size());
		assertEquals("chicken", result.get(0));
	}

	@Test
	public void jobRemoveBreedableArgTestWithMoreArgs() {
		Command command = mock(Command.class);
		when(command.getLabel()).thenReturn("jobcenter");
		String[] args = { "job", "removeBreedable", "myjob", "cow", "" };
		List<String> result = tabCompleter.onTabComplete(null, command, null, args);
		assertEquals(0, result.size());
	}
}
