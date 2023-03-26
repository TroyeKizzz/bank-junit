package com.troyekizzz.app;

import com.troyekizzz.app.utils.Currency;
import com.troyekizzz.app.utils.InvoiceStatus;

import lombok.Data;

/**
 * A class that represents an invoice.
 * 
 * @author TroyeKizzz
 */
@Data
public class Invoice {
  /**
   * The customer who sent the invoice.
   */
  private Customer from;

  /**
   * The customer who receives the invoice.
   */
  private Customer to;

  /**
   * The account of from which the invoice is paid.
   */
  private Account fromAccount = null;

  /**
   * The account of the customer who receives the invoice.
   */
  private Account toAccount;

  /**
   * The amount of the invoice.
   */
  private float amount;

  /**
   * The currency of the invoice.
   */
  private Currency currency;

  /**
   * The serial number of the invoice.
   */
  private String number;

  /**
   * The status of the invoice.
   */
  private InvoiceStatus status;

  /**
   * The tax percentage for the invoiced items.
   */
  private float taxPercentage;

  /**
   * Creates a new invoice.
   * 
   * @param from      The customer who sends the invoice.
   * @param to        The customer who receives the invoice.
   * @param toAccount The account of the customer who receives the invoice.
   * @param amount    The amount of the invoice.
   * @param currency  The currency of the invoice.
   */
  public Invoice(Customer from, Customer to, Account toAccount, float amount, Currency currency, float taxPercentage) {
    this.from = from;
    this.to = to;
    this.toAccount = toAccount;
    this.amount = amount;
    this.currency = currency;
    this.taxPercentage = taxPercentage;
    this.status = InvoiceStatus.UNACCEPTED;
    this.number = String.valueOf((int) (Math.random() * 1000000));
  }

  /**
   * Returns the amount of the tax.
   * 
   * @return The amount of the tax.
   */
  public float getTaxAmount() {
    return this.amount * this.taxPercentage;
  }

  /**
   * Returns the total amount of the invoice.
   * 
   * @return The total amount of the invoice.
   */
  public float getTotalAmount() {
    return this.amount + this.getTaxAmount();
  }

  /**
   * Accepts the invoice.
   * 
   * The invoice has to be previously unaccepted.
   * The customer has to have enough money on the account.
   * 
   * @param fromAccount The account of the customer who sends the invoice.
   */
  public void accept(Account fromAccount) {
    if (Exchange.getInstance().convert(fromAccount.getCurrency(), this.currency, fromAccount.getBalance()) < this.amount) 
      throw new IllegalArgumentException("Not enough money on the account.");
    if (this.status != InvoiceStatus.UNACCEPTED) 
      throw new IllegalArgumentException("The invoice is already accepted.");
    this.fromAccount = fromAccount;
    this.status = InvoiceStatus.FALLING_DUE;
  }

  /**
   * Rejects the invoice.
   * 
   * The invoice has to be previously unaccepted or accepted.
   */
  public void reject() {
    if (this.status == InvoiceStatus.REJECTED || this.status == InvoiceStatus.PAID)
      throw new IllegalArgumentException("The invoice is already rejected or paid.");
    this.status = InvoiceStatus.REJECTED;
  }

  /**
   * Pays the invoice, i.e. sends the money from the sender to the receiver.
   * 
   * The invoice has to be previously falling due.
   * The customer has to have enough money on the account.
   */
  public void pay() {
    if (this.status != InvoiceStatus.FALLING_DUE) 
      throw new IllegalArgumentException("The invoice is not falling due.");
    try {
      Account.transfer(fromAccount, toAccount, amount);
      this.status = InvoiceStatus.PAID;
    } catch (IllegalArgumentException e) {
      throw e;
    }
  }
}
