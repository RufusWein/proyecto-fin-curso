package tk.srwhiteskull.studentmanager.Modelos;

import android.widget.FrameLayout;

public class Alumno {
    public int id;
    public String nombre,
                  dni,
                  grupo,
                  edad,

                  telefono,
                  email,

                  url;

    public FrameLayout contenedor;

    public boolean seleccionado=false,
                   visible     =true,
                   faltaHoy    =false;

    public Alumno(int id, String url, String nombre, String dni, String grupo, String edad, String telefono, String email, boolean faltaHoy, FrameLayout contenedor){
        this.id=id;
        this.url=url;
        this.nombre=nombre;
        this.dni=dni;
        this.grupo=grupo;
        this.edad=edad;
        this.telefono=telefono;
        this.email=email;
        this.faltaHoy=faltaHoy;
        this.contenedor=contenedor;
    }

}
