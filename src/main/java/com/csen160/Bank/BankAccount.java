package com.csen160.Bank;

public class BankAccount {
    private int balance = 1000;

    public synchronized void deposit(int amount) {
        balance += amount;
        System.out.println("Deposited: " + amount + ", New Balance: " + balance);
    }

    public synchronized void withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            System.out.println("Withdrew: " + amount + ", New Balance: " + balance);
        } else {
            System.out.println("Failed Withdrawal of: " + amount + " - Insufficient Funds. Current Balance: " + balance);
        }
    }

    public int getBalance() {
        return balance;
    }
}
