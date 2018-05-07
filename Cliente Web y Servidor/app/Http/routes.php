<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the controller to call when that URI is requested.
|
*/

/*Route::get('/', function () {
    return view('welcome');
});*/

//Route::get("/","ControladorDeAlumnos@index");
//Route::get("/listado","ControladorDeAlumnos@listado");
//Route::post("/guardaf","ControladorDeAlumnos@guardaf");

///////////////////////////////////////////////////////////////////////////////
// APLICACION - Leyenda (= retorna), ({} parametros), ([] opcional) (| & O Y respectivamente)
Route::get("/","ControladorDeAlumnos@index");

// SERVCIOS
    // LISTADO JSON PARA APLICACIONES QUE NO SE EJECUTEN EN UN NAVEGADOR (Como la de Android)
    Route::get("/servicios/listado","ControladorDeAlumnos@listadoJSON");

    // GESTION DE ASSISTENCIA [{ (poner|quitar) & ids }]
    Route::get("/servicios/faltas","ControladorDeFaltas@accion");// = ausentesDeHoy

    // ENVIO EMAIL { mensaje & destinatarios }
    Route::get("/servicios/email","ControladorEmail@enviarCorreos");

    // PASARELA SMS 
    Route::get("/servicios/smss"      ,"ControladorSMS@enviarSMS");// =json
    Route::get("/servicios/enviados"  ,"ControladorSMS@enviados"); // { ids }
    Route::get("/servicios/guardarsms","ControladorSMS@guardarSMS"); // { mensaje & destinatarios }
///////////////////////////////////////////////////////////////////////////////

Route::get('/artisan', function() {
    $exitCode = Artisan::call('view:clear');
    $exitCode += Artisan::call('cache:clear');
    // return what you want
    return $exitCode;
});
/*
 * Cojonudo sino tienes alojamiento Web con terminal SSH    

Route::get('/logout', function() {
 Session::forget('key');
  if(!Session::has('key'))
   {
      return "signout";
   }
 });
 */