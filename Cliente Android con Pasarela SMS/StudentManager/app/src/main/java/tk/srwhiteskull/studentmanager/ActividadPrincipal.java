package tk.srwhiteskull.studentmanager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Toast;
import tk.srwhiteskull.studentmanager.Fragmentos.AyudaFragment;
import tk.srwhiteskull.studentmanager.Fragmentos.SobreFragment;
import tk.srwhiteskull.studentmanager.Modelos.Alumno;

import java.io.File;

public class ActividadPrincipal extends AppCompatActivity {
    public Bundle cofre=new Bundle();
    public boolean esPrincipal=true;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isNetworkAvailable()) {
            stopService(new Intent(Registros.actividad, SMSgateway.class));
            System.exit(1);
            finish();
        }

        setContentView(R.layout.layout_actividad_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        Registros.init(this);

        // Cuando se pulsa sobre el icono del sobre
        findViewById(R.id.boton_sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(this,"Toast por defecto "+getSupportFragmentManager().getFragments().get(1).getClass(), Toast.LENGTH_SHORT).show();
                if (Registros.numSeleccionados>0){
                    final DialgoEnviarMensajeSMS dialogo=new DialgoEnviarMensajeSMS();
                    dialogo.onSms=new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Mensajero.enviarMensajes(v, dialogo.mensaje.getText().toString(),Registros.telefonosSeleccionados());
                            } catch (java.lang.SecurityException se) {
                                Toast.makeText(v.getContext(), "Compruebe que tiene permisos habilitados en el sistema (android 6+)", Toast.LENGTH_LONG).show();
                            } finally {
                                dialogo.dismiss();
                            }
                        }
                    };
                    dialogo.onEmail=new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Mensajero.enviarCorreos(v, dialogo.mensaje.getText().toString(),Registros.correosSeleccionados());
                            dialogo.dismiss();
                        }
                    };
                    dialogo.show(getSupportFragmentManager(),null);
                } else
                Snackbar.make(view, "Seleccione o marque los alumnos para enviar un mensaje", Snackbar.LENGTH_LONG)
                       .setAction("Action", null).show();
            }
        });

        // Iniciamos el servicio SMS gateway, que se encargará de leer los sms del servidor y enviarlos desde este dispositivo (donde este corriendo)
        startService(new Intent(this, SMSgateway.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actividad_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.accion_actualizar:
                File fichero = new File(getFilesDir(), "resultado.txt");
                fichero.delete();
                Registros.bitmaps.clear();
                for (Alumno alumno : Registros.registros ){
                    String nombreFichero = alumno.url.substring(alumno.url.lastIndexOf("/")+1);
                    fichero = new File(getFilesDir(), nombreFichero);
                    fichero.delete();
                }
                Registros.registros.clear();
                ((LinearLayout)Registros.principal.findViewById(R.id.listaDeAlumnos)).removeAllViews();
                Registros.principal.findViewById(R.id.bloqueador).setVisibility(View.VISIBLE);
                Mensajero.leerAlumnos();
                return true;
            case R.id.accion_poner_falta:
                if (Registros.numSeleccionados>0){
                    Registros.quitaPonFaltas("poner");
                } else
                    Snackbar.make(findViewById(R.id.boton_sms), "Seleccione o marque los alumnos que han faltado", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                return true;
            case R.id.accion_quitar_falta:
                if (Registros.numSeleccionados>0){
                    Registros.quitaPonFaltas("quitar");
                } else
                    Snackbar.make(findViewById(R.id.boton_sms), "Seleccione o marque los alumnos que quiera retirarle la falta", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                return true;
            case R.id.accion_salir:
                new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Cerrando aplicación")
                .setMessage("¿Estás seguro que quieres cerrar la aplicación?")
                .setNegativeButton("No", null)
                .setPositiveButton("Si", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopService(new Intent(Registros.actividad, SMSgateway.class));
                        System.exit(0);
                        finish();
                    }
                })
                .show();
                return true;
            case R.id.accion_sobre_la_app:
                cambiaFrame("", new SobreFragment());
                return true;
            case R.id.accion_ayuda:
                cambiaFrame("",new AyudaFragment());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cambiaFrame(String nombre, Fragment fragmento){
        esPrincipal=false;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().getBackStackEntryCount()>=1){
            if (getSupportFragmentManager().getFragments().get(1).getClass()!=fragmento.getClass()) {
                getSupportFragmentManager().popBackStack();
                ft.replace(R.id.fragment, fragmento);
                ft.addToBackStack(nombre);
            }
        } else {
            ft.add(R.id.fragment, fragmento);
            ft.addToBackStack(nombre);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        findViewById(R.id.boton_sms).setVisibility(View.INVISIBLE);
        ft.commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!esPrincipal) {
            menu.findItem(R.id.accion_actualizar).setVisible(false);
            menu.findItem(R.id.accion_poner_falta).setVisible(false);
            menu.findItem(R.id.accion_quitar_falta).setVisible(false);
        } else {
            menu.findItem(R.id.accion_actualizar).setVisible(true);
            menu.findItem(R.id.accion_poner_falta).setVisible(true);
            menu.findItem(R.id.accion_quitar_falta).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        esPrincipal=true;
        //Toast.makeText(this,"Toast por defecto "+getSupportFragmentManager().getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
        if (getSupportFragmentManager().getBackStackEntryCount()<1) {
            getSupportFragmentManager().popBackStack();
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Cerrando aplicación")
            .setMessage("¿Estás seguro que quieres cerrar la aplicación?")
            .setNegativeButton("No", null)
            .setPositiveButton("Si", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopService(new Intent(Registros.actividad, SMSgateway.class));
                    System.exit(0);
                    finish();
                }

            })
            .show();
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            findViewById(R.id.boton_sms).setVisibility(View.VISIBLE);
            super.onBackPressed();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        onBackPressed();
        return true;
    }
}
