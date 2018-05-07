<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;

use Mail;

class ControladorEmail extends Controller
{
    public function enviarCorreos(Request $request){
        // mensaje ;
        // destinatarios ;        
        $mensaje       = $request->mensaje;
        $destinatarios = explode(",",$request->destinatarios); 
        
        try {
        
            foreach ($destinatarios as $destinatario) {                       
                
                Mail::raw($mensaje, function($message) use ($destinatario) {
                    $message->to($destinatario, $destinatario )->subject('Aviso Importante!');
                    $message->from(env('MAIL_USERNAME'),'Dirección - IES Zonzamas');
                }); 
            }

            return "Mensaje enviado! Gracias y que tenga un buen día.";
        
        } catch (Exception $e) {
            return 'No pudo enviarse el mensaje!!\n '.
                 'Error : ' .$e;
        }
    }
}
