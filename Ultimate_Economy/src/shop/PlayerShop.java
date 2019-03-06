package shop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import ultimate_economy.Ultimate_Economy;

public class PlayerShop extends Shop{

	private boolean mode;

	public PlayerShop(Ultimate_Economy main,String name,Player player,String s) {
		super(main, name, player, s, true);
		mode = true;
		for(String item:itemNames) {
			loadItem(item);
		}
	}
	@Override
	public void setupShopItems() {
		super.setupShopItems();
		ItemStack itemStack = new ItemStack(Material.CRAFTING_TABLE);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName("Stock");
		itemStack.setItemMeta(meta);
		addShopItemToInv(itemStack, 1, size-2, 0.0, 0.0);
		itemNames.add("CRAFTING_TABLE_0");
	}
	public boolean available(String material) {
		boolean available = false;
		config = YamlConfiguration.loadConfiguration(file);
		if(config.getInt("ShopItems." + material + ".stock") >= config.getInt("ShopItems." + material + ".Amount")) {
			available = true;
		}
		return available;
	}
	public void increaseStock(String material, int stock) {
		config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopItems." + material + ".stock", (config.getInt("ShopItems." + material + ".stock") + stock));
		save();
	}
	public void decreaseStock(String material, int stock) {
		config = YamlConfiguration.loadConfiguration(file);
		if((config.getInt("ShopItems." + material + ".stock") - stock) >= 0) {
		config.set("ShopItems." + material + ".stock", (config.getInt("ShopItems." + material + ".stock") - stock));
		}
		save();
	}
	public String getOwner() {
		String owner = name.substring(name.indexOf("_") + 1);
		return owner;
	}
	public void setOwner(String newOwner) {
		File oldF = new File(main.getDataFolder() , name + ".yml");
		name = name.substring(0,name.indexOf("_") + 1) + newOwner;
		config = YamlConfiguration.loadConfiguration(file);
		config.set("ShopName", name);
		File newF = new File(main.getDataFolder() , name + ".yml");
		oldF.renameTo(newF);
		file = newF;
		villager.setCustomName(name);
		Inventory newInventory = Bukkit.createInventory(null, size,name);
		newInventory.setContents(inventory.getContents());
		inventory = newInventory;
		save();
	}
	private void stockpileBuild(String item) {
		List<String> list = config.getStringList("ShopItems." + item + ".lore");
		int slot = config.getInt("ShopItems." + item + ".Slot");
		int stock = config.getInt("ShopItems." + item + ".stock");
		boolean isEnchanted = false;
		boolean isPotion = false;
		String realName = item;
		String displayName = "default";
		if(item.contains("|")) {
			displayName = item.substring(0,item.indexOf("|"));
			item = item.substring(item.indexOf("|")+1);
		}
		if(item.contains("#Enchanted_")) {
			isEnchanted = true;
			item = item.substring(0,item.indexOf("#")).toUpperCase();
		}
		else if(item.contains("potion:")) {
			isPotion = true;
			item = item.substring(0,item.indexOf(":")).toUpperCase();
		}
		ItemStack itemStack = new ItemStack(Material.valueOf(item), 1);
		if(isEnchanted) {
			addEnchantments(itemStack, new ArrayList<String>(config.getStringList("ShopItems." + realName + ".enchantments")));
		}
		else if(isPotion) {
			String property = realName.substring(realName.indexOf("#")+1);
			boolean extended = false;
			boolean upgraded = false;
			if(property.equalsIgnoreCase("extended")) {
				extended = true;
			}
			else if(property.equalsIgnoreCase("upgraded")) {
				upgraded = true;
			}
			PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
			potionMeta.setBasePotionData(new PotionData(PotionType.valueOf(realName.substring(realName.indexOf(":") + 1,realName.indexOf("#")).toUpperCase()),extended,upgraded));
			itemStack.setItemMeta(potionMeta);
		}
		ItemMeta meta2 = itemStack.getItemMeta();
		if(stock != 1) {
		list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Items");
		}
		else {
			list.add(ChatColor.GREEN + String.valueOf(stock) + ChatColor.GOLD + " Item");
		}
		meta2.setLore(list);
		if(!displayName.equals("default")) {
			meta2.setDisplayName(displayName);
		}
		itemStack.setItemMeta(meta2);
		inventory.setItem(slot, itemStack);
	}
	public void switchStockpile() {
		config = YamlConfiguration.loadConfiguration(file);
		if(mode) {
			mode = false;
			inventory.clear();
			ItemStack stack2;
			ItemMeta meta;
			for(String item: itemNames) {
				if(!item.equals("ANVIL_0") && !item.equals("CRAFTING_TABLE_0")) {
					stockpileBuild(item);
				}
			}
			stack2 = new ItemStack(Material.CRAFTING_TABLE, 1);
			meta = stack2.getItemMeta();
			List<String> infos = new ArrayList<>();
			infos.add(ChatColor.GOLD + "Middle Mouse: " + ChatColor.GREEN + "close stockpile");
			infos.add(ChatColor.GOLD + "Rightclick: " + ChatColor.GREEN + "add specified amount");
			infos.add(ChatColor.GOLD + "Shift-Rightclick: " + ChatColor.GREEN + "add all");
			infos.add(ChatColor.GOLD + "Leftclick: " + ChatColor.GREEN + "get specified amount");
			meta.setLore(infos);
			meta.setDisplayName("Infos");
			stack2.setItemMeta(meta);
			inventory.setItem(size - 1, stack2);
		}
		else {
			mode = true;
			inventory.clear();
			for(String item: itemNames) {
				if(!item.equals("ANVIL_0") && !item.equals("CRAFTING_TABLE_0")) {
					loadItem(item);
				}
			}
			setupShopItems();
		}
	}
	public void refreshStockpile() {
		if(!mode) {
			for(String item: itemNames) {
				if(!item.equals("ANVIL_0") && !item.equals("CRAFTING_TABLE_0")) {
					stockpileBuild(item);
				}
			}
		}
	}
 }