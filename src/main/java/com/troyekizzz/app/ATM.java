package com.troyekizzz.app;

import java.util.ArrayList;
import java.util.List;

import com.troyekizzz.app.utils.Currency;

import lombok.Getter;

/**
 * A class that represents an ATM.
 * 
 * @author TroyeKizzz
 */
@Getter
public class ATM {
  /**
   * The state of the ATM. 
   * 
   * True if the ATM is active, false if the ATM is
   */
  private boolean active = true;

  /**
   * The bank that the ATM is owned by.
   */
  private Bank bank;

  /**
   * The ATM location.
   */
  private String location;

  /**
   * The ATM balance.
   */
  private float balance;

  /**
   * The ATM transactions history.
   */
  private List<Transaction> transactions = new ArrayList<>();

  /**
   * Creates a new ATM.
   * 
   * @param bank     The bank that the ATM is owned by.
   * @param location The ATM location.
   * @param balance  The ATM balance.
   */
  public ATM(Bank bank, String location, float balance) {
    this.bank = bank;
    this.location = location;
    this.balance = balance;
  }

  /**
   * A method that deactivates the ATM and returns the ATM balance.
   * 
   * @return The ATM balance.
   * @throws IllegalStateException If the ATM is already deactivated.
   */
  public float deactivate() throws IllegalStateException {
    if (!active)
      throw new IllegalStateException("ATM is already deactivated");
    active = false;
    float balance = this.balance;
    this.balance = 0;
    return balance;
  }

  /**
   * A method that withdraws money from the the card balance.
   * 
   * The money is withdrawn from the card balance and added to the ATM balance.
   * The transaction is added to the card transaction history and the ATM.
   * 
   * @param card     The card that is used to withdraw money.
   * @param amount   The amount of money to withdraw.
   * @param currency The currency of the money to withdraw.
   */
  public void withdrawCash(Card card, float amount, Currency currency, String pin) throws IllegalStateException {
    if (!active)
      throw new IllegalStateException("ATM is deactivated");
    if (!card.validatePin(pin))
      throw new IllegalStateException("Invalid pin");
    if (balance < amount)
      throw new IllegalStateException("Not enough money in the ATM");
    card.getAccount().withdraw(amount, currency);
    balance -= amount;
    Transaction transaction = new Transaction(card, amount, currency, "Cash withdrawal from ATM at " + location);
    transactions.add(transaction);
    card.getHistory().add(transaction);
  }

  /**
   * A method that deposits money to the card balance.
   * 
   * The money is withdrawn from the ATM balance and added to the card balance.
   * The transaction is added to the card transaction history and the ATM.
   * 
   * @param card     The card that is used to deposit money.
   * @param amount   The amount of money to deposit.
   * @param currency The currency of the money to deposit.
   */
  public void depositCash(Card card, float amount, Currency currency, String pin) throws IllegalStateException {
    if (!active)
      throw new IllegalStateException("ATM is deactivated");
    if (!card.validatePin(pin))
      throw new IllegalStateException("Invalid pin");
    card.getAccount().deposit(amount, currency);
    balance += amount;
    Transaction transaction = new Transaction(card, amount, currency, "Cash deposit to ATM at " + location);
    transactions.add(transaction);
    card.getHistory().add(transaction);
  }

  /**
   * A method that checks the card balance.
   * 
   * @param card The card that is used to check the balance.
   * @param pin  The pin of the card.
   * @return The card balance.
   */
  public String checkBalance(Card card, String pin) throws IllegalStateException {
    if (!active)
      throw new IllegalStateException("ATM is deactivated");
    if (!card.validatePin(pin))
      throw new IllegalStateException("Invalid pin");
    return "Your balance is " + card.getAccount().getBalance() + " " + card.getAccount().getCurrency();
  }

  /**
   * A method that displays the last message from the card owner.
   * 
   * @param card The card that is used to display the message.
   * @param pin  The pin of the card.
   * @return The last message from the card owner.
   */
  public String displayMessage(Card card, String pin) throws IllegalStateException {
    if (!active)
      throw new IllegalStateException("ATM is deactivated");
    if (!card.validatePin(pin))
      throw new IllegalStateException("Invalid pin");
    List<String> messages = card.getAccount().getOwner().getMessages();
    return messages.isEmpty() ? "No messages" : messages.get(messages.size() - 1);
  }
}
