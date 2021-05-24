package org.ue.vault.impl;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.ue.bank.logic.api.BankAccount;
import org.ue.bank.logic.api.BankManager;
import org.ue.common.logic.api.GeneralEconomyException;
import org.ue.common.utils.ServerProvider;
import org.ue.config.logic.api.ConfigManager;
import org.ue.economyplayer.logic.api.EconomyPlayerManager;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class UltimateEconomyVaultImpl implements Economy {

	private final String name = "Ultimate_Economy";
	private final ConfigManager configManager;
	private final BankManager bankManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private final ServerProvider serverProvider;

	public UltimateEconomyVaultImpl(ConfigManager configManager, BankManager bankManager,
			EconomyPlayerManager ecoPlayerManager, ServerProvider serverProvider) {
		this.configManager = configManager;
		this.bankManager = bankManager;
		this.ecoPlayerManager = ecoPlayerManager;
		this.serverProvider = serverProvider;
	}

	@Override
	public String currencyNamePlural() {
		//System.out.println("currencyNamePlural");
		return configManager.getCurrencyPl();
	}

	@Override
	public String currencyNameSingular() {
		//System.out.println("currencyNameSingular");
		return configManager.getCurrencySg();
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		//System.out.println("depositPlayer " + player.getName() + " " + amount);
		return depositPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
		//System.out.println("depositPlayer " + player.getName() + " "+ world + " " + amount);
		return depositPlayer(player, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String owner, double amount) {
		//System.out.println("depositPlayer " + owner + " " + amount);
		try {
			ecoPlayerManager.getEconomyPlayerByName(owner).increasePlayerAmount(amount, false);
			return new EconomyResponse(amount, getBalance(owner), ResponseType.SUCCESS, "");
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse depositPlayer(String player, String world, double amount) {
		//System.out.println("depositPlayer " + player + " " + world + " " + amount);
		return depositPlayer(player, amount);
	}

	@Override
	public String format(double amount) {
		//System.out.println("format " + amount);
		return amount + "";
	}

	@Override
	public int fractionalDigits() {
		//System.out.println("fractionalDigits");
		return -1;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		//System.out.println("getBalance " + player.getName());
		try {
			return ecoPlayerManager.getEconomyPlayerByName(player.getName()).getBankAccount().getAmount();
		} catch (GeneralEconomyException e) {
			return 0;
		}
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		//System.out.println("getBalance " + player.getName() + " " + world);
		return getBalance(player);
	}

	@Override
	public double getBalance(String owner) {
		//System.out.println("getBalance " + owner);
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
		//System.out.println("getBalance " + player + " " + world);
		return getBalance(player);
	}

	@Override
	public String getName() {
		//System.out.println("getName");
		return name;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		//System.out.println("has " + player.getName() + " " + amount);
		return has(player.getName(), amount);
	}

	@Override
	public boolean has(OfflinePlayer player, String world, double amount) {
		//System.out.println("has " + player.getName() + " " + world + " " + amount);
		return has(player, amount);
	}

	@Override
	public boolean has(String owner, double amount) {
		//System.out.println("has " + owner + " " + amount);
		try {
			return ecoPlayerManager.getEconomyPlayerByName(owner).hasEnoughtMoney(amount);
		} catch (GeneralEconomyException e) {
			return false;
		}
	}

	@Override
	public boolean has(String player, String world, double amount) {
		//System.out.println("has " + player + " " + world + " " + amount);
		return has(player, amount);
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		//System.out.println("hasAccount " + player.getName());
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String world) {
		//System.out.println("hasAccount " + player.getName() + " " + world);
		return true;
	}

	@Override
	public boolean hasAccount(String owner) {
		//System.out.println("hasAccount " + owner);
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
		//System.out.println("hasAccount " + owner + " " + world);
		return hasAccount(owner);
	}

	@Override
	public boolean hasBankSupport() {
		//System.out.println("hasBankSupport");
		return true;
	}

	@Override
	public boolean isEnabled() {
		//System.out.println("isEnabled");
		if (serverProvider.getJavaPluginInstance() == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		//System.out.println("withdrawPlayer " + player.getName() + " " + amount);
		return withdrawPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
		//System.out.println("withdrawPlayer " + player.getName() + " " + world + " " + amount);
		return withdrawPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String owner, double amount) {
		//System.out.println("withdrawPlayer " + owner + " " + amount);
		try {
			ecoPlayerManager.getEconomyPlayerByName(owner).decreasePlayerAmount(amount, false);
			return new EconomyResponse(amount, getBalance(owner), ResponseType.SUCCESS, "");
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String player, String world, double amount) {
		//System.out.println("withdrawPlayer " + player + " " + world + " " + amount);
		return withdrawPlayer(player, amount);
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer arg0) {
		//System.out.println("createPlayerAccount " + arg0.getName());
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer arg0, String arg1) {
		//System.out.println("createPlayerAccount " + arg0.getName() + " " + arg1);
		return false;
	}

	@Override
	public boolean createPlayerAccount(String owner) {
		//System.out.println("createPlayerAccount " + owner);
		return false;
	}

	@Override
	public boolean createPlayerAccount(String owner, String world) {
		//System.out.println("createPlayerAccount " + owner + " " + world);
		return false;
	}

	@Override
	public EconomyResponse bankBalance(String iban) {
		//System.out.println("bankBalance " + iban);
		try {
			double amount = bankManager.getBankAccountByIban(iban).getAmount();
			return new EconomyResponse(0, amount, ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse bankDeposit(String iban, double amount) {
		//System.out.println("bankDeposit " + iban + " " + amount);
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
		//System.out.println("bankHas " + iban + " " + amount);
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
		//System.out.println("bankWithdraw " + iban + " " + amount);
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
		//System.out.println("createBank " + accountName + " " + player.getName());
		return createBank(accountName, player.getName());
	}

	@Override
	public EconomyResponse createBank(String accountName, String player) {
		//System.out.println("createBank " + accountName + " " + player);
		try {
			bankManager.createExternalBankAccount(0, accountName);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse deleteBank(String iban) {
		//System.out.println("deleteBank " + iban);
		try {
			bankManager.deleteBankAccount(bankManager.getBankAccountByIban(iban));
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public List<String> getBanks() {
		//System.out.println("getBanks");
		return bankManager.getIbanList();
	}

	@Override
	public EconomyResponse isBankMember(String arg0, String arg1) {
		//System.out.println("isBankMember " + arg0 + " " + arg1);
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
		//System.out.println("isBankMember " + arg0 + " " + arg1.getName());
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, String arg1) {
		//System.out.println("isBankOwner " + arg0 + " " + arg1);
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
		//System.out.println("isBankOwner " + arg0 + " " + arg1.getName());
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}
}
