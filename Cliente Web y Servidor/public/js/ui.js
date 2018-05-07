/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class UI {
    static setOnConfirmado(onConfirmado){
        this.onConfirmado=onConfirmado;
    }
    
    static enviarMensajeConfirmado(){
        UI.cancelarDialogoConfirmacion();           
        UI.cancelarDialogo();
        UI.onConfirmado();
    }
    static getMensaje(){
        return document.getElementById("mensaje").value;
    }
    
    static confirmarEnvio(){
        document.getElementById("dialogoConfirmacion").style.display="flex";
        document.getElementById("mensajeConfirmado").innerHTML=
                "¿Estás seguro que quieres enviar el mensaje : '"+UI.getMensaje()+"'?"
    }

    static enviarMensaje(){
        if (UI.getSeleccionados().length>0){
            document.getElementById("dialogoEnvio").style.display="flex";
            document.getElementById("dialogoSombra").style.display="flex";            
        }
    }
        
    static cancelarDialogoConfirmacion(){
        document.getElementById("dialogoConfirmacion").style.display="none";
        document.getElementById("dialogoConfirmacion").style.display="none";            
    } 
        
    static cancelarDialogo(){
        document.getElementById("dialogoEnvio").style.display="none";
        document.getElementById("dialogoSombra").style.display="none";            
    }
        
    static getSeleccionados(dato){
        var tabla        = document.getElementsByTagName("TABLE")[0],
            checkboxes   = tabla.getElementsByTagName("INPUT"),
            seleccionados="";
        Array.from(checkboxes).forEach((checkbox)=>{                
            if (checkbox.checked) {
                let id=(dato=='id'?checkbox.id.substring(2):checkbox.name);
                seleccionados+=id+",";
            }                                            
        });                 
        return (seleccionados.length>0?seleccionados.substring(0,seleccionados.length-1):"");
    }
    
    static actualizaInfo(){
        var tabla        = document.getElementsByTagName("TABLE")[0],
            checkboxes   = tabla.getElementsByTagName("INPUT"),
            seleccionados= 0;
            
        Array.from(checkboxes).forEach((checkbox)=>{                
            if (checkbox.checked) seleccionados++;                                            
        });                        
        document.getElementById("seleccionados").innerHTML="Seleccionados "+seleccionados+" de "+checkboxes.length;
    }
        
    static marcarTodos(checkbox){
       var estado=checkbox.checked;
       var checkboxes=document.getElementsByTagName("TABLE")[0].getElementsByTagName("INPUT");
       // Las colecciones de DOM no son instancias de Array y no disponen de forEach
       Array.from(checkboxes).forEach((cb)=> cb.checked=estado );       
       return true; 
    }

    static marca(registro){
       var checkBox=document.getElementById("cb"+registro);        
       checkBox.checked=!checkBox.checked;
    }
        
    static esChecked(alumno_id){
        var checkbox=document.getElementById("cb"+alumno_id);
        if (checkbox!=null) return checkbox.checked;
        return false; 
    }
        
    static crearRegistro(alumno){
        var registro="<tr id=\"reg"+alumno.id+"\">",
            estado  = UI.esChecked(alumno.id);
        // El checkbox
        registro+="<td class=\"seleccionador\"><input id=\"cb"+alumno.id+"\" type=\"checkbox\" ";
        registro+=" name=\""+alumno.email+"\" "+(estado?"checked":"")+"></td>";
        // El retrato
        registro+="<td class=\"registro\"><div class=\"marco "+(alumno.faltaHoy?"falta":"")+"\" onclick=\"UI.marca("+alumno.id+")\">";   
        registro+="<img id=\"img"+alumno.id+"\" src=\""+alumno.url+"\"></div></td>"; 
        // Informacion del alumno
        registro+="<td class=\"info\">";
        registro+="<strong> Nombre Completo : </strong>"+alumno.nombre+"<br>";
        registro+="<strong> DNI : </strong>"+alumno.dni+"<br>";
        registro+="<strong> Grupo : </strong>"+alumno.grupo+"<br>";
        registro+="<strong> Edad : </strong>"+alumno.edad+"</td>";
        return registro+"</tr>";
    }    
}

