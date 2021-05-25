package org.ue.jobsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.ue.common.utils.TabCompleterUtils;
import org.ue.jobsystem.logic.api.JobManager;
import org.ue.jobsystem.logic.api.JobcenterManager;

public class JobTabCompleterImpl extends TabCompleterUtils implements TabCompleter {

	private final JobcenterManager jobcenterManager;
	private final JobManager jobManager;

	public JobTabCompleterImpl(JobManager jobManager, JobcenterManager jobcenterManager) {
		this.jobcenterManager = jobcenterManager;
		this.jobManager = jobManager;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (command.getLabel()) {
		case "jobinfo":
			return handleJobNameParamTabComplete(1, args.length, args[0]);
		case "jobcenter":
			return handleJobcenterTabComplete(args);
		default:
			return new ArrayList<>();
		}
	}

	private List<String> handleJobcenterTabComplete(String[] args) {
		if (args.length > 0) {
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
				return getMatchingList(getAllJobcenterCommands(), args[0]);
			}
		}
		return new ArrayList<>();
	}

	private List<String> handleJobTabComplete(String[] args) {
		if (args.length > 1) {
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
				return handleJobNameParamTabComplete(3, args.length, args[2]);
			case "create":
				return new ArrayList<>();
			default:
				if (args.length == 2) {
					return getMatchingList(getAllJobCommands(), args[1]);
				}
			}
		}
		return new ArrayList<>();
	}

	private List<String> handleAddRemoveBreedableTabComlete(String[] args) {
		if (args.length == 4) {
			return getBreedableList(args[3]);
		}
		return handleJobNameParamTabComplete(3, args.length, args[2]);
	}

	private List<String> handleAddRemoveItemTabComplete(String[] args) {
		if (args.length == 4) {
			return getMatchingEnumList(Material.values(), args[3]);
		}
		return handleJobNameParamTabComplete(3, args.length, args[2]);
	}

	private List<String> handleAddRemoveMobTabComplete(String[] args) {
		if (args.length == 4) {
			return getMatchingEnumList(EntityType.values(), args[3]);
		}
		return handleJobNameParamTabComplete(3, args.length, args[2]);
	}

	private List<String> handleAddRemoveFisherTabComplete(String[] args) {
		if (args.length == 4) {
			return getFisherLootTypes(args[3]);
		}
		return handleJobNameParamTabComplete(3, args.length, args[2]);
	}

	private List<String> handleJobNameParamTabComplete(int maxArgs, int argsLength, String jobArg) {
		if (argsLength == maxArgs) {
			return getMatchingList(jobManager.getJobNameList(), jobArg);
		}
		return new ArrayList<>();
	}

	private List<String> handleDeleteAndMoveTabComplete(String[] args) {
		if (args.length == 2) {
			return getMatchingList(jobcenterManager.getJobcenterNameList(), args[1]);
		}
		return new ArrayList<>();
	}

	private List<String> handleRemoveJobTabComplete(String[] args) {
		if (args.length == 3) {
			return getMatchingList(jobManager.getJobNameList(), args[2]);
		}
		return handleDeleteAndMoveTabComplete(args);
	}

	private List<String> handleAddJobTabComplete(String[] args) {
		if (args.length == 2) {
			return getMatchingList(jobcenterManager.getJobcenterNameList(), args[1]);
		} else if (args.length == 3) {
			return getMatchingList(jobManager.getJobNameList(), args[2]);
		}
		return handleAddRemoveItemTabComplete(args);
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

	private List<String> getAllJobCommands() {
		return Arrays.asList("create", "delete", "addItem", "removeItem", "addFisher", "removeFisher", "addMob",
				"removeMob", "addBreedable", "removeBreedable");
	}

	private List<String> getAllJobcenterCommands() {
		return Arrays.asList("create", "delete", "move", "job", "addJob", "removeJob");
	}

	private List<String> getBreedableList(String arg) {
		EntityType[] breedableMobs = {EntityType.BEE, EntityType.COW, EntityType.HOGLIN,
				EntityType.MUSHROOM_COW, EntityType.PIG, EntityType.SHEEP, EntityType.WOLF, EntityType.CAT,
				EntityType.DONKEY, EntityType.HORSE, EntityType.OCELOT, EntityType.POLAR_BEAR, EntityType.TURTLE,
				EntityType.CHICKEN, EntityType.FOX, EntityType.LLAMA, EntityType.PANDA, EntityType.RABBIT,
				EntityType.VILLAGER};
		return getMatchingEnumList(breedableMobs, arg);
	}
}
