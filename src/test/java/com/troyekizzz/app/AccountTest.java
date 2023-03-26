package com.troyekizzz.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.troyekizzz.app.utils.Currency;

public class AccountTest {
  private static Customer customer = new Customer("John", "Doe", "john.doe@gmail.com", "+1234567890");
  private Account account = new Account(AccountTest.customer, Currency.EUR);

  static Stream<Arguments> getDepositValues() {
    return Stream.of(
      Arguments.of(0.0f, Currency.EUR, 0.0f),
      Arguments.of(1.0f, Currency.EUR, 1.0f),
      Arguments.of(100.0f, Currency.EUR, 100.0f),
      Arguments.of(0.0f, Currency.USD, 0.0f),
      Arguments.of(1.0f, Currency.USD, 0.9f),
      Arguments.of(100.0f, Currency.USD, 90.0f),
      Arguments.of(0.0f, Currency.GBP, 0.0f),
      Arguments.of(1.0f, Currency.GBP, 1.25f),
      Arguments.of(100.0f, Currency.GBP, 125.0f)
    );
  }

  static Stream<Arguments> getWithdrawValues() {
    return Stream.of(
      Arguments.of(0.0f, Currency.EUR, 1000.0f),
      Arguments.of(1.0f, Currency.EUR, 999.0f),
      Arguments.of(100.0f, Currency.EUR, 900.0f),
      Arguments.of(0.0f, Currency.USD, 1000.0f),
      Arguments.of(1.0f, Currency.USD, 999.1f),
      Arguments.of(100.0f, Currency.USD, 910.0f),
      Arguments.of(0.0f, Currency.GBP, 1000.0f),
      Arguments.of(1.0f, Currency.GBP, 998.75f),
      Arguments.of(100.0f, Currency.GBP, 875.0f)
    );
  }

  static Stream<Arguments> getTransferValues() {
    List<Arguments> values = new ArrayList<>();

    Account account1 = new Account(customer, Currency.EUR);
    Account account2 = new Account(customer, Currency.EUR);
    account1.deposit(1000.0f, Currency.EUR);
    account2.deposit(1000.0f, Currency.EUR);
    values.add(Arguments.of(account1, account2, 100.0f, 900.0f, 1100.0f));

    account1 = new Account(customer, Currency.USD);
    account2 = new Account(customer, Currency.EUR);
    account1.deposit(1000.0f, Currency.USD);
    account2.deposit(1000.0f, Currency.EUR);
    values.add(Arguments.of(account1, account2, 100.0f, 900.0f, 1090.0f));

    account1 = new Account(customer, Currency.GBP);
    account2 = new Account(customer, Currency.EUR);
    account1.deposit(1000.0f, Currency.GBP);
    account2.deposit(1000.0f, Currency.EUR);
    values.add(Arguments.of(account1, account2, 100.0f, 900.0f, 1125.0f));

    return values.stream();
  }

  @ParameterizedTest(name = "Test account constructor with {0} currency")
  @EnumSource(Currency.class)
  public void testConstructor(Currency currency) {
    Account account = new Account(customer, currency);
    assertAll("Account constructor tests", 
    () -> assertTrue(account.isOpen()), 
    () -> assertEquals(0, account.getBalance()),
    () -> assertEquals(currency, account.getCurrency()),
    () -> assertEquals(0, account.getInterestRate()),
    () -> assertEquals(customer, account.getOwner())
    );
  }

  @ParameterizedTest(name = "Test deposit method with {0} {1}")
  @MethodSource("getDepositValues")
  public void testDeposit(float amount, Currency currency, float result) {
    this.account.deposit(amount, currency);
    if (this.account.getBalance() != result) {
      System.out.println("Balance:");
      System.out.println(this.account.getBalance());
      System.out.println("Result:");
      System.out.println(result);
    }
    assertAll("Test deposit method", 
    () -> assertEquals(result, this.account.getBalance()),
    () -> assertEquals(Currency.EUR, this.account.getCurrency())
    );
  }

  @ParameterizedTest(name = "Test withdraw method with {0} {1}")
  @MethodSource("getWithdrawValues")
  public void testWithdraw(float amount, Currency currency, float result) {
    this.account.deposit(1000.0f, Currency.EUR);
    this.account.withdraw(amount, currency);
    assertAll("Test withdraw method", 
    () -> assertEquals(result, account.getBalance()),
    () -> assertEquals(Currency.EUR, account.getCurrency())
    );
  }

  @Test
  @DisplayName("Test close method with 0 balance")
  public void testCloseEmpty() {
    this.account.close();
    assertFalse(this.account.isOpen());
  }

  @Test
  @DisplayName("Test close method with 1000 balance")
  public void testCloseWithMoney() {
    this.account.deposit(1000.0f, Currency.EUR);
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      this.account.close();
    });
    assertEquals("The account has a positive balance.", exception.getMessage());
  }

  @Test
  @DisplayName("Test close method with already closed account")
  public void testCloseAlreadyClosed() {
    this.account.close();
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      this.account.close();
    });
    assertEquals("The account is already closed.", exception.getMessage());
  }

  @Test
  @DisplayName("Test addInterest method with 0 balance")
  public void testAddInterestEmpty() {
    this.account.addInterest();
    assertEquals(0, this.account.getBalance());
  }

  @Test
  @DisplayName("Test addInterest method with 1000 balance and 10% interest rate")
  public void testAddInterest() {
    this.account.deposit(1000.0f, Currency.EUR);
    this.account.setInterestRate(0.1f);
    this.account.addInterest();
    assertEquals(1100, this.account.getBalance());
  }

  @Test
  @DisplayName("Test addInterest method with closed account")
  public void testAddInterestClosed() {
    this.account.setInterestRate(0.1f);
    this.account.close();
    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      this.account.addInterest();
    });
    assertEquals("The account is closed.", exception.getMessage());
  }

  @ParameterizedTest(name = "Test transfer method with {2}")
  @MethodSource("getTransferValues")
  public void testTransfer(Account from, Account to, float amount, float fromResult, float toResult) {
    Account.transfer(from, to, amount);
    assertAll("Test transfer method", 
    () -> assertEquals(fromResult, from.getBalance()),
    () -> assertEquals(toResult, to.getBalance())
    );
  }

  @Test
  @DisplayName("Test transfer method with too much amount")
  public void testTransferTooMuch() {
    Account account1 = new Account(customer, Currency.EUR);
    Account account2 = new Account(customer, Currency.EUR);
    account1.deposit(1000.0f, Currency.EUR);
    account2.deposit(1000.0f, Currency.EUR);
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
      Account.transfer(account1, account2, 2000.0f);
    });
    assertAll("Test transfer too much amount", 
    () -> assertEquals("The amount is greater than the balance.", exception.getMessage()),
    () -> assertEquals(1000.0f, account1.getBalance()),
    () -> assertEquals(1000.0f, account2.getBalance())
    );
  }
}
