<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Sms extends Model
{
    //
    protected $table    = "smss",
              $fillable = ["alumno_id","mensaje","fecha"];
    
    public $timestamps = false; 
    
    public static function losMasViejos($limite){
        return Sms::orderBy('fecha','asc')->take($limite)->get();
    }
    
    public function telefono(){
        return Contacto::where('alumno_id',$this->alumno_id)->first()->telefono;
    } 
}
