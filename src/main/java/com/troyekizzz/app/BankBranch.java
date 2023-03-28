package com.troyekizzz.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.troyekizzz.app.utils.Currency;

import lombok.Getter;
import lombok.Setter;

/**
 * A class that represents a bank branch.
 * 
 * @author TroyeKizzz
 */
@Getter
@Setter
public class BankBranch {
  /**
   * The bank that the branch is owned by.
   */
  private Bank bank;

  /**
   * The branch location.
   */
  private String location;

  /**
   * The branch balance.
   */
  private float balance;

  /**
   * The branch opening hours.
   */
  private NavigableMap<Integer, int[]> openingHours= new TreeMap<>();

  /**
   * The branch appointments.
   */
  private List<Appointment> appointments = new ArrayList<>();


  /**
   * Creates a new bank branch.
   * 
   * @param bank     The bank that the branch is owned by.
   * @param location The branch location.
   * @param balance  The branch balance.
   */
  public BankBranch(Bank bank, String location, float balance) {
    this.bank = bank;
    this.location = location;
    this.balance = balance;
    this.openingHours.put(Calendar.MONDAY, new int[] {8, 17});
    this.openingHours.put(Calendar.TUESDAY, new int[] {8, 17});
    this.openingHours.put(Calendar.WEDNESDAY, new int[] {8, 17});
    this.openingHours.put(Calendar.THURSDAY, new int[] {8, 17});
    this.openingHours.put(Calendar.FRIDAY, new int[] {8, 17});
    this.openingHours.put(Calendar.SATURDAY, new int[] {8, 12});
    this.openingHours.put(Calendar.SUNDAY, new int[] {0, 0});
  }

  /**
   * A method that checks if the branch is open at a given time.
   * 
   * @param date The date to check.
   * @return True if the branch is open, false if the branch is closed.
   */
  public boolean isOpen(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int[] hours = this.openingHours.get(day);
    if (hours[0] == 0 && hours[1] == 0)
      return false;
    int opensAtMinutes = hours[0] * 60;
    int closesAtMinutes = hours[1] * 60;
    return opensAtMinutes <= hour * 60 + minute && hour * 60 + minute <= closesAtMinutes;
  }

  /**
   * A method that books an appointment at the branch.
   * 
   * @param date     The date of the appointment.
   * @param customer The customer that books the appointment.
   */
  public Appointment bookAppointment(Date date, Customer customer) {
    if (!isOpen(date))
      throw new IllegalArgumentException("The bank is closed at this time.");
    Appointment appointment = new Appointment(customer, date, new Date(date.getTime() + 3600000), this);
    this.appointments.add(appointment);
    return appointment;
  }

  /**
   * A method that cancels an appointment at the branch.
   * 
   * @param date     The date of the appointment.
   * @param customer The customer that books the appointment.
   */
  public void cancelAppointment(Date date, Customer customer) {
    for (Appointment appointment : this.appointments) {
      if (appointment.getStartDate().equals(date) && appointment.getCustomer().equals(customer)) {
        appointment.cancel();
        this.appointments.remove(appointment);
        return;
      }
    }
    throw new IllegalArgumentException("No appointment found.");
  }

  /**
   * A method that withdraws cash from the branch.
   * 
   * @param account  The account to withdraw from.
   * @param amount   The amount to withdraw.
   * @param currency The currency to withdraw in.
   */
  public void withdrawCash(Account account, float amount, Currency currency) {
    if (account.getBalance() < amount)
      throw new IllegalArgumentException("Not enough money.");
    if (account.getCurrency() != currency)
      throw new IllegalArgumentException("Cannot withdraw in different currency.");
    account.withdraw(amount, currency);;
    this.balance -= amount;
  }

  /**
   * A method that deposits cash to the branch.
   * 
   * @param account  The account to deposit to.
   * @param amount   The amount to deposit.
   * @param currency The currency to deposit in.
   */
  public void depositCash(Account account, float amount, Currency currency) {
    if (account.getCurrency() != currency)
      throw new IllegalArgumentException("Cannot deposit in different currency.");
    account.deposit(amount, currency);
    this.balance += amount;
  }
}
