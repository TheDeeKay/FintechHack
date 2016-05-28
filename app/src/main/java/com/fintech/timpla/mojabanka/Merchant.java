package com.fintech.timpla.mojabanka;

import com.orm.SugarRecord;

public class Merchant extends SugarRecord{

    private String Name;
    private String APIKey;

    public Merchant(){
        // Required empty constructor
    }

    public Merchant(String name, String APIKey){
        this.Name = name;
        this.APIKey = APIKey;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getAPIKey() {
        return APIKey;
    }

    public void setAPIKey(String APIKey) {
        this.APIKey = APIKey;
    }
}
