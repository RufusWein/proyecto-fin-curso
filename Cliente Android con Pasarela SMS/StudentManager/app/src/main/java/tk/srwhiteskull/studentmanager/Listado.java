package tk.srwhiteskull.studentmanager;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.*;
import tk.srwhiteskull.studentmanager.Modelos.Alumno;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;

public class Listado {
    public static void filtraPorEdad() {
        for (Alumno alumno : Registros.registros) {
            if (Integer.parseInt(alumno.edad) < Registros.edad_min ||
                    Integer.parseInt(alumno.edad) > Registros.edad_max) {
                alumno.visible = (alumno.seleccionado = false);
                alumno.contenedor.setVisibility(View.GONE);
                ((CheckBox) alumno.contenedor.findViewById(R.id.checkBox)).setChecked(alumno.seleccionado);
            } else {
                alumno.visible = true;
                alumno.contenedor.setVisibility(View.VISIBLE);
            }
        }
        Registros.actualizaInfo();
        ((CheckBox) Registros.actividad.findViewById(R.id.opcion_todos)).setChecked(false);
    }

    public static void filtraPorGrupo(String grupo_id) {
        for (Alumno alumno : Registros.registros) {
            if (!alumno.grupo.contains(grupo_id)) {
                alumno.visible = alumno.seleccionado = false;
                alumno.contenedor.setVisibility(View.GONE);
                ((CheckBox) alumno.contenedor.findViewById(R.id.checkBox)).setChecked(alumno.seleccionado);
            } else {
                alumno.visible = true;
                alumno.contenedor.setVisibility(View.VISIBLE);
            }
        }
        Registros.actualizaInfo();
        ((CheckBox) Registros.actividad.findViewById(R.id.opcion_todos)).setChecked(false);
    }

    public static void ordenarPor(final String criterio, final String opcion) {
        LinearLayout listaDeAlumnos = Registros.principal.findViewById(R.id.listaDeAlumnos);
        Alumno [] listaOrdenada=Registros.registros.toArray(new Alumno[0]);

        Arrays.sort(listaOrdenada, new Comparator<Alumno>() {
            @Override
            public int compare(Alumno alumno1, Alumno alumno2) {
                int resultado=0;
                switch (criterio){
                    case "Nombre":
                        resultado = alumno1.nombre.compareTo(alumno2.nombre);
                        break;
                    case "Grupo":
                        resultado = alumno1.grupo.compareTo(alumno2.grupo);
                        break;
                    case "Edad":
                        resultado = alumno1.edad.compareTo(alumno2.edad);
                }
                return (opcion.contains("ASC") && resultado!=0)?resultado*-1:resultado;
            }
        });

        Registros.registros = new ArrayList(Arrays.asList(listaOrdenada));

        int indice=0;
        for (final Alumno alumno: Registros.registros ) {
            //Registros.crearNuevo(alumno.id, alumno.url, alumno.nombre,alumno.dni, alumno.grupo, alumno.edad, alumno.telefono, alumno.email, alumno.faltaHoy, alumno.seleccionado, alumno.visible);
            FrameLayout contenedor = (FrameLayout) listaDeAlumnos.getChildAt(indice++);

            if (contenedor!=null) {
                TextView nombre_text = contenedor.findViewById(R.id.nombre_text),
                        dni_text = contenedor.findViewById(R.id.dni_text),
                        grupo_text = contenedor.findViewById(R.id.grupo_text),
                        edad_text = contenedor.findViewById(R.id.edad_text);
                ImageView falta_img = contenedor.findViewById(R.id.falta);
                CheckBox checkBox = contenedor.findViewById(R.id.checkBox);
                alumno.contenedor = contenedor;
                nombre_text.setText(Html.fromHtml("<strong>Nombre Completo : </strong>" + alumno.nombre));
                dni_text.setText(Html.fromHtml("<strong>DNI : </strong>" + alumno.dni));
                grupo_text.setText(Html.fromHtml("<strong>Grupo ID : </strong>" + alumno.grupo));
                edad_text.setText(Html.fromHtml("<strong>Edad : </strong>" + alumno.edad));
                falta_img.setVisibility((alumno.faltaHoy ? View.VISIBLE : View.INVISIBLE));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //Toast.makeText(actividad,"Toast por defecto", Toast.LENGTH_SHORT).show();
                        alumno.seleccionado = isChecked;
                        Registros.actualizaInfo();
                    }
                });
                checkBox.setChecked(alumno.seleccionado);

                if (!alumno.visible) contenedor.setVisibility(View.GONE);
                else contenedor.setVisibility(View.VISIBLE);

                ImageView retrato = contenedor.findViewById(R.id.retrato);
                if (Registros.bitmaps.get(alumno.url) == null) { // cache
                    new MiCargadorDeImagenesMAGIC(Registros.principal.getContext(), retrato).setImageDrawable(alumno.url);
                } else {
                    retrato.setImageBitmap(Registros.bitmaps.get(alumno.url));
                }
            }
        }
        Registros.actualizaInfo();
    }

    public static void filtrarPorAsistencia(boolean isChecked) {
        for (Alumno alumno : Registros.registros) {
            if (alumno.faltaHoy!=isChecked) {
                alumno.visible = (alumno.seleccionado = false);
                alumno.contenedor.setVisibility(View.GONE);
                ((CheckBox) alumno.contenedor.findViewById(R.id.checkBox)).setChecked(alumno.seleccionado);
            } else {
                alumno.visible = true;
                alumno.contenedor.setVisibility(View.VISIBLE);
            }
        }
        Registros.actualizaInfo();
        ((CheckBox) Registros.principal.findViewById(R.id.opcion_todos)).setChecked(false);
    }
}
