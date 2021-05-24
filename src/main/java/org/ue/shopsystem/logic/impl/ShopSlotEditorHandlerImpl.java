package org.ue.shopsystem.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ue.common.logic.api.CustomSkullService;
import org.ue.common.logic.api.MessageEnum;
import org.ue.common.logic.api.SkullTextureEnum;
import org.ue.common.logic.impl.InventoryGuiHandlerImpl;
import org.ue.common.utils.ServerProvider;
import org.ue.common.utils.api.MessageWrapper;
import org.ue.economyplayer.logic.api.EconomyPlayer;
import org.ue.shopsystem.logic.api.AbstractShop;
import org.ue.shopsystem.logic.api.ShopItem;
import org.ue.shopsystem.logic.api.ShopSlotEditorHandler;
import org.ue.shopsystem.logic.api.ShopValidator;
import org.ue.shopsystem.logic.api.ShopsystemException;

public class ShopSlotEditorHandlerImpl extends InventoryGuiHandlerImpl implements ShopSlotEditorHandler {

	private final ShopValidator validationHandler;
	private final MessageWrapper messageWrapper;
	private AbstractShop shop;

	// current state, false = off/minus
	private boolean sellFactorState = false;
	private boolean buyFactorState = false;

	private boolean sellOperatorState = true;
	private boolean buyOperatorState = true;
	private boolean amountOperatorState = true;

	private double selectedBuyPrice = 0.0;
	private double selectedSellPrice = 0.0;
	private int selectedAmount = 1;

	private int selectedEditorSlot;

	/**
	 * Constructor for a new Slot Editor handler.
	 * 
	 * @param serverProvider
	 * @param messageWrapper
	 * @param validationHandler
	 * @param skullService
	 * @param backLink
	 */
	public ShopSlotEditorHandlerImpl(ServerProvider serverProvider, MessageWrapper messageWrapper,
			ShopValidator validationHandler, CustomSkullService skullService, Inventory backLink) {
		super(skullService, serverProvider, backLink);
		this.validationHandler = validationHandler;
		this.messageWrapper = messageWrapper;
	}

	@Override
	public void setupSlotEditor(AbstractShop shop) {
		selectedEditorSlot = 0;
		this.shop = shop;
		inventory = shop.createVillagerInventory(27, "SlotEditor");
		setSkull(SkullTextureEnum.K_OFF, null, "factor off", 12);
		setSkull(SkullTextureEnum.K_OFF, null, "factor off", 21);

		setItem(Material.GREEN_WOOL, null, ChatColor.YELLOW + "save changes", 8);
		setItem(Material.RED_WOOL, null, ChatColor.RED + "exit without save", 7);
		setItem(Material.BARRIER, null, ChatColor.RED + "remove item", 26);
	}

	@Override
	public void setSelectedSlot(int slot) {
		selectedEditorSlot = slot;
		setupSlotEditorWithShopItemInformations(slot);
	}

	private void setupSlotEditorWithShopItemInformations(int slot) {
		try {
			ShopItem item = shop.getShopItem(slot);
			selectedBuyPrice = item.getBuyPrice();
			selectedSellPrice = item.getSellPrice();
			selectedAmount = item.getAmount();

			ItemStack stack = item.getItemStack();
			stack.setAmount(selectedAmount);
			inventory.setItem(0, stack);
		} catch (ShopsystemException e) {
			setItem(Material.BARRIER, null, ChatColor.GREEN + "select item", 0);
		}
		List<String> listBuy = Arrays.asList(ChatColor.GOLD + "Price: " + selectedBuyPrice);
		List<String> listSell = Arrays.asList(ChatColor.GOLD + "Price: " + selectedSellPrice);
		setupItemsInSlotEditor(Arrays.asList(2, 11, 20), SkullTextureEnum.PLUS, "plus", listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(6, 15, 24), SkullTextureEnum.TWENTY, "twenty", listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(5, 14, 23), SkullTextureEnum.TEN, "ten", listBuy, listSell);
		setupItemsInSlotEditor(Arrays.asList(4, 13, 22), SkullTextureEnum.ONE, "one", listBuy, listSell);

		setSkull(SkullTextureEnum.SELL, listSell, "sellprice", 18);
		setSkull(SkullTextureEnum.BUY, listBuy, "buyprice", 9);
	}

