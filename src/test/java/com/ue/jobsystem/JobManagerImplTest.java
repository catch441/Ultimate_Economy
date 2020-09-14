package com.ue.jobsystem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ue.common.utils.ComponentProvider;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServiceComponent;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.economyplayer.logic.impl.EconomyPlayerManagerImpl;
import com.ue.jobsyste.dataaccess.api.JobDao;
import com.ue.jobsystem.logic.api.Job;
import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.Jobcenter;
import com.ue.jobsystem.logic.api.JobcenterManager;
import com.ue.jobsystem.logic.api.JobsystemValidationHandler;
import com.ue.jobsystem.logic.impl.JobManagerImpl;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.jobsystem.logic.impl.JobcenterManagerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

@ExtendWith(MockitoExtension.class)
public class JobManagerImplTest {

	@InjectMocks
	JobManagerImpl jobManager;
	@Mock
	ComponentProvider componentProvider;
	@Mock
	JobcenterManager jobcenterManager;
	@Mock
	JobsystemValidationHandler validationHandler;
	@Mock
	EconomyPlayerManager ecoPlayerManager;
	@Mock
	MessageWrapper messageWrapper;

	@Test
	public void loadAllJobsTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			job.addBlock("STONE", 1);
			job.addFisherLootType("fish", 2);
			job.addMob("COW", 3);
			JobController.getJobList().clear();
			assertEquals(0, JobController.getJobList().size());
			JobController.loadAllJobs();
			assertEquals(1, JobController.getJobList().size());
			Job result = JobController.getJobList().get(0);
			assertEquals("myjob", result.getName());
			assertEquals(1, result.getBlockList().size());
			assertTrue(result.getBlockList().containsKey("STONE"));
			assertTrue(result.getBlockList().containsValue(1.0));
			assertEquals(1, result.getEntityList().size());
			assertTrue(result.getEntityList().containsKey("COW"));
			assertTrue(result.getEntityList().containsValue(3.0));
			assertEquals(1, result.getFisherList().size());
			assertTrue(result.getFisherList().containsKey("fish"));
			assertTrue(result.getFisherList().containsValue(2.0));
		} catch (GeneralEconomyException | JobSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void loadAllJobsTestWithLoadingError() {
		try {
			JobController.createJob("myjob");
			JobController.getJobList().get(0);
			JobController.getJobList().clear();
			assertEquals(0, JobController.getJobList().size());
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			file.delete();
			JobController.loadAllJobs();
			assertEquals(0, JobController.getJobList().size());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void createJobTest() {
		try {
			JobController.createJob("myjob");
			Job job = JobController.getJobList().get(0);
			assertEquals(1, JobController.getJobList().size());
			List<String> list = UltimateEconomy.getInstance.getConfig().getStringList("JobList");
			assertEquals(1, list.size());
			assertEquals("myjob", list.get(0));
			// check job
			assertEquals("myjob", job.getName());
			assertEquals(0, job.getBlockList().size());
			assertEquals(0, job.getEntityList().size());
			assertEquals(0, job.getFisherList().size());
			// check savefile
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			assertEquals("myjob", config.getString("Jobname"));
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void createJobTestWithSameName() {
		try {
			JobController.createJob("myjob");
			JobController.createJob("myjob");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§c§4myjob§c already exists!", e.getMessage());
		}
	}

	@Test
	public void removeJobFromAllPlayers() {
		try {
			JobController.createJob("myjob");
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			Job job = JobController.getJobList().get(0);
			ecoPlayer.joinJob(job, false);
			JobController.removeJobFromAllPlayers(job);
			assertEquals(0, ecoPlayer.getJobList().size());
		} catch (GeneralEconomyException | EconomyPlayerException | JobSystemException e) {
			assertTrue(false);
		}
	}

	@Test
	public void deleteJobTest() {
		try {
			JobcenterManagerImpl.createJobcenter("jobcenter", new Location(world, 1, 1, 1), 9);
			JobcenterManagerImpl.createJobcenter("other", new Location(world, 10, 1, 1), 9);
			JobController.createJob("myjob");
			Jobcenter jobcenter = JobcenterManagerImpl.getJobcenterList().get(0);
			Job job = JobController.getJobList().get(0);
			EconomyPlayer ecoPlayer = EconomyPlayerManagerImpl.getAllEconomyPlayers().get(0);
			jobcenter.addJob(job, "STONE", 0);
			ecoPlayer.joinJob(job, false);
			JobController.deleteJob(job);
			assertEquals(0, JobController.getJobList().size());
			assertEquals(0, jobcenter.getJobList().size());
			assertEquals(0, ecoPlayer.getJobList().size());
			// check savefile
			List<String> list = UltimateEconomy.getInstance.getConfig().getStringList("JobList");
			assertEquals(0, list.size());
			File file = new File(UltimateEconomy.getInstance.getDataFolder(), "myjob-Job.yml");
			if (file.exists()) {
				assertTrue(false);
			}
		} catch (JobSystemException | GeneralEconomyException | EconomyPlayerException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getJobListTest() {
		try {
			JobController.createJob("myjob");
			List<Job> list = JobController.getJobList();
			assertEquals(1, list.size());
			assertEquals("myjob", list.get(0).getName());
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getJobNameList() {
		try {
			JobController.createJob("myjob");
			JobController.createJob("myjob2");
			List<String> list = JobController.getJobNameList();
			assertEquals(2, list.size());
			assertEquals("myjob", list.get(0));
			assertEquals("myjob2", list.get(1));
		} catch (GeneralEconomyException e) {
			assertTrue(false);
		}
	}

	@Test
	public void getJobByNameTest() {
		ServiceComponent serviceComponent = mock(ServiceComponent.class);
		JobDao jobDao = mock(JobDao.class);
		when(componentProvider.getServiceComponent()).thenReturn(serviceComponent);
		when(serviceComponent.getJobDao()).thenReturn(jobDao);
		assertDoesNotThrow(() -> jobManager.createJob("myJob"));
		assertDoesNotThrow(() -> assertEquals("myJob", jobManager.getJobByName("myJob").getName()));
	}

	@Test
	public void getJobByNameTestWithNoJob() {
		try {
			JobController.getJobByName("myjob");
			assertTrue(false);
		} catch (GeneralEconomyException e) {
			assertTrue(e instanceof GeneralEconomyException);
			assertEquals("§c§4myjob§c does not exist!", e.getMessage());
		}
	}
}
