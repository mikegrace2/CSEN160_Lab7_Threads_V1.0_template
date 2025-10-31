package com.csen160.Bank;

public class A_Main {
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
