package com.fintech.timpla.mojabanka;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class Authorization extends SugarRecord{

    @Unique
    private String objectId;
    private String MerchantId;
    private String BankAccount;

    public Authorization() {
    }

    public Authorization(String objectId, String merchantId, String bankAccount) {
        this.objectId = objectId;
        this.MerchantId = merchantId;
        this.BankAccount = bankAccount;
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

}
