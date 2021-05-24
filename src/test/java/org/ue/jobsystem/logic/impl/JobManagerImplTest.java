package org.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.UltimateEconomyProvider;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemException;
import org.ue.jobsystem.logic.api.JobsystemValidator;

@ExtendWith(MockitoExtension.class)
public class JobManagerImplTest {

	@InjectMocks
	JobManagerImpl jobManager;
	@Mock
	ServerProvider serverProvider;
	@Mock
	JobcenterManager jobcenterManager;
	@Mock
	JobsystemValidator validationHandler;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	ConfigDao configDao;

	@Test
	public void loadAllJobsTest() {
		when(configDao.loadJobList()).thenReturn(Arrays.asList("myJob"));
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		Job job = mock(Job.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createJob()).thenReturn(job);
		jobManager.loadAllJobs();
		Job result = jobManager.getJobList().get(0);
		assertEquals(job, result);
		verify(job).setupExisting("myJob");
	}

	@Test
	public void createJobTest() {
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		Job job = mock(Job.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createJob()).thenReturn(job);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValueNotInList(anyList(), eq("myJob")));
		verify(configDao).saveJobList(anyList());
		assertEquals(1, jobManager.getJobList().size());
		assertEquals(job, jobManager.getJobList().get(0));
		verify(job).setupNew("myJob");
	}

	@Test
	public void createJobTestWithSameName() throws JobsystemException {
		doThrow(JobsystemException.class).when(validationHandler).checkForValueNotInList(anyList(), eq("myJob"));
		assertThrows(JobsystemException.class, () -> jobManager.createJob("myJob"));
		verify(configDao, never()).saveJobList(anyList());
		assertEquals(0, jobManager.getJobList().size());
	}

	@Test
	public void removeJobFromAllPlayersTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Job job = mock(Job.class);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		jobManager.removeJobFromAllPlayers(job);
		assertDoesNotThrow(() -> verify(ecoPlayer).leaveJob(job, false));
	}

	@Test
	public void removeJobFromAllPlayersTestWithError() throws EconomyPlayerException {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Job job = mock(Job.class);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(job.getName()).thenReturn("myJob");
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		doThrow(e).when(ecoPlayer).leaveJob(job, false);
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		when(e.getMessage()).thenReturn("my error message");
		jobManager.removeJobFromAllPlayers(job);
		verify(e).getMessage();
	}

	@Test
	public void deleteJobTest() {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		Job job = mock(Job.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createJob()).thenReturn(job);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		reset(configDao);
		Jobcenter jobcenter = mock(Jobcenter.class);
		when(jobcenterManager.getJobcenterList()).thenReturn(Arrays.asList(jobcenter));
		when(jobcenter.hasJob(job)).thenReturn(true);
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		when(job.getName()).thenReturn("myJob");
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		jobManager.deleteJob(job);
		verify(job).deleteJob();

		verify(configDao).saveJobList(anyList());
		assertDoesNotThrow(() -> verify(ecoPlayer).leaveJob(job, false));
		assertDoesNotThrow(() -> verify(jobcenter).removeJob(job));
		assertEquals(0, jobManager.getJobList().size());
	}

	@Test
	public void deleteJobTestWithFailedToRemoveJob() throws JobsystemException {
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		Job job = mock(Job.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createJob()).thenReturn(job);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		reset(configDao);
		Jobcenter jobcenter = mock(Jobcenter.class);
		JobsystemException e = mock(JobsystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(jobcenter).removeJob(job);
		when(job.getName()).thenReturn("myJob");
		when(jobcenterManager.getJobcenterList()).thenReturn(Arrays.asList(jobcenter));
		when(jobcenter.hasJob(job)).thenReturn(true);
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		jobManager.deleteJob(job);
		verify(job).deleteJob();
		verify(configDao).saveJobList(anyList());
		assertDoesNotThrow(() -> verify(ecoPlayer).leaveJob(job, false));
		verify(e).getMessage();
		assertEquals(0, jobManager.getJobList().size());
	}

	@Test
	public void getJobListTest() {
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		Job job = mock(Job.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createJob()).thenReturn(job);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		List<Job> list = jobManager.getJobList();
		assertEquals(1, list.size());
		assertEquals(job, list.get(0));
	}

	@Test
	public void getJobNameList() {
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		Job job = mock(Job.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createJob()).thenReturn(job);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		assertDoesNotThrow(() -> jobManager.createJob("myJob2"));
		List<String> list = jobManager.getJobNameList();
		assertEquals(2, list.size());
		assertEquals("myJob", list.get(0));
		assertEquals("myJob2", list.get(1));
	}

	@Test
	public void getJobByNameTest() {
		UltimateEconomyProvider provider = mock(UltimateEconomyProvider.class);
		Job job = mock(Job.class);
		when(serverProvider.getProvider()).thenReturn(provider);
		when(provider.createJob()).thenReturn(job);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		assertDoesNotThrow(() -> assertEquals(job, jobManager.getJobByName("myJob")));
	}

	@Test
	public void getJobByNameTestWithNoJob() throws JobsystemException {
		doThrow(JobsystemException.class).when(validationHandler).checkForValueExists(null, "myJob");
		assertThrows(JobsystemException.class, () -> jobManager.getJobByName("myJob"));
	}
}
