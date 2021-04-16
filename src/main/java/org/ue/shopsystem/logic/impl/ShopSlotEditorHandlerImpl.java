package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.EconomyPlayerException;
import org.ue.general.GeneralEconomyException;
import org.ue.shopsystem.logic.ShopSystemException;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import org.ue.shopsystem.logic.api.ShopValidationHandler;

public class ShopSlotEditorHandlerImpl implements ShopSlotEditorHandler {

	private Inventory slotEditorInv;
	private int selectedEditorSlot;
	private AbstractShop shop;
	private final CustomSkullService skullService;
	private final ShopValidationHandler validationHandler;
	private final MessageWrapper messageWrapper;
	private final ServerProvider serverProvider;

	/**
	 * Constructor for a new Slot Editor handler.
	 * 
	 * @param serverProvider
	 * @param messageWrapper
	 * @param validationHandler
	 * @param skullService
	 * @param shop
	 */
	public ShopSlotEditorHandlerImpl(ServerProvider serverProvider, MessageWrapper messageWrapper,
			ShopValidationHandler validationHandler, CustomSkullService skullService, AbstractShop shop) {
		this.shop = shop;
		this.skullService = skullService;
		this.validationHandler = validationHandler;
		this.messageWrapper = messageWrapper;
		this.serverProvider = serverProvider;
		selectedEditorSlot = 0;
		setupSlotEditor();
	}

	private void setupSlotEditor() {
		slotEditorInv = shop.createVillagerInventory(27, shop.getName() + "-SlotEditor");
		setupFactorItem();
		setupDefaultItem(Material.GREEN_WOOL, ChatColor.YELLOW + "save changes", 8);
		setupDefaultItem(Material.RED_WOOL, ChatColor.RED + "exit without save", 7);
		setupDefaultItem(Material.BARRIER, ChatColor.RED + "remove item", 26);
	}

	private void setupFactorItem() {
		ItemStack item = skullService.getSkullWithName(SkullTextureEnum.K_OFF, "factor off");
		getSlotEditorInventory().setItem(12, item);
		getSlotEditorInventory().setItem(21, item);
	}

	private void setupDefaultItem(Material material, String displayName, int slot) {
		ItemStack item = serverProvider.createItemStack(material, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(slot, item);
	}

	@Override
	public Inventory getSlotEditorInventory() {
		return slotEditorInv;
	}

	@Override
	public void changeInventoryName(String newName) {
		Inventory slotEditorNew = shop.createVillagerInventory(27, newName + "-SlotEditor");
		slotEditorNew.setContents(getSlotEditorInventory().getContents());
		slotEditorInv = slotEditorNew;
	}

	@Override
	public void setSelectedSlot(int slot) throws ShopSystemException, GeneralEconomyException {
		selectedEditorSlot = slot;
		setupSlotEditorWithShopItemInformations(slot);
	}

	private void setupSlotEditorWithShopItemInformations(int slot) throws ShopSystemException, GeneralEconomyException {
		double buyPrice = 0;
		double sellPrice = 0;
		try {
			ShopItem item = shop.getShopItem(slot);
			buyPrice = item.getBuyPrice();
			sellPrice = item.getSellPrice();
		} catch (EconomyPlayerException e) {
		}
		List<String> listBuy = new ArrayList<String>();
		List<String> listSell = new ArrayList<String>();
		listBuy.add(ChatColor.GOLD + "Price: " + buyPrice);
		listSell.add(ChatColor.GOLD + "Price: " + sellPrice);
		setupItemsInSlotEditor(Arrays.asList(2, 11, 20), SkullTextureEnum.PLUS, "plus", listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(6, 15, 24), SkullTextureEnum.TWENTY, "twenty", listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(5, 14, 23), SkullTextureEnum.TEN, "ten", listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(4, 13, 22), SkullTextureEnum.ONE, "one", listBuy, listSell);
		addSkullToSlotEditor("sellprice", 18, listSell, SkullTextureEnum.SELL);
		addSkullToSlotEditor("buyprice", 9, listBuy, SkullTextureEnum.BUY);
		setupSlotItemInSlotEditor(slot);
	}

	private void setupSlotItemInSlotEditor(int slot) throws GeneralEconomyException, ShopSystemException {
		try {
			ShopItem item = shop.getShopItem(slot);
			ItemStack stack = item.getItemStack();
			stack.setAmount(item.getAmount());
			getSlotEditorInventory().setItem(0, stack);
		} catch (EconomyPlayerException e) {
			ItemStack item = serverProvider.createItemStack(Material.BARRIER, 1);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "select item");
			item.setItemMeta(meta);
			getSlotEditorInventory().setItem(0, item);
		}
	}

