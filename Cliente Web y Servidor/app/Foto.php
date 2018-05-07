<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Foto extends Model
{
    //
    protected $table="fotos",
              $fillable = ["url","alumno_id"];
    
    public $timestamps = false;    
    
}
