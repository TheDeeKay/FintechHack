package com.fintech.timpla.mojabanka;

import android.content.ContentValues;
import android.net.Uri;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class Merchant extends SugarRecord{

    public static final String NAME = "Name";
    public static final String APIKEY = "APIKey";

    @Unique
    private String Name;
    private String APIKey;

    public Merchant(){
        // Required empty constructor
    }

    public Merchant(String name, String APIKey){
        this.Name = name;
        this.APIKey = APIKey;
    }

    public Merchant(ContentValues cv){
        if (cv != null && cv.containsKey(NAME) && cv.containsKey(APIKEY)){
            Name = cv.getAsString(NAME);
            APIKey = cv.getAsString(APIKEY);
        }
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

    /*
    Builds a URI referencing all the Merchant entries
     */
    public static Uri buildMerchantUri(){
        return DbProvider.MERCHANT_CONTENT_URI;
    }

    /*
    Builds a URI referencing a Merchant with the provided id
     */
    public static Uri buildMerchantUriWithId(long id){
        return buildMerchantUri().buildUpon().appendPath(String.valueOf(id)).build();
    }
}
