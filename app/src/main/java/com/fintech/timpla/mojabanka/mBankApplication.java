package com.fintech.timpla.mojabanka;

import android.app.Application;

import com.backendless.Backendless;
import com.orm.SugarContext;

public class MBankApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(this);

        String appVersion = "v1";

        Backendless.initApp( this, getString(R.string.APPID), getString(R.string.SEC_KEY), appVersion);
    }

    @Override
    public void onTerminate() {
        SugarContext.terminate();
        super.onTerminate();
    }
}
