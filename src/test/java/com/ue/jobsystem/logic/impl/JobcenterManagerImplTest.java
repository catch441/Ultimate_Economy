package com.ue.jobsystem.logic.impl;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import org.slf4j.Logger;

import com.ue.common.utils.ServerProvider;
import com.ue.common.utils.ServiceComponent;
import com.ue.common.utils.MessageWrapper;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.general.impl.GeneralEconomyExceptionMessageEnum;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.Jobcenter;

@ExtendWith(MockitoExtension.class)
public class JobcenterManagerImplTest {

	@InjectMocks
	JobcenterManagerImpl manager;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	ServerProvider serverProvider;
	@Mock
	ConfigDao configDao;
	@Mock
	Logger logger;
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	@Test
	public void getJobcenterByNameTest() {
		Jobcenter center1 = createJobcenter("other");
		Jobcenter center2 = createJobcenter("center");
		when(center1.getName()).thenReturn("other");
		when(center2.getName()).thenReturn("center");
		Jobcenter result = assertDoesNotThrow(() -> manager.getJobcenterByName("center"));
		assertEquals(center2, result);
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
	public void getJobcenterByNameTestWithNoJobcenter() {
		try {
			manager.getJobcenterByName("center");
			fail();
		} catch (GeneralEconomyException e) {
			assertEquals(1, e.getParams().length);
			assertEquals("center", e.getParams()[0]);
			assertEquals(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, e.getKey());
		}
	}

	@Test
	public void getJobcenterNameListTest() {
		Jobcenter center = createJobcenter("center");
		when(center.getName()).thenReturn("center");
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
		verify(center).despawnVillager();;
	}

	@Test
	public void deleteJobcenterTest() {
		Jobcenter jobcenter = mock(Jobcenter.class);
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		when(jobcenter.getJobList()).thenReturn(Arrays.asList(job0, job));
		manager.getJobcenterList().add(jobcenter);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		when(ecoPlayer.hasJob(job)).thenReturn(true);
		when(ecoPlayer.hasJob(job0)).thenReturn(true);
		when(ecoPlayerManager.getAllEconomyPlayers()).thenReturn(Arrays.asList(ecoPlayer));
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
		verify(logger).warn("[Ultimate_Economy] Failed to leave the job myJob");
		verify(logger).warn("[Ultimate_Economy] Caused by: my error message");
	}
	
	@Test
	public void deleteJobcenterTestWithJobInOtherJobcenter() {
		Jobcenter jobcenter0 = mock(Jobcenter.class);
		when(jobcenter0.getName()).thenReturn("center0");
		Jobcenter jobcenter = mock(Jobcenter.class);
		Job job0 = mock(Job.class);
		Job job = mock(Job.class);
		when(jobcenter0.hasJob(job)).thenReturn(true);
		when(jobcenter0.hasJob(job0)).thenReturn(true);
		when(jobcenter.getJobList()).thenReturn(Arrays.asList(job0, job));
		manager.getJobcenterList().add(jobcenter);
		manager.getJobcenterList().add(jobcenter0);
		assertDoesNotThrow(() -> manager.deleteJobcenter(jobcenter));
		verify(jobcenter).deleteJobcenter();
		assertEquals(1, manager.getJobcenterList().size());
		verify(configDao).saveJobcenterList(Arrays.asList("center0"));
		assertDoesNotThrow(() -> verify(ecoPlayerManager, never()).getAllEconomyPlayers());
	}

	@Test
	public void createJobcenterTestWithAlreadyExists() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValueNotInList(new ArrayList<>(), "center");
		assertThrows(GeneralEconomyException.class, () -> manager.createJobcenter("center", null, 9));
		assertEquals(0, manager.getJobcenterList().size());
		verify(configDao, never()).saveJobcenterList(anyList());
	}

	@Test
	public void createJobcenterTestWithInvalidSize() throws GeneralEconomyException {
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSize(9);
		assertThrows(GeneralEconomyException.class, () -> manager.createJobcenter("center", null, 9));
		assertEquals(0, manager.getJobcenterList().size());
		verify(configDao, never()).saveJobcenterList(anyList());
	}

	@Test
	public void createJobcenterTest() {
		Location location = mock(Location.class);
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		Jobcenter center = mock(Jobcenter.class);
		when(center.getName()).thenReturn("center");
		when(serviceComponent.getJobcenter()).thenReturn(center);
		when(serverProvider.getServiceComponent()).thenReturn(serviceComponent);
		assertDoesNotThrow(() -> manager.createJobcenter("center", location, 9));
		assertDoesNotThrow(
				() -> verify(generalValidator).checkForValueNotInList(new ArrayList<>(), "center"));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSize(9));
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
		verify(center).setupExisting("center");
	}
}
