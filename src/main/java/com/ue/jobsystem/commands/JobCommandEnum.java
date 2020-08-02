package com.ue.jobsystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.ue.common.utils.MessageWrapper;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.logic.impl.JobSystemException;
import com.ue.jobsystem.logic.impl.JobcenterManagerImpl;
import com.ue.ultimate_economy.GeneralEconomyException;

public enum JobCommandEnum {

	CREATE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 3) {
				JobcenterManagerImpl.createJobcenter(args[1], player.getLocation(), Integer.parseInt(args[2]));
				player.sendMessage(MessageWrapper.getString("jobcenter_create", args[1]));
			} else {
				player.sendMessage("/jobcenter create <jobcenter> <size> <- size have to be a multible of 9");
			}
			return true;
		}
	},
	DELETE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 2) {
				JobcenterManagerImpl.deleteJobcenter(JobcenterManagerImpl.getJobcenterByName(args[1]));
				player.sendMessage(MessageWrapper.getString("jobcenter_delete", args[1]));
			} else {
				player.sendMessage("/jobcenter delete <jobcenter>");
			}
			return true;
		}
	},
	MOVE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 2) {
				Jobcenter jobcenter = JobcenterManagerImpl.getJobcenterByName(args[1]);
				jobcenter.moveJobcenter(player.getLocation());
			} else {
				player.sendMessage("/jobcenter move <jobcenter>");
			}
			return true;
		}
	},
	ADDJOB {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 5) {
				Jobcenter jobcenter = JobcenterManagerImpl.getJobcenterByName(args[1]);
				Job job = JobController.getJobByName(args[2]);
				jobcenter.addJob(job, args[3], Integer.valueOf(args[4]) - 1);
				// TODO aus messages holen
				player.sendMessage(ChatColor.GOLD + "The job " + ChatColor.GREEN + args[2] + ChatColor.GOLD
						+ " was added to the JobCenter " + ChatColor.GREEN + args[1] + ".");
			} else {
				player.sendMessage("/jobcenter addJob <jobcenter> <job> <material> <slot>");
			}
			return true;
		}
	},
	REMOVEJOB {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 3) {
				Jobcenter jobcenter = JobcenterManagerImpl.getJobcenterByName(args[1]);
				Job job = JobController.getJobByName(args[2]);
				jobcenter.removeJob(job);
				player.sendMessage(MessageWrapper.getString("jobcenter_removeJob", args[2]));
			} else {
				player.sendMessage("/jobcenter removeJob <jobcenter> <job>");
			}
			return true;
		}
	},
	JOB_CREATE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 3) {
				JobController.createJob(args[2]);
				player.sendMessage(MessageWrapper.getString("jobcenter_createJob", args[2]));
			} else {
				player.sendMessage("/jobcenter job create <job>");
			}
			return true;
		}
	},
	JOB_DELETE {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 3) {
				JobController.deleteJob(JobController.getJobByName(args[2]));
				player.sendMessage(MessageWrapper.getString("jobcenter_delJob", args[2]));
			} else {
				player.sendMessage("/jobcenter job delete <job>");
			}
			return true;
		}
	},
	JOB_ADDFISHER {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 5) {
				Job job = JobController.getJobByName(args[2]);
				job.addFisherLootType(args[3], Double.valueOf(args[4]));
				player.sendMessage(MessageWrapper.getString("jobcenter_addFisher", args[3]));
			} else {
				player.sendMessage("/jobcenter job addFisher <job> [fish/treasure/junk] <price>");
			}
			return true;
		}
	},
	JOB_REMOVEFISHER {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 4) {
				Job job = JobController.getJobByName(args[2]);
				job.delFisherLootType(args[3]);
				player.sendMessage(MessageWrapper.getString("jobcenter_removeFisher", args[3]));
			} else {
				player.sendMessage("/jobcenter job removeFisher <jobname> <fish/treasure/junk>");
			}
			return true;
		}
	},
	JOB_ADDITEM {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 5) {
				Job job = JobController.getJobByName(args[2]);
				job.addBlock(args[3], Double.valueOf(args[4]));
				player.sendMessage(MessageWrapper.getString("jobcenter_addItem", args[3]));
			} else {
				player.sendMessage("/jobcenter job addItem <job> <material> <price>");
			}
			return true;
		}
	},
	JOB_REMOVEITEM {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 4) {
				Job job = JobController.getJobByName(args[2]);
				job.deleteBlock(args[3]);
				player.sendMessage(MessageWrapper.getString("jobcenter_removeItem", args[3]));
			} else {
				player.sendMessage("/jobcenter job removeItem <job> <material>");
			}
			return true;
		}
	},
	JOB_ADDMOB {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 5) {
				Job job = JobController.getJobByName(args[2]);
				job.addMob(args[3], Double.valueOf(args[4]));
				player.sendMessage(MessageWrapper.getString("jobcenter_addMob", args[3]));
			} else {
				player.sendMessage("/jobcenter job addMob <job> <entity> <price>");
			}
			return true;
		}
	},
	JOB_REMOVEMOB {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 4) {
				Job job = JobController.getJobByName(args[2]);
				job.deleteMob(args[3]);
				player.sendMessage(MessageWrapper.getString("jobcenter_removeMob", args[3]));
			} else {
				player.sendMessage("/jobcenter job removeMob <jobname> <entity>");
			}
			return true;
		}
	},
	JOB {
		@Override
		boolean perform(String label, String[] args, Player player)
				throws JobSystemException, EconomyPlayerException, GeneralEconomyException {
			if (args.length == 1) {
				player.sendMessage("/jobcenter job [create/delete/addItem/removeItem/"
						+ "addMob/removeMob/addFisher/removeFisher]");
			} else {
				JobCommandEnum commandEnum = JobCommandEnum.getEnum("JOB_" + args[1]);
				if (commandEnum != null) {
					return commandEnum.perform(label, args, player);
				} else {
					player.sendMessage("/jobcenter job [create/delete/addItem/removeItem/"
							+ "addMob/removeMob/addFisher/removeFisher]");
				}
			}
			return true;
		}
	};

	abstract boolean perform(String label, String[] args, Player player)
			throws JobSystemException, EconomyPlayerException, GeneralEconomyException;

	/**
	 * Returns a enum. Return null, if no enum is found.
	 * 
	 * @param value
	 * @return job command enum
	 */
	public static JobCommandEnum getEnum(String value) {
		for (JobCommandEnum command : values()) {
			if (command.name().equalsIgnoreCase(value)) {
				return command;
			}
		}
		return null;
	}
}
