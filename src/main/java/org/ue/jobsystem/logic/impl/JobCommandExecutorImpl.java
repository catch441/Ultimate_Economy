package org.ue.jobsystem.logic.impl;

import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.jobsystem.logic.JobCommandEnum;
import org.ue.jobsystem.logic.JobSystemException;
import org.ue.jobsystem.logic.api.Job;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.Jobcenter;
import org.ue.jobsystem.logic.api.JobcenterManager;

public class JobCommandExecutorImpl implements CommandExecutor {

	private final JobManager jobManager;
	private final MessageWrapper messageWrapper;
	private final JobcenterManager jobcenterManager;
	private final ConfigManager configManager;
	
	@Inject
	public JobCommandExecutorImpl(ConfigManager configManager, JobcenterManager jobcenterManager, JobManager jobManager,
			MessageWrapper messageWrapper) {
		this.jobManager = jobManager;
		this.messageWrapper = messageWrapper;
		this.jobcenterManager = jobcenterManager;
		this.configManager = configManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				return handleCommand(label, args, player);
			} catch (JobSystemException | EconomyPlayerException | GeneralEconomyException e) {
				player.sendMessage(e.getMessage());
			} catch (NumberFormatException e) {
				player.sendMessage(messageWrapper.getErrorString("invalid_parameter", "number"));
			}
		}
		return true;
	}

	private boolean handleCommand(String label, String[] args, Player player)
			throws NumberFormatException, GeneralEconomyException, JobSystemException, EconomyPlayerException {
		switch (label) {
		case "joblist":
			return handleJobListCommand(args, player);
		case "jobinfo":
			return handleJobInfoCommand(args, player);
		case "jobcenter":
			if (args.length != 0) {
				return performJobcenterCommand(label, args, player);
			}
			return false;
		default:
			return false;
		}
	}

	private boolean handleJobListCommand(String[] args, Player player) {
		if (args.length == 0) {
			List<String> jobNames = jobManager.getJobNameList();
			player.sendMessage(messageWrapper.getString("joblist_info", jobNames.toString()));
			return true;
		}
		return false;
	}

	private boolean handleJobInfoCommand(String[] args, Player player)
			throws JobSystemException, GeneralEconomyException {
		if (args.length == 1) {
			Job job = jobManager.getJobByName(args[0]);
			player.sendMessage(messageWrapper.getString("jobinfo_info", job.getName()));
			sendBlockInfo(player, job);
			sendFisherInfo(player, job);
			sendEntityInfo(player, job);
			sendBreedableInfo(player, job);
		} else {
			return false;
		}
		return true;
	}

	private void sendBreedableInfo(Player player, Job job) {
		for (Entry<String, Double> entry : job.getBreedableList().entrySet()) {
			player.sendMessage(messageWrapper.getString("jobinfo_breedprice", entry.getKey().toLowerCase(),
					entry.getValue(), configManager.getCurrencyText(entry.getValue())));
		}
	}

	private void sendEntityInfo(Player player, Job job) {
		for (Entry<String, Double> entry : job.getEntityList().entrySet()) {
			player.sendMessage(messageWrapper.getString("jobinfo_killprice", entry.getKey().toLowerCase(),
					entry.getValue(), configManager.getCurrencyText(entry.getValue())));
		}
	}

	private void sendFisherInfo(Player player, Job job) {
		for (Entry<String, Double> entry : job.getFisherList().entrySet()) {
			player.sendMessage(messageWrapper.getString("jobinfo_fishingprice", entry.getKey().toLowerCase(),
					entry.getValue(), configManager.getCurrencyText(entry.getValue())));
		}
	}

	private void sendBlockInfo(Player player, Job job) {
		for (Entry<String, Double> entry : job.getBlockList().entrySet()) {
			player.sendMessage(ChatColor.GOLD + entry.getKey().toLowerCase() + " " + ChatColor.GREEN + entry.getValue()
					+ configManager.getCurrencyText(entry.getValue()));
		}
	}

	private boolean performJobcenterCommand(String label, String[] args, Player player)
			throws NumberFormatException, GeneralEconomyException, JobSystemException, EconomyPlayerException {
		switch (JobCommandEnum.getEnum(args[0])) {
		case ADDJOB:
			return performAddJobCommand(label, args, player);
		case CREATE:
			return performCreateCommand(label, args, player);
		case DELETE:
			return performDeleteCommand(label, args, player);
		case JOB:
			return performJobCommand(label, args, player);
		case MOVE:
			return performMoveCommand(label, args, player);
		case REMOVEJOB:
			return performRemoveJobCommand(label, args, player);
		default:
			return false;
		}
	}

	private boolean performCreateCommand(String label, String[] args, Player player)
			throws NumberFormatException, JobSystemException, GeneralEconomyException {
		if (args.length == 3) {
			jobcenterManager.createJobcenter(args[1], player.getLocation(), Integer.parseInt(args[2]));
			player.sendMessage(messageWrapper.getString("created", args[1]));
		} else {
			player.sendMessage("/jobcenter create <jobcenter> <size> <- size have to be a multible of 9");
		}
		return true;
	}

	private boolean performDeleteCommand(String label, String[] args, Player player)
			throws JobSystemException, GeneralEconomyException {
		if (args.length == 2) {
			jobcenterManager.deleteJobcenter(jobcenterManager.getJobcenterByName(args[1]));
			player.sendMessage(messageWrapper.getString("deleted", args[1]));
		} else {
			player.sendMessage("/jobcenter delete <jobcenter>");
		}
		return true;
	}

	private boolean performMoveCommand(String label, String[] args, Player player) throws GeneralEconomyException {
		if (args.length == 2) {
			Jobcenter jobcenter = jobcenterManager.getJobcenterByName(args[1]);
			jobcenter.moveJobcenter(player.getLocation());
		} else {
			player.sendMessage("/jobcenter move <jobcenter>");
		}
		return true;
	}

	private boolean performAddJobCommand(String label, String[] args, Player player)
			throws GeneralEconomyException, NumberFormatException, JobSystemException, EconomyPlayerException {
		if (args.length == 5) {
			Jobcenter jobcenter = jobcenterManager.getJobcenterByName(args[1]);
			Job job = jobManager.getJobByName(args[2]);
			jobcenter.addJob(job, args[3], Integer.valueOf(args[4]) - 1);
			player.sendMessage(messageWrapper.getString("added", args[2]));
		} else {
			player.sendMessage("/jobcenter addJob <jobcenter> <job> <material> <slot>");
		}
		return true;
	}

	private boolean performRemoveJobCommand(String label, String[] args, Player player)
			throws JobSystemException, GeneralEconomyException {
		if (args.length == 3) {
			Jobcenter jobcenter = jobcenterManager.getJobcenterByName(args[1]);
			Job job = jobManager.getJobByName(args[2]);
			jobcenter.removeJob(job);
			player.sendMessage(messageWrapper.getString("removed", args[2]));
		} else {
			player.sendMessage("/jobcenter removeJob <jobcenter> <job>");
		}
		return true;
	}

	private boolean performJobCreateCommand(String label, String[] args, Player player) throws GeneralEconomyException {
		if (args.length == 3) {
			jobManager.createJob(args[2]);
			player.sendMessage(messageWrapper.getString("created", args[2]));
		} else {
			player.sendMessage("/jobcenter job create <job>");
		}
		return true;
	}

	private boolean performJobDeleteCommand(String label, String[] args, Player player) throws GeneralEconomyException {
		if (args.length == 3) {
			jobManager.deleteJob(jobManager.getJobByName(args[2]));
			player.sendMessage(messageWrapper.getString("deleted", args[2]));
		} else {
			player.sendMessage("/jobcenter job delete <job>");
		}
		return true;
	}

	private boolean performJobAddFisherCommand(String label, String[] args, Player player)
			throws NumberFormatException, JobSystemException, GeneralEconomyException {
		if (args.length == 5) {
			Job job = jobManager.getJobByName(args[2]);
			job.addFisherLootType(args[3], Double.valueOf(args[4]));
			player.sendMessage(messageWrapper.getString("added", args[3]));
		} else {
			player.sendMessage("/jobcenter job addFisher <job> [fish/treasure/junk] <price>");
		}
		return true;
	}

	private boolean performJobRemoveFisherCommand(String label, String[] args, Player player)
			throws GeneralEconomyException, JobSystemException {
		if (args.length == 4) {
			Job job = jobManager.getJobByName(args[2]);
			job.removeFisherLootType(args[3]);
			player.sendMessage(messageWrapper.getString("removed", args[3]));
		} else {
			player.sendMessage("/jobcenter job removeFisher <jobname> <fish/treasure/junk>");
		}
		return true;
	}

	private boolean performJobAddItemCommand(String label, String[] args, Player player)
			throws NumberFormatException, JobSystemException, GeneralEconomyException {
		if (args.length == 5) {
			Job job = jobManager.getJobByName(args[2]);
			job.addBlock(args[3], Double.valueOf(args[4]));
			player.sendMessage(messageWrapper.getString("added", args[3]));
		} else {
			player.sendMessage("/jobcenter job addItem <job> <material> <price>");
		}
		return true;
	}

	private boolean performJobRemoveItemCommand(String label, String[] args, Player player)
			throws JobSystemException, GeneralEconomyException {
		if (args.length == 4) {
			Job job = jobManager.getJobByName(args[2]);
			job.deleteBlock(args[3]);
			player.sendMessage(messageWrapper.getString("removed", args[3]));
		} else {
			player.sendMessage("/jobcenter job removeItem <job> <material>");
		}
		return true;
	}

	private boolean performJobAddMobCommand(String label, String[] args, Player player)
			throws GeneralEconomyException, NumberFormatException, JobSystemException {
		if (args.length == 5) {
			Job job = jobManager.getJobByName(args[2]);
			job.addMob(args[3], Double.valueOf(args[4]));
			player.sendMessage(messageWrapper.getString("added", args[3]));
		} else {
			player.sendMessage("/jobcenter job addMob <job> <entity> <price>");
		}
		return true;
	}

	private boolean performJobRemoveMobCommand(String label, String[] args, Player player)
			throws GeneralEconomyException, JobSystemException {
		if (args.length == 4) {
			Job job = jobManager.getJobByName(args[2]);
			job.deleteMob(args[3]);
			player.sendMessage(messageWrapper.getString("removed", args[3]));
		} else {
			player.sendMessage("/jobcenter job removeMob <jobname> <entity>");
		}
		return true;
	}

	private boolean performJobAddBreedableCommand(String label, String[] args, Player player)
			throws GeneralEconomyException, NumberFormatException, JobSystemException {
		if (args.length == 5) {
			Job job = jobManager.getJobByName(args[2]);
			EntityType entity = null;
			try {
				entity = EntityType.valueOf(args[3].toUpperCase());
			} catch (IllegalArgumentException e) {
				player.sendMessage(messageWrapper.getErrorString("invalid_parameter", args[3]));
				return true;
			}
			job.addBreedable(entity, Double.valueOf(args[4]));
			player.sendMessage(messageWrapper.getString("added", args[3]));
		} else {
			player.sendMessage("/jobcenter job addBreedable <job> <entity> <price>");
		}
		return true;
	}

	private boolean performJobRemoveBreedableCommand(String label, String[] args, Player player)
			throws GeneralEconomyException, JobSystemException {
		if (args.length == 4) {
			try {
				Job job = jobManager.getJobByName(args[2]);
				job.deleteBreedable(EntityType.valueOf(args[3].toUpperCase()));
				player.sendMessage(messageWrapper.getString("removed", args[3]));
			} catch (IllegalArgumentException e) {
				player.sendMessage(messageWrapper.getErrorString("invalid_parameter", args[3]));
			}
		} else {
			player.sendMessage("/jobcenter job removeBreedable <jobname> <entity>");
		}
		return true;
	}

	private boolean performJobCommand(String label, String[] args, Player player)
			throws GeneralEconomyException, JobSystemException {
		if (args.length == 1) {
			player.sendMessage("/jobcenter job [create/delete/addItem/removeItem/"
					+ "addMob/removeMob/addFisher/removeFisher/addBreedable/removeBreedable]");
		} else {
			JobCommandEnum commandEnum = JobCommandEnum.getEnum("JOB_" + args[1]);
			switch (commandEnum) {
			case JOB_ADDFISHER:
				return performJobAddFisherCommand(label, args, player);
			case JOB_ADDITEM:
				return performJobAddItemCommand(label, args, player);
			case JOB_ADDMOB:
				return performJobAddMobCommand(label, args, player);
			case JOB_CREATE:
				return performJobCreateCommand(label, args, player);
			case JOB_DELETE:
				return performJobDeleteCommand(label, args, player);
			case JOB_REMOVEFISHER:
				return performJobRemoveFisherCommand(label, args, player);
			case JOB_REMOVEITEM:
				return performJobRemoveItemCommand(label, args, player);
			case JOB_REMOVEMOB:
				return performJobRemoveMobCommand(label, args, player);
			case JOB_ADDBREEDABLE:
				return performJobAddBreedableCommand(label, args, player);
			case JOB_REMOVEBREEDABLE:
				return performJobRemoveBreedableCommand(label, args, player);
			default:
				player.sendMessage("/jobcenter job [create/delete/addItem/removeItem/"
						+ "addMob/removeMob/addFisher/removeFisher/addBreedable/removeBreedable]");
				return true;
			}
		}
		return true;
	}
}
