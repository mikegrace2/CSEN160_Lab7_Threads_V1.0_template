package com.csen160;

class BankAccount {
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

class AccountHolder extends Thread {
    private BankAccount account;

    public AccountHolder(BankAccount account) {
        this.account = account;
    }

    @Override
    public void run() {
    	// simulate multiple random deposit/withdraw 
    	// transactions with random amounts on the bank  
    	// account. Between each transaction sleep for a second.
    }
}

public class BankSimulation {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();
        Thread holder1 = new AccountHolder(account);
        Thread holder2 = new AccountHolder(account);
        Thread holder3 = new AccountHolder(account);

        holder1.start();
        holder2.start();
        holder3.start();
    }
}
