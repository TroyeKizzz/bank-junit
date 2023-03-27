package com.troyekizzz.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.troyekizzz.app.utils.Currency;
import com.troyekizzz.app.utils.NotificationType;

public class TransactionTest {
  static private ArrayList<Customer> getCustomers() {
    Customer customerGold = new Customer("John", "Doe", "john.doe@gmail.com", "+1234567890");
    Account account1 = new Account(customerGold, Currency.EUR);
    account1.deposit(1000, Currency.EUR);
    Account account2 = new Account(customerGold, Currency.USD);
    account2.deposit(2000, Currency.USD);
    Account account3 = new Account(customerGold, Currency.GBP);
    account3.deposit(3000, Currency.GBP);

    Customer customerPlatinum = new Customer("Jane", "Doe", "jane.doe@gmail.com", "+0987654321");
    account1 = new Account(customerPlatinum, Currency.EUR);
    account1.deposit(3000, Currency.EUR);
    account2 = new Account(customerPlatinum, Currency.USD);
    account2.deposit(5000, Currency.USD);
    account3 = new Account(customerPlatinum, Currency.GBP);
    account3.deposit(3000, Currency.GBP);

    Customer customerSilver = new Customer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    account1 = new Account(customerSilver, Currency.EUR);
    account1.deposit(800, Currency.EUR);
    account2 = new Account(customerSilver, Currency.USD);

    ArrayList<Customer> customers = new ArrayList<>();
    customers.add(customerSilver);
    customers.add(customerGold);
    customers.add(customerPlatinum);
    return customers;
  }

  static Stream<Arguments> getCalculateFeeValues() {
    Customer customerSilver = getCustomers().get(0);
    Customer customerGold = getCustomers().get(1);
    Customer customerPlatinum = getCustomers().get(2);

    return Stream.of(
      Arguments.of(customerSilver, customerSilver, 1000.0f, 20.0f),
      Arguments.of(customerGold, customerSilver, 1000.0f, 10.0f),
      Arguments.of(customerPlatinum, customerSilver, 1000.0f, 0.0f),
      Arguments.of(customerSilver, customerGold, 2000.0f, 40.0f),
      Arguments.of(customerGold, customerGold, 2000.0f, 20.0f),
      Arguments.of(customerPlatinum, customerGold, 2000.0f, 0.0f)
    );
  }

  static Stream<Arguments> getCheckFraudStatusValues() {
    Customer customerSilver = getCustomers().get(0);
    Customer customerGold = getCustomers().get(1);
    Customer customerPlatinum = getCustomers().get(2);

    return Stream.of(
      Arguments.of(customerSilver, customerSilver, 200.0f, false),
      Arguments.of(customerGold, customerSilver, 5000.0f, false),
      Arguments.of(customerPlatinum, customerSilver, 10000.0f, false),
      Arguments.of(customerSilver, customerGold, 1000.0f, true),
      Arguments.of(customerGold, customerGold, 10000.0f, true),
      Arguments.of(customerPlatinum, customerGold, 50000.0f, true)
    );
  }

  static Stream<Arguments> getInterestRateValues() {
    Customer customerSilver = getCustomers().get(0);
    Customer customerGold = getCustomers().get(1);
    Customer customerPlatinum = getCustomers().get(2);

    return Stream.of(
      Arguments.of(customerSilver, customerSilver, 0.15f),
      Arguments.of(customerGold, customerSilver, 0.10f),
      Arguments.of(customerPlatinum, customerSilver, 0.05f)
    );
  }

  static Stream<Arguments> getRepeatValues() {
    Customer customerSilver = getCustomers().get(0);
    Customer customerGold = getCustomers().get(1);
    Customer customerPlatinum = getCustomers().get(2);

    return Stream.of(
      Arguments.of(customerSilver, customerGold, 300.0f, 200.0f, 1600.0f),
      Arguments.of(customerGold, customerSilver, 500.0f, 600.0f, 1200.0f),
      Arguments.of(customerPlatinum, customerSilver, 1000.0f, 1000.0f, 3200.0f)
    );
  }

