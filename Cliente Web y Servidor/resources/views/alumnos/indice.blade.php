<html>
    <head>
        <title>Student Manager</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css?family=Varela" rel="stylesheet" type="text/css">  
        <script src="/public/js/alumnos.js"></script>
        <script src="/public/js/conexion.js"></script>
        <script src="/public/js/ui.js"></script>
        <link href="/public/css/ui.css" rel="stylesheet">
        <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">       
    </head>
    <body>  
    <div class="gestion">
        <div class="btn-group btn-group-lg">
            <button onclick="Conexion.ponerFalta(UI.getSeleccionados('id'));" class="btn btn-default">PONER FALTA</button>
            <button onclick="Conexion.quitarFalta(UI.getSeleccionados('id'));" class="btn btn-default">QUITAR FALTA</button>           
        </div>
    </div>
    <div class="filtros">
        <div>
            <span class="marcarTodos"><input onchange="UI.marcarTodos(this);" type="checkbox">Todos</span>
            <span>Filtrar por <select onchange="alumnos.cambiaFiltro(this);" class="selectpicker">
                <option>Asistencia</option>
                <option>Grupo</option> 
                <option>Edad</option>
            </select></span>
            <span class="marcarTodos" id="opcionFiltro"><input onchange="alumnos.cambiarAusente(this);" id='mostrarAusentes' type="checkbox">Ausente</span>
            <span>Ordenar por <select onchange="alumnos.cambiaOrden(this);" class="selectpicker">
                <option>Nombre DESC</option>
                <option>Nombre ASC</option>
                <option>Grupo DESC</option>
                <option>Grupo ASC</option>
                <option>Edad DESC</option>
                <option>Edad ASC</option>
            </select></span> 
            <span id="seleccionados">Seleccionados 0 de 0</span>
        </div>
    </div>
    <div class="contenedor">
        <table id="listaDeAlumnos"> 
            @foreach (json_decode($alumnos, false) as $alumno)
                <tr id="reg{{ $alumno->id }}">
                    <td class="seleccionador">
                        <input id='cb{{ $alumno->id }}' email="{{ $alumno->email }}" type="checkbox">
                    </td>
                    <td class='registro'>
                        <div class="marco {{ ($alumno->faltaHoy?"falta":"") }}" onclick="marca('{{ $alumno->id }}')">
                            <img id="img{{ $alumno->id }}" src='{{ $alumno->url }}'></div>
                    </td>
                    <td class='info'>
                        <strong> Nombre Completo : </strong>{{ strtoupper($alumno->nombre) }}<br>
                        <strong> DNI : </strong>{{ strtoupper($alumno->dni) }}<br>
                        <strong> Grupo : </strong>{{ strtoupper($alumno->grupo) }}<br>
                        <strong> Edad : </strong>{{ strtoupper($alumno->edad) }}
                    </td>
                </tr>
            @endforeach
        </table> 
        <img id="iconoFlotante" onclick="UI.enviarMensaje();" src="/public/img/carta.png">
    </div>
        <!-- Aqui se incluyen las capas de diálogo que forman parte de la UI de la aplicacion --> 
        @include('alumnos.ui')
    </body>
    <script type="application/javascript">
        // Ni VUE ni ostias, VanillaScript usando Emacs 6 (es mas facil para trabajar con clases y funciones)
        var alumnos=new Alumnos({!! $alumnos !!}); // hereda de Array, puede usarse los métodos del mismo.
                        
        Conexion.setOnEnviarMensaje((tipo)=>{ // Definimos la accion, para cuando se quiere enviar un mensaje          
            UI.setOnConfirmado(()=>{ // Definimos la accion una vez se confirma con el dialo de confirmacion
                Conexion.enviarMensajeConfirmado((tipo=="id"?"SMS":"EMAIL"), UI.getMensaje(), UI.getSeleccionados(tipo));
            });
            UI.confirmarEnvio(); // lanzamos el dialogo de confirmacion, que de confirmar ejecuta lo definido arriba
        });

        // Definimos eventos en la lista de "alumnos" (sin estos eventos, el objeto se limita a funcionar como un array)
        alumnos.onAsistencia= () => // Cuando activamos el filtro de asistencia
            document.getElementById("opcionFiltro").innerHTML="<input onchange=\"alumnos.cambiarAusente(this);\" id=\"mostrarAusentes\" type=\"checkbox\">Ausente";
        
        alumnos.onEdad= () => { // Cuando activamos el filtro de edad
            let optionsDel1Al99="",campoEdad="Min <select onchange=\"alumnos.cambiaEdad('min',this);\" class=\"selectpicker\">";
            for (let i=1;i<100;i++) optionsDel1Al99+="<option>"+i+"</option>";
            campoEdad+=optionsDel1Al99.replace("<option>1<","<option selected>1<")+"</select>";                     
            campoEdad+=" Max <select onchange=\"alumnos.cambiaEdad('max',this);\" class=\"selectpicker\">";
            campoEdad+=optionsDel1Al99.replace("<option>99<","<option selected>99<")+"</select>";
            document.getElementById("opcionFiltro").innerHTML=campoEdad;            
        };
        
        alumnos.onGrupo = (opcionf) => { // Cuando activamos el filtro de grupo          
            this.gruposCache="";
            let campoGrupo="ID <select onchange=\"alumnos.cambiaGrupo(this);\" class=\"selectpicker\">";
            alumnos.grupos.split(",").forEach((grupo) => this.gruposCache+="<option>"+grupo+"</opcion>" , this);
            campoGrupo+=this.gruposCache+"</select>";
            document.getElementById("opcionFiltro").innerHTML=campoGrupo.replace("<option>"+opcionf+"<","<option selected>"+opcionf+"<");    
        };
        
        alumnos.onProcesoFinalizado= (listaAlumnos) => { // Cuando finaliza el filtrado y ordenado
            let nuevaTabla="";
            listaAlumnos.forEach((alumno)=> nuevaTabla+=UI.crearRegistro(alumno, UI.esChecked(alumno.id)) );
            document.getElementById("listaDeAlumnos").innerHTML=nuevaTabla;            
        };
        
        // Definimos la acción para cuando llegue la lista de faltos del servidor.
        // Que podrá llegar por petición nuestra o automática por medio de un setInterval()
        Conexion.setOnReadyFaltas( (alumnosFaltos) => {            
            alumnos.ocupado=true;
            alumnos.forEach((alumno)=>{
                alumno.faltaHoy=false;
                alumnosFaltos.forEach((alumno_id)=>{                            
                    if (alumno.id==alumno_id) alumno.faltaHoy=true;
                });
            });
            alumnos.actualizar();
        });

        alumnos.filtrarYordenar();
        setInterval(()=>{
            if (!alumnos.ocupado) UI.actualizaInfo();
        },1000);
        // Para que exista concurrencia. Permite que el resto de aplicaciones se actualice.
        setInterval(()=>{
            Conexion.actualizaFaltas("","");
        },10000);        
    </script>
</html>


