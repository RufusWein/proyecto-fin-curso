/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class Conexion {
    static setOnEnviarMensaje(onEnviarMensaje){
        this.onEnviarMensaje=onEnviarMensaje;
    }
    
    static enviarMensaje(tipo){
        Conexion.onEnviarMensaje(tipo);
    }
    // En el EMACS 6 se definen los atributos estáticos de una clase en un método estático.
    // Aunque también se puede hacer externamente, ejemplo : Conexion.onReadyFaltas = null;
    static setOnReadyFaltas(onReadyFaltas){
        this.onReadyFaltas=onReadyFaltas;
    }
    
    static OnReadyFaltas(listaDeFaltos){
        this.onReadyFaltas(listaDeFaltos);
    }
    
    static actualizaFaltas(accion, seleccion){
        var xhttp = new XMLHttpRequest();

        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                Conexion.OnReadyFaltas(this.responseText.split(','));                    
            } else if (this.status == 500){
                document.write(this.responseText);
            }

        };
        if (accion=="poner" || accion=="quitar"){
            accion="?"+accion+"="+seleccion;
            if (seleccion.length==0) accion="";
        } else accion="";
        xhttp.open("GET", "/servicios/faltas"+accion, true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send(JSON.stringify({ _token : "{{ csrf_token() }}" }));
    }

    static ponerFalta(seleccion){
        Conexion.actualizaFaltas("poner",seleccion);
    }

    static quitarFalta(seleccion){
        Conexion.actualizaFaltas("quitar",seleccion);
    }
    
    static enviarMensajeConfirmado(accion, mensaje, seleccion){
        var xhttp = new XMLHttpRequest();
        console.log("Accion = "+accion+" Mensaje = "+mensaje+" Seleccion = "+seleccion);
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                alert(this.responseText);
            } else if (this.status == 500){
                document.write(this.responseText);
            }
        };
        if ((accion=="SMS" || accion=="EMAIL") && mensaje.length>0 && seleccion.length>0){
            let url={ SMS : "/servicios/guardarsms", EMAIL : "/servicios/email" }[accion];
            url+="?mensaje="+mensaje+"&destinatarios="+seleccion;
            xhttp.open("GET", url, true);
            xhttp.setRequestHeader("Content-type", "application/json");
            xhttp.send(JSON.stringify({ _token : "{{ csrf_token() }}" }));
        } 
    }
}

