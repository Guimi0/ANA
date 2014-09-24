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

import android.provider.Settings.Secure;
import net.guimi.ANA.AcelerometroGestor;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * ANA: Ataque Nuclear Androide
 * ANA: Android Nuclear Attack
 * 
 * Típico juego de aviones/naves en el que el jugador debe dispara a (casi)todo lo
 *   que ve.
 * 
 * @author guimi
 *
 */
public class ANAJuego extends Activity implements AcelerometroInterfaz {
    // Gestión de energía para evitar que a mitad juego se bloquee el dispositivo
    //+  por no estar en uso (porque no se pulsan teclas)
	protected PowerManager.WakeLock miWakeLock;
    private String android_id;

	// Definimos los identificadores de los diferentes diálogos a mostrar
	static final int DIALOGO_SALIR = 1;

    /** Variable auxiliar para gestionar la vista principal */
	private Pantalla miPantalla;
    /** Variable auxiliar para guardar nuestro contexto.
     * Podría sustituirse por getContext() */
    private static Context miContexto;

    // Esta variable nos permite identificar el estado (Bundle) de la aplicación
    //  a la hora de guardarlo y cargarlo
    //private static String nombreEstado = "ANA-guimi";
    /** Variable auxiliar que indica la versión de la aplicación */
    private static String versionAplicacion;
    
	
    /**
     * Sobreescribimos la función de creación de la actividad. 
     */
    @Override
    public void onCreate(Bundle miEstadodeInstancia) {
        super.onCreate(miEstadodeInstancia);
        // Obtenemos datos enviados en el "fardo" (Bundle) por la actividad padre 
        Bundle miBundle = this.getIntent().getExtras();
        // Esta variable nos indica si debemos usar sonido
        versionAplicacion = miBundle.getString("versionAplicacion");
        
        // Necesitaremos el contexto para el acelerómetro
        miContexto = this;
        android_id = Secure.getString(miContexto.getContentResolver(), Secure.ANDROID_ID);
        // En el emulador android_id == null
        if (android_id == null) android_id = "ANA_emulador";

        //scoreNinjaAdapter = new ScoreNinjaAdapter(miContexto, "ana-androidnuclearattack", "4099FBE1133A4DC0A743BDDD736B721C");

        // Gestión de energía
    	final PowerManager miPowerManager = (PowerManager)miContexto.getSystemService(Context.POWER_SERVICE);
    	miWakeLock = miPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "ANAJuego");
    	miWakeLock.acquire();

        // Generamos nuestra pantalla de juego
        miPantalla = new Pantalla(this);
        miPantalla.leePreferencias();
        miPantalla.ponAndroid_id(android_id);
        miPantalla.ponVersionAplicacion(versionAplicacion);
        //miPantalla.ponModoJuego(Pantalla.MODO_EN_MARCHA);
        
        // Cargamos nuestra vista (nuestra pantalla de juego) 
        setContentView(miPantalla);

