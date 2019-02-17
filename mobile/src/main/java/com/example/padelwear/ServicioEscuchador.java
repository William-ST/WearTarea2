package com.example.padelwear;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import static com.example.comun.Constants.ARG_START_REMOTE_ACTIVITY;
import static com.example.comun.Constants.MOBILE_ARRANCAR_ACTIVIDAD;

public class ServicioEscuchador extends WearableListenerService {

    private final String TAG = ServicioEscuchador.class.getCanonicalName();
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived 1");
        Log.d(TAG, "messageEvent.getPath(): "+messageEvent.getPath());
        if (messageEvent.getPath().equalsIgnoreCase(MOBILE_ARRANCAR_ACTIVIDAD)) {
            Intent intent = new Intent(this, Contador.class);
            intent.putExtra(ARG_START_REMOTE_ACTIVITY, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}