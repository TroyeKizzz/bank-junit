package com.troyekizzz.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.troyekizzz.app.utils.Currency;

public class BankBranchTest {
  static private Bank bank = new Bank("Nordea", 100000);
  static private Customer customer = new Customer("John", "Doe", "john.doe@gmail.com", "+1234567890");
  static private BankBranch bankBranch = new BankBranch(bank, "Kaupankatu 9", 10000);

  @Test
  @DisplayName("Test isOpen when bank is open")
  public void testIsOpenNormal() {;
    // 2023/03/08 12:00:00 Wednesday
    assertTrue(bankBranch.isOpen(new Date(1678269600000L)));
  }

  @Test
  @DisplayName("Test isOpen when bank is closed on Sunday midnight")
  public void testIsOpenSundayMidnight() {;
    // 2023/03/12 00:00 Sunday
    assertFalse(bankBranch.isOpen(new Date(1678572000000L)));
  }

  @Test
  @DisplayName("Test isOpen when bank is closed")
  public void testIsOpenClosed() {;
    // 2023/03/08 19:00:00 Wednesday
    assertFalse(bankBranch.isOpen(new Date(1678294800000L)));
  }

  @Test
  @DisplayName("Test book appointment when bank is closed")
  public void testBookAppointmentWhenClosed() {
    // 2023/03/08 19:00:00 Wednesday
    assertThrows(IllegalArgumentException.class, 
      () -> bankBranch.bookAppointment(new Date(1678294800000L), customer)
    );
  }

  @Test
  @DisplayName("Test book appointment when bank is open")
  public void testBookAppointmentWhenOpen() {
    // 2023/03/08 12:00:00 Wednesday
    assertAll("Test book appointment",
      () -> assertDoesNotThrow(() -> bankBranch.bookAppointment(new Date(1678269600000L), customer)),
      () -> assertEquals(1, bankBranch.getAppointments().size())
    );
  }

  @Test
  @DisplayName("Test book appointment when bank is closed on Sunday midnight")
  public void testBookAppointmentSundayMidnight() {
    // 2023/03/12 00:00 Sunday
    assertThrows(IllegalArgumentException.class, 
      () -> bankBranch.bookAppointment(new Date(1678572000000L), customer)
    );
  }

  @Test
  @DisplayName("Test book appointment when bank is open on Sunday midnight")
  public void testCancelAppointmentNormal() {
    // 2023/03/08 12:00:00 Wednesday
    Date date = new Date(1678269600000L);
    bankBranch.bookAppointment(date, customer);
    assertAll("Test cancel appointment",
      () -> assertDoesNotThrow(() -> bankBranch.cancelAppointment(date, customer)),
      () -> assertEquals(0, bankBranch.getAppointments().size())
    );
  }

  @Test 
  @DisplayName("Test cancel appointment when bank is closed")
  public void testCancelAppointmentNoAppointment() {
    // 2023/03/08 12:00:00 Wednesday
    Date date = new Date(1678269600000L);
    assertThrows(IllegalArgumentException.class, 
      () -> bankBranch.cancelAppointment(date, customer)
    );
  }

  @Test
  @DisplayName("Test cancel appointment when bank is closed on Sunday midnight")
  public void testCancelAppointmentSundayMidnight() {
    // 2023/03/12 00:00 Sunday
    Date date = new Date(1678572000000L);
    assertThrows(IllegalArgumentException.class, 
      () -> bankBranch.cancelAppointment(date, customer)
    );
  }

  @Test
  @DisplayName("Test deposit cash EUR normal")
  public void testDepositCashEURNormal() {
    Account account = new Account(customer, Currency.EUR);
    assertAll("Test deposit cash",
      () -> assertDoesNotThrow(() -> bankBranch.depositCash(account, 1000, Currency.EUR)),
      () -> assertEquals(11000, bankBranch.getBalance()),
      () -> assertEquals(1000, account.getBalance())
    );
  }

  @Test
  @DisplayName("Test deposit cash USD normal")
  public void testDepositCashUSDNormal() {
    Account account = new Account(customer, Currency.USD);
    assertAll("Test deposit cash",
      () -> assertDoesNotThrow(() -> bankBranch.depositCash(account, 1000, Currency.USD)),
      () -> assertEquals(11000, bankBranch.getBalance()),
      () -> assertEquals(1000, account.getBalance())
    );
  }

  @Test
  @DisplayName("Test deposit cash GBP normal")
  public void testDepositCashGBPNormal() {
    Account account = new Account(customer, Currency.GBP);
    assertAll("Test deposit cash",
      () -> assertDoesNotThrow(() -> bankBranch.depositCash(account, 1000, Currency.GBP)),
      () -> assertEquals(11000, bankBranch.getBalance()),
      () -> assertEquals(1000, account.getBalance())
    );
  }

  @Test
  @DisplayName("Test deposit cash wrong currency")
  public void testDepositCashWrongCurrency() {
    Account account = new Account(customer, Currency.EUR);
    assertThrows(IllegalArgumentException.class, 
      () -> bankBranch.depositCash(account, 1000, Currency.USD)
    );
  }

  @Test
  @DisplayName("Test withdraw cash EUR normal")
  public void testWithdrawCashEURNormal() {
    Account account = new Account(customer, Currency.EUR);
    account.deposit(1000, Currency.EUR);
    assertAll("Test withdraw cash",
      () -> assertDoesNotThrow(() -> bankBranch.withdrawCash(account, 1000, Currency.EUR)),
      () -> assertEquals(9000, bankBranch.getBalance()),
      () -> assertEquals(0, account.getBalance())
    );
  }

  @Test
  @DisplayName("Test withdraw cash USD normal")
  public void testWithdrawCashUSDNormal() {
    Account account = new Account(customer, Currency.USD);
    account.deposit(1000, Currency.USD);
    assertAll("Test withdraw cash",
      () -> assertDoesNotThrow(() -> bankBranch.withdrawCash(account, 1000, Currency.USD)),
      () -> assertEquals(9000, bankBranch.getBalance()),
      () -> assertEquals(0, account.getBalance())
    );
  }

  @Test
  @DisplayName("Test withdraw cash GBP normal")
  public void testWithdrawCashGBPNormal() {
    Account account = new Account(customer, Currency.GBP);
    account.deposit(1000, Currency.GBP);
    assertAll("Test withdraw cash",
      () -> assertDoesNotThrow(() -> bankBranch.withdrawCash(account, 1000, Currency.GBP)),
      () -> assertEquals(9000, bankBranch.getBalance()),
      () -> assertEquals(0, account.getBalance())
    );
  }

  @Test 
  @DisplayName("Test withdraw cash wrong currency")
  public void testWithdrawCashNoCash() {
    Account account = new Account(customer, Currency.EUR);
    assertThrows(IllegalArgumentException.class, 
      () -> bankBranch.withdrawCash(account, 1000, Currency.EUR)
    );
  }

  @BeforeEach
  public void beforeEach() {
    bank = new Bank("Nordea", 100000);
    customer = new Customer("John", "Doe", "john.doe@gmail.com", "+1234567890");
    bankBranch = new BankBranch(bank, "Kaupankatu 9", 10000);
  }
}



