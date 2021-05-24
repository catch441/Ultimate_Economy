package org.ue.common.logic.impl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;

public abstract class InventoryGuiHandlerImpl implements InventoryGuiHandler {

	protected final ServerProvider serverProvider;
	protected final CustomSkullService skullService;
	protected Inventory inventory;
	private Inventory backLink;

	protected InventoryGuiHandlerImpl(CustomSkullService skullService, ServerProvider serverProvider,
			Inventory backLink) {
		this.serverProvider = serverProvider;
		this.skullService = skullService;
		this.backLink = backLink;
	}

	@Override
	public void openInventory(Player player) {
		player.openInventory(inventory);
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	@Override
	public void updateBackLink(Inventory backLink) {
		this.backLink = backLink;
	}

	protected void returnToBackLink(Player player) {
		player.closeInventory();
		if(backLink != null) {
			player.openInventory(backLink);
		}
	}
	
	protected void updateItemLore(int slot,  List<String> lore) {
		ItemMeta meta = inventory.getItem(slot).getItemMeta();
		meta.setLore(lore);
		inventory.getItem(slot).setItemMeta(meta);
	}

	protected void setItem(Material material, List<String> lore, String displayName, int slot) {
		if (lore == null) {
			lore = new ArrayList<>();
		}
		ItemStack stack = serverProvider.createItemStack(material, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		stack.setItemMeta(meta);
		inventory.setItem(slot, stack);
	}

	protected void setSkull(SkullTextureEnum skullTexture, List<String> lore, String displayName, int slot) {
		ItemStack stack = skullService.getSkullWithName(skullTexture, displayName);
		if (lore != null) {
			ItemMeta meta = stack.getItemMeta();
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}
		inventory.setItem(slot, stack);
	}
}
