<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;

class ControladorDeAlumnos extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */

    public function index(){ //Aplicacione Web
        return view("alumnos.indice",[ "alumnos" => $this->listadoJSON()] );        
    }
    
    public function listadoJSON(){  // Adnroid
        $json=[];
        foreach (\App\Alumno::get() as $alumno){
            $contacto=\App\Contacto::where("alumno_id",$alumno->id)->get();
            if (isset($contacto)) { // Si no existe creamos uno por defecto
                // O tambien podriamos crear un valor que la aplicacion
                // pudiera interpretar como que no es posible ponerse en contacto 
                $contacto = new \App\Contacto();
                $contacto->telefono = "+34628874763";
                $contacto->email    = "rgpedroa@gmail.com";
                $contacto->alumno_id= $alumno->id;
                $contacto->save();
            }
            
            $alumno=[              
              "id"      => $alumno->id,
              "url"      => $alumno->foto(),                
              "nombre"  => $alumno->nombreCompleto(null),
              "dni"     => $alumno->dni,
              "grupo"   => $alumno->grupo_id,
              "edad"    => $alumno->edad(),
              "telefono"=> $contacto->telefono,
              "email"   => $contacto->email,
              "faltaHoy"=> (\App\Falta::where("alumno_id",$alumno->id)->where("fecha", date('Y-m-d'))->count()>0?true:false)
                    ];
            array_push($json, $alumno);
        }
        
        return json_encode($json);
    }
    
    /*public function guardaf(Request $request)
    {  // Esto era para una cochinada que hice para recabar imagenes para mis falsos esdtudiantes XD
       // algunas imagenes no era apropiadas o no se caragaban asi que tenia que decidir manualmente si eran aptas o no
        $fotos=$request->fotos;
        
        foreach ($fotos as $foto){
            $registro=new \App\Foto;
            $registro->fill(["url"=>$foto[0],"alumno_id"=>$foto[1]])->save();
        }
        
        return "OK";
    }*/

}