        /*
        // Si el estado es nulo, se acaba de crear la actividad
        if (miEstadodeInstancia == null) {
            // Configuramos un nuevo juego
        } else {
            // Venimos de una pausa
            Bundle mapa = miEstadodeInstancia.getBundle(nombreEstado);
            if (mapa != null) {
            	//miPantalla.recuperaEstado(mapa);
            } else {
            	//miPantalla.ponModoJuego(Pantalla.PAUSA);
            }
        }
        */
    }
    

    /**
     * Sobreescribimos la función onPause para que se pause el juego
     *  al pausarse la actividad. 
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Desbloqueamos el gestor de energía
        if (miWakeLock.isHeld()) {
        	miWakeLock.release();
        }
        if (miPantalla.leeModoJuego() == Pantalla.MODO_EN_MARCHA) miPantalla.ponModoJuego(Pantalla.MODO_PAUSA);
    }
    
    /** Al ponerse en marcha la aplicación lanzamos la interfaz */
    @Override
    protected void onResume() {
        super.onResume();
        // Bloqueamos el gestor de energía
        miWakeLock.acquire();
        // Conectamos el interfaz del acelerómetro
        if (AcelerometroGestor.estaSoportado(miContexto)) {
        	AcelerometroGestor.iniciaInterfaz(this, miContexto);
        }
    }
 
    /** Al pararse la aplicación paramos la interfaz */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desbloqueamos el gestor de energía
        if (miWakeLock.isHeld()) {
        	miWakeLock.release();
        }
        // Desconectamos el interfaz del acelerómetro
        if (AcelerometroGestor.estaFuncionando()) {
        	AcelerometroGestor.paraInterfaces();
        }
     }


    /**
     * Sobreescribimos la función onSaveInstanceState para que se guarde el
     *  estado del juego al guardarse el estado de la actividad. 
     */
    // TODO
    /*
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(nombreEstado, miSerpienteVista.guardaEstado());
    }
    */

    //************************************************************************
    //       PULSACIONES DE TECLAS DE SISTEMA (BACK, MENU...)
    //************************************************************************
    /**
     * Sobreescribimos la función onKeyDown para capturar la tecla "back"
     * y redirigir al diálogo "Salir"
     */
    @Override public boolean onKeyDown(int idTecla, KeyEvent evento) {
    	if (idTecla == KeyEvent.KEYCODE_BACK) {
        	// Capturamos al tecla "back"
    		// Si el juego está en marcha, hacemos una pausa
    		if (miPantalla.leeModoJuego() == Pantalla.MODO_EN_MARCHA) {
    			miPantalla.ponModoJuego(Pantalla.MODO_PAUSA);
    		} else if (miPantalla.leeModoJuego() == Pantalla.MODO_FIN) {
            	this.finish();
    		} else {
    			showDialog(DIALOGO_SALIR);
    		}
    	} else if (idTecla == KeyEvent.KEYCODE_MENU) {
        	// Capturamos al tecla "menu"
    	    // Si el juego está en marcha, hacemos una pausa
    	    if (miPantalla.leeModoJuego() == Pantalla.MODO_EN_MARCHA) {
    	    	miPantalla.ponModoJuego(Pantalla.MODO_PAUSA);
    	    }

    	    // Propagamos la pulsación de la tecla, para que cumpla sus funciones
    	    return false;
  	}
 
    	// Si no hemos gestionado el evento de tecla, lo propagamos
    	return false;
    }
    
    //************************************************************************
    //       MENU
    //************************************************************************
    /**
     * Sobreescribimos la función onCreateOptionsMenu para añadir nuestro menú
     */
    @Override
    public boolean onCreateOptionsMenu(Menu miMenu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_juego, miMenu);
        return true;
    }

    /**
     * Sobreescribimos la función onOptionsItemSelected para incluir opciones
     * en el menú
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menuJuegoJugar:
        		miPantalla.leePreferencias();
        		if (miPantalla.leeModoJuego() == Pantalla.MODO_PAUSA) {
        			miPantalla.ponModoJuego(Pantalla.MODO_EN_MARCHA);
        		} else if (miPantalla.leeModoJuego() == Pantalla.MODO_FIN) {
        			miPantalla.iniciaJuego();
        		}
        		break;
        	case R.id.menuJuegoPreferencias:
        		Intent actividadPreferencias = new Intent(getBaseContext(), Preferencias.class);
        		startActivity(actividadPreferencias);
        		break;
            case R.id.menuJuegoSalir:
        		if (miPantalla.leeModoJuego() == Pantalla.MODO_FIN) {
                	this.finish();
        		} else {
        			showDialog(DIALOGO_SALIR);
        		}
        	    // Si el juego está en pausa (bien porque lo estaba, bien porque la acabamos
        	    //   de poner, solicitamos confirmación para salir
        	    //if (miSerpienteVista.leeModoJuego() == SerpienteVista.PAUSA) {
        	    	showDialog(DIALOGO_SALIR);
        	    //} else {
        	    	// Si el juego no está pausado (principio o final de juego) salimos sin más
                	//this.finish();
        	    //}
            	break;
        }
        return true;
    }


    //************************************************************************
    //       DIÁLOGOS
    //************************************************************************
	/**
	 *  Función de creación de diálogos llamada desde showDialog
	 * 
	 */
	protected Dialog onCreateDialog(int idDialogo) {
	    Dialog miDialogo = null;
	    
	    switch(idDialogo) {
	    	case DIALOGO_SALIR:
	    		// Generamos un constructor para el diálogo "Salir"
				AlertDialog.Builder dialogoSalir = new AlertDialog.Builder(this);
				dialogoSalir.setMessage(R.string.menuSalirTexto_Jugando)
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

	
    //************************************************************************
    //       INTERFAZ ACELERÓMETRO
    //************************************************************************
    /** Función que se lanza al detectarse un cambio en el acelerómetro **/
	public void onAcelerometroCambio(float x, float y, float z) {
		miPantalla.mueveHeroeAcel(-x, y);
	}

    /** Función que se lanza al detectarse una agitación **/
	public void onAcelerometroAgitacion(float fuerza) {
		;
	}

    /** Función que se lanza al detectarse una agitación instantánea **/
	public void onAcelerometroAgitacionInstantanea(float fuerza) {
		;
	}

}