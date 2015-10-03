package com.fiuba.tdp.petadopt.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {
    public InstanceIDListenerService() {
        super();
    }

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}