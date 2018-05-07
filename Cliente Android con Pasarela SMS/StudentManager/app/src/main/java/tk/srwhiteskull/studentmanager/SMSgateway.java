package tk.srwhiteskull.studentmanager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.*;
import android.os.Process;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SMSgateway extends Service {
    Handler hilo; // Hilo seguro, tambien podia haber usado simplemente Thread
    Looper bucleInfinito;
    boolean estado=false;
    NotificationCompat.Builder notificacion;
    NotificationManagerCompat notificador;

    @Override
    public void onCreate() {
        super.onCreate();

        Intent actividadInten = Registros.actividad.getIntent();
        notificacion = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.icono_app))
                .setSmallIcon(R.mipmap.icono_app)
                .setContentTitle("Pasarela SMS")
                .setAutoCancel(true)
                .setGroup(getResources().getString(R.string.app_name))
                .setContentTitle("Pasarela SMS").setContentText("El sistema no permite enviar SMS...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(PendingIntent.getActivity(this, 0, actividadInten, 0));
        notificador = NotificationManagerCompat.from(this);

        HandlerThread ht = new HandlerThread("SMSgateway", Process.THREAD_PRIORITY_BACKGROUND);
        ht.start();
        bucleInfinito = ht.getLooper();
        hilo = new Handler(bucleInfinito){
            SmsManager smsManager = SmsManager.getDefault();
            StringBuffer chaine;
            String ids;
            boolean enviado;
            long segundos=1;

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                synchronized (this){
                    try {
                        while (estado) {
                            chaine = new StringBuffer("");
                            ids="";
                            //notificador.cancelAll();
                            try{
                                Log.d("Debug","Contador de segundos = "+(segundos % 59));
                                // Solicitamos mensajes SMS del servidor para enviar (bandeja de salida)
                                if ((segundos % 60)==59) { // comienza desde cero
                                    Log.d("Debug","Solicitando mensajes SMS desde el servidor");
                                    final URL url = new URL("http://srwhiteskull.tk/servicios/smss"), confirmaURL;
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(), confirmacion;
                                    connection.setRequestProperty("User-Agent", "");
                                    connection.setRequestMethod("GET");
                                    connection.setDoInput(true);
                                    connection.connect();

                                    InputStream inputStream = connection.getInputStream();

                                    BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                                    String line = "";
                                    while ((line = rd.readLine()) != null) {
                                        chaine.append(line);
                                    }

                                    // Enviamos SMSs desde este dispositivo
                                    try {
                                        JSONArray ja = new JSONArray(chaine.toString());
                                        for (int i = 0; i < ja.length(); i++) {
                                            JSONObject jo = ja.getJSONObject(i);
                                            ids += jo.getInt("id") + ",";

                                            smsManager.sendTextMessage(jo.getString("telefono"), null, jo.getString("mensaje"), null, null);
                                        }

                                        enviado = true;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (StringIndexOutOfBoundsException so) {
                                        so.printStackTrace();
                                    } catch (java.lang.SecurityException se) {
                                        notificador.notify(GeneradorID.getID(), notificacion.setContentText("El sistema no permite enviar SMS...").build());
                                        enviado = false;
                                    }

                                    if (ids != "" && enviado) {
                                        if (ids.substring(ids.length() - 1).equals(","))
                                            ids = ids.substring(0, ids.length() - 1);

                                        // Eliminamos mensajes del servidor (bandeja de salida)
                                        confirmaURL = new URL("http://srwhiteskull.tk/servicios/enviados?ids=" + ids);
                                        confirmacion = (HttpURLConnection) confirmaURL.openConnection();
                                        confirmacion.setRequestProperty("User-Agent", "");
                                        confirmacion.setRequestMethod("GET");
                                        confirmacion.setDoInput(true);
                                        confirmacion.connect();
                                        inputStream = confirmacion.getInputStream();

                                        rd = new BufferedReader(new InputStreamReader(inputStream));
                                        line = "";
                                        while ((line = rd.readLine()) != null) {
                                            chaine.append(line);
                                        }

                                        notificador.notify(GeneradorID.getID(), notificacion.setContentText("Mensaje SMS enviado...").build());
                                    }
                                } // fin solicitud de mensajes SMS

                                if ((segundos % 10)==9) { // cada 30 segundos
                                    Log.d("Debug","Solicitando actualizacion de faltas desde el servidor.");
                                    Mensajero.quitaPonFaltas("","",true);
                                }
                            } catch(IOException e) {
                                // Writing exception to log
                                e.printStackTrace();
                            }

                            Thread.sleep(1000);
                            segundos++;
                        } // fin bucle del hilo
                    } catch (InterruptedException e) {
                        notificador.notify(GeneradorID.getID(), notificacion.setContentText("No hay conexión...").build());
                    }
                    stopSelfResult(msg.arg1);
                }
            }
        };
        estado=true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = hilo.obtainMessage();
        msg.arg1 = startId;
        hilo.sendMessage(msg);
        return START_STICKY;//super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        estado=false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;//throw new UnsupportedOperationException("Not yet implemented");
    }
}
