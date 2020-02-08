package com.ue.jobsystem.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.JobSystemException;
import com.ue.exceptions.PlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.api.JobcenterController;
import com.ue.language.MessageWrapper;
import com.ue.ultimate_economy.UltimateEconomy;

public class JobCommandExecutor implements CommandExecutor {

    private UltimateEconomy plugin;

    /**
     * Constructor for job command executor.
     * 
     * @param plugin
     */
    public JobCommandExecutor(UltimateEconomy plugin) {
	this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (sender instanceof Player) {
	    Player player = (Player) sender;
	    try {
		if ("jobcenter".equalsIgnoreCase(label)) {
		    if (args.length != 0) {
			switch (args[0]) {
			case "create":
			    handleCreateCommand(args, player);
			    break;
			case "delete":
			    handleDeleteCommand(args, player);
			    break;
			case "move":
			    handleMoveCommand(args, player);
			    break;
			case "addJob":
			    handleAddJobCommand(args, player);
			    break;
			case "removeJob":
			    handleRemoveJobCommand(args, player);
			    break;
			case "job":
			    handleJobCommands(args, player);
			    break;
			default:
			    return false;
			}
		    } else {
			return false;
		    }
		}
	    } catch (JobSystemException | PlayerException | GeneralEconomyException e) {
		player.sendMessage(e.getMessage());
	    } catch (NumberFormatException e) {
		player.sendMessage(MessageWrapper.getErrorString("invalid_parameter", ""));
	    }
	}
	return true;
    }

    private void handleJobCommands(String[] args, Player player) throws JobSystemException, GeneralEconomyException {
	if (args.length == 1) {
	    player.sendMessage(
		    "/jobcenter job [create/delJob/addItem/removeItem/" + "addMob/removeMob/addFisher/removeFisher]");
	} else {
	    switch (args[1]) {
	    case "create":
		handleCreateJobCommand(args, player);
		break;
	    case "delete":
		handleDeleteJobCommand(args, player);
		break;
	    case "addMob":
		handleAddMobCommand(args, player);
		break;
	    case "removeMob":
		handleRemoveCommand(args, player);
		break;
	    case "addItem":
		handleAddItemCommand(args, player);
		break;
	    case "removeItem":
		handleRemoveItemCommand(args, player);
		break;
	    case "addFisher":
		handleAddFisherCommand(args, player);
		break;
	    case "removeFisher":
		handleRemoveFisherCommand(args, player);
		break;
	    default:
		player.sendMessage("/jobcenter job [create/delete/addItem/addFisher"
			+ "/removeFisher/removeItem/addMob/removeMob]");
	    }
	}
    }

    private void handleRemoveFisherCommand(String[] args, Player player)
	    throws JobSystemException, GeneralEconomyException {
	if (args.length == 4) {
	    Job job = JobController.getJobByName(args[2]);
	    job.delFisherLootType(args[3]);
	    player.sendMessage(MessageWrapper.getString("jobcenter_removeFisher", args[3]));
	} else {
	    player.sendMessage("/jobcenter job removeFisher <jobname> <fish/treasure/junk>");
	}
    }

    private void handleAddFisherCommand(String[] args, Player player)
	    throws JobSystemException, GeneralEconomyException {
	if (args.length == 5) {
	    Job job = JobController.getJobByName(args[2]);
	    job.addFisherLootType(args[3], Double.valueOf(args[4]));
	    player.sendMessage(MessageWrapper.getString("jobcenter_addFisher", args[3]));
	} else {
	    player.sendMessage("/jobcenter job addFisher <job> [fish/treasure/junk] <price>");
	}
    }

    private void handleRemoveItemCommand(String[] args, Player player)
	    throws JobSystemException, GeneralEconomyException {
	if (args.length == 4) {
	    Job job = JobController.getJobByName(args[2]);
	    job.deleteItem(args[3]);
	    player.sendMessage(MessageWrapper.getString("jobcenter_removeItem", args[3]));
	} else {
	    player.sendMessage("/jobcenter job removeItem <job> <material>");
	}
    }

    private void handleAddItemCommand(String[] args, Player player) throws JobSystemException, GeneralEconomyException {
	if (args.length == 5) {
	    Job job = JobController.getJobByName(args[2]);
	    job.addItem(args[3], Double.valueOf(args[4]));
	    player.sendMessage(MessageWrapper.getString("jobcenter_addItem", args[3]));
	} else {
	    player.sendMessage("/jobcenter job addItem <job> <material> <price>");
	}
    }

