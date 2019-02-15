package com.example.padelwear;

import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends WearableActivity {
    // Elementos a mostrar en la lista
    String[] elementos = {"Partida", "Terminar partida", "Historial",
            "Notificación", "Pasos", "Pulsaciones", "Terminar partida"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WearableRecyclerView lista = (WearableRecyclerView)
                findViewById(R.id.lista);
        lista.setLayoutManager(new WearableLinearLayoutManager(this));
        Adaptador adaptador = new Adaptador(this, elementos);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer tag = (Integer) v.getTag();
                Toast.makeText(MainActivity.this, "Elegida opción:" + tag, Toast.LENGTH_SHORT).show();
            }
            }
        );
        lista.setAdapter(adaptador);
    }
}