	private void setupItemsInSlotEditor(List<Integer> slots, SkullTextureEnum skullType, String skullName,
			List<String> listBuy, List<String> listSell) {
		setSkull(skullType, null, skullName, slots.get(0));
		setSkull(skullType, listBuy, skullName, slots.get(1));
		setSkull(skullType, listSell, skullName, slots.get(2));
	}

	@Override
	public void handleInventoryClick(ClickType clickType, int rawSlot, EconomyPlayer whoClicked) {
		List<Integer> buySlotList = Arrays.asList(9, 11, 13, 14, 15);
		List<Integer> sellSlotList = Arrays.asList(18, 20, 22, 23, 24);
		try {
			switch (rawSlot) {
			case 4:
				handlePlusMinusAmount(1, amountOperatorState);
				break;
			case 5:
				handlePlusMinusAmount(10, amountOperatorState);
				break;
			case 6:
				handlePlusMinusAmount(20, amountOperatorState);
				break;
			case 13:
				selectedBuyPrice = handlePlusMinusPrice(buySlotList, 1, buyFactorState, buyOperatorState,
						selectedBuyPrice);
				break;
			case 14:
				selectedBuyPrice = handlePlusMinusPrice(buySlotList, 10, buyFactorState, buyOperatorState,
						selectedBuyPrice);
				break;
			case 15:
				selectedBuyPrice = handlePlusMinusPrice(buySlotList, 20, buyFactorState, buyOperatorState,
						selectedBuyPrice);
				break;
			case 22:
				selectedSellPrice = handlePlusMinusPrice(sellSlotList, 1, sellFactorState, sellOperatorState,
						selectedSellPrice);
				break;
			case 23:
				selectedSellPrice = handlePlusMinusPrice(sellSlotList, 10, sellFactorState, sellOperatorState,
						selectedSellPrice);
				break;
			case 24:
				selectedSellPrice = handlePlusMinusPrice(sellSlotList, 20, sellFactorState, sellOperatorState,
						selectedSellPrice);
				break;
			case 8:
				handleSaveChanges(whoClicked.getPlayer());
			case 7:
				returnToBackLink(whoClicked.getPlayer());
				break;
			case 26:
				handleRemoveItem(whoClicked.getPlayer());
				returnToBackLink(whoClicked.getPlayer());
				break;
			case 2:
				amountOperatorState = handleSwitchPlusMinus(rawSlot, amountOperatorState);
				break;
			case 11:
				buyOperatorState = handleSwitchPlusMinus(rawSlot, buyOperatorState);
				break;
			case 20:
				sellOperatorState = handleSwitchPlusMinus(rawSlot, sellOperatorState);
				break;
			case 12:
				buyFactorState = handleSwitchFactor(rawSlot, buyFactorState);
				break;
			case 21:
				sellFactorState = handleSwitchFactor(rawSlot, sellFactorState);
				break;
			default:
				if (rawSlot > 26) {
					handleAddItemToSlotEditor(rawSlot);
				}
			}
		} catch (ShopsystemException e) {
			whoClicked.getPlayer().sendMessage(e.getMessage());
		}
	}

	private void handleAddItemToSlotEditor(int slot) {
		ItemStack clickedItem = inventory.getItem(slot);
		if (clickedItem != null && clickedItem.getType() != Material.SPAWNER) {
			ItemStack editorItemStack = clickedItem.clone();
			editorItemStack.setAmount(selectedAmount);
			inventory.setItem(0, editorItemStack);
		}
	}

