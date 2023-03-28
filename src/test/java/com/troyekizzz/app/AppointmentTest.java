package com.troyekizzz.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.troyekizzz.app.utils.Currency;
import com.troyekizzz.app.utils.NotificationType;

public class AppointmentTest {
  static private Bank bank = new Bank("Nordea", 100000);
  static private Customer customer = bank.addCustomer("John", "Doe", "john.doe@gmail.com", "+1234567890");
  static private BankBranch bankBranch = bank.addBranch("Kaupankatu 9", 10000);

  static Stream<Arguments> getAppointmentCostValues() {
    List<Arguments> values = new ArrayList<>();

    Customer customer = bank.addCustomer("John", "Doe", "john.doe@gmail.com", "+1234567890");
    Account account1 = bank.openAccount(customer, Currency.EUR);
    account1.deposit(1000, Currency.EUR);
    Account account2 = bank.openAccount(customer, Currency.USD);
    account2.deposit(2000, Currency.USD);
    Account account3 = bank.openAccount(customer, Currency.GBP);
    account3.deposit(3000, Currency.GBP);
    // GOLD
    values.add(Arguments.of(customer, 10.0f));

    customer = bank.addCustomer("Jane", "Doe", "jane.doe@gmail.com", "+0987654321");
    account1 = bank.openAccount(customer, Currency.EUR);
    account1.deposit(3000, Currency.EUR);
    account2 = bank.openAccount(customer, Currency.USD);
    account2.deposit(5000, Currency.USD);
    account3 = bank.openAccount(customer, Currency.GBP);
    account3.deposit(3000, Currency.GBP);
    // PLATINUM
    values.add(Arguments.of(customer, 0.0f));

    customer = bank.addCustomer("John", "Smith", "john.smith@gmail.com", "+6789054321");
    account1 = bank.openAccount(customer, Currency.EUR);
    account1.deposit(800, Currency.EUR);
    account2 = bank.openAccount(customer, Currency.USD);
    // SILVER
    values.add(Arguments.of(customer, 20.0f));

    return values.stream();
  }

  @Test
  @DisplayName("Test cancel appointment normal")
  public void testCancel() {
    Appointment appointment = new Appointment(customer, new Date(), new Date(new Date().getTime() + 3600000), bankBranch);
    assertDoesNotThrow(() -> appointment.cancel());
  }

  @Test
  @DisplayName("Test cancel appointment twice")
  public void testCancelTwice() {
    Appointment appointment = new Appointment(customer, new Date(), new Date(new Date().getTime() + 3600000), bankBranch);
    assertAll("Cancel appointment twice",
      () -> assertDoesNotThrow(() -> appointment.cancel()),
      () -> assertThrows(IllegalStateException.class , () -> appointment.cancel())
    );
  }

  @Test
  @DisplayName("Test cancel appointment 2 hours long")
  public void testCancel2HourAppointment() {
    Appointment appointment = new Appointment(customer, new Date(), new Date(new Date().getTime() + 7200000), bankBranch);
    assertAll("Cancel 2 hour appointment",
      () -> assertDoesNotThrow(() -> appointment.cancel()),
      () -> assertThrows(IllegalStateException.class , () -> appointment.cancel())
    );
  }

  @Test
  @DisplayName("Test send details by email")
  public void testSendDetailsByEmail() {
    Appointment appointment = new Appointment(customer, new Date(), new Date(new Date().getTime() + 3600000), bankBranch);
    assertAll("Send details by email",
      () -> assertDoesNotThrow(() -> appointment.sendDetails(NotificationType.EMAIL)),
      () -> assertEquals(1, customer.getMessages().size())
    );
  }

  @Test
  @DisplayName("Test send details by sms")
  public void testSendDetailsBySms() {
    Appointment appointment = new Appointment(customer, new Date(), new Date(new Date().getTime() + 3600000), bankBranch);
    assertAll("Send details by sms",
      () -> assertDoesNotThrow(() -> appointment.sendDetails(NotificationType.SMS)),
      () -> assertEquals(1, customer.getMessages().size())
    );
  }

  @Test
  @DisplayName("Test send details by email and sms")
  public void testSendDetailsByEmailAndSms() {
    Appointment appointment = new Appointment(customer, new Date(), new Date(new Date().getTime() + 3600000), bankBranch);
    assertAll("Send details by email and sms",
      () -> assertDoesNotThrow(() -> appointment.sendDetails(NotificationType.EMAIL)),
      () -> assertDoesNotThrow(() -> appointment.sendDetails(NotificationType.SMS)),
      () -> assertEquals(2, customer.getMessages().size())
    );
  }

  @Test
  @DisplayName("Test convert appointment to string 1")
  public void testToString1() {
    Appointment appointment = new Appointment(customer, new Date(1678294800000L), new Date(1678294800000L + 3600000L), bankBranch);
    assertEquals(
      "Appointment [startDate=Wed Mar 08 19:00:00 EET 2023, endDate=Wed Mar 08 20:00:00 EET 2023, bankBranch=Kaupankatu 9, isCancelled=false]", 
      appointment.toString()
    );
  }

  @Test
  @DisplayName("Test convert appointment to string 2")
  public void testToString2() {
    Appointment appointment = new Appointment(customer, new Date(1678294800000L), new Date(1678294800000L + 7200000L), bankBranch);
    assertEquals(
      "Appointment [startDate=Wed Mar 08 19:00:00 EET 2023, endDate=Wed Mar 08 21:00:00 EET 2023, bankBranch=Kaupankatu 9, isCancelled=false]", 
      appointment.toString()
    );
  }

  @Test
  @DisplayName("Test convert appointment to string 3")
  public void testToString3() {
    Appointment appointment = new Appointment(customer, new Date(1678294800000L), new Date(1678294800000L + 3600000L), bankBranch);
    appointment.cancel();
    assertEquals(
      "Appointment [startDate=Wed Mar 08 19:00:00 EET 2023, endDate=Wed Mar 08 20:00:00 EET 2023, bankBranch=Kaupankatu 9, isCancelled=true]", 
      appointment.toString()
    );
  }

  @ParameterizedTest(name = "Test appointment cost {1} EUR")
  @MethodSource("getAppointmentCostValues")
  public void testGetAppointmentCost(Customer customer, float expected) {
    Appointment appointment = new Appointment(customer, new Date(), new Date(new Date().getTime() + 3600000), bankBranch);
    assertEquals(expected, appointment.getAppointmentCost());
  }

  @ParameterizedTest(name = "Test pay appointment cost {1} EUR")
  @MethodSource("getAppointmentCostValues")
  public void testPayAppointmentCost(Customer customer, float expected) {
    Appointment appointment = new Appointment(customer, new Date(), new Date(new Date().getTime() + 3600000), bankBranch);
    assertAll("Pay appointment cost",
      () -> assertDoesNotThrow(() -> appointment.payAppointmentCost(customer.getBankAccounts().get(0)))
    );
  }

  @BeforeEach
  public void setUp() {
    bank = new Bank("Nordea", 100000);
    customer = bank.addCustomer("John", "Doe", "john.doe@gmail.com", "+1234567890");
    bankBranch = bank.addBranch("Kaupankatu 9", 10000);
  }
}
