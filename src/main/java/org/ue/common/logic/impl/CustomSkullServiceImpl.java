package org.ue.common.logic.impl;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class CustomSkullServiceImpl implements CustomSkullService {

	private static final Logger log = LoggerFactory.getLogger(CustomSkullServiceImpl.class);
	private final ServerProvider serverProvider;
	private Map<SkullTextureEnum, ItemStack> customSkullMap = new HashMap<>();
	
	public CustomSkullServiceImpl(ServerProvider serverProvider) {
		this.serverProvider = serverProvider;
	}
	
	@Override
	public void setup() {
		for(SkullTextureEnum skull: SkullTextureEnum.values()) {
			customSkullMap.put(skull, getSkull(skull.getValue(), ""));
		}
	}

	@Override
	public ItemStack getSkullWithName(SkullTextureEnum skullTexture, String name) {
		ItemStack item = customSkullMap.get(skullTexture);
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
		NamespacedKey key = new NamespacedKey(serverProvider.getJavaPluginInstance(), "ue-texture");
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
			log.warn("[Ultimate_Economy] Failed to request skull texture from minecraft.");
			log.warn("[Ultimate_Economy] Caused by: " + e.getMessage());
		}
		headMeta.setDisplayName(name);
		head.setItemMeta(headMeta);
		return head;
	}
}
