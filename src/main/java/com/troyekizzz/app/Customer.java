package com.troyekizzz.app;

import java.util.ArrayList;
import java.util.List;

import com.troyekizzz.app.utils.BenefitLevel;
import com.troyekizzz.app.utils.Currency;
import com.troyekizzz.app.utils.NotificationType;

import lombok.Data;

/**
 * A class that represents a customer.
 *
 * @author TroyeKizzz
 */
@Data
public class Customer {
  /**
   * The customer's first name.
   */
  private String firstName;

  /**
   * The customer's last name.
   */
  private String lastName;

  /**
   * The customer's email.
   */
  private String email;

  /**
   * The customer's phone number.
   */
  private String phoneNumber;

  /**
   * Bank accounts of the customer.
   */
  private List<Account> bankAccounts = new ArrayList<>();

  /**
   * Messages sent to the customer.
   */
  private List<String> messages = new ArrayList<>();

  /**
   * Creates a new customer.
   *
   * @param firstName  The customer's first name.
   * @param lastName   The customer's last name.
   * @param email      The customer's email.
   * @param phoneNumber The customer's phone number.
   */
  public Customer(String firstName, String lastName, String email, String phoneNumber) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phoneNumber = phoneNumber;
  }

  /**
   * Returns the total balance of the customer's accounts.
   * 
   * @return The total balance of the customer's accounts.
   */
  public float getTotalBalance(Currency currency) {
    float totalBalance = 0;
    for (Account account : bankAccounts) {
      totalBalance += Exchange.convert(account.getCurrency(), currency, account.getBalance());
    }
    return totalBalance;
  }

  /**
   * Returns the benefit level of the customer.
   * 
   * The benefit level is determined by the total balance of the customer's accounts.
   * 
   * If the total balance is less than 1000, the benefit level is SILVER.
   * If the total balance is less than 10000, the benefit level is GOLD.
   * If the total balance is 10000 or more, the benefit level is PLATINUM.
   *
   * @return The benefit level of the customer.
   */
  public BenefitLevel getBenefitLevel() {
    float totalBalance = this.getTotalBalance(Currency.EUR);
    if (totalBalance < 1000) {
      return BenefitLevel.SILVER;
    } else if (totalBalance < 10000) {
      return BenefitLevel.GOLD;
    } else {
      return BenefitLevel.PLATINUM;
    }
  }

  /**
   * Returns the string representation of the customer.
   * 
   * @return The string representation of the customer.
   */
  @Override
  public String toString() {
    return "Customer [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", phoneNumber="
        + phoneNumber + "]";
  }

  /**
   * Sends a message to the customer.
   * 
   * The message is sent by email if the customer has an email address.
   * The message is sent by SMS if the customer has a phone number.
   * 
   * @param message The message to send.
   */
  public void notify(String message, NotificationType type) throws IllegalStateException {
    if (type == NotificationType.EMAIL && (this.email == null || this.email.isEmpty()))
      throw new IllegalStateException("Email is not set for the customer.");
    if (type == NotificationType.SMS && (this.phoneNumber == null || this.phoneNumber.isEmpty()))
      throw new IllegalStateException("Phone number is not set for the customer.");

    messages.add(message);
  }
}