  @ParameterizedTest(name = "Test calculate fee for {3} EUR")
  @MethodSource("getCalculateFeeValues")
  public void testCalculateFee(Customer customerFrom, Customer customerTo, float amount, float expectedFee) {
    Transaction transaction = new Transaction(customerFrom.getBankAccounts().get(0), customerTo.getBankAccounts().get(0), amount, Currency.EUR, "Test");
    float fee = transaction.calculateFee();
    assertEquals(expectedFee, fee);
  }

  @ParameterizedTest(name = "Test {3} check fraud status for {2} EUR")
  @MethodSource("getCheckFraudStatusValues")
  public void testCheckFraudStatus(Customer customerFrom, Customer customerTo, float amount, boolean expectedFraudStatus) {
    Transaction transaction = new Transaction(customerFrom.getBankAccounts().get(0), customerTo.getBankAccounts().get(0), amount, Currency.EUR, "Test");
    assertEquals(expectedFraudStatus, transaction.checkFraudStatus());
  }

  @ParameterizedTest(name = "Test interest rate {2}")
  @MethodSource("getInterestRateValues")
  public void testInterestRate(Customer customerFrom, Customer customerTo, float expectedInterestRate) {
    Transaction transaction = new Transaction(customerFrom.getBankAccounts().get(0), customerTo.getBankAccounts().get(0), 1000.0f, Currency.EUR, "Test");
    assertEquals(expectedInterestRate, transaction.getInterestRate());
  }

  @ParameterizedTest(name = "Test repeat {2}")
  @MethodSource("getRepeatValues")
  public void testRepeat(Customer customerFrom, Customer customerTo, float amount, float expectedBalanceFrom, float expectedBalanceTo) {
    Account accountFrom = customerFrom.getBankAccounts().get(0);
    Account accountTo = customerTo.getBankAccounts().get(0);
    Transaction transaction = Account.transfer(accountFrom, accountTo, amount);
    transaction.repeat();
    assertAll("Test transaction repeat",
      () -> assertEquals(expectedBalanceFrom, accountFrom.getBalance()),
      () -> assertEquals(expectedBalanceTo, accountTo.getBalance())
    );
  }

  @Test
  public void testSendDetailsByEmail() {
    Customer customerFrom = getCustomers().get(2);
    Customer customerTo = getCustomers().get(1);
    Account accountFrom = customerFrom.getBankAccounts().get(0);
    Account accountTo = customerTo.getBankAccounts().get(0);
    Transaction transaction = Account.transfer(accountFrom, accountTo, 1000.0f);
    transaction.sendDetails(NotificationType.EMAIL);
    String message = customerFrom.getMessages().get(0);
    assertTrue(message.equals("Money transfer from " + accountFrom.getNumber() + " to " + accountTo.getNumber() + ", in total 1000.0 EUR."));
  }

  @Test
  public void testSendDetailsBySMS() {
    Customer customerFrom = getCustomers().get(2);
    Customer customerTo = getCustomers().get(1);
    Account accountFrom = customerFrom.getBankAccounts().get(0);
    Account accountTo = customerTo.getBankAccounts().get(0);
    Transaction transaction = Account.transfer(accountFrom, accountTo, 1000.0f);
    transaction.sendDetails(NotificationType.SMS);
    String message = customerFrom.getMessages().get(0);
    assertTrue(message.equals("Money transfer from " + accountFrom.getNumber() + " to " + accountTo.getNumber() + ", in total 1000.0 EUR."));
  }

  @Test
  public void testSendDetailsWithoutEmail() {
    Customer customerFrom = new Customer("John", "Doe", "", "+1234567890");
    Account accountFrom = new Account(customerFrom, Currency.EUR);
    accountFrom.deposit(1000, Currency.EUR);

    Customer customerTo = getCustomers().get(1);
    Account accountTo = customerTo.getBankAccounts().get(0);
    Transaction transaction = Account.transfer(accountFrom, accountTo, 1000.0f);
    Throwable exception = assertThrows(RuntimeException.class, () -> {
      transaction.sendDetails(NotificationType.EMAIL);
    });
    assertEquals("java.lang.IllegalStateException: Email is not set for the customer.", exception.getMessage());
  }
}
