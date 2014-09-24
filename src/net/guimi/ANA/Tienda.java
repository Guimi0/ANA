package net.guimi.ANA;

import android.app.Activity;
import android.os.Bundle;

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
public class Tienda extends Activity {
    /**
     * Sobreescribimos la función de creación de la actividad. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Indicamos la distribución de pantalla (layout) a cargar (xml)
        setContentView(R.layout.tienda);
    }

}