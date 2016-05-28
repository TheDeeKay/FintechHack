package com.fintech.timpla.mojabanka;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class Merchant extends SugarRecord{

    @Unique
    private String objectId;
    private String Name;
    private String APIKey;

    public Merchant() {
        // Required empty constructor
    }

    public Merchant(String objectId, String name, String APIKey) {
        this.objectId = objectId;
        this.Name = name;
        this.APIKey = APIKey;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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
