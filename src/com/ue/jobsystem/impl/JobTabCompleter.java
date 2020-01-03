package com.ue.jobsystem.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import com.ue.jobsystem.api.JobController;

public class JobTabCompleter implements TabCompleter{
	
	private FileConfiguration config;
	
	public JobTabCompleter(FileConfiguration config) {
		this.config = config;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		if (command.getName().equals("jobcenter")) {
			if (args.length == 1) {
				if (args[0].equals("")) {
					list.add("create");
					list.add("delete");
					list.add("move");
					list.add("job");
					list.add("addJob");
					list.add("removeJob");
				} else if (args.length == 1) {
					if ("create".contains(args[0])) {
						list.add("create");
					}
					if ("delete".contains(args[0])) {
						list.add("delete");
					}
					if ("move".contains(args[0])) {
						list.add("move");
					}
					if ("job".contains(args[0])) {
						list.add("job");
					}
					if ("addJob".contains(args[0])) {
						list.add("addJob");
					}
					if ("removeJob".contains(args[0])) {
						list.add("removeJob");
					}
				}
			} else if (args[0].equals("delete") || args[0].equals("move") || args[0].equals("removeJob")
					|| args[0].equals("addJob")) {
				if (args.length == 2) {
					List<String> temp = config.getStringList("JobCenterNames");
					if (args[1].equals("")) {
						list = temp;
					} else {
						for (String jobname : temp) {
							if (jobname.contains(args[1])) {
								list.add(jobname);
							}
						}
					}
				} else if (args[0].equals("removeJob") || args[0].equals("addJob")) {
					if (args.length == 3) {
						list = getJobList(args[2]);
					} else if (args.length == 4 && args[0].equals("addJob")) {
						list = getMaterialList(args[3]);
					}
				}
			} else if (args[0].equals("job")) {
				if (args[1].equals("")) {
					list.add("create");
					list.add("delete");
					list.add("addItem");
					list.add("removeItem");
					list.add("addFisher");
					list.add("removeFisher");
					list.add("addMob");
					list.add("removeMob");
				} else if (args[1].equals("addItem") || args[1].equals("removeItem") || args[1].equals("addFisher")
						|| args[1].equals("removeFisher") || args[1].equals("removeFisher") || args[1].equals("addMob")
						|| args[1].equals("removeMob")) {
					if (args.length == 3) {
						list = getJobList(args[2]);
					} else if (args[1].equals("addItem") || args[1].equals("removeItem") || args[1].equals("addItem")) {
						if (args.length == 4) {
							list = getMaterialList(args[3]);
						}
					} else if (args[1].equals("addMob") || args[1].equals("removeMob")) {
						if (args.length == 4) {
							list = getEntityList(args[3]);
						}
					} else if (args[1].equals("addFisher") || args[1].equals("removeFisher")) {
						if (args.length == 4) {
							if (args[3].equals("")) {
								list.add("fish");
								list.add("treasure");
								list.add("junk");
							} else {
								if ("fish".contains(args[3])) {
									list.add("fish");
								}
								if ("treasure".contains(args[3])) {
									list.add("treasure");
								}
								if ("junk".contains(args[3])) {
									list.add("junk");
								}
							}
						}
					}
				} else if (args[1].equals("delete")) {
					if (args.length == 3) {
						list = getJobList(args[2]);
					}
				} else if (args.length == 2) {
					if ("create".contains(args[1])) {
						list.add("create");
					}
					if ("delete".contains(args[1])) {
						list.add("delete");
					}
					if ("addItem".contains(args[1])) {
						list.add("addItem");
					}
					if ("removeItem".contains(args[1])) {
						list.add("removeItem");
					}
					if ("addFisher".contains(args[1])) {
						list.add("addFisher");
					}
					if ("delFisher".contains(args[1])) {
						list.add("delFisher");
					}
					if ("addMob".contains(args[1])) {
						list.add("addMob");
					}
					if ("removeMob".contains(args[1])) {
						list.add("removeMob");
					}
				}
			}
		}
		return list;
	}
	
	private List<String> getJobList(String arg) {
		List<String> temp = JobController.getJobNameList();
		List<String> list = new ArrayList<>();
		if (arg.equals("")) {
			list = temp;
		} else {
			for (String jobname : temp) {
				if (jobname.contains(arg)) {
					list.add(jobname);
				}
			}
		}
		return list;
	}
	
	private static List<String> getMaterialList(String arg) {
		Material[] materials = Material.values();
		List<String> list = new ArrayList<>();
		if (arg.equals("")) {
			for (Material material : materials) {
				list.add(material.name().toLowerCase());
			}
		} else {
			for (Material material : materials) {
				if (material.name().toLowerCase().contains(arg)) {
					list.add(material.name().toLowerCase());
				}
			}
		}
		return list;
	}

	private static List<String> getEntityList(String arg) {
		List<String> list = new ArrayList<>();
		EntityType[] entityTypes = EntityType.values();
		if (arg.equals("")) {
			for (EntityType entityname : entityTypes) {
				list.add(entityname.name().toLowerCase());
			}
		} else {
			for (EntityType entityname : entityTypes) {
				if (entityname.name().toLowerCase().contains(arg)) {
					list.add(entityname.name().toLowerCase());
				}
			}
		}
		return list;
	}
}
