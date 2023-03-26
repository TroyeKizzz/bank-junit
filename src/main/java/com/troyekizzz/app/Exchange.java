package com.troyekizzz.app;

import com.troyekizzz.app.utils.Currency;

public class Exchange {
  private static Object[][] rates = {
    {Currency.EUR, Currency.USD, 1.1f},
    {Currency.EUR, Currency.GBP, 0.8f},
    {Currency.USD, Currency.GBP, 0.72f},
    {Currency.USD, Currency.EUR, 0.9f},
    {Currency.GBP, Currency.EUR, 1.25f},
    {Currency.GBP, Currency.USD, 1.38f},
  };

  public static float getRate(Currency from, Currency to) throws IllegalArgumentException {
    if (from == to) {
      return 1.0f;
    }
    for (Object[] rate : rates) {
      if (rate[0] == from && rate[1] == to) {
        return (float) rate[2];
      }
    }
    throw new IllegalArgumentException("The exchange rate is not defined.");
  }

  public static float convert(Currency from, Currency to, float amount) throws IllegalArgumentException {
    if (amount < 0) {
      throw new IllegalArgumentException("The amount must be positive.");
    }
    return amount * getRate(from, to);
  }
}
