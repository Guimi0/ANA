/*
Copyright (c) 2004-2010 Luis Miguel Armendáriz
http://guimi.net

Basado en Android Accelerometer Sensor Manager Archetype
 * @author antoine vianey (GPL 3)

Está permitido copiar, distribuir y/o modificar
	los desarrollos bajo los términos de la
	GNU General Public License, Versión 3

Para obtener una copia de dicha licencia
	visite http://www.fsf.org/licenses/gpl.txt.
*/
package net.guimi.ANA;

import java.util.List;
 
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
 
public class AcelerometroGestor {
	/**
	 * Este gestor detecta cualquier cambio en el acelerómetro según el parámetro SENSOR_DELAY
	 * y genera el evento "onAcelerometroCambio"
	 *         	// SENSOR_DELAY_FASTEST : tan rápido como sea posible
	 *          // SENSOR_DELAY_GAME : tasa rápida para juegos
	 *          // SENSOR_DELAY_NORMAL : tasa normal
	 *          // SENSOR_DELAY_UI : tasa aceptable para un hilo de UI
	 * 
	 * También detecta "agitaciones" (shake).
	 * Cuando el usuario agita el dispositivo, lo mueve brúscamente varias veces en breve espacio de tiempo.
	 * Una "agitación instantánea" es un movimiento brusco. Es decir un cambio por encima de "umbralFuerza".
	 *   Definimos un "intervaloEntreAgitaciones" mínimo que debe transcurrir entre una y otra. 
	 * Una "agitación" es un conjunto de movimientos bruscos en un breve espacio de tiempo "duracionAgitacion".
	 *  
	 * Cada movimiento brusco se detecta como una "agitación instantanea" y genera un evento
	 *    "onAcelerometroAgitacionInstantanea".
	 * La suma de varias "agitaciones instantáneas" durante un periodo de tiempo, es una "agitación" y 
	 *    genera un evento "onAcelerometroAgitacion".
	 * 
	 * El plazo de agitación comienza con una agitación instantánea y termina un tiempo después
	 *  (no necesariamente con otra agitación instantanea).
	 *  
	 **/
    private static float umbralFuerza = 0.03f;
    private static int intervaloEntreAgitaciones = 2000;
    private static int duracionAgitacion = 6000;
    /**
     *  Indica si durante una agitación debe emitir los eventos "onAcelerometroCambio"
     *  Lo habitual es que no se desee recibir los datos de cambio durante una agitación,
     *  sino únicamente la agitación en sí.
     */
    private static boolean cambiosDuranteAgitacion = false;

    /** Variables de trabajo**/
    private static Sensor miSensor;
    private static SensorManager miSensorManager;
    private static AcelerometroInterfaz miInterfaz;
 
    /** Indica si el sensor del acelerómetro está disponible */
    private static Boolean SensorSoportado;
    /** Indica si el sensor del acelerómetro está funcionando */
    private static boolean SensorFuncionando = false;
 
    /** Valores del acelerómetro **/
	private static float acelX = 0;
	private static float acelY = 0;
	private static float acelZ = 0;
    
	
	/** Función que indica si el sensor está funcionando **/
    public static boolean estaFuncionando() {
        return SensorFuncionando;
    }
 
	
	/** Función que permite configurar si se desea emitir señales de cambio durante las agitaciones
	 **/
    public void ponCambiosDuranteAgitacion(boolean emitirCambios) {
    	cambiosDuranteAgitacion = emitirCambios;
    }
    
    /**
     * Desregistra las interfaces
     */
    public static void paraInterfaces() {
    	SensorFuncionando = false;
        try {
            if (miSensorManager != null && miSensorEventListener != null) {
            	miSensorManager.unregisterListener(miSensorEventListener);
            }
        } catch (Exception e) {}
    }
 
    /**
     * Devuelve "cierto" (true) si hay al menos un acelerómetro disponible
     */
    public static boolean estaSoportado(Context contexto) {
        if (SensorSoportado == null) {
            if (contexto != null) {
            	miSensorManager = (SensorManager) contexto.
                        getSystemService(Context.SENSOR_SERVICE);
                List<Sensor> sensors = miSensorManager.getSensorList(
                        Sensor.TYPE_ACCELEROMETER);
                SensorSoportado = new Boolean(sensors.size() > 0);
            } else {
            	SensorSoportado = Boolean.FALSE;
            }
        }
        return SensorSoportado;
    }
 
    
    /**
     * Configura el interfaz para las agitaciones (shake)
     * @param umbralFuerza
     * 		variación mínima para considerar "Agitación" (shake) 
     * @param intervaloEntreAgitaciones
     * 		intervalo mínimo entre dos agitaciones
     */
    public static void configuraAgitacion(int umbralFuerza, int intervaloEntreAgitaciones) {
    	AcelerometroGestor.umbralFuerza = umbralFuerza;
    	AcelerometroGestor.intervaloEntreAgitaciones = intervaloEntreAgitaciones;
    }

    
    /** Funciones que devuelven los valores de X Y Z **/
	public float leeX(){return acelX;}
	public float leeY(){return acelY;}
	public float leeZ(){return acelZ;}

    
    /**
     * Inicia el interfaz y comienza la escucha
     * @param miAcelerometroInterfaz - gestionador de eventos del acelerómetro
     * @param contexto - contexto
     */
    public static void iniciaInterfaz(AcelerometroInterfaz miAcelerometroInterfaz,
    		Context contexto) {
    	
    	miSensorManager = (SensorManager) contexto.
                getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = miSensorManager.getSensorList(
                Sensor.TYPE_ACCELEROMETER);
        
        if (sensors.size() > 0) {
        	miSensor = sensors.get(0);

            SensorFuncionando = miSensorManager.registerListener(
                    miSensorEventListener, miSensor, 
                    SensorManager.SENSOR_DELAY_GAME);
            
            miInterfaz = miAcelerometroInterfaz;
        }
    }
 
    
    /**
     * Inicia el interfaz y comienza la escucha
     * @param miAcelerometroInterfaz
     * 		gestionador de eventos del acelerómetro
     * @param contexto
     * 		contexto
     * @param umbral
     * 		variación mínima para considerar "Agitación" (shake) 
     * @param intervalo
     * 		intervalo mínimo entre dos agitaciones
     */
    public static void iniciaInterfaz(AcelerometroInterfaz miAcelerometroInterfaz,
    		Context contexto,
            int umbral, int intervalo) {
    	
    	configuraAgitacion(umbral, intervalo);
    	iniciaInterfaz(miAcelerometroInterfaz, contexto);
    }
 
