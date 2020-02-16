package com.ue.vault;

import java.util.List;

import org.bukkit.OfflinePlayer;

import com.ue.bank.api.BankAccount;
import com.ue.bank.api.BankController;
import com.ue.config.api.ConfigController;
import com.ue.exceptions.GeneralEconomyException;
import com.ue.exceptions.PlayerException;
import com.ue.player.api.EconomyPlayerController;
import com.ue.ultimate_economy.UltimateEconomy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class EconomyUltimateEconomy implements Economy {

    private final String name = "Ultimate_Economy";
    private UltimateEconomy plugin = UltimateEconomy.getInstance;

    @Override
    public String currencyNamePlural() {
	// System.out.println("1");
	return ConfigController.getCurrencyPl();
    }

    @Override
    public String currencyNameSingular() {
	// System.out.println("2");
	return ConfigController.getCurrencySg();
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
	    if (EconomyPlayerController.getEconomyPlayerNameList().contains(owner)) {
		EconomyPlayerController.getEconomyPlayerByName(owner).increasePlayerAmount(amount, false);
	    } else {
		BankController.getBankAccountByIban(owner).increaseAmount(amount);
	    }
	    return new EconomyResponse(amount, getBalance(owner), ResponseType.SUCCESS, "");
	} catch (PlayerException | GeneralEconomyException e) {
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
	    return EconomyPlayerController.getEconomyPlayerByName(player.getName()).getBankAmount();
	} catch (PlayerException e) {
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
	    if (EconomyPlayerController.getEconomyPlayerNameList().contains(owner)) {
		return EconomyPlayerController.getEconomyPlayerByName(owner).getBankAmount();
	    } else {
		return BankController.getBankAccountByIban(owner).getAmount();
	    }
	} catch (PlayerException | GeneralEconomyException e) {
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
	    return EconomyPlayerController.getEconomyPlayerByName(player.getName()).hasEnoughtMoney(amount);
	} catch (PlayerException e) {
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
	    if (EconomyPlayerController.getEconomyPlayerNameList().contains(owner)) {
		return EconomyPlayerController.getEconomyPlayerByName(owner).hasEnoughtMoney(amount);
	    } else {
		return BankController.getBankAccountByIban(owner).hasAmount(amount);
	    }
	} catch (PlayerException | GeneralEconomyException e) {
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
	if (EconomyPlayerController.getEconomyPlayerNameList().contains(owner)) {
	    return true;
	} else {
	    if (BankController.getIbanList().contains(owner)) {
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
	    if (EconomyPlayerController.getEconomyPlayerNameList().contains(owner)) {
		EconomyPlayerController.getEconomyPlayerByName(owner).decreasePlayerAmount(amount, false);
	    } else {
		BankController.getBankAccountByIban(owner).decreaseAmount(amount);
	    }
	    return new EconomyResponse(amount, getBalance(owner), ResponseType.SUCCESS, "");
	} catch (PlayerException | GeneralEconomyException e) {
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
	    BankController.createExternalBankAccount(0, owner);
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
	    double amount = BankController.getBankAccountByIban(iban).getAmount();
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
	    BankAccount account = BankController.getBankAccountByIban(iban);
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
	    BankAccount account = BankController.getBankAccountByIban(iban);
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
	    BankAccount account = BankController.getBankAccountByIban(iban);
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
	    BankController.createExternalBankAccount(0, accountName);
	    return new EconomyResponse(0, 0, ResponseType.SUCCESS,null);
	} catch (GeneralEconomyException e) {
	    // System.out.println("37 error: " + e.getMessage());
	    return new EconomyResponse(0, 0, ResponseType.FAILURE,e.getMessage());
	}
    }

    @Override
    public EconomyResponse deleteBank(String iban) {
	// System.out.println("38 " + iban);
	try {
	    BankController.deleteBankAccount(iban);
	    return new EconomyResponse(0, 0, ResponseType.SUCCESS,null);
	} catch (GeneralEconomyException e) {
	    // System.out.println("38 error: " + e.getMessage());
	    return new EconomyResponse(0, 0, ResponseType.FAILURE,e.getMessage());
	}
    }

    @Override
    public List<String> getBanks() {
	// System.out.println("39");
	return BankController.getIbanList();
    }

    @Override
    public EconomyResponse isBankMember(String arg0, String arg1) {
	// System.out.println("40 " + arg0 + " " + arg1);
	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,null);
    }

    @Override
    public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
	// System.out.println("41 " + arg0 + " " + arg1);
	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,null);
    }

    @Override
    public EconomyResponse isBankOwner(String arg0, String arg1) {
	// System.out.println("42 " + arg0 + " " + arg1);
	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,null);
    }

    @Override
    public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
	// System.out.println("43 " + arg0 + " " + arg1);
	return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,null);
    }
}
