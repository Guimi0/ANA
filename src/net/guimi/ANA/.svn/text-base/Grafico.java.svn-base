package net.guimi.ANA;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Clase que define los objetos gráficos que gestiona el juego
 * 
 * @author martin (http://www.droidnova.com), Guimi (http://guimi.net)
 */
public class Grafico {
	// DEFINICIONES
    //-----------------------------------------------------
    // Definimos los identificadores de los diferentes enemigos
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

	// VARIABLES AUXILIARES
    //-----------------------------------------------------
	// Generamos una variable auxiliar de tipo Bitmap que almacena el dibujo del objeto
    private Bitmap miDibujo;
	// Generamos una variable auxiliar de tipo Rectangulo que indica la parte del dibujo a utilizar
    private Rect miRectangulo;
    // Variable auxiliar que indica que marco de un "tileset" usamos
    private int miMarcoActual = 0;
    private int miAnchoSprite;
    private int miAltoSprite;
    private int miNumeroMarcos = 1;
	// Generamos una variable auxiliar que cuenta el tiempo desde que se creó el objeto
    //   lo que podemos utilizar en las animaciones
    private int tiempoCreado = 0;
    // Generamos una variable auxiliar que nos indica que el objeto debe disparar
    private boolean debeDisparar = false;
    /** Datos aleatorios */
    Random aleatorio = new Random();
    private int estadoObjeto = 0;

	// VARIABLES DE JUEGO
    //-----------------------------------------------------
    // Coordenadas que almacenan el punto en que se encuentra el gráfico
    private int miX = 0;
    private int miY = 0;
	// Desplazamiento del objeto
    private int miDespX = 0;
    private int miDespY = 0;
    // Indica si el objeto ha acando su vida y debe ser eliminado del juego
    private boolean esAcabado = false;
	// Generamos una variable auxiliar que indica el tipo del objeto gráfico
    private int miTipo;
    // Generamos una variable auxiliar que nos indica la vida del objeto
    private int vidaObjeto = 10;


	/**
     * Constructor
     * @param bitmap - Mapa de bits que deberemos dibujar
     */
    public Grafico(Bitmap bitmap) {
        miDibujo = bitmap;
        miAnchoSprite = miDibujo.getWidth();
        miAltoSprite =  miDibujo.getHeight();
    	miNumeroMarcos = 1;
        miMarcoActual = 0;
        // left, top, right, bottom
        miRectangulo = new Rect(0,0,miAnchoSprite,miAltoSprite);
    }
    public Grafico(Bitmap bitmap, int ancho, int alto, int marcos) {
        miDibujo = bitmap;
        miAnchoSprite = ancho;
        miAltoSprite =  alto;
    	miNumeroMarcos = marcos;
        miMarcoActual = 0;
        // left, top, right, bottom
        miRectangulo = new Rect(0,0,miAnchoSprite,miAltoSprite);
    }

    /** @param bitmap - Mapa de bits que deberemos dibujar */
    public void ponDibujo(Bitmap bitmap) {miDibujo = bitmap;}
    public void ponDibujo(Bitmap bitmap, int ancho, int alto, int marcos) {
    	miAnchoSprite = ancho;
    	miAltoSprite = alto;
    	miNumeroMarcos = marcos;
        miDibujo = bitmap;
        // left, top, right, bottom
        miRectangulo = new Rect(0,0,miAnchoSprite,miAltoSprite);
    }
    public Bitmap leeDibujo() {return miDibujo;}

    public int leeAnchoSprite() {return miAnchoSprite;}
    public int leeAltoSprite() {return miAltoSprite;}

    /** @param miX, miY coordenadas iniciales del rectangulo
     *  @param ancho, alto tamaño del rectangulo */
    public void ponRectangulo(int miX, int miY, int ancho, int alto) {
    	miRectangulo.left = miX;
    	miRectangulo.top = miY;
    	miAnchoSprite = ancho;
    	miAltoSprite = alto;
    	miRectangulo.right = miX+miAnchoSprite;
    	miRectangulo.bottom = miY+miAltoSprite;
    }

    public void ponEstadoObjeto(int estado) {estadoObjeto = estado;}
    public void modificaEstadoObjeto(int estado) {estadoObjeto = estadoObjeto + estado;}
    public int leeEstadoObjeto() {return estadoObjeto;}

    public void ponVidaObjeto(int vida) {vidaObjeto = vida;}
    public void modificaVidaObjeto(int vida) {
    	vidaObjeto = vidaObjeto + vida;
    	// OJO: El objeto no acaba su vida en el juego cuando vidaObjeto < 0
    	// Por ejemplo los enemigos cuando vidaObjeto < 0 pasan a vivir como explosiones
    	//if (vidaObjeto < 1) esAcabado=true;
    }
    public int leeVidaObjeto() {return vidaObjeto;}

    public boolean esAcabado() {return esAcabado;}

    public void ponDebeDisparar(boolean disparo) {debeDisparar=disparo;}
    public boolean leeDebeDisparar() {return debeDisparar;}
    
    /** @param tipo - Tipo del objeto gráfico */
    public void ponTipo(int tipo) {
        miTipo = tipo;
    }
    /** @return Tipo del objeto gráfico */
    public int leeTipo() {
        return miTipo;
    }

    /** @param paso - Paso en la vida del objeto */
    public void ponTiempoCreado(int paso) {
    	tiempoCreado = paso;
    }
    /** @return Paso en la vida del objeto */
    public int leeTiempoCreado() {
        return tiempoCreado;
    }

    /** @return Coordenada x de la esquina superior izquierda */
    public int leeX() {return miX;}
    public void ponX(int valor) {miX = valor;}

    /** @return Coordenada y de la esquina superior izquierda */
    public int leeY() {return miY;}
    public void ponY(int valor) {miY = valor;}

    /** @return Desplazamiento en el eje X */
    public int leeDespX() {return miDespX;}
    public void ponDespX(int valor) {miDespX = valor;}

    /** @return Desplazamiento en el eje Y */
    public int leeDespY() {return miDespY;}
    public void ponDespY(int valor) {miDespY = valor;}

    
    //************************************************************************
    //       INTELIGENCIA ARTIFICIAL (IA) DEL OBJETO GRÁFICO
    //************************************************************************
    /**
     * Esta función ejecuta la IA del objeto gráfico.
     *   Establece su nueva posición, decide si dispara, desaparece, cambia de dirección...
     */
    public void ejecutaIA(Pantalla miPantalla) {
    	// Incrementamos el contador de tiempo que lleva creado el objeto
        tiempoCreado++;
    	// Generamos un entero aleatorio entre 0 y 1
        int rand = Math.abs(aleatorio.nextInt() % 2);
        
        switch (miTipo) {
			case GRAFICO_ESTRELLA:
                // Establecemos su nueva ubicación
				miY = miY + miDespY;
            	// Si queda fuera de pantalla lo asignamos para eliminar
            	if (miY > miPantalla.getHeight()) {
            		esAcabado = true;
            	}
                break;
			case GRAFICO_ANIMACION_HEROE:
                // Establecemos su nueva ubicación
				miY = miY + miDespY;
            	// Si queda fuera de pantalla lo asignamos para eliminar
            	if (miY < -180) {
            		esAcabado = true;
            	}
                break;
    		case GRAFICO_EXPLOSION:
            	// Cada 5 ciclos cambiamos el dibujo que corresponde al objeto
                if ((tiempoCreado % 5) == 0) actualizaMarco();
                if (tiempoCreado >= (5*miNumeroMarcos)) esAcabado = true;
                break;
    		case GRAFICO_BRILLO:
            	// Cada 5 ciclos cambiamos el dibujo que corresponde al objeto
                if ((tiempoCreado % 3) == 0) actualizaMarco();
                if (tiempoCreado >=  (3*miNumeroMarcos)) esAcabado = true;
                break;
    		case GRAFICO_RADAR:
            	// Cada 5 ciclos cambiamos el dibujo que corresponde al objeto
                if ((tiempoCreado % 12) == 0) actualizaMarco();
                if (tiempoCreado >=  (48)) esAcabado = true;
                break;
    		case GRAFICO_DISPARO_HEROE:
                // Establecemos su nueva ubicación
				miY = miY + miDespY;
                // Verificamos si sale de pantalla en el eje Y
                if (miY < (0 - miAltoSprite)) {
                	esAcabado = true;
                }
                break;
    		case GRAFICO_DISPARO_ENEMIGO_3:
    		case GRAFICO_DISPARO_ENEMIGO_9:
    		case GRAFICO_DISPARO_ENEMIGO_13:
                // Establecemos su nueva ubicación
				miY = miY + miDespY;
                // Verificamos si sale de pantalla en el eje Y
            	if (miY > miPantalla.getHeight()) {
                	esAcabado = true;
                }
                break;
    		case GRAFICO_CAPSULA:
				if (estadoObjeto > 0) {
	                // Establecemos su nueva ubicación
					miX = miX + miDespX;
	                // Verificamos si sale de pantalla en el eje X
	                if ((miX < 0 - miDibujo.getWidth()) || (miX > miPantalla.getWidth())) {
	                	esAcabado = true;
	                }
				} else {
					if (tiempoCreado > 24) esAcabado = true;
					else if ((tiempoCreado % 5) == 0) actualizaMarco();
				}
    			break;
    		case GRAFICO_BOTIQUIN:
    		case GRAFICO_BOMBA:
                // Establecemos su nueva ubicación
				miY = miY + miDespY;
            	// Cada 5 ciclos cambiamos el dibujo que corresponde al objeto
                if ((tiempoCreado % 5) == 0) actualizaMarco();
            	// Cuando llega a una cierta altura en pantalla aceleramos
                if (miY > miPantalla.getHeight()*2/3) miDespY=6;
            	// Si queda fuera de pantalla lo asignamos para eliminar
            	if (miY > miPantalla.getHeight()) {
            		esAcabado = true;
            	}
                break;
    		case GRAFICO_DIAMANTE_GRIS:
    		case GRAFICO_DIAMANTE_VERDE:
    		case GRAFICO_DIAMANTE_ROJO:
                // Establecemos su nueva ubicación
				miY = miY + miDespY;
            	// Cuando llega a una cierta altura en pantalla aceleramos
                if (miY > miPantalla.getHeight()*2/3) miDespY=6;
            	// Si queda fuera de pantalla lo asignamos para eliminar
            	if (miY > miPantalla.getHeight()) {
            		esAcabado = true;
            	}
                break;
    		case GRAFICO_ROCA:
                // Establecemos su nueva ubicación
				miX = miX + miDespX;
				miY = miY + miDespY;
                
            	// Si queda fuera de pantalla lo asignamos para eliminar
            	if ((miY > miPantalla.getHeight()) || (miX < 0 - miDibujo.getWidth()) || (miX > miPantalla.getWidth())) {
                	esAcabado = true;
                }
    			break;
    		case GRAFICO_ENEMIGO_01:
                // Establecemos su nueva ubicación
				miX = miX + miDespX;
				miY = miY + miDespY;
                // Verificamos que no salga de pantalla en el eje X
                if (miX < 0) {
                	miDespX = -miDespX;
                    miX = 0;
                } else if (miX + miAnchoSprite > miPantalla.getWidth()) {
                	miDespX = -miDespX;
                    miX = miPantalla.getWidth() - miAnchoSprite;
                }
                // Verificamos que no salga de pantalla en el eje Y por arriba
                if (miY < 0) {
                	miDespY = -miDespY;
                    miY = 0;
                }
            	// Si queda fuera de pantalla lo asignamos para eliminar
            	if (miY > miPantalla.getHeight()) {
            		esAcabado = true;
            	}

    			// Cuando llega a una cierta altura dispara y cambia su trayectoria
                if ((miY > miPantalla.getHeight()/2) && (estadoObjeto==0)) {
                    debeDisparar = true;
                	estadoObjeto=1;
                	miDespY = 2;
                	if (rand == 0) miDespX = -4;
                	else miDespX = 4;
                }
            	break;
    		case GRAFICO_ENEMIGO_02:
                // Establecemos su nueva ubicación
				miX = miX + miDespX;
				miY = miY + miDespY;
                // Verificamos que no salga de pantalla en el eje X
                if (miX < 0) {
                	miDespX = -miDespX;
                    miX = 0;
                } else if (miX + miAnchoSprite > miPantalla.getWidth()) {
                	miDespX = -miDespX;
                    miX = miPantalla.getWidth() - miAnchoSprite;
                }
                // Verificamos que no salga de pantalla en el eje Y por arriba
                if (miY < 0) {
                	miDespY = -miDespY;
                    miY = 0;
                }
            	// Si queda fuera de pantalla lo asignamos para eliminar
            	if (miY > miPantalla.getHeight()) {
            		esAcabado = true;
            	}

    			// Cuando llega a una cierta altura cambia su velocidad y puede cambiar su trayectoria
                if ((miY > miPantalla.getHeight()/3) && (estadoObjeto==0)) {
                	estadoObjeto=1;
                	miDespY = 4;
                	if (rand == 0) miDespX = -4;
                	else miDespX = 4;
                }
    			break;
    		case GRAFICO_ENEMIGO_03:
                // Establecemos su nueva ubicación
				miX = miX + miDespX;
				miY = miY + miDespY;

                // Verificamos que no salga de pantalla en el eje X
                if (miX < 0) {
                	miDespX = -miDespX;
                    miX = 0;
        			debeDisparar = true;
                } else if (miX + miAnchoSprite > miPantalla.getWidth()) {
                	miDespX = -miDespX;
                    miX = miPantalla.getWidth() - miAnchoSprite;
        			debeDisparar = true;
                }

                // Verificamos que no salga de pantalla en el eje Y por arriba
                if (miY < 0) {
                	miDespY = -miDespY;
                    miY = 0;
                }

            	// Si queda fuera de pantalla lo asignamos para eliminar
            	if (miY > miPantalla.getHeight()) {
            		esAcabado = true;
            	}

            	break;
    		case GRAFICO_ENEMIGO_05:
                // Establecemos su nueva ubicación
				miX = miX + miDespX;
				miY = miY + miDespY;

				if ((tiempoCreado % 30) == 0) debeDisparar = true;;
                
                // Verificamos que no salga de pantalla en el eje X
                if (miX < 0) {
                	miDespX = -miDespX;
                    miX = 0;
        			debeDisparar = true;
                } else if (miX + miAnchoSprite > miPantalla.getWidth()) {
                	miDespX = -miDespX;
                    miX = miPantalla.getWidth() - miAnchoSprite;
        			debeDisparar = true;
                }

                // Verificamos que no salga de pantalla en el eje Y por arriba
                if (miY < 0) {
                	miDespY = -miDespY;
                    miY = 0;
                }

            	// Si queda fuera de pantalla lo asignamos para eliminar
            	if (miY > miPantalla.getHeight()) {
            		esAcabado = true;
            	}

            	break;
    		case GRAFICO_ENEMIGO_06:
                if ((tiempoCreado % 5) == 0) {
                	actualizaMarco();
        			if (miMarcoActual==12) debeDisparar = true;
                }
                // el resto igual que el 4
    		case GRAFICO_ENEMIGO_04:
                // Establecemos su nueva ubicación
				miX = miX + miDespX;
				miY = miY + miDespY;
                
                // Verificamos que no salga de pantalla en el eje X
                if (miX < 0) {
                	miDespX = -miDespX;
                    miX = 0;
                } else if (miX + miAnchoSprite > miPantalla.getWidth()) {
                	miDespX = -miDespX;
                    miX = miPantalla.getWidth() - miAnchoSprite;
                }

                // Verificamos que no salga de pantalla en el eje Y por arriba
                if (miY < 0) {
                	miDespY = -miDespY;
                    miY = 0;
                }

            	// Si queda fuera de pantalla lo asignamos para eliminar
            	if (miY > miPantalla.getHeight()) {
            		esAcabado = true;
            	}
            	break;
    		case GRAFICO_SUPERENEMIGO_01:
    		case GRAFICO_SUPERENEMIGO_02:
    		case GRAFICO_SUPERENEMIGO_03:
    			// Cada cierto tiempo dispara 
                if ((tiempoCreado % 50) == 0) debeDisparar = true;;

                // Establecemos su nueva ubicación
				miX = miX + miDespX;
				miY = miY + miDespY;
                // Verificamos que no salga de pantalla en el eje X
                if (miX < 0) {
                	miDespX = -miDespX;
                    miX = 0;
                } else if (miX + miAnchoSprite > miPantalla.getWidth()) {
                	miDespX = -miDespX;
                    miX = miPantalla.getWidth() - miAnchoSprite;
                }
                // Verificamos que no salga de pantalla en el eje Y por arriba
                if (miY < 0) {
                	miDespY = -miDespY;
                    miY = 0;
                }
    			// Cuando llega a una cierta altura vuelve a subir
                if (miY > miPantalla.getHeight()/4) {
                	miDespY = -miDespY;
                }
    			break;
    	}
    }
    

    //************************************************************************
    //       FUNCIONES GRÁFICAS AUXILIARES
    //************************************************************************
    private void actualizaMarco() {
    	miMarcoActual++;
    	if (miMarcoActual >= miNumeroMarcos) miMarcoActual = 0;
    	miRectangulo.top = miMarcoActual * miAltoSprite;
    	miRectangulo.bottom = miRectangulo.top + miAltoSprite;    	
    }
    
    /** Dibuja el mapa de bits en la posición correspondiente del lienzo que se pasa por parámetro
     * @param lienzo - Lienzo (canvas) en que se debe dibujar el mapa de bits */
    public void dibuja(Canvas lienzo) {
    	//lienzo.drawBitmap(miDibujo, miX, miY, null);

    	Rect dest = new Rect(miX, miY, miX + miAnchoSprite, miY + miAltoSprite);
    	lienzo.drawBitmap(miDibujo, miRectangulo, dest, null);
    }
    
}