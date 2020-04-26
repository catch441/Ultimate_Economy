package com.ue.jobsystem.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;

import com.ue.jobsystem.api.JobController;
import com.ue.jobsystem.api.JobcenterController;

public class JobTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
	switch (args[0]) {
	case "":
	    return getAllJobcenterCommands();
	case "delete":
	case "move":
	    return handleDeleteAndMoveTabComplete(args);
	case "removeJob":
	    return handleRemoveJobTabComplete(args);
	case "addJob":
	    return handleAddJobTabComplete(args);
	case "job":
	    return handleJobTabComplete(args);
	case "create":
	    return new ArrayList<>();
	default:
	    return getMatchingJobcenterCommands(args);
	}
    }

    private List<String> handleJobTabComplete(String[] args) {
	switch (args[1]) {
	case "":
	    return getAllJobCommands();
	case "addItem":
	case "removeItem":
	    return handleAddRemoveItemTabComplete(args);
	case "addMob":
	case "deleteMob":
	    return handleAddDeleteMobTabComplete(args);
	case "addFisher":
	case "removeFisher":
	    return handleAddRemoveFisherTabComplete(args);
	case "delete":
	    return handleJobDeleteTabComplete(args);
	case "create":
	    return new ArrayList<>();
	default:
	    return getMatchingJobCommands(args);
	}
    }

    private List<String> handleAddRemoveItemTabComplete(String[] args) {
	if (args.length == 4) {
	    return getMaterialList(args[3]);
	} else if (args.length == 3) {
	    return getJobList(args[2]);
	} else {
	    return new ArrayList<>();
	}
    }

    private List<String> handleAddDeleteMobTabComplete(String[] args) {
	if (args.length == 4) {
	    return getEntityList(args[3]);
	} else if (args.length == 3) {
	    return getJobList(args[2]);
	} else {
	    return new ArrayList<>();
	}
    }

    private List<String> handleAddRemoveFisherTabComplete(String[] args) {
	if (args.length == 4) {
	    return getFisherLootTypes(args[3]);
	} else if (args.length == 3) {
	    return getJobList(args[2]);
	} else {
	    return new ArrayList<>();
	}
    }

    private List<String> handleJobDeleteTabComplete(String[] args) {
	if (args.length == 3) {
	    return getJobList(args[2]);
	} else {
	    return new ArrayList<>();
	}
    }

    private List<String> handleDeleteAndMoveTabComplete(String[] args) {
	if (args.length == 2) {
	    return getJobcenterNames(args[1]);
	} else {
	    return new ArrayList<>();
	}
    }

    private List<String> handleRemoveJobTabComplete(String[] args) {
	if (args.length == 3) {
	    return getJobList(args[2]);
	} else {
	    return handleDeleteAndMoveTabComplete(args);
	}
    }

    private List<String> handleAddJobTabComplete(String[] args) {
	if (args.length == 2) {
	    return getJobcenterNames(args[1]);
	} else if (args.length == 3) {
	    return getJobList(args[2]);
	} else {
	    return handleAddRemoveItemTabComplete(args);
	}
    }

    private List<String> getJobcenterNames(String arg) {
	List<String> all = JobcenterController.getJobcenterNameList();
	if ("".equals(arg)) {
	    return all;
	} else {
	    List<String> list = new ArrayList<>();
	    for (String jobname : JobcenterController.getJobcenterNameList()) {
		if (jobname.contains(arg)) {
		    list.add(jobname);
		}
	    }
	    return list;
	}
    }

    private List<String> getFisherLootTypes(String arg) {
	List<String> list = new ArrayList<>();
	if ("".equals(arg)) {
	    list.add("fish");
	    list.add("treasure");
	    list.add("junk");
	} else {
	    if ("fish".contains(arg)) {
		list.add("fish");
	    }
	    if ("treasure".contains(arg)) {
		list.add("treasure");
	    }
	    if ("junk".contains(arg)) {
		list.add("junk");
	    }
	}
	return list;
    }

    private List<String> getMatchingJobCommands(String[] args) {
	List<String> list = new ArrayList<>();
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
	if ("removeFisher".contains(args[1])) {
	    list.add("removeFisher");
	}
	if ("addMob".contains(args[1])) {
	    list.add("addMob");
	}
	if ("removeMob".contains(args[1])) {
	    list.add("removeMob");
	}
	return list;
    }

    private List<String> getAllJobCommands() {
	List<String> list = new ArrayList<>();
	list.add("create");
	list.add("delete");
	list.add("addItem");
	list.add("removeItem");
	list.add("addFisher");
	list.add("removeFisher");
	list.add("addMob");
	list.add("removeMob");
	return list;
    }

    private List<String> getMatchingJobcenterCommands(String[] args) {
	List<String> list = new ArrayList<>();
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
	return list;
    }

    private List<String> getAllJobcenterCommands() {
	List<String> list = new ArrayList<>();
	list.add("create");
	list.add("delete");
	list.add("move");
	list.add("job");
	list.add("addJob");
	list.add("removeJob");
	return list;
    }

    private List<String> getJobList(String arg) {
	List<String> temp = JobController.getJobNameList();
	List<String> list = new ArrayList<>();
	if ("".equals(arg)) {
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
	if ("".equals(arg)) {
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
	if ("".equals(arg)) {
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
