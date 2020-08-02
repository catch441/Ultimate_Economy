package com.ue.vault;

import java.util.List;

import javax.inject.Inject;

import org.bukkit.OfflinePlayer;

import com.ue.bank.logic.api.BankAccount;
import com.ue.bank.logic.api.BankManager;
import com.ue.config.logic.api.ConfigManager;
import com.ue.economyplayer.logic.api.EconomyPlayerManager;
import com.ue.economyplayer.logic.impl.EconomyPlayerException;
import com.ue.ultimate_economy.GeneralEconomyException;
import com.ue.ultimate_economy.UltimateEconomy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class UltimateEconomyVault implements Economy {

	private final String name = "Ultimate_Economy";
	private final ConfigManager configManager;
	private final BankManager bankManager;
	private final EconomyPlayerManager ecoPlayerManager;
	private UltimateEconomy plugin = UltimateEconomy.getInstance;

	/**
	 * UE economy implementation of vault economy.
	 * 
	 * @param ecoPlayerManager
	 * @param bankManager
	 * @param configManager
	 */
	@Inject
	public UltimateEconomyVault(EconomyPlayerManager ecoPlayerManager, BankManager bankManager,
			ConfigManager configManager) {
		this.configManager = configManager;
		this.bankManager = bankManager;
		this.ecoPlayerManager = ecoPlayerManager;
	}

	@Override
	public String currencyNamePlural() {
		// System.out.println("1");
		return configManager.getCurrencyPl();
	}

	@Override
	public String currencyNameSingular() {
		// System.out.println("2");
		return configManager.getCurrencySg();
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		// System.out.println("3 " + player.getName() + " " + amount);
		return depositPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
		// System.out.println("4 " + player.getName() + " " + world + " " + amount);
		return depositPlayer(player, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String owner, double amount) {
		// System.out.println("5 " + owner + " " + amount);
		try {
			if (ecoPlayerManager.getEconomyPlayerNameList().contains(owner)) {
				ecoPlayerManager.getEconomyPlayerByName(owner).increasePlayerAmount(amount, false);
			} else {
				bankManager.getBankAccountByIban(owner).increaseAmount(amount);
			}
			return new EconomyResponse(amount, getBalance(owner), ResponseType.SUCCESS, "");
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			// System.out.println("5 error: " + e.getMessage());
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse depositPlayer(String player, String world, double amount) {
		// System.out.println("6 " + player + " " + world + " " + amount);
		return depositPlayer(player, amount);
	}

	@Override
	public String format(double amount) {
		// System.out.println("7 " + amount);
		return amount + "";
	}

	@Override
	public int fractionalDigits() {
		// System.out.println("8");
		return -1;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		// System.out.println("9 " + player.getName());
		try {
			return ecoPlayerManager.getEconomyPlayerByName(player.getName()).getBankAccount().getAmount();
		} catch (EconomyPlayerException e) {
			// System.out.println("9 error: " + e.getMessage());
			return 0;
		}
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		// System.out.println("10 " + player.getName() + " " + world);
		return getBalance(player);
	}

	@Override
	public double getBalance(String owner) {
		// System.out.println("11 " + owner);
		try {
			if (ecoPlayerManager.getEconomyPlayerNameList().contains(owner)) {
				return ecoPlayerManager.getEconomyPlayerByName(owner).getBankAccount().getAmount();
			} else {
				return bankManager.getBankAccountByIban(owner).getAmount();
			}
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			// System.out.println("11 error: " + e.getMessage());
			return 0;
		}
	}

	@Override
	public double getBalance(String player, String world) {
		// System.out.println("12 " + player + " " + world);
		return getBalance(player);
	}

	@Override
	public String getName() {
		// System.out.println("13");
		return name;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		// System.out.println("14 " + player.getName() + " " + amount);
		try {
			return ecoPlayerManager.getEconomyPlayerByName(player.getName()).hasEnoughtMoney(amount);
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			// System.out.println("14 error: " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean has(OfflinePlayer player, String world, double amount) {
		// System.out.println("15 " + player.getName() + " " + world + " " + amount);
		return has(player, amount);
	}

	@Override
	public boolean has(String owner, double amount) {
		// System.out.println("16 " + owner + " " + amount);
		try {
			if (ecoPlayerManager.getEconomyPlayerNameList().contains(owner)) {
				return ecoPlayerManager.getEconomyPlayerByName(owner).hasEnoughtMoney(amount);
			} else {
				return bankManager.getBankAccountByIban(owner).hasAmount(amount);
			}
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			// System.out.println("16 error: " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean has(String player, String world, double amount) {
		// System.out.println("17 " + player + " " + world + " " + amount);
		return has(player, amount);
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		// System.out.println("18 " + player.getName());
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String world) {
		// System.out.println("19 " + player.getName() + " " + world);
		return true;
	}

	@Override
	public boolean hasAccount(String owner) {
		// System.out.println("20 " + owner);
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
		// System.out.println("21 " + owner + " " + world);
		return hasAccount(owner);
	}

	@Override
	public boolean hasBankSupport() {
		// System.out.println("22");
		return true;
	}

	@Override
	public boolean isEnabled() {
		// System.out.println("23");
		if (plugin == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		// System.out.println("24 " + player.getName() + " " + amount);
		return withdrawPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
		// System.out.println("25 " + player.getName() + " " + world + " " + amount);
		return withdrawPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String owner, double amount) {
		// System.out.println("26 " + owner + " " + amount);
		try {
			if (ecoPlayerManager.getEconomyPlayerNameList().contains(owner)) {
				ecoPlayerManager.getEconomyPlayerByName(owner).decreasePlayerAmount(amount, false);
			} else {
				bankManager.getBankAccountByIban(owner).decreaseAmount(amount);
			}
			return new EconomyResponse(amount, getBalance(owner), ResponseType.SUCCESS, "");
		} catch (EconomyPlayerException | GeneralEconomyException e) {
			// System.out.println("26 error: " + e.getMessage());
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String player, String world, double amount) {
		// System.out.println("27 " + player + " " + world + " " + amount);
		return withdrawPlayer(player, amount);
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer arg0) {
		// System.out.println("28 " + arg0);
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer arg0, String arg1) {
		// System.out.println("29 " + arg0 + " " + arg1);
		return false;
	}

	@Override
	public boolean createPlayerAccount(String owner) {
		// System.out.println("30 " + owner);
		try {
			bankManager.createExternalBankAccount(0, owner);
			return true;
		} catch (GeneralEconomyException e) {
			// System.out.println("30 error: " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean createPlayerAccount(String owner, String world) {
		// System.out.println("31 " + owner + " " + world);
		return createPlayerAccount(owner);
	}

	@Override
	public EconomyResponse bankBalance(String iban) {
		// System.out.println("32 " + iban);
		try {
			double amount = bankManager.getBankAccountByIban(iban).getAmount();
			return new EconomyResponse(0, amount, ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			// System.out.println("32 error: " + e.getMessage());
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse bankDeposit(String iban, double amount) {
		// System.out.println("33 " + iban + " " + amount);
		try {
			BankAccount account = bankManager.getBankAccountByIban(iban);
			account.increaseAmount(amount);
			return new EconomyResponse(amount, account.getAmount(), ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			// System.out.println("33 error: " + e.getMessage());
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse bankHas(String iban, double amount) {
		// System.out.println("34 " + iban + " " + amount);
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
			// System.out.println("34 error: " + e.getMessage());
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse bankWithdraw(String iban, double amount) {
		// System.out.println("35 " + iban + " " + amount);
		try {
			BankAccount account = bankManager.getBankAccountByIban(iban);
			account.decreaseAmount(amount);
			return new EconomyResponse(amount, account.getAmount(), ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			// System.out.println("35 error: " + e.getMessage());
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse createBank(String accountName, OfflinePlayer player) {
		// System.out.println("36 " + accountName + " " + player.getName());
		return createBank(accountName, player.getName());
	}

	@Override
	public EconomyResponse createBank(String accountName, String player) {
		// System.out.println("37 " + accountName + " " + player);
		try {
			bankManager.createExternalBankAccount(0, accountName);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			// System.out.println("37 error: " + e.getMessage());
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public EconomyResponse deleteBank(String iban) {
		// System.out.println("38 " + iban);
		try {
			bankManager.deleteBankAccount(bankManager.getBankAccountByIban(iban));
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		} catch (GeneralEconomyException e) {
			// System.out.println("38 error: " + e.getMessage());
			return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
		}
	}

	@Override
	public List<String> getBanks() {
		// System.out.println("39");
		return bankManager.getIbanList();
	}

	@Override
	public EconomyResponse isBankMember(String arg0, String arg1) {
		// System.out.println("40 " + arg0 + " " + arg1);
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
		// System.out.println("41 " + arg0 + " " + arg1);
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, String arg1) {
		// System.out.println("42 " + arg0 + " " + arg1);
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
		// System.out.println("43 " + arg0 + " " + arg1);
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, null);
	}
}
