package com.troyekizzz.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.troyekizzz.app.utils.CardType;
import com.troyekizzz.app.utils.Currency;
import com.troyekizzz.app.utils.NotificationType;

public class ATMTest {
  static Stream<Arguments> getDeactivateValues() {
    return Stream.of(
      Arguments.of(10000, 2000, 8000),
      Arguments.of(10000, 1000, 9000),
      Arguments.of(8000, 0, 8000),
      Arguments.of(5000, 5000, 0)
    );
  }

  static Stream<Arguments> getWithdrawCashValues() {
    return Stream.of(
      Arguments.of(10000, 2000, 8000, "1111", "1111", true),
      Arguments.of(10000, 1000, 9000, "1111", "1111", true),
      Arguments.of(5000, 5000, 0, "1111", "1111", true),
      Arguments.of(5000, 5001, 5000, "1111", "1111", false),
      Arguments.of(5000, 0, 5000, "1111", "1111", false),
      Arguments.of(10000, 2000, 10000, "1111", "2222", false),
      Arguments.of(10000, 1000, 10000, "1111", "2222", false),
      Arguments.of(5000, 5000, 5000, "1111", "2222", false)
    );
  }

  static Stream<Arguments> getDepositCashValues() {
    return Stream.of(
      Arguments.of(10000, 2000, 12000, "1111", "1111", true),
      Arguments.of(10000, 1000, 11000, "1111", "1111", true),
      Arguments.of(5000, 5000, 10000, "1111", "1111", true),
      Arguments.of(10000, -1, 10000, "1111", "1111", false),
      Arguments.of(10000, 0, 10000, "1111", "1111", false),
      Arguments.of(5000, 5000, 5000, "1111", "2222", false)
    );
  }

  static Stream<Arguments> getCheckBalanceValues() {
    return Stream.of(
      Arguments.of(100, Currency.USD, "1111", "1111", "Your balance is 100.0 USD", true),
      Arguments.of(100, Currency.EUR, "1111", "1111", "Your balance is 100.0 EUR", true),
      Arguments.of(100, Currency.GBP, "1111", "1111", "Your balance is 100.0 GBP", true),
      Arguments.of(100, Currency.USD, "1111", "2222", "", false),
      Arguments.of(100, Currency.EUR, "1111", "2222", "", false),
      Arguments.of(100, Currency.GBP, "1111", "2222", "", false)
    );
  }

  @ParameterizedTest
  @MethodSource("getDeactivateValues")
  public void testDeactivate(float capital, float balance, float expectedCapital) {
    Bank bank = new Bank("Test Bank", capital);
    ATM atm = bank.addAtm("Test location", balance);
    assertAll("Test ATM deactivate",
      () -> assertEquals(balance, atm.getBalance()),
      () -> assertEquals(expectedCapital, bank.getCapital()),
      () -> assertEquals(balance, atm.deactivate()),
      () -> assertEquals(0, atm.getBalance()),
      () -> assertFalse(atm.isActive()),
      () -> assertThrows(IllegalStateException.class, () -> atm.deactivate())
    );
  }

  @ParameterizedTest
  @MethodSource("getWithdrawCashValues")
  public void testWithdrawCash(float balance, float amount, float expectedBalance, String correctPin, String testPin, boolean expected) {
    Bank bank = new Bank("Test Bank", 100000);
    ATM atm = bank.addAtm("Test location", balance);
    Customer customer = bank.addCustomer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    Account account = bank.openAccount(customer, Currency.USD);
    account.deposit(10000, Currency.USD);
    Card card = bank.addCard(account, CardType.CREDIT, correctPin);
    if (expected) {
      assertAll("Test ATM withdraw cash",
        () -> assertDoesNotThrow(() -> atm.withdrawCash(card, amount, Currency.USD, testPin)),
        () -> assertEquals(expectedBalance, atm.getBalance())
      );
    } else {
      assertAll("Test ATM withdraw cash",
        () -> assertThrows(IllegalStateException.class, () -> atm.withdrawCash(card, amount, Currency.USD, testPin)),
        () -> assertEquals(expectedBalance, atm.getBalance()));
    }
  }