	private void addSkullToSlotEditor(String displayName, int slot, List<String> loreList, SkullTextureEnum skull) {
		ItemStack item = skullService.getSkullWithName(skull, displayName);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(loreList);
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(slot, item);
	}

	private void setupItemsInSlotEditor(List<Integer> slots, SkullTextureEnum skullType, String skullName,
			List<String> listBuy, List<String> listSell) {
		ItemStack item = skullService.getSkullWithName(skullType, skullName);
		getSlotEditorInventory().setItem(slots.get(0), item);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(listBuy);
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(slots.get(1), item);
		meta = item.getItemMeta();
		meta.setLore(listSell);
		item.setItemMeta(meta);
		getSlotEditorInventory().setItem(slots.get(2), item);
	}

	@Override
	public void handleSlotEditor(InventoryClickEvent event) {
		if (event.getCurrentItem() != null) {
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			try {
				int slot = event.getSlot();
				String operator = getOperatorForHandleSlotEditor(event, slot);
				double price = getPriceForHandleSlotEditor(event, slot);
				ItemStack editorItemStack = slotEditorInv.getItem(0);
				// to exclude any interactio with the selected item
				if (editorItemStack != event.getCurrentItem()) {
					String command = "";
					if (event.getCurrentItem().getItemMeta() != null) {
						command = event.getCurrentItem().getItemMeta().getDisplayName();
					}
					handleSlotEditorCommand(event, player, slot, operator, price, editorItemStack, command);
				}
			} catch (ShopSystemException | EconomyPlayerException | GeneralEconomyException e) {
				player.sendMessage(e.getMessage());
			}
		}
	}

