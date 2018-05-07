package tk.srwhiteskull.studentmanager;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DialgoEnviarMensajeSMS extends DialogFragment {
    public View.OnClickListener onSms, onEmail;
    public EditText mensaje;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enviar mensaje");
        LayoutInflater li = LayoutInflater.from(builder.getContext());
        View layoutEnviarMensaje = li.inflate(R.layout.dialogo_enviar_mensaje, null);
        Button botonCancelar = layoutEnviarMensaje.findViewById(R.id.boton_cancelar),
               botonSms      = layoutEnviarMensaje.findViewById(R.id.boton_sms),
               botonEmail    = layoutEnviarMensaje.findViewById(R.id.boton_email);
        //EditText
            mensaje     = layoutEnviarMensaje.findViewById(R.id.mensaje_edit);
        builder.setView(layoutEnviarMensaje);
        final Dialog dialogo = builder.create();

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });
        botonSms.setOnClickListener(onSms);
        botonEmail.setOnClickListener(onEmail);

        return dialogo;
    }

}
