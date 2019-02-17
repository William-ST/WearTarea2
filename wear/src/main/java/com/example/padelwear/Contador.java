package com.example.padelwear;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.comun.DireccionesGestureDetector;
import com.example.comun.Partida;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.example.comun.Constants.ASSET_FOTO;
import static com.example.comun.Constants.ITEM_FOTO;

public class Contador extends WearableActivity implements DataClient.OnDataChangedListener {

    private static final String WEAR_PUNTUACION = "/puntuacion";
    private static final String KEY_MIS_PUNTOS = "com.example.padel.key.mis_puntos";
    private static final String KEY_MIS_JUEGOS = "com.example.padel.key.mis_juegos";
    private static final String KEY_MIS_SETS = "com.example.padel.key.mis_sets";
    private static final String KEY_SUS_PUNTOS = "com.example.padel.key.sus_puntos";
    private static final String KEY_SUS_JUEGOS = "com.example.padel.key.sus_juegos";
    private static final String KEY_SUS_SETS = "com.example.padel.key.sus_sets";

    private Partida partida;
    private FrameLayout frameLayout;
    private TextView misPuntos, misJuegos, misSets, susPuntos, susJuegos, susSets, hora;
    private Vibrator vibrador;
    private long[] vibrEntrada = {0l, 500};
    private long[] vibrDeshacer = {0l, 500, 500, 500};
    private DismissOverlayView dismissOverlay;
    private Typeface fuenteNormal = Typeface.create("sans-serif", Typeface.NORMAL);
    private Typeface fuenteFina = Typeface.create("sans-serif-thin", Typeface.NORMAL);

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer eventos) {
        for (DataEvent evento : eventos) {
            if (evento.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = evento.getDataItem();
                if (item.getUri().getPath().equals(ITEM_FOTO)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(item);
                    Asset asset = dataMapItem.getDataMap().getAsset(ASSET_FOTO);
                    LoadBitmapFromAsset tarea = new LoadBitmapFromAsset();
                    tarea.execute(asset);
                }
            }
        }
    }

    private void sincronizaDatos() {
        Log.d("Padel Wear", "Sincronizando");
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(WEAR_PUNTUACION);
        putDataMapReq.getDataMap().putByte(KEY_MIS_PUNTOS, partida.getMisPuntosByte());
        putDataMapReq.getDataMap().putByte(KEY_MIS_JUEGOS, partida.getMisJuegosByte());
        putDataMapReq.getDataMap().putByte(KEY_MIS_SETS, partida.getMisSetsByte());
        putDataMapReq.getDataMap().putByte(KEY_SUS_PUNTOS, partida.getSusPuntosByte());
        putDataMapReq.getDataMap().putByte(KEY_SUS_JUEGOS, partida.getSusJuegosByte());
        putDataMapReq.getDataMap().putByte(KEY_SUS_SETS, partida.getSusSetsByte());

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.getDataClient(getApplicationContext()).putDataItem(putDataReq);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        misPuntos.setTypeface(fuenteFina);
        misPuntos.getPaint().setAntiAlias(false);
        misJuegos.setTypeface(fuenteFina);
        misJuegos.getPaint().setAntiAlias(false);
        misSets.setTypeface(fuenteFina);
        misSets.getPaint().setAntiAlias(false);
        susPuntos.setTypeface(fuenteFina);
        susPuntos.getPaint().setAntiAlias(false);
        susJuegos.setTypeface(fuenteFina);
        susJuegos.getPaint().setAntiAlias(false);
        susSets.setTypeface(fuenteFina);
        susSets.getPaint().setAntiAlias(false);
        hora.setTypeface(fuenteFina);
        hora.getPaint().setAntiAlias(false);
        refreshHour();
        hora.setVisibility(View.VISIBLE);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        hora.setVisibility(View.GONE);
        misPuntos.setTypeface(fuenteNormal);
        misPuntos.getPaint().setAntiAlias(true);
        misJuegos.setTypeface(fuenteNormal);
        misJuegos.getPaint().setAntiAlias(true);
        misSets.setTypeface(fuenteNormal);
        misSets.getPaint().setAntiAlias(true);
        susPuntos.setTypeface(fuenteNormal);
        susPuntos.getPaint().setAntiAlias(true);
        susJuegos.setTypeface(fuenteNormal);
        susJuegos.getPaint().setAntiAlias(true);
        susSets.setTypeface(fuenteNormal);
        susSets.getPaint().setAntiAlias(true);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        refreshHour();
    }

    private void refreshHour() {
        try {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        hora.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAmbientEnabled();
        setContentView(R.layout.contador);

        dismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        dismissOverlay.setIntroText("Para salir de la aplicación, haz una pulsación larga");
        dismissOverlay.showIntroIfNecessary();

        partida = new Partida();
        vibrador = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        misPuntos = (TextView) findViewById(R.id.misPuntos);
        susPuntos = (TextView) findViewById(R.id.susPuntos);
        misJuegos = (TextView) findViewById(R.id.misJuegos);
        susJuegos = (TextView) findViewById(R.id.susJuegos);
        misSets = (TextView) findViewById(R.id.misSets);
        susSets = (TextView) findViewById(R.id.susSets);
        hora = (TextView) findViewById(R.id.hora);
        actualizaNumeros();
        View fondo = findViewById(R.id.fondo);
        fondo.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(Contador.this, new DireccionesGestureDetector.SimpleOnDireccionesGestureListener() {
                @Override
                public boolean onArriba(MotionEvent e1, MotionEvent e2, float distX, float distY) {
                    partida.rehacerPunto();
                    vibrador.vibrate(vibrDeshacer, -1);
                    actualizaNumeros();
                    return true;
                }

                @Override
                public boolean onAbajo(MotionEvent e1, MotionEvent e2, float distX, float distY) {
                    partida.deshacerPunto();
                    vibrador.vibrate(vibrDeshacer, -1);
                    actualizaNumeros();
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    dismissOverlay.show();
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent evento) {
                detector.onTouchEvent(evento);
                return true;
            }
        });
        misPuntos.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(Contador.this, new DireccionesGestureDetector.SimpleOnDireccionesGestureListener() {
                @Override
                public boolean onDerecha(MotionEvent e1, MotionEvent e2, float distX, float distY) {
                    partida.puntoPara(true);
                    vibrador.vibrate(vibrEntrada, -1);
                    actualizaNumeros();
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    dismissOverlay.show();
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent evento) {
                detector.onTouchEvent(evento);
                return true;
            }
        });
        susPuntos.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector detector = new DireccionesGestureDetector(Contador.this, new DireccionesGestureDetector.SimpleOnDireccionesGestureListener() {
                @Override
                public boolean onDerecha(MotionEvent e1, MotionEvent e2, float distX, float distY) {
                    partida.puntoPara(false);
                    vibrador.vibrate(vibrEntrada, -1);
                    actualizaNumeros();
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    dismissOverlay.show();
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent evento) {
                detector.onTouchEvent(evento);
                return true;
            }
        });
    }

    void actualizaNumeros() {
        misPuntos.setText(partida.getMisPuntos());
        susPuntos.setText(partida.getSusPuntos());
        misJuegos.setText(partida.getMisJuegos());
        susJuegos.setText(partida.getSusJuegos());
        misSets.setText(partida.getMisSets());
        susSets.setText(partida.getSusSets());
        sincronizaDatos();
    }

    class LoadBitmapFromAsset extends AsyncTask<Asset, Void, Bitmap> {
        private static final int TIMEOUT_MS = 2000;

        @Override
        protected Bitmap doInBackground(Asset... assets) {
            if (assets.length < 1 || assets[0] == null) {
                throw new IllegalArgumentException("El asset no puede ser null");
            }
            InputStream assetInputStream = null;
            try {
                assetInputStream = Tasks.await(Wearable.getDataClient(getApplicationContext()).getFdForAsset(assets[0])).getInputStream();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (assetInputStream == null) {
                Log.w("Sincronización", "Asset desconocido");
                return null;
            } // decodificamos el Stream en un Bitmap
            return BitmapFactory.decodeStream(assetInputStream);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            frameLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }
}
