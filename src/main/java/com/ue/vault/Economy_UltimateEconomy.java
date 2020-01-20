package com.ue.vault;

import java.util.List;

import org.bukkit.OfflinePlayer;

import com.ue.exceptions.PlayerException;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.Ultimate_Economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_UltimateEconomy implements Economy {
	
	private final String name = "Ultimate_Economy";
	private Ultimate_Economy plugin = Ultimate_Economy.getInstance;

	@Override
	public String currencyNamePlural() {
		return "$";
	}

	@Override
	public String currencyNameSingular() {
		return "$";
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		try {
			EconomyPlayerController.getEconomyPlayerByName(player.getName()).increasePlayerAmount(amount, false);
			return new EconomyResponse(amount, getBalance(player), ResponseType.SUCCESS , "");
		} catch (PlayerException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE , e.getMessage());
		}
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
		return depositPlayer(player, amount);
	}

	@Override
	public String format(double amount) {
		return amount + "";
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		try {
			return EconomyPlayerController.getEconomyPlayerByName(player.getName()).getBankAmount();
		} catch (PlayerException e) {
			return 0;
		}
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		return getBalance(player);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		try {
			return EconomyPlayerController.getEconomyPlayerByName(player.getName()).hasEnoughtMoney(amount);
		} catch (PlayerException e) {
			return false;
		}
	}

	@Override
	public boolean has(OfflinePlayer player, String world, double amount) {
		return has(player, amount);
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String world) {
		return true;
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		if(plugin == null) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		try {
			EconomyPlayerController.getEconomyPlayerByName(player.getName()).decreasePlayerAmount(amount,false);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS , "");
		} catch (PlayerException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE , e.getMessage());
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
		return withdrawPlayer(player, amount);
	}
	
	//not used, because it's done automatically by ue
	
	@Override
	public boolean createPlayerAccount(OfflinePlayer arg0) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer arg0, String arg1) {
		return false;
	}
	
	//not implemented
	
	@Override
	public EconomyResponse bankBalance(String arg0) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");

	}

	@Override
	public EconomyResponse bankDeposit(String arg0, double arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");

	}

	@Override
	public EconomyResponse bankHas(String arg0, double arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");

	}

	@Override
	public EconomyResponse bankWithdraw(String arg0, double arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");

	}
	
	@Override
	public EconomyResponse createBank(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse deleteBank(String arg0) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");
	}
	
	@Override
	public List<String> getBanks() {
		return null;
	}
	
	@Override
	public EconomyResponse isBankMember(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");
	}
	
	//deprecated
	
	@Override
	public boolean createPlayerAccount(String arg0) {
		return false;
	}
	
	@Override
	public boolean createPlayerAccount(String arg0, String arg1) {
		return false;
	}
	
	@Override
	public double getBalance(String player) {
		try {
			return EconomyPlayerController.getEconomyPlayerByName(player).getBankAmount();
		} catch (PlayerException e) {
			return 0;
		}
	}
	
	@Override
	public double getBalance(String player, String world) {
		return getBalance(player);
	}
	
	@Override
	public boolean hasAccount(String player) {
		return true;
	}
	
	@Override
	public boolean hasAccount(String player, String world) {
		return true;
	}
	
	@Override
	public boolean has(String player, double amount) {
		try {
			return EconomyPlayerController.getEconomyPlayerByName(player).hasEnoughtMoney(amount);
		} catch (PlayerException e) {
			return false;
		}
	}
	
	@Override
	public boolean has(String player, String world, double amount) {
		return has(player,amount);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(String player, double amount) {
		try {
			EconomyPlayerController.getEconomyPlayerByName(player).decreasePlayerAmount(amount,false);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS , "");
		} catch (PlayerException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE , e.getMessage());
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String player, String world, double amount) {
		return withdrawPlayer(player, amount);
	}
	
	@Override
	public EconomyResponse depositPlayer(String player, double amount) {
		try {
			EconomyPlayerController.getEconomyPlayerByName(player).increasePlayerAmount(amount,false);
			return new EconomyResponse(amount, getBalance(player), ResponseType.SUCCESS , "");
		} catch (PlayerException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE , e.getMessage());
		}
	}
	
	@Override
	public EconomyResponse depositPlayer(String player, String world, double amount) {
		return depositPlayer(player, amount);
	}
	
	@Override
	public EconomyResponse createBank(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Ultimate_Economy does not support bank accounts!");
	}
}
