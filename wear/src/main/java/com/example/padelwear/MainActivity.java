package com.example.padelwear;

import android.content.Intent;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.comun.Constants.ARG_START_REMOTE_ACTIVITY;
import static com.example.comun.Constants.MOBILE_ARRANCAR_ACTIVIDAD;

public class MainActivity extends WearableActivity {
    // Elementos a mostrar en la lista
    String[] elementos = {"Partida", "Terminar partida", "Historial", "Jugadores",
            "Notificación", "Pasos", "Pulsaciones", "Terminar partida", "Swipe dismiss", "Pasos 2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WearableRecyclerView lista = (WearableRecyclerView)
                findViewById(R.id.lista);
        lista.setEdgeItemsCenteringEnabled(true);
        lista.setLayoutManager(new WearableLinearLayoutManager(this, new CustomLayoutCallback()));
        Adaptador adaptador = new Adaptador(this, elementos);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer tag = (Integer) v.getTag();
                //Toast.makeText(MainActivity.this, "Elegida opción:" + tag, Toast.LENGTH_SHORT).show();
                switch (tag) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, Contador.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, Confirmacion.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, Historial.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, Jugadores.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, Pasos.class));
                        break;
                    case 8:
                        startActivity(new Intent(MainActivity.this, SwipeDismiss.class));
                        break;
                    case 9:
                        startActivity(new Intent(MainActivity.this, Pasos2.class));
                        break;
                }
            }
        });
        lista.setAdapter(adaptador);

        lista.setCircularScrollingGestureEnabled(true);
        lista.setScrollDegreesPerScreen(180);
        lista.setBezelFraction(0.5f);

        try {
            Log.d("MainActivity", ">>> waear");
            if (!getIntent().getBooleanExtra(ARG_START_REMOTE_ACTIVITY, false)) {
                callRemoteActivity();
            }
        } catch (Exception e) {
            callRemoteActivity();
        }
    }

    private void callRemoteActivity() {
        mandarMensaje(MOBILE_ARRANCAR_ACTIVIDAD, "");
    }

    private void mandarMensaje(final String path, final String texto) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Node> nodos = null;
                try {
                    nodos = Tasks.await(Wearable.getNodeClient(getApplicationContext()).getConnectedNodes());
                    for (Node nodo : nodos) {
                        Task<Integer> task =
                                Wearable.getMessageClient(getApplicationContext()).sendMessage(nodo.getId(), path, texto.getBytes());
                        task.addOnSuccessListener(new OnSuccessListener<Integer>() {
                            @Override
                            public void onSuccess(Integer i) {
                                Toast.makeText(getApplicationContext(), "enviado", Toast.LENGTH_LONG).show();
                            }
                        });
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getApplicationContext(), "Error :" + e, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (ExecutionException e) {
                    Log.e("Sincronización Wear", e.toString());
                } catch (InterruptedException e) {
                    Log.e("Sincronización Wear", e.toString());
                }
            }
        }).start();
    }
}