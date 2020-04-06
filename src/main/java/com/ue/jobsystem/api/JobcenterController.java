package com.ue.jobsystem.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyExceptionMessageEnum;
import com.ue.exceptions.JobSystemException;
import com.ue.jobsystem.impl.JobcenterImpl;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobcenterController {

    private static List<Jobcenter> jobCenterList = new ArrayList<>();

    /**
     * This method returns a jobcenter by it's name.
     * 
     * @param name
     * @return JobCenter
     * @throws GeneralEconomyException
     */
    public static Jobcenter getJobcenterByName(String name) throws GeneralEconomyException {
	for (Jobcenter jobcenter : getJobcenterList()) {
	    if (jobcenter.getName().equals(name)) {
		return jobcenter;
	    }
	}
	throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.DOES_NOT_EXIST, name);
    }

    /**
     * This method returns a namelist of all jobcenters.
     * 
     * @return List of Strings
     */
    public static List<String> getJobcenterNameList() {
	List<String> jobCenterNames = new ArrayList<>();
	for (Jobcenter jobcenter : getJobcenterList()) {
	    jobCenterNames.add(jobcenter.getName());
	}
	return jobCenterNames;
    }

    /**
     * This method returns a list of all existing jobcenters.
     * 
     * @return List of JobCenters
     */
    public static List<Jobcenter> getJobcenterList() {
	return jobCenterList;
    }

    /**
     * This method should me used to delete a jobcenter.
     * 
     * @param jobcenter
     * @throws JobSystemException
     */
    public static void deleteJobcenter(Jobcenter jobcenter) throws JobSystemException {
	jobcenter.deleteJobcenter();
	getJobcenterList().remove(jobcenter);
	for (Job job : jobcenter.getJobList()) {
	    if (!otherJobcenterHasJob(job)) {
		JobController.removeJobFromAllPlayers(job);
	    }
	}
	saveJobcenterNameList();
    }

    private static boolean otherJobcenterHasJob(Job job) throws JobSystemException {
	for (Jobcenter jobCenter2 : getJobcenterList()) {
	    if (jobCenter2.hasJob(job)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * This method should be used to create a new jobcenter.
     * 
     * @param name
     * @param spawnLocation
     * @param size
     * @throws JobSystemException
     * @throws GeneralEconomyException
     */
    public static void createJobcenter(String name, Location spawnLocation, int size)
	    throws JobSystemException, GeneralEconomyException {
	checkForJobcenterNameDoesNotExist(name);
	checkForValidSize(size);
	getJobcenterList().add(new JobcenterImpl(name, spawnLocation, size));
	saveJobcenterNameList();
    }

    /**
     * This method loads all jobcenters from the save files. !!!
     * JobController.loadAllJobs() have to be executed before this method. !!!
     */
    public static void loadAllJobcenters() {
	for (String jobCenterName : UltimateEconomy.getInstance.getConfig().getStringList("JobCenterNames")) {
	    File file = new File(UltimateEconomy.getInstance.getDataFolder(), jobCenterName + "-JobCenter.yml");
	    if (file.exists()) {
		getJobcenterList().add(new JobcenterImpl(jobCenterName));
	    } else {
		Bukkit.getLogger().warning("[Ultimate_Economy] Failed to load the jobcenter " + jobCenterName);
		Bukkit.getLogger().warning("[Ultimate_Economy] Caused by: No savefile found!");
	    }
	}
    }

    /**
     * This method despawns all jobcenter villager.
     */
    public static void despawnAllVillagers() {
	for (Jobcenter jobcenter : getJobcenterList()) {
	    jobcenter.despawnVillager();
	}
    }
    
    /*
     * Save methods
     * 
     */
    
    private static void saveJobcenterNameList() {
	UltimateEconomy.getInstance.getConfig().set("JobCenterNames", JobcenterController.getJobcenterNameList());
	UltimateEconomy.getInstance.saveConfig();
    }

    /*
     * Validation check methods
     * 
     */

    private static void checkForValidSize(int size) throws GeneralEconomyException {
	if (size % 9 != 0) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.INVALID_PARAMETER, size);
	}
    }

    private static void checkForJobcenterNameDoesNotExist(String name) throws GeneralEconomyException {
	if (getJobcenterNameList().contains(name)) {
	    throw GeneralEconomyException.getException(GeneralEconomyExceptionMessageEnum.ALREADY_EXISTS, name);
	}
    }
}
