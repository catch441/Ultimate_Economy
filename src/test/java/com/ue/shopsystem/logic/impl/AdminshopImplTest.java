package com.ue.shopsystem.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.ue.common.api.CustomSkullService;
import com.ue.common.api.SkullTextureEnum;
import com.ue.common.utils.MessageWrapper;
import com.ue.common.utils.ServerProvider;
import com.ue.config.dataaccess.api.ConfigDao;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayer;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.api.GeneralEconomyValidationHandler;
import com.ue.general.impl.GeneralEconomyException;
import com.ue.shopsystem.dataaccess.api.ShopDao;
import com.ue.shopsystem.logic.api.AdminshopManager;
import com.ue.shopsystem.logic.api.ShopValidationHandler;
import com.ue.shopsystem.logic.to.ShopItem;

@ExtendWith(MockitoExtension.class)
public class AdminshopImplTest {

	@InjectMocks
	AdminshopImpl adminshop;
	@Mock
	ServerProvider serverProvider;
	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ConfigDao configDao;
	@Mock
	CustomSkullService skullService;
	@Mock
	ShopDao shopDao;
	@Mock
	ShopValidationHandler validationHandler;
	@Mock
	Logger logger;
	@Mock
	ConfigManager configManager;
	@Mock
	AdminshopManager adminshopManager;
	@Mock
	GeneralEconomyValidationHandler generalValidator;

	private void createNewAdminshop() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(skullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(infoItem);
		adminshop.setupNew("myshop", "A0", loc, 9);
	}

