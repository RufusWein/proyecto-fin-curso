package tk.srwhiteskull.studentmanager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import tk.srwhiteskull.studentmanager.Modelos.Alumno;

import java.io.*;
import java.net.URL;
import java.util.*;

import static android.content.ContentValues.TAG;

public class Registros {
    public static ActividadPrincipal actividad;
    public static ArrayList<Alumno> registros=new ArrayList<>();
    private static Set<String> grupos = new LinkedHashSet<>();
    public static int edad_min=0;
    public static int edad_max=99;
    public static int grupoSeleccionado=0;
    public static View principal;
    protected static int numSeleccionados;
    public static boolean asistencia=false;
    public static String filtro="Asistencia";
    public static String ordenar="Nombre ASC";
    public static Map<String, Bitmap> bitmaps = new HashMap<>();

    public static void crearNuevo(int id, String url, String nombre, String dni, String grupo, String edad, String telefono, String email, boolean faltaHoy,boolean seleccionado, boolean visible ) {
        LinearLayout listaDeAlumnos = Registros.principal.findViewById(R.id.listaDeAlumnos);
        LayoutInflater inflater = ((Activity)Registros.principal.getContext()).getLayoutInflater();
        FrameLayout contenedor = (FrameLayout) inflater.inflate(R.layout.registro_plantilla,null);//, listaDeAlumnos);//,null);
        //url="http://2.bp.blogspot.com/-L7Htgvant3Q/Toyc_3j2SFI/AAAAAAAAAYU/uvGXF_GZHiQ/s1600/rajoy-sorprendido.jpg";
        if (bitmaps.get(url)==null) { // cache
            new MiCargadorDeImagenesMAGIC(Registros.principal.getContext(), (ImageView) contenedor.findViewById(R.id.retrato)).setImageDrawable(url);
        } else {
            ((ImageView) contenedor.findViewById(R.id.retrato)).setImageBitmap(bitmaps.get(url));
        }

        TextView nombre_text = contenedor.findViewById(R.id.nombre_text),
                 dni_text    = contenedor.findViewById(R.id.dni_text),
                 grupo_text  = contenedor.findViewById(R.id.grupo_text),
                 edad_text   = contenedor.findViewById(R.id.edad_text);

        ImageView falta_img  = contenedor.findViewById(R.id.falta);
        Registros.grupos.add(grupo);

        CheckBox checkBox    = contenedor.findViewById(R.id.checkBox);

        final Alumno alumno=new Alumno(id, url, nombre, dni, grupo, edad, telefono, email, faltaHoy, contenedor);
        registros.add(alumno);

        nombre_text.setText(Html.fromHtml("<strong>Nombre Completo : </strong>"+nombre));
        dni_text.setText(Html.fromHtml("<strong>DNI : </strong>"+dni));
        grupo_text.setText(Html.fromHtml("<strong>Grupo ID : </strong>"+grupo));
        edad_text.setText(Html.fromHtml("<strong>Edad : </strong>"+edad));
        falta_img.setVisibility((faltaHoy?View.VISIBLE:View.INVISIBLE));
        checkBox.setChecked(seleccionado);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toast.makeText(actividad,"Toast por defecto", Toast.LENGTH_SHORT).show();
                alumno.seleccionado=isChecked;
                Registros.actualizaInfo();
            }
        });

        if (!(alumno.visible=visible)) contenedor.setVisibility(View.GONE);
        listaDeAlumnos.addView(contenedor);
        Registros.actualizaInfo();
    }

    protected static void actualizaInfo() {
        int contadorSeleccionados=0, contador=0;

        for (Alumno alumno: registros ) {
            if (alumno.contenedor.getVisibility()== View.VISIBLE) {
                contador++;
                if (alumno.seleccionado) contadorSeleccionados++;
            }
        }
        Registros.numSeleccionados=contadorSeleccionados;
        TextView info= Registros.principal.findViewById(R.id.seleccionados_text);
        info.setText("Seleccionados "+contadorSeleccionados+" de "+contador);
    }

    public static void init(ActividadPrincipal actividad) {
        Registros.actividad=actividad;
    }

    public static void marcaTodos(boolean isChecked) {
        for (Alumno alumno: registros ) {
            alumno.seleccionado=isChecked;
            ((CheckBox)alumno.contenedor.findViewById(R.id.checkBox)).setChecked(isChecked);
        }
        Registros.actualizaInfo();
    }

    public static String[] grupos() {
        return Registros.grupos.toArray(new String[0]);
    }

    public static String[] telefonosSeleccionados() {
        String telefonos="";
        for (Alumno alumno: registros ) {
            if (alumno.contenedor.getVisibility()== View.VISIBLE) {
                if (alumno.seleccionado) telefonos+=alumno.telefono+",";
            }
        }

        if (telefonos!="") telefonos=telefonos.substring(0,telefonos.length()-1);

        return telefonos.split(",");
    }

    public static String correosSeleccionados() {
        String emails="";
        for (Alumno alumno: registros ) {
            if (alumno.contenedor.getVisibility()== View.VISIBLE) {
                if (alumno.seleccionado) emails+=alumno.email+",";
            }
        }

        if (emails!="") emails=emails.substring(0,emails.length()-1);

        return emails;
    }

    public static void quitaPonFaltas(String accion) {
        String alumnos_ids="";
        for (Alumno alumno: registros ) {
            if (alumno.contenedor.getVisibility()== View.VISIBLE) {
                if (alumno.seleccionado) alumnos_ids+=alumno.id+",";
            }
        }
        if (alumnos_ids!="") alumnos_ids=alumnos_ids.substring(0,alumnos_ids.length()-1);

        Mensajero.quitaPonFaltas(accion,alumnos_ids, true);
    }

    /*public static void filtraYordena(){
        switch (Registros.filtro){
            case "Asistencia":
                Listado.filtrarPorAsistencia(Registros.asistencia);
                break;
            case "Grupo":
                Listado.filtraPorGrupo(Registros.grupos()[Registros.grupoSeleccionado]);
                break;
            case "Edad":
                Listado.filtraPorEdad();
        }
        String[] seleccion = Registros.ordenar.split(" ");
        Listado.ordenarPor(seleccion[0], seleccion[1]);
    }*/

}
