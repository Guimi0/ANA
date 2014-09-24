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
 
public interface AcelerometroInterfaz {
	/**
	 * Este gestor detecta cambios en el acelerómetro y genera el evento "onAcelerometroCambio"
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

	// Evento que se lanza cada vez que se detecta un cambio en el acelerómetro
	public void onAcelerometroCambio(float x, float y, float z);
 
	// Evento que se lanza cada vez que se detecta una agitación en el acelerómetro
	public void onAcelerometroAgitacion(float fuerza);

	// ATENCION: Esta función no es necesaria casi-nunca.
	//   La función que se necesita habitualmente para detectar agitaciones es onAcelerometroAgitacion
	// Evento que se lanza cada vez que se detecta un movimiento brusco (agitación instantánea) en el acelerómetro
	public void onAcelerometroAgitacionInstantanea(float fuerza);

}