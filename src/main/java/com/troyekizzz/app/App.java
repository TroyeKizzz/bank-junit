package com.troyekizzz.app;

import java.util.Date;

import com.troyekizzz.app.utils.CardType;
import com.troyekizzz.app.utils.Currency;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Bank bank = new Bank("Nordea", 100000);
        BankBranch bankBranch1 = bank.addBranch("Hameenkatu 22", 10000);
        BankBranch bankBranch2 = bank.addBranch("Kauppakatu 10", 10000);
        ATM atm1 = bank.addAtm("Hameenkatu 22", 1000);
        ATM atm2 = bank.addAtm("Kauppakatu 10", 1000);

        Customer customer1 = bank.addCustomer("John", "Doe", "john.doe@gmail.com", "+1234567890");
        bank.openAccount(customer1, Currency.EUR);
        bank.openAccount(customer1, Currency.USD);
        bank.openAccount(customer1, Currency.GBP);

        Customer customer2 = bank.addCustomer("Jane", "Doe", "jane.doe@gmail.com", "+0987654321");
        bank.openAccount(customer2, Currency.EUR);
        bank.openAccount(customer2, Currency.USD);
        bank.openAccount(customer2, Currency.GBP);

        Customer customer3 = bank.addCustomer("John", "Smith", "john.smith@gmail.com", "+6789054321");
        bank.openAccount(customer3, Currency.EUR);
        bank.openAccount(customer3, Currency.USD);
        bank.openAccount(customer3, Currency.GBP);

        Card card1 = bank.addCard(customer1.getBankAccounts().get(0), CardType.CREDIT, "1234");
        Card card2 = bank.addCard(customer2.getBankAccounts().get(1), CardType.DEBIT, "1111");
        Card card3 = bank.addCard(customer3.getBankAccounts().get(2), CardType.CREDIT, "2222");

        System.out.println("\nBefore depositing money:");
        System.out.println(atm1.checkBalance(card1, "1234"));
        System.out.println(atm1.checkBalance(card2, "1111"));
        System.out.println(atm2.checkBalance(card3, "2222"));

        atm1.depositCash(card1, 100, Currency.EUR, "1234");
        atm2.depositCash(card2, 200, Currency.EUR, "1111");
        atm2.depositCash(card3, 300, Currency.GBP, "2222");

        System.out.println("\nAfter depositing money:");
        System.out.println(atm1.checkBalance(card1, "1234"));
        System.out.println(atm1.checkBalance(card2, "1111"));
        System.out.println(atm2.checkBalance(card3, "2222"));

        Customer shop = bank.addCustomer("Grocery Shop", "H-Market", "info@h-market.fi", "+358 123 456 789");
        bank.openAccount(shop, Currency.EUR);
        card1.processPurchase(50, Currency.EUR, "1234", shop);
        atm2.withdrawCash(card2, 100, Currency.EUR, "1111");
        atm1.withdrawCash(card3, 50, Currency.GBP, "2222");

        System.out.println("\nTransactions:");
        System.out.println(card1.getHistory().get(1).getDescription());
        System.out.println(card2.getHistory().get(1).getDescription());
        System.out.println(card3.getHistory().get(1).getDescription());

        System.out.println("\nAfter transactions:");
        System.out.println(atm1.checkBalance(card1, "1234"));
        System.out.println(atm1.checkBalance(card2, "1111"));
        System.out.println(atm2.checkBalance(card3, "2222"));

        Appointment appointment = bankBranch1.bookAppointment(new Date(1678269600000L), customer2);
        System.out.println("\nAppointment:");
        System.out.println(
            appointment.getCustomer().getFirstName() + " " + 
            appointment.getCustomer().getLastName() + " booked an appointment that costs " + 
            appointment.getAppointmentCost() + " EUR at " + 
            appointment.getBankBranch().getLocation() + " on " + appointment.getStartDate()
        );

        Invoice invoice = new Invoice(shop, customer3, shop.getBankAccounts().get(0), 100, Currency.GBP, 0);
        invoice.accept(card3.getAccount());
        invoice.pay();
        System.out.println("\nAfter paying invoice:");
        System.out.println(atm1.checkBalance(card3, "2222"));

        bank.removeCustomer(customer1);
        bank.removeCustomer(customer2);
        bank.removeCustomer(customer3);

        bank.removeAtm(atm1);
        bank.removeAtm(atm2);
    }
}
