package com.ue.shopsystem.adminshop.impl;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.GeneralEconomyMessageEnum;
import com.ue.exceptions.PlayerException;
import com.ue.exceptions.PlayerExceptionMessageEnum;
import com.ue.exceptions.ShopSystemException;
import com.ue.exceptions.TownSystemException;
import com.ue.language.MessageWrapper;
import com.ue.shopsystem.adminshop.api.Adminshop;
import com.ue.shopsystem.adminshop.api.AdminshopController;
import com.ue.shopsystem.impl.AbstractShopImpl;
import com.ue.ultimate_economy.UltimateEconomy;

public class AdminshopCommandExecutor implements CommandExecutor {

    private UltimateEconomy plugin;

    /**
     * Constructor for the adminshop command executor.
     * 
     * @param plugin
     */
    public AdminshopCommandExecutor(UltimateEconomy plugin) {
	this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (sender instanceof Player) {
	    Player player = (Player) sender;
	    try {
		if (args.length != 0) {
		    switch (args[0]) {
		    case "create":
			handleCreateCommand(label, args, player);
			break;
		    case "delete":
			handleDeleteCommand(label, args, player);
			break;
		    case "rename":
			handleRenameCommand(label, args, player);
			break;
		    case "resize":
			handleResizeCommand(label, args, player);
			break;
		    case "move":
			handleMoveCommand(label, args, player);
			break;
		    case "editShop":
			handleEditShopCommand(label, args, player);
			break;
		    case "changeProfession":
			handleChangeProfessionCommand(label, args, player);
			break;
		    case "addItem":
			handleAddItemCommand(label, args, player);
			break;
		    case "removeItem":
			handleRemoveItemCommand(label, args, player);
			break;
		    case "addPotion":
			handleAddPotionCommand(label, args, player);
			break;
		    case "addEnchantedItem":
			handleAddEnchantedItemCommand(label, args, player);
			break;
		    case "addSpawner":
			handleAddSpawnerCommand(label, args, player);
			break;
		    case "removeSpawner":
			handleRemoveSpawnerCommand(label, args, player);
			break;
		    case "editItem":
			handleEditItemCommand(label, args, player);
			break;
		    default:
			return false;
		    }
		} else {
		    return false;
		}
	    } catch (ShopSystemException | PlayerException | GeneralEconomyException e) {
		player.sendMessage(e.getMessage());
	    } catch (NumberFormatException e) {
		player.sendMessage(MessageWrapper.getErrorString("invalid_parameter"));
	    } catch (TownSystemException e) {
	    }
	}
	return true;
    }

