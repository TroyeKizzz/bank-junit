package com.troyekizzz.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.troyekizzz.app.utils.Currency;
import com.troyekizzz.app.utils.InvoiceStatus;

public class InvoiceTest {
  private Customer customer1 = new Customer("John", "Doe", "john.doe@gmail.com", "+1234567890");
  private Customer customer2 = new Customer("Jane", "Doe", "jane.doe@gmail.com", "+0987654321");
  private Account account = new Account(customer1, Currency.EUR);
  
  static Stream<Arguments> getTaxAmountValues() {
    return Stream.of(
      Arguments.of(100.0f, 0.0f, 0.0f),
      Arguments.of(100.0f, 0.1f, 10.0f),
      Arguments.of(300.0f, 0.2f, 60.0f),
      Arguments.of(300.0f, 0.3f, 90.0f)
    );
  }

  static Stream<Arguments> getTotalAmountValues() {
    return Stream.of(
      Arguments.of(100.0f, 0.0f, 100.0f),
      Arguments.of(100.0f, 0.1f, 110.0f),
      Arguments.of(300.0f, 0.2f, 360.0f),
      Arguments.of(300.0f, 0.3f, 390.0f)
    );
  }

  @ParameterizedTest(name = "Test taxAmount method with {0} amount and {1} tax")
  @MethodSource("getTaxAmountValues")
  public void testTaxAmount(float amount, float taxPercentage, float taxAmount) {
    Invoice invoice = new Invoice(customer1, customer2, account, amount, Currency.GBP, taxPercentage);
    assertEquals(taxAmount, invoice.getTaxAmount());
  }

  @ParameterizedTest(name = "Test totalAmount method with {0} amount and {1} tax")
  @MethodSource("getTotalAmountValues")
  public void testTotalAmount(float amount, float taxPercentage, float totalAmount) {
    Invoice invoice = new Invoice(customer1, customer2, account, amount, Currency.GBP, taxPercentage);
    assertEquals(totalAmount, invoice.getTotalAmount());
  }

  @Test
  public void testAcceptNormal() {
    Invoice invoice = new Invoice(customer1, customer2, account, 100.0f, Currency.GBP, 0.1f);
    Account accountFrom = new Account(customer2, Currency.EUR);
    accountFrom.deposit(1000, Currency.GBP);
    invoice.accept(accountFrom);
    assertEquals(InvoiceStatus.FALLING_DUE, invoice.getStatus());
  }

  @Test
  public void testAcceptTwice() {
    Invoice invoice = new Invoice(customer1, customer2, account, 100.0f, Currency.GBP, 0.1f);
    Account accountFrom = new Account(customer2, Currency.EUR);
    accountFrom.deposit(1000, Currency.GBP);
    invoice.accept(accountFrom);
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
      invoice.accept(accountFrom);
    });
    assertEquals("The invoice is already accepted.", exception.getMessage());
  }

  @Test
  public void testAcceptNotEnoughMoney() {
    Invoice invoice = new Invoice(customer1, customer2, account, 100.0f, Currency.GBP, 0.1f);
    Account accountFrom = new Account(customer2, Currency.EUR);
    accountFrom.deposit(10, Currency.GBP);
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
      invoice.accept(accountFrom);
    });
    assertEquals("Not enough money on the account.", exception.getMessage());
  }

  @Test
  public void testRejectNormal() {
    Invoice invoice = new Invoice(customer1, customer2, account, 100.0f, Currency.GBP, 0.1f);
    invoice.reject();
    assertEquals(InvoiceStatus.REJECTED, invoice.getStatus());
  }

  @Test
  public void testRejectTwice() {
    Invoice invoice = new Invoice(customer1, customer2, account, 100.0f, Currency.GBP, 0.1f);
    invoice.reject();
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
      invoice.reject();
    });
    assertEquals("The invoice is already rejected or paid.", exception.getMessage());
  }

  @Test
  public void testRejectPaid() {
    Invoice invoice = new Invoice(customer1, customer2, account, 100.0f, Currency.GBP, 0.1f);
    Account accountFrom = new Account(customer2, Currency.EUR);
    accountFrom.deposit(1000, Currency.GBP);
    invoice.accept(accountFrom);
    invoice.pay();
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
      invoice.reject();
    });
    assertEquals("The invoice is already rejected or paid.", exception.getMessage());
  }

  @Test
  public void testPayNormal() {
    Invoice invoice = new Invoice(customer1, customer2, account, 100.0f, Currency.GBP, 0.1f);
    Account accountFrom = new Account(customer2, Currency.EUR);
    accountFrom.deposit(1000, Currency.GBP);
    invoice.accept(accountFrom);
    invoice.pay();
    assertEquals(InvoiceStatus.PAID, invoice.getStatus());
  }

  @Test
  public void testPayRejected() {
    Invoice invoice = new Invoice(customer1, customer2, account, 100.0f, Currency.GBP, 0.1f);
    invoice.reject();
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
      invoice.pay();
    });
    assertEquals("The invoice is not falling due.", exception.getMessage());
  }

  @Test
  public void testPayNotEnoughMoney() {
    Invoice invoice = new Invoice(customer1, customer2, account, 100.0f, Currency.GBP, 0.1f);
    Account accountFrom = new Account(customer2, Currency.EUR);
    accountFrom.deposit(1000, Currency.GBP);
    invoice.accept(accountFrom);
    accountFrom.withdraw(950, Currency.GBP);
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
      invoice.pay();
    });
    assertEquals("The amount is greater than the balance.", exception.getMessage());
  }
}