    private void handleRemoveCommand(String[] args, Player player) throws JobSystemException, GeneralEconomyException {
	if (args.length == 4) {
	    Job job = JobController.getJobByName(args[2]);
	    job.deleteMob(args[3]);
	    player.sendMessage(MessageWrapper.getString("jobcenter_removeMob", args[3]));
	} else {
	    player.sendMessage("/jobcenter job removeMob <jobname> <entity>");
	}
    }

    private void handleAddMobCommand(String[] args, Player player) throws JobSystemException, GeneralEconomyException {
	if (args.length == 5) {
	    Job job = JobController.getJobByName(args[2]);
	    job.addMob(args[3], Double.valueOf(args[4]));
	    player.sendMessage(MessageWrapper.getString("jobcenter_addMob", args[3]));
	} else {
	    player.sendMessage("/jobcenter job addMob <job> <entity> <price>");
	}
    }

    private void handleDeleteJobCommand(String[] args, Player player) throws JobSystemException {
	if (args.length == 3) {
	    JobController.deleteJob(args[2]);
	    plugin.getConfig().set("JobList", JobController.getJobNameList());
	    plugin.saveConfig();
	    player.sendMessage(MessageWrapper.getString("jobcenter_delJob", args[2]));
	} else {
	    player.sendMessage("/jobcenter job delete <job>");
	}
    }

    private void handleCreateJobCommand(String[] args, Player player) throws JobSystemException {
	if (args.length == 3) {
	    JobController.createJob(plugin.getDataFolder(), args[2]);
	    plugin.getConfig().set("JobList", JobController.getJobNameList());
	    plugin.saveConfig();
	    player.sendMessage(MessageWrapper.getString("jobcenter_createJob", args[2]));
	} else {
	    player.sendMessage("/jobcenter job create <job>");
	}
    }

    private void handleRemoveJobCommand(String[] args, Player player) throws JobSystemException {
	if (args.length == 3) {
	    Jobcenter jobcenter = JobcenterController.getJobCenterByName(args[1]);
	    Job job = JobController.getJobByName(args[2]);
	    jobcenter.removeJob(job);
	    player.sendMessage(MessageWrapper.getString("jobcenter_removeJob", args[2]));
	} else {
	    player.sendMessage("/jobcenter removeJob <jobcenter> <job>");
	}
    }

    private void handleAddJobCommand(String[] args, Player player)
	    throws JobSystemException, PlayerException, GeneralEconomyException {
	if (args.length == 5) {
	    Jobcenter jobcenter = JobcenterController.getJobCenterByName(args[1]);
	    Job job = JobController.getJobByName(args[2]);
	    jobcenter.addJob(job, args[3], Integer.valueOf(args[4]));
	    // TODO aus messages holen
	    player.sendMessage(ChatColor.GOLD + "The job " + ChatColor.GREEN + args[2] + ChatColor.GOLD
		    + " was added to the JobCenter " + ChatColor.GREEN + args[1] + ".");
	} else {
	    player.sendMessage("/jobcenter addJob <jobcenter> <job> <material> <slot>");
	}
    }

    private void handleMoveCommand(String[] args, Player player) throws JobSystemException {
	if (args.length == 2) {
	    Jobcenter jobcenter = JobcenterController.getJobCenterByName(args[1]);
	    jobcenter.moveJobCenter(player.getLocation());
	} else {
	    player.sendMessage("/jobcenter move <jobcenter>");
	}
    }

    private void handleDeleteCommand(String[] args, Player player) throws JobSystemException {
	if (args.length == 2) {
	    JobcenterController.deleteJobCenter(JobcenterController.getJobCenterByName(args[1]));
	    plugin.getConfig().set("JobCenterNames", JobcenterController.getJobCenterNameList());
	    plugin.saveConfig();
	    player.sendMessage(MessageWrapper.getString("jobcenter_delete", args[1]));
	} else {
	    player.sendMessage("/jobcenter delete <jobcenter>");
	}
    }

    private void handleCreateCommand(String[] args, Player player) throws JobSystemException, GeneralEconomyException {
	if (args.length == 3) {
	    JobcenterController.createJobCenter(plugin.getServer(), plugin.getDataFolder(), args[1],
		    player.getLocation(), Integer.parseInt(args[2]));
	    plugin.getConfig().set("JobCenterNames", JobcenterController.getJobCenterNameList());
	    plugin.saveConfig();
	    player.sendMessage(MessageWrapper.getString("jobcenter_create", args[1]));
	} else {
	    player.sendMessage("/jobcenter create <jobcenter> <size> <- size have to be a multible of 9");
	}
    }
}
