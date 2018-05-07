/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// clases EMAC 6 (Soportado en navegadores modernos, no anteriores a 2015)
class Alumnos extends Array {
    constructor(array){
        super();    

        this.grupos="";
        array.forEach((elemento)=>{
            this.push(elemento);
            if (!this.grupos.includes(elemento.grupo)) this.grupos+=elemento.grupo+",";
        }, this);

        this.grupos=this.grupos.substring(0,this.grupos.length-1);
        this.ocupado=true;
        this.opcion_ausente=false;
        this.opcion_edad_min=1;
        this.opcion_edad_max=99;
        this.opcion_grupo=this[0].grupo;
        this.filtroAnterior="Asistencia";
        this.criterio_ordenarPorDefecto="Nombre";
        this.opcion_ordenarPorDefecto="DESC";
        this.criterio_filtroPorDefecto="Asistencia";
        this.opcion_filtroPorDefecto=false;
        this.onEdad= ()=>{};
        this.onGrupo= ()=>{};
        this.onAsistencia= ()=>{};
        this.onProcesoFinalizado= ()=>{};
    }

    cambiaFiltro(selector){
        let opcion ={ "Asistencia": this.opcion_ausente, "Grupo" : this.opcion_grupo, "Edad" : [this.opcion_edad_min, this.opcion_edad_max] }[selector.options[selector.selectedIndex].text];
        this.criterio_filtroPorDefecto=selector.options[selector.selectedIndex].text;
        this.opcion_filtroPorDefecto=opcion;
        this.filtrarYordenar(this.criterio_ordenarPorDefecto,this.opcion_ordenarPorDefecto, selector.options[selector.selectedIndex].text, opcion);  
    }

    cambiaOrden(selector){
        let resultado=selector.options[selector.selectedIndex].text; 
        this.criterio_ordenarPorDefecto=resultado.split(' ')[0];
        this.opcion_ordenarPorDefecto=resultado.split(' ')[1];
        this.filtrarYordenar(resultado.split(' ')[0], resultado.split(' ')[1], this.criterio_filtroPorDefecto, this.opcion_filtroPorDefecto);              
    }

    cambiaEdad(tipo, selector){
        if (tipo=="max") this.opcion_edad_max=selector.options[selector.selectedIndex].text;
        else this.opcion_edad_min=selector.options[selector.selectedIndex].text;
        this.opcion_filtroPorDefecto=[this.opcion_edad_min, this.opcion_edad_max];
        this.filtrarYordenar(this.criterio_ordenarPorDefecto, this.opcion_ordenarPorDefecto, "Edad", [this.opcion_edad_min, this.opcion_edad_max]);
    }

    cambiaGrupo(selector){
        this.opcion_grupo=selector.options[selector.selectedIndex].text;
        this.opcion_filtroPorDefecto=this.opcion_grupo;
        this.filtrarYordenar(this.criterio_ordenarPorDefecto, this.opcion_ordenarPorDefecto, "Grupo", this.opcion_grupo);
    }

    cambiarAusente(checkbox){
        this.opcion_ausente=checkbox.checked;
        this.opcion_filtroPorDefecto=this.opcion_ausente;
        this.filtrarYordenar(this.criterio_ordenarPorDefecto, this.opcion_ordenarPorDefecto, "Asistencia", this.opcion_ausente);
    }

    actualizar(){
        this.filtrarYordenar(this.criterio_ordenarPorDefecto, this.opcion_ordenarPorDefecto  , 
                             this.criterio_filtroPorDefecto , this.opcion_filtroPorDefecto );
    }

    filtrarYordenar(criterioo  = this.criterio_ordenarPorDefecto, 
                    opciono    = this.opcion_ordenarPorDefecto  , 
                    criteriof  = this.criterio_filtroPorDefecto , 
                    opcionf    = this.opcion_filtroPorDefecto){
        this.ocupado=true;
        var alumnosFiltrados=[];            

        switch(criteriof){
            case "Asistencia":
                if (this.filtroAnterior!=criteriof) this.onAsistencia();
                this.forEach((alumno) => {
                    if (alumno.faltaHoy==opcionf) alumnosFiltrados.push(alumno);
                });
                break;
            case "Grupo":
                if (this.filtroAnterior!=criteriof) this.onGrupo(opcionf);
                this.forEach((alumno) =>{ 
                    if (alumno.grupo== opcionf ) alumnosFiltrados.push(alumno);
                }); 
                break;
            case "Edad":
                if (this.filtroAnterior!=criteriof) this.onEdad(); 
                opcionf[0]=parseInt(opcionf[0]);
                opcionf[1]=parseInt(opcionf[1]);                
                if (opcionf[0]<=opcionf[1]){
                    this.forEach((alumno) =>{ 
                        if (parseInt(alumno.edad)>=opcionf[0] && parseInt(alumno.edad)<= opcionf[1]) alumnosFiltrados.push(alumno); 
                    });                 
                }

        }                      
        //console.log("Criterio O. = "+criterioo+ " Opcion O. = "+opciono+" Criterio F. = "+criteriof+" Opcion F. = "+opcionf);
        this.onProcesoFinalizado(this.msort(alumnosFiltrados, criterioo.toLowerCase(), opciono=="DESC"));
        this.filtroAnterior=criteriof;
        this.ocupado=false;
    }

    // ordenamiento simple por sistema de burbujeo
    msort(array, campo, inverso){
        for(var i =0;i<array.length;i++){
            for(var j= i+1;j<array.length;j++){
                if (inverso){
                    if(array[i][campo]>array[j][campo]){
                        var swap = array[i];
                        array[i] = array[j];
                        array[j] = swap;
                    }
                } else {
                    if(array[i][campo]<array[j][campo]){
                        var swap = array[i];
                        array[i] = array[j];
                        array[j] = swap;
                    }                        
                }
            }
        }
        return array;
    }
} 

