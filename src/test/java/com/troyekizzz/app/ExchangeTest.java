package com.troyekizzz.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.troyekizzz.app.utils.Currency;

public class ExchangeTest {
  static Stream<Arguments> getGetRateValues() {
    return Stream.of(
      Arguments.of(Currency.EUR, Currency.USD, 1.1f),
      Arguments.of(Currency.USD, Currency.GBP, 0.72f),
      Arguments.of(Currency.GBP, Currency.EUR, 1.25f)
    );
  }

  static Stream<Arguments> getConvertValues() {
    return Stream.of(
      Arguments.of(1000, Currency.EUR, Currency.USD, 1100.0f),
      Arguments.of(2000, Currency.USD, Currency.GBP, 1440.0f),
      Arguments.of(3000, Currency.GBP, Currency.EUR, 3750.0f)
    );
  }

  static Stream<Arguments> getChangeRateValues() {
    return Stream.of(
      Arguments.of(Currency.EUR, Currency.USD, 1.2f),
      Arguments.of(Currency.USD, Currency.GBP, 0.8f),
      Arguments.of(Currency.GBP, Currency.EUR, 1.3f)
    );
  }

  @ParameterizedTest(name = "Test getRate method between {0} and {1} currencies")
  @MethodSource("getGetRateValues")
  public void testGetRate(Currency from, Currency to, float rate) {
    assertEquals(rate, Exchange.getInstance().getRate(from, to));
  }

  @ParameterizedTest(name = "Test convert method from {0} {1} to {2}")
  @MethodSource("getConvertValues")
  public void testConvert(float amount, Currency from, Currency to, float converted) {
    assertEquals(converted, Exchange.getInstance().convert(from, to, amount));
  }

  @ParameterizedTest(name = "Test changeRate method between {0} and {1} currencies")
  @MethodSource("getChangeRateValues")
  public void testChangeRate(Currency from, Currency to, float rate) {
    Exchange.getInstance().changeRate(from, to, rate);
    assertEquals(rate, Exchange.getInstance().getRate(from, to));
  }

  @ParameterizedTest(name = "Test disableRate method between {0} and {1} currencies")
  @MethodSource("getGetRateValues")
  public void testDisableRate(Currency from, Currency to) {
    Exchange exchange = Exchange.getInstance();
    exchange.disableRate(from, to);
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
      exchange.getRate(from, to);
    });
    assertEquals("The exchange between " + from + " and " + to + " is disabled.", exception.getMessage());
  }

  @Test
  @DisplayName("Test getInstance singleton with changeRate method")
  public void testSingletonChangeRate() {
    Exchange exchange1 = Exchange.getInstance();
    Exchange exchange2 = Exchange.getInstance();

    exchange1.changeRate(Currency.EUR, Currency.USD, 1.3f);

    assertEquals(1.3f, exchange2.getRate(Currency.EUR, Currency.USD));
  }

  @Test
  @DisplayName("Test getInstance singleton with disableRate method")
  public void testSingletonDisableRate() {
    Exchange exchange1 = Exchange.getInstance();
    Exchange exchange2 = Exchange.getInstance();

    exchange1.disableRate(Currency.EUR, Currency.USD);

    Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
      exchange2.getRate(Currency.EUR, Currency.USD);
    });
    assertEquals("The exchange between EUR and USD is disabled.", exception.getMessage());
  }

  @Test
  @DisplayName("Test getInstance singleton with enableRate method")
  public void testSingletonEnableRate() {
    Exchange exchange1 = Exchange.getInstance();
    Exchange exchange2 = Exchange.getInstance();

    exchange1.disableRate(Currency.EUR, Currency.USD);
    exchange1.enableRate(Currency.EUR, Currency.USD);

    assertEquals(1.1f, exchange2.getRate(Currency.EUR, Currency.USD));
  }

  @BeforeEach
  public void setUp() {
    // Reset the singleton instance
    Exchange.instance = null;
  }
}
