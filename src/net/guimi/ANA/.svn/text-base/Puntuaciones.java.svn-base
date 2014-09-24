package net.guimi.ANA;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

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
public class Puntuaciones extends Activity {
    static final String FICHERO_PUNTUACIONES = "ANA_puntuaciones";
	TextView nivelScroll, puntuacionesScroll, nombresScroll;
	
	private SQLiteGestor miSQLiteGestor; 
	
    /**
     * Sobreescribimos la función de creación de la actividad. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Indicamos la distribución de pantalla (layout) a cargar (xml)
        setContentView(R.layout.puntuaciones);

        // Tomamos los campos de información y los rellenamos
        nivelScroll = (TextView)findViewById(R.id.nivelScroll);
        puntuacionesScroll = (TextView)findViewById(R.id.puntuacionesScroll);
        nombresScroll = (TextView)findViewById(R.id.nombresScroll);
        ArrayList<Puntos> puntuaciones = leePuntuacionesDB();
        for (Puntos misPuntos : puntuaciones) {
        	//puntuacionesScroll.setText(puntuacionesScroll.getText()+misPuntos.puntos+"\t\t"+misPuntos.piloto+"\n");
        	nivelScroll.setText(nivelScroll.getText()+misPuntos.nivel+"\n");
        	puntuacionesScroll.setText(puntuacionesScroll.getText()+misPuntos.puntos+"\n");
        	nombresScroll.setText(nombresScroll.getText()+misPuntos.piloto+"\n");
        }
    }

    private ArrayList<Puntos> leePuntuacionesDB() {
    	miSQLiteGestor = new SQLiteGestor(this);
    	ArrayList<Puntos> misPuntos = miSQLiteGestor.obtenPuntuaciones();
    	miSQLiteGestor.cierraOH();
    	return misPuntos;
    }
    
}