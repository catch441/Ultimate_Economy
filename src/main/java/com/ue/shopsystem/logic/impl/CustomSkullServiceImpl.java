package com.ue.shopsystem.logic.impl;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.slf4j.Logger;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.ue.shopsystem.logic.api.CustomSkullService;
import com.ue.ultimate_economy.UltimateEconomy;

public class CustomSkullServiceImpl implements CustomSkullService {

	// minecraft skull texture links
	public final String plus = "http://textures.minecraft.net/texture/"
			+ "9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777";
	public final String minus = "http://textures.minecraft.net/texture/"
			+ "935e4e26eafc11b52c11668e1d6634e7d1d0d21c411cb085f9394268eb4cdfba";
	public final String one = "http://textures.minecraft.net/texture/"
			+ "d2a6f0e84daefc8b21aa99415b16ed5fdaa6d8dc0c3cd591f49ca832b575";
	public final String seven = "http://textures.minecraft.net/texture/"
			+ "9e198fd831cb61f3927f21cf8a7463af5ea3c7e43bd3e8ec7d2948631cce879";
	public final String slotfilled = "http://textures.minecraft.net/texture/"
			+ "9e42f682e430b55b61204a6f8b76d5227d278ed9ec4d98bda4a7a4830a4b6";
	public final String kOff = "http://textures.minecraft.net/texture/"
			+ "e883b5beb4e601c3cbf50505c8bd552e81b996076312cffe27b3cc1a29e3";
	public final String ten = "http://textures.minecraft.net/texture/"
			+ "b0cf9794fbc089dab037141f67875ab37fadd12f3b92dba7dd2288f1e98836";
	public final String twenty = "http://textures.minecraft.net/texture/"
			+ "f7b29a1bb25b2ad8ff3a7a38228189c9461f457a4da98dae29384c5c25d85";
	public final String buy = "http://textures.minecraft.net/texture/"
			+ "e5da4847272582265bdaca367237c96122b139f4e597fbc6667d3fb75fea7cf6";
	public final String sell = "http://textures.minecraft.net/texture/"
			+ "abae89e92ac362635ba3e9fb7c12b7ddd9b38adb11df8aa1aff3e51ac428a4";
	public final String kOn = "http://textures.minecraft.net/texture/"
			+ "d42a4802b6b2deb49cfbb4b7e267e2f9ad45da24c73286f97bef91d21616496";
	public final String slotempty = "http://textures.minecraft.net/texture/"
			+ "b55d5019c8d55bcb9dc3494ccc3419757f89c3384cf3c9abec3f18831f35b0";
	private final Logger logger;

	private Map<String, ItemStack> customSkullMap = new HashMap<>();
	
	/**
	 * Inject constructor.
	 * 
	 * @param logger
	 */
	@Inject
	public CustomSkullServiceImpl(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void setup() {
		customSkullMap.put("SLOTEMPTY", getSkull(slotempty, ""));
		customSkullMap.put("SLOTFILLED", getSkull(slotfilled, ""));
		customSkullMap.put("K_ON", getSkull(kOn, ""));
		customSkullMap.put("K_OFF", getSkull(kOff, ""));
		customSkullMap.put("SELL", getSkull(sell, ""));
		customSkullMap.put("BUY", getSkull(buy, ""));
		customSkullMap.put("TWENTY", getSkull(twenty, ""));
		customSkullMap.put("TEN", getSkull(ten, ""));
		customSkullMap.put("SEVEN", getSkull(seven, ""));
		customSkullMap.put("ONE", getSkull(one, ""));
		customSkullMap.put("MINUS", getSkull(minus, ""));
		customSkullMap.put("PLUS", getSkull(plus, ""));
	}

	@Override
	public ItemStack getSkullWithName(String skull, String name) {
		skull = skull.toUpperCase();
		ItemStack item = customSkullMap.get(skull);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack getSkull(String url, String name) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		if (url.isEmpty()) {
			return head;
		}
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		// for testing
		NamespacedKey key = new NamespacedKey(UltimateEconomy.getInstance, "ue-texture");
		headMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, url);
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = Base64.getEncoder()
				.encode((String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes()));
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		Field profileField = null;
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			logger.warn("[Ultimate_Economy] Failed to request skull texture from minecraft.");
			logger.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
		headMeta.setDisplayName(name);
		head.setItemMeta(headMeta);
		return head;
	}
}
