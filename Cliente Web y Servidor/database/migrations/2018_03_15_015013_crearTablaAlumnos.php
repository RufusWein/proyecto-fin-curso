<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CrearTablaAlumnos extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        //
        Schema::create('alumnos', function (Blueprint $table) {
            $table->increments('id');
            $table->string('dni',9)->unique();
            $table->string('nombre');
            $table->string('apellido1',12);
            $table->string('apellido2',12);
            $table->string('grupo_id',8);
            $table->string('registro',8);
            $table->date('fecha_nacimiento');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        //
        Schema::drop('alumnos');
    }
}
