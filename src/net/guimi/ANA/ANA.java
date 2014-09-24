package net.guimi.ANA;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ANA extends Activity {
	/** Variable auxiliar que indica el paquete de la aplicacion */
	private static String paqueteAplicacion = "net.guimi.ANA";
    /** Variable auxiliar que indica la versión de la aplicación */
    private static String versionAplicacion;

    /**
     * We serialize access to our persistent data through a global static
     * object.  This ensures that in the unlikely event of the our backup/restore
     * agent running to perform a backup while our UI is updating the file, the
     * agent will not accidentally read partially-written data.
     *
     * <p>Curious but true: a zero-length array is slightly lighter-weight than
     * merely allocating an Object, and can still be synchronized on.
     */
    static final Object[] sDataLock = new Object[0];
    static final String FICHERO_PUNTUACIONES = "ANA_puntuaciones";
    
	// Definimos los identificadores de los diferentes diálogos a mostrar
	static final int DIALOGO_ACERCA_DE = 0;
	static final int DIALOGO_AYUDA = 1;
	static final int DIALOGO_SALIR = 2;
    
	// Creamos los botones
    private Button botonInicio;
    private Button botonPreferencias;
    private Button botonAyuda;
    private Button botonAcercaDe;
    private Button botonPuntuaciones;
    //private Button botonMasJuegos;
    //private Button botonSalir;
    
    /**
     * Sobreescribimos la función de creación de la actividad. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Indicamos la distribución de pantalla (layout) a cargar (xml)
        setContentView(R.layout.main);
        // Obtenemos la versión de la aplicación
        try {
        	PackageInfo miPackageInfo = getPackageManager().getPackageInfo(paqueteAplicacion, 0);
            versionAplicacion = miPackageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        	versionAplicacion = "";
        }
      
        //************************************************************************
        //       BOTONES
        //************************************************************************
        // Definimos el botón de inicio
        botonInicio = (Button)findViewById(R.id.botonInicio);
        botonInicio.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		lanzaJuego();
        	}
        });

        // Definimos el botón de preferencias
        botonPreferencias = (Button)findViewById(R.id.botonPreferencias);
        botonPreferencias.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent actividadPreferencias = new Intent(getBaseContext(), Preferencias.class);
        		startActivity(actividadPreferencias);
        	}
        });

        // Definimos el botón de ayuda
        botonAyuda = (Button)findViewById(R.id.botonAyuda);
        botonAyuda.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
    			showDialog(DIALOGO_AYUDA);
        	}
        });

        // Definimos el botón de Acerca de...
        botonAcercaDe = (Button)findViewById(R.id.botonAcercaDe);
        botonAcercaDe.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
    			showDialog(DIALOGO_ACERCA_DE);
        	}
        });

        // Definimos el botón de puntuaciones
        botonPuntuaciones = (Button)findViewById(R.id.botonPuntuaciones);
        botonPuntuaciones.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent puntuacionesIntent = new Intent(ANA.this,Puntuaciones.class);
        		startActivity(puntuacionesIntent);
        	}
        });

        /*
        // Definimos el botón de más juegos
        botonMasJuegos = (Button)findViewById(R.id.botonMasJuegos);
        botonMasJuegos.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent webIntent = new Intent(ANA.this,Web.class);
        		startActivity(webIntent);
        	}
        });
        */

        /*
        // Definimos el botón de salir
        botonSalir = (Button)findViewById(R.id.botonSalir);
        botonSalir.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
    			showDialog(DIALOGO_SALIR);
        	}
        });
        */

        
        //************************************************************************
        //       SPLASH
        //************************************************************************
		// Creamos un nuevo "Intent" para el "Splash"
		Intent splashIntent = new Intent(ANA.this,Splash.class);
        // Iniciamos la actividad "Splash"
		startActivity(splashIntent);

    }

    
    /**
     * Esta función se encarga de lanzar un nuevo juego
     */
    private void lanzaJuego() {
		// Creamos un nuevo "Intent"
		Intent inicioJuegoIntent = new Intent(ANA.this,ANAJuego.class);
		
		// Generamos un "fardo" (Bundle) con información para la actividad hija
        Bundle miBundle = new Bundle();
        miBundle.putString("versionAplicacion", versionAplicacion);
        inicioJuegoIntent.putExtras(miBundle);
        
        // Iniciamos la actividad que gestiona el juego
		startActivity(inicioJuegoIntent);
    }


    //************************************************************************
    //       PULSACIONES DE TECLAS DE SISTEMA (BACK, MENU...)
    //************************************************************************
    /**
     * Sobreescribimos la función onKeyDown para capturar la tecla "back"
     * y redirigir al diálogo "Salir"
     */
    /*
    @Override public boolean onKeyDown(int idTecla, KeyEvent evento) {
    	if (idTecla == KeyEvent.KEYCODE_BACK) {
	    	showDialog(DIALOGO_SALIR);
	    	// No hacemos más acciones como resultado de la pulsación
    	    return true;
    	}
 
    	// Si no hemos gestionado el evento de tecla, lo propagamos
    	return false;
    }
    */

    
    //************************************************************************
    //       MENU
    //************************************************************************
    /**
     * Sobreescribimos la función onCreateOptionsMenu para añadir nuestro menú
     */
    @Override
    public boolean onCreateOptionsMenu(Menu miMenu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_inicio, miMenu);
        return true;
    }

    /**
     * Sobreescribimos la función onOptionsItemSelected para incluir opciones
     * en el menú
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuInicioJugar:
    		lanzaJuego();
    		break;
    	case R.id.menuInicioPreferencias:
    		Intent actividadPreferencias = new Intent(getBaseContext(), Preferencias.class);
    		startActivity(actividadPreferencias);
        	break;
    	case R.id.menuInicioAyuda:
			showDialog(DIALOGO_AYUDA);
        	break;
    	case R.id.menuInicioAcercaDe:
			showDialog(DIALOGO_ACERCA_DE);
        	break;
        /*
    	case R.id.menuInicioMasJuegos:
    		Intent webIntent = new Intent(ANA.this,Web.class);
    		startActivity(webIntent);
        	break;
        	*/
    	case R.id.menuInicioPuntuaciones:
    		Intent puntuacionesIntent = new Intent(ANA.this,Puntuaciones.class);
    		startActivity(puntuacionesIntent);
    		break;
        case R.id.menuInicioSalir:
        	showDialog(DIALOGO_SALIR);
        	//this.finish();
        	break;
        }
        return true;
    }

    
    //************************************************************************
    //       DIÁLOGOS
    //************************************************************************
	/** Función de creación de diálogos llamada desde showDialog */
	protected Dialog onCreateDialog(int idDialogo) {
	    Dialog miDialogo = null;
	    
	    switch(idDialogo) {
	    	case DIALOGO_AYUDA:
	    		// Generamos un constructor para el diálogo "Ayuda"
				AlertDialog.Builder dialogoAyuda = new AlertDialog.Builder(this);
				dialogoAyuda.setMessage(R.string.menuAyudaTexto)
					.setCancelable(false)
					.setNeutralButton(R.string.menuBotonCerrar, new DialogInterface.OnClickListener() {
	    		           public void onClick(DialogInterface miDialogo, int id) {
	    		        	   miDialogo.dismiss();
	    		        	   //miDialogo.cancel();
	    		           }
					});
				AlertDialog miDialogoAyuda = dialogoAyuda.create();
				// Titulo del diálogo
				miDialogoAyuda.setTitle(R.string.menuAyudaTitulo);
				// Icono para el diálogo
				miDialogoAyuda.setIcon(R.drawable.ic_menu_help);
				// Asignamos el diálogo recien creado al que devolverá la función
				miDialogo=miDialogoAyuda;
	    		break;
	    	case DIALOGO_ACERCA_DE:
	    		// Generamos un constructor para el diálogo "Acerca de..."
				AlertDialog.Builder dialogoAcercaDe = new AlertDialog.Builder(this);
				dialogoAcercaDe.setMessage(R.string.menuAcercaTexto)
					.setCancelable(false)
					.setNeutralButton(R.string.menuBotonCerrar, new DialogInterface.OnClickListener() {
	    		           public void onClick(DialogInterface miDialogo, int id) {
	    		        	   miDialogo.dismiss();
	    		        	   //miDialogo.cancel();
	    		           }
					});
				AlertDialog miDialogoAcercaDe = dialogoAcercaDe.create();
				// Titulo del diálogo
				miDialogoAcercaDe.setTitle(getResources().getString(R.string.menuAcercaTitulo)+" "+versionAplicacion);
				// Icono para el diálogo
				miDialogoAcercaDe.setIcon(R.drawable.icono);
				// Asignamos el diálogo recien creado al que devolverá la función
				miDialogo=miDialogoAcercaDe;
	    		break;
	    	case DIALOGO_SALIR:
	    		// Generamos un constructor para el diálogo "Salir"
				AlertDialog.Builder dialogoSalir = new AlertDialog.Builder(this);
				dialogoSalir.setMessage(R.string.menuSalirTexto)
					.setCancelable(false)
					.setPositiveButton(R.string.menuBotonSi, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface miDialogo, int id) {
							finish();
	    		        }
					})
					.setNegativeButton(R.string.menuBotonNo, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface miDialogo, int id) {
							miDialogo.cancel();
						}
					});
				AlertDialog miDialogoSalir = dialogoSalir.create();
				// Titulo del diálogo
				miDialogoSalir.setTitle(R.string.menuSalirTitulo);
				// Icono para el diálogo
				miDialogoSalir.setIcon(R.drawable.icono);
				// Asignamos el diálogo recien creado al que devolverá la función
				miDialogo=miDialogoSalir;	    		
	    		break;
	    	default:
	    		miDialogo = null;
	    }
	    return miDialogo;
	}
	
}