	private void handleSaveChanges(Player player) throws ShopsystemException {
		validationHandler.checkForPricesGreaterThenZero(selectedSellPrice, selectedBuyPrice);
		ItemStack stackInEditor = inventory.getItem(0);
		try {
			ShopItem shopItem = shop.getShopItem(selectedEditorSlot);
			// slot is occupied, check if item changed
			if (!shopItem.getItemStack().isSimilar(stackInEditor)) {
				// remove and add
				handleRemoveItem(player);
				handleAddNewItem(player, stackInEditor);
			} else {
				// edit
				Integer amountChange = generateChangeAmount(stackInEditor.getAmount(), shopItem);
				Double sellPriceChange = generateChangeValue(shopItem.getSellPrice(), selectedSellPrice);
				Double buyPriceChange = generateChangeValue(shopItem.getBuyPrice(), selectedBuyPrice);
				player.sendMessage(
						shop.editShopItem(selectedEditorSlot, amountChange, sellPriceChange, buyPriceChange));
			}
		} catch (ShopsystemException e) {
			handleAddNewItem(player, stackInEditor);
		}
	}

	private void handleAddNewItem(Player player, ItemStack stack) throws ShopsystemException {
		if(stack.getType() != Material.BARRIER) {
			shop.addShopItem(selectedEditorSlot, selectedSellPrice, selectedBuyPrice, stack);
			player.sendMessage(messageWrapper.getString(MessageEnum.ADDED, stack.getType().toString().toLowerCase()));
		}
	}

	private Integer generateChangeAmount(int value, ShopItem shopItem) {
		if (shopItem.getAmount() == value) {
			return null;
		}
		return value;
	}

	private Double generateChangeValue(double oldPrice, double newPrice) {
		if (oldPrice == newPrice) {
			return null;
		}
		return newPrice;
	}

	private void handleRemoveItem(Player player) throws ShopsystemException {
		ItemStack item = shop.getShopItem(selectedEditorSlot).getItemStack();
		String deletedIem = item.getType().toString().toLowerCase();
		shop.removeShopItem(selectedEditorSlot);
		player.sendMessage(messageWrapper.getString(MessageEnum.REMOVED, deletedIem));
	}

	private boolean handleSwitchFactor(int slot, boolean oldState) {
		if (oldState) {
			setSkull(SkullTextureEnum.K_OFF, null, "factor off", slot);
		} else {
			setSkull(SkullTextureEnum.K_ON, null, "factor on", slot);
		}
		return !oldState;
	}

	private boolean handleSwitchPlusMinus(int slot, boolean oldState) {
		if (oldState) {
			setSkull(SkullTextureEnum.MINUS, null, "minus", slot);
		} else {
			setSkull(SkullTextureEnum.PLUS, null, "plus", slot);
		}
		return !oldState;
	}

	private void handlePlusMinusAmount(int value, boolean state) {
		ItemStack editorItemStack = inventory.getItem(0);
		int newAmount = editorItemStack.getAmount();
		if (state) {
			newAmount += value;
			if (newAmount > 64) {
				newAmount = 64;
			}
		} else {
			newAmount -= value;
			if (newAmount < 1) {
				newAmount = 1;
			}
		}
		editorItemStack.setAmount(newAmount);
		selectedAmount = newAmount;
	}

	private double handlePlusMinusPrice(List<Integer> slots, int value, boolean factorState, boolean operatorState,
			double oldPrice) {
		double newPrice = oldPrice;
		int factor = 1;
		if (factorState) {
			factor = 1000;
		}
		if (operatorState) {
			newPrice += factor * value;
		} else {
			newPrice -= factor * value;
			if (newPrice < 0) {
				newPrice = 0;
			}
		}
		updateEditorPrice(slots, newPrice);
		return newPrice;
	}

	private void updateEditorPrice(List<Integer> slots, Double price) {
		List<String> list = new ArrayList<>();
		list.add(ChatColor.GOLD + "Price: " + price);
		for (Integer slot : slots) {
			updateItemLore(slot, list);
		}
	}
}
