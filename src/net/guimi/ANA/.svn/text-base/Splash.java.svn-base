package net.guimi.ANA;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

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
public class Splash extends Activity implements OnTouchListener {
	/** Indica si el Splash sigue en pantalla **/
	protected boolean enSplash = true;
	/** Indica la duración en ms del Splash **/
	protected int tiempoSplash = 3500;
	
    /**
     * Sobreescribimos la función de creación de la actividad. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Indicamos la distribución de pantalla (layout) a cargar (xml)
        setContentView(R.layout.splash);
        
        // Obtenemos la vista principal
        LinearLayout miSplash = (LinearLayout) findViewById(R.id.splashLayout);
        // Capturamos el evento "onTouch"
        miSplash.setOnTouchListener(this);
        
        // Hilo para controlar el tiempo de splash
        Thread hiloSplash = new Thread() {
            @Override
            public void run() {
        		int esperado = 0;
               	try {
               		while(enSplash && (esperado < tiempoSplash)) {
                       	sleep(100);
                       	if(enSplash) {
                       		esperado += 100;
                       	}
               		}
               	} catch(InterruptedException e) {
                   // do nothing
               	} finally {
               		finish();
               		//stop();
               	}
            }
        };
        // Lanzamos el hilo
        hiloSplash.start();

    }

    //************************************************************************
    //       TOQUE EN LA PANTALLA
    //************************************************************************
    public boolean onTouch(View miVista, MotionEvent miEvento) {
    	// Indicamos al hilo que termine el Splash
    	enSplash = false;
    	
    	// tells the system that we handled the event and no further processing is required
        return (false);
    }

    /*
    private class Animacion extends View {
		public Animacion(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
    	;
    }
	*/

}
