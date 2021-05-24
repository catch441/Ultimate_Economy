package org.ue.common.logic.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.InventoryGuiHandler;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.economyplayer.logic.api.EconomyPlayer;

@ExtendWith(MockitoExtension.class)
public class InventoryGuiHandlerTest {

	@Mock
	ServerProvider serverProvider;
	@Mock
	CustomSkullService skullService;
	@Mock
	Inventory backLink;
	@Mock
	Inventory inventory;

	private static class AbstractInventoryGui extends InventoryGuiHandlerImpl {
		public AbstractInventoryGui(CustomSkullService skullService, ServerProvider serverProvider, Inventory backLink,
				Inventory inventory) {
			super(skullService, serverProvider, backLink);
			this.inventory = inventory;
		}

		@Override
		public void handleInventoryClick(ClickType clickType, int rawSlot, EconomyPlayer whoClicked) {
		}
	}

	private AbstractInventoryGui createInventoryGui() {
		return new AbstractInventoryGui(skullService, serverProvider, backLink, inventory);
	}
	
	@Test
	public void updateBackLinkTest() {
		Inventory inv = mock(Inventory.class);
		Player player = mock(Player.class);
		AbstractInventoryGui handler = createInventoryGui();
		handler.updateBackLink(inv);
		handler.returnToBackLink(player);
		verify(player).closeInventory();
		verify(player).openInventory(inv);
	}

	@Test
	public void openInventoryTest() {
		Player player = mock(Player.class);
		InventoryGuiHandler handler = createInventoryGui();
		handler.openInventory(player);
		verify(player).openInventory(inventory);
	}

	@Test
	public void returnToBackLinkTest() {
		Player player = mock(Player.class);
		AbstractInventoryGui handler = createInventoryGui();
		handler.returnToBackLink(player);
		verify(player).closeInventory();
		verify(player).openInventory(backLink);
	}

	@Test
	public void returnToBackLinkTestWithNullBackLink() {
		Player player = mock(Player.class);
		AbstractInventoryGui handler = new AbstractInventoryGui(skullService, serverProvider, null, inventory);
		handler.returnToBackLink(player);
		verify(player).closeInventory();
	}

	@Test
	public void getInventoryTest() {
		AbstractInventoryGui handler = createInventoryGui();
		assertEquals(inventory, handler.getInventory());
	}

	@Test
	public void setItemTest() {
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		AbstractInventoryGui handler = createInventoryGui();
		List<String> lore = Arrays.asList("lore1", "lore2");
		handler.setItem(Material.STONE, lore, "customName", 4);
		verify(meta).setLore(lore);
		verify(meta).setDisplayName("customName");
		verify(meta).addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		verify(stack).setItemMeta(meta);
		verify(inventory).setItem(4, stack);
	}

	@Test
	public void setSkullTest() {
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(skullService.getSkullWithName(SkullTextureEnum.ONE, "customName")).thenReturn(stack);
		List<String> lore = Arrays.asList("lore1", "lore2");
		AbstractInventoryGui handler = createInventoryGui();
		handler.setSkull(SkullTextureEnum.ONE, lore, "customName", 4);
		verify(meta).setLore(lore);
		verify(stack).setItemMeta(meta);
		verify(inventory).setItem(4, stack);
	}

	@Test
	public void setSkullTestWithNullLore() {
		ItemStack stack = mock(ItemStack.class);
		when(skullService.getSkullWithName(SkullTextureEnum.ONE, "customName")).thenReturn(stack);
		AbstractInventoryGui handler = createInventoryGui();
		handler.setSkull(SkullTextureEnum.ONE, null, "customName", 4);
		verify(inventory).setItem(4, stack);
	}

	@Test
	public void setItemTestWithNullLore() {
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(serverProvider.createItemStack(Material.STONE, 1)).thenReturn(stack);
		AbstractInventoryGui handler = createInventoryGui();
		handler.setItem(Material.STONE, null, "customName", 4);
		verify(meta).setLore(Arrays.asList());
		verify(meta).setDisplayName("customName");
		verify(meta).addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		verify(stack).setItemMeta(meta);
		verify(inventory).setItem(4, stack);
	}

	@Test
	public void updateItemLoreTest() {
		ItemStack stack = mock(ItemStack.class);
		ItemMeta meta = mock(ItemMeta.class);
		when(stack.getItemMeta()).thenReturn(meta);
		when(inventory.getItem(2)).thenReturn(stack);
		List<String> lore = Arrays.asList("lore1", "lore2");
		AbstractInventoryGui handler = createInventoryGui();
		handler.updateItemLore(2, lore);
		verify(meta).setLore(lore);
		verify(stack).setItemMeta(meta);
	}
}
