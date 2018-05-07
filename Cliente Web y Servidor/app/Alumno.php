<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Alumno extends Model
{
    //
    protected $table    = "alumnos",
              $fillable = ["dni","nombre","apellido1","apellido2","grupo_id","registro","fecha_nacimiento"];
    
    public $timestamps = false;
    
    public function nombreCompleto($html){
        if (!isset($html)) {
            return $this->nombre." ".$this->apellido1." ".$this->apellido2;
        } 
        
        return "<$html>".$this->nombre." </$html>".
               "<$html>".$this->apellido1." </$html>".
               "<$html>".$this->apellido2." </$html>";
    }   
    
    public function edad(){       
        return date_diff(date_create(date("Y-m-d")), date_create($this->fecha_nacimiento))->format("%y");
    }
    
    public function foto(){
        $resultado=Foto::where('alumno_id',$this->id)->first();
        
        return ($resultado!=null?$resultado->url:"/public/img/anonimo.jpg");
    }
}
