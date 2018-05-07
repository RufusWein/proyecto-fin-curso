<?php

use Illuminate\Database\Seeder;

class SembradorDeAlumnos extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        //       
        factory(App\Alumno::class, 300)->create();
    }
}
