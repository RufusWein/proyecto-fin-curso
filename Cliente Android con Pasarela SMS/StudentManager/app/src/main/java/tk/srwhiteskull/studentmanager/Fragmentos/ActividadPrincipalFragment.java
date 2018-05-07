package tk.srwhiteskull.studentmanager.Fragmentos;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.*;
import tk.srwhiteskull.studentmanager.*;
import tk.srwhiteskull.studentmanager.Modelos.Alumno;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ActividadPrincipalFragment extends Fragment {

    public ActividadPrincipalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actividad_principal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GifView gifview= view.findViewById(R.id.gifview);
        gifview.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // cheese
        gifview.setGifImageResource(R.drawable.cargando);

        int listaTotal=((LinearLayout)view.findViewById(R.id.listaDeAlumnos)).getChildCount();
        Registros.principal = view;

        final Spinner filtroDesplegable = view.findViewById(R.id.filtros_list);
        ArrayAdapter<String> filtroArrayAdapter = new ArrayAdapter<String>(
                view.getContext(),R.layout.elemento_lista ,
                Arrays.asList("Asistencia", "Grupo", "Edad")){

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                if (filtroDesplegable.getSelectedItemPosition()==position) {
                    view.setBackgroundColor(R.color.colorAccent);
                    view.setTextColor(Color.LTGRAY);
                }
                return view;
            }

        };

        filtroArrayAdapter.setDropDownViewResource(R.layout.elemento_lista);
        filtroDesplegable.setAdapter(filtroArrayAdapter);
        filtroDesplegable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                LinearLayout primeraFila = Registros.principal.findViewById(R.id.primera_fila);
                LayoutInflater inflater = Registros.actividad.getLayoutInflater();
                View contenedor = inflater.inflate(R.layout.opcion_asistencia,primeraFila, false);
                primeraFila.removeViewAt(1);

                if (view!=null)
                switch ((Registros.filtro=((TextView)view).getText().toString())) {
                    case "Asistencia":
                        ((CheckBox)contenedor).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                Listado.filtrarPorAsistencia(Registros.asistencia=isChecked);
                            }
                        });
                        Listado.filtrarPorAsistencia(Registros.asistencia);
                        break;

                    case "Grupo":
                        contenedor = inflater.inflate(R.layout.opcion_grupos,primeraFila,false);
                        final Spinner grupo_list= contenedor.findViewById(R.id.grupos_list);
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                contenedor.getContext() ,R.layout.elemento_lista , Registros.grupos()){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TextView elemento = (TextView)super.getView(position, convertView, parent);
                                elemento.setTextColor(R.color.gris);
                                return elemento;
                            }

                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent){
                                TextView elemento = (TextView) super.getDropDownView(position,convertView,parent);
                                if (grupo_list.getSelectedItemPosition()==position) {
                                    elemento.setBackgroundColor(R.color.colorAccent);
                                    elemento.setTextColor(Color.LTGRAY);
                                } else elemento.setTextColor(R.color.gris);
                                return elemento;
                            }
                        };
                        spinnerArrayAdapter.setDropDownViewResource(R.layout.elemento_lista);
                        grupo_list.setAdapter(spinnerArrayAdapter);
                        grupo_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Registros.grupoSeleccionado = position;
                                Listado.filtraPorGrupo(((TextView)view).getText().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) { }
                        });
                        grupo_list.setSelection(Registros.grupoSeleccionado);
                        break;
                    case "Edad":
                        contenedor = inflater.inflate(R.layout.opcion_edad,primeraFila,false);
                        final EditText min_text = contenedor.findViewById(R.id.min_edit),
                                 max_text = contenedor.findViewById(R.id.max_edit);
                        min_text.setText(""+Registros.edad_min);
                        min_text.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) { }

                            @Override
                            public void afterTextChanged(Editable s) {
                                String numero=s.toString();
                                if (numero.length()>0) {
                                    Registros.edad_min = Integer.parseInt(numero);
                                    if (Registros.edad_min<=Registros.edad_max) Listado.filtraPorEdad();
                                }
                            }
                        });
                        max_text.setText(""+Registros.edad_max);
                        max_text.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) { }

                            @Override
                            public void afterTextChanged(Editable s) {
                                String numero=s.toString();
                                if (numero.length()>0) {
                                    Registros.edad_max = Integer.parseInt(numero);
                                    if (Registros.edad_max>=Registros.edad_min) Listado.filtraPorEdad();
                                }
                            }
                        });
                        Listado.filtraPorEdad();

                }
                primeraFila.addView(contenedor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        final Spinner ordenarDesplegable = view.findViewById(R.id.criterios_list);
        ArrayAdapter<String> ordenarArrayAdapter = new ArrayAdapter<String>(
                view.getContext(), R.layout.elemento_lista ,
                Arrays.asList("Nombre ASC", "Nombre DESC", "Grupo ASC", "Grupo DESC", "Edad ASC", "Edad DESC")){

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                if (ordenarDesplegable.getSelectedItemPosition()==position) {
                    view.setBackgroundColor(R.color.colorAccent);
                    view.setTextColor(Color.LTGRAY);
                }
                return view;
            }

        };
        ordenarArrayAdapter.setDropDownViewResource(R.layout.elemento_lista);
        ordenarDesplegable.setAdapter(ordenarArrayAdapter);
        ordenarDesplegable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    String[] seleccion = (Registros.ordenar = ((TextView) view).getText().toString()).split(" ");
                    Listado.ordenarPor(seleccion[0], seleccion[1]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        ((CheckBox) view.findViewById(R.id.opcion_todos)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Registros.marcaTodos(isChecked);
            }
        });

        if (listaTotal==0) {
            Mensajero.leerAlumnos();
        }
        //Registros.crearNuevo("Pedro A. Rodriguez Gonzalez","45553667M","AAAAX","40",false,true);
        //Registros.crearNuevo("Marlo Rodriguez Gonzalez","45553667M","XAXAX","50",false,true);
        //Registros.crearNuevo("Pineda Rodriguez Gonzalez","45553667M","AAAAX","30",false,true);
        //Registros.crearNuevo("Adam Rodriguez Gonzalez","45553667M","AAAAX","12",false,true);
        //Registros.crearNuevo("Mery Rodriguez Gonzalez","45553667M","XAXAX","30",false,true);
        //Registros.crearNuevo("Tyson Rodriguez Gonzalez","45553667M","12345","15",false,true);
        //(LinearLayout)view.findViewById(R.id.)

    }
}
