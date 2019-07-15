package com.ue.jobsystem;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ue.exceptions.JobSystemException;

import ultimate_economy.Ultimate_Economy;

public class JobCommandExecutor implements CommandExecutor{
	
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
						if (args[0].equals("create")) {
							if (args.length == 3) {
								JobCenter.createJobCenter(plugin.getServer(), plugin.getDataFolder(), args[1], player.getLocation(),
										Integer.parseInt(args[2]));
								plugin.getConfig().set("JobCenterNames", JobCenter.getJobCenterNameList());
								plugin.saveConfig();
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("jobcenter_create1") + " "
												+ ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_create2"));
							} else {
								player.sendMessage("/jobcenter create <name> <size (9,18,27...)>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("delete")) {
							if (args.length == 2) {
								JobCenter.deleteJobCenter(args[1]);
								plugin.getConfig().set("JobCenterNames", JobCenter.getJobCenterNameList());
								plugin.saveConfig();
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("jobcenter_delete1") + " "
												+ ChatColor.GREEN + args[1] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_delete2"));
							} else {
								player.sendMessage("/jobcenter delete <name>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("move")) {
							if (args.length == 5) {
								JobCenter jobCenter = JobCenter.getJobCenterByName(args[1]);
								jobCenter.moveShop(Double.valueOf(args[2]), Double.valueOf(args[3]),
										Double.valueOf(args[4]));
							} else {
								player.sendMessage("/jobcenter move <name> <x> <y> <z>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("addJob")) {
							if (args.length == 5) {
								JobCenter jobCenter = JobCenter.getJobCenterByName(args[1]);
								jobCenter.addJob(args[2], args[3], Integer.valueOf(args[4]));
								player.sendMessage(
										ChatColor.GOLD + "The job " + ChatColor.GREEN + args[2] + ChatColor.GOLD
												+ " was added to the JobCenter " + ChatColor.GREEN + args[1] + ".");
							} else {
								player.sendMessage("/jobcenter addJob <jobcentername> <jobname> <material> <slot>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("removeJob")) {
							if (args.length == 3) {
								JobCenter jobCenter = JobCenter.getJobCenterByName(args[1]);
								jobCenter.removeJob(args[2]);
								player.sendMessage(
										ChatColor.GOLD + Ultimate_Economy.messages.getString("jobcenter_removeJob1")
												+ " " + ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_removeJob2"));
							} else {
								player.sendMessage("/jobcenter removeJob <jobcentername> <jobname>");
							}
						}
						//////////////////////////////////////////////////////////////////////////////////////////////////////////////
						else if (args[0].equals("job")) {
							if (args.length == 1) {
								player.sendMessage(
										"/jobcenter job <createJob/delJob/addItem/removeItem/addMob/removeMob/addFisher/removeFisher>");
							} else {
								if (args[1].equals("createJob")) {
									if (args.length == 3) {
										Job.createJob(plugin.getDataFolder(), args[2]);
										plugin.getConfig().set("JobList", Job.getJobNameList());
										plugin.saveConfig();
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_createJob1") + " "
												+ ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_createJob2"));
									} else {
										player.sendMessage("/jobcenter job createJob <jobname>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("delJob")) {
									if (args.length == 3) {
										Job.deleteJob(args[2]);
										plugin.getConfig().set("JobList", Job.getJobNameList());
										plugin.saveConfig();
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_delJob1") + " "
												+ ChatColor.GREEN + args[2] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_delJob2"));
									} else {
										player.sendMessage("/jobcenter job delJob <jobname>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("addMob")) {
									if (args.length == 5) {
										Job job = Job.getJobByName(args[2]);
										job.addMob(args[3], Double.valueOf(args[4]));
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_addMob1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_addMob2"));
									} else {
										player.sendMessage("/jobcenter job addMob <jobname> <entity> <price>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("removeMob")) {
									if (args.length == 4) {
										Job job = Job.getJobByName(args[2]);
										job.deleteMob(args[3]);
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_removeMob1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_removeMob2") + " "
												+ ChatColor.GREEN + job.getName() + ".");
									} else {
										player.sendMessage("/jobcenter job removeMob <jobname> <entity>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("addItem")) {
									if (args.length == 5) {
										Job job = Job.getJobByName(args[2]);
										job.addItem(args[3], Double.valueOf(args[4]));
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_addItem1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_addItem2") + " "
												+ ChatColor.GREEN + job.getName() + ".");
									} else {
										player.sendMessage("/jobcenter job addItem <jobname> <material> <price>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("removeItem")) {
									if (args.length == 4) {
										Job job = Job.getJobByName(args[2]);
										job.deleteItem(args[3]);
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_removeItem1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_removeItem2") + " "
												+ ChatColor.GREEN + job.getName() + ".");
									} else {
										player.sendMessage("/jobcenter job removeItem <jobname> <material>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("addFisher")) {
									if (args.length == 5) {
										Job job = Job.getJobByName(args[2]);
										job.addFisherLootType(args[3], Double.valueOf(args[4]));
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_addFisher1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_addFisher2") + " "
												+ ChatColor.GREEN + job.getName() + ".");
									} else {
										player.sendMessage(
												"/jobcenter job addFisher <jobname> <fish/treasure/junk> <price>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if (args[1].equals("removeFisher")) {
									if (args.length == 4) {
										Job job = Job.getJobByName(args[2]);
										job.delFisherLootType(args[3]);
										player.sendMessage(ChatColor.GOLD
												+ Ultimate_Economy.messages.getString("jobcenter_removeFisher1") + " "
												+ ChatColor.GREEN + args[3] + ChatColor.GOLD + " "
												+ Ultimate_Economy.messages.getString("jobcenter_removeFisher2") + " "
												+ ChatColor.GREEN + job.getName() + ".");
									} else {
										player.sendMessage(
												"/jobcenter job removeFisher <jobname> <fish/treasure/junk>");
									}
								}
								//////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else {
									player.sendMessage(
											"/jobcenter job <createJob/delJob/addItem/addFisher/removeFisher/removeItem/addMob/removeMob>");
								}
							}
						} else {
							player.sendMessage("/jobcenter <create/delete/move/job/addJob>");
						}
					} else {
						player.sendMessage("/jobcenter <create/delete/move/job/addjob>");
					}
				}
			} catch (JobSystemException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			} catch (NumberFormatException e2) {
				player.sendMessage(ChatColor.RED + Ultimate_Economy.messages.getString("invalid_number"));
			}
		}
				
		return false;
	}

}
