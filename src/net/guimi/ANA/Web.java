package net.guimi.ANA;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/*
 * Copyright (c) 2004-2010 Luis Miguel Armendáriz
 * http://guimi.net
 * 
 * Está permitido copiar, distribuir y/o modificar
 * los desarrollos bajo los términos de la
 * GNU General Public License, Versión 2
 * 
 * Para obtener una copia de dicha licencia
 * visite http://www.fsf.org/licenses/gpl.txt.
 * 
 */
public class Web extends Activity {
	WebView webview;
	
    /**
     * Sobreescribimos la función de creación de la actividad. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Indicamos la distribución de pantalla (layout) a cargar (xml)
        setContentView(R.layout.web);

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://www.guimi.net/ANA/entrada_movil.php");
        
        webview.setWebViewClient(new miNavegadorWeb());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (webview.canGoBack())) {
            webview.goBack();
            return true;
        } else return super.onKeyDown(keyCode, event);
    }
    
    private class miNavegadorWeb extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}