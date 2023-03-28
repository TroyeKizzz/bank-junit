package com.troyekizzz.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.troyekizzz.app.utils.CardType;
import com.troyekizzz.app.utils.Currency;

public class BankTest {
  static Stream<Arguments> getConstructorValues() {
    return Stream.of(
      Arguments.of(100000, true),
      Arguments.of(100000, true),
      Arguments.of(-1000, false),
      Arguments.of(0, true)
    );
  }

  static Stream<Arguments> getAddRemoveCustomerValues() {
    return Stream.of(
      Arguments.of("John", "Smith", "john.smith@gmail.com", "+6789054321"),
      Arguments.of("John", "Doe", "john.doe@gmail.com", "+1234567890"),
      Arguments.of("Jane", "Doe", "jane.doe@gmail.com", "+0987654321")
    );
  }

  static Stream<Arguments> getAddRemoveCardValues() {
    return Stream.of(
      Arguments.of("John", "Smith", "john.smith@gmail.com", "+6789054321", Currency.EUR, CardType.DEBIT),
      Arguments.of("John", "Doe", "john.doe@gmail.com", "+1234567890", Currency.USD, CardType.DEBIT),
      Arguments.of("Jane", "Doe", "jane.doe@gmail.com", "+0987654321", Currency.GBP, CardType.DEBIT),
      Arguments.of("John", "Smith", "john.smith@gmail.com", "+6789054321", Currency.EUR, CardType.CREDIT),
      Arguments.of("John", "Doe", "john.doe@gmail.com", "+1234567890", Currency.USD, CardType.CREDIT),
      Arguments.of("Jane", "Doe", "jane.doe@gmail.com", "+0987654321", Currency.GBP, CardType.CREDIT)
    );
  }

  static Stream<Arguments> getAddRemoveAtmValues() {
    return Stream.of(
      Arguments.of(10000, "Tampere", 2000, 8000, true),
      Arguments.of(10000, "Helsinki", 3000, 7000, true),
      Arguments.of(10000, "Turku", 11000, -1, false),
      Arguments.of(10000, "Oulu", 0, 10000, true)
    );
  }

  @ParameterizedTest(name = "Test constructor with capital {0}")
  @MethodSource("getConstructorValues")
  public void testConstructor(float capital, boolean expected) {
    if (expected) {
      assertAll("Test bank constructor",
        () -> assertDoesNotThrow(() -> new Bank("Nordea", capital)),
        () -> assertEquals(capital, new Bank("Nordea", capital).getCapital())
      );
    } else {
      assertThrows(IllegalArgumentException.class, () -> new Bank("Nordea", capital));
    }
  }

  @ParameterizedTest(name = "Test add/remove customer {0} {1}")
  @MethodSource("getAddRemoveCustomerValues")
  public void testAddRemoveCustomer(String firstName, String lastName, String email, String phone) {
    Bank bank = new Bank("Nordea", 100000);
    Customer customer = bank.addCustomer(firstName, lastName, email, phone);
    assertEquals(0, bank.getCustomers().indexOf(customer));
    bank.removeCustomer(customer);
    assertAll("Test remove customer",
      () -> assertEquals(-1, bank.getCustomers().indexOf(customer)),
      () -> assertEquals(0, bank.getCustomers().size())
    );
  }

  @ParameterizedTest(name = "Test open/close account in {0}")
  @EnumSource(Currency.class)
  public void testOpenCloseAccount(Currency currency) {
    Bank bank = new Bank("Nordea", 100000);
    Customer customer = bank.addCustomer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    Account account = bank.openAccount(customer, currency);
    assertAll("Test",
      () -> assertEquals(0, bank.getAccounts().indexOf(account)),
      () -> assertDoesNotThrow(() -> {
        bank.closeAccount(account);
      }),
      () -> assertEquals(-1, bank.getAccounts().indexOf(account)),
      () -> assertEquals(0, bank.getAccounts().size())
    );
  }

  @ParameterizedTest(name = "Test add/remove {6} card in {5}")
  @MethodSource("getAddRemoveCardValues")
  public void testAddRemoveCard(String firstName, String lastName, String email, String phone, Currency currency, CardType cardType) {
    Bank bank = new Bank("Nordea", 100000);
    Customer customer = bank.addCustomer(firstName, lastName, email, phone);
    Account account = bank.openAccount(customer, currency);
    Card card = bank.addCard(account, cardType, "1234");
    assertAll("Test",
      () -> assertEquals(0, bank.getCards().indexOf(card)),
      () -> assertDoesNotThrow(() -> {
        bank.removeCard(card);
      }),
      () -> assertEquals(-1, bank.getCards().indexOf(card)),
      () -> assertEquals(0, bank.getCards().size())
    );
  }

  @ParameterizedTest(name = "Test add/remove atm in {1}")
  @MethodSource("getAddRemoveAtmValues")
  public void testAddRemoveAtm(int capital, String location, int balance, int expectedBalance, boolean expected) {
    Bank bank = new Bank("Nordea", capital);
    if (expected) {
      ATM atm = bank.addAtm(location, balance);
      assertAll("Test",
        () -> assertEquals(0, bank.getAtms().indexOf(atm)),
        () -> assertEquals(expectedBalance, bank.getCapital()),
        () -> assertDoesNotThrow(() -> {
          bank.removeAtm(atm);
        }),
        () -> assertEquals(-1, bank.getAtms().indexOf(atm)),
        () -> assertEquals(0, bank.getAtms().size()),
        () -> assertEquals(capital, bank.getCapital())
      );
    } else {
      assertThrows(IllegalArgumentException.class, () -> bank.addAtm(location, balance));
    }
  }
}
