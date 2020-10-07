package com.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.JobcenterManager;

@ExtendWith(MockitoExtension.class)
public class JobTabCompleterImplTest {

	@InjectMocks
	JobTabCompleterImpl tabCompleter;
	@Mock
	JobcenterManager jobcenterManager;
	@Mock
	JobManager jobManager;

	@Test
	public void zeroArgsTest() {
		String[] args = { "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
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
		String[] args = { "e" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(4, result.size());
		assertEquals("create", result.get(0));
		assertEquals("delete", result.get(1));
		assertEquals("move", result.get(2));
		assertEquals("removeJob", result.get(3));
	}

	@Test
	public void zeroArgsTestWithMatching2() {
		String[] args = { "Jo" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("addJob", result.get(0));
		assertEquals("removeJob", result.get(1));
	}

	@Test
	public void oneArgJobTest() {
		String[] args = { "job" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void createArgTest() {
		String[] args = { "create" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void removeJobArgTestWithEmptyThreeArgs() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "removeJob", "jobcenter", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void removeJobArgTestWithThreeArgs() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "removeJob", "jobcenter", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void removeJobArgTestWithMoreArgs() {
		String[] args = { "removeJob", "jobcenter", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void removeJobArgTestWithTwoArgs() {
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter1", "myJobcenter2"));
		String[] args = { "removeJob", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("myJobcenter1", result.get(0));
		assertEquals("myJobcenter2", result.get(1));
	}

	@Test
	public void removeJobArgTestWithTwoArgsMatching() {
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter1", "myJobcenter2"));
		String[] args = { "removeJob", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter1", result.get(0));
	}

	@Test
	public void jobArgTest() {
		String[] args = { "job", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(8, result.size());
		assertEquals("create", result.get(0));
		assertEquals("delete", result.get(1));
		assertEquals("addItem", result.get(2));
		assertEquals("removeItem", result.get(3));
		assertEquals("addFisher", result.get(4));
		assertEquals("removeFisher", result.get(5));
		assertEquals("addMob", result.get(6));
		assertEquals("removeMob", result.get(7));
	}

	@Test
	public void jobArgTestWithMatching1() {
		String[] args = { "job", "e" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(7, result.size());
		assertEquals("create", result.get(0));
		assertEquals("delete", result.get(1));
		assertEquals("addItem", result.get(2));
		assertEquals("removeItem", result.get(3));
		assertEquals("addFisher", result.get(4));
		assertEquals("removeFisher", result.get(5));
		assertEquals("removeMob", result.get(6));
	}

	@Test
	public void jobArgTestWithMatching2() {
		String[] args = { "job", "dM" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("addMob", result.get(0));
	}

	@Test
	public void addJobArgTest() {
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter"));
		String[] args = { "addJob", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter", result.get(0));
	}

	@Test
	public void addJobArgTestWithMatching() {
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter1", "myJobcenter2"));
		String[] args = { "addJob", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter1", result.get(0));
	}

	@Test
	public void addJobArgTestThreeArgs() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "addJob", "myjobcenter", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void addJobArgTestThreeArgsMatching() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "addJob", "myjobcenter", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void addJobArgTestMaterialArg() {
		String[] args = { "addJob", "myjobcenter", "myjob", "iron_or" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("iron_ore", result.get(0));
		assertEquals("legacy_iron_ore", result.get(1));
	}

	@Test
	public void addJobArgTestMoreArgs() {
		String[] args = { "addJob", "myjobcenter", "myjob", "iron_ore", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void moveArgTest() {
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter"));
		String[] args = { "move", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter", result.get(0));
	}

	@Test
	public void moveArgTestWithMatching() {
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter1", "myJobcenter2"));
		String[] args = { "move", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter1", result.get(0));
	}

	@Test
	public void deleteArgTest() {
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter"));
		String[] args = { "delete", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter", result.get(0));
	}

	@Test
	public void deleteArgTestWithMatching() {
		when(jobcenterManager.getJobcenterNameList()).thenReturn(Arrays.asList("myJobcenter1", "myJobcenter2"));
		String[] args = { "delete", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("myJobcenter1", result.get(0));
	}

	@Test
	public void deleteArgTestWithMoreArgs() {
		String[] args = { "delete", "jobcenter", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void moveArgTestWithMoreArgs() {
		String[] args = { "move", "jobcenter", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobCreateArgTest() {
		String[] args = { "job", "create" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobDeleteArgTest() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "delete", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobDeleteArgTestWithMatching() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "delete", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobDeleteArgTestWithMoreArgs() {
		String[] args = { "job", "delete", "job", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobAddFisherArgTestWithThreeArgs() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addFisher", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobAddFisherArgTestWithThreeArgsMatching() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addFisher", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobAddFisherArgTestWithFourArgs() {
		String[] args = { "job", "addFisher", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(3, result.size());
		assertEquals("fish", result.get(0));
		assertEquals("treasure", result.get(1));
		assertEquals("junk", result.get(2));
	}

	@Test
	public void jobAddFisherArgTestWithFourArgsMatching1() {
		String[] args = { "job", "addFisher", "myjob", "s" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("fish", result.get(0));
		assertEquals("treasure", result.get(1));
	}

	@Test
	public void jobAddFisherArgTestWithFourArgsMatching2() {
		String[] args = { "job", "addFisher", "myjob", "j" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("junk", result.get(0));
	}

	@Test
	public void jobAddFisherArgTestWithMoreArgs() {
		String[] args = { "job", "addFisher", "myjob", "fish", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobRemoveFisherArgTestWithThreeArgs() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeFisher", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobRemoveFisherArgTestWithThreeArgsMatching() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeFisher", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobRemoveFisherArgTestWithFourArgs() {
		String[] args = { "job", "removeFisher", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(3, result.size());
		assertEquals("fish", result.get(0));
		assertEquals("treasure", result.get(1));
		assertEquals("junk", result.get(2));
	}

	@Test
	public void jobRemoveFisherArgTestWithFourArgsMatching() {
		String[] args = { "job", "removeFisher", "myjob", "s" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("fish", result.get(0));
		assertEquals("treasure", result.get(1));
	}

	@Test
	public void jobRemoveFisherArgTestWithMoreArgs() {
		String[] args = { "job", "removeFisher", "myjob", "fish", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobAddItemArgTest() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addItem", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobAddItemArgTestMatching() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addItem", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobAddItemArgTestMatchingItem() {
		String[] args = { "job", "addItem", "myjob", "iron_or" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("iron_ore", result.get(0));
		assertEquals("legacy_iron_ore", result.get(1));
	}

	@Test
	public void jobAddItemArgTestItemArg() {
		String[] args = { "job", "addItem", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1540, result.size());
	}

	@Test
	public void jobAddItemArgTestMoreArgs() {
		String[] args = { "job", "addItem", "myjob", "iron_ore", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobRemoveItemArgTest() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeItem", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobRemoveItemArgTestMatching() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeItem", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobRemoveItemArgTestMatchingItem() {
		String[] args = { "job", "removeItem", "myjob", "iron_or" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("iron_ore", result.get(0));
		assertEquals("legacy_iron_ore", result.get(1));
	}

	@Test
	public void jobRemoveItemArgTestMoreArgs() {
		String[] args = { "job", "removeItem", "myjob", "iron_ore", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobAddMobArgTest() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addMob", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobAddMobArgTestWithMatching() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "addMob", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobAddMobArgTestWithMobArgMatching() {
		String[] args = { "job", "addMob", "myjob", "_drag" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("ender_dragon", result.get(0));
	}

	@Test
	public void jobAddMobArgTestWithMobArg() {
		String[] args = { "job", "addMob", "myjob", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(109, result.size());
	}

	@Test
	public void jobAddMobArgTestWithMoreArgs() {
		String[] args = { "job", "addMob", "myjob", "cow", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}

	@Test
	public void jobRemoveMobArgTest() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeMob", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(2, result.size());
		assertEquals("job1", result.get(0));
		assertEquals("job2", result.get(1));
	}

	@Test
	public void jobRemoveMobArgTestWithMatching() {
		when(jobManager.getJobNameList()).thenReturn(Arrays.asList("job1", "job2"));
		String[] args = { "job", "removeMob", "1" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("job1", result.get(0));
	}

	@Test
	public void jobRemoveMobArgTestWithMobArgMatching() {
		String[] args = { "job", "removeMob", "myjob", "_drag" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(1, result.size());
		assertEquals("ender_dragon", result.get(0));
	}

	@Test
	public void jobRemoveMobArgTestWithMoreArgs() {
		String[] args = { "job", "removeMob", "myjob", "cow", "" };
		List<String> result = tabCompleter.onTabComplete(null, null, null, args);
		assertEquals(0, result.size());
	}
}
