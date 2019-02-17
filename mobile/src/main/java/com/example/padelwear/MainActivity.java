package com.example.padelwear;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.comun.Constants.ARG_START_REMOTE_ACTIVITY;
import static com.example.comun.Constants.ASSET_FOTO;
import static com.example.comun.Constants.ITEM_FOTO;
import static com.example.comun.Constants.WEAR_ARRANCAR_ACTIVIDAD;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_RESPUESTA_POR_VOZ = "extra_respuesta_por_voz";
    NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;
    final static String MI_GRUPO_DE_NOTIFIC = "mi_grupo_de_notific";
    public static final String EXTRA_MESSAGE="com.example.padelwear.EXTRA_MESSAGE";
    public static final String ACTION_DEMAND="com.example.padelwear.ACTION_DEMAND";

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

        //Miramos si hemos recibido una respuesta por voz
        Bundle respuesta = RemoteInput.getResultsFromIntent(getIntent());
        if (respuesta != null) {
            CharSequence texto = respuesta.getCharSequence(EXTRA_RESPUESTA_POR_VOZ);
            ((TextView) findViewById(R.id.textViewRespuesta)).setText(texto);
            homework1();
            return;
        }

        try {
            Log.d("MainActivity", ">>> mobile");
            if (!getIntent().getBooleanExtra(ARG_START_REMOTE_ACTIVITY, false)) {
                callRemoteActivity();
            }
        } catch (Exception e) {
            callRemoteActivity();
        }

        homework1();
    }

    private void homework1() {

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripcion del canal");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 100, 300, 100});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Button wearButton = (Button) findViewById(R.id.boton1);
        wearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String s = "Texto largo con descripción detallada de la notificación. ";

                Intent intencionLlamar = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:555123456"));
                PendingIntent intencionPendienteLlamar =
                        PendingIntent.getActivity(MainActivity.this, 0, intencionLlamar, 0);

                // Creamos intención pendiente
                Intent intencionMapa = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=universidad+politecnica+valencia"));
                PendingIntent intencionPendienteMapa =
                        PendingIntent.getActivity(MainActivity.this, 0, intencionMapa, 0);

                // Creamos la acción
                NotificationCompat.Action accion =
                        new NotificationCompat.Action.Builder(R.mipmap.ic_action_call,
                                "llamar Wear", intencionPendienteLlamar).build();

                //Creamos una lista de acciones
                List<NotificationCompat.Action> acciones =
                        new ArrayList<NotificationCompat.Action>();
                acciones.add(accion);
                acciones.add(new NotificationCompat.Action(R.mipmap.ic_action_locate,
                        "Ver mapa", intencionPendienteMapa));

                // Creamos un BigTextStyle para la segunda página
                NotificationCompat.BigTextStyle segundaPg = new NotificationCompat.BigTextStyle();
                segundaPg.setBigContentTitle("Página 2").bigText("Más texto, pág 2.");

                Notification notificacionPg2 = new NotificationCompat.Builder(MainActivity.this)
                        .setStyle(segundaPg)
                        .build();

                // Creamos un BigTextStyle para la segunda página
                NotificationCompat.BigTextStyle terceraPg = new NotificationCompat.BigTextStyle();
                segundaPg.setBigContentTitle("Página 3").bigText("Aún más texto, pág 3.");

                Notification notificacionPg3 = new NotificationCompat.Builder(MainActivity.this)
                        .setStyle(terceraPg)
                        .build();

                // Creamos un BigTextStyle para la segunda página
                NotificationCompat.BigTextStyle cuartaPg = new NotificationCompat.BigTextStyle();
                segundaPg.setBigContentTitle("Página 4").bigText("El último texto, pág 4.");

                Notification notificacionPg4 = new NotificationCompat.Builder(MainActivity.this)
                        .setStyle(cuartaPg)
                        .build();

                List<Notification> notificationPages = new ArrayList<>();
                notificationPages.add(notificacionPg2);
                notificationPages.add(notificacionPg3);
                notificationPages.add(notificacionPg4);

                NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.escudo_upv))
                        .addActions(acciones)
                        .addPages(notificationPages);

                NotificationCompat.Builder notificacion =
                        new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Título")
                                .setContentText(Html.fromHtml("<b>Notificación</b> <u>Android <i>Wear</i></u>"))
                                .setContentIntent(intencionPendienteMapa)
                                .addAction(R.mipmap.ic_action_call, "llamar", intencionPendienteLlamar)
                                //.extend(new NotificationCompat.WearableExtender().addActions(acciones))
                                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.escudo_upv))
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(s + s + s + s))
                                .extend(wearableExtender)
                                .setGroup(MI_GRUPO_DE_NOTIFIC);
                notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                Toast.makeText(MainActivity.this, "Solo se ve hasta la 2da página wear2", Toast.LENGTH_SHORT).show();


                int idNotificacion2 = 002;
                NotificationCompat.Builder notificacion2 =
                        new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                                .setContentTitle("Nueva Conferencia")
                                .setContentText("Los neutrinos")
                                .setSmallIcon(R.mipmap.ic_action_mail_add)
                                .setGroup(MI_GRUPO_DE_NOTIFIC);
                notificationManager.notify(idNotificacion2, notificacion2.build());


                int idNotificacion3 = 003;
                NotificationCompat.Builder notificacion3 =
                        new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                                .setContentTitle("2 notificaciones UPV")
                                .setSmallIcon(R.mipmap.ic_action_attach)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.escudo_upv))
                                .setStyle(new NotificationCompat.InboxStyle()
                                        .addLine("Nueva Conferencia Los neutrinos")
                                        .addLine("Nuevo curso Wear OS")
                                        .setBigContentTitle("2 notificaciones UPV")
                                        .setSummaryText("info@upv.es"))
                                .setNumber(2)
                                .setGroup(MI_GRUPO_DE_NOTIFIC)
                                .setGroupSummary(true);
                notificationManager.notify(idNotificacion3, notificacion3.build());

            }
        });


        Button butonVoz = (Button) findViewById(R.id.boton_voz);
        butonVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creamos una intención de respuesta
                Intent intencion = new Intent(MainActivity.this, MainActivity.class);
                PendingIntent intencionPendiente = PendingIntent.getActivity(MainActivity.this, 0, intencion, PendingIntent.FLAG_UPDATE_CURRENT);

                String[] opcRespuesta = getResources().getStringArray(R.array.opciones_respuesta);

                // Creamos la entrada remota para añadirla a la acción
                RemoteInput entradaRemota = new RemoteInput.Builder(EXTRA_RESPUESTA_POR_VOZ)
                        .setLabel("respuesta por voz")
                        .setChoices(opcRespuesta)
                        .build();
                // Creamos la acción
                NotificationCompat.Action accion = new NotificationCompat.Action.Builder(
                        android.R.drawable.ic_menu_set_as, "responder", intencionPendiente)
                        .addRemoteInput(entradaRemota).build();
                // Creamos la notificación
                int idNotificacion = 004;
                NotificationCompat.Builder notificacion4 =
                        new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Respuesta por Voz")
                                .setContentText("Indica una respuesta")
                                .extend(new NotificationCompat.WearableExtender()
                                        .addAction(accion));
                // Lanzamos la notificación
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                notificationManager.notify(idNotificacion, notificacion4.build());
            }
        });

        Button buttonVozBroadcast = (Button) findViewById(R.id.boton_voz_broadcast);
        buttonVozBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creamos una intención de respuesta
                Intent intencion = new Intent(MainActivity.this, WearReceiver.class)
                        .putExtra(EXTRA_MESSAGE, "alguna información relevante")
                        .setAction(ACTION_DEMAND);

                PendingIntent intencionPendiente = PendingIntent.getBroadcast(MainActivity.this, 0, intencion, 0);

                String[] opcRespuesta = getResources().getStringArray(R.array.opciones_respuesta);

                // Creamos la entrada remota para añadirla a la acción
                RemoteInput entradaRemota = new RemoteInput.Builder(EXTRA_RESPUESTA_POR_VOZ)
                        .setLabel("respuesta por voz")
                        .setChoices(opcRespuesta)
                        .build();
                // Creamos la acción
                NotificationCompat.Action accion = new NotificationCompat.Action.Builder(
                        android.R.drawable.ic_menu_set_as, "responder", intencionPendiente)
                        .addRemoteInput(entradaRemota).build();

                // Creamos la notificación
                int idNotificacion = 005;
                NotificationCompat.Builder notificacion4 =
                        new NotificationCompat.Builder(MainActivity.this, CANAL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Respuesta por Voz")
                                .setContentText("Indica una respuesta")
                                .extend(new NotificationCompat.WearableExtender()
                                        .addAction(accion));
                // Lanzamos la notificación
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                notificationManager.notify(idNotificacion, notificacion4.build());
            }
        });
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
        } else if (id == R.id.take_photo) {
            mandarFoto();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mandarFoto() {
        Intent intencion = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intencion.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intencion, 1234);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent datos) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            Bundle extras = datos.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            Asset asset = createAssetFromBitmap(bitmap);
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create(ITEM_FOTO);
            putDataMapReq.getDataMap().putAsset(ASSET_FOTO, asset);
            putDataMapReq.getDataMap().putLong("marca_de_tiempo", new Date().getTime());
            PutDataRequest request = putDataMapReq.asPutDataRequest();
            Wearable.getDataClient(getApplicationContext()).putDataItem(request);
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
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
