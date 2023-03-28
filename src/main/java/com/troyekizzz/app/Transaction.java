package com.troyekizzz.app;

import java.util.Date;

import com.troyekizzz.app.utils.BenefitLevel;
import com.troyekizzz.app.utils.Currency;
import com.troyekizzz.app.utils.NotificationType;

import lombok.Data;

/**
 * A class that represents a transaction.
 * 
 * @author TroyeKizzz
 */
@Data
public class Transaction {
  /**
   * The account from which the transaction is made.
   */
  private Account from;
  
  /**
   * The account to which the transaction is made.
   */
  private Account to;

  /**
   * The amount of the transaction.
   */
  private float amount;

  /**
   * The currency of the transaction.
   */
  private Currency currency;

  /**
   * The date of the transaction.
   */
  private Date date;

  /**
   * The description of the transaction.
   */
  private String description;

  /**
   * Creates a new transaction.
   * 
   * @param from        The account from which the transaction is made.
   * @param to          The account to which the transaction is made.
   * @param amount      The amount of the transaction.
   * @param currency    The currency of the transaction.
   * @param description The description of the transaction.
   */
  public Transaction(Account from, Account to, float amount, Currency currency, String description) {
    if (from == null || to == null || amount <= 0 || currency == null) {
      throw new IllegalArgumentException("Invalid transaction");
    }
    this.from = from;
    this.to = to;
    this.amount = amount;
    this.currency = currency;
    this.date = new Date();
    this.description = description;
  }

  public Transaction(Card card, float amount, Currency currency, String description) {
    if (amount <= 0 || currency == null) {
      throw new IllegalArgumentException("Invalid transaction");
    }
    this.from = card.getAccount();
    this.to = null;
    this.amount = amount;
    this.currency = currency;
    this.date = new Date();
    this.description = description;
  }

  /**
   * Calculates the fee of the transaction based on the sender's benefit level.
   * 
   * @return The fee of the transaction.
   */
  public float calculateFee() {
    float percentage = 0.02f;
    if (from.getOwner().getBenefitLevel() == BenefitLevel.GOLD) {
      percentage = 0.01f;
    }
    if (from.getOwner().getBenefitLevel() == BenefitLevel.PLATINUM) {
      percentage = 0.0f;
    }
    return amount * percentage;
  }

  /**
   * Checks if the transaction is a fraud.
   * 
   * @return True if the transaction is a fraud, false otherwise.
   */
  public boolean checkFraudStatus() {
    if (from.getOwner().getBenefitLevel() == BenefitLevel.SILVER) {
      if (amount > 700) {
        return true;
      }
    } else if (from.getOwner().getBenefitLevel() == BenefitLevel.GOLD) {
      if (amount > 7000) {
        return true;
      }
    } else if (from.getOwner().getBenefitLevel() == BenefitLevel.PLATINUM) {
      if (amount > 30000) {
        return true;
      }
    }
    return false;
  }

  /**
   * Calculates the interest rate of the transaction based on the sender's benefit
   * level.
   * 
   * @return The interest rate of the transaction.
   */
  public float getInterestRate() throws RuntimeException {
    if (from.getOwner().getBenefitLevel() == BenefitLevel.SILVER) {
      return 0.15f;
    } else if (from.getOwner().getBenefitLevel() == BenefitLevel.GOLD) {
      return 0.1f;
    } else if (from.getOwner().getBenefitLevel() == BenefitLevel.PLATINUM) {
      return 0.05f;
    }
    throw new RuntimeException("Invalid benefit level");
  }

  /**
   * Repeats the transaction.
   */
  public void repeat() throws RuntimeException {
    try {
      Account.transfer(from, to, amount);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sends the transaction details to the sender.
   * 
   * @param type The type of the notification.
   */
  public void sendDetails(NotificationType type) throws RuntimeException {
    try {
      from.getOwner().notify(this.getDescription(), type);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
