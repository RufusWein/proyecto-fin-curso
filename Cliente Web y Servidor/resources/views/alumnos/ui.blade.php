
    <div id="dialogoSombra">
        <div></div>
    </div>
    <div id="dialogoEnvio">
        <div class="formulario">
            <h1 style="margin-left: 20px;">Enviar mensaje</h1>
            <textarea id="mensaje" maxlength="160" placeholder="Introduzca mensaje no superior a 160 caracteres" rows="4" cols="50"></textarea>
            <div style="margin-left: 20px; margin-right: 20px;">
                <button onclick="Conexion.enviarMensaje('id');" class="btn btn-default"><strong>POR SMS</strong></button>
                <button onclick="Conexion.enviarMensaje('email');" class="btn btn-default"><strong>POR EMAIL</strong></button>
                <button onclick="UI.cancelarDialogo();" class="btn btn-default pull-right"><strong>CANCELAR</strong></button>     
            </div>
        </div>
    </div>
    <div id="dialogoConfirmacion">
        <div class="formularioConfirmacion">
            <div class="confirmaEncabezado">
                <img src="http://en.icône.com/images/icones/8/6/pictograms-aem-0058-general-warning-hazard.png" width="100px">
                <h1 class="titulo">Confirmar envío</h1>
            </div>
            <p id="mensajeConfirmado">¿Estás seguro que quieres enviar el mensaje : ''?</p>
            <div style="margin-left: 20px; margin-right: 20px;">
                <button onclick="UI.cancelarDialogoConfirmacion();" class="btn btn-default pull-right"><strong>NO</strong></button>     
                <button onclick="UI.enviarMensajeConfirmado();" class="btn btn-default pull-right"><strong>SI</strong></button>     
            </div>
        </div>
    </div>
