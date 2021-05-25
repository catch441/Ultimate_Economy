package org.ue.economyvillager.logic.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.ExceptionMessageEnum;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.economyvillager.logic.api.EconomyVillager;

@ExtendWith(MockitoExtension.class)
public class EconomyVillagerCustomizeHandlerTest {

	@Mock
	MessageWrapper messageWrapper;
	@Mock
	ServerProvider serverProvider;
	@Mock
	CustomSkullService customSkullService;
	@Mock
	EconomyVillager<AbstractException> villager;

	private class AbstractCustomizer extends EconomyVillagerCustomizeHandlerImpl<AbstractException> {

		public AbstractCustomizer(MessageWrapper messageWrapper, ServerProvider serverProvider,
				CustomSkullService skullService, EconomyVillager<AbstractException> ecoVillager, Type biomeType,
				Profession profession) {
			super(messageWrapper, serverProvider, skullService, ecoVillager, biomeType, profession);
		}

	}
	
	private static class AbstractException extends GeneralEconomyException {
		private static final long serialVersionUID = 1L;

		public AbstractException(MessageWrapper messageWrapper, ExceptionMessageEnum key, Object[] params) {
			super(messageWrapper, key, params);
		}
	}

	private InventoryGuiHandler createHandler(Type biomeType, Profession profession) {
		return new AbstractCustomizer(messageWrapper, serverProvider, customSkullService, villager, biomeType,
				profession);
	}
	