    /**
     * La interfaz que escucha los eventos del interfaz del acelerómetro
     */
    private static SensorEventListener miSensorEventListener = 
        new SensorEventListener() {
 
    	// Momento en el que se produce el cambio en los datos del acelerómetro (evento)
        private long ahora = 0;
    	// Momento del último cambio
        private long ultimoCambio = 0;
        // Valor de la fuerza del movimiento (indica la "cantidad" de cambio)
        //    si sobrepasa "umbralFuerza" el cambio se considerará "agitación instantánea"
        private float fuerza = 0;
        // Tiempo transcurrido desde el último cambio
        private long difTiempoCambio = 0;

    	// Momento de la última agitación instantánea
        private long ultimaAgitacionInstantanea = 0;
        // Tiempo transcurrido desde la última agitación instantánea
        private long difTiempoAgitacionInstantanea = 0;

    	// Momento en que se inicia una agitación (empieza con una agitación instantánea)
        private long inicioAgitacion = 0;
       // Indica si estamos en una agitación
        private boolean enAgitacion = false;
        // Valor de la fuerza de la agitación (suma de fuerzas de agitaciones instantáneas)
        private float fuerzaAgitacion = 0;
 
        // Valores anteriores de X, Y y Z
        private float ultimaX = 0;
        private float ultimaY = 0;
        private float ultimaZ = 0;

 
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
 
        public void onSensorChanged(SensorEvent evento) {
            // Usamos el momento del evento (timestamp) como referencia
            ahora = (evento.timestamp / 100000);
            acelX = evento.values[0];
            acelY = evento.values[1];
            acelZ = evento.values[2];
 
            // Si es el primer cambio en el sensor solo inicializamos variables
            if (ultimoCambio == 0) {
            	ultimoCambio = ahora;
            	ultimaAgitacionInstantanea = ahora;
            	ultimaX = acelX;
            	ultimaY = acelY;
            	ultimaZ = acelZ;
            } else {
            	// Medimos los tiempos transcurridos desde la ultima lectura
            	difTiempoCambio = ahora - ultimoCambio;
            	difTiempoAgitacionInstantanea = ahora - ultimaAgitacionInstantanea;
            	// Medimos la fuerza del cambio
               	fuerza = Math.abs(acelX + acelY + acelZ - ultimaX - ultimaY - ultimaZ) / difTiempoCambio;
               	
               	// Si la fuerza del cambio supera umbralFuerza y ha pasado el intervaloEntreAgitaciones mínimo,
               	//   consideramos el cambio una agitación instantánea
               	if (fuerza > umbralFuerza && difTiempoAgitacionInstantanea >= intervaloEntreAgitaciones) {
               		// Lanzamos el evento onAcelerometroAgitacionInstantanea 
               		miInterfaz.onAcelerometroAgitacionInstantanea(fuerza);
               		// Indicamos el momento de la agitación 
               		ultimaAgitacionInstantanea = ahora;
               		// Sumamos la fuerza a fuerzaAgitacion
               		fuerzaAgitacion = fuerzaAgitacion + fuerza;
               		// Si no estamos en una agitación, inicializamos una
               		if (!enAgitacion) {
               			enAgitacion = true;
               			// Indicamos el inicio de la agitación
               			inicioAgitacion = ahora;
               			// No sumamos la fuerza de agitaciones anteriores
               			fuerzaAgitacion = fuerza;
                    }
               	}
               	
               	// Si estamos en una agitación y ha pasado el plazo de la misma
               	//   damos por terminada la agitación y emitimos el evento "onAcelerometroAgitacion"
               	if (enAgitacion && ahora - inicioAgitacion >= duracionAgitacion) {
                   	miInterfaz.onAcelerometroAgitacion(fuerzaAgitacion);
                   	enAgitacion = false;
               	}
               	
               	// Indicamos los valores actuales del cambio en el sensor como los últimos disponibles
               	ultimaX = acelX;
               	ultimaY = acelY;
               	ultimaZ = acelZ;
               	ultimoCambio = ahora;
            }
            
            /*
             * Lanzamos el evento de cambio siempre que no estemos en una agitacion o la configuración
             *   nos indique que debemos emitir cambios incluso durante las agitaciones
             */
            if (!enAgitacion || cambiosDuranteAgitacion) {
            	// Lanzamos evento de cambio en el sensor (acelerómetro)
            	miInterfaz.onAcelerometroCambio(acelX, acelY, acelZ);
            }
        }
    };
 
}