package com.example.padelwear;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import static com.example.comun.Constants.ARG_START_REMOTE_ACTIVITY;
import static com.example.comun.Constants.WEAR_ARRANCAR_ACTIVIDAD;

public class ServicioEscuchador extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equalsIgnoreCase(WEAR_ARRANCAR_ACTIVIDAD)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(ARG_START_REMOTE_ACTIVITY, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}