	@Test
	public void sellShopItemTestWithInvalidSlot() throws GeneralEconomyException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSlot(-1, 8);
		assertThrows(GeneralEconomyException.class, () -> adminshop.sellShopItem(-1, 1, null, true));
	}

	@Test
	public void sellShopItemTestWithOfflinePlayer() throws EconomyPlayerException {
		createNewAdminshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerIsOnline(ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> adminshop.sellShopItem(1, 1, ecoPlayer, true));
	}

	@Test
	public void sellShopItemTestPlural() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack stackCloneCloneClone = mock(ItemStack.class);
		ItemStack contentStack = mock(ItemStack.class);
		ItemStack contentStackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		ItemStack[] contents = new ItemStack[1];
		contents[0] = contentStack;
		when(contentStackClone.getAmount()).thenReturn(10);
		when(inv.getStorageContents()).thenReturn(contents);
		when(player.getInventory()).thenReturn(inv);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 2, stack));
		when(stackCloneClone.clone()).thenReturn(stackCloneCloneClone);
		when(contentStackClone.toString()).thenReturn("itemString");
		when(stackCloneCloneClone.toString()).thenReturn("itemString");
		when(configManager.getCurrencyText(10.0)).thenReturn("$");
		when(messageWrapper.getString("shop_sell_plural", "10", 10.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> adminshop.sellShopItem(3, 10, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(2)).checkForSlotIsNotEmpty(3, adminshop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(10.0, false));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}

	@Test
	public void sellShopItemTestSingular() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack stackCloneCloneClone = mock(ItemStack.class);
		ItemStack contentStack = mock(ItemStack.class);
		ItemStack contentStackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		ItemStack[] contents = new ItemStack[1];
		contents[0] = contentStack;
		when(contentStackClone.getAmount()).thenReturn(10);
		when(inv.getStorageContents()).thenReturn(contents);
		when(player.getInventory()).thenReturn(inv);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(contentStack.clone()).thenReturn(contentStackClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 2, stack));
		when(stackCloneClone.clone()).thenReturn(stackCloneCloneClone);
		when(contentStackClone.toString()).thenReturn("itemString");
		when(stackCloneCloneClone.toString()).thenReturn("itemString");
		when(configManager.getCurrencyText(1.0)).thenReturn("$");
		when(messageWrapper.getString("shop_sell_singular", "1", 1.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> adminshop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(2)).checkForSlotIsNotEmpty(3, adminshop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(ecoPlayer).increasePlayerAmount(1.0, false));
		verify(inv).removeItem(contentStackClone);
		verify(player).sendMessage("my message");
	}

	@Test
	public void sellShopItemTestOnlyBuyPrice() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 0, 2, stack));

		assertDoesNotThrow(() -> adminshop.sellShopItem(3, 1, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(2)).checkForSlotIsNotEmpty(3, adminshop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(() -> verify(ecoPlayer, never()).increasePlayerAmount(anyDouble(), eq(false)));
		verify(player, never()).sendMessage(anyString());
	}

	@Test
	public void setupNewTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack skullInfoItem = mock(ItemStack.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemStack stuff = mock(ItemStack.class);
		ItemMeta stuffMeta = mock(ItemMeta.class);
		ItemMeta infoItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		when(infoItemMeta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(stuff);

		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(stuff.getItemMeta()).thenReturn(stuffMeta);
		when(infoItem.getItemMeta()).thenReturn(infoItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(skullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(skullInfoItem);
		
		adminshop.setupNew("myshop", "A0", loc, 9);

		verify(shopDao).setupSavefile("A0");
		verify(shopDao).saveShopLocation(loc);
		verify(shopDao).saveShopName("myshop");
		verify(adminshop.getShopVillager()).setCustomName("myshop");
		verify(adminshop.getShopVillager()).setCustomNameVisible(true);
		verify(adminshop.getShopVillager()).setSilent(true);
		verify(adminshop.getShopVillager()).setVillagerLevel(2);
		verify(adminshop.getShopVillager()).setCollidable(false);
		verify(adminshop.getShopVillager()).setInvulnerable(true);
		verify(adminshop.getShopVillager()).setProfession(Profession.NITWIT);
		verify(adminshop.getShopVillager()).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(adminshop.getShopVillager()).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("A0", adminshop.getShopId());
		assertEquals(loc, adminshop.getShopLocation());
		assertEquals("myshop", adminshop.getName());

		verify(infoItemMeta).setDisplayName("Info");
		verify(infoItem, times(2)).setItemMeta(infoItemMeta);
		verify(infoItemMeta).setLore(Arrays.asList("§6Rightclick: §asell specified amount",
				"§6Shift-Rightclick: §asell all", "§6Leftclick: §abuy"));
		verify(inv).setItem(8, infoItem);
	}

	@Test
	public void setupExistingTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack skullInfoItem = mock(ItemStack.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemStack emptyItem = mock(ItemStack.class);
		ItemStack stuff = mock(ItemStack.class);
		ItemStack filledItem = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);	
		ItemMeta shopItemStackMeta = mock(ItemMeta.class);
		ItemMeta stuffMeta = mock(ItemMeta.class);
		ItemMeta infoItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editor = mock(Inventory.class);
		Inventory slotEditor = mock(Inventory.class);
		ShopItem shopItem = mock(ShopItem.class);
		Entity entity = mock(Entity.class);
		when(entity.getCustomName()).thenReturn("myshop");
		when(world.getNearbyEntities(loc, 10, 10, 10)).thenReturn(Arrays.asList(entity));
		when(infoItemMeta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(slotEditor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(stuff);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(shopItemStack.getItemMeta()).thenReturn(shopItemStackMeta);
		when(stuff.getItemMeta()).thenReturn(stuffMeta);
		
		when(infoItem.getItemMeta()).thenReturn(infoItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(skullService.getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 1")).thenReturn(filledItem);
		when(skullService.getSkullWithName(eq(SkullTextureEnum.SLOTEMPTY), anyString())).thenReturn(emptyItem);
		when(skullService.getSkullWithName(eq(SkullTextureEnum.K_OFF), anyString())).thenReturn(skullInfoItem);

		when(shopDao.loadShopName()).thenReturn("myshop");
		when(shopDao.loadShopSize()).thenReturn(9);
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopVillagerProfession()).thenReturn(Profession.ARMORER);
		when(shopDao.loadItemHashList()).thenReturn(Arrays.asList(543346345));
		when(shopDao.loadItem(543346345)).thenReturn(shopItem);
		when(shopItem.getSlot()).thenReturn(0);
		when(shopItem.getAmount()).thenReturn(5);
		when(shopItem.getSellPrice()).thenReturn(2.0);
		when(shopItem.getBuyPrice()).thenReturn(3.0);
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(inv.getItem(0)).thenReturn(shopItemStack);
		
		assertDoesNotThrow(() -> adminshop.setupExisting(null, "A0"));
		
		verify(editor).setItem(0, filledItem);
		verify(editor, times(7)).setItem(anyInt(), eq(emptyItem));
		verify(entity).remove();
		verify(shopDao).setupSavefile("A0");
		verify(adminshop.getShopVillager()).setCustomName("myshop");
		verify(adminshop.getShopVillager()).setCustomNameVisible(true);
		verify(adminshop.getShopVillager()).setSilent(true);
		verify(adminshop.getShopVillager()).setVillagerLevel(2);
		verify(adminshop.getShopVillager()).setCollidable(false);
		verify(adminshop.getShopVillager()).setInvulnerable(true);
		verify(adminshop.getShopVillager()).setProfession(Profession.NITWIT);
		verify(adminshop.getShopVillager()).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(adminshop.getShopVillager()).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("A0", adminshop.getShopId());
		assertEquals(loc, adminshop.getShopLocation());
		assertEquals("myshop", adminshop.getName());
		assertDoesNotThrow(() -> assertEquals(shopItem, adminshop.getShopItem(0)));

		verify(infoItemMeta).setDisplayName("Info");
		verify(infoItem, times(2)).setItemMeta(infoItemMeta);
		verify(infoItemMeta).setLore(Arrays.asList("§6Rightclick: §asell specified amount",
				"§6Shift-Rightclick: §asell all", "§6Leftclick: §abuy"));
		verify(inv).setItem(8, infoItem);

		verify(shopItemStack).setItemMeta(shopItemStackMeta);
		verify(shopItemStackMeta).setLore(Arrays.asList("§65 buy for §a3.0 $", "§65 sell for §a2.0 $"));
		verify(inv).setItem(0, shopItemStack);
	}

	@Test
	public void setupExistingOldTest() {
		Plugin plugin = mock(Plugin.class);
		JavaPlugin javaPlugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack skullInfoItem = mock(ItemStack.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemStack stuff = mock(ItemStack.class);
		ItemStack shopItemStack = mock(ItemStack.class);
		ItemMeta shopItemStackMeta = mock(ItemMeta.class);
		ItemMeta stuffMeta = mock(ItemMeta.class);
		ItemMeta infoItemMeta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		ShopItem shopItem = mock(ShopItem.class);
		when(infoItemMeta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(stuff);
		when(serverProvider.createItemStack(Material.BARRIER, 1)).thenReturn(stuff);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getPluginInstance()).thenReturn(plugin);
		when(serverProvider.getJavaPluginInstance()).thenReturn(javaPlugin);
		when(shopItemStack.getItemMeta()).thenReturn(shopItemStackMeta);
		when(stuff.getItemMeta()).thenReturn(stuffMeta);
		when(infoItem.getItemMeta()).thenReturn(infoItemMeta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(skullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(skullInfoItem);

		when(shopDao.loadShopName()).thenReturn("myshop");
		when(shopDao.loadShopSize()).thenReturn(9);
		assertDoesNotThrow(() -> when(shopDao.loadShopLocation()).thenReturn(loc));
		when(shopDao.loadShopVillagerProfession()).thenReturn(Profession.ARMORER);
		when(shopDao.loadItemHashList()).thenReturn(Arrays.asList(543346345));
		when(shopDao.loadItem(543346345)).thenReturn(shopItem);
		when(shopItem.getSlot()).thenReturn(0);
		when(shopItem.getAmount()).thenReturn(5);
		when(shopItem.getSellPrice()).thenReturn(2.0);
		when(shopItem.getBuyPrice()).thenReturn(3.0);
		when(shopItem.getItemStack()).thenReturn(shopItemStack);
		when(inv.getItem(0)).thenReturn(shopItemStack);
		File dataFolder = mock(File.class);
		when(plugin.getDataFolder()).thenReturn(dataFolder);

		assertDoesNotThrow(() -> adminshop.setupExisting("myshop", "A0"));

		verifyNoInteractions(logger);
		assertDoesNotThrow(() -> verify(shopDao).changeSavefileName(dataFolder, "A0"));
		verify(shopDao).setupSavefile("A0");
		verify(adminshop.getShopVillager()).setCustomName("myshop");
		verify(adminshop.getShopVillager()).setCustomNameVisible(true);
		verify(adminshop.getShopVillager()).setSilent(true);
		verify(adminshop.getShopVillager()).setVillagerLevel(2);
		verify(adminshop.getShopVillager()).setCollidable(false);
		verify(adminshop.getShopVillager()).setInvulnerable(true);
		verify(adminshop.getShopVillager()).setProfession(Profession.NITWIT);
		verify(adminshop.getShopVillager()).setMetadata(eq("ue-id"), any(FixedMetadataValue.class));
		verify(adminshop.getShopVillager()).setMetadata(eq("ue-type"), any(FixedMetadataValue.class));
		assertEquals("A0", adminshop.getShopId());
		assertEquals(loc, adminshop.getShopLocation());
		assertEquals("myshop", adminshop.getName());
		assertDoesNotThrow(() -> assertEquals(shopItem, adminshop.getShopItem(0)));

		verify(infoItemMeta).setDisplayName("Info");
		verify(infoItem, times(2)).setItemMeta(infoItemMeta);
		verify(infoItemMeta).setLore(Arrays.asList("§6Rightclick: §asell specified amount",
				"§6Shift-Rightclick: §asell all", "§6Leftclick: §abuy"));
		verify(inv).setItem(8, infoItem);

		verify(shopItemStack).setItemMeta(shopItemStackMeta);
		verify(shopItemStackMeta).setLore(Arrays.asList("§65 buy for §a3.0 $", "§65 sell for §a2.0 $"));
		verify(inv).setItem(0, shopItemStack);
	}

	@Test
	public void deleteShopTest() {
		createNewAdminshop();
		adminshop.deleteShop();
		verify(adminshop.getShopVillager()).remove();
		verify(shopDao).deleteFile();
		verify(adminshop.getShopLocation().getWorld()).save();
	}

	@Test
	public void buyShopItemTestWithInvalidSlot() throws GeneralEconomyException {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(0, 1, 2, stack));
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSlot(9, 8);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertThrows(GeneralEconomyException.class, () -> adminshop.buyShopItem(9, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithEmptySlot() throws GeneralEconomyException, ShopSystemException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForSlotIsNotEmpty(3,
				adminshop.getShopInventory(), 1);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		assertThrows(GeneralEconomyException.class, () -> adminshop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithOfflinePlayer() throws EconomyPlayerException {
		createNewAdminshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerIsOnline(ecoPlayer);
		assertThrows(EconomyPlayerException.class, () -> adminshop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithFullInventory() throws EconomyPlayerException {
		createNewAdminshop();
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		doThrow(EconomyPlayerException.class).when(validationHandler).checkForPlayerInventoryNotFull(inv);
		assertThrows(EconomyPlayerException.class, () -> adminshop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(ecoPlayer, never()).decreasePlayerAmount(anyDouble(), eq(true)));
	}

	@Test
	public void buyShopItemTestWithNoMessage() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 4, stack));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);

		assertDoesNotThrow(() -> adminshop.buyShopItem(3, ecoPlayer, false));

		assertDoesNotThrow(() -> verify(generalValidator, times(1)).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(2)).checkForSlotIsNotEmpty(3, adminshop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(2);
		verify(inv).addItem(stackCloneClone);
		verify(player, never()).sendMessage(anyString());
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(4.0, true));
	}

	@Test
	public void buyShopItemTestWithNormalItemSingular() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 2, stack));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(configManager.getCurrencyText(2.0)).thenReturn("$");
		when(messageWrapper.getString("shop_buy_singular", "1", 2.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> adminshop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(2)).checkForSlotIsNotEmpty(3, adminshop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(1);
		verify(inv).addItem(stackCloneClone);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(2.0, true));
	}

	@Test
	public void buyShopItemTestWithNormalItemPlural() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 4, stack));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(configManager.getCurrencyText(4.0)).thenReturn("$");
		when(messageWrapper.getString("shop_buy_plural", "2", 4.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> adminshop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(2)).checkForSlotIsNotEmpty(3, adminshop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(2);
		verify(inv).addItem(stackCloneClone);
		verify(player).sendMessage("my message");
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(4.0, true));
	}

	@Test
	public void buyShopItemTestWithSpawner() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta stackMetaCloneClone = mock(ItemMeta.class);
		EconomyPlayer ecoPlayer = mock(EconomyPlayer.class);
		Player player = mock(Player.class);
		PlayerInventory inv = mock(PlayerInventory.class);
		when(ecoPlayer.getName()).thenReturn("catch441");
		when(stackMetaCloneClone.getDisplayName()).thenReturn("COW");
		when(stackCloneClone.getType()).thenReturn(Material.SPAWNER);
		when(stackCloneClone.getItemMeta()).thenReturn(stackMetaCloneClone);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 2, stack));
		when(ecoPlayer.getPlayer()).thenReturn(player);
		when(player.getInventory()).thenReturn(inv);
		when(configManager.getCurrencyText(2.0)).thenReturn("$");
		when(messageWrapper.getString("shop_buy_singular", "1", 2.0, "$")).thenReturn("my message");

		assertDoesNotThrow(() -> adminshop.buyShopItem(3, ecoPlayer, true));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(3, 8));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerIsOnline(ecoPlayer));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(2)).checkForSlotIsNotEmpty(3, adminshop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPlayerInventoryNotFull(inv));
		verify(stackCloneClone).setAmount(1);
		verify(inv).addItem(stackCloneClone);
		verify(player).sendMessage("my message");
		verify(stackMetaCloneClone).setDisplayName("COW-catch441");
		verify(stackCloneClone).setItemMeta(stackMetaCloneClone);
		assertDoesNotThrow(() -> verify(ecoPlayer).decreasePlayerAmount(2.0, true));
	}

	@Test
	public void addShopItemTest() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> adminshop.addShopItem(0, 1, 4, stack));
		assertDoesNotThrow(() -> verify(validationHandler).checkForItemDoesNotExist(eq("item string".hashCode()), anyList()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsEmpty(0, adminshop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("1.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("4.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(1.0, 4.0));
		assertDoesNotThrow(() -> assertEquals(1, adminshop.getItemList().size()));
		ShopItem shopItem = assertDoesNotThrow(() -> adminshop.getShopItem(0));
		verify(shopDao).saveShopItem(shopItem, false);
		assertEquals(2, shopItem.getAmount());
		assertEquals(4.0, shopItem.getBuyPrice());
		assertEquals(1.0, shopItem.getSellPrice());
		assertEquals(0, shopItem.getSlot());
		assertEquals("item string".hashCode(), shopItem.getItemHash());
		assertEquals(stackCloneClone, shopItem.getItemStack());
		// verify that the set occupied method of the editor is called
		verify(skullService).getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 1");
		verify(adminshop.getShopInventory()).setItem(0, stackClone);
		verify(stackClone).setAmount(2);
		verify(stackMetaClone).setLore(Arrays.asList("§62 buy for §a4.0 $", "§62 sell for §a1.0 $"));
	}

	@Test
	public void addShopItemTestWithOnlyBuyPrice() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(stackMetaClone.getLore()).thenReturn(new ArrayList<>());
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> adminshop.addShopItem(0, 0, 4, stack));
		assertDoesNotThrow(() -> verify(validationHandler).checkForItemDoesNotExist(eq("item string".hashCode()), anyList()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsEmpty(0, adminshop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("0.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("4.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(0.0, 4.0));
		assertDoesNotThrow(() -> assertEquals(1, adminshop.getItemList().size()));
		ShopItem shopItem = assertDoesNotThrow(() -> adminshop.getShopItem(0));
		verify(shopDao).saveShopItem(shopItem, false);
		assertEquals(2, shopItem.getAmount());
		assertEquals(4.0, shopItem.getBuyPrice());
		assertEquals(0.0, shopItem.getSellPrice());
		assertEquals(0, shopItem.getSlot());
		assertEquals("item string".hashCode(), shopItem.getItemHash());
		assertEquals(stackCloneClone, shopItem.getItemStack());
		// verify that the set occupied method of the editor is called
		verify(skullService).getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 1");
		verify(adminshop.getShopInventory()).setItem(0, stackClone);
		verify(stackClone).setAmount(2);
		verify(stackMetaClone).setLore(Arrays.asList("§62 buy for §a4.0 $"));
	}

	@Test
	public void addShopItemTestWithOnlySellPrice() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> adminshop.addShopItem(0, 1, 0, stack));
		assertDoesNotThrow(() -> verify(validationHandler).checkForItemDoesNotExist(eq("item string".hashCode()), anyList()));
		assertDoesNotThrow(() -> verify(validationHandler).checkForSlotIsEmpty(0, adminshop.getShopInventory(), 1));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("1.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForValidPrice("0.0"));
		assertDoesNotThrow(() -> verify(validationHandler).checkForPricesGreaterThenZero(1.0, 0.0));
		assertDoesNotThrow(() -> assertEquals(1, adminshop.getItemList().size()));
		ShopItem shopItem = assertDoesNotThrow(() -> adminshop.getShopItem(0));
		verify(shopDao).saveShopItem(shopItem, false);
		assertEquals(2, shopItem.getAmount());
		assertEquals(0.0, shopItem.getBuyPrice());
		assertEquals(1.0, shopItem.getSellPrice());
		assertEquals(0, shopItem.getSlot());
		assertEquals("item string".hashCode(), shopItem.getItemHash());
		assertEquals(stackCloneClone, shopItem.getItemStack());
		// verify that the set occupied method of the editor is called
		verify(skullService).getSkullWithName(SkullTextureEnum.SLOTFILLED, "Slot 1");
		verify(adminshop.getShopInventory()).setItem(0, stackClone);
		verify(stackClone).setAmount(2);
		verify(stackMetaClone).setLore(Arrays.asList("§62 sell for §a1.0 $"));
	}

	@Test
	public void addShopItemTestWithInvalidPrice() throws GeneralEconomyException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidPrice("-2.0");
		assertThrows(GeneralEconomyException.class, () -> adminshop.addShopItem(0, -2.0, 2.0, null));
		verify(shopDao, never()).saveShopItem(any(ShopItem.class), eq(false));
	}

	@Test
	public void addShopItemTestWithoutPrices() throws ShopSystemException {
		createNewAdminshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForPricesGreaterThenZero(0.0, 0.0);
		assertThrows(ShopSystemException.class, () -> adminshop.addShopItem(0, 0.0, 0.0, null));
		verify(shopDao, never()).saveShopItem(any(ShopItem.class), eq(false));
	}

	@Test
	public void addShopItemTestWithOccupiedSlot() throws GeneralEconomyException, EconomyPlayerException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForSlotIsEmpty(0, adminshop.getShopInventory(),
				1);
		assertThrows(GeneralEconomyException.class, () -> adminshop.addShopItem(0, 0.0, 0.0, null));
		verify(shopDao, never()).saveShopItem(any(ShopItem.class), eq(false));
	}

	@Test
	public void editShopItemTest() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta stackMetaCloneClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackCloneClone.getItemMeta()).thenReturn(stackMetaCloneClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> adminshop.addShopItem(0, 1, 4, stack));
		when(stackCloneClone.getType()).thenReturn(Material.STONE);

		String response = assertDoesNotThrow(() -> adminshop.editShopItem(0, "5", "15", "25"));

		assertEquals("§6Updated §aamount §asellPrice §abuyPrice §6for item §astone", response);

		verify(shopDao).saveShopItemSellPrice("item string".hashCode(), 15.0);
		verify(shopDao).saveShopItemBuyPrice("item string".hashCode(), 25.0);
		verify(shopDao).saveShopItemAmount("item string".hashCode(), 5);
		assertDoesNotThrow(() -> assertEquals(5, adminshop.getShopItem(0).getAmount()));
		assertDoesNotThrow(() -> assertEquals(15.0, adminshop.getShopItem(0).getSellPrice()));
		assertDoesNotThrow(() -> assertEquals(25.0, adminshop.getShopItem(0).getBuyPrice()));
		verify(adminshop.getShopInventory()).setItem(0, stackCloneClone);
		verify(stackCloneClone).setAmount(5);
		verify(stackMetaCloneClone).setLore(Arrays.asList("§65 buy for §a25.0 $", "§65 sell for §a15.0 $"));
	}

	@Test
	public void editShopItemTestWithOnlyAmount() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta stackMetaCloneClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackCloneClone.getItemMeta()).thenReturn(stackMetaCloneClone);
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> adminshop.addShopItem(0, 1, 4, stack));
		when(stackCloneClone.getType()).thenReturn(Material.STONE);

		String response = assertDoesNotThrow(() -> adminshop.editShopItem(0, "5", "none", "none"));

		assertEquals("§6Updated §aamount §6for item §astone", response);

		verify(shopDao).saveShopItemAmount("item string".hashCode(), 5);
		assertDoesNotThrow(() -> assertEquals(5, adminshop.getShopItem(0).getAmount()));
		assertDoesNotThrow(() -> assertEquals(1.0, adminshop.getShopItem(0).getSellPrice()));
		assertDoesNotThrow(() -> assertEquals(4.0, adminshop.getShopItem(0).getBuyPrice()));
		verify(adminshop.getShopInventory()).setItem(0, stackCloneClone);
		verify(stackCloneClone).setAmount(5);
		verify(stackMetaCloneClone).setLore(Arrays.asList("§65 buy for §a4.0 $", "§65 sell for §a1.0 $"));
	}

	@Test
	public void editShopItemTestWithOnlySellPrice() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta stackMetaCloneClone = mock(ItemMeta.class);
		when(stackCloneClone.getItemMeta()).thenReturn(stackMetaCloneClone);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> adminshop.addShopItem(0, 1, 4, stack));
		when(stackCloneClone.getType()).thenReturn(Material.STONE);

		String response = assertDoesNotThrow(() -> adminshop.editShopItem(0, "none", "15", "none"));

		assertEquals("§6Updated §asellPrice §6for item §astone", response);

		verify(shopDao).saveShopItemSellPrice("item string".hashCode(), 15.0);
		assertDoesNotThrow(() -> assertEquals(2, adminshop.getShopItem(0).getAmount()));
		assertDoesNotThrow(() -> assertEquals(15.0, adminshop.getShopItem(0).getSellPrice()));
		assertDoesNotThrow(() -> assertEquals(4.0, adminshop.getShopItem(0).getBuyPrice()));
		verify(adminshop.getShopInventory()).setItem(0, stackCloneClone);
		verify(stackCloneClone).setAmount(2);
		verify(stackMetaCloneClone).setLore(Arrays.asList("§62 buy for §a4.0 $", "§62 sell for §a15.0 $"));
	}

	@Test
	public void editShopItemTestWithOnlyBuyPrice() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaCloneClone = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackCloneClone.getItemMeta()).thenReturn(stackMetaCloneClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> adminshop.addShopItem(0, 1, 4, stack));
		when(stackCloneClone.getType()).thenReturn(Material.STONE);

		String response = assertDoesNotThrow(() -> adminshop.editShopItem(0, "none", "none", "25"));

		assertEquals("§6Updated §abuyPrice §6for item §astone", response);

		verify(shopDao).saveShopItemBuyPrice("item string".hashCode(), 25.0);
		assertDoesNotThrow(() -> assertEquals(2, adminshop.getShopItem(0).getAmount()));
		assertDoesNotThrow(() -> assertEquals(1.0, adminshop.getShopItem(0).getSellPrice()));
		assertDoesNotThrow(() -> assertEquals(25.0, adminshop.getShopItem(0).getBuyPrice()));
		verify(adminshop.getShopInventory()).setItem(0, stackCloneClone);
		verify(stackCloneClone).setAmount(2);
		verify(stackMetaCloneClone).setLore(Arrays.asList("§62 buy for §a25.0 $", "§62 sell for §a1.0 $"));
	}

	@Test
	public void editShopItemTestWithEmptySlot() throws GeneralEconomyException, ShopSystemException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForSlotIsNotEmpty(1,
				adminshop.getShopInventory(), 1);
		assertThrows(GeneralEconomyException.class, () -> adminshop.editShopItem(1, "100", "10.0", "25.0"));
	}

	@Test
	public void editShopItemTestWithInvalidBuyPrice() throws GeneralEconomyException {
		createNewAdminshop();
		doNothing().when(validationHandler).checkForValidPrice("10.0");
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidPrice("-25.0");
		assertThrows(GeneralEconomyException.class, () -> adminshop.editShopItem(1, "8", "10.0", "-25.0"));
	}

	@Test
	public void editShopItemTestWithZeroPrices() throws ShopSystemException {
		createNewAdminshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForOnePriceGreaterThenZeroIfBothAvailable("0",
				"0");
		assertThrows(ShopSystemException.class, () -> adminshop.editShopItem(1, "8", "0", "0"));
	}

	@Test
	public void editShopItemTestWithInvalidAmount() throws GeneralEconomyException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidAmount("100");
		assertThrows(GeneralEconomyException.class, () -> adminshop.editShopItem(1, "100", "10.0", "25.0"));
	}

	@Test
	public void editShopItemTestWithInvalidSellPrice() throws GeneralEconomyException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForValidPrice("-10.0");
		assertThrows(GeneralEconomyException.class, () -> adminshop.editShopItem(1, "8", "-10.0", "25.0"));
	}

	@Test
	public void removeItemTest() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(adminshop.getShopInventory().getItem(3)).thenReturn(stack);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 2, stack));
		reset(adminshop.getShopInventory());
		assertDoesNotThrow(() -> adminshop.removeShopItem(3));

		assertDoesNotThrow(() -> verify(validationHandler).checkForItemCanBeDeleted(3, 9));
		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(3, 8));
		assertDoesNotThrow(
				() -> verify(validationHandler, times(2)).checkForSlotIsNotEmpty(3, adminshop.getShopInventory(), 1));
		verify(adminshop.getShopInventory()).clear(3);
		verify(shopDao).saveShopItem(any(), eq(true));
		assertDoesNotThrow(() -> assertEquals(0, adminshop.getItemList().size()));
	}

	@Test
	public void removeItemTestWithEmptySlot() throws GeneralEconomyException, ShopSystemException {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 2, stack));
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForSlotIsNotEmpty(3,
				adminshop.getShopInventory(), 1);
		assertThrows(GeneralEconomyException.class, () -> adminshop.removeShopItem(3));
		assertEquals(1, adminshop.getItemList().size());
	}

	@Test
	public void removeItemTestWithInvalidSlot() throws GeneralEconomyException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSlot(-3, 8);
		assertThrows(GeneralEconomyException.class, () -> adminshop.removeShopItem(-3));
		verify(adminshop.getShopInventory(), never()).clear(anyInt());
	}

	@Test
	public void removeItemTestWithInvalidItem() throws ShopSystemException {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 2, stack));
		doThrow(ShopSystemException.class).when(validationHandler).checkForItemCanBeDeleted(8, 9);
		assertThrows(ShopSystemException.class, () -> adminshop.removeShopItem(8));
		assertEquals(1, adminshop.getItemList().size());
	}

	@Test
	public void moveShopTest() {
		createNewAdminshop();
		Location loc = mock(Location.class);

		assertDoesNotThrow(() -> adminshop.moveShop(loc));

		assertEquals(loc, adminshop.getShopLocation());
		verify(shopDao).saveShopLocation(loc);
		verify(adminshop.getShopVillager()).teleport(loc);
	}

	@Test
	public void changeProfessionTest() {
		createNewAdminshop();
		adminshop.changeProfession(Profession.FARMER);
		verify(adminshop.getShopVillager()).setProfession(Profession.FARMER);
		verify(shopDao).saveProfession(Profession.FARMER);
	}

	@Test
	public void changeShopNameTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editor = mock(Inventory.class);
		Inventory slotEditor = mock(Inventory.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(slotEditor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(skullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(infoItem);
		
		adminshop.setupNew("myshop", "A0", loc, 9);

		Inventory invNew = mock(Inventory.class);
		Inventory editorNew = mock(Inventory.class);
		Inventory slotEditorNew = mock(Inventory.class);

		when(serverProvider.createInventory(adminshop.getShopVillager(), 9, "newName")).thenReturn(invNew);
		when(serverProvider.createInventory(adminshop.getShopVillager(), 9, "newName-Editor")).thenReturn(editorNew);
		when(serverProvider.createInventory(adminshop.getShopVillager(), 27, "newName-SlotEditor"))
				.thenReturn(slotEditorNew);

		assertDoesNotThrow(() -> adminshop.changeShopName("newName"));

		assertEquals("newName", adminshop.getName());
		verify(shopDao).saveShopName("newName");
		verify(invNew).setContents(inv.getContents());
		verify(editorNew).setContents(editor.getContents());
		verify(slotEditorNew).setContents(slotEditor.getContents());

		verify(adminshop.getShopVillager()).setCustomName("newName");
	}

	@Test
	public void changeShopNameTestWithInvalidName() throws ShopSystemException {
		createNewAdminshop();
		doThrow(ShopSystemException.class).when(validationHandler).checkForValidShopName("my_name");
		assertThrows(ShopSystemException.class, () -> adminshop.changeShopName("my_name"));
		assertEquals("myshop", adminshop.getName());
		verify(shopDao, never()).saveShopName("my_shop");
	}

	@Test
	public void changeShopNameTestWithExistingName() throws GeneralEconomyException {
		createNewAdminshop();
		when(adminshopManager.getAdminshopNameList()).thenReturn(new ArrayList<>());
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValueNotInList(new ArrayList<>(),
				"newShop");
		assertThrows(GeneralEconomyException.class, () -> adminshop.changeShopName("newShop"));
		assertEquals("myshop", adminshop.getName());
		verify(shopDao, never()).saveShopName("newShop");
	}

	@Test
	public void changeShopSizeTest() {
		createNewAdminshop();
		
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaCloneClone = mock(ItemMeta.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(2);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackCloneClone.getItemMeta()).thenReturn(stackMetaCloneClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		when(configManager.getCurrencyText(anyDouble())).thenReturn("$");
		assertDoesNotThrow(() -> adminshop.addShopItem(0, 1, 4, stack));
		
		ShopItem shopItem = assertDoesNotThrow(() -> adminshop.getShopItem(0));
		when(shopDao.loadItem("item string".hashCode())).thenReturn(shopItem);
		
		Inventory oldInv = adminshop.getShopInventory();
		Inventory inv = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		ItemStack empty = mock(ItemStack.class);
		ItemStack filled = mock(ItemStack.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta infoItemMeta = mock(ItemMeta.class);
		when(inv.getItem(0)).thenReturn(stackClone);
		when(infoItemMeta.getDisplayName()).thenReturn("Info");
		when(infoItem.getItemMeta()).thenReturn(infoItemMeta);
		when(serverProvider.createItemStack(Material.ANVIL, 1)).thenReturn(infoItem);
		when(skullService.getSkullWithName(eq(SkullTextureEnum.SLOTFILLED), anyString())).thenReturn(filled);
		when(skullService.getSkullWithName(eq(SkullTextureEnum.SLOTEMPTY), anyString())).thenReturn(empty);
		when(serverProvider.createInventory(adminshop.getShopVillager(), 18, "myshop-Editor")).thenReturn(editorStuff);
		when(serverProvider.createInventory(adminshop.getShopVillager(), 18, "myshop")).thenReturn(inv);
		assertDoesNotThrow(() -> adminshop.changeShopSize(18));
		verify(shopDao).saveShopSize(18);
		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSize(18));
		assertDoesNotThrow(() -> verify(validationHandler).checkForResizePossible(oldInv, 9, 18, 1));
		assertEquals(18, adminshop.getSize());
		
		verify(editorStuff, times(16)).setItem(anyInt(), eq(empty));
		verify(editorStuff).setItem(0, filled);

		verify(serverProvider).createInventory(adminshop.getShopVillager(), 18, "myshop");
		verify(serverProvider).createInventory(adminshop.getShopVillager(), 18, "myshop-Editor");

		verify(inv).setItem(0, stackCloneClone);
		
		verify(infoItemMeta).setDisplayName("Info");
		verify(infoItem, times(2)).setItemMeta(infoItemMeta);
		verify(infoItemMeta).setLore(Arrays.asList("§6Rightclick: §asell specified amount",
				"§6Shift-Rightclick: §asell all", "§6Leftclick: §abuy"));
		verify(inv).setItem(17, infoItem);
		verify(inv, times(2)).setItem(anyInt(), any(ItemStack.class));
	}

	@Test
	public void changeShopSizeTestWithInvalidSize() throws GeneralEconomyException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSize(18);
		assertThrows(GeneralEconomyException.class, () -> adminshop.changeShopSize(18));
		assertEquals(9, adminshop.getSize());
	}

	@Test
	public void changeShopSizeTestWithOccupiedSlots() throws ShopSystemException, GeneralEconomyException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForResizePossible(adminshop.getShopInventory(),
				9, 18, 1);
		assertThrows(GeneralEconomyException.class, () -> adminshop.changeShopSize(18));
		assertEquals(9, adminshop.getSize());
	}

	@Test
	public void despawnVillagerTest() {
		createNewAdminshop();
		adminshop.despawnVillager();
		verify(adminshop.getShopVillager()).remove();
	}

	@Test
	public void getShopItemTest() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 2, stack));

		ShopItem shopItem = assertDoesNotThrow(() -> adminshop.getShopItem(3));

		assertEquals(stackCloneClone, shopItem.getItemStack());
	}

	@Test
	public void getShopTestWithEmptySlot() throws GeneralEconomyException, ShopSystemException {
		createNewAdminshop();
		doThrow(GeneralEconomyException.class).when(validationHandler).checkForSlotIsNotEmpty(3,
				adminshop.getShopInventory(), 1);
		assertThrows(GeneralEconomyException.class, () -> adminshop.getShopItem(3));
	}

	@Test
	public void getShopTestWithStack() {
		createNewAdminshop();
		ItemStack stack = mock(ItemStack.class);
		ItemStack stackClone = mock(ItemStack.class);
		ItemStack stackCloneClone = mock(ItemStack.class);
		ItemStack searchStack = mock(ItemStack.class);
		ItemStack searchStackClone = mock(ItemStack.class);
		ItemMeta stackMetaClone = mock(ItemMeta.class);
		ItemMeta searchStackCloneMeta = mock(ItemMeta.class);
		when(searchStackClone.toString()).thenReturn("item string");
		when(searchStackCloneMeta.hasLore()).thenReturn(true);
		ArrayList<String> lore = new ArrayList<>();
		lore.add("some lore");
		lore.add("2 buy for 10");
		lore.add("2 sell for 5");
		lore.add(ChatColor.GREEN + "2" + ChatColor.GOLD + "Items");
		when(searchStackCloneMeta.getLore()).thenReturn(lore);
		when(searchStackClone.getItemMeta()).thenReturn(searchStackCloneMeta);
		when(searchStack.clone()).thenReturn(searchStackClone);
		when(stack.getAmount()).thenReturn(1);
		when(stack.toString()).thenReturn("item string");
		when(stackClone.getItemMeta()).thenReturn(stackMetaClone);
		when(stack.clone()).thenReturn(stackClone);
		when(stackClone.clone()).thenReturn(stackCloneClone);
		assertDoesNotThrow(() -> adminshop.addShopItem(3, 1, 2, stack));

		ShopItem shopItem = assertDoesNotThrow(() -> adminshop.getShopItem(searchStack));

		assertEquals(stackCloneClone, shopItem.getItemStack());
	}

	@Test
	public void getShopTestWithStackEmptySlot() {
		createNewAdminshop();
		ItemStack searchStack = mock(ItemStack.class);
		ItemStack searchStackClone = mock(ItemStack.class);
		ItemMeta searchStackCloneMeta = mock(ItemMeta.class);
		when(searchStackCloneMeta.hasLore()).thenReturn(true);
		when(searchStackCloneMeta.getLore()).thenReturn(Arrays.asList("some lore"));
		when(searchStackClone.getItemMeta()).thenReturn(searchStackCloneMeta);
		when(searchStack.clone()).thenReturn(searchStackClone);

		try {
			adminshop.getShopItem(searchStack);
		} catch (ShopSystemException e) {
			assertEquals(ShopExceptionMessageEnum.ITEM_DOES_NOT_EXIST, e.getKey());
			assertEquals(0, e.getParams().length);
		}
	}

	@Test
	public void openSlotEditorTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editor = mock(Inventory.class);
		Inventory slotEditor = mock(Inventory.class);
		Player player = mock(Player.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(slotEditor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(skullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(infoItem);
		adminshop.setupNew("myshop", "A0", loc, 9);
		assertDoesNotThrow(() -> when(validationHandler.isSlotEmpty(0, adminshop.getShopInventory(), 1)).thenReturn(true));

		assertDoesNotThrow(() -> adminshop.openSlotEditor(player, 0));

		assertDoesNotThrow(() -> verify(generalValidator).checkForValidSlot(0, 8));
		verify(player).openInventory(slotEditor);
		// verify that the selected slot method is executed
		assertDoesNotThrow(() -> verify(validationHandler, times(2)).isSlotEmpty(0, adminshop.getShopInventory(), 1));
	}

	@Test
	public void openSlotEditorTestWithInvalidSlot() throws GeneralEconomyException {
		createNewAdminshop();
		Player player = mock(Player.class);
		doThrow(GeneralEconomyException.class).when(generalValidator).checkForValidSlot(0, 8);
		assertThrows(GeneralEconomyException.class, () -> adminshop.openSlotEditor(player, 0));
		verify(player, never()).openInventory(any(Inventory.class));
	}

	@Test
	public void openShopInventoryTest() {
		createNewAdminshop();
		Player player = mock(Player.class);
		assertDoesNotThrow(() -> adminshop.openShopInventory(player));
		verify(player).openInventory(adminshop.getShopInventory());
	}

	@Test
	public void openEditorInventoryTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editor = mock(Inventory.class);
		Inventory slotEditor = mock(Inventory.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(slotEditor);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(skullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(infoItem);
		adminshop.setupNew("myshop", "A0", loc, 9);
		Player player = mock(Player.class);

		assertDoesNotThrow(() -> adminshop.openEditor(player));

		verify(player).openInventory(editor);
	}

	@Test
	public void getSizeTest() {
		createNewAdminshop();
		assertEquals(9, adminshop.getSize());
	}

	@Test
	public void getShopIdTest() {
		createNewAdminshop();
		assertEquals("A0", adminshop.getShopId());
	}

	@Test
	public void getShopLocationTest() {
		JavaPlugin plugin = mock(JavaPlugin.class);
		Location loc = mock(Location.class);
		World world = mock(World.class);
		Villager villager = mock(Villager.class);
		Chunk chunk = mock(Chunk.class);
		ItemStack infoItem = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		Inventory inv = mock(Inventory.class);
		Inventory editorStuff = mock(Inventory.class);
		when(meta.getDisplayName()).thenReturn("Info");
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-Editor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop-SlotEditor"))).thenReturn(editorStuff);
		when(serverProvider.createInventory(eq(villager), anyInt(), eq("myshop"))).thenReturn(inv);
		when(serverProvider.createItemStack(any(), eq(1))).thenReturn(infoItem);
		when(loc.getWorld()).thenReturn(world);
		when(serverProvider.getJavaPluginInstance()).thenReturn(plugin);
		when(infoItem.getItemMeta()).thenReturn(meta);
		when(world.spawnEntity(loc, EntityType.VILLAGER)).thenReturn(villager);
		when(loc.getChunk()).thenReturn(chunk);
		when(skullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(infoItem);
		adminshop.setupNew("myshop", "A0", loc, 9);

		assertEquals(loc, adminshop.getShopLocation());
	}
}