	private void handleSlotEditorCommand(InventoryClickEvent event, Player player, int slot, String operator,
			double price, ItemStack editorItemStack, String command)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		switch (ChatColor.stripColor(command)) {
		case "minus":
		case "plus":
			handleSwitchPlusMinus(slot, command);
			break;
		case "factor off":
		case "factor on":
			handleSwitchFactor(slot, command);
			break;
		case "one":
			handlePlusMinusNumber(1, Arrays.asList(4, 13, 22), slot, operator, price, editorItemStack);
			break;
		case "ten":
			handlePlusMinusNumber(10, Arrays.asList(5, 14, 23), slot, operator, price, editorItemStack);
			break;
		case "twenty":
			handlePlusMinusNumber(20, Arrays.asList(6, 15, 24), slot, operator, price, editorItemStack);
			break;
		case "save changes":
			handleSaveChanges(player);
			shop.openEditor(player);
			break;
		case "remove item":
			handleRemoveItem(player);
		case "exit without save":
			shop.openEditor(player);
			break;
		default:
			if (!"buyprice".equals(command) && !"sellprice".equals(command)) {
				handleAddItemToSlotEditor(event.getCurrentItem());
			}
			break;
		}
	}

	private void handleAddItemToSlotEditor(ItemStack clickedItem) {
		if (clickedItem.getType() != Material.SPAWNER) {
			ItemStack editorItemStack = clickedItem.clone();
			editorItemStack.setAmount(1);
			getSlotEditorInventory().setItem(0, editorItemStack);
		}
	}

	private void handleSaveChanges(Player player)
			throws ShopSystemException, EconomyPlayerException, GeneralEconomyException {
		double buyPrice = Double
				.valueOf(getSlotEditorInventory().getItem(9).getItemMeta().getLore().get(0).substring(9));
		double sellPrice = Double
				.valueOf(getSlotEditorInventory().getItem(18).getItemMeta().getLore().get(0).substring(9));
		validationHandler.checkForPricesGreaterThenZero(sellPrice, buyPrice);
		ItemStack stackInEditor = getSlotEditorInventory().getItem(0);
		try {
			ShopItem shopItem = shop.getShopItem(selectedEditorSlot);
			// slot is occupied, check if item changed
			if (!shopItem.getItemStack().isSimilar(stackInEditor)) {
				// remove and add
				handleRemoveItem(player);
				shop.addShopItem(selectedEditorSlot, sellPrice, buyPrice, stackInEditor);
				player.sendMessage(messageWrapper.getString("added", stackInEditor.getType().toString().toLowerCase()));
			} else {
				// edit
				Integer amountChange = generateChangeAmount(stackInEditor.getAmount(), shopItem);
				Double sellPriceChange = generateChangeSellPrice(sellPrice, shopItem);
				Double buyPriceChange = generateChangeBuyPrice(buyPrice, shopItem);
				player.sendMessage(
						shop.editShopItem(selectedEditorSlot, amountChange, sellPriceChange, buyPriceChange));
			}
		} catch (GeneralEconomyException | ShopSystemException e) {
			// item is new
			validationHandler.checkForItemDoesNotExist(stackInEditor.toString().hashCode(), shop.getItemList());
			if (stackInEditor.getType() != Material.BARRIER) {
				shop.addShopItem(selectedEditorSlot, sellPrice, buyPrice, stackInEditor);
				player.sendMessage(messageWrapper.getString("added", stackInEditor.getType().toString().toLowerCase()));
			}
		}
	}

	private Integer generateChangeAmount(int value, ShopItem shopItem) {
		if (shopItem.getAmount() == value) {
			return null;
		}
		return value;
	}

	private Double generateChangeSellPrice(double value, ShopItem shopItem) {
		if (shopItem.getSellPrice() == value) {
			return null;
		}
		return value;
	}

	private Double generateChangeBuyPrice(double value, ShopItem shopItem) {
		if (shopItem.getBuyPrice() == value) {
			return null;
		}
		return value;
	}

	private void handleRemoveItem(Player player)
			throws ShopSystemException, GeneralEconomyException, EconomyPlayerException {
		ItemStack item = shop.getShopItem(selectedEditorSlot).getItemStack();
		String deletedIem = item.getType().toString().toLowerCase();
		shop.removeShopItem(selectedEditorSlot);
		if (item.getType() == Material.SPAWNER) {
			player.sendMessage(messageWrapper.getString("removed", item.getItemMeta().getDisplayName().toLowerCase()));
		} else {
			player.sendMessage(messageWrapper.getString("removed", deletedIem));
		}
	}

	private void handleSwitchFactor(int slot, String state) {
		if ("factor off".equals(state)) {
			ItemStack item = skullService.getSkullWithName(SkullTextureEnum.K_ON, "factor on");
			getSlotEditorInventory().setItem(slot, item);
		} else {
			ItemStack item = skullService.getSkullWithName(SkullTextureEnum.K_OFF, "factor off");
			getSlotEditorInventory().setItem(slot, item);
		}
	}

	private void handleSwitchPlusMinus(int slot, String state) {
		if ("plus".equals(state)) {
			ItemStack item = skullService.getSkullWithName(SkullTextureEnum.MINUS, "minus");
			getSlotEditorInventory().setItem(slot, item);
		} else {
			ItemStack item = skullService.getSkullWithName(SkullTextureEnum.PLUS, "plus");
			getSlotEditorInventory().setItem(slot, item);
		}
	}

	private void handlePlusMinusNumber(int amount, List<Integer> slots, int slot, String operator, double price,
			ItemStack editorItemStack) {
		if (slots.get(0) == slot) {
			handlePlusMinusAmount(amount, operator, editorItemStack);
		} else if (slots.get(1) == slot) {
			handlePlusMinusPrice(Arrays.asList(9, 11, 13, 14, 15), amount, getFactor(12), operator, price);
		} else if (slots.get(2) == slot) {
			handlePlusMinusPrice(Arrays.asList(18, 20, 22, 23, 24), amount, getFactor(21), operator, price);
		}
	}

	private int getFactor(int slot) {
		int factor = 1;
		if (getSlotEditorInventory().getItem(slot).getItemMeta().getDisplayName().equals("factor on")) {
			factor = 1000;
		}
		return factor;
	}

	private void handlePlusMinusAmount(int value, String operator, ItemStack editorItemStack) {
		if (editorItemStack != null) {
			if ("plus".equals(operator)) {
				if ((editorItemStack.getAmount() + value <= 64)) {
					editorItemStack.setAmount(editorItemStack.getAmount() + value);
				} else {
					editorItemStack.setAmount(64);
				}
			} else {
				if (editorItemStack.getAmount() > value) {
					editorItemStack.setAmount(editorItemStack.getAmount() - value);
				} else {
					editorItemStack.setAmount(1);
				}
			}
		}
	}

	private void handlePlusMinusPrice(List<Integer> slots, int value, int factor, String operator, double price) {
		if (price >= (value * factor) && "minus".equals(operator)) {
			updateEditorPrice(slots.get(0), slots.get(1), slots.get(2), slots.get(3), slots.get(4),
					price - value * factor);
		} else if ("plus".equals(operator)) {
			updateEditorPrice(slots.get(0), slots.get(1), slots.get(2), slots.get(3), slots.get(4),
					price + value * factor);
		} else {
			updateEditorPrice(slots.get(0), slots.get(1), slots.get(2), slots.get(3), slots.get(4), 0.0);
		}
	}

	private double getPriceForHandleSlotEditor(InventoryClickEvent event, int slot) {
		switch (slot) {
		case 13:
		case 14:
		case 15:
			return Double.valueOf(getSlotEditorInventory().getItem(9).getItemMeta().getLore().get(0).substring(9));
		case 22:
		case 23:
		case 24:
			return Double.valueOf(getSlotEditorInventory().getItem(18).getItemMeta().getLore().get(0).substring(9));
		default:
			return 0.0;
		}
	}

	private String getOperatorForHandleSlotEditor(InventoryClickEvent event, int slot) {
		switch (slot) {
		case 4:
		case 5:
		case 6:
			return getSlotEditorInventory().getItem(2).getItemMeta().getDisplayName();
		case 13:
		case 14:
		case 15:
			return getSlotEditorInventory().getItem(11).getItemMeta().getDisplayName();
		case 22:
		case 23:
		case 24:
			return getSlotEditorInventory().getItem(20).getItemMeta().getDisplayName();
		default:
			return null;
		}
	}

	private void updateEditorPrice(int a, int b, int c, int d, int e, Double price) {
		List<String> list = new ArrayList<>();
		list.add(ChatColor.GOLD + "Price: " + price);
		ItemMeta meta = getSlotEditorInventory().getItem(a).getItemMeta();
		meta.setLore(list);
		getSlotEditorInventory().getItem(a).setItemMeta(meta);
		meta = getSlotEditorInventory().getItem(b).getItemMeta();
		meta.setLore(list);
		getSlotEditorInventory().getItem(b).setItemMeta(meta);
		meta = getSlotEditorInventory().getItem(c).getItemMeta();
		meta.setLore(list);
		getSlotEditorInventory().getItem(c).setItemMeta(meta);
		meta = getSlotEditorInventory().getItem(d).getItemMeta();
		meta.setLore(list);
		getSlotEditorInventory().getItem(d).setItemMeta(meta);
		meta = getSlotEditorInventory().getItem(e).getItemMeta();
		meta.setLore(list);
		getSlotEditorInventory().getItem(e).setItemMeta(meta);
	}
}
