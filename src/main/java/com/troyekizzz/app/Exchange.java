package com.troyekizzz.app;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.troyekizzz.app.utils.Currency;

/**
 * A singleton class that represents an exchange rate.
 * 
 * @author TroyeKizzz
 */
public class Exchange {
  /**
   * The exchange rates of the currencies.
   */
  private List<Object[]> rates = new ArrayList<>();

  private List<EnumSet<Currency>> disabledRates = new ArrayList<>();

  /**
   * The singleton instance.
   */
  public static Exchange instance = null;

  /**
   * Creates a new exchange rate.
   */
  private Exchange() {
    rates.add(new Object[] { Currency.EUR, Currency.USD, 1.1f });
    rates.add(new Object[] { Currency.EUR, Currency.GBP, 0.8f });
    rates.add(new Object[] { Currency.USD, Currency.GBP, 0.72f });
    rates.add(new Object[] { Currency.USD, Currency.EUR, 0.9f });
    rates.add(new Object[] { Currency.GBP, Currency.EUR, 1.25f });
    rates.add(new Object[] { Currency.GBP, Currency.USD, 1.38f });
  }

  /**
   * Returns the singleton instance.
   * 
   * If the instance does not exist, it is created.
   * Otherwise, the existing instance is returned.
   * 
   * @return The singleton instance.
   */
  public static synchronized Exchange getInstance() {
    if (instance == null) {
      instance = new Exchange();
    }
    return instance;
  }

  /**
   * Returns the exchange rate between the currencies.
   * 
   * The currencies have to be supported.
   * 
   * @param from The currency to convert from.
   * @param to   The currency to convert to.
   * @return The exchange rate.
   */
  public float getRate(Currency from, Currency to) throws IllegalArgumentException {
    if (from == to) {
      return 1.0f;
    }
    for (Object[] rate : rates) {
      if (rate[0] == from && rate[1] == to) {
        if (disabledRates.contains(EnumSet.of(from, to))) {
          throw new IllegalArgumentException("The exchange between " + from + " and " + to + " is disabled.");
        }
        return (float) rate[2];
      }
    }
    throw new IllegalArgumentException("The exchange rate is not defined.");
  }

  /**
   * Converts the amount from one currency to another.
   * 
   * The currencies have to be supported
   * and the amount has to be positive.
   * 
   * @param from   The currency to convert from.
   * @param to     The currency to convert to.
   * @param amount The amount to convert.
   * @return The converted amount.
   */
  public float convert(Currency from, Currency to, float amount) throws IllegalArgumentException {
    if (amount < 0) 
      throw new IllegalArgumentException("The amount must be positive.");
    
    try {
      return amount * getRate(from, to);
    } catch (IllegalArgumentException e) {
      throw e;
    }
  }

  /**
   * Changes the exchange rate between the currencies.
   * 
   * The currencies have to be supported
   * and the rate has to be positive.
   * 
   * @param from The currency to convert from.
   * @param to   The currency to convert to.
   * @param rate The new exchange rate.
   */
  public void changeRate(Currency from, Currency to, float rate) throws IllegalArgumentException {
    if (rate < 0) 
      throw new IllegalArgumentException("The rate must be positive.");

    for (Object[] r : rates) {
      if (r[0] == from && r[1] == to) {
        r[2] = rate;
        return;
      }
    }

    throw new IllegalArgumentException("The exchange rate is not defined.");
  }

  /**
   * Disables the exchange rate between the currencies.
   * 
   * If the exchange rate is disabled, the currencies cannot be exchanged.
   * 
   * @param from The currency to convert from.
   * @param to  The currency to convert to.
   */
  public void disableRate(Currency from, Currency to) throws IllegalArgumentException {
    if (from == to) 
      throw new IllegalArgumentException("The exchange rate of the same currency is always disabled.");
    if (disabledRates.contains(EnumSet.of(from, to))) 
      throw new IllegalArgumentException("The exchange rate is already disabled.");

    disabledRates.add(EnumSet.of(from, to));
  }

  /**
   * Enables the exchange rate between the currencies.
   * 
   * If the exchange rate is enabled, the currencies can be exchanged.
   * 
   * @param from The currency to convert from.
   * @param to  The currency to convert to.
   */
  public void enableRate(Currency from, Currency to) {
    if (!disabledRates.contains(EnumSet.of(from, to)))
      throw new IllegalArgumentException("The exchange rate is already enabled.");

    disabledRates.remove(EnumSet.of(from, to));
  }
}
