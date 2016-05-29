package com.fintech.timpla.mojabanka;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class Authorization extends SugarRecord {

    @Unique
    private String objectId;
    private String MerchantId;
    private String BankAccount;
    private String CipheredBankAccount;
    private String UserName;

    public Authorization() {
    }

    public Authorization(String objectId, String merchantId, String bankAccount) {
        this.objectId = objectId;
        this.MerchantId = merchantId;
        this.BankAccount = bankAccount;
    }

    public Authorization(String objectId, String merchantId, String bankAccount, String cipheredBankAccount) {
        this.objectId = objectId;
        MerchantId = merchantId;
        BankAccount = bankAccount;
        CipheredBankAccount = cipheredBankAccount;
    }

    public Authorization(String objectId, String merchantId, String bankAccount, String cipheredBankAccount, String userName) {
        this.objectId = objectId;
        MerchantId = merchantId;
        BankAccount = bankAccount;
        CipheredBankAccount = cipheredBankAccount;
        UserName = userName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getMerchantId() {
        return MerchantId;
    }

    public void setMerchantId(String merchantId) {
        this.MerchantId = merchantId;
    }

    public String getBankAccount() {
        return BankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.BankAccount = bankAccount;
    }

    public String getCipheredBankAccount() {
        return CipheredBankAccount;
    }

    public void setCipheredBankAccount(String cipheredBankAccount) {
        this.CipheredBankAccount = cipheredBankAccount;
    }

    public String getUserName() {

        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
