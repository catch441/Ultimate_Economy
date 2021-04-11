package com.ue.vault.impl;

import java.util.List;

import javax.inject.Inject;

import org.bukkit.OfflinePlayer;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.common.utils.ServerProvider;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.general.impl.GeneralEconomyException;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class UltimateEconomyVaultImpl implements Economy {

	private final String name = "Ultimate_Economy";
	private final ConfigManager configManager;
	private final BankManager bankManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final ServerProvider serverProvider;

	@Inject
	public UltimateEconomyVaultImpl(ConfigManager configManager, BankManager bankManager,
			EconomyPlayerManager ecoPlayerManager, ServerProvider serverProvider) {
		this.configManager = configManager;
		this.bankManager = bankManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.serverProvider = serverProvider;
	}

	@Override
	public String currencyNamePlural() {
		return configManager.getCurrencyPl();
	}

	@Override
	public String currencyNameSingular() {
		return configManager.getCurrencySg();
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		return depositPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
		return depositPlayer(player, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String owner, double amount) {
		try {
			ecoPlayerManager.getEconomyPlayerByName(owner).increasePlayerAmount(amount, false);
			return new EconomyResponse(amount, getBalance(owner), ResponseType.SUCCESS, "");
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse depositPlayer(String player, String world, double amount) {
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
			return ecoPlayerManager.getEconomyPlayerByName(player.getName()).getBankAccount().getAmount();
		} catch (GeneralEconomyException e) {
			return 0;
		}
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		return getBalance(player);
	}

	@Override
	public double getBalance(String owner) {
		try {
			if (ecoPlayerManager.getEconomyPlayerNameList().contains(owner)) {
				return ecoPlayerManager.getEconomyPlayerByName(owner).getBankAccount().getAmount();
			} else {
				return bankManager.getBankAccountByIban(owner).getAmount();
			}
		} catch (GeneralEconomyException e) {
			return 0;
		}
	}

	@Override
	public double getBalance(String player, String world) {
		return getBalance(player);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return has(player.getName(), amount);
	}

	@Override
	public boolean has(OfflinePlayer player, String world, double amount) {
		return has(player, amount);
	}

	@Override
	public boolean has(String owner, double amount) {
		try {
			return ecoPlayerManager.getEconomyPlayerByName(owner).hasEnoughtMoney(amount);
		} catch (GeneralEconomyException e) {
			return false;
		}
	}

	@Override
	public boolean has(String player, String world, double amount) {
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
	public boolean hasAccount(String owner) {
		if (ecoPlayerManager.getEconomyPlayerNameList().contains(owner)) {
			return true;
		} else {
			if (bankManager.getIbanList().contains(owner)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean hasAccount(String owner, String world) {
		return hasAccount(owner);
	}

	@Override
	public boolean hasBankSupport() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		if (serverProvider.getJavaPluginInstance() == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		return withdrawPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
		return withdrawPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String owner, double amount) {
		try {
			ecoPlayerManager.getEconomyPlayerByName(owner).decreasePlayerAmount(amount, false);
			return new EconomyResponse(amount, getBalance(owner), ResponseType.SUCCESS, "");
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String player, String world, double amount) {
		return withdrawPlayer(player, amount);
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer arg0) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer arg0, String arg1) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(String owner) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(String owner, String world) {
		return false;
	}

	@Override
	public EconomyResponse bankBalance(String iban) {
		try {
			double amount = bankManager.getBankAccountByIban(iban).getAmount();
			return new EconomyResponse(0, amount, ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse bankDeposit(String iban, double amount) {
		try {
			BankAccount account = bankManager.getBankAccountByIban(iban);
			account.increaseAmount(amount);
			return new EconomyResponse(amount, account.getAmount(), ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse bankHas(String iban, double amount) {
		try {
			BankAccount account = bankManager.getBankAccountByIban(iban);
			boolean has = account.hasAmount(amount);
			if (has) {
				return new EconomyResponse(0, account.getAmount(), ResponseType.SUCCESS, null);
			} else {
				return new EconomyResponse(0, account.getAmount(), ResponseType.FAILURE,
						"Bank account has not enough money!");
			}
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse bankWithdraw(String iban, double amount) {
		try {
			BankAccount account = bankManager.getBankAccountByIban(iban);
			account.decreaseAmount(amount);
			return new EconomyResponse(amount, account.getAmount(), ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse createBank(String accountName, OfflinePlayer player) {
		return createBank(accountName, player.getName());
	}

	@Override
	public EconomyResponse createBank(String accountName, String player) {
		try {
			bankManager.createExternalBankAccount(0, accountName);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse deleteBank(String iban) {
		try {
			bankManager.deleteBankAccount(bankManager.getBankAccountByIban(iban));
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public List<String> getBanks() {
		return bankManager.getIbanList();
	}

	@Override
	public EconomyResponse isBankMember(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}
}
