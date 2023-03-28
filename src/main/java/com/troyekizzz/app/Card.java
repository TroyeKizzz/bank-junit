package com.troyekizzz.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.troyekizzz.app.utils.BenefitLevel;
import com.troyekizzz.app.utils.CardType;
import com.troyekizzz.app.utils.Currency;

import lombok.Getter;

/**
 * A class that represents a bank card.
 * 
 * @author TroyeKizzz
 */
@Getter
public class Card {
  /**
   * The card type.
   */
  private CardType type;

  /**
   * The card number.
   */
  private String number;

  /**
   * The card cvv.
   */
  private int cvv;

  /**
   * The card expiration date.
   */
  private Date expirationDate;

  /**
   * The card owner.
   */
  private Customer owner;

  /**
   * The account that the card is linked to.
   */
  private Account account;

  /**
   * The card pin.
   */
  private String pin;

  /**
   * One-time payment limit.
   */
  private float limit = 0;

  /**
   * The card transaction history.
   */
  private List<Transaction> history = new ArrayList<>();

  /**
   * Creates a new card.
   * 
   * @param type         The card type.
   * @param expirationDate The card expiration date.
   * @param account      The account that the card is linked to.
   * @param pin          The card pin.
   */
  public Card(CardType type, Account account, String pin) {
    this.type = type;
    this.number = String.valueOf((int) (Math.random() * 100000000)) + String.valueOf((int) (Math.random() * 100000000));
    this.cvv = (int) (Math.random() * 1000);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.YEAR, 4);
    this.expirationDate = calendar.getTime();
    this.owner = account.getOwner();
    this.account = account;
    this.pin = pin;
  }

  /**
   * Processes a purchase.
   * 
   * @param amount   The amount to be transferred.
   * @param currency The currency of the amount.
   * @param pin      The card pin.
   * @param customer The customer that the purchase is made for.
   * @return         The transaction.
   */
  public Transaction processPurchase(float amount, Currency currency, String pin, Customer customer) {
    if (amount <= 0 || !this.validatePin(pin)) {
      throw new IllegalArgumentException("Invalid purchase of " + amount + " " + currency.toString());
    }
    if (this.limit > 0 && amount > this.limit) {
      throw new IllegalArgumentException("Purchase amount exceeds limit");
    }
    Transaction transaction = Account.transfer(
      this.account, 
      customer.getBankAccounts().get(0), 
      Exchange.getInstance().convert(currency, this.account.getCurrency(), amount)
    );
    transaction.setDescription("Purchase of goods from " + customer.getFirstName());
    this.history.add(transaction);
    return transaction;
  }

  /**
   * Validates a pin code.
   * 
   * @param pin
   * @return True if the pin is valid, false otherwise.
   */
  public boolean validatePin(String pin) {
    if (pin == null || this.pin == null) {
      return false;
    }
    return pin == this.pin;
  }

  /**
   * Sets a payment limit.
   * 
   * @param limit
   * @param pin
   */
  public void setLimit(float limit, String pin) {
    if (!this.validatePin(pin)) {
      throw new IllegalArgumentException("Invalid pin");
    }
    if (limit <= 0) {
      throw new IllegalArgumentException("Invalid limit");
    }
    this.limit = limit;
  }

  /**
   * Unsets a payment limit.
   * 
   * @param pin
   */
  public void unsetLimit(String pin) {
    if (!this.validatePin(pin)) {
      throw new IllegalArgumentException("Invalid pin");
    }
    this.limit = 0;
  }

  /**
   * Returns the card cvv.
   * 
   * @param pin
   * @return The card cvv.
   */
  public int getCvv(String pin) {
    if (!this.validatePin(pin)) {
      throw new IllegalArgumentException("Invalid pin");
    }
    return cvv;
  }

  /**
   * Returns the card benefit level.
   * 
   * @return The card benefit level.
   */
  public BenefitLevel getCardBenefitLevel() {
    return this.owner.getBenefitLevel();
  }
}
