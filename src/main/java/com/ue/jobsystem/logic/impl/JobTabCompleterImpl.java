package com.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;

import com.ue.jobsystem.logic.api.JobManager;
import com.ue.jobsystem.logic.api.JobcenterManager;

public class JobTabCompleterImpl implements TabCompleter {

	private final JobcenterManager jobcenterManager;
	private final JobManager jobManager;
	
	@Inject
	public JobTabCompleterImpl(JobManager jobManager, JobcenterManager jobcenterManager) {
		this.jobcenterManager = jobcenterManager;
		this.jobManager = jobManager;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch(command.getLabel()) {
		case "jobinfo":
			return handleJobinfoTabComplete(args);
		case "jobcenter":
			return handleJobcenterTabComplete(args);
		default:
			return new ArrayList<>();
		}
	}
	
	private List<String> handleJobinfoTabComplete(String[] args) {
		if (args.length == 1) {
			return getJobList(args[0]);
		} else {
			return new ArrayList<>();
		}
	}
	
	private List<String> handleJobcenterTabComplete(String[] args) {
		if(args.length > 0) {
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
		return new ArrayList<>();
	}

	private List<String> handleJobTabComplete(String[] args) {
		if(args.length > 1) {
			switch (args[1]) {
			case "":
				return getAllJobCommands();
			case "addItem":
			case "removeItem":
				return handleAddRemoveItemTabComplete(args);
			case "addMob":
			case "removeMob":
				return handleAddRemoveMobTabComplete(args);
			case "addFisher":
			case "removeFisher":
				return handleAddRemoveFisherTabComplete(args);
			case "addBreedable":
			case "removeBreedable":
				return handleAddRemoveBreedableTabComlete(args);
			case "delete":
				return handleJobDeleteTabComplete(args);
			case "create":
				return new ArrayList<>();
			default:
				if(args.length == 2) {
					return getMatchingJobCommands(args);
				}
			}
		}
		return new ArrayList<>();
	}
	
	private List<String> handleAddRemoveBreedableTabComlete(String[] args) {
		if (args.length == 4) {
			return getBreedableList(args[3]);
		} else if (args.length == 3) {
			return getJobList(args[2]);
		} else {
			return new ArrayList<>();
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

	private List<String> handleAddRemoveMobTabComplete(String[] args) {
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
		List<String> all = jobcenterManager.getJobcenterNameList();
		if ("".equals(arg)) {
			return all;
		} else {
			List<String> list = new ArrayList<>();
			for (String jobname : all) {
				addIfMatching(list, jobname, arg);
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
			addIfMatching(list, "fish", arg);
			addIfMatching(list, "treasure", arg);
			addIfMatching(list, "junk", arg);
		}
		return list;
	}

	private List<String> getMatchingJobCommands(String[] args) {
		List<String> list = new ArrayList<>();
		for (String cmd : getAllJobCommands()) {
			addIfMatching(list, cmd, args[1]);
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
		list.add("addBreedable");
		list.add("removeBreedable");
		return list;
	}

	private List<String> getMatchingJobcenterCommands(String[] args) {
		List<String> list = new ArrayList<>();
		for (String cmd : getAllJobcenterCommands()) {
			addIfMatching(list, cmd, args[0]);
		}
		return list;
	}

	private void addIfMatching(List<String> list, String command, String arg) {
		if (command.contains(arg)) {
			list.add(command);
		}
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
		List<String> temp = jobManager.getJobNameList();
		List<String> list = new ArrayList<>();
		if ("".equals(arg)) {
			list = temp;
		} else {
			for (String jobname : temp) {
				addIfMatching(list, jobname, arg);
			}
		}
		return list;
	}

	private List<String> getMaterialList(String arg) {
		Material[] materials = Material.values();
		List<String> list = new ArrayList<>();
		for (Material material : materials) {
			addIfMatching(list, material.name().toLowerCase(), arg);
		}
		return list;
	}
	
	private List<String> getBreedableList(String arg) {
		List<String> list = new ArrayList<>();
		List<EntityType> breedableMobs = Arrays.asList(EntityType.BEE, EntityType.COW, EntityType.HOGLIN,
				EntityType.MUSHROOM_COW, EntityType.PIG, EntityType.SHEEP, EntityType.WOLF, EntityType.CAT,
				EntityType.DONKEY, EntityType.HORSE, EntityType.OCELOT, EntityType.POLAR_BEAR, EntityType.TURTLE,
				EntityType.CHICKEN, EntityType.FOX, EntityType.LLAMA, EntityType.PANDA, EntityType.RABBIT,
				EntityType.VILLAGER);
		addMatchingEntities(arg, list, breedableMobs);
		return list;
	}

	private List<String> getEntityList(String arg) {
		List<String> list = new ArrayList<>();
		EntityType[] entityTypes = EntityType.values();
		addMatchingEntities(arg, list, Arrays.asList(entityTypes));
		return list;
	}

	private void addMatchingEntities(String arg, List<String> list, List<EntityType> entityTypes) {
		for (EntityType entityname : entityTypes) {
			addIfMatching(list, entityname.name().toLowerCase(), arg);
		}
	}
}
