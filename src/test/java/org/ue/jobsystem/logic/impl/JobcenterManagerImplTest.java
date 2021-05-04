package org.ue.jobsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.ServiceComponent;
import org.ue.config.dataaccess.api.ConfigDao;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyplayer.logic.api.EconomyPlayerException;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobsystemException;
import org.ue.jobsystem.logic.api.JobsystemValidationHandler;

@ExtendWith(MockitoExtension.class)
public class JobcenterManagerImplTest {

	@InjectMocks
	JobcenterManagerImpl manager;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	ServerProvider serverProvider;
	@Mock
	ConfigDao configDao;
	@Mock
	JobsystemValidationHandler validator;

	@Test
	public void getJobcenterByNameTest() {
		createJobcenter("other");
		Jobcenter center = createJobcenter("center");
		Jobcenter result = assertDoesNotThrow(() -> manager.getJobcenterByName("center"));
		assertEquals(center, result);
	}

	private Jobcenter createJobcenter(String name) {
		Location location = mock(Location.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Jobcenter center = mock(Jobcenter.class);
		when(serviceComponent.getJobcenter()).thenReturn(center);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> manager.createJobcenter(name, location, 9));
		return center;
	}

	@Test
	public void getJobcenterByNameTestWithNoJobcenter() throws JobsystemException {
		doThrow(JobsystemException.class).when(validator).checkForValueExists(null, "center");
		assertThrows(JobsystemException.class, () -> manager.getJobcenterByName("center"));
	}

	@Test
	public void getJobcenterNameListTest() {
		createJobcenter("center");
		List<String> list = manager.getJobcenterNameList();
		assertEquals(1, list.size());
		assertEquals("center", list.get(0));
	}

	@Test
	public void getJobcenterListTest() {
		Jobcenter center = createJobcenter("center");
		List<Jobcenter> centers = manager.getJobcenterList();
		assertEquals(1, centers.size());
		assertEquals(center, centers.get(0));
	}

	@Test
	public void despawnAllVillagersTest() {
		Jobcenter center = createJobcenter("center");
		manager.despawnAllVillagers();
		verify(center).despawn();
		;
	}

	@Test
	public void deleteJobcenterTest() {
		Jobcenter jobcenter = createJobcenter("center0");
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		when(jobcenter.getName()).thenReturn("center0");
		when(jobcenter.getJobList()).thenReturn(Arrays.asList(job0, job));
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		when(ecoPlayer.hasJob(job0)).thenReturn(true);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		assertEquals(1, manager.getJobcenterList().size());
		assertDoesNotThrow(() -> manager.deleteJobcenter(jobcenter));
		verify(jobcenter).deleteJobcenter();
		assertEquals(0, manager.getJobcenterList().size());
		assertDoesNotThrow(() -> verify(ecoPlayer).leaveJob(job, false));
		verify(configDao).saveJobcenterList(new ArrayList<>());
	}

	@Test
	public void deleteJobcenterTestWithFailedToLeaveJob() throws EconomyPlayerException {
		Jobcenter jobcenter = mock(Jobcenter.class);
		Job job = mock(Job.class);
		when(jobcenter.getJobList()).thenReturn(Arrays.asList(job));
		manager.getJobcenterList().add(jobcenter);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		EconomyPlayerException e = mock(EconomyPlayerException.class);
		when(job.getName()).thenReturn("myJob");
		when(e.getMessage()).thenReturn("my error message");
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
		doThrow(e).when(ecoPlayer).leaveJob(job, false);
		assertDoesNotThrow(() -> manager.deleteJobcenter(jobcenter));
		verify(jobcenter).deleteJobcenter();
		assertEquals(0, manager.getJobcenterList().size());
		assertThrows(EconomyPlayerException.class, () -> ecoPlayer.leaveJob(job, false));
		verify(configDao).saveJobcenterList(new ArrayList<>());
		verify(e).getMessage();
	}

	@Test
	public void deleteJobcenterTestWithJobInOtherJobcenter() {
		Jobcenter center0 = createJobcenter("center0");
		Jobcenter center1 = createJobcenter("center1");
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		when(center1.getName()).thenReturn("center1");
		when(center0.hasJob(job)).thenReturn(true);
		when(center0.hasJob(job0)).thenReturn(true);
		when(center1.getJobList()).thenReturn(Arrays.asList(job0, job));
		reset(configDao);
		
		assertDoesNotThrow(() -> manager.deleteJobcenter(center1));
		verify(center1).deleteJobcenter();
		assertEquals(1, manager.getJobcenterList().size());
		verify(configDao).saveJobcenterList(Arrays.asList("center0"));
		assertDoesNotThrow(() -> verify(ecoPlayerManager, never()).getAllEconomyPlayers());
	}

	@Test
	public void createJobcenterTestWithAlreadyExists() throws JobsystemException {
		doThrow(JobsystemException.class).when(validator).checkForValueNotInList(new ArrayList<>(),
				"center");
		assertThrows(JobsystemException.class, () -> manager.createJobcenter("center", null, 9));
		assertEquals(0, manager.getJobcenterList().size());
		verify(configDao, never()).saveJobcenterList(anyList());
	}

	@Test
	public void createJobcenterTestWithInvalidSize() throws JobsystemException {
		doThrow(JobsystemException.class).when(validator).checkForValidSize(9);
		assertThrows(JobsystemException.class, () -> manager.createJobcenter("center", null, 9));
		assertEquals(0, manager.getJobcenterList().size());
		verify(configDao, never()).saveJobcenterList(anyList());
	}

	@Test
	public void createJobcenterTest() {
		Location location = mock(Location.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Jobcenter center = mock(Jobcenter.class);
		when(serviceComponent.getJobcenter()).thenReturn(center);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> manager.createJobcenter("center", location, 9));
		assertDoesNotThrow(() -> verify(validator).checkForValueNotInList(new ArrayList<>(), "center"));
		assertDoesNotThrow(() -> verify(validator).checkForValidSize(9));
		verify(configDao).saveJobcenterList(Arrays.asList("center"));
		Jobcenter result = manager.getJobcenterList().get(0);
		assertEquals(center, result);
		verify(center).setupNew("center", location, 9);
	}

	@Test
	public void loadAllJobcenterTest() {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(configDao.loadJobcenterList()).thenReturn(Arrays.asList("center"));
		Jobcenter center = mock(Jobcenter.class);
		when(serviceComponent.getJobcenter()).thenReturn(center);
		manager.loadAllJobcenters();
		Jobcenter result = manager.getJobcenterList().get(0);
		assertEquals(center, result);
		assertDoesNotThrow(() -> verify(center).setupExisting("center"));
	}
}
