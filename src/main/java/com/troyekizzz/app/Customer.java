package com.troyekizzz.app;

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
  public String firstName;

  /**
   * The customer's last name.
   */
  public String lastName;

  /**
   * The customer's email.
   */
  public String email;

  /**
   * The customer's phone number.
   */
  public String phoneNumber;

  /**
   * Bank accounts of the customer.
   */
  public Account[] bankAccounts;
}
