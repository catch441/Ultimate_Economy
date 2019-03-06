package shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ultimate_economy.Ultimate_Economy;

public class AdminShop extends Shop{
	
	public AdminShop(Ultimate_Economy main,String name,Player player,String s) {
		super(main, name, player, s, false);
		for(String item:itemNames) {
			loadItem(item);
		}
	}
	@Override
	public void loadItem(String name1) {
		super.loadItem(name1);
		if(config.getString("ShopItems." + name1 + ".Name") != null) {
			String string = config.getString("ShopItems." + name1 + ".Name");
			if(string.contains("SPAWNER")) {
				String entityname = string.substring(8);
				ItemStack itemStack = new ItemStack(Material.SPAWNER);
				ItemMeta meta = itemStack.getItemMeta();
				meta.setDisplayName(entityname);
				itemStack.setItemMeta(meta);
				addShopItemToInv(itemStack, config.getInt("ShopItems." + name1 + ".Amount"), config.getInt("ShopItems." + name1 + ".Slot"), config.getDouble("ShopItems." + name1 + ".sellPrice"), config.getDouble("ShopItems." + name1 + ".buyPrice"));
			}
		}
	}
	public String getSlotSpawnerName(int slot) {
	    ItemStack itemStack = inventory.getItem(slot - 1);
	    String name = itemStack.getItemMeta().getDisplayName();
	    name = "SPAWNER_" + name;
		return name;
	}
 }