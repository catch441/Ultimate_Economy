package org.ue.economyvillager.logic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Type;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.entity.Villager.Profession;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.logic.impl.InventoryGuiHandlerImpl;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyvillager.logic.api.EconomyVillager;

public class EconomyVillagerCustomizeHandlerImpl<T extends GeneralEconomyException> extends InventoryGuiHandlerImpl {

	private static class InvEntry {
		Profession profession;
		Material material;
		Type biomeType;
		SkullTextureEnum skullType;
		SkullTextureEnum skullTypeSelected;
		int size;

		private InvEntry(Profession profession, Material material) {
			this.profession = profession;
			this.material = material;
		}

		private InvEntry(Type biomeType, Material material) {
			this.biomeType = biomeType;
			this.material = material;
		}

		private InvEntry(SkullTextureEnum skullType, SkullTextureEnum skullTypeSelected, int size) {
			this.skullType = skullType;
			this.size = size;
			this.skullTypeSelected = skullTypeSelected;
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

	private static final Map<Integer, InvEntry> SIZES = new HashMap<>();
	static {
		SIZES.put(0, new InvEntry(SkullTextureEnum.ONE, SkullTextureEnum.ONE_GOLD, 9));
		SIZES.put(1, new InvEntry(SkullTextureEnum.TWO, SkullTextureEnum.TWO_GOLD, 18));
		SIZES.put(2, new InvEntry(SkullTextureEnum.THREE, SkullTextureEnum.THREE_GOLD, 27));
		SIZES.put(3, new InvEntry(SkullTextureEnum.FOUR, SkullTextureEnum.FOUR_GOLD, 36));
		SIZES.put(4, new InvEntry(SkullTextureEnum.FIVE, SkullTextureEnum.FIVE_GOLD, 45));
		SIZES.put(5, new InvEntry(SkullTextureEnum.SIX, SkullTextureEnum.SIX_GOLD, 54));
	}

	private final MessageWrapper messageWrapper;
	private EconomyVillager<T> ecoVillager;
	private Type selectedBiomeType;
	private Profession selectedProfession;
	private int selectedSize;

	public EconomyVillagerCustomizeHandlerImpl(MessageWrapper messageWrapper, ServerProvider serverProvider,
			CustomSkullService skullService, EconomyVillager<T> ecoVillager, Type biomeType, Profession profession) {
		super(skullService, serverProvider, null);
		this.messageWrapper = messageWrapper;
		setup(ecoVillager, biomeType, profession);
	}

	private void setup(EconomyVillager<T> ecoVillager, Type biomeType, Profession profession) {
		this.ecoVillager = ecoVillager;
		inventory = ecoVillager.createVillagerInventory(54, "Customize Villager");
		selectedBiomeType = biomeType;
		selectedProfession = profession;
		selectedSize = ecoVillager.getSize();
		// save, return
		setItem(Material.GREEN_WOOL, null, ChatColor.YELLOW + "save changes", 8);
		setItem(Material.RED_WOOL, null, ChatColor.RED + "exit without save", 7);
		// size
		for (Entry<Integer, InvEntry> entry : SIZES.entrySet()) {
			if (entry.getValue().size == selectedSize) {
				inventory.setItem(entry.getKey(), skullService.getSkullWithName(entry.getValue().skullTypeSelected,
						ChatColor.YELLOW + "" + entry.getValue().size));
			} else {
				inventory.setItem(entry.getKey(), skullService.getSkullWithName(entry.getValue().skullType,
						ChatColor.YELLOW + "" + entry.getValue().size));
			}
		}
		// biomes
		for (Entry<Integer, InvEntry> entry : BIOMES.entrySet()) {
			setItem(entry.getValue().material, null,
					ChatColor.YELLOW + entry.getValue().biomeType.toString().toLowerCase(), entry.getKey());
			if (entry.getValue().biomeType == biomeType) {
				setItem(entry.getValue().material, null, ChatColor.GOLD + biomeType.toString().toLowerCase(), 18);
			}
		}
		// professions
		for (Entry<Integer, InvEntry> entry : PROFESSIONS.entrySet()) {
			setItem(entry.getValue().material, null,
					ChatColor.YELLOW + entry.getValue().profession.toString().toLowerCase(), entry.getKey());
			if (entry.getValue().profession == profession) {
				setItem(entry.getValue().material, null, ChatColor.GOLD + profession.toString().toLowerCase(), 36);
			}
		}
	}

	@Override
	public void openInventory(Player player) {
		player.openInventory(inventory);
	}

	@Override
	public void handleInventoryClick(ClickType clickType, int rawSlot, EconomyPlayer ecoPlayer) {
		if (rawSlot == 7) {
			returnToBackLink(ecoPlayer.getPlayer());
		} else if (rawSlot == 8) {
			returnToBackLink(ecoPlayer.getPlayer());
			try {
				ecoVillager.changeSize(selectedSize);
				ecoVillager.changeProfession(selectedProfession);
				ecoVillager.changeBiomeType(selectedBiomeType);
				ecoPlayer.getPlayer()
						.sendMessage(messageWrapper.getString(MessageEnum.CONFIG_CHANGE,
								selectedSize + " " + selectedBiomeType.toString().toLowerCase() + " "
										+ selectedProfession.toString().toLowerCase()));
			} catch (GeneralEconomyException e) {
				ecoPlayer.getPlayer().sendMessage(e.getMessage());
			}
		} else if (rawSlot < 27 && rawSlot > 19) {
			setItem(BIOMES.get(rawSlot).material, null,
					ChatColor.GOLD + BIOMES.get(rawSlot).biomeType.toString().toLowerCase(), 18);
			selectedBiomeType = BIOMES.get(rawSlot).biomeType;
		} else if ((rawSlot < 54 && rawSlot > 46) || (rawSlot < 45 && rawSlot > 37)) {
			setItem(PROFESSIONS.get(rawSlot).material, null,
					ChatColor.GOLD + PROFESSIONS.get(rawSlot).profession.toString().toLowerCase(), 36);
			selectedProfession = PROFESSIONS.get(rawSlot).profession;
		} else if (rawSlot < 6) {
			// reset old selected size
			for (Entry<Integer, InvEntry> entry : SIZES.entrySet()) {
				if (entry.getValue().size == selectedSize) {
					inventory.setItem(entry.getKey(), skullService.getSkullWithName(entry.getValue().skullType,
							ChatColor.YELLOW + "" + entry.getValue().size));
					break;
				}
			}
			// set new selected size
			inventory.setItem(rawSlot, skullService.getSkullWithName(SIZES.get(rawSlot).skullTypeSelected,
					ChatColor.YELLOW + "" + SIZES.get(rawSlot).size));
			selectedSize = (rawSlot + 1) * 9;
		}
	}
}
