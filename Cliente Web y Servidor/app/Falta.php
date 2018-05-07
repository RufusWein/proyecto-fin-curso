<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Falta extends Model
{
    //
    protected $table    = "faltas",
              $fillable = ["alumno_id","fecha"];
    
    public $timestamps = false; 

    public static function ponerFaltaHoy($alumno_id){
        Falta::where("alumno_id",$alumno_id)->where("fecha", date('Y-m-d'))->delete();
        //if (Falta::where("alumno_id",$alumno_id)->where("fecha", date('Y-m-d'))->count()==0){        
            $falta=new Falta; 
            $falta->fecha     = date('Y-m-d');
            $falta->alumno_id = $alumno_id;
            $falta->save();
        //}
    }
    
    public static function quitarFaltaHoy($alumno_id){
        Falta::where("alumno_id",$alumno_id)->where("fecha", date('Y-m-d'))->delete();
    }
    
    public static function losAusentesDeHoy(){
        $losAusentesDeHoy= Falta::where("fecha", date('Y-m-d'))->get();
        $resultado="";
        foreach ($losAusentesDeHoy as $falta){
            $resultado.=$falta->alumno_id.",";
        }
        
        return ($resultado!=""?substr($resultado, 0, -1):"");
    }
}
