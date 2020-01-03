package com.ue.jobsystem.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.exceptions.JobSystemException;
import com.ue.jobsystem.api.Job;
import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.Jobcenter;
import com.ue.jobsystem.api.JobcenterController;

import ultimate_economy.Ultimate_Economy;

public class JobCommandExecutor implements CommandExecutor {

	private Ultimate_Economy plugin;

	public JobCommandExecutor(Ultimate_Economy plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			try {
				if (label.equalsIgnoreCase("jobcenter")) {
					if (args.length != 0) {
						switch (args[0]) {
							case "create":
								if (args.length == 3) {
									JobcenterController.createJobCenter(plugin.getServer(), plugin.getDataFolder(), args[1],
											player.getLocation(), Integer.parseInt(args[2]));
									plugin.getConfig().set("JobCenterNames", JobcenterController.getJobCenterNameList());
									plugin.saveConfig();
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("jobcenter_create1")
													+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("jobcenter_create2"));
								} else {
									player.sendMessage(
											"/jobcenter create <jobcenter> <size> <- size have to be a multible of 9");
								}
								break;
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////
							case "delete":
								if (args.length == 2) {
									JobcenterController.deleteJobCenter(args[1]);
									plugin.getConfig().set("JobCenterNames", JobcenterController.getJobCenterNameList());
									plugin.saveConfig();
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("jobcenter_delete1")
													+ " " + ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("jobcenter_delete2"));
								} else {
									player.sendMessage("/jobcenter delete <jobcenter>");
								}
								break;
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////
							case "move":
								if (args.length == 2) {
									Jobcenter jobcenter = JobcenterController.getJobCenterByName(args[1]);
									jobcenter.moveJobCenter(player.getLocation());
								} else {
									player.sendMessage("/jobcenter move <jobcenter>");
								}
								break;
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////
							case "addJob":
								if (args.length == 5) {
									Jobcenter jobcenter = JobcenterController.getJobCenterByName(args[1]);
									jobcenter.addJob(args[2], args[3], Integer.valueOf(args[4]));
									player.sendMessage(
											ChatColor.GOLD + "The job " + ChatColor.GREEN + args[2] + ChatColor.GOLD
													+ " was added to the JobCenter " + ChatColor.GREEN + args[1] + ".");
								} else {
									player.sendMessage("/jobcenter addJob <jobcenter> <job> <material> <slot>");
								}
								break;
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////
							case "removeJob":
								if (args.length == 3) {
									Jobcenter jobcenter = JobcenterController.getJobCenterByName(args[1]);
									jobcenter.removeJob(args[2]);
									player.sendMessage(
											ChatColor.GOLD + Ultimate_Economy.messages.getString("jobcenter_removeJob1")
													+ " " + ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
													+ Ultimate_Economy.messages.getString("jobcenter_removeJob2"));
								} else {
									player.sendMessage("/jobcenter removeJob <jobcenter> <job>");
								}
								break;
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////
							case "job":
								if (args.length == 1) {
									player.sendMessage(
											"/jobcenter job [create/delJob/addItem/removeItem/addMob/removeMob/addFisher/removeFisher]");
								} else {
									switch (args[1]) {
										case "create":
											if (args.length == 3) {
												JobController.createJob(plugin.getDataFolder(), args[2]);
												plugin.getConfig().set("JobList", JobController.getJobNameList());
												plugin.saveConfig();
												player.sendMessage(ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("jobcenter_createJob1")
														+ " " + ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("jobcenter_createJob2"));
											} else {
												player.sendMessage("/jobcenter job create <job>");
											}
											break;
										//////////////////////////////////////////////////////////////////////////////////////////////////
										case "delete":
											if (args.length == 3) {
												JobController.deleteJob(args[2]);
												plugin.getConfig().set("JobList", JobController.getJobNameList());
												plugin.saveConfig();
												player.sendMessage(ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("jobcenter_delJob1") + " "
														+ ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("jobcenter_delJob2"));
											} else {
												player.sendMessage("/jobcenter job delete <job>");
											}
											break;
										//////////////////////////////////////////////////////////////////////////////////////////////////
										case "addMob":
											if (args.length == 5) {
												Job job = JobController.getJobByName(args[2]);
												job.addMob(args[3], Double.valueOf(args[4]));
												player.sendMessage(ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("jobcenter_addMob1") + " "
														+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("jobcenter_addMob2"));
											} else {
												player.sendMessage("/jobcenter job addMob <job> <entity> <price>");
											}
											break;
										//////////////////////////////////////////////////////////////////////////////////////////////////
										case "removeMob":
											if (args.length == 4) {
												Job job = JobController.getJobByName(args[2]);
												job.deleteMob(args[3]);
												player.sendMessage(ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("jobcenter_removeMob1")
														+ " " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("jobcenter_removeMob2")
														+ " " + ChatColor.GREEN + job.getName() + ".");
											} else {
												player.sendMessage("/jobcenter job removeMob <jobname> <entity>");
											}
											break;
										//////////////////////////////////////////////////////////////////////////////////////////////////
										case "addItem":
											if (args.length == 5) {
												Job job = JobController.getJobByName(args[2]);
												job.addItem(args[3], Double.valueOf(args[4]));
												player.sendMessage(ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("jobcenter_addItem1")
														+ " " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("jobcenter_addItem2")
														+ " " + ChatColor.GREEN + job.getName() + ".");
											} else {
												player.sendMessage("/jobcenter job addItem <job> <material> <price>");
											}
											break;
										//////////////////////////////////////////////////////////////////////////////////////////////////
										case "removeItem":
											if (args.length == 4) {
												Job job = JobController.getJobByName(args[2]);
												job.deleteItem(args[3]);
												player.sendMessage(ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("jobcenter_removeItem1")
														+ " " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("jobcenter_removeItem2")
														+ " " + ChatColor.GREEN + job.getName() + ".");
											} else {
												player.sendMessage("/jobcenter job removeItem <job> <material>");
											}
											break;
										//////////////////////////////////////////////////////////////////////////////////////////////////
										case "addFisher":
											if (args.length == 5) {
												Job job = JobController.getJobByName(args[2]);
												job.addFisherLootType(args[3], Double.valueOf(args[4]));
												player.sendMessage(ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("jobcenter_addFisher1")
														+ " " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("jobcenter_addFisher2")
														+ " " + ChatColor.GREEN + job.getName() + ".");
											} else {
												player.sendMessage(
														"/jobcenter job addFisher <job> [fish/treasure/junk] <price>");
											}
											break;
										//////////////////////////////////////////////////////////////////////////////////////////////////
										case "removeFisher":
											if (args.length == 4) {
												Job job = JobController.getJobByName(args[2]);
												job.delFisherLootType(args[3]);
												player.sendMessage(ChatColor.GOLD
														+ Ultimate_Economy.messages.getString("jobcenter_removeFisher1")
														+ " " + ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
														+ Ultimate_Economy.messages.getString("jobcenter_removeFisher2")
														+ " " + ChatColor.GREEN + job.getName() + ".");
											} else {
												player.sendMessage(
														"/jobcenter job removeFisher <jobname> <fish/treasure/junk>");
											}
											break;
										//////////////////////////////////////////////////////////////////////////////////////////////////
										default:
											player.sendMessage(
													"/jobcenter job [create/delete/addItem/addFisher/removeFisher/removeItem/addMob/removeMob]");
									}
								}
								break;
							//////////////////////////////////////////////////////////////////////////////////////////////////////////////
							default:
								return false;
						}
					} else {
						return false;
					}
				}
			} catch (JobSystemException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
		return true;
	}
}
