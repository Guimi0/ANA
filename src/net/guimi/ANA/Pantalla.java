package net.guimi.ANA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;


/**
 * Nuestra superficie de juego.
 * 
 * Para poder utilizarla necesitamos implementar SurfaceHolder que nos provee del lienzo a utilizar
 * 
 * @author Guimi (http://guimi.net), estructura basada en un trabajo de martin (http://www.droidnova.com)
 */
public class Pantalla extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = "Pantalla";
	/* Niveles de debug
	v — Verbose (lowest priority) - Eclipse/LogCat: mensajes en negro
	d — Debug					  - Eclipse/LogCat: mensajes en azul
	i — Info					  - Eclipse/LogCat: mensajes en verde
	w — Warning	                  - Eclipse/LogCat: mensajes en amarillo
	e — Error					  - Eclipse/LogCat: mensajes en rojo
	f — Fatal
	s — Silent (highest priority, on which nothing is ever printed)
	*/
    public static final boolean DEBUG = false;

	// Definimos los identificadores con "static final ints" en vez de una enumeración por rendimiento
	//----------------------------------------------------------------------------------------
	// Modo de juego
    public static final int MODO_PAUSA = 0;
    public static final int MODO_EN_MARCHA = 1;
    public static final int MODO_FIN = 2;
    public static final int MODO_CAMBIO_NIVEL = 3;
    public static final int MODO_TIENDA = 4;
    public static final int MODO_TUTORIAL = 5;

    static final String FICHERO_PUNTUACIONES = "ANA_puntuaciones";
    private String android_id;
    /** Variable auxiliar que indica la versión de la aplicación */
    private static String versionAplicacion;
    
    // Definimos los identificadores de los diferentes enemigos
	static final int GRAFICO_ALEATORIO = -1;
	static final int GRAFICO_HEROE = 0;
	static final int GRAFICO_ESTRELLA = 1;
	static final int GRAFICO_DISPARO_HEROE = 11;
	static final int GRAFICO_DISPARO_ENEMIGO_3 = 12;
	static final int GRAFICO_DISPARO_ENEMIGO_9 = 13;
	static final int GRAFICO_DISPARO_ENEMIGO_13 = 14;
	static final int GRAFICO_CAPSULA = 50;
	static final int GRAFICO_BOTIQUIN = 51;
	static final int GRAFICO_BOMBA = 52;
	static final int GRAFICO_DIAMANTE_GRIS = 53;
	static final int GRAFICO_DIAMANTE_VERDE = 54;
	static final int GRAFICO_DIAMANTE_ROJO = 55;
	static final int GRAFICO_EXPLOSION = 101;
	static final int GRAFICO_BRILLO = 102;
	static final int GRAFICO_RADAR = 103;
	static final int GRAFICO_ANIMACION_HEROE = 201;
	static final int GRAFICO_ROCA = 501;
	static final int GRAFICO_ENEMIGO_01 = 1001;
	static final int GRAFICO_ENEMIGO_02 = 1002;
	static final int GRAFICO_ENEMIGO_03 = 1003;
	static final int GRAFICO_ENEMIGO_04 = 1004;
	static final int GRAFICO_ENEMIGO_05 = 1005;
	static final int GRAFICO_ENEMIGO_06 = 1006;
	static final int GRAFICO_SUPERENEMIGO_01 = 2001;
	static final int GRAFICO_SUPERENEMIGO_02 = 2002;
	static final int GRAFICO_SUPERENEMIGO_03 = 2003;
	static final int GRAFICO_SUPERENEMIGO_01b = 2101;
	static final int GRAFICO_SUPERENEMIGO_02b = 2102;

	// La variable "usoAcelerometro" nos indica si queremos usar el acelerómetro y con qué sensibilidad
	static final int ACEL_DESCONECTADO = 0;
	static final int ACEL_SENSIBILIDAD_BAJA = 1;
	static final int ACEL_SENSIBILIDAD_MEDIA = 2;
	static final int ACEL_SENSIBILIDAD_ALTA = 3;
    private static int usoAcelerometro = ACEL_SENSIBILIDAD_MEDIA;

	// Parámetros de pantalla
	static final int BARRA_ANCHO = 100; // Ancho de la barra de vida
	static final int BARRA_ALTO = 10; // Alto de la barra de vida

	// Parámetros de juego
	static final int VIDA_INICIAL = 30; // Vida inicial del Heroe
	static final int VIDA_MAXIMA = 40; // Vida máxima del Heroe
	static final int NAVES_INICIALES = 2; // Naves de repuesto iniciales que tiene el Heroe
	static final int NAVES_MAXIMAS = 4; // Naves de repuesto máximas que puede tener el Heroe
	static final int BOMBAS_INICIALES = 3; // Bombas iniciales que tiene el Heroe
	static final int BOMBAS_MAXIMAS = 9; // Bombas máximas que puede tener el Heroe
	static final int TIEMPO_INVULNERABLE = 150; // Tiempo durante el cual la nave es invulnerable
	static final int TIEMPO_INVISIBLE = 40; // Tiempo durante el cual la nave es invulnerable
	static final int TIEMPO_NO_BOMBA = 30; // Tiempo durante el cual la nave es invulnerable
	static final float VELOCIDAD_MAXIMA_HEROE = 10f;
	static final float VELOCIDAD_ALTA_HEROE = VELOCIDAD_MAXIMA_HEROE * 0.8f;
	static final float VELOCIDAD_MEDIA_HEROE = VELOCIDAD_MAXIMA_HEROE * 0.2f;
	static final float ACELERACION_BAJA = 1f;
	static final float ACELERACION_MEDIA = 1.8f;
	static final float ACELERACION_ALTA = 2.6f;
	static final float COMPENSACION_Y = 2.5f;

	// Variables auxiliares
	//-------------------------------------
    /** Datos aleatorios */
    Random aleatorio = new Random();
    /** Variable auxiliar que indica si debemos usar sonido */
    private static boolean usoSonido = true;
    /** Variable auxiliar que indica si debemos mostrar tutorial */
    private static boolean prefTutorial = true;
    /** Desplazamiento X del héroe */
	private static float despHeroeX = 0;
    /** Desplazamiento Y del héroe */
	private static float despHeroeY = 0;
	/** Tiempo durante el cual no se puede lanzar una bomba (para evitar lanzar dos seguidas sin querer) */
	private static long tiempoNoBomba = 0;
	/** Tiempo durante el cual el heroe es invulnerable */
	private static long tiempoInvulnerable = TIEMPO_INVULNERABLE;
	/** Tiempo durante el cual el heroe es invisible */
	private static long tiempoInvisible = 0;
	private static boolean escudoVisible = true;
	/** Esta variable, si es mayor que 0, indica que deben aparecer enemigos cada x ciclos
	 * se usa para generar enemigos mientras hay un super enemigo en pantalla */
	private int enemigosAleatorios = 0;
	/** A veces se llama a guardaPuntuacion dos veces seguidas. Con esta variable lo controlamos */
	private boolean puntuacionGuardada = false;
	
    /** Variable auxiliar para almacenar parámetros de pintura como color, tipo de letra... */
	Paint miPaint = new Paint();
	/** Variable auxiliar para definir el rectangulo de vida */
    RectF miRectF = new RectF(0, 0, 0, 0);
	/* Variable para acceder a los recursos (la R) */
    private Resources misRecursos;
    private Context miContexto;
    /** Hilo que gestiona el ciclo de juego */
    private ANAHilo miHilo;
    /** Un "SoundPool" que nos permite reproducir varios sonidos a la vez */
    private SoundPool miSoundPool;
    /** Identificador de nuestro archivo de sonido */
    private int sonidoExplosion = 0;
    private int sonidoBomba = 0;
    private int sonidoCapsula = 0;
    private int sonidoAyuda = 0;
    private int sonidoRadar = 0;
    /** Variable para guardar un caché de todas las imágenes que utilizamos */
    private Map<Integer, Bitmap> miCacheGraficos = new HashMap<Integer, Bitmap>();
    /** El objeto gráfico que representa nuestro heroe; el que maneja el jugador */
    private Grafico miHeroe;
    //private int ratioEnemigos = 70;
    private static String nombrePiloto = "Guimi";
    
    
    // Variables para el contador de FPS
	//--------------------------------------------
    private long momentoInicial;     
    private long momentoAnterior;     
    private int marcosIntervalo = 0;
    private int marcosTotales = 0;
    private int miFPS = 0;
    private int miFPSGral = 0;
    
    
    // Gestión de niveles
	//--------------------------------------------
    // Indica el nivel en que nos encontramos
    private int nivelActual = 1;
    // Este vector almacena todo el nivel
    private int[][] vectorNivelActual = nivel01;
    // Este vector almacena la próxima acción indicada en el nivel 
    private int[] vectorAccionActual = {};
    // Índice del nivel Actual que indica la acción cargada en vectorAccionActual  
    private int accionActual = 0;

	// Vectores de gráficos
	//--------------------------------------------
    /** Vector de estrellas a gestionar */
    private ArrayList<Grafico> vectorEstrellas = new ArrayList<Grafico>();
    /** Vector de enemigos a gestionar */
    private ArrayList<Grafico> vectorEnemigos = new ArrayList<Grafico>();    
    /** Vector de efectos gráficos a gestionar */
    private ArrayList<Grafico> vectorEfectos = new ArrayList<Grafico>();
    /** Vector de ayudas a gestionar */
    private ArrayList<Grafico> vectorAyudas = new ArrayList<Grafico>();
    /** Vector de disparos del heroe a gestionar */
    private ArrayList<Grafico> vectorDisparosHeroe = new ArrayList<Grafico>();
    /** Vector de disparos enemigos a gestionar */
    private ArrayList<Grafico> vectorDisparosEnemigo = new ArrayList<Grafico>();

	// Contadores de juego
	//------------------------------------
    /** Contador de ciclos de juego */
    private long cicloJuego = 0;
    /** Contador de puntos de juego */
    private int puntosJuego = 0;
    /** Contador de créditos de juego */
    private int creditosJuego = 0;
    private int creditosJuegoAnterior = 0;
    /** Contador de naves de repuesto ("vidas")*/
    private int navesRepuesto = NAVES_INICIALES;
    /** Contador de nivel de juego*/
    //private int nivelJuego = 1;
    /** Contador de nivel de juego*/
    private int numeroBombas = BOMBAS_INICIALES;
    /** Modo de la aplicación: [PAUSA, EN_MARCHA, FIN, CAMBIO_NIVEL] */
    private static int modoJuego = MODO_EN_MARCHA;

	// Opciones Tienda
	//------------------------------------
    public boolean opcDisparo7 = false;
    public boolean opcDisparo9 = false;
    public boolean opcVida40 = false;
	// Objetivos y Estadísticas Juego
	//------------------------------------
	private int estadEnemigosEscapados = 0;
	private int estadEnemigosEliminados = 0;
	private float porEnemigosEliminados = 0;
	public boolean objSinCompras = true;
	public boolean objNoBombas = true;
	public boolean objTodosEnemigos = true;
	public boolean objNingunEnemigo = true;
	public boolean objTerminarNivel = false;
	private int creditosTemporal = 0;

    
    /**
     * Constructor
     * @param contexto - Contexto de la actividad que llama a la clase
     */
    public Pantalla(Context contexto) {
        super(contexto);
        
        miContexto = contexto;

        // Cargamos la caché de gráficos
        cargaCacheGraficos();
        // Generamos el "SoundPool"
        miSoundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 100);
        // Cargamos los ficheros de sonido
        sonidoExplosion = miSoundPool.load(getContext(), R.raw.explosion_espacial, 0);
        sonidoBomba = miSoundPool.load(getContext(), R.raw.explosion_bomba_grande, 0);
        sonidoAyuda = miSoundPool.load(getContext(), R.raw.ayuda, 0);
        sonidoCapsula = miSoundPool.load(getContext(), R.raw.capsula, 0);
        sonidoRadar = miSoundPool.load(getContext(), R.raw.radar, 0);
        
        // Añadimos nuestra pantalla al SurfaceHolder
        getHolder().addCallback(this);
        
        // Tomamos los recursos del juego (la R)
        misRecursos = miContexto.getResources();

		// Configuración básica de miPaint
        miPaint.setStyle(Paint.Style.FILL);
        miPaint.setAntiAlias(true);

        // Inicializamos el heroe
        miHeroe = new Grafico(miCacheGraficos.get(R.drawable.heroe_animacion_40), 29, 40, 5);
        miHeroe.ponTipo(GRAFICO_HEROE);

		// Inicializamos el juego
		iniciaJuego();

        // Generamos nuestro hilo de juego
        miHilo = new ANAHilo(this);
    }


	/** Función que prepara el juego al inicio */
    public void iniciaJuego() {
		// Limpiamos marcadores
		//----------------------------------
    	puntosJuego = 0;
		creditosJuego = 0;
		creditosJuegoAnterior = 0;
		navesRepuesto = NAVES_INICIALES;
		numeroBombas = BOMBAS_INICIALES;
    	// Limpiamos el objetivo "SinCompras"
    	objSinCompras = true;
		// Configuramos la vida del Heroe
        miHeroe.ponVidaObjeto(VIDA_INICIAL);
        // Indicamos que no hemos guardado la puntuación de la partida
        puntuacionGuardada = false;
        
		// Cargamos el nivel 1
		//----------------------------------
		cargaNivel(1);
		leePreferencias();
		if (prefTutorial) {
			modoJuego = MODO_TUTORIAL;
		} else {
			modoJuego = MODO_EN_MARCHA;
		}

		// Indicamos que aceptamos el foco
		//----------------------------------
        setFocusable(true);
	}
    
    
    public void leePreferencias() {
        // Get the xml/preferences.xml preferences
        SharedPreferences misPreferencias = PreferenceManager.getDefaultSharedPreferences(getContext());
        usoSonido = misPreferencias.getBoolean("miPrefSonido", true);
        prefTutorial = misPreferencias.getBoolean("miPrefTutorial", true);
		usoAcelerometro = Integer.parseInt(misPreferencias.getString("miPrefAcelerometro", "2"));
        nombrePiloto = misPreferencias.getString("miPrefNombre", "Guimi");
    }

    public void ponAndroid_id (String mi_id) {
    	android_id = mi_id;
    }
    
    public void ponVersionAplicacion (String mi_version) {
    	versionAplicacion = mi_version;
    }
    
    //************************************************************************
    //       TOQUE EN PANTALLA
    //************************************************************************
    /**
     * Gestionamos el evento de toque en pantalla
     */
    //@Override
    public boolean onTouchEvent(MotionEvent miEvento) {
    	// Sincronizamos con la superficie
        synchronized (getHolder()) {
            if (miEvento.getAction() == MotionEvent.ACTION_DOWN) {
            	// Si estamos en juego
            	if (modoJuego == MODO_EN_MARCHA) {
            		// Si pulsa en el botón de bombas, lanzamos una
                	if ((miEvento.getX() > getWidth()-60) && (miEvento.getY() > getHeight()-60)) {
                		lanzaBomba();
                	}
            	} else if (modoJuego == MODO_TUTORIAL) {
            		modoJuego = MODO_EN_MARCHA;
            	} else if (modoJuego == MODO_PAUSA) {
            		leePreferencias();
            		modoJuego = MODO_EN_MARCHA;
            	} else if (modoJuego == MODO_TIENDA) {
            		//Toast.makeText(getContext(), String.valueOf(miEvento.getX())+","+String.valueOf(miEvento.getY()), Toast.LENGTH_SHORT).show();

            		// Según donde pulse hacemos una cosa u otra
                	if ((miEvento.getX() > 30) && (miEvento.getX() < 300)  
                			&& (miEvento.getY() > 90) && (miEvento.getY() < 120)) {
                    	// Compramos una bomba
                		if (creditosJuego >= 300) {
                			if (numeroBombas < BOMBAS_MAXIMAS) {
                				// Aumentamos el número de bombas
                        		numeroBombas ++;
                				// Disminuimos el número de créditos
                        		creditosJuego = creditosJuego - 300;
                        		// Avisamos de la compra
                        		Toast.makeText(getContext(), (String)misRecursos.getText(R.string.juego_bomba_comprada), Toast.LENGTH_SHORT).show();
                            	// Emitimos el sonido de la ayuda
                            	if (usoSonido) miSoundPool.play(sonidoAyuda, 1, 1, 0, 0, 1);
                			} else {
                        		Toast.makeText(getContext(), (String)misRecursos.getText(R.string.juego_bombas_maximas), Toast.LENGTH_SHORT).show();
                			}
                		} else {
                    		Toast.makeText(getContext(), (String)misRecursos.getText(R.string.juego_sin_creditos), Toast.LENGTH_SHORT).show();
                		}
                	} else if ((miEvento.getX() > 30) && (miEvento.getX() < 300)  
                			&& (miEvento.getY() > 150) && (miEvento.getY() < 180)) {
                    	// Compramos blindaje
                		if (creditosJuego >= 300) {
                			if (miHeroe.leeVidaObjeto() < VIDA_MAXIMA) {
                				// Aumentamos el blindaje
                				miHeroe.modificaVidaObjeto(10);
                				if (miHeroe.leeVidaObjeto() > VIDA_MAXIMA) {
                					miHeroe.ponVidaObjeto(VIDA_MAXIMA);
                				}
                				// Disminuimos el número de créditos
                        		creditosJuego = creditosJuego - 300;
                        		// Avisamos de la compra
                        		Toast.makeText(getContext(), (String)misRecursos.getText(R.string.juego_vida_comprada), Toast.LENGTH_SHORT).show();
                            	// Emitimos el sonido de la ayuda
                            	if (usoSonido) miSoundPool.play(sonidoAyuda, 1, 1, 0, 0, 1);
                			} else {
                        		Toast.makeText(getContext(), (String)misRecursos.getText(R.string.juego_vidas_maximas), Toast.LENGTH_SHORT).show();
                			}
                		} else {
                    		Toast.makeText(getContext(), (String)misRecursos.getText(R.string.juego_sin_creditos), Toast.LENGTH_SHORT).show();
                		}
                	} else if ((miEvento.getX() > 30) && (miEvento.getX() < 300)  
                			&& (miEvento.getY() > 210) && (miEvento.getY() < 240)) {
                    	// Compramos nave extra
                		if (creditosJuego >= 1600) {
                			if (navesRepuesto < NAVES_MAXIMAS) {
                				// Aumentamos el número de bombas
                				navesRepuesto ++;
                				// Disminuimos el número de créditos
                        		creditosJuego = creditosJuego - 1600;
                        		// Avisamos de la compra
                        		Toast.makeText(getContext(), (String)misRecursos.getText(R.string.juego_nave_comprada), Toast.LENGTH_SHORT).show();
                            	// Emitimos el sonido de la ayuda
                            	if (usoSonido) miSoundPool.play(sonidoAyuda, 1, 1, 0, 0, 1);
                			} else {
                        		Toast.makeText(getContext(), (String)misRecursos.getText(R.string.juego_naves_maximas), Toast.LENGTH_SHORT).show();
                			}
                		} else {
                    		Toast.makeText(getContext(), (String)misRecursos.getText(R.string.juego_sin_creditos), Toast.LENGTH_SHORT).show();
                		}
                	} else if ((miEvento.getX() > 60) && (miEvento.getX() < 300)  
                			&& (miEvento.getY() > 400) && (miEvento.getY() < 450)) {
                		// Cargamos el nuevo nivel
                		leePreferencias();
                		nivelActual++;
                		cargaNivel(nivelActual);
                	}
            	} else if (modoJuego == MODO_CAMBIO_NIVEL) {
            		modoJuego = MODO_TIENDA;
            	} else if (modoJuego == MODO_FIN) {
            		// En modo fin no hacemos nada
            		//-> Forzamos a que pulsen "back" o usen el menú.
            	}
            } else if (miEvento.getAction() == MotionEvent.ACTION_MOVE) {
            } else if (miEvento.getAction() == MotionEvent.ACTION_UP) {
            }
        }
    	return true;
    }
    
    
    //************************************************************************
    //       TECLADO
    //************************************************************************
    //@Override
    public boolean onKeyDown(int codigoTecla, KeyEvent miEvento) {
        if (codigoTecla == KeyEvent.KEYCODE_DPAD_UP) {
    		despHeroeY = -(VELOCIDAD_MEDIA_HEROE+1);
    		// Para detectar "longpress"
    		miEvento.startTracking();
            return (true);
        }
        if (codigoTecla == KeyEvent.KEYCODE_DPAD_DOWN) {
    		despHeroeY = (VELOCIDAD_MEDIA_HEROE+1);
    		// Para detectar "longpress"
    		miEvento.startTracking();
            return (true);
        }
        if (codigoTecla == KeyEvent.KEYCODE_DPAD_LEFT) {
    		despHeroeX = -(VELOCIDAD_MEDIA_HEROE+1);
    		// Para detectar "longpress"
    		miEvento.startTracking();
            return (true);
        }
        if (codigoTecla == KeyEvent.KEYCODE_DPAD_RIGHT) {
    		despHeroeX = (VELOCIDAD_MEDIA_HEROE+1);
    		// Para detectar "longpress"
    		miEvento.startTracking();
            return (true);
        }

        if (codigoTecla == KeyEvent.KEYCODE_DPAD_CENTER) {
        	lanzaBomba();
            return (true);
        }
        return super.onKeyDown(codigoTecla, miEvento);
    }

    public boolean onKeyLongPress(int codigoTecla, KeyEvent miEvento) {
        if (codigoTecla == KeyEvent.KEYCODE_DPAD_UP) {
    		despHeroeY = -VELOCIDAD_MAXIMA_HEROE;
    		// Indicamos que NO hemos finalizado (lo haremos en keyup)
            return false;
        }
        if (codigoTecla == KeyEvent.KEYCODE_DPAD_DOWN) {
    		despHeroeY = VELOCIDAD_MAXIMA_HEROE;
    		// Indicamos que NO hemos finalizado (lo haremos en keyup)
            return false;
        }
        if (codigoTecla == KeyEvent.KEYCODE_DPAD_LEFT) {
        	despHeroeX = -VELOCIDAD_MAXIMA_HEROE;
    		// Indicamos que NO hemos finalizado (lo haremos en keyup)
            return false;
        }
        if (codigoTecla == KeyEvent.KEYCODE_DPAD_RIGHT) {
        	despHeroeX = VELOCIDAD_MAXIMA_HEROE;
    		// Indicamos que NO hemos finalizado (lo haremos en keyup)
            return false;
        }
        return super.onKeyLongPress(codigoTecla, miEvento);
    }
    
    public boolean onKeyUp(int codigoTecla, KeyEvent miEvento) {
        if ((codigoTecla == KeyEvent.KEYCODE_DPAD_UP) || (codigoTecla == KeyEvent.KEYCODE_DPAD_DOWN)) {
    		despHeroeY = 0;
            return (true);
        }
        if ((codigoTecla == KeyEvent.KEYCODE_DPAD_LEFT) || (codigoTecla == KeyEvent.KEYCODE_DPAD_RIGHT)) {
    		despHeroeX = 0;
            return (true);
        }
        return super.onKeyDown(codigoTecla, miEvento);
    }
    
    //************************************************************************
    //       CICLO DE JUEGO
    //************************************************************************
    //************************************************************************
    //       ACTUALIZACIÓN DE OBJETOS GRÁFICOS
    //************************************************************************
    /**
     * Esta función actualiza la posición de los objetos gráficos
     */
    public void actualizaObjetos() {
    	// Excepto en pausa, siempre movemos el fondo
    	if (modoJuego != MODO_PAUSA) actualizaFondo();
    	// Solo si estamos en marcha movemos el ciclo de juego y el heroe
    	if (modoJuego == MODO_EN_MARCHA) {
    		actualizaCicloJuego();
        	actualizaHeroe();
    	}
    	// En marcha y fin movemos todo
    	if ((modoJuego == MODO_EN_MARCHA) || (modoJuego == MODO_FIN)) {
    		actualizaDisparosHeroe();
    		actualizaEnemigos();
        	actualizaEfectos();
        	actualizaAyudas();
    	}
    }
    private void actualizaFondo() {
    	if (modoJuego != MODO_TUTORIAL) {
    		// Vector auxiliar de gráficos a extraer de otros vectores
            ArrayList<Grafico> objetosFuera = new ArrayList<Grafico>();

            // ESTRELLAS
            //-----------------------------------------------------
    		// Cada varios ciclos generamos estrellas
            if ((cicloJuego % 300)==0) creaEstrella(false);
            // Cada varios ciclos movemos las estrellas...
            if ((cicloJuego % 5)==0) {
    			for (Grafico miGrafico : vectorEstrellas) {
                	// Lanzamos su IA
                	miGrafico.ejecutaIA(this);

                	// Si sale de pantalla lo asignamos para eliminar
                	if (miGrafico.esAcabado()) {
                		objetosFuera.add(miGrafico);
                    }
                }
                // Eliminamos las estrellas que están marcadas para ello
                if (!objetosFuera.isEmpty()) {
                	vectorEstrellas.removeAll(objetosFuera);
                	objetosFuera.clear();
                }
    		}
    	}
    }
    private void actualizaCicloJuego() {
		// Cada varios ciclos generamos disparos y bombas...
        //if ((cicloJuego % 30000)==0) numeroBombas++;
        if ((tiempoInvisible < cicloJuego) && ((cicloJuego % 18)==0)) creaDisparoHeroe();

        // Seguimos el mapa de nivel
        //Momento, Vector (1 - ayudas, 2 - enemigos, 3 - efectos), AZAR (0-no, 1-si), Objeto, X, Y, despX, despY
        while (vectorAccionActual[0]==cicloJuego) { // Momento
        	switch (vectorAccionActual[1]) { // Vector
        		case 1: // Vector de ayudas
           			creaCapsula(vectorAccionActual[3]);
        			break;
        		case 2: // Vector de enemigos
        			if (vectorAccionActual[2] == 0) {
        				// Objeto, X, Y, despX, despY
            			creaEnemigo(vectorAccionActual[3], vectorAccionActual[4], vectorAccionActual[5], vectorAccionActual[6], vectorAccionActual[7]);
        			} else {
        				// Obejto aleatorio
        				creaEnemigoAzar(vectorAccionActual[3]);
        			}
        			break;
        		case 3: // Vector de efectos
    				creaEfecto(vectorAccionActual[3]);
        			break;
        	}
        	accionActual++;
			vectorAccionActual = vectorNivelActual[accionActual];
        } // while (vectorAccionActual[0]==cicloJuego)
        
        if (enemigosAleatorios > 0) {
    		// JUEGO ALEATORIO
            if ((cicloJuego % enemigosAleatorios)==0) creaEnemigoAzar(GRAFICO_ALEATORIO);
            /*
            if ((cicloJuego % 2000)==0) {
            	enemigosAleatorios=enemigosAleatorios-5;
            	if (enemigosAleatorios < 30) enemigosAleatorios = 30;
            }
            if ((cicloJuego % 4000)==0) {
            	numeroBombas++;
            	creaCapsula(GRAFICO_BOTIQUIN);
            }
            */
        }
        
        if ((vectorAccionActual[0] == -1) &&
        	(vectorEnemigos.isEmpty()) &&
        	(vectorEfectos.isEmpty()) &&
        	(vectorAyudas.isEmpty()) &&
        	(vectorDisparosEnemigo.isEmpty())) {
        		// Limpiamos el vector de disparos del Héroe
                vectorDisparosHeroe.clear();

        		// Sumamos créditos
        		//----------------------------
                creditosJuegoAnterior = creditosJuego;

        		// Fin de nivel
        		creditosJuego = creditosJuego + 200;
        		// Porcentaje de enemigos eliminados
            	porEnemigosEliminados = estadEnemigosEliminados * 100 / (estadEnemigosEliminados + estadEnemigosEscapados);
            	if (porEnemigosEliminados < 95) {
                	if (porEnemigosEliminados < 90) {
                    	if (porEnemigosEliminados < 75) {
                    		if (porEnemigosEliminados > 50) {
                    			creditosTemporal = (int) (2 * porEnemigosEliminados);   		
                    		} else {
                    			creditosTemporal = 0;
                    		}
                		} else {
                        	creditosTemporal = (int) (3 * porEnemigosEliminados);   		
                		}
                	} else {
                    	creditosTemporal = (int) (5 * porEnemigosEliminados);   		
                	}
            	} else {
                	creditosTemporal = (int) (6 * porEnemigosEliminados);   		
            	}
        		creditosJuego = creditosJuego + creditosTemporal;
        		// Blindaje restante
        		creditosJuego = creditosJuego + (10 * miHeroe.leeVidaObjeto());
        		// Naves de repuesto restantes
        		creditosJuego = creditosJuego + (300 * navesRepuesto);
        		// Bombas restantes
        		creditosJuego = creditosJuego + (20 * numeroBombas);

        		if (estadEnemigosEliminados == 0) objNingunEnemigo = true;
        		else if (estadEnemigosEscapados == 0) objTodosEnemigos = true;
        		
        		// Indicamos que estamos en cambio de nivel
                modoJuego=MODO_CAMBIO_NIVEL;
        }

        // Incrementamos el contador de ciclo de juego
        cicloJuego++;

    }
    private void actualizaHeroe() {
        // HEROE
        //-----------------------------------------------------
        // Realizamos el movimiento del heroe con los datos del acelerómetro
    	miHeroe.ponX(miHeroe.leeX() + (int) despHeroeX);
    	miHeroe.ponY(miHeroe.leeY() + (int) despHeroeY);
        // Indicamos el marco adecuado a dibujar // left, top, right, bottom
        if (despHeroeX < -VELOCIDAD_ALTA_HEROE) miHeroe.ponRectangulo(64,0,9,40);
        else if (despHeroeX < -VELOCIDAD_MEDIA_HEROE) miHeroe.ponRectangulo(35,0,22,40);
        else if (despHeroeX > VELOCIDAD_ALTA_HEROE) miHeroe.ponRectangulo(83,0,9,40);
        else if (despHeroeX > VELOCIDAD_MEDIA_HEROE) miHeroe.ponRectangulo(99,0,22,40);
        else miHeroe.ponRectangulo(0,0,29,40);
        // Verificamos que no salga de pantalla en el eje X
        if (miHeroe.leeX() < 0) {
        	miHeroe.ponX(0);
        } else if (miHeroe.leeX() + miHeroe.leeAnchoSprite() > getWidth()) {
        	miHeroe.ponX(getWidth() - miHeroe.leeAnchoSprite());
        }
        // Verificamos que no salga de pantalla en el eje Y
        if (miHeroe.leeY() < 0) {
        	miHeroe.ponY(0);
        } else if (miHeroe.leeY() + miHeroe.leeAltoSprite() > getHeight()) {
        	miHeroe.ponY(getHeight() - miHeroe.leeAltoSprite());
        }
        // Actualizamos la variable escudoVisible
        if (tiempoInvulnerable > cicloJuego) {
        	// Si la nave es invulnerable dibujamos intermitentemente el escudo
        	if ((tiempoInvulnerable-cicloJuego) % 10 == 0) escudoVisible = !escudoVisible;
        } else {
        	// Si la nave es vulnerable no dibujamos el escudo
        	escudoVisible = false;
        }
    }
    private void actualizaDisparosHeroe() {
        // DISPAROS DEL HEROE
        //-----------------------------------------------------
        // Vector auxiliar de gráficos a extraer de otros vectores
        ArrayList<Grafico> objetosFuera = new ArrayList<Grafico>();
                
        // Para cada objeto gráfico del vector de disparos del heroe...
        for (Grafico miGrafico : vectorDisparosHeroe) {
        	// Lanzamos su IA
        	miGrafico.ejecutaIA(this);
        	
        	// Si queda fuera de pantalla lo asignamos para eliminar
        	if (miGrafico.esAcabado()) {
        		objetosFuera.add(miGrafico);
            }
        }
        // Eliminamos los disparos que están marcados para ello
        if (!objetosFuera.isEmpty()) {
        	vectorDisparosHeroe.removeAll(objetosFuera);
        	objetosFuera.clear();
        }
    }
    private void actualizaEnemigos() {
		// Vector auxiliar de gráficos a extraer de otros vectores
        ArrayList<Grafico> objetosFuera = new ArrayList<Grafico>();
                
        // ENEMIGOS
        //-----------------------------------------------------
        // Para cada objeto gráfico del vector de enemigos...
        for (Grafico miGrafico : vectorEnemigos) {
        	// Lanzamos su IA
        	miGrafico.ejecutaIA(this);
        	
        	// Creamos disparos si procede
        	if (miGrafico.leeDebeDisparar()) {
        		if ((miGrafico.leeTipo() == GRAFICO_ENEMIGO_01)
        				|| (miGrafico.leeTipo() == GRAFICO_ENEMIGO_06)
            			|| ((miGrafico.leeTipo() == GRAFICO_ENEMIGO_03)
           					&& (miGrafico.leeX() + miGrafico.leeAnchoSprite()/2 > miHeroe.leeX())
           					&& (miGrafico.leeX() + miGrafico.leeAnchoSprite()/2 < (miHeroe.leeX()+miHeroe.leeAnchoSprite())))) {
                		creaDisparoEnemigo(miGrafico.leeX()+(miGrafico.leeAnchoSprite()/2), miGrafico.leeY()+miGrafico.leeAltoSprite(), GRAFICO_DISPARO_ENEMIGO_3);
                		miGrafico.ponDebeDisparar(false);
            	} else if (miGrafico.leeTipo() == GRAFICO_ENEMIGO_05) {
                		creaDisparoEnemigo(miGrafico.leeX()+23, miGrafico.leeY()+miGrafico.leeAltoSprite(), GRAFICO_DISPARO_ENEMIGO_3);
                		creaDisparoEnemigo(miGrafico.leeX()+35, miGrafico.leeY()+miGrafico.leeAltoSprite(), GRAFICO_DISPARO_ENEMIGO_3);
                		miGrafico.ponDebeDisparar(false);
            	} else if (miGrafico.leeTipo() == GRAFICO_SUPERENEMIGO_01) {
            		creaDisparoEnemigo(miGrafico.leeX()+49, miGrafico.leeY()+80, GRAFICO_DISPARO_ENEMIGO_13);
            		miGrafico.ponDebeDisparar(false);
            	} else if (miGrafico.leeTipo() == GRAFICO_SUPERENEMIGO_02) {
            		creaDisparoEnemigo(miGrafico.leeX()+5, miGrafico.leeY()+72, GRAFICO_DISPARO_ENEMIGO_9);
            		creaDisparoEnemigo(miGrafico.leeX()+16, miGrafico.leeY()+72, GRAFICO_DISPARO_ENEMIGO_9);
            		creaDisparoEnemigo(miGrafico.leeX()+89, miGrafico.leeY()+72, GRAFICO_DISPARO_ENEMIGO_9);
            		creaDisparoEnemigo(miGrafico.leeX()+102, miGrafico.leeY()+72, GRAFICO_DISPARO_ENEMIGO_9);
            		miGrafico.ponDebeDisparar(false);
            	} else if (miGrafico.leeTipo() == GRAFICO_SUPERENEMIGO_03) {
            		creaDisparoEnemigo(miGrafico.leeX()+5, miGrafico.leeY()+72, GRAFICO_DISPARO_ENEMIGO_13);
            		creaDisparoEnemigo(miGrafico.leeX()+16, miGrafico.leeY()+72, GRAFICO_DISPARO_ENEMIGO_13);
            		creaDisparoEnemigo(miGrafico.leeX()+89, miGrafico.leeY()+72, GRAFICO_DISPARO_ENEMIGO_13);
            		creaDisparoEnemigo(miGrafico.leeX()+102, miGrafico.leeY()+72, GRAFICO_DISPARO_ENEMIGO_13);
            		miGrafico.ponDebeDisparar(false);
            	}
        	}
        	
        	// Si sale de pantalla lo asignamos para eliminar
        	if (miGrafico.esAcabado()) {
        		objetosFuera.add(miGrafico);
        		if ((miGrafico.leeTipo() == GRAFICO_ROCA) || (miGrafico.leeTipo() == GRAFICO_CAPSULA)) {
        			;
        		} else {
        			if (modoJuego == MODO_EN_MARCHA) estadEnemigosEscapados++;
        		}
            }
        }
        // Eliminamos los enemigos que están marcados para ello
        if (!objetosFuera.isEmpty()) {
        	vectorEnemigos.removeAll(objetosFuera);
        	objetosFuera.clear();
        }
        
        // DISPAROS ENEMIGOS
        //-----------------------------------------------------
        // Para cada objeto gráfico del vector de disparos de enemigos...
        for (Grafico miGrafico : vectorDisparosEnemigo) {
        	// Lanzamos su IA
        	miGrafico.ejecutaIA(this);
        	
        	// Si queda fuera de pantalla lo asignamos para eliminar
        	if (miGrafico.esAcabado()) {
        		objetosFuera.add(miGrafico);
            }
        }
        // Eliminamos los disparos que están marcados para ello
        if (!objetosFuera.isEmpty()) {
        	vectorDisparosEnemigo.removeAll(objetosFuera);
        	objetosFuera.clear();
        }
    }

    private void actualizaEfectos() {
        // EFECTOS GRÁFICOS
        //-----------------------------------------------------
		// Vector auxiliar de gráficos a extraer de otros vectores
        ArrayList<Grafico> objetosFuera = new ArrayList<Grafico>();
        // Dibujamos cada efecto en el lienzo
        for (Grafico miGrafico : vectorEfectos) {
        	// Ejecutamos la IA del objeto
        	miGrafico.ejecutaIA(this);
            // Si el efecto ya ha terminado su ciclo de vida lo marcamos para eliminarlo 
            if (miGrafico.esAcabado()) {
            	objetosFuera.add(miGrafico);
            }
        }
        
        // Eliminamos los efectos que han terminado
        if (!objetosFuera.isEmpty()) {
        	vectorEfectos.removeAll(objetosFuera);
        }
    }

    private void actualizaAyudas() {
        // AYUDAS
        //-----------------------------------------------------
		// Vector auxiliar de gráficos a extraer de otros vectores
        ArrayList<Grafico> objetosFuera = new ArrayList<Grafico>();
        // Para cada objeto gráfico del vector de enemigos...
        for (Grafico miGrafico : vectorAyudas) {
        	// Lanzamos su IA
        	miGrafico.ejecutaIA(this);
        	
        	// Si queda fuera de pantalla lo asignamos para eliminar
        	if (miGrafico.leeY() > getHeight()) {
            	objetosFuera.add(miGrafico);
            }
        }
        // Eliminamos los enemigos que están marcados para ello
        if (!objetosFuera.isEmpty()) {
        	vectorAyudas.removeAll(objetosFuera);
        	objetosFuera.clear();
        }
    }


    //************************************************************************
    //       FUNCIONES DE CREACIÓN DE OBJETOS GRÁFICOS
    //************************************************************************
	/** Esta función crea una nueva estrella para el fondo del juego
	 * @param YAleatorio - Indica si la coordenada Y debe ser aleatorio (true) o debe ser 0 (false) */
    private void creaEstrella(boolean YAleatorio) {
    	// Generamos una variable auxiliar de objeto gráfico
    	Grafico miGrafico = null;
    	// Indicamos que el gráfico es una estrella
		int rand = Math.abs(aleatorio.nextInt() % 12);
		if (rand < 4) miGrafico = new Grafico(miCacheGraficos.get(R.drawable.estrella_3));
		else if (rand < 7) miGrafico = new Grafico(miCacheGraficos.get(R.drawable.estrella_2));
		else if (rand < 9) miGrafico = new Grafico(miCacheGraficos.get(R.drawable.estrella_1));
		else if (rand < 10) miGrafico = new Grafico(miCacheGraficos.get(R.drawable.estrella_4));
		else if (rand < 11) miGrafico = new Grafico(miCacheGraficos.get(R.drawable.estrella_5));
		else if (rand < 12) miGrafico = new Grafico(miCacheGraficos.get(R.drawable.estrella_6));
		miGrafico.ponTipo(GRAFICO_ESTRELLA);

        // Indicamos el desplazamiento inicial
        miGrafico.ponDespX(0);
        miGrafico.ponDespY(1);
        // Indicamos las coordenadas iniciales
		if (YAleatorio) {
			// Al inicio no conocemos el ancho de la vista
			// así que asignamos las estrellas en un rango "fiable" de 0-300
			rand = Math.abs(aleatorio.nextInt() % 300);
	        miGrafico.ponX(rand);
			rand = Math.abs(aleatorio.nextInt() % 300);
	        miGrafico.ponY(rand);
		} else {
			rand = Math.abs(aleatorio.nextInt() % getWidth());
	        miGrafico.ponX(rand);
	        miGrafico.ponY(0);
		}
        // Añadimos el gráfico al vector de estrellas
        vectorEstrellas.add(miGrafico);
    }

    
    private void creaAnimacionHeroe() {
    	// Generamos una variable auxiliar de objeto gráfico
    	Grafico miGrafico = null;
    	// Indicamos el tipo de gráfico
		miGrafico = new Grafico(miCacheGraficos.get(R.drawable.heroe));
		miGrafico.ponTipo(GRAFICO_ANIMACION_HEROE);

        // Indicamos el desplazamiento inicial
        miGrafico.ponDespX(0);
        miGrafico.ponDespY(-3);
        // Indicamos las coordenadas iniciales
        miGrafico.ponX(getWidth()/2 - 64);
        miGrafico.ponY(getHeight()+10);
        // Añadimos el gráfico al vector de estrellas
        vectorEfectos.add(miGrafico);
    }

    
	/** Esta función crea un nuevo disparo enemigo */
    private void creaDisparoEnemigo(int miX, int miY, int disparo) {
    	// Generamos una variable auxiliar de objeto gráfico
    	Grafico miGrafico = null;
    	// Indicamos el gráfico correspondiente
    	switch(disparo) {
    		case GRAFICO_DISPARO_ENEMIGO_3:
    			miGrafico = new Grafico(miCacheGraficos.get(R.drawable.disparo_enemigo_3));
    			miGrafico.ponTipo(GRAFICO_DISPARO_ENEMIGO_3);
    			miGrafico.ponVidaObjeto(10);
    	        // Indicamos el desplazamiento inicial
    	        miGrafico.ponDespX(0);
    	        miGrafico.ponDespY(5);
    			break;
    		case GRAFICO_DISPARO_ENEMIGO_9:
    			miGrafico = new Grafico(miCacheGraficos.get(R.drawable.disparo_9));
    			miGrafico.ponTipo(GRAFICO_DISPARO_ENEMIGO_9);
    			miGrafico.ponVidaObjeto(15);
    	        // Indicamos el desplazamiento inicial
    	        miGrafico.ponDespX(0);
    	        miGrafico.ponDespY(3);
    			break;
    		case GRAFICO_DISPARO_ENEMIGO_13:
    			miGrafico = new Grafico(miCacheGraficos.get(R.drawable.disparo_13));
    			miGrafico.ponTipo(GRAFICO_DISPARO_ENEMIGO_13);
    			miGrafico.ponVidaObjeto(20);
    	        // Indicamos el desplazamiento inicial
    	        miGrafico.ponDespX(0);
    	        miGrafico.ponDespY(3);
    			break;
    	}

        // Indicamos las coordenadas iniciales
		// Alineamos el centro del disparo con el centro del Heroe
        miGrafico.ponX(miX);
        miGrafico.ponY(miY);
        // Añadimos el gráfico al vector de disparos del heroe
        vectorDisparosEnemigo.add(miGrafico);
    }
    
	/** Esta función crea un nuevo disparo del heroe */
    private void creaDisparoHeroe() {
    	// Generamos una variable auxiliar de objeto gráfico
    	Grafico miGrafico = null;
    	// Indicamos que el gráfico es un disparo del héroe
		miGrafico = new Grafico(miCacheGraficos.get(R.drawable.disparo_heroe_3));
		miGrafico.ponTipo(GRAFICO_DISPARO_HEROE);

        // Indicamos el desplazamiento inicial
        miGrafico.ponDespX(0);
        miGrafico.ponDespY(-8);
        // Indicamos las coordenadas iniciales
		// Alineamos el centro del disparo con el centro del Heroe
        miGrafico.ponX(miHeroe.leeX()
			+ (miHeroe.leeAnchoSprite() / 2)
			- (miGrafico.leeAnchoSprite() / 2));
        miGrafico.ponY(miHeroe.leeY());
        // Añadimos el gráfico al vector de disparos del heroe
        vectorDisparosHeroe.add(miGrafico);
    }

    
    /** Esta función selecciona un tipo de enemigo al azar */
    private int leeTipoEnemigoAzar() {
    	int tipo;
    	int rand;
    	
    	switch (nivelActual) {
    		case 2:
    			rand = Math.abs(aleatorio.nextInt() % 6);
    			break;
    		case 3:
    			rand = Math.abs(aleatorio.nextInt() % 6);
    			break;
    		case 1:
    		default:
    			rand = Math.abs(aleatorio.nextInt() % 3);
    			break;
    	}

        // Elegimos el tipo de enemigo de forma aleatoria
        switch (rand) {
        	case 1:
    			tipo = GRAFICO_ENEMIGO_02;
    			break;
        	case 2:
    			tipo = GRAFICO_ENEMIGO_03;
    			break;
        	case 3:
    			tipo = GRAFICO_ENEMIGO_04;
    			break;
        	case 4:
    			tipo = GRAFICO_ENEMIGO_05;
    			break;
        	case 5:
    			tipo = GRAFICO_ENEMIGO_06;
    			break;
       		/*
           	case 6:
       			tipo = GRAFICO_ROCA;
       			break;
       		*/
        	case 0:
    		default:
    			tipo = GRAFICO_ENEMIGO_01;
    			break;
        }

        return tipo;
    }
    

    /** Esta función genera datos aleatorios para poner un enemigo en el juego */
    private void creaEnemigoAzar(int tipo) {
    	// Variables auxiliares para almacenar los datos necesarios para generar el enemigo
    	int miX, miY, despX, despY;

        // Seleccionamos el tipo de gráfico si debe ser aleatorio
        if (tipo == GRAFICO_ALEATORIO) tipo = leeTipoEnemigoAzar();
    	
        // Indicamos las coordenadas iniciales
        miX = (int) Math.abs(aleatorio.nextInt() % getWidth());
        miY = 10;
        despX = 0;
        despY = 1;

        // Según el tipo de objeto gráfico indicamos un desplazamiento inicial
        switch (tipo) {
        	case GRAFICO_ENEMIGO_01:
        		// Indicamos el desplazamiento inicial
        		despX = 0;
        		despY = 3;
        		break;
        	case GRAFICO_ENEMIGO_02:
        		// Indicamos el desplazamiento inicial
        		despX = 3;
        		despY = 3;
        		break;
        	case GRAFICO_ENEMIGO_03:
        		// Indicamos el desplazamiento inicial
        		despX = 4;
        		despY = 1;
        		break;
        	case GRAFICO_ENEMIGO_04:
        		// Indicamos el desplazamiento inicial
        		despX = 1;
        		despY = 6;
        		break;
        	case GRAFICO_ENEMIGO_05:
        		// Indicamos el desplazamiento inicial
        		despX = 4;
        		despY = 1;
        		break;
        	case GRAFICO_ENEMIGO_06:
        		// Indicamos el desplazamiento inicial
        		despX = 4;
        		despY = 1;
        		break;
        	case GRAFICO_ROCA:
        		// Indicamos el desplazamiento inicial
        		despX = aleatorio.nextInt() % 2;
        		despY = Math.abs(aleatorio.nextInt() % 4);
        		break;
        }
        
        // Evitamos que el objeto se quede quieto verticalmente
        if (despY < 1) despY = 1;

        // Creamos el enemigo con los datos generados
        creaEnemigo(tipo, miX, miY, despX, despY);
    }
    
    
    /** Esta función genera un nuevo enemigo en el juego */
    private void creaEnemigo(int tipo, int miX, int miY, int miDespX, int miDespY) {
    	// Generamos una variable auxiliar de objeto gráfico
    	Grafico miGrafico = null;

        switch (tipo) {
        	case GRAFICO_ROCA:
        		//miGrafico = new Grafico(miCacheGraficos.get(R.drawable.roca_animacion), 53, 51, 8);
        		miGrafico = new Grafico(miCacheGraficos.get(R.drawable.roca1));
    			miGrafico.ponTipo(GRAFICO_ROCA);
    			miGrafico.ponVidaObjeto(50000);
    			break;
            case GRAFICO_ENEMIGO_01:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.enemigo1_40));
        		miGrafico.ponTipo(GRAFICO_ENEMIGO_01);
                break;
            case GRAFICO_ENEMIGO_02:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.enemigo2_40_animacion), 33, 40, 2);
        		miGrafico.ponTipo(GRAFICO_ENEMIGO_02);
        		miGrafico.ponVidaObjeto(20);
                break;
            case GRAFICO_ENEMIGO_03:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.enemigo3_40));
        		miGrafico.ponTipo(GRAFICO_ENEMIGO_03);
        		miGrafico.ponDebeDisparar(true);
                break;
            case GRAFICO_ENEMIGO_04:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.enemigo4_40));
        		miGrafico.ponTipo(GRAFICO_ENEMIGO_04);
                break;
            case GRAFICO_ENEMIGO_05:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.enemigo5_26_animacion), 57, 26, 2);
        		miGrafico.ponTipo(GRAFICO_ENEMIGO_05);
        		miGrafico.ponVidaObjeto(20);
        		miGrafico.ponDebeDisparar(true);
                break;
            case GRAFICO_ENEMIGO_06:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.enemigo6_animacion), 28, 28, 16);
        		miGrafico.ponTipo(GRAFICO_ENEMIGO_06);
                break;
            case GRAFICO_SUPERENEMIGO_01:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.superenemigo_1_animacion), 98, 86, 2);
        		miGrafico.ponTipo(GRAFICO_SUPERENEMIGO_01);
    			miGrafico.ponVidaObjeto(500);
    			// Indica que es el enemigo final de nivel
    			miGrafico.ponEstadoObjeto(0);
    			// Activamos enemigos aleatorios mientras "vive" el enemigo final
    			enemigosAleatorios = 70;
                break;
            case GRAFICO_SUPERENEMIGO_01b:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.superenemigo_1_animacion), 98, 86, 2);
        		miGrafico.ponTipo(GRAFICO_SUPERENEMIGO_01);
    			miGrafico.ponVidaObjeto(500);
    			// Indica que no es el enemigo final de nivel
    			miGrafico.ponEstadoObjeto(1);
                break;
            case GRAFICO_SUPERENEMIGO_02:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.superenemigo_2_animacion), 107, 86, 2);
        		miGrafico.ponTipo(GRAFICO_SUPERENEMIGO_02);
    			miGrafico.ponVidaObjeto(600);
    			// Indica que es el enemigo final de nivel
    			miGrafico.ponEstadoObjeto(0);
    			// Activamos enemigos aleatorios mientras "vive" el enemigo final
    			enemigosAleatorios = 70;
                break;
            case GRAFICO_SUPERENEMIGO_02b:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.superenemigo_2_animacion), 107, 86, 2);
        		miGrafico.ponTipo(GRAFICO_SUPERENEMIGO_02);
    			miGrafico.ponVidaObjeto(600);
    			// Indica que no es el enemigo final de nivel
    			miGrafico.ponEstadoObjeto(1);
                break;
            case GRAFICO_SUPERENEMIGO_03:
            	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.superenemigo_3_animacion), 107, 86, 2);
        		miGrafico.ponTipo(GRAFICO_SUPERENEMIGO_03);
    			miGrafico.ponVidaObjeto(800);
    			// Indica que es el enemigo final de nivel
    			miGrafico.ponEstadoObjeto(0);
    			// Activamos enemigos aleatorios mientras "vive" el enemigo final
    			enemigosAleatorios = 70;
                break;
        }

        // Indicamos las coordenadas iniciales
        miGrafico.ponX(miX);
        miGrafico.ponY(miY);
    	// Indicamos el desplazamiento inicial
        miGrafico.ponDespX(miDespX);
        miGrafico.ponDespY(miDespY);

        // Añadimos el gráfico al vector de enemigos
        vectorEnemigos.add(miGrafico);
    }

    
    /** Esta función genera una nueva ayuda en el juego */
    private void creaCapsula(int tipo) {
    	// Generamos una variable auxiliar de objeto gráfico
    	Grafico miGrafico = null;

        // Creamos el objeto gráfico
		miGrafico = new Grafico(miCacheGraficos.get(R.drawable.capsula_animacion), 44, 11, 5);
		miGrafico.ponTipo(GRAFICO_CAPSULA);
        miGrafico.ponVidaObjeto(1);
        miGrafico.ponEstadoObjeto(tipo);
		// Indicamos el desplazamiento inicial
        miGrafico.ponDespX(2);
        miGrafico.ponDespY(0);
        // Indicamos las coordenadas iniciales
        miGrafico.ponX(-miGrafico.leeAnchoSprite());
        miGrafico.ponY(60);

        // Añadimos el gráfico al vector de enemigos
        vectorEnemigos.add(miGrafico);
    }


    /** Esta función genera una nueva ayuda en el juego */
    private void creaAyuda(int tipo, int miX, int miY) {
    	// Generamos una variable auxiliar de objeto gráfico
    	Grafico miGrafico = null;

        // Creamos el objeto gráfico
    	switch (tipo) {
    		case GRAFICO_DIAMANTE_GRIS:
    	    	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.diamantes_animacion), 19, 15, 3);
    			miGrafico.ponRectangulo(0,0,19,15);
    			miGrafico.ponTipo(GRAFICO_DIAMANTE_GRIS);
    			miGrafico.ponVidaObjeto(50);
    			break;
    		case GRAFICO_DIAMANTE_VERDE:
    	    	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.diamantes_animacion), 19, 15, 3);
    			miGrafico.ponRectangulo(0,15,19,15);
    			miGrafico.ponTipo(GRAFICO_DIAMANTE_VERDE);
    			miGrafico.ponVidaObjeto(150);
    			break;
    		case GRAFICO_DIAMANTE_ROJO:
    	    	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.diamantes_animacion), 19, 15, 3);
    			miGrafico.ponRectangulo(0,30,19,15);
    			miGrafico.ponTipo(GRAFICO_DIAMANTE_ROJO);
    			miGrafico.ponVidaObjeto(500);
    			break;
    		case GRAFICO_BOTIQUIN:
    	    	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.botiquin_animacion), 14, 13, 6);
    			miGrafico.ponTipo(GRAFICO_BOTIQUIN);
    			break;
    		case GRAFICO_BOMBA:
    	    	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.bombas_animacion), 23, 11, 7);
    			miGrafico.ponTipo(GRAFICO_BOMBA);
    			break;
    	}
		// Indicamos el desplazamiento inicial
        miGrafico.ponDespX(0);
        miGrafico.ponDespY(1);
        // Indicamos las coordenadas iniciales
        miGrafico.ponX(miX - (miGrafico.leeAnchoSprite()/2));
        // DEBUG miGrafico.ponX(150);
        miGrafico.ponY(miY - (miGrafico.leeAltoSprite()/2));

        // Añadimos el gráfico al vector de ayudas
        vectorAyudas.add(miGrafico);
    }

    private void creaEfecto(int tipo) {
    	// Generamos una variable auxiliar de objeto gráfico
    	Grafico miGrafico = null;

    	switch (tipo) {
    		case GRAFICO_RADAR:
    	    	// Emitimos el sonido del radar
    	    	if (usoSonido) miSoundPool.play(sonidoRadar, 1, 1, 0, 0, 1);
    	    	
    	    	miGrafico = new Grafico(miCacheGraficos.get(R.drawable.radar_00_animacion), 83, 83, 2);
    			miGrafico.ponTipo(GRAFICO_RADAR);
    	    	// Ponemos las coordenadas del radar
    			miGrafico.ponX(0);
    			miGrafico.ponY(getHeight() - 83);
    			break;
    	}

    	// Indicamos que está en el primer paso como brillo
		miGrafico.ponTiempoCreado(0);
		// Indicamos el desplazamiento inicial
        miGrafico.ponDespX(0);
        miGrafico.ponDespY(0);

        // Añadimos el gráfico al vector de efectos
        vectorEfectos.add(miGrafico);
    }
    
    private void restaVidaEnemigo(Grafico miGrafico, int modificaVida, boolean impactoExplosivo) {
		// Restamos vida al objeto
		miGrafico.modificaVidaObjeto(modificaVida);

    	// Si no le queda vida
		if (miGrafico.leeVidaObjeto() < 1) {
			if (miGrafico.leeTipo() == GRAFICO_CAPSULA) {
				if (miGrafico.leeEstadoObjeto() > 0) {
                	// Emitimos el sonido de la Capsula
                	if (usoSonido) miSoundPool.play(sonidoCapsula, 1, 1, 0, 0, 1);
                	// Generamos una ayuda
                	switch (miGrafico.leeEstadoObjeto()) {
                		case GRAFICO_BOMBA:
                        	creaAyuda(GRAFICO_BOMBA, miGrafico.leeX()+(miGrafico.leeAnchoSprite()/2), miGrafico.leeY()+(miGrafico.leeAltoSprite()/2));
                        	break;
                		case GRAFICO_BOTIQUIN:
                        	creaAyuda(GRAFICO_BOTIQUIN, miGrafico.leeX()+(miGrafico.leeAnchoSprite()/2), miGrafico.leeY()+(miGrafico.leeAltoSprite()/2));
                        	break;
                	}
					// Cambiamos el estado
                	miGrafico.ponEstadoObjeto(-1);
                	// Indicamos que está en el primer paso del segundo estado 
                	miGrafico.ponTiempoCreado(0);
				}
			} else if ((miGrafico.leeTipo() == GRAFICO_SUPERENEMIGO_01) 
					|| (miGrafico.leeTipo() == GRAFICO_SUPERENEMIGO_02)
					|| (miGrafico.leeTipo() == GRAFICO_SUPERENEMIGO_03)
					) {
				// Tomamos nota del enemigo eliminado
				estadEnemigosEliminados++;
    			// Sumamos puntos
				puntosJuego=puntosJuego+500;

            	// Marcamos que ahora es una explosión
            	miGrafico.ponTipo(GRAFICO_EXPLOSION);
            	// Indicamos que está en el primer paso de la explosión 
            	miGrafico.ponTiempoCreado(0);
            	// Indicamos el dibujo que corresponde al objeto
            	miGrafico.ponDibujo(miCacheGraficos.get(R.drawable.explosion_animacion), 32, 32, 7);
				// Quitamos el gráfico del vector de enemigos y lo pasamos a efectos
            	//vectorEnemigos.remove(miGrafico);			
            	//vectorEfectos.add(miGrafico);			

				// Si el estado es 0 significa que el superenemigo es el ultimo del nivel
				// y tenemos que finalizar el nivel
				if (miGrafico.leeEstadoObjeto() == 0) {
					// Desactivamos los enemigos aleatorios
					enemigosAleatorios = 0;
			    	// Emitimos el sonido de una bomba
			    	if (usoSonido) miSoundPool.play(sonidoBomba, 1, 1, 0, 0, 1);
			    	// Vibramos
					((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(800);
	                // Nos aseguramos que no queda ningún enemigo
					vectorEnemigos.clear();
	                vectorDisparosEnemigo.clear();

	                // Lanzamos animacion fin de nivel
					tiempoInvisible = cicloJuego +1000;
					creaAnimacionHeroe();
				}
			} else { // No es ni capsula ni superenemigo
				// Tomamos nota del enemigo eliminado
				estadEnemigosEliminados++;
    			// Sumamos puntos
				puntosJuego=puntosJuego+10;        			
            	// Emitimos el sonido de la explosión
            	if (usoSonido) miSoundPool.play(sonidoExplosion, 0.7f, 0.7f, 0, 0, 1);
			}
		} else { // El gráfico todavía tiene vida
			if (miGrafico.leeTipo() == GRAFICO_ENEMIGO_02) miGrafico.ponRectangulo(0,40,33,40);
			else if (miGrafico.leeTipo() == GRAFICO_ENEMIGO_05) miGrafico.ponRectangulo(0,26,57,26);
			else if ((miGrafico.leeTipo() == GRAFICO_SUPERENEMIGO_01) && ( 
					((miGrafico.leeVidaObjeto() - modificaVida >= 250) && (miGrafico.leeVidaObjeto() < 250))
					|| ((miGrafico.leeVidaObjeto() - modificaVida >= 125) && (miGrafico.leeVidaObjeto() < 125))
					|| ((miGrafico.leeVidaObjeto() - modificaVida >= 60) && (miGrafico.leeVidaObjeto() < 60)))
				) {
				miGrafico.ponRectangulo(0,86,98,86);
				creaAyuda(GRAFICO_DIAMANTE_GRIS, miGrafico.leeX() + 49, miGrafico.leeY() + 86);
			} else if ((miGrafico.leeTipo() == GRAFICO_SUPERENEMIGO_02) && ( 
					  ((miGrafico.leeVidaObjeto() - modificaVida >= 300) && (miGrafico.leeVidaObjeto() < 300))
					  || ((miGrafico.leeVidaObjeto() - modificaVida >= 150) && (miGrafico.leeVidaObjeto() < 150))
					  || ((miGrafico.leeVidaObjeto() - modificaVida >= 60) && (miGrafico.leeVidaObjeto() < 60)))
				) {
				miGrafico.ponRectangulo(0,86,107,86);
				creaAyuda(GRAFICO_DIAMANTE_ROJO, miGrafico.leeX() + 49, miGrafico.leeY() + 86);
			} else if ((miGrafico.leeTipo() == GRAFICO_SUPERENEMIGO_03) && ( 
					  ((miGrafico.leeVidaObjeto() - modificaVida >= 400) && (miGrafico.leeVidaObjeto() < 400))
					  || ((miGrafico.leeVidaObjeto() - modificaVida >= 200) && (miGrafico.leeVidaObjeto() < 200))
					  || ((miGrafico.leeVidaObjeto() - modificaVida >= 60) && (miGrafico.leeVidaObjeto() < 60)))
				) {
				miGrafico.ponRectangulo(0,86,107,86);
				creaAyuda(GRAFICO_DIAMANTE_VERDE, miGrafico.leeX() + 49, miGrafico.leeY() + 86);
			}
			
			// Los disparos causan explosiones en la nave enemiga aunque no la destruyan
			//+ pero la colisión con el Héroe no (habitualmente hay unas 10 colisiones por cada "contacto"
			//+ entre el Héroe y otro objeto).
			if (impactoExplosivo) {
	        	// Emitimos el sonido de la explosión
            	if (usoSonido) miSoundPool.play(sonidoExplosion, 0.7f, 0.7f, 0, 0, 1);
				// Generamos una nueva explosión
				Grafico nuevaExplosion = null;
				nuevaExplosion = new Grafico(miCacheGraficos.get(R.drawable.explosion_animacion), 32, 32, 7);
				nuevaExplosion.ponTipo(GRAFICO_EXPLOSION);
		        // Indicamos el desplazamiento inicial
				nuevaExplosion.ponDespX(0);
				nuevaExplosion.ponDespY(0);
		        // Indicamos las coordenadas iniciales
				nuevaExplosion.ponX(miGrafico.leeX() + (miGrafico.leeAnchoSprite() / 2) - 16);
				nuevaExplosion.ponY(miGrafico.leeY() - 1);
		        // Añadimos el gráfico al vector de explosiones
		        vectorEfectos.add(nuevaExplosion);
			}
		}

    }

    //************************************************************************
    //       COLISIONES
    //************************************************************************
    /**
     * Comprueba si existen colisiones entre los objetos gráficos
     *   y si procede los pasa al vector de efectos gráficos 
     */
    public void compruebaColisiones() {
        if (modoJuego == MODO_EN_MARCHA) {
        	// Generamos un vector auxiliar con los objetos gráficos
        	//   que deben salir de su vector
            ArrayList<Grafico> objetosFuera = new ArrayList<Grafico>();
            ArrayList<Grafico> disparosFuera = new ArrayList<Grafico>();
            
            // Para cada objeto gráfico del vector de enemigos
            for (Grafico miGrafico : vectorEnemigos) {
                // Verificamos colisiones con los disparos del heroe
                for (Grafico miDisparo : vectorDisparosHeroe) {
                	if (compruebaColision(miDisparo, miGrafico)){
        				// Eliminamos el disparo del vector
                		disparosFuera.add(miDisparo);
            			// Restamos vida al objeto
                		restaVidaEnemigo(miGrafico, -15, true);
                	}
            	} // Verificamos colisiones con los disparos del heroe

                // Si hay algún disparo que eliminar
                if (!disparosFuera.isEmpty()) {
                	// Lo(s) sacamos del vector de enemigos
                	vectorDisparosHeroe.removeAll(disparosFuera);
                    // Limpiamos el vector
                	disparosFuera.clear();
                }
                
                // Verificamos colisiones con el heroe
            	if ((miGrafico.leeVidaObjeto() > 0) && (compruebaColision(miHeroe, miGrafico))) {
            		((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);
            		if (miGrafico.leeTipo() == GRAFICO_ROCA) {
            			// Restamos vida al heroe
            			if (tiempoInvulnerable < cicloJuego) miHeroe.modificaVidaObjeto(-1);
            		} else {
            			// Restamos vida al heroe
            			if (tiempoInvulnerable < cicloJuego) miHeroe.modificaVidaObjeto(-1);
            			// Restamos vida al objeto
                		restaVidaEnemigo(miGrafico, -1, false);
            		}
            		
            		if (miHeroe.leeVidaObjeto() < 1){
            			muerteHeroe();
            		}
            	} // Verificamos colisiones con el heroe

            	// Si no le queda vida al objeto del vector de enemigos
        		if (miGrafico.leeVidaObjeto() < 1) {
                	// Marcamos que ahora es una explosión
                	miGrafico.ponTipo(GRAFICO_EXPLOSION);
                	// Indicamos que está en el primer paso de la explosión 
                	miGrafico.ponTiempoCreado(0);
                	// Indicamos el dibujo que corresponde al objeto
                	miGrafico.ponDibujo(miCacheGraficos.get(R.drawable.explosion_animacion), 32, 32, 7);
    				// Ponemos el gráfico en el vector objetosFuera
    				objetosFuera.add(miGrafico);
        		}
            	
            } // for (Grafico miGrafico : vectorEnemigos) {


            // Si hay algún objeto que pasar a efectos gráficos
            if (!objetosFuera.isEmpty()) {
            	// Lo(s) añadimos al vector de efectos gráficos
            	vectorEfectos.addAll(objetosFuera);
            	// Lo(s) sacamos del vector de enemigos
                vectorEnemigos.removeAll(objetosFuera);
                // Limpiamos el vector
                objetosFuera.clear();
            }

            // Si el heroe está visible, comprobamos colisiones con las ayudas
            if (tiempoInvisible < cicloJuego)
           	// Para cada objeto gráfico del vector de ayudas
            for (Grafico miGrafico : vectorAyudas) {
            	// Verificamos colisiones con el heroe
            	if (compruebaColision(miHeroe, miGrafico)){
            		//((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);

            		switch (miGrafico.leeTipo()) {
            			case GRAFICO_BOTIQUIN:
                    		miHeroe.modificaVidaObjeto(10);
                    		if (miHeroe.leeVidaObjeto() > VIDA_MAXIMA) {
            					miHeroe.ponVidaObjeto(VIDA_MAXIMA);
            					puntosJuego = puntosJuego + 10;
            				}
            				break;
            			case GRAFICO_BOMBA:
                			if (numeroBombas < BOMBAS_MAXIMAS) {
                        		numeroBombas++;
                			} else {
            					puntosJuego = puntosJuego + 10;
                			}
            				break;
            			case GRAFICO_DIAMANTE_GRIS:
            			case GRAFICO_DIAMANTE_VERDE:
            			case GRAFICO_DIAMANTE_ROJO:
                    		creditosJuego=creditosJuego+miGrafico.leeVidaObjeto();
            				break;
            		}

                	// Emitimos el sonido de la ayuda
                	if (usoSonido) miSoundPool.play(sonidoAyuda, 1, 1, 0, 0, 1);
            		// Marcamos que ahora es un brillo
            		miGrafico.ponTipo(GRAFICO_BRILLO);
                	// Indicamos que está en el primer paso como brillo
            		miGrafico.ponTiempoCreado(0);
                	// Movemos el brillo encima del heroe
            		miGrafico.ponX(miHeroe.leeX()-5);
            		miGrafico.ponY(miHeroe.leeY());
                	// Indicamos el dibujo que corresponde al objeto
            		miGrafico.ponDibujo(miCacheGraficos.get(R.drawable.brillo_animacion), 39, 39, 5);
                	// Ponemos el gráfico en el vector objetosFuera 
        			objetosFuera.add(miGrafico);
            	}
            } // for (Grafico miGrafico : vectorAyudas)
                    
            // Si hay algún objeto que pasar a efectos gráficos
            if (!objetosFuera.isEmpty()) {
            	// Lo(s) añadimos al vector de efectos gráficos
            	vectorEfectos.addAll(objetosFuera);
            	// Lo(s) sacamos del vector de ayudas
            	vectorAyudas.removeAll(objetosFuera);
                // Limpiamos el vector
                objetosFuera.clear();
            }

            // Para cada objeto gráfico del vector de disparos enemigos
            for (Grafico miGrafico : vectorDisparosEnemigo) {
            	// Verificamos colisiones con el heroe
    			if ((tiempoInvulnerable < cicloJuego) && (compruebaColision(miHeroe, miGrafico))) {
                	// Emitimos el sonido de la explosión
                	if (usoSonido) miSoundPool.play(sonidoExplosion, 0.7f, 0.7f, 0, 0, 1);
                	// Marcamos que ahora es una explosión
                	miGrafico.ponTipo(GRAFICO_EXPLOSION);
                	// Indicamos que está en el primer paso de la explosión 
                	miGrafico.ponTiempoCreado(0);
                	// Indicamos el dibujo que corresponde al objeto
                	miGrafico.ponDibujo(miCacheGraficos.get(R.drawable.explosion_animacion), 32, 32, 7);
    				// Ponemos el gráfico en el vector objetosFuera
    				objetosFuera.add(miGrafico);

        			// Restamos vida al heroe
            		((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);
            		miHeroe.modificaVidaObjeto(-miGrafico.leeVidaObjeto());

            		if (miHeroe.leeVidaObjeto() < 1) {
            			muerteHeroe();
            		}
    				
            	}
            } // for (Grafico miGrafico : vectorDisparosEnemigo)

            // Si hay algún objeto que pasar a efectos gráficos
            if (!objetosFuera.isEmpty()) {
            	// Lo(s) añadimos al vector de efectos gráficos
            	vectorEfectos.addAll(objetosFuera);
            	// Lo(s) sacamos del vector de ayudas
            	vectorDisparosEnemigo.removeAll(objetosFuera);
                // Limpiamos el vector
                objetosFuera.clear();
            }

        } // if (modoJuego == MODO_EN_MARCHA)
    }


    private void muerteHeroe() {
    	// Generamos una vibración larga
		((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1200);
		// Generamos una nueva explosión
		Grafico nuevaExplosion = null;
		nuevaExplosion = new Grafico(miCacheGraficos.get(R.drawable.explosion_animacion), 32, 32, 7);
		nuevaExplosion.ponTipo(GRAFICO_EXPLOSION);
        // Indicamos el desplazamiento inicial de la explosion
		nuevaExplosion.ponDespX(0);
		nuevaExplosion.ponDespY(0);
        // Indicamos las coordenadas iniciales de la explosion
		nuevaExplosion.ponX(miHeroe.leeX() + 4);
		nuevaExplosion.ponY(miHeroe.leeY() - 1);
        // Añadimos el gráfico al vector de explosiones
        vectorEfectos.add(nuevaExplosion);

        // Comprobamos si nos quedan naves de repuesto
		navesRepuesto--;
		if (navesRepuesto < 0) {
			// Terminamos la partida
			terminaPartida();
		} else {
			// Ponemos la nueva nave en juego
			miHeroe.ponVidaObjeto(VIDA_INICIAL);
			tiempoInvulnerable = cicloJuego + TIEMPO_INVULNERABLE;
			tiempoInvisible = cicloJuego + TIEMPO_INVISIBLE;
	        miHeroe.ponX(150);
	        miHeroe.ponY(410);
	        miHeroe.ponDespX(0);
	        miHeroe.ponDespY(0);
		}
    }

    private void terminaPartida() {
    	// Podemos terminar porque muere el Heroe o porque finalizan los niveles
    	if (accionActual == -1) { // Ha finalizado el juego
    		// Sumamos puntos por el número de créditos restante
    		puntosJuego = puntosJuego + (creditosJuego / 2);
    		creditosJuego = 0;
    	} else { // Ha muerto
    		// A veces llega a -1
    		miHeroe.ponVidaObjeto(0);
    		// Sumamos puntos por el número de créditos restante (menos que si ha terminado)
    		puntosJuego = puntosJuego + (creditosJuego / 3);
    		creditosJuego = 0;
    	}
		// Terminamos el juego
		modoJuego = MODO_FIN;
		guardaPuntuacion();

    }

    private void guardaPuntuacion() {
    	// Evitamos que se guarde dos veces seguidas la puntuación
    	if (puntuacionGuardada) return;
    	
    	SQLiteGestor miSQLiteGestor = new SQLiteGestor(getContext());
    	miSQLiteGestor.insertaPuntuacion(versionAplicacion, android_id, String.valueOf(nivelActual), String.valueOf(puntosJuego), nombrePiloto);
    	miSQLiteGestor.cierraOH();
    	puntuacionGuardada = true;
    }


	/**
     * Función que calcula si dos objetos gráficos entran en colisión
     * 
     * @param primero - Primer objeto gráfico
     * @param segundo - Segundo objeto gráfico
     * @return Devuelve cierto (true) si hay colisión
     */
    private boolean compruebaColision(Grafico primero, Grafico segundo) {
    	// Generamos una variable auxiliar que indica si hay colisión
    	//   En principio no hay colisión
        boolean hayColision = false;
        
        // Tomamos información sobre la posición y tamaño de los objetos gráficos
        //----------------------------------------------------------------------
        // Tomamos los rangos de X e Y que ocupa el primer objeto
        int inicioRangoXPrimero = primero.leeX();
        int finalRangoXPrimero = inicioRangoXPrimero + primero.leeAnchoSprite();
        int inicioRangoYPrimero = primero.leeY();
        int finalRangoYPrimero = inicioRangoYPrimero + primero.leeAltoSprite();

        // Tomamos los rangos de X e Y que ocupa el segundo objeto        
        int inicioRangoXSegundo = segundo.leeX();
        int finalRangoXSegundo = inicioRangoXSegundo + segundo.leeAnchoSprite();
        int inicioRangoYSegundo = segundo.leeY();
        int finalRangoYSegundo = inicioRangoYSegundo + segundo.leeAltoSprite();
        
		// Hay colisión si el inicio del rango de uno de los gráficos está dentro del rango del otro gráfico
        if ((inicioRangoXPrimero >= inicioRangoXSegundo && inicioRangoXPrimero <= finalRangoXSegundo)
			|| (inicioRangoXSegundo >= inicioRangoXPrimero && inicioRangoXSegundo <= finalRangoXPrimero)) {
	        if ((inicioRangoYPrimero >= inicioRangoYSegundo && inicioRangoYPrimero <= finalRangoYSegundo)
				|| (inicioRangoYSegundo >= inicioRangoYPrimero && inicioRangoYSegundo <= finalRangoYPrimero)) {
            	hayColision = true;
            }
        }
        
        // Devolvemos la variable que indica si hay colisión
        return hayColision;
    }
    


    //************************************************************************
    //       ON DRAW
    //************************************************************************
    /**
     * Sobreescribimos la función onDraw para dibujar nuestra pantalla (SurfaceView).
     * 
     * Orden (el enemigo y los enemigos quedan encima de los marcadores):
     * - Fondo
     * - Marcadores (textos): puntuación, ciclo de juego...
     * - Barra de vida
     * - Objetos gráficos de vectorEnemigos
     * - Objetos gráficos de vectorEfectos
     * - Heroe
     * - Botón bomba
     * - Pausa
     * 
     */
    @Override
    public void onDraw(Canvas miLienzo) {hiloDibuja(miLienzo);}
    // Las funciones de respuesta a señales como onDraw onXxxx no deben ser llamadas directamente
    // Por eso desde onDraw soolo llamo a una función, que es la misma que puedo llamar desde el hilo
    public void hiloDibuja(Canvas miLienzo) {
        // Variable auxiliar para generar cadenas de texto
        String miString;

        // Calculamos FPS
        //-----------------------------------------------------
        // Variable auxiliar de tiempo (para FPS)
        long ahora = System.currentTimeMillis();
        if (momentoAnterior != 0) {
        	marcosIntervalo++;
        	marcosTotales++;
        	//Cada 10 marcos
        	if (marcosIntervalo == 10) {
        		miFPS = (int) (1000 / (ahora - momentoAnterior));
        		miFPSGral = (int) ((marcosTotales*1000) / (ahora - momentoInicial));
        		marcosIntervalo = 0;
        	}
        } else {
        	momentoInicial = ahora;
        }
        momentoAnterior = ahora;

        // FONDO
        //-----------------------------------------------------
    	// Dibujamos el fondo en el lienzo
		miLienzo.drawColor(Color.BLACK);
        //miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.fondo_0), 0, 0, null);
        // Dibujamos cada estrella en el lienzo
        for (Grafico miGrafico : vectorEstrellas) {
        	miGrafico.dibuja(miLienzo);
        }
        
        // NAVES REPUESTO
        //-----------------------------------------------------
    	// Dibujamos los iconos de naves de repuesto
        if (navesRepuesto > 0) {
            miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.heroe_17), getWidth() - 16, 1, null);
            if (navesRepuesto > 1) {
                miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.heroe_17), getWidth() - 30, 1, null);
                if (navesRepuesto > 2) {
                    miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.heroe_17), getWidth() - 44, 1, null);
                    if (navesRepuesto > 3) {
                        miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.heroe_17), getWidth() - 60, 1, null);
                    }
                }
            }
        }
        
        // BARRA VIDA
        //-----------------------------------------------------
        if (miHeroe.leeVidaObjeto() > VIDA_INICIAL) miPaint.setARGB(255, 0, 255, 0);
        else if (miHeroe.leeVidaObjeto() > (VIDA_INICIAL * 3 / 4)) miPaint.setARGB(255, 120, 180, 0);
        else if (miHeroe.leeVidaObjeto() > (VIDA_INICIAL / 2)) miPaint.setARGB(255, 180, 160, 0);
        else if (miHeroe.leeVidaObjeto() < 0) miPaint.setARGB(255, 255, 0, 0);
        else miPaint.setARGB(255, 250, 80, 0);
        miRectF.set(110,
        		2,
                110 + (BARRA_ANCHO * miHeroe.leeVidaObjeto() / VIDA_INICIAL),
                2 + BARRA_ALTO);
        miLienzo.drawRect(miRectF, miPaint);

        // Si la vida es mayor que la VIDA_INICIAL sobredibujamos otra barra con el color "normal"
        if (miHeroe.leeVidaObjeto() > VIDA_INICIAL) {
        	miPaint.setARGB(255, 120, 180, 0);
            miRectF.set(110,
            		2,
            		110 + BARRA_ANCHO,
                    2 + BARRA_ALTO);
            miLienzo.drawRect(miRectF, miPaint);
        }

        // TEXTOS
        //-----------------------------------------------------
        // Configuramos el pincel
        miPaint.setColor(Color.WHITE);
        miPaint.setTextAlign(Paint.Align.LEFT);

        // Ponemos la vida del Heroe
        miPaint.setTextSize(8);
        miString = String.valueOf(miHeroe.leeVidaObjeto());
    	miLienzo.drawText(miString, 116, 10, miPaint);

    	// Ponemos los puntos del Juego
    	miPaint.setTextSize(10);
    	miString = (String)misRecursos.getText(R.string.juego_puntuacion)+" "+String.valueOf(puntosJuego);
    	miLienzo.drawText(miString, 2, 10, miPaint);
    	// Ponemos el nivel del Juego
    	miString = (String)misRecursos.getText(R.string.juego_nivel)+" "+String.valueOf(nivelActual);
    	miLienzo.drawText(miString, 2, 20, miPaint);
    	// Ponemos los créditos del jugador
    	miString = (String)misRecursos.getText(R.string.juego_creditos)+": "+String.valueOf(creditosJuego);
    	miLienzo.drawText(miString, 2, 30, miPaint);

    	// DEBUG Ponemos el modo y ciclo de juego
    	if (DEBUG) {
        	miPaint.setTextSize(10);
        	// Datos sobre ciclo
        	//miString = "DEBUG ["+String.valueOf(vectorAccionActual[0]/10)+"|"+String.valueOf(vectorAccionActual[1])+"|"+String.valueOf(vectorAccionActual[2])+"] ["+String.valueOf(modoJuego)+"] "+String.valueOf(cicloJuego/10);
        	// FPS
        	miString = "FPS: "+String.valueOf(miFPS)+" FPS Gral: "+String.valueOf(miFPSGral)+"["+String.valueOf(modoJuego)+"]";
        	miLienzo.drawText(miString, 10, getHeight() -10, miPaint);
    	}
    	
        // ENEMIGOS
        //-----------------------------------------------------
        // Dibujamos cada enemigo en el lienzo
        for (Grafico miGrafico : vectorEnemigos) {
        	miGrafico.dibuja(miLienzo);
        }
        
        // DISPAROS HEROE
        //-----------------------------------------------------
        // Dibujamos cada disparo del heroe en el lienzo
        for (Grafico miGrafico : vectorDisparosHeroe) {
        	miGrafico.dibuja(miLienzo);
        }
        
        // DISPAROS ENEMIGOS
        //-----------------------------------------------------
        // Dibujamos cada disparo del heroe en el lienzo
        for (Grafico miGrafico : vectorDisparosEnemigo) {
        	miGrafico.dibuja(miLienzo);
        }
        
        // AYUDAS
        //-----------------------------------------------------
        // Dibujamos cada ayuda en el lienzo
        for (Grafico miGrafico : vectorAyudas) {
        	miGrafico.dibuja(miLienzo);
        }
        
        // HEROE Y ESCUDOS
        //-----------------------------------------------------
        // Dibujamos al heroe y los escudos 
		if ((tiempoInvisible < cicloJuego) && (miHeroe.leeVidaObjeto() > 0)) {
	        // Si la nave es vulnerable dibujamos al héroe
			if (tiempoInvulnerable < cicloJuego) {
				// Dibujamos al héroe
		    	miHeroe.dibuja(miLienzo);
			} else { // Si es invulnerable...
				// ...dibujamos un escudo permanente en la barra de vida
	            miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.escudo), 150, 1, null);
	            // ...y de manera intermitente dibujamos al héroe con un anillo naranja
				if (escudoVisible) {
					// Dibujamos al héroe
			    	miHeroe.dibuja(miLienzo);
					// Un escudo encima del heroe
		            miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.anillo_naranja), miHeroe.leeX()-2, miHeroe.leeY()-2, null);
				}
	        }
		}


        // EFECTOS GRÁFICOS
        //-----------------------------------------------------
        for (Grafico miGrafico : vectorEfectos) {
        	// Dibujamos el objeto gráfico
        	miGrafico.dibuja(miLienzo);
        }

        // BOTON BOMBA
        //-----------------------------------------------------
        // Botón para disparar bombas
        if (numeroBombas > 0) {
       		miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.punto_de_mira_00), getWidth() - 55, getHeight() - 55, null);
        	miPaint.setTextSize(10);
        	miString = String.valueOf(numeroBombas);
        	miLienzo.drawText(miString, getWidth() - 16, getHeight() - 10, miPaint);
        }

        // MODOS ESPECÍFICOS [PAUSA, FIN, CAMBIO_NIVEL]
        //-----------------------------------------------------
       	if (modoJuego == MODO_PAUSA) {
       		miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.fondo_trans), 0, 0, null);

            miPaint.setColor(Color.WHITE);
            miPaint.setTextAlign(Paint.Align.CENTER);
            miPaint.setTextSize(25);
            miString = (String)misRecursos.getText(R.string.juego_pausa);
        	miLienzo.drawText(miString, getWidth() / 2, (getHeight() / 2) - 13, miPaint);
            miString = (String)misRecursos.getText(R.string.juego_toque_pantalla);
        	miLienzo.drawText(miString, getWidth() / 2, (getHeight() / 2) + 13, miPaint);
        } else if (modoJuego == MODO_FIN) {
            miPaint.setColor(Color.WHITE);
            miPaint.setTextAlign(Paint.Align.CENTER);
            miPaint.setTextSize(25);
        	if (accionActual == -1) {
                miString = (String)misRecursos.getText(R.string.juego_felicidades);
            	miLienzo.drawText(miString, getWidth() / 2, (getHeight() / 2) - 30, miPaint);
        	}
        	miString = (String)misRecursos.getText(R.string.juego_fin);
        	miLienzo.drawText(miString, getWidth() / 2, getHeight() / 2, miPaint);
    		miPaint.setTextSize(13);
        	miString = (String)misRecursos.getText(R.string.juego_version_pro);
        	miLienzo.drawText(miString, getWidth() / 2, (getHeight() / 2) + 15, miPaint);
        	
            miPaint.setTextSize(20);
        	miString = (String)misRecursos.getText(R.string.juego_pulse_back);
        	miLienzo.drawText(miString, getWidth() / 2, (getHeight() / 2) + 100, miPaint);
        	
       	} else if (modoJuego == MODO_TUTORIAL) {        	
            miPaint.setColor(Color.WHITE);
            miPaint.setTextAlign(Paint.Align.CENTER);
            miPaint.setTextSize(12);

            miString = (String)misRecursos.getText(R.string.juego_tutorial_1);
        	miLienzo.drawText(miString, getWidth() / 2, 60, miPaint);
            miString = (String)misRecursos.getText(R.string.juego_tutorial_2);
        	miLienzo.drawText(miString, getWidth() / 2, 80, miPaint);

        	miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.control), getWidth() / 2 - 72, 100, null);

            miString = (String)misRecursos.getText(R.string.juego_tutorial_4);
        	miLienzo.drawText(miString, getWidth() / 2, 380, miPaint);
            miString = (String)misRecursos.getText(R.string.juego_tutorial_3);
        	miLienzo.drawText(miString, getWidth() / 2, 420, miPaint);

            miString = (String)misRecursos.getText(R.string.juego_toque_pantalla);
        	miLienzo.drawText(miString, getWidth() / 2, 450, miPaint);

       	} else if (modoJuego == MODO_CAMBIO_NIVEL) {        	
            miPaint.setColor(Color.WHITE);
            miPaint.setTextAlign(Paint.Align.CENTER);
            miPaint.setTextSize(25);

            miString = (String)misRecursos.getText(R.string.juego_felicidades);
        	miLienzo.drawText(miString, getWidth() / 2, 60, miPaint);
        	miString = (String)misRecursos.getText(R.string.juego_nivel_terminado);
        	miLienzo.drawText(miString, getWidth() / 2, 90, miPaint);

            miPaint.setTextAlign(Paint.Align.LEFT);
        	miPaint.setTextSize(18);
    		miString = (String)misRecursos.getText(R.string.juego_creditos)+": "+String.valueOf(creditosJuegoAnterior);
        	miLienzo.drawText(miString, 30,140, miPaint);
        	
    		miPaint.setTextSize(13);
    		miString = (String)misRecursos.getText(R.string.juego_nivel_terminado)+": 200";
        	miLienzo.drawText(miString, 30,170, miPaint);
        	porEnemigosEliminados = estadEnemigosEliminados * 100 / (estadEnemigosEliminados + estadEnemigosEscapados);
        	if (porEnemigosEliminados < 95) {
            	if (porEnemigosEliminados < 90) {
                	if (porEnemigosEliminados < 75) {
                		if (porEnemigosEliminados > 50) {
                			creditosTemporal = (int) (2 * porEnemigosEliminados);   		
                		} else {
                			creditosTemporal = 0;
                		}
            		} else {
                    	creditosTemporal = (int) (3 * porEnemigosEliminados);   		
            		}
            	} else {
                	creditosTemporal = (int) (5 * porEnemigosEliminados);   		
            	}
        	} else {
            	creditosTemporal = (int) (6 * porEnemigosEliminados);   		
        	}
    		miString = (String)misRecursos.getText(R.string.juego_enemigos_eliminados)+" ["+String.valueOf(porEnemigosEliminados)+"%]: "+String.valueOf(creditosTemporal);
        	miLienzo.drawText(miString, 30,190, miPaint);
    		miString = (String)misRecursos.getText(R.string.juego_blindaje)+" ["+String.valueOf(miHeroe.leeVidaObjeto())+"]: "+String.valueOf(10 * miHeroe.leeVidaObjeto());
        	miLienzo.drawText(miString, 30,210, miPaint);
    		miString = (String)misRecursos.getText(R.string.juego_naves_extra)+" ["+String.valueOf(navesRepuesto)+"]: "+String.valueOf(300 * navesRepuesto);
        	miLienzo.drawText(miString, 30,230, miPaint);
    		miString = (String)misRecursos.getText(R.string.juego_bombas)+" ["+String.valueOf(numeroBombas)+"]: "+String.valueOf(20 * numeroBombas);
        	miLienzo.drawText(miString, 30,250, miPaint);

        	miPaint.setTextSize(18);
    		miString = (String)misRecursos.getText(R.string.juego_creditos)+": "+String.valueOf(creditosJuego);
        	miLienzo.drawText(miString, 30,280, miPaint);

            miPaint.setTextAlign(Paint.Align.CENTER);
        	miPaint.setTextSize(25);
            miPaint.setColor(Color.WHITE);
            miString = (String)misRecursos.getText(R.string.juego_toque_pantalla);
        	miLienzo.drawText(miString, getWidth() / 2, 350, miPaint);
        	
        } else if (modoJuego == MODO_TIENDA) {
            miPaint.setTextAlign(Paint.Align.LEFT);
        	miPaint.setTextSize(20);
            miPaint.setColor(Color.WHITE);
    		miString = (String)misRecursos.getText(R.string.juego_creditos)+": "+String.valueOf(creditosJuego);
        	miLienzo.drawText(miString, 30,60, miPaint);

            //miPaint.setColor(Color.RED);
        	/*
        	Rect dest = new Rect(25, 105, 225, 155);
            Rect miRectangulo = new Rect(0,0,40,50);
        	miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.fondo_boton2), miRectangulo, dest, null);
        	*/
        	miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.punto_de_mira_17),30, 93, null);
        	miPaint.setTextSize(24);
            miPaint.setColor(Color.CYAN);
            miString = (String)misRecursos.getText(R.string.juego_compra_bomba);
            miLienzo.drawText(miString, 50, 110, miPaint);
        	miPaint.setTextSize(12);
            miPaint.setColor(Color.LTGRAY);
            miString = "300 "+ (String)misRecursos.getText(R.string.juego_creditos);
            miLienzo.drawText(miString, 50, 130, miPaint);
        	miPaint.setTextSize(24);
            miPaint.setColor(Color.CYAN);
        	miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.escudo),30, 153, null);
            miString = (String)misRecursos.getText(R.string.juego_compra_vida);
        	miLienzo.drawText(miString, 50, 170, miPaint);
        	miPaint.setTextSize(12);
            miPaint.setColor(Color.LTGRAY);
            miString = "300 "+ (String)misRecursos.getText(R.string.juego_creditos);
            miLienzo.drawText(miString, 50, 190, miPaint);
        	miPaint.setTextSize(24);
            miPaint.setColor(Color.CYAN);
        	miLienzo.drawBitmap(miCacheGraficos.get(R.drawable.heroe_17),30, 213, null);
            miString = (String)misRecursos.getText(R.string.juego_compra_nave);
        	miLienzo.drawText(miString, 50, 230, miPaint);
        	miPaint.setTextSize(12);
            miPaint.setColor(Color.LTGRAY);
            miString = "1600 "+ (String)misRecursos.getText(R.string.juego_creditos);
            miLienzo.drawText(miString, 50, 250, miPaint);
        	
        	miPaint.setTextSize(13);
            miPaint.setColor(Color.WHITE);
    		miString = (String)misRecursos.getText(R.string.juego_compra);
        	miLienzo.drawText(miString, 30,290, miPaint);
    		miString = (String)misRecursos.getText(R.string.juego_version_pro);
        	miLienzo.drawText(miString, 30,310, miPaint);

            miPaint.setTextAlign(Paint.Align.CENTER);
        	miPaint.setTextSize(25);
            //miPaint.setColor(Color.WHITE);
            miString = (String)misRecursos.getText(R.string.juego_toque_aqui);
        	miLienzo.drawText(miString, getWidth() / 2, 430, miPaint);
        	
        }
        
    }


    //************************************************************************
    //       DES/ACTIVACION DE SONIDO
    //************************************************************************
	/** Esta función des/activa el sonido
	 * @param sonido - true para activar el sonido
    public void ponSonido(Boolean sonido) {
    	usoSonido = sonido;
    }
	 */


    //************************************************************************
    //       MOVIMIENTO DEL HEROE
    //************************************************************************
    /** Esta función permite indicar al héroe cuanto debe desplazarse 
     *    en los ejes X e Y */
    public void mueveHeroeAcel(float x, float y) {
    	if ((modoJuego==MODO_EN_MARCHA) && (usoAcelerometro != ACEL_DESCONECTADO)) {
			// Compensamos la inclinación típica
	    	//   Si no compensamos (=0) la nave queda quieta con el dispositivo horizontal
			//   Con una compensación de entre 2 y 4 la nave queda quieta con el dispositivo
			//     levemente inclinado hacia delante (posición de juego más cómoda)
			//   Esto hace que la nave vaya hacia delante muy rápido y hacia atrás muy despacio
    		float aceleracion = 1;
    		switch (usoAcelerometro) {
    			case ACEL_SENSIBILIDAD_BAJA:
    				aceleracion = ACELERACION_BAJA;
    				break;
    			case ACEL_SENSIBILIDAD_MEDIA:
    				aceleracion = ACELERACION_MEDIA;
    				break;
    			case ACEL_SENSIBILIDAD_ALTA:
    				aceleracion = ACELERACION_ALTA;
    				break;
    		}
    		
			x = x * aceleracion;
			if (x < -VELOCIDAD_MAXIMA_HEROE) {
				x = -VELOCIDAD_MAXIMA_HEROE;
			} else if (x > VELOCIDAD_MAXIMA_HEROE) {
				x = VELOCIDAD_MAXIMA_HEROE;
			}
			
			y = (y - COMPENSACION_Y) * aceleracion;
			if (y < -VELOCIDAD_MAXIMA_HEROE) {
				y = -VELOCIDAD_MAXIMA_HEROE;
			} else if (y > VELOCIDAD_MAXIMA_HEROE) {
				y = VELOCIDAD_MAXIMA_HEROE;
			}

			despHeroeX = x;
    		despHeroeY = y;
    	}
    }
    
    //************************************************************************
    //       LANZAMIENTO DE BOMBA
    //************************************************************************
    /** Esta función es un envoltorio (wrapper) de la función explosionBomba()
     * que se ocupa de verificar que el jugador no lanza dos bombas seguidas por error
     */
    private void lanzaBomba() {
		//Toast.makeText(getContext(), " Boumb! ", Toast.LENGTH_SHORT).show();
    	if ((tiempoNoBomba < cicloJuego) && (numeroBombas > 0)) {
    		tiempoNoBomba = cicloJuego + TIEMPO_NO_BOMBA;
    		numeroBombas--;
    		explosionBomba();
    		objNoBombas = false;
    	}
    }
    private void explosionBomba() {
		// Vector auxiliar de gráficos a extraer de otros vectores
        ArrayList<Grafico> objetosFuera = new ArrayList<Grafico>();
        
    	// Emitimos el sonido de la explosión
    	if (usoSonido) miSoundPool.play(sonidoBomba, 1, 1, 0, 0, 1);
    	// Vibramos
		((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);

    	// Para cada objeto gráfico del vector de enemigos
        for (Grafico miGrafico : vectorEnemigos) {
   			// Restamos vida al objeto
       		restaVidaEnemigo(miGrafico, -30, true);

       		// Si no le queda vida al objeto del vector de enemigos
    		if (miGrafico.leeVidaObjeto() < 1) {
            	// Marcamos que ahora es una explosión
            	miGrafico.ponTipo(GRAFICO_EXPLOSION);
            	// Indicamos que está en el primer paso de la explosión 
            	miGrafico.ponTiempoCreado(0);
            	// Indicamos el dibujo que corresponde al objeto
            	miGrafico.ponDibujo(miCacheGraficos.get(R.drawable.explosion_animacion), 32, 32, 7);
				// Ponemos el gráfico en el vector objetosFuera
				objetosFuera.add(miGrafico);
    		}
        } // for (Grafico miGrafico : vectorEnemigos) 

        // Si hay algún objeto que pasar a efectos gráficos
        if (!objetosFuera.isEmpty()) {
        	// Lo(s) añadimos al vector de efectos gráficos
        	vectorEfectos.addAll(objetosFuera);
        	// Lo(s) sacamos del vector de enemigos
            vectorEnemigos.removeAll(objetosFuera);
            // Limpiamos el vector
            objetosFuera.clear();
        }
    }
    
    
    //************************************************************************
    //       MODO DE JUEGO
    //************************************************************************
    /**
     * Actualiza el modo de juego de la apliación (EN_MARCHA, PAUSA...)
	 * @param nuevoModo - (EN_MARCHA, PAUSA...) */
    public void ponModoJuego(int nuevoModo) {
    	modoJuego = nuevoModo;
    }
    public int leeModoJuego() {
    	return modoJuego;
    }


    //************************************************************************
    //       FUNCIONES DE SURFACE
    //************************************************************************
    /**
     * Sobreescribimos la función "surfaceChanged" que se lanza al cambiar la superficie
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * Sobreescribimos la función "surfaceCreated" que se lanza al crearse la superficie
     *   al inicio o al relanzarse la actividad
     */
    public void surfaceCreated(SurfaceHolder holder) {
    	// Verifico si el hilo ya está vivo
        if (!miHilo.isAlive()) {
        	// Si no, lo creamos
            miHilo = new ANAHilo(this);
        }
        
        // Ponemos el hilo en marcha
        miHilo.ponEnMarcha(true);
        miHilo.start();
    }

    /**
     * Sobreescribimos la función "surfaceDestroyed" que se lanza al destruirse la superficie
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
    	// Generamos una variable auxiliar que nos indica si dbemos reintentar matar el proceso correctamente
        boolean reintentar = true;

        // Le decimos al hilo que termine y espere a acabar,
    	//   si no podría tocar la superficie después de que volvamos
        miHilo.ponEnMarcha(false);
        while (reintentar) {
            try {
                miHilo.join();
                reintentar = false;
            } catch (InterruptedException e) {
                // Probamos una y otra vez hasta que lo consigamos
            }
        }
        //Log.i("Hilo", "Hilo terminado...");
    }


    //************************************************************************
    //       CARGA LA CACHE DE DIBUJOS
    //************************************************************************
    /** Esta función carga la caché de gráficos */
    private void cargaCacheGraficos() {
        miCacheGraficos.put(R.drawable.anillo_naranja, BitmapFactory.decodeResource(getResources(), R.drawable.anillo_naranja));
        miCacheGraficos.put(R.drawable.bombas_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.bombas_animacion));
        miCacheGraficos.put(R.drawable.botiquin_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.botiquin_animacion));
        miCacheGraficos.put(R.drawable.brillo_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.brillo_animacion));
        miCacheGraficos.put(R.drawable.capsula_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.capsula_animacion));
        miCacheGraficos.put(R.drawable.control, BitmapFactory.decodeResource(getResources(), R.drawable.control));
        miCacheGraficos.put(R.drawable.diamantes_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.diamantes_animacion));
        miCacheGraficos.put(R.drawable.disparo_13, BitmapFactory.decodeResource(getResources(), R.drawable.disparo_13));
        miCacheGraficos.put(R.drawable.disparo_9, BitmapFactory.decodeResource(getResources(), R.drawable.disparo_9));
        miCacheGraficos.put(R.drawable.disparo_enemigo_3, BitmapFactory.decodeResource(getResources(), R.drawable.disparo_enemigo_3));
        miCacheGraficos.put(R.drawable.disparo_heroe_3, BitmapFactory.decodeResource(getResources(), R.drawable.disparo_heroe_3));
        miCacheGraficos.put(R.drawable.enemigo1_40, BitmapFactory.decodeResource(getResources(), R.drawable.enemigo1_40));
        miCacheGraficos.put(R.drawable.enemigo2_40_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.enemigo2_40_animacion));
        miCacheGraficos.put(R.drawable.enemigo3_40, BitmapFactory.decodeResource(getResources(), R.drawable.enemigo3_40));
        miCacheGraficos.put(R.drawable.enemigo4_40, BitmapFactory.decodeResource(getResources(), R.drawable.enemigo4_40));
        miCacheGraficos.put(R.drawable.enemigo5_26_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.enemigo5_26_animacion));
        miCacheGraficos.put(R.drawable.enemigo6_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.enemigo6_animacion));
        miCacheGraficos.put(R.drawable.escudo, BitmapFactory.decodeResource(getResources(), R.drawable.escudo));
        miCacheGraficos.put(R.drawable.estrella_1, BitmapFactory.decodeResource(getResources(), R.drawable.estrella_1));
        miCacheGraficos.put(R.drawable.estrella_2, BitmapFactory.decodeResource(getResources(), R.drawable.estrella_2));
        miCacheGraficos.put(R.drawable.estrella_3, BitmapFactory.decodeResource(getResources(), R.drawable.estrella_3));
        miCacheGraficos.put(R.drawable.estrella_4, BitmapFactory.decodeResource(getResources(), R.drawable.estrella_4));
        miCacheGraficos.put(R.drawable.estrella_5, BitmapFactory.decodeResource(getResources(), R.drawable.estrella_5));
        miCacheGraficos.put(R.drawable.estrella_6, BitmapFactory.decodeResource(getResources(), R.drawable.estrella_6));
        miCacheGraficos.put(R.drawable.explosion_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.explosion_animacion));
        //miCacheGraficos.put(R.drawable.fondo_0, BitmapFactory.decodeResource(getResources(), R.drawable.fondo_0));
        miCacheGraficos.put(R.drawable.fondo_trans, BitmapFactory.decodeResource(getResources(), R.drawable.fondo_trans));
        miCacheGraficos.put(R.drawable.heroe, BitmapFactory.decodeResource(getResources(), R.drawable.heroe));
        miCacheGraficos.put(R.drawable.heroe_17, BitmapFactory.decodeResource(getResources(), R.drawable.heroe_17));
        miCacheGraficos.put(R.drawable.heroe_animacion_40, BitmapFactory.decodeResource(getResources(), R.drawable.heroe_animacion_40));
        miCacheGraficos.put(R.drawable.punto_de_mira_00, BitmapFactory.decodeResource(getResources(), R.drawable.punto_de_mira_00));
        miCacheGraficos.put(R.drawable.punto_de_mira_17, BitmapFactory.decodeResource(getResources(), R.drawable.punto_de_mira_17));
        miCacheGraficos.put(R.drawable.radar_00_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.radar_00_animacion));
        miCacheGraficos.put(R.drawable.roca1, BitmapFactory.decodeResource(getResources(), R.drawable.roca1));
        miCacheGraficos.put(R.drawable.superenemigo_1_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.superenemigo_1_animacion));
        miCacheGraficos.put(R.drawable.superenemigo_2_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.superenemigo_2_animacion));
        miCacheGraficos.put(R.drawable.superenemigo_3_animacion, BitmapFactory.decodeResource(getResources(), R.drawable.superenemigo_3_animacion));
        }

    
    //************************************************************************
    //       NIVELES
    //************************************************************************
    private void cargaNivel(int nivelJuego) {
		// Desactivamos la generación aleatoria de enemigos
		enemigosAleatorios = 0;
    	// Cargamos el nivel correspondiente
        switch (nivelJuego) {
    		case 1:
    			if (DEBUG) {
    				vectorNivelActual = nivel_debug_01;
    			} else {
        			vectorNivelActual = nivel01;
    			}
    			objTerminarNivel = true;
    			break;
			case 2:
    			if (DEBUG) {
        			vectorNivelActual = nivel_debug_02;
    			} else {
        			vectorNivelActual = nivel02;
    			}
    			break;
			case 3:
    			if (DEBUG) {
        			vectorNivelActual = nivel_debug_03;
    			} else {
        			vectorNivelActual = nivel03;
    			}
    			break;
    		default: // Se ha terminado el último nivel
	        	accionActual = -1;
    			vectorNivelActual = null;
				terminaPartida();
    			break;
        }
        if (vectorNivelActual != null) {
        	// Carga de nivel
    		//----------------------------------
            // Cargamos la primera acción del nivel
        	accionActual = 0;
    		vectorAccionActual = vectorNivelActual[accionActual];
    		// Reiniciamos el ciclo de juego
    		cicloJuego = 0;

    		// Limpiamos tiempos
    		//----------------------------------
    		tiempoInvulnerable = cicloJuego + TIEMPO_INVULNERABLE;
    		tiempoInvisible = cicloJuego;
    		tiempoNoBomba = 0;
    		
    		// Limpiamos vectores
    		//----------------------------------
    		vectorEstrellas.clear();
    		vectorEnemigos.clear();
    		vectorEfectos.clear();
    		vectorAyudas.clear();
    		vectorDisparosHeroe.clear();
    		vectorDisparosEnemigo.clear();
    		
    		// Limpiamos estadísticas y objetivos
    		//----------------------------------
    		estadEnemigosEscapados = 0;
    		estadEnemigosEliminados = 0;
    	    objNoBombas = true;
    	    objTodosEnemigos = true;
    	    objNingunEnemigo = true;
    	    objTerminarNivel = false;
    	    
    		// Nuevo fondo
    		//----------------------------------
    		vectorEstrellas.clear();
    		// Creamos unas cuantas estrellas iniciales
    		int contador = 0;
    		while (contador < 15) {
    			creaEstrella(true);
    			contador++;
    		}

    		// Ponemos al heroe en posición de partida
    		//----------------------------------
            miHeroe.ponX(150);
            miHeroe.ponY(410);
            //miHeroe.ponX(getWidth() / 2);
            //miHeroe.ponY(getHeight() - miHeroe.leeAltoSprite());
            miHeroe.ponDespX(0);
            miHeroe.ponDespY(0);

    		// Nos ponemos en marcha
    		modoJuego = MODO_EN_MARCHA;
        }
    }


    /*
     * Cada acción es un vector de enteros que indica:
     * + Momento, Vector (1 - ayudas, 2 - enemigos, 3 - efectos), AZAR (0-no,1-sí), Objeto, X, Y, despX, despY
     * 
	// Nave enemiga aleatoria normal
	{xxxxx,2,1,GRAFICO_ALEATORIO},
	// Nave enemiga aleatoria normal del tipo especificado
	{xxxxx,2,1,GRAFICO_ROCA},
	// Naves normales de diferentes tipos
	{xxxxx,2,0,GRAFICO_ENEMIGO_01,20,0,0,3},
	{xxxxx,2,0,GRAFICO_ENEMIGO_02,100,0,3,3},
	{xxxxx,2,0,GRAFICO_ENEMIGO_03,200,0,4,1},
   	// Capsula normal
   	{xxxxx,1,1,GRAFICO_CAPSULA},
	*/
    //------------------------------------------
    // NIVELES DE DEBUG
    //------------------------------------------
    static final int[][] nivel_debug_01 = {
    	{   10,1,0,GRAFICO_BOTIQUIN},
    	{ 100,2,0,GRAFICO_ENEMIGO_01,260,0,0,3},
    	{-1,-1,-1,-1}};

    static final int[][] nivel_debug_02 = {
    	{  10,1,0,GRAFICO_BOTIQUIN},
    	{  70,1,0,GRAFICO_BOMBA},
    	{ 120,2,0,GRAFICO_SUPERENEMIGO_02,0,0,1,1},
    	{-1,-1,-1,-1}};

    static final int[][] nivel_debug_03 = {
    	{   10,1,0,GRAFICO_BOTIQUIN},
    	{   70,1,0,GRAFICO_BOMBA},
    	{  100,2,0,GRAFICO_SUPERENEMIGO_01b,0,0,1,1},
    	{  150,1,0,GRAFICO_BOMBA},
    	{  200,1,0,GRAFICO_BOMBA},
    	{  250,1,0,GRAFICO_BOTIQUIN},
    	{  300,1,0,GRAFICO_BOMBA},
    	{  350,1,0,GRAFICO_BOTIQUIN},
    	{  400,1,0,GRAFICO_BOMBA},
    	{  450,1,0,GRAFICO_BOTIQUIN},
    	{  500,1,0,GRAFICO_BOTIQUIN},
    	{  550,1,0,GRAFICO_BOMBA},
    	{  600,1,0,GRAFICO_BOTIQUIN},
    	{  650,1,0,GRAFICO_BOMBA},
    	{  700,1,0,GRAFICO_BOTIQUIN},
    	{  750,1,0,GRAFICO_BOMBA},
    	{  800,1,0,GRAFICO_BOTIQUIN},
    	{  850,1,0,GRAFICO_BOMBA},
    	{  900,1,0,GRAFICO_BOTIQUIN},
    	{  950,1,0,GRAFICO_BOMBA},
    	{ 1000,2,0,GRAFICO_SUPERENEMIGO_02b,0,0,1,1},
    	{ 1000,1,0,GRAFICO_BOTIQUIN},
    	{ 1050,1,0,GRAFICO_BOMBA},
    	{ 1100,1,0,GRAFICO_BOTIQUIN},
    	{ 1150,1,0,GRAFICO_BOMBA},
    	{ 1200,1,0,GRAFICO_BOTIQUIN},
    	{ 1250,1,0,GRAFICO_BOMBA},
    	{ 1300,2,1,GRAFICO_ALEATORIO},
    	{ 1350,2,1,GRAFICO_ALEATORIO},
    	{ 1400,1,0,GRAFICO_BOTIQUIN},
    	{ 1450,1,0,GRAFICO_BOMBA},
    	{ 1500,1,0,GRAFICO_BOMBA},
    	{ 1550,1,0,GRAFICO_BOTIQUIN},
    	{ 1600,1,0,GRAFICO_BOMBA},
    	{ 1650,1,0,GRAFICO_BOTIQUIN},
    	{ 1700,1,0,GRAFICO_BOMBA},
    	{ 1750,1,0,GRAFICO_BOTIQUIN},
    	{ 1800,1,0,GRAFICO_BOMBA},
    	{ 1850,1,0,GRAFICO_BOTIQUIN},

    	{ 1900,2,0,GRAFICO_SUPERENEMIGO_01b,0,0,1,1},
    	{ 1950,2,0,GRAFICO_SUPERENEMIGO_02b,0,0,1,1},

    	{ 2000,1,0,GRAFICO_BOMBA},
    	{ 2050,1,0,GRAFICO_BOTIQUIN},
    	{ 2100,1,0,GRAFICO_BOMBA},
    	{ 2150,1,0,GRAFICO_BOTIQUIN},
    	{ 2200,1,0,GRAFICO_BOMBA},
    	{ 2250,1,0,GRAFICO_BOTIQUIN},
    	{ 2300,1,0,GRAFICO_BOMBA},
    	{ 2350,1,0,GRAFICO_BOTIQUIN},
    	{ 2400,1,0,GRAFICO_BOMBA},
    	{ 2450,1,0,GRAFICO_BOTIQUIN},
    	{ 2500,1,0,GRAFICO_BOMBA},
    	{ 2550,1,0,GRAFICO_BOTIQUIN},
    	{ 2600,1,0,GRAFICO_BOMBA},
    	{ 2650,1,0,GRAFICO_BOTIQUIN},
    	{ 2700,1,0,GRAFICO_BOMBA},
    	{ 2750,1,0,GRAFICO_BOTIQUIN},
    	{ 2800,1,0,GRAFICO_BOMBA},
    	{ 2850,1,0,GRAFICO_BOTIQUIN},
    	{ 2900,1,0,GRAFICO_BOMBA},
    	{ 2950,1,0,GRAFICO_BOTIQUIN},
    	{ 3000,1,0,GRAFICO_BOMBA},
    	{ 3050,1,0,GRAFICO_BOTIQUIN},
    	{ 3100,1,0,GRAFICO_BOMBA},
    	{ 3150,1,0,GRAFICO_BOTIQUIN},
    	{ 3200,1,0,GRAFICO_BOMBA},
    	{ 3250,1,0,GRAFICO_BOTIQUIN},
    	{ 3300,1,0,GRAFICO_BOMBA},
    	{ 3350,1,0,GRAFICO_BOTIQUIN},

    	{ 3500,2,0,GRAFICO_ENEMIGO_01,260,0,0,3},

    	{-1,-1,-1,-1}};

    //------------------------------------------
    // NIVELES DE JUEGO
    //------------------------------------------
    static final int[][] nivel01 = {
    	// Empezamos con premio ;-)
    	// Capsula normal
    	{   10,1,0,GRAFICO_BOTIQUIN},
    	
    	// Dejamos que el jugador se adapte a los controles
    	//   y aprenda la lógica de enemigo_01
    	// (Azul) desp[0,3]. Cuando llega a una cierta altura dispara y cambia su trayectoria
    	{   60,2,0,GRAFICO_ENEMIGO_01,20,0,0,3},
    	{  160,2,0,GRAFICO_ENEMIGO_01,140,0,0,3},
    	{  220,2,0,GRAFICO_ENEMIGO_01,260,0,0,3},
    	// Primeras formaciones
    	{  320,2,0,GRAFICO_ENEMIGO_01,10,0,0,3},
    	{  320,2,0,GRAFICO_ENEMIGO_01,50,0,0,3},
    	{  420,2,0,GRAFICO_ENEMIGO_01,220,0,0,3},
    	{  420,2,0,GRAFICO_ENEMIGO_01,270,0,0,3},
    	// Subimos un poco el nivel
    	{  490,2,0,GRAFICO_ENEMIGO_01,20,0,0,3},
    	{  490,2,0,GRAFICO_ENEMIGO_01,140,0,0,3},
    	{  490,2,0,GRAFICO_ENEMIGO_01,260,0,0,3},
    	{  520,2,0,GRAFICO_ENEMIGO_01,80,0,0,3},
    	{  520,2,0,GRAFICO_ENEMIGO_01,200,0,0,3},
    	
    	// Presentamos a enemigo_02
		// (Verde)  desp[3,3]. Tiene más vida. Cuando llega a una cierta altura cambia su velocidad y puede cambiar su trayectoria
    	{  600,2,0,GRAFICO_ENEMIGO_02,20,0,3,3},
    	{  600,2,0,GRAFICO_ENEMIGO_02,140,0,3,3},
    	{  600,2,0,GRAFICO_ENEMIGO_02,260,0,3,3},
    	// Continuamos con un poco de azar (para que no se aprendan todo el nivel fácilmente)
    	{  700,2,1,GRAFICO_ENEMIGO_02,20,0,3,3},
    	{  750,2,1,GRAFICO_ENEMIGO_02,140,0,3,3},
    	{  800,2,1,GRAFICO_ENEMIGO_02,140,0,3,3},

    	// Presentamos a enemigo_03
		// (Gris) desp[4,1]. Va de lado a lado y si se cruza con el heroe dispara (una vez por trayecto lateral)
    	// Pareja de tipos 3 arriba a la izquierda
    	{  900,2,0,GRAFICO_ENEMIGO_03,10,0,4,1},
    	{  900,2,0,GRAFICO_ENEMIGO_03,50,0,4,1},
    	// Pareja de tipos 3 arriba a la derecha
    	{  900,2,0,GRAFICO_ENEMIGO_03,240,0,-4,1},
    	{  900,2,0,GRAFICO_ENEMIGO_03,280,0,-4,1},

    	
    	// Terminadas las presentaciones, seguimos
    	// Dos columnas de 2 enemigo_01
    	{ 1000,2,0,GRAFICO_ENEMIGO_01,0,0,0,3},
    	{ 1000,2,0,GRAFICO_ENEMIGO_01,280,0,0,3},
    	{ 1020,2,0,GRAFICO_ENEMIGO_01,0,0,0,3},
    	{ 1020,2,0,GRAFICO_ENEMIGO_01,280,0,0,3},
    	
    	// 3 enemigo_02 por el lateral izquierdo
    	{ 1100,2,0,GRAFICO_ENEMIGO_02,-30,80,3,3},
    	{ 1120,2,0,GRAFICO_ENEMIGO_02,-30,80,3,3},
    	{ 1140,2,0,GRAFICO_ENEMIGO_02,-30,80,3,3},
    	
    	// 3 enemigo_02 por el lateral derecho
    	{ 1200,2,0,GRAFICO_ENEMIGO_02,340,80,-3,3},
    	{ 1220,2,0,GRAFICO_ENEMIGO_02,340,80,-3,3},
    	{ 1240,2,0,GRAFICO_ENEMIGO_02,340,80,-3,3},
    	
    	// 3 enemigo_03 arriba
    	{ 1300,2,0,GRAFICO_ENEMIGO_03,0,0,4,1},
    	{ 1300,2,0,GRAFICO_ENEMIGO_03,140,0,4,1},
    	{ 1300,2,0,GRAFICO_ENEMIGO_03,280,0,-4,1},

    	{ 1400,2,1,GRAFICO_ALEATORIO},
    	{ 1450,2,1,GRAFICO_ALEATORIO},
    	{ 1500,2,1,GRAFICO_ALEATORIO},
    	{ 1550,2,1,GRAFICO_ALEATORIO},
    	{ 1600,2,1,GRAFICO_ALEATORIO},
    	{ 1650,2,1,GRAFICO_ALEATORIO},
    	{ 1700,2,1,GRAFICO_ALEATORIO},
    	{ 1700,2,1,GRAFICO_ALEATORIO},
    	{ 1700,2,1,GRAFICO_ALEATORIO},

    	{ 1800,2,1,GRAFICO_ALEATORIO},
    	{ 1850,2,1,GRAFICO_ALEATORIO},
    	{ 1900,2,1,GRAFICO_ALEATORIO},
    	{ 1950,2,1,GRAFICO_ALEATORIO},
    	{ 2000,2,1,GRAFICO_ALEATORIO},
    	{ 2050,2,1,GRAFICO_ALEATORIO},
    	{ 2050,2,1,GRAFICO_ALEATORIO},
    	{ 2050,2,1,GRAFICO_ALEATORIO},
    	
    	{ 2200,2,1,GRAFICO_ALEATORIO},
    	{ 2250,2,1,GRAFICO_ALEATORIO},
    	{ 2300,2,1,GRAFICO_ALEATORIO},
    	{ 2350,2,1,GRAFICO_ALEATORIO},
    	{ 2400,2,1,GRAFICO_ALEATORIO},
    	{ 2450,2,1,GRAFICO_ALEATORIO},
    	{ 2450,2,1,GRAFICO_ALEATORIO},
    	{ 2450,2,1,GRAFICO_ALEATORIO},

    	{ 2600,2,1,GRAFICO_ALEATORIO},
    	{ 2650,2,1,GRAFICO_ALEATORIO},
    	{ 2700,2,1,GRAFICO_ALEATORIO},
    	{ 2750,2,1,GRAFICO_ALEATORIO},
    	{ 2800,2,1,GRAFICO_ALEATORIO},
    	{ 2850,2,1,GRAFICO_ALEATORIO},
    	{ 2900,2,1,GRAFICO_ALEATORIO},
    	{ 2900,2,1,GRAFICO_ALEATORIO},
    	{ 2900,2,1,GRAFICO_ALEATORIO},

    	// Dos columnas de enemigo_01
    	{ 3100,2,0,GRAFICO_ENEMIGO_01,0,0,0,3},
    	{ 3100,2,0,GRAFICO_ENEMIGO_01,280,0,0,3},
    	{ 3120,2,0,GRAFICO_ENEMIGO_01,0,0,0,3},
    	{ 3120,2,0,GRAFICO_ENEMIGO_01,280,0,0,3},
    	{ 3140,2,0,GRAFICO_ENEMIGO_01,0,0,0,3},
    	{ 3140,2,0,GRAFICO_ENEMIGO_01,280,0,0,3},

    	// Vamos acelerando
    	// Unos cuantos enemigos aleatorios
    	{ 3250,2,1,GRAFICO_ALEATORIO},
    	{ 3300,2,1,GRAFICO_ALEATORIO},
    	{ 3350,2,1,GRAFICO_ALEATORIO},
    	{ 3400,2,1,GRAFICO_ALEATORIO},
    	{ 3450,2,1,GRAFICO_ALEATORIO},
    	{ 3500,2,1,GRAFICO_ALEATORIO},
    	{ 3500,2,1,GRAFICO_ALEATORIO},
    	{ 3500,2,1,GRAFICO_ALEATORIO},
    	
    	// TRACA FINAL
    	// Capsula normal para dar un respiro
    	{ 3580,1,0,GRAFICO_BOTIQUIN},

    	// Pareja de tipos 3 arriba a la izquierda
    	{ 3620,2,0,GRAFICO_ENEMIGO_03,10,0,4,1},
    	{ 3620,2,0,GRAFICO_ENEMIGO_03,50,0,4,1},
    	// Pareja de tipos 3 arriba a la derecha
    	{ 3620,2,0,GRAFICO_ENEMIGO_03,240,0,-4,1},
    	{ 3620,2,0,GRAFICO_ENEMIGO_03,280,0,-4,1},
    	// 3 tipo 2 por cada lateral
    	{ 3720,2,0,GRAFICO_ENEMIGO_02,-30,80,3,3},
    	{ 3720,2,0,GRAFICO_ENEMIGO_02,340,80,-3,3},
    	{ 3740,2,0,GRAFICO_ENEMIGO_02,-30,80,3,3},
    	{ 3740,2,0,GRAFICO_ENEMIGO_02,340,80,-3,3},
    	{ 3760,2,0,GRAFICO_ENEMIGO_02,-30,80,3,3},
    	{ 3760,2,0,GRAFICO_ENEMIGO_02,340,80,-3,3},
    	// Pareja de tipos 3 arriba a la izquierda
    	{ 3800,2,0,GRAFICO_ENEMIGO_03,10,0,4,1},
    	{ 3800,2,0,GRAFICO_ENEMIGO_03,50,0,4,1},
    	// Pareja de tipos 3 arriba a la derecha
    	{ 3800,2,0,GRAFICO_ENEMIGO_03,240,0,-4,1},
    	{ 3800,2,0,GRAFICO_ENEMIGO_03,280,0,-4,1},
    	// Formación 3-2 de enemigo_1
    	{ 3880,2,0,GRAFICO_ENEMIGO_01,20,0,0,3},
    	{ 3880,2,0,GRAFICO_ENEMIGO_01,140,0,0,3},
    	{ 3880,2,0,GRAFICO_ENEMIGO_01,260,0,0,3},
    	{ 3910,2,0,GRAFICO_ENEMIGO_01,80,0,0,3},
    	{ 3910,2,0,GRAFICO_ENEMIGO_01,200,0,0,3},

    	// Premio para el final
    	{ 4000,1,0,GRAFICO_BOMBA},
    	// Entra el superenemigo
    	{ 4100,2,0,GRAFICO_SUPERENEMIGO_01,0,0,1,1},
    	
    	// Indica que el nivel ha terminado
    	{-1,-1,-1,-1}};

    
    static final int[][] nivel02 = {
    	// Lluvia de meteoritos, cada vez más intensa
    	// Presentamos enemigo_06
    	// (Gris giratorio) desp[4,1]. Tiene poca vida. Va de lado a lado disparando cada vez que está de frente.
    	{   10,2,1,GRAFICO_ROCA},
    	{   60,2,1,GRAFICO_ROCA},
    	{  110,2,1,GRAFICO_ROCA},
    	{  170,2,1,GRAFICO_ROCA},
    	{  240,2,1,GRAFICO_ROCA},
    	{  310,2,1,GRAFICO_ROCA},
    	{  480,2,1,GRAFICO_ROCA},
    	{  580,2,1,GRAFICO_ROCA},
       	{  600,2,1,GRAFICO_ENEMIGO_06},
        {  630,2,1,GRAFICO_ROCA},
    	{  680,2,1,GRAFICO_ROCA},
    	{  700,2,1,GRAFICO_ROCA},
    	{  750,2,1,GRAFICO_ENEMIGO_06},
    	{  800,2,1,GRAFICO_ROCA},
    	{  850,2,1,GRAFICO_ROCA},
    	{  900,2,1,GRAFICO_ROCA},
    	{  950,2,1,GRAFICO_ROCA},
    	{  980,2,1,GRAFICO_ROCA},
    	{ 1000,1,0,GRAFICO_BOTIQUIN},
    	{ 1110,2,1,GRAFICO_ROCA},
    	{ 1140,2,1,GRAFICO_ROCA},
    	{ 1140,2,1,GRAFICO_ROCA},
    	{ 1170,2,1,GRAFICO_ROCA},
       	{ 1180,2,1,GRAFICO_ENEMIGO_06},
    	{ 1200,2,1,GRAFICO_ROCA},
    	{ 1220,2,1,GRAFICO_ROCA},
    	{ 1240,2,1,GRAFICO_ROCA},
       	{ 1270,2,1,GRAFICO_ENEMIGO_06},
    	{ 1300,2,1,GRAFICO_ROCA},
    	{ 1400,2,1,GRAFICO_ROCA},
    	{ 1400,2,1,GRAFICO_ROCA},
    	{ 1450,2,1,GRAFICO_ROCA},
    	{ 1450,2,1,GRAFICO_ROCA},
       	{ 1470,2,1,GRAFICO_ENEMIGO_06},
       	{ 1490,2,1,GRAFICO_ENEMIGO_06},
    	{ 1500,2,1,GRAFICO_ROCA},
    	{ 1550,2,1,GRAFICO_ROCA},
    	{ 1580,2,1,GRAFICO_ROCA},
       	{ 1600,2,1,GRAFICO_ENEMIGO_06},
       	{ 1650,2,1,GRAFICO_ENEMIGO_06},
       	{ 1700,2,1,GRAFICO_ENEMIGO_06},
    	{ 1720,2,1,GRAFICO_ROCA},
       	{ 1750,2,1,GRAFICO_ENEMIGO_06},
       	{ 1800,2,1,GRAFICO_ENEMIGO_06},
       	{ 1850,2,1,GRAFICO_ENEMIGO_06},
    	{ 1900,2,1,GRAFICO_ROCA},
    	{ 1950,2,1,GRAFICO_ROCA},
       	{ 2000,2,1,GRAFICO_ENEMIGO_06},
       	{ 2050,2,1,GRAFICO_ENEMIGO_06},
       	{ 2100,2,1,GRAFICO_ENEMIGO_06},
    	{ 2120,2,1,GRAFICO_ROCA},
    	{ 2140,2,1,GRAFICO_ROCA},
    	{ 2160,2,1,GRAFICO_ROCA},
    	{ 2180,2,1,GRAFICO_ROCA},
       	{ 2190,2,1,GRAFICO_ENEMIGO_06},
       	{ 2190,2,1,GRAFICO_ENEMIGO_06},
    	{ 2200,2,1,GRAFICO_ROCA},
    	{ 2220,2,1,GRAFICO_ROCA},
    	{ 2250,2,1,GRAFICO_ROCA},
    	{ 2280,2,1,GRAFICO_ROCA},
       	{ 2300,2,1,GRAFICO_ENEMIGO_06},
    	{ 2350,2,1,GRAFICO_ROCA},
    	{ 2400,2,1,GRAFICO_ROCA},
    	{ 2480,2,1,GRAFICO_ROCA},
    	{ 2510,2,1,GRAFICO_ROCA},

    	// 3 tipo 2 por cada lateral
    	{ 2550,2,0,GRAFICO_ENEMIGO_02,-30,80,3,3},
    	{ 2550,2,0,GRAFICO_ENEMIGO_02,340,80,-3,3},
    	{ 2570,2,0,GRAFICO_ENEMIGO_02,-30,80,3,3},
    	{ 2570,2,0,GRAFICO_ENEMIGO_02,340,80,-3,3},
    	{ 2590,2,0,GRAFICO_ENEMIGO_02,-30,80,3,3},
    	{ 2590,2,0,GRAFICO_ENEMIGO_02,340,80,-3,3},

    	{ 2600,2,1,GRAFICO_ROCA},
    	{ 2650,2,1,GRAFICO_ROCA},

    	// Unos cuantos enemigos aleatorios
    	{ 2700,2,1,GRAFICO_ALEATORIO},
    	{ 2760,2,1,GRAFICO_ALEATORIO},
    	{ 2820,2,1,GRAFICO_ALEATORIO},
    	{ 2880,2,1,GRAFICO_ALEATORIO},
    	{ 2940,2,1,GRAFICO_ALEATORIO},
    	{ 3020,2,1,GRAFICO_ALEATORIO},
    	{ 3020,2,1,GRAFICO_ALEATORIO},
    	{ 3020,2,1,GRAFICO_ALEATORIO},

    	// Presentamos enemigo_04
    	// (Flecha gris) desp[0,6]. Solo corre por la pantalla
    	// 3 enemigo_04 arriba
    	{ 3120,2,0,GRAFICO_ENEMIGO_04,0,0,0,6},
    	{ 3120,2,0,GRAFICO_ENEMIGO_04,140,0,0,6},
    	{ 3120,2,0,GRAFICO_ENEMIGO_04,280,0,0,6},
    	// 2 enemigo_4 arriba
    	{ 3200,2,0,GRAFICO_ENEMIGO_04,70,0,0,6},
    	{ 3200,2,0,GRAFICO_ENEMIGO_04,210,0,0,6},
    	// 3 enemigo_04 arriba
    	{ 3280,2,0,GRAFICO_ENEMIGO_04,0,0,0,6},
    	{ 3280,2,0,GRAFICO_ENEMIGO_04,140,0,0,6},
    	{ 3280,2,0,GRAFICO_ENEMIGO_04,280,0,0,6},
    	// 2 enemigo_04 arriba
    	{ 3360,2,0,GRAFICO_ENEMIGO_04,70,0,0,6},
    	{ 3360,2,0,GRAFICO_ENEMIGO_04,210,0,0,6},

    	{ 3400,2,1,GRAFICO_ROCA},
    	{ 3420,2,1,GRAFICO_ROCA},
    	{ 3440,2,1,GRAFICO_ROCA},

    	// Unos cuantos enemigos aleatorios
    	{ 3450,2,1,GRAFICO_ALEATORIO},
    	{ 3500,2,1,GRAFICO_ALEATORIO},
    	{ 3550,2,1,GRAFICO_ALEATORIO},
    	
    	// 3 enemigo_04 arriba
    	{ 3600,2,0,GRAFICO_ENEMIGO_04,0,0,0,6},
    	{ 3600,2,0,GRAFICO_ENEMIGO_04,140,0,0,6},
    	{ 3600,2,0,GRAFICO_ENEMIGO_04,280,0,0,6},
    	
    	// Pareja de tipos 3 arriba a la izquierda
    	{ 3700,2,0,GRAFICO_ENEMIGO_03,10,0,4,1},
    	{ 3700,2,0,GRAFICO_ENEMIGO_03,50,0,4,1},
    	// Pareja de tipos 3 arriba a la derecha
    	{ 3700,2,0,GRAFICO_ENEMIGO_03,240,0,-4,1},
    	{ 3700,2,0,GRAFICO_ENEMIGO_03,280,0,-4,1},

    	{ 3800,2,1,GRAFICO_ROCA},
    	{ 3810,2,1,GRAFICO_ROCA},
    	{ 3815,2,1,GRAFICO_ENEMIGO_06},
    	{ 3815,2,1,GRAFICO_ENEMIGO_06},
    	{ 3820,2,1,GRAFICO_ROCA},
    	{ 3830,2,1,GRAFICO_ROCA},
    	{ 3830,2,1,GRAFICO_ENEMIGO_06},
    	{ 3840,2,1,GRAFICO_ROCA},

    	// Presentamos enemigo_05
    	// (Verde con 2 cañones) desp[4,1]. Tiene más vida. Va de lado a lado y si se cruza con el heroe dispara (una vez por trayecto lateral)
    	{ 3840,2,1,GRAFICO_ENEMIGO_05},
    	{ 3910,2,1,GRAFICO_ENEMIGO_05},
    	{ 3980,2,1,GRAFICO_ENEMIGO_05},
    	{ 4050,2,1,GRAFICO_ENEMIGO_05},

    	{ 4100,2,1,GRAFICO_ENEMIGO_05},
    	{ 4100,2,1,GRAFICO_ENEMIGO_06},
    	{ 4150,2,1,GRAFICO_ENEMIGO_05},
    	{ 4150,2,1,GRAFICO_ENEMIGO_06},
    	{ 4200,2,1,GRAFICO_ENEMIGO_05},
    	{ 4200,2,1,GRAFICO_ENEMIGO_06},

    	// Formación de 4 enemigos 5 cruzados
    	{ 4300,2,0,GRAFICO_ENEMIGO_05,0,10,4,1},
    	{ 4300,2,0,GRAFICO_ENEMIGO_05,285,50,-4,1},
    	{ 4300,2,0,GRAFICO_ENEMIGO_05,0,90,4,1},
    	{ 4300,2,0,GRAFICO_ENEMIGO_05,285,130,-4,1},

    	{ 4320,2,0,GRAFICO_ENEMIGO_04,0,0,0,6},
    	{ 4320,2,0,GRAFICO_ENEMIGO_04,140,0,0,6},
    	{ 4320,2,0,GRAFICO_ENEMIGO_04,280,0,0,6},
    	
    	// Premio para el final
    	{ 4400,1,0,GRAFICO_BOMBA},
    	// Entra el superenemigo
    	{ 4460,2,0,GRAFICO_SUPERENEMIGO_02,0,0,1,1},
    	
    	// Indica que el nivel ha terminado
    	{-1,-1,-1,-1}};


    
    
    
    static final int[][] nivel03 = {
    	// Empezamos con premio ;-)
    	// Capsula normal
    	{   10,1,0,GRAFICO_BOTIQUIN},
    	
    	// Avisamos que viene una nave por detrás
    	{   10,3,0,GRAFICO_RADAR},
    	// Enemigo_6 por detrás
       	{   50,2,0,GRAFICO_ENEMIGO_06,20, 479, 4,-1},

    	{  100,3,0,GRAFICO_RADAR},
       	{  150,2,0,GRAFICO_ENEMIGO_06,220, 479, -4,-1},
    	
    	{  200,2,1,GRAFICO_ALEATORIO},
    	{  250,2,1,GRAFICO_ALEATORIO},
    	{  300,2,1,GRAFICO_ALEATORIO},
    	{  350,2,1,GRAFICO_ALEATORIO},
    	{  400,2,1,GRAFICO_ALEATORIO},
    	{  450,2,1,GRAFICO_ALEATORIO},
    	{  500,2,1,GRAFICO_ALEATORIO},
    	{  550,2,1,GRAFICO_ALEATORIO},
    	{  600,2,1,GRAFICO_ALEATORIO},

    	{  600,3,0,GRAFICO_RADAR},
       	{  650,2,0,GRAFICO_ENEMIGO_06,20, 479, 4,-1},
       	{  650,2,0,GRAFICO_ENEMIGO_06,220, 479, -4,-1},

    	// 3 enemigo_04 arriba
    	{  700,2,0,GRAFICO_ENEMIGO_04,0,0,0,6},
    	{  700,2,0,GRAFICO_ENEMIGO_04,140,0,0,6},
    	{  700,2,0,GRAFICO_ENEMIGO_04,280,0,0,6},
    	// 2 enemigo_4 arriba
    	{  780,2,0,GRAFICO_ENEMIGO_04,70,0,1,6},
    	{  780,2,0,GRAFICO_ENEMIGO_04,210,0,-1,6},
    	{  860,2,0,GRAFICO_ENEMIGO_04,0,0,1,6},
    	{  860,2,0,GRAFICO_ENEMIGO_04,280,0,-1,6},

    	// Dos columnas de enemigo_01
    	{  900,2,0,GRAFICO_ENEMIGO_01,0,0,0,3},
    	{  900,2,0,GRAFICO_ENEMIGO_01,280,0,0,3},
    	{  920,2,0,GRAFICO_ENEMIGO_01,0,0,0,3},
    	{  920,2,0,GRAFICO_ENEMIGO_01,280,0,0,3},
    	{  940,2,0,GRAFICO_ENEMIGO_01,0,0,0,3},
    	{  940,2,0,GRAFICO_ENEMIGO_01,280,0,0,3},

    	{ 1000,2,1,GRAFICO_ALEATORIO},
    	{ 1050,2,1,GRAFICO_ALEATORIO},
    	{ 1100,2,1,GRAFICO_ALEATORIO},
    	{ 1100,3,0,GRAFICO_RADAR},
    	{ 1150,2,1,GRAFICO_ALEATORIO},
       	{ 1200,2,0,GRAFICO_ENEMIGO_06,20, 479, 4,-1},
       	{ 1200,2,0,GRAFICO_ENEMIGO_06,220, 479, -4,-1},
    	{ 1250,2,1,GRAFICO_ALEATORIO},
    	{ 1250,2,1,GRAFICO_ALEATORIO},

    	// Premio
    	{ 1250,1,0,GRAFICO_BOMBA},

    	{ 1350,2,1,GRAFICO_ALEATORIO},
    	{ 1400,2,1,GRAFICO_ALEATORIO},
    	{ 1450,2,1,GRAFICO_ALEATORIO},
    	{ 1450,2,1,GRAFICO_ALEATORIO},
    	{ 1450,2,1,GRAFICO_ALEATORIO},

    	{ 1600,2,1,GRAFICO_ALEATORIO},
    	{ 1650,2,1,GRAFICO_ALEATORIO},
    	{ 1700,2,1,GRAFICO_ALEATORIO},
    	{ 1750,2,1,GRAFICO_ALEATORIO},
    	{ 1800,3,0,GRAFICO_RADAR},
    	{ 1850,2,1,GRAFICO_ALEATORIO},
       	{ 1900,2,0,GRAFICO_ENEMIGO_06,220, 479, -4,-1},
    	{ 1900,2,1,GRAFICO_ALEATORIO},
    	{ 1900,2,1,GRAFICO_ALEATORIO},
    	{ 1900,2,1,GRAFICO_ALEATORIO},

    	{ 2050,2,0,GRAFICO_SUPERENEMIGO_01b,0,0,1,1},

    	// Formación de 4 enemigos 5 cruzados
    	{ 2300,2,0,GRAFICO_ENEMIGO_05,0,10,4,1},
    	{ 2300,2,0,GRAFICO_ENEMIGO_05,285,50,-4,1},
    	{ 2300,2,0,GRAFICO_ENEMIGO_05,0,90,4,1},
    	{ 2300,2,0,GRAFICO_ENEMIGO_05,285,130,-4,1},

    	// Premio2
    	{ 2400,1,0,GRAFICO_BOTIQUIN},
    	{ 2400,2,1,GRAFICO_ALEATORIO},
    	{ 2450,2,1,GRAFICO_ALEATORIO},
    	{ 2500,1,0,GRAFICO_BOMBA},
    	{ 2500,2,1,GRAFICO_ALEATORIO},
    	{ 2550,2,1,GRAFICO_ALEATORIO},
    	
    	{ 2700,2,0,GRAFICO_SUPERENEMIGO_02b,0,0,1,1},
    	{ 2700,3,0,GRAFICO_RADAR},
       	{ 2750,2,0,GRAFICO_ENEMIGO_06,20, 479, 4,-1},
       	{ 2750,2,0,GRAFICO_ENEMIGO_06,220, 479, -4,-1},

    	{ 2850,2,1,GRAFICO_ALEATORIO},
    	{ 2900,2,1,GRAFICO_ALEATORIO},
    	{ 2950,2,1,GRAFICO_ALEATORIO},
    	{ 2950,1,0,GRAFICO_BOMBA},
    	{ 3000,2,1,GRAFICO_ALEATORIO},
    	{ 3050,2,1,GRAFICO_ALEATORIO},

    	// Pareja de tipos 3 arriba a la izquierda
    	{ 3100,2,0,GRAFICO_ENEMIGO_03,10,0,4,1},
    	{ 3100,2,0,GRAFICO_ENEMIGO_03,50,0,4,1},
    	// Pareja de tipos 3 arriba a la derecha
    	{ 3100,2,0,GRAFICO_ENEMIGO_03,240,0,-4,1},
    	{ 3100,2,0,GRAFICO_ENEMIGO_03,280,0,-4,1},

    	{ 3120,1,0,GRAFICO_BOTIQUIN},

    	{ 3200,2,1,GRAFICO_ENEMIGO_05},
    	{ 3200,2,1,GRAFICO_ENEMIGO_06},
    	{ 3250,2,1,GRAFICO_ENEMIGO_05},
    	{ 3250,2,1,GRAFICO_ENEMIGO_06},

    	{ 3270,1,0,GRAFICO_BOMBA},

    	{ 3400,2,1,GRAFICO_ALEATORIO},
    	{ 3500,2,1,GRAFICO_ALEATORIO},
    	{ 3500,2,1,GRAFICO_ALEATORIO},
    	
    	// Pareja de tipos 3 arriba a la izquierda
    	{ 3620,2,0,GRAFICO_ENEMIGO_03,10,0,4,1},
    	{ 3620,2,0,GRAFICO_ENEMIGO_03,50,0,4,1},
    	// Pareja de tipos 3 arriba a la derecha
    	{ 3620,2,0,GRAFICO_ENEMIGO_03,240,0,-4,1},
    	{ 3620,2,0,GRAFICO_ENEMIGO_03,280,0,-4,1},

    	{ 3700,1,0,GRAFICO_BOMBA},

    	{ 3900,2,1,GRAFICO_ALEATORIO},
    	{ 4000,2,1,GRAFICO_ALEATORIO},
    	{ 4100,2,1,GRAFICO_ALEATORIO},
    	{ 4200,2,1,GRAFICO_ALEATORIO},
    	{ 4300,2,1,GRAFICO_ALEATORIO},

    	{ 4320,1,0,GRAFICO_BOTIQUIN},

    	{ 4400,2,1,GRAFICO_ALEATORIO},
    	{ 4500,2,1,GRAFICO_ALEATORIO},

    	{ 4600,2,0,GRAFICO_SUPERENEMIGO_03,0,0,1,1},
    	
    	// Indica que el nivel ha terminado
    	{-1,-1,-1,-1}};

    

}