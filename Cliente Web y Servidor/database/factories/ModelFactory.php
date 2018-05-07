<?php

/*
|--------------------------------------------------------------------------
| Model Factories
|--------------------------------------------------------------------------
|
| Here you may define all of your model factories. Model factories give
| you a convenient way to create models for testing and seeding your
| database. Just tell the factory how a default model should look.
|
*/

$factory->define(App\User::class, function (Faker\Generator $faker) {
    return [
        'name' => $faker->name,
        'email' => $faker->email,
        'password' => bcrypt(str_random(10)),
        'remember_token' => str_random(10),
    ];
});

$factory->define(App\Alumno::class, function (Faker\Generator $faker) {
    return [
        'dni'              => $faker->bothify("########?"),
        'nombre'           => $faker->firstName,
        'apellido1'        => $faker->lastName,
        'apellido2'        => $faker->lastName,
        'grupo_id'         => ["UGIT2","ERE2","CAR1","XESA2","XDAW3","XAFI3","IEA1A","IEA2","SEA1","EVE2","GAD2","DAW1","SRE1","ESA1","IEA1B","CIN1","XAFI1","VIT1","ALO1","UECA1","COC1","EB1","ACO1","ASI1","UGIT1"]
                              [$faker->numberBetween($min = 0, $max = 24)],
        'registro'         => "".($faker->unique()->numberBetween($min = 1000, $max = 13999)),
        'fecha_nacimiento' => $faker->dateTimeBetween($startDate = '-16 years', $endDate = '-12 years', $timezone = null)
    ];
});