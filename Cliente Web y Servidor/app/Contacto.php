<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Contacto extends Model
{
    //
    protected $table    = "contactos",
              $fillable = ["alumno_id","email","telefono"];
    
    public $timestamps = false;        
    
}
