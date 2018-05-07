<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;

// Medidador de la pasarela SMS
class ControladorSMS extends Controller
{
    // PASO WEB
    // Esta funcion solo es para las aplicaciones que se ejecutan en los navegadores
    public function guardarSMS(Request $request){
        $listaDeIds = array_map('intval', explode(",",$request->destinatarios));

        foreach ($listaDeIds as $alumno_id) {
            $sms = new \App\Sms();
            $sms->alumno_id = $alumno_id;
            $sms->mensaje   = $request->mensaje;
            $sms->fecha     = date('Y-m-d');            
            $sms->save();
        }
        
        return "OK";
    }
    
    // PASO 1
    // Lo invoca la pasarela SMS para recoger los mensajes y enviarlos
    public function enviarSMS(){
        $json=[];
        foreach (\App\Sms::losMasViejos(1) as $sms){
            $sms=[ "id"=>$sms->id, "telefono"=> $sms->telefono(), "mensaje" => $sms->mensaje ];
            array_push($json, $sms);
        }
        
        return json_encode($json);
    }
    
    // PASO 2
    // Le indicamos una cadena de ids separados con comas, pertenecientes
    // a las identificaciones de los registros de smss. De esta forma
    // el procede a eliminarlos pues ya estan enviados.
    // Se podria hacer para que hiciera una backup, etc... pero no hay time para eso
    public function enviados(Request $request){ // Lo invoca la pasarela SMS para indicar que envio los SMSs
        $listaDeIds = array_map('intval', explode(",",$request->ids));
        
        \App\Sms::destroy($listaDeIds);       
    
        return "OK";
    }
}
