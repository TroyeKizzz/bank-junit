package com.troyekizzz.app;

import java.util.Date;

import com.troyekizzz.app.utils.BenefitLevel;
import com.troyekizzz.app.utils.Currency;
import com.troyekizzz.app.utils.NotificationType;

import lombok.Getter;

@Getter
public class Appointment {
  private Customer customer;
  private Date startDate;
  private Date endDate;
  private BankBranch bankBranch;
  private boolean isCancelled;
  

  public Appointment(Customer customer, Date startDate, Date endDate, BankBranch bankBranch) {
    this.customer = customer;
    this.startDate = startDate;
    this.endDate = endDate;
    this.bankBranch = bankBranch;
    this.isCancelled = false;
  }

  public void cancel() throws IllegalStateException {
    if (this.isCancelled) {
      throw new IllegalStateException("The appointment is already cancelled.");
    }
    this.isCancelled = true;
  }

  public void sendDetails(NotificationType type) {
    this.customer.notify("You have an upcoming appointment: " + this.toString(), type);
  }

  public String toString() {
    return "Appointment [startDate=" + startDate + ", endDate=" + endDate + ", bankBranch="
        + bankBranch.getLocation() + ", isCancelled=" + isCancelled + "]";
  }

  public float getAppointmentCost() {
    if (customer.getBenefitLevel() == BenefitLevel.GOLD) {
      return 10;
    } else if (customer.getBenefitLevel() == BenefitLevel.SILVER) {
      return 20;
    } else {
      return 0;
    }
  }

  public void payAppointmentCost(Account account) {
    if (!this.customer.getBankAccounts().contains(account)) {
      throw new IllegalArgumentException("The account is not linked to the customer.");
    }
    float appointmentCost = getAppointmentCost();
    account.withdraw(appointmentCost, Currency.EUR);
    this.bankBranch.setBalance(this.bankBranch.getBalance() + appointmentCost);
  }
}
