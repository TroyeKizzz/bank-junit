package com.troyekizzz.app;

import com.troyekizzz.app.utils.Currency;

import lombok.Data;

/**
 * A class that represents a bank account.
 *
 * @author TroyeKizzz
 */
@Data
public class Account {
  /**
   * Shows if the account is open, i.e. it can be used.
   */
  private boolean isOpen;

  /**
   * The account balance in its currency.
   */
  private float balance;

  /**
   * The account currency.
   */
  private Currency currency;

  /**
   * The account interest rate.
   */
  private float interestRate;

  /**
   * The account owner.
   */
  private Customer owner;

  /**
   * The account number.
   */
  private String number;

  /**
   * Creates a new account.
   * 
   * The account is open by default, has zero balance,
   * and zero interest rate.
   *
   * @param owner    The account owner.
   * @param currency The account currency.
   */
  public Account(Customer owner, Currency currency) {
    this.owner = owner;
    this.currency = currency;
    this.balance = 0;
    this.isOpen = true;
    this.interestRate = 0;
    this.number = "FI" + (int) (Math.random() * 10000000) + (int) (Math.random() * 10000000);
    owner.getBankAccounts().add(this);
  }

  /**
   * Closes the account.
   *
   * The account can be closed only if it has zero balance.
   */
  public void close() throws IllegalStateException {
    if (this.balance > 0) {
      throw new IllegalStateException("The account has a positive balance.");
    }
    if (!this.isOpen()) {
      throw new IllegalStateException("The account is already closed.");
    }
    this.isOpen = false;
    this.owner.getBankAccounts().remove(this);
  }

  /**
   * Deposits money to the account.
   *
   * The money is deposited to the account only if it is open.
   * 
   * @param amount   The amount to deposit. Positive.
   * @param currency The currency of the amount.
   */
  public void deposit(float amount, Currency currency) throws IllegalArgumentException, IllegalStateException {
    if (amount < 0) {
      throw new IllegalArgumentException("The amount must be positive.");
    }
    if (!isOpen) {
      throw new IllegalStateException("The account is closed.");
    }
    try {
      this.balance += Exchange.convert(currency, this.currency, amount);
    } catch (IllegalArgumentException e) {
      throw e;
    }
  }

  /**
   * Withdraws money from the account.
   *
   * The money is withdrawn from the account only if it is open
   * and the amount is less than or equal to the balance.
   * 
   * @param amount   The amount to withdraw. Positive.
   * @param currency The currency of the amount.
   */
  public void withdraw(float amount, Currency currency) throws IllegalArgumentException, IllegalStateException {
    if (amount < 0) {
      throw new IllegalArgumentException("The amount must be positive.");
    }
    if (!isOpen) {
      throw new IllegalStateException("The account is closed.");
    }
    if (amount > this.balance) {
      throw new IllegalArgumentException("The amount is greater than the balance.");
    }
    try {
      this.balance -= Exchange.convert(currency, this.currency, amount);
    } catch (IllegalArgumentException e) {
      throw e;
    }
  }

  /**
   * Transfers money from one account to another.
   *
   * The currency of the transfer is the currency of the account 
   * from which the money is withdrawn.
   * 
   * @param from    The account to transfer money from.
   * @param to      The account to transfer money to.
   * @param amount  The amount to transfer.
   */
  static public void transfer(Account from, Account to, float amount) throws IllegalArgumentException, IllegalStateException {
    // Save the balances of the accounts in case of an exception.
    float fromBalance = from.getBalance();
    float toBalance = to.getBalance();

    try {
      from.withdraw(amount, from.getCurrency());
      to.deposit(amount, from.getCurrency());
    } catch (IllegalArgumentException e) {
      // Restore the balances of the accounts.
      from.setBalance(fromBalance);
      to.setBalance(toBalance);
      throw e;
    } catch (IllegalStateException e) {
      // Restore the balances of the accounts.
      from.setBalance(fromBalance);
      to.setBalance(toBalance);
      throw e;
    }
  }

  /**
   * Adds interest to the account.
   *
   * The interest is added to the account only if it is open.
   */
  public void addInterest() throws IllegalStateException {
    if (!isOpen) {
      throw new IllegalStateException("The account is closed.");
    }
    this.balance += this.getBalance() * this.getInterestRate();
  }

  /**
   * Returns a string representation of the account.
   */
  public String toString() {
    return "Account " + this.getNumber() + " of " + this.getOwner().getFirstName() + " " + this.getOwner().getLastName()
        + " has a balance of " + this.getBalance() + " " + this.getCurrency() + ".";
  }

}
