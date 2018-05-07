package tk.srwhiteskull.studentmanager.Fragmentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import tk.srwhiteskull.studentmanager.R;
import tk.srwhiteskull.studentmanager.Registros;

public class SobreFragment extends Fragment {
    public SobreFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actividad_sobre, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Registros.actividad.invalidateOptionsMenu();
        ((WebView)view.findViewById(R.id.webview)).loadData(getString(R.string.sobre_la_aplicacion), "text/html; charset=utf-8","UTF-8");
        super.onViewCreated(view, savedInstanceState);
    }

}
