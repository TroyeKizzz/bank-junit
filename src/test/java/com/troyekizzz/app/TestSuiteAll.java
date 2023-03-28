package com.troyekizzz.app;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
  AccountTest.class,
  Appointment.class,
  ATMTest.class,
  BankBranchTest.class,
  BankTest.class,
  CardTest.class,
  CustomerTest.class,
  ExchangeTest.class,
  InvoiceTest.class,
  TransactionTest.class,
})
public class TestSuiteAll {
  
}
