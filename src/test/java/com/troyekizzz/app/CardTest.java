package com.troyekizzz.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.troyekizzz.app.utils.BenefitLevel;
import com.troyekizzz.app.utils.CardType;
import com.troyekizzz.app.utils.Currency;

public class CardTest {
  static private Customer getShop() {
    Customer shop = new Customer("Grocery Shop", "H-Market", "info@h-market.fi", "+358 123 456 789");
    new Account(shop, Currency.EUR);
    return shop;
  }

  static Stream<Arguments> getValidatePinValues() {
    return Stream.of(
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(0), "1212"), "1212", true),
      Arguments.of(new Card(CardType.CREDIT, new Date(), getAccounts().get(3), "1234"), "1234", true),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(6), "1331"), "3113", false),
      Arguments.of(new Card(CardType.CREDIT, new Date(), getAccounts().get(7), "0000"), "", false),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(8), "1111"), null, false)
    );
  }

  static Stream<Arguments> getBenefitLevelValues() {
    return Stream.of(
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(0), "1212"), BenefitLevel.SILVER),
      Arguments.of(new Card(CardType.CREDIT, new Date(), getAccounts().get(3), "1234"), BenefitLevel.GOLD),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(6), "1331"), BenefitLevel.PLATINUM),
      Arguments.of(new Card(CardType.CREDIT, new Date(), getAccounts().get(7), "0000"), BenefitLevel.PLATINUM),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(1), "1111"), BenefitLevel.SILVER)
    );
  }

  static Stream<Arguments> getProcessPurchaseValues() {
    return Stream.of(
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(0), "1212"), 100.0f, Currency.EUR, "1212", 700.0f, true),
      Arguments.of(new Card(CardType.CREDIT, new Date(), getAccounts().get(4), "1234"), 100.0f, Currency.GBP, "1212", 2000.0f, false),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(6), "1331"), 0.0f, Currency.GBP, "1331", 3000.0f, false),
      Arguments.of(new Card(CardType.CREDIT, new Date(), getAccounts().get(7), "0000"), 0.0f, Currency.EUR, "1212", 5000.0f, false),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(1), "1212"), 100.0f, Currency.USD, "1212", 0.0f, false),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(5), "1234"), 100.0f, Currency.USD, "1234", 2928.0f, true)
    );
  }

  static Stream<Arguments> getLimitValues() {
    return Stream.of(
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(0), "1212"), 100.0f, Currency.EUR, 200.0f, "1212", 700.0f, true),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(0), "1212"), 100.0f, Currency.EUR, 50.0f, "1212", 800.0f, false),
      Arguments.of(new Card(CardType.CREDIT, new Date(), getAccounts().get(4), "1234"), 100.0f, Currency.GBP, 1000.0f, "1212", 2000.0f, false),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(6), "1331"), 150.0f, Currency.GBP, 500.0f, "1331", 2812.5f, true),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(6), "1331"), 150.0f, Currency.GBP, 100.0f, "1331", 3000.0f, false),
      Arguments.of(new Card(CardType.CREDIT, new Date(), getAccounts().get(7), "0000"), 0.0f, Currency.EUR, 1000.0f, "1212", 5000.0f, false),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(1), "1212"), 100.0f, Currency.USD, 500.0f, "1212", 0.0f, false),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(5), "1234"), 100.0f, Currency.USD, 400.0f, "1234", 2928.0f, true),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(5), "1234"), 100.0f, Currency.USD, 50.0f, "1234", 3000.0f, false),
      Arguments.of(new Card(CardType.CREDIT, new Date(), getAccounts().get(0), "1212"), 100.0f, Currency.EUR, 0.0f, "1212", 700.0f, true),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(6), "1331"), 150.0f, Currency.GBP, 0.0f, "1331", 2812.5f, true),
      Arguments.of(new Card(CardType.DEBIT, new Date(), getAccounts().get(5), "1234"), 100.0f, Currency.USD, 0.0f, "1234", 2928.0f, true)
    );
  }

  static private ArrayList<Account> getAccounts() {
    ArrayList<Account> accounts = new ArrayList<>();

    Customer customerSilver = new Customer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    Account account1 = new Account(customerSilver, Currency.EUR);
    account1.deposit(800, Currency.EUR);
    Account account2 = new Account(customerSilver, Currency.USD);
    Account account3 = new Account(customerSilver, Currency.USD);

    accounts.add(account1);
    accounts.add(account2);
    accounts.add(account3);

    Customer customerGold = new Customer("John", "Doe", "john.doe@gmail.com", "+1234567890");
    account1 = new Account(customerGold, Currency.EUR);
    account1.deposit(1000, Currency.EUR);
    account2 = new Account(customerGold, Currency.USD);
    account2.deposit(2000, Currency.USD);
    account3 = new Account(customerGold, Currency.GBP);
    account3.deposit(3000, Currency.GBP);

    accounts.add(account1);
    accounts.add(account2);
    accounts.add(account3);

    Customer customerPlatinum = new Customer("Jane", "Doe", "jane.doe@gmail.com", "+0987654321");
    account1 = new Account(customerPlatinum, Currency.EUR);
    account1.deposit(3000, Currency.EUR);
    account2 = new Account(customerPlatinum, Currency.USD);
    account2.deposit(5000, Currency.USD);
    account3 = new Account(customerPlatinum, Currency.GBP);
    account3.deposit(3000, Currency.GBP);

    accounts.add(account1);
    accounts.add(account2);
    accounts.add(account3);

   return accounts;
  }
  
  @ParameterizedTest(name = "Card pin validation test {index}")
  @MethodSource("getValidatePinValues")
  public void testValidatePin(Card card, String pin, boolean expected) {
    assertTrue(card.validatePin(pin) == expected);
  }

  @ParameterizedTest(name = "Card cvv validation test {index}")
  @MethodSource("getValidatePinValues")
  public void testGetCvv(Card card, String pin, boolean expected) {
    if (expected) {
      assertDoesNotThrow(() -> {
        card.getCvv(pin);
      });
    } else {
      assertThrows(IllegalArgumentException.class, () -> {
        card.getCvv(pin);
      });
    }
  }

  @ParameterizedTest(name = "Card benefit level test {1}")
  @MethodSource("getBenefitLevelValues")
  public void testGetBenefitLevel(Card card, BenefitLevel expected) {
    assertTrue(card.getCardBenefitLevel() == expected);
  }

  @ParameterizedTest(name = "Card process purchase test {index}")
  @MethodSource("getProcessPurchaseValues")
  public void testProcessPurchase(Card card, float amount, Currency currency, String pin, float expected, boolean success) {
    if (success) {
      assertAll("Test processPurchase " + String.valueOf(amount) + " " + currency.toString() + " " + pin,
        () -> assertDoesNotThrow(() -> {
          card.processPurchase(amount, currency, pin, getShop());
        }),
        () -> assertEquals(expected, card.getAccount().getBalance())
      );
    } else {
      assertAll("Test processPurchase",
        () -> assertThrows(IllegalArgumentException.class, () -> {
          card.processPurchase(amount, currency, pin, getShop());
        }),
        () -> assertEquals(expected, card.getAccount().getBalance())
      );
    }
  }

  @ParameterizedTest
  @MethodSource("getLimitValues")
  public void testLimit(Card card, float amount, Currency currency, float limit, String pin, float accountBalance, boolean success) {
    if (success) {
      assertAll("Test limit " + String.valueOf(amount) + " " + currency.toString() + " " + pin,
        () -> assertDoesNotThrow(() -> {
          if (limit > 0) {
            card.setLimit(limit, pin);
          } else {
            card.unsetLimit(pin);
          }
          card.processPurchase(amount, currency, pin, getShop());
        }),
        () -> assertEquals(accountBalance, card.getAccount().getBalance())
      );
    } else {
      assertAll("Test limit",
        () -> assertThrows(IllegalArgumentException.class, () -> {
          if (limit > 0) {
            card.setLimit(limit, pin);
          } else {
            card.unsetLimit(pin);
          }
          card.processPurchase(amount, currency, pin, getShop());
        }),
        () -> assertEquals(accountBalance, card.getAccount().getBalance())
      );
    }
  }
}
