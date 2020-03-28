package com.ue.jobsystem.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;

public interface Jobcenter {

    /**
     * This method adds a job to this jobcenter.
     * 
     * @param job
     * @param itemMaterial
     * @param slot
     * @throws JobSystemException
     * @throws PlayerException
     * @throws GeneralEconomyException
     */
    public void addJob(Job job, String itemMaterial, int slot)
	    throws JobSystemException, PlayerException, GeneralEconomyException;

    /**
     * This method removes a job from this jobcenter.
     * 
     * @param job
     * @throws JobSystemException
     */
    public void removeJob(Job job) throws JobSystemException;

    /**
     * This method moves a jobcenter villager to a other location.
     * 
     * @param location
     */
    public void moveJobCenter(Location location);

    /**
     * This method returns the name of this jobcenter.
     * 
     * @return String
     */
    public String getName();

    /**
     * This method despawns the jobcenter villager.
     */
    public void despawnVillager();

    /**
     * This method returns a list of all jobs in this jobcenter.
     * 
     * @return List of jobs
     */
    public List<Job> getJobList();

    /**
     * This method opens the jobcenter inventory.
     * 
     * @param player
     */
    public void openInv(Player player);

    /**
     * This method returns true if this jobcenter contains this job.
     * 
     * @param job
     * @return boolean
     */
    public boolean hasJob(Job job);

    /**
     * Deletes savefile and despawns villager.
     */
    public void deleteJobCenter();
}
