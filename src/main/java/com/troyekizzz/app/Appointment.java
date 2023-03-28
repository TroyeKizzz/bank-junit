package com.troyekizzz.app;

import java.util.Date;

import com.troyekizzz.app.utils.BenefitLevel;
import com.troyekizzz.app.utils.Currency;
import com.troyekizzz.app.utils.NotificationType;

import lombok.Getter;

/**
 * A class that represents a bank appointment.
 * 
 * @author TroyeKizzz
 */
@Getter
public class Appointment {
  /**
   * The customer that the appointment is for.
   */
  private Customer customer;

  /**
   * The start date of the appointment.
   */
  private Date startDate;

  /**
   * The end date of the appointment.
   */
  private Date endDate;

  /**
   * The bank branch where the appointment is.
   */
  private BankBranch bankBranch;

  /**
   * The appointment cancellation status.
   */
  private boolean isCancelled;
  

  /**
   * A constructor that creates a new appointment.
   * 
   * @param customer The customer that the appointment is for.
   * @param startDate The start date of the appointment.
   * @param endDate The end date of the appointment.
   * @param bankBranch The bank branch where the appointment is.
   */
  public Appointment(Customer customer, Date startDate, Date endDate, BankBranch bankBranch) {
    this.customer = customer;
    this.startDate = startDate;
    this.endDate = endDate;
    this.bankBranch = bankBranch;
    this.isCancelled = false;
  }

  /**
   * A method that cancels the appointment.
   * 
   */
  public void cancel() throws IllegalStateException {
    if (this.isCancelled) {
      throw new IllegalStateException("The appointment is already cancelled.");
    }
    this.isCancelled = true;
  }

  /**
   * A method that sends the appointment details to the customer.
   * 
   * @param type The notification type.
   */
  public void sendDetails(NotificationType type) {
    this.customer.notify("You have an upcoming appointment: " + this.toString(), type);
  }

  /**
   * A method that returns the appointment details.
   */
  @Override
  public String toString() {
    return "Appointment [startDate=" + startDate + ", endDate=" + endDate + ", bankBranch="
        + bankBranch.getLocation() + ", isCancelled=" + isCancelled + "]";
  }

  /**
   * A method that returns the appointment cost.
   * 
   * @return The appointment cost.
   */
  public float getAppointmentCost() {
    if (customer.getBenefitLevel() == BenefitLevel.GOLD) {
      return 10;
    } else if (customer.getBenefitLevel() == BenefitLevel.SILVER) {
      return 20;
    } else {
      return 0;
    }
  }

  /**
   * A method that pays the appointment cost.
   * 
   * @param account The account that the appointment cost is paid from.
   */
  public void payAppointmentCost(Account account) {
    if (!this.customer.getBankAccounts().contains(account)) {
      throw new IllegalArgumentException("The account is not linked to the customer.");
    }
    float appointmentCost = getAppointmentCost();
    account.withdraw(appointmentCost, Currency.EUR);
    this.bankBranch.setBalance(this.bankBranch.getBalance() + appointmentCost);
  }
}
