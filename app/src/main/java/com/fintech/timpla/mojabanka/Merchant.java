package com.fintech.timpla.mojabanka;

import com.orm.SugarRecord;

public class Merchant extends SugarRecord{

    private String name;
    private String APIKey;

    public Merchant(){
        // Required empty constructor
    }

    public Merchant(String name, String APIKey){
        this.name = name;
        this.APIKey = APIKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAPIKey() {
        return APIKey;
    }

    public void setAPIKey(String APIKey) {
        this.APIKey = APIKey;
    }
}
