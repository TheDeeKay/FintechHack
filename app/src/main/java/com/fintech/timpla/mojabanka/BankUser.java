package com.fintech.timpla.mojabanka;

public class BankUser {
    private String EMail;
    private String Password;
    private String BankAccount;

    public BankUser() {
    }

    public BankUser(String EMail, String password, String bankAccount) {
        this.EMail = EMail;
        Password = password;
        BankAccount = bankAccount;
    }

    public String getEMail() {
        return EMail;
    }

    public void setEMail(String EMail) {
        this.EMail = EMail;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getBankAccount() {
        return BankAccount;
    }

    public void setBankAccount(String bankAccount) {
        BankAccount = bankAccount;
    }
}