    private void handleEditItemCommand(String label, String[] args, Player player)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	if (args.length == 6) {
	    player.sendMessage(AdminshopController.getAdminShopByName(args[1]).editShopItem(Integer.valueOf(args[2]),
		    args[3], args[4], args[5]));
	} else {
	    player.sendMessage(MessageWrapper.getString("shop_editItem_errorinfo"));
	    player.sendMessage("/" + label + " editItem <shopname> <slot> <amount> <sellPrice> <buyPrice>");
	}
    }

    private void handleRemoveSpawnerCommand(String label, String[] args, Player player)
	    throws ShopSystemException, GeneralEconomyException {
	if (args.length == 3) {
	    Adminshop shop = AdminshopController.getAdminShopByName(args[1]);
	    String itemName = shop.getItem(Integer.valueOf(args[2])).getType().toString().toLowerCase();
	    shop.removeShopItem(Integer.valueOf(args[2]) - 1);
	    player.sendMessage(MessageWrapper.getString("shop_removeSpawner", itemName));
	} else {
	    player.sendMessage("/" + label + " removeSpawner <shopname> <slot>");
	}
    }

    private void handleAddSpawnerCommand(String label, String[] args, Player player)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	if (args.length == 5) {
	    ItemStack itemStack = new ItemStack(Material.SPAWNER, 1);
	    ItemMeta meta = itemStack.getItemMeta();
	    meta.setDisplayName(args[2].toUpperCase());
	    itemStack.setItemMeta(meta);
	    AdminshopController.getAdminShopByName(args[1]).addShopItem(Integer.valueOf(args[3]) - 1, 0.0,
		    Double.valueOf(args[4]), itemStack);
	    player.sendMessage(MessageWrapper.getString("shop_addSpawner", args[2]));
	} else {
	    player.sendMessage("/" + label + " addSpawner <shopname> <entity type> <slot> <buyPrice>");
	}
    }

    private void handleAddEnchantedItemCommand(String label, String[] args, Player player)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	if (args.length >= 9) {
	    handleAddEnchantedItem(player, args, AdminshopController.getAdminShopByName(args[1]));
	} else {
	    player.sendMessage("/" + label + " addEnchantedItem <shopname> <material> <slot> "
		    + "<amount> <sellPrice> <buyPrice> [<enchantment> <lvl>]");
	}
    }

    private void handleAddPotionCommand(String label, String[] args, Player player)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	if (args.length == 9) {
	    handleAddPotion(player, AdminshopController.getAdminShopByName(args[1]), args);
	} else {
	    player.sendMessage("/" + label + " addPotion <shopname> <potionType> <potionEffect> "
		    + "<extended/upgraded/none> <slot> <amount> <sellprice> <buyprice>");
	}
    }

    private void handleRemoveItemCommand(String label, String[] args, Player player)
	    throws ShopSystemException, GeneralEconomyException {
	if (args.length == 3) {
	    Adminshop shop = AdminshopController.getAdminShopByName(args[1]);
	    String itemName = shop.getItem(Integer.valueOf(args[2])).getType().toString().toLowerCase();
	    shop.removeShopItem(Integer.valueOf(args[2]) - 1);
	    player.sendMessage(MessageWrapper.getString("shop_removeItem", itemName));
	} else {
	    player.sendMessage("/" + label + " removeItem <shopname> <slot (> 0)>");
	}
    }

    private void handleAddItemCommand(String label, String[] args, Player player)
	    throws GeneralEconomyException, ShopSystemException, PlayerException {
	if (args.length == 7) {
	    if (Material.matchMaterial(args[2].toUpperCase()) == null) {
		throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, args[2]);
	    } else {
		ItemStack itemStack = new ItemStack(Material.getMaterial(args[2].toUpperCase()),
			Integer.valueOf(args[4]));
		AdminshopController.getAdminShopByName(args[1]).addShopItem(Integer.valueOf(args[3]) - 1,
			Double.valueOf(args[5]), Double.valueOf(args[6]), itemStack);
		player.sendMessage(
			MessageWrapper.getString("shop_addItem", itemStack.getType().toString().toLowerCase()));
	    }
	} else {
	    player.sendMessage("/" + label + " addItem <shopname> <material> <slot> <amount> <sellPrice> <buyPrice>");
	}
    }

    private void handleChangeProfessionCommand(String label, String[] args, Player player) throws ShopSystemException {
	if (args.length == 3) {
	    try {
		AdminshopController.getAdminShopByName(args[1])
			.changeProfession(Profession.valueOf(args[2].toUpperCase()));
		player.sendMessage(MessageWrapper.getString("profession_changed"));
	    } catch (IllegalArgumentException e) {
		player.sendMessage(MessageWrapper.getErrorString("invalid_parameter", args[2]));
	    }
	} else {
	    player.sendMessage("/" + label + " changeProfession <shopname> <profession>");
	}
    }

    private void handleEditShopCommand(String label, String[] args, Player player) throws ShopSystemException {
	if (args.length == 2) {
	    AdminshopController.getAdminShopByName(args[1]).openEditor(player);
	} else {
	    player.sendMessage("/" + label + " editShop <shopname>");
	}
    }

    private void handleMoveCommand(String label, String[] args, Player player)
	    throws TownSystemException, PlayerException, ShopSystemException {
	if (args.length == 2) {
	    AdminshopController.getAdminShopByName(args[1]).moveShop(player.getLocation());
	} else {
	    player.sendMessage("/" + label + " move <shopname>");
	}
    }

    private void handleResizeCommand(String label, String[] args, Player player)
	    throws ShopSystemException, GeneralEconomyException, PlayerException {
	if (args.length == 3) {
	    AdminshopController.getAdminShopByName(args[1]).changeShopSize(Integer.valueOf(args[2]));
	    player.sendMessage(MessageWrapper.getString("shop_resize", args[2]));
	} else {
	    player.sendMessage("/" + label + " resize <shopname> <new size>");
	}
    }

    private void handleRenameCommand(String label, String[] args, Player player) throws ShopSystemException {
	if (args.length == 3) {
	    AdminshopController.getAdminShopByName(args[1]).changeShopName(args[2]);
	    player.sendMessage(MessageWrapper.getString("shop_rename", args[1], args[2]));
	} else {
	    player.sendMessage("/" + label + " rename <oldName> <newName>");
	}
    }

    private void handleDeleteCommand(String label, String[] args, Player player) throws ShopSystemException {
	if (args.length == 2) {
	    AdminshopController.deleteAdminShop(AdminshopController.getAdminShopByName(args[1]));
	    player.sendMessage(MessageWrapper.getString("shop_delete", args[1]));
	    plugin.getConfig().set("AdminShopIds", AdminshopController.getAdminshopIdList());
	    plugin.saveConfig();
	} else {
	    player.sendMessage("/" + label + " delete <shopname>");
	}
    }

    private void handleCreateCommand(String label, String[] args, Player player)
	    throws ShopSystemException, GeneralEconomyException {
	if (args.length == 3) {
	    AdminshopController.createAdminShop(plugin.getDataFolder(), args[1], player.getLocation(),
		    Integer.valueOf(args[2]));
	    player.sendMessage(MessageWrapper.getString("shop_create", args[1]));
	    plugin.getConfig().set("AdminShopIds", AdminshopController.getAdminshopIdList());
	    plugin.saveConfig();
	} else {
	    player.sendMessage("/" + label + " create <shopname> <size> <- size have to be a multible of 9");
	}
    }

    private static void handleAddPotion(Player p, Adminshop s, String[] args)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	if (!args[2].equalsIgnoreCase("potion") && !args[2].equalsIgnoreCase("splash_potion")
		&& !args[2].equalsIgnoreCase("lingering_potion")) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, args[2]);
	} else if (!args[4].equalsIgnoreCase("extended") && !args[4].equalsIgnoreCase("upgraded")
		&& !args[4].equalsIgnoreCase("none")) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, args[4]);
	} else if (!args[2].toUpperCase().equals("HAND") && Material.matchMaterial(args[2].toUpperCase()) == null) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, args[2]);
	} else {
	    ItemStack itemStack = new ItemStack(Material.valueOf(args[2].toUpperCase()), Integer.valueOf(args[6]));
	    PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
	    boolean extended = false;
	    boolean upgraded = false;
	    if (args[4].equalsIgnoreCase("extended")) {
		extended = true;
	    } else if (args[4].equalsIgnoreCase("upgraded")) {
		upgraded = true;
	    }
	    meta.setBasePotionData(new PotionData(PotionType.valueOf(args[3].toUpperCase()), extended, upgraded));
	    itemStack.setItemMeta(meta);
	    s.addShopItem(Integer.valueOf(args[5]) - 1, Double.valueOf(args[7]), Double.valueOf(args[8]), itemStack);
	    p.sendMessage(MessageWrapper.getString("shop_addItem", itemStack.getType().toString().toLowerCase()));
	}
    }

    private static void handleAddEnchantedItem(Player p, String[] args, Adminshop s)
	    throws ShopSystemException, PlayerException, GeneralEconomyException {
	if (!args[2].toUpperCase().equals("HAND") && Material.matchMaterial(args[2].toUpperCase()) == null) {
	    throw GeneralEconomyException.getException(GeneralEconomyMessageEnum.INVALID_PARAMETER, args[2]);
	} else {
	    Integer length = args.length - 7;
	    if (length % 2 == 0) {
		ArrayList<String> enchantmentList = new ArrayList<>();
		for (Integer i = 1; i < length; i = i + 2) {
		    enchantmentList.add(args[i + 6].toLowerCase() + "-" + args[i + 7]);
		}
		ItemStack iStack = new ItemStack(Material.valueOf(args[2].toUpperCase()), Integer.valueOf(args[4]));
		ArrayList<String> newEnchantmentList = AbstractShopImpl.addEnchantments(iStack, enchantmentList);
		if (newEnchantmentList.size() < enchantmentList.size()) {
		    p.sendMessage(ChatColor.RED + "Not all enchantments could be used!");
		}
		s.addShopItem(Integer.valueOf(args[3]) - 1, Double.valueOf(args[5]), Double.valueOf(args[6]), iStack);
		p.sendMessage(MessageWrapper.getString("shop_addItem", iStack.getType().toString().toLowerCase()));
	    } else {
		throw PlayerException.getException(PlayerExceptionMessageEnum.ENCHANTMENTLIST_INCOMPLETE);
	    }
	}
    }
}
