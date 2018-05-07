package tk.srwhiteskull.studentmanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tk.srwhiteskull.studentmanager.Modelos.Alumno;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Mensajero {
    public static void enviarMensajes(final View view, final String mensaje, final String [] telefonos){
        if (mensaje.length()>0) {
            AlertDialog.Builder dialogo = new AlertDialog.Builder(view.getContext());
            dialogo.setIcon(android.R.drawable.ic_dialog_alert);
            dialogo.setTitle("Confirmar envío");
            dialogo.setMessage("¿Estás seguro que quieres enviar el mensaje :\n'"+mensaje+"'?");
            dialogo.setNegativeButton("No", null);
            dialogo.setPositiveButton("Si", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Mejor con un bucle
                    SmsManager smsManager = SmsManager.getDefault();
                        try {

                        for (String telefono : telefonos) {
                                smsManager.sendTextMessage(telefono, null, mensaje, null, null);
                        }

                        Toast.makeText(view.getContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show();
                    } catch (java.lang.SecurityException se) {
                        Toast.makeText(view.getContext(), "El sistema no permite enviar SMS", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialogo.show();
        } else {
            Toast.makeText(view.getContext(), "No hay mensaje para enviar", Toast.LENGTH_SHORT).show();
        }
    }

    public static void enviarCorreos(final View view, final String mensaje, final String correos){
        if (mensaje.length()>0) {
            AlertDialog.Builder dialogo = new AlertDialog.Builder(view.getContext());
            dialogo.setIcon(android.R.drawable.ic_dialog_alert);
            dialogo.setTitle("Confirmar envío");
            dialogo.setMessage("¿Estás seguro que quieres enviar el mensaje :\n'"+mensaje+"'?");
            dialogo.setNegativeButton("No", null);
            dialogo.setPositiveButton("Si", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Mejor con un bucle
                    //for (String telefono : telefonos) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            StringBuffer chaine = new StringBuffer("");
                            try{
                                URL url = new URL("http://srwhiteskull.tk/servicios/email?mensaje="+mensaje+"&destinatarios="+correos);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

                                Registros.actividad.cofre.putString("resultado",chaine.toString());
                            } catch(IOException e) {
                                // Writing exception to log
                                e.printStackTrace();
                            }
                            Registros.actividad.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(view.getContext(), Registros.actividad.cofre.getString("resultado"),Toast.LENGTH_SHORT).show();
                                }
                            });

                        }


                    }.start();
                }
            });
            dialogo.show();
        } else {
            Toast.makeText(view.getContext(), "No hay mensaje para enviar", Toast.LENGTH_SHORT).show();
        }


    } // fin enviar correo

    public static void leerAlumnos(){

        // comprobamos que existe el fichero resultado
        final File fichero = new File(Registros.principal.getContext().getFilesDir(), "resultado.txt");

        if (!fichero.exists()){
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    StringBuffer chaine = new StringBuffer("");
                    try{
                        URL url = new URL("http://srwhiteskull.tk/servicios/listado");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

                        Registros.actividad.cofre.putString("resultado",chaine.toString());
                    } catch(IOException e) {
                        // Writing exception to log
                        e.printStackTrace();
                    }

                    try {
                        JSONArray ja=new JSONArray(chaine.toString());
                        for (int i=0; i<ja.length();i++) {
                            final JSONObject jo = ja.getJSONObject(i);

                            ((Activity)Registros.principal.getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Registros.crearNuevo( jo.getInt("id"),
                                                jo.getString("url"),
                                                jo.getString("nombre"),
                                                jo.getString("dni"),
                                                jo.getString("grupo"),
                                                jo.getString("edad"),
                                                jo.getString("telefono"),
                                                jo.getString("email"),
                                                jo.getBoolean("faltaHoy"),false,true);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            Thread.sleep(25);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ((Activity)Registros.principal.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Registros.principal.findViewById(R.id.bloqueador).setVisibility(View.GONE);
                            Mensajero.quitaPonFaltas("","", false);
                            Listado.ordenarPor("Nombre", "ASC");
                            Toast.makeText(Registros.principal.getContext(), "Carga finalizada", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }.start();

        //cargamos fichero
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if ((System.currentTimeMillis()-fichero.lastModified())>(24*3600000)) {
                        fichero.delete();
                        Mensajero.leerAlumnos();
                    // en caso de ser inferior a 24 horas cargamos el ficher
                    } else {
                        try {
                            char [] cadena = new char[(int) fichero.length()];
                            FileReader writer = new FileReader(fichero);
                            writer.read(cadena);
                            writer.close();

                            try {
                                JSONArray ja=new JSONArray(new String(cadena));
                                for (int i=0; i<ja.length();i++) {
                                    final JSONObject jo = ja.getJSONObject(i);

                                    ((Activity)Registros.principal.getContext()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Registros.crearNuevo( jo.getInt("id"),
                                                        jo.getString("url"),
                                                        jo.getString("nombre"),
                                                        jo.getString("dni"),
                                                        jo.getString("grupo"),
                                                        jo.getString("edad"),
                                                        jo.getString("telefono"),
                                                        jo.getString("email"),
                                                        jo.getBoolean("faltaHoy"),false,true);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    Thread.sleep(25);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            ((Activity)Registros.principal.getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Registros.principal.findViewById(R.id.bloqueador).setVisibility(View.GONE);
                                    Mensajero.quitaPonFaltas("","", false);
                                    Listado.ordenarPor("Nombre", "ASC");
                                    Toast.makeText(Registros.principal.getContext(), "Carga finalizada", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                        }
            }).start();

        }
    }

    public static void quitaPonFaltas(final String accion, final String alumnos_ids, final boolean mensaje)  {
        new Thread() {
            @Override
            public void run() {
                super.run();
                StringBuffer chaine = new StringBuffer("");
                try{
                    String parametros="";
                    if (accion!="" && alumnos_ids!="") parametros= "?"+accion+"="+ alumnos_ids;
                    URL url = new URL("http://srwhiteskull.tk/servicios/faltas"+parametros );
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

                    Registros.actividad.cofre.putString("resultado",chaine.toString());
                } catch(IOException e) {
                    // Writing exception to log
                    e.printStackTrace();
                }

                Registros.actividad.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String[] ids = Registros.actividad.cofre.getString("resultado").split(",");
                            for (Alumno alumno : Registros.registros) {
                                alumno.faltaHoy = false;
                                alumno.contenedor.findViewById(R.id.falta).setVisibility(View.INVISIBLE);
                                for (int i = 0; i < ids.length; i++) {
                                    if ((alumno.id + "").equals(ids[i])) {
                                        alumno.faltaHoy = true;
                                        alumno.contenedor.findViewById(R.id.falta).setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                            if (Registros.filtro.equals("Asistencia"))
                                Listado.filtrarPorAsistencia(Registros.asistencia);
                            if (mensaje)
                                Toast.makeText(Registros.actividad, "Faltas actualizadas", Toast.LENGTH_SHORT).show();
                        } catch (java.lang.NullPointerException n){
                            Toast.makeText(Registros.actividad, "Problema con la conexión", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } // fin run (hilo)

        }.start();
    }
}