  @ParameterizedTest
  @MethodSource("getDepositCashValues")
  public void testDepositCash(float balance, float amount, float expectedBalance, String correctPin, String testPin, boolean expected) {
    Bank bank = new Bank("Test Bank", 100000);
    ATM atm = bank.addAtm("Test location", balance);
    Customer customer = bank.addCustomer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    Account account = bank.openAccount(customer, Currency.USD);
    account.deposit(10000, Currency.USD);
    Card card = bank.addCard(account, CardType.CREDIT, correctPin);
    if (expected) {
      assertAll("Test ATM withdraw cash",
        () -> assertDoesNotThrow(() -> atm.depositCash(card, amount, Currency.USD, testPin)),
        () -> assertEquals(expectedBalance, atm.getBalance())
      );
    } else {
      assertAll("Test ATM withdraw cash",
        () -> assertThrows(IllegalStateException.class, () -> atm.depositCash(card, amount, Currency.USD, testPin)),
        () -> assertEquals(expectedBalance, atm.getBalance()));
    }
  }

  @ParameterizedTest(name = "Test ATM check balance: {0}")
  @MethodSource("getCheckBalanceValues")
  public void testCheckBalance(float amount, Currency currency, String correctPin, String testPin, String message, boolean expected) {
    Bank bank = new Bank("Test Bank", 100000);
    ATM atm = bank.addAtm("Test location", 1000);
    Customer customer = bank.addCustomer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    Account account = bank.openAccount(customer, currency);
    account.deposit(amount, currency);
    Card card = bank.addCard(account, CardType.DEBIT, correctPin);
    if (expected) {
      assertAll("Test ATM check balance",
        () -> assertEquals(message, atm.checkBalance(card, testPin)),
        () -> assertEquals(amount, card.getAccount().getBalance())
      );
    } else {
      assertAll("Test ATM check balance",
        () -> assertThrows(IllegalStateException.class, () -> atm.checkBalance(card, testPin)),
        () -> assertEquals(amount, card.getAccount().getBalance()));
    }
  }

  @Test
  public void testDisplayMessageWrongPin() {
    Bank bank = new Bank("Test Bank", 100000);
    ATM atm = bank.addAtm("Test location", 1000);
    Customer customer = bank.addCustomer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    Account account = bank.openAccount(customer, Currency.USD);
    Card card = bank.addCard(account, CardType.DEBIT, "1111");
    assertThrows(IllegalStateException.class, () -> atm.displayMessage(card, "2222"));
  }

  @Test
  public void testDisplayMessageCorrectPin() {
    Bank bank = new Bank("Test Bank", 100000);
    ATM atm = bank.addAtm("Test location", 1000);
    Customer customer1 = bank.addCustomer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    Customer customer2 = bank.addCustomer("Grocery Shop", "H-Market", "info@h-market.fi", "+358 123 456 789");
    Account account1 = bank.openAccount(customer1, Currency.USD);
    bank.openAccount(customer2, Currency.USD);
    Card card = bank.addCard(account1, CardType.DEBIT, "1111");
    account1.deposit(1000, Currency.USD);
    Transaction transaction = card.processPurchase(100, Currency.USD, "1111", customer2);
    transaction.sendDetails(NotificationType.EMAIL);;
    assertEquals("Purchase of goods from Grocery Shop H-Market in amount of 100.0 USD", atm.displayMessage(card, "1111"));
  }

  @Test
  public void testDisplayMessageNoMessages() {
    Bank bank = new Bank("Test Bank", 100000);
    ATM atm = bank.addAtm("Test location", 1000);
    Customer customer= bank.addCustomer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    Account account = bank.openAccount(customer, Currency.USD);
    bank.openAccount(customer, Currency.USD);
    Card card = bank.addCard(account, CardType.DEBIT, "1111");
    assertEquals("No messages", atm.displayMessage(card, "1111"));
  }
}
