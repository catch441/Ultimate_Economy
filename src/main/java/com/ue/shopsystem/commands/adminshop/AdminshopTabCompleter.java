package com.ue.shopsystem.commands.adminshop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.ue.shopsystem.api.AdminshopController;

public class AdminshopTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args[0]) {
		case "delete":
		case "editShop":
		case "resize":
		case "editItem":
		case "move":
		case "rename":
		case "removeItem":
		case "removeSpawner":
			return handleAdminshopNameTabComplete(args);
		case "addSpawner":
			return handlerAddSpawnerTabComplete(args);
		case "addItem":
			return handleAddItemTabComplete(args);
		case "addPotion":
			return handleAddPotionTabComplete(args);
		case "changeProfession":
			return handleChangeProfessionTabComplete(args);
		case "addEnchantedItem": {
			return handleAddEnchantedItemTabComplete(args);
		}
		case "":
			return getAllCommands();
		default:
			return handleDefaultMatchingTabComplete(args);
		}
	}

	private List<String> handleAddEnchantedItemTabComplete(String[] args) {
		if(args.length == 2) {
			return getAdminshopList(args[1]);
		} else if(args.length == 3) {
			return getMaterialList(args[2]);
		} else if(args.length >= 7 && (args.length % 2) == 0) {
			return handleAddEnchantedItemEnchantementTabComplete(args);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleChangeProfessionTabComplete(String[] args) {
		if(args.length == 2) {
			return getAdminshopList(args[1]);
		} else if(args.length == 3) {
			return getProfessions(args[2]);
		} else {
			return new ArrayList<>();
		}
	}
	
	private List<String> getProfessions(String arg) {
		List<String> list = new ArrayList<>();
		for (Profession profession : Profession.values()) {
			if ("".equals(arg)) {
				list.add(profession.name().toLowerCase());
			} else {
				if (profession.name().toLowerCase().contains(arg)) {
					list.add(profession.name().toLowerCase());
				}
			}
		}
		return list;
	}

	private List<String> handleAddPotionTabComplete(String[] args) {
		if (args.length == 2) {
			return getAdminshopList(args[1]);
		} else if (args.length == 3) {
			return handleAddPotionPotionTypeTabComplete(args[2]);
		} else if (args.length == 4) {
			return handleAddPotionEffectTabComplete(args[3]);
		} else if (args.length == 5) {
			return handleAddPotionPropertyTabComplete(args[4]);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleAddItemTabComplete(String[] args) {
		if (args.length == 2) {
			return getAdminshopList(args[1]);
		} else if (args.length == 3) {
			return getMaterialList(args[2]);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handlerAddSpawnerTabComplete(String[] args) {
		if (args.length == 2) {
			return getAdminshopList(args[1]);
		} else if (args.length == 3) {
			return getEntityList(args[2]);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> handleDefaultMatchingTabComplete(String[] args) {
		List<String> list = new ArrayList<>();
		addIfMatching(list, "create", args[0]);
		addIfMatching(list, "delete", args[0]);
		addIfMatching(list, "move", args[0]);
		addIfMatching(list, "addItem", args[0]);
		addIfMatching(list, "removeItem", args[0]);
		addIfMatching(list, "rename", args[0]);
		addIfMatching(list, "resize", args[0]);
		addIfMatching(list, "changeProfession", args[0]);
		addIfMatching(list, "editItem", args[0]);
		addIfMatching(list, "editShop", args[0]);
		addIfMatching(list, "addEnchantedItem", args[0]);
		addIfMatching(list, "addPotion", args[0]);
		addIfMatching(list, "addSpawner", args[0]);
		addIfMatching(list, "removeSpawner", args[0]);
		return list;
	}

	private void addIfMatching(List<String> list, String command, String arg) {
		if (command.contains(arg)) {
			list.add(command);
		}
	}

	private List<String> handleAddPotionPotionTypeTabComplete(String arg) {
		List<String> list = new ArrayList<>();
		for (PotionType pType : PotionType.values()) {
			if ("".equals(arg)) {
				list.add(pType.name().toLowerCase());
			} else {
				if (pType.name().toLowerCase().contains(arg)) {
					list.add(pType.name().toLowerCase());
				}
			}
		}
		return list;
	}

	private List<String> handleAddEnchantedItemEnchantementTabComplete(String[] args) {
		List<String> list = new ArrayList<>();
		for (Enchantment enchantment : Enchantment.values()) {
			if (args[args.length - 1].equals("")) {
				list.add(enchantment.getKey().getKey());
			} else {
				if (enchantment.getKey().getKey().toLowerCase().contains(args[args.length - 1])) {
					list.add(enchantment.getKey().getKey().toLowerCase());
				}
			}
		}
		return list;
	}

	private List<String> handleAddPotionPropertyTabComplete(String arg) {
		List<String> list = new ArrayList<>();
		if ("".equals(arg)) {
			list.add("extended");
			list.add("upgraded");
			list.add("none");
		} else {
			addIfMatching(list, "extended", arg);
			addIfMatching(list, "upgraded", arg);
			addIfMatching(list, "none", arg);
		}
		return list;
	}

	private List<String> handleAddPotionEffectTabComplete(String arg) {
		List<String> list = new ArrayList<>();
		for (PotionEffectType peType : PotionEffectType.values()) {
			if ("".equals(arg)) {
				list.add(peType.getName().toLowerCase());
			} else {
				if (peType.getName().toLowerCase().contains(arg)) {
					list.add(peType.getName().toLowerCase());
				}
			}
		}
		return list;
	}

	private List<String> getAllCommands() {
		List<String> list = new ArrayList<>();
		list.add("create");
		list.add("delete");
		list.add("move");
		list.add("editShop");
		list.add("addItem");
		list.add("removeItem");
		list.add("rename");
		list.add("resize");
		list.add("changeProfession");
		list.add("editItem");
		list.add("addEnchantedItem");
		list.add("addPotion");
		list.add("addSpawner");
		list.add("removeSpawner");
		return list;
	}

	private List<String> handleAdminshopNameTabComplete(String[] args) {
		if (args.length == 2) {
			return getAdminshopList(args[1]);
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> getAdminshopList(String arg) {
		List<String> temp = AdminshopController.getAdminshopNameList();
		List<String> list = new ArrayList<>();
		if ("".equals(arg)) {
			list = temp;
		} else {
			for (String shopName : temp) {
				addIfMatching(list, shopName, arg);
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
