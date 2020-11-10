package com.ue.common.impl;

import java.util.List;

import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ue.common.api.CustomSkullService;
import com.ue.common.api.InventoryGui;
import com.ue.common.api.SkullTextureEnum;
import com.ue.common.utils.ServerProvider;

public class InventoryGuiImpl implements InventoryGui {

	private final ServerProvider serverProvider;
	private final CustomSkullService customSkullService;

	private Inventory inventory;

	/**
	 * Inject constructor.
	 * 
	 * @param serverProvider
	 * @param customSkullService
	 */
	@Inject
	public InventoryGuiImpl(ServerProvider serverProvider, CustomSkullService customSkullService) {
		this.serverProvider = serverProvider;
		this.customSkullService = customSkullService;
	}

	@Override
	public void setup(InventoryHolder invHolder, int size, String invName) {
		inventory = serverProvider.createInventory(invHolder, size, invName);
	}

	@Override
	public void setItem(int slot, List<String> lore, String name, Material material, int amount) {
		ItemStack item = serverProvider.createItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		inventory.setItem(slot, item);
	}

	@Override
	public void setItem(int slot, List<String> lore, String name, SkullTextureEnum skullTexture, int amount) {
		ItemStack item = customSkullService.getSkullWithName(skullTexture, name);
		item.setAmount(amount);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		inventory.setItem(slot, item);
	}
}
