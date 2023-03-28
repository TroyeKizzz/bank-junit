package com.troyekizzz.app;

import java.util.ArrayList;
import java.util.List;

import com.troyekizzz.app.utils.CardType;
import com.troyekizzz.app.utils.Currency;

import lombok.Getter;

/**
 * A class that represents a bank.
 * 
 * @author TroyeKizzz
 */
@Getter
public class Bank {
  /**
   * The bank name.
   */
  private String name;

  /**
   * The bank customers.
   */
  private List<Customer> customers = new ArrayList<>();

  /**
   * The bank accounts.
   */
  private List<Account> accounts = new ArrayList<>();

  /**
   * The bank cards.
   */
  private List<Card> cards = new ArrayList<>();

  /**
   * The bank ATMs.
   */
  private List<ATM> atms = new ArrayList<>();

  /**
   * The bank capital. The total amount of money that the bank has.
   */
  private float capital = 0;

  /**
   * The bank constructor.
   * 
   * @param name   The bank name.
   * @param capital The bank capital.
   */
  public Bank(String name, float capital) throws IllegalArgumentException {
    if (capital < 0)
      throw new IllegalArgumentException("Capital cannot be negative");
    this.name = name;
    this.capital = capital;
  }

  /**
   * A method that removes money from the bank capital.
   * 
   * @param amount The amount of money to remove.
   */
  private void removeCapital(float amount) throws IllegalArgumentException {
    if (amount > capital)
      throw new IllegalArgumentException("Not enough capital");
    capital -= amount;
  }

  /**
   * A method that adds money to the bank capital.
   * 
   * @param amount The amount of money to add.
   */
  public Customer addCustomer(String firstName, String lastName, String email, String phone) {
    Customer customer = new Customer(firstName, lastName, email, phone);
    customers.add(customer);
    return customer;
  }

  /**
   * A method that adds an account to the bank.
   * 
   * @param customer The account owner.
   * @param currency The account currency.
   */
  public Account openAccount(Customer customer, Currency currency) throws IllegalArgumentException{
    if (customers.indexOf(customer) == -1)
      throw new IllegalArgumentException("Customer does not exist");
    Account account = new Account(customer, currency);
    accounts.add(account);
    return account;
  }

  /**
   * A method that adds a card to the bank.
   * 
   * @param account The account that the card is linked to.
   * @param type    The card type.
   * @param pin     The card pin.
   */
  public Card addCard(Account account, CardType type, String pin) {
    Card card = new Card(type, account, pin);
    cards.add(card);
    return card;
  }

  /**
   * A method that adds an ATM to the bank.
   * 
   * @param location The ATM location.
   * @param balance  The ATM balance.
   */
  public ATM addAtm(String location, float balance) throws IllegalArgumentException {
    try {
      this.removeCapital(balance);
    } catch (IllegalArgumentException e) {
      throw e;
    }
    ATM atm = new ATM(this, location, balance);
    atms.add(atm);
    return atm;
  }

  /**
   * A method that removes a card from the bank.
   * 
   * @param card The card to remove.
   */
  public void removeCard(Card card) {
    cards.remove(card);
  }

  /**
   * A method that removes an account from the bank.
   * 
   * All the account cards are removed.
   * 
   * @param account The account to remove.
   */
  public void closeAccount(Account account) {
    List<Card> cardsToRemove = new ArrayList<>();
    for (Card card : cards) {
      if (card.getAccount().equals(account)) {
        cardsToRemove.add(card);
      }
    }
    for (Card card : cardsToRemove) {
      removeCard(card);
    }
    accounts.remove(account);
  }

  /**
   * A method that removes a customer from the bank.
   * 
   * All the customer accounts are closed.
   * And all the customer cards are removed.
   * 
   * @param customer The customer to remove.
   */
  public void removeCustomer(Customer customer) {
    List<Account> accountsToRemove = new ArrayList<>();
    for (Account account : accounts) {
      if (account.getOwner().equals(customer)) {
        accountsToRemove.add(account);
      }
    }
    for (Account account : accountsToRemove) {
      closeAccount(account);
    }
    customers.remove(customer);
  }

  /**
   * A method that removes an ATM from the bank.
   * 
   * The ATM balance is added to the bank capital.
   * 
   * @param atm The ATM to remove.
   */
  public void removeAtm(ATM atm) {
    this.capital += atm.deactivate();
    atms.remove(atm);
  }
}
