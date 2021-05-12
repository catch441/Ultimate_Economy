package org.ue.economyvillager.logic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Type;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyvillager.logic.api.EconomyVillager;
import org.ue.economyvillager.logic.api.EconomyVillagerCustomizeHandler;

public abstract class EconomyVillagerCustomizeHandlerImpl<T extends GeneralEconomyException>
		implements EconomyVillagerCustomizeHandler<T> {

	private static class InvEntry {
		Profession profession;
		Material material;
		Type biomeType;

		private InvEntry(Profession profession, Material material) {
			this.profession = profession;
			this.material = material;
		}

		private InvEntry(Type biomeType, Material material) {
			this.biomeType = biomeType;
			this.material = material;
		}
	}

	private static final Map<Integer, InvEntry> PROFESSIONS = new HashMap<>();
	static {
		PROFESSIONS.put(38, new InvEntry(Profession.NITWIT, Material.GREEN_TERRACOTTA));
		PROFESSIONS.put(39, new InvEntry(Profession.FLETCHER, Material.ARROW));
		PROFESSIONS.put(40, new InvEntry(Profession.LEATHERWORKER, Material.LEATHER));
		PROFESSIONS.put(41, new InvEntry(Profession.MASON, Material.STONECUTTER));
		PROFESSIONS.put(42, new InvEntry(Profession.CLERIC, Material.EXPERIENCE_BOTTLE));
		PROFESSIONS.put(43, new InvEntry(Profession.SHEPHERD, Material.WHITE_WOOL));
		PROFESSIONS.put(44, new InvEntry(Profession.LIBRARIAN, Material.BOOK));
		PROFESSIONS.put(47, new InvEntry(Profession.CARTOGRAPHER, Material.FILLED_MAP));
		PROFESSIONS.put(48, new InvEntry(Profession.BUTCHER, Material.PORKCHOP));
		PROFESSIONS.put(49, new InvEntry(Profession.ARMORER, Material.DIAMOND_CHESTPLATE));
		PROFESSIONS.put(50, new InvEntry(Profession.FARMER, Material.WHEAT));
		PROFESSIONS.put(51, new InvEntry(Profession.TOOLSMITH, Material.DIAMOND_AXE));
		PROFESSIONS.put(52, new InvEntry(Profession.WEAPONSMITH, Material.DIAMOND_SWORD));
		PROFESSIONS.put(53, new InvEntry(Profession.FISHERMAN, Material.FISHING_ROD));
	}
	private static final Map<Integer, InvEntry> BIOMES = new HashMap<>();
	static {
		BIOMES.put(20, new InvEntry(Type.DESERT, Material.SAND));
		BIOMES.put(21, new InvEntry(Type.JUNGLE, Material.JUNGLE_LOG));
		BIOMES.put(22, new InvEntry(Type.PLAINS, Material.GRASS));
		BIOMES.put(23, new InvEntry(Type.SAVANNA, Material.ACACIA_LOG));
		BIOMES.put(24, new InvEntry(Type.SNOW, Material.SNOW_BLOCK));
		BIOMES.put(25, new InvEntry(Type.SWAMP, Material.LILY_PAD));
		BIOMES.put(26, new InvEntry(Type.TAIGA, Material.SPRUCE_LOG));
	}

	protected final CustomSkullService skullService;
	private final ServerProvider serverProvider;
	private final MessageWrapper messageWrapper;
	private EconomyVillager<T> ecoVillager;
	private Inventory inventory;
	private Type selectedBiomeType;
	private Profession selectedProfession;
	private int selectedSize;

	public EconomyVillagerCustomizeHandlerImpl(MessageWrapper messageWrapper, ServerProvider serverProvider,
			CustomSkullService skullService) {
		this.skullService = skullService;
		this.serverProvider = serverProvider;
		this.messageWrapper = messageWrapper;
	}

	protected void setupCustomizeHandler(EconomyVillager<T> ecoVillager, Type biomeType, Profession profession) {
		this.ecoVillager = ecoVillager;
		inventory = ecoVillager.createVillagerInventory(54, "Customize Villager");
		selectedBiomeType = biomeType;
		selectedProfession = profession;
		selectedSize = ecoVillager.getSize();
		// save, return
		setItem(Material.GREEN_WOOL, ChatColor.YELLOW + "save changes", 8);
		setItem(Material.RED_WOOL, ChatColor.RED + "exit without save", 7);
		// size
		inventory.setItem(0, skullService.getSkullWithName(SkullTextureEnum.ONE, ChatColor.YELLOW + "9"));
		inventory.setItem(1, skullService.getSkullWithName(SkullTextureEnum.TWO, ChatColor.YELLOW + "18"));
		inventory.setItem(2, skullService.getSkullWithName(SkullTextureEnum.THREE, ChatColor.YELLOW + "27"));
		inventory.setItem(3, skullService.getSkullWithName(SkullTextureEnum.FOUR, ChatColor.YELLOW + "36"));
		inventory.setItem(4, skullService.getSkullWithName(SkullTextureEnum.FIVE, ChatColor.YELLOW + "45"));
		inventory.setItem(5, skullService.getSkullWithName(SkullTextureEnum.SIX, ChatColor.YELLOW + "54"));
		// biomes
		for (Entry<Integer, InvEntry> entry : BIOMES.entrySet()) {
			setItem(entry.getValue().material, ChatColor.YELLOW + entry.getValue().biomeType.toString().toLowerCase(),
					entry.getKey());
			if (entry.getValue().biomeType == biomeType) {
				setItem(entry.getValue().material, ChatColor.GOLD + biomeType.toString().toLowerCase(), 18);
			}
		}
		// professions
		for (Entry<Integer, InvEntry> entry : PROFESSIONS.entrySet()) {
			setItem(entry.getValue().material, ChatColor.YELLOW + entry.getValue().profession.toString().toLowerCase(),
					entry.getKey());
			if (entry.getValue().profession == profession) {
				setItem(entry.getValue().material, ChatColor.GOLD + profession.toString().toLowerCase(), 36);
			}
		}
	}

	@Override
	public void openCustomizer(Player player) {
		player.openInventory(inventory);
	}

	@Override
	public void handleCustomizeInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() != null) {
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			int slot = event.getRawSlot();
			if (slot == 7) {
				player.closeInventory();
			} else if (slot == 8) {
				player.closeInventory();
				try {
					ecoVillager.changeSize(selectedSize);
					ecoVillager.changeProfession(selectedProfession);
					ecoVillager.changeBiomeType(selectedBiomeType);
					player.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, selectedSize + " "
							+ selectedBiomeType.name() + " " + selectedProfession.toString().toLowerCase()));
				} catch (GeneralEconomyException e) {
					player.sendMessage(e.getMessage());
				}
			} else if (slot < 27 && slot > 19) {
				setItem(BIOMES.get(slot).material, ChatColor.GOLD + BIOMES.get(slot).biomeType.toString().toLowerCase(),
						18);
				selectedBiomeType = BIOMES.get(slot).biomeType;
			} else if ((slot < 54 && slot > 46) || (slot < 45 && slot > 37)) {
				setItem(PROFESSIONS.get(slot).material,
						ChatColor.GOLD + PROFESSIONS.get(slot).profession.toString().toLowerCase(), 36);
				selectedProfession = PROFESSIONS.get(slot).profession;
			} else if (slot < 6) {
				selectedSize = (slot + 1) * 9;
			}
		}
	}

	private void setItem(Material material, String displayName, int slot) {
		ItemStack stack = serverProvider.createItemStack(material, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(displayName);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		stack.setItemMeta(meta);
		inventory.setItem(slot, stack);
	}
}
