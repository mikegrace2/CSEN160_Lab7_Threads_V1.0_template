package com.csen160.Bank;

public class AccountHolder extends Thread {
    private BankAccount account;

    public AccountHolder(BankAccount account) {
        this.account = account;
    }

    @Override
    public void run() {
        // simulate multiple random deposit/withdraw
        // transactions with random amounts on the bank
        // account. Between each transaction sleep for a second.
        throw new UnsupportedOperationException("Not supported yet.");
    }
}