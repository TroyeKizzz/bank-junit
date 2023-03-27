package com.troyekizzz.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.troyekizzz.app.utils.BenefitLevel;
import com.troyekizzz.app.utils.Currency;
import com.troyekizzz.app.utils.NotificationType;

public class CustomerTest {
  static Stream<Arguments> getConstructorValues() {
    return Stream.of(
      Arguments.of("John", "Doe", "john.doe@gmail.com", "+1234567890"),
      Arguments.of("Jane", "Doe", "jane.doe@gmail.com", "+0987654321"),
      Arguments.of("John", "Smith", "john.smith@gmail.com", "+6789054321")
    );
  }

  static Stream<Arguments> getTotalBalanceValues() {
    List<Arguments> values = new ArrayList<>();

    Customer customer = new Customer("John", "Doe", "john.doe@gmail.com", "+1234567890");
    Account account1 = new Account(customer, Currency.EUR);
    account1.deposit(1000, Currency.EUR);
    Account account2 = new Account(customer, Currency.USD);
    account2.deposit(2000, Currency.USD);
    Account account3 = new Account(customer, Currency.GBP);
    account3.deposit(3000, Currency.GBP);
    values.add(Arguments.of(customer, Currency.EUR, 6550.0f));

    customer = new Customer("Jane", "Doe", "jane.doe@gmail.com", "+0987654321");
    account1 = new Account(customer, Currency.EUR);
    account1.deposit(3000, Currency.EUR);
    account2 = new Account(customer, Currency.USD);
    account2.deposit(5000, Currency.USD);
    values.add(Arguments.of(customer, Currency.USD, 8300.0f));

    customer = new Customer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    account1 = new Account(customer, Currency.EUR);
    account1.deposit(1000, Currency.EUR);
    account2 = new Account(customer, Currency.USD);
    values.add(Arguments.of(customer, Currency.GBP, 800.0f));

    return values.stream();
  }

  static Stream<Arguments> getBenefitLevelValues() {
    List<Arguments> values = new ArrayList<>();

    Customer customer = new Customer("John", "Doe", "john.doe@gmail.com", "+1234567890");
    Account account1 = new Account(customer, Currency.EUR);
    account1.deposit(1000, Currency.EUR);
    Account account2 = new Account(customer, Currency.USD);
    account2.deposit(2000, Currency.USD);
    Account account3 = new Account(customer, Currency.GBP);
    account3.deposit(3000, Currency.GBP);
    // 6550 EUR
    values.add(Arguments.of(customer, BenefitLevel.GOLD));

    customer = new Customer("Jane", "Doe", "jane.doe@gmail.com", "+0987654321");
    account1 = new Account(customer, Currency.EUR);
    account1.deposit(3000, Currency.EUR);
    account2 = new Account(customer, Currency.USD);
    account2.deposit(5000, Currency.USD);
    account3 = new Account(customer, Currency.GBP);
    account3.deposit(3000, Currency.GBP);
    // 12440 EUR
    values.add(Arguments.of(customer, BenefitLevel.PLATINUM));

    customer = new Customer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    account1 = new Account(customer, Currency.EUR);
    account1.deposit(800, Currency.EUR);
    account2 = new Account(customer, Currency.USD);
    // 800 EUR
    values.add(Arguments.of(customer, BenefitLevel.SILVER));

    return values.stream();
  }

  @ParameterizedTest(name = "Test Customer constructor for {0} {1}")
  @MethodSource("getConstructorValues")
  public void testConstructor(String firstName, String lastName, String email, String phoneNumber) {
    Customer customer = new Customer(firstName, lastName, email, phoneNumber);
    assertAll("Test Customer constructor",
      () -> assertEquals(firstName, customer.getFirstName()),
      () -> assertEquals(lastName, customer.getLastName()),
      () -> assertEquals(email, customer.getEmail()),
      () -> assertEquals(phoneNumber, customer.getPhoneNumber()),
      () -> assertEquals(0, customer.getBankAccounts().size())
    );
  }

  @ParameterizedTest(name = "Test getTotalBalance method with currency {1}")
  @MethodSource("getTotalBalanceValues")
  public void testGetTotalBalance(Customer customer, Currency currency, float expected) {
    assertEquals(expected, customer.getTotalBalance(currency));
  }

  @ParameterizedTest(name = "Test getBenefitLevel method for {0}")
  @MethodSource("getBenefitLevelValues")
  public void testGetBenefitLevel(Customer customer, BenefitLevel expected) {
    assertEquals(expected, customer.getBenefitLevel());
  }

  @ParameterizedTest(name = "Test toString method for {0} {1}")
  @MethodSource("getConstructorValues")
  public void testToString(String firstName, String lastName, String email, String phoneNumber) {
    Customer customer = new Customer(firstName, lastName, email, phoneNumber);
    assertAll("Test toString method",
      () -> assertTrue(customer.toString().length() > 0),
      () -> assertTrue(customer.toString().contains(firstName)),
      () -> assertTrue(customer.toString().contains(lastName)),
      () -> assertTrue(customer.toString().contains(email)),
      () -> assertTrue(customer.toString().contains(phoneNumber)),
      () -> assertTrue(customer.toString().startsWith("Customer [firstName=")),
      () -> assertTrue(customer.toString().endsWith("]"))
    );
  }

  @Test
  @DisplayName("Test notify method with EMAIL")
  public void testNotifyByEmail() {
    Customer customer = new Customer("John", "Doe", "john.doe@gmail.com", "+1234567890");
    customer.notify("Test message", NotificationType.EMAIL);
    assertTrue(customer.getMessages().contains("Test message"));
  }

  @Test
  @DisplayName("Test notify method with SMS")
  public void testNotifyBySms() {
    Customer customer = new Customer("John", "Doe", "john.doe@gmail.com", "+1234567890");
    customer.notify("Test message", NotificationType.SMS);
    assertTrue(customer.getMessages().contains("Test message"));
  }

  @Test
  @DisplayName("Test notify method without email")
  public void testNotifyWithoutEmail() {
    Customer customer = new Customer("John", "Doe", "", "+1234567890");
    Throwable exception = assertThrows(IllegalStateException.class, () -> customer.notify("Test message", NotificationType.EMAIL));
    assertEquals("Email is not set for the customer.", exception.getMessage());
  }
}
