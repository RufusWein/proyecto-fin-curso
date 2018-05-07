package tk.srwhiteskull.studentmanager.Fragmentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import tk.srwhiteskull.studentmanager.R;
import tk.srwhiteskull.studentmanager.Registros;

public class AyudaFragment extends Fragment {
    public AyudaFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actividad_ayuda, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Registros.actividad.invalidateOptionsMenu();
        ((WebView)view.findViewById(R.id.webview)).loadDataWithBaseURL("file:///android_res/drawable/",getString(R.string.ayuda_aplicacion),"text/html","UTF-8",null);
        super.onViewCreated(view, savedInstanceState);
    }
}