	private InventoryGuiHandler createHandlerWithSetupMocks(Inventory inventory, Type biomeType, Profession profession) {
		Inventory inv = mock(Inventory.class);
		if(inventory != null) {
			inv = inventory;
		}
		ItemStack item = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(villager.createVillagerInventory(54, "Customize Villager")).thenReturn(inv);
		when(item.getItemMeta()).thenReturn(meta);
		when(customSkullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(item);
		when(serverProvider.createItemStack(any(Material.class), anyInt())).thenReturn(item);
		return new AbstractCustomizer(messageWrapper, serverProvider, customSkullService, villager, biomeType,
				profession);
	}

	@Test
	public void constructorTest() {
		Inventory inv = mock(Inventory.class);
		ItemStack one = mock(ItemStack.class);
		ItemStack two = mock(ItemStack.class);
		ItemStack three = mock(ItemStack.class);
		ItemStack four = mock(ItemStack.class);
		ItemStack five = mock(ItemStack.class);
		ItemStack six = mock(ItemStack.class);
		
		ItemStack save = mock(ItemStack.class);
		ItemStack exit = mock(ItemStack.class);
		ItemMeta saveMeta = mock(ItemMeta.class);
		ItemMeta exitMeta = mock(ItemMeta.class);
		
		ItemStack desert = mock(ItemStack.class);
		ItemStack jungle = mock(ItemStack.class);
		ItemStack plains = mock(ItemStack.class);
		ItemStack savanna = mock(ItemStack.class);
		ItemStack snow = mock(ItemStack.class);
		ItemStack swamp = mock(ItemStack.class);
		ItemStack taiga = mock(ItemStack.class);
		ItemMeta desertMeta = mock(ItemMeta.class);
		ItemMeta jungleMeta = mock(ItemMeta.class);
		ItemMeta plainsMeta = mock(ItemMeta.class);
		ItemMeta savannaMeta = mock(ItemMeta.class);
		ItemMeta snowMeta = mock(ItemMeta.class);
		ItemMeta swampMeta = mock(ItemMeta.class);
		ItemMeta taigaMeta = mock(ItemMeta.class);
		
		ItemStack nitwit = mock(ItemStack.class);
		ItemStack fletcher = mock(ItemStack.class);
		ItemStack leatherworker = mock(ItemStack.class);
		ItemStack mason = mock(ItemStack.class);
		ItemStack cleric = mock(ItemStack.class);
		ItemStack sheperd = mock(ItemStack.class);
		ItemStack librarian = mock(ItemStack.class);
		ItemStack cartographer = mock(ItemStack.class);
		ItemStack butcher = mock(ItemStack.class);
		ItemStack armorer = mock(ItemStack.class);
		ItemStack farmer = mock(ItemStack.class);
		ItemStack toolsmith = mock(ItemStack.class);
		ItemStack weaponsmith = mock(ItemStack.class);
		ItemStack fisherman = mock(ItemStack.class);
		ItemMeta nitwitMeta = mock(ItemMeta.class);
		ItemMeta fletcherMeta = mock(ItemMeta.class);
		ItemMeta leatherworkerMeta = mock(ItemMeta.class);
		ItemMeta masonMeta = mock(ItemMeta.class);
		ItemMeta clericMeta = mock(ItemMeta.class);
		ItemMeta sheperdMeta = mock(ItemMeta.class);
		ItemMeta librarianMeta = mock(ItemMeta.class);
		ItemMeta cartographerMeta = mock(ItemMeta.class);
		ItemMeta butcherMeta = mock(ItemMeta.class);
		ItemMeta armorerMeta = mock(ItemMeta.class);
		ItemMeta farmerMeta = mock(ItemMeta.class);
		ItemMeta toolsmithMeta = mock(ItemMeta.class);
		ItemMeta weaponsmithMeta = mock(ItemMeta.class);
		ItemMeta fishermanMeta = mock(ItemMeta.class);
		
		when(serverProvider.createItemStack(Material.GREEN_TERRACOTTA, 1)).thenReturn(nitwit);
		when(serverProvider.createItemStack(Material.ARROW, 1)).thenReturn(fletcher);
		when(serverProvider.createItemStack(Material.LEATHER, 1)).thenReturn(leatherworker);
		when(serverProvider.createItemStack(Material.STONECUTTER, 1)).thenReturn(mason);
		when(serverProvider.createItemStack(Material.EXPERIENCE_BOTTLE, 1)).thenReturn(cleric);
		when(serverProvider.createItemStack(Material.WHITE_WOOL, 1)).thenReturn(sheperd);
		when(serverProvider.createItemStack(Material.BOOK, 1)).thenReturn(librarian);
		when(serverProvider.createItemStack(Material.FILLED_MAP, 1)).thenReturn(cartographer);
		when(serverProvider.createItemStack(Material.PORKCHOP, 1)).thenReturn(butcher);
		when(serverProvider.createItemStack(Material.DIAMOND_CHESTPLATE, 1)).thenReturn(armorer);
		when(serverProvider.createItemStack(Material.WHEAT, 1)).thenReturn(farmer);
		when(serverProvider.createItemStack(Material.DIAMOND_AXE, 1)).thenReturn(toolsmith);
		when(serverProvider.createItemStack(Material.DIAMOND_SWORD, 1)).thenReturn(weaponsmith);
		when(serverProvider.createItemStack(Material.FISHING_ROD, 1)).thenReturn(fisherman);
		when(nitwit.getItemMeta()).thenReturn(nitwitMeta);
		when(fletcher.getItemMeta()).thenReturn(fletcherMeta);
		when(leatherworker.getItemMeta()).thenReturn(leatherworkerMeta);
		when(mason.getItemMeta()).thenReturn(masonMeta);
		when(cleric.getItemMeta()).thenReturn(clericMeta);
		when(sheperd.getItemMeta()).thenReturn(sheperdMeta);
		when(librarian.getItemMeta()).thenReturn(librarianMeta);
		when(cartographer.getItemMeta()).thenReturn(cartographerMeta);
		when(butcher.getItemMeta()).thenReturn(butcherMeta);
		when(armorer.getItemMeta()).thenReturn(armorerMeta);
		when(farmer.getItemMeta()).thenReturn(farmerMeta);
		when(toolsmith.getItemMeta()).thenReturn(toolsmithMeta);
		when(weaponsmith.getItemMeta()).thenReturn(weaponsmithMeta);
		when(fisherman.getItemMeta()).thenReturn(fishermanMeta);
		
		when(villager.createVillagerInventory(54, "Customize Villager")).thenReturn(inv);
		when(customSkullService.getSkullWithName(SkullTextureEnum.ONE, ChatColor.YELLOW + "9")).thenReturn(one);
		when(customSkullService.getSkullWithName(SkullTextureEnum.TWO, ChatColor.YELLOW + "18")).thenReturn(two);
		when(customSkullService.getSkullWithName(SkullTextureEnum.THREE_GOLD, ChatColor.YELLOW + "27")).thenReturn(three);
		when(customSkullService.getSkullWithName(SkullTextureEnum.FOUR, ChatColor.YELLOW + "36")).thenReturn(four);
		when(customSkullService.getSkullWithName(SkullTextureEnum.FIVE, ChatColor.YELLOW + "45")).thenReturn(five);
		when(customSkullService.getSkullWithName(SkullTextureEnum.SIX, ChatColor.YELLOW + "54")).thenReturn(six);
		
		when(serverProvider.createItemStack(Material.GREEN_WOOL, 1)).thenReturn(save);
		when(serverProvider.createItemStack(Material.RED_WOOL, 1)).thenReturn(exit);
		when(save.getItemMeta()).thenReturn(saveMeta);
		when(exit.getItemMeta()).thenReturn(exitMeta);
		
		when(serverProvider.createItemStack(Material.SAND, 1)).thenReturn(desert);
		when(serverProvider.createItemStack(Material.JUNGLE_LOG, 1)).thenReturn(jungle);
		when(serverProvider.createItemStack(Material.GRASS, 1)).thenReturn(plains);
		when(serverProvider.createItemStack(Material.ACACIA_LOG, 1)).thenReturn(savanna);
		when(serverProvider.createItemStack(Material.SNOW_BLOCK, 1)).thenReturn(snow);
		when(serverProvider.createItemStack(Material.LILY_PAD, 1)).thenReturn(swamp);
		when(serverProvider.createItemStack(Material.SPRUCE_LOG, 1)).thenReturn(taiga);
		when(desert.getItemMeta()).thenReturn(desertMeta);
		when(jungle.getItemMeta()).thenReturn(jungleMeta);
		when(plains.getItemMeta()).thenReturn(plainsMeta);
		when(savanna.getItemMeta()).thenReturn(savannaMeta);
		when(snow.getItemMeta()).thenReturn(snowMeta);
		when(swamp.getItemMeta()).thenReturn(swampMeta);
		when(taiga.getItemMeta()).thenReturn(taigaMeta);
		
		when(villager.getSize()).thenReturn(27);
		
		createHandler(Type.DESERT, Profession.FARMER);
		
		verify(inv).setItem(0, one);
		verify(inv).setItem(1, two);
		verify(inv).setItem(2, three);
		verify(inv).setItem(3, four);
		verify(inv).setItem(4, five);
		verify(inv).setItem(5, six);
		verify(inv).setItem(7, exit);
		verify(inv).setItem(8, save);
		verify(saveMeta).setDisplayName(ChatColor.YELLOW + "save changes");
		verify(exitMeta).setDisplayName(ChatColor.RED + "exit without save");
		verify(save).setItemMeta(saveMeta);
		verify(exit).setItemMeta(exitMeta);
		
		verify(inv).setItem(36, farmer);
		verify(inv).setItem(38, nitwit);
		verify(inv).setItem(39, fletcher);
		verify(inv).setItem(40, leatherworker);
		verify(inv).setItem(41, mason);
		verify(inv).setItem(42, cleric);
		verify(inv).setItem(43, sheperd);
		verify(inv).setItem(44, librarian);
		verify(inv).setItem(47, cartographer);
		verify(inv).setItem(48, butcher);
		verify(inv).setItem(49, armorer);
		verify(inv).setItem(50, farmer);
		verify(inv).setItem(51, toolsmith);
		verify(inv).setItem(52, weaponsmith);
		verify(inv).setItem(53, fisherman);
		verify(nitwitMeta).setDisplayName(ChatColor.YELLOW + Profession.NITWIT.toString().toLowerCase());
		verify(fletcherMeta).setDisplayName(ChatColor.YELLOW + Profession.FLETCHER.toString().toLowerCase());
		verify(leatherworkerMeta).setDisplayName(ChatColor.YELLOW + Profession.LEATHERWORKER.toString().toLowerCase());
		verify(masonMeta).setDisplayName(ChatColor.YELLOW + Profession.MASON.toString().toLowerCase());
		verify(clericMeta).setDisplayName(ChatColor.YELLOW + Profession.CLERIC.toString().toLowerCase());
		verify(sheperdMeta).setDisplayName(ChatColor.YELLOW + Profession.SHEPHERD.toString().toLowerCase());
		verify(librarianMeta).setDisplayName(ChatColor.YELLOW + Profession.LIBRARIAN.toString().toLowerCase());
		verify(cartographerMeta).setDisplayName(ChatColor.YELLOW + Profession.CARTOGRAPHER.toString().toLowerCase());
		verify(butcherMeta).setDisplayName(ChatColor.YELLOW + Profession.BUTCHER.toString().toLowerCase());
		verify(armorerMeta).setDisplayName(ChatColor.YELLOW + Profession.ARMORER.toString().toLowerCase());
		verify(farmerMeta).setDisplayName(ChatColor.YELLOW + Profession.FARMER.toString().toLowerCase());
		verify(toolsmithMeta).setDisplayName(ChatColor.YELLOW + Profession.TOOLSMITH.toString().toLowerCase());
		verify(weaponsmithMeta).setDisplayName(ChatColor.YELLOW + Profession.WEAPONSMITH.toString().toLowerCase());
		verify(fishermanMeta).setDisplayName(ChatColor.YELLOW + Profession.FISHERMAN.toString().toLowerCase());
		verify(nitwit).setItemMeta(nitwitMeta);
		verify(fletcher).setItemMeta(fletcherMeta);
		verify(leatherworker).setItemMeta(leatherworkerMeta);
		verify(mason).setItemMeta(masonMeta);
		verify(cleric).setItemMeta(clericMeta);
		verify(sheperd).setItemMeta(sheperdMeta);
		verify(librarian).setItemMeta(librarianMeta);
		verify(cartographer).setItemMeta(cartographerMeta);
		verify(butcher).setItemMeta(butcherMeta);
		verify(armorer).setItemMeta(armorerMeta);
		verify(farmer, times(2)).setItemMeta(farmerMeta);
		verify(toolsmith).setItemMeta(toolsmithMeta);
		verify(weaponsmith).setItemMeta(weaponsmithMeta);
		verify(fisherman).setItemMeta(fishermanMeta);
		
		verify(inv).setItem(18, desert);
		verify(inv).setItem(20, desert);
		verify(inv).setItem(21, jungle);
		verify(inv).setItem(22, plains);
		verify(inv).setItem(23, savanna);
		verify(inv).setItem(24, snow);
		verify(inv).setItem(25, swamp);
		verify(inv).setItem(26, taiga);
		verify(desert, times(2)).setItemMeta(desertMeta);
		verify(jungle).setItemMeta(jungleMeta);
		verify(plains).setItemMeta(plainsMeta);
		verify(savanna).setItemMeta(savannaMeta);
		verify(snow).setItemMeta(snowMeta);
		verify(swamp).setItemMeta(swampMeta);
		verify(taiga).setItemMeta(taigaMeta);
		verify(desertMeta).setDisplayName(ChatColor.YELLOW + Type.DESERT.toString().toLowerCase());
		verify(jungleMeta).setDisplayName(ChatColor.YELLOW + Type.JUNGLE.toString().toLowerCase());
		verify(plainsMeta).setDisplayName(ChatColor.YELLOW + Type.PLAINS.toString().toLowerCase());
		verify(savannaMeta).setDisplayName(ChatColor.YELLOW + Type.SAVANNA.toString().toLowerCase());
		verify(snowMeta).setDisplayName(ChatColor.YELLOW + Type.SNOW.toString().toLowerCase());
		verify(swampMeta).setDisplayName(ChatColor.YELLOW + Type.SWAMP.toString().toLowerCase());
		verify(taigaMeta).setDisplayName(ChatColor.YELLOW + Type.TAIGA.toString().toLowerCase());
	}
	
	@Test
	public void openInventoryTest() {
		Player player = mock(Player.class);
		Inventory inv = mock(Inventory.class);
		ItemStack item = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(villager.createVillagerInventory(54, "Customize Villager")).thenReturn(inv);
		when(item.getItemMeta()).thenReturn(meta);
		when(customSkullService.getSkullWithName(any(SkullTextureEnum.class), anyString())).thenReturn(item);
		when(serverProvider.createItemStack(any(Material.class), anyInt())).thenReturn(item);
		InventoryGuiHandler handler = createHandler(null, null);
		handler.openInventory(player);
		verify(player).openInventory(inv);
	}
	
	@Test
	public void handleInventoryClickTestWithExit() {
		Player player = mock(Player.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		when(whoClicked.getPlayer()).thenReturn(player);
		InventoryGuiHandler handler = createHandlerWithSetupMocks(null, null, null);
		handler.handleInventoryClick(null, 7, whoClicked);
		verify(player).closeInventory();
	}
	
	@Test
	public void handleInventoryClickTestWithNoChangesSave() {
		Player player = mock(Player.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		when(whoClicked.getPlayer()).thenReturn(player);
		when(villager.getSize()).thenReturn(18);
		when(messageWrapper.getString(MessageEnum.CONFIG_CHANGE, "18 desert farmer")).thenReturn("message");
		InventoryGuiHandler handler = createHandlerWithSetupMocks(null, Type.DESERT, Profession.FARMER);
		handler.handleInventoryClick(null, 8, whoClicked);
		verify(player).closeInventory();
		assertDoesNotThrow(() -> verify(villager).changeSize(18));
		verify(villager).changeProfession(Profession.FARMER);
		verify(villager).changeBiomeType(Type.DESERT);
		verify(player).sendMessage("message");
	}
	
	@Test
	public void handleInventoryClickTestWithErrorOnSave() throws AbstractException {
		Player player = mock(Player.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		when(whoClicked.getPlayer()).thenReturn(player);
		when(villager.getSize()).thenReturn(18);
		AbstractException e = mock(AbstractException.class);
		doThrow(e).when(villager).changeSize(18);
		when(e.getMessage()).thenReturn("message");
		InventoryGuiHandler handler = createHandlerWithSetupMocks(null, Type.DESERT, Profession.FARMER);
		handler.handleInventoryClick(null, 8, whoClicked);
		verify(player).closeInventory();
		assertDoesNotThrow(() -> verify(villager).changeSize(18));
		verify(player).sendMessage("message");
	}
	
	@Test
	public void handleInventoryClickTestWithSetBiomeType() {
		Inventory inv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		ItemStack item = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(whoClicked.getPlayer()).thenReturn(player);
		when(item.getItemMeta()).thenReturn(meta);
		InventoryGuiHandler handler = createHandlerWithSetupMocks(inv, Type.DESERT, Profession.FARMER);
		reset(serverProvider);
		when(serverProvider.createItemStack(Material.SPRUCE_LOG, 1)).thenReturn(item);
		handler.handleInventoryClick(null, 26, whoClicked);
		// save
		handler.handleInventoryClick(null, 8, whoClicked);
		verify(villager).changeBiomeType(Type.TAIGA);
		verify(inv).setItem(18, item);
		verify(meta).setDisplayName("§6taiga");
		verify(item).setItemMeta(meta);
	}
	
	@Test
	public void handleInventoryClickTestWithSetProfession() {
		Inventory inv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		ItemStack item = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(whoClicked.getPlayer()).thenReturn(player);
		when(item.getItemMeta()).thenReturn(meta);
		InventoryGuiHandler handler = createHandlerWithSetupMocks(inv, Type.DESERT, Profession.FARMER);
		reset(serverProvider);
		when(serverProvider.createItemStack(Material.FISHING_ROD, 1)).thenReturn(item);
		handler.handleInventoryClick(null, 53, whoClicked);
		// save
		handler.handleInventoryClick(null, 8, whoClicked);
		verify(villager).changeProfession(Profession.FISHERMAN);
		verify(inv).setItem(36, item);
		verify(meta).setDisplayName("§6fisherman");
		verify(item).setItemMeta(meta);
	}
	
	@Test
	public void handleInventoryClickTestWithSetSize() {
		Inventory inv = mock(Inventory.class);
		Player player = mock(Player.class);
		EconomyPlayer whoClicked = mock(EconomyPlayer.class);
		ItemStack itemOld = mock(ItemStack.class);
		ItemStack itemNew = mock(ItemStack.class);
		when(villager.getSize()).thenReturn(27);
		when(whoClicked.getPlayer()).thenReturn(player);
		InventoryGuiHandler handler = createHandlerWithSetupMocks(inv, Type.DESERT, Profession.FARMER);
		reset(customSkullService);
		when(customSkullService.getSkullWithName(SkullTextureEnum.THREE, ChatColor.YELLOW + "27")).thenReturn(itemOld);
		when(customSkullService.getSkullWithName(SkullTextureEnum.ONE_GOLD, ChatColor.YELLOW + "9")).thenReturn(itemNew);
		handler.handleInventoryClick(null, 0, whoClicked);
		// save
		handler.handleInventoryClick(null, 8, whoClicked);
		assertDoesNotThrow(() -> verify(villager).changeSize(9));
		verify(inv).setItem(2, itemOld);
		verify(inv).setItem(0, itemNew);
	}
}
