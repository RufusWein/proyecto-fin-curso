<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;

class ControladorDeFaltas extends Controller
{
    public function accion(Request $request){
        if (isset($request->poner)) {
            $alumnos_ids=explode(",", $request->poner);
            foreach ($alumnos_ids as $alumno_id){
                \App\Falta::ponerFaltaHoy($alumno_id);
            }        
        } else // Al principio queria que hiciera las dos acciones
               // desde una unica consulta pero luego es mas lioso,
               // mejor otro dia XD jajajja
        if (isset($request->quitar)) {
            $alumnos_ids=explode(",", $request->quitar);
            foreach ($alumnos_ids as $alumno_id){
                \App\Falta::quitarFaltaHoy($alumno_id);
            }        
        }
        
        return \App\Falta::losAusentesDeHoy();
    }
}
