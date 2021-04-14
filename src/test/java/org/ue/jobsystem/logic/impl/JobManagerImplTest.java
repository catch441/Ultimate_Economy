package org.ue.jobsystem.logic.impl;

import static org.junit.Assert.fail;
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
import org.ue.common.utils.ServiceComponent;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.api.GeneralEconomyValidationHandler;
import org.ue.general.GeneralEconomyException;
import org.ue.general.GeneralEconomyExceptionMessageEnum;
import org.ue.jobsystem.dataaccess.api.JobDao;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;
import org.ue.jobsystem.logic.api.JobsystemValidationHandler;
import org.ue.jobsystem.logic.JobSystemException;

@ExtendWith(MockitoExtension.class)
public class JobManagerImplTest {

	@InjectMocks
	JobManagerImpl jobManager;
	@Mock
	ServerProvider serverProvider;
	@Mock
	JobcenterManager jobcenterManager;
	@Mock
	JobsystemValidationHandler validationHandler;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ConfigDao configDao;
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@Test
	public void loadAllJobsTest() {
		when(configDao.loadJobList()).thenReturn(Arrays.asList("myJob"));
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		JobDao jobDao = mock(JobDao.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getJobDao()).thenReturn(jobDao);
		jobManager.loadAllJobs();
		Job job = jobManager.getJobList().get(0);
		assertEquals(job, jobManager.getJobList().get(0));
		verify(jobDao).loadBlockList();
	}

	@Test
	public void createJobTest() {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		JobDao jobDao = mock(JobDao.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getJobDao()).thenReturn(jobDao);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValueNotInList(anyList(), eq("myJob")));
		verify(configDao).saveJobList(anyList());
		assertEquals(1, jobManager.getJobList().size());
		assertEquals("myJob", jobManager.getJobList().get(0).getName());
	}

	@Test
	public void createJobTestWithSameName() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValueNotInList(anyList(),
				eq("myJob"));
		assertThrows(GeneralEconomyException.class, () -> jobManager.createJob("myJob"));
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
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		JobDao jobDao = mock(JobDao.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getJobDao()).thenReturn(jobDao);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		reset(configDao);
		Job job = jobManager.getJobList().get(0);
		Jobcenter jobcenter = mock(Jobcenter.class);
		when(jobcenterManager.getJobcenterList()).thenReturn(Arrays.asList(jobcenter));
		when(jobcenter.hasJob(job)).thenReturn(true);
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		jobManager.deleteJob(job);
		verify(jobDao).deleteSavefile();
		;
		verify(configDao).saveJobList(anyList());
		assertDoesNotThrow(() -> verify(ecoPlayer).leaveJob(job, false));
		assertDoesNotThrow(() -> verify(jobcenter).removeJob(job));
		assertEquals(0, jobManager.getJobList().size());
	}

	@Test
	public void deleteJobTestWithFailedToRemoveJob() throws JobSystemException {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		JobDao jobDao = mock(JobDao.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getJobDao()).thenReturn(jobDao);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		reset(configDao);
		Job job = jobManager.getJobList().get(0);
		Jobcenter jobcenter = mock(Jobcenter.class);
		JobSystemException e = mock(JobSystemException.class);
		when(e.getMessage()).thenReturn("my error message");
		doThrow(e).when(jobcenter).removeJob(job);
		when(jobcenterManager.getJobcenterList()).thenReturn(Arrays.asList(jobcenter));
		when(jobcenter.hasJob(job)).thenReturn(true);
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		jobManager.deleteJob(job);
		verify(jobDao).deleteSavefile();
		;
		verify(configDao).saveJobList(anyList());
		assertDoesNotThrow(() -> verify(ecoPlayer).leaveJob(job, false));
		verify(e).getMessage();
		assertEquals(0, jobManager.getJobList().size());
	}

	@Test
	public void getJobListTest() {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		JobDao jobDao = mock(JobDao.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getJobDao()).thenReturn(jobDao);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		List<Job> list = jobManager.getJobList();
		assertEquals(1, list.size());
		assertEquals("myJob", list.get(0).getName());
	}

	@Test
	public void getJobNameList() {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		JobDao jobDao = mock(JobDao.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getJobDao()).thenReturn(jobDao);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		assertDoesNotThrow(() -> jobManager.createJob("myJob2"));
		List<String> list = jobManager.getJobNameList();
		assertEquals(2, list.size());
		assertEquals("myJob", list.get(0));
		assertEquals("myJob2", list.get(1));
	}

	@Test
	public void getJobByNameTest() {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		JobDao jobDao = mock(JobDao.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getJobDao()).thenReturn(jobDao);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		assertDoesNotThrow(() -> assertEquals("myJob", jobManager.getJobByName("myJob").getName()));
	}

	@Test
	public void getJobByNameTestWithNoJob() {
		when(messageWrapper.getErrorString("does_not_exist", "myJob")).thenReturn("my error message");
		try {
			assertEquals("myJob", jobManager.getJobByName("myJob").getName());
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
			assertEquals("my error message", e.getMessage());
			assertEquals(1, e.getParams().length);
			assertEquals("myJob", e.getParams()[0]);
		}
	}
}
