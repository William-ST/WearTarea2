package com.example.padelwear;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.comun.Constants.ARG_START_REMOTE_ACTIVITY;
import static com.example.comun.Constants.WEAR_ARRANCAR_ACTIVIDAD;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            Log.d("MainActivity", ">>> mobile");
            if (!getIntent().getBooleanExtra(ARG_START_REMOTE_ACTIVITY, false)) {
                callRemoteActivity();
            }
        } catch (Exception e) {
            callRemoteActivity();
        }
    }

    private void callRemoteActivity() {
        mandarMensaje(WEAR_ARRANCAR_ACTIVIDAD, "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.accion_contador) {
            startActivity(new Intent(this, Contador.